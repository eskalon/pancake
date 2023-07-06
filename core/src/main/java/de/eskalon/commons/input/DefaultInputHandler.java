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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.settings.IntProperty;

public class DefaultInputHandler<E extends Enum<E>>
		implements DefaultInputProcessor, IInputHandler<E> {

	private static final String KEYBIND_SETTINGS_PREFIX = "keybind_";
	private static final int MOUSE_ANY_BUTTON = -1;

	private EskalonSettings settings;

	private ArrayMap<E, AxisBinding> axisBindings = new ArrayMap<>();
	private ArrayMap<E, BinaryBinding> binaryBindings = new ArrayMap<>();
	private LinkedList<AxisBindingListener> axisListeners = new LinkedList<>();
	private LinkedList<BinaryBindingListener> binaryListeners = new LinkedList<>();

	public DefaultInputHandler(EskalonSettings settings) {
		this.settings = settings;
	}

	@Override
	public void registerAxisBinding(E id, int keycodeMin, int keycodeMax,
			int mouseAxis) {
		axisBindings.put(id,
				new AxisBinding(getProperty(id, "keycode_min", keycodeMin),
						getProperty(id, "keycode_max", keycodeMax),
						getProperty(id, "mouse_axis", mouseAxis)));
	}

	@Override
	public void registerBinaryBinding(E id, int keycode, int mouseButton,
			boolean toogleable) {
		binaryBindings.put(id,
				new BinaryBinding(getProperty(id, "mouse_button", mouseButton),
						getProperty(id, "keycode", keycode),

						toogleable));
	}

	private IntProperty getProperty(E id, String name, int defaultValue) {
		return settings.getIntProperty(KEYBIND_SETTINGS_PREFIX
				+ (id.toString().toLowerCase()) + "_" + name, defaultValue);
	}

	@Override
	public void addAxisBindingListener(AxisBindingListener<E> listener) {
		axisListeners.add(listener);
	}

	@Override
	public void addBinaryBindingListener(BinaryBindingListener<E> listener) {
		binaryListeners.add(listener);
	}

	@Override
	public void removeAxisBindingListener(AxisBindingListener<E> listener) {
		axisListeners.remove(listener);
	}

	@Override
	public void removeBinaryBindingListener(BinaryBindingListener<E> listener) {
		binaryListeners.remove(listener);
	}

	@Override
	public boolean keyDown(int keycode) {
		/* BINARY BINDINGS */
		for (Iterator<Entry<E, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			Entry<E, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.keycode.get() <= BinaryBinding.NOT_SET)
				continue;

			if (b.keycode.get() == keycode || b.keycode.get() == Keys.ANY_KEY) {
				if (!b.toogleable) {
					/* Handle untoggleable binding */
					if (!b.currentState) {
						b.currentState = true;

						for (BinaryBindingListener l : binaryListeners) {
							if (l.on(e.key))
								return true;
						}
					}
				} else {
					/* Handle toggleable binding */
					b.currentState = !b.currentState;

					for (BinaryBindingListener l : binaryListeners) {
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
				b.currentState -= 1;

				for (AxisBindingListener l : axisListeners) {
					if (l.axisChanged(e.key,
							MathUtils.clamp(b.currentState, -1, 1)))
						return true;
				}
				return true;
			}
			if (b.keycodeMax.get() == keycode) {
				b.currentState += 1;

				for (AxisBindingListener l : axisListeners) {
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
		for (Iterator<Entry<E, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			Entry<E, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.keycode.get() <= BinaryBinding.NOT_SET)
				continue;

			if (b.keycode.get() == keycode || b.keycode.get() == Keys.ANY_KEY) {
				if (!b.toogleable && b.currentState) {
					/* Handle untoggleable binding */
					b.currentState = false;

					for (BinaryBindingListener l : binaryListeners) {
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

			if (b.keycodeMin.get() == keycode) {
				b.currentState += 1;

				for (AxisBindingListener l : axisListeners) {
					if (l.axisChanged(e.key,
							MathUtils.clamp(b.currentState, -1, 1)))
						return true;
				}
				return true;
			}
			if (b.keycodeMax.get() == keycode) {
				b.currentState -= 1;

				for (AxisBindingListener l : axisListeners) {
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
		/* BINARY BINDINGS */
		for (Iterator<Entry<E, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			Entry<E, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.mouseButton.get() <= BinaryBinding.NOT_SET)
				continue;

			if (b.mouseButton.get() == button
					|| b.mouseButton.get() == MOUSE_ANY_BUTTON) {
				if (!b.toogleable && !b.currentState) {
					/* Handle untoggleable binding */
					b.currentState = true;

					for (BinaryBindingListener l : binaryListeners) {
						if (l.on(e.key))
							return true;
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		/* BINARY BINDINGS */
		for (Iterator<Entry<E, BinaryBinding>> iter = binaryBindings
				.iterator(); iter.hasNext();) {
			Entry<E, BinaryBinding> e = iter.next();
			BinaryBinding b = e.value;

			if (b.mouseButton.get() <= BinaryBinding.NOT_SET)
				continue;

			if (b.mouseButton.get() == button
					|| b.mouseButton.get() == MOUSE_ANY_BUTTON) {
				if (!b.toogleable && b.currentState) {
					/* Handle untoggleable binding */
					b.currentState = false;

					for (BinaryBindingListener l : binaryListeners) {
						if (l.off(e.key))
							return true;
					}
				}

				return true;
			}
		}

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
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

			if (amountX != 0
					&& (b.mouseAxis.get() == AxisBinding.SCROLL_AXIS_X)) {
				for (AxisBindingListener l : axisListeners) {
					// b.currentState = amountX;

					if (l.axisChanged(e.key, amountX))
						return true;
				}
				return true;
			}
			if (amountY != 0
					&& (b.mouseAxis.get() == AxisBinding.SCROLL_AXIS_Y)) {
				for (AxisBindingListener l : axisListeners) {
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
	public boolean isOn(E e) {
		return binaryBindings.get(e).currentState;
	}

	@Override
	public float getAxisState(E e) {
		AxisBinding b = axisBindings.get(e);
		if (b.mouseAxis.get() == AxisBinding.MOUSE_AXIS_X) {
			return Gdx.input.getDeltaX();
		}
		if (b.mouseAxis.get() == AxisBinding.MOUSE_AXIS_Y) {
			return Gdx.input.getDeltaX();
		}
		if (b.mouseAxis.get() == AxisBinding.SCROLL_AXIS_X
				|| b.mouseAxis.get() == AxisBinding.SCROLL_AXIS_Y) {
			// TODO add InputProcessor that keeps track of scroll wheel activity
			// and is reset every render() call
			throw new UnsupportedOperationException(
					"libGDX does not support polling the scroll wheel state.");
		}
		return b.currentState;
	}

	@Override
	public void clear() {
		axisBindings.clear();
		axisListeners.clear();
		binaryBindings.clear();
		binaryListeners.clear();
	}

	final class AxisBinding {
		public static final int NOT_SET = -2; // ANY_KEY is already -1
		public static final int MOUSE_AXIS_X = 0;
		public static final int MOUSE_AXIS_Y = 1;
		public static final int SCROLL_AXIS_X = 2;
		public static final int SCROLL_AXIS_Y = 3;

		// private IntProperty controllerAxis;
		private IntProperty keycodeMin, keycodeMax;
		private IntProperty mouseAxis;

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
		public static final int NOT_SET = -2; // ANY_KEY is already -1

		private IntProperty mouseButton;
		private IntProperty keycode;
		// private IntProperty controllerButton;
		private boolean toogleable;

		public boolean currentState = false;

		public BinaryBinding(IntProperty keycode, IntProperty mouseButton,
				boolean toogleable) {
			this.keycode = keycode;
			this.mouseButton = mouseButton;
			this.toogleable = toogleable;
		}
	}

}
