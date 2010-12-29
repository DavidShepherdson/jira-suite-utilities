package com.googlecode.jsu.workflow.condition;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.workflow.condition.AbstractJiraCondition;
import com.googlecode.jsu.util.WorkflowUtils;
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
    private final Logger log = Logger.getLogger(UserIsInAnyGroupsCondition.class);

    /* (non-Javadoc)
     * @see com.opensymphony.workflow.Condition#passesCondition(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
     */
    public boolean passesCondition(Map transientVars, Map args, PropertySet ps) {
        boolean allowUser = false;

        try {
            // Obtains the current user.
            WorkflowContext context = (WorkflowContext) transientVars.get("context");
            String caller = context.getCaller();

            if (caller == null) {
                // User not logged in

                return false;
            }

            User userLogged = UserManager.getInstance().getUser(caller);

            // If there aren't groups selected, hidGroupsList is equal to "".
            // And groupsSelected will be an empty collection.
            String strGroupsSelected = (String) args.get("hidGroupsList");
            Collection groupsSelected = WorkflowUtils.getGroups(strGroupsSelected, WorkflowUtils.SPLITTER);

            Iterator<Group> it = groupsSelected.iterator();

            while (it.hasNext() && !allowUser){
                if (userLogged.inGroup(it.next())){
                    allowUser = true;
                }
            }
        } catch (EntityNotFoundException e) {
            log.error("Unable to find user from context", e);
        }

        return allowUser;
    }
}
