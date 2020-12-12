package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;

import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;

public class TextureAssetLoaderParametersFactory
		implements AssetLoaderParametersFactory<Texture> {

	@Override
	public AssetLoaderParameters<Texture> newInstance(String path,
			String params) {
		TextureParameter param = new TextureParameter();
		if (params != null)
			if (params.contains("-genmipmap"))
				param.genMipMaps = true;

		return param;
	}

}