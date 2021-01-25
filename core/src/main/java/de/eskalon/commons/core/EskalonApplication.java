/*
 * Copyright 2020 eskalon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.eskalon.commons.core;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.reflect.ReflectionUtils;
import com.github.acanthite.gdx.graphics.g2d.FreeTypeSkinLoader;

import de.damios.guacamole.annotations.GwtIncompatible;
import de.damios.guacamole.gdx.assets.Text;
import de.damios.guacamole.gdx.assets.TextLoader;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.BitmapFontAssetLoaderParametersFactory;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.asset.PlaylistDefinitionLoader;
import de.eskalon.commons.asset.SkinAssetLoaderParametersFactory;
import de.eskalon.commons.audio.DefaultSoundManager;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.CommonsEvents.CommonsAssetsLoadedEvent;
import de.eskalon.commons.event.EventQueueBus;
import de.eskalon.commons.input.EskalonGameInputProcessor;
import de.eskalon.commons.misc.DebugInfoRenderer;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlankTimedTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankEskalonScreen;
import de.eskalon.commons.screens.EskalonSplashScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.commons.utils.ScreenshotUtils;
import de.eskalon.commons.utils.graphics.GL32CMacIssueHandler;

/**
 * A basic game application. Takes care of setting some convenience variables
 * and constants. Furthermore, adds:
 * <ul>
 * <li>an {@linkplain #getAssetManager() asset manager}</li>
 * <li>a {@linkplain #getSoundManager() sound manager}</li>
 * <li>an {@linkplain #getEventBus() event bus}</li>
 * <li>a {@linkplain #getSpriteBatch() batch} and an {@linkplain #getUICamera()
 * ui camera}</li>
 * <li>support for setting an {@linkplain #uiSkin UI skin}</li>
 * <li>a screenshot keybind (see {@link EskalonGameInputProcessor}) and a
 * {@linkplain DebugInfoRenderer debug overlay}</li>
 * </ul>
 * When the application is created, a {@link EskalonSplashScreen} is pushed.
 * 
 * @author damios
 * @see BasicScreenManager
 */
