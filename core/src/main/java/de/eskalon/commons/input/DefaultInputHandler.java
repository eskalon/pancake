/*
 * Copyright 2023 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.input;

import java.util.Iterator;
import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.screen.ManagedScreen;
import de.eskalon.commons.settings.BooleanProperty;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.settings.IntProperty;

/**
 * This class is the main implementation of {@link IInputHandler}.
 * <p>
 * It has to be added as an input processor to libGDX (e.g. via
 * {@link ManagedScreen#addInputProcessor(InputProcessor)}).
 * 
 * @param <E>
 * @param <F>
 */
public class DefaultInputHandler<E extends Enum<E>, F extends Enum<F>>
		implements DefaultInputProcessor, IInputHandler<E, F> {

	public static final int NOT_SET = -2; // ANY_KEY is already -1
	public static final int MOUSE_ANY_BUTTON = -1;
	public static final int SCROLL_AXIS_X = 0;
	public static final int SCROLL_AXIS_Y = 1;

	private ArrayMap<E, AxisBinding> axisBindings = new ArrayMap<>();
	private ArrayMap<F, BinaryBinding> binaryBindings = new ArrayMap<>();
	private LinkedList<IInputListener> listeners = new LinkedList<>();

	public DefaultInputHandler(EskalonSettings settings,
			Class<E> axisBindingsClazz, Class<F> binaryBindingsClazz) {

		/* Create all axis bindings */
		for (E e : axisBindingsClazz.getEnumConstants()) {
			axisBindings.put(e, new AxisBinding(
					settings.getIntProperty(
							IInputHandler.getPropertyName(e, "keycode_min")),
					settings.getIntProperty(
							IInputHandler.getPropertyName(e, "keycode_max")),
					settings.getIntProperty(
							IInputHandler.getPropertyName(e, "mouse_axis"))));
		}

		/* Create all binary bindings */
		for (F f : binaryBindingsClazz.getEnumConstants()) {
			binaryBindings.put(f, new BinaryBinding(
					settings.getIntProperty(
							IInputHandler.getPropertyName(f, "keycode")),
					settings.getIntProperty(
							IInputHandler.getPropertyName(f, "mouse_button")),
					settings.getBooleanProperty(
							IInputHandler.getPropertyName(f, "toggleable"))));
		}
	}

	@Override
	public void addListener(IInputListener<E, F> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(IInputListener<E, F> listener) {
		listeners.remove(listener);
	}

	@Override
	public boolean keyDown(int keycode) {
		/* BINARY BINDINGS */
		for (Iterator<Entry<F, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			Entry<F, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.keycode.get() <= NOT_SET)
				continue;

			if (b.keycode.get() == keycode || b.keycode.get() == Keys.ANY_KEY) {
				if (!b.toogleable.get()) {
					/* Handle untoggleable binding */
					if (!b.currentState) {
						b.currentState = true;

						for (IInputListener l : listeners) {
							if (l.on(e.key))
								return true;
						}
					}
				} else {
					/* Handle toggleable binding */
					b.currentState = !b.currentState;

					for (IInputListener l : listeners) {
						if (!b.currentState) {
							if (l.off(e.key))
								return true;
						} else {
							if (l.on(e.key))
								return true;
						}
					}
				}

				return true;
			}
		}

		/* AXIS BINDINGS */
		for (Iterator<Entry<E, AxisBinding>> iter = axisBindings
				.iterator(); iter.hasNext();) {
			Entry<E, AxisBinding> e = iter.next();
			AxisBinding b = e.value;

			if (b.keycodeMin.get() <= Keys.ANY_KEY
					|| b.keycodeMax.get() <= Keys.ANY_KEY)
				continue;

			if (b.keycodeMin.get() == keycode) {
				b.keyminState = true;
				b.currentState -= 1;

				for (IInputListener l : listeners) {
					if (l.axisChanged(e.key,
							MathUtils.clamp(b.currentState, -1, 1)))
						return true;
				}
				return true;
			}
			if (b.keycodeMax.get() == keycode) {
				b.keymaxState = true;
				b.currentState += 1;

				for (IInputListener l : listeners) {
					if (l.axisChanged(e.key,
							MathUtils.clamp(b.currentState, -1, 1)))
						return true;
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		/* BINARY BINDINGS */
		for (Iterator<Entry<F, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			Entry<F, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.keycode.get() <= NOT_SET)
				continue;

			if (b.keycode.get() == keycode || b.keycode.get() == Keys.ANY_KEY) {
				if (!b.toogleable.get() && b.currentState) {
					/* Handle untoggleable binding */
					b.currentState = false;

					for (IInputListener l : listeners) {
						if (l.off(e.key))
							return true;
					}
				}
				return true;
			}
		}

		/* AXIS BINDINGS */
		for (Iterator<Entry<E, AxisBinding>> iter = axisBindings
				.iterator(); iter.hasNext();) {
			Entry<E, AxisBinding> e = iter.next();
			AxisBinding b = e.value;

			if (b.keycodeMin.get() <= Keys.ANY_KEY
					|| b.keycodeMax.get() <= Keys.ANY_KEY)
				continue;

			if (b.keycodeMin.get() == keycode && b.keyminState) {
				b.keyminState = false;
				b.currentState += 1;

				for (IInputListener l : listeners) {
					if (l.axisChanged(e.key,
							MathUtils.clamp(b.currentState, -1, 1)))
						return true;
				}
				return true;
			}
			if (b.keycodeMax.get() == keycode && b.keymaxState) {
				b.keymaxState = false;
				b.currentState -= 1;

				for (IInputListener l : listeners) {
					if (l.axisChanged(e.key,
							MathUtils.clamp(b.currentState, -1, 1)))
						return true;
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		boolean ret = false;

		/* BINARY BINDINGS */
		for (Iterator<Entry<F, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			if (ret)
				break;

			Entry<F, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.mouseButton.get() <= NOT_SET)
				continue;

			if (b.mouseButton.get() == button
					|| b.mouseButton.get() == MOUSE_ANY_BUTTON) {
				if (!b.toogleable.get() && !b.currentState) {
					/* Handle untoggleable binding */
					b.currentState = true;

					for (IInputListener l : listeners) {
						if (l.on(e.key)) {
							ret = true;
							break;
						}
					}
				}

				ret = true;
				break;
			}
		}

		/* CURSOR MOVEMENT */
		// On touch screens there is no mouseMoved event, thus we need to hand
		// over the first touchDown event as cursor movement; this should happen
		// after the binary bindings were processed (so Button.LEFT etc.
		// bindings were already triggered).
		for (IInputListener l : listeners) {
			if (l.moved(screenX, screenY))
				return true;
		}
		return ret;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		boolean ret = false;

		/* BINARY BINDINGS */
		for (Iterator<Entry<F, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			if (ret)
				break;

			Entry<F, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.mouseButton.get() <= NOT_SET)
				continue;

			if (b.mouseButton.get() == button
					|| b.mouseButton.get() == MOUSE_ANY_BUTTON) {
				if (!b.toogleable.get() && b.currentState) {
					/* Handle untoggleable binding */
					b.currentState = false;

					for (IInputListener l : listeners) {
						if (l.off(e.key)) {
							ret = true;
							break;
						}
					}
				}

				ret = true;
				break;
			}
		}
		/* CURSOR MOVEMENT */
		for (IInputListener l : listeners) {
			if (l.moved(screenX, screenY))
				return true;
		}
		return ret;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		for (IInputListener l : listeners) {
			if (l.moved(screenX, screenY))
				return true;
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		for (IInputListener l : listeners) {
			if (l.moved(screenX, screenY))
				return true;
		}
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		/* AXIS BINDINGS */
		for (Iterator<Entry<E, AxisBinding>> iter = axisBindings
				.iterator(); iter.hasNext();) {
			Entry<E, AxisBinding> e = iter.next();
			AxisBinding b = e.value;

			if (b.mouseAxis.get() <= -1)
				continue;

			if (amountX != 0 && (b.mouseAxis.get() == SCROLL_AXIS_X)) {
				for (IInputListener l : listeners) {
					// b.currentState = amountX;

					if (l.axisChanged(e.key, amountX))
						return true;
				}
				return true;
			}
			if (amountY != 0 && (b.mouseAxis.get() == SCROLL_AXIS_Y)) {
				for (IInputListener l : listeners) {
					// b.currentState = amountY;

					if (l.axisChanged(e.key, amountY))
						return true;
				}
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isOn(F f) {
		return binaryBindings.get(f).currentState;
	}

	@Override
	public float getAxisState(E e) {
		AxisBinding b = axisBindings.get(e);
		if (b.mouseAxis.get() == SCROLL_AXIS_X
				|| b.mouseAxis.get() == SCROLL_AXIS_Y) {
			// TODO add InputProcessor that keeps track of scroll wheel activity
			// and is reset every render() call
			throw new UnsupportedOperationException(
					"libGDX does not support polling the scroll wheel state.");
		}
		return b.currentState;
	}

	@Override
	public int getCursorX() {
		return Gdx.input.getX();
	}

	@Override
	public int getCursorY() {
		return Gdx.input.getY();
	}

	@Override
	public void clear() {
		axisBindings.clear();
		binaryBindings.clear();
		listeners.clear();
	}

	@Override
	public void reset() {
		for (AxisBinding a : axisBindings.values()) {
			a.currentState = 0;
		}
		for (BinaryBinding b : binaryBindings.values()) {
			b.currentState = false;
		}
	}

	final class AxisBinding {

		// private IntProperty controllerAxis;
		private IntProperty keycodeMin, keycodeMax;
		private IntProperty mouseAxis;

		private boolean keyminState = false;
		private boolean keymaxState = false;

		public float currentState = 0;

		public AxisBinding(IntProperty keycodeMin, IntProperty keycodeMax,
				IntProperty mouseAxis) {
			// This is because mouse axis values don't play nicely with the
			// clamped values of the other axis; in addition, they aren't reset
			Preconditions.checkArgument(mouseAxis.get() <= NOT_SET
					|| (/* controllerAxis.get() <= NOT_SET && */ keycodeMin
							.get() <= Keys.ANY_KEY
							&& keycodeMax.get() <= Keys.ANY_KEY),
					"If a mouse axis is set, AxisBinding does not support any other axis.");

			this.keycodeMin = keycodeMin;
			this.keycodeMax = keycodeMax;
			this.mouseAxis = mouseAxis;
		}
	}

	final class BinaryBinding {

		private IntProperty mouseButton;
		private IntProperty keycode;
		// private IntProperty controllerButton;
		private BooleanProperty toogleable;

		public boolean currentState = false;

		public BinaryBinding(IntProperty keycode, IntProperty mouseButton,
				BooleanProperty toogleable) {
			this.keycode = keycode;
			this.mouseButton = mouseButton;
			this.toogleable = toogleable;
		}
	}

}
