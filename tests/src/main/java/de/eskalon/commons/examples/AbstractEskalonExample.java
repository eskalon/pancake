package de.eskalon.commons.examples;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;

/**
 * The base class for all examples.
 * 
 * @author damios
 */
public abstract class AbstractEskalonExample extends EskalonApplication {

	@Override
	protected EskalonApplicationConfiguration getAppConfig() {
		return super.getAppConfig().setAppName("Eskalon Tests")
				.enableDebugLoggingOnStartup().skipSplashScreen();
	}

	public int getPrefWidth() {
		return 1280;
	}

	public int getPrefHeight() {
		return 720;
	}

	public boolean isResizable() {
		return false;
	}

}
