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

public interface IInputHandler<E extends Enum<E>> {

	public static interface AxisBindingListener<E> {

		public boolean axisChanged(E id, float value);

	}

	public static interface BinaryBindingListener<E> {

		public boolean on(E id);

		public boolean off(E id);

	}

	public static interface CursorMovementListener {

		public boolean moved(float screenX, float screenY);

	}

	public void registerAxisBinding(E id,
			/* int controllerAxis, */ int keycodeMin, int keycodeMax,
			int scrollAxis);

	public void registerBinaryBinding(E id,
			/* int controllerButton, */int keycode, int mouseButton,
			boolean toogleable);

	// Since there is only one cursor position, we don't need to register any
	// binding
	// public void setCursorPositionBinding(int controllerAxis, int
	// controllerButton);

	public void addAxisBindingListener(AxisBindingListener<E> listener);

	public void addBinaryBindingListener(BinaryBindingListener<E> listener);

	public void addCursorMovementListener(CursorMovementListener listener);

	public void removeAxisBindingListener(AxisBindingListener<E> listener);

	public void removeBinaryBindingListener(BinaryBindingListener<E> listener);

	public void removeCursorMovementListener(CursorMovementListener listener);

	public boolean isOn(E e);

	public float getAxisState(E e);

	public float getCursorX();

	public float getCursorY();

	public void clear();

}
