/*
 * Copyright 2026 eskalon
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

import de.damios.guacamole.Preconditions;

/**
 * Wrapper around System.nanoTime().
 * <p>
 * The time can be frozen. {@link #freeze()}, {@link #unfreeze()} and
 * {@link #thaw(long)} must always be called from the same thread; all other
 * methods are thread-safe.
 * 
 * @author damios
 */
public final class TimeUtils {

	private static final long NANOS_PER_MILLI = 1000000;
	private static volatile boolean frozen;
	private static volatile long frozenNanos;
	private static volatile long timeOfFreeze;

	private TimeUtils() {
		throw new UnsupportedOperationException();
	}

	/*
	 * FREEZE TIME
	 */
	public static void freeze() {
		Preconditions.checkState(!frozen);
		timeOfFreeze = nano();
		frozen = true;
	}

	public static void unfreeze() {
		Preconditions.checkState(frozen);
		frozenNanos = realNano() - timeOfFreeze;
		frozen = false;
	}

	public static void thaw(long nanos) {
		frozenNanos -= nanos;
		timeOfFreeze += nanos;
	}

	public static boolean isFrozen() {
		return frozen;
	}

	/*
	 * GET TIME
	 */
	public static long nano() {
		if (frozen)
			return timeOfFreeze;
		return System.nanoTime() - frozenNanos;
	}

	public static long realNano() {
		return System.nanoTime();
	}

	/*
	 * CONVERSION
	 */
	public static long toMillis(long nanos) {
		return nanos / NANOS_PER_MILLI;
	}

	public static long toNanos(long millis) {
		return millis * NANOS_PER_MILLI;
	}

	/*
	 * CALCULATE PASSED TIME
	 */
	public static long timeSince(long prevNanoTime) {
		return nano() - prevNanoTime;
	}

	public static long realTimeSince(long prevNanoTime) {
		return realNano() - prevNanoTime;
	}

	public static boolean haveNanosPassedSince(long prevNanoTime,
			long nanosToPass) {
		return timeSince(prevNanoTime) >= nanosToPass;
	}

	public static boolean haveRealNanosPassedSince(long prevNanoTime,
			long nanosToPass) {
		return realTimeSince(prevNanoTime) >= nanosToPass;
	}

	/**
	 * 
	 * @param prevNanoTime
	 *            this is the previous time in nano seconds!
	 * @param millisToPass
	 * @return
	 */
	public static boolean haveMillisPassedSince(long prevNanoTime,
			long millisToPass) {
		return timeSince(prevNanoTime) >= toNanos(millisToPass);
	}

	/**
	 * 
	 * @param prevNanoTime
	 *            this is the previous time in nano seconds!
	 * @param millisToPass
	 * @return
	 */
	public static boolean haveRealMillisPassedSince(long prevNanoTime,
			long millisToPass) {
		return realTimeSince(prevNanoTime) >= toNanos(millisToPass);
	}

}
