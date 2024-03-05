package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;

public class PositionalAudioExample extends AbstractEskalonApplication {

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		EskalonInjector.instance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public class TestScreen extends BlankScreen {

		private BitmapFont font;

		@Inject // needed so the class does not have to be static
		public TestScreen() {
			soundManager.addSoundEffect(
					Gdx.audio.newSound(Gdx.files.internal("sound_mono.wav")),
					"test_sound");

			addInputProcessor(new DefaultInputProcessor() {
				@Override
				public boolean touchDown(int screenX, int screenY, int pointer,
						int button) {
					if (button == Buttons.LEFT) {
						// x = left/right; y = up/down; z = front/back
						soundManager.playSoundEffect("test_sound")
								.setSoundPosition(
										30F * screenX / Gdx.graphics.getWidth()
												- 15F,
										17F * screenY / Gdx.graphics.getHeight()
												- 8.5F,
										0);
						return true;
					}
					return false;
				}
			});

			font = assetManager.get(EskalonCommonsAssets.DEFAULT_FONT_NAME);
		}

		@Override
		public void render(float delta) {
			batch.begin();
			font.draw(batch, "BOTTOM", 640, 20);
			font.draw(batch, "TOP", 640, 710);
			font.draw(batch, "LEFT", 10, 360);
			font.draw(batch, "RIGHT", 1230, 360);
			batch.end();
		}

	}

}
