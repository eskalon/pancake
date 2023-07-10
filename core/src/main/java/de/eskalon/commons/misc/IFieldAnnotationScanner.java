package de.eskalon.commons.misc;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.badlogic.gdx.utils.reflect.Field;

public interface IFieldAnnotationScanner {

	public void forEachFieldAnnotatedWith(String packageRoot,
			Class<? extends Annotation> annotation, Consumer<Field> action);

}
