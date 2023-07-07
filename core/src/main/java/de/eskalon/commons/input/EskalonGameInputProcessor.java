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

package de.eskalon.commons.input;

import com.badlogic.gdx.Input.Keys;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.audio.ISoundManager;

/**
 * This input processor takes care of some basic application-wide key binds:
 * <ul>
 * <li><b>F2:</b> toggle the debug overlay</li>
 * <li><b>F12:</b> take a screenshot</li>
 * </ul>
 * 
 * @author damios
 */
public class EskalonGameInputProcessor implements DefaultInputProcessor {

	private boolean enabled = false;
	public static final int takeScreenshotKey = Keys.F12;
	public static final int toggleOverlayKey = Keys.F2;
	public static final int skipSongKey = Keys.F9;
	// protected int toggleConsoleKey = Keys.BACKSLASH;

	private ISoundManager soundManager;
	private boolean takeScreenshot = false;
	private boolean overlayEnabled = false;

	public EskalonGameInputProcessor(ISoundManager soundManager) {
		this.soundManager = soundManager;
	}

	@Override
	public final boolean keyDown(int keycode) {
		if (!enabled)
			return false;

		if (keycode == takeScreenshotKey) {
			takeScreenshot = true;
			return true;
		}
		if (keycode == skipSongKey) {
			soundManager.skipSong();
			return true;
		}
		if (keycode == toggleOverlayKey) {
			overlayEnabled = !overlayEnabled;
			return true;
		}

		return false;
	}

	public boolean isDebugOverlayEnabled() {
		return overlayEnabled;
	}

	public boolean pollTakeScreenshot() {
		if (takeScreenshot) {
			takeScreenshot = false;
			return true;
		} else {
			return false;
		}
	}

	public void enable() {
		this.enabled = true;
	}

}