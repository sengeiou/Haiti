package com.aimir.util;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.TariffType;

public class TariffTypeEditor extends PropertyEditorSupport{

	@Override
	public String getAsText() {		
		TariffType tariff = (TariffType)getValue();
		if (tariff == null || tariff.getId() == null || tariff.getId() == 0)
			return "";
		else
			return String.valueOf(tariff.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new TariffType(Integer.parseInt(text)));
	}
}
