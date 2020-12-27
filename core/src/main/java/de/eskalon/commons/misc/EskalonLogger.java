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

package de.eskalon.commons.misc;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.ApplicationLogger;

/**
 * @author damios
 */
public class EskalonLogger implements ApplicationLogger {

	public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;
	private final String INFO_LOG_FORMAT = "%tT - [INFO ] [%s]:  %s";
	private final String ERROR_LOG_FORMAT = "%tT - [ERROR] [%s]:  %s";
	private final String DEBUG_LOG_FORMAT = "%tT - [DEBUG] [%s]:  %s";

	private static final String formatMessage(String formatString, String tag,
			String message) {
		return String.format(formatString, new Date(), tag, message);
	}

	@Override
	public void log(String tag, String message) {
		System.out.println(formatMessage(INFO_LOG_FORMAT, tag, message));
	}

	@Override
	public void log(String tag, String message, Throwable exception) {
		System.out.println(formatMessage(INFO_LOG_FORMAT, tag, message));
		exception.printStackTrace(System.out);
	}

	@Override
	public void error(String tag, String message) {
		System.out.println(formatMessage(ERROR_LOG_FORMAT, tag, message));
	}

	@Override
	public void error(String tag, String message, Throwable exception) {
		System.out.println(formatMessage(ERROR_LOG_FORMAT, tag, message));
		exception.printStackTrace(System.err);
	}

	@Override
	public void debug(String tag, String message) {
		System.out.println(formatMessage(DEBUG_LOG_FORMAT, tag, message));
	}

	@Override
	public void debug(String tag, String message, Throwable exception) {
		System.out.println(formatMessage(DEBUG_LOG_FORMAT, tag, message));
		exception.printStackTrace(System.out);
	}

}
