package com.atlassian.jira.plugin.workflow.function;

import java.util.Map;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 *
 * This function copies the value from a field to another one.
 * Cooming soon...
 *  
 */
public class CopyValueFromOtherFieldPostFunction implements FunctionProvider
{
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		MutableIssue issueObject = (MutableIssue) transientVars.get("issue");
		
		String sourceFieldKey = (String) args.get("sourceField");
		String destinationFieldKey = (String) args.get("destinationField");
		
		Field fieldFrom = (Field) WorkflowUtils.getFieldFromKey(sourceFieldKey);
		Field fieldTo = (Field) WorkflowUtils.getFieldFromKey(destinationFieldKey);
		
		// It gives the value from the source field.
		Object sourceValue = WorkflowUtils.getFieldValueFromIssueAsString(issueObject, fieldFrom);
		
		FieldManager fldManager = ManagerFactory.getFieldManager();
		
		// It set the value to field.
		try{
			if (fldManager.isCustomField(fieldTo)){
				CustomField customField = (CustomField) fieldTo;
				issueObject.setCustomFieldValue(customField, sourceValue);
				
			} else {
				GenericValue gvIssue = issueObject.getGenericValue();
				gvIssue.set(destinationFieldKey, sourceValue);
			}
		}catch(Exception cce){
			System.out.println("Desde: " + sourceFieldKey + "- Hacia: " + destinationFieldKey);
			cce.printStackTrace();
		}
		
	}
	
}
