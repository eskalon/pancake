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

package de.eskalon.commons.asset;

import java.util.List;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.audio.Playlist;

/**
 * An asset defining a {@link Playlist}.
 * <p>
 * <u>Example file:</u>
 * 
 * <pre>
 * {
 *   name: "best_playlist_ever",
 *   shuffle: true,
 *   repeat: true,
 *   music: [
 *        [
 *           "My favourite song",
 *           "my_favourite_song.mp3"
 *        ],
 *     ]
 * }
 * </pre>
 * 
 * @author damios
 * @see #addPlaylistDefinitionToSoundManager(PlaylistDefinition, ISoundManager,
 *      AssetManager)
 */
public class PlaylistDefinition {

	public String name;
	public boolean shuffle;
	public boolean repeat;

	/**
	 * The music files as String arrays. The arrays contain the song's file path
	 * (relative to the playlist file) at index 0 and the song name at index 1.
	 */
	public List<String[]> music;

	public static void addPlaylistDefinitionToSoundManager(
			PlaylistDefinition def, ISoundManager soundManager,
			AssetManager assetManager) {
		for (String[] s : def.music) {
			soundManager.addMusic(assetManager.get(s[1], Music.class), s[0],
					def.name);
		}

		soundManager.setPlaylistShuffle(def.name, def.shuffle);
		soundManager.setPlaylistRepeat(def.name, def.repeat);
	}

}