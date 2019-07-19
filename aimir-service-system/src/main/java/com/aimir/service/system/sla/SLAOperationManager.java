package com.aimir.service.system.sla;

import org.apache.cxf.annotations.WSDLDocumentation;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.util.Map;

/**
 * Created on 16-08-17.
 */

@WSDLDocumentation("Service Level Agreement Management( SLA Report, Metering Schedule or Strategy )")
@WebService(name="SLAOperationService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style= SOAPBinding.Style.DOCUMENT, use= SOAPBinding.Use.LITERAL, parameterStyle= SOAPBinding.ParameterStyle.WRAPPED)
public interface SLAOperationManager {

    /**
     * Get the list of table
     */
    @WebMethod
    @WebResult(name="getGroupStragetyList")
    public Map<String, Object> getGroupStragetyList(
            @WebParam(name ="supplierId")Integer supplierId
    );

    /**
     * Add new record
     */
    @WebMethod
    @WebResult(name="addNewGroupStragety")
    public Map<String, Object> addNewGroupStragety();

    /**
     * Update the record
     */
    @WebMethod
    @WebResult(name="updateGroupStragety")
    public Map<String, Object> updateGroupStragety();



}
