package de.eskalon.commons.inject;

import java.util.Arrays;
import java.util.HashMap;

import javax.annotation.Nullable;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import de.damios.guacamole.gdx.log.Logger;
import de.damios.guacamole.gdx.log.LoggerService;

public class EskalonInjector implements IInjector {

	private static final Logger LOG = LoggerService
			.getLogger(EskalonInjector.class);

	private static EskalonInjector instance;

	public static EskalonInjector getInstance() {
		if (instance == null)
			instance = new EskalonInjector();
		return instance;
	}

	private final HashMap<Class<?>, Class<?>> links = new HashMap<>();
	private final HashMap<Class<?>, Object> instances = new HashMap<>();
	private final HashMap<Class<?>, Provider<?>> providers = new HashMap<>();
	private final HashMap<Class<?>, QualifiedProvider<?>> qualifiedProviders = new HashMap<>();

	@Override
	public <T> void bindTo(Class<T> clazz, Class<? extends T> linkedClazz) {
		links.put(clazz, linkedClazz);
	}

	@Override
	public <T> void bindToInstance(Class<T> clazz, T instance) {
		instances.put(clazz, instance);
	}

	@Override
	public <T> void bindToProvider(Class<T> clazz, Provider<T> provider) {
		providers.put(clazz, provider);
	}

	@Override
	public <T> void bindToQualifiedProvider(Class<T> clazz,
			QualifiedProvider<T> provider) {
		qualifiedProviders.put(clazz, provider);
	}

	private @Nullable Object getInstanceForField(Field field) {
		Class<?> type = field.getType();

		// Linked bindings
		type = links.getOrDefault(type, type);

		// Instance bindings
		Object value = instances.get(type);
		if (value != null)
			return value;

		// Provider binding
		Provider<?> provider = providers.get(type);
		if (provider != null) {
			value = provider.provide();

			if (value != null)
				return value;
		}

		// Qualified provider binding
		QualifiedProvider<?> qualifiedProvider = qualifiedProviders.get(type);

		if (qualifiedProvider != null) {
			return qualifiedProvider.provide(field);
		}

		LOG.error("No binding found for %s with the following qualifiers: %s",
				type, Arrays.toString(field.getDeclaredAnnotations()));

		return null;
	}

	@Override
	public void injectMembers(Object target) {
		for (Field field : ClassReflection
				.getDeclaredFields(target.getClass())) {
			if (field.isAnnotationPresent(Inject.class)) {
				try {
					field.setAccessible(true);
					field.set(target, getInstanceForField(field));
				} catch (ReflectionException e) {
					LOG.error(
							"Error while injecting a value for %s into %s: %s",
							field, target, e);
				}
			}
		}

	}

}
