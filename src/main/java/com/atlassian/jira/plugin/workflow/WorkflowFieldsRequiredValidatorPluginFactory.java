package com.atlassian.jira.plugin.workflow;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This class defines the parameters available for Fields Required Validator.
 * 
 */
public class WorkflowFieldsRequiredValidatorPluginFactory extends AbstractWorkflowPluginFactory 
		implements WorkflowPluginValidatorFactory {
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List allFields = CommonPluginUtils.getRequirableFields();
		
		velocityParams.put("val-fieldsList", Collections.unmodifiableCollection(allFields));
		velocityParams.put("val-splitter", WorkflowUtils.SPLITTER);
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		velocityParams.remove("val-fieldsList");
		
		String strFieldsSelected = (String)args.get("hidFieldsList");
		Collection fieldsSelected = WorkflowUtils.getFields(strFieldsSelected, WorkflowUtils.SPLITTER);
		
		List allFields = CommonPluginUtils.getRequirableFields();
		allFields.removeAll(fieldsSelected);
		
		velocityParams.put("val-fieldsListSelected", Collections.unmodifiableCollection(fieldsSelected));
		velocityParams.put("val-hidFieldsList", WorkflowUtils.getStringField(fieldsSelected, WorkflowUtils.SPLITTER));
		velocityParams.put("val-fieldsList", Collections.unmodifiableCollection(allFields));
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		String strFieldsSelected = (String)args.get("hidFieldsList");
		Collection fieldsSelected = WorkflowUtils.getFields(strFieldsSelected, WorkflowUtils.SPLITTER);
		
		velocityParams.put("val-fieldsListSelected", Collections.unmodifiableCollection(fieldsSelected));
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map validatorParams) {
		Map params = new HashMap();
		
		try{
			String strFieldsSelected = extractSingleParam(validatorParams, "hidFieldsList");
			params.put("hidFieldsList", strFieldsSelected);
			
		}catch(IllegalArgumentException iae){
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
	
}
