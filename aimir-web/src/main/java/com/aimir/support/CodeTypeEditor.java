package com.aimir.support;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.Code;

public class CodeTypeEditor extends PropertyEditorSupport{

	@Override
	public String getAsText() {
		Code code = (Code)getValue();
		if (code == null || code.getId() == null || code.getId() == 0)
			return "";
		else
			return String.valueOf(code.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new Code(Integer.parseInt(text)));
	}

}
