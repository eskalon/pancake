package de.eskalon.commons.settings;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;

/**
 * Manages the actual game settings.
 */
public class EskalonSettings {

	protected final BetterPreferences preferences;
	private static final String MASTER_VOLUME = "masterVolume";
	private static final String EFFECT_VOLUME = "effectVolume";
	private static final String MUSIC_VOLUME = "musicVolume";
	private static final String KEYBINDING_PREFIX = "keybind_";

	private final Map<String, KeyBinding> keybinds;

	/**
	 * @param fileName
	 *                     the name of the preferences file
	 */
	public EskalonSettings(String fileName) {
		this.preferences = BetterPreferences.createInstance(Gdx.app.getPreferences(fileName));
		this.preferences.setAutoFlushing(true);

		this.keybinds = new HashMap<>();
	}

	// Master volume
	public float getMasterVolume() {
		return preferences.getFloat(MASTER_VOLUME, 0.5F);
	}

	public void setMasterVolume(float masterVolume) {
		preferences.putFloat(MASTER_VOLUME, masterVolume);
	}

	// Effect volume
	public float getEffectVolume() {
		return preferences.getFloat(EFFECT_VOLUME, 0.7F);
	}

	public void setEffectVolume(float effectVolume) {
		preferences.putFloat(EFFECT_VOLUME, effectVolume);
	}

	// Music volume
	public float getMusicVolume() {
		return preferences.getFloat(MUSIC_VOLUME, 0.7F);
	}

	public void setMusicVolume(float musicVolume) {
		preferences.putFloat(MUSIC_VOLUME, musicVolume);
	}

	// Keybinds
	private KeyBinding getKeybind(String name, int defaultKey) {
		if (keybinds.containsKey(name)) {
			return keybinds.get(name);
		} else {
			KeyBinding k = new KeyBinding(defaultKey);
			keybinds.put(name, k);
			preferences.putInteger(KEYBINDING_PREFIX + name, defaultKey);

			return k;
		}
	}

	public KeyBinding getKeybind(String name) {
		return getKeybind(name, KeyBinding.KEYCODE_NOT_SET);
	}

	public void setKeybind(String name, int key) {
		if (keybinds.containsKey(name)) {
			keybinds.get(name).setKeycode(key);
		} else {
			KeyBinding k = new KeyBinding(key);
			keybinds.put(name, k);
		}

		preferences.putInteger(KEYBINDING_PREFIX + name, key);
	}

	public void setDefaultKeybind(String name, int defaultKey) {
		if (!keybinds.containsKey(name)) {
			int key = preferences.getInteger(KEYBINDING_PREFIX + name, defaultKey);

			KeyBinding k = new KeyBinding(key);
			keybinds.put(name, k);
		}
	}

}
