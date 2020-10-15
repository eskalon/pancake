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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;

/**
 * A post processing effect that is applied via a
 * {@link PostProcessingPipeline}.
 * 
 * @author damios
 */
public abstract class PostProcessingEffect implements Disposable {

	/**
	 * @param source
	 *            the source texture to which the effect should be applied
	 * @param dest
	 *            the framebuffer destination
	 */
	public abstract void apply(Texture source, NestableFrameBuffer dest);

	public abstract void resize(int width, int height);

}
