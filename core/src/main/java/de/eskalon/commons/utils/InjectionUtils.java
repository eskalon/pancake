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

package de.eskalon.commons.utils;

import java.util.Arrays;
import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Method;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.inject.IInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Singleton;

public class InjectionUtils {

	private static final Logger LOG = LoggerService
			.getLogger(InjectionUtils.class);

	private InjectionUtils() {
		throw new UnsupportedOperationException();
	}

	public static <T> @Nullable T createInstanceViaConstructor(
			IInjector injector, Constructor constructor) {
		// Retrieve constructor parameters
		Class[] c = constructor.getParameterTypes();
		Object[] o = new Object[c.length];

		for (int i = 0; i < c.length; i++) {
			// TODO: Since libGDX does not support retrieving the annotations of
			// constructor parameters, QualifiedProviders do not work for
			// constructor injection

			o[i] = injector.getInstance(c[i], null);
		}

		// Create instance
		return ReflectionUtils.newInstanceWithParamsOrNull(constructor, o);
	}

	public static java.lang.reflect.@Nullable Constructor getInjectableConstructor(
			Class<?> clazz, boolean includeNoArgsConstructor) {
		// TODO: This needs to be ported to libGDX's reflection handling
		// to make it compatible with GWT
		java.lang.reflect.Constructor ret = null;
		for (java.lang.reflect.Constructor c : clazz.getConstructors()) {
			if (c.isAnnotationPresent(Inject.class))
				return c;

			// Save no-args constructor as fallback
			if (includeNoArgsConstructor && c.getParameterCount() == 0)
				ret = c;
		}

		return ret;
	}

	public static boolean isMethodAnnotatedWithSingleton(Class<?> clazz,
			String methodName, Class... paramTypes) {
		Set<Class<?>> allSuperTypes = ReflectionUtils
				.retrieveAllSuperTypes(clazz);
		for (Class<?> type : allSuperTypes) {
			for (Method method : ClassReflection.getMethods(type)) {
				if (method.getName().equals(methodName) && Arrays
						.equals(method.getParameterTypes(), paramTypes)) {
					return method.isAnnotationPresent(Singleton.class);
				}
			}
		}

		LOG.error("Could not determine whether %s provides a singleton.",
				clazz.getSimpleName());

		return false;
	}

}
