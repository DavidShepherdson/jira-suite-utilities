package com.atlassian.jira.plugin.helpers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.atlassian.plugin.util.WorkflowUtils;

/**
 * @author Gustavo Martin
 * 
 * This manager is used to handle all Condition Types.
 *  
 */
public class ConditionManager {
	private static ConditionManager singleton = null;
	private Hashtable conditions = null;
	
	/**
	 * @return an instance of this manager.
	 */
	public static ConditionManager getManager() {
		if (singleton == null) {
			singleton = new ConditionManager();
			ConditionManager.reload();
		}
		return singleton;
	}
	
	/**
	 * Reloads the list of all Condition Types.
	 */
	public static void reload() {
		singleton.conditions = new Hashtable();
		
		ConditionType condMajor = new ConditionType();
		condMajor.setId(new Integer(1));
		condMajor.setValue(WorkflowUtils.CONDITION_MAJOR);
		singleton.conditions.put(condMajor.getId(), condMajor);
		
		ConditionType condMajorEq = new ConditionType();
		condMajorEq.setId(new Integer(2));
		condMajorEq.setValue(WorkflowUtils.CONDITION_MAJOR_EQUAL);
		singleton.conditions.put(condMajorEq.getId(), condMajorEq);
		
		ConditionType condEqual = new ConditionType();
		condEqual.setId(new Integer(3));
		condEqual.setValue(WorkflowUtils.CONDITION_EQUAL);
		singleton.conditions.put(condEqual.getId(), condEqual);
		
		ConditionType condMinorEq = new ConditionType();
		condMinorEq.setId(new Integer(4));
		condMinorEq.setValue(WorkflowUtils.CONDITION_MINOR_EQUAL);
		singleton.conditions.put(condMinorEq.getId(), condMinorEq);
		
		ConditionType condMinor = new ConditionType();
		condMinor.setId(new Integer(5));
		condMinor.setValue(WorkflowUtils.CONDITION_MINOR);
		singleton.conditions.put(condMinor.getId(), condMinor);
		
		ConditionType condDiff = new ConditionType();
		condDiff.setId(new Integer(6));
		condDiff.setValue(WorkflowUtils.CONDITION_DIFFERENT);
		singleton.conditions.put(condDiff.getId(), condDiff);
		
	}
	
	/**
	 * @param id the Condition Type identifier.
	 * @return an Condition.
	 */
	public ConditionType getCondition(Integer id) {
		return (ConditionType)conditions.get(id);
	}
	
	/**
	 * @return a List with all Condition Types availables.
	 */
	public List getAllConditions() {
		List retList = new ArrayList();
		
		Iterator it = singleton.conditions.values().iterator();
		while(it.hasNext()){
			retList.add((ConditionType) it.next());
		}
		
		return retList;
	}	
	
}
