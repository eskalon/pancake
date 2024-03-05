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

import org.jspecify.annotations.Nullable;

import de.damios.guacamole.annotations.Beta;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Qualifier;

public interface IInjector {

	/**
	 * Bind {@code clazz} to one of its subclasses.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param linkedClazz
	 */
	public <T> void bindToSubclass(Class<T> clazz,
			Class<? extends T> linkedClazz);

	/**
	 * Bind {@code clazz} to a specific instance. This is only useful if the
	 * class has no dependencies that need to be injected.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param instance
	 */
	public <T> void bindToInstance(Class<T> clazz, T instance);

	/**
	 * Bind {@code clazz} to an instance that gets created upon injection.
	 * {@code clazz} needs to either have a no-args constructor or one annotated
	 * with {@link Inject}. Parameters of the constructor are injected.
	 * 
	 * @param <T>
	 * @param clazz
	 */
	public <T> void bindToConstructor(Class<T> clazz);

	/**
	 * Bind {@code clazz} to a {@link Provider} which is responsible for
	 * supplying the necessary instances.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param provider
	 */
	//@formatter:off
//	public <T> void bindToProvider(Class<T> clazz,
//			Class<? extends Provider<T>> provider);
	//@formatter:on

	/**
	 * Bind {@code clazz} to a {@link QualifiedProvider} which is responsible
	 * for supplying the necessary instances. Fields for which a
	 * {@link QualifiedProvider} is responsible need to be annotated with a
	 * {@link Qualifier}.
	 * <p>
	 * The provider needs to either have a no-args constructor or one annotated
	 * with {@link Inject}.
	 * 
	 * @param <T>
	 * @param clazz
	 * @param qualifierClazz
	 * @param qualifiedProviderClass
	 */
	public <T, Q extends Annotation> void bindToQualifiedProvider(
			Class<T> clazz, Class<Q> qualifierClazz,
			Class<? extends QualifiedProvider<T, Q>> qualifiedProviderClass);

	/**
	 * Retrieve an instance of {@code type}. {@code annotations} are the
	 * annotations for the field/parameter the dependency should get injected
	 * into; this is needed for {@link QualifiedProvider}s.
	 * 
	 * @param <T>
	 * @param type
	 * @param annotations
	 * @return
	 */
	public <T> T getInstance(Class<T> type,
			com.badlogic.gdx.utils.reflect.Annotation @Nullable [] annotations);

	/**
	 * Retrieve an instance of {@code type}.
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T> T getInstance(Class<T> type);

	/**
	 * Inject dependencies into the members of {@code target}. Any dependency
	 * that is injected also gets its own members injected.
	 * <p>
	 * Dependencies are resolved via the registered bindings.
	 * 
	 * @param <T>
	 * @param target
	 * @return the provided target
	 */
	public <T> T injectMembers(T target);

	@Beta
	public void reloadMembers(Object target);

	public void clear();

}
