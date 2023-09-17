/*
 * Copyright 2021 eskalon
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

package de.eskalon.commons.core;

/**
 * This class holds the configuration for an {@link AbstractEskalonApplication}. Unlike
 * {@link StartArguments}, properties of {@link EskalonApplicationConfiguration}
 * are not passed to the application from the outside, but rather represent
 * (permanent) decisions of the application's developer.
 * 
 * @author damios
 */
public class EskalonApplicationConfiguration {

	public static EskalonApplicationConfiguration create() {
		return new EskalonApplicationConfiguration();
	}

	private boolean createPostProcessor;
	private boolean provideDepthBuffers;

	private EskalonApplicationConfiguration() {
		// reduce visibility
	}

	public EskalonApplicationConfiguration createPostProcessor() {
		this.createPostProcessor = true;
		return this;
	}

	public EskalonApplicationConfiguration provideDepthBuffers() {
		this.provideDepthBuffers = true;
		return this;
	}

	public boolean shouldCreatePostProcessor() {
		return createPostProcessor;
	}

	public boolean shouldProvideDepthBuffers() {
		return provideDepthBuffers;
	}

	@Override
	public String toString() {
		return "EskalonApplicationConfiguration{createPostProcessor="
				+ createPostProcessor + ",provideDepthBuffers="
				+ provideDepthBuffers + "}";
	}

}
