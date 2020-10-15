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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.DefaultRenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * @author Sarroxxie
 */
public class GeometryPass implements Disposable {

	private DeferredRenderer renderer;

	private ModelCache modelCache;
	private Array<Renderable> renderables = new Array<>();

	private ShaderProvider shaderProvider;
	private RenderableSorter renderableSorter;

	public GeometryPass(DeferredRenderer renderer) {
		this.renderer = renderer;
		this.modelCache = new ModelCache();

		this.shaderProvider = new BaseShaderProvider() {
			@Override
			protected Shader createShader(Renderable renderable) {
				DefaultGeometryPassShader shader = new DefaultGeometryPassShader();
				shader.init();
				return shader;
			}
		};
		this.renderableSorter = new DefaultRenderableSorter() {
			@Override
			public int compare(Renderable o1, Renderable o2) {
				return o1.material.compareTo(o2.material);
			}
		};
	}

	public void render(Array<ModelInstance> objects) {
		/*
		 * Combine models; set shaders; sort renderables.
		 */
		this.renderables.clear();

		this.modelCache.begin(this.renderer.camera);
		this.modelCache.add(objects);
		this.modelCache.end();
		this.modelCache.getRenderables(this.renderables, null);

		for (Renderable renderable : this.renderables) {
			renderable.shader = shaderProvider.getShader(renderable);
		}

		this.renderableSorter.sort(this.renderer.camera, this.renderables);

		/*
		 * Fills all the specified buffers for the #gBuffer.
		 */
		this.renderer.context.begin();
		this.renderer.gBuffer.begin();

		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Shader currentShader = null;
		for (int i = 0; i < this.renderables.size; i++) {
			final Renderable renderable = this.renderables.get(i);
			if (currentShader != renderable.shader) {
				if (currentShader != null)
					currentShader.end();
				currentShader = renderable.shader;
				currentShader.begin(this.renderer.camera,
						this.renderer.context);
			}
			currentShader.render(renderable);
		}
		if (currentShader != null)
			currentShader.end();

		this.renderer.gBuffer.end();
		this.renderer.context.end();
	}

	@Override
	public void dispose() {
		modelCache.dispose();
		shaderProvider.dispose();
	}

}
