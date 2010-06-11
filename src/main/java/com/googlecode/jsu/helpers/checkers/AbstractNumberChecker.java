package com.googlecode.jsu.helpers.checkers;

import org.apache.log4j.Logger;

import com.googlecode.jsu.helpers.ConditionChecker;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
abstract class AbstractNumberChecker implements ConditionChecker {
	private final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Compare two doubles.
	 * 
	 * @param double1
	 * @param double2
	 * @return
	 */
	protected abstract boolean checkNumbers(Double double1, Double double2);

	/* (non-Javadoc)
	 * @see com.googlecode.jsu.helpers.ConditionChecker#checkValues(java.lang.Object, java.lang.Object)
	 */
	public final boolean checkValues(Object value1, Object value2) {
		Double d1, d2;
		
		try {
			d1 = asDouble(value1);
		} catch (NumberFormatException e) {
			log.warn("Wrong number format at [" + value1 + "]");
			
			return false;
		}
		
		try {
			d2 = asDouble(value2);
		} catch (NumberFormatException e) {
			log.warn("Wrong number format at [" + value2 + "]");
			
			return false;
		}
		
		boolean result = checkNumbers(d1, d2);

		if (log.isDebugEnabled()) {
			log.debug("Compare doubles [" + d1 + "] and [" + d2 + "] with result [" + result + "]");
		}

		return result;
	}
	
	protected Double asDouble(Object obj) throws NumberFormatException {
		if (obj == null) {
			return null;
		}
		
		Double numberValue;

		if (obj instanceof String) {
			final String s = (String) obj; 
			
			if (s.trim().length() == 0) {
				return null;
			}
			
			numberValue = Double.valueOf(s);
		} else if (obj instanceof Number) {
			numberValue = ((Number) obj).doubleValue();
		} else {
			throw new NumberFormatException();
		}
		
		return numberValue;
	}
}
