package com.atlassian.jira.plugin.workflow.validator;

import java.util.Collection;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.annotation.Argument;
import com.atlassian.jira.plugin.annotation.TransientVariable;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * This validator verifies that certain fields must be required at execution of a transition.
 * 
 * @author Gustavo Martin
 */
public class FieldsRequiredValidator extends GenericValidator {
	@TransientVariable
	private Issue issue;
	
	@Argument("hidFieldsList")
	private String fieldList;
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Validator#validate(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	protected void validate() throws InvalidInputException, WorkflowException {
		// It obtains the fields that are required for the transition.
		Collection<Field> fieldsSelected = WorkflowUtils.getFields(fieldList, WorkflowUtils.SPLITTER);
		
		for (Field field : fieldsSelected) {
			Object fieldValue = WorkflowUtils.getFieldValueFromIssue(issue, field);
			
			if ((fieldValue == null) && !CommonPluginUtils.isFieldHidden(issue, field)) {
				// Sets Exception message.
				this.setExceptionMessage(
						issue, field, 
						field.getName() + " is required.", 
						field.getName() + " is required. But it is not present on screen."
				);
			}
		}
	}
}
