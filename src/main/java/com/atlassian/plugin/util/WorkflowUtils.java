package com.atlassian.plugin.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.core.user.GroupUtils;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueRelationConstants;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.worklog.WorkRatio;
import com.atlassian.jira.workflow.WorkflowActionsBean;
import com.opensymphony.user.Group;
import com.opensymphony.workflow.loader.ActionDescriptor;

/**
 * @author Gustavo Martin.
 * 
 * This utils class exposes common methods to custom workflow objects.
 *
 */
public class WorkflowUtils {
	
	public static final String SPLITTER = "@@";
	
	public static final String CONDITION_MAJOR = ">";
	public static final String CONDITION_MAJOR_EQUAL = ">=";
	public static final String CONDITION_EQUAL = "=";
	public static final String CONDITION_MINOR_EQUAL = "<=";
	public static final String CONDITION_MINOR = "<";
	public static final String CONDITION_DIFFERENT = "!=";
	
	public static final String BOOLEAN_YES = "Yes";
	public static final String BOOLEAN_NO = "No";
	
	public static final String COMPARISON_TYPE_STRING = "String";
	public static final String COMPARISON_TYPE_NUMBER = "Number";
	public static final String COMPARISON_TYPE_DATE = "Date";
	
	private static final WorkflowActionsBean workflowActionsBean = new WorkflowActionsBean();
	
	/**
	 * @param text
	 * @param splitter
	 * @return a List of Strings
	 * 
	 * Parse de string parameter, and split it by splitter param.
	 * 
	 */
	public static List parseStringParam(String text, String splitter){
		StringTokenizer token = new StringTokenizer(text, splitter);
		List retList = new ArrayList();
		
		while (token.hasMoreTokens())
			retList.add(token.nextToken());
		
		return retList;
	}
	
	/**
	 * @return a list of boolean values.
	 */
	public static List getBooleanList(){
		List booleanList = new ArrayList();
		
		booleanList.add(BOOLEAN_YES);
		booleanList.add(BOOLEAN_NO);
		
		return booleanList;
	}
	
	/**
	 * @return a list of comparison types.
	 */
	public static List getComparisonList(){
		List comparisonList = new ArrayList();
		
		comparisonList.add(COMPARISON_TYPE_STRING);
		comparisonList.add(COMPARISON_TYPE_NUMBER);
		// Not implemented yet.
		//comparisonList.add(COMPARISON_TYPE_DATE);
		
		return comparisonList;
	}
	
	/**
	 * @return  a list of conditions types.
	 */
	public static List getConditionList(){
		List conditionList = new ArrayList();
		
		conditionList.add(CONDITION_MAJOR);
		conditionList.add(CONDITION_MAJOR_EQUAL);
		conditionList.add(CONDITION_EQUAL);
		conditionList.add(CONDITION_MINOR_EQUAL);
		conditionList.add(CONDITION_MINOR);
		conditionList.add(CONDITION_DIFFERENT);
		
		return conditionList;
	}
	
	/**
	 * @param condition
	 * @return a String with the description from given condition.
	 */
	public static String getConditionString(String condition){
		String retVal = null;
		
		if(condition.equals(CONDITION_MINOR)) retVal = "minor to";
		if(condition.equals(CONDITION_MINOR_EQUAL)) retVal = "minor or equal to";
		if(condition.equals(CONDITION_EQUAL)) retVal = "equal to";
		if(condition.equals(CONDITION_MAJOR_EQUAL)) retVal = "greater or equal than";
		if(condition.equals(CONDITION_MAJOR)) retVal = "greater than";
		if(condition.equals(CONDITION_DIFFERENT)) retVal = "different to";
		
		return retVal;
	}
	
	/**
	 * @param key
	 * @return a String with the field name from given key.
	 */
	public static String getFieldNameFromKey(String key){
		return getFieldFromKey(key).getName();
	}	
	
	/**
	 * @param key
	 * @return a Field object from given key. (Field or Custom Field).
	 */
	public static Field getFieldFromKey(String key){
		return ManagerFactory.getFieldManager().getField(key);
	}	
	
