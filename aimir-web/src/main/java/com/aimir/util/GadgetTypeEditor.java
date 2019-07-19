package com.aimir.util;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.Gadget;

public class GadgetTypeEditor extends PropertyEditorSupport{

	@Override
	public String getAsText() {		
		Gadget gadget = (Gadget)getValue();
		if (gadget == null || gadget.getId() == null || gadget.getId() == 0)
			return "";
		else
			return String.valueOf(gadget.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new Gadget(Integer.parseInt(text)));
	}
}
