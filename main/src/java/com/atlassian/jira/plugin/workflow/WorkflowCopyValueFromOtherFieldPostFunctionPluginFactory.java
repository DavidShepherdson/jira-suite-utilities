package com.atlassian.jira.plugin.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.plugin.util.CommonPluginUtils;
import com.atlassian.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This class defines the parameters available for Copy Value From Other Field Post Function.
 * Cooming soon...
 * 
 */
public class WorkflowCopyValueFromOtherFieldPostFunctionPluginFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List sourceFields = CommonPluginUtils.getCopyFromFields();
		List destinationFields = CommonPluginUtils.getCopyToFields();
		
		velocityParams.put("val-sourceFieldsList", Collections.unmodifiableList(sourceFields));
		velocityParams.put("val-destinationFieldsList", Collections.unmodifiableList(destinationFields));
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
		Map args = functionDescriptor.getArgs();
		
		String sourceFieldKey = (String) args.get("sourceField");
		String destinationFieldKey = (String) args.get("destinationField");
		
		Field sourceFieldId = (Field) WorkflowUtils.getFieldFromKey(sourceFieldKey);
		Field destinationField = (Field) WorkflowUtils.getFieldFromKey(destinationFieldKey);
		
		velocityParams.put("val-sourceFieldSelected", sourceFieldId);
		velocityParams.put("val-destinationFieldSelected", destinationField);
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
		Map args = functionDescriptor.getArgs();
		
		String sourceFieldKey = (String) args.get("sourceField");
		String destinationFieldKey = (String) args.get("destinationField");
		
		Field sourceFieldId = (Field) WorkflowUtils.getFieldFromKey(sourceFieldKey);
		Field destinationField = (Field) WorkflowUtils.getFieldFromKey(destinationFieldKey);
		
		velocityParams.put("val-sourceFieldSelected", sourceFieldId);
		velocityParams.put("val-destinationFieldSelected", destinationField);
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map conditionParams) {
		Map params = new HashMap();
		
		try{
			String sourceField = extractSingleParam(conditionParams, "sourceFieldsList");
			String destinationField = extractSingleParam(conditionParams, "destinationFieldsList");
			
			params.put("sourceField", sourceField);
			params.put("destinationField", destinationField);
			
		}catch(IllegalArgumentException iae){
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
	
}
