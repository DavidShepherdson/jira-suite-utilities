package com.googlecode.jsu.helpers.checkers;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
@SuppressWarnings("unchecked")
interface ComparingSnipet {
	/**
	 * Execute comparing action for objects.
	 * 
	 * @param <T>
	 * @param comp1
	 * @param comp2
	 * @return
	 */
	boolean compareObjects(Comparable comp1, Comparable comp2);
}
