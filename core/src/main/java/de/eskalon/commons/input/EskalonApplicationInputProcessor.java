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
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Singleton;
import de.eskalon.commons.utils.TimeUtils;

/**
 * This input processor takes care of some basic application-wide key binds:
 * <ul>
 * <li><b>F2:</b> toggle the debug overlay</li>
 * <li><b>F9:</b> {@linkplain ISoundManager#skipSong() skip a song}</li>
 * <li><b>F12:</b> take a screenshot</li>
 * </ul>
 * 
 * @author damios
 */
public class EskalonApplicationInputProcessor implements DefaultInputProcessor {

	private boolean enabled = false;
	public static final int takeScreenshotKey = Keys.F12;
	public static final int toggleOverlayKey = Keys.F2;
	public static final int skipSongKey = Keys.F9;
	public static final int stepModeKey1 = Keys.CONTROL_LEFT;
	public static final int stepModeKey2 = Keys.F11;
	public static final int doStepKey = Keys.ENTER;
	// protected int toggleConsoleKey = Keys.BACKSLASH;

	private ISoundManager soundManager;
	private boolean takeScreenshot = false;
	private boolean overlayEnabled = false;
	private boolean stepModeEnabled = false;
	private boolean continueStepping = false;
	private boolean doStep = false;
	private boolean stepModeTriggered1 = false;
	private boolean stepModeTriggered2 = false;

	private long lastStepTime;

	@Inject
	@Singleton
	public EskalonApplicationInputProcessor(ISoundManager soundManager) {
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
		if (keycode == stepModeKey1) {
			stepModeTriggered1 = true;

			if (stepModeTriggered2)
				stepModeEnabled = !stepModeEnabled;

			if (stepModeEnabled)
				doStep = true; // do the first step

			return true;
		}
		if (keycode == stepModeKey2) {
			stepModeTriggered2 = true;

			if (stepModeTriggered1)
				stepModeEnabled = !stepModeEnabled;

			if (stepModeEnabled)
				doStep = true; // do the first step

			return true;
		}
		if (stepModeEnabled && keycode == doStepKey) {
			continueStepping = true;
			doStep = true;
			return true;
		}

		return false;
	}

	@Override
	public final boolean keyUp(int keycode) {
		if (!enabled)
			return false;

		if (keycode == stepModeKey1) {
			stepModeTriggered1 = false;
			return true;
		}
		if (keycode == stepModeKey2) {
			stepModeTriggered2 = false;
			return true;
		}
		if (stepModeEnabled && keycode == doStepKey) {
			continueStepping = false;
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

	public boolean isStepModeEnabled() {
		return stepModeEnabled;
	}

	public boolean pollDoStep() {
		if (doStep) {
			doStep = false;
			lastStepTime = TimeUtils.realNano();
			return true;
		} else {
			if (continueStepping && (lastStepTime > 0
					&& TimeUtils.haveRealMillisPassedSince(lastStepTime, 500)))
				doStep = true;
			return false;
		}
	}

}