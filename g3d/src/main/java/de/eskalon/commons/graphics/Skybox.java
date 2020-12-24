package de.eskalon.commons.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Disposable;

import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;

public class Skybox implements Disposable{
	private Camera camera;
	private Mesh cube;
	private ShaderProgram program;
	private RenderContext context;
	private Cubemap cubemap;

	public Skybox(Camera camera, RenderContext context, Cubemap cubemap) {
		this.camera = camera;
		this.context = context;
		this.cubemap = cubemap;
		// TODO: use mipmaps here
		this.cubemap.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		this.cubemap.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
		this.cube = this.createCube();

		this.program = ShaderProgramFactory.fromFile(
				Gdx.files.internal("resources/shaders/cube.vert"),
				Gdx.files.internal("resources/shaders/cube.frag"));
	}

	public void render() {
		// TODO: find best location in the code to disable the depth test
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(false);
		this.context.begin();
		
		Matrix4 view = this.camera.view.cpy();
		view.val[Matrix4.M03] = 0;
		view.val[Matrix4.M13] = 0;
		view.val[Matrix4.M23] = 0;

		this.program.bind();
		// TODO: check this against the cube.vert shader!
		this.program.setUniformMatrix("u_view", view);
		this.program.setUniformMatrix("u_proj", this.camera.projection);
			this.program.setUniformi("u_cube",
					this.context.textureBinder
							.bind(this.cubemap));
		this.cube.render(this.program, GL20.GL_TRIANGLE_STRIP);

		this.context.end();
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

		// these indices are the indices for a triangle strip ->
		// https://stackoverflow.com/questions/28375338/cube-using-single-gl-triangle-strip
		mesh.setIndices(
				new short[] { 3, 7, 2, 6, 5, 7, 4, 3, 0, 2, 1, 5, 0, 4 });
		return mesh;
	}
	
	public void setCubemap(Cubemap cubemap) {
		this.cubemap = cubemap;
	}

	@Override
	public void dispose() {
		this.cubemap.dispose();
		this.cube.dispose();	
	}
}
