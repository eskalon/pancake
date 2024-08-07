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

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.eskalon.commons.core.StartArguments;
import de.eskalon.commons.event.CommonsEvents;
import de.eskalon.commons.event.CommonsEvents.CommonsAssetsLoadedEvent;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.inject.annotations.Inject;

/**
 * This screen is the first screen shown to the user when they start the game.
 * It displays the eskalon logo and loads the internal assets.
 * <p>
 * Upon completion, a {@link CommonsAssetsLoadedEvent} is posted.
 * 
 * @author damios
 */
public class EskalonSplashScreen extends AbstractEskalonScreen {

	private AssetManager assetManager;
	private @Inject SpriteBatch batch;
	private @Inject EventBus eventBus;

	private Viewport viewport;
	private Texture titleImage;

	private long startTime = -1;
	private long duration = 1325;
	private boolean skip;

	private boolean isDone = false;
	private int xPos;
	private int yPos;

	@Inject
	public EskalonSplashScreen(StartArguments startArgs,
			AssetManager assetManager) {
		this.assetManager = assetManager;
		this.skip = startArgs.shouldSkipSplashScreen();
		this.viewport = new ScreenViewport();

		// Don't use injections for performance reasons
		assetManager.load(EskalonCommonsAssets.LOGO_TEXTURE_PATH,
				Texture.class);
		titleImage = assetManager
				.finishLoadingAsset(EskalonCommonsAssets.LOGO_TEXTURE_PATH);

		/* Add common assets to loading queue */
		// Default font
		FreeTypeFontLoaderParameter fontParam = new FreeTypeFontLoaderParameter();
		fontParam.fontFileName = EskalonCommonsAssets.DEFAULT_FONT_PATH;
		fontParam.fontParameters.size = 14;
		assetManager.load(EskalonCommonsAssets.DEFAULT_FONT_NAME,
				BitmapFont.class, fontParam);

		// Shutter sound
		assetManager.load(EskalonCommonsAssets.SHUTTER_SOUND_PATH, Sound.class);
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();

		if (!skip)
			batch.draw(this.titleImage, xPos, yPos);

		if (startTime == -1) {
			this.startTime = System.currentTimeMillis();
		}

		if (!isDone && assetManager.update(1000 / 30) && (skip
				|| (startTime + duration) < System.currentTimeMillis())) {
			isDone = true;

			eventBus.post(new CommonsEvents.CommonsAssetsLoadedEvent());
		}

		batch.end();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		xPos = (width - titleImage.getWidth()) / 2;
		yPos = (height - titleImage.getHeight()) / 2 + 40;
	}

	@Override
	public void dispose() {
		// not needed; the title image might be used in other screens
	}

	/**
	 * The assets used by pancake itself. They are loaded, while the splash
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
