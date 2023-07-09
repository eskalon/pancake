package de.eskalon.commons.inject;

public interface IInjector {

	public <T> void bindTo(Class<T> clazz, Class<? extends T> linkedClazz);

	public <T> void bindToInstance(Class<T> clazz, T instance);

	public <T> void bindToProvider(Class<T> clazz, Provider<T> provider);

	// public <T> void bindToProvider(Class<T> clazz, Class providerClass);

	public <T> void bindToQualifiedProvider(Class<T> clazz,
			QualifiedProvider<T> provider);

	public void injectMembers(Object target);

}