	/**
	 * @param issue	an issue object.
	 * @param field a field object. (May be a Custom Field)
	 * @return an Object
	 * 
	 * It returns the value of a field within issue object. May be a Collection,
	 * a List, a Strong, or any FildType within JIRA.
	 * 
	 */
	public static Object getFieldValueFromIssue(Issue issue, Field field){
		FieldManager fldManager = ManagerFactory.getFieldManager();
		Object retVal = null;
		
		try{
			if (fldManager.isCustomField(field)){
				// Return the CustomField value. It will be any object.
				CustomField customField = (CustomField) field;
				retVal = issue.getCustomFieldValue(customField);
			} else {
				String fieldId = field.getId();
				Collection retCollection = null;
				boolean isEmpty = false;
				
				// Special treatment of fields.
				if(fieldId.equals("attachment")){
					// return a collection with the attachments associated to given issue.
					retCollection = (Collection)issue.getExternalFieldValue(fieldId);
					if(retCollection==null || retCollection.isEmpty()){
						isEmpty = true;
					}else{
						retVal = retCollection;
					}
				}
				if(fieldId.equals("versions")){
					retCollection = issue.getAffectedVersions();
					if(retCollection==null || retCollection.isEmpty()){
						isEmpty = true;
					}else{
						retVal = retCollection;
					}
				}
				if(fieldId.equals("comment")){
					// return a list with the comments of a given issue.
					try {
						retCollection = ManagerFactory.getIssueManager().getEntitiesByIssue(IssueRelationConstants.COMMENTS, issue.getGenericValue());
						if(retCollection==null || retCollection.isEmpty()){
							isEmpty = true;
						}else{
							retVal = retCollection;
						}
					} catch (GenericEntityException e) {
						retVal = null;
					}
				}
				if(fieldId.equals("components")){
					retCollection = issue.getComponents();
					if(retCollection==null || retCollection.isEmpty()){
						isEmpty = true;
					}else{
						retVal = retCollection;
					}
				}
				if(fieldId.equals("fixVersions")){
					retCollection = issue.getFixVersions();
					if(retCollection==null || retCollection.isEmpty()){
						isEmpty = true;
					}else{
						retVal = retCollection;
					}
				}
				if(fieldId.equals("thumbnail")){
					// Not implemented, yet.
					isEmpty = true;
				}
				if(fieldId.equals("issuetype")){
					retVal = issue.getIssueTypeObject();
				}
				if(fieldId.equals("timetracking")){
					// Not implemented, yet.
					isEmpty = true;
				}
				if(fieldId.equals("issuelinks")){
					retVal = ComponentManager.getInstance().getIssueLinkManager().getIssueLinks(issue.getId());
				}
				if(fieldId.equals("workratio")){
					retVal = String.valueOf(WorkRatio.getWorkRatio(issue));
				}
				if(fieldId.equals("issuekey")){
					retVal = issue.getKey();
				}
				if(fieldId.equals("subtasks")){
					retCollection = issue.getSubTasks();
					if(retCollection==null || retCollection.isEmpty()){
						isEmpty = true;
					}else{
						retVal = retCollection;
					}
				}
				if(fieldId.equals("priority")){
					retVal = issue.getPriorityObject();
				}
				if(fieldId.equals("resolution")){
					retVal = issue.getResolutionObject();
				}
				if(fieldId.equals("status")){
					retVal = issue.getStatusObject();
				}
				if(fieldId.equals("project")){
					retVal = issue.getProject();
				}
				if(fieldId.equals("security")){
					retVal = issue.getSecurityLevel();
				}
				
				if(retVal==null && !isEmpty){
					GenericValue gvIssue = issue.getGenericValue();
					retVal = gvIssue.get(fieldId);
				}
			}
		}catch (NullPointerException e){
			retVal = null;
			//log.error(e);
		}
		
		return retVal;
	}
	
