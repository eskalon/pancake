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
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionConverter;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import de.damios.guacamole.Exceptions;
import de.damios.guacamole.Preconditions;
import de.damios.guacamole.annotations.Beta;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Reloadable;
import de.eskalon.commons.inject.annotations.Singleton;
import de.eskalon.commons.utils.InjectionUtils;

class DefaultInjector implements IInjector {

	private static final Logger LOG = LoggerService
			.getLogger(DefaultInjector.class);

	private final HashMap<Class<?>, Class<?>> links = new HashMap<>();
	private final HashMap<Class<?>, Object> singletonInstances = new HashMap<>();
	private final HashMap<Class<?>, ConstructorBinding> constructorBindings = new HashMap<>();
	//@formatter:off
//	private final HashMap<Class<?>, ProviderBinding> providerBindings = new HashMap<>();
	//@formatter:on
	private final HashMap<Class<?>, QualifiedProvider<?, ?>> qualifiedProviderInstances = new HashMap<>();
	private final HashMap<Class<?>, QualifiedProviderBinding> qualifiedProviderBindings = new HashMap<>();

	private boolean fallbackToConstructorReflection = false;

	@Override
	public <T> void bindToSubclass(Class<T> clazz,
			Class<? extends T> linkedClazz) {
		links.put(clazz, linkedClazz);
	}

	@Override
	public <T> void bindToInstance(Class<T> clazz, T instance) {
		singletonInstances.put(clazz, instance);
	}

	@Override
	public <T> void bindToConstructor(Class<T> clazz) {
		constructorBindings.put(clazz, new ConstructorBinding<>(clazz));
	}

	//@formatter:off
//	@Override
//	public <T> void bindToProvider(Class<T> clazz,
//			Class<? extends Provider<T>> providerClazz) {
//		providerBindings.put(clazz, new ProviderBinding<>(providerClazz));
//	}
	//@formatter:on

	@Override
	public <T, Q extends Annotation> void bindToQualifiedProvider(
			Class<T> clazz, Class<Q> qualifierClazz,
			Class<? extends QualifiedProvider<T, Q>> qualifiedProviderClass) {
		qualifiedProviderBindings.put(clazz, new QualifiedProviderBinding<>(
				qualifierClazz, qualifiedProviderClass));
	}

	@Override
	public <T> T getInstance(Class<T> type) {
		return getInstance(type, null);
	}

	@Override
	public <T> @Nullable T getInstance(Class<T> type,
			@Nullable com.badlogic.gdx.utils.reflect.Annotation[] annotations) {
		/* Linked bindings */
		while (links.containsKey(type)) {
			type = (Class<T>) links.get(type);
		}

		/* Existing singleton */
		T value = (T) singletonInstances.get(type);

		if (value != null)
			return value;

		/* Constructor bindings */
		ConstructorBinding constructorBinding = constructorBindings.get(type);
		if (constructorBinding != null) {
			value = InjectionUtils.createInstanceViaConstructor(this,
					constructorBinding.classConstructor);

			if (value != null) {
				injectMembers(value);

				if (constructorBinding.isSingleton)
					singletonInstances.put(type, value);

				return value;
			}
		}

		/* Provider bindings */
		// TODO: Cache providers?
		//@formatter:off
//		ProviderBinding providerBinding = providerBindings.get(type);
//		if (providerBinding != null) {
//			Provider<T> provider = (Provider<T>) InjectionUtils
//					.createInstanceViaConstructor(this,
//							providerBinding.providerConstructor);
//			if (provider != null) {
//				injectMembers(provider);
//				value = provider.provide();
//
//				if (value != null) {
//					injectMembers(value);
//
//					if (providerBinding.isSingleton)
//						singletonInstances.put(type, value);
//
//					return value;
//				}
//			}
//		}
		//@formatter:on

		/* Qualified provider bindings */
		if (annotations != null) {
			QualifiedProviderBinding qualifiedProviderBinding = qualifiedProviderBindings
					.get(type);

			if (qualifiedProviderBinding != null) {
				QualifiedProvider provider = qualifiedProviderInstances
						.get(type);

				if (provider == null) {
					// Create & cache provider
					provider = (QualifiedProvider) InjectionUtils
							.createInstanceViaConstructor(this,
									qualifiedProviderBinding.providerConstructor);

					if (provider != null) {
						injectMembers(provider);

						// There is no point in caching the provider if it
						// provided a singleton
						if (!qualifiedProviderBinding.isSingleton)
							qualifiedProviderInstances.put(type, provider);
					}
				}

				if (provider != null) {
					// Retrieve qualifier
					Annotation qualifier = null;
					for (com.badlogic.gdx.utils.reflect.Annotation a : annotations) {
						if (a.getAnnotationType().equals(
								qualifiedProviderBinding.qualifierClass)) {
							qualifier = a.getAnnotation(
									qualifiedProviderBinding.qualifierClass);
							break;
						}
					}

					// Call provider
					if (qualifier != null) {
						value = (T) provider.provide(qualifier);

						if (value != null) {
							injectMembers(value);

							if (qualifiedProviderBinding.isSingleton)
								singletonInstances.put(type, value);

							return value;
						}
					} else {
						LOG.debug(
								"The qualified provider for type '%s' was skipped since no matching qualifier ('@%s') was present on the field.",
								type, qualifiedProviderBinding.qualifierClass
										.getSimpleName());
					}
				}
			}
		}

		/* Constructor reflection fallback */
		if (fallbackToConstructorReflection) {
			value = ReflectionUtils.newInstanceOrNull(type);

			if (value != null) {
				// NOTE: don't try to inject the value's members in this case;
				// the class would have been registered if it expected field
				// injection to happen
				LOG.debug("Falling back to constructor reflection for type %s",
						type);
				return value;
			}
		}

		/* Dependency could not be resolved */
		LOG.debug("The dependency '%s' could not be resolved.", type);

		return null;
	}

	@Override
	public <T> T injectMembers(T target) {
		Set<Class<?>> allSuperTypes = ReflectionUtils
				.retrieveAllSuperTypes(target.getClass());
		for (Class<?> type : allSuperTypes) {
			for (Field field : ClassReflection.getDeclaredFields(type)) {
				if (field.isAnnotationPresent(Inject.class)) {
					try {
						field.setAccessible(true);
						Object oldValue = field.get(target);
						if (oldValue == null) {
							Object newValue = getInstance(field.getType(),
									field.getDeclaredAnnotations());

							if (newValue == null) {
								if (LoggerService.isErrorEnabled())
									LOG.error(
											"Unable to inject dependency of type '%s' into '%s#%s' (annotated with: %s). The dependency could not be resolved.",
											field.getType().getSimpleName(),
											type.getSimpleName(),
											field.getName(),
											Arrays.stream(field
													.getDeclaredAnnotations())
													.map(a -> "@" + a
															.getAnnotationType()
															.getSimpleName())
													.collect(Collectors
															.joining(", ")));
								continue;
							}
							field.set(target, newValue);
						} else {
							LOG.debug(
									"Field '%s' of '%s' is already set to '%s'. As a consequence, no value was injected.",
									field.getName(), target, oldValue);
						}
					} catch (ReflectionException e) {
						LOG.error(
								"Error while injecting a value for '%s' into '%s': %s",
								field.getName(), target,
								Exceptions.getStackTraceAsString(e));
					}
				}
			}
		}

		return target;

	}

	@Beta
	@Override
	public void reloadMembers(Object target) {
		Set<Class<?>> allSuperTypes = ReflectionUtils
				.retrieveAllSuperTypes(target.getClass());
		for (Class<?> type : allSuperTypes) {
			for (Field field : ClassReflection.getDeclaredFields(type)) {
				if (field.isAnnotationPresent(Inject.class)
						&& field.isAnnotationPresent(Reloadable.class)) {
					try {
						field.setAccessible(true);

						Object oldValue = field.get(target);
						if (oldValue != null && oldValue instanceof Disposable)
							((Disposable) oldValue).dispose();

						field.set(target, getInstance(field.getType(),
								field.getDeclaredAnnotations()));
					} catch (ReflectionException e) {
						LOG.error(
								"Error while injecting a value for '%s' into '%s': %s",
								field.getName(), target,
								Exceptions.getStackTraceAsString(e));
					}
				}
			}
		}
	}

	public void setFallbackToConstructorReflection(
			boolean fallbackToConstructorReflection) {
		this.fallbackToConstructorReflection = fallbackToConstructorReflection;
	}

	@Override
	public void clear() {
		links.clear();
		singletonInstances.clear();
		constructorBindings.clear();
		//@formatter:off
//		providerBindings.clear();
		//@formatter:on
		qualifiedProviderInstances.clear();
		qualifiedProviderBindings.clear();

		fallbackToConstructorReflection = false;
	}

	/*
	 * BINDING CLASSES
	 */
	public class ConstructorBinding<T> {
		private Constructor classConstructor;
		private boolean isSingleton = false;

		ConstructorBinding(Class<?> clazz) {
			java.lang.reflect.Constructor c = InjectionUtils
					.getInjectableConstructor(clazz, true);
			this.classConstructor = ReflectionConverter
					.convertConstructorObject(c);

			Preconditions.checkArgument(c != null, "Class '"
					+ clazz.getSimpleName()
					+ "' does not have a no-args constructor nor one annotated with @Inject!");

			this.isSingleton = c.isAnnotationPresent(Singleton.class);
		}
	}

	//@formatter:off
//	public class ProviderBinding<T> {
//		private Constructor providerConstructor;
//		private boolean isSingleton = false;
//
//		ProviderBinding(Class<? extends Provider<T>> providerClazz) {
//			java.lang.reflect.Constructor c = InjectionUtils
//					.getInjectableConstrcutor(providerClazz, true);
//			this.providerConstructor = ReflectionConverter
//					.convertConstructorObject(c);
//			this.isSingleton = InjectionUtils
//					.isMethodAnnotatedWithSingleton(providerClazz, "provide");
//
//			Preconditions.checkArgument(c != null, "Class "
//					+ providerClazz.getSimpleName()
//					+ " does not have a constructor annotated with @Inject nor a zero-args one!");
//		}
//	}
	//@formatter:on

	public class QualifiedProviderBinding<T, Q extends Annotation> {
		private Class<Q> qualifierClass;
		private Constructor providerConstructor;
		private boolean isSingleton = false;

		QualifiedProviderBinding(Class<Q> qualifierClazz,
				Class<? extends QualifiedProvider<T, Q>> providerClazz) {
			java.lang.reflect.Constructor c = InjectionUtils
					.getInjectableConstructor(providerClazz, true);
			this.providerConstructor = ReflectionConverter
					.convertConstructorObject(c);
			this.isSingleton = InjectionUtils.isMethodAnnotatedWithSingleton(
					providerClazz, "provide", qualifierClazz);
			this.qualifierClass = qualifierClazz;

			Preconditions.checkArgument(c != null, "Class '"
					+ providerClazz.getSimpleName()
					+ "' does not have a constructor annotated with @Inject nor a zero-args one!");
		}
	}

}
