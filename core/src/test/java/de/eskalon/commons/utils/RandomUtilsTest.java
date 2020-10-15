package de.eskalon.commons.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.Test;

public class RandomUtilsTest {
	@Test
	public void test() {
		for (int i = 0; i < 20; i++) {
			assertMinMax(RandomUtils.getInt(5, 9), 5, 9);
		}

		Random r = new Random();
		for (int i = 0; i < 20; i++) {
			assertMinMax(RandomUtils.getInt(r, 4, 8), 4, 8);
		}
	}

	public void assertMinMax(int actual, int minIncl, int maxIncl) {
		assertTrue(actual >= minIncl);
		assertTrue(actual <= maxIncl);
	}
}
