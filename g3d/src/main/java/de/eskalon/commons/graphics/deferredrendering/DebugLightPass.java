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

package de.eskalon.commons.graphics.deferredrendering;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;
import de.damios.guacamole.gdx.graphics.QuadMeshGenerator;
import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.commons.utils.GL32CMacIssueHandler;

/**
 * Renders the content of the g-buffer for debug purposes.
 * 
 * Layout: \ albedo \ normal \ depth \ ambient occlusion \ shaded result \
 * (empty) \ metallic \ roughness \ (empty)
 * 
 * @author Sarroxxie
 */
public class DebugLightPass extends LightPass {

	private Camera orthoCam;
	private Map<String, Mesh> quads = new ConcurrentHashMap<String, Mesh>();
	private BitmapFont defaultFont;
	private ShapeRenderer shapeRenderer;
	private SpriteBatch sBatch;

	private FrameBuffer finalRenderBuffer;
	private LightPass ambientLightPass;

	private int viewportWidth;
	private int viewportHeight;

	/**
	 * Paths to the needed shader files.
	 */
	private final FileHandle vert = Gdx.files
			.internal("resources/shaders/image.vert");
	private final FileHandle imageFrag = Gdx.files
			.internal("resources/shaders/image.frag");
	private final FileHandle depthFrag = Gdx.files
			.internal("resources/shaders/imageDepth.frag");
	private final FileHandle ambientFrag = Gdx.files
			.internal("resources/shaders/imageBlue.frag");
	private final FileHandle roughnessFrag = Gdx.files
			.internal("resources/shaders/imageGreen.frag");
	private final FileHandle metallicFrag = Gdx.files
			.internal("resources/shaders/imageRed.frag");

	/**
	 * Contains the line positions like such: {left, right, bottom, top}
	 */
	private float[] linePositions = new float[4];

	/**
	 * Contains the text positions like such: {left collumn, mid collumn, right
	 * collumn, bottom row, mid row, top row}
	 */
	private float[] textPositions = new float[6];

	/**
	 * This is the percentage of the screen height that the text will be offset
	 * on the negative Y-axis (downwards).
	 */
	public float yOffsetPct = 1 / 50f;

	/**
	 * This is the thickness of the lines between the boxes.
	 */
	public float lineWidth = 1f;

	private ShaderProgram program, depth, metallic, roughness, ambient;

	public DebugLightPass(DeferredRenderer renderer) {
		super(renderer);
		this.orthoCam = new OrthographicCamera(renderer.game.getWidth(),
				renderer.game.getHeight());
		this.orthoCam.combined.setToOrtho2D(0, 0, renderer.game.getWidth(),
				renderer.game.getHeight());
		this.sBatch = this.renderer.game.getSpriteBatch();
		this.shapeRenderer = new ShapeRenderer(5000, GL32CMacIssueHandler
				.createImmediateModeRenderer20DefaultShader(false, true, 0));

		this.ambientLightPass = new AmbientLightPass(this.renderer);

		this.viewportWidth = this.renderer.game.getWidth();
		this.viewportHeight = this.renderer.game.getHeight();

		this.calculatePositions(viewportWidth, viewportHeight);

		this.finalRenderBuffer = new NestableFrameBuffer(Format.RGBA8888,
				viewportWidth, viewportHeight, false);

		this.addQuads();

		// all necessary shader programs are compiled
		this.program = ShaderProgramFactory.fromFile(vert, imageFrag);
		this.depth = ShaderProgramFactory.fromFile(vert, depthFrag);
		this.ambient = ShaderProgramFactory.fromFile(vert, ambientFrag);
		this.roughness = ShaderProgramFactory.fromFile(vert, roughnessFrag);
		this.metallic = ShaderProgramFactory.fromFile(vert, metallicFrag);
	}

