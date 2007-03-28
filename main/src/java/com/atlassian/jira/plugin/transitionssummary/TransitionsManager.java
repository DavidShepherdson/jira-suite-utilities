package com.atlassian.jira.plugin.transitionssummary;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.ofbiz.core.entity.GenericValue;
import com.atlassian.jira.issue.Issue;
import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.jira.ofbiz.DefaultOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
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
	public static List getTransitionSummary(Issue issue){
		Map summary = new TreeMap();
		Timestamp tsCreated = issue.getTimestamp("created");
		List retList = new ArrayList();
		
		// Reads all status changes, associated with the execution of transitions.
		List statusChanges = getStatusChanges(issue, tsCreated);
		
		Iterator itStatuses = statusChanges.iterator();
		if(!itStatuses.hasNext()){
			retList = Collections.EMPTY_LIST;
		}
		
		while(itStatuses.hasNext()){
			Transition trans = (Transition) itStatuses.next();
			
			// Sets an ID for the Transition.
			String transitionId = trans.getFromStatus().getId().toString() + "to" + trans.getToStatus().getId().toString();
			
			// System.out.println("transition found: " + transitionId); // Debug output
			
			TransitionSummary tranSummary = null;
			if(summary.containsKey(transitionId)){
				tranSummary = (TransitionSummary) summary.get(transitionId);
			}else{
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
	private static List getStatusChanges(Issue issue, Timestamp tsCreated){
		List retList = new ArrayList();
		Timestamp tsStartDate = new Timestamp(tsCreated.getTime());
		
		OfBizDelegator delegator = new DefaultOfBizDelegator(CoreFactory.getGenericDelegator());
		Map params = EasyMap.build("issue", issue.getLong("id"));
		List changeGroups = delegator.findByAnd("ChangeGroup", params);
		
		GenericValue changeGroup;
		GenericValue changeItem;
		
		// Added by caisd_1998 at hotmail dot com
		Collections.sort(changeGroups,
		  new Comparator() {
                public int compare(Object o1, Object o2) {
                  GenericValue c1 = (GenericValue)o1;
                  GenericValue c2 = (GenericValue)o2;
                  return c1.getTimestamp("created").compareTo(c2.getTimestamp("created"));
                }
		  }
		);

		Iterator itGroups = changeGroups.iterator();
		while(itGroups.hasNext()){
			
			changeGroup = (GenericValue)itGroups.next();
			
			// Obtains all ChangeItems that contains an status change.
			Map paramsItem = EasyMap.build("group", changeGroup.getLong("id"),"field","status");
			List changeItems = delegator.findByAnd("ChangeItem", paramsItem);
			
			Iterator itItems = changeItems.iterator();
			while(itItems.hasNext()){
				changeItem = (GenericValue)itItems.next();
				
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