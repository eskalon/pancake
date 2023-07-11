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

package de.eskalon.commons.inject;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.damios.guacamole.tuple.Pair;

public class EskalonInjector implements IInjector {

	private static final Logger LOG = LoggerService
			.getLogger(EskalonInjector.class);

	private static EskalonInjector instance;

	public static EskalonInjector getInstance() {
		if (instance == null)
			instance = new EskalonInjector();
		return instance;
	}

	private final HashMap<Class<?>, Class<?>> links = new HashMap<>();
	private final HashMap<Class<?>, Object> instances = new HashMap<>();
	private final HashMap<Class<?>, Provider<?>> providers = new HashMap<>();
	private final HashMap<Class<?>, Pair<QualifiedProvider<?, Annotation>, Class<? extends Annotation>>> qualifiedProviders = new HashMap<>();

	@Override
	public <T> void bindTo(Class<T> clazz, Class<? extends T> linkedClazz) {
		links.put(clazz, linkedClazz);
	}

	@Override
	public <T> void bindToInstance(Class<T> clazz, T instance) {
		instances.put(clazz, instance);
	}

	@Override
	public <T> void bindToProvider(Class<T> clazz, Provider<T> provider) {
		providers.put(clazz, provider);
	}

	@Override
	public <T, Q extends Annotation> void bindToQualifiedProvider(
			Class<T> clazz, Class<Q> qualifierClazz,
			QualifiedProvider<T, Q> provider) {
		qualifiedProviders.put(clazz, new Pair(provider, qualifierClazz));
	}

	private @Nullable Object getInstanceForField(Field field) {
		Class<?> type = field.getType();

		// Linked bindings
		type = links.getOrDefault(type, type);

		// Instance bindings
		Object value = instances.get(type);
		if (value != null)
			return value;

		// Provider binding
		Provider<?> provider = providers.get(type);
		if (provider != null) {
			value = provider.provide();

			if (value != null)
				return value;
		}

		// Qualified provider binding
		Pair<QualifiedProvider<?, Annotation>, Class<? extends Annotation>> pair = qualifiedProviders
				.get(type);

		if (pair != null) {
			Annotation qualifier = ReflectionUtils.getAnnotationObject(field,
					pair.y);

			if (qualifier != null)
				return pair.x.provide(qualifier);

		}

		LOG.error("No binding found for %s with the following qualifiers: %s",
				type, Arrays.toString(field.getDeclaredAnnotations()));

		return null;
	}

	@Override
	public void injectMembers(Object target) {
		for (Field field : ClassReflection
				.getDeclaredFields(target.getClass())) {
			if (field.isAnnotationPresent(Inject.class)) {
				try {
					field.setAccessible(true);
					field.set(target, getInstanceForField(field));
				} catch (ReflectionException e) {
					LOG.error(
							"Error while injecting a value for %s into %s: %s",
							field, target, e);
				}
			}
		}

	}

}
