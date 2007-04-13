package com.atlassian.jira.plugin.workflow.validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author Gustavo Martin
 *
 * This validator verifies that certain fields must be required at execution of a transition.
 *  
 */
public class FieldsRequiredValidator implements Validator {
	private Issue issue = null;
	private InvalidInputException invIn = null;
	private WorkflowException invWork = null;
	private boolean isNew = false;
	private boolean hasViewScreen = false;
	
	public FieldsRequiredValidator() {
	}
	
	/**
	 * @param param
	 * @return
	 */
	public static ValidatorDescriptor makeDescriptor(String param) {
		if (param==null) {
			return makeDescriptor();
		}
		return makeDescriptor();
	}
	
	/**
	 * @return
	 */
	public static ValidatorDescriptor makeDescriptor()
	{
		ValidatorDescriptor permValidator = new ValidatorDescriptor();
		permValidator.setType("class");
		permValidator.getArgs().put("class.name", FieldsRequiredValidator.class.getName());
		return permValidator;
	}
	
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Validator#validate(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void validate(Map transientVars, Map args, PropertySet ps)
	throws InvalidInputException, WorkflowException {
		
		issue = (Issue) transientVars.get("issue");
		
		// Obtains if this transition has an screen associated.
		WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor)transientVars.get("descriptor");
		Integer actionId = (Integer)transientVars.get("actionId");
		ActionDescriptor actionDescriptor = workflowDescriptor.getAction(actionId.intValue());
		
		FieldScreen fieldScreen = WorkflowUtils.getFieldScreen(actionDescriptor);
		
		this.hasViewScreen = (fieldScreen != null); 
		
		// It obtains the fields that are required for the transition.
		String strFieldsSelected = (String)args.get("hidFieldsList");
		Collection fieldsSelected = WorkflowUtils.getFields(strFieldsSelected, WorkflowUtils.SPLITTER);
		
		Iterator it = fieldsSelected.iterator();
		
		while(it.hasNext()){
			Field field = (Field) it.next();
			
			Object fieldValue = WorkflowUtils.getFieldValueFromIssue(issue, field);
			
			if (fieldValue==null && !CommonPluginUtils.isFieldHidden(issue, field)) {
				// Sets Exception message.
				if (hasViewScreen) {
					if(CommonPluginUtils.isFieldOnScreen(issue, field, fieldScreen)){
						setError(field, field.getName() + " is required.");
					}else{
						setError(null, field.getName() + " is required. But it is not present on screen.");
					}
					
				}else{
					setError(null, field.getName() + " is required.");
				}
				
			}
		}
		
		if(isNew || !hasViewScreen){
			if (invWork!=null) throw invWork;
		}else{
			if (invIn!=null) throw invIn;
		}
	}
	
	/**
	 * @param field
	 * @param errmsg
	 * 
	 * Sets an Exception if not fullfit the condition.
	 */
	private void setError(Field field, String errmsg) {
		if(isNew || !hasViewScreen){
			invWork = new WorkflowException(errmsg);
		}else{
			if (field == null) {
				if (invIn == null)
					invIn = new InvalidInputException(errmsg);
				else
					invIn.addError(errmsg);
			} else {
				if (invIn == null)
					invIn = new InvalidInputException(field.getId(), errmsg);
				else
					invIn.addError(field.getId(), errmsg);
			}
		}
	}
}
