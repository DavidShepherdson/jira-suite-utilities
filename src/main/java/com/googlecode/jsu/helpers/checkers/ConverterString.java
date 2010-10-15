package com.googlecode.jsu.helpers.checkers;

import org.apache.commons.lang.StringUtils;

import com.atlassian.jira.issue.IssueConstant;
import com.opensymphony.user.Entity;

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
        } else if (object instanceof Entity) {
            result = ((Entity) object).getName();
        } else {
            result = object.toString();
        }

        if (StringUtils.isBlank(result)) {
            return null;
        }

        return result;
    }
}
