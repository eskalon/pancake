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

import org.lwjgl.openal.AL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl3.audio.OpenALLwjgl3Audio;
import com.badlogic.gdx.math.Vector3;

/**
 * @author damios
 */
public class DesktopSoundInstance extends DefaultSoundInstance {

	private int sourceId = -1;

	public DesktopSoundInstance(Sound sound, long soundId) {
		super(sound, soundId);
		sourceId = ((OpenALLwjgl3Audio) Gdx.audio).getSourceId(soundId);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only supports mono sounds!
	 */
	@Override
	public void setSoundOrientation(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_ORIENTATION, x, y, z);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only supports mono sounds!
	 */
	@Override
	public void setSoundOrientation(Vector3 dir) {
		super.setSoundOrientation(dir);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only supports mono sounds!
	 */
	@Override
	public void setSoundVelocity(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_VELOCITY, x, y, z);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only supports mono sounds!
	 */
	@Override
	public void setSoundVelocity(Vector3 dir) {
		super.setSoundVelocity(dir);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only supports mono sounds!
	 */
	@Override
	public void setSoundPosition(float x, float y, float z) {
		AL10.alSource3f(sourceId, AL10.AL_POSITION, x, y, z);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Only supports mono sounds!
	 */
	@Override
	public void setSoundPosition(Vector3 dir) {
		super.setSoundPosition(dir);
	}

}
