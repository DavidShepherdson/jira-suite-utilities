package com.atlassian.jira.plugin.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.plugin.util.CommonPluginUtils;
import com.atlassian.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This class defines the parameters available for Windows Date Validator.
 * 
 */
public class WorkflowWindowsDateValidatorPluginFactory extends
AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List allDateFiels = CommonPluginUtils.getAllDateFields();
		
		velocityParams.put("val-date1FieldsList", Collections.unmodifiableList(allDateFiels));
		velocityParams.put("val-date2FieldsList", Collections.unmodifiableList(allDateFiels));
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		String date1 = (String) args.get("date1Selected");
		String date2 = (String) args.get("date2Selected");
		String windowsDays = (String) args.get("windowsDays");
		
		velocityParams.put("val-date1Selected", WorkflowUtils.getFieldFromKey(date1));
		velocityParams.put("val-date2Selected", WorkflowUtils.getFieldFromKey(date2));
		velocityParams.put("val-windowsDays", windowsDays);
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		String date1 = (String) args.get("date1Selected");
		String date2 = (String) args.get("date2Selected");
		String windowsDays = (String) args.get("windowsDays");
		
		velocityParams.put("val-date1Selected", WorkflowUtils.getFieldFromKey(date1));
		velocityParams.put("val-date2Selected", WorkflowUtils.getFieldFromKey(date2));
		velocityParams.put("val-windowsDays", windowsDays);
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map validatorParams) {
		Map params = new HashMap();
		
		try{
			String date1 = extractSingleParam(validatorParams, "date1FieldsList");
			String date2 = extractSingleParam(validatorParams, "date2FieldsList");
			String windowsDays = extractSingleParam(validatorParams, "windowsDays");
			
			params.put("date1Selected", date1);
			params.put("date2Selected", date2);
			params.put("windowsDays", windowsDays);
			
		}catch(IllegalArgumentException iae){
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
	
}
