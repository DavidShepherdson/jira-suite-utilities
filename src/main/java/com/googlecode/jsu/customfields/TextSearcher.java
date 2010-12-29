package com.googlecode.jsu.customfields;

import static com.googlecode.jsu.util.ComponentUtils.getComponent;

import com.atlassian.jira.issue.customfields.searchers.transformer.CustomFieldInputHelper;
import com.atlassian.jira.jql.operand.JqlOperandResolver;
import com.atlassian.jira.web.FieldVisibilityManager;

/**
 * Wrapper on Jira TextSearcher for using inside plugins v2.
 *
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class TextSearcher extends com.atlassian.jira.issue.customfields.searchers.TextSearcher {
    /**
     * Default constructor without injection.
     */
    public TextSearcher() {
        super(
                getComponent(FieldVisibilityManager.class),
                getComponent(JqlOperandResolver.class),
                getComponent(CustomFieldInputHelper.class)
        );
    }
}
