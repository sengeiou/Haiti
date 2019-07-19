package com.aimir.support;

import java.beans.PropertyEditorSupport;

public class IntegerTypeEditor extends PropertyEditorSupport {

	public String getAsText() {
		Integer i = (Integer)getValue();
		if (i == null)
			return "";
		else
			return String.valueOf(i);
	}


	public void setAsText(String text) throws NumberFormatException {
		if (text == null || "".equals(text)) {
			setValue(null);
		}
		else {
			setValue(Integer.valueOf(text));
		}
	}

}
