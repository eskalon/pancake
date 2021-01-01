package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;


/**
 * Starts the application for the desktop-based builds.
 */
public class DesktopLauncher {

	/**
	 * The entry point for the whole application on desktop systems.
	 *
	 * @param args
	 *            The start arguments.
	 */
	public static void main(String[] args) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

		config.setWindowedMode(1920, 1080);
		config.useVsync(false);

//		config.setWindowIcon(FileType.Internal, "icon16.png", "icon32.png",
//				"icon48.png");

		config.useOpenGL3(true, 3, 2);
		ShaderProgram.prependVertexCode = "#version 150\n";
		ShaderProgram.prependFragmentCode = "#version 150\n";

		try {
			// Start the game
			new Lwjgl3Application(new GraphicsDebugApplication(), config);
		} catch (Exception e) {
			System.err.println(
					"An unexpected error occurred while starting the game!");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
