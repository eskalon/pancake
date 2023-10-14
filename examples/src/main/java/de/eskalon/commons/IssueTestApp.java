package de.eskalon.commons;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.damios.guacamole.gdx.log.Logger;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.core.EskalonApplicationStarter;
import de.eskalon.commons.core.StartArguments;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.providers.LoggerProvider.Log;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;

/**
 * A simple runnable app to test issues.
 */
public class IssueTestApp extends AbstractEskalonApplication {

	public IssueTestApp() {
		super(EskalonApplicationConfiguration.create().build());
	}

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		EskalonInjector.instance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public static class TestScreen extends BlankScreen {
		private @Log(TestScreen.class) @Inject Logger LOG;

		@Override
		public void show() {
			super.show();
			LOG.error("Test");
		}

		@Override
		public void render(float delta) {
		}

		@Override
		public Color getClearColor() {
			return Color.FIREBRICK;
		}
	}

	public static void main(String[] args) {
		StartOnFirstThreadHelper.executeIfJVMValid(() -> {
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setTitle("Test App");
			config.setWindowedMode(1280, 720);
			config.setResizable(false);
			config.useVsync(false);
			config.setForegroundFPS(60);

			try {
				new Lwjgl3Application(new EskalonApplicationStarter("Test App",
						IssueTestApp.class,
						StartArguments.create().enableDebugLogging().build()),
						config);
			} catch (Exception e) {
				System.err.println(
						"An unexpected error occurred while starting the app:");
				e.printStackTrace();
				System.exit(-1);
			}
		});
	}

}
