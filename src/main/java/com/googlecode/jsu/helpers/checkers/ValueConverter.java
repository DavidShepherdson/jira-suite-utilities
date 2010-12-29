package com.googlecode.jsu.helpers.checkers;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
interface ValueConverter {
    /**
     * Get comparable value from object.
     * @param <T>
     * @param object
     * @return
     */
    Comparable<?> getComparable(Object object);
}
