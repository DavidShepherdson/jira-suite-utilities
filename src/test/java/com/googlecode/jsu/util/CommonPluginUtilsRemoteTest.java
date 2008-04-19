package com.googlecode.jsu.util;

import java.sql.Timestamp;

import org.apache.cactus.ServletTestCase;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class CommonPluginUtilsRemoteTest extends ServletTestCase {
	public void testGetAllFields() {
		CommonPluginUtils.getAllFields();
	}
	
	public void testNiceDate() {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		String niceDate = CommonPluginUtils.getNiceDate(timestamp);
		
		assertNotNull("Nice date must be not null", niceDate);
		assertTrue("Nice date must be not empty", niceDate.length() > 10);
	}
}
