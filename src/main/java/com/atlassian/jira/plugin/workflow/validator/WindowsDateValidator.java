package com.atlassian.jira.plugin.workflow.validator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.atlassian.jira.ManagerFactory;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.issue.fields.screen.FieldScreen;
import com.atlassian.jira.plugin.util.CommonPluginUtils;
import com.atlassian.jira.plugin.util.WorkflowUtils;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.WorkflowDescriptor;

/**
 * @author Gustavo Martin
 * 
 * This validator compare two datetime fields, and verifies if the first of them,
 * is less than the second plus a number of days. 
 * And returning an exception if it doesn't fulfill the condition.
 *  
 */
public class WindowsDateValidator implements Validator {
	
	Issue issue = null;
	InvalidInputException invIn = null;
	WorkflowException invWork = null;
	Map mCFields = null;
	boolean isNew = false;
	boolean hasViewScreen = false;
	FieldScreen fieldScreen = null;
	
	public WindowsDateValidator() {
	}
	
	/* (non-Javadoc)
	 * @see com.opensymphony.workflow.Validator#validate(java.util.Map, java.util.Map, com.opensymphony.module.propertyset.PropertySet)
	 */
	public void validate(Map transientVars, Map args, PropertySet ps)
	throws InvalidInputException, WorkflowException {
		
		issue = (Issue) transientVars.get("issue");
		
		if(issue.getKey()==null){
			isNew = true;
		}
		
		// Obtains if this transition has an screen associated.
		WorkflowDescriptor workflowDescriptor = (WorkflowDescriptor)transientVars.get("descriptor");
		Integer actionId = (Integer)transientVars.get("actionId");
		ActionDescriptor actionDescriptor = workflowDescriptor.getAction(actionId.intValue());
		
		fieldScreen = WorkflowUtils.getFieldScreen(actionDescriptor);
		if (fieldScreen!=null){
			hasViewScreen = true;
		}
		
		String date1 = (String) args.get("date1Selected");
		String date2 = (String) args.get("date2Selected");
		String sWindow = (String) args.get("windowsDays");
		
		Field fldDate1 = WorkflowUtils.getFieldFromKey(date1);
		Field fldDate2 = WorkflowUtils.getFieldFromKey(date2);
		
		// Compare Dates.
		if ((fldDate1!=null) && (fldDate2!=null)){
			checkDatesCondition(fldDate1, fldDate2, sWindow);
		}
		
		if(isNew || !hasViewScreen){
			if (invWork!=null) throw invWork;
		}else{
			if (invIn!=null) throw invIn;
		}
	}
	
	/**
	 * @param fldDate1
	 * @param fldDate2
	 * @param window
	 * 
	 * It makes the comparison properly this.
	 */
	private void checkDatesCondition(Field fldDate1, Field fldDate2, String window) {
		
		boolean condOK = false;
		
		Object objDate1 = WorkflowUtils.getFieldValueFromIssue(issue, fldDate1);
		Object objDate2 = WorkflowUtils.getFieldValueFromIssue(issue, fldDate2);
		
		if((objDate1!=null) && (objDate2!=null)){
			// It Takes the Locale for inicialize dates.
			ApplicationProperties ap = ManagerFactory.getApplicationProperties();
			Locale locale = ap.getDefaultLocale();
			
			Calendar calDate1 = Calendar.getInstance(locale);
			Calendar calDate2 = Calendar.getInstance(locale);
			Calendar calWindowsDate = Calendar.getInstance(locale);
			
			calDate1.setTime((Date) objDate1);
			calDate2.setTime((Date) objDate2);
			calWindowsDate.setTime((Date) objDate2);
			calWindowsDate.add(Calendar.DATE, Integer.parseInt(window));
			
			CommonPluginUtils.clearCalendarTimePart(calDate1);
			CommonPluginUtils.clearCalendarTimePart(calDate2);
			
			Date date1 = calDate1.getTime();
			Date date2 = calDate2.getTime();
			Date windowsDate = calWindowsDate.getTime();
			
			int comparacion = date1.compareTo(windowsDate);
			
			if(comparacion < 0){
				comparacion = date1.compareTo(date2);
				
				if(comparacion>=0){
					condOK = true;
				}
			}
			
			if(!condOK){
				// Formats date to current locale, for display the Exception.
				SimpleDateFormat formatter = null;
				SimpleDateFormat defaultFormatter = null;
				defaultFormatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT));
				formatter = new SimpleDateFormat(ap.getDefaultString(APKeys.JIRA_DATE_PICKER_JAVA_FORMAT), locale);
				
				String errorMsg = "";
				try{
					errorMsg = " ( Between " + formatter.format(date2) + " and " + formatter.format(windowsDate) +  " )";
				}catch(IllegalArgumentException e){
					try{
						errorMsg = " ( Between " + defaultFormatter.format(date2) + " and " + defaultFormatter.format(windowsDate) +  " )";
					}catch(Exception e1){
						errorMsg = " ( Between " + date2 + " and " + windowsDate +  " )";
					}
				}
				
				// Sets Exception message.
				if(hasViewScreen){
					if(CommonPluginUtils.isFieldOnScreen(issue, fldDate1, fieldScreen)){
						setError(fldDate1, fldDate1.getName() + " is not within " + fldDate2.getName() + ", more " + window + " days. " + errorMsg);
					}else{
						setError(null, fldDate1.getName() + " is not within " + fldDate2.getName() + ", more " + window + " days. " + errorMsg);
					}
				}else{
					setError(null, fldDate1.getName() + " is not within " + fldDate2.getName() + ", more " + window + " days. " + errorMsg);
				}
			}
			
		}else{
			// If any of fields are null, validates if the field is required. Otherwise, doesn't throws an Exception.
			if(objDate1==null){
				validateRequired(fldDate1);
			}
			if(objDate2==null){
				validateRequired(fldDate2);
			}
		}
	}
	
	/**
	 * @param field
	 * @param errmsg
	 * 
	 * Sets an Exception if not fullfit the condition.
	 */
	private void setError(Field field, String errmsg) {
		if(isNew || !hasViewScreen){
			invWork = new WorkflowException(errmsg);
		}else{
			if (field == null) {
				if (invIn == null)
					invIn = new InvalidInputException(errmsg);
				else
					invIn.addError(errmsg);
			} else {
				if (invIn == null)
					invIn = new InvalidInputException(field.getId(), errmsg);
				else
					invIn.addError(field.getId(), errmsg);
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
			if(hasViewScreen){
				if(CommonPluginUtils.isFieldOnScreen(issue, fldDate, fieldScreen)){
					setError(fldDate, fldDate.getName() + " is required.");
				}else{
					setError(null, fldDate.getName() + " is required.");
				}
			}else{
				setError(null, fldDate.getName() + " is required.");
			}
		}
	}
	
}
