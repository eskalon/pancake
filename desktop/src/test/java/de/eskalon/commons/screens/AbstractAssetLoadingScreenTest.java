package de.eskalon.commons.screens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.BitmapFontAssetLoaderParametersFactory;
import de.eskalon.commons.core.EskalonApplication;

public class AbstractAssetLoadingScreenTest extends LibgdxUnitTest {

	public static AnnotationAssetManager createAssetManager() {
		FileHandleResolver resolver = new InternalFileHandleResolver();
		AnnotationAssetManager aM = new AnnotationAssetManager(resolver);
		aM.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(resolver));
		aM.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(resolver));

		aM.registerAssetLoaderParametersFactory(BitmapFont.class,
				new BitmapFontAssetLoaderParametersFactory());

		return aM;
	}

	private boolean isFinished = false;

	@Test
	public void test() {
		EskalonApplication game = Mockito.spy(EskalonApplication.class);
		AnnotationAssetManager a = createAssetManager();
		Mockito.doReturn(a).when(game).getAssetManager();

		AbstractAssetLoadingScreen s = new AbstractAssetLoadingScreen(game,
				"de.eskalon.commons.screens.test") {

			@Override
			public void dispose() {
			}

			@Override
			public void render(float delta, float progress) {
			}

			@Override
			protected void onFinishedLoading() {
				isFinished = true;
			}

			@Override
			protected void loadOwnAssets() {
			}
		};

		while (!isFinished)
			s.render(1);

		assertEquals(game, s.getApplication());

		assertTrue(a.isLoaded("test.png"));
		assertTrue(a.isLoaded("randomName.ttf"));
		assertTrue(!a.isLoaded("test2.png"));
		// used in CustomAssetTypesTest
		assertTrue(!a.isLoaded("text.txt"));
	}

}
