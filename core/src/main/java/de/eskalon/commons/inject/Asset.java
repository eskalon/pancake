package de.eskalon.commons.inject;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Qualifier
public @interface Asset {

	boolean disabled() default false;

	/**
	 * @return the path to the asset to inject.
	 */
	String value();

	String params() default "";
}