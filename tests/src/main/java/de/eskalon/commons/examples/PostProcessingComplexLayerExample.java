package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.ChainVfxEffect;
import com.crashinvaders.vfx.effects.CrtEffect;
import com.crashinvaders.vfx.effects.CrtEffect.LineStyle;
import com.crashinvaders.vfx.effects.CrtEffect.SizeSource;
import com.crashinvaders.vfx.effects.MotionBlurEffect;
import com.crashinvaders.vfx.effects.VignettingEffect;
import com.crashinvaders.vfx.effects.util.MixEffect.Method;

import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.input.EskalonGameInputProcessor;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.AbstractImageScreen;

public class PostProcessingComplexLayerExample extends AbstractEskalonExample {

	@Override
	protected EskalonApplicationConfiguration getAppConfig() {
		return super.getAppConfig().createPostProcessor();
	}

	@Override
	protected AbstractEskalonScreen initApp() {
		Gdx.graphics.setVSync(false);
		Gdx.input.getInputProcessor()
				.keyDown(EskalonGameInputProcessor.toggleOverlayKey);
		return new TestScreen();
	}

	public class TestScreen extends AbstractImageScreen {

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport2 = new ScreenViewport();

		private CrtEffect a = new CrtEffect(LineStyle.HORIZONTAL_HARD, 2.3f,
				0.1f);
		private ChainVfxEffect b = new VignettingEffect(false);
		private ChainVfxEffect c = new MotionBlurEffect(Method.MIX, 0.8F);

		public TestScreen() {
			super(getPrefWidth(), getPrefHeight());

			postProcessor.addEffects(a, b, c);

			a.setSizeSource(SizeSource.SCREEN);
			a.setDisabled(true);
			b.setDisabled(true);
			c.setDisabled(true);

			setImage(new Texture(Gdx.files.internal("grid1280x720.png")));
		}

		private NestableFrameBuffer fbo = new NestableFrameBuffer(
				Format.RGBA8888, getPrefWidth(), getPrefHeight(), false);

		@Override
		public void render(float delta) {
			/** FIRST PASS: render the effects for the purple rectangle **/
			a.setDisabled(false);

			postProcessor.beginCapture();

			viewport2.apply();
			shapeRenderer.setProjectionMatrix(viewport2.getCamera().combined);

			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.PURPLE);
			shapeRenderer.rect(650, 155, 550, 175);
			shapeRenderer.end();

			postProcessor.endCapture();
			postProcessor.renderEffectsToFbo(fbo, delta);

			a.setDisabled(true);

			/**
			 * SECOND PASS: render a vignette and a motion blur effect for the
			 * whole screen
			 **/
			b.setDisabled(false);
			c.setDisabled(false);

			postProcessor.beginCapture();

			super.render(delta); // Render the background image

			// Render the first pass
			viewport2.apply();
			batch.setProjectionMatrix(viewport2.getCamera().combined);
			batch.begin();
			batch.draw(fbo.getColorBufferTexture(), 0, 0, getPrefWidth(),
					getPrefHeight(), 0, 0, 1, 1);
			batch.end();

			// Render the blue circle
			shapeRenderer.setProjectionMatrix(viewport2.getCamera().combined);
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.ROYAL);
			shapeRenderer.circle(Gdx.input.getX(),
					Gdx.graphics.getHeight() - Gdx.input.getY(), 75);
			shapeRenderer.end();

			postProcessor.endCapture();
			postProcessor.renderEffectsToScreen(delta);

			b.setDisabled(true);
			c.setDisabled(true);
		}

		@Override
		public void resize(int width, int height) {
			super.resize(width, height);
			viewport2.update(width, height, true);
		}

		@Override
		public void dispose() {
			shapeRenderer.dispose();
		}

		@Override
		protected EskalonApplication getApplication() {
			return PostProcessingComplexLayerExample.this;
		}

	}

}
