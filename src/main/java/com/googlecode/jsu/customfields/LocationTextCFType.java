package com.googlecode.jsu.customfields;

import static com.googlecode.jsu.util.ComponentUtils.getComponent;

import com.atlassian.jira.issue.customfields.converters.StringConverter;
import com.atlassian.jira.issue.customfields.impl.RenderableTextCFType;
import com.atlassian.jira.issue.customfields.manager.GenericConfigManager;
import com.atlassian.jira.issue.customfields.persistence.CustomFieldValuePersister;

/**
 * Wrapper on Jira RenderableTextCFType for using inside plugins v2.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class LocationTextCFType extends RenderableTextCFType {
	/**
	 * Default constructor without injection.
	 */
	public LocationTextCFType() {
		super(
				getComponent(CustomFieldValuePersister.class), 
				getComponent(StringConverter.class), 
				getComponent(GenericConfigManager.class)
		);
	}
}
