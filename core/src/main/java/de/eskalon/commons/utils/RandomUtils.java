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

import java.util.List;
import java.util.Random;

import de.damios.guacamole.Preconditions;

/**
 * A utility class for dealing with random numbers and chances.
 * 
 * @author damios
 */
public final class RandomUtils {

	public static final Random RANDOM = new Random();

	private RandomUtils() {
		throw new UnsupportedOperationException();
	}

	/*
	 * INTEGER
	 */
	/**
	 * Generates a random number within the specified range.
	 *
	 * @param min
	 *            included minimal value
	 * @param max
	 *            included maximum value
	 * @return the random integer
	 */
	public static int getInt(int min, int max) {
		return getInt(RANDOM, min, max);
	}

	/**
	 * Generates a random number within the specified range.
	 *
	 * @param random
	 *            the random generator used
	 * @param min
	 *            included minimal value
	 * @param max
	 *            included maximum value
	 * @return the random integer
	 */
	public static int getInt(Random random, int min, int max) {
		Preconditions.checkNotNull(random, "The random cannot be null");
		Preconditions.checkArgument(min <= max,
				"min needs to be less than or euqal max");

		return random.nextInt(max - min + 1) + min;
	}

	/*
	 * BOOLEAN
	 */
	/**
	 * Flips a coin and returns true with a chance of 50%.
	 *
	 * @return
	 */
	public static boolean isTrue() {
		return isTrue(2);
	}

	/**
	 * Rolls a die and returns true with a chance of {@code 1/x }.
	 *
	 * @param x
	 *            the reciprocal of the chance
	 * @return whether the roll succeeded
	 */
	public static boolean isTrue(int x) {
		return isTrue(RANDOM, x);
	}

	/**
	 * Rolls a die and returns true with a chance of {@code 1/x }.
	 *
	 * @param random
	 *            the random generator used
	 * @param x
	 *            the reciprocal of the chance
	 * @return whether the roll succeeded
	 */
	public static boolean isTrue(Random random, int x) {
		return getInt(random, 1, x) == 1;
	}

	/*
	 * ELEMENTS IN A LIST
	 */
	/**
	 * Returns a random element from the given {@link List}.
	 *
	 * @param random
	 * @param list
	 * @return the randomly selected element; {@code null} if the list is empty
	 */
	public static <T> T getElement(Random random, List<T> list) {
		Preconditions.checkNotNull(list, "The list cannot be null");

		if (list.isEmpty())
			return null;

		T item = list.get(getInt(random, 0, list.size() - 1));
		return item;
	}

	/**
	 * Returns a random element from the given {@link List}.
	 *
	 * @param list
	 * @return the randomly selected element; {@code null} if the list is empty
	 */
	public static <T> T getElement(List<T> list) {
		return getElement(RANDOM, list);
	}

	/**
	 * Returns a random element from the given array.
	 *
	 * @param random
	 * @param array
	 * @return the randomly selected element; {@code null} if the array is empty
	 */
	public static <T> T getElement(Random random, T[] array) {
		if (array.length == 0)
			return null;
		return array[getInt(random, 0, array.length - 1)];
	}

	/**
	 * Returns a random element from the given array.
	 *
	 * @param array
	 * @return the randomly selected element; {@code null} if the array is empty
	 */
	public static <T> T getElement(T[] array) {
		return getElement(RANDOM, array);
	}
}
