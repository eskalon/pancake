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

import java.util.EventObject;
import java.util.Objects;

/**
 * Wraps an event that was posted, but which had no subscribers and thus could
 * not be delivered.
 */
public final class DeadEvent {

	private final Object event;

	public DeadEvent(Object event) {
		this.event = Objects.requireNonNull(event);
	}

	/**
	 * @return the 'dead' event that could not be delivered.
	 */
	public Object getEvent() {
		return event;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{event=" + event + "}";
	}
}
