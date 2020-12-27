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

import java.io.UnsupportedEncodingException;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import de.eskalon.commons.asset.PlaylistDefinitionLoader.PlaylistDefinitionParameter;

/**
 * An asset loader for a {@link PlaylistDefinition} file.
 * 
 * @author damios
 */
public class PlaylistDefinitionLoader extends
		AsynchronousAssetLoader<PlaylistDefinition, PlaylistDefinitionParameter> {

	private Json jsonParser;

	private PlaylistDefinition data;

	public PlaylistDefinitionLoader(FileHandleResolver resolver) {
		super(resolver);

		this.jsonParser = new Json();
		this.jsonParser.setTypeName(null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName,
			FileHandle file, PlaylistDefinitionParameter parameter) {
		Array<AssetDescriptor> deps = new Array<>();
		String json;
		try { // Charset.defaultCharset(), etc. is not supported on GWT
			json = new String(file.readBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			json = new String(file.readBytes());
		}
		data = jsonParser.fromJson(PlaylistDefinition.class, json);

		for (String[] s : data.music) {
			FileHandle f = file.parent().child(s[1]);
			deps.add(new AssetDescriptor<>(f, Music.class));
			s[1] = f.toString();
		}

		return deps;
	}

	@Override
	public PlaylistDefinition loadSync(AssetManager assetManager,
			String fileName, FileHandle file,
			PlaylistDefinitionParameter parameter) {
		PlaylistDefinition def = data;
		data = null;

		return def;
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName,
			FileHandle file, PlaylistDefinitionParameter parameter) {
	}

	public static class PlaylistDefinitionParameter
			extends AssetLoaderParameters<PlaylistDefinition> {
	}

}
