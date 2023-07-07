package de.eskalon.commons.examples;

import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.BlankScreen;

public class MusicExample extends AbstractEskalonExample {

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen(this));
		return "test-screen";
	}

	public class TestScreen extends BlankScreen {

		public TestScreen(EskalonApplication app) {
			super(app);

			app.getAssetManager().load("music/playlist.json",
					PlaylistDefinition.class);
			getApplication().getAssetManager().finishLoading();

			PlaylistDefinition playlistDef = app.getAssetManager()
					.get("music/playlist.json", PlaylistDefinition.class);

			PlaylistDefinition.addPlaylistDefinitionToSoundManager(playlistDef,
					app.getSoundManager(), app.getAssetManager());
			app.getSoundManager().playMusic("default");
		}

	}
}