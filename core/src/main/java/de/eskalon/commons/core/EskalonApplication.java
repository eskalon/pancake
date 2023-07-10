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

import java.nio.IntBuffer;

import javax.annotation.Nullable;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.BufferUtils;
import com.github.acanthite.gdx.graphics.g2d.FreeTypeSkinLoader;

import de.damios.guacamole.gdx.assets.Text;
import de.damios.guacamole.gdx.assets.TextLoader;
import de.damios.guacamole.gdx.graphics.ShaderCompatibilityHelper;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.BitmapFontAssetLoaderParametersFactory;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.asset.PlaylistDefinitionLoader;
import de.eskalon.commons.asset.SkinAssetLoaderParametersFactory;
import de.eskalon.commons.audio.DefaultSoundManager;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.CommonsEvents.CommonsAssetsLoadedEvent;
import de.eskalon.commons.event.EventBusLogger;
import de.eskalon.commons.event.EventQueueBus;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.input.EskalonGameInputProcessor;
import de.eskalon.commons.misc.DebugInfoRenderer;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlankTimedTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;
import de.eskalon.commons.screens.EskalonSplashScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.utils.DevUtils;
import de.eskalon.commons.utils.ScreenshotUtils;
import de.eskalon.commons.utils.graphics.GL32CMacIssueHandler;

