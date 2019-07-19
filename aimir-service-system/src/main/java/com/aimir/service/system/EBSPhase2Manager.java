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

import com.aimir.service.system.impl.EBSDataMapper;
import com.aimir.service.system.impl.ETreeNode;

/**
 * EnergyBalanceMonitoringManager.java Description
 * <p>
 * 
 * <pre>
 * Date          Version     Author   Description
 * 2012. 3. 12.  v1.0        문동규   Energy Balance Monitoring Service
 * </pre>
 */
@WSDLDocumentation("Energy Balance Monitoring Service")
@WebService(name = "EBSPhase2Service", targetNamespace = "http://aimir.com/services")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
public interface EBSPhase2Manager {

	
	   /**
	  * method name : getEbsMonitoringList<b/>
	  * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다.
	  * 
	  * @param conditionMap
	  * @return
	  */
	 @WebMethod
	 @WebResult(name = "EbsMonitoringList")
	 public List<Map<String, EBSDataMapper>> getEbsMonitoringList(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);
	 
	/**
     * method name : getEbsDeviceList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDeviceList")
    public List<Map<String, Object>> getEbsDeviceList(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);
    
    @WebMethod
    @WebResult(name = "EbsDeviceList")
    public Integer getEbsDeviceListCount(Map<String, Object> conditionMap);
    
    @WebMethod
    @WebResult(name = "EbsDeviceList")
    public void insertEbsDevice(Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name = "EbsDeviceList")
    public void modifyEbsDevice(Map<String, Object> conditionMap);

    @WebMethod
    @WebResult(name = "EbsDeviceList")
    public boolean deleteEbsDevice(int id);
    /**
     * method name : getEbsMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsMeterList")
    public List<Map<String, Object>> getEbsMeterList(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    public Integer getEbsMeterListTotalCnt(Map<String, Object> conditionMap);
    
    public List<ETreeNode> getEbsMonitoringTree(Map<String, Object> conditionMap);
    /**
     * method name : getCurrentMonthSearchCondition<b/>
     * method Desc : Energy Balance Monitoring 미니가젯의 조회월 타이틀 및 조회일자 조건을 가져온다.
     * 
     * @param supplierId
     * @return
     */

    @WSDLDocumentationCollection({
            @WSDLDocumentation(value = "Supplier ID based on the current query results in terms", placement = WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value = "CurrentMonthSearchCondition Map", placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT) })
    @WebMethod
    @WebResult(name = "CurrentMonthSearchConditionMap")
    public Map<String, Object> getCurrentMonthSearchCondition(@WebParam(name = "supplierId") Integer supplierId);

    /**
     * method name : getEbsSuspectedDtsList<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WSDLDocumentationCollection({
            @WSDLDocumentation(value = "Suspected Substation List to look up", placement = WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value = "EbsSuspectedDts List", placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT) })
    @WebMethod
    @WebResult(name = "EbsSuspectedDtsList")
    public List<Map<String, Object>> getEbsSuspectedDtsList(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsSuspectedDtsListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Suspected Substation List 의 Total Count 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WSDLDocumentationCollection({
            @WSDLDocumentation(value = "Total Count of Suspected Substation List to look up.", placement = WSDLDocumentation.Placement.BINDING_OPERATION),
            @WSDLDocumentation(value = "EbsSuspectedDtsList Count", placement = WSDLDocumentation.Placement.BINDING_OPERATION_OUTPUT) })
    @WebMethod
    @WebResult(name = "EbsSuspectedDtsListCount")
    public Integer getEbsSuspectedDtsListTotalCount(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsStateChartData<b/>
     * method Desc : Energy Balance Monitoring 미니가젯에서 Normal/Suspected Substation Count Chart Data 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDtsStateChartList")
    public List<Map<String, Object>> getEbsDtsStateChartData(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsTreeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Data 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDtsTree")
    public List<Map<String, Object>> getEbsDtsTreeData(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsTreeContractNodeData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Contract Node Data 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDtsTreeContractMeterNodeList")
    public List<Map<String, Object>> getEbsDtsTreeContractMeterNodeData(
            @WebParam(name = "conditionMap") Map<String, Object> conditionMap);

 
    /**
     * method name : getEbsDtsListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS List 의 Total Count 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDtsListCount")
    public Integer getEbsDtsListTotalCount(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsMeterListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Meter List 의 Total Count 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsMeterListCount")
    public Integer getEbsMeterListTotalCount(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsContractMeterList<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Contract Meter List 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsContractMeterList")
    public List<Map<String, Object>> getEbsContractMeterList(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsContractMeterListTotalCount<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 Contract Meter List 의 Total Count 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsContractMeterListCount")
    public Integer getEbsContractMeterListTotalCount(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsNameDup<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Name 의 중복을 체크한다.
     * 
     * @param conditionMap
     * @return true : 중복
     */
    @WebMethod
    @WebResult(name = "EbsDtsNameDup")
    public boolean getEbsDtsNameDup(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : insertEbsDts<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 정보를 저장한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    public void insertEbsDts(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : updateEbsDtsList<b/> 
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 리스트를 수정한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    public void updateEbsDtsList(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : addEbsMeterNode<b/> 
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Meter Node 를 추가한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    public void addEbsMeterNode(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : addEbsContractMeterNode<b/> 
     * method Desc : Energy Balance Monitoring 맥스가젯에서 Meter Phase 에 Contract Meter Node 를 추가한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    public void addEbsContractMeterNode(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : deleteEbsDtsTreeNode<b/> 
     * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS Tree 의 Node 를 삭제한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    public void deleteEbsDtsTreeNode(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsImportChartData<b/> 
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 당월 Import
     * Energy Data, 당월/전월/전년도 Consume Energy Data 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDtsImportChartMap")
    public Map<String, Object> getEbsDtsImportChartData(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsDtsConsumeChartData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree Chart 의 당월/전월/전년도
     * Consume Energy Data 를 조회한다.
     * 
     * @param conditionMap
     * @return
     */
    @WebMethod
    @WebResult(name = "EbsDtsConsumeChartMap")
    public Map<String, Object> getEbsDtsConsumeChartData(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);

    /**
     * method name : getEbsExportExcelData<b/>
     * method Desc : Energy Balance Monitoring 맥스가젯의 DTS Tree 의 Export Excel Data 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> supplierId : Supplier.id - supplier id
     * <li> searchStartDate : String - 조회시작일자(yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자(yyyyMMdd)
     * <li> searchPreStartDate : String - 조회시작일자 - 7일(yyyyMMdd)
     * <li> dtsName : DistTrfmrSubstation.name - dts name
     * <li> threshold : DistTrfmrSubstation.threshold - threshold
     * <li> locationId : Location.id - location id
     * <li> suspected : Boolean - 의심이 가는 dts 만 조회할지 여부
     * </ul>
     * 
     * @return List of List {Integer - number
     *                       Location.name - location name
     *                       DistTrfmrSubstation.name - dts name
     *                       DistTrfmrSubstation.threshold - threshold
     *                       Double - Delivered Energy [kWh]
     *                       Double - Tolerance Delivered Energy [kWh]
     *                       Double - Consumed Energy [kWh]
     *                      }
     */
    @WebMethod
    @WebResult(name = "EbsExportExcelDataList")
    public List<List<Object>> getEbsExportExcelData(@WebParam(name = "conditionMap") Map<String, Object> conditionMap);
}