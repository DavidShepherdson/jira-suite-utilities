package com.googlecode.jsu.workflow.condition;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.googlecode.jsu.helpers.ComparisonManager;
import com.googlecode.jsu.helpers.ComparisonType;
import com.googlecode.jsu.helpers.ConditionManager;
import com.googlecode.jsu.helpers.ConditionType;
import com.googlecode.jsu.util.LogUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;

/**
 * This condition evaluates if a given field fulfills the condition.
 * 
 * @author Gustavo Martin
 */
public class ValueFieldCondition extends AbstractJiraCondition {
	private final Logger log = Logger.getLogger(ValueFieldCondition.class);
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
		Issue issue = getIssue(transientVars);

		String sField = (String) args.get("fieldsList");
		String fieldCondition = (String) args.get("conditionList");
		String comparisonType = (String) args.get("comparisonType");
		// fieldValue could be empty.
		String fieldValue = (String) args.get("fieldValue");
		
		boolean result = false;
		
		try { 
			Field field = WorkflowUtils.getFieldFromKey(sField);
			ComparisonType compType = ComparisonManager.getManager().getComparisonType(new Integer(comparisonType));
			ConditionType cond = ConditionManager.getManager().getCondition(new Integer(fieldCondition));

			result = checkCondition(issue, field, cond.getValue(), compType.getValue(), fieldValue);
		} catch (Exception e) {
			log.error("Unable to check value for field '" + sField + "'", e);
		}
		
		return result;
	}

	/**
	 * This method make the evaluation properly this. If the comparison is by
	 * String, only equal or different are valid options for condition.
	 * 
	 * @param issue
	 * @param field
	 * @param condition
	 * @param comparisonType
	 * @param fieldValue
	 * @return true if fulfills the condition, false otherwise.
	 */
	private boolean checkCondition(Issue issue, Field field, String condition, String comparisonType, String fieldValue) {
		boolean condOK = false;

		// If the comparisson is by String.
		if (comparisonType.equals(WorkflowUtils.COMPARISON_TYPE_STRING)) {
			String originalValue = WorkflowUtils.getFieldValueFromIssueAsString(issue, field);

			condOK = originalValue.equals(fieldValue);

			if (condition.equals("=")) {
				condOK = true && condOK;
			} else {
				if (condition.equals(WorkflowUtils.CONDITION_DIFFERENT)) {
					condOK = true && !condOK;
				} else {
					condOK = false;
				}
			}
		} else if (comparisonType.equals(WorkflowUtils.COMPARISON_TYPE_NUMBER)) {
			int comparison = -111;

			Object value = WorkflowUtils.getFieldValueFromIssue(issue, field);

			try {
				if (fieldValue.trim().equals("")) {
					if (value == null) {
						comparison = 0;
					}
				} else {
					Double expectingValue = Double.valueOf(fieldValue);

					if (value != null) {
						Double numberValue;

						if (value instanceof String) {
							numberValue = Double.valueOf((String) value);
						} else if (value instanceof Number) {
							numberValue = ((Number) value).doubleValue();
						} else {
							throw new NumberFormatException();
						}

						comparison = expectingValue.compareTo(numberValue);
					}
				}
			} catch (ClassCastException cce) {
				// It could be that you try to make a comparison type with wrong data types.
				// But the user does not receive notifications on the happened thing.
				LogUtils.getGeneral().error("Unable to compare fields", cce);
				comparison = -111;
			} catch (NumberFormatException nfe) {
				LogUtils.getGeneral().error("Wrong number format", nfe);
				comparison = -111;
			}

			// If there were no errors, verifies that the comparison between the fields 
			// is same that the condition passed like parameter.
			if (comparison != (-111)) {

				if ((comparison == 0) && ((condition.equals("<=")) || (condition.equals("=")) || (condition.equals(">="))))
					condOK = true;

				if ((comparison < 0) && ((condition.equals("<")) || (condition.equals("<="))))
					condOK = true;

				if ((comparison > 0) && ((condition.equals(">")) || (condition.equals(">="))))
					condOK = true;

				if ((comparison != 0) && (condition.equals(WorkflowUtils.CONDITION_DIFFERENT)))
					condOK = true;

			}

		} else if (comparisonType.equals(WorkflowUtils.COMPARISON_TYPE_DATE)) {
			// Not implemented yet.
			//Timestamp originalValue = (Timestamp)WorkflowUtils.getFieldValueFromIssue(issueObject, field);

			//comparison = originalValue.compareTo(Timestamp.valueOf(fieldValue));
		}

		return condOK;
	}
}
