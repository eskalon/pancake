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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor;

/**
 * Adds additional identifier for Metallic, AmbientOcclusion and Roughness
 * Textures to use in
 * <a href="https://en.wikipedia.org/wiki/Physically_based_rendering">
 * Physically Based Rendering</a>.
 * 
 * @author Sarroxxie
 */
public class PBRTextureAttribute extends TextureAttribute {

	public final static String AlbedoAlias = "albedoTexture";
	public final static long Albedo = register(AlbedoAlias);
	public final static String MetallicAlias = "metallicTexture";
	public final static long Metallic = register(MetallicAlias);
	public final static String RoughnessAlias = "roughnessTexture";
	public final static long Roughness = register(RoughnessAlias);

	static {
		Mask |= Albedo | Metallic | Roughness;
	}

	public static PBRTextureAttribute createAlbedo(final Texture texture) {
		return new PBRTextureAttribute(Albedo, texture);
	}

	public static PBRTextureAttribute createAlbedo(final TextureRegion region) {
		return new PBRTextureAttribute(Albedo, region);
	}

	public static PBRTextureAttribute createMetallic(final Texture texture) {
		return new PBRTextureAttribute(Metallic, texture);
	}

	public static PBRTextureAttribute createMetallic(
			final TextureRegion region) {
		return new PBRTextureAttribute(Metallic, region);
	}

	public static PBRTextureAttribute createRoughness(final Texture texture) {
		return new PBRTextureAttribute(Roughness, texture);
	}

	public static PBRTextureAttribute createRoughness(
			final TextureRegion region) {
		return new PBRTextureAttribute(Roughness, region);
	}

	public PBRTextureAttribute(final long type) {
		super(type);
	}

	public <T extends Texture> PBRTextureAttribute(final long type,
			final TextureDescriptor<T> textureDescription) {
		super(type, textureDescription);
	}

	public <T extends Texture> PBRTextureAttribute(final long type,
			final TextureDescriptor<T> textureDescription, float offsetU,
			float offsetV, float scaleU, float scaleV, int uvIndex) {
		super(type, textureDescription, offsetU, offsetV, scaleU, scaleV,
				uvIndex);
	}

	public <T extends Texture> PBRTextureAttribute(final long type,
			final TextureDescriptor<T> textureDescription, float offsetU,
			float offsetV, float scaleU, float scaleV) {
		super(type, textureDescription, offsetU, offsetV, scaleU, scaleV);
	}

	public PBRTextureAttribute(final long type, final Texture texture) {
		super(type, texture);
	}

	public PBRTextureAttribute(final long type, final TextureRegion region) {
		super(type, region);
	}

	public PBRTextureAttribute(final TextureAttribute copyFrom) {
		super(copyFrom);
	}

}
