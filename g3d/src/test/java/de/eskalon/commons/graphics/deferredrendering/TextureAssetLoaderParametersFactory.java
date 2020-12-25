package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;

import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextureAssetLoaderParametersFactory
		implements AssetLoaderParametersFactory<Texture> {

	private Map<String, Texture.TextureFilter> filterStrings;
	private Map<String, Texture.TextureWrap> wrapStrings;

	public TextureAssetLoaderParametersFactory() {
		filterStrings = new HashMap<>();
		filterStrings.put("nearest", Texture.TextureFilter.Nearest);
		filterStrings.put("linear", Texture.TextureFilter.Linear);
		filterStrings.put("mipmap", Texture.TextureFilter.MipMap);
		filterStrings.put("mipmapnearestnearest", Texture.TextureFilter.MipMapNearestNearest);
		filterStrings.put("mipmaplinearnearest", Texture.TextureFilter.MipMapLinearNearest);
		filterStrings.put("mipmapnearestlinear", Texture.TextureFilter.MipMapNearestLinear);
		filterStrings.put("mipmaplinearlinear", Texture.TextureFilter.MipMapLinearLinear);

		wrapStrings = new HashMap<>();
		wrapStrings.put("repeat", Texture.TextureWrap.Repeat);
		wrapStrings.put("mirror", Texture.TextureWrap.MirroredRepeat);
		wrapStrings.put("clamp", Texture.TextureWrap.ClampToEdge);
	}

	@Override
	public AssetLoaderParameters<Texture> newInstance(String path,
			String params) {
		TextureParameter param = new TextureParameter();
		if (params == null) return param;
		
		List<String> splits = Arrays.asList(params.toLowerCase().split(" "));

		if (splits.contains("-genmipmap"))
			param.genMipMaps = true;

		if(splits.contains("-minfilter")){
			String nextArg = getNextArgument(splits, "-minfilter");
			if(nextArg != null){
				param.minFilter = getFilterFromString(nextArg);
			}
		}

		if(splits.contains("-magfilter")){
			String nextArg = getNextArgument(splits, "-magfilter");
			if(nextArg != null){
				param.magFilter = getFilterFromString(nextArg);
			}
		}

		if(splits.contains("-wrapu")){
			String nextArg = getNextArgument(splits, "-wrapu");
			if(nextArg != null){
				param.wrapU = getWrapFromString(nextArg);
			}
		}

		if(splits.contains("-wrapv")){
			String nextArg = getNextArgument(splits, "-wrapv");
			if(nextArg != null){
				param.wrapV = getWrapFromString(nextArg);
			}
		}

		return param;
	}

	private static String getNextArgument(List<String> splits, String arg){
		int argIndex = splits.indexOf(arg);
		if(argIndex + 1 < splits.size()) return splits.get(argIndex+1);
		return null;
	}

	private Texture.TextureFilter getFilterFromString(String filter){
		if(filterStrings.containsKey(filter)) return filterStrings.get(filter);
		return Texture.TextureFilter.Linear;
	}

	private Texture.TextureWrap getWrapFromString(String wrap){
		if(wrapStrings.containsKey(wrap)) return wrapStrings.get(wrap);
		return Texture.TextureWrap.Repeat;
	}

}