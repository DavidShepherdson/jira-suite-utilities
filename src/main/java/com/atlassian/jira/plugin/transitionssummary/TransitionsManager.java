package com.atlassian.jira.plugin.transitionssummary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.ofbiz.DefaultOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.plugin.util.LogUtils;
import com.atlassian.jira.util.map.EasyMap;

/**
 * @author Gustavo Martin
 * 
 * This class is in charge to obtain the changes from the Change History, and to summarized them.
 *  
 */
public class TransitionsManager {
	
	/**
	 * @param gvIssue the current issue.
	 * @return a List with the Transition Summaries.
	 * 
	 * It obtains all Transition Summaries.
	 */
	public static List<TransitionSummary> getTransitionSummary(Issue issue){
		Timestamp tsCreated = issue.getTimestamp("created");
		// Reads all status changes, associated with the execution of transitions.
		List<Transition> statusChanges = getStatusChanges(issue, tsCreated);
		
		if (statusChanges.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		Map<String, TransitionSummary> summary = new TreeMap<String, TransitionSummary>();
		List<TransitionSummary> retList = new ArrayList<TransitionSummary>();

		for (Transition trans : statusChanges) {
			// Sets an ID for the Transition.
			String transitionId = trans.getFromStatus().getId().toString() + "to" + trans.getToStatus().getId().toString();
			
			LogUtils.getGeneral().debug("transition found: " + transitionId); // Debug output
			
			TransitionSummary tranSummary = null;
			
			if (summary.containsKey(transitionId)){
				tranSummary = (TransitionSummary) summary.get(transitionId);
			} else {
				tranSummary = new TransitionSummary(transitionId, trans.getFromStatus(), trans.getToStatus());
				
				summary.put(transitionId, tranSummary);
				retList.add(tranSummary);
			}
			
			// Adds the current Transition to the corresponding TransitionSummary.
			tranSummary.addTransition(trans);
		}
		
		return retList;
	}
	
	/**
	 * @param issue the current issue.
	 * @param tsCreated when the issue was created. It allows to calculate the duration of the first transition.
	 * @return a List with the Status Changes.
	 * 
	 * It obtains all status changes data from the Change History.
	 */
	private static List<Transition> getStatusChanges(Issue issue, Timestamp tsCreated){
		OfBizDelegator delegator = new DefaultOfBizDelegator(CoreFactory.getGenericDelegator());
		Map params = EasyMap.build("issue", issue.getLong("id"));
		List<GenericValue> changeGroups = delegator.findByAnd("ChangeGroup", params);
		
		// Added by caisd_1998 at hotmail dot com
		Collections.sort(changeGroups,
			new Comparator<GenericValue>() {
				public int compare(GenericValue o1, GenericValue o2) {
					return o1.getTimestamp("created").compareTo(o2.getTimestamp("created"));
				}
			}
		);

		List<Transition> retList = new ArrayList<Transition>();
		Timestamp tsStartDate = new Timestamp(tsCreated.getTime());

		for (GenericValue changeGroup : changeGroups) {
			// Obtains all ChangeItems that contains an status change.
			Map paramsItem = EasyMap.build(
					"group", changeGroup.getLong("id"),
					"field", "status",
					"fieldtype", "jira"
			);
			
			List<GenericValue> changeItems = delegator.findByAnd("ChangeItem", paramsItem);
			
			for (GenericValue changeItem : changeItems) {
				// And it creates the corresponding Transition.
				Transition tran = new Transition();
				
				tran.setChangedBy(changeGroup.getString("author"));
				tran.setChangedAt(changeGroup.getTimestamp("created"));
				tran.setFromStatus(Long.valueOf(changeItem.getString("oldvalue")));
				tran.setToStatus(Long.valueOf(changeItem.getString("newvalue")));
				tran.setStartAt(tsStartDate);
				
				retList.add(tran);
				
				// It is used to calculate the duration of the next transition.
				tsStartDate = new Timestamp(changeGroup.getTimestamp("created").getTime());
			}
		}
		
		return retList;
	}
}