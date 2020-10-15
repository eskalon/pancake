package com.badlogic.gdx.utils.reflect;

import javax.annotation.Nullable;

/**
 * Reflection utils.
 * 
 * @author damios
 */
public class ReflectionUtils {

	private ReflectionUtils() {
		throw new UnsupportedOperationException();
	}

	public static Field convertFieldObject(java.lang.reflect.Field field) {
		// The constructor is package private
		return new Field(field);
	}

	/**
	 * Creates a class via libGDX reflection by using its name. Returns null, if the
	 * reflection or instantiation fails.
	 * 
	 * @param <T>
	 * @param className
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T> T newInstance(String className, Class<T> clazz) {
		try {
			return (T) ClassReflection.newInstance(ClassReflection.forName(className));
		} catch (ReflectionException e) {
			return null;
		}
	}

}
