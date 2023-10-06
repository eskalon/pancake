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

package de.eskalon.commons.core;

import java.util.HashMap;
import java.util.Map;

import de.eskalon.commons.screen.transition.ScreenTransition;

/**
 * This class holds information related to the application.
 */
public class EskalonApplicationContext {

	private String appName;
	private String version;
	private Map<String, ScreenTransition> transitions;

	public EskalonApplicationContext(String appName, String version,
			Map<String, ScreenTransition> transitions) {
		this.appName = appName;
		this.version = version;
		this.transitions = transitions;
	}

	public EskalonApplicationContext(String appName, String version) {
		this(appName, version, new HashMap<>());
	}

	public String getAppName() {
		return appName;
	}

	public String getVersion() {
		return version;
	}

	public Map<String, ScreenTransition> getTransitions() {
		return transitions;
	}

	@Override
	public String toString() {
		return "EskalonApplicationContext{appName=" + appName + ",version="
				+ version + ",transitions=" + transitions + "}";
	}

}
