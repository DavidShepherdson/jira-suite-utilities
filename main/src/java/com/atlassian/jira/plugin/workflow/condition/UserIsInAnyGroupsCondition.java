package com.atlassian.jira.plugin.workflow.condition;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.atlassian.plugin.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;
import com.opensymphony.user.UserManager;
import com.opensymphony.workflow.WorkflowContext;

/**
 * @author Gustavo Martin
 * 
 * This Condition validates if the current user is in any of the selected groups.
 *  
 */
public class UserIsInAnyGroupsCondition extends AbstractJiraCondition {
	
	Issue issueObject = null;
	
	public UserIsInAnyGroupsCondition() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
		
		issueObject = (Issue) transientVars.get("issue");
		
		boolean allowUser = false; 
		
		try {
			// Obtains the current user.
			WorkflowContext context = (WorkflowContext) transientVars.get("context");
			User userLogged = UserManager.getInstance().getUser(context.getCaller());
			
			// If there aren't groups selected, hidGroupsList is equal to "".
			// And groupsSelected will be an empty collection.
			String strGroupsSelected = (String)args.get("hidGroupsList");
			Collection groupsSelected = WorkflowUtils.getGroups(strGroupsSelected, WorkflowUtils.SPLITTER);
			
			Iterator it = groupsSelected.iterator();
			while(it.hasNext() && !allowUser){
				if(userLogged.inGroup((Group) it.next())){
					allowUser = true;
				}
			}
			
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		
		return allowUser;		
		
	}
	
}
