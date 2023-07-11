package de.eskalon.commons.inject.providers;

import com.badlogic.gdx.utils.reflect.Field;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;
import de.damios.guacamole.gdx.reflection.ReflectionUtils;
import de.eskalon.commons.inject.Log;
import de.eskalon.commons.inject.QualifiedProvider;

public class LoggerProvider implements QualifiedProvider<Logger> {

	@Override
	public Logger provide(Field field) {
		Log log = ReflectionUtils.getAnnotationObject(field, Log.class);
		if (log == null)
			return null;

		return LoggerService.getLogger(log.value());
	}

}
