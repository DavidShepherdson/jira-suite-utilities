package com.googlecode.jsu.workflow.function;

import java.util.Map;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 *
 * This function copies the value from a field to another one.
 */
public class CopyValueFromOtherFieldPostFunction extends AbstractPreserveChangesPostFunction {
    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.function.AbstractPreserveChangesPostFunction#executeFunction(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet, com.atlassian.jira.issue.util.IssueChangeHolder)
     */
    @Override
    protected void executeFunction(
            Map<String, Object> transientVars, Map<String, String> args,
            PropertySet ps, IssueChangeHolder holder
    ) throws WorkflowException {
        String fieldFromKey = (String) args.get("sourceField");
        String fieldToKey = (String) args.get("destinationField");

        Field fieldFrom = (Field) WorkflowUtils.getFieldFromKey(fieldFromKey);
        Field fieldTo = (Field) WorkflowUtils.getFieldFromKey(fieldToKey);

        String fieldFromName = (fieldFrom != null) ? fieldFrom.getName() : fieldFromKey;
        String fieldToName = (fieldTo != null) ? fieldTo.getName() : fieldToKey;

        try {
            MutableIssue issue = getIssue(transientVars);

            // It gives the value from the source field.
            Object sourceValue = WorkflowUtils.getFieldValueFromIssue(issue, fieldFrom);

            if (log.isDebugEnabled()) {
                log.debug(
                        String.format(
                                "Copying value [%s] from issue %s field '%s' to '%s'",
                                sourceValue, issue.getKey(),
                                fieldFromName,
                                fieldToName
                        )
                );
            }

            // It set the value to field.
            WorkflowUtils.setFieldValue(issue, fieldToKey, sourceValue, holder);

            if (log.isDebugEnabled()) {
                log.debug("Value was successfully copied");
            }
        } catch (Exception e) {
            String message = String.format("Unable to copy value from '%s' to '%s'", fieldFromName, fieldToName);

            log.error(message, e);

            throw new WorkflowException(message);
        }
    }
}
