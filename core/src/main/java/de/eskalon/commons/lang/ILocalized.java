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

/**
 * This interface is used to mark an already localized entity, i.e. one whose
 * name doesn't change in different languages or where the name is provided
 * programmatically.
 * 
 * @author damios
 */
public interface ILocalized {
	/**
	 * @return the localized name of the entity.
	 * @see Lang#get(ILocalized)
	 */
	public String getLocalizedName();
}
