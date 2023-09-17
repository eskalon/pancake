package de.eskalon.commons.examples;

import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.examples.ImageScreenExample.TestScreen;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;

public class MusicExample extends AbstractEskalonApplication {

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		EskalonInjector.getInstance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public class TestScreen extends BlankScreen {

		@Inject // needed so the class does not have to be static
		public TestScreen() {
			assetManager.load("music/playlist.json", PlaylistDefinition.class);
			assetManager.finishLoading();

			PlaylistDefinition playlistDef = assetManager
					.get("music/playlist.json", PlaylistDefinition.class);

			PlaylistDefinition.addPlaylistDefinitionToSoundManager(playlistDef,
					soundManager, assetManager);
			soundManager.playMusic("default");
		}

	}
}