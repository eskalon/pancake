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

package de.eskalon.commons.utils.graphics;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import de.damios.guacamole.gdx.graphics.NestableFrameBuffer;

/**
 * This handler takes care of two framebuffers that are used alternatingly. This
 * is needed for postprocessing.
 * <p>
 * To use it call {@link #getCurrentBuffer()}. Whenever
 * {@link PingPongBuffer#end() end()} is called on this buffer, this handler
 * switches to the other framebuffer.
 * 
 * @author damios
 */
public class PingPongBufferHandler implements Disposable {

	private NestableFrameBuffer buffer1;
	private NestableFrameBuffer buffer2;

	private NestableFrameBuffer currentBuffer;
	private NestableFrameBuffer oldBuffer;

	public PingPongBufferHandler(Format format, int width, int height,
			boolean hasDepth) {
		buffer1 = new PingPongBuffer(format, width, height, hasDepth);
		buffer2 = new PingPongBuffer(format, width, height, hasDepth);

		currentBuffer = buffer1;
		oldBuffer = buffer2;
	}

	public NestableFrameBuffer getCurrentBuffer() {
		return currentBuffer;
	}

	public Texture getLastTexture() {
		return oldBuffer.getColorBufferTexture();
	}

	void swapBuffers() {
		oldBuffer = currentBuffer;
		currentBuffer = (currentBuffer == buffer1) ? buffer2 : buffer1;
	}

	@Override
	public void dispose() {
		buffer1.dispose();
		buffer2.dispose();
	}

	public class PingPongBuffer extends NestableFrameBuffer {

		public PingPongBuffer(Format format, int width, int height,
				boolean hasDepth) {
			super(format, width, height, hasDepth);
		}

		@Override
		public void end(int x, int y, int width, int height) {
			super.end(x, y, width, height);
			swapBuffers();
		}

	}

}
