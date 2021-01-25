/*
 * Copyright 2020 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.screens;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.event.CommonsEvents.CommonsAssetsLoadedEvent;

/**
 * This screen is the first screen shown to the user when he starts the game. It
 * shows the eskalon logo and loads some internal assets.
 * 
 * @author damios
 */
public class EskalonSplashScreen extends AbstractEskalonScreen {

	private EskalonApplication game;
	private Texture titleImage;

	private long startTime = -1;
	private long duration = 1325;
	private boolean skip;

	private boolean isDone = false;
	private int xPos;
	private int yPos;

	public EskalonSplashScreen(EskalonApplication game) {
		this(game, false);
	}

	public EskalonSplashScreen(EskalonApplication game, boolean skip) {
		this.game = game;
		this.skip = skip;
	}

	@Override
	protected void create() {
		// don't use injections for performance reasons
		game.getAssetManager().load(EskalonCommonsAssets.LOGO_TEXTURE_PATH,
				Texture.class);
		titleImage = game.getAssetManager()
				.finishLoadingAsset(EskalonCommonsAssets.LOGO_TEXTURE_PATH);

		addCommonAssets();
	}

	@Override
	public void render(float delta) {
		game.getSpriteBatch().begin();
		game.getSpriteBatch().setProjectionMatrix(game.getUICamera().combined);
		if (!skip)
			game.getSpriteBatch().draw(this.titleImage, xPos, yPos);

		if (startTime == -1) {
			this.startTime = System.currentTimeMillis();
		}

		if (!isDone && game.getAssetManager().update(1000 / 30) && (skip
				|| (startTime + duration) < System.currentTimeMillis())) {
			isDone = true;
			onCommonAssetsFinished();

			game.getEventBus().dispatch(new CommonsAssetsLoadedEvent());
		}

		game.getSpriteBatch().end();
	}

	private void addCommonAssets() {
		// Default font
		FreeTypeFontLoaderParameter fontParam = new FreeTypeFontLoaderParameter();
		fontParam.fontFileName = EskalonCommonsAssets.DEFAULT_FONT_PATH;
		fontParam.fontParameters.size = 14;
		game.getAssetManager().load(EskalonCommonsAssets.DEFAULT_FONT_NAME,
				BitmapFont.class, fontParam);

		// Shutter sound
		game.getAssetManager().load(EskalonCommonsAssets.SHUTTER_SOUND_PATH,
				Sound.class);
	}

	private void onCommonAssetsFinished() {
		game.getSoundManager().addSoundEffect(
				game.getAssetManager()
						.get(EskalonCommonsAssets.SHUTTER_SOUND_PATH),
				EskalonCommonsAssets.SHUTTER_SOUND_NAME);
	}

	@Override
	public void resize(int width, int height) {
		xPos = (width - titleImage.getWidth()) / 2;
		yPos = (height - titleImage.getHeight()) / 2 + 40;
	}

	@Override
	protected EskalonApplication getApplication() {
		return game;
	}

	@Override
	public void dispose() {
		// not needed
	}

	/**
	 * The assets used by pancake itself. They are
	 * {@link EskalonSplashScreen#addCommonAssets() loaded}, while the splash
	 * screen is shown.
	 */
	public static class EskalonCommonsAssets {
		public static final String LOGO_TEXTURE_PATH = "resources/eskalon.png";
		private static final String DEFAULT_FONT_PATH = "resources/fonts/OpenSans-Regular.ttf";
		public static final String DEFAULT_FONT_NAME = "commons/default_font.ttf";
		public static final String SHUTTER_SOUND_PATH = "resources/audio/shutter.wav";
		public static final String SHUTTER_SOUND_NAME = "commons/shutter_sound";
	}

}
