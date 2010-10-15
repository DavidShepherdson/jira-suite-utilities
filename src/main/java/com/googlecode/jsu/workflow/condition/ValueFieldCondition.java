package com.googlecode.jsu.workflow.condition;

import static com.googlecode.jsu.util.ComponentUtils.getComponent;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.googlecode.jsu.helpers.ComparisonType;
import com.googlecode.jsu.helpers.ConditionCheckerFactory;
import com.googlecode.jsu.helpers.ConditionType;
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
    public boolean passesCondition(
            @SuppressWarnings("rawtypes") Map transientVars,
            @SuppressWarnings("rawtypes") Map args,
            PropertySet ps
    ) {
        final ConditionCheckerFactory conditionCheckerFactory = getComponent(ConditionCheckerFactory.class);
        final Issue issue = getIssue(transientVars);

        String fieldId = (String) args.get("fieldsList");
        String valueForCompare = (String) args.get("fieldValue");

        ComparisonType comparison = conditionCheckerFactory.findComparisonById((String) args.get("comparisonType"));
        ConditionType condition = conditionCheckerFactory.findConditionById((String) args.get("conditionList"));

        boolean result = false;

        try {
            Field field = WorkflowUtils.getFieldFromKey(fieldId);
            Object fieldValue = WorkflowUtils.getFieldValueFromIssue(issue, field);

            result = conditionCheckerFactory.
                    getChecker(comparison, condition).
                    checkValues(fieldValue, valueForCompare);

            if (log.isDebugEnabled()) {
                log.debug(
                        "Comparing field '" + fieldId +
                        "': [" + fieldValue + "]" +
                        condition.getValue() +
                        "[" + valueForCompare + "] as " +
                        comparison.getValue() + " = " + result
                );
            }
        } catch (Exception e) {
            log.error("Unable to compare values for field '" + fieldId + "'", e);
        }

        return result;
    }
}
