package de.eskalon.commons.examples;

import com.badlogic.gdx.Input.Buttons;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.input.DefaultInputHandler;
import de.eskalon.commons.input.DefaultInputListener;
import de.eskalon.commons.input.IInputHandler;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;

public class InputMouseDraggedExample extends AbstractEskalonApplication {

	enum TestScreenAxisBindingType {
	}

	enum TestScreenBinaryBindingType {
		MOUSE_CLICK;
	}

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		EskalonInjector.getInstance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public class TestScreen extends BlankScreen {

		private IInputHandler<TestScreenAxisBindingType, TestScreenBinaryBindingType> inputHandler;

		@Inject // needed so the class does not have to be static
		public TestScreen() {
			/* Register default bindings */
			IInputHandler.registerBinaryBinding(settings,
					TestScreenBinaryBindingType.MOUSE_CLICK, -2, Buttons.LEFT,
					false);

			/* Create input handler & listener */
			inputHandler = new DefaultInputHandler<>(settings,
					TestScreenAxisBindingType.class,
					TestScreenBinaryBindingType.class);

			// The input processor is used to model the default libGDX behaviour
			addInputProcessor(new DefaultInputProcessor() {
				int startX, startY;
				int button;

				@Override
				public boolean touchDown(int screenX, int screenY, int pointer,
						int button) {
					if (button == Buttons.LEFT) {
						startX = screenX;
						startY = screenY;
					}
					this.button = button;
					return false; // so events still get passed to the input
									// handler
				}

				@Override
				public boolean touchDragged(int screenX, int screenY,
						int pointer) {
					if (button == Buttons.LEFT) {
						System.out.println("InputProcessor:");
						System.out.println(screenX - startX);
						System.out.println(startY - screenY);
						startX = screenX;
						startY = screenY;
					}
					return false; // so events still get passed to the input
									// handler
				}
			});

			// Compare the default behaviour to the input handler
			addInputProcessor((DefaultInputHandler) inputHandler);
			inputHandler.addListener(
					new DefaultInputListener<TestScreenAxisBindingType, TestScreenBinaryBindingType>() {
						boolean start;
						boolean isTouchDown;
						int startX, startY;

						@Override
						public boolean on(TestScreenBinaryBindingType id) {
							if (id == TestScreenBinaryBindingType.MOUSE_CLICK) {
								isTouchDown = true;
								start = true;
								return true;
							}
							return false;
						}

						@Override
						public boolean off(TestScreenBinaryBindingType id) {
							if (id == TestScreenBinaryBindingType.MOUSE_CLICK) {
								isTouchDown = false;
								start = false;
								return true;
							}
							return false;
						}

						@Override
						public boolean moved(int screenX, int screenY) {
							if (start) { // moved() is called first by
											// touchDown()!
								startX = screenX;
								startY = screenY;
								start = false;
								return true;
							} else if (isTouchDown) {
								System.out.println("InputHandler:");
								System.out.println(screenX - startX);
								System.out.println(startY - screenY);
								startX = screenX;
								startY = screenY;
								return true;
							}
							return false;
						}
					});
		}

	}

}
