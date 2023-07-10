package de.eskalon.commons.misc;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Consumer;

import org.reflections8.Reflections;
import org.reflections8.scanners.FieldAnnotationsScanner;

import com.badlogic.gdx.utils.reflect.Field;

import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.asset.AnnotationAssetManager;
import de.eskalon.commons.asset.AnnotationAssetManager.Asset;
import de.eskalon.commons.misc.IFieldAnnotationScanner;

public class DesktopFieldAnnotationScanner implements IFieldAnnotationScanner {

	@Override
	public void forEachFieldAnnotatedWith(String packageRoot,
			Class<? extends Annotation> annotation, Consumer<Field> action) {
		Reflections reflections = new Reflections(packageRoot,
				new FieldAnnotationsScanner());
		Set<java.lang.reflect.Field> assetFields = reflections
				.getFieldsAnnotatedWith(annotation);

		for (java.lang.reflect.Field f : assetFields) {
			action.accept(ReflectionUtils.convertFieldObject(f));
		}
	}

}
