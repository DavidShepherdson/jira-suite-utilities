package com.atlassian.jira.plugin.transitionssummary.issuetabpanel;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericValue;
import org.ofbiz.core.util.UtilMisc;
import com.atlassian.jira.issue.Issue;
//import com.atlassian.jira.issue.action.AbstractGVIssueAction;  // Corrected for 3.7
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.opensymphony.user.User;

/**
 * @author Gustavo Martin
 * 
 * This is a valid Action, that it allows to visualize the Transition Summaries.
 *  
 */
public class TransitionSummaryAction extends AbstractIssueAction{
	
	protected final IssueTabPanelModuleDescriptor descriptor;
	protected List tranSummaries;
	protected Timestamp timePerformed;
	
	/**
	 * @param issue current issue.
	 * @param remoteUser current user (logged in).
	 * @param tranSummaries List containing TransitionSummary objects.
	 * @param descriptor
	 */
	public TransitionSummaryAction(List tranSummaries, IssueTabPanelModuleDescriptor descriptor){
		super(descriptor);
		this.tranSummaries = tranSummaries;
		this.descriptor = descriptor;
		this.timePerformed = new Timestamp(Calendar.getInstance().getTimeInMillis());
	}
	
	/**
	 * @return a List
	 * 
	 * It allows Velocity to obtain the List of Transition Summaries.
	 */
	public List getTransitions() {
		return tranSummaries;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.AbstractIssueAction#getTemplateName()
	 */
	//protected String getTemplateName() {
		//return null;
	//}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getType()
	 */
	//public String getType() {
		//return "plugin.transitionssummary.transitionsummary";
	//}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getUsername()
	 */
	//public String getUsername() {
		//return "";
	//}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getTimePerformed()
	 */
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getTimePerformed()
	 */
	public Date getTimePerformed() {
		return (Date)this.timePerformed;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getHtml(com.atlassian.jira.web.action.JiraWebActionSupport)
	 */
	public String getHtml(JiraWebActionSupport webAction)
	{
		Map params = UtilMisc.toMap("webAction", webAction, "action", this);
		return descriptor.getHtml("view", params);
	}
	
	protected void populateVelocityParams(Map params)
	{	
		//System.out.println("Call to populateVelocityParams"); // Debug output
		params.put("action", this);
	}
}
