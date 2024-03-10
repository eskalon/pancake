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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.github.acanthite.gdx.graphics.g2d.FreeTypeSkinLoader;

import de.damios.guacamole.Exceptions;
import de.damios.guacamole.gdx.assets.Text;
import de.damios.guacamole.gdx.assets.TextLoader;
import de.damios.guacamole.gdx.log.Logger;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.BitmapFontAssetLoaderParametersFactory;
import de.eskalon.commons.asset.PlaylistDefinition;
import de.eskalon.commons.asset.PlaylistDefinitionLoader;
import de.eskalon.commons.asset.SkinAssetLoaderParametersFactory;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.CommonsEvents.CommonsAssetsLoadedEvent;
import de.eskalon.commons.event.EventBusLogger;
import de.eskalon.commons.event.EventQueueBus;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.IInjector;
import de.eskalon.commons.inject.annotations.Inject;
import de.eskalon.commons.inject.providers.LoggerProvider.Log;
import de.eskalon.commons.input.EskalonApplicationInputProcessor;
import de.eskalon.commons.misc.DebugInfoRenderer;
import de.eskalon.commons.screen.transition.ScreenTransition;
import de.eskalon.commons.screen.transition.impl.BlankTimedTransition;
import de.eskalon.commons.screen.transition.impl.BlendingTransition;
import de.eskalon.commons.screens.AbstractEskalonScreen;
import de.eskalon.commons.screens.BlankScreen;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.commons.screens.EskalonSplashScreen;
import de.eskalon.commons.screens.EskalonSplashScreen.EskalonCommonsAssets;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.utils.ScreenshotUtils;

/**
 * A basic game application. Has to be created via
 * {@link EskalonApplicationStarter} to take advantage of its features!
 * <p>
 * The application provides the following objects via {@linkplain IInjector
 * dependency injection}:
 * <ul>
 * <li>an {@link AnnotationAssetManager}</li>
 * <li>an {@link EskalonApplicationContext}</li>
 * <li>an {@link EventQueueBus} (submitted events are queued first and then
 * processed on the rendering thread)</li>
 * <li>a {@link PostProcessingPipeline} (if activated via
 * {@linkplain EskalonApplicationConfiguration#createPostProcessor()})</li>
 * <li>an {@link ISoundManager}</li>
 * <li>a {@link SpriteBatch}</li>
 * <li>the {@link StartArguments}</li>
 * </ul>
 * In addition, the application registers some convenience keybinds, in
 * particular F2 to toggle a {@linkplain DebugInfoRenderer debug overlay} and
 * F12 to take a screenshot.
 * <p>
 * When the application is created, an {@link EskalonSplashScreen} is pushed.
 * 
 * @author damios
 * @see ManagedGame
 */
