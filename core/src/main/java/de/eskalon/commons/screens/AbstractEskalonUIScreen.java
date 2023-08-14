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

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import de.eskalon.commons.core.EskalonApplication;

/**
 * The base class of all UI screens. It automatically loads the
 * {@linkplain #skin skin} of the {@linkplain EskalonApplication#getUISkin()
 * application} and sets the {@link #stage} as {@linkplain InputProcessor input
 * processor}. A {@linkplain #mainTable main table} is automatically added to
 * the stage for ease of use.
 * <p>
 * An UI skin has to be {@linkplain EskalonApplication#setUISkin(Skin) set for
 * the whole application} before this screen is instantiated.
 */
public abstract class AbstractEskalonUIScreen extends AbstractImageScreen {

	/**
	 * The used stage. Contains a {@linkplain #mainTable table} by default.
	 */
	protected Stage stage;
	/**
	 * The main table, to which the {@link Actor}s are usually added.
	 */
	protected Table mainTable;
	/**
	 * The default UI skin. Is automatically set upon instantiation.
	 */
	protected Skin skin;

	public AbstractEskalonUIScreen(int screenWidth, int screenHeight) {
		super(screenWidth, screenHeight);
		this.setMode(ImageScreenMode.CENTERED_ORIGINAL_SIZE);

		skin = getApplication().getUISkin();

		stage = new Stage(new ScreenViewport(),
				getApplication().getSpriteBatch());
		mainTable = new Table();
		stage.addActor(mainTable);
		mainTable.setFillParent(true);

		addInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		renderBackground(delta);

		stage.getViewport().apply();
		stage.act(delta);
		stage.draw();

		// Resets the alpha changes made by scene2d actions
		stage.getBatch().setColor(Color.WHITE);
	}

	protected void renderBackground(float delta) {
		super.render(delta);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (stage != null)
			stage.dispose();
	}

}
