package com.atlassian.jira.plugin.helpers;

/**
 * @author Gustavo Martin
 * 
 * This class represents a Yes/No Type. Its values could be YES or NO. 
 * It will be used in Workflow Condition, Validator, or Function.
 *  
 */
public class YesNoType {
	
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
				YesNoType ct = (YesNoType) obj;
				if(getId().equals(ct.getId())){
					retVal = true;
				}
			}catch (ClassCastException cce){
				retVal = false;
			}
		}
		return retVal;
	}
	
	
}
