package com.atlassian.jira.plugin.helpers;

/**
 * @author Gustavo Martin
 * 
 * This class represents a Condition Type. This will be used in Workflow Condition, Validator, or Function.
 *  
 */
public class ConditionType {
	
	private Integer id;
	private String value;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public boolean equals(Object obj) {
		boolean retVal = false;
		
		if(obj!=null){
			try{
				ConditionType cond = (ConditionType) obj;
				if(getId().equals(cond.getId())){
					retVal = true;
				}
			}catch (ClassCastException cce){
				retVal = false;
			}
		}
		return retVal;
	}
	public String toString() {
		String retVal = "";
		
		if(id.equals(new Integer(1)))
			retVal = "greater than";
		
		if(id.equals(new Integer(2)))
			retVal = "greater than or equal to";
		
		if(id.equals(new Integer(3)))
			retVal = "equal to";
		
		if(id.equals(new Integer(4)))
			retVal = "less than or equal to";
		
		if(id.equals(new Integer(5)))
			retVal = "less than";
		
		if(id.equals(new Integer(6)))
			retVal = "not equal to";
		
		return retVal;
	}
	
}
