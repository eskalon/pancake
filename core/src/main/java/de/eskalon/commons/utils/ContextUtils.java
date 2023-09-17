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

package de.eskalon.commons.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;

import de.damios.guacamole.annotations.GwtIncompatible;
import de.eskalon.commons.core.AbstractEskalonApplication;

/**
 * Development utils.
 * 
 * @author damios
 */
public final class ContextUtils {

	private ContextUtils() {
		throw new UnsupportedOperationException();
	}

	@GwtIncompatible
	// TODO super-source: return !GWT.isProdMode() && GWT.isClient();
	public static boolean isInDevEnv() {
		return AbstractEskalonApplication.class.getPackage()
				.getImplementationVersion() == null;
	}

	public static String getVersion() {
		return isInDevEnv() ? "Development Build"
				: (Gdx.app.getType() != ApplicationType.WebGL
						? AbstractEskalonApplication.class.getPackage()
								.getImplementationVersion()
						: "Version undefined");
	}

}
