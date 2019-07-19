package com.aimir.util;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.Location;;

public class LocationTypeEditor extends PropertyEditorSupport{

	@Override
	public String getAsText() {		
		Location location = (Location)getValue();
		if (location == null || location.getId() == null || location.getId() == 0)
			return "";
		else
			return String.valueOf(location.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new Location(Integer.parseInt(text)));
	}
}
