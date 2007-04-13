package com.atlassian.jira.plugin.util;

import org.apache.log4j.Logger;

/**
 * @author Alexey Abashev
 */
public final class LogUtils {
	private static final Logger GENERAL = Logger.getLogger("com.atlassian.plugin.suite-utilities");
	
	public static Logger getGeneral() {
		return GENERAL;
	}

	private LogUtils() {
	}
}
