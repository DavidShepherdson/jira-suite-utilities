package com.googlecode.jsu.workflow.function;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.atlassian.jira.issue.history.ChangeItemBean;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.util.IssueChangeHolder;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

/**
 * Abstract post-function with transparent change tracking.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
abstract class AbstractPreserveChangesPostFunction extends AbstractJiraFunctionProvider {
	private static final String CHANGE_ITEMS = "changeItems";
	
	protected final Logger log = Logger.getLogger(this.getClass());
	
	/**
	 * Mirror for execute method but with holder for changes
	 * 
	 * @param transientVars
	 * @param args
	 * @param ps
	 * @param holder
	 * @throws WorkflowException
	 */
	protected abstract void executeFunction(
			Map<String, Object> transientVars, Map<String, String> args, 
			PropertySet ps, IssueChangeHolder holder
	) throws WorkflowException;
	
	@SuppressWarnings("unchecked")
	public final void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
		IssueChangeHolder holder = createChangeHolder(transientVars);
		
		try {
			executeFunction(transientVars, args, ps, holder);
		} finally {
			releaseChangeHolder(holder, transientVars);
		}
	}
	
	/**
	 * Create new holder with changes from transient vars
	 * @param transientVars
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private IssueChangeHolder createChangeHolder(Map<String, Object> transientVars) {
		List<ChangeItemBean> changeItems = (List<ChangeItemBean>) transientVars.get(CHANGE_ITEMS);
		
        if (changeItems == null) {
            changeItems = new LinkedList<ChangeItemBean>();
        }

        if (log.isDebugEnabled()) {
			log.debug("Create new holder with items - " + changeItems.toString());
		}
        
        IssueChangeHolder holder = new DefaultIssueChangeHolder();
        
        holder.setChangeItems(changeItems);
        
        return holder;
	}
	
	/**
	 * Release holder for changes.
	 * @param holder
	 * @param transientVars
	 */
	@SuppressWarnings("unchecked")
	private void releaseChangeHolder(IssueChangeHolder holder, Map<String, Object> transientVars) {
		List<ChangeItemBean> items = holder.getChangeItems();
		
		if (log.isDebugEnabled()) {
			log.debug("Release holder with items - " + items.toString());
		}
		
		transientVars.put(CHANGE_ITEMS, items);
	}
}
