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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

import de.damios.guacamole.gdx.graphics.NestableFrameBuffer.NestableFrameBufferBuilder;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.graphics.IRenderer;
import de.eskalon.commons.graphics.Scene;

/**
 * @author Sarroxxie
 */
public class DeferredRenderer implements IRenderer, Disposable {

	protected EskalonApplication game;
	private GeometryPass geometryPass;
	private LightPass lightPass;

	/**
	 * Stores the base color in 24 bit.
	 */
	public static final int ALBEDO_ATTACHMENT_INDEX = 0;

	/**
	 * Stores the world normal in 24 bit.
	 */
	public static final int NORMAL_ATTACHMENT_INDEX = 1;

	/**
	 * Stores the metalness, roughness and ambient occlusion in 8bit each.
	 */
	public static final int MATERIAL_ATTACHMENT_INDEX = 2;

	/**
	 * Stores the depth in 32 bit.
	 */
	// TODO: depth as 24 bit?
	public static final int DEPTH_ATTACHMENT_INDEX = 3;

	protected RenderContext context;
	protected FrameBuffer gBuffer;
	protected Camera camera;

	public DeferredRenderer(EskalonApplication game) {
		this.game = game;
		this.context = this.game.getRenderContext();

		NestableFrameBufferBuilder builder = new NestableFrameBufferBuilder(
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB,
				GL30.GL_UNSIGNED_BYTE);
		// TODO: is it worth to use 16 bit float here for the normals?
		builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB,
				GL30.GL_UNSIGNED_BYTE);
		builder.addColorTextureAttachment(GL30.GL_RGB8, GL30.GL_RGB,
				GL30.GL_UNSIGNED_BYTE);
		builder.addDepthTextureAttachment(GL30.GL_DEPTH_COMPONENT24,
				GL30.GL_UNSIGNED_SHORT);
		// TODO: add stencil buffer

		this.gBuffer = builder.build();

		// TODO: this bliting will be required inside the light (shading) pass
		// Gdx.gl30.glReadBuffer(gBuffer.getFramebufferHandle());
		// Gdx.gl30.glBlitFramebuffer(0, 0, game.getWidth(), game.getHeight(),
		// 0,
		// 0, game.getWidth(), game.getHeight(), GL30.GL_DEPTH_BUFFER_BIT,
		// GL30.GL_NEAREST);

		// TODO: set the light pass from the outside maybe?
		this.geometryPass = new GeometryPass(this);
		//this.lightPass = new DebugLightPass(this);
		this.lightPass = new AmbientLightPass(this);
	}

	@Override
	public void render(Scene scene) {
		// TODO: skybox needs to be rendered here
		this.camera = scene.getCamera();
		this.geometryPass.render(scene.getInstances());
		this.lightPass.render();

		Gdx.gl.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, gBuffer.getFramebufferHandle());
		Gdx.gl30.glBlitFramebuffer(0, 0, gBuffer.getWidth(),
				gBuffer.getHeight(), 0, 0, gBuffer.getWidth(),
				gBuffer.getHeight(), GL30.GL_DEPTH_BUFFER_BIT, GL30.GL_NEAREST);

		if (scene.getSkybox() != null)
			scene.getSkybox().render();
	}

	@Override
	public void dispose() {
		this.gBuffer.dispose();
		this.geometryPass.dispose();
		this.lightPass.dispose();
	}

}
