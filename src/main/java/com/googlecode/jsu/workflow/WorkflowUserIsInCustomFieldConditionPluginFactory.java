package com.googlecode.jsu.workflow;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author Anton Afanassiev
 */
public class WorkflowUserIsInCustomFieldConditionPluginFactory 
		extends AbstractWorkflowPluginFactory 
		implements WorkflowPluginConditionFactory {

	private final CustomFieldManager customFieldManager;

	public WorkflowUserIsInCustomFieldConditionPluginFactory(CustomFieldManager customFieldManager) {
		this.customFieldManager = customFieldManager;
	}

	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	
	protected void getVelocityParamsForInput(Map velocityParams) {
		velocityParams.put("val-fieldsList", customFieldManager.getCustomFieldObjects());
	}

	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	
	protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
		Map args = conditionDescriptor.getArgs();
		
		String sField = (String) args.get("fieldsList");
		
		Field field = null;
		
		try {
			field = WorkflowUtils.getFieldFromKey(sField);
		} catch (Exception e) {
		}
	
		if (field != null) {
			velocityParams.put("val-fieldSelected", field);
		}
	}


	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
		Map args = conditionDescriptor.getArgs();
		
		String sField = (String) args.get("fieldsList");

		Field field = null;
		
		try {
			field = WorkflowUtils.getFieldFromKey(sField);
		} catch (Exception e) {
		}
	
		if (field != null) {
			velocityParams.put("val-fieldSelected", field);
		} else {
			velocityParams.put("val-errorMessage", "Unable to find field '" + sField + "'");
		}
	}

	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	
	public Map getDescriptorParams(Map conditionParams) {
		Map params = new HashMap();
		
		try {
			String field = extractSingleParam(conditionParams, "fieldsList");
			
			params.put("fieldsList", field);
			
		} catch(IllegalArgumentException iae) {
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
}
