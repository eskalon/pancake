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

import com.badlogic.gdx.graphics.Color;

/**
 * Color utils.
 * 
 * @author damios
 */
public final class ColorUtils {

	private ColorUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a color, that is located in between the given hue values.
	 * Deciding parameter is the {@code currentPercentage}.
	 *
	 * @param startHue
	 *            the minimum hue
	 * @param endHue
	 *            the maximum hue
	 * @param currentPercentage
	 *            where this color should be located in between the hue values
	 * @param saturation
	 *            the saturation
	 * @param value
	 *            the hue
	 * @return A color in between the min an max hue values, denoted by the
	 *         {@code currentPercentage}.
	 */
	public static Color getInterpolatedColor(float minHue, float maxHue,
			float currentPercentage, float saturation, float value) {
		float[] tmp = ColorUtils.hsvToRgb(
				MathStuffUtils.lerp(minHue, maxHue, currentPercentage),
				saturation, value);

		return new Color(tmp[0], tmp[1], tmp[2], 1);
	}

	/**
	 * Converts a hsv color to a rgb one.
	 *
	 * @param hue
	 * @param saturation
	 * @param value
	 * @return
	 */
	public static float[] hsvToRgb(float hue, float saturation, float value) {
		hue /= 360f;
		saturation /= 100f;
		value /= 100f;

		int h = (int) (hue * 6);
		float f = hue * 6 - h;
		float p = value * (1 - saturation);
		float q = value * (1 - f * saturation);
		float t = value * (1 - (1 - f) * saturation);

		switch (h) {
		case 0:
			return new float[] { value, t, p };
		case 1:
			return new float[] { q, value, p };
		case 2:
			return new float[] { p, value, t };
		case 3:
			return new float[] { p, q, value };
		case 4:
			return new float[] { t, p, value };
		case 5:
			return new float[] { value, p, q };
		default:
			throw new IllegalArgumentException(
					"Something went wrong when converting from HSV to RGB. Input was "
							+ hue + ", " + saturation + ", " + value);
		}
	}

}
