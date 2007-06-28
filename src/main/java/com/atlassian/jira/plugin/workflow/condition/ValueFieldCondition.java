package com.atlassian.jira.plugin.workflow.condition;

import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.helpers.ComparisonManager;
import com.atlassian.jira.plugin.helpers.ComparisonType;
import com.atlassian.jira.plugin.helpers.ConditionManager;
import com.atlassian.jira.plugin.helpers.ConditionType;
import com.atlassian.jira.plugin.util.LogUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * @author Gustavo Martin
 * 
 * This condition evaluates if a given field fulfills the condition.
 *  
 */
public class ValueFieldCondition extends AbstractJiraCondition {
	private Issue issueObject = null;
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
		this.issueObject = getIssue(transientVars);
		
		String sField = (String) args.get("fieldsList");
		String fieldCondition = (String) args.get("conditionList");
		String comparisonType = (String) args.get("comparisonType");
		// fieldValue could be empty.
		String fieldValue = (String) args.get("fieldValue");

		Field field = WorkflowUtils.getFieldFromKey(sField);
		ComparisonType compType = ComparisonManager.getManager().getComparisonType(new Integer(comparisonType));
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(fieldCondition));
		
		return checkCondition(field, cond.getValue(), compType.getValue(), fieldValue);
	}
	
	/**
	 * @param field
	 * @param condition
	 * @param comparisonType
	 * @param fieldValue
	 * @return true if fulfills the condition, false otherwise.
	 * 
	 * This method make the evaluation properly this.
	 * If the comparisson is by String, only equal or different are valid options for condition.
	 */
	private boolean checkCondition(Field field, String condition, String comparisonType, String fieldValue) {
		boolean condOK = false;
		
		// If the comparisson is by String.
		if(comparisonType.equals(WorkflowUtils.COMPARISON_TYPE_STRING)){
			String originalValue = WorkflowUtils.getFieldValueFromIssueAsString(issueObject, field);
			
			condOK = originalValue.equals(fieldValue);
			
			if((condition.equals("="))){
				condOK = true && condOK;
			}else{
				if((condition.equals(WorkflowUtils.CONDITION_DIFFERENT))){
					condOK = true && !condOK;
				}else{
					condOK = false;
				}
			}
			
		}else{
			int comparison = -111;
			
			try{
				// Makes the comparison between the fields passed like parameters.
				if(comparisonType.equals(WorkflowUtils.COMPARISON_TYPE_NUMBER)){
					Double originalValue = Double.valueOf(WorkflowUtils.getFieldValueFromIssueAsString(issueObject, field));
					
					comparison = originalValue.compareTo(Double.valueOf(fieldValue));
				}
				
				if(comparisonType.equals(WorkflowUtils.COMPARISON_TYPE_DATE)){
					// Not implemented yet.
					//Timestamp originalValue = (Timestamp)WorkflowUtils.getFieldValueFromIssue(issueObject, field);
					
					//comparison = originalValue.compareTo(Timestamp.valueOf(fieldValue));
				}
			} catch (ClassCastException cce) {
				// It could be that you try to make a comparison type with wrong data types.
				// But the user does not receive notifications on the happened thing.
				LogUtils.getGeneral().error("Unable to compare fields", cce);
				comparison = -111;
			}
			
			// If there were no errors, verifies that the comparison between the fields 
			// is same that the condition passed like parameter.
			if(comparison!=-111){
				
				if ((comparison == 0) && ((condition.equals("<=")) || (condition.equals("=")) || (condition.equals(">="))))
					condOK = true;
				
				if ((comparison < 0) && ((condition.equals("<")) || (condition.equals("<="))))
					condOK = true;
				
				if ((comparison > 0) && ((condition.equals(">")) || (condition.equals(">="))))
					condOK = true;
				
				if ((comparison != 0) && (condition.equals(WorkflowUtils.CONDITION_DIFFERENT)))
					condOK = true;
				
			}
		}
		
		return condOK;
	}
}

