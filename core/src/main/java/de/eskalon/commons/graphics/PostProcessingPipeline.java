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

package de.eskalon.commons.graphics;

import com.badlogic.gdx.utils.Disposable;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.ChainVfxEffect;

import de.eskalon.commons.screen.ScreenManager;

/**
 * A post processing pipeline.
 * <p>
 * Capture a screen with {@link #beginCapture()} and {@link #endCapture()} and
 * then render the effects onto the screen via
 * {@link #renderEffectsOntoScreen(float)}. Effects can be added via
 * {@link #addEffect(PostProcessingEffect)}.
 * <p>
 * If there are no active effects when {@link #beginCapture()} is called, the
 * post processor does nothing.
 * <p>
 * <u>An example of how to use it:</u>
 * 
 * <pre>
 * postProcessor.beginCapture();
 * render(); // the actual rendering
 * postProcessor.endCapture();
 * postProcessor.renderEffectsOntoBatch(batch);
 * </pre>
 * 
 * @author damios
 * @see VfxManager
 */
public class PostProcessingPipeline implements Disposable {

	private VfxManager vfxManager;
	private ScreenManager screenManager; // needed for the clear color
	private boolean hasDepth; // needed for resize()
	private boolean doPostProcessing;

	public PostProcessingPipeline(ScreenManager screenManager, int screenWidth,
			int screenHeight, boolean hasDepth) {
		this.vfxManager = new VfxManager(screenWidth, screenHeight, hasDepth);
		this.screenManager = screenManager;
		this.hasDepth = hasDepth;
	}

	public void beginCapture() {
		doPostProcessing = vfxManager.hasEffects();

		if (doPostProcessing) {
			vfxManager.clear(screenManager.getCurrentScreen().getClearColor());
			vfxManager.beginCapture();
		}
	}

	public void endCapture() {
		if (doPostProcessing)
			vfxManager.endCapture();
	}

	public void renderEffectsOntoScreen(float delta) {
		if (doPostProcessing) {
			vfxManager.update(delta);
			vfxManager.applyEffects();
			vfxManager.renderToScreen();
		}
	}

	public void addEffect(ChainVfxEffect effect) {
		vfxManager.addEffect(effect);
	}

	public void removeEffect(ChainVfxEffect effect) {
		vfxManager.removeEffect(effect);
	}

	public void removeAllEffects() {
		vfxManager.removeAllEffects();
	}

	public void resize(int width, int height) {
		vfxManager.resize(width, height);
	}

	@Override
	public void dispose() {
		vfxManager.dispose();
	}

}
