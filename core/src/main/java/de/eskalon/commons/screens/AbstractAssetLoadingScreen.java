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

package de.eskalon.commons.screens;

import java.lang.reflect.Field;
import java.util.Set;

import javax.annotation.Nullable;

import org.reflections8.Reflections;
import org.reflections8.scanners.FieldAnnotationsScanner;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;

import de.damios.guacamole.annotations.GwtIncompatible;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.core.EskalonApplication;

/**
 * This screen takes care of loading all assets for an
 * {@link EskalonApplication}. Assets have to be in a package below the
 * specified root package and be annotated with {@link Asset}.
 * <p>
 * Afterwards, the loaded assets can be injected in the respective fields via
 * {@link AnnotationAssetManager#injectAssets(Object)}.
 * 
 * @author damios
 */
@GwtIncompatible
public abstract class AbstractAssetLoadingScreen extends AbstractEskalonScreen {

	protected EskalonApplication application;
	private @Nullable String packageRoot;

	private int loadingFps;
	private float progress;
	private boolean isDone = false;

	/**
	 * @param application
	 * @param packageRoot
	 *            the root package, e.g. "de.eskalon"
	 * @param loadingFps
	 *            used to call {@link AssetManager#update(int)}
	 */
	public AbstractAssetLoadingScreen(EskalonApplication application,
			@Nullable String packageRoot, int loadingFps) {
		this.application = application;
		this.packageRoot = packageRoot;
		this.loadingFps = loadingFps;
	}

	public AbstractAssetLoadingScreen(EskalonApplication application,
			@Nullable String packageRoot) {
		this(application, packageRoot, 30);
	}

	@Override
	protected void create() {
		loadOwnAssets();

		if (packageRoot != null) {
			Reflections reflections = new Reflections(packageRoot,
					new FieldAnnotationsScanner());
			Set<Field> assetFields = reflections
					.getFieldsAnnotatedWith(Asset.class);

			for (Field f : assetFields) {
				application.getAssetManager().loadAnnotatedAsset(
						ReflectionUtils.convertFieldObject(f));
			}
		}
	}

	@Override
	public void show() {
		super.show();
		this.isDone = false;
	}

	/**
	 * Loads the assets used in the loading screen itself.
	 */
	protected abstract void loadOwnAssets();

	/**
	 * This method is responsible for finishing up the loaded assets (e.g.
	 * create the skin) and pushing the next screen.
	 */
	protected abstract void onFinishedLoading();

	@Override
	public void render(float delta) {
		progress = MathUtils.clamp(
				application.getAssetManager().getProgress() + 0.02F, 0, 1);

		// Check if the asset manager is done
		if (!isDone
				&& application.getAssetManager().update(1000 / loadingFps)) {
			isDone = true;
			onFinishedLoading();
		}

		render(delta, progress);
	}

	@Override
	protected EskalonApplication getApplication() {
		return application;
	}

	public abstract void render(float delta, float progress);

}
