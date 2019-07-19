package com.aimir.model;


import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Base PK for Model objects.  This is basically for the toString method.
 */
public class BasePk implements Serializable {

	private static final long serialVersionUID = 2054611155895070835L;

	public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

	@Override
 	public boolean equals(Object o) {
 		return EqualsBuilder.reflectionEquals(this, o);
    }

	@Override
    public int hashCode() {
    	return HashCodeBuilder.reflectionHashCode(this);
    }
}