package com.googlecode.jsu.util;

import java.sql.Timestamp;

import org.apache.cactus.ServletTestCase;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import static com.googlecode.jsu.util.CommonPluginUtils.isIssueHasField;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class CommonPluginUtilsRemoteTest extends ServletTestCase {
	public void testGetAllFields() {
		CommonPluginUtils.getAllFields();
	}
	
	public void testNiceDate() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String niceDate = CommonPluginUtils.getNiceDate(timestamp);
		
		assertNotNull("Nice date must be not null", niceDate);
		assertTrue("Nice date must be not empty", niceDate.length() > 10);
	}
	
	/**
	 * This method is used for testing 'hidden' fields. Before run test you should create
	 * two projects with structure like this one:
	 * project1 - PRF
	 *  - PRF-1 issue
	 * project2 - PRS
	 *  - PRS-1 issue
	 * project3 - PRS
	 *  - PRT-1 issue
	 *  - Add field configuration scheme with hidden Custom_3
	 *  
	 * Add custom fields:
	 *  - Custom_1 - for project1
	 *  - Custom_2 - for project2
	 *  - Custom_3 - for all projects
	 * 
	 */
	public void testHiddenFields() {
		final IssueManager issueManager = ComponentManager.getInstance().getIssueManager();
		final CustomFieldManager fieldManager = ComponentManager.getInstance().getCustomFieldManager();
		
		MutableIssue firstIssue = issueManager.getIssueObject("PRF-1");
		MutableIssue secondIssue = issueManager.getIssueObject("PRS-1");
		
		// Checking access rights for custom field context 
		CustomField custom1 = fieldManager.getCustomFieldObjectByName("Custom_1");
		CustomField custom2 = fieldManager.getCustomFieldObjectByName("Custom_2");
		CustomField custom3 = fieldManager.getCustomFieldObjectByName("Custom_3");
		
		assertTrue("Custom_1 must be available for Project1", isIssueHasField(firstIssue, custom1));
		assertFalse("Custom_2 can't be available for Project1", isIssueHasField(firstIssue, custom2));
		assertTrue("Custom_3 must be available for Project1", isIssueHasField(firstIssue, custom3));
	
		assertFalse("Custom_1 can't be available for Project2", isIssueHasField(secondIssue, custom1));
		assertTrue("Custom_2 must be available for Project2", isIssueHasField(secondIssue, custom2));
		assertTrue("Custom_3 must be available for Project2", isIssueHasField(secondIssue, custom3));
		
		// Checking field configuration scheme
		MutableIssue thirdIssue = issueManager.getIssueObject("PRT-1");
		
		assertFalse("Custom_1 can't be available for Project3", isIssueHasField(thirdIssue, custom1));
		assertFalse("Custom_2 can't be available for Project3", isIssueHasField(thirdIssue, custom2));
		assertFalse("Custom_3 can't be available for Project3", isIssueHasField(thirdIssue, custom3));
	}
}
