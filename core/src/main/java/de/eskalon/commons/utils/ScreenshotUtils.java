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

package de.eskalon.commons.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

import de.damios.guacamole.annotations.GwtIncompatible;

/**
 * Takes care of saving a screenshot.
 * 
 * @author damios
 */
@GwtIncompatible
public class ScreenshotUtils {

	private static final String PNG_FILE_EXT = ".png";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss-SSS");

	private ScreenshotUtils() {
		throw new UnsupportedOperationException();
	}

	public static void takeAndSaveScreenshot() {
		if (Gdx.app.getType() == ApplicationType.Desktop)
			takeAndSaveScreenshot("/Desktop/");
		else
			takeAndSaveScreenshot("");
	}

	/**
	 * Takes a screenshot of the currently rendered screen and saves it on disk
	 * as a PNG. The file's name is the current date and time in the format
	 * {@code "yyyy-MM-dd_HH-mm-ss-SSS"}.
	 * 
	 * @param path
	 */
	public static void takeAndSaveScreenshot(String path) {
		Pixmap pixmap = takeScreenshot();
		PixmapIO.writePNG(
				Gdx.files.external(
						path + DATE_FORMAT.format(new Date()) + PNG_FILE_EXT),
				pixmap);
		pixmap.dispose();
	}

	/**
	 * Takes a screenshot of the currently rendered screen.
	 * 
	 * @return a pixmap
	 */
	public static Pixmap takeScreenshot() {
		byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0,
				Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(), true);

		Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
		BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);

		return pixmap;
	}

}
