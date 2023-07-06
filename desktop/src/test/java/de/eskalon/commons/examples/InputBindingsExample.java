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
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.input.IInputHandler.AxisBindingListener;
import de.eskalon.commons.input.IInputHandler.BinaryBindingListener;
import de.eskalon.commons.screens.BlankScreen;
import de.eskalon.commons.settings.EskalonSettings;

public class InputBindingsExample extends AbstractEskalonExample {

	enum MyGameBinding {
		CROUCH, X_AXIS;
	}

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen(this));
		return "test-screen";
	}

	public class TestScreen extends BlankScreen {

		private IInputHandler<MyGameBinding> inputHandler;

		private ShapeRenderer shapeRenderer = new ShapeRenderer();
		private Viewport viewport = new ScreenViewport();

		private Vector2 pos = new Vector2(1280 / 2, 720 / 2);
		private Vector2 dir = new Vector2();
		private float vel = 50f;

		public TestScreen(EskalonApplication app) {
			super(app);
			inputHandler = new DefaultInputHandler<>(settings);
			addInputProcessor((DefaultInputHandler) inputHandler);

			inputHandler.registerBinaryBinding(MyGameBinding.CROUCH, Keys.SPACE,
					-2, true);
			inputHandler.registerAxisBinding(MyGameBinding.X_AXIS, Keys.A,
					Keys.D, -2);

			inputHandler.addBinaryBindingListener(
					new BinaryBindingListener<MyGameBinding>() {
						@Override
						public boolean on(MyGameBinding id) {
							if (id == MyGameBinding.CROUCH) {
								vel = 175f;
								return true;
							}
							return false;
						}

						@Override
						public boolean off(MyGameBinding id) {
							if (id == MyGameBinding.CROUCH) {
								vel = 50f;
								return true;
							}
							return false;
						}
					});

			inputHandler.addAxisBindingListener(
					new AxisBindingListener<MyGameBinding>() {
						@Override
						public boolean axisChanged(MyGameBinding id,
								float value) {
							if (id == MyGameBinding.X_AXIS) {
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
