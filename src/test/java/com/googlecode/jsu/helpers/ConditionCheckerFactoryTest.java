package com.googlecode.jsu.helpers;

import java.util.Calendar;

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
        assertTrue(checker.checkValues(val4, val2));

        checker = factory.getChecker(NUMBER, LESS);

        assertFalse(checker.checkValues(val1, val1));
        assertTrue(checker.checkValues(val1, val2));
        assertFalse(checker.checkValues(val1, val3));
        assertFalse(checker.checkValues(val4, val2));

        checker = factory.getChecker(NUMBER, LESS_EQUAL);

        assertTrue(checker.checkValues(val1, val1));
        assertTrue(checker.checkValues(val1, val2));
        assertFalse(checker.checkValues(val1, val3));
        assertFalse(checker.checkValues(val4, val2));

        checker = factory.getChecker(NUMBER, GREATER);

        assertFalse(checker.checkValues(val1, val1));
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
        final String val4 = null;

        ConditionChecker checker = factory.getChecker(STRING, EQUAL);

        assertTrue(checker.checkValues(val1, val1));
        assertFalse(checker.checkValues(val1, val2));
        assertFalse(checker.checkValues(val1, val3));
        assertFalse(checker.checkValues(val1, val4));
        assertFalse(checker.checkValues(val4, val1));

        checker = factory.getChecker(STRING, NOT_EQUAL);

        assertFalse(checker.checkValues(val1, val1));
        assertTrue(checker.checkValues(val1, val2));
        assertTrue(checker.checkValues(val1, val3));
        assertTrue(checker.checkValues(val1, val4));
        assertTrue(checker.checkValues(val4, val1));
    }

    @Test
    public void dateComparisons() {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        final Calendar cal3 = Calendar.getInstance();
        final Calendar cal4 = null;

        cal2.add(Calendar.MINUTE, 5);
        cal3.add(Calendar.DAY_OF_MONTH, 1);

        ConditionChecker checker = factory.getChecker(DATE, EQUAL);

        assertTrue(checker.checkValues(cal1, cal1));
        assertFalse(checker.checkValues(cal1, cal2));
        assertFalse(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE, NOT_EQUAL);

        assertFalse(checker.checkValues(cal1, cal1));
        assertTrue(checker.checkValues(cal1, cal2));
        assertTrue(checker.checkValues(cal1, cal3));
        assertTrue(checker.checkValues(cal4, cal2));
        assertTrue(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE, LESS);

        assertFalse(checker.checkValues(cal1, cal1));
        assertTrue(checker.checkValues(cal1, cal2));
        assertTrue(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE, LESS_EQUAL);

        assertTrue(checker.checkValues(cal1, cal1));
        assertTrue(checker.checkValues(cal1, cal2));
        assertTrue(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE, GREATER);

        assertFalse(checker.checkValues(cal1, cal1));
        assertFalse(checker.checkValues(cal1, cal2));
        assertFalse(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE, GREATER_EQUAL);

        assertTrue(checker.checkValues(cal1, cal1));
        assertFalse(checker.checkValues(cal1, cal2));
        assertFalse(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));
    }

    @Test
    public void dateWithoutTimeComparisons() {
        final Calendar cal1 = Calendar.getInstance();
        final Calendar cal2 = Calendar.getInstance();
        final Calendar cal3 = Calendar.getInstance();
        final Calendar cal4 = null;

        cal1.set(2010, 3, 3, 10, 0, 45);
        cal2.set(2010, 3, 3, 10, 0, 45);
        cal3.set(2010, 3, 3, 10, 0, 45);

        cal2.add(Calendar.MINUTE, 5);
        cal3.add(Calendar.DAY_OF_MONTH, 1);

        ConditionChecker checker = factory.getChecker(DATE_WITHOUT_TIME, EQUAL);

        assertTrue(checker.checkValues(cal1, cal1));
        assertTrue(checker.checkValues(cal1, cal2));
        assertFalse(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE_WITHOUT_TIME, NOT_EQUAL);

        assertFalse(checker.checkValues(cal1, cal1));
        assertFalse(checker.checkValues(cal1, cal2));
        assertTrue(checker.checkValues(cal1, cal3));
        assertTrue(checker.checkValues(cal4, cal2));
        assertTrue(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE_WITHOUT_TIME, LESS);

        assertFalse(checker.checkValues(cal1, cal1));
        assertFalse(checker.checkValues(cal1, cal2));
        assertTrue(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE_WITHOUT_TIME, LESS_EQUAL);

        assertTrue(checker.checkValues(cal1, cal1));
        assertTrue(checker.checkValues(cal1, cal2));
        assertTrue(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE_WITHOUT_TIME, GREATER);

        assertFalse(checker.checkValues(cal1, cal1));
        assertFalse(checker.checkValues(cal1, cal2));
        assertFalse(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));

        checker = factory.getChecker(DATE_WITHOUT_TIME, GREATER_EQUAL);

        assertTrue(checker.checkValues(cal1, cal1));
        assertTrue(checker.checkValues(cal1, cal2));
        assertFalse(checker.checkValues(cal1, cal3));
        assertFalse(checker.checkValues(cal4, cal2));
        assertFalse(checker.checkValues(cal2, cal4));
    }
}
