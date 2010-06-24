package com.googlecode.jsu.helpers.checkers;

import com.atlassian.jira.issue.IssueConstant;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;

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
		
		String result;
		
        if (object instanceof IssueConstant) {
        	result = ((IssueConstant) object).getName();
        } else if (object instanceof User) {
        	result = ((User) object).getName();
        } else if (object instanceof Group) {
        	result = ((Group) object).getName();
		} else {
			result = object.toString();
		}
		
		return result;
	}
}
