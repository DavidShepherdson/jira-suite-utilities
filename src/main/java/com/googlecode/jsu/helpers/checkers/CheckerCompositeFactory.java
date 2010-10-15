package com.googlecode.jsu.helpers.checkers;

import org.apache.log4j.Logger;

/**
 * @author <A href="mailto:abashev at gmail dot com">Alexey Abashev</A>
 * @version $Id$
 */
public class CheckerCompositeFactory {
    private static final Logger log = Logger.getLogger(CheckerCompositeFactory.class);

    /**
     * Create composite for checking values.
     *
     * @param converterClass
     * @param snipetClass
     * @return
     */
    public CheckerComposite getComposite(String converterClass, String snipetClass) {
        ComparingSnipet snipet = getInstance(snipetClass);

        if (snipet == null) {
            return null;
        }

        ValueConverter converter = getInstance(converterClass);

        if (converter == null) {
            return null;
        }

        return (new CheckerComposite(converter, snipet));
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstance(String className) {
        T instance = null;

        try {
            instance = (T) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            log.error("Unable to initialize class [" + className + "]", e);
        } catch (IllegalAccessException e) {
            log.error("Unable to initialize class [" + className + "]", e);
        } catch (ClassNotFoundException e) {
            log.error("Unable to initialize class [" + className + "]", e);
        }

        return instance;
    }
}
