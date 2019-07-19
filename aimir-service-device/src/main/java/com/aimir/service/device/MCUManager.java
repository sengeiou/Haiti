package com.aimir.service.device;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import com.aimir.model.device.CommLog;
import com.aimir.model.device.CommStateByLocationVO;
import com.aimir.model.device.LocationByCommStateVO;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUTypeByCommStateVO;
import com.aimir.model.device.MCUTypeByLocationVO;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.service.device.impl.MockData;

@WebService(name="McuService", targetNamespace="http://aimir.com/services")
public interface MCUManager {

    @Deprecated
    public List<Map<String, Object>> getGridData(Map<String, String> conditionMap);

	public List<Map<String, Object>> getDcuGridData(Map<String, Object> conditionMap);

    @Deprecated
    public List<MCU> getGridDataExcel(Map<String, String> conditionMap);

	public List<MCU> getDcuGridDataExcel(Map<String, Object> conditionMap);

    public Integer getDcuGridDataTotalCount(Map<String, Object> conditionMap);

	public List<MCUTypeByLocationVO> getMcusByCondition(Map<String, String> conditionMap);
    
	public List<String> getHwVersions();

	public List<String> getSwVersions();

	@WebMethod
	@WebResult(name="locationLists")
	public Map<String, List<String>> getLocationTreeToRows(int supplierId);

	@WebMethod
	public @WebResult(name="MCU") MCU getMCU(@WebParam(name="McuIdInteger") Integer mcuId);
	
	@WebMethod (operationName="getMCUBySysId")
	public @WebResult(name="MCU") MCU getMCU(@WebParam(name="SysIdString") String name);
	
	@WebMethod (operationName="updateMCUByMap")
	public void updateMCU(Map<String, String> map);

	public Integer getMCUCountByCondition(Map<String, String> condition);
	
	public Map<String, String> getPagingInfo(int page, Integer totalRowCount, String pagingType);
	
	public List<MCUTypeByLocationVO> getMCUTypeByLocationDataBack();
	
	public String getMCUTypeByLocationData(String supplierId);
	
	public Map<String, Object> getMCUTypeListByLocationData(String supplierId);
	
	public List<MCUTypeByCommStateVO> getMCUTypeByCommStateData(String supplierId);
	
	public String getLocationByMCUTypeData(String supplierId);
	
	public List<Map<String, Object>> getLocationListByMCUTypeData(String supplierId);
	
	public List<LocationByCommStateVO> getLocationByCommStateData(String supplierId);
	
	public String getCommStateByMCUTypeData(String supplierId);
	
	public List<Map<String, Object>> getCommStateListByMCUTypeData(String supplierId);
	
	public List<CommStateByLocationVO> getCommStateByLocationDataBack();
	
	public String getCommStateByLocationData(String supplierId);
	
	public Map<String, Object> getCommStateListByLocationData(String supplierId);

	@Deprecated
	public List<CommLog> getCommunicationLogs(String[] array);
	
	public List<MockData> getUpdateLogs(String[] array);

    @Deprecated
    public List<Map<String, String>> getBrokenLogs(String[] array);

    @Deprecated
    public List<OperationLog> getCommandLogs(String[] array);

    @Deprecated
    public Map<String, String> getCommunicationLogCount(String[] array);

	public Map<String, String> getUpdateLogCount(String[] array);

    @Deprecated
    public Map<String, String> getBrokenLogCount(String[] array);

    @Deprecated
    public Map<String, String> getCommandLogCount(String[] array);

    @Deprecated
    public Set<Modem> getConnectedDevices(Integer mcuId);

	public List<MCU> getChartMCUs(String[] array);
	
	public MCU insertMCU(MCU mcu);

	public MCU updateMCU(MCU mcu);

	public void deleteMCU(Integer mcuId);
	
	public Integer updateDcuStatus(Integer mcuId);
	
	public List<Object> getMCUNameList(Map<String, Object> condition);
	
	public String getInstallDateChage(MCU mcu, Integer supplierId);
	
	public String getLastCommDateChage(MCU mcu, Integer supplierId);

    /**
     * method name : getConnectedDeviceList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getConnectedDeviceList(Map<String, Object> conditionMap);

    /**
     * method name : getConnectedDeviceListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getConnectedDeviceListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getCommLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getCommLogList(Map<String, Object> conditionMap);

    /**
     * method name : getCommLogListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getCommLogListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getCommLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getCommLogData(Map<String, Object> conditionMap);

    /**
     * method name : getEventAlertLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEventAlertLogList(Map<String, Object> conditionMap);

    /**
     * method name : getEventAlertLogListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getEventAlertLogListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getOperationLogList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getOperationLogList(Map<String, Object> conditionMap);

    /**
     * method name : getOperationLogListTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Integer getOperationLogListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getMCUMiniChart<b/>
     * method Desc : 집중기관리 미니가젯에서 통신상태/집중기타입 정보를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<List<Map<String, Object>>> getMCUMiniChart(Map<String, Object> condition);
    
    @WebMethod(operationName="setLocation")
    public @WebResult(name=("result")) int setLocation(@WebParam(name="MCU_ID") String mcuId,
            @WebParam(name="Address") String address, 
            @WebParam(name="GPS_X") double x, 
            @WebParam(name="GPS_Y") double y,
            @WebParam(name="GPS_Z") double z);
    
    public List<String> getFirmwareVersionList(Map<String, Object> condition);
    
    public List<String> getDeviceList(Map<String, Object> condition);
    
    public List<String> getTargetList(Map<String, Object> condition);
    
    /**
     * method name : getTitleName<b/>
     * method Desc : Upload Excel Template getTitle
     *
     * @param String
     * @return
     */
    public String getTitleName(String excel, String ext);

    /**
     * method name : readOnlyExcelXLS / readOnlyExcelXLSX<b/>
     * method Desc : Upload Excel Data Divide
     *
     * @param String
     * @return
     */
	public Map<String, Object> readOnlyExcelXLS(String excel, int supplierId);
	public Map<String, Object> readOnlyExcelXLSX(String excel, int supplierId);
	
	public List<String> getMcuSearchedList(Map<String, Object> conditionMap);
	
	//sp-1066
    public List<String> getCodiFirmwareVersionList(Map<String, Object> condition);
    public List<String> getCodiDeviceList(Map<String, Object> condition);
    public List<String> getCodiTargetList(Map<String, Object> condition);
    
    public List<Map<String, Object>> getCodiGridData(Map<String, Object> conditionMap);
    public Integer getCodiGridDataTotalCount(Map<String, Object> conditionMap);
}