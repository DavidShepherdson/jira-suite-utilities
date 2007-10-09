package com.googlecode.jsu.transitionssummary.issuetabpanel;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.util.UtilMisc;

import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.plugin.issuetabpanel.AbstractIssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.googlecode.jsu.transitionssummary.TransitionSummary;
import com.opensymphony.user.EntityNotFoundException;

/**
 * @author Gustavo Martin
 * 
 * This is a valid Action, that it allows to visualize the Transition Summaries.
 */
public class TransitionSummaryAction extends AbstractIssueAction {
	protected final IssueTabPanelModuleDescriptor descriptor;
	protected List<TransitionSummary> tranSummaries;
	protected Timestamp timePerformed;
	
	/**
	 * @param issue current issue.
	 * @param remoteUser current user (logged in).
	 * @param tranSummaries List containing TransitionSummary objects.
	 * @param descriptor
	 */
	public TransitionSummaryAction(List<TransitionSummary> tranSummaries, IssueTabPanelModuleDescriptor descriptor){
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
	public List<TransitionSummary> getTransitions() {
		return tranSummaries;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getTimePerformed()
	 */
	public Date getTimePerformed() {
		return (Date) this.timePerformed;
	}
	
	/* (non-Javadoc)
	 * @see com.atlassian.jira.issue.action.IssueAction#getHtml(com.atlassian.jira.web.action.JiraWebActionSupport)
	 */
	public String getHtml(JiraWebActionSupport webAction) {
		Map params = UtilMisc.toMap("webAction", webAction, "action", this);
		
		return descriptor.getHtml("view", params);
	}
	
	protected void populateVelocityParams(Map params) {	
		params.put("action", this);
	}
	
	public boolean isUserExists(String username) {
		try {
			UserUtils.getUser(username);
		} catch (EntityNotFoundException e) {
			return false;
		}
		
		return true;
	}
}
