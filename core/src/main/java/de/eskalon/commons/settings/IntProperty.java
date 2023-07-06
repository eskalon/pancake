package de.eskalon.commons.settings;

import java.util.LinkedList;
import java.util.List;
import java.util.function.IntConsumer;

import javax.annotation.Nullable;

public final class IntProperty {

	private @Nullable List<IntConsumer> listeners = null;
	private int value;

	IntProperty(int value) {
		this.value = value;
	}

	public int get() {
		return value;
	}

	void set(int value) {
		this.value = value;

		if (listeners != null) {
			for (IntConsumer l : listeners) {
				l.accept(value);
			}
		}
	}

	public void addListener(IntConsumer listener) {
		if (listeners == null)
			listeners = new LinkedList<>();

		listeners.add(listener);
	}

	public void removeListener(IntConsumer listener) {
		listeners.remove(listener);
	}

}
