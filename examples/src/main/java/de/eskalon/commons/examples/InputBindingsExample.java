package de.eskalon.commons.examples;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.DefaultInputListener;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;

public class InputBindingsExample extends AbstractEskalonApplication {

	enum TestScreenAxisBindingType {
		X_AXIS;
	}

	enum TestScreenBinaryBindingType {
		SPEED_UP;
	}

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		EskalonInjector.instance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public class TestScreen extends BlankScreen {

		private IInputHandler<TestScreenAxisBindingType, TestScreenBinaryBindingType> inputHandler;

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport = new ScreenViewport();

		private Vector2 pos = new Vector2(1280 / 2, 720 / 2);
		private Vector2 dir = new Vector2();
		private float vel = 50f;

		@Inject // needed so the class does not have to be static
		public TestScreen() {
			/* Register default bindings */
			IInputHandler.registerAxisBinding(settings,
					TestScreenAxisBindingType.X_AXIS, Keys.A, Keys.D, -2);
			IInputHandler.registerBinaryBinding(settings,
					TestScreenBinaryBindingType.SPEED_UP, Keys.SPACE, -2,
					false);

			/* Create input handler & listener */
			inputHandler = new DefaultInputHandler<>(settings,
					TestScreenAxisBindingType.class,
					TestScreenBinaryBindingType.class);
			addInputProcessor((DefaultInputHandler) inputHandler);

			inputHandler.addListener(
					new DefaultInputListener<TestScreenAxisBindingType, TestScreenBinaryBindingType>() {
						@Override
						public boolean on(TestScreenBinaryBindingType id) {
							if (id == TestScreenBinaryBindingType.SPEED_UP) {
								vel = 175f;
								return true;
							}
							return false;
						}

						@Override
						public boolean off(TestScreenBinaryBindingType id) {
							if (id == TestScreenBinaryBindingType.SPEED_UP) {
								vel = 50f;
								return true;
							}
							return false;
						}

						@Override
						public boolean axisChanged(TestScreenAxisBindingType id,
								float value) {
							if (id == TestScreenAxisBindingType.X_AXIS) {
								dir.x = value;
								return true;
							}
							return false;
						}
					});
		}

		@Override
		public void render(float delta) {
			/* Process inputs */
			pos.x += dir.x * vel * delta;
			pos.y += dir.y * vel * delta;

			/* Render */
			viewport.apply();
			shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

			shapeRenderer.begin(ShapeType.Filled);
			shapeRenderer.setColor(Color.GREEN);
			shapeRenderer.circle(pos.x - 10, pos.y - 10, 20);
			shapeRenderer.end();
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