	/**
	 * Calculates the positions for the lines and the texts using the given
	 * viewport width and height.
	 * 
	 * @param viewportWidth
	 *            screen width.
	 * @param viewportHeight
	 *            screen height.
	 */
	private void calculatePositions(int viewportWidth, int viewportHeight) {
		// calculates the positions of the lines
		this.linePositions[0] = viewportWidth / 3f;
		this.linePositions[1] = linePositions[0] * 2f;
		this.linePositions[2] = viewportHeight / 3f;
		this.linePositions[3] = linePositions[2] * 2f;

		float yOffset = viewportHeight * this.yOffsetPct + this.lineWidth / 2;

		// calculates the positions of the debug texts
		this.textPositions[0] = viewportWidth / 6f;
		this.textPositions[1] = textPositions[0] + linePositions[0];
		this.textPositions[2] = textPositions[1] + linePositions[0];

		this.textPositions[3] = linePositions[2] - yOffset;
		this.textPositions[4] = linePositions[3] - yOffset;
		this.textPositions[5] = viewportHeight - yOffset;
	}

	/**
	 * Registers all the used quads to the {@linkplain #quads map}.
	 */
	private void addQuads() {
		this.quads.put("albedo", QuadMeshGenerator.createQuadFromCoordinates(0,
				linePositions[3], linePositions[0], viewportHeight, true));
		this.quads.put("normal",
				QuadMeshGenerator.createQuadFromCoordinates(linePositions[0],
						linePositions[3], linePositions[1], viewportHeight,
						true));
		this.quads.put("depth",
				QuadMeshGenerator.createQuadFromCoordinates(linePositions[1],
						linePositions[3], viewportWidth, viewportHeight, true));
		this.quads.put("final render",
				QuadMeshGenerator.createQuadFromCoordinates(linePositions[0],
						linePositions[2], linePositions[1], linePositions[3],
						true));
		this.quads.put("ambient occlusion",
				QuadMeshGenerator.createQuadFromCoordinates(0, 0,
						linePositions[0], linePositions[2], true));
		this.quads.put("roughness", QuadMeshGenerator.createQuadFromCoordinates(
				linePositions[0], 0, linePositions[1], linePositions[2], true));
		this.quads.put("metallic", QuadMeshGenerator.createQuadFromCoordinates(
				linePositions[1], 0, viewportWidth, linePositions[2], true));
	}

	@Override
	public void render() {
		this.renderFinalRender();
		this.renderer.context.begin();

		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// renders the content of the g-buffer onto quads
		this.renderQuad(program, "albedo", this.renderer.gBuffer,
				DeferredRenderer.ALBEDO_ATTACHMENT_INDEX);
		this.renderQuad(program, "normal", this.renderer.gBuffer,
				DeferredRenderer.NORMAL_ATTACHMENT_INDEX);
		this.renderQuad(depth, "depth", this.renderer.gBuffer,
				DeferredRenderer.DEPTH_ATTACHMENT_INDEX);
		this.renderQuad(program, "final render", finalRenderBuffer, 0);
		this.renderQuad(ambient, "ambient occlusion", this.renderer.gBuffer,
				DeferredRenderer.MATERIAL_ATTACHMENT_INDEX);
		this.renderQuad(roughness, "roughness", this.renderer.gBuffer,
				DeferredRenderer.MATERIAL_ATTACHMENT_INDEX);
		this.renderQuad(metallic, "metallic", this.renderer.gBuffer,
				DeferredRenderer.MATERIAL_ATTACHMENT_INDEX);

		this.renderer.context.end();

		this.renderDebugLines();
		this.renderDebugText();
	}

	/**
	 * Renders a texture onto a given quad at a given attachment index using a
	 * given {@link ShaderProgram}. Note, that this method uses
	 * {@linkplain de.eskalon.commons.graphics.deferredrendering.DeferredRenderer#context
	 * this render context}, so in order to work correctly,
	 * {@linkplain com.badlogic.gdx.graphics.g3d.utils.RenderContext#begin()
	 * begin()} should be called before calling this method and
	 * {@linkplain com.badlogic.gdx.graphics.g3d.utils.RenderContext#end()
	 * end()} should be called after calling this method.
	 * 
	 * @param program
	 *            The {@link ShaderProgram} that renders the quad.
	 * @param quadID
	 *            ID of the quad in the {@linkplain #quads map}.
	 * @param attachmentIndex
	 *            The index of the texture attachment in the
	 *            {@linkplain de.eskalon.commons.graphics.deferredrendering.DeferredRenderer#gBuffer
	 *            g-Buffer}.
	 */
	private void renderQuad(ShaderProgram program, String quadID,
			FrameBuffer buffer, int attachmentIndex) {
		this.renderer.gBuffer.getTextureAttachments().get(attachmentIndex)
				.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		program.bind();
		program.setUniformMatrix("u_projTrans", this.orthoCam.combined);
		program.setUniformi("u_texture", this.renderer.context.textureBinder
				.bind(buffer.getTextureAttachments().get(attachmentIndex)));
		this.quads.get(quadID).render(program, GL20.GL_TRIANGLE_STRIP);
	}

