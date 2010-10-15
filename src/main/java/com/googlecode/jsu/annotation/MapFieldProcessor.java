package com.googlecode.jsu.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: MapFieldProcessor.java 105 2007-10-09 13:34:25Z abashev $
 */
public class MapFieldProcessor extends AbstractVisitor {
    private final Logger log = Logger.getLogger(MapFieldProcessor.class);

    private final Class<? extends Annotation> annotation;
    private final Map<String, Object> values;

    /**
     * @param annotation
     * @param values
     */
    public MapFieldProcessor(Class<? extends Annotation> annotation, Map<String, Object> values) {
        super();

        this.annotation = annotation;
        this.values = values;
    }

    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.annotation.AbstractVisitor#visitField(java.lang.reflect.Field)
     */
    public void visitField(Object source, Field field, Annotation sourceAnnon) {
        String fieldName = getAnnotationValue(sourceAnnon);

        if ((fieldName == null) || ("".equals(fieldName))) {
            fieldName = field.getName();
        }

        try {
            boolean access = field.isAccessible();

            field.setAccessible(true);
            field.set(source, values.get(fieldName));
            field.setAccessible(access);
        } catch (IllegalArgumentException e) {
            log.error("Unable to set class field - " + fieldName, e);
        } catch (IllegalAccessException e) {
            log.error("Unable to set class field - " + fieldName, e);
        }
    }

    protected String getAnnotationValue(Annotation annotation) {
        String result = null;

        try {
            Method valueMethod = annotation.getClass().getDeclaredMethod("value", new Class[] {});

            result = (String) valueMethod.invoke(annotation, new Object[] {});
        } catch (SecurityException e) {
            // Everything ok
        } catch (NoSuchMethodException e) {
            // Everything ok
        } catch (IllegalArgumentException e) {
            // Everything ok
        } catch (IllegalAccessException e) {
            // Everything ok
        } catch (InvocationTargetException e) {
            // Everything ok
        }

        return result;
    }
}
