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

public interface IInjector {

	public <T> void bindTo(Class<T> clazz, Class<? extends T> linkedClazz);

	public <T> void bindToInstance(Class<T> clazz, T instance);

	public <T> void bindToProvider(Class<T> clazz, Provider<T> provider);

	// public <T> void bindToProvider(Class<T> clazz, Class providerClass);

	public <T, Q extends Annotation> void bindToQualifiedProvider(
			Class<T> clazz, Class<Q> qualifierClazz,
			QualifiedProvider<T, Q> provider);

	public void injectMembers(Object target);

}