public abstract class EskalonApplication
		extends ManagedGame<AbstractEskalonScreen, ScreenTransition> {

	private static final Logger LOG = LoggerService
			.getLogger(EskalonApplication.class);

	@GwtIncompatible
	public final boolean IN_DEV_ENV = EskalonApplication.class.getPackage()
			.getImplementationVersion() == null;
	@GwtIncompatible
	public final String VERSION = IN_DEV_ENV ? "Development Build"
			: EskalonApplication.class.getPackage().getImplementationVersion();

	private DebugInfoRenderer debugInfoRenderer;

	protected AnnotationAssetManager assetManager = new AnnotationAssetManager(
			new InternalFileHandleResolver());

	protected ISoundManager soundManager;

	private EskalonGameInputProcessor applicationInputProcessor = new EskalonGameInputProcessor();

	protected EventQueueBus eventBus = new EventQueueBus();

	protected SpriteBatch batch;
	protected OrthographicCamera uiCamera;

	protected Skin uiSkin;

	private boolean debugLogging;
	private boolean hasDepth;

	protected EskalonApplication() {
		this(false, false);
	}

	protected EskalonApplication(boolean debugLogging, boolean hasDepth) {
		this.debugLogging = debugLogging;
		this.hasDepth = hasDepth;
	}

	@Override
	public final void create() {
		// Log stuff
		if (Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
			Gdx.app.setApplicationLogger(ReflectionUtils.newInstance(
					"de.eskalon.commons.log.EskalonDesktopLogger",
					ApplicationLogger.class));

		if (debugLogging)
			LoggerService.showAll();
		else
			LoggerService.showInfoAndErrors();

		LOG.info("Version: '%s' | App Type: '%s' | OS: '%s'", VERSION,
				Gdx.app.getType(), System.getProperty("os.name"));
		LOG.debug("GL30 Available: '%b' | Renderer: '%s'",
				Gdx.graphics.isGL30Available(),
				Gdx.graphics.getGLVersion().getRendererString());
		LOG.debug("Java Version: '%s'", System.getProperty("java.version"));

		// Initialize managed game
		super.create();

		// Add input listener
		getInputMultiplexer().addProcessor(applicationInputProcessor);

		// Configure asset manager
		this.assetManager.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(
						this.assetManager.getFileHandleResolver()));
		this.assetManager.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(
						this.assetManager.getFileHandleResolver()));
		this.assetManager.setLoader(Text.class,
				new TextLoader(this.assetManager.getFileHandleResolver()));
		this.assetManager.setLoader(PlaylistDefinition.class,
				new PlaylistDefinitionLoader(
						this.assetManager.getFileHandleResolver()));
		this.assetManager.setLoader(Skin.class, new FreeTypeSkinLoader(
				this.assetManager.getFileHandleResolver()));

		this.assetManager.registerAssetLoaderParametersFactory(BitmapFont.class,
				new BitmapFontAssetLoaderParametersFactory());
		this.assetManager.registerAssetLoaderParametersFactory(Skin.class,
				new SkinAssetLoaderParametersFactory());

		// Sound manager
		if (Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
			this.soundManager = ReflectionUtils.newInstance(
					"de.eskalon.commons.audio.DesktopSoundManager",
					ISoundManager.class);

		if (this.soundManager == null)
			this.soundManager = new DefaultSoundManager();

		// Create sprite batch & camera
		this.batch = new SpriteBatch(1000,
				GL32CMacIssueHandler.doUse32CShader()
						? GL32CMacIssueHandler.createSpriteBatchShader()
						: null);

		this.uiCamera = new OrthographicCamera(viewportWidth, viewportHeight);
		this.uiCamera.combined.setToOrtho2D(0, 0, getWidth(), getHeight());

		// Configure screen manager
		this.getScreenManager().setHasDepth(hasDepth);

		// Debug info renderer
		debugInfoRenderer = new DebugInfoRenderer(batch, debugLogging, VERSION,
				soundManager);

		// Splash Screen
		this.screenManager.addScreen("blank", new BlankEskalonScreen(this));
		this.screenManager.addScreen("splash", new EskalonSplashScreen(this));

		eventBus.on(CommonsAssetsLoadedEvent.class, () -> {
			// Enable stuff depending on commons assets
			applicationInputProcessor.enable();
			debugInfoRenderer.initialize(getWidth(), getHeight(),
					assetManager.get(EskalonCommonsAssets.DEFAULT_FONT_NAME));

			// Push second screen (usually asset loading)
			screenManager.pushScreen("blank", "splashOutTransition1");
			screenManager.pushScreen("blank", "splashOutTransition2");
			screenManager.pushScreen(initApp(), "splashOutTransition3");
		});

		BlendingTransition splashBlendingTransition = new BlendingTransition(
				batch, 0.25F, Interpolation.exp10In);
		screenManager.addScreenTransition("splashInTransition",
				splashBlendingTransition);
		BlendingTransition splashOutTransition1 = new BlendingTransition(batch,
				0.18F, Interpolation.fade);
		screenManager.addScreenTransition("splashOutTransition1",
				splashOutTransition1);
		BlankTimedTransition splashOutTransition2 = new BlankTimedTransition(
				0.22F);
		screenManager.addScreenTransition("splashOutTransition2",
				splashOutTransition2);
		BlendingTransition splashOutTransition3 = new BlendingTransition(batch,
				0.35F, Interpolation.pow2In);
		screenManager.addScreenTransition("splashOutTransition3",
				splashOutTransition3);

		// Push the splash screen
		screenManager.pushScreen("splash", "splashInTransition");
	}

	/**
	 * Takes care of initializing the application.
	 * 
	 * @return the name of the screen that should be pushed after the splash
	 *         screen
	 */
	protected abstract String initApp();

	@Override
	public void render() {
		/*
		 * Takes care of posting the events in the rendering thread
		 */
		eventBus.dispatchEvents();

		/*
		 * Render the screen
		 */
		super.render();

		/*
		 * Update fps counter
		 */
		debugInfoRenderer.update(Gdx.graphics.getDeltaTime());

		/*
		 * Debug overlay
		 */
		if (applicationInputProcessor.isDebugOverlayEnabled()) {
			debugInfoRenderer.render();
		}
		/*
		 * Take a screenshot
		 */
		if (applicationInputProcessor.pollTakeScreenshot()) {
			ScreenshotUtils.takeAndSaveScreenshot();
			soundManager
					.playSoundEffect(EskalonCommonsAssets.SHUTTER_SOUND_NAME);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		debugInfoRenderer.resize(width, height);
		uiCamera.combined.setToOrtho2D(0, 0, getWidth(), getHeight());
	}

	/**
	 * @return the asset manager used by the game
	 */
	public AnnotationAssetManager getAssetManager() {
		return this.assetManager;
	}

	/**
	 * @return the sound manager used to play the game's audio
	 */
	public ISoundManager getSoundManager() {
		return soundManager;
	}

	/**
	 * @return the sprite batch to render 2D stuff with
	 */
	public SpriteBatch getSpriteBatch() {
		return batch;
	}

	/**
	 * @return the camera used by the UI screens
	 */
	public OrthographicCamera getUICamera() {
		return this.uiCamera;
	}

	/**
	 * @return the events bus; events are queued first and then processed in the
	 *         rendering thread; see {@link EventQueueBus}
	 */
	public EventQueueBus getEventBus() {
		return eventBus;
	}

	public void setUISkin(Skin skin) {
		this.uiSkin = skin;
	}

	/**
	 * @return the application's UI skin; has to be set via
	 *         {@link #setUISkin(Skin)} beforehand
	 */
	public Skin getUISkin() {
		return uiSkin;
	}

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		batch.dispose();

		// if (uiSkin != null)
		// uiSkin.dispose();
	}

}
