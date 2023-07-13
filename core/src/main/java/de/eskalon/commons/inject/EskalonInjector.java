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

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import de.damios.guacamole.annotations.Beta;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;

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
	private final HashMap<Class<?>, InstanceWrapper<?>> instances = new HashMap<>();
	private final HashMap<Class<?>, Provider<?>> providers = new HashMap<>();
	private final HashMap<Class<?>, QualifiedProviderWrapper<?, ?>> qualifiedProviders = new HashMap<>();

	private boolean fallbackToConstructorReflection = false;

	@Override
	public <T> void bindTo(Class<T> clazz, Class<? extends T> linkedClazz) {
		links.put(clazz, linkedClazz);
	}

	@Override
	public <T> void bindToInstance(Class<T> clazz, T instance) {
		instances.put(clazz, new InstanceWrapper(instance));
	}

	@Override
	public <T> void bindToProvider(Class<T> clazz, Provider<T> provider) {
		providers.put(clazz, provider);
	}

	@Override
	public <T, Q extends Annotation> void bindToQualifiedProvider(
			Class<T> clazz, Class<Q> qualifierClazz,
			QualifiedProvider<T, Q> provider) {
		qualifiedProviders.put(clazz,
				new QualifiedProviderWrapper(provider, qualifierClazz));
	}

	private @Nullable Object getInstanceForField(Field field) {
		Class<?> type = field.getType();

		// Linked bindings
		type = links.getOrDefault(type, type);

		// Instance bindings
		InstanceWrapper instanceWrapper = instances.get(type);
		if (instanceWrapper != null) {
			if (!instanceWrapper.membersInjected) { // only inject once; for
													// providers this is taken
													// care of by the
													// requirement that fields
													// have to have 'null' as
													// value for injection to
													// take place
				injectMembers(instanceWrapper.instance);
				instanceWrapper.membersInjected = true;
			}

			return instanceWrapper.instance;
		}

		// Provider binding
		Provider<?> provider = providers.get(type);
		if (provider != null) {
			Object value = provider.provide();

			if (value != null) {
				injectMembers(value);
				return value;
			}
		}

		// Qualified provider binding
		QualifiedProviderWrapper qualifiedProviderWrapper = qualifiedProviders
				.get(type);

		if (qualifiedProviderWrapper != null) {
			Annotation qualifier = ReflectionUtils.getAnnotationObject(field,
					qualifiedProviderWrapper.qualifierClass);

			if (qualifier != null) {
				Object value = qualifiedProviderWrapper.qualifiedProvider
						.provide(qualifier);

				if (value != null) {
					injectMembers(value);
					return value;
				}
			}
		}

		if (fallbackToConstructorReflection) {
			Object value = ReflectionUtils.newInstanceOrNull(type);

			if (value != null) {
				// NOTE: don't try to inject the value's members in this case
				LOG.debug("Falling back to constructor reflection for type %s",
						type);
				return value;
			}
		}

		LOG.error("No binding found for %s with the following qualifiers: %s",
				type, Arrays.toString(field.getDeclaredAnnotations()));

		return null;
	}

	@Override
	public <T> T injectMembers(T target) {
		for (Field field : ClassReflection
				.getDeclaredFields(target.getClass())) {
			if (field.isAnnotationPresent(Inject.class)) {
				try {
					field.setAccessible(true);
					Object oldValue = field.get(target);
					if (oldValue == null)
						field.set(target, getInstanceForField(field));
					else
						LOG.debug(
								"Field %s of %s is already set ('%s'). As a consequence, no value is injected.",
								field, target, oldValue);
				} catch (ReflectionException e) {
					LOG.error(
							"Error while injecting a value for %s into %s: %s",
							field, target, e);
				}
			}
		}

		return target;
	}

	@Beta
	public void reloadMembers(Object target) {
		for (Field field : ClassReflection
				.getDeclaredFields(target.getClass())) {
			if (field.isAnnotationPresent(Inject.class)
					&& field.isAnnotationPresent(Reloadable.class)) {
				try {
					field.setAccessible(true);

					Object oldValue = field.get(target);
					if (oldValue != null && oldValue instanceof Disposable)
						((Disposable) oldValue).dispose();

					field.set(target, getInstanceForField(field));
				} catch (ReflectionException e) {
					LOG.error(
							"Error while injecting a value for %s into %s: %s",
							field, target, e);
				}
			}
		}
	}

	public void setFallbackToConstructorReflection(
			boolean fallbackToConstructorReflection) {
		this.fallbackToConstructorReflection = fallbackToConstructorReflection;
	}

	private class InstanceWrapper<T> {
		private T instance;
		private boolean membersInjected;

		private InstanceWrapper(T instance) {
			this.instance = instance;
			this.membersInjected = false;
		}
	}

	private class QualifiedProviderWrapper<T, Q extends Annotation> {
		private QualifiedProvider<T, Q> qualifiedProvider;
		private Class<Q> qualifierClass;

		private QualifiedProviderWrapper(
				QualifiedProvider<T, Q> qualifiedProvider,
				Class<Q> qualifierClass) {
			this.qualifiedProvider = qualifiedProvider;
			this.qualifierClass = qualifierClass;
		}

	}

}
