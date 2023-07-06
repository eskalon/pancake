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

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

/**
 * A sound manager responsible for playing (a) {@linkplain Sound sound effects}
 * and (b) {@linkplain Music music} via playlists.
 * 
 * @author damios
 */
public interface ISoundManager {

	/**
	 * Plays a {@linkplain #addSoundEffect(Sound, String) previously registered}
	 * sound effect.
	 * 
	 * @param name
	 *            the name of the sound
	 * @return the sound instance used to control the playback
	 */
	public default ISoundInstance playSoundEffect(String name) {
		return playSoundEffect(name, false);
	}

	/**
	 * Plays a {@linkplain #addSoundEffect(Sound, String) previously registered}
	 * sound effect.
	 * 
	 * @param name
	 *            the name of the sound
	 * @param stopIfPlaying
	 *            whether to stop all currently playing instances of this sound
	 * @return the sound instance used to control the playback
	 */
	public default ISoundInstance playSoundEffect(String name,
			boolean stopIfPlaying) {
		return playSoundEffect(name, stopIfPlaying, 1F);
	}

	/**
	 * Plays a {@linkplain #addSoundEffect(Sound, String) previously registered}
	 * sound effect.
	 * 
	 * @param name
	 *            the name of the sound
	 * @param stopIfPlaying
	 *            whether to stop all currently playing instances of the sound
	 * @param pitch
	 *            the pitch multiplier; {@code 1} by default, {@code >1} to play
	 *            faster, {@code <1} to play slower; the value has to be between
	 *            {@code 0.5} and {@code 2.0}
	 * @return the sound instance used to control the playback
	 */
	public ISoundInstance playSoundEffect(String name, boolean stopIfPlaying,
			float pitch);

	/**
	 * Plays a certain music playlist.
	 * 
	 * @param playlistName
	 *            the name of the playlist
	 */
	public default void playMusic(String playlistName) {
		playMusic(playlistName, true);
	}

	/**
	 * Plays a certain music playlist.
	 * 
	 * @param playlistName
	 *            the name of the playlist
	 * @param finishCurrentSong
	 *            whether to finish playing the current song or fade it out
	 *            immediately
	 */
	public void playMusic(String playlistName, boolean finishCurrentSong);

	/**
	 * Stops the currently playing music.
	 * 
	 * @param finishCurrentSong
	 *            whether to finish playing the current song or fade it out
	 *            immediately
	 */
	public void stopMusic(boolean finishCurrentSong);

	/**
	 * Adds a sound effect.
	 * 
	 * @param soundEffect
	 *            the {@link Sound}
	 * @param name
	 *            the name used to identify the sound
	 */
	public void addSoundEffect(Sound soundEffect, String name);

	/**
	 * Adds a song.
	 * 
	 * @param music
	 *            the {@link Music}
	 * @param songName
	 *            the name of the song
	 * @param paylistName
	 *            the name used to identify the playlist
	 */
	public void addMusic(Music music, String songName, String paylistName);

	public String getCurrentMusicTitle();

	public void setPlaylistShuffle(String paylistName, boolean shuffle);

	public void setPlaylistRepeat(String paylistName, boolean repeat);

	public void setListenerOrientation(float lookX, float lookY, float lookZ,
			float upX, float upY, float upZ);

	public default void setListenerOrientation(Vector3 look, Vector3 up) {
		setListenerOrientation(look.x, look.y, look.z, up.x, up.y, up.z);
	}

	public default void setListenerOrientation(Vector3 look) {
		setListenerOrientation(look, Vector3.Y);
	}

	public void setListenerVelocity(float x, float y, float z);

	public default void setListenerVelocity(Vector3 dir) {
		setListenerVelocity(dir.x, dir.y, dir.z);
	}

	public void setListenerPosition(float x, float y, float z);

	public default void setListenerPosition(Vector3 dir) {
		setListenerPosition(dir.x, dir.y, dir.z);
	}

}
