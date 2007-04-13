package com.atlassian.jira.plugin.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/**
 * Class responsible for setting up all that is necessary for the execution of
 * the plugin.
 * 
 * @author Cristiane Fontana
 * @version 1.0 Plugin creation.
 * 
 */
public class WorkflowUpdateIssueCustomFieldFunctionPluginFactory extends
		AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {

	public static final String PARAM_FIELD_ID = "fieldId";

	public static final String PARAM_FIELD_VALUE = "fieldValue";

	public static final String TARGET_FIELD_NAME = "field.name";

	public static final String TARGET_FIELD_VALUE = "field.value";

	private static final String PARAM_NAME = "eventType";

	private final CustomFieldManager customFieldManager;

	private final List fields = new ArrayList(2);

	public WorkflowUpdateIssueCustomFieldFunctionPluginFactory(
			CustomFieldManager customFieldManager) {
		this.customFieldManager = customFieldManager;
		List customFields = customFieldManager.getCustomFieldObjects();
		for (int i = 0; i < customFields.size(); i++) {
			fields.add((CustomField)customFields.get(i));			
		}
	}

	public Map getDescriptorParams(Map conditionParams) {
		Map params = new HashMap();
		String fieldId = extractSingleParam(conditionParams, "fieldId");
		params.put("field.name", fieldId);
		String fieldValue = extractSingleParam(conditionParams, "fieldValue");
		params.put("field.value", fieldValue);
		return params;
	}

	protected void getVelocityParamsForEdit(Map velocityParams,
			AbstractDescriptor descriptor) {
		getVelocityParamsForInput(velocityParams);
		if (!(descriptor instanceof FunctionDescriptor))
			throw new IllegalArgumentException(
					"Descriptor must be a FunctionDescriptor.");
		FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
		velocityParams.put("fieldId", functionDescriptor.getArgs().get(
				"field.name"));
		String value = (String) functionDescriptor.getArgs().get("field.value");
		if (value == null || value.equals("null"))
			velocityParams.put("fieldValue", null);
		else
			velocityParams.put("fieldValue", value);
	}

	protected void getVelocityParamsForInput(Map velocityParams) {
		velocityParams.put("fields", fields);
	}

	protected void getVelocityParamsForView(Map velocityParams,
			AbstractDescriptor descriptor) {
		if (!(descriptor instanceof FunctionDescriptor)) {
			throw new IllegalArgumentException(
					"Descriptor must be a FunctionDescriptor.");
		} else {
			FunctionDescriptor functionDescriptor = (FunctionDescriptor) descriptor;
			velocityParams.put("fieldId", customFieldManager
					.getCustomFieldObject(
							(String) functionDescriptor.getArgs().get(
									"field.name")).getNameKey());
			velocityParams.put("fieldValue", functionDescriptor.getArgs().get(
					"field.value"));
			return;
		}
	}

}
