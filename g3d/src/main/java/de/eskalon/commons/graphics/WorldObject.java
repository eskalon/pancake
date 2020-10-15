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

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import de.damios.guacamole.Preconditions;

/**
 * Adds a bit of functionality to the
 * {@linkplain com.badlogic.gdx.graphics.g3d.ModelInstance model instance} and
 * stores a corresponding
 * {@linkplain com.badlogic.gdx.math.collision.BoundingBox bounding box}.
 *
 * @author Sarroxxie
 */
public class WorldObject extends ModelInstance {

	// TODO: Add functionality for animations

	// TODO: missing constructors, super from model instance

	/**
	 * Stored {@linkplain com.badlogic.gdx.math.collision.BoundingBox Bounding
	 * Box}. This contains necessary information for collision tests and
	 * <a href=
	 * "https://en.wikipedia.org/wiki/Hidden-surface_determination#Viewing-frustum_culling">
	 * View Frustum Culling</a>.
	 */
	private final BoundingBox bounds;

	/**
	 * Constructs a new WorldObject and calculates its bounding box.
	 * 
	 * @param model
	 *            The {@link com.badlogic.gdx.graphics.g3d.Model Model} to
	 *            create an instance of.
	 */
	public WorldObject(Model model) {
		super(model);
		this.bounds = new BoundingBox();
		this.calculateBoundingBox(this.bounds);
	}

	/**
	 * Calculates the center of the {@linkplain #bounds Bounding Box}.
	 * 
	 * @return Center of the {@linkplain #bounds Bounding Box} in Object-Space.
	 */
	public Vector3 getBoundingCenter() {
		return this.bounds.getCenter(new Vector3()); // TODO cache?
	}

	/**
	 * Calculates the minimal radius from the {@linkplain #bounds Bounding Box}
	 * so a sphere with this radius around the {@linkplain #getBoundingCenter()
	 * center} of the {@linkplain #bounds Bounding Box} fully contains the
	 * geometry.
	 * 
	 * @return Radius of the {@linkplain #bounds Bounding Box} in Object-Space.
	 */
	public float getBoundingRadius() {
		return this.bounds.getDimensions(new Vector3()).len() / 2f;
	}

	/**
	 * Sets the scaling of the World Object by modifying its
	 * {@linkplain com.badlogic.gdx.graphics.g3d.ModelInstance#transform world
	 * transform}.
	 * 
	 * @param scaleX
	 *            The x-coordinate of the new scale vector.
	 * @param scaleY
	 *            The y-coordinate of the new scale vector.
	 * @param scaleZ
	 *            The z-coordinate of the new scale vector.
	 */
	public void setScale(float scaleX, float scaleY, float scaleZ) {
		Preconditions.checkArgument(scaleX != 0 && scaleY != 0 && scaleZ != 0,
				"Neither coordinate of the scale vector is allowed to be 0.");
		Vector3 oldScale = new Vector3();
		this.transform.getScale(oldScale);
		this.transform.scale(scaleX / oldScale.x, scaleY / oldScale.y,
				scaleZ / oldScale.z);
	}

	/**
	 * Sets the scaling of the World Object by modifying its
	 * {@linkplain com.badlogic.gdx.graphics.g3d.ModelInstance#transform world
	 * transform}.
	 * 
	 * @param scale
	 *            The new scale vector.
	 */
	public void setScale(Vector3 scale) {
		this.setScale(scale.x, scale.y, scale.z);
	}
}
