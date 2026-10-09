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

/**
 * Wrapper around System.nanoTime().
 * 
 * @author damios
 */
public final class TimeUtils {

	private static final long NANOS_PER_MILLI = 1000000;
	private static long currentlyFrozenSince = -1;
	private static long frozenNanos = -1;

	private TimeUtils() {
		throw new UnsupportedOperationException();
	}

	/*
	 * FREEZE TIME
	 */
	public static void freeze() {
		currentlyFrozenSince = realNano();
	}

	public static void unfreeze() {
		frozenNanos += realTimeSince(currentlyFrozenSince);
		currentlyFrozenSince = -1;
	}

	public static void thaw(long nanos) {
		frozenNanos -= nanos;
	}

	private static long getFreezingOffset() {
		return frozenNanos + (currentlyFrozenSince != -1
				? realTimeSince(currentlyFrozenSince)
				: 0);
	}

	/*
	 * GET TIME
	 */
	public static long nano() {
		return System.nanoTime() - getFreezingOffset();
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

	public static boolean haveMillisPassedSince(long prevNanoTime,
			long millisToPass) {
		return timeSince(prevNanoTime) >= toNanos(millisToPass);
	}

	public static boolean haveRealMillisPassedSince(long prevNanoTime,
			long millisToPass) {
		return realTimeSince(prevNanoTime) >= toNanos(millisToPass);
	}

}
