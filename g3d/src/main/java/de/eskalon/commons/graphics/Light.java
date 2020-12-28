package de.eskalon.commons.graphics;

import com.badlogic.gdx.math.Vector3;

public class Light {

	public Vector3 position;
	public Vector3 color;
	public float radius;

	public Light(Vector3 position, Vector3 color, float radius) {
		this.position = position.cpy();
		this.color = color.cpy();
		this.radius = radius;
	}

	public Vector3 getPosition() {
		return this.position.cpy();
	}

	public void setPosition(Vector3 position) {
		this.position = position.cpy();
	}

	public void setPosition(float x, float y, float z) {
		this.position = new Vector3(x, y, z);
	}

	public Vector3 getColor() {
		return this.color.cpy();
	}

	public void setColor(Vector3 color) {
		this.color = color.cpy();
	}

	public void setColor(float r, float g, float b) {
		this.color = new Vector3(r, g, b);
	}

	public float getRadius() {
		return this.radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
}
