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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import org.jspecify.annotations.Nullable;

import de.damios.guacamole.annotations.GwtIncompatible;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;

/**
 * Machine identification utilities.
 * 
 * @author damios
 */
@GwtIncompatible
public final class MachineIdentificationUtils {

	private static final Logger LOG = LoggerService
			.getLogger(MachineIdentificationUtils.class);

	private static final String HOSTNAME_COMMAND = "hostname";
	private static final String UNKNOWN_HOST_PREFIX = "_unidentified";

	private MachineIdentificationUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return a simple identifier for the machine. Is usually the (host)name.
	 *         If one cannot be determined, the MAC address of the first found
	 *         network adapter is returned. If that doesn't work either,
	 *         <code>{@value #UNKNOWN_HOST_PREFIX}</code> is returned followed
	 *         by the {@linkplain System#currentTimeMillis() current time}.
	 */
	public static String getSimpleIdentifier() {
		String id = getHostname();

		if (id != null) {
			id.trim().toLowerCase().replace(".home", "");
		} else {
			byte[] b = getMACAddress();

			if (b != null)
				id = String.valueOf(b);
		}

		if (id == null)
			id = UNKNOWN_HOST_PREFIX
					+ String.valueOf(System.currentTimeMillis());

		return id;
	}

	public static @Nullable String getHostname() {
		try (Scanner s = new Scanner(Runtime.getRuntime().exec(HOSTNAME_COMMAND)
				.getInputStream());) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		} catch (IOException e) {
			LOG.warn(e.getLocalizedMessage());
			return null;
		}
	}

	public static @Nullable byte[] getMACAddress() {
		try {
			NetworkInterface nwi = NetworkInterface
					.getByInetAddress(InetAddress.getLocalHost());

			if (nwi != null)
				return nwi.getHardwareAddress();

		} catch (SocketException | UnknownHostException e) {
			LOG.warn(e.getLocalizedMessage());
		}
		return null;
	}

}
