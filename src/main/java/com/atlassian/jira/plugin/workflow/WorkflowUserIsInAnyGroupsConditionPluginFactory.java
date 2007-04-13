package com.atlassian.jira.plugin.workflow;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.core.user.GroupUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This class defines the parameters available for User Is In Any Group Condition.
 *
 */
public class WorkflowUserIsInAnyGroupsConditionPluginFactory extends
AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory {
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
	 */
	protected void getVelocityParamsForInput(Map velocityParams) {
		Collection groups = GroupUtils.getGroups();
		
		velocityParams.put("val-groupsList", Collections.unmodifiableCollection(groups));
		velocityParams.put("val-splitter", WorkflowUtils.SPLITTER);
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		
		getVelocityParamsForInput(velocityParams);
		
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
		Map args = conditionDescriptor.getArgs();
		
		velocityParams.remove("val-groupsList");
		
		String strGroupsSelected = (String)args.get("hidGroupsList");
		Collection groupsSelected = WorkflowUtils.getGroups(strGroupsSelected, WorkflowUtils.SPLITTER);
		
		Collection groups = GroupUtils.getGroups();
		groups.removeAll(groupsSelected);
		
		velocityParams.put("val-groupsListSelected", Collections.unmodifiableCollection(groupsSelected));
		velocityParams.put("val-hidGroupsList", WorkflowUtils.getStringGroup(groupsSelected, WorkflowUtils.SPLITTER));
		velocityParams.put("val-groupsList", Collections.unmodifiableCollection(groups));
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
	 */
	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		
		ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
		Map args = conditionDescriptor.getArgs();
		
		String strGroupsSelected = (String)args.get("hidGroupsList");
		Collection groupsSelected = WorkflowUtils.getGroups(strGroupsSelected, WorkflowUtils.SPLITTER);
		
		velocityParams.put("val-groupsListSelected", Collections.unmodifiableCollection(groupsSelected));
		
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
	 */
	public Map getDescriptorParams(Map conditionParams) {
		Map params = new HashMap();
		
		try{
			String strGroupsSelected = extractSingleParam(conditionParams, "hidGroupsList");
			params.put("hidGroupsList", strGroupsSelected);
			
		}catch(IllegalArgumentException iae){
			// Aggregate so that Transitions can be added.
		}
		
		return params;
	}
	
}
