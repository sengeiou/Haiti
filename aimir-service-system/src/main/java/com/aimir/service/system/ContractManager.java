/**
 * ContractManager.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system;

import java.util.List;
import java.util.Map;

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

import com.aimir.model.system.Contract;

/**
 * ContractManager.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0       김상연         Contract 유무 체크
 * 2011. 4. 12.   v1.1        김상연         Contract 검색 (Contract)
 *
 */
@WSDLDocumentation("Contract Information Service")
@WebService(name="ContractService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface ContractManager {

    /**
     *
     * @param contract
     */
    @WSDLDocumentationCollection(
                {@WSDLDocumentation(value="Adding contract information",
                        placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
                )
    @WebMethod
    @Deprecated
    public void addContract(@WebParam(name="contract") Contract contract);

    /**
     * method name : createContract<b/>
     * method Desc : Contract 를 생성한다.
     *
     * @param contract
     */
    @WebMethod
    public void createContract(@WebParam(name = "contract") Contract contract);

    /**
     *
     * @param customerId
     * @return
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get the contract information by contract ID",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
             @WSDLDocumentation(value="Contract object",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
    @WebResult(name="ContractInstance")
    public Contract getContract(
            @WebParam(name="contractId") int contractId);

    /**
     *
     * @param meterId
     * @return
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get contact information associated with meter(meter Id)",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
             @WSDLDocumentation(value="Contract object",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
    @WebResult(name="ContractByMeterId")
    public Contract getContractByMeterId(@WebParam(name="meterId") int meterId);

    /**
     * method name : getContractByMeterNo<b/>
     * method Desc :
     *
     * @param meterId
     * @return
     */
    @WebMethod
    @WebResult(name="ContractByMeterNo")
    public Contract getContractByMeterNo(@WebParam(name="meterId") String meterId);

    // all meter list fetch
    @SuppressWarnings("rawtypes")
    @Deprecated
    public List getMeterList(Map<String, String> conditionMap);

    // all meter list tot count fetch
    @Deprecated
    public String getMeterListDataCount(Map<String, String> conditionMap);

    /**
     * method name : getMeterGridList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getMeterGridList(Map<String, Object> conditionMap);

    /**
     * method name : getMeterGridListDataCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getMeterGridListDataCount(Map<String, Object> conditionMap);

    /**
     * method name : getMyEnergy
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @param customerId Contract.customer.id
     * @param serviceTypeId energy service type code id
     * @return Array of {Contract.id, Contract.contractNumber}
     */
    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a list of contact information by service type of energy for a customer",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameters(customerId, serviceType code id)",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Returns Array of {Contract.id, Contract.contractNumber}",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    @WebMethod
    @WebResult(name="MyEnergyList")
    public List<Object> getMyEnergy(
            @WebParam(name="customerId") int customerId,
            @WebParam(name="serviceTypeId") int serviceTypeId);

    /**
     *
     * @param contract
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Updating contract information",
                placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
            )
    public void updateContract(
            @WebParam(name="contract") Contract contract);

    /**
     * method name : numberOverlapCheck
     * method Desc : 계약번호가 Contract 정보에 중북되는지 확인
     *
     * @param contractNumber Contract.contractNumber
     * @return 중복되는 값이 없으면 0이 리턴된다.
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Check duplicate contract number - Return Count, If you can not duplicate returns 0",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value="numberOverlapCheck",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
    @WebResult(name="numberOverlapCheck")
    public int numberOverlapCheck(
            @WebParam(name="contractNumber") String contractNumber);

    /**
     *
     * @param id
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Deleting contract information",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
            )
    @WebMethod
    @Deprecated
    public void contractDelete(
            @WebParam(name="id") Integer id);

    /**
     *
     * @param id
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Deleting contract information",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
            )
    @WebMethod
    public void deleteContract(@WebParam(name="id") Integer id, @WebParam(name="operatorId") Integer operatorId);

    /**
     *
     * @param id
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="All contract information associated with customer ID are deleted. Parameter( id means customer id)",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION)}
            )
    @WebMethod
    public void contractAllDelete(
            @WebParam(name="id") Integer id);

    /**
     *
     * @param conditionMap
     * @return
     */

    public List<Map<String, Object>> getContracts(Map<String, Object> conditionMap);

    /**
     * method name : getContractsTree<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a list of contact information by parameters",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameter Map \r\n"
                            + " ($key=customerNo, $valueType=String, @description=Customer.customerNo) \r\n"
                            + " ($key=customerName, $valueType=String, @description=Customer.customerNo) \r\n"
                            + " ($key=location, $valueType=String, @description=Location.id) \r\n"
                            + " ($key=tariffIndex, $valueType=String, @description=TariffType.id) \r\n"
                            + " ($key=contractDemand, $valueType=String, @description=Query condition not mandatory, Contract.contractDemand(double string)) \r\n"
                            + " ($key=creditType, $valueType=String, @description=Code.id(credit type code id)) \r\n"
                            + " ($key=mdsId, $valueType=String, @description=Meter.id) \r\n"
                            + " ($key=status, $valueType=String, @description=Code.id (Supply Status code 2.1.X)) \r\n"
                            + " ($key=dr, $valueType=String, @description=Customer.demandResponse('true','false')) \r\n"
                            + " ($key=startDate, $valueType=String, @description=yyyyMMddHHmmss) \r\n"
                            + " ($key=endDate, $valueType=String, @description=yyyyMMddHHmmss) \r\n"
                            + " ($key=serviceTypeTab, $valueType=String, @description=type (''-All,'EM'-Electric,'GM'-Gas,'WM'-Water) \r\n"
                            + " ($key=supplierId, $valueType=String, @description=Supplier.id)",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Returns List of Map \r\n"
                            + " ($key=customerName, $valueType=String, @description=Customer.name) \r\n"
                            + " ($key=customerId, $valueType=String, @description=Customer.id) \r\n"
                            + " ($key=customerNo, $valueType=String, @description=Customer.customerNo) \r\n"
                            + " ($key=customerSize, $valueType=String, @description=Contracts with customers) \r\n"
                            + " ($key=children, $valueType=Map<String,Object>, @description=List of contract information) \r\n"
                            + " 'children' data description  \r\n"
                            + " ($key=customerId, $valueType=String, @description=Customer.id) \r\n"
                            + " ($key=customerName, $valueType=String, @description=Customer.name) \r\n"
                            + " ($key=address, $valueType=String, @description=Customer.address) \r\n"
                            + " ($key=contractId, $valueType=String, @description=Contract.id) \r\n"
                            + " ($key=contractNumber, $valueType=String, @description=Contract.contractNumber) \r\n"
                            + " ($key=location, $valueType=String, @description=Contract.location.id) \r\n"
                            + " ($key=serviceTypeName, $valueType=String, @description=service type name(Electricity, Gas,Water)) \r\n"
                            + " ($key=meterId, $valueType=String, @description=Meter.mdsId) \r\n"
                            + " ($key=customerNo, $valueType=String, @description=Customer.customerNo)",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    public List<Map<String, Object>> getContractsTree(Map<String, Object> conditionMap);

    /**
     * method name : getContractsChildTree<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractsChildTree(Map<String, Object> conditionMap);

    ///////////////////////////// KYH Tree Add End ////////////////////////////////////////////////////////////////////
    @WebMethod
    @WebResult(name="ContractsList")
    @Deprecated
    public List<Map<String, Object>> getContractsByParam(
            @WebParam(name="Customer.customerNo") String customerNo,
            @WebParam(name="Customer.customerNo") String customerName,
            @WebParam(name="Location.id") String location,
            @WebParam(name="TariffType.id") String tariffIndex,
            @WebParam(name="Contract.contractDemand") String contractDemand,
            @WebParam(name="Code.id") String creditType,
            @WebParam(name="Meter.id") String mdsId,
            @WebParam(name="Code.id") String status,
            @WebParam(name="Customer.demandResponse") String dr,
            @WebParam(name="yyyyMMddHHmmss") String startDate,
            @WebParam(name="yyyyMMddHHmmss") String endDate,
            @WebParam(name = "serviceTypeTab") String serviceTypeTab,
            @WebParam(name="Supplier.id") String supplierId
            );

    /**
     * method name : getContractCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public String getContractCount( Map<String, Object> conditionMap);

    /**
     * method name : getTotalContractCount<b/>
     * method Desc : Customer Contract 맥스가젯에서 전체 계약 개수를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getTotalContractCount(Map<String, Object> conditionMap);

    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a number of contract list by parameters",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameter Map \r\n"
                            + " ($key=customerNo, $valueType=String, @description=Customer.customerNo) \r\n"
                            + " ($key=customerName, $valueType=String, @description=Customer.name) \r\n"
                            + " ($key=location, $valueType=String, @description=Location.id) \r\n"
                            + " ($key=tariffIndex, $valueType=String, @description=TariffType.id) \r\n"
                            + " ($key=contractDemand, $valueType=String, @description=Query condition not mandatory, Contract.contractDemand(double string)) \r\n"
                            + " ($key=creditType, $valueType=String, @description=Code.id(credit type code id)) \r\n"
                            + " ($key=mdsId, $valueType=String, @description=Meter.id) \r\n"
                            + " ($key=status, $valueType=String, @description=Code.id (Supply Status code 2.1.X)) \r\n"
                            + " ($key=dr, $valueType=String, @description=Customer.demandResponse('true','false')) \r\n"
                            + " ($key=startDate, $valueType=String, @description=yyyyMMddHHmmss) \r\n"
                            + " ($key=endDate, $valueType=String, @description=yyyyMMddHHmmss) \r\n"
                            + " ($key=serviceTypeTab, $valueType=String, @description=type (''-All,'EM'-Electric,'GM'-Gas,'WM'-Water) \r\n"
                            + " ($key=supplierId, $valueType=String, @description=Supplier.id)",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Return count",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    @WebMethod
    @WebResult(name="ContractCount")
    public String getContractCountByParam(
            @WebParam(name="Customer.customerNo") String customerNo,
            @WebParam(name="Customer.customerNo") String customerName,
            @WebParam(name="Location.id") String location,
            @WebParam(name="TariffType.id") String tariffIndex,
            @WebParam(name="Contract.contractDemand") String contractDemand,
            @WebParam(name="Code.id") String creditType,
            @WebParam(name="Meter.id") String mdsId,
            @WebParam(name="Code.id") String status,
            @WebParam(name="Customer.demandResponse") String dr,
            @WebParam(name="yyyyMMddHHmmss") String startDate,
            @WebParam(name="yyyyMMddHHmmss") String endDate,
            @WebParam(name = "serviceTypeTab") String serviceTypeTab,
            @WebParam(name="Supplier.id") String supplierId
            );

    /**
     *
     * @param contractId
     * @return
     */
    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a contact information by contract id",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameters(contract id (Contract.id))",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Return Map  \r\n"
                            + " ($key=tariffIndexName, $valueType=String, @description=TariffType.name) \r\n"
                            + " ($key=locationName, $valueType=String, @description=Location.name) \r\n"
                            + " ($key=contractDemand, $valueType=String, @description=Double String) \r\n"
                            + " ($key=statusName, $valueType=String, @description=Supply Status code name 2.1.x) \r\n"
                            + " ($key=creditTypeName, $valueType=String, @description=credit type name) \r\n"
                            + " ($key=tariffIndexId, $valueType=String, @description=TariffType.id) \r\n"
                            + " ($key=locationId, $valueType=String, @description=Location.id) \r\n"
                            + " ($key=statusId, $valueType=String, @description=Supply Status code id 2.1.x) \r\n"
                            + " ($key=creditTypeId, $valueType=String, @description=Contract.creditType) \r\n"
                            + " ($key=serviceTypeName, $valueType=String, @description= service type code name) \r\n"
                            + " ($key=mdsId, $valueType=String, @description=Meter.mdsId)",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    @WebMethod(operationName ="ContractInfoMap")
    @WebResult(name="ContractInfoMap")
    public Map<String, Object> getContractInfo(
            @WebParam(name = "contractId") int contractId);

    /**
     *
     * @param contractId
     * @param supplierId
     * @return
     */

    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a contact information by contract id",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameters( Contract.id, Supplier.id)",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Return Map  \r\n"
                            + " ($key=tariffIndexName, $valueType=String, @description=TariffType.name) \r\n"
                            + " ($key=locationName, $valueType=String, @description=Location.name) \r\n"
                            + " ($key=contractDemand, $valueType=String, @description=Double String) \r\n"
                            + " ($key=statusName, $valueType=String, @description=Supply Status code name 2.1.x) \r\n"
                            + " ($key=creditTypeName, $valueType=String, @description=credit type name) \r\n"
                            + " ($key=tariffIndexId, $valueType=String, @description=TariffType.id) \r\n"
                            + " ($key=locationId, $valueType=String, @description=Location.id) \r\n"
                            + " ($key=statusId, $valueType=String, @description=Supply Status code id 2.1.x) \r\n"
                            + " ($key=creditTypeId, $valueType=String, @description=Contract.creditType) \r\n"
                            + " ($key=serviceTypeName, $valueType=String, @description= service type code name) \r\n"
                            + " ($key=mdsId, $valueType=String, @description=Meter.mdsId)",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    @WebMethod(operationName ="ContractInfoMapBySupplierId")
    @WebResult(name="ContractInfoMap")
    public Map<String, Object> getContractInfo(
            @WebParam(name="contractId") int contractId,
            @WebParam(name="supplierId") int supplierId);

    /**
     *
     * @param conditionMap
     * @return
     */
    @WSDLDocumentationCollection(
            {
                @WSDLDocumentation(value = "Get a contact information by contract id",
                                    placement = WSDLDocumentation.Placement.BINDING_OPERATION),
                @WSDLDocumentation(value = "Parameters(customerNo, customerName, location, mdsId, customerType,address, serviceType,supplierId  )",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_INPUT),
                @WSDLDocumentation(value = "Return Map of {ID, NAME, ADDRESS, CONTID, CONTRACT_NUMBER, LOCNAME, SERVICETYPENAME,MDS_ID, CUSTOMTYPENAME,SERVICETYPE_ID,CUSTOMERNO,CHECKED}",
                                   placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)
            }
    )
    @WebMethod
    @WebResult(name="ContractListList")
    public List<Object[]> getContractListByMeter(
            @WebParam(name = "conditionMap") Map<String, String> conditionMap);

    /**
     * method name : checkContract
     * method Desc : Contract 유무 체크
     *
     * @param contract
     * @return
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Check duplicate contract  - If you can not duplicate returns false",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value="customer object",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
    @WebResult(name="checkContract")
    public boolean checkContract(
            @WebParam(name="contract") Contract contract);

    /**
     * method name : getContractByContract
     * method Desc : Contract List 조회 (조건별)
     *
     * @param contract
     * @return List of Contract @see com.aimir.model.system.Contract
     */
    @WebMethod
    @WebResult(name="getContractList")
    public List<Contract> getContractByContract(
            @WebParam(name="contract") Contract contract);


    /**
     * method name : getContractByContractNumber
     * method Desc : contractNo에 속한 Contract의 목록을 리턴한다.
     *
     * @param contractNumber Contract.contractNo
     * @return @see com.aimir.model.system.Contract
     */
    @WSDLDocumentationCollection(
            {@WSDLDocumentation(value="Get the customer information by customer ID",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION),
             @WSDLDocumentation(value="customer object",
                    placement=WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT)}
            )
    @WebMethod
    @WebResult(name="ContractByContractNumber")
    public Contract getContractByContractNumber(
            @WebParam(name="contractNumber") String contractNumber);
    
    @WebMethod
    @WebResult(name="ContractByContractNumber2")
    public List<Contract> getContractByContractNumber2(
            @WebParam(name="contractNumber") String contractNumber,
            @WebParam(name="supplierId") Integer supplierId);

    @WebMethod
    @WebResult(name="PartpayInfoByContractNumber")
    public Map<String,Object> getPartpayInfoByContractNumber(String contractNumber, Integer supplierId);

    /**
     *
     * @param excel
     * @return
     */
    @WebMethod
    @WebResult(name="readExcelXLS")
    public Map<String,Object> readExcelXLS(
            @WebParam(name="excel") String excel,
            @WebParam(name="supplierId") int supplierId);

    /**
     *
     * @param excel
     * @param supplier
     * @return
     */
    @WebMethod
    @WebResult(name="saveExcelXLS")
    public Map<String,Object> saveExcelXLS(
            @WebParam(name="excel") String excel,
            @WebParam(name="supplierId") int supplierId);
    /**
     *
     * @param excel
     * @return
     */
    @WebMethod
    @WebResult(name="readExcelXLSX")
    public Map<String,Object> readExcelXLSX(
            @WebParam(name="excel") String excel,
            @WebParam(name="supplierId") int supplierId);

    /**
     *
     * @param excel
     * @param supplier
     * @return
     */
    @WebMethod
    @WebResult(name="saveExcelXLSX")
    public Map<String,Object> saveExcelXLSX(
            @WebParam(name="excel") String excel,
            @WebParam(name="supplierId") int supplierId);

    /**
     * @desc    미터id의 계약 여부를 Check
     */
    @Deprecated
    public int checkContractedMeterYn(String meterId);

    /**
     * method name : checkContractMeterYn<b/>
     * method Desc :
     *
     * @param meterId
     * @return
     */
    @WebMethod
    @WebResult(name="checkContractMeterYn")
    public int checkContractMeterYn(@WebParam(name="meterId") String meterId);

    /**
     * method name : getCheckContractByMeterId<b/>
     * method Desc : Contract 등록/수정 시 선택한 Meter 가 현재 다른 Contract 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @WebMethod
    @WebResult(name="getCheckContractByMeterId")
    @Deprecated
    public Map<String, Object> getCheckContractByMeterId(@WebParam(name="meterId") Integer meterId,
            @WebParam(name="contractId") Integer contractId);

    /**
     * method name : getCheckContractByMeterNo<b/>
     * method Desc : Contract 등록/수정 시 선택한 Meter 가 현재 다른 Contract 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @WebMethod
    @WebResult(name="getCheckContractByMeterNo")
    public Map<String, Object> getCheckContractByMeterNo(@WebParam(name="meterNo") String meterNo,
            @WebParam(name="contractId") Integer contractId);

    /**
     * method name : getCheckContractNumber<b/>
     * method Desc : Contract 등록/수정 시 입력한 Contract Number 가 현재 다른 Customer 에 연결되어 있는지 여부를 체크한다.
     *
     * @param contractNumber
     * @return
     */
    @WebMethod
    @WebResult(name="getCheckContractNumber")
    public Map<String, Object> getCheckContractNumber(@WebParam(name="contractNumber") String contractNumber);

    /**
     * method name : insertContract<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return 
     */
    @WebMethod
    @WebResult(name="insertContract")
    public Contract insertContract(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : modifyContract<b/>
     * method Desc :
     *
     * @param conditionMap
     */
    @WebMethod
    @WebResult(name="modifyContract")
    public void modifyContract(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getCustomerListByType<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="customerListByType")
    public List<Map<String, Object>> getCustomerListByType(@WebParam(name="conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getCustomerListByTypeTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name="customerListByTypeTotalCount")
    public Integer getCustomerListByTypeTotalCount(@WebParam(name="conditionMap") Map<String, Object> conditionMap);
    
    /**
     * ServiceType2  조회
     */
    public List<Object> getServiceType2();
    
    @WebMethod
    @WebResult(name="getContractByloginId")
    public List<Map<String, Object>> getContractByloginId(
            @WebParam(name="loginId") String loginId,
            @WebParam(name="METER_TYPE") String meterType);
}