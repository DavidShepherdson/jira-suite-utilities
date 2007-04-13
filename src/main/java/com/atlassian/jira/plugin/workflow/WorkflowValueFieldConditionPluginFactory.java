package com.atlassian.jira.plugin.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.helpers.ComparisonManager;
import com.atlassian.jira.plugin.helpers.ComparisonType;
import com.atlassian.jira.plugin.helpers.ConditionType;
import com.atlassian.jira.plugin.helpers.ConditionManager;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This class defines the parameters available for Value Field Condition.
 * 
 */
public class WorkflowValueFieldConditionPluginFactory extends
AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory {
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List fields = CommonPluginUtils.getValueFieldConditionFields();
		List conditionList = ConditionManager.getManager().getAllConditions();
		List comparisonList = ComparisonManager.getManager().getAllComparisonType();
		
		velocityParams.put("val-fieldsList", Collections.unmodifiableList(fields));
		velocityParams.put("val-conditionList", Collections.unmodifiableList(conditionList));
		velocityParams.put("val-comparisonList", Collections.unmodifiableList(comparisonList));
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
		Map args = conditionDescriptor.getArgs();
		
		String sField = (String) args.get("fieldsList");
		String fieldCondition = (String) args.get("conditionList");
		String comparisonType = (String) args.get("comparisonType");
		String fieldValue = (String) args.get("fieldValue");
		
		Field field = WorkflowUtils.getFieldFromKey(sField);
		ComparisonType compType = ComparisonManager.getManager().getComparisonType(new Integer(comparisonType));
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(fieldCondition));
		
		velocityParams.put("val-fieldSelected", field);
		velocityParams.put("val-conditionSelected", cond);
		velocityParams.put("val-comparisonTypeSelected", compType);
		velocityParams.put("val-fieldValue", fieldValue);
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
		Map args = conditionDescriptor.getArgs();
		
		String sField = (String) args.get("fieldsList");
		String fieldCondition = (String) args.get("conditionList");
		String comparisonType = (String) args.get("comparisonType");
		String fieldValue = (String) args.get("fieldValue");
		
		Field field = WorkflowUtils.getFieldFromKey(sField);
		ComparisonType compType = ComparisonManager.getManager().getComparisonType(new Integer(comparisonType));
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(fieldCondition));
		
		velocityParams.put("val-fieldSelected", field);
		velocityParams.put("val-conditionSelected", cond);
		velocityParams.put("val-comparisonTypeSelected", compType);
		velocityParams.put("val-fieldValue", fieldValue);
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map conditionParams) {
		Map params = new HashMap();
		
		try{
			String field = extractSingleParam(conditionParams, "fieldsList");
			String fieldCondition = extractSingleParam(conditionParams, "conditionList");
			String comparisonType = extractSingleParam(conditionParams, "comparisonType");
			String fieldValue = extractSingleParam(conditionParams, "fieldValue");
			
			params.put("fieldsList", field);
			params.put("conditionList", fieldCondition);
			params.put("comparisonType", comparisonType);
			params.put("fieldValue", fieldValue);
			
		}catch(IllegalArgumentException iae){
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
	
}
