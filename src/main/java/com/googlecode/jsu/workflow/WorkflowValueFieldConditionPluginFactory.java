package com.googlecode.jsu.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginConditionFactory;
import com.googlecode.jsu.helpers.ComparisonType;
import com.googlecode.jsu.helpers.ConditionCheckerFactory;
import com.googlecode.jsu.helpers.ConditionType;
import com.googlecode.jsu.util.CommonPluginUtils;
import com.googlecode.jsu.util.WorkflowUtils;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;

/**
 * @author Gustavo Martin.
 *
 * This class defines the parameters available for Value Field Condition.
 *
 */
public class WorkflowValueFieldConditionPluginFactory extends
        AbstractWorkflowPluginFactory implements WorkflowPluginConditionFactory {

    private final ConditionCheckerFactory conditionCheckerFactory;

    /**
     * @param conditionCheckerFactory
     */
    public WorkflowValueFieldConditionPluginFactory(ConditionCheckerFactory conditionCheckerFactory) {
        this.conditionCheckerFactory = conditionCheckerFactory;
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForInput(java.util.Map)
     */
    protected void getVelocityParamsForInput(Map velocityParams) {
        List fields = CommonPluginUtils.getValueFieldConditionFields();

        List<ConditionType> conditionList = conditionCheckerFactory.getConditionTypes();
        List<ComparisonType> comparisonList = conditionCheckerFactory.getComparisonTypes();

        velocityParams.put("val-fieldsList", fields);
        velocityParams.put("val-conditionList", conditionList);
        velocityParams.put("val-comparisonList", comparisonList);
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForEdit(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
     */
    protected void getVelocityParamsForEdit(
            Map velocityParams,	AbstractDescriptor descriptor
    ) {
        getVelocityParamsForInput(velocityParams);

        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
        Map args = conditionDescriptor.getArgs();

        String sField = (String) args.get("fieldsList");
        String conditionTypeId = (String) args.get("conditionList");
        String comparisonTypeId = (String) args.get("comparisonType");
        String fieldValue = (String) args.get("fieldValue");

        Field field = null;

        try {
            field = WorkflowUtils.getFieldFromKey(sField);
        } catch (Exception e) {
        }

        if (field != null) {
            ComparisonType comparisonType = conditionCheckerFactory.findComparisonById(comparisonTypeId);
            ConditionType conditionType = conditionCheckerFactory.findConditionById(conditionTypeId);

            velocityParams.put("val-fieldSelected", field);
            velocityParams.put("val-conditionSelected", conditionType);
            velocityParams.put("val-comparisonTypeSelected", comparisonType);
            velocityParams.put("val-fieldValue", fieldValue);
        }
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.AbstractWorkflowPluginFactory#getVelocityParamsForView(java.util.Map, com.opensymphony.workflow.loader.AbstractDescriptor)
     */
    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
        ConditionDescriptor conditionDescriptor = (ConditionDescriptor) descriptor;
        Map args = conditionDescriptor.getArgs();

        String sField = (String) args.get("fieldsList");
        String conditionTypeId = (String) args.get("conditionList");
        String comparisonTypeId = (String) args.get("comparisonType");
        String fieldValue = (String) args.get("fieldValue");

        Field field = null;

        try {
            field = WorkflowUtils.getFieldFromKey(sField);
        } catch (Exception e) {
        }

        if (field != null) {
            ComparisonType comparisonType = conditionCheckerFactory.findComparisonById(comparisonTypeId);
            ConditionType conditionType = conditionCheckerFactory.findConditionById(conditionTypeId);

            velocityParams.put("val-fieldSelected", field);
            velocityParams.put("val-conditionSelected", conditionType);
            velocityParams.put("val-comparisonTypeSelected", comparisonType);
            velocityParams.put("val-fieldValue", fieldValue);
        } else {
            velocityParams.put("val-errorMessage", "Unable to find field '" + sField + "'");
        }
    }

    /* (non-Javadoc)
     * @see com.googlecode.jsu.workflow.WorkflowPluginFactory#getDescriptorParams(java.util.Map)
     */
    public Map getDescriptorParams(Map conditionParams) {
        Map params = new HashMap();

        try {
            String field = extractSingleParam(conditionParams, "fieldsList");
            String fieldCondition = extractSingleParam(conditionParams, "conditionList");
            String comparisonType = extractSingleParam(conditionParams, "comparisonType");
            String fieldValue = extractSingleParam(conditionParams, "fieldValue");

            params.put("fieldsList", field);
            params.put("conditionList", fieldCondition);
            params.put("comparisonType", comparisonType);
            params.put("fieldValue", fieldValue);

        } catch(IllegalArgumentException iae) {
            // Aggregate so that Transitions can be added.
        }

        return params;
    }
}
