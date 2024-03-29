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

import java.util.Objects;

import com.badlogic.gdx.utils.reflect.Method;

/**
 * This event is published when an exceptions occurs on the bus.
 */
public final class ExceptionEvent {

	private final EventBus eventBus;
	private final Object subscriber;
	private final Method subscriberMethod;
	private final Object event;
	private final Throwable cause;

	public ExceptionEvent(EventBus eventBus, Object subscriber,
			Method subscriberMethod, Object event, Throwable cause) {
		this.eventBus = eventBus;
		this.subscriber = Objects.requireNonNull(subscriber);
		this.subscriberMethod = Objects.requireNonNull(subscriberMethod);
		this.event = Objects.requireNonNull(event);
		this.cause = cause;
	}

	/**
	 * @return the {@link EventBus} that handled the event
	 */
	public EventBus getEventBus() {
		return eventBus;
	}

	/**
	 * @return the event object that caused the exception
	 */
	public Object getEvent() {
		return event;
	}

	/**
	 * @return the subscriber object on which which threw the exception
	 */
	public Object getSubscriber() {
		return subscriber;
	}

	/**
	 * @return the subscribed method that threw the exception
	 */
	public Method getSubscriberMethod() {
		return subscriberMethod;
	}

	/**
	 * @return the actual exception
	 */
	public Throwable getCause() {
		return cause;
	}
}
