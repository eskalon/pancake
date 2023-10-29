/*
 * Copyright 2023 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import de.damios.guacamole.ConcatenatedIterator;
import de.damios.guacamole.Exceptions;
import de.damios.guacamole.MoreObjects;
import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;

/**
 * A simple event bus which utilises reflection. This class is inspired by
 * Guava's event bus.
 * <p>
 * There are two ways subscribers can receive an event: either they provide a
 * public method annotated with {@linkplain Subscribe @Subscribe} and with only
 * one parameter (see {@link #register(Object)}) or they extend {@link Consumer}
 * (see {@link #register(Class, Consumer)}).
 */
public class EventBus {

	private static final Logger LOG = LoggerService.getLogger(EventBus.class);

	/**
	 * All registered subscribers, indexed by event type.
	 */
	private final ConcurrentMap<Integer, HashSet<Subscriber>> subscribers;
	private final Map<Class<?>, Iterable<Method>> subscriberMethodsCache;
	private final Map<Class<?>, Set<Class<?>>> typeHierarchyCache;
	final Executor executor;

	public EventBus() {
		this.subscribers = new ConcurrentHashMap<>();
		this.subscriberMethodsCache = new ConcurrentHashMap<>();
		this.typeHierarchyCache = new ConcurrentHashMap<>();
		this.executor = Runnable::run; // this executor runs each task in the
										// thread that invokes Executor#execute
	}

	/**
	 * Registers all subscriber methods of the given object.
	 *
	 * @param subscriber
	 */
	public void register(Object subscriberObject) {
		Map<Integer, List<Subscriber>> subscribersWithinObject = findAllSubscribers(
				subscriberObject);

		for (Map.Entry<Integer, List<Subscriber>> entry : subscribersWithinObject
				.entrySet()) {
			int hashCode = entry.getKey();
			Collection<Subscriber> subsribersWithinObjectForEvent = entry
					.getValue();
			HashSet<Subscriber> registeredSubscribers = subscribers
					.get(hashCode);
			if (registeredSubscribers == null) {
				HashSet<Subscriber> newSet = new HashSet<>();
				registeredSubscribers = MoreObjects.firstNonNull(
						subscribers.putIfAbsent(hashCode, newSet), newSet);
			}
			registeredSubscribers.addAll(subsribersWithinObjectForEvent);
		}
	}

	public <T> void register(Class<T> eventType, Consumer<T> consumer) {
		int hashCode = eventType.getName().hashCode();

		HashSet<Subscriber> registeredSubscribers = subscribers.get(hashCode);
		if (registeredSubscribers == null) {
			HashSet<Subscriber> newSet = new HashSet<>();
			registeredSubscribers = MoreObjects.firstNonNull(
					subscribers.putIfAbsent(hashCode, newSet), newSet);
		}
		try {
			Method m = ClassReflection.getDeclaredMethod(consumer.getClass(),
					"accept", Object.class);
			m.setAccessible(true); // Consumer#accept(T) is package private
			registeredSubscribers.add(new Subscriber(consumer, m));
		} catch (ReflectionException e) {
			LOG.error(
					"Cannot retrieve the accept(Object) method of the given consumer. "
							+ Exceptions.getStackTraceAsString(e));
		}
	}

	/**
	 * Unregisters all subscriber methods of the given object.
	 * 
	 * @param subscriber
	 */
	public void unregister(Object subscriberObject) {
		Map<Integer, List<Subscriber>> subscribersWithinObject = findAllSubscribers(
				subscriberObject);
		for (Map.Entry<Integer, List<Subscriber>> entry : subscribersWithinObject
				.entrySet()) {
			int hashCode = entry.getKey();
			Collection<Subscriber> subsribersWithinObjectForEvent = entry
					.getValue();
			HashSet<Subscriber> registeredSubscribers = subscribers
					.get(hashCode);
			if (registeredSubscribers != null) {
				registeredSubscribers.removeAll(subsribersWithinObjectForEvent);
			}
		}
	}

