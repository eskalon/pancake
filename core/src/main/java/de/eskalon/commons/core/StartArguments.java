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

/**
 * This class represents the arguments passed to the application from outside.
 */
public class StartArguments {

	private boolean enableDebugLoggingOnStartup;
	private boolean skipSplashScreen;

	/* Builder */
	public static StartArgumentsBuilder create() {
		return new StartArgumentsBuilder();
	}

	public static class StartArgumentsBuilder {

		private StartArguments ret;

		private StartArgumentsBuilder() {
			this.ret = new StartArguments();
		}

		public StartArgumentsBuilder enableDebugLogging() {
			ret.enableDebugLoggingOnStartup = true;
			return this;
		}

		public StartArgumentsBuilder skipSplashScreen() {
			ret.skipSplashScreen = true;
			return this;
		}

		public StartArguments build() {
			return ret;
		}

	}

	/* Class itself */
	private StartArguments() {
		// reduce visibility
	}

	public boolean shouldEnableDebugLogging() {
		return enableDebugLoggingOnStartup;
	}

	public boolean shouldSkipSplashScreen() {
		return skipSplashScreen;
	}

	@Override
	public String toString() {
		return "StartArguments{enableDebugLoggingOnStartup="
				+ enableDebugLoggingOnStartup + ",skipSplashScreen="
				+ skipSplashScreen + "}";
	}

}
