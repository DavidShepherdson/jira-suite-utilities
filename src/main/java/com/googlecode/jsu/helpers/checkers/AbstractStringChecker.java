package com.googlecode.jsu.helpers.checkers;

import org.apache.log4j.Logger;

import com.googlecode.jsu.helpers.ConditionChecker;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
abstract class AbstractStringChecker implements ConditionChecker {
	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Check two strings.
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	protected abstract boolean checkStrings(String str1, String str2);

	/* (non-Javadoc)
	 * @see com.googlecode.jsu.helpers.ConditionChecker#checkValues(java.lang.Object, java.lang.Object)
	 */
	public final boolean checkValues(Object value1, Object value2) {
		final String str1 = asString(value1);
		final String str2 = asString(value2);
		
		boolean result = checkStrings(str1, str2);

		if (log.isDebugEnabled()) {
			log.debug("Compare strings [" + str1 + "] and [" + str2 + "] with result [" + result + "]");
		}

		return result;
	}
	
	/**
	 * Base method for converting values into strings.
	 * 
	 * @param o
	 * @return
	 */
	protected String asString(Object o) {
		if (o == null) {
			return null;
		}
		
		return o.toString();
	}
}
