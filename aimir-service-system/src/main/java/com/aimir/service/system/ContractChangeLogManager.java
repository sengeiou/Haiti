package com.aimir.service.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.apache.cxf.annotations.WSDLDocumentationCollection;

import com.aimir.model.system.ContractChangeLog;
import com.aimir.util.Condition;

@WSDLDocumentation("Contract Change Information Management Service")
@WebService(name="ContractChangeLogService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ContractChangeLogManager {

    /**
     * method name : getContractChangeLog
     * method Desc : ContractChangeLog의 id 로 ContractChangeLog 정보 리턴
     *
     * @param id ContractChangeLog.id
     * @return @see com.aimir.model.system.ContractChangeLog
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get the contract change log information by log ID",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
             @WSDLDocumentation(value="ContractChangeLog Instance",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
    @WebResult(name="ContractChangeLogInstance")
    public ContractChangeLog getContractChangeLog(Long id);

    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog entity  추가, Database insert 수행
     *
     * @param contractChangeLog
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="The changed information is added to the log.",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void addContractChangeLog(
            @WebParam(name="contractChangeLog") ContractChangeLog contractChangeLog);

    /**
      * method name : updateContractChangeLog
     * method Desc :  ContractChangeLog entity  갱신, Database update 수행
     *
     * @param contractChangeLog
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="The changed information is updated to the log.",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION)
            })
    @WebMethod
    public void updateContractChangeLog(
            @WebParam(name="contractChangeLog") ContractChangeLog contractChangeLog);

    /**
     * method name : getContractChangeLogCountByListCondition
     * method Desc : 조회조건에 해당하는 계약변경로그 리스트의 카운트를 리턴한다.
     *
     * @param set
     * @return List of Object (total Count)
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="The changed information is updated to the log.",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="ContractChangeLogCount List",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            })
    @WebMethod
    @WebResult(name="ContractChangeLogCountList")
    public List<Object> getContractChangeLogCountByListCondition(Set<Condition> set);

    /**
     * method name : getContractChangeLogByListCondition
     * method Desc : 조회조건에 해당하는 계약변경로그 리스트를 리턴한다.
     *
     * @param set
     * @return List of ContractChangeLog @see com.aimir.model.system.ContractChangeLog
     */
    @WebMethod(operationName ="ContractChangeLogList")
    @WebResult(name="ContractChangeLogList")
    public List<ContractChangeLog> getContractChangeLogByListCondition(
            @WebParam(name="set") Set<Condition> set);


    /**
     * method name : getContractChangeLogByListCondition
     * method Desc : 조회조건에 해당하는 계약변경로그 리스트를 리턴한다.
     *
     * @param set
     * @param supplierId Supplier.id
     * @return List of Map {startDatetime, locale date}
     */
    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a list of contact information by parameters",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameter Map \r\n"
                            + " set : Set<Condition>  \r\n"
                            + " supplierId : Supplier.id",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Returns List of Map \r\n"
                            + " ($key=id, $valueType=Integer, @description=ContractChangeLog.id) \r\n"
                            + " ($key=customer, $valueType=Customer, @description=Customer Instance) \r\n"
                            + " ($key=customerId, $valueType=Integer, @description=Customer.id) \r\n"
                            + " ($key=contract, $valueType=Contract, @description=Contact instance) \r\n"
                            + " ($key=contractId, $valueType=Integer, @description=Contract.id) \r\n"
                            + " ($key=startDatetime, $valueType=String, @description=applied date yyyyMMddHHmmss) \r\n"
                            + " ($key=changeField, $valueType=String, @description=Change Attribute) \r\n"
                            + " ($key=beforeValue, $valueType=String, @description=Before Value) \r\n"
                            + " ($key=afterValue, $valueType=String, @description=After Value) \r\n"
                            + " ($key=operator, $valueType=Operator, @description=Operator instance) \r\n"
                            + " ($key=operatorId, $valueType=Integer, @description=Operator.id) \r\n"
                            + " ($key=writeDatetime, $valueType=String, @description=DB insert time yyyyMMddHHmmss) \r\n"
                            + " ($key=descr, $valueType=String, @description=Description)",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    @WebMethod(operationName ="ContractChangeLogListById")
    @WebResult(name="ContractChangeLogList")
    public List<Map<String,Object>> getContractChangeLogByListCondition(
            @WebParam(name="set") Set<Condition> set,
            @WebParam(name="supplierId") String supplierId);
    /**
     * method name : contractLogDelete
     * method Desc : 계약정보 아이디에 해당하는 계약정보 변경 로그 를 삭제한다.
     *
     * @param contractId Contract.id
     */
    @WSDLDocumentationCollection(
                {@WSDLDocumentation(value="Deleting a contract change log by ID.",
                        placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
                )
    @WebMethod
    public void contractLogDelete(
            @WebParam(name="contractId") int contractId);
    /**
     * method name : contractLogAllDelete
     * method Desc : 고객 아이디에 해당하는 계약정보 변경 로그 전체를 삭제한다.
     *
     * @param customerId Customer.id
     */
    @WSDLDocumentationCollection(
                {@WSDLDocumentation(value="The entire log data is deleted by customer ID (Customer.id)",
                        placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
                )
    @WebMethod
    public void contractLogAllDelete(
            @WebParam(name="customerId") int customerId);


    /**
     * method name : getContractChangeLogList<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="contractChangeLogList")
    public List<Map<String, Object>> getContractChangeLogList(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getContractChangeLogListTotalCount<b/>
     * method Desc : Customer Contract Management 맥스 가젯에서 Contract ChangeLog Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="contractChangeLogListTotalCount")
    public Integer getContractChangeLogListTotalCount(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
}