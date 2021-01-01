package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.graphics.Light;
import de.eskalon.commons.graphics.PBRTextureAttribute;
import de.eskalon.commons.graphics.Scene;
import de.eskalon.commons.graphics.Skybox;
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

	@Asset(value = "textures/skybox/right.jpg", params = "-genmipmap")
	private Texture cubeRight;
	@Asset(value = "textures/skybox/left.jpg", params = "-genmipmap")
	private Texture cubeLeft;
	@Asset(value = "textures/skybox/top.jpg", params = "-genmipmap")
	private Texture cubeTop;
	@Asset(value = "textures/skybox/bottom.jpg", params = "-genmipmap")
	private Texture cubeBottom;
	@Asset(value = "textures/skybox/back.jpg", params = "-genmipmap")
	private Texture cubeBack;
	@Asset(value = "textures/skybox/front.jpg", params = "-genmipmap")
	private Texture cubeFront;

	private Scene scene;
	private DeferredRenderer renderer;

	private Camera camera;
	private CameraInputController cameraController;

	public Debug3DScreen(EskalonApplication application) {
		this.application = application;
	}

	@Override
	public void render(float delta) {
		this.cameraController.update();
		this.renderer.render(this.scene);
	}

	@Override
	protected void create() {
		this.camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		this.camera.near = 0.2f;
		this.camera.far = 100f;
		this.camera.position.set(3, 2, 3);
		this.camera.lookAt(0, 0, 0);
		this.camera.up.set(0, 1, 0);
		this.camera.update();
		this.cameraController = new CameraInputController(this.camera);
		this.addInputProcessor(this.cameraController);

		this.scene = new Scene(this.application.getWidth(),
				this.application.getHeight());
		this.scene.setCamera(camera);
		this.scene.addLight(
				new Light(new Vector3(0, 1, 1), new Vector3(1f,1f,1f), 6f));
		Cubemap cubemap = new Cubemap(cubeRight.getTextureData(),
				cubeLeft.getTextureData(), cubeTop.getTextureData(),
				cubeBottom.getTextureData(), cubeFront.getTextureData(),
				cubeBack.getTextureData());
		this.scene.setSkybox(new Skybox(cubemap));

		this.renderer = new DeferredRenderer(this.application);

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
		this.red_bricks_albedo.setFilter(TextureFilter.MipMap,
				TextureFilter.Linear);
		this.red_bricks_albedo.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.red_bricks_normal.setFilter(TextureFilter.MipMap,
				TextureFilter.Linear);
		this.red_bricks_normal.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		this.red_bricks_metallic.setFilter(TextureFilter.MipMap,
				TextureFilter.Linear);
		this.red_bricks_metallic.setWrap(TextureWrap.Repeat,
				TextureWrap.Repeat);
		this.red_bricks_roughness.setFilter(TextureFilter.MipMap,
				TextureFilter.Linear);
		this.red_bricks_roughness.setWrap(TextureWrap.Repeat,
				TextureWrap.Repeat);
		this.red_bricks_ao.setFilter(TextureFilter.MipMap,
				TextureFilter.Linear);
		this.red_bricks_ao.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		red_bricks
				.set(PBRTextureAttribute.createAlbedo(this.red_bricks_albedo));
		red_bricks
				.set(PBRTextureAttribute.createNormal(this.red_bricks_normal));
		red_bricks.set(
				PBRTextureAttribute.createMetallic(this.red_bricks_metallic));
		red_bricks.set(
				PBRTextureAttribute.createRoughness(this.red_bricks_roughness));
		red_bricks.set(PBRTextureAttribute.createAmbient(this.red_bricks_ao));

		WorldObject debugObject = new WorldObject(this.debug);
		this.scene.addInstance(debugObject);
	}

	@Override
	public void dispose() {
		this.scene.dispose();
	}

	@Override
	protected EskalonApplication getApplication() {
		return this.application;
	}

}
