/*
 * Copyright 2023 eskalon
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

package de.eskalon.commons.inject.providers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.I18NBundle;

import de.damios.guacamole.gdx.assets.Text;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.inject.IInjector;
import de.eskalon.commons.inject.QualifiedProvider;

public class AssetProvider {

	public static void bindAssetProvider(IInjector injector,
			AnnotationAssetManager assetManager) {
		/* libGDX */
		injector.bindToQualifiedProvider(BitmapFont.class, Asset.class,
				new AssetTypeProvider<BitmapFont>(assetManager));
		injector.bindToQualifiedProvider(Cubemap.class, Asset.class,
				new AssetTypeProvider<Cubemap>(assetManager));
		injector.bindToQualifiedProvider(I18NBundle.class, Asset.class,
				new AssetTypeProvider<I18NBundle>(assetManager));
		injector.bindToQualifiedProvider(Model.class, Asset.class,
				new AssetTypeProvider<Model>(assetManager));
		injector.bindToQualifiedProvider(Music.class, Asset.class,
				new AssetTypeProvider<Music>(assetManager));
		injector.bindToQualifiedProvider(ParticleEffect.class, Asset.class,
				new AssetTypeProvider<ParticleEffect>(assetManager));
		injector.bindToQualifiedProvider(Pixmap.class, Asset.class,
				new AssetTypeProvider<Pixmap>(assetManager));
		injector.bindToQualifiedProvider(ShaderProgram.class, Asset.class,
				new AssetTypeProvider<ShaderProgram>(assetManager));
		injector.bindToQualifiedProvider(Skin.class, Asset.class,
				new AssetTypeProvider<Skin>(assetManager));
		injector.bindToQualifiedProvider(Sound.class, Asset.class,
				new AssetTypeProvider<Sound>(assetManager));
		injector.bindToQualifiedProvider(TextureAtlas.class, Asset.class,
				new AssetTypeProvider<TextureAtlas>(assetManager));
		injector.bindToQualifiedProvider(Texture.class, Asset.class,
				new AssetTypeProvider<Texture>(assetManager));

		/* Pancake */
		// injector.bindToQualifiedProvider(JSON.class, Asset.class,
		// new AssetTypeProvider<JSON>(assetManager));
		injector.bindToQualifiedProvider(PlaylistDefinition.class, Asset.class,
				new AssetTypeProvider<PlaylistDefinition>(assetManager));
		injector.bindToQualifiedProvider(Text.class, Asset.class,
				new AssetTypeProvider<Text>(assetManager));
	}

	private AssetProvider() {
		throw new UnsupportedOperationException();
	}

	private static class AssetTypeProvider<T>
			implements QualifiedProvider<T, Asset> {

		private AssetManager assetManager;

		private AssetTypeProvider(AssetManager assetManager) {
			this.assetManager = assetManager;
		}

		@Override
		public T provide(Asset asset) {
			if (asset.disabled())
				return null;

			return assetManager.get(asset.value());
		}

	}

}
