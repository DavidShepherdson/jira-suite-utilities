package com.googlecode.jsu.helpers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Return object for checking conditions.
 * 
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id: GenericValidator.java 173 2008-10-14 13:04:43Z abashev $
 */
public class ConditionCheckerFactory {
	public static final ConditionType GREATER = new ConditionType(1, ">", "greater than", "G");
	public static final ConditionType GREATER_EQUAL = new ConditionType(2, ">=", "greater than or equal to", "GE");
	public static final ConditionType EQUAL = new ConditionType(3, "=", "equal to", "E");
	public static final ConditionType LESS_EQUAL = new ConditionType(4, "<=", "less than or equal to", "LE");
	public static final ConditionType LESS = new ConditionType(5, "<", "less than", "L");
	public static final ConditionType NOT_EQUAL = new ConditionType(6, "!=", "not equal to", "NE");
	
	public static final ComparisonType STRING = new ComparisonType(1, "String");
	public static final ComparisonType NUMBER = new ComparisonType(2, "Number");
	public static final ComparisonType DATE = new ComparisonType(3, "Date with time");
	public static final ComparisonType DATE_WITHOUT_TIME = new ComparisonType(4, "Date without time");
	
	/** Template for checker class. */
	private static final String CHECKER_CLASS_TEMPLATE = 
			ConditionCheckerFactory.class.getPackage().getName() + ".checkers.Checker%s%s";
	
	/** Cache for searching through conditions */
	private static final Map<Integer, ConditionType> CONDITIONS_CACHE = 
		new LinkedHashMap<Integer, ConditionType>() {{
			put(GREATER.getId(), GREATER); 
			put(GREATER_EQUAL.getId(), GREATER_EQUAL); 
			put(EQUAL.getId(), EQUAL);
			put(LESS_EQUAL.getId(), LESS_EQUAL); 
			put(LESS.getId(), LESS);
			put(NOT_EQUAL.getId(), NOT_EQUAL); 
	}};

	/** Cache for searching through types */
	private static final Map<Integer, ComparisonType> COMPARISONS_CACHE = 
		new LinkedHashMap<Integer, ComparisonType>() {{
			put(STRING.getId(), STRING);
			put(NUMBER.getId(), NUMBER);
			put(DATE.getId(), DATE);
			put(DATE_WITHOUT_TIME.getId(), DATE_WITHOUT_TIME);
	}};
	
	private final Logger log = Logger.getLogger(ConditionCheckerFactory.class);
	
	public ConditionChecker getChecker(ComparisonType type, ConditionType condition) {
		String clazz = getCheckerClassName(type, condition);

		if (log.isDebugEnabled()) {
			log.debug(
					"Using class [" + clazz + 
					"] for type [" + type.getValue() + 
					"] and condition [" + condition.getValue() + 
					"]"
			);
		}

		return initChecker(clazz);
	}
	
	/**
	 * Get all possible condition types.
	 * 
	 * @return
	 */
	public List<ConditionType> getConditionTypes() {
		return new ArrayList<ConditionType>(CONDITIONS_CACHE.values());
	}
	
	/**
	 * Get all possible comparison types.
	 * @return
	 */
	public List<ComparisonType> getComparisonTypes() {
		return new ArrayList<ComparisonType>(COMPARISONS_CACHE.values());
	}
	
	private String getCheckerClassName(ComparisonType type, ConditionType condition) {
		return String.format(CHECKER_CLASS_TEMPLATE, type.getValue(), condition.getMnemonic());
	}
	
	private ConditionChecker initChecker(String clazz) {
		ConditionChecker checker = null;
		
		try {
			checker = (ConditionChecker) Class.forName(clazz).newInstance();
		} catch (InstantiationException e) {
			log.error("Unable to initialize class [" + clazz + "]", e);
		} catch (IllegalAccessException e) {
			log.error("Unable to initialize class [" + clazz + "]", e);
		} catch (ClassNotFoundException e) {
			log.error("Unable to initialize class [" + clazz + "]", e);
		}
		
		return checker;
	}
}
