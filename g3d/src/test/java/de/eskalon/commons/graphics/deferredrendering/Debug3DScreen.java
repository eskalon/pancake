package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.graphics.PBRTextureAttribute;
import de.eskalon.commons.graphics.Scene;
import de.eskalon.commons.graphics.WorldObject;
import de.eskalon.commons.screens.AbstractEskalonScreen;

public class Debug3DScreen extends AbstractEskalonScreen {
	
	private EskalonApplication application;

	@Asset("models/debug_model_tangent.g3dj")
	private Model debug;

	// @Asset(value = "textures/granite/granite albedo.png", params =
	// "-genmipmap")
	// private Texture granite_albedo;
	// @Asset(value = "textures/granite/granite normal.png", params =
	// "-genmipmap")
	// private Texture granite_normal;
	// @Asset(value = "textures/granite/granite metallic.png", params =
	// "-genmipmap")
	// private Texture granite_metallic;
	// @Asset(value = "textures/granite/granite roughness.png", params =
	// "-genmipmap")
	// private Texture granite_roughness;
	// @Asset(value = "textures/granite/granite ao.png", params = "-genmipmap")
	// private Texture granite_ao;

	@Asset(value = "textures/red_bricks/red_bricks albedo.png", params = "-genmipmap")
	private Texture red_bricks_albedo;
	@Asset(value = "textures/red_bricks/red_bricks normal.png", params = "-genmipmap")
	private Texture red_bricks_normal;
	@Asset(value = "textures/red_bricks/red_bricks metallic.png", params = "-genmipmap")
	private Texture red_bricks_metallic;
	@Asset(value = "textures/red_bricks/red_bricks roughness.png", params = "-genmipmap")
	private Texture red_bricks_roughness;
	@Asset(value = "textures/red_bricks/red_bricks ao.png", params = "-genmipmap")
	private Texture red_bricks_ao;
	@Asset(value = "textures/red_bricks/red_bricks height.png", params = "-genmipmap")
	private Texture red_bricks_height;

	private Scene scene;

	private Camera camera;
	private CameraInputController cameraController;

	public Debug3DScreen(EskalonApplication application) {
		this.application = application;
	}

	@Override
	public void render(float delta) {
		this.cameraController.update();
		this.scene.render();
	}

	@Override
	protected void create() {
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		camera.near = 0.2f;
		camera.far = 100f;
		camera.position.set(3, 2, 3);
		camera.lookAt(0, 0, 0);
		camera.up.set(0, 1, 0);
		camera.update();
		cameraController = new CameraInputController(camera);
		this.addInputProcessor(cameraController);

		this.scene = new Scene(this.application, this.application.getWidth(),
				this.application.getHeight());
		this.scene.setCamera(camera);

		// Material granite = this.debug.materials.get(0);
		// this.granite_albedo.setFilter(TextureFilter.MipMapLinearLinear,
		// TextureFilter.MipMapLinearLinear);
		// this.granite_albedo.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		// this.granite_normal.setFilter(TextureFilter.MipMapLinearLinear,
		// TextureFilter.MipMapLinearLinear);
		// this.granite_normal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		// this.granite_metallic.setFilter(TextureFilter.MipMapLinearLinear,
		// TextureFilter.MipMapLinearLinear);
		// this.granite_metallic.setWrap(TextureWrap.Repeat,
		// TextureWrap.Repeat);
		// this.granite_roughness.setFilter(TextureFilter.MipMapLinearLinear,
		// TextureFilter.MipMapLinearLinear);
		// this.granite_roughness.setWrap(TextureWrap.Repeat,
		// TextureWrap.Repeat);
		// this.granite_ao.setFilter(TextureFilter.MipMapLinearLinear,
		// TextureFilter.MipMapLinearLinear);
		// this.granite_ao.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		// granite.set(PBRTextureAttribute.createAlbedo(granite_albedo));
		// granite.set(PBRTextureAttribute.createNormal(granite_normal));
		// granite.set(PBRTextureAttribute.createMetallic(granite_metallic));
		// granite.set(PBRTextureAttribute.createRoughness(granite_roughness));
		// granite.set(PBRTextureAttribute.createAmbient(granite_ao));

		Material red_bricks = this.debug.materials.get(0);
		this.red_bricks_albedo.setFilter(TextureFilter.MipMapLinearLinear,
				TextureFilter.MipMapLinearLinear);
		this.red_bricks_albedo.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.red_bricks_normal.setFilter(TextureFilter.MipMapLinearLinear,
				TextureFilter.MipMapLinearLinear);
		this.red_bricks_normal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.red_bricks_metallic.setFilter(TextureFilter.MipMapLinearLinear,
				TextureFilter.MipMapLinearLinear);
		this.red_bricks_metallic.setWrap(TextureWrap.Repeat,
				TextureWrap.Repeat);
		this.red_bricks_roughness.setFilter(TextureFilter.MipMapLinearLinear,
				TextureFilter.MipMapLinearLinear);
		this.red_bricks_roughness.setWrap(TextureWrap.Repeat,
				TextureWrap.Repeat);
		this.red_bricks_ao.setFilter(TextureFilter.MipMapLinearLinear,
				TextureFilter.MipMapLinearLinear);
		this.red_bricks_ao.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		red_bricks.set(PBRTextureAttribute.createAlbedo(red_bricks_albedo));
		red_bricks.set(PBRTextureAttribute.createNormal(red_bricks_normal));
		red_bricks.set(PBRTextureAttribute.createMetallic(red_bricks_metallic));
		red_bricks
				.set(PBRTextureAttribute.createRoughness(red_bricks_roughness));
		red_bricks.set(PBRTextureAttribute.createAmbient(red_bricks_ao));

		WorldObject debugObject = new WorldObject(this.debug);
		this.scene.addInstance(debugObject);
	}

	@Override
	public void dispose() {
		scene.dispose();
	}

	@Override
	protected EskalonApplication getApplication() {
		return this.application;
	}

}
