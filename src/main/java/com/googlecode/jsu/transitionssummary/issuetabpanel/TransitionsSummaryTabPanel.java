package com.googlecode.jsu.transitionssummary.issuetabpanel;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.tabpanels.GenericMessageAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueAction;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanel;
import com.atlassian.jira.plugin.issuetabpanel.IssueTabPanelModuleDescriptor;
import com.googlecode.jsu.transitionssummary.TransitionSummary;
import com.googlecode.jsu.transitionssummary.TransitionsManager;
import com.opensymphony.user.User;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * @author Gustavo Martin
 * 
 * It will be add a new Issue Tab Panel.
 *  
 */
public class TransitionsSummaryTabPanel implements IssueTabPanel {
	
	protected IssueTabPanelModuleDescriptor descriptor;
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.issuetabpanel.IssueTabPanel#init(com.googlecode.jsu.issuetabpanel.IssueTabPanelModuleDescriptor)
	 */
	public void init(IssueTabPanelModuleDescriptor descriptor) {
		this.descriptor = descriptor;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.issuetabpanel.IssueTabPanel#getActions(org.ofbiz.core.entity.GenericValue, com.opensymphony.user.User)
	 */
	public List getActions(Issue issue, User remoteUser) {
		List<IssueAction> retList = new ArrayList<IssueAction>();
		List<TransitionSummary> transitions = TransitionsManager.getTransitionSummary(issue);
		
		// Allway adds only one record to the tab. This is thus, because if there are transition sumeries,
		// it exposes the List with all Transition Summaries. Then, velocity will be in charge of render it properly.
		if (transitions == null || transitions.isEmpty()) {
			GenericMessageAction action = new GenericMessageAction("There aren't workflow transitions executed yet.");
			
			retList.add(action);
		} else {
			Collections.sort(transitions, new TransitionSummaryComparator());
			
			retList.add(new TransitionSummaryAction(transitions, descriptor));	
		}
		
		return retList;
	}
	
	/* (non-Javadoc)
	 * @see com.googlecode.jsu.issuetabpanel.IssueTabPanel#showPanel(org.ofbiz.core.entity.GenericValue)
	 */
	public boolean showPanel(Issue issue, User remoteUser) {
		return true;
	}
	
}
