package de.eskalon.commons.examples;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.DefaultInputListener;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.screens.BlankScreen;

public class InputBindingsExample extends AbstractEskalonExample {

	enum MyGameAxisBindings {
		X_AXIS;
	}

	enum MyGameBinaryBindings {
		SPEED_UP;
	}

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen(this));
		return "test-screen";
	}

	public class TestScreen extends BlankScreen {

		private IInputHandler<MyGameAxisBindings, MyGameBinaryBindings> inputHandler;

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport = new ScreenViewport();

		private Vector2 pos = new Vector2(1280 / 2, 720 / 2);
		private Vector2 dir = new Vector2();
		private float vel = 50f;

		public TestScreen(EskalonApplication app) {
			super(app);

			/* Register default bindings */
			IInputHandler.registerAxisBinding(settings,
					MyGameAxisBindings.X_AXIS, Keys.A, Keys.D, -2);
			IInputHandler.registerBinaryBinding(settings,
					MyGameBinaryBindings.SPEED_UP, Keys.SPACE, -2, false);

			/* Create input handler & listener */
			inputHandler = new DefaultInputHandler<>(settings,
					MyGameAxisBindings.class, MyGameBinaryBindings.class);
			addInputProcessor((DefaultInputHandler) inputHandler);

			inputHandler.addListener(
					new DefaultInputListener<MyGameAxisBindings, MyGameBinaryBindings>() {
						@Override
						public boolean on(MyGameBinaryBindings id) {
							if (id == MyGameBinaryBindings.SPEED_UP) {
								vel = 175f;
								return true;
							}
							return false;
						}

						@Override
						public boolean off(MyGameBinaryBindings id) {
							if (id == MyGameBinaryBindings.SPEED_UP) {
								vel = 50f;
								return true;
							}
							return false;
						}

						@Override
						public boolean axisChanged(MyGameAxisBindings id,
								float value) {
							if (id == MyGameAxisBindings.X_AXIS) {
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
