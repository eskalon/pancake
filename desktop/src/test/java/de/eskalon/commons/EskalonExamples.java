package de.eskalon.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import de.eskalon.commons.examples.AbstractEskalonExample;
import de.eskalon.commons.examples.ImageScreenExample;
import de.eskalon.commons.examples.PostProcessingExample;
import de.eskalon.commons.examples.PostProcessingSimpleLayerExample;
import de.eskalon.commons.examples.PostProcessingComplexLayerExample;

//Based on libGDX's GdxTests
public class EskalonExamples {

	public static final List<Class<? extends AbstractEskalonExample>> TESTS = new ArrayList<>(
			Arrays.asList(
			// @formatter:off
					ImageScreenExample.class,
					PostProcessingExample.class,
					PostProcessingSimpleLayerExample.class,
					PostProcessingComplexLayerExample.class
			// @formatter:on
			));

	public static @Nullable AbstractEskalonExample newTest(String testName) {
		try {
			return forName(testName).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static @Nullable Class<? extends AbstractEskalonExample> forName(
			String name) {
		for (Class clazz : TESTS) {
			if (clazz.getSimpleName().equals(name))
				return clazz;
		}
		return null;
	}

}
