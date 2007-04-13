package com.atlassian.jira.plugin.workflow.function;

import java.util.Map;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.plugin.util.LogUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.atlassian.jira.plugin.workflow.WorkflowClearFieldValueFunctionPluginFactory;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

/**
 * This function clears field value.
 * 
 * @author Alexey Abashev
 */
public class ClearFieldValuePostFunction implements FunctionProvider {
	@SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		MutableIssue issueObject = (MutableIssue) transientVars.get("issue");
		String fieldKey = (String) args.get(WorkflowClearFieldValueFunctionPluginFactory.FIELD);
		
		// It set the value to field.
		try {
			WorkflowUtils.setFieldValue(issueObject, fieldKey, null);
		} catch (Exception e) {
			LogUtils.getGeneral().error("Unable to set field - " + fieldKey, e);
		}
	}
}
