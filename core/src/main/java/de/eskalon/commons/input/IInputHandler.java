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

import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.settings.IntProperty;

public interface IInputHandler<E extends Enum<E>, F extends Enum<F>> {

	public void addListener(IInputListener<E, F> listener);

	public void removeListener(IInputListener<E, F> listener);

	public boolean isOn(F f);

	public float getAxisState(E e);

	public int getCursorX();

	public int getCursorY();

	public void reset();

	public void clear();

	/* Static methods */
	static final String KEYBIND_SETTINGS_PREFIX = "keybind_";

	public static <E extends Enum<E>> void registerAxisBinding(
			EskalonSettings settings, E id, int keycodeMin, int keycodeMax,
			int mouseAxis) {
		// Don't use set because the given values are just default ones
		settings.getIntProperty(getPropertyName(id, "keycode_min"), keycodeMin);
		settings.getIntProperty(getPropertyName(id, "keycode_max"), keycodeMax);
		settings.getIntProperty(getPropertyName(id, "mouse_axis"), mouseAxis);
	}

	public static <F extends Enum<F>> void registerBinaryBinding(
			EskalonSettings settings, F id, int keycode, int mouseButton,
			boolean toogleable) {
		// Don't use set because the given values are just default ones
		settings.getIntProperty(getPropertyName(id, "keycode"), keycode);
		settings.getIntProperty(getPropertyName(id, "mouse_button"),
				mouseButton);
		settings.getBooleanProperty(getPropertyName(id, "toogleable"),
				toogleable);
	}

	static <G extends Enum<G>> String getPropertyName(G id, String name) {
		return KEYBIND_SETTINGS_PREFIX + (id.toString().toLowerCase()) + "_"
				+ name;
	}

}
