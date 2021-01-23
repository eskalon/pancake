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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * An event bus supporting queueing.
 * <p>
 * After events got {@link #post(Object) posted}, they have to be dispatched to
 * the consumers via {@link #dispatchEvents()}. This can be useful if events
 * have to get handled in a certain thread.
 * 
 * @author damios
 */
public class EventQueueBus extends EventBus {

	/**
	 * Queue of posted events. Is taken care of when {@link #dispatchEvents()()}
	 * is called.
	 */
	private Queue<Object> eventQueue = new ConcurrentLinkedQueue<>();

	/**
	 * Posts an event to all registered consumers. The events get queued until
	 * {@link #dispatchEvents()} is called. This method will return successfully
	 * regardless of any exceptions thrown by consumers.
	 * 
	 * @param event
	 */
	public void post(Object event) {
		this.eventQueue.add(event);
	}

	/**
	 * Dispatches the events. After this method is called the
	 * {@linkplain #eventQueue queued events} get distributed to their
	 * respective consumers.
	 */
	public void dispatchEvents() {
		Object event = eventQueue.poll();
		while (event != null) {
			dispatch(event);
			event = eventQueue.poll();
		}
	}

	@Override
	public void clear() {
		super.clear();
		eventQueue.clear();
	}

}
