/*
 * Copyright 2021 eskalon
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

/**
 * The class used to configure {@link EskalonApplication}.
 * 
 * @author damios
 */
public class EskalonApplicationConfiguration {

	String appName = "Eskalon App";
	boolean createPostProcessor;
	boolean enableDebugLoggingOnStartup;
	boolean provideDepthBuffers;
	boolean skipSplashScreen;

	public EskalonApplicationConfiguration() {
		this.createPostProcessor = false;
		this.enableDebugLoggingOnStartup = false;
		this.provideDepthBuffers = false;
		this.skipSplashScreen = false;
	}

	public EskalonApplicationConfiguration createPostProcessor() {
		this.createPostProcessor = true;
		return this;
	}

	public EskalonApplicationConfiguration enableDebugLoggingOnStartup() {
		this.enableDebugLoggingOnStartup = true;
		return this;
	}

	public EskalonApplicationConfiguration provideDepthBuffers() {
		this.provideDepthBuffers = true;
		return this;
	}

	public EskalonApplicationConfiguration skipSplashScreen() {
		this.skipSplashScreen = true;
		return this;
	}

	public EskalonApplicationConfiguration setAppName(String appName) {
		this.appName = appName;
		return this;
	}

}
