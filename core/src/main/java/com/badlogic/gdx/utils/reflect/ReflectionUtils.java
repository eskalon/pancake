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

package com.badlogic.gdx.utils.reflect;

import javax.annotation.Nullable;

import de.damios.guacamole.gdx.Log;

/**
 * Reflection utils.
 * 
 * @author damios
 */
public class ReflectionUtils {

	private ReflectionUtils() {
		throw new UnsupportedOperationException();
	}

	public static Field convertFieldObject(java.lang.reflect.Field field) {
		// The constructor is package private
		return new Field(field);
	}

	/**
	 * Creates a class via libGDX reflection by using its name. Returns
	 * {@code null}, if the reflection or instantiation fails.
	 * 
	 * @param <T>
	 * @param className
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T newInstance(String className, Class<T> clazz) {
		try {
			return (T) ClassReflection
					.newInstance(ClassReflection.forName(className));
		} catch (ReflectionException e) {
			Log.debug("ReflectionUtils", e.getLocalizedMessage());
			return null;
		}
	}

}
