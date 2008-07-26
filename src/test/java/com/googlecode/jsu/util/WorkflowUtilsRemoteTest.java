package com.googlecode.jsu.util;

import org.apache.cactus.ServletTestCase;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class WorkflowUtilsRemoteTest extends ServletTestCase {
	/**
	 * Custom radio field must return null for 'None' value.
	 * Issue #42
	 * 
	 * For testing this issue are used issue 'PRF-1' and field 'Radio_1'
	 */
	public void testRadioNoneValue() {
		final IssueManager issueManager = ComponentManager.getInstance().getIssueManager();
		final CustomFieldManager fieldManager = ComponentManager.getInstance().getCustomFieldManager();

		MutableIssue firstIssue = issueManager.getIssueObject("PRF-1");
		CustomField radio1 = fieldManager.getCustomFieldObjectByName("Radio_1");
		
		assertNull("Value for radio button must be null", WorkflowUtils.getFieldValueFromIssue(firstIssue, radio1));
	}
}
