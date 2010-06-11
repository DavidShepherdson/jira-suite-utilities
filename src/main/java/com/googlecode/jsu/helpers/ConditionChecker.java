package com.googlecode.jsu.helpers;

/**
 * Interface for checking conditions. Used as strategy pattern.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public interface ConditionChecker {
	/**
	 * Check two values and return true if condition success or false is not.
	 * 
	 * @param value1
	 * @param value2
	 * @return
	 */
	boolean checkValues(Object value1, Object value2);
}
