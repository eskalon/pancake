package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.examples.InputBindingsExample.TestScreen;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;

public class PositionalAudioExample extends AbstractEskalonExample {

	@Override
	protected AbstractEskalonScreen initApp() {
		return new TestScreen(this);
	}

	public class TestScreen extends BlankScreen {

		private BitmapFont font;

		public TestScreen(EskalonApplication app) {
			super(app);

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
										30F * screenX / getPrefWidth() - 15F,
										17F * screenY / getPrefHeight() - 8.5F,
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
