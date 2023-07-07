package de.eskalon.commons.input;

public interface DefaultInputListener<E extends Enum<E>, F extends Enum<F>>
		extends IInputListener<E, F> {

	public default boolean on(F id) {
		return false;
	}

	public default boolean off(F id) {
		return false;
	}

	public default boolean axisChanged(E id, float value) {
		return false;
	}

	public default boolean moved(int screenX, int screenY) {
		return false;
	}

}