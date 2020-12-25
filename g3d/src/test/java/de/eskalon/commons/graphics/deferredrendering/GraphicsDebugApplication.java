package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.graphics.Texture;

import de.eskalon.commons.asset.TextureAssetLoaderParametersFactory;
import de.eskalon.commons.core.EskalonApplication;

public class GraphicsDebugApplication extends EskalonApplication {

	public GraphicsDebugApplication() {
		super(false, false);
	}

	@Override
	protected String initApp() {
		this.screenManager.addScreen("loading",
				new AssetLoadingScreen(this, "de.eskalon"));
		this.screenManager.addScreen("debug", new Debug3DScreen(this));

		assetManager.registerAssetLoaderParametersFactory(Texture.class,
				new TextureAssetLoaderParametersFactory());
		return "loading";
	}

	@Override
	public void render() {
		super.render();
	}

}
