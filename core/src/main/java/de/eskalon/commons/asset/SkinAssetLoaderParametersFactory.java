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
import com.badlogic.gdx.assets.loaders.SkinLoader.SkinParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.AnnotationAssetManager.AssetLoaderParametersFactory;

/**
 * Example for an {@link Asset @Asset} annotation loading a {@link Skin} with
 * parameters:
 * 
 * <pre>
 * {@code @Asset(value = "path/to/skin.json", params = "path/to/skin.atlas")}
 * </pre>
 * 
 * @author damios
 */
public class SkinAssetLoaderParametersFactory
		implements AssetLoaderParametersFactory<Skin> {

	@Override
	public AssetLoaderParameters<Skin> newInstance(String path, String params) {
		SkinParameter skinParam = new SkinParameter(params);
		return skinParam;
	}

}
