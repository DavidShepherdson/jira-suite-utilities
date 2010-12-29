package com.googlecode.jsu.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used for marking fields as container for arguments in validators,
 * conditions and post-function.
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: Argument.java 105 2007-10-09 13:34:25Z abashev $
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Argument {
    String value() default "";
}