	private void renderFinalRender() {
		this.finalRenderBuffer.begin();
		this.ambientLightPass.render();
		this.finalRenderBuffer.end();
	}

	/**
	 * Renders a debug text for each of the boxes.
	 */
	protected void renderDebugText() {
		if (this.defaultFont == null) {
			this.defaultFont = this.renderer.game.getAssetManager()
					.get(EskalonCommonsAssets.DEFAULT_FONT_NAME);
		}
		this.sBatch.begin();

		this.renderCenteredString("Albedo", this.textPositions[0],
				this.textPositions[5]);
		this.renderCenteredString("Normal", this.textPositions[1],
				this.textPositions[5]);
		this.renderCenteredString("Depth", this.textPositions[2],
				this.textPositions[5]);
		this.renderCenteredString("Final Render (ambient lighting only)",
				this.textPositions[1], this.textPositions[4]);
		this.renderCenteredString("Ambient Occlusion", this.textPositions[0],
				this.textPositions[3]);
		this.renderCenteredString("Roughness", this.textPositions[1],
				this.textPositions[3]);
		this.renderCenteredString("Metallic", this.textPositions[2],
				this.textPositions[3]);

		this.sBatch.end();
	}

	/**
	 * Renders a given text at a given position. The text will be x-centered.
	 * 
	 * @param text
	 * @param x
	 *            x-coordinate where the center of the text will be.
	 * @param y
	 *            y-coordinate where the center of the text will be.
	 */
	private void renderCenteredString(String text, float x, float y) {
		this.defaultFont.draw(this.sBatch, text,
				x - this.getCenteredFontOffset(this.defaultFont, text), y);
	}

	/**
	 * Renders lines with a certain {@linkplain #lineWidth thickness} to
	 * seperate the boxes.
	 */
	protected void renderDebugLines() {
		this.shapeRenderer.begin(ShapeType.Filled);
		this.shapeRenderer.setColor(Color.LIGHT_GRAY);

		// MIDDLE LINES
		// left line
		this.shapeRenderer.rectLine(this.linePositions[0], 0f,
				this.linePositions[0], this.renderer.game.getHeight(),
				this.lineWidth);
		// right line
		this.shapeRenderer.rectLine(this.linePositions[1], 0f,
				this.linePositions[1], this.renderer.game.getHeight(),
				this.lineWidth);
		// bottom line
		this.shapeRenderer.rectLine(0f, this.linePositions[2],
				this.renderer.game.getWidth(), this.linePositions[2],
				this.lineWidth);
		// top line
		this.shapeRenderer.rectLine(0f, this.linePositions[3],
				this.renderer.game.getWidth(), this.linePositions[3],
				this.lineWidth);

		// SCREEN BORDER LINES
		if (this.lineWidth > 1f) {
			// left line
			this.shapeRenderer.rectLine(0f, 0f, 0f, this.viewportHeight,
					this.lineWidth);
			// right line
			this.shapeRenderer.rectLine(this.viewportWidth, 0f,
					this.viewportWidth, this.viewportHeight, this.lineWidth);
			// bottom line
			this.shapeRenderer.rectLine(0f, 0f, this.viewportWidth, 0f,
					this.lineWidth);
			// top line
			this.shapeRenderer.rectLine(0f, this.viewportHeight,
					this.viewportWidth, this.viewportHeight, this.lineWidth);
		}

		this.shapeRenderer.end();
	}

	/**
	 * Calculates the necessary x-offset so the BitmapFont can be rendered
	 * centered.
	 * 
	 * @param bitmapFont
	 *            Used Font.
	 * @param text
	 * @return x-offset
	 */
	private float getCenteredFontOffset(BitmapFont bitmapFont, String text) {
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(bitmapFont, text);
		return glyphLayout.width / 2;
	}

	@Override
	public void dispose() {
		this.program.dispose();
		this.depth.dispose();
		this.metallic.dispose();
		this.roughness.dispose();
		this.ambient.dispose();
	}

}
