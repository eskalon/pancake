package de.eskalon.commons.settings;

import java.util.LinkedList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import de.damios.guacamole.func.FloatConsumer;

public final class FloatProperty {

	private @Nullable List<FloatConsumer> listeners = null;
	private float value;

	FloatProperty(float value) {
		this.value = value;
	}

	public float get() {
		return value;
	}

	void set(float value) {
		this.value = value;

		if (listeners != null) {
			for (FloatConsumer l : listeners) {
				l.accept(value);
			}
		}
	}

	public void addChangeListener(FloatConsumer listener) {
		if (listeners == null)
			listeners = new LinkedList<>();

		listeners.add(listener);
	}

	public void removeChangeListener(FloatConsumer listener) {
		listeners.remove(listener);
	}

	@Override
	public String toString() {
		return "FloatProperty{value=" + value + ",listeners=" + listeners + "}";
	}

}
