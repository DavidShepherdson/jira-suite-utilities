package com.googlecode.jsu.workflow;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory;
import com.googlecode.jsu.helpers.ConditionManager;
import com.googlecode.jsu.helpers.ConditionType;
import com.googlecode.jsu.helpers.YesNoManager;
import com.googlecode.jsu.helpers.YesNoType;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This class defines the parameters available for Date Compare Validator.
 * 
 */
public class WorkflowDateCompareValidatorPluginFactory extends
AbstractWorkflowPluginFactory implements WorkflowPluginValidatorFactory {
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		List allDateFiels = CommonPluginUtils.getAllDateFields();
		List booleanList = YesNoManager.getManager().getAllOptions();
		List conditionList = ConditionManager.getManager().getAllConditions();
		
		velocityParams.put("val-date1FieldsList", Collections.unmodifiableList(allDateFiels));
		velocityParams.put("val-date2FieldsList", Collections.unmodifiableList(allDateFiels));
		velocityParams.put("val-conditionList", Collections.unmodifiableList(conditionList));
		velocityParams.put("val-includeTime", Collections.unmodifiableList(booleanList));
		
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		String date1 = (String) args.get("date1Selected");
		String date2 = (String) args.get("date2Selected");
		String condition = (String) args.get("conditionSelected");
		String includeTime = (String) args.get("includeTimeSelected");
		
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(condition));
		YesNoType ynTime = YesNoManager.getManager().getOption(new Integer(includeTime));
		
		velocityParams.put("val-date1Selected", WorkflowUtils.getFieldFromKey(date1));
		velocityParams.put("val-date2Selected", WorkflowUtils.getFieldFromKey(date2));
		velocityParams.put("val-conditionSelected", cond);
		velocityParams.put("val-includeTimeSelected", ynTime);
		
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		ValidatorDescriptor validatorDescriptor = (ValidatorDescriptor) descriptor;
		Map args = validatorDescriptor.getArgs();
		
		String date1 = (String) args.get("date1Selected");
		String date2 = (String) args.get("date2Selected");
		String condition = (String) args.get("conditionSelected");
		String includeTime = (String) args.get("includeTimeSelected");
		
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(condition));
		YesNoType ynTime = YesNoManager.getManager().getOption(new Integer(includeTime));
		
		velocityParams.put("val-date1Selected", WorkflowUtils.getFieldFromKey(date1));
		velocityParams.put("val-date2Selected", WorkflowUtils.getFieldFromKey(date2));
		velocityParams.put("val-conditionSelected", cond);
		velocityParams.put("val-includeTimeSelected", ynTime);
		
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map validatorParams) {
		Map params = new HashMap();
		
		try{
			String date1 = extractSingleParam(validatorParams, "date1FieldsList");
			String date2 = extractSingleParam(validatorParams, "date2FieldsList");
			String condition = extractSingleParam(validatorParams, "conditionList");
			String includeTime = extractSingleParam(validatorParams, "includeTimeList");
			
			params.put("date1Selected", date1);
			params.put("date2Selected", date2);
			params.put("conditionSelected", condition);
			params.put("includeTimeSelected", includeTime);
			
		}catch(IllegalArgumentException iae){
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
	
}
