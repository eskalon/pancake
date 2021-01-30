package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.MotionBlurEffect;
import com.crashinvaders.vfx.effects.util.MixEffect.Method;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.input.EskalonGameInputProcessor;
import de.eskalon.commons.screens.BlankEskalonScreen;

public class PostProcessingExample extends AbstractEskalonExample {

	@Override
	protected EskalonApplicationConfiguration getAppConfig() {
		return super.getAppConfig().createPostProcessor();
	}

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen(this));
		Gdx.input.getInputProcessor()
				.keyDown(EskalonGameInputProcessor.toggleOverlayKey);
		return "test-screen";
	}

	public class TestScreen extends BlankEskalonScreen {

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport = new ScreenViewport();

		public TestScreen(EskalonApplication app) {
			super(app);

			BloomEffect effect1 = new BloomEffect();
			MotionBlurEffect effect2 = new MotionBlurEffect(Method.MAX, 0.9F);
			postProcessor.addEffects(effect1, effect2);

			// Toggle effects via 'M'
			addInputProcessor(new DefaultInputProcessor() {
				@Override
				public boolean keyDown(int keycode) {
					if (keycode == Keys.M) {
						if (postProcessor.hasEffects())
							postProcessor.removeAllEffects();
						else
							postProcessor.addEffects(effect1, effect2);
					}
					return false;
				}
			});
		}

		@Override
		public void render(float delta) {
			postProcessor.beginCapture();

			/**********************/

			viewport.apply();
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
		}

		@Override
		public void dispose() {
			shapeRenderer.dispose();
		}

		@Override
		public Color getClearColor() {
			return Color.DARK_GRAY;
		}

	}

}
