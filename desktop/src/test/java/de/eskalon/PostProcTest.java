package de.eskalon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.MotionBlurEffect;
import com.crashinvaders.vfx.effects.util.MixEffect.Method;

import de.damios.guacamole.gdx.StartOnFirstThreadHelper;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.input.EskalonGameInputProcessor;
import de.eskalon.commons.screens.BlankEskalonScreen;

public class PostProcTest extends EskalonApplication {

	private static final int WIDTH = 1280, HEIGHT = 720;

	public PostProcTest() {
		super(true, false);
	}

	@Override
	protected String initApp() {
		screenManager.addScreen("test", new TestScreen(this));
		Gdx.input.getInputProcessor()
				.keyDown(EskalonGameInputProcessor.toggleOverlayKey);
		return "test";
	}

	public class TestScreen extends BlankEskalonScreen {
		private PostProcessingPipeline postProcessor = new PostProcessingPipeline(
				getScreenManager(), WIDTH, HEIGHT, false);
		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport = new FitViewport(WIDTH, HEIGHT);

		public TestScreen(EskalonApplication app) {
			super(app);

			postProcessor.addEffect(new BloomEffect());
			postProcessor.addEffect(new MotionBlurEffect(Method.MAX, 0.9F));
		}

		@Override
		public void render(float delta) {
			postProcessor.beginCapture();

			/**********************/

			viewport.apply(); // you need to apply your viewport first
			shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.circle(Gdx.input.getX(),
					Gdx.graphics.getHeight() - Gdx.input.getY(), 75);
			shapeRenderer.end();

			/**********************/

			postProcessor.endCapture();
			postProcessor.renderEffectsOntoScreen(delta);
		}

		@Override
		public void resize(int width, int height) {
			viewport.update(width, height, true);
			postProcessor.resize(width, height);
		}

		@Override
		public void dispose() {
			postProcessor.dispose();
			shapeRenderer.dispose();
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
			config.setWindowedMode(WIDTH, HEIGHT);
			config.setResizable(true);
			config.useVsync(false);
			config.setForegroundFPS(144);

			try {
				new Lwjgl3Application(new PostProcTest(), config);
			} catch (Exception e) {
				System.err.println(
						"An unexpected error occurred while starting the game:");
				e.printStackTrace();
				System.exit(-1);
			}
		});
	}

}
