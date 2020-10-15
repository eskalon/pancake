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

import com.badlogic.gdx.utils.I18NBundle;

/**
 * This interface is used to mark localizable entities, i.e. entities whose name
 * is specified in a localization {@linkplain I18NBundle bundle}.
 * 
 * @author damios
 */
public interface ILocalizable {
	/**
	 * @return the unlocalized name of the entity.
	 * @see Lang#get(ILocalizable)
	 */
	public String getUnlocalizedName();
}
