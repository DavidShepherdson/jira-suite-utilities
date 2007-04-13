package com.atlassian.jira.plugin.helpers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.atlassian.plugin.util.WorkflowUtils;

/**
 * @author Gustavo Martin
 * 
 * This manager is used to handle all Comparison Types.
 *  
 */
public class ComparisonManager {
	private static ComparisonManager singleton = null;
	private Hashtable Comparisons = null;
	
	/**
	 * @return an instance of this manager.
	 */
	public static ComparisonManager getManager() {
		if (singleton == null) {
			singleton = new ComparisonManager();
			ComparisonManager.reload();
		}
		return singleton;
	}
	
	/**
	 * Reloads the list of all Comparison Types.
	 */
	public static void reload() {
		singleton.Comparisons = new Hashtable();
		
		ComparisonType compStr = new ComparisonType();
		compStr.setId(new Integer(1));
		compStr.setValue(WorkflowUtils.COMPARISON_TYPE_STRING);
		singleton.Comparisons.put(compStr.getId(), compStr);
		
		ComparisonType compNum = new ComparisonType();
		compNum.setId(new Integer(2));
		compNum.setValue(WorkflowUtils.COMPARISON_TYPE_NUMBER);
		singleton.Comparisons.put(compNum.getId(), compNum);
		
		// Not implemented yet.
		//ComparisonType compDate = new ComparisonType();
		//compDate.setId(new Integer(3));
		//compDate.setValue(WorkflowUtils.COMPARISON_TYPE_DATE);
		//singleton.Comparisons.put(compDate.getId(), compDate);
		
	}
	
	/**
	 * @param id the Comparison Type identifier.
	 * @return an ComparisonType.
	 */
	public ComparisonType getComparisonType(Integer id) {
		return (ComparisonType)Comparisons.get(id);
	}
	
	/**
	 * @return a List with all Comparison Types availables.
	 */
	public List getAllComparisonType() {
		List retList = new ArrayList();
		
		Iterator it = singleton.Comparisons.values().iterator();
		while(it.hasNext()){
			retList.add((ComparisonType) it.next());
		}
		
		return retList;
	}	
	
}
