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
		return enabled && processor.keyDown(keycode);
	}

	public boolean keyUp(int keycode) {
		return enabled && processor.keyUp(keycode);
	}

	public boolean keyTyped(char character) {
		return enabled && processor.keyTyped(character);
	}

	public boolean touchDown(int screenX, int screenY, int pointer,
			int button) {
		return enabled
				&& processor.touchDown(screenX, screenY, pointer, button);
	}

	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return enabled && processor.touchUp(screenX, screenY, pointer, button);
	}

	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return enabled && processor.touchDragged(screenX, screenY, pointer);
	}

	public boolean mouseMoved(int screenX, int screenY) {
		return enabled && processor.mouseMoved(screenX, screenY);
	}

	public boolean scrolled(float amountX, float amountY) {
		return enabled && processor.scrolled(amountX, amountY);
	}
}
