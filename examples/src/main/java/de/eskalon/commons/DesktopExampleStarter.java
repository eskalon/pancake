package de.eskalon.commons;

import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.eskalon.commons.utils.JavaProcess;

// Based on libGDX's Lwjgl3TestStarter
public class DesktopExampleStarter {

	public static void main(String[] args) {
		StartOnFirstThreadHelper.executeOnValidJVM(() -> {
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setWindowedMode(500, 415);
			config.setTitle("Desktop Example Starter");
			config.setResizable(false);
			config.useVsync(false);
			config.setForegroundFPS(144);
			// config.useOpenGL3(true, 3, 2);

			new Lwjgl3Application(new TestChooser(), config);
		}, args);
	}

	static class TestChooser extends ApplicationAdapter {
		private Stage stage;
		private Skin skin;
		private TextButton lastClickedTestButton;

		@Override
		public void create() {
			final Preferences prefs = Gdx.app
					.getPreferences("eskalon-desktop-examples");

			stage = new Stage(new ScreenViewport());
			Gdx.input.setInputProcessor(stage);
			skin = new Skin(Gdx.files.internal("uiskin-min/uiskin.json"));

			Table container = new Table();
			stage.addActor(container);
			container.setFillParent(true);

			Table table = new Table();

			ScrollPane scroll = new ScrollPane(table, skin);
			scroll.setSmoothScrolling(false);
			scroll.setFadeScrollBars(false);
			stage.setScrollFocus(scroll);

			int tableSpace = 4;
			table.pad(10).defaults().expandX().space(tableSpace);
			for (final Class<?> testClass : EskalonExamples.TESTS) {
				String testName = testClass.getSimpleName();
				final TextButton testButton = new TextButton(testName, skin);
				// testButton.setDisabled(!options.isTestCompatible(testName));
				testButton.setName(testName);
				table.add(testButton).fillX();
				table.row();
				testButton.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						// A new process is created so that the different static
						// DI instances do not interfere with each other
						JavaProcess.exec(EskalonExamples.class,
								List.of(testName));

						System.out.println(
								" --- Started test: " + testName + " --- ");
						prefs.putString("LastTest", testName);
						prefs.flush();

						if (testButton != lastClickedTestButton) {
							testButton.setColor(Color.CYAN);
							if (lastClickedTestButton != null) {
								lastClickedTestButton.setColor(Color.WHITE);
							}
							lastClickedTestButton = testButton;
						}
					}
				});
			}

			container.add(scroll).expand().fill();
			container.row();

			lastClickedTestButton = (TextButton) table
					.findActor(prefs.getString("LastTest"));
			if (lastClickedTestButton != null) {
				lastClickedTestButton.setColor(Color.CYAN);
				scroll.layout();
				float scrollY = lastClickedTestButton.getY()
						+ scroll.getScrollHeight() / 2
						+ lastClickedTestButton.getHeight() / 2 + tableSpace * 2
						+ 20;
				scroll.scrollTo(0, scrollY, 0, 0, false, false);

				// Since ScrollPane takes some time for scrolling to a position,
				// we just "fake" time
				stage.act(1f);
				stage.act(1f);
				stage.draw();
			}
		}

		@Override
		public void render() {
			Gdx.gl.glClearColor(1, 1, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act();
			stage.draw();
		}

		@Override
		public void resize(int width, int height) {
			stage.getViewport().update(width, height, true);
		}

		@Override
		public void dispose() {
			skin.dispose();
			stage.dispose();
		}
	}
}