package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.WaterDistortionEffect;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.input.EskalonApplicationInputProcessor;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.AbstractImageScreen;

public class PostProcessingSimpleLayerExample
		extends AbstractEskalonApplication {

	public PostProcessingSimpleLayerExample() {
		super(EskalonApplicationConfiguration.create().createPostProcessor()
				.build());
	}

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		Gdx.graphics.setVSync(false);
		Gdx.input.getInputProcessor()
				.keyDown(EskalonApplicationInputProcessor.toggleOverlayKey);

		EskalonInjector.getInstance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public class TestScreen extends AbstractImageScreen {

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport2 = new ScreenViewport();

		@Inject // needed so the class does not have to be static
		public TestScreen() {
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

	}

}
