package com.googlecode.jsu.helpers;

/**
 * @author Gustavo Martin
 *
 * This class represents a Comparison Type. This will be used in Workflow Condition, Validator, or Function.
 *
 */
public class ComparisonType {
    private final int id;
    private final String value;
    private final String mnemonic;

    /**
     * @param id
     * @param value
     */
    public ComparisonType(int id, String value, String mnemonic) {
        this.id = id;
        this.value = value;
        this.mnemonic = mnemonic;
    }

    /**
     * Get comparision id.
     * @return
     */
    public Integer getId() {
        return id;
    }

    /**
     * Get name of comparision.
     *
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the mnemonic
     */
    public String getMnemonic() {
        return mnemonic;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ComparisonType))
            return false;
        ComparisonType other = (ComparisonType) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