public abstract class AbstractEskalonApplication
		extends ManagedGame<AbstractEskalonScreen, ScreenTransition> {

	private @Inject @Log(AbstractEskalonApplication.class) Logger LOG;

	protected EskalonApplicationConfiguration config;

	protected @Inject EskalonApplicationContext appContext;
	protected @Inject AnnotationAssetManager assetManager;
	protected @Inject SpriteBatch batch;
	protected @Inject EventQueueBus eventBus;
	protected @Inject PostProcessingPipeline postProcessor;
	protected @Inject EskalonSettings settings;
	protected @Inject ISoundManager soundManager;
	protected @Inject StartArguments startArgs;

	// Convenience keybind stuff
	private EskalonApplicationInputProcessor eskalonInputProcessor;
	private DebugInfoRenderer debugInfoRenderer;

	protected AbstractEskalonApplication() {
		this(EskalonApplicationConfiguration.create().build());
	}

	protected AbstractEskalonApplication(
			EskalonApplicationConfiguration config) {
		super(new EskalonScreenManager());
		this.config = config;
	}

	@Override
	public final void create() {
		/*
		 * INITIALIZE MANAGED GAME
		 */
		super.create();

		/*
		 * ASSET LOADING
		 */
		// Loader
		FileHandleResolver resolver = assetManager.getFileHandleResolver();
		assetManager.setLoader(FreeTypeFontGenerator.class,
				new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf",
				new FreetypeFontLoader(resolver));
		assetManager.setLoader(Text.class, new TextLoader(resolver));
		assetManager.setLoader(PlaylistDefinition.class,
				new PlaylistDefinitionLoader(resolver));
		assetManager.setLoader(Skin.class, new FreeTypeSkinLoader(resolver));

		// Loader Parameter Factories
		assetManager.registerAssetLoaderParametersFactory(BitmapFont.class,
				new BitmapFontAssetLoaderParametersFactory());
		assetManager.registerAssetLoaderParametersFactory(Skin.class,
				new SkinAssetLoaderParametersFactory());

		/*
		 * EVENT BUS
		 */
		eventBus.register(new EventBusLogger());

		/*
		 * SCREEN MANAGER
		 */
		IInjector injector = EskalonInjector.instance();
		injector.injectMembers(this.getScreenManager());
		this.getScreenManager().setHasDepth(config.shouldProvideDepthBuffers());
		this.getScreenManager().setAutoDispose(true, false);

		/*
		 * POST PROCESSING
		 */
		if (config.shouldCreatePostProcessor()) {
			postProcessor.initialize(Gdx.graphics.getBackBufferWidth(),
					Gdx.graphics.getBackBufferHeight(),
					config.shouldProvideDepthBuffers());
		} else {
			postProcessor = null;
		}

		/*
		 * MISC
		 */
		debugInfoRenderer = new DebugInfoRenderer(batch,
				appContext.getVersion(), soundManager);

		/*
		 * INPUT
		 */
		eskalonInputProcessor = new EskalonApplicationInputProcessor(
				soundManager);
		getInputMultiplexer().addProcessor(eskalonInputProcessor);

		/*
		 * SPLASH SCREEN
		 */
		eventBus.register(CommonsAssetsLoadedEvent.class, (ev) -> {
			try {
				// Retrieve the loaded assets
				soundManager.addSoundEffect(
						assetManager
								.get(EskalonCommonsAssets.SHUTTER_SOUND_PATH),
						EskalonCommonsAssets.SHUTTER_SOUND_NAME);
				// Enable stuff that depends on eskalon's assets
				eskalonInputProcessor.enable();
				debugInfoRenderer.initialize(Gdx.graphics.getWidth(),
						Gdx.graphics.getHeight(), assetManager
								.get(EskalonCommonsAssets.DEFAULT_FONT_NAME));

				// Push second screen (usually asset loading)
				if (!startArgs.shouldSkipSplashScreen()) {
					injector.bindToConstructor(BlankScreen.class);
					screenManager.pushScreen(
							injector.getInstance(BlankScreen.class),
							new BlendingTransition(batch, 0.18F,
									Interpolation.fade));
					screenManager.pushScreen(
							injector.getInstance(BlankScreen.class),
							new BlankTimedTransition(0.22F));
					screenManager.pushScreen(injector.getInstance(initApp()),
							new BlendingTransition(batch, 0.35F,
									Interpolation.pow2In));
				} else {
					screenManager.pushScreen(injector.getInstance(initApp()),
							null);
				}
			} catch (Exception e) {
				LOG.error(
						"An unexpected error occurred while initialising the application: %s",
						Exceptions.getStackTraceAsString(e));
				Gdx.app.exit();
			}
		});

		// Log current status
		LOG.debug("Application was started successfully!");

		// Push the splash screen
		injector.bindToConstructor(EskalonSplashScreen.class);

		screenManager.pushScreen(
				injector.getInstance(EskalonSplashScreen.class),
				startArgs.shouldSkipSplashScreen() ? null
						: new BlendingTransition(batch, 0.25F,
								Interpolation.exp10In));
	}

	/**
	 * Takes care of initializing the application.
	 * 
	 * @return the class of the screen that should be pushed after the splash
	 *         screen; the class has to be registered with the injector!
	 */
	protected abstract Class<? extends AbstractEskalonScreen> initApp();

	@Override
	public void render() {
		// Take care of posting the events in the rendering thread
		eventBus.dispatchEvents();

		// Profile stuff
		debugInfoRenderer.resetProfiler();
		debugInfoRenderer.setProfilingEnabled(
				eskalonInputProcessor.isDebugOverlayEnabled());

		// Render the screen
		super.render();

		debugInfoRenderer.update(Gdx.graphics.getDeltaTime());

		// Render debug overlay
		if (eskalonInputProcessor.isDebugOverlayEnabled()) {
			debugInfoRenderer.render();
		}

		// Take a screenshot
		if (eskalonInputProcessor.pollTakeScreenshot()) {
			ScreenshotUtils.takeAndSaveScreenshot();
			soundManager
					.playSoundEffect(EskalonCommonsAssets.SHUTTER_SOUND_NAME);
		}
	}

	@Override
	public void resize(int width, int height) {
		if (width == 0 || height == 0)
			return; // see ManagedGame#resize

		super.resize(width, height);

		if (postProcessor != null)
			postProcessor.resize(width, height);

		debugInfoRenderer.resize(width, height);
	}

	@Override
	public void dispose() {
		EskalonInjector.reset(); // on Android, the JVM might not be terminated,
									// so this prevents the static instance from
									// staying around

		super.dispose();

		assetManager.dispose();
		batch.dispose();

		if (postProcessor != null)
			postProcessor.dispose();

		for (ScreenTransition transition : appContext.getTransitions()
				.values()) {
			transition.dispose();
		}
	}

}
