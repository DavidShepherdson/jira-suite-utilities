package com.googlecode.jsu.util;

import static com.atlassian.jira.ComponentManager.getComponentInstanceOfType;
import static com.atlassian.jira.ComponentManager.getOSGiComponentInstanceOfType;
import static com.atlassian.plugin.util.Assertions.notNull;

/**
 * Utils class for loading components. That's dirty hack for new 2nd plugins version.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public final class ComponentUtils {
	/**
	 * Get components from different containers.
	 * 
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T> T getComponent(Class<T> clazz) {
		notNull("class", clazz);
		
		T component = getComponentInstanceOfType(clazz);
		
		if (component == null) {
			// Didn't find in pico container
			component = getOSGiComponentInstanceOfType(clazz);
		}

		notNull(clazz.getSimpleName(), component);
		
		return component;
	}
}
