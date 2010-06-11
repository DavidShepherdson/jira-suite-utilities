package com.googlecode.jsu.helpers;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static com.googlecode.jsu.helpers.ConditionCheckerFactory.*;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class ConditionCheckerFactoryTest {
	private ConditionCheckerFactory factory;
	
	@Before
	public void initFactory() {
		Logger.getLogger("com.googlecode.jsu").setLevel(Level.DEBUG);
		
		this.factory = new ConditionCheckerFactory();
	}
	
	@Test
	public void numberComparisons() {
		final String val1 = "5";
		final String val2 = "10";
		final String val3 = "sss";
		final String val4 = null;
		
		ConditionChecker checker = factory.getChecker(NUMBER, EQUAL);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
		assertFalse(checker.checkValues(val4, val2));
		
		checker = factory.getChecker(NUMBER, NOT_EQUAL);
		
		assertFalse(checker.checkValues(val1, val1));
		assertTrue(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
		assertFalse(checker.checkValues(val4, val2));

		checker = factory.getChecker(NUMBER, LESS);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
		assertFalse(checker.checkValues(val4, val2));

		checker = factory.getChecker(NUMBER, LESS_EQUAL);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
		assertFalse(checker.checkValues(val4, val2));

		checker = factory.getChecker(NUMBER, GREATER);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
		assertFalse(checker.checkValues(val4, val2));

		checker = factory.getChecker(NUMBER, GREATER_EQUAL);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
		assertFalse(checker.checkValues(val4, val2));
	}
	
	@Test
	public void stringComparisons() {
		final String val1 = "string_1";
		final String val2 = "string_2";
		final String val3 = "3333";
		
		ConditionChecker checker = factory.getChecker(STRING, EQUAL);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));

		checker = factory.getChecker(STRING, NOT_EQUAL);
		
		assertTrue(checker.checkValues(val1, val1));
		assertFalse(checker.checkValues(val1, val2));
		assertFalse(checker.checkValues(val1, val3));
	}
	
	@Test
	public void dateComparisons() {
		// TODO
	}
	
	@Test
	public void dateWithoutTimeComparisons() {
		// TODO
	}
}
