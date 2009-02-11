package com.googlecode.jsu.workflow.function;

import static com.googlecode.jsu.workflow.WorkflowUpdateIssueCustomFieldFunctionPluginFactory.TARGET_FIELD_NAME;
import static com.googlecode.jsu.workflow.WorkflowUpdateIssueCustomFieldFunctionPluginFactory.TARGET_FIELD_VALUE;

import java.util.Map;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.User;
import com.opensymphony.workflow.WorkflowException;

/**
 * Class related to the execution of the plugin.
 * 
 * @author Cristiane Fontana
 * @version 1.0
 *
 */
public class UpdateIssueCustomFieldPostFunction extends AbstractPreserveChangesPostFunction {
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.function.AbstractPreserveChangesPostFunction#executeFunction(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet, com.atlassian.jira.issue.util.IssueChangeHolder)
	 */
	@Override
	protected void executeFunction(
			Map<String, Object> transientVars, Map<String, String> args, 
			PropertySet ps, IssueChangeHolder holder
	) throws WorkflowException {
		String fieldKey = (String) args.get(TARGET_FIELD_NAME);

		final Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);
		final String fieldName = (field != null) ? field.getName() : "null";

		String fieldValue = (String) args.get(TARGET_FIELD_VALUE);
		
		if ((fieldValue != null) && ("null".equals(fieldValue))) {
			fieldValue = null;
		}

		if (fieldValue.equals("%%CURRENT_USER%%")) {
            try {
                User currentUser = getCaller(transientVars, args);

    			fieldValue = currentUser.toString();
			} catch (Exception e) {
				log.error("Unable to find caller for function", e);
			}
		}
		
		MutableIssue issue = null;

		try {
			issue = getIssue(transientVars);

			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Updating custom field '%s - %s' in issue [%s] with value [%s]",
						fieldKey, fieldName, issue.getKey(), fieldValue
				));
			}

			WorkflowUtils.setFieldValue(issue, fieldKey, fieldValue, holder);
		} catch (Exception e) {
			final String message = String.format(
					"Unable to update custom field '%s - %s' in issue [%s]",
					fieldKey, fieldName, (issue != null) ? issue.getKey() : "null"
			);
			
			log.error(message, e);
			
			throw new WorkflowException(message);
		}
	}
}
