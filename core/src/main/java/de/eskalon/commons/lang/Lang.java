/*
 * Copyright 2020 eskalon
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

package de.eskalon.commons.lang;

import java.text.ChoiceFormat;

import com.badlogic.gdx.utils.I18NBundle;

/**
 * This utility class helps taking care of the localization.
 * <p>
 * The main method is {@link #get(String, Object...)}.
 * 
 * @author damios
 * @see I18NBundle
 * @see ILocalizable
 * @see ILocalized
 */
public class Lang {

	private static I18NBundle bundle;

	private Lang() {
		// shouldn't get instantiated
	}

	/**
	 * @param bundle
	 *            the used language bundle.
	 */
	public static void setBundle(I18NBundle bundle) {
		Lang.bundle = bundle;
	}

	/**
	 * @param key
	 * @return the localization for a given key
	 */
	public static String get(String key) {
		return bundle.get(key);
	}

	/**
	 * @param localizable
	 * @return the localization for a localizable object
	 */
	public static String get(ILocalizable localizable) {
		return get(localizable.getUnlocalizedName());
	}

	/**
	 * @param localized
	 * @return the localization for an already localized object
	 */
	public static String get(ILocalized localized) {
		return localized.getLocalizedName();
	}

	/**
	 * Localizes a given key via the localization
	 * {@linkplain #setBundle(I18NBundle) bundle}. The given String is formatted
	 * with the specified arguments.
	 * <ul>
	 * <li>boolean arguments are cast to an integer ({@code 0}/{@code 1}), which
	 * easily allows using {@link ChoiceFormat}</li>
	 * <li>arguments implementing {@link ILocalizable} are themselves localized
	 * via the bundle</li>
	 * <li>arguments implementing {@link ILocalized} are localized by using
	 * their name</li>
	 * </ul>
	 * 
	 * @param key
	 *            the localization key (= unlocalized name)
	 * @param args
	 *            the used parameters; each one of these will be localized as
	 *            well, if possible
	 * @return the localized text
	 */
	public static String get(String key, Object... args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Boolean) {
					// Cast boolean to integer -> is usable for ChoiceFormat
					args[i] = ((Boolean) args[i]) ? 1 : 0;
				} else if (args[i] instanceof ILocalizable) {
					// Localize the given object
					args[i] = get((ILocalizable) args[i]);
				} else if (args[i] instanceof ILocalized) {
					// If the object is already localized use its name
					args[i] = get(((ILocalized) args[i]));
				}
			}
		}

		return bundle.format(key, args);
	}

}
