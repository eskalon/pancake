package de.eskalon.commons.examples;

import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.backends.lwjgl3.audio.Wav.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.BlankEskalonScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.commons.utils.MathStuffUtils;

public class PositionalAudioExample extends AbstractEskalonExample {

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen(this));
		return "test-screen";
	}

	public class TestScreen extends BlankEskalonScreen {

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
			float halfWidth = getPrefWidth() / 2;
			float halfHeight = getPrefHeight() / 2;

			batch.begin();
			font.draw(batch, "BOTTOM", 640, 20);
			font.draw(batch, "TOP", 640, 710);
			font.draw(batch, "LEFT", 10, 360);
			font.draw(batch, "RIGHT", 1230, 360);
			batch.end();
		}

	}

}
