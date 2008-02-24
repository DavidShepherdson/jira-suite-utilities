package com.googlecode.jsu.workflow.function;

import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.googlecode.jsu.util.LogUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 *
 * This function copies the value from a field to another one.
 */
public class CopyValueFromOtherFieldPostFunction extends AbstractJiraFunctionProvider {
	private final Logger log = LogUtils.getGeneral();
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.FunctionProvider#execute(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void execute(
			@SuppressWarnings("unchecked") Map transientVars, 
			@SuppressWarnings("unchecked") Map args,
			PropertySet ps
	) throws WorkflowException {
		String fieldFromKey = (String) args.get("sourceField");
		String fieldToKey = (String) args.get("destinationField");
		
		Field fieldFrom = (Field) WorkflowUtils.getFieldFromKey(fieldFromKey);

		try {
			MutableIssue issue = getIssue(transientVars);
			
			// It gives the value from the source field.
			Object sourceValue = WorkflowUtils.getFieldValueFromIssue(issue, fieldFrom);

			if (log.isDebugEnabled()) {
				log.debug(
						String.format(
								"Copying value \"%s\" from issue %s field [%s] to field [%s] ", 
								sourceValue.toString(), issue.getKey(), 
								fieldFromKey, fieldToKey
						)
				);
			}
			
			// It set the value to field.
			WorkflowUtils.setFieldValue(issue, fieldToKey, sourceValue);

			if (log.isDebugEnabled()) {
				log.debug("Value was successfully copied");
			}
		} catch (Exception e) {
			Field fieldTo = (Field) WorkflowUtils.getFieldFromKey(fieldToKey);
			String fieldFromName = (fieldFrom != null) ? fieldFrom.getName() : fieldFromKey;
			String fieldToName = (fieldTo != null) ? fieldTo.getName() : fieldToKey;
			String message = "Unable to copy value from " + fieldFromName + " to " + fieldToName;
			
			log.error(message, e);
			
			throw new WorkflowException(message);
		}
	}
}
