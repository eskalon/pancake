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

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screen.ManagedScreen;

/**
 * A basic screen for use with {@link EskalonApplication}. Is automatically
 * registered as event listener when the screen is {@linkplain #show() shown}
 * /{@linkplain #hide() hidden}.
 * 
 * @author damios
 */
public abstract class AbstractEskalonScreen extends ManagedScreen {

	protected boolean isVisible = false;

	protected abstract EskalonApplication getApplication();

	@Override
	public void show() {
		isVisible = true;
		super.show();
		getApplication().getEventBus().register(this);
	}

	@Override
	public void hide() {
		isVisible = false;
		getApplication().getEventBus().unregister(this);
	}

	@Override
	public void resize(int width, int height) {
		// empty default method
	}

}
