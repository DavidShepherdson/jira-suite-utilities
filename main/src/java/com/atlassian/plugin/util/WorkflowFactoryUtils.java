package com.atlassian.plugin.util;

import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * @author Alexey Abashev
 */
public class WorkflowFactoryUtils {
	@SuppressWarnings("unchecked")
	public static Field getFieldByName(AbstractDescriptor descriptor, String name) { 
		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
		Map args = functionDescriptor.getArgs();
		String fieldKey = (String) args.get(name);

		return (Field) WorkflowUtils.getFieldFromKey(fieldKey);
	}
	
	private WorkflowFactoryUtils() {
	}
}
