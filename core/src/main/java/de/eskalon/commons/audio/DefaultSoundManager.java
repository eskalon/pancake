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

import java.util.HashMap;
import java.util.NoSuchElementException;

import javax.annotation.Nullable;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Timer;

import de.damios.guacamole.tuple.Pair;
import de.eskalon.commons.utils.MathStuffUtils;

/**
 * The default implementation of a sound manager responsible for playing (a)
 * {@linkplain Sound sound effects} and (b) {@linkplain Music music} via
 * playlists. Does not support spatial audio.
 * 
 * @author damios
 */
public class DefaultSoundManager implements ISoundManager, Disposable {

	protected HashMap<String, Sound> soundEffects = new HashMap<>();
	protected HashMap<String, Playlist> musicPlaylists = new HashMap<>();

	protected float effectVolume = 1F;
	protected float musicVolume = 1F;
	protected float masterVolume = 1F;

	protected MusicFadeOutTask fadeOutTask = new MusicFadeOutTask();
	protected MusicFadeInTask fadeInTask = new MusicFadeInTask();
	protected float musicFadeStep = 0.06F;
	protected float musicFadeInterval = 0.55F;

	protected @Nullable Playlist currentPlaylist;
	protected @Nullable Pair<Music, String> currentSong;

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

		return new DefaultSoundInstance(effect, id);
	}

	@Override
	public void playMusic(String playlistName, boolean finishCurrentSong) {
		currentPlaylist = musicPlaylists.get(playlistName);

		if (currentPlaylist == null)
			throw new NoSuchElementException(
					"There is no playlist with the name '" + currentPlaylist
							+ "'");

		currentPlaylist.reset();

		if (currentSong == null) { // if there was no music playing before
			playNextSong();

			currentSong.x.setVolume(0F);
			fadeInTask.songToFadeIn = currentSong.x;
			Timer.schedule(fadeInTask, 0F, musicFadeInterval);
		} else if (!finishCurrentSong) { // fade out current music immediately
			currentSong.x.setOnCompletionListener(null);
			fadeInTask.cancel();
			fadeOutTask.cancel();
			fadeOutTask.songToFadeOut = currentSong.x;
			Timer.schedule(fadeOutTask, 0F, musicFadeInterval);
		}
	}

	@Override
	public void stopMusic(boolean finishCurrentSong) {
		if (currentSong != null) {
			currentPlaylist = null;

			if (!finishCurrentSong) { // fade out this song
				currentSong.x.setOnCompletionListener(null);
				fadeInTask.cancel();
				fadeOutTask.cancel();
				fadeOutTask.songToFadeOut = currentSong.x;
				Timer.schedule(fadeOutTask, 0F, musicFadeInterval);
			}
		}
	}

	/**
	 * Plays the next song, if a playlist is set and this playlist is not empty.
	 * After completion of that song, the next song is played.
	 * 
	 * @return whether a new song was started
	 */
	private void playNextSong() {
		if (currentPlaylist == null) {
			currentSong = null;
			return;
		}

		currentSong = currentPlaylist.getNextSong();

		if (currentSong == null) {
			currentPlaylist = null;
			return;
		}

		currentSong.x.setVolume(getEffectiveVolume(musicVolume));
		currentSong.x.play();
		currentSong.x.setOnCompletionListener((Music) -> {
			playNextSong();
		});
	}

	@Override
	public void addSoundEffect(Sound soundEffect, String name) {
		soundEffects.put(name, soundEffect);
	}

	@Override
	public void addMusic(Music music, String songName, String paylistName) {
		musicPlaylists.putIfAbsent(paylistName, new Playlist());
		musicPlaylists.get(paylistName).addSong(music, songName);
	}

	@Override
	public String getCurrentMusicTitle() {
		return currentSong == null ? "null" : currentSong.y;
	}

	@Override
	public void setPlaylistShuffle(String paylistName, boolean shuffle) {
		musicPlaylists.putIfAbsent(paylistName, new Playlist());
		musicPlaylists.get(paylistName).setShuffle(shuffle);
	}

	@Override
	public void setPlaylistRepeat(String paylistName, boolean repeat) {
		musicPlaylists.putIfAbsent(paylistName, new Playlist());
		musicPlaylists.get(paylistName).setRepeat(repeat);
	}

	@Override
	public void setEffectVolume(float effectVolume) {
		this.effectVolume = effectVolume;
	}

	@Override
	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;

		if (currentSong != null)
			currentSong.x.setVolume(getEffectiveVolume(musicVolume));
	}

	@Override
	public void setMasterVolume(float masterVolume) {
		this.masterVolume = masterVolume;

		if (currentSong != null)
			currentSong.x.setVolume(getEffectiveVolume(musicVolume));
	}

	/**
	 * Converts the linear scale of the volume sliders to a logarithmic scale to
	 * compensate for the human hearing being logarithmic rather than linear.
	 * 
	 * @param volumeValue
	 *            the volume obtained from a normal slider that is changed
	 *            linearly
	 * @return the effective volume to use
	 * 
	 * @see <a href=
	 *      "https://www.dr-lex.be/info-stuff/volumecontrols.html">Additional
	 *      information on this issue</a>
	 */
	protected float getEffectiveVolume(float volumeValue) {
		return (float) MathStuffUtils.linToExp(volumeValue * masterVolume, 2);
	}

	@Override
	public void setListenerOrientation(float lookX, float lookY, float lookZ,
			float upX, float upY, float upZ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setListenerVelocity(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setListenerPosition(float x, float y, float z) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
		// not needed as sounds and music are to be disposed via the asset
		// manager
	}

	class MusicFadeInTask extends Timer.Task {
		public Music songToFadeIn;

		@Override
		public void run() {
			if (songToFadeIn.getVolume() < getEffectiveVolume(musicVolume))
				songToFadeIn
						.setVolume(songToFadeIn.getVolume() + musicFadeStep);
			else {
				songToFadeIn.setVolume(getEffectiveVolume(musicVolume));
				this.cancel();
			}
		}
	}

	class MusicFadeOutTask extends Timer.Task {
		public Music songToFadeOut;

		@Override
		public void run() {
			if (songToFadeOut.getVolume() >= musicFadeStep)
				songToFadeOut
						.setVolume(songToFadeOut.getVolume() - musicFadeStep);
			else {
				songToFadeOut.stop();
				playNextSong();
				this.cancel();
			}
		}
	}

}
