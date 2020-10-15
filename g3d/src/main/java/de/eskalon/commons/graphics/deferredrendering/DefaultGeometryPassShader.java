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

import java.lang.reflect.Field;

import org.lwjgl.opengl.GL32C;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;

import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;
import de.eskalon.commons.graphics.PBRTextureAttribute;

/**
 * @author Sarroxxie
 */
public class DefaultGeometryPassShader implements Shader {

	private ShaderProgram program;
	private RenderContext context;

	static final long Mask = PBRTextureAttribute.Albedo
			| PBRTextureAttribute.Normal | PBRTextureAttribute.Metallic
			| PBRTextureAttribute.Roughness | PBRTextureAttribute.Ambient;

	private Matrix3 matrix3 = new Matrix3();

	@Override
	public void dispose() {
		this.program.dispose();
	}

	@Override
	public void init() {
		this.program = ShaderProgramFactory.fromFile(
				Gdx.files.internal("resources/shaders/defaultGeometryPassShader.vert"),
				Gdx.files.internal("resources/shaders/defaultGeometryPassShader.frag"));

		int handle = 0;
		// TODO use program.getHandle();
		try {
			Field field = ShaderProgram.class.getDeclaredField("program");
			field.setAccessible(true);
			handle = (int) field.getInt(program);
		} catch (NoSuchFieldException | SecurityException
				| IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		System.out.println(handle);
		GL32C.glBindFragDataLocation(handle, 0, "albedoOut");
		GL32C.glBindFragDataLocation(handle, 1, "normalOut");
		GL32C.glBindFragDataLocation(handle, 2, "materialOut");
		Gdx.gl20.glLinkProgram(handle);
	}

	@Override
	public int compareTo(Shader other) {
		// TODO: find a suitable comparison
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		if (instance.meshPart.mesh.getVertexAttribute(
				VertexAttributes.Usage.TextureCoordinates) == null) {
			return false;
		}
		if (instance.meshPart.mesh
				.getVertexAttribute(VertexAttributes.Usage.Normal) == null) {
			return false;
		}
		return instance.material.has(Mask);
	}

	@Override
	public void begin(Camera camera, RenderContext context) {
		this.context = context;
		this.program.bind();
		this.program.setUniformMatrix("u_projViewTrans", camera.combined);
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);
	}

	@Override
	public void render(Renderable renderable) {
		Material material = renderable.material;

		TextureAttribute albedoTexture = (TextureAttribute) material
				.get(PBRTextureAttribute.Albedo);
		TextureAttribute normalTexture = (TextureAttribute) material
				.get(PBRTextureAttribute.Normal);
		TextureAttribute metallicTexture = (TextureAttribute) material
				.get(PBRTextureAttribute.Metallic);
		TextureAttribute ambientTexture = (TextureAttribute) material
				.get(PBRTextureAttribute.Ambient);
		TextureAttribute roughnessTexture = (TextureAttribute) material
				.get(PBRTextureAttribute.Roughness);

		if (albedoTexture != null) {
			this.program.setUniformi("u_albedoTexture", context.textureBinder
					.bind(albedoTexture.textureDescription.texture));
		}
		if (normalTexture != null) {
			this.program.setUniformi("u_normalTexture", context.textureBinder
					.bind(normalTexture.textureDescription.texture));
		}
		if (metallicTexture != null) {
			this.program.setUniformi("u_metallicTexture", context.textureBinder
					.bind(metallicTexture.textureDescription.texture));
		}
		if (roughnessTexture != null) {
			this.program.setUniformi("u_roughnessTexture", context.textureBinder
					.bind(roughnessTexture.textureDescription.texture));
		}
		if (ambientTexture != null) {
			this.program.setUniformi("u_ambientTexture", context.textureBinder
					.bind(ambientTexture.textureDescription.texture));
		}

		this.program.setUniformMatrix("u_worldTrans",
				renderable.worldTransform);

		// TODO: keep an eye on this thing here, so no transformations are wrong
		// in the end
		this.program.setUniformMatrix("u_normalWorldTrans",
				matrix3.set(renderable.worldTransform).inv().transpose());

		renderable.meshPart.render(this.program);
	}

	@Override
	public void end() {
	}

}
