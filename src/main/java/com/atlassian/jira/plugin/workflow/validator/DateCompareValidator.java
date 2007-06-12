package com.atlassian.jira.plugin.workflow.validator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.annotation.Argument;
import com.atlassian.jira.plugin.annotation.TransientVariable;
import com.atlassian.jira.plugin.helpers.ConditionManager;
import com.atlassian.jira.plugin.helpers.ConditionType;
import com.atlassian.jira.plugin.helpers.YesNoManager;
import com.atlassian.jira.plugin.helpers.YesNoType;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.WorkflowException;

/**
 * @author Gustavo Martin
 * 
 * This validator compare two datetime fields, using the given comparison type. 
 * And returning an exception if it doesn't fulfill the condition.
 *  
 */
public class DateCompareValidator extends GenericValidator {
	@TransientVariable
	private Issue issue;

	@Argument("date1Selected")
	private String date1;
	
	@Argument("date2Selected")
	private String date2;

	@Argument("conditionSelected")
	private String condition;

	@Argument("includeTimeSelected")
	private String includeTime;

	/* (non-Javadoc)
	 * @see com.atlassian.jira.plugin.workflow.validator.GenericValidator#validate()
	 */
	protected void validate() throws InvalidInputException, WorkflowException {
		Field fldDate1 = WorkflowUtils.getFieldFromKey(date1);
		Field fldDate2 = WorkflowUtils.getFieldFromKey(date2);
		ConditionType cond = ConditionManager.getManager().getCondition(new Integer(condition));
		YesNoType ynTime = YesNoManager.getManager().getOption(new Integer(includeTime));
		
		// Compare Dates.
		if ((fldDate1 != null) && (fldDate2 != null)) {
			checkDatesCondition(fldDate1, fldDate2, cond.getValue(), ynTime.getValue());
		}
	}
	
	/**
	 * @param fldDate1
	 * @param fldDate2
	 * @param cond
	 * @param includeTime with or wothout the time part.
	 * 
	 * It makes the comparison properly this.
	 */
	private void checkDatesCondition(Field fldDate1, Field fldDate2, String cond, String includeTime) {
		boolean condOK = false;
		
		Object objDate1 = WorkflowUtils.getFieldValueFromIssue(issue, fldDate1);
		Object objDate2 = WorkflowUtils.getFieldValueFromIssue(issue, fldDate2);
		
		if ((objDate1!=null) && (objDate2!=null)) {
			// It Takes the Locale for inicialize dates.
			ApplicationProperties ap = ManagerFactory.getApplicationProperties();
			Locale locale = ap.getDefaultLocale();
			
			Calendar calDate1 = Calendar.getInstance(locale);
			Calendar calDate2 = Calendar.getInstance(locale);
			
			calDate1.setTime((Date) objDate1);
			calDate2.setTime((Date) objDate2);
			
			// If the comparison is only date part, cleans the time part.
			if (includeTime.equals(WorkflowUtils.BOOLEAN_NO)) {
				CommonPluginUtils.clearCalendarTimePart(calDate1);
				CommonPluginUtils.clearCalendarTimePart(calDate2);
			} else {
				calDate1.clear(Calendar.SECOND);
				calDate1.clear(Calendar.MILLISECOND);
				
				calDate2.clear(Calendar.SECOND);
				calDate2.clear(Calendar.MILLISECOND);
			}
			
			Date date1 = calDate1.getTime();
			Date date2 = calDate2.getTime();
			
			int comparacion = date1.compareTo(date2);
			
			if ((comparacion == 0) && ((cond.equals("<=")) || (cond.equals("=")) || (cond.equals(">="))))
				condOK = true;
			
			if ((comparacion < 0) && ((cond.equals("<")) || (cond.equals("<="))))
				condOK = true;
			
			if ((comparacion > 0) && ((cond.equals(">")) || (cond.equals(">="))))
				condOK = true;
			
			if ((comparacion != 0) && (cond.equals(WorkflowUtils.CONDITION_DIFFERENT)))
				condOK = true;
			
			// Dates are different. Throws an exception.
			if (!condOK) {
				String condString = WorkflowUtils.getConditionString(cond);
				
				// Formats date to current locale, for display the Exception.
				SimpleDateFormat formatter = null;
				SimpleDateFormat defaultFormatter = null;
				
				if (includeTime.equals(WorkflowUtils.BOOLEAN_NO)) {
					defaultFormatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT));
					formatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT), locale);
				}else{
					defaultFormatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT));
					formatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_TIME_PICKER_JAVA_FORMAT), locale);
				}
				
				String errorMsg = "";
				
				try{
					errorMsg = " ( " + formatter.format(objDate2) + " )";
				} catch (IllegalArgumentException e) {
					try {
						errorMsg = " ( " + defaultFormatter.format(objDate2) + " )";
					} catch(Exception e1) {
						errorMsg = " ( " + objDate2 + " )";
					}
				}
				
				this.setExceptionMessage(
						issue, fldDate1,
						fldDate1.getName() + " isn't " + condString + " " + fldDate2.getName() + errorMsg, 
						fldDate1.getName() + " isn't " + condString + " " + fldDate2.getName() + errorMsg
				);
			}
		} else {
			// If any of fields are null, validates if the field is required. Otherwise, doesn't throws an Exception.
			if (objDate1 == null) {
				validateRequired(fldDate1);
			}
			
			if (objDate2 == null) {
				validateRequired(fldDate2);
			}
		}
	}
	
	/**
	 * @param fldDate
	 * 
	 * Throws an Exception if the field is null, but it is required.
	 */
	private void validateRequired(Field fldDate){
		if(CommonPluginUtils.isFieldRequired(issue, fldDate)){
			this.setExceptionMessage(
					issue, fldDate, 
					fldDate.getName() + " is required.", 
					fldDate.getName() + " is required."
			);
		}
	}
}
