package de.eskalon.commons.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;

public class Skybox implements Disposable{
	private Mesh mesh;
	private ShaderProgram program;
	private Cubemap cubemap;
	public static final int PRIMITIVE_TYPE = GL30.GL_TRIANGLE_STRIP;

	public Skybox(Cubemap cubemap) {
		this.cubemap = cubemap;
		// TODO: find out how to use mipmaps here
		this.cubemap.setFilter(Texture.TextureFilter.Linear, 
				Texture.TextureFilter.Linear);
		this.cubemap.setWrap(Texture.TextureWrap.ClampToEdge, 
				Texture.TextureWrap.ClampToEdge);
		this.mesh = this.createCube();

		this.program = ShaderProgramFactory.fromFile(
				Gdx.files.internal("resources/shaders/skybox.vert"),
				Gdx.files.internal("resources/shaders/skybox.frag"));
	}
	
	@Override
	public void dispose() {
		this.cubemap.dispose();
		this.mesh.dispose();	
	}
	
	public Cubemap getCubemap() {
		return this.cubemap;
	}
	
	public void setCubemap(Cubemap cubemap) {
		this.cubemap = cubemap;
	}
	
	public Mesh getMesh() {
		return this.mesh;
	}
	
	public ShaderProgram getShaderProgram() {
		return this.program;
	}
	
	public Mesh createCube() {
		Mesh mesh = new Mesh(true, 8, 14, VertexAttribute.Position());
		mesh.setVertices(new float[] {
				-1f,-1f, 1f, 
				 1f,-1f, 1f, 
				 1f,-1f,-1f,
				-1f,-1f,-1f, 
				-1f, 1f, 1f,
				 1f, 1f, 1f,
				 1f, 1f,-1f,
				-1f, 1f,-1f});

		// these indices are the indices for a triangle strip
		mesh.setIndices(
				new short[] {3, 7, 2, 6, 5, 7, 4, 3, 0, 2, 1, 5, 0, 4});
		return mesh;
	}
}
