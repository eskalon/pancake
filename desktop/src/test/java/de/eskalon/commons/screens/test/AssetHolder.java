package de.eskalon.commons.screens.test;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.eskalon.commons.asset.AnnotationAssetManager.Asset;

public class AssetHolder {
	@Asset("test.png")
	private Texture test1a;

	@Asset(value = "randomName.ttf", params = "font/OpenSans.ttf, 19")
	private BitmapFont test1b;

	@Asset(value = "test2.png", disabled = true)
	private static Texture test2;
}
