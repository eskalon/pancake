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
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.inject.IInjector;
import de.eskalon.commons.inject.QualifiedProvider;
import de.eskalon.commons.inject.annotations.Inject;

public class AssetProviders {

	public static void bindAssetProviders(IInjector injector) {
		/* libGDX */
		injector.bindToQualifiedProvider(BitmapFont.class, Asset.class,
				BitmapAssetProvider.class);
		injector.bindToQualifiedProvider(Cubemap.class, Asset.class,
				CubemapAssetProvider.class);
		injector.bindToQualifiedProvider(I18NBundle.class, Asset.class,
				I18NBundleAssetProvider.class);
		injector.bindToQualifiedProvider(Model.class, Asset.class,
				ModelAssetProvider.class);
		injector.bindToQualifiedProvider(Music.class, Asset.class,
				MusicAssetProvider.class);
		injector.bindToQualifiedProvider(ParticleEffect.class, Asset.class,
				ParticleEffectAssetProvider.class);
		injector.bindToQualifiedProvider(Pixmap.class, Asset.class,
				PixmapAssetProvider.class);
		injector.bindToQualifiedProvider(ShaderProgram.class, Asset.class,
				ShaderProgramAssetProvider.class);
		injector.bindToQualifiedProvider(Skin.class, Asset.class,
				SkinAssetProvider.class);
		injector.bindToQualifiedProvider(Sound.class, Asset.class,
				SoundAssetProvider.class);
		injector.bindToQualifiedProvider(TextureAtlas.class, Asset.class,
				TextureAtlasAssetProvider.class);
		injector.bindToQualifiedProvider(Texture.class, Asset.class,
				TextureAssetProvider.class);

		/* Pancake */
		// injector.bindToQualifiedProvider(JSON.class, Asset.class,
		// new AssetTypeProvider<JSON>(assetManager));
		injector.bindToQualifiedProvider(PlaylistDefinition.class, Asset.class,
				PlaylistDefinitionAssetProvider.class);
		injector.bindToQualifiedProvider(Text.class, Asset.class,
				TextAssetProvider.class);
	}

	private AssetProviders() {
		throw new UnsupportedOperationException();
	}

	/*
	 * BASE CLASS
	 */
	public static abstract class AssetProvider<T>
			implements QualifiedProvider<T, Asset> {
		@Inject
		private AssetManager assetManager;

		@Override
		public T provide(Asset asset) {
			if (asset.disabled())
				return null;

			return assetManager.get(asset.value());
		}
	}

	/*
	 * LIBGDX PROVIDERS
	 */
	public static class BitmapAssetProvider extends AssetProvider<BitmapFont> {
	}

	public static class CubemapAssetProvider extends AssetProvider<Cubemap> {
	}

	public static class I18NBundleAssetProvider
			extends AssetProvider<I18NBundle> {
	}

	public static class ModelAssetProvider extends AssetProvider<Model> {
	}

	public static class MusicAssetProvider extends AssetProvider<Music> {
	}

	public static class ParticleEffectAssetProvider
			extends AssetProvider<ParticleEffect> {
	}

	public static class PixmapAssetProvider extends AssetProvider<Pixmap> {
	}

	public static class ShaderProgramAssetProvider
			extends AssetProvider<ShaderProgram> {
	}

	public static class SkinAssetProvider extends AssetProvider<Skin> {
	}

	public static class SoundAssetProvider extends AssetProvider<Sound> {
	}

	public static class TextureAtlasAssetProvider
			extends AssetProvider<TextureAtlas> {
	}

	public static class TextureAssetProvider extends AssetProvider<Texture> {
	}

	/*
	 * PANCAKE PROVIDERS
	 */
	public static class PlaylistDefinitionAssetProvider
			extends AssetProvider<PlaylistDefinition> {
	}

	public static class TextAssetProvider extends AssetProvider<Text> {
	}

}
