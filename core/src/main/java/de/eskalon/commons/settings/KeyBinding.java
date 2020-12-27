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

package de.eskalon.commons.settings;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Preferences;

/**
 * This class denotes a key binding for a certain game action and is used to
 * synch changes from {@link Preferences} to the classes utilizing keyboard
 * events.
 * 
 * @author damios
 * @see EskalonSettings#setDefaultKeybind(String, int)
 * @see EskalonSettings#getKeybind(String)
 */
public class KeyBinding {

	public static final int KEYCODE_ANY_BUTTON = -1;
	public static final int KEYCODE_NOT_SET = -2;

	private int keycode;

	public KeyBinding(int keycode) {
		this.keycode = keycode;
	}

	public boolean isTriggered(int keycode) {
		return this.keycode == keycode || this.keycode == KEYCODE_ANY_BUTTON;
	}

	void setKeycode(int keycode) {
		this.keycode = keycode;
	}

	public int getBoundKeycode() {
		return keycode;
	}

	public String toString() {
		if (keycode == KEYCODE_NOT_SET)
			return "";
		if (keycode == KEYCODE_ANY_BUTTON)
			return "[Any]";
		return Keys.toString(keycode);
	}

}
