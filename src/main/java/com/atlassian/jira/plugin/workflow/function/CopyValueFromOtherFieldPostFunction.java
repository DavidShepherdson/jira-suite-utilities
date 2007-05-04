package com.atlassian.jira.plugin.workflow.function;

import java.util.Map;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.util.LogUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 *
 * This function copies the value from a field to another one.
 */
public class CopyValueFromOtherFieldPostFunction implements FunctionProvider {
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		MutableIssue issueObject = (MutableIssue) transientVars.get("issue");
		
		String sourceFieldKey = (String) args.get("sourceField");
		String destinationFieldKey = (String) args.get("destinationField");
		
		Field fieldFrom = (Field) WorkflowUtils.getFieldFromKey(sourceFieldKey);

		try {
			// It gives the value from the source field.
			Object sourceValue = WorkflowUtils.getFieldValueFromIssue(issueObject, fieldFrom);
		
			// It set the value to field.
			WorkflowUtils.setFieldValue(issueObject, destinationFieldKey, sourceValue);
		} catch (Exception e) {
			final Field destField = (Field) WorkflowUtils.getFieldFromKey(destinationFieldKey);
			final String message = "Unable to copy value from " + fieldFrom.getName() + " to " + destField.getName();
			
			LogUtils.getGeneral().error(message, e);
			
			throw new WorkflowException(message);
		}
	}
}
