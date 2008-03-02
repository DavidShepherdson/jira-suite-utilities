package com.googlecode.jsu.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.ofbiz.core.entity.GenericEntityException;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.core.user.GroupUtils;
import com.atlassian.core.user.UserUtils;
import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueFieldConstants;
import com.atlassian.jira.issue.IssueRelationConstants;
import com.atlassian.jira.issue.ModifiedValue;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutStorageException;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder;
import com.atlassian.jira.issue.worklog.WorkRatio;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.workflow.WorkflowActionsBean;
import com.opensymphony.user.EntityNotFoundException;
import com.opensymphony.user.Group;
import com.opensymphony.user.User;
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
	 * @return a list of boolean values.
	 */
	public static List<String> getBooleanList() {
		List<String> booleanList = new ArrayList<String>();

		booleanList.add(BOOLEAN_YES);
		booleanList.add(BOOLEAN_NO);

		return booleanList;
	}

	/**
	 * @return a list of comparison types.
	 */
	public static List<String> getComparisonList() {
		List<String> comparisonList = new ArrayList<String>();

		comparisonList.add(COMPARISON_TYPE_STRING);
		comparisonList.add(COMPARISON_TYPE_NUMBER);
		// Not implemented yet.
		//comparisonList.add(COMPARISON_TYPE_DATE);

		return comparisonList;
	}

	/**
	 * @return a list of conditions types.
	 */
	public static List<String> getConditionList() {
		List<String> conditionList = new ArrayList<String>();

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
	public static String getConditionString(String condition) {
		String retVal = null;

		if (condition.equals(CONDITION_MINOR))
			retVal = "minor to";
		if (condition.equals(CONDITION_MINOR_EQUAL))
			retVal = "minor or equal to";
		if (condition.equals(CONDITION_EQUAL))
			retVal = "equal to";
		if (condition.equals(CONDITION_MAJOR_EQUAL))
			retVal = "greater or equal than";
		if (condition.equals(CONDITION_MAJOR))
			retVal = "greater than";
		if (condition.equals(CONDITION_DIFFERENT))
			retVal = "different to";

		return retVal;
	}

	/**
	 * @param key
	 * @return a String with the field name from given key.
	 */
	public static String getFieldNameFromKey(String key) {
		return getFieldFromKey(key).getName();
	}

	/**
	 * @param key
	 * @return a Field object from given key. (Field or Custom Field).
	 */
	public static Field getFieldFromKey(String key) {
		FieldManager fieldManager = ManagerFactory.getFieldManager();
		Field field;
		
		if (fieldManager.isCustomField(key)) {
			field = fieldManager.getCustomField(key);
		} else {
			field = fieldManager.getField(key);
		}
		
		if (field == null) {
			throw new IllegalArgumentException("Unable to find field '" + key + "'");
		}
		
		return field;
	}

	/**
	 * @param issue
	 *            an issue object.
	 * @param field
	 *            a field object. (May be a Custom Field)
	 * @return an Object
	 * 
	 * It returns the value of a field within issue object. May be a Collection,
	 * a List, a Strong, or any FildType within JIRA.
	 * 
	 */
	public static Object getFieldValueFromIssue(Issue issue, Field field) {
		FieldManager fldManager = ManagerFactory.getFieldManager();
		Object retVal = null;

		try {
			if (fldManager.isCustomField(field)) {
				// Return the CustomField value. It will be any object.
				CustomField customField = (CustomField) field;
				retVal = issue.getCustomFieldValue(customField);
			} else {
				String fieldId = field.getId();
				Collection retCollection = null;
				boolean isEmpty = false;

				// Special treatment of fields.
				if (fieldId.equals(IssueFieldConstants.ATTACHMENT)) {
					// return a collection with the attachments associated to given issue.
					retCollection = (Collection) issue.getExternalFieldValue(fieldId);
					if (retCollection == null || retCollection.isEmpty()) {
						isEmpty = true;
					} else {
						retVal = retCollection;
					}
				} else if (fieldId.equals(IssueFieldConstants.AFFECTED_VERSIONS)) {
					retCollection = issue.getAffectedVersions();
					if (retCollection == null || retCollection.isEmpty()) {
						isEmpty = true;
					} else {
						retVal = retCollection;
					}
				} else if (fieldId.equals(IssueFieldConstants.COMMENT)) {
					// return a list with the comments of a given issue.
					try {
						retCollection = ManagerFactory.getIssueManager().getEntitiesByIssue(IssueRelationConstants.COMMENTS, issue.getGenericValue());
						if (retCollection == null || retCollection.isEmpty()) {
							isEmpty = true;
						} else {
							retVal = retCollection;
						}
					} catch (GenericEntityException e) {
						retVal = null;
					}
				} else if (fieldId.equals(IssueFieldConstants.COMPONENTS)) {
					retCollection = issue.getComponents();
					if (retCollection == null || retCollection.isEmpty()) {
						isEmpty = true;
					} else {
						retVal = retCollection;
					}
				} else if (fieldId.equals(IssueFieldConstants.FIX_FOR_VERSIONS)) {
					retCollection = issue.getFixVersions();
					if (retCollection == null || retCollection.isEmpty()) {
						isEmpty = true;
					} else {
						retVal = retCollection;
					}
				} else if (fieldId.equals(IssueFieldConstants.THUMBNAIL)) {
					// Not implemented, yet.
					isEmpty = true;
				} else if (fieldId.equals(IssueFieldConstants.ISSUE_TYPE)) {
					retVal = issue.getIssueTypeObject();
				} else if (fieldId.equals(IssueFieldConstants.TIMETRACKING)) {
					// Not implemented, yet.
					isEmpty = true;
				} else if (fieldId.equals(IssueFieldConstants.ISSUE_LINKS)) {
					retVal = ComponentManager.getInstance().getIssueLinkManager().getIssueLinks(issue.getId());
				} else if (fieldId.equals(IssueFieldConstants.WORKRATIO)) {
					retVal = String.valueOf(WorkRatio.getWorkRatio(issue));
				} else if (fieldId.equals(IssueFieldConstants.ISSUE_KEY)) {
					retVal = issue.getKey();
				} else if (fieldId.equals(IssueFieldConstants.SUBTASKS)) {
					retCollection = issue.getSubTasks();
					
					if (retCollection == null || retCollection.isEmpty()) {
						isEmpty = true;
					} else {
						retVal = retCollection;
					}
				} else if (fieldId.equals(IssueFieldConstants.PRIORITY)) {
					retVal = issue.getPriorityObject();
				} else if (fieldId.equals(IssueFieldConstants.RESOLUTION)) {
					retVal = issue.getResolutionObject();
				} else if (fieldId.equals(IssueFieldConstants.STATUS)) {
					retVal = issue.getStatusObject();
				} else if (fieldId.equals(IssueFieldConstants.PROJECT)) {
					retVal = issue.getProject();
				} else if (fieldId.equals(IssueFieldConstants.SECURITY)) {
					retVal = issue.getSecurityLevel();
				} else if (fieldId.equals(IssueFieldConstants.TIME_ESTIMATE)) {
					retVal = issue.getEstimate();
				} else if (fieldId.equals(IssueFieldConstants.ASSIGNEE)) {
					retVal = issue.getAssignee();
				} else if (fieldId.equals(IssueFieldConstants.REPORTER)) {
					retVal = issue.getReporter();
				} else if (fieldId.equals(IssueFieldConstants.DESCRIPTION)) {
					retVal = issue.getDescription();
				} else if (fieldId.equals(IssueFieldConstants.ENVIRONMENT)) {
					retVal = issue.getEnvironment();
				} else if (fieldId.equals(IssueFieldConstants.SUMMARY)) {
					retVal = issue.getSummary();
				} else {
					LogUtils.getGeneral().error("Default field \"" + field + "\" is not supported");

					GenericValue gvIssue = issue.getGenericValue();
					
					if (gvIssue != null) {
						retVal = gvIssue.get(fieldId);
					}
				}
			}
		} catch (NullPointerException e) {
			retVal = null;
			LogUtils.getGeneral().error("Unable to get field \"" + field + "\" value", e);
		}

		return retVal;
	}

	/**
	 * Sets specified value to the field for the issue.
	 * 
	 * @param issue
	 * @param field
	 * @param value
	 */
	public static void setFieldValue(MutableIssue issue, Field field, Object value) {
		FieldManager fldManager = ManagerFactory.getFieldManager();

		if (fldManager.isCustomField(field)) {
			CustomField customField = (CustomField) field;
			Object oldValue = issue.getCustomFieldValue(customField);

			try {
				FieldLayoutItem fieldLayoutItem = CommonPluginUtils.getFieldLayoutItem(issue, field);

				customField.updateValue(
						fieldLayoutItem, 
						issue, 
						new ModifiedValue(oldValue, value),
						new DefaultIssueChangeHolder()
				);
			} catch (FieldLayoutStorageException e) {
				LogUtils.getGeneral().error("Unable to get field layout item", e);

				throw new IllegalStateException(e);
			}
		} else {
			final String fieldId = field.getId();

			// Special treatment of fields.
			if (fieldId.equals(IssueFieldConstants.ATTACHMENT)) {
				throw new IllegalArgumentException("Not implemented");
				//				// return a collection with the attachments associated to given issue.
				//				retCollection = (Collection)issue.getExternalFieldValue(fieldId);
				//				if(retCollection==null || retCollection.isEmpty()){
				//					isEmpty = true;
				//				}else{
				//					retVal = retCollection;
				//				}
			} else if (fieldId.equals(IssueFieldConstants.AFFECTED_VERSIONS)) {
				if (value == null) {
					issue.setAffectedVersions(Collections.EMPTY_SET);
				} else if (value instanceof String) {
					VersionManager versionManager = ComponentManager.getInstance().getVersionManager();
					Version v = versionManager.getVersion(issue.getProjectObject().getId(), (String) value);

					if (v != null) {
						issue.setAffectedVersions(Arrays.asList(v));
					} else {
						throw new IllegalArgumentException("Wrong affected version value");
					}
				} else if (value instanceof Version) {
					issue.setAffectedVersions(Arrays.asList(value));
				} else {
					throw new IllegalArgumentException("Wrong affected version value");
				}
			} else if (fieldId.equals(IssueFieldConstants.COMMENT)) {
				throw new IllegalArgumentException("Not implemented");

				//				// return a list with the comments of a given issue.
				//				try {
				//					retCollection = ManagerFactory.getIssueManager().getEntitiesByIssue(IssueRelationConstants.COMMENTS, issue.getGenericValue());
				//					if(retCollection==null || retCollection.isEmpty()){
				//						isEmpty = true;
				//					}else{
				//						retVal = retCollection;
				//					}
				//				} catch (GenericEntityException e) {
				//					retVal = null;
				//				}
			} else if (fieldId.equals(IssueFieldConstants.COMPONENTS)) {
				throw new IllegalArgumentException("Not implemented");

				//				retCollection = issue.getComponents();
				//				if(retCollection==null || retCollection.isEmpty()){
				//					isEmpty = true;
				//				}else{
				//					retVal = retCollection;
				//				}
			} else if (fieldId.equals(IssueFieldConstants.FIX_FOR_VERSIONS)) {
				if (value == null) {
					issue.setFixVersions(Collections.EMPTY_SET);
				} else if (value instanceof String) {
					VersionManager versionManager = ComponentManager.getInstance().getVersionManager();
					Version v = versionManager.getVersion(issue.getProjectObject().getId(), (String) value);

					if (v != null) {
						issue.setFixVersions(Arrays.asList(v));
					}
				} else if (value instanceof Version) {
					issue.setFixVersions(Arrays.asList(value));
				} else {
					throw new IllegalArgumentException("Wrong fix version value");
				}
			} else if (fieldId.equals(IssueFieldConstants.THUMBNAIL)) {
				throw new IllegalArgumentException("Not implemented");

				//				// Not implemented, yet.
				//				isEmpty = true;
			} else if (fieldId.equals(IssueFieldConstants.ISSUE_TYPE)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retVal = issue.getIssueTypeObject();
			} else if (fieldId.equals(IssueFieldConstants.TIMETRACKING)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				// Not implemented, yet.
				//				isEmpty = true;
			} else if (fieldId.equals(IssueFieldConstants.ISSUE_LINKS)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retVal = ComponentManager.getInstance().getIssueLinkManager().getIssueLinks(issue.getId());
			} else if (fieldId.equals(IssueFieldConstants.WORKRATIO)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retVal = String.valueOf(WorkRatio.getWorkRatio(issue));
			} else if (fieldId.equals(IssueFieldConstants.ISSUE_KEY)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retVal = issue.getKey();
			} else if (fieldId.equals(IssueFieldConstants.SUBTASKS)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retCollection = issue.getSubTasks();
				//				if(retCollection==null || retCollection.isEmpty()){
				//					isEmpty = true;
				//				}else{
				//					retVal = retCollection;
				//				}
			} else if (fieldId.equals(IssueFieldConstants.PRIORITY)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retVal = issue.getPriorityObject();
			} else if (fieldId.equals(IssueFieldConstants.RESOLUTION)) {
				throw new IllegalArgumentException("Not implemented");
				//
				//				retVal = issue.getResolutionObject();
			} else if (fieldId.equals(IssueFieldConstants.STATUS)) {
				throw new IllegalArgumentException("Not implemented");
				//				retVal = issue.getStatusObject();
			} else if (fieldId.equals(IssueFieldConstants.PROJECT)) {
				throw new IllegalArgumentException("Not implemented");
				//				retVal = issue.getProject();
			} else if (fieldId.equals(IssueFieldConstants.SECURITY)) {
				throw new IllegalArgumentException("Not implemented");
				//				retVal = issue.getSecurityLevel();
			} else if (fieldId.equals(IssueFieldConstants.ASSIGNEE)) {
				if (value instanceof User) {
					issue.setAssignee((User) value);
				} else if (value instanceof String) {
					try {
						User user = UserUtils.getUser((String) value);
						if (null != user) {
							issue.setAssignee(user);
						}
					} catch (EntityNotFoundException e) {
						throw new IllegalArgumentException(String.format("User \"%s\" not found", value));
					}
				}
			} else if (fieldId.equals(IssueFieldConstants.DUE_DATE)) {
				if (value == null) {
					issue.setDueDate(null);
				}
				
				if (value instanceof Timestamp) {
					issue.setDueDate((Timestamp) value);
				} else if (value instanceof String) {
					ApplicationProperties properties = ManagerFactory.getApplicationProperties();
					SimpleDateFormat formatter = new SimpleDateFormat(
							properties.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT)
					);

					try {
						Date date = formatter.parse((String) value);
						
						if (date != null) {
							issue.setDueDate(new Timestamp(date.getTime()));
						} else {
							issue.setDueDate(null);
						}
					} catch (ParseException e) {
						throw new IllegalArgumentException("Wrong date format exception for \"" + value + "\"");
					}
				}
			}
		}
	}

	/**
	 * Method sets value for issue field. Field was defined as string
	 * 
	 * @param issue
	 *            Muttable issue for changing
	 * @param fieldKey
	 *            Field name
	 * @param value
	 *            Value for setting
	 */
	public static void setFieldValue(MutableIssue issue, String fieldKey, Object value) {
		final Field field = (Field) WorkflowUtils.getFieldFromKey(fieldKey);

		setFieldValue(issue, field, value);
	}

	/**
	 * @param issue
	 *            an issue object.
	 * @param field
	 *            a field object. (May be a Custom Field)
	 * @return an String.
	 * 
	 * It returns the value of a field within issue object as String. Instead
	 * null, it will return an empty String ( "" ).
	 * 
	 */
	public static String getFieldValueFromIssueAsString(Issue issue, Field field) throws UnsupportedOperationException {

		FieldManager fldManager = ManagerFactory.getFieldManager();
		String retVal = null;

		try {
			if (fldManager.isCustomField(field)) {
				CustomField customField = (CustomField) field;
				if (issue.getCustomFieldValue(customField) != null) {
					retVal = issue.getCustomFieldValue(customField).toString();
				} else {
					retVal = "";
				}
			} else {
				String fieldId = field.getId();

				// Unsupported fields as single String value.
				if (fieldId.equals("versions")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("attachment")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("comment")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("components")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("fixVersions")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("thumbnail")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("issuelinks")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("subtasks")) {
					throw new UnsupportedOperationException();
				}
				if (fieldId.equals("timetracking")) {
					throw new UnsupportedOperationException();
				}

				// Special treatment of fields.
				if (fieldId.equals("issuetype")) {
					if (issue.getIssueTypeObject() != null) {
						retVal = issue.getIssueTypeObject().getNameTranslation();
					} else {
						retVal = "";
					}
				}
				if (fieldId.equals("workratio")) {
					retVal = String.valueOf(WorkRatio.getWorkRatio(issue));
				}
				if (fieldId.equals("priority")) {
					if (issue.getPriorityObject() != null) {
						retVal = issue.getPriorityObject().getNameTranslation();
					} else {
						retVal = "";
					}
				}
				if (fieldId.equals("project")) {
					if (issue.getProjectObject() != null) {
						retVal = issue.getProjectObject().getName();
					} else {
						retVal = "";
					}
				}
				if (fieldId.equals("resolution")) {
					if (issue.getResolutionObject() != null) {
						retVal = issue.getResolutionObject().getNameTranslation();
					} else {
						retVal = "";
					}
				}
				if (fieldId.equals("status")) {
					if (issue.getStatusObject() != null) {
						retVal = issue.getStatusObject().getNameTranslation();
					} else {
						retVal = "";
					}
				}
				if (fieldId.equals("security")) {
					if (issue.getSecurityLevel() != null) {
						retVal = issue.getSecurityLevel().getString("name");
					} else {
						retVal = "";
					}
				}
				if (fieldId.equals("issuekey")) {
					retVal = issue.getKey();
				}
				if (fieldId.equals(IssueFieldConstants.ASSIGNEE)) {
					retVal = issue.getAssigneeId();
				}
				if (fieldId.equals(IssueFieldConstants.REPORTER)) {
					retVal = issue.getReporterId();
				}

				// Get the value from the Generic Value of Issue.
				if (retVal == null) {
					GenericValue gvIssue = issue.getGenericValue();
					if (gvIssue.get(fieldId) != null) {
						retVal = gvIssue.get(fieldId).toString();

						if((fieldId.equals("timeoriginalestimate")) || 
								(fieldId.equals("timeestimate")) || 
								(fieldId.equals("timespent"))){

							retVal = String.valueOf(new Long(retVal).longValue() / 60);
						}

					} else {
						retVal = "";
					}

				}
			}
		} catch (NullPointerException e) {
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
	public static List<Group> getGroups(String strGroups, String splitter) {
		String[] groups = strGroups.split("\\Q" + splitter + "\\E");
		List<Group> groupList = new ArrayList<Group>(groups.length);

		for (String s : groups) {
			Group group = GroupUtils.getGroup(s);

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
	public static String getStringGroup(Collection<Group> groups, String splitter) {
		StringBuilder sb = new StringBuilder();

		for (Group g : groups) {
			sb.append(g.getName()).append(splitter);
		}

		return sb.toString();
	}

	/**
	 * @param strFields
	 * @param splitter
	 * @return a List of Field
	 * 
	 * Get Fields from a string.
	 * 
	 */
	public static List<Field> getFields(String strFields, String splitter) {
		String[] fields = strFields.split("\\Q" + splitter + "\\E");
		List<Field> fieldList = new ArrayList<Field>(fields.length);

		for (String s : fields) {
			Field field = ManagerFactory.getFieldManager().getField(s);

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
	public static String getStringField(Collection<Field> fields, String splitter) {
		StringBuilder sb = new StringBuilder();

		for (Field f : fields) {
			sb.append(f.getId()).append(splitter);
		}

		return sb.toString();
	}

	/**
	 * @param actionDescriptor
	 * @return the FieldScreen of the transition. Or null, if the transition
	 *         hasn't a screen asociated.
	 * 
	 * It obtains the fieldscreen for a transition, if it have one.
	 * 
	 */
	public static FieldScreen getFieldScreen(ActionDescriptor actionDescriptor) {
		return workflowActionsBean.getFieldScreenForView(actionDescriptor);
	}
}