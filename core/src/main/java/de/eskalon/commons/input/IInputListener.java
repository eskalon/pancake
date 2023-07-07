package de.eskalon.commons.input;

public interface IInputListener<E extends Enum<E>, F extends Enum<F>> {

	public boolean on(F id);

	public boolean off(F id);

	public boolean axisChanged(E id, float value);

	public boolean moved(int screenX, int screenY);

}