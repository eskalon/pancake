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

package de.eskalon.commons.asset;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import de.damios.guacamole.Preconditions;
import de.eskalon.commons.inject.Qualifier;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;

/**
 * Allows loading and storing assets (like textures, bitmapfonts, tile maps,
 * sounds, music and so on) via annotations.
 * 
 * @author damios
 * @see Asset
 */
public class AnnotationAssetManager extends AssetManager {

	private HashMap<Class<?>, AssetLoaderParametersFactory<?>> paramFactories = new HashMap<>();

	public AnnotationAssetManager(FileHandleResolver resolver) {
		super(resolver);
	}

	/**
	 * @param clazz
	 *            the class whose fields annotated with {@link Asset Asset}
	 *            should get loaded
	 */
	public <T> void loadAnnotatedAssets(Class<T> clazz) {
		for (Field field : ClassReflection.getDeclaredFields(clazz)) {
			if (!field.isAnnotationPresent(Asset.class))
				continue;
			loadAnnotatedAsset(field);
		}

		if (clazz.getSuperclass() != null) {
			loadAnnotatedAssets(clazz.getSuperclass());
		}
	}

	/**
	 * @param field
	 *            the annotated field
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> void loadAnnotatedAsset(Field field) {
		Preconditions.checkArgument(field.isAnnotationPresent(Asset.class));

		Asset asset = field.getDeclaredAnnotation(Asset.class)
				.getAnnotation(Asset.class);

		if (!asset.disabled())
			load(asset.value(), (Class) field.getType(),
					getAssetLoaderParameters(asset, field));
	}

	/**
	 * Sets the fields of the given instance (or only the static fields if the
	 * instance is {@code null}) to the previously
	 * {@linkplain #loadAnnotatedAssets(Class) loaded} assets.
	 * 
	 * @param clazz
	 * @param instance
	 */
	@Deprecated
	private <T> void injectAssets(Class<T> clazz, @Nullable T instance) {
		for (Field field : ClassReflection.getDeclaredFields(clazz)) {
			if (!field.isAnnotationPresent(Asset.class))
				continue;
			Annotation annotation = field.getDeclaredAnnotation(Asset.class);
			if (annotation == null)
				continue;

			injectAsset(instance, field, annotation.getAnnotation(Asset.class));
		}

		if (clazz.getSuperclass() != null) {
			injectAssets(clazz.getSuperclass(), instance);
		}
	}

	private <T> void injectAsset(@Nullable T instance, Field field, Asset asset) {
		if (!asset.disabled()) {
			try {
				if (instance != null || field.isStatic()) {
					field.setAccessible(true);
					field.set(instance, get(asset.value()));
				}
			} catch (IllegalArgumentException | ReflectionException e) {
				throw new IllegalArgumentException(
						"Failed to set field '" + field.getName()
								+ "' of class '" + instance.getClass().getName()
								+ "' to the loaded asset value.",
						e);
			}
		}
	}

	private @Nullable AssetLoaderParameters<?> getAssetLoaderParameters(
			Asset asset, Field field) {
		if (asset.params() == null || asset.params().length() == 0)
			return null;

		final Class<?> fieldType = field.getType();
		AssetLoaderParametersFactory<?> factory = paramFactories.get(fieldType);

		Preconditions.checkState(factory != null,
				"Arguments for a field of type '" + fieldType
						+ "' cannot be processed without a corresponding params factory.");

		try {
			return factory.newInstance(asset.value(), asset.params());
		} catch (Exception e) {
			throw new RuntimeException(
					"Error while parsing the params for field '"
							+ field.getName() + "' with the following factory: "
							+ factory.getClass().getName(),
					e);
		}
	}

	public <T> void registerAssetLoaderParametersFactory(Class<T> clazz,
			AssetLoaderParametersFactory<T> factory) {
		paramFactories.put(clazz, factory);
	}

	/** @see #injectAssets(Class, Object) */
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T> void injectAssets(T container) {
		injectAssets((Class<T>) container.getClass(), container);
	}

	/** @see #getAssetsSet(Class, Object) */
	@Deprecated
	public void injectAssets(Class<?> container) {
		injectAssets(container, null);
	}

	/**
	 * These factories are responsible for parsing the {@link Asset#params()} to
	 * {@link AssetLoaderParameters}.
	 *
	 * @param <T>
	 */
	public static interface AssetLoaderParametersFactory<T> {
		public AssetLoaderParameters<T> newInstance(String path, String params);
	}

	/**
	 * Allows conveniently loading annotated assets in a class using
	 * {@link AnnotationAssetManager#loadAnnotatedAssets(Class)}.
	 * <p>
	 * The assets can then be injected via
	 * {@link AnnotationAssetManager#injectAssets(Class)} and
	 * {@link AnnotationAssetManager#injectAssets(Object)}.
	 * 
	 * @see AbstractAssetLoadingScreen
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.FIELD })
	@Qualifier
	public static @interface Asset {

		boolean disabled() default false;

		/**
		 * @return the path to the asset to inject.
		 */
		String value();

		String params() default "";
	}

}
