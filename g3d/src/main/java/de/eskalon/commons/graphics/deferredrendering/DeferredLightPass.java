package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import de.damios.guacamole.gdx.graphics.ShaderProgramFactory;
import de.eskalon.commons.graphics.Light;
import de.eskalon.commons.graphics.Scene;

public class DeferredLightPass extends LightPass {

	private ShaderProgram program;
	private final FileHandle vert = Gdx.files
			.internal("resources/shaders/pbr.vert");
	private final FileHandle frag = Gdx.files
			.internal("resources/shaders/pbr.frag");

	// TODO: find out which subdivision levels fit best
	private final Mesh lowMesh = IcosphereBuilder.createIcosphere(1f, 1);
	private final Mesh highMesh = IcosphereBuilder.createIcosphere(1f, 2);

	public DeferredLightPass(DeferredRenderer renderer) {
		super(renderer);
		this.program = ShaderProgramFactory.fromFile(vert, frag);
	}

	// TODO: maybe save the uniform location rather than grabbing them every
	// frame
	@Override
	public void render(Scene scene) {
		this.renderer.context.begin();

		// pass uniforms to shader
		this.program.bind();
		this.program.setUniformMatrix("u_projView", scene.getCamera().combined);
		this.program.setUniformMatrix("u_inv_projView",
				scene.getCamera().invProjectionView);
		this.program.setUniformf("u_cam_position", scene.getCamera().position);
		this.program.setUniformf("u_viewport_width",
				scene.getCamera().viewportWidth);
		this.program.setUniformf("u_viewport_height",
				scene.getCamera().viewportHeight);
		this.program.setUniformi("u_albedo",
				this.renderer.context.textureBinder
						.bind(this.renderer.gBuffer.getTextureAttachments().get(
								DeferredRenderer.ALBEDO_ATTACHMENT_INDEX)));
		this.program.setUniformi("u_normal",
				this.renderer.context.textureBinder
						.bind(this.renderer.gBuffer.getTextureAttachments().get(
								DeferredRenderer.NORMAL_ATTACHMENT_INDEX)));
		this.program.setUniformi("u_material",
				this.renderer.context.textureBinder
						.bind(this.renderer.gBuffer.getTextureAttachments().get(
								DeferredRenderer.MATERIAL_ATTACHMENT_INDEX)));
		this.program.setUniformi("u_depth",
				this.renderer.context.textureBinder
						.bind(this.renderer.gBuffer.getTextureAttachments()
								.get(DeferredRenderer.DEPTH_ATTACHMENT_INDEX)));

		// TODO: find out if the faces really get culled
		Gdx.gl.glEnable(GL30.GL_CULL_FACE);
		Gdx.gl.glCullFace(GL30.GL_BACK);
		// TODO: depth test needs to be enabled for the final thing
		// TODO: no lighting should be calculated, where no objects are
		// -> check if there is any value in the depth buffer maybe
		Gdx.gl.glDepthMask(false);
		Gdx.gl.glDisable(GL30.GL_DEPTH_TEST);

		// TODO: each light should add its color to the previous calculated
		// color -> need to access old color value in fragment shader
		// TODO: move ambient light into a seperate pass? or wait for IBL :)
		for (Light light : scene.getLights()) {
			this.program.bind();
			// TODO: add heuristic to select best mesh
			// -> distance to cam + radius
			Mesh lightMesh = light.getRadius() < 1 ? this.lowMesh
					: this.highMesh;

			this.program.setUniformf("u_color", light.color);
			this.program.setUniformf("u_position", light.position);
			this.program.setUniformf("u_radius", light.radius);

			lightMesh.render(this.program, IcosphereBuilder.PRIMITIVE_TYPE);
		}

		// TODO: reset to standards
		Gdx.gl.glDisable(GL30.GL_CULL_FACE);
		Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
		Gdx.gl.glDepthMask(true);

		this.renderer.context.end();
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
