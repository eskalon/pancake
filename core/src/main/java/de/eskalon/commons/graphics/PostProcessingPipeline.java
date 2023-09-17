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

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.annotations.Singleton;

/**
 * A post processing pipeline.
 * <p>
 * Has to be {@linkplain #initialize(int, int, boolean) initialized} before it
 * can be used.
 * <p>
 * Capture a screen with {@link #beginCapture()} and {@link #endCapture()} and
 * then render the effects onto the screen via
 * {@link #renderEffectsToScreen(float)}. Effects can be added via
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
 * postProcessor.renderEffectsToScreen(Gdx.graphics.getDeltaTime());
 * </pre>
 * 
 * @author damios
 * @see VfxManager
 */
public class PostProcessingPipeline implements Disposable {

	private VfxManager vfxManager;
	private boolean disabled = false;
	private boolean doPostProcessing;

	private boolean initialized = false;

	@Inject
	@Singleton
	public PostProcessingPipeline() {
		// empty default constructor
	}

	public void initialize(int screenWidth, int screenHeight,
			boolean hasDepth) {
		this.vfxManager = new VfxManager(screenWidth, screenHeight, hasDepth);
		this.vfxManager.setBlendingEnabled(true); // this is useful if effects
													// should only be applied to
													// one layer, but not the
													// background.
		this.initialized = true;
	}

	public void beginCapture() {
		Preconditions.checkState(initialized,
				"The post processing pipeline has to be initalized first!");

		doPostProcessing = !disabled && vfxManager.hasEffects();

		if (doPostProcessing) {
			vfxManager.clear();
			vfxManager.beginCapture();
		}
	}

	public void endCapture() {
		if (doPostProcessing)
			vfxManager.endCapture();
	}

	public void renderEffectsToScreen(float delta) {
		if (doPostProcessing) {
			vfxManager.update(delta);
			vfxManager.applyEffects();
			vfxManager.renderToScreen();
		}
	}

	public void renderEffectsToFbo(NestableFrameBuffer fbo, float delta) {
		if (doPostProcessing) {
			vfxManager.update(delta);
			vfxManager.applyEffects();
			vfxManager.renderToFbo(fbo);
		}
	}

	public void addEffect(ChainVfxEffect effect) {
		vfxManager.addEffect(effect);
	}

	public void addEffects(ChainVfxEffect... effects) {
		for (ChainVfxEffect effect : effects) {
			addEffect(effect);
		}
	}

	public void removeEffect(ChainVfxEffect effect) {
		vfxManager.removeEffect(effect);
	}

	public void removeEffects(ChainVfxEffect... effects) {
		for (ChainVfxEffect effect : effects) {
			removeEffect(effect);
		}
	}

	public void removeAllEffects() {
		vfxManager.removeAllEffects();
	}

	public boolean hasEffects() {
		return vfxManager.hasEffects();
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void resize(int width, int height) {
		vfxManager.resize(width, height);
	}

	@Override
	public void dispose() {
		vfxManager.dispose();
	}

}
