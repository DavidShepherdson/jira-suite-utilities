package com.atlassian.jira.plugin.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation is used for marking fields as container for transient field in validators,
 * conditions and post-function.
 * 
 * @author <A href="mailto:Alexey_Abashev@epam.com">Alexey Abashev</A>
 * @version $Id$
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface TransientVariable {
	String value() default "";
}
