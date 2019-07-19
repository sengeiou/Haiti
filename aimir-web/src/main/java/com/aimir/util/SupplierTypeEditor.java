package com.aimir.util;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.Supplier;

public class SupplierTypeEditor extends PropertyEditorSupport{

	@Override
	public String getAsText() {		
		Supplier supplier = (Supplier)getValue();
		if (supplier == null || supplier.getId() == null || supplier.getId() == 0)
			return "";
		else
			return String.valueOf(supplier.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new Supplier(Integer.parseInt(text)));
	}
}
