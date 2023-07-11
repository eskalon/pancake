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
import com.badlogic.gdx.utils.reflect.Field;

import de.damios.guacamole.gdx.assets.Text;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.inject.IInjector;
import de.eskalon.commons.inject.QualifiedProvider;

public class AssetProvider {

	public static void bindAssetProvider(IInjector injector,
			AnnotationAssetManager assetManager) {
		/* libGDX */
		injector.bindToQualifiedProvider(BitmapFont.class,
				new AssetTypeProvider<BitmapFont>(assetManager));
		injector.bindToQualifiedProvider(Cubemap.class,
				new AssetTypeProvider<Cubemap>(assetManager));
		injector.bindToQualifiedProvider(I18NBundle.class,
				new AssetTypeProvider<I18NBundle>(assetManager));
		injector.bindToQualifiedProvider(Model.class,
				new AssetTypeProvider<Model>(assetManager));
		injector.bindToQualifiedProvider(Music.class,
				new AssetTypeProvider<Music>(assetManager));
		injector.bindToQualifiedProvider(ParticleEffect.class,
				new AssetTypeProvider<ParticleEffect>(assetManager));
		injector.bindToQualifiedProvider(Pixmap.class,
				new AssetTypeProvider<Pixmap>(assetManager));
		injector.bindToQualifiedProvider(ShaderProgram.class,
				new AssetTypeProvider<ShaderProgram>(assetManager));
		injector.bindToQualifiedProvider(Skin.class,
				new AssetTypeProvider<Skin>(assetManager));
		injector.bindToQualifiedProvider(Sound.class,
				new AssetTypeProvider<Sound>(assetManager));
		injector.bindToQualifiedProvider(TextureAtlas.class,
				new AssetTypeProvider<TextureAtlas>(assetManager));
		injector.bindToQualifiedProvider(Texture.class,
				new AssetTypeProvider<Texture>(assetManager));

		/* Pancake */
		// injector.bindToQualifiedProvider(JSON.class,
		// new AssetTypeProvider<JSON>(assetManager));
		injector.bindToQualifiedProvider(PlaylistDefinition.class,
				new AssetTypeProvider<PlaylistDefinition>(assetManager));
		injector.bindToQualifiedProvider(Text.class,
				new AssetTypeProvider<Text>(assetManager));
	}

	private AssetProvider() {
		throw new UnsupportedOperationException();
	}

	private static class AssetTypeProvider<T> implements QualifiedProvider<T> {

		private AssetManager assetManager;

		private AssetTypeProvider(AssetManager assetManager) {
			this.assetManager = assetManager;
		}

		@Override
		public T provide(Field field) {
			Asset asset = ReflectionUtils.getAnnotationObject(field,
					Asset.class);
			if (asset == null || asset.disabled())
				return null;

			return assetManager.get(asset.value());
		}

	}

}
