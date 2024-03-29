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

package de.eskalon.commons.log;

import java.util.Date;

import com.badlogic.gdx.ApplicationLogger;

/**
 * A logger for libGDX's desktop backends which also logs the current time.
 * 
 * @author damios
 */
public class EskalonDesktopLogger implements ApplicationLogger {

	private final String LOG_FORMAT = "%tH:%<tM:%<tS.%<tL - [%s] %s";

	@Override
	public void log(String tag, String message) {
		System.out.println(String.format(LOG_FORMAT, new Date(), tag, message));
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		System.out.println(String.format(LOG_FORMAT, new Date(), tag, message));
		exception.printStackTrace(System.out);
	}

	@Override
	public void error(String tag, String message) {
		System.err.println(String.format(LOG_FORMAT, new Date(), tag, message));
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		System.err.println(String.format(LOG_FORMAT, new Date(), tag, message));
		exception.printStackTrace(System.err);
	}

	@Override
	public void debug(String tag, String message) {
		System.out.println(String.format(LOG_FORMAT, new Date(), tag, message));
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		System.out.println(String.format(LOG_FORMAT, new Date(), tag, message));
		exception.printStackTrace(System.out);
	}

}
