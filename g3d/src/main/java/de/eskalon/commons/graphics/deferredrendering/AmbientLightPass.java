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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

import de.damios.guacamole.gdx.graphics.QuadMeshGenerator;
import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;

/**
 * @author Sarroxxie
 */
public class AmbientLightPass extends LightPass {

	private ShaderProgram program;
	private Camera orthoCam;
	public Vector3 ambientColor = new Vector3(0.6f, 0.6f, 0.6f);

	private Mesh screenQuad;

	// TODO: Write missing shader!
	private final FileHandle vert = Gdx.files
			.internal("resources/shaders/image.vert");
	private final FileHandle frag = Gdx.files
			.internal("resources/shaders/ambient.frag");

	public AmbientLightPass(DeferredRenderer renderer) {
		super(renderer);
		this.orthoCam = new OrthographicCamera(renderer.game.getWidth(),
				renderer.game.getHeight());
		this.orthoCam.combined.setToOrtho2D(0, 0, renderer.game.getWidth(),
				renderer.game.getHeight());
		this.program = ShaderProgramFactory.fromFile(vert, frag);
		this.screenQuad = QuadMeshGenerator.createFullScreenQuad(
				renderer.game.getWidth(), renderer.game.getHeight(), true);
	}

	@Override
	public void render() {
		this.renderer.context.begin();

		this.renderer.gBuffer.getTextureAttachments()
				.get(DeferredRenderer.MATERIAL_ATTACHMENT_INDEX)
				.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		this.renderer.gBuffer.getTextureAttachments()
				.get(DeferredRenderer.ALBEDO_ATTACHMENT_INDEX)
				.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

		this.program.bind();
		this.program.setUniformMatrix("u_projTrans", this.orthoCam.combined);
		this.program.setUniformf("u_ambient_color", ambientColor.x,
				ambientColor.y, ambientColor.z);
		this.program.setUniformi("u_ambient_occlusion",
				this.renderer.context.textureBinder
						.bind(this.renderer.gBuffer.getTextureAttachments().get(
								DeferredRenderer.MATERIAL_ATTACHMENT_INDEX)));
		this.program.setUniformi("u_albedo",
				this.renderer.context.textureBinder
						.bind(this.renderer.gBuffer.getTextureAttachments().get(
								DeferredRenderer.ALBEDO_ATTACHMENT_INDEX)));
		this.screenQuad.render(this.program, GL20.GL_TRIANGLE_STRIP);

		this.renderer.context.end();
	}

	@Override
	public void dispose() {
		this.program.dispose();
	}

	public void resize() {
		this.screenQuad = QuadMeshGenerator.createFullScreenQuad(
				renderer.game.getWidth(), renderer.game.getHeight(), true);
	}

}
