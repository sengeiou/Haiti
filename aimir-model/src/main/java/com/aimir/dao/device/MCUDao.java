package com.aimir.dao.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.CommStateByLocationVO;
import com.aimir.model.device.LocationByCommStateVO;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUTypeByCommStateVO;
import com.aimir.model.device.MCUTypeByLocationVO;
import com.aimir.model.system.Code;

public interface MCUDao extends GenericDao<MCU, Integer> {
    public MCU get(String sysID);

    public List<String> getHwVersions();

    public List<String> getSwVersions();
    
    public List<MCU> getMcusByCondition(String[] array, int rowPerPage);

    public List<MCU> getMcusByCondition(Map<String, String> conditionMap);

    public Integer getMCUCountByCondition(Map<String, String> conditionMap);

    public List<MCUTypeByLocationVO> getMCUTypeByLocationDataBack();

    public String getMCUTypeByLocationData(String supplierId);

    public List<Object> getMCUTypeListByLocationData(String supplierId);

    public List<MCUTypeByCommStateVO> getMCUTypeByCommStateData(String supplierId);

    public String getLocationByMCUTypeData(String supplierId);

    public List<Map<String, Object>> getLocationListByMCUTypeData(String supplierId);

    public List<LocationByCommStateVO> getLocationByCommStateData(String supplierId);

    public String getCommStateByMCUTypeData(String supplierId);

    public List<Map<String, Object>> getCommStateListByMCUTypeData(String supplierId);

    public List<CommStateByLocationVO> getCommStateByLocationDataBack();

    public String getCommStateByLocationData(String supplierId);

    public List<Object> getCommStateListByLocationData(String supplierId);

    public Map<String, Object> getDcuGridData(Map<String, Object> conditionMap, boolean isCount);

    @Deprecated
    public List<MCU> getGridData(Map<String, String> conditionMap);

    @Deprecated
    public Integer getMcuGridDataTotalCount(String[] conditionArray);

    public List<Object> getMCUNameList(Map<String, Object> condition);

    public List<MCU> getMCUWithGpio(HashMap<String, Object> condition);

    public List<MCU> getMCUWithGpioCodi(HashMap<String, Object> condition);

    public List<MCU> getMCUWithoutGpio(HashMap<String, Object> condition);

    public List<MCU> getMCUMapDataWithoutGpio(HashMap<String, Object> condition);

    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 MCU 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);

    public void updateFWByotaEvent(String sql) throws Exception;

    public List<MCU> getMCUbyCodi(String codiID);

    /**
     * method name : getMiniChartMCUTypeByCommStatus
     * method Desc : 집중기관리 미니가젯에서 통신상태별 집중기타입 정보를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<List<Map<String, Object>>> getMiniChartMCUTypeByCommStatus(Map<String, Object> condition);

    /**
     * method name : getMiniChartCommStatusByMCUType
     * method Desc : 집중기관리 미니가젯에서 집중기타입별 통신상태 정보를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<List<Map<String, Object>>> getMiniChartCommStatusByMCUType(Map<String, Object> condition);

    /**
     * method name : getMiniChartCommStatusByMCUType
     * method Desc : 집중기관리 미니가젯에서 집중기타입별 통신상태 정보를 조회한다.
     *
     * @param condition
     * @param arrMessage
     * @return
     */
    public List<List<Map<String, Object>>> getMiniChartCommStatusByMCUType(Map<String, Object> condition, String[] arrMessage);

	public List<MCU> getMcusByTargetList(int searchType, List<String> targetList);
	
	public List<Map<String, Object>> getMcuSysIdList();
	
	/**
     * method name : deleteDcuStatus
     * method Desc : 화면에서 보이지 않게 하도록 Dcu 상태 변경
     * 
	 * @param mcuId
	 * @param code //delete를 의미하는 코드
	 * @return
	 */
	public int updateDcuStatus(int mcuId, Code code);
	
	public List<String> getMcuByIp(String ip);
	
	public List<String> getFirmwareVersionList(Map<String, Object> condition);
	
	public List<String> getDeviceList(Map<String, Object> condition);
	
	public List<String> getTargetList(Map<String, Object> condition);

	/**
     * method name : getGroupMcuList<b/>
     * method Desc : Group으로 MCU Schedule 가져올 때 Selected Group MCU 리스트 출력
     *
     * @param groupId
     * @return
     */
	public List<String> getGroupMcuList(Integer groupId);
	
	public List<Map<String, Object>> getValidMCUList(Map<String, Object> condition);	//sp-1028
	
	//sp-1066
	public List<String> getCodiFirmwareVersionList(Map<String, Object> condition);
	public List<String> getCodiDeviceList(Map<String, Object> condition);
	public List<String> getCodiTargetList(Map<String, Object> condition);
	
	public Map<String, Object> getCodiGridData(Map<String, Object> conditionMap, boolean isCount);

	public List<Map<String, Object>> getValidCodiList(Map<String, Object> condition);

}
