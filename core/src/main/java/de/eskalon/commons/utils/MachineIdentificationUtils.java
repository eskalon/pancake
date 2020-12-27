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

package de.eskalon.commons.utils;

import java.io.IOException;
import java.util.Scanner;

import de.damios.guacamole.annotations.GwtIncompatible;

/**
 * Machine identification utils.
 * 
 * @author damios
 */
@GwtIncompatible
public final class MachineIdentificationUtils {

	private static final String HOSTNAME_COMMAND = "hostname";
	private static final String UNKNOWN_HOST_PREFIX = "_u-";

	private MachineIdentificationUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the (host)name of the machine. If the name cannot be found,
	 *         <code>{@value #UNKNOWN_HOST_PREFIX}</code> is returned followed
	 *         by the {@linkplain System#currentTimeMillis() current time
	 *         millis}.
	 */
	public static String getHostname() {
		try {
			return getHostnameCommandResult().trim().toLowerCase()
					.replace(".home", "");
		} catch (IOException e) {
			return UNKNOWN_HOST_PREFIX
					+ String.valueOf(System.currentTimeMillis());
		}
	}

	private static String getHostnameCommandResult() throws IOException {
		try (Scanner s = new Scanner(Runtime.getRuntime().exec(HOSTNAME_COMMAND)
				.getInputStream());) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

}
