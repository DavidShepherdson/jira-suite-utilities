package com.atlassian.jira.plugin.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author <A href="mailto:Alexey_Abashev@epam.com">Alexey Abashev</A>
 * @version $Id$
 */
public abstract class AbstractVisitor {
	public abstract Class<? extends Annotation> getAnnotation();
	
	public void visitField(Object source, Field field, Annotation annotation) {
	}
}
