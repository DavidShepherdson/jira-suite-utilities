package com.atlassian.jira.plugin.workflow.function;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutStorageException;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.LogUtils;
import com.atlassian.jira.util.map.EasyMap;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.util.TextUtils;
import com.opensymphony.workflow.WorkflowException;

/**
 * Class related to the execution of the plugin.
 * 
 * @author Cristiane Fontana
 * @version 1.0
 *
 */
public class UpdateIssueCustomFieldPostFunction extends AbstractJiraFunctionProvider {
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		String fieldName = (String) args.get("field.name");
		String fieldValue = (String) args.get("field.value");
		
		if (fieldValue != null && "null".equals(fieldValue)) {
			fieldValue = null;
		}
		
		if (TextUtils.stringSet((String) args.get("field.type"))) {
			LogUtils.getGeneral().debug(
					"There is no need to specify the field type in this version of JIRA. Remove the 'field.type' argument from the functions arguments."
			);
		}

		processField(getIssue(transientVars), fieldName, fieldValue);
	}

	private void processField(MutableIssue issue, String fieldName, String fieldValue) throws WorkflowException {
		FieldManager fieldManager = ManagerFactory.getFieldManager();
		IssueChangeHolder changeHolder = new DefaultIssueChangeHolder();

		CustomField field = fieldManager.getCustomField(fieldName);
		Map params = EasyMap.build(field.getId(), new String[] { fieldValue });
		Map fieldValuesHolder = new HashMap();
		field.populateFromParams(fieldValuesHolder, params);

		FieldLayoutItem fieldLayoutItem;

		try {
			fieldLayoutItem	= CommonPluginUtils.getFieldLayoutItem(issue, field);
		} catch (FieldLayoutStorageException e) {
			String msg = "GenerateChangeHistory is unable to resolve a field layout item for " + field.getName();

			LogUtils.getGeneral().error(msg, e);
			
			throw new WorkflowException(msg);
		}

		field.updateIssue(fieldLayoutItem, issue, fieldValuesHolder);

		if (issue.getModifiedFields().containsKey(field.getId())) {
			field.updateValue(
					fieldLayoutItem, 
					issue, 
					(ModifiedValue) issue.getModifiedFields().get(field.getId()), 
					changeHolder
			);

			// Ensure the field is not modified by other workflow functions
			issue.getModifiedFields().remove(field.getId());
		}
	}
}
