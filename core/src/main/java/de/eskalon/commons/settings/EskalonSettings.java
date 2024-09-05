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

import java.util.HashMap;

import com.badlogic.gdx.Gdx;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.log.Logger;
import de.eskalon.commons.core.EskalonApplicationContext;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Singleton;
import de.eskalon.commons.inject.providers.LoggerProvider.Log;

/**
 * This class manages game settings via properties.
 */
public class EskalonSettings {

	private static final String FIRST_STARTUP_SETTING = "is_first_startup";

	private @Inject @Log(EskalonSettings.class) Logger LOG;

	protected final AutoFlushingPreferences preferences;

	private final HashMap<String, BooleanProperty> booleanProperties = new HashMap<>();
	private final HashMap<String, FloatProperty> floatProperties = new HashMap<>();
	private final HashMap<String, IntProperty> intProperties = new HashMap<>();

	private final boolean isFirstStartup;

	/**
	 * @param fileName
	 *            the name of the preferences file
	 */
	@Inject
	@Singleton
	public EskalonSettings(EskalonApplicationContext appContext) {
		Preconditions.checkNotNull(appContext);

		String fileName = appContext.getAppName().trim().replace(" ", "-")
				.toLowerCase();

		this.preferences = AutoFlushingPreferences
				.createInstance(Gdx.app.getPreferences(fileName));
		this.preferences.setAutoFlushing(true);
		this.preferences.setFlushSavely(true);

		this.isFirstStartup = this.preferences.getBoolean(FIRST_STARTUP_SETTING,
				true);
		if (this.isFirstStartup) // set flag to false for future runs
			this.preferences.putBoolean(FIRST_STARTUP_SETTING, false);
	}

	// First Startup
	/**
	 * @return {@code true} when the game is started for the very first time
	 */
	public boolean isFirstStartup() {
		return isFirstStartup;
	}

	/*
	 * PROPERTIES
	 */
	// Boolean Properties
	public BooleanProperty getBooleanProperty(String name) {
		return getBooleanProperty(name, false);
	}

	public BooleanProperty getBooleanProperty(String name,
			boolean defaultValue) {
		return booleanProperties.computeIfAbsent(name, k -> {
			boolean value = preferences.getBoolean(name, defaultValue);
			return new BooleanProperty(value);
		});
	}

	public void setBooleanProperty(String name, boolean value) {
		preferences.putBoolean(name, value);
		getBooleanProperty(name, value).set(value);
	}

	// Float Properties
	public FloatProperty getFloatProperty(String name) {
		return getFloatProperty(name, 0F);
	}

	public FloatProperty getFloatProperty(String name, float defaultValue) {
		return floatProperties.computeIfAbsent(name, k -> {
			float value = preferences.getFloat(name, defaultValue);
			return new FloatProperty(value);
		});
	}

	public void setFloatProperty(String name, float value) {
		preferences.putFloat(name, value);
		getFloatProperty(name, value).set(value);
	}

	// Integer Properties
	public IntProperty getIntProperty(String name) {
		return getIntProperty(name, 0);
	}

	public IntProperty getIntProperty(String name, int defaultValue) {
		return intProperties.computeIfAbsent(name, k -> {
			int value = preferences.getInteger(name, defaultValue);
			return new IntProperty(value);
		});
	}

	public void setIntProperty(String name, int value) {
		preferences.putInteger(name, value);
		getIntProperty(name, value).set(value);
	}

}
