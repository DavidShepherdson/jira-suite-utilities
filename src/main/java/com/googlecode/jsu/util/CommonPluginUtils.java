package com.googlecode.jsu.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.config.properties.ApplicationPropertiesImpl;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.customfields.CustomFieldType;
import com.atlassian.jira.issue.customfields.impl.DateCFType;
import com.atlassian.jira.issue.customfields.impl.DateTimeCFType;
import com.atlassian.jira.issue.customfields.impl.ImportIdLinkCFType;
import com.atlassian.jira.issue.customfields.impl.ReadOnlyCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldException;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutStorageException;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.bean.FieldVisibilityBean;
import com.atlassian.jira.web.bean.I18nBean;
import com.googlecode.jsu.helpers.NameComparatorEx;

/**
 * @author Gustavo Martin.
 *
 * This utils class exposes common methods to plugin.
 * 
 */
public class CommonPluginUtils {
	private static final Collection<String> TIME_TRACKING_FIELDS = Arrays.asList(
			IssueFieldConstants.TIME_ESTIMATE,
			IssueFieldConstants.TIME_ORIGINAL_ESTIMATE,
			IssueFieldConstants.TIME_SPENT,
			IssueFieldConstants.TIMETRACKING
	);
	
	/**
	 * @return a complete list of fields, including custom fields.
	 */
	public static List<Field> getAllFields() {
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		Set<Field> allFieldsSet = new TreeSet<Field>(getComparator());
		
		allFieldsSet.addAll(fieldManager.getOrderableFields());
		
		try {
			allFieldsSet.addAll(fieldManager.getAllAvailableNavigableFields());
		} catch (FieldException e) {
			LogUtils.getGeneral().error("Unable to load navigable fields", e);
		}
		
		return new ArrayList<Field>(allFieldsSet);
	}
	
	/**
	 * @return a list of fields, including custom fields, which could be modified. 
	 */
	public static List<Field> getAllEditableFields(){
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		Set<Field> allFields = new TreeSet<Field>(getComparator());
		
		try {
			final Set<Field> fields = fieldManager.getAllAvailableNavigableFields();
			
			for (Field f : fields) {
				allFields.add(f);
			}
		} catch (FieldException e) {
			LogUtils.getGeneral().error("Unable to load navigable fields", e);
		}
		
		return new ArrayList<Field>(allFields);
	}
	
	/**
	 * @param allFields list of fields to be sorted.
	 * @return a list with fields sorted by name.
	 */
	public static List<Field> sortFields(List<Field> allFields) {
		Collections.sort(allFields, getComparator());
		
		return allFields;
	}
	
