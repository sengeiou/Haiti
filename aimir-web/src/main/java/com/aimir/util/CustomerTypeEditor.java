package com.aimir.util;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.Customer;

public class CustomerTypeEditor extends PropertyEditorSupport{

	@Override
	public String getAsText() {		
		Customer customer = (Customer)getValue();
		if (customer == null || customer.getId() == null || customer.getId() == 0)
			return "";
		else
			return String.valueOf(customer.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new Customer(Integer.parseInt(text)));
	}
}
