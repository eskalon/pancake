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

import com.badlogic.gdx.utils.reflect.Method;

import de.damios.guacamole.gdx.reflection.ReflectionUtils;

/**
 * A subscriber for a specific event.
 */
public class Subscriber {

	/** The object with the subscriber method. */
	final Object instance;
	/** The subscribing method. */
	final Method method;

	Subscriber(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}

	@Override
	public final int hashCode() {
		return (31 + method.hashCode()) * 31
				+ System.identityHashCode(instance);
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof Subscriber) {
			Subscriber that = (Subscriber) obj;
			// Use == so that different (but equal) instances will still receive
			// events. We only guard against the case that the same object is
			// registered multiple times
			return instance == that.instance
					&& ReflectionUtils.areMethodsEqual(method, that.method);
		}
		return false;
	}

}