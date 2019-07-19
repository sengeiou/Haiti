package com.aimir.bo.system.vendormodel;

import java.util.List;

import net.sf.json.JSONString;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

public class DeviceFormResult extends BeanPropertyBindingResult implements JSONString{

	public DeviceFormResult(Object target, String objectName) {
		super(target, objectName);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	public String toJSONString() {
	    String retValue = "";
	    String filedErrorString="{\"fieldErrors\":[";
	    List<FieldError> fieldErrorList =this.getFieldErrors();
	    int i=0;
	    for (FieldError fieldError :  fieldErrorList) {
	    	
	    	String field = "{\"objectName\":\""+fieldError.getObjectName()+"\",\"field\":\""+fieldError.getField()+"\",\"defaultMessage\":\""+fieldError.getDefaultMessage()+"\"}";
	    	
	    		filedErrorString=filedErrorString+field;
	    		if(i !=fieldErrorList.size()-1)
	    			filedErrorString= filedErrorString+",";
	    	i++;
		}
	    filedErrorString=filedErrorString+"]";
	    
	    retValue =  filedErrorString
	        + ",\"errorCount\":\"" + this.getErrorCount()
	        + "\"}";
	    
	    
	    
	   
	    return retValue;
	}
	
}
