package com.aimir.dao.device;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;

public interface ModemDao extends GenericDao<Modem, Integer> {
    public Modem get(String deviceSerial);
   
    public Serializable setModem(Modem modem);   // Modem 정보 저장
    
    
    // Mini Gadget
    public List<Object> getMiniChartModemTypeByLocation(Map<String, Object> condition);   
    public List<Object> getMiniChartModemTypeByCommStatus(Map<String, Object> condition);
                           
    public List<Object> getMiniChartLocationByModemType(Map<String, Object> condition);				
    public List<Object> getMiniChartLocationByCommStatus(Map<String, Object> condition);				
                           
    public List<Object> getMiniChartCommStatusByModemType(Map<String, Object> condition);
    
    public int deleteModemStatus(int modemId, Code code);

	public List<Object> getMiniChartCommStatusByModemType(Map<String, Object> condition,String[] arrFmtmessagecommalert );
    
    
    public List<Object> getMiniChartCommStatusByModemType2(Map<String, Object> condition);
    
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition);
    
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition,String[] arrFmtmessagecommalert );
    
    
    // Max Gadget
    public List<Object> getModemSearchChart(Map<String, Object> condition);    
    public List<Object> getModemSearchGrid(Map<String, Object> condition);
    
    public List<Object> getModemSearchGrid2(Map<String, Object> condition);
    
    public List<Object> getModemLogChart(Map<String, Object> condition);    
    
    /*  모뎀가젯의 history탭 : 구현이 안되어 있는 부분이고 필요없는 기능이라 삭제
     * public List<Object> getModemLogGrid(Map<String, Object> condition);*/
    
    public List<Object> getModemCommLog(Map<String, Object> condition);
    public List<Object> getModemOperationLog(Map<String, Object> condition);

    public Map<String, Object> getModemSearchCondition();
    

    // Etc
    public List<Object> getModemSerialList(Map<String, Object> condition);
    
    public List<Modem> getModemWithGpio(HashMap<String, Object> condition);

	public List<Object> getBatteryLog(Map<String, Object> condition);
	public List<Object> getBatteryLogByLocation(Map<String, Object> condition);
	public List<Object> getBatteryLogList(Map<String, Object> condition);
	public List<Object> getBatteryLogList(Map<String, Object> condition, boolean isCount);
	public List<Object> getBatteryLogDetailList(Map<String, Object> condition);
	public List<Object> getBatteryLogDetailList(Map<String, Object> condition, boolean isCount);

	public List<Modem> getModemWithoutGpio(HashMap<String, Object> condition);

	/**
	 * method name : getModemMapDataWithoutGpio<b/>
	 * method Desc : Modem Management 맥스가젯의 위치정보 탭에서 맵정보를 조회한다.
	 *
	 * @param condition
	 * @return
	 */
	public List<Modem> getModemMapDataWithoutGpio(HashMap<String, Object> condition);

    public List<Modem> getModemHavingMCU(Integer id);

    @Deprecated
	public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Modem 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);
    
    /**
     * method name : getHomeGroupMemberSelectData<b/>
     * method Desc : HomeGroup Management 가젯에서 Member 로 등록 가능한 Modem 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Object> getHomeGroupMemberSelectData(Map<String, Object> conditionMap);
    
	public List<Object> getModemListByMCUsysID(String sys_id);
	
	public List<Object> getModemIdListByDevice_serial(String device_serial);
	
	public List<Object> getGroupMember(String name, int supplierId);

    /**
     * method name : getMcuConnectedDeviceList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getMcuConnectedDeviceList(Map<String, Object> conditionMap, boolean isCount);
    
    /**
     * method name : getMcuCount<b/>
     * method Desc : 모뎀 개수를 조회
     *
     * @param condition
     * @return
     */
    public Integer getModemCount(Map<String, String> condition);
    
    public List<Object> getDeviceSerialByMcu(String sys_id);
    
    public void updateModemColumn(String modemTypeName, String DeviceSerial);
    
    public List<Object[]> getModemByIp(String ip);		// INSERT SP-193
    
    public String getModemIpv6ByDeviceSerial(String serial);	// INSERT SP-193
    
    /**
     * method name : getModemWithMCU<b/>
     * method Desc : SP-572
     * @param condition
     * @return
     */
    public List<Map<String, Object>>  getModemWithMCU(Map<String, Object> condition);

    public List<String> getFirmwareVersionList(Map<String, Object> condition);
    
    public List<String> getDeviceList(Map<String, Object> condition);

    public List<String> getDeviceListModem(Map<String, Object> condition);

    public List<String> getTargetList(Map<String, Object> condition);
    
    public List<String> getTargetListModem(Map<String, Object> condition);

    public List<Object> getModemList(Map<String, Object> condition);

	public List<Map<String, Object>> getValidModemList(Map<String, Object> condition);	//sp-1028
	
	public List<Map<String, Object>> getParentDevice(Map<String, Object> condition);	//sp-1004
}