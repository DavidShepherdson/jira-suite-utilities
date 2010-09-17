package com.googlecode.jsu.customfields;

import static com.googlecode.jsu.util.ComponentUtils.getComponent;

import com.atlassian.jira.issue.customfields.converters.SelectConverter;
import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.impl.SelectCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;

/**
 * Wrapper on Jira SelectCFType for using inside plugins v2.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class LocationSelectCFType extends SelectCFType {
	/**
	 * Default constructor without injection.
	 */
	public LocationSelectCFType() {
		super(
				getComponent(CustomFieldValuePersister.class), 
				getComponent(StringConverter.class), 
				getComponent(SelectConverter.class), 
				getComponent(OptionsManager.class), 
				getComponent(GenericConfigManager.class)
		);
	}
}