	public <T> void unregister(Class<T> eventType, Consumer<T> consumer) {
		int hashCode = eventType.getName().hashCode();

		HashSet<Subscriber> registeredSubscribers = subscribers.get(hashCode);
		if (registeredSubscribers != null) {
			registeredSubscribers.removeIf((s) -> {
				return s.instance == consumer;
			});
		}
	}

	private Map<Integer, List<Subscriber>> findAllSubscribers(
			Object subscriberObject) {
		Map<Integer, List<Subscriber>> methodsInListener = new HashMap<>();
		Class<?> clazz = subscriberObject.getClass();
		for (Method method : findSubscriberMethods(clazz)) {
			Class<?> parameterClass = method.getParameterTypes()[0];
			int hashCode = parameterClass.getName().hashCode();

			List<Subscriber> subscriberList = methodsInListener
					.computeIfAbsent(hashCode, k -> new ArrayList<>());
			method.setAccessible(true); // the method may be private
			subscriberList.add(new Subscriber(subscriberObject, method));
		}
		return methodsInListener;
	}

	/**
	 * Posts an event to all subscribers registered for this event type. It will
	 * return after the event has been posted to all subscribers, and regardless
	 * of any exceptions thrown by subscribers. *
	 * <p>
	 * If no subscribers have been subscribed for {@code event}'s class, and
	 * {@code event} is not already a {@link DeadEvent}, it will be wrapped in a
	 * DeadEvent and reposted as such.
	 * 
	 * @param event
	 */
	public void post(Object event) {
		Preconditions.checkNotNull(event);
		Iterator<Subscriber> subscribers = getSubscribers(event);

		if (subscribers.hasNext()) {
			while (subscribers.hasNext()) {
				Subscriber s = subscribers.next();
				executor.execute(() -> {
					try {
						s.method.invoke(s.instance, event);
					} catch (ReflectionException e) {
						// do not post if an exception occurs while already
						// handling an exception
						if (!(event instanceof ExceptionEvent)) {
							post(new ExceptionEvent(this, s.instance, s.method,
									event, e.getCause()));
						}
					}
				});

			}
		} else {
			if (event instanceof ExceptionEvent)
				LOG.warn(
						"An ExceptionEvent was thrown, but no subscriber is registered for it.");
			else if (event instanceof DeadEvent)
				LOG.warn(
						"A DeadEvent was thrown, but no subscriber is registered for it.");
			else
				post(new DeadEvent(event));
		}
	}

	/**
	 * @param event
	 * @return an iterator of all subscribers registered for the given event
	 */
	private Iterator<Subscriber> getSubscribers(Object event) {
		Set<Class<?>> eventTypes = retrieveAllSuperTypes(event.getClass());
		LinkedList<Iterator<Subscriber>> subscriberIterators = new LinkedList<>();
		for (Class<?> eventType : eventTypes) {
			int hashCode = eventType.getName().hashCode();
			HashSet<Subscriber> eventSubscribers = subscribers.get(hashCode);
			if (eventSubscribers != null) {
				subscriberIterators.add(eventSubscribers.iterator());
			}
		}
		return new ConcatenatedIterator<>(subscriberIterators);
	}

	private Iterable<Method> findSubscriberMethods(Class<?> clazz) {
		return subscriberMethodsCache.computeIfAbsent(clazz, (c) -> {
			return ReflectionUtils.findAnnotatedMethods(Subscribe.class, c,
					(m) -> {
						Class<?>[] parameterTypes = m.getParameterTypes();
						if (parameterTypes.length != 1) {
							throw new IllegalArgumentException("Method " + m
									+ " has the @Subscribe annotation but has "
									+ parameterTypes.length + " parameters."
									+ " Subscriber methods must have exactly 1 parameter.");
						}
						return true;
					});
		});
	}

	private Set<Class<?>> retrieveAllSuperTypes(Class<?> clazz) {
		return typeHierarchyCache.computeIfAbsent(clazz,
				ReflectionUtils::retrieveAllSuperTypes);
	}

}