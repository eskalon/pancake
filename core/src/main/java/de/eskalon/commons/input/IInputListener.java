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

package de.eskalon.commons.input;

/**
 * This is a listener for input events provided by {@link IInputHandler}.
 * 
 * @param <E>
 * @param <F>
 */
public interface IInputListener<E extends Enum<E>, F extends Enum<F>> {

	public boolean on(F id);

	public boolean off(F id);

	public boolean axisChanged(E id, float value);

	public boolean moved(int screenX, int screenY);

}