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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;

/**
 * Example for an {@link Asset @Asset} annotation loading a {@link Texture} with
 * parameters:
 * 
 * <pre>
 * {@code @Asset(value = "path/to/texture.png", params = "-genmipmap")}
 * </pre>
 * 
 * Supported params are:
 * <ul>
 * <li>{@code -genmipmap}</li>
 * <li>{@code -minfilter [filter]}, with filters being:
 * nearest/linear/mipmap/mipmapnearestnearest/mipmaplinearnearest/mipmaplinearlinear</li>
 * <li>{@code -magfilter [filter]}</li>
 * <li>{@code -wrapu [wrap]}, with wrap being: repeat/mirror/clamp</li>
 * <li>{@code -wrapv [wrap]}</li>
 * </ul>
 * 
 * @author NouCake
 */
public class TextureAssetLoaderParametersFactory
		implements AssetLoaderParametersFactory<Texture> {

	private Map<String, Texture.TextureFilter> filterStrings;
	private Map<String, Texture.TextureWrap> wrapStrings;

	public TextureAssetLoaderParametersFactory() {
		filterStrings = new HashMap<>();
		filterStrings.put("nearest", Texture.TextureFilter.Nearest);
		filterStrings.put("linear", Texture.TextureFilter.Linear);
		filterStrings.put("mipmap", Texture.TextureFilter.MipMap);
		filterStrings.put("mipmapnearestnearest",
				Texture.TextureFilter.MipMapNearestNearest);
		filterStrings.put("mipmaplinearnearest",
				Texture.TextureFilter.MipMapLinearNearest);
		filterStrings.put("mipmapnearestlinear",
				Texture.TextureFilter.MipMapNearestLinear);
		filterStrings.put("mipmaplinearlinear",
				Texture.TextureFilter.MipMapLinearLinear);

		wrapStrings = new HashMap<>();
		wrapStrings.put("repeat", Texture.TextureWrap.Repeat);
		wrapStrings.put("mirror", Texture.TextureWrap.MirroredRepeat);
		wrapStrings.put("clamp", Texture.TextureWrap.ClampToEdge);
	}

	@Override
	public AssetLoaderParameters<Texture> newInstance(String path,
			String params) {
		TextureParameter param = new TextureParameter();
		if (params == null || params.isEmpty())
			return param;

		List<String> splits = Arrays.asList(params.toLowerCase().split(" "));

		if (splits.contains("-genmipmap"))
			param.genMipMaps = true;

		if (splits.contains("-minfilter")) {
			String nextArg = getNextArgument(splits, "-minfilter");
			if (nextArg != null) {
				param.minFilter = getFilterFromString(nextArg);
			}
		}

		if (splits.contains("-magfilter")) {
			String nextArg = getNextArgument(splits, "-magfilter");
			if (nextArg != null) {
				param.magFilter = getFilterFromString(nextArg);
			}
		}

		if (splits.contains("-wrapu")) {
			String nextArg = getNextArgument(splits, "-wrapu");
			if (nextArg != null) {
				param.wrapU = getWrapFromString(nextArg);
			}
		}

		if (splits.contains("-wrapv")) {
			String nextArg = getNextArgument(splits, "-wrapv");
			if (nextArg != null) {
				param.wrapV = getWrapFromString(nextArg);
			}
		}

		return param;
	}

	@Nullable
	private static String getNextArgument(List<String> splits, String arg) {
		int argIndex = splits.indexOf(arg);
		if (argIndex + 1 < splits.size())
			return splits.get(argIndex + 1);
		return null;
	}

	private Texture.TextureFilter getFilterFromString(String filter) {
		if (filterStrings.containsKey(filter))
			return filterStrings.get(filter);
		return Texture.TextureFilter.Linear;
	}

	private Texture.TextureWrap getWrapFromString(String wrap) {
		if (wrapStrings.containsKey(wrap))
			return wrapStrings.get(wrap);
		return Texture.TextureWrap.Repeat;
	}

}