/*
 * Copyright 2023 eskalon
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

import java.util.function.Supplier;

import javax.annotation.Nullable;

import de.eskalon.commons.core.EskalonApplicationContext;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screen.ManagedScreen;
import de.eskalon.commons.screen.ScreenManager;
import de.eskalon.commons.screen.transition.ScreenTransition;

/**
 * A simple extension of {@link ScreenManager} that automatically (un)registers
 * screens as event listener whenever they are {@linkplain ManagedScreen#show()
 * shown} /{@linkplain ManagedScreen#hide() hidden}.
 */
public class EskalonScreenManager
		extends ScreenManager<AbstractEskalonScreen, ScreenTransition> {

	protected @Inject EventBus eventBus;
	protected @Inject EskalonApplicationContext appContext;

	/**
	 * Push a screen.
	 * 
	 * @param screen
	 * @param transitionName
	 */
	public void pushScreen(Class<? extends AbstractEskalonScreen> screenClass,
			@Nullable String transitionName) {
		super.pushScreen(() -> {
			return EskalonInjector.instance().getInstance(screenClass);
		}, () -> {
			return transitionName != null
					? appContext.getTransitions().get(transitionName)
					: null;
		});
	}

	public void pushScreen(Class<? extends AbstractEskalonScreen> screenClass) {
		pushScreen(screenClass, null);
	}

	@Override
	@Deprecated // use pushScreen(Class, String) instead
	public void pushScreen(AbstractEskalonScreen screen,
			@Nullable ScreenTransition transition) {
		super.pushScreen(screen, transition);
	}

	@Override
	@Deprecated // use pushScreen(Class, String) instead
	public void pushScreen(Supplier<AbstractEskalonScreen> screenSupplier,
			@Nullable Supplier<ScreenTransition> transitionSupplier) {
		super.pushScreen(screenSupplier, transitionSupplier);
	}

	@Override
	protected void initializeScreen(ManagedScreen newScreen) {
		eventBus.register(newScreen);
		super.initializeScreen(newScreen);
	}

	@Override
	protected void finalizeScreen(ManagedScreen oldScreen) {
		eventBus.unregister(oldScreen);
		super.finalizeScreen(oldScreen);
	}

}
