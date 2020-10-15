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

import com.badlogic.gdx.Preferences;

/**
 * A preferences wrapper that adds some convenience methods.
 * 
 * @author damios
 */
public class BetterPreferences extends AutoFlushingPreferences {

	public static BetterPreferences createInstance(Preferences preferences) {
		return new BetterPreferences(preferences);
	}

	protected BetterPreferences(Preferences preferences) {
		super(preferences);
	}

	public Preferences increaseInt(String key, int increase) {
		return putInteger(key, increase + getInteger(key));
	}

	public Preferences increaseFloat(String key, float increase) {
		return putFloat(key, increase + getFloat(key));
	}

	public Preferences invertBoolean(String key) {
		return putBoolean(key, !getBoolean(key));
	}

}
