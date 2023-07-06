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

package de.eskalon.commons.audio;

import java.nio.FloatBuffer;
import java.util.NoSuchElementException;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.GdxRuntimeException;

import de.eskalon.commons.settings.EskalonSettings;

/**
 * @author damios
 */
public class DesktopSoundManager extends DefaultSoundManager {

	public DesktopSoundManager(EskalonSettings settings) {
		super(settings);
	}

	@Override
	public ISoundInstance playSoundEffect(String name, boolean stopIfPlaying,
			float pitch) {
		Sound effect = soundEffects.get(name);

		if (effect == null)
			throw new NoSuchElementException(
					"There is no sound effect with the name '" + name
							+ "' registered");

		if (stopIfPlaying)
			effect.stop();

		long id = effect.play(getEffectiveVolume(effectVolume));

		if (id == -1)
			throw new GdxRuntimeException(
					"Some error occurred playing the sound");

		effect.setPitch(id, pitch);

		return new DesktopSoundInstance(effect, id);
	}

	@Override
	public void setListenerOrientation(float lookX, float lookY, float lookZ,
			float upX, float upY, float upZ) {
		// By default: look = (0.0, 0.0, -1.0); up = 0.0, 1.0, 0.0
		// --> x = left/right; y = up/down; z = front/back
		AL10.alListenerfv(AL10.AL_ORIENTATION,
				(FloatBuffer) BufferUtils.createFloatBuffer(6)
						.put(new float[] { lookX, lookY, lookZ, upX, upY, upZ })
						.flip());
	}

	@Override
	public void setListenerVelocity(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_VELOCITY, x, y, z);
	}

	@Override
	public void setListenerPosition(float x, float y, float z) {
		AL10.alListener3f(AL10.AL_POSITION, x, y, z);
	}

}
