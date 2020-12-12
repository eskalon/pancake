package de.eskalon.commons.graphics.deferredrendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

import de.eskalon.commons.core.EskalonApplication;
import de.eskalon.commons.screens.AbstractAssetLoadingScreen;
import de.eskalon.commons.screens.AbstractEskalonScreen;

public class AssetLoadingScreen extends AbstractAssetLoadingScreen {

	public AssetLoadingScreen(EskalonApplication application,
			String packageRoot) {
		super(application, packageRoot);
	}

	@Override
	protected void loadOwnAssets() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onFinishedLoading() {
		System.out.println("finished");
		this.application.getScreenManager().pushScreen("debug", "splashOutTransition3");
		
		for (AbstractEskalonScreen s : application.getScreenManager().getScreens()) {
            if (s != this) {// exclude loading screen
                application.getAssetManager().injectAssets(s);
                s.initializeScreen();
            }
        }

	}

	@Override
	public void render(float delta, float progress) {
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
	}
	
	@Override
	public Color getClearColor() {
		return Color.WHITE;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

}
