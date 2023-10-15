package de.eskalon.commons.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import de.damios.guacamole.gdx.DefaultInputProcessor;
import de.eskalon.commons.IssueTestApp.TestScreen;
import de.eskalon.commons.core.AbstractEskalonApplication;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.AbstractImageScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;

public class ImageScreenExample extends AbstractEskalonApplication {

	@Override
	protected Class<? extends AbstractEskalonScreen> initApp() {
		EskalonInjector.getInstance().bindToConstructor(TestScreen.class);
		return TestScreen.class;
	}

	public class TestScreen extends AbstractImageScreen {

		private BitmapFont font;

		@Inject // needed so the class does not have to be static
		public TestScreen() {
			Gdx.graphics.setResizable(true);

			setImage(new Texture(Gdx.files.internal("test.png")));

			addInputProcessor(new DefaultInputProcessor() {
				@Override
				public boolean keyDown(int keycode) {
					if (keycode == Keys.SPACE || keycode == Keys.M) {
						setMode(ImageScreenMode
								.values()[(getMode().ordinal() + 1)
										% ImageScreenMode.values().length]);
					}
					return false;
				}
			});

			font = assetManager.get(EskalonCommonsAssets.DEFAULT_FONT_NAME);
		}

		@Override
		public void render(float delta) {
			super.render(delta);

			batch.begin();
			font.draw(batch, getMode().toString(), 5, 15);
			batch.end();
		}

	}

}
