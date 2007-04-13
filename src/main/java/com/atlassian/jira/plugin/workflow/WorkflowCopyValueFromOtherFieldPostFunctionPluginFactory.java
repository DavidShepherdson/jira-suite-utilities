package com.atlassian.jira.plugin.workflow;

import static com.atlassian.plugin.util.WorkflowFactoryUtils.getFieldByName;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.plugin.util.CommonPluginUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;

/**
 * This class defines the parameters available for Copy Value From Other Field Post Function.
 * 
 * @author Gustavo Martin.
 */
public class WorkflowCopyValueFromOtherFieldPostFunctionPluginFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List<Field> sourceFields = CommonPluginUtils.getCopyFromFields();
		List<Field> destinationFields = CommonPluginUtils.getCopyToFields();
		
		velocityParams.put("val-sourceFieldsList", Collections.unmodifiableList(sourceFields));
		velocityParams.put("val-destinationFieldsList", Collections.unmodifiableList(destinationFields));
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		Field sourceFieldId = getFieldByName(descriptor, "sourceField");
		Field destinationField = getFieldByName(descriptor, "destinationField");
		
		velocityParams.put("val-sourceFieldSelected", sourceFieldId);
		velocityParams.put("val-destinationFieldSelected", destinationField);
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		Field sourceFieldId = getFieldByName(descriptor, "sourceField");
		Field destinationField = getFieldByName(descriptor, "destinationField");
		
		velocityParams.put("val-sourceFieldSelected", sourceFieldId);
		velocityParams.put("val-destinationFieldSelected", destinationField);
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map<String, String> getDescriptorParams(Map conditionParams) {
		Map<String, String> params = new HashMap<String, String>();
		
		try{
			String sourceField = extractSingleParam(conditionParams, "sourceFieldsList");
			String destinationField = extractSingleParam(conditionParams, "destinationFieldsList");
			
			params.put("sourceField", sourceField);
			params.put("destinationField", destinationField);
		} catch (IllegalArgumentException iae) {
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
}