/**
 * A basic game application. Takes care of setting some convenience variables
 * and constants. Furthermore, adds:
 * <ul>
 * <li>an {@linkplain #getAssetManager() asset manager}</li>
 * <li>an {@linkplain #getEventBus() event bus}</li>
 * <li>a {@linkplain #getPostProcessor() post processor}</li>
 * <li>a {@linkplain #getSoundManager() sound manager}</li>
 * <li>a {@linkplain #getSpriteBatch() batch}</li>
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

	public static String VERSION;

	private static final Logger LOG = LoggerService
			.getLogger(EskalonApplication.class);

	private final EskalonApplicationConfiguration config;

	// Asset manager
	protected AnnotationAssetManager assetManager = new AnnotationAssetManager(
			new InternalFileHandleResolver());

	// Events
	protected EventQueueBus eventBus = new EventQueueBus();

	// Graphics & UI
	protected SpriteBatch batch;
	protected Skin uiSkin;
	protected @Nullable PostProcessingPipeline postProcessor;
	private DebugInfoRenderer debugInfoRenderer;

	// Input
	private EskalonGameInputProcessor applicationInputProcessor;

	// Settings
	protected EskalonSettings settings;

	// Sound
	protected ISoundManager soundManager;

	protected EskalonApplication() {
		this.config = getAppConfig();
	}

	protected EskalonApplicationConfiguration getAppConfig() {
		return new EskalonApplicationConfiguration();
	}

	@Override
	public final void create() {
		/*
		 * CONSTANTS
		 */
		VERSION = DevUtils.IN_DEV_ENV ? "Development Build"
				: (Gdx.app.getType() != ApplicationType.WebGL
						? EskalonApplication.class.getPackage()
								.getImplementationVersion()
						: "Version undefined");
		/*
		 * LOGGING
		 */
		if (Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
			Gdx.app.setApplicationLogger(ReflectionUtils.newInstanceOrNull(
					"de.eskalon.commons.log.EskalonDesktopLogger",
					ApplicationLogger.class));

		if (config.enableDebugLoggingOnStartup)
			LoggerService.showAll();
		else
			LoggerService.showInfoAndErrors();

		LOG.info("Version: '%s' | App Type: '%s' | OS: '%s'", VERSION,
				Gdx.app.getType(), System.getProperty("os.name"));
		LOG.debug("GL Context: '%s' (%s %d.%d.%d) | Renderer: '%s'",
				(Gdx.graphics.isGL32Available() ? "3.2"
						: (Gdx.graphics.isGL31Available() ? "3.1"
								: (Gdx.graphics.isGL30Available() ? "3.0"
										: "2.0"))),
				Gdx.graphics.getGLVersion().getType(),
				Gdx.graphics.getGLVersion().getMajorVersion(),
				Gdx.graphics.getGLVersion().getMinorVersion(),
				Gdx.graphics.getGLVersion().getReleaseVersion(),
				Gdx.graphics.getGLVersion().getRendererString());
		if (LoggerService.isDebugEnabled()) {
			IntBuffer tmpBuffer = BufferUtils.newIntBuffer(16);
			Gdx.gl20.glGetIntegerv(GL20.GL_MAX_TEXTURE_SIZE, tmpBuffer);
			LOG.debug("Max Texture Size: '%s' | Available Memory: %.2f GB",
					tmpBuffer.get(),
					(Runtime.getRuntime().maxMemory() / 1024F / 1024F / 1024F));
		}
		LOG.debug("Java Version: '%s'", System.getProperty("java.version"));
		LOG.info("");

		/*
		 * LOAD SETTINGS
		 */
		this.settings = new EskalonSettings(
				config.appName.trim().replace(" ", "-").toLowerCase());

		/*
		 * INITIALIZE MANAGED GAME
		 */
		super.create();

		/*
		 * CONFIGURE ASSET MANAGER
		 */
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

		/*
		 * EVENT BUS
		 */
		this.eventBus.register(new EventBusLogger());

		/*
		 * SOUND MANAGER
		 */
		if (Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
			this.soundManager = ReflectionUtils.newInstanceWithParamsOrNull(
					"de.eskalon.commons.audio.DesktopSoundManager",
					ISoundManager.class, new Class[] { EskalonSettings.class },
					new Object[] { settings });

		if (this.soundManager == null)
			this.soundManager = new DefaultSoundManager(settings);

		/*
		 * SPRITEBATCH
		 */
		this.batch = new SpriteBatch(1000,
				ShaderCompatibilityHelper.mustUse32CShader()
						? GL32CMacIssueHandler.createSpriteBatchShader()
						: null);

		/*
		 * SCREEN MANAGER
		 */
		this.getScreenManager().setHasDepth(config.provideDepthBuffers);

		/*
		 * POST PROCESSOR
		 */
		if (config.createPostProcessor) {
			this.postProcessor = new PostProcessingPipeline(
					Gdx.graphics.getBackBufferWidth(),
					Gdx.graphics.getBackBufferHeight(),
					config.provideDepthBuffers);
		}

		/*
		 * MISC
		 */
		debugInfoRenderer = new DebugInfoRenderer(batch, VERSION, soundManager);

		/*
		 * INPUT
		 */
		applicationInputProcessor = new EskalonGameInputProcessor(soundManager);
		getInputMultiplexer().addProcessor(applicationInputProcessor);

		/*
		 * SPLASH SCREEN
		 */
		this.screenManager.addScreen("blank", new BlankScreen(this));
		this.screenManager.addScreen("splash",
				new EskalonSplashScreen(this, config.skipSplashScreen));

		eventBus.register(CommonsAssetsLoadedEvent.class, (ev) -> {
			try {
				// Retrieve the loaded assets
				soundManager.addSoundEffect(
						assetManager
								.get(EskalonCommonsAssets.SHUTTER_SOUND_PATH),
						EskalonCommonsAssets.SHUTTER_SOUND_NAME);

				// Enable stuff that depends on eskalon's assets
				applicationInputProcessor.enable();
				debugInfoRenderer.initialize(getWidth(), getHeight(),
						assetManager
								.get(EskalonCommonsAssets.DEFAULT_FONT_NAME));

				// Push second screen (usually asset loading)
				if (!config.skipSplashScreen) {
					screenManager.pushScreen("blank", "splashOutTransition1");
					screenManager.pushScreen("blank", "splashOutTransition2");
					screenManager.pushScreen(initApp(), "splashOutTransition3");
				} else {
					screenManager.pushScreen(initApp(), null);
				}
			} catch (Exception e) {
				LOG.error(
						"An unexpected error occurred while initialising the application: %s",
						e.getLocalizedMessage());
			}
		});

		if (!config.skipSplashScreen) {
			BlendingTransition splashBlendingTransition = new BlendingTransition(
					batch, 0.25F, Interpolation.exp10In);
			screenManager.addScreenTransition("splashInTransition",
					splashBlendingTransition);
			BlendingTransition splashOutTransition1 = new BlendingTransition(
					batch, 0.18F, Interpolation.fade);
			screenManager.addScreenTransition("splashOutTransition1",
					splashOutTransition1);
			BlankTimedTransition splashOutTransition2 = new BlankTimedTransition(
					0.22F);
			screenManager.addScreenTransition("splashOutTransition2",
					splashOutTransition2);
			BlendingTransition splashOutTransition3 = new BlendingTransition(
					batch, 0.35F, Interpolation.pow2In);
			screenManager.addScreenTransition("splashOutTransition3",
					splashOutTransition3);
		}

		// Push the splash screen
		screenManager.pushScreen("splash",
				config.skipSplashScreen ? null : "splashInTransition");
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
		// Take care of posting the events in the rendering thread
		eventBus.dispatchEvents();

		// Profile stuff
		debugInfoRenderer.resetProfiler();
		debugInfoRenderer.setProfilingEnabled(
				applicationInputProcessor.isDebugOverlayEnabled());

		// Render the screen
		super.render();

		debugInfoRenderer.update(Gdx.graphics.getDeltaTime());

		// Render debug overlay
		if (applicationInputProcessor.isDebugOverlayEnabled()) {
			debugInfoRenderer.render();
		}

		// Take a screenshot
		if (applicationInputProcessor.pollTakeScreenshot()) {
			ScreenshotUtils.takeAndSaveScreenshot();
			soundManager
					.playSoundEffect(EskalonCommonsAssets.SHUTTER_SOUND_NAME);
		}
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);

		if (postProcessor != null)
			postProcessor.resize(width, height);

		debugInfoRenderer.resize(width, height);
	}

	/**
	 * @return the asset manager used by the game
	 */
	public AnnotationAssetManager getAssetManager() {
		return this.assetManager;
	}

	/**
	 * @return the events bus; events are queued first and then processed in the
	 *         rendering thread; see {@link EventQueueBus}
	 */
	public EventQueueBus getEventBus() {
		return eventBus;
	}

	/**
	 * @return the post processing pipeline for this application; has to be
	 *         activated via
	 *         {@linkplain EskalonApplicationConfiguration#createPostProcessor()};
	 *         otherwise it is {@code null}
	 */
	public @Nullable PostProcessingPipeline getPostProcessor() {
		return postProcessor;
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

	public EskalonSettings getSettings() {
		return settings;
	}

	@Override
	public void dispose() {
		super.dispose();

		assetManager.dispose();
		batch.dispose();

		if (postProcessor != null)
			postProcessor.dispose();
	}

}
