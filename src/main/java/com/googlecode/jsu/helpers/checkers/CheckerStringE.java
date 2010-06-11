package com.googlecode.jsu.helpers.checkers;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class CheckerStringE extends AbstractStringChecker {
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.helpers.checkers.AbstractStringChecker#checkStrings(java.lang.String, java.lang.String)
	 */
	@Override
	protected boolean checkStrings(String str1, String str2) {
		if (str1 == null) {
			return (str2 == null);
		}
		
		return str1.equals(str2);
	}
}
