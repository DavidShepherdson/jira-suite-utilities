package com.atlassian.jira.plugin.workflow.validator;

import java.util.Collection;
import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.ValidatorErrorsBuilder;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * This validator verifies that certain fields must be required at execution of a transition.
 * 
 * @author Gustavo Martin
 */
public class FieldsRequiredValidator implements Validator {
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Validator#validate(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException, WorkflowException {
		Issue issue = (Issue) transientVars.get("issue");
		
		// Obtains if this transition has an screen associated.
		WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor)transientVars.get("descriptor");
		Integer actionId = (Integer) transientVars.get("actionId");
		ActionDescriptor actionDescriptor = workflowDescriptor.getAction(actionId.intValue());
		FieldScreen fieldScreen = WorkflowUtils.getFieldScreen(actionDescriptor);
		
		boolean hasViewScreen = (fieldScreen != null); 
		
		ValidatorErrorsBuilder veb = new ValidatorErrorsBuilder(hasViewScreen);
		
		// It obtains the fields that are required for the transition.
		String strFieldsSelected = (String) args.get("hidFieldsList");
		Collection<Field> fieldsSelected = WorkflowUtils.getFields(strFieldsSelected, WorkflowUtils.SPLITTER);
		
		for (Field field : fieldsSelected) {
			Object fieldValue = WorkflowUtils.getFieldValueFromIssue(issue, field);
			
			if ((fieldValue == null) && !CommonPluginUtils.isFieldHidden(issue, field)) {
				// Sets Exception message.
				if (hasViewScreen) {
					if (CommonPluginUtils.isFieldOnScreen(issue, field, fieldScreen)) {
						veb.addError(field, field.getName() + " is required.");
					} else {
						veb.addError(field.getName() + " is required. But it is not present on screen.");
					}
				} else {
					veb.addError(field.getName() + " is required.");
				}
			}
		}
		
		veb.process();
	}
}