	/**
	 * @param issue	an issue object.
	 * @param field a field object. (May be a Custom Field)
	 * @return an String.
	 * 
	 * It returns the value of a field within issue object as String.
	 * Instead null, it will return an empty String ( "" ).
	 *
	 */
	public static String getFieldValueFromIssueAsString(Issue issue, Field field) 
	throws UnsupportedOperationException{
		
		FieldManager fldManager = ManagerFactory.getFieldManager();
		String retVal = null;
		
		try{
			if (fldManager.isCustomField(field)){
				CustomField customField = (CustomField) field;
				if(issue.getCustomFieldValue(customField)!=null){
					retVal = issue.getCustomFieldValue(customField).toString();
				}else{
					retVal = "";
				}
			} else {
				String fieldId = field.getId();
				
				// Unsupported fields as single String value.
				if(fieldId.equals("versions")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("attachment")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("comment")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("components")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("fixVersions")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("thumbnail")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("issuelinks")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("subtasks")){
					throw new UnsupportedOperationException();
				}
				if(fieldId.equals("timetracking")){
					throw new UnsupportedOperationException();
				}
				
				// Special treatment of fields.
				if(fieldId.equals("issuetype")){
					if(issue.getIssueTypeObject()!=null){
						retVal = issue.getIssueTypeObject().getNameTranslation();
					}else{
						retVal = "";
					}
				}
				if(fieldId.equals("workratio")){
					retVal = String.valueOf(WorkRatio.getWorkRatio(issue));
				}
				if(fieldId.equals("priority")){
					if(issue.getPriorityObject()!=null){
						retVal = issue.getPriorityObject().getNameTranslation();
					}else{
						retVal = "";
					}
				}
				if(fieldId.equals("project")){
					if(issue.getProject()!=null){
						retVal = issue.getProject().getString("name");
					}else{
						retVal = "";
					}
				}
				if(fieldId.equals("resolution")){
					if(issue.getResolutionObject()!=null){
						retVal = issue.getResolutionObject().getNameTranslation();
					}else{
						retVal = "";
					}
				}
				if(fieldId.equals("status")){
					if(issue.getStatusObject()!=null){
						retVal = issue.getStatusObject().getNameTranslation();
					}else{
						retVal = "";
					}
				}
				if(fieldId.equals("security")){
					if(issue.getSecurityLevel()!=null){
						retVal = issue.getSecurityLevel().getString("name");
					}else{
						retVal = "";
					}
				}
				if(fieldId.equals("issuekey")){
					retVal = issue.getKey();
				}
				
				// Get the value from the Generic Value of Issue.
				if(retVal==null){
					GenericValue gvIssue = issue.getGenericValue();
					if(gvIssue.get(fieldId)!=null){
						retVal = gvIssue.get(fieldId).toString();
						
						if((fieldId.equals("timeoriginalestimate")) || 
								(fieldId.equals("timeestimate")) || 
								(fieldId.equals("timespent"))){
							
							retVal = String.valueOf(new Long(retVal).longValue()/60);
						}
						
					}else{
						retVal = "";
					}
					
				}
			}
		}catch (NullPointerException e){
			retVal = null;
		}
		
		return retVal;
	}
	
	/**
	 * @param strGroups
	 * @param splitter
	 * @return a List of Group
	 * 
	 * Get Groups from a string.
	 * 
	 */
	public static List getGroups(String strGroups, String splitter){
		
		List groups = parseStringParam(strGroups, splitter);
		List groupList = new ArrayList(groups.size());
		
		Iterator it = groups.iterator();
		while(it.hasNext()){
			Group group = GroupUtils.getGroup((String) it.next());
			groupList.add(group);
		}
		
		return groupList;
	}
	
	/**
	 * @param group
	 * @param splitter
	 * @return a String with the groups selected.
	 * 
	 * Get Groups as String.
	 * 
	 */
	public static String getStringGroup(Collection group, String splitter){
		String retString = "";
		
		Iterator it = group.iterator();
		while(it.hasNext()){
			retString = retString + ((Group) it.next()).getName() + splitter;
		}
		return retString;
	}
	
	/**
	 * @param strFields
	 * @param splitter
	 * @return a List of Field
	 * 
	 * Get Fields from a string.
	 * 
	 */
	public static List getFields(String strFields, String splitter){
		
		List fields = parseStringParam(strFields, splitter);
		List fieldList = new ArrayList(fields.size());
		
		Iterator it = fields.iterator();
		while(it.hasNext()){
			Field field = ManagerFactory.getFieldManager().getField((String) it.next());
			fieldList.add(field);
		}
		
		return CommonPluginUtils.sortFields(fieldList);
	}
	
	/**
	 * @param fields
	 * @param splitter
	 * @return a String with the fields selected.
	 * 
	 * Get Fields as String.
	 * 
	 */
	public static String getStringField(Collection fields, String splitter){
		String retString = "";
		
		Iterator it = fields.iterator();
		while(it.hasNext()){
			retString = retString + ((Field) it.next()).getId() + splitter;
		}
		return retString;
	}	
	
	/**
	 * @param actionDescriptor
	 * @return the FieldScreen of the transition. Or null, if the transition hasn't a screen asociated.
	 * 
	 * It obtains the fieldscreen for a transition, if it have one.
	 * 
	 */
	public static FieldScreen getFieldScreen(ActionDescriptor actionDescriptor){
		return workflowActionsBean.getFieldScreenForView(actionDescriptor);
	}
}