	/**
	 * @return a list of all fields of type date and datetime.
	 */
	public static List<Field> getAllDateFields() {
		List<Field> allDateFields = new ArrayList<Field>();
		
		List<CustomField> fields = ManagerFactory.getCustomFieldManager().getCustomFieldObjects();
		
		for (CustomField cfDate : fields) {
			CustomFieldType customFieldType = cfDate.getCustomFieldType();
			
			if ((customFieldType instanceof DateCFType) || (customFieldType instanceof DateTimeCFType)){
				allDateFields.add(cfDate);
			}
		}
		
		// Obtain all fields type date from model.
		ModelEntity modelIssue = CoreFactory.getGenericDelegator().getModelEntity("Issue");
		Iterator<ModelField> modelFields = modelIssue.getFieldsIterator();
		
		while (modelFields.hasNext()) {
			ModelField modelField = modelFields.next();
			
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
		final FieldManager fieldManager = ComponentManager.getInstance().getFieldManager();

		if (fieldManager.isCustomField(field)) {
			CustomFieldType type = ((CustomField) field).getCustomFieldType(); 
			
			if ((type instanceof ReadOnlyCFType) || 
					(type instanceof ImportIdLinkCFType)) {
				return false;
			}
		}
		
		boolean retVal = false;
		Iterator<FieldScreenTab> itTabs = fieldScreen.getTabs().iterator();
		
		while(itTabs.hasNext() && !retVal){
			FieldScreenTab tab = itTabs.next();
			Iterator<FieldScreenLayoutItem> itFields = tab.getFieldScreenLayoutItems().iterator();

			while(itFields.hasNext() && !retVal){
				FieldScreenLayoutItem fieldScreenLayoutItem = itFields.next();

				if (field.getId().equals(fieldScreenLayoutItem.getFieldId()) && 
						!isFieldHidden(issue, field)) {
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
	public static boolean isFieldHidden(Issue issue, Field field) {
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		final String fieldId = field.getId();
		
		if (fieldManager.isCustomField(field)) {
			CustomField customField = (CustomField) field;
			List<Project> assignedProjects = customField.getAssociatedProjects();
		        
			if (!customField.isGlobal() && !assignedProjects.contains(issue.getProjectObject())) {
				return true;
			}
		}
		
		if (TIME_TRACKING_FIELDS.contains(fieldId)) {
			ApplicationProperties applicationProperties = ManagerFactory.getApplicationProperties();
			
			return !applicationProperties.getOption(APKeys.JIRA_OPTION_TIMETRACKING);
		} else {
			FieldVisibilityBean fieldVisibilityBean = new FieldVisibilityBean();

	        return fieldVisibilityBean.isFieldHidden(field.getId(), issue);
		}
	}
	
	public static FieldLayoutItem getFieldLayoutItem(Issue issue, Field field) throws FieldLayoutStorageException {
		final FieldLayoutManager fieldLayoutManager = ComponentManager.getInstance().getFieldLayoutManager();

		FieldLayout layout = fieldLayoutManager.getFieldLayout(issue.getProject(), issue.getIssueTypeObject().getId());
		
		if (layout.getId() == null) {
			layout = fieldLayoutManager.getEditableDefaultFieldLayout();
		}
		
		return layout.getFieldLayoutItem(field.getId());		
	}
	
	/**
	 * @param issue: issue to which the field belongs
	 * @param field: wished field
	 * @return if a field is required.
	 */
	public static boolean isFieldRequired(Issue issue, Field field){
		boolean retVal = false;
		
		try {
			retVal = getFieldLayoutItem(issue, field).isRequired();
		} catch (FieldLayoutStorageException e) {
			LogUtils.getGeneral().error("Unable to check is field required", e);
		}
		
		return retVal;
	}
	
	/**
	 * @return a list of fields that could be chosen to copy their value.
	 */
	public static List<Field> getCopyFromFields(){
		List<Field> allFields = getAllFields();
		
		allFields.removeAll(getNonCopyFromFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getCopyFromFields().
	 */
	private static List<Field> getNonCopyFromFields(){
		List<Field> fields = new ArrayList<Field>();
		
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
	public static List<Field> getRequirableFields(){
		List<Field> allFields = getAllFields();
		
		allFields.removeAll(getNonRequirableFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getRequirableFields().
	 */
	private static List<Field> getNonRequirableFields(){
		List<Field> fields = new ArrayList<Field>();
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		
		Field attachment = fieldManager.getField(IssueFieldConstants.ATTACHMENT);
		Field comment = fieldManager.getField(IssueFieldConstants.COMMENT);
		Field created = fieldManager.getField(IssueFieldConstants.CREATED);
		Field estimate = fieldManager.getField(IssueFieldConstants.TIME_ORIGINAL_ESTIMATE);
		Field issuekey = fieldManager.getField(IssueFieldConstants.ISSUE_KEY);
		Field issuelinks = fieldManager.getField(IssueFieldConstants.ISSUE_LINKS);
		Field issuetype = fieldManager.getField(IssueFieldConstants.ISSUE_TYPE);
		Field project = fieldManager.getField(IssueFieldConstants.PROJECT);
//		Field remaining = ManagerFactory.getFieldManager().getField("timeestimate");
		Field status = fieldManager.getField(IssueFieldConstants.STATUS);
		Field subtasks = fieldManager.getField(IssueFieldConstants.SUBTASKS);
		Field thumbnail = fieldManager.getField(IssueFieldConstants.THUMBNAIL);
		Field timeSpent = fieldManager.getField(IssueFieldConstants.TIME_SPENT);
		Field timetracking = fieldManager.getField(IssueFieldConstants.TIMETRACKING);
		Field updated = fieldManager.getField(IssueFieldConstants.UPDATED);
		Field votes = fieldManager.getField(IssueFieldConstants.VOTES);
		Field workratio = fieldManager.getField(IssueFieldConstants.WORKRATIO);
		Field security = fieldManager.getField(IssueFieldConstants.SECURITY);
		
		fields.add(attachment);
		fields.add(comment);
		fields.add(created);
		fields.add(estimate);
		fields.add(issuekey);
		fields.add(issuelinks);
		fields.add(issuetype);
		fields.add(project);
//		fields.add(remaining);
		fields.add(status);
		fields.add(subtasks);
		fields.add(thumbnail);
		fields.add(timeSpent);
		fields.add(timetracking);
		fields.add(updated);
		fields.add(votes);
		fields.add(workratio);
		fields.add(security);
		
		return fields;
	}
	
	/**
	 * @return a list of fields that could be chosen in Value-Field Condition.
	 */
	public static List<Field> getValueFieldConditionFields(){
		List<Field> allFields = getAllFields();
		
		allFields.removeAll(getNonValueFieldConditionFields());
		// Date fields are removed, because date comparison is not implemented yet.
		allFields.removeAll(getAllDateFields());
		
		return allFields;
	}
	
	/**
	 * @return a list of fields that will be eliminated from getValueFieldConditionFields().
	 */
	private static List<Field> getNonValueFieldConditionFields(){
		List<Field> fields = new ArrayList<Field>();
		
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		
		Field attachment = fieldManager.getField("attachment");
		Field versions = fieldManager.getField("versions");
		Field comment = fieldManager.getField("comment");
		Field components = fieldManager.getField("components");
		Field created = fieldManager.getField("created");
		Field fixVersions = fieldManager.getField("fixVersions");
		Field thumbnail = fieldManager.getField("thumbnail");
		Field issuelinks = fieldManager.getField("issuelinks");
		Field issuekey = fieldManager.getField("issuekey");
		Field subtasks = fieldManager.getField("subtasks");
		Field timetracking = fieldManager.getField("timetracking");
		Field updated = fieldManager.getField("updated");
		Field votes = fieldManager.getField("votes");
		Field workratio = fieldManager.getField(IssueFieldConstants.WORKRATIO);

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
		fields.add(workratio);
		
		return fields;
	}
	
	/**
	 * @param cal
	 * 
	 * Clear the time part from a given Calendar.
	 * 
	 */
	public static void clearCalendarTimePart(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
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
	
	/**
	 * Get comparator for sorting fields.
	 * @return
	 */
	private static Comparator<Field> getComparator() {
		ApplicationProperties ap = new ApplicationPropertiesImpl();
		I18nBean i18n = new I18nBean(ap.getDefaultLocale().getDisplayName());

		return new NameComparatorEx(i18n); 
	}
}