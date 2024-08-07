package de.eskalon.commons.inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManagerTest;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.providers.AssetProviders;

public class AnnotationAssetInjectionTest extends LibgdxUnitTest {

	@Test
	// Prints: #test3 cannot be resolved
	public void test() {
		AnnotationAssetManager aM = AnnotationAssetManagerTest
				.createAssetManager();

		// Loading assets for object
		AssetHolder holder = new AssetHolder();
		aM.loadAnnotatedAssets(AssetHolder.class);
		aM.finishLoading();

		// Inject the assets via EskalonInjector
		IInjector injector = new DefaultInjector();
		injector.bindToInstance(AssetManager.class, aM);
		AssetProviders.bindAssetProviders(injector);
		injector.injectMembers(holder);

		assertNotNull(holder.test1a);
		assertNotNull(holder.test1b);
		assertNotNull(AssetHolder.test2);
	}

	public static class AssetHolder {
		@Asset("test.png")
		private @Inject Texture test1a;

		@Asset(value = "randomName.ttf", params = "font/OpenSans.ttf, 19")
		private @Inject BitmapFont test1b;

		@Asset("test2.png")
		private static @Inject Texture test2;

		@Asset(value = "i_do_not_exist.png", disabled = true)
		private static @Inject Texture test3;
	}

}
