package com.atlassian.jira.plugin.workflow.function;

import java.util.Map;

import com.atlassian.jira.plugin.util.LogUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.atlassian.jira.plugin.workflow.WorkflowClearFieldValueFunctionPluginFactory;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * This function clears field value.
 * 
 * @author Alexey Abashev
 */
public class ClearFieldValuePostFunction extends AbstractJiraFunctionProvider {
	@SuppressWarnings("unchecked")
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		String fieldKey = (String) args.get(WorkflowClearFieldValueFunctionPluginFactory.FIELD);
		
		// It set the value to field.
		try {
			WorkflowUtils.setFieldValue(getIssue(transientVars), fieldKey, null);
		} catch (Exception e) {
			LogUtils.getGeneral().error("Unable to set field - " + fieldKey, e);
		}
	}
}
