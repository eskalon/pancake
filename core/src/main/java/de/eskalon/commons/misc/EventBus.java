/*
 * Copyright 2020 eskalon
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

package de.eskalon.commons.misc;

import java.util.function.Consumer;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;

/**
 * This is an event bus.
 * 
 * @author damios
 */
public class EventBus {

	private static final Logger LOG = LoggerService.getLogger(EventBus.class);

	/**
	 * The consumers for the individual event types.
	 */
	private final ObjectMap<Class<?>, Array<Consumer<?>>> eventConsumer = new ObjectMap<>();

	/**
	 * Registers a new consumer.
	 * 
	 * @param <T>
	 * @param type
	 * @param listener
	 */
	public <T> void on(Class<T> type, Consumer<T> listener) {
		Array<Consumer<?>> consumer = eventConsumer.get(type);
		if (consumer == null) {
			consumer = new Array<>();
			eventConsumer.put(type, consumer);
		}
		consumer.add(listener);
	}

	/**
	 * Registers a new consumer.
	 * 
	 * @param type
	 * @param listener
	 */
	public void on(Class<?> type, Runnable listener) {
		on(type, o -> listener.run());
	}

	/**
	 * Immediately dispatches an event. The event is not queued.
	 * 
	 * @param <T>
	 * @param t
	 */
	@SuppressWarnings("unchecked")
	public <T> void dispatch(T t) {
		if (eventConsumer.containsKey(t.getClass())) {
			eventConsumer.get(t.getClass()).forEach(e -> {
				try {
					((Consumer<T>) e).accept(t);
				} catch (Exception ex) {
					LOG.error(
							"Exception thrown by consumer '%s' when dispatching event '%s'",
							e, t);
					LOG.error(ex.toString());
					ex.printStackTrace();
				}
			});
		} else {
			LOG.debug(
					"The event '%s' was dispatched, but there were no matching subscribers registered",
					t);
		}
	}

	public void clear() {
		eventConsumer.clear();
	}

}
