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

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;

/**
 * Example for an {@link Asset @Asset} annotation loading a {@link BitmapFont}:
 * 
 * <pre>
 * {@code @Asset(value = "fontNameUsedInAssetManager.ttf", params = "path/to/font.ttf, 18")}
 * </pre>
 * 
 * @author damios
 */
public class BitmapFontAssetLoaderParametersFactory
		implements AssetLoaderParametersFactory<BitmapFont> {

	@Override
	public AssetLoaderParameters<BitmapFont> newInstance(String path,
			String params) {
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		String[] param = params.split(",");

		if (param.length == 2) {
			font.fontFileName = param[0];
			font.fontParameters.size = Integer.valueOf(param[1].trim());
			return font;
		}

		throw new IllegalArgumentException("The params '" + params
				+ "' for the asset '" + path + "' are not in a valid format.");
	}

}
