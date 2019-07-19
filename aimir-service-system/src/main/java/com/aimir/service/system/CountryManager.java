package com.aimir.service.system;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import com.aimir.model.system.Country;

@WebService(name="CountryService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface CountryManager {
	@WebMethod
	@WebResult(name="get")
    public Country get(
    		@WebParam(name="countryId") Integer countryId);
	
	@WebMethod
	@WebResult(name="CountriesList")
    public List<Country> getCountries();
}
