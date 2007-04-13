package com.atlassian.jira.plugin.workflow.function;

import java.util.Map;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.plugin.workflow.WorkflowClearFieldValueFunctionPluginFactory;
import com.atlassian.plugin.util.LogUtils;
import com.atlassian.plugin.util.WorkflowUtils;
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
		Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);
		FieldManager fldManager = ManagerFactory.getFieldManager();
		
		// It set the value to field.
		try {
			if (fldManager.isCustomField(field)) {
				CustomField customField = (CustomField) field;
				
				issueObject.setCustomFieldValue(customField, null);
			} else {
				GenericValue gvIssue = issueObject.getGenericValue();

				gvIssue.set(fieldKey, null);
			}
		} catch (Exception e) {
			LogUtils.getGeneral().error("Unable to set field - " + fieldKey, e);
		}
	}
}
