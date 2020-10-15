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
import com.badlogic.gdx.math.Vector3;

/**
 * This class is used to interact with the instance of a {@link Sound} currently
 * played.
 * 
 * @author damios
 */
public interface ISoundInstance {

	/**
	 * Stops this sound instance. If the sound is no longer playing, this has no
	 * effect.
	 */
	public void stop();

	/**
	 * Pauses this sound instance. If the sound is no longer playing, this has
	 * no effect.
	 */
	public void pause();

	/**
	 * Resumes this sound instance. If the sound is not paused, this has no
	 * effect.
	 */
	public void resume();

	/**
	 * Sets this sound instance to be looping. If the sound is no longer playing
	 * this has no effect.
	 * 
	 * @param looping
	 *            whether to loop or not.
	 */
	public void setLooping(boolean looping);

	/**
	 * Changes the pitch multiplier of this sound instance. If the sound is no
	 * longer playing, this has no effect.
	 * 
	 * @param pitch
	 *            the pitch multiplier; {@code 1} by default, {@code >1} to play
	 *            faster, {@code <1} to play slower; the value has to be between
	 *            {@code 0.5} and {@code 2.0}
	 */
	public void setPitch(float pitch);

	/**
	 * Changes the volume of this sound instance. If the sound is no longer
	 * playing, this has no effect.
	 * 
	 * @param volume
	 *            the volume in the range {@code 0} (silent) to {@code 1} (max
	 *            volume).
	 */
	public void setVolume(float volume);

	/**
	 * Sets the panning and volume of this sound instance. If the sound is no
	 * longer playing, this has no effect. Note that panning only works for mono
	 * sounds, not for stereo sounds!
	 * 
	 * @param pan
	 *            panning in the range {@code -1} (full left) to {@code 1} (full
	 *            right). {@code 0} is center position.
	 * @param volume
	 *            the volume in the range {@code [0,1]}.
	 */
	public void setPan(float pan, float volume);

	/**
	 * Sets the orientation of this sound instance.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setSoundOrientation(float x, float y, float z);

	/**
	 * Sets the orientation of this sound instance.
	 * 
	 * @param dir
	 */
	public default void setSoundOrientation(Vector3 dir) {
		setSoundOrientation(dir.x, dir.y, dir.z);
	}

	/**
	 * Sets the velocity of this sound instance.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setSoundVelocity(float x, float y, float z);

	/**
	 * Sets the velocity of this sound instance.
	 * 
	 * @param dir
	 */
	public default void setSoundVelocity(Vector3 dir) {
		setSoundVelocity(dir.x, dir.y, dir.z);
	}

	/**
	 * Sets the position of this sound instance.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setSoundPosition(float x, float y, float z);

	/**
	 * Sets the position of this sound instance.
	 * 
	 * @param dir
	 */
	public default void setSoundPosition(Vector3 dir) {
		setSoundPosition(dir.x, dir.y, dir.z);
	}

}
