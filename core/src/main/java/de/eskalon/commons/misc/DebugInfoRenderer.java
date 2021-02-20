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

package de.eskalon.commons.misc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.damios.guacamole.Preconditions;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.graphics.ShaderCompatibilityHelper;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.utils.ColorUtils;
import de.eskalon.commons.utils.graphics.GL32CMacIssueHandler;

/**
 * Prints the current fps count, a corresponding graph and some other debug
 * information on screen.
 * <p>
 * Has to be continuously {@linkplain #update(float) updated} for the fps
 * counter to work.
 * 
 * @author damios
 */
public class DebugInfoRenderer {

	private static int MAX_SNAPSHOT_COUNT = 2 * 20;

	private Viewport viewport;
	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shapeRenderer;
	private int width;
	private int height;

	private ISoundManager soundManager;
	private String gameVersion;
	private String appType;
	private String debugLogging;

	private FPSCounter fpsCounter;
	private int fpsGraphY;
	private int fpsGraphX;

	/**
	 * Creates a debug info renderer.
	 * 
	 * @param batch
	 * @param gameVersion
	 * 
	 * @see #initilaize(int, int, BitmapFont)
	 */
	public DebugInfoRenderer(SpriteBatch batch, boolean debugLogging,
			String gameVersion, ISoundManager soundManager) {
		this.viewport = new ScreenViewport();
		this.batch = batch;

		this.gameVersion = gameVersion;
		this.appType = "App Type: " + Gdx.app.getType() + " ("
				+ System.getProperty("os.name") + ")";
		this.debugLogging = "Debug Logging: " + String.valueOf(debugLogging);
		this.soundManager = soundManager;
		this.fpsCounter = new FPSCounter(MAX_SNAPSHOT_COUNT);
		this.shapeRenderer = new ShapeRenderer(5000,
				ShaderCompatibilityHelper.mustUse32CShader()
						? GL32CMacIssueHandler
								.createImmediateModeRenderer20DefaultShader(
										false, true, 0)
						: null);
	}

	public void initialize(int width, int height, BitmapFont font) {
		this.font = font;
		resize(width, height);
	}

	public void update(float delta) {
		fpsCounter.update(delta);
	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		this.width = width;
		this.height = height;

		this.fpsGraphY = 5;
		this.fpsGraphX = width - 356;
	}

	public void render() {
		Preconditions.checkState(font != null,
				"The debug renderer has to be initialized first!");

		viewport.apply();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		/*
		 * INFO
		 */
		font.draw(batch, gameVersion, 6, height - 5);
		font.draw(batch, appType, 6, height - 25);
		font.draw(batch, debugLogging, 6, height - 45);

		font.draw(batch,
				"Threads in Pool: "
						+ ThreadHandler.getInstance().getActiveThreadCount()
						+ "/" + ThreadHandler.getInstance().getPoolSize(),
				6, height - 75);
		font.draw(batch, "Mouse Pos: (x) " + Gdx.input.getX() + " (y) "
				+ Gdx.input.getY(), 6, height - 95);

		font.draw(batch,
				"Music: \"" + soundManager.getCurrentMusicTitle() + "\"", 6,
				height - 125);

		/*
		 * FPS COUNT
		 */
		font.draw(batch,
				String.valueOf(fpsCounter.getFramesPerSecond()) + " FPS",
				width - 65, height - 5);

		/*
		 * FPS GRPAH
		 */
		font.draw(batch, "8 ms", fpsGraphX + 5 * MAX_SNAPSHOT_COUNT + 111,
				fpsGraphY + 43 + 6);
		font.draw(batch, "16 ms", fpsGraphX + 5 * MAX_SNAPSHOT_COUNT + 111,
				fpsGraphY + 86 + 6);
		font.draw(batch,
				"High: " + Math.round(
						fpsCounter.getPastFrameTimes().getHighest() * 10) / 10F
						+ " ms",
				fpsGraphX, fpsGraphY + 43);
		font.draw(batch,
				"Mean: " + Math.round(
						fpsCounter.getPastFrameTimes().getMean() * 10) / 10F
						+ " ms",
				fpsGraphX, fpsGraphY + 43 - 15);
		font.draw(batch,
				"Low: " + Math.round(
						fpsCounter.getPastFrameTimes().getLowest() * 10) / 10F
						+ " ms",
				fpsGraphX, fpsGraphY + 43 - 30);
		batch.end();

		shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.LIGHT_GRAY);
		// Horizontal bottom
		shapeRenderer.line(fpsGraphX + 100, fpsGraphY,
				fpsGraphX + 5 * (MAX_SNAPSHOT_COUNT + 1) + 100, fpsGraphY);
		// Horizontal mid
		shapeRenderer.line(fpsGraphX + 100, fpsGraphY + 43,
				fpsGraphX + 5 * (MAX_SNAPSHOT_COUNT + 1) + 100, fpsGraphY + 43);
		// Horizontal top
		shapeRenderer.line(fpsGraphX + 100, fpsGraphY + 86,
				fpsGraphX + 5 * (MAX_SNAPSHOT_COUNT + 1) + 100, fpsGraphY + 86);
		// Vertical left
		shapeRenderer.line(fpsGraphX + 101, fpsGraphY, fpsGraphX + 101,
				fpsGraphY + 86);
		// Vertical right
		shapeRenderer.line(fpsGraphX + 5 * (MAX_SNAPSHOT_COUNT + 1) + 100,
				fpsGraphY, fpsGraphX + 5 * (MAX_SNAPSHOT_COUNT + 1) + 100,
				fpsGraphY + 86);
		shapeRenderer.end();

		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		shapeRenderer.begin(ShapeType.Filled);
		// Background
		shapeRenderer.setColor(new Color(0.2F, 0.2F, 0.2F, 0.7F));
		shapeRenderer.rect(fpsGraphX + 100, fpsGraphY,
				5 * (MAX_SNAPSHOT_COUNT + 1), 86);

		// Bars
		float[] times = fpsCounter.getPastFrameTimes().getWindowValues();
		int startIndex = MAX_SNAPSHOT_COUNT - times.length;
		for (int i = 0; i < times.length; i++) {
			float frameTime = times[i];

			shapeRenderer.setColor(ColorUtils.getInterpolatedColor(0, 129,
					Math.min(1, 1000 / frameTime / 140F), 90, 70));

			if (frameTime > 16.7F) { // use logarithmic scale above certain
										// threshold
				shapeRenderer.rect(fpsGraphX + 103 + (startIndex + i) * 5,
						fpsGraphY, 4,
						(16.7F + (float) Math.sqrt((frameTime - 17F) * 2.8F))
								* (43 / 8));
			} else {
				shapeRenderer.rect(fpsGraphX + 103 + (startIndex + i) * 5,
						fpsGraphY, 4, Math.max((frameTime * (43 / 8)), 1));
			}

		}

		shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

}
