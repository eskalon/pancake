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

package de.eskalon.commons.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.graphics.deferredrendering.DeferredRenderer;

/**
 * This class contains all information needed for rendering a 3D Scene.
 *
 * @author Sarroxxie
 */
public class Scene implements Disposable {

	private Array<ModelInstance> instances;
	private IRenderer renderer;
	private EskalonApplication game;
	private Skybox skybox;

	private Camera camera;

	/**
	 * 
	 * @param width
	 *            viewport width
	 * @param height
	 *            viewport height
	 */
	public Scene(EskalonApplication game, int width, int height) {
		this.game = game;
		this.instances = new Array<ModelInstance>();
		this.renderer = new DeferredRenderer(this.game);
	}

	public void render() {
		// TODO: let the renderer deal with the skybox
//		if (this.skybox != null)
//			this.skybox.render();
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);
		this.renderer.render(this);
		if (this.skybox != null)
			this.skybox.render();
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}
	
	public Camera getCamera() {
		return this.camera;
	}
	
	public Array<ModelInstance> getInstances() {
		return this.instances;
	}

	public void addInstance(ModelInstance instance) {
		this.instances.add(instance);
	}

	@Override
	public void dispose() {
		if (renderer instanceof Disposable)
			((Disposable) renderer).dispose();
	}

}
