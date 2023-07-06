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

import java.util.Map;

import com.badlogic.gdx.Preferences;

/**
 * Wraps a {@link Preferences} instance to enable automatic flushing after every
 * put call.
 * 
 * @author damios
 */
public class AutoFlushingPreferences implements Preferences {

	private boolean autoFlush = false;
	private Preferences preferences;

	public static AutoFlushingPreferences createInstance(
			Preferences preferences) {
		return new AutoFlushingPreferences(preferences);
	}

	protected AutoFlushingPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public void setAutoFlushing(boolean autoFlush) {
		this.autoFlush = autoFlush;
	}

	public boolean isAutoFlushing() {
		return autoFlush;
	}

	@Override
	public Preferences putBoolean(String key, boolean value) {
		preferences.putBoolean(key, value);
		if (autoFlush)
			preferences.flush();
		return this;
	}

	@Override
	public Preferences putInteger(String key, int value) {
		preferences.putInteger(key, value);
		if (autoFlush)
			preferences.flush();
		return this;
	}

	@Override
	public Preferences putLong(String key, long value) {
		preferences.putLong(key, value);
		if (autoFlush)
			preferences.flush();
		return this;
	}

	@Override
	public Preferences putFloat(String key, float value) {
		preferences.putFloat(key, value);
		if (autoFlush)
			preferences.flush();
		return this;
	}

	@Override
	public Preferences putString(String key, String value) {
		preferences.putString(key, value);
		if (autoFlush)
			preferences.flush();
		return this;
	}

	@Override
	public Preferences put(Map<String, ?> vals) {
		preferences.put(vals);
		if (autoFlush)
			preferences.flush();
		return this;
	}

	@Override
	public boolean getBoolean(String key) {
		return preferences.getBoolean(key);
	}

	@Override
	public int getInteger(String key) {
		return preferences.getInteger(key);
	}

	@Override
	public long getLong(String key) {
		return preferences.getLong(key);
	}

	@Override
	public float getFloat(String key) {
		return preferences.getFloat(key);
	}

	@Override
	public String getString(String key) {
		return preferences.getString(key);
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return preferences.getBoolean(key, defValue);
	}

	@Override
	public int getInteger(String key, int defValue) {
		return preferences.getInteger(key, defValue);
	}

	@Override
	public long getLong(String key, long defValue) {
		return preferences.getLong(key, defValue);
	}

	@Override
	public float getFloat(String key, float defValue) {
		return preferences.getFloat(key, defValue);
	}

	@Override
	public String getString(String key, String defValue) {
		return preferences.getString(key, defValue);
	}

	@Override
	public Map<String, ?> get() {
		return preferences.get();
	}

	@Override
	public boolean contains(String key) {
		return preferences.contains(key);
	}

	@Override
	public void clear() {
		preferences.clear();
	}

	@Override
	public void remove(String key) {
		preferences.remove(key);
	}

	@Override
	public void flush() {
		preferences.flush();
	}

}
