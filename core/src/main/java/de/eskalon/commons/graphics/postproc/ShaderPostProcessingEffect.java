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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;
import de.damios.guacamole.gdx.graphics.QuadMeshGenerator;
import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;
import de.eskalon.commons.utils.graphics.GL32CMacIssueHandler;

/**
 * A post processing effect utilizing a shader.
 * <p>
 * Two uniforms are set:
 * <ul>
 * <li>{@code mat4 u_projTrans}: the combined projection and view matrix of the
 * used camera</li>
 * <li>{@code sampler2D u_texture}: the texture</li>
 * </ul>
 * 
 * @author damios
 */
public class ShaderPostProcessingEffect extends PostProcessingEffect {

	protected ShaderProgram program;
	private Mesh mesh;
	private Camera camera;

	public ShaderPostProcessingEffect(Camera camera, String vertCode,
			String fragCode) {
		this.camera = camera;
		this.program = ShaderProgramFactory.fromString(vertCode, fragCode, true,
				true);
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void apply(Texture source, NestableFrameBuffer dest) {
		dest.begin();
		source.bind();
		program.bind();
		setUniforms();
		mesh.render(program, GL20.GL_TRIANGLE_STRIP);
		dest.end();
	}

	protected void setUniforms() {
		program.setUniformMatrix("u_projTrans", camera.combined);
		program.setUniformi("u_texture", 0);
	}

	@Override
	public void resize(int width, int height) {
		if (this.mesh != null)
			this.mesh.dispose();
		this.mesh = QuadMeshGenerator.createFullScreenQuad(width, height, true);
	}

	@Override
	public void dispose() {
		this.mesh.dispose();
		this.program.dispose();
	}

	// @formatter:off
	protected static String getDefaultVertexShader() {
		if(GL32CMacIssueHandler.doUse32CShader())
			return "#version 150\n" + 
					"in vec4 a_position;\n" + 
					"in vec4 a_color;\n" + 
					"in vec2 a_texCoord0;\n" + 
					"\n" + 
					"uniform mat4 u_projTrans;\n" + 
					"\n" + 
					"out vec4 v_color;\n" + 
					"out vec2 v_texCoords;\n" + 
					"\n" + 
					"void main() {\n" + 
					"    v_color = a_color;\n" + 
					"    v_texCoords = a_texCoord0;\n" + 
					"    gl_Position = u_projTrans * a_position;\n" + 
					"}";
		
		return "#version " + GL32CMacIssueHandler.
				getDefaultShaderVersion() + "\n" + 
				"attribute vec4 a_position;\n" + 
				"attribute vec4 a_color;\n" + 
				"attribute vec2 a_texCoord0;\n" + 
				"\n" + 
				"uniform mat4 u_projTrans;\n" + 
				"\n" + 
				"varying vec4 v_color;\n" + 
				"varying vec2 v_texCoords;\n" + 
				"\n" + 
				"void main() {\n" + 
				"    v_color = a_color;\n" + 
				"    v_texCoords = a_texCoord0;\n" + 
				"    gl_Position = u_projTrans * a_position;\n" + 
				"}";
	}
	// @formatter:on

}
