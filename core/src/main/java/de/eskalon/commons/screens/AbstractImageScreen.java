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

import javax.annotation.Nullable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.Preconditions;

/**
 * A basic screen displaying one texture.
 * 
 * @author damios
 */
public abstract class AbstractImageScreen extends AbstractEskalonScreen {

	public enum ImageScreenMode {
		/**
		 * The image is stretched to fit the screen.
		 */
		STRETCH,
		/**
		 * The image is scaled to fit the screen. The initial aspect ratio is
		 * kept.
		 */
		SCALE,
		/**
		 * The image is scaled to fit the screen and centered. The initial
		 * aspect ratio is kept.
		 */
		CENTERED_SCALE,
		/**
		 * The image is displayed in its original size.
		 */
		ORIGINAL_SIZE,
		/**
		 * The image is displayed in its original size and centered.
		 */
		CENTERED_ORIGINAL_SIZE
	}

	public static final ImageScreenMode DEFAULT_SCREEN_MODE = ImageScreenMode.STRETCH;

	private @Nullable Texture image;
	private Vector2 dimensions;
	private Vector2 position;

	private Viewport viewport;
	private ImageScreenMode mode;

	public AbstractImageScreen(int screenWidth, int screenHeight) {
		this.dimensions = new Vector2(screenWidth, screenHeight);
		this.position = new Vector2(0, 0);
		this.mode = DEFAULT_SCREEN_MODE;
		this.viewport = new ScreenViewport();
	}

	@Override
	protected void create() {
		// do nothing
	}

	@Override
	public void render(float delta) {
		if (image != null) {
			viewport.apply();
			getApplication().getSpriteBatch()
					.setProjectionMatrix(viewport.getCamera().combined);

			getApplication().getSpriteBatch().begin();
			getApplication().getSpriteBatch().draw(this.image, this.position.x,
					this.position.y, this.dimensions.x, this.dimensions.y);
			getApplication().getSpriteBatch().end();
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		calculateDimensions();
	}

	public @Nullable Texture getImage() {
		return this.image;
	}

	public ImageScreenMode getMode() {
		return this.mode;
	}

	/**
	 * Changes the displayed image.
	 * 
	 * @param image
	 */
	public void setImage(@Nullable Texture image) {
		this.image = image;
		this.calculateDimensions();
	}

	/**
	 * Sets the display mode used for the {@link #image}.
	 * 
	 * @param mode
	 */
	public void setMode(ImageScreenMode mode) {
		Preconditions.checkNotNull(mode);
		this.mode = mode;
		this.calculateDimensions();
	}

	/**
	 * Adjusts {@link #dimensions} and {@link #position} to fit the set
	 * {@link #mode}.
	 */
	private void calculateDimensions() {
		if (image == null)
			return;

		float scl;

		switch (this.mode) {
		case STRETCH:
			this.dimensions.set(getApplication().getWidth(),
					getApplication().getHeight());
			this.position.set(0, 0);
			break;
		case SCALE:
			scl = image.getWidth() - getApplication().getWidth() >= image
					.getHeight() - getApplication().getHeight()
							? getApplication().getWidth()
									/ (float) image.getWidth()
							: getApplication().getHeight()
									/ (float) image.getHeight();
			this.dimensions.set(image.getWidth() * scl,
					image.getHeight() * scl);
			this.position.set(0, 0);
			break;
		case CENTERED_SCALE:
			scl = image.getWidth() - getApplication().getWidth() >= image
					.getHeight() - getApplication().getHeight()
							? getApplication().getWidth()
									/ (float) image.getWidth()
							: getApplication().getHeight()
									/ (float) image.getHeight();
			this.dimensions.set(image.getWidth() * scl,
					image.getHeight() * scl);
			this.position.set(
					(getApplication().getWidth() - this.dimensions.x) / 2, 0);
			break;
		case ORIGINAL_SIZE:
			this.dimensions.set(image.getWidth(), image.getHeight());
			this.position.set(0, 0);
			break;
		case CENTERED_ORIGINAL_SIZE:
			this.dimensions.set(image.getWidth(), image.getHeight());
			this.position.set(
					(getApplication().getWidth() - image.getWidth()) / 2F,
					(getApplication().getHeight() - image.getHeight()) / 2F);
			break;
		}
	}

	@Override
	public void dispose() {
		// not needed
	}
}
