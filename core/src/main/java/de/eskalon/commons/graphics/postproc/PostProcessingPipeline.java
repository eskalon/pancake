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

package de.eskalon.commons.graphics.postproc;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.utils.Disposable;

import de.damios.guacamole.Preconditions;
import de.eskalon.commons.utils.graphics.PingPongBufferHandler;

/**
 * A post processing pipeline.
 * <p>
 * Capture a screen with {@link #beginCapture()} and {@link #endCapture()} and
 * then render the effects {@linkplain #renderEffectsOntoBatch(SpriteBatch) onto
 * a batch} or {@linkplain #renderEffectsIntoTexture() into a texture}. Effects
 * can be added via {@link #addEffect(PostProcessingEffect)}.
 * <p>
 * <u>An example on how to use it:</u>
 * 
 * <pre>
 * boolean doPostProcessing = postProcessor.hasEffects();
 * if (doPostProcessing) {
 * 	postProcessor.beginCapture();
 * }
 * render(); // the actual rendering
 * if (doPostProcessing) {
 * 	postProcessor.endCapture();
 * 	postProcessor.renderEffectsOntoBatch(batch);
 * }
 * </pre>
 * 
 * 
 * @author damios
 */
public class PostProcessingPipeline implements Disposable {

	private LinkedList<PostProcessingEffect> effects = new LinkedList<>();
	private PingPongBufferHandler bufferHandler;
	private int width, height;
	private boolean hasDepth; // is needed for resize()
	private boolean isCapturing = false;

	public PostProcessingPipeline(int screenWidth, int screenHeight,
			boolean hasDepth) {
		this.hasDepth = hasDepth;
		this.width = screenWidth;
		this.height = screenHeight;
		this.bufferHandler = new PingPongBufferHandler(Format.RGBA8888,
				HdpiUtils.toBackBufferX(screenWidth),
				HdpiUtils.toBackBufferY(screenHeight), hasDepth);
	}

	public void beginCapture() {
		Preconditions.checkState(!isCapturing);
		isCapturing = true;
		bufferHandler.getCurrentBuffer().begin();
	}

	public void endCapture() {
		Preconditions.checkState(isCapturing);
		isCapturing = false;
		bufferHandler.getCurrentBuffer().end();
	}

	public void renderEffectsOntoBatch(SpriteBatch batch) {
		TextureRegion t = new TextureRegion(renderEffectsIntoTexture());
		t.flip(false, true);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.begin();
		batch.draw(t, 0, 0, width, height);
		batch.end();
	}

	/**
	 * @return a texture with the effects applied to the previously captured
	 *         screen; is turned upside down
	 */
	public Texture renderEffectsIntoTexture() {
		if (effects.size() > 0) {
			for (int i = 0; i < effects.size(); i++) {
				PostProcessingEffect effect = effects.get(i);
				effect.apply(bufferHandler.getLastTexture(),
						bufferHandler.getCurrentBuffer());
			}
		}

		return bufferHandler.getLastTexture();
	}

	public void addEffect(PostProcessingEffect effect) {
		effects.add(effect);
	}

	public void removeEffect(PostProcessingEffect effect) {
		effects.remove(effect);
	}

	public void resize(int width, int height) {
		bufferHandler.dispose();
		bufferHandler = new PingPongBufferHandler(Format.RGBA8888,
				HdpiUtils.toBackBufferX(width), HdpiUtils.toBackBufferX(height),
				hasDepth);
	}

	@Override
	public void dispose() {
		bufferHandler.dispose();

		for (PostProcessingEffect effect : effects) {
			effect.dispose();
		}
	}

	public boolean hasEffects() {
		return !effects.isEmpty();
	}

}
