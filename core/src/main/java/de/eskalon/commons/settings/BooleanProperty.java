package de.eskalon.commons.settings;

import java.util.LinkedList;
import java.util.List;

import org.jspecify.annotations.Nullable;

import de.damios.guacamole.func.BooleanConsumer;

public final class BooleanProperty {

	private @Nullable List<BooleanConsumer> listeners = null;
	private boolean value;

	BooleanProperty(boolean value) {
		this.value = value;
	}

	public boolean get() {
		return value;
	}

	void set(boolean value) {
		this.value = value;

		if (listeners != null) {
			for (BooleanConsumer l : listeners) {
				l.accept(value);
			}
		}
	}

	public void addChangeListener(BooleanConsumer listener) {
		if (listeners == null)
			listeners = new LinkedList<>();

		listeners.add(listener);
	}

	public void removeChangeListener(BooleanConsumer listener) {
		listeners.remove(listener);
	}

	@Override
	public String toString() {
		return "BooleanProperty{value=" + value + ",listeners=" + listeners
				+ "}";
	}

}
