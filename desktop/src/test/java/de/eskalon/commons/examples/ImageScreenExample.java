package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractImageScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;

public class ImageScreenExample extends AbstractEskalonExample {

	@Override
	protected String initApp() {
		screenManager.addScreen("test-screen", new TestScreen());
		return "test-screen";
	}

	public class TestScreen extends AbstractImageScreen {

		private BitmapFont font;

		public TestScreen() {
			super(getPrefWidth(), getPrefHeight());
		}

		@Override
		protected void create() {
			setImage(new Texture(Gdx.files.internal("test.png")));

			addInputProcessor(new DefaultInputProcessor() {
				@Override
				public boolean keyDown(int keycode) {
					if (keycode == Keys.M) {
						setMode(ImageScreenMode
								.values()[(getMode().ordinal() + 1)
										% ImageScreenMode.values().length]);
					}
					return false;
				}
			});

			font = assetManager.get(EskalonCommonsAssets.DEFAULT_FONT_NAME);
		}

		@Override
		public void render(float delta) {
			super.render(delta);

			batch.begin();
			/*
			 * INFO
			 */
			font.draw(batch, getMode().toString(), 5, 15);
			batch.end();
		}

		@Override
		protected EskalonApplication getApplication() {
			return ImageScreenExample.this;
		}

	}

}
