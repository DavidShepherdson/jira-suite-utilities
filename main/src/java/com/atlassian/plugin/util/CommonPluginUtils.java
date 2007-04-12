package com.atlassian.plugin.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelField;

import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.properties.ApplicationPropertiesImpl;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.impl.DateCFType;
import com.atlassian.jira.issue.customfields.impl.DateTimeCFType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutStorageException;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.plugin.issue.fields.NameComparatorEx;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.bean.I18nBean;

/**
 * @author Gustavo Martin.
 *
 * This utils class exposes common methods to plugin.
 * 
 */
public class CommonPluginUtils {
	
	/**
	 * @return a complete list of fields, including custom fields.
	 */
	public static List<Field> getAllFields() {
		List<Field> allFields = new ArrayList<Field>();
		Set<Field> allFieldsSet = new TreeSet<Field>();
		
		allFieldsSet.addAll(getOrderableFields());
		allFieldsSet.addAll(getAllAvailableNavigableFields());
		
		for (Field f : allFieldsSet) {
			allFields.add(f);
		}
		
		return sortFields(allFields);
	}
	
	/**
	 * @return a list of fields, including custom fields, which could be modified. 
	 */
	public static List<Field> getAllEditableFields(){
		List<Field> allFields = new ArrayList<Field>();
		
		for (Field f : getAllAvailableNavigableFields()) {
			allFields.add(f);
		}
		
		return sortFields(allFields);
	}
	
	/**
	 * @param allFields list of fields to be sorted.
	 * @return a list with fields sorted by name.
	 */
	public static List<Field> sortFields(List<Field> allFields) {
		ApplicationProperties ap = new ApplicationPropertiesImpl();
		I18nBean i18n = new I18nBean(ap.getDefaultLocale().getDisplayName());
		NameComparatorEx nameComparator = new NameComparatorEx(i18n); 
		
		Collections.sort(allFields, nameComparator);
		
		return allFields;
	}
	
	/**
	 * @return all orderable fields
	 */
	@SuppressWarnings("unchecked")
	private static Set<Field> getOrderableFields() {
		Set<Field> orderableFields = ManagerFactory.getFieldManager().getOrderableFields();
		
		return orderableFields;
	}
	
	/**
	 * @return all navigable fields, include custom fields.
	 */
	@SuppressWarnings("unchecked")
	private static Set<Field> getAllAvailableNavigableFields() {
		Set<Field> navigableFields = Collections.EMPTY_SET;
		
		try {
			navigableFields = ManagerFactory.getFieldManager().getAllAvailableNavigableFields();
		} catch (FieldException e) {
			e.printStackTrace();
		}
		
		return navigableFields;
	}
	
	/**
	 * @return a list of all fields of type date and datetime.
	 */
	public static List getAllDateFields() {
		List allDateFields = new ArrayList();
		
		CustomField cfDate = null;
		Iterator it = ManagerFactory.getCustomFieldManager().getCustomFieldObjects().iterator();
		
		while(it.hasNext()){
			cfDate = (CustomField) it.next(); 
			
			CustomFieldType customFieldType = cfDate.getCustomFieldType();
			
			if ((customFieldType instanceof DateCFType) || (customFieldType instanceof DateTimeCFType)){
				allDateFields.add(cfDate);
			}
		}
		
		// Obtain all fields type date from model.
		ModelEntity modelIssue = CoreFactory.getGenericDelegator().getModelEntity("Issue");
		Iterator it1 = modelIssue.getFieldsIterator();
		
		while (it1.hasNext()){
			ModelField modelField = (ModelField) it1.next();
			
			if(modelField.getType().equals("date-time")){
				Field fldDate = ManagerFactory.getFieldManager().getField(modelField.getName());
				allDateFields.add(fldDate);
			}
			
		}
		
		return sortFields(allDateFields);
	}
	
