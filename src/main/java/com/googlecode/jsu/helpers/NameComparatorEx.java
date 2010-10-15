package com.googlecode.jsu.helpers;

import java.util.Comparator;

import com.atlassian.jira.issue.fields.Field;
import com.atlassian.jira.web.bean.I18nBean;

/**
 * @author Gustavo Martin
 *
 * This Comparator is used to compare two fields by its internationalized name.
 *
 */
public class NameComparatorEx implements Comparator<Field> {
    private final I18nBean i18nBean;

    public NameComparatorEx(I18nBean i18nBean) {
        this.i18nBean = i18nBean;
    }

    public int compare(Field o1, Field o2) {
        if (o1 == null)
            throw new IllegalArgumentException("The first parameter is null");
        if (o2 == null)
            throw new IllegalArgumentException("The second parameter is null");

        String name1 = i18nBean.getText(o1.getName());
        String name2 = i18nBean.getText(o2.getName());

        return name1.compareTo(name2);
    }
}
