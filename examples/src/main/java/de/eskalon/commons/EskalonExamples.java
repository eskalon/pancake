package de.eskalon.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jspecify.annotations.Nullable;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.core.EskalonApplicationStarter;
import de.eskalon.commons.core.StartArguments;
import de.eskalon.commons.examples.ImageScreenExample;
import de.eskalon.commons.examples.InputBindingsExample;
import de.eskalon.commons.examples.InputMouseDraggedExample;
import de.eskalon.commons.examples.MusicExample;
import de.eskalon.commons.examples.PositionalAudioExample;
import de.eskalon.commons.examples.PostProcessingComplexLayerExample;
import de.eskalon.commons.examples.PostProcessingExample;
import de.eskalon.commons.examples.PostProcessingSimpleLayerExample;

public class EskalonExamples {

	public static final List<Class<? extends AbstractEskalonApplication>> TESTS = new ArrayList<>(
			Arrays.asList(
			// @formatter:off
					ImageScreenExample.class,
					InputBindingsExample.class,
					InputMouseDraggedExample.class,
					MusicExample.class,
					PositionalAudioExample.class,
					PostProcessingExample.class,
					PostProcessingSimpleLayerExample.class,
					PostProcessingComplexLayerExample.class
			// @formatter:on
			));

	public static void main(String[] args) {
		// PLEASE NOTE: This class should not be executed manually! Use
		// DesktopExampleStarter instead!

		Class<? extends AbstractEskalonApplication> testAppClazz = forName(
				args[0]);
		Preconditions.checkNotNull(testAppClazz);

		StartOnFirstThreadHelper.executeOnValidJVM(() -> {
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setTitle(args[0]);
			config.setWindowedMode(1280, 720);
			config.setResizable(false);
			config.useVsync(false);
			config.setForegroundFPS(150);

			try {
				new Lwjgl3Application(
						new EskalonApplicationStarter(args[0], testAppClazz,
								StartArguments.create().enableDebugLogging()
										.skipSplashScreen().build()),
						config);
			} catch (Exception e) {
				System.err.println(
						"An unexpected error occurred while starting the test application:");
				e.printStackTrace();
				System.exit(-1);
			}
		}, args);
	}

	private static @Nullable Class<? extends AbstractEskalonApplication> forName(
			String name) {
		for (Class<? extends AbstractEskalonApplication> clazz : TESTS) {
			if (clazz.getSimpleName().equals(name))
				return clazz;
		}
		return null;
	}

}
