package com.googlecode.jsu.helpers.checkers;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
class ConverterString implements ValueConverter {
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.helpers.checkers.ValueConverter#getComparable(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public Comparable getComparable(Object object) {
		if (object == null) {
			return null;
		}
		
		return object.toString();
	}
}
