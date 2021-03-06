package com.googlecode.jsu.workflow.validator;

import static com.googlecode.jsu.helpers.ConditionCheckerFactory.EQUAL;
import static com.googlecode.jsu.helpers.ConditionCheckerFactory.STRING;
import static com.googlecode.jsu.util.CommonPluginUtils.isIssueHasField;
import static com.googlecode.jsu.util.ComponentUtils.getComponent;
import static com.googlecode.jsu.workflow.WorkflowFieldsRequiredValidatorPluginFactory.SELECTED_FIELDS;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.googlecode.jsu.annotation.Argument;
import com.googlecode.jsu.helpers.ConditionChecker;
import com.googlecode.jsu.helpers.ConditionCheckerFactory;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * This validator verifies that certain fields must be required at execution of a transition.
 *
 * @author Gustavo Martin
 */
public class FieldsRequiredValidator extends GenericValidator {
    private static final Logger log = Logger.getLogger(FieldsRequiredValidator.class);

    @Argument(SELECTED_FIELDS)
    private String fieldList;

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.Validator#validate(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
     */
    protected void validate() throws InvalidInputException, WorkflowException {
        final ConditionCheckerFactory conditionCheckerFactory = getComponent(ConditionCheckerFactory.class);
        final ConditionChecker checker = conditionCheckerFactory.getChecker(STRING, EQUAL);

        // It obtains the fields that are required for the transition.
        Collection<Field> fieldsSelected = WorkflowUtils.getFields(fieldList, WorkflowUtils.SPLITTER);
        final Issue issue = getIssue();
        String issueKey = issue.getKey();

        if (issueKey == null) {
            issueKey = "'New issue'";
        }

        if (log.isDebugEnabled()) {
            log.debug(issueKey + ": Found " + fieldsSelected.size() + " fields for validation");
        }

        for (Field field : fieldsSelected) {
            if (isIssueHasField(issue, field)) {
                Object fieldValue = WorkflowUtils.getFieldValueFromIssue(issue, field);

                if (log.isDebugEnabled()) {
                    log.debug(
                            issueKey + ": Field '" + field.getName() +
                            " - " +	field.getId() +
                            "' has value [" + fieldValue + "]"
                    );
                }

                if (checker.checkValues(fieldValue, null)) {
                    // Sets Exception message.
                    this.setExceptionMessage(
                            field,
                            field.getName() + " is required.",
                            field.getName() + " is required. But it is not present on screen."
                    );
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug(
                            issueKey + ": Field '" + field.getName() +
                            " - " +	field.getId() +
                            "' is not assigned for the issue"
                    );
                }
            }
        }
    }
}
