package com.atlassian.jira.plugin.helpers;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.plugin.util.WorkflowUtils;

/**
 * @author Gustavo Martin
 * 
 * This manager is used to handle Yes/No Type.
 *  
 */
public class YesNoManager {
	private static YesNoManager singleton = null;
	private Hashtable yesNoOptions = null;
	
	/**
	 * @return an instance of this manager.
	 */
	public static YesNoManager getManager() {
		if (singleton == null) {
			singleton = new YesNoManager();
			YesNoManager.reload();
		}
		return singleton;
	}
	
	/**
	 * Reloads the list of YesNo Types. It will be only two (YES and NO)
	 */
	public static void reload() {
		singleton.yesNoOptions = new Hashtable();
		
		YesNoType yes = new YesNoType();
		yes.setId(new Integer(1));
		yes.setValue(WorkflowUtils.BOOLEAN_YES);
		singleton.yesNoOptions.put(yes.getId(), yes);
		
		YesNoType no = new YesNoType();
		no.setId(new Integer(2));
		no.setValue(WorkflowUtils.BOOLEAN_NO);
		singleton.yesNoOptions.put(no.getId(), no);
		
	}
	
	/**
	 * @param id the YesNo Type identifier.
	 * @return an YesNoType.
	 */
	public YesNoType getOption(Integer id) {
		return (YesNoType)yesNoOptions.get(id);
	}
	
	/**
	 * @return a List with all YesNo Types availables.
	 */
	public List getAllOptions() {
		List retList = new ArrayList();
		
		Iterator it = singleton.yesNoOptions.values().iterator();
		while(it.hasNext()){
			retList.add((YesNoType) it.next());
		}
		
		return retList;
	}	
	
}
