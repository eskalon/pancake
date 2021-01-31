package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.WaterDistortionEffect;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.screens.AbstractImageScreen;

public class PostProcessingSimpleLayerExample extends AbstractEskalonExample {

	@Override
	protected EskalonApplicationConfiguration getAppConfig() {
		return super.getAppConfig().createPostProcessor();
	}

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen(this));
		return "test-screen";
	}

	public class TestScreen extends AbstractImageScreen {

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport2 = new ScreenViewport();

		public TestScreen(EskalonApplication app) {
			super(getPrefWidth(), getPrefHeight());

			WaterDistortionEffect effect = new WaterDistortionEffect(3.5F,
					2.5F);
			postProcessor.addEffect(effect);

			// Toggle effects via 'M'
			addInputProcessor(new DefaultInputProcessor() {
				@Override
				public boolean keyDown(int keycode) {
					if (keycode == Keys.M) {
						postProcessor.setDisabled(!postProcessor.isDisabled());
					}
					return false;
				}
			});
		}

		@Override
		protected void create() {
			setImage(new Texture(Gdx.files.internal("test.png")));
		}

		@Override
		public void render(float delta) {
			super.render(delta); // Render the background image

			postProcessor.beginCapture();

			viewport2.apply();
			shapeRenderer.setProjectionMatrix(viewport2.getCamera().combined);

			/**
			 * Apply the post processing effect only to the green circle.
			 */
			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.circle(Gdx.input.getX(),
					Gdx.graphics.getHeight() - Gdx.input.getY(), 75);
			shapeRenderer.end();

			postProcessor.endCapture();
			postProcessor.renderEffectsToScreen(delta);
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
		public Color getClearColor() {
			return Color.DARK_GRAY;
		}

		@Override
		protected EskalonApplication getApplication() {
			return PostProcessingSimpleLayerExample.this;
		}

	}

}