	/**
	 * @param issue: issue to which the field belongs
	 * @param field wished field
	 * @param fieldScreen wished screen
	 * @return if a field is displayed in a screen.
	 */
	public static boolean isFieldOnScreen(Issue issue, Field field, FieldScreen fieldScreen){
		boolean retVal = false;
		
		Iterator itTabs = fieldScreen.getTabs().iterator();
		while(itTabs.hasNext() && !retVal){
			FieldScreenTab tab = (FieldScreenTab) itTabs.next();
			Iterator itFields = tab.getFieldScreenLayoutItems().iterator();
			while(itFields.hasNext() && !retVal){
				FieldScreenLayoutItem fieldScreenLayoutItem = (FieldScreenLayoutItem) itFields.next();
				
				if(field.getId().equals(fieldScreenLayoutItem.getFieldId()) && !isFieldHidden(issue, field)){
					retVal = true;
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * @param issue: issue to which the field belongs
	 * @param field: wished field
	 * @return if a field is hidden.
	 */
	public static boolean isFieldHidden(Issue issue, Field field){
		boolean retVal = false;
		
		try {
			FieldLayoutManager fieldLayoutManager = ComponentManager.getInstance().getFieldLayoutManager();
			// Change by Bettina Zucker
			//FieldLayoutItem layoutItem = fieldLayoutManager.getFieldLayout().getFieldLayoutItem(field.getId());
			FieldLayoutItem layoutItem = fieldLayoutManager.getFieldLayout(issue.getProject(), issue.getIssueTypeObject().getId()).getFieldLayoutItem(field.getId());		
			retVal = layoutItem.isHidden();
			
		} catch (FieldLayoutStorageException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * @param issue: issue to which the field belongs
	 * @param field: wished field
	 * @return if a field is required.
	 */
	public static boolean isFieldRequired(Issue issue, Field field){
		boolean retVal = false;
		
		try {
			FieldLayoutManager fieldLayoutManager = ComponentManager.getInstance().getFieldLayoutManager();
			// Change by Bettina Zucker
			//FieldLayoutItem layoutItem = fieldLayoutManager.getFieldLayout().getFieldLayoutItem(field.getId());
			FieldLayoutItem layoutItem = fieldLayoutManager.getFieldLayout(issue.getProject(), issue.getIssueTypeObject().getId()).getFieldLayoutItem(field.getId());		
			retVal = layoutItem.isRequired();
			
		} catch (FieldLayoutStorageException e) {
			e.printStackTrace();
		}
		
		return retVal;
	}
	
	/**
	 * @return a list of fields that could be chosen to copy their value.
	 */
	public static List getCopyFromFields(){
		List allFields = getAllFields();
		allFields.removeAll(getNonCopyFromFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getCopyFromFields().
	 */
	private static List getNonCopyFromFields(){
		List fields = new ArrayList();
		
		Field attachment = ManagerFactory.getFieldManager().getField("attachment");
		Field comment = ManagerFactory.getFieldManager().getField("comment");
		Field components = ManagerFactory.getFieldManager().getField("components");
		Field fixVersions = ManagerFactory.getFieldManager().getField("fixVersions");
		Field issuelinks = ManagerFactory.getFieldManager().getField("issuelinks");
		Field security = ManagerFactory.getFieldManager().getField("security");
		Field subtasks = ManagerFactory.getFieldManager().getField("subtasks");
		Field thumbnail = ManagerFactory.getFieldManager().getField("thumbnail");
		Field timetracking = ManagerFactory.getFieldManager().getField("timetracking");
		Field versions = ManagerFactory.getFieldManager().getField("versions");
		
		fields.add(attachment);
		fields.add(comment);
		fields.add(components);
		fields.add(fixVersions);
		fields.add(issuelinks);
		fields.add(security);
		fields.add(subtasks);
		fields.add(thumbnail);
		fields.add(timetracking);
		fields.add(versions);
		
		return fields;
	}
	
	/**
	 * @return a list of fields that could be chosen to copy their value.
	 */
	public static List<Field> getCopyToFields(){
		List<Field> allFields = getAllEditableFields();
		allFields.removeAll(getNonCopyToFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getCopyFromFields().
	 */
	private static List<Field> getNonCopyToFields(){
		List<Field> fields = new ArrayList<Field>();
		
		Field attachment = ManagerFactory.getFieldManager().getField("attachment");
		Field comment = ManagerFactory.getFieldManager().getField("comment");
		Field components = ManagerFactory.getFieldManager().getField("components");
		Field created = ManagerFactory.getFieldManager().getField("created");
		Field estimate = ManagerFactory.getFieldManager().getField("timeoriginalestimate");
		Field fixVersions = ManagerFactory.getFieldManager().getField("fixVersions");
		Field issueKey = ManagerFactory.getFieldManager().getField("issuekey");
		Field issuelinks = ManagerFactory.getFieldManager().getField("issuelinks");
		Field issueType = ManagerFactory.getFieldManager().getField("issuetype");
		Field priority = ManagerFactory.getFieldManager().getField("priority");
		Field project = ManagerFactory.getFieldManager().getField("project");
		Field remaining = ManagerFactory.getFieldManager().getField("timeestimate");
		Field resolution = ManagerFactory.getFieldManager().getField("resolution");
		Field security = ManagerFactory.getFieldManager().getField("security");
		Field status = ManagerFactory.getFieldManager().getField("status");
		Field subtasks = ManagerFactory.getFieldManager().getField("subtasks");
		Field thumbnail = ManagerFactory.getFieldManager().getField("thumbnail");
		Field timeSpent = ManagerFactory.getFieldManager().getField("timespent");
		Field timetracking = ManagerFactory.getFieldManager().getField("timetracking");
		Field updated = ManagerFactory.getFieldManager().getField("updated");
		Field versions = ManagerFactory.getFieldManager().getField("versions");
		Field votes = ManagerFactory.getFieldManager().getField("votes");
		Field workratio = ManagerFactory.getFieldManager().getField("workratio");
		
		fields.add(attachment);
		fields.add(comment);
		fields.add(components);
		fields.add(created);
		fields.add(estimate);
		fields.add(fixVersions);
		fields.add(issueKey);
		fields.add(issuelinks);
		fields.add(issueType);
		fields.add(priority);
		fields.add(project);
		fields.add(remaining);
		fields.add(resolution);
		fields.add(security);
		fields.add(status);
		fields.add(subtasks);
		fields.add(thumbnail);
		fields.add(timeSpent);
		fields.add(timetracking);
		fields.add(updated);
		fields.add(versions);
		fields.add(votes);
		fields.add(workratio);
		
		return fields;
	}
	
	/**
	 * @return a list of fields that could be chosen like required.
	 */
	public static List getRequirableFields(){
		List allFields = getAllFields();
		allFields.removeAll(getNonRequirableFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getRequirableFields().
	 */
	private static List getNonRequirableFields(){
		List fields = new ArrayList();
		
		Field attachment = ManagerFactory.getFieldManager().getField("attachment");
		Field comment = ManagerFactory.getFieldManager().getField("comment");
		Field created = ManagerFactory.getFieldManager().getField("created");
		Field estimate = ManagerFactory.getFieldManager().getField("timeoriginalestimate");
		Field issuekey = ManagerFactory.getFieldManager().getField("issuekey");
		Field issuelinks = ManagerFactory.getFieldManager().getField("issuelinks");
		Field issuetype = ManagerFactory.getFieldManager().getField("issuetype");
		Field project = ManagerFactory.getFieldManager().getField("project");
		Field remaining = ManagerFactory.getFieldManager().getField("timeestimate");
		Field status = ManagerFactory.getFieldManager().getField("status");
		Field subtasks = ManagerFactory.getFieldManager().getField("subtasks");
		Field thumbnail = ManagerFactory.getFieldManager().getField("thumbnail");
		Field timeSpent = ManagerFactory.getFieldManager().getField("timespent");
		Field timetracking = ManagerFactory.getFieldManager().getField("timetracking");
		Field updated = ManagerFactory.getFieldManager().getField("updated");
		Field votes = ManagerFactory.getFieldManager().getField("votes");
		Field workratio = ManagerFactory.getFieldManager().getField("workratio");
		
		fields.add(attachment);
		fields.add(comment);
		fields.add(created);
		fields.add(estimate);
		fields.add(issuekey);
		fields.add(issuelinks);
		fields.add(issuetype);
		fields.add(project);
		fields.add(remaining);
		fields.add(status);
		fields.add(subtasks);
		fields.add(thumbnail);
		fields.add(timeSpent);
		fields.add(timetracking);
		fields.add(updated);
		fields.add(votes);
		fields.add(workratio);
		
		return fields;
	}
	
	/**
	 * @return a list of fields that could be chosen in Value-Field Condition.
	 */
	public static List getValueFieldConditionFields(){
		List allFields = getAllFields();
		allFields.removeAll(getNonValueFieldConditionFields());
		// Date fields are removed, because date comparison is not implemented yet.
		allFields.removeAll(getAllDateFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getValueFieldConditionFields().
	 */
	private static List getNonValueFieldConditionFields(){
		List fields = new ArrayList();
		
		Field attachment = ManagerFactory.getFieldManager().getField("attachment");
		Field versions = ManagerFactory.getFieldManager().getField("versions");
		Field comment = ManagerFactory.getFieldManager().getField("comment");
		Field components = ManagerFactory.getFieldManager().getField("components");
		Field created = ManagerFactory.getFieldManager().getField("created");
		Field fixVersions = ManagerFactory.getFieldManager().getField("fixVersions");
		Field thumbnail = ManagerFactory.getFieldManager().getField("thumbnail");
		Field issuelinks = ManagerFactory.getFieldManager().getField("issuelinks");
		Field issuekey = ManagerFactory.getFieldManager().getField("issuekey");
		Field subtasks = ManagerFactory.getFieldManager().getField("subtasks");
		Field timetracking = ManagerFactory.getFieldManager().getField("timetracking");
		Field updated = ManagerFactory.getFieldManager().getField("updated");
		Field votes = ManagerFactory.getFieldManager().getField("votes");
		
		fields.add(attachment);
		fields.add(versions);
		fields.add(comment);
		fields.add(components);
		fields.add(created);
		fields.add(fixVersions);
		fields.add(issuekey);
		fields.add(issuelinks);
		fields.add(subtasks);
		fields.add(thumbnail);
		fields.add(timetracking);
		fields.add(updated);
		fields.add(votes);
		
		return fields;
	}
	
	/**
	 * @param cal
	 * 
	 * Clear the time part from a given Calendar.
	 * 
	 */
	public static void clearCalendarTimePart (Calendar cal){
		cal.clear(Calendar.HOUR_OF_DAY);
		cal.clear(Calendar.HOUR);
		cal.clear(Calendar.MINUTE);
		cal.clear(Calendar.SECOND);
		cal.clear(Calendar.MILLISECOND);
	}
	
	/**
	 * @param tsDate
	 * @return a String.
	 * 
	 * It formats to a date nice.
	 */
	public static String getNiceDate(Timestamp tsDate){
		Date timePerformed = new Date(tsDate.getTime());
		I18nHelper i18n = new I18nBean();
		return ManagerFactory.getOutlookDateManager().getOutlookDate(i18n.getLocale()).formatDMYHMS(timePerformed);
	}
}