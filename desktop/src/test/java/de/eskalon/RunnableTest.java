package de.eskalon;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.BlankEskalonScreen;

public class RunnableTest extends EskalonApplication {

	public RunnableTest() {
		super(true, false);
	}

	@Override
	protected String initApp() {
		screenManager.addScreen("test", new TestScreen(this));
		return "test";
	}

	public class TestScreen extends BlankEskalonScreen {
		private final Logger LOG = LoggerService.getLogger(TestScreen.class);

		public TestScreen(EskalonApplication app) {
			super(app);
		}

		@Override
		public void show() {
			LOG.error("Test");
			super.show();
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
				new Lwjgl3Application(new RunnableTest(), config);
			} catch (Exception e) {
				System.err.println(
						"An unexpected error occurred while starting the game:");
				e.printStackTrace();
				System.exit(-1);
			}
		});
	}

}
