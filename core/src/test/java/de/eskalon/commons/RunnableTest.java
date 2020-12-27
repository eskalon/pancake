package de.eskalon.commons;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
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
		public TestScreen(EskalonApplication app) {
			super(app);
		}
	}

	public static void main(String[] args) {
		StartOnFirstThreadHelper.executeIfJVMValid(() -> {
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setTitle("Test App");
			config.setWindowedMode(1280, 720);
			config.setResizable(false);
			config.useVsync(false);
			//config.setForegroundFPS(60);

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
