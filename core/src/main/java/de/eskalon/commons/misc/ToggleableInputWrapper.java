/*
 * Copyright 2020 eskalon
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

package de.eskalon.commons.misc;

import com.badlogic.gdx.InputProcessor;

import de.damios.guacamole.Preconditions;

/**
 * A wrapper for {@link InputProcessor}s that allows
 * {@linkplain #setEnabled(boolean) disabling} them.
 * 
 * @author damios
 */
public class ToggleableInputWrapper implements InputProcessor {
	private InputProcessor processor;
	private boolean enabled = true;

	public ToggleableInputWrapper(InputProcessor processor) {
		Preconditions.checkNotNull(processor);
		this.processor = processor;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean keyDown(int keycode) {
		if (enabled)
			return processor.keyDown(keycode);
		return false;
	}

	public boolean keyUp(int keycode) {
		if (enabled)
			return processor.keyUp(keycode);
		return false;
	}

	public boolean keyTyped(char character) {
		if (enabled)
			return processor.keyTyped(character);
		return false;
	}

	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		if (enabled)
			return processor.touchDown(screenX, screenY, pointer, button);
		return false;
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (enabled)
			return processor.touchUp(screenX, screenY, pointer, button);
		return false;
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (enabled)
			return processor.touchDragged(screenX, screenY, pointer);
		return false;
	}

	public boolean mouseMoved(int screenX, int screenY) {
		if (enabled)
			return processor.mouseMoved(screenX, screenY);
		return false;
	}

	public boolean scrolled(float amountX, float amountY) {
		if (enabled)
			return processor.scrolled(amountX, amountY);
		return false;
	}
}
