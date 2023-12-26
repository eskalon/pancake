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

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.Deflater;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import de.damios.guacamole.annotations.GwtIncompatible;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;

/**
 * Takes care of saving a screenshot.
 * 
 * @author damios
 */
@GwtIncompatible
public class ScreenshotUtils {

	private static final Logger LOG = LoggerService
			.getLogger(ScreenshotUtils.class);

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
		FileHandle file = Gdx.files
				.external(path + DATE_FORMAT.format(new Date()) + PNG_FILE_EXT);

		LOG.debug("Screenshot saved to %s", file.path());

		PixmapIO.writePNG(file, pixmap, Deflater.NO_COMPRESSION, true);
		pixmap.dispose();
	}

	/**
	 * Takes a screenshot of the currently rendered screen.
	 * 
	 * @return a pixmap
	 */
	public static Pixmap takeScreenshot() {
		return takeScreenshot(0, 0, Gdx.graphics.getBackBufferWidth(),
				Gdx.graphics.getBackBufferHeight(), false);
	}

	public static Pixmap takeScreenshot(int x, int y, int w, int h,
			boolean flipY) {
		Pixmap pixmap = Pixmap.createFromFrameBuffer(x, y, w, h);
		ByteBuffer pixels = pixmap.getPixels();

		final int numBytes = w * h * 4;

		// Flip horizontally
		if (flipY) {
			byte[] lines = new byte[numBytes];
			final int numBytesPerLine = w * 4;

			for (int i = 0; i < h; i++) {
				pixels.position((h - i - 1) * numBytesPerLine);
				pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
			}
			pixels.clear();
			pixels.put(lines);
		}

		// Remove transparency
		for (int i = 3; i < numBytes; i += 4) {
			pixels.put(i, (byte) 255);
		}

		return pixmap;
	}

}
