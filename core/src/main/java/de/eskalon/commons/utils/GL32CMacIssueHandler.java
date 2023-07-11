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

package de.eskalon.commons.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;

/**
 * Provides updated versions of the default shaders that are compatible with
 * GLSL 1.50 (OpenGL 3.2).
 * <p>
 * This is needed when OpenGL 3+ features (e.g. multiple render targets) are
 * used on mac, as mac only supports core profiles and those are not backward
 * compatible.
 * 
 * @author damios
 * @see <a href=
 *      "https://www.khronos.org/opengl/wiki/OpenGL_Context#OpenGL_3.2_and_Profiles">Additional
 *      information on core profiles on mac os</a>
 */
public final class GL32CMacIssueHandler {

	// TODO https://github.com/libgdx/libgdx/pull/5960

	private GL32CMacIssueHandler() {
		throw new UnsupportedOperationException();
	}

	public static ShaderProgram createSpriteBatchShader() {
		// @formatter:off
		String vertexShader = "in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
				+ "uniform mat4 u_projTrans;\n" //
				+ "out vec4 v_color;\n" //
				+ "out vec2 v_texCoords;\n" //
				+ "\n" //
				+ "void main()\n" //
				+ "{\n" //
				+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
				+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
				+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE
				+ "0;\n" //
				+ "   gl_Position =  u_projTrans * "
				+ ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
				+ "}\n";
		String fragmentShader = "#ifdef GL_ES\n" //
				+ "#define LOWP lowp\n" //
				+ "precision mediump float;\n" //
				+ "#else\n" //
				+ "#define LOWP \n" //
				+ "#endif\n" //
				+ "in LOWP vec4 v_color;\n" //
				+ "in vec2 v_texCoords;\n" //
				+ "uniform sampler2D u_texture;\n" //
				+ "out vec4 fragColor;\n" + "void main()\n"//
				+ "{\n" //
				+ "  fragColor = v_color * texture(u_texture, v_texCoords);\n" //
				+ "}";
		// @formatter:on
		return ShaderProgramFactory.fromString(vertexShader, fragmentShader);
	}

	private static String createImmediateModeRenderer20VertexShader(
			boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "in vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasNormals
						? "in vec3 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n"
						: "")
				+ (hasColors
						? "in vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
						: "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "in vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}

		shader += "uniform mat4 u_projModelView;\n";
		shader += (hasColors ? "out vec4 v_col;\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "out vec2 v_tex" + i + ";\n";
		}

		shader += "void main() {\n" + "   gl_Position = u_projModelView * "
				+ ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasColors
						? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
						: "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE
					+ i + ";\n";
		}
		shader += "   gl_PointSize = 1.0;\n";
		shader += "}\n";
		return shader;
	}

	private static String createImmediateModeRenderer20FragmentShader(
			boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "#ifdef GL_ES\n" + "precision mediump float;\n"
				+ "#endif\n";

		if (hasColors)
			shader += "in vec4 v_col;\n";
		for (int i = 0; i < numTexCoords; i++) {
			shader += "in vec2 v_tex" + i + ";\n";
			shader += "uniform sampler2D u_sampler" + i + ";\n";
		}

		shader += "out vec4 fragColor;\n";

		shader += "void main() {\n" + "   fragColor = "
				+ (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");

		if (numTexCoords > 0)
			shader += " * ";

		for (int i = 0; i < numTexCoords; i++) {
			if (i == numTexCoords - 1) {
				shader += " texture(u_sampler" + i + ",  v_tex" + i + ")";
			} else {
				shader += " texture(u_sampler" + i + ",  v_tex" + i + ") *";
			}
		}

		shader += ";\n}";
		return shader;
	}

	/**
	 * Returns a new instance of the default shader used by SpriteBatch for GL2
	 * when no shader is specified.
	 */
	public static ShaderProgram createImmediateModeRenderer20DefaultShader(
			boolean hasNormals, boolean hasColors, int numTexCoords) {
		String vertexShader = createImmediateModeRenderer20VertexShader(
				hasNormals, hasColors, numTexCoords);
		String fragmentShader = createImmediateModeRenderer20FragmentShader(
				hasNormals, hasColors, numTexCoords);
		ShaderProgram program = new ShaderProgram(vertexShader, fragmentShader);
		return program;
	}

}
