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

import javax.annotation.Nullable;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Array;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.tuple.Pair;

/**
 * The internal representation of a music playlist.
 * 
 * @author damios
 */
public class Playlist {

	private Array<Pair<Music, String>> songs = new Array<>();
	private boolean repeat = true;
	private boolean shuffle = true;

	private int currentIndex = -1;

	public void setShuffle(boolean shuffle) {
		this.shuffle = shuffle;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	public void addSong(Music song, String name) {
		songs.add(new Pair<>(song, name));
	}

	public int getSize() {
		return songs.size;
	}

	void reset() {
		currentIndex = -1;
	}

	/**
	 * Selects the next song to be played. Throws an exception if the playlist
	 * is empty.
	 * 
	 * @return the next song; {@code null}, if the playlist is finished
	 */
	@Nullable
	Pair<Music, String> getNextSong() {
		Preconditions.checkState(songs.size > 0,
				"A playlist cannot be played if there are no songs");

		currentIndex++;

		boolean quit = currentIndex >= songs.size && !repeat;

		if (currentIndex == 0 || currentIndex >= songs.size) {
			if (shuffle)
				songs.shuffle();
			currentIndex = 0;
		}

		if (quit)
			return null;

		return songs.get(currentIndex);
	}

}
