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

import com.badlogic.gdx.audio.Sound;

/**
 * This class is used to interact with the instance of a {@link Sound} currently
 * played.
 * 
 * @author damios
 */
public class DefaultSoundInstance implements ISoundInstance {

	private Sound sound;
	private long soundId;

	public DefaultSoundInstance(Sound sound, long soundId) {
		this.sound = sound;
		this.soundId = soundId;
	}

	@Override
	public void stop() {
		sound.stop(soundId);
	}

	@Override
	public void pause() {
		sound.pause(soundId);
	}

	@Override
	public void resume() {
		sound.resume(soundId);
	}

	@Override
	public void setLooping(boolean looping) {
		sound.setLooping(soundId, looping);
	}

	@Override
	public void setPitch(float pitch) {
		sound.setPitch(soundId, pitch);
	}

	@Override
	public void setVolume(float volume) {
		sound.setVolume(soundId, volume);
	}

	@Override
	public void setPan(float pan, float volume) {
		sound.setPan(soundId, pan, volume);
	}

	@Override
	public void setSoundOrientation(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSoundVelocity(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSoundPosition(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

}
