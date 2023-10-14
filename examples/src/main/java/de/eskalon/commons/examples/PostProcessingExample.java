package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.MotionBlurEffect;
import com.crashinvaders.vfx.effects.util.MixEffect.Method;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.core.EskalonApplicationConfiguration;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.input.EskalonApplicationInputProcessor;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;

public class PostProcessingExample extends AbstractEskalonApplication {

	public PostProcessingExample() {
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

	public class TestScreen extends BlankScreen {

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport = new ScreenViewport();

		@Inject // needed so the class does not have to be static
		public TestScreen() {
			BloomEffect effect1 = new BloomEffect();
			MotionBlurEffect effect2 = new MotionBlurEffect(Method.MAX, 0.9F);
			postProcessor.addEffects(effect1, effect2);

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
		public void render(float delta) {
			postProcessor.beginCapture();

			// Motion blur and bloom both require the screen to be cleared with
			// a solid color, otherwise there will be ugly artifacts!
			// Alternatively, a background image etc. can be rendered.
			//
			// This is also the reason, why those two effects cannot be used for
			// layers which are only part of a screen. In those cases, the rest
			// of the screen, which is not part of the layer, would have to be
			// cleared with a solid color, making the layer useless.
			ScreenUtils.clear(0.25F, 0.25F, 0.25F, 1F);

			/** RENDER THE ACTUAL SCENE **/

			viewport.apply();
			shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.circle(Gdx.input.getX(),
					Gdx.graphics.getHeight() - Gdx.input.getY(), 75);
			shapeRenderer.end();

			/**********************/

			postProcessor.endCapture();
			postProcessor.renderEffectsToScreen(delta);
		}

		@Override
		public void resize(int width, int height) {
			viewport.update(width, height, true);
		}

		@Override
		public void dispose() {
			shapeRenderer.dispose();
		}

	}

}
