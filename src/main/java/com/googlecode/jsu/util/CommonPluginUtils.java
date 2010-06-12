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

import org.apache.log4j.Logger;
import org.ofbiz.core.entity.model.ModelEntity;
import org.ofbiz.core.entity.model.ModelField;

import com.atlassian.core.ofbiz.CoreFactory;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
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
import com.atlassian.jira.issue.fields.NavigableField;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutStorageException;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem;
import com.atlassian.jira.issue.fields.screen.FieldScreenTab;
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
	private static final Logger log = Logger.getLogger(CommonPluginUtils.class);
	
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
			log.error("Unable to load navigable fields", e);
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
			final Set<NavigableField> fields = fieldManager.getAllAvailableNavigableFields();
			
			for (Field f : fields) {
				allFields.add(f);
			}
		} catch (FieldException e) {
			log.error("Unable to load navigable fields", e);
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

				if (field.getId().equals(fieldScreenLayoutItem.getFieldId()) && isIssueHasField(issue, field)) {
					retVal = true;
				}
			}
		}
		
		return retVal;
	}
	
	/**
	 * Check is the issue has the field.
	 * 
	 * @param issue: issue to which the field belongs
	 * @param field: wished field
	 * @return if a field is available.
	 */
	public static boolean isIssueHasField(Issue issue, Field field) {
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		final String fieldId = field.getId();
		
		boolean isHidden = false;
		
		if (TIME_TRACKING_FIELDS.contains(fieldId)) {
			isHidden = !fieldManager.isTimeTrackingOn();
		} else {
			FieldVisibilityBean fieldVisibilityBean = new FieldVisibilityBean();

			isHidden = fieldVisibilityBean.isFieldHidden(field.getId(), issue);
		}
		
		if (isHidden) {
			// Looks like we found hidden field
			return false;
		}
		
		if (fieldManager.isCustomField(field)) {
			CustomField customField = (CustomField) field;
			FieldConfig config = customField.getRelevantConfig(issue);
			
			return (config != null); 
		}
		
		return true;
	}
	
	public static FieldLayoutItem getFieldLayoutItem(Issue issue, Field field) throws FieldLayoutStorageException {
		final FieldLayoutManager fieldLayoutManager = ComponentManager.getInstance().getFieldLayoutManager();

		FieldLayout layout = fieldLayoutManager.getFieldLayout(
				issue.getProjectObject().getGenericValue(), 
				issue.getIssueTypeObject().getId()
		);
		
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
			log.error("Unable to check is field required", e);
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
		return asFields(
				IssueFieldConstants.ATTACHMENT,
				IssueFieldConstants.COMMENT,
				IssueFieldConstants.COMPONENTS,
// For issue #65
//				IssueFieldConstants.FIX_FOR_VERSIONS,
//				IssueFieldConstants.AFFECTED_VERSIONS,
				IssueFieldConstants.ISSUE_LINKS,
				IssueFieldConstants.SECURITY,
				IssueFieldConstants.SUBTASKS,
				IssueFieldConstants.THUMBNAIL,
				IssueFieldConstants.TIMETRACKING
		);
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
		return asFields(
				IssueFieldConstants.ATTACHMENT,
				IssueFieldConstants.COMMENT,
				IssueFieldConstants.COMPONENTS,
				IssueFieldConstants.CREATED,
				IssueFieldConstants.TIMETRACKING,
				IssueFieldConstants.TIME_ORIGINAL_ESTIMATE,
				IssueFieldConstants.TIME_ESTIMATE,
				IssueFieldConstants.TIME_SPENT,
				IssueFieldConstants.AGGREGATE_TIME_ORIGINAL_ESTIMATE,
				IssueFieldConstants.AGGREGATE_TIME_ESTIMATE,
				IssueFieldConstants.AGGREGATE_PROGRESS,
				IssueFieldConstants.ISSUE_KEY,
				IssueFieldConstants.ISSUE_LINKS,
				IssueFieldConstants.ISSUE_TYPE,
				IssueFieldConstants.PRIORITY,
				IssueFieldConstants.PROJECT,
				IssueFieldConstants.SECURITY,
				IssueFieldConstants.STATUS,
				IssueFieldConstants.SUBTASKS,
				IssueFieldConstants.THUMBNAIL,
				IssueFieldConstants.UPDATED,
				IssueFieldConstants.VOTES,
				IssueFieldConstants.WORKRATIO
		);
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
		return asFields(
				IssueFieldConstants.ATTACHMENT,
				IssueFieldConstants.COMMENT,
				IssueFieldConstants.CREATED,
				IssueFieldConstants.TIMETRACKING,
				IssueFieldConstants.TIME_ORIGINAL_ESTIMATE,
				IssueFieldConstants.PROGRESS,
				IssueFieldConstants.AGGREGATE_TIME_ORIGINAL_ESTIMATE,
				IssueFieldConstants.AGGREGATE_PROGRESS,
				IssueFieldConstants.ISSUE_KEY,
				IssueFieldConstants.ISSUE_LINKS,
				IssueFieldConstants.ISSUE_TYPE,
				IssueFieldConstants.PROJECT,
				IssueFieldConstants.STATUS,
				IssueFieldConstants.SUBTASKS,
				IssueFieldConstants.THUMBNAIL,
				IssueFieldConstants.UPDATED,
				IssueFieldConstants.VOTES,
				IssueFieldConstants.WORKRATIO,
				IssueFieldConstants.SECURITY
		);
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
		return asFields(
				IssueFieldConstants.ATTACHMENT,
				IssueFieldConstants.COMMENT,
				IssueFieldConstants.COMPONENTS,
				IssueFieldConstants.CREATED,
				IssueFieldConstants.AFFECTED_VERSIONS,
				IssueFieldConstants.FIX_FOR_VERSIONS,
				IssueFieldConstants.ISSUE_KEY,
				IssueFieldConstants.ISSUE_LINKS,
				IssueFieldConstants.SUBTASKS,
				IssueFieldConstants.THUMBNAIL,
				IssueFieldConstants.TIMETRACKING,
				IssueFieldConstants.UPDATED,
				IssueFieldConstants.VOTES,
				IssueFieldConstants.WORKRATIO
		);
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
	
	/**
	 * Convert array of names into list of fields
	 * @param names
	 * @return
	 */
	private static List<Field> asFields(String ... names) {
		final FieldManager fieldManager = ManagerFactory.getFieldManager();
		List<Field> result = new ArrayList<Field>(names.length);
		
		for (String name : names) {
			result.add(fieldManager.getField(name));
		}
		
		return result;
	}
}