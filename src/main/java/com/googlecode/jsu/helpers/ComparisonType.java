package com.googlecode.jsu.helpers;

/**
 * @author Gustavo Martin
 * 
 * This class represents a Comparison Type. This will be used in Workflow Condition, Validator, or Function.
 *  
 */
public class ComparisonType {
	
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
				ComparisonType ct = (ComparisonType) obj;
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
