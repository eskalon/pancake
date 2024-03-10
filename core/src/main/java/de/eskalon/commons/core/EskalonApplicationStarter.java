/*
 * Copyright 2023 eskalon
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

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.BufferUtils;

import de.damios.guacamole.Exceptions;
import de.damios.guacamole.concurrent.ThreadHandler;
import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.audio.DefaultSoundManager;
import de.eskalon.commons.audio.ISoundManager;
import de.eskalon.commons.event.EventBus;
import de.eskalon.commons.event.EventQueueBus;
import de.eskalon.commons.graphics.PostProcessingPipeline;
import de.eskalon.commons.inject.EskalonInjector;
import de.eskalon.commons.inject.providers.AssetProviders;
import de.eskalon.commons.inject.providers.LoggerProvider;
import de.eskalon.commons.inject.providers.LoggerProvider.Log;
import de.eskalon.commons.screen.ScreenManager;
import de.eskalon.commons.screens.EskalonScreenManager;
import de.eskalon.commons.screens.EskalonSplashScreen;
import de.eskalon.commons.settings.EskalonSettings;
import de.eskalon.commons.utils.ContextUtils;
import de.eskalon.commons.utils.GL32CMacIssueHandler;

/**
 * This class wraps an instance of {@link AbstractEskalonApplication} which is
 * created via {@linkplain EskalonInjector#getInstance(Class) dependency
 * injection}.
 */
public class EskalonApplicationStarter implements ApplicationListener {

	private static final Logger LOG = LoggerService
			.getLogger(EskalonApplicationStarter.class);

	private AbstractEskalonApplication application;

	private String appName;
	private Class<? extends AbstractEskalonApplication> applicationClazz;
	private StartArguments startArgs;

	public EskalonApplicationStarter(String appName,
			Class<? extends AbstractEskalonApplication> applicationClazz,
			StartArguments startArgs) {
		this.appName = appName;
		this.applicationClazz = applicationClazz;
		this.startArgs = startArgs;
	}

	public EskalonApplicationStarter(String appName,
			Class<? extends AbstractEskalonApplication> applicationClazz) {
		this(appName, applicationClazz,
				StartArguments.create().enableDebugLogging().build());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void create() {
		/*
		 * SET UP LOGGING
		 */
		if (Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
			Gdx.app.setApplicationLogger(ReflectionUtils.newInstanceOrNull(
					"de.eskalon.commons.log.EskalonDesktopLogger",
					ApplicationLogger.class));

		LoggerService.setLogLevel(startArgs.getLogLevel());

		ThreadHandler.instance().setExceptionHandler((r, t) -> {
			LOG.error("Uncaught exception in ThreadHandler: %s",
					Exceptions.getStackTraceAsString(t));
		});

		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			LOG.error("Uncaught exception in thread '%s': %s", t.getName(),
					Exceptions.getStackTraceAsString(e));
		});

		/*
		 * CREATE APP CONTEXT
		 */
		EskalonApplicationContext appContext = new EskalonApplicationContext(
				appName, ContextUtils.getVersion());

		/*
		 * LOG DEBUG INFORMATION
		 */
		LOG.info("Version: '%s' | App Type: '%s' | OS: '%s'",
				appContext.getVersion(), Gdx.app.getType(),
				System.getProperty("os.name"));
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
		 * INITIALIZE DEPENDENCY INJECTION
		 * 
		 * Registering the dependency providers outside of EskalonApplication
		 * itself makes testing the game easier!
		 */
		EskalonInjector injector = EskalonInjector.instance();

		// App context
		injector.bindToInstance(EskalonApplicationContext.class, appContext);

		// Asset manager
		AnnotationAssetManager assetManager = new AnnotationAssetManager(
				new InternalFileHandleResolver());
		injector.bindToInstance(AnnotationAssetManager.class, assetManager);
		injector.bindToSubclass(AssetManager.class,
				AnnotationAssetManager.class);
		AssetProviders.bindAssetProviders(injector);

		// Event bus
		injector.bindToConstructor(EventQueueBus.class);
		injector.bindToSubclass(EventBus.class, EventQueueBus.class);

		// Logger
		injector.bindToQualifiedProvider(Logger.class, Log.class,
				LoggerProvider.class);

		// Post processor
		injector.bindToConstructor(PostProcessingPipeline.class);

		// Settings
		injector.bindToConstructor(EskalonSettings.class);

		// Sound manager
		Class<? extends ISoundManager> soundManagerImplClass;
		if (Gdx.app.getType() == ApplicationType.Desktop
				|| Gdx.app.getType() == ApplicationType.HeadlessDesktop)
			soundManagerImplClass = (Class<? extends ISoundManager>) ReflectionUtils
					.getClassByNameOrNull(
							"de.eskalon.commons.audio.DesktopSoundManager");
		else
			soundManagerImplClass = DefaultSoundManager.class;

		injector.bindToConstructor(soundManagerImplClass);
		injector.bindToSubclass(ISoundManager.class, soundManagerImplClass);

		// Sprite batch
		injector.bindToInstance(SpriteBatch.class,
				GL32CMacIssueHandler.createSpriteBatch());

		// Start args
		injector.bindToInstance(StartArguments.class, startArgs);

		// Splash screen
		injector.bindToConstructor(EskalonSplashScreen.class);

		LOG.debug("Dependency injection initialized!");

		/*
		 * CREATE GAME VIA DEPENDENCY INJECTION
		 * 
		 * Creating the game via dependency injection allows us to inject its
		 * own members. It also takes care of the fact that injection couldn't
		 * happen in the game's constructor since libGDX wouldn't be initialized
		 * yet.
		 */
		injector.bindToConstructor(applicationClazz);
		application = injector.getInstance(applicationClazz);
		injector.bindToSubclass(ScreenManager.class,
				EskalonScreenManager.class);
		injector.bindToInstance(EskalonScreenManager.class,
				(EskalonScreenManager) application.getScreenManager()); // Screenmanager
																		// is
																		// created
																		// in
																		// the
																		// super
																		// class
		application.create();
	}

	@Override
	public void resize(int width, int height) {
		application.resize(width, height);
	}

	@Override
	public void render() {
		application.render();
	}

	@Override
	public void pause() {
		application.pause();
	}

	@Override
	public void resume() {
		application.resume();
	}

	@Override
	public void dispose() {
		application.dispose();
	}

	public AbstractEskalonApplication getApplication() {
		return application;
	}

}
