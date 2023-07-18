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

import java.util.NavigableMap;

/**
 * Math utils.
 * 
 * @author damios
 */
public final class MathStuffUtils {

	private MathStuffUtils() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Interpolates a <i>value</i> linearly.
	 *
	 * @param v0
	 * @param v1
	 * @param t
	 * @return
	 */
	public static float lerp(float v0, float v1, float t) {
		return v0 + t * (v1 - v0);
	}

	/**
	 * Interpolates a <i>point</i> linearly.
	 *
	 * @param y0
	 * @param x0
	 * @param y1
	 * @param x1
	 * @param x
	 * @return
	 */
	public static float lerp(float y0, float x0, float y1, float x1, float x) {
		return (y0 * (x1 - x) + y1 * (x - x0)) / (x1 - x0);
	}

	/**
	 * Converts a linear value to an exponential one. Is especially useful for
	 * sound levels.
	 *
	 * @param x
	 *            the linear value; is usually in the range of {@code 0} to
	 *            {@code 1}
	 * @param base
	 *            the exponential base to use
	 * @return
	 */
	public static double linToExp(double x, int base) {
		return (Math.pow(base, x) - 1) / (base - 1);
	}

	/**
	 * Converts an exponential value obtained via {@link #expToLin(double, int)}
	 * back to its original linear one.
	 *
	 * @param x
	 *            the exponential value
	 * @param base
	 *            the exponential base that was used
	 * @return
	 */
	public static double expToLin(double x, int base) {
		return log((x * (base - 1)) + 1, base);
	}

	/**
	 * @param x
	 * @param base
	 * @return the logarithm of {@code x} with base {@code a}
	 */
	public static double log(double x, int base) {
		return (Math.log(x) / Math.log(base));
	}

	public static float interpolateFunction(
			NavigableMap<Float, Float> functionValues, float x) {
		if (functionValues.containsKey(x))
			return functionValues.get(x);

		Float higherX = functionValues.higherKey(x);
		Float lowerX = functionValues.lowerKey(x);

		if (higherX == null)
			return functionValues.get(lowerX);

		if (lowerX == null)
			return functionValues.get(higherX);

		return lerp(lowerX, functionValues.get(lowerX), higherX,
				functionValues.get(higherX), x);
	}

}
