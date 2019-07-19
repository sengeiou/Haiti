package com.aimir.support;

import java.beans.PropertyEditorSupport;

import com.aimir.model.system.DeviceVendor;

public class DeviceVendorTypeEditor extends PropertyEditorSupport {

	@Override
	public String getAsText() {
		DeviceVendor deviceVendor = (DeviceVendor)getValue();
		if (deviceVendor == null || deviceVendor.getId() == null || deviceVendor.getId() == 0)
			return "";
		else
			return String.valueOf(deviceVendor.getId());
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text == "")
			setValue(null);
		else
			setValue(new DeviceVendor(Integer.parseInt(text)));
	}

}
