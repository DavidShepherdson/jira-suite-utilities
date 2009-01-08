package com.googlecode.jsu.workflow.function;

import java.util.Map;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.googlecode.jsu.util.WorkflowUtils;
import com.googlecode.jsu.workflow.WorkflowClearFieldValueFunctionPluginFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This function clears field value.
 * 
 * @author Alexey Abashev
 */
public class ClearFieldValuePostFunction extends AbstractPreserveChangesPostFunction {
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.function.AbstractPreserveChangesPostFunction#executeFunction(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet, com.atlassian.jira.issue.util.IssueChangeHolder)
	 */
	@Override
	protected void executeFunction(
			Map<String, Object> transientVars, Map<String, String> args, 
			PropertySet ps, IssueChangeHolder holder
	) throws WorkflowException {
		String fieldKey = (String) args.get(WorkflowClearFieldValueFunctionPluginFactory.FIELD);
		Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);

		final String fieldName = (field != null) ? field.getName() : "null";
		
		// It set the value to field.
		try {
			MutableIssue issue = getIssue(transientVars);

			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"Clean field '%s - %s' in the issue [%s]",
						fieldKey, fieldName, issue.getKey()
				));
			}

			WorkflowUtils.setFieldValue(issue, fieldKey, null, holder);
		} catch (Exception e) {
			String message = "Unable to clean field - '" + fieldKey + " - " + fieldName + "'";
			
			log.error(message, e);
			
			throw new WorkflowException(message);
		}
	}
}
