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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * This class contains all information needed for rendering a 3D Scene.
 *
 * @author Sarroxxie
 */
public class Scene implements Disposable {

	private Array<ModelInstance> instances;
	private Skybox skybox;

	private Camera camera;

	/**
	 * 
	 * @param width
	 *            viewport width
	 * @param height
	 *            viewport height
	 */
	public Scene(int width, int height) {
		this.instances = new Array<ModelInstance>();
	}

	public Camera getCamera() {
		return this.camera;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}
	
	public Skybox getSkybox() {
		return this.skybox;
	}

	public void setSkybox(Skybox skybox) {
		this.skybox = skybox;
	}
	
	public Array<ModelInstance> getInstances() {
		return this.instances;
	}
	
	public void addInstance(ModelInstance instance) {
		this.instances.add(instance);
	}
	
	public void addInstances(Array<? extends ModelInstance> instances) {
		this.instances.addAll(instances);
	}

	@Override
	public void dispose() {
		this.skybox.dispose();
	}

}
