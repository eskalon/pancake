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

package de.eskalon.commons.screens;

/**
 * A blank screen. Can be used for creating complex transitions.
 * 
 * @author damios
 */
public class BlankScreen extends AbstractEskalonScreen {

	@Override
	public void render(float delta) {
		// do nothing
	}

	@Override
	public void dispose() {
		// not needed
	}

}
