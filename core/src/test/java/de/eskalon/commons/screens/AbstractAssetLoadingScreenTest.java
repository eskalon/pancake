package de.eskalon.commons.screens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.eskalon.commons.LibgdxUnitTest;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManagerTest;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;

public class AbstractAssetLoadingScreenTest extends LibgdxUnitTest {

	private boolean isFinished = false;

	@Test
	public void test() {
		EskalonApplication game = Mockito.spy(EskalonApplication.class);
		AnnotationAssetManager a = AnnotationAssetManagerTest
				.createAssetManager();
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

		s.create();
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
