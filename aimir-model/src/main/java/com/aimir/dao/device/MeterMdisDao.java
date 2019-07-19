package com.aimir.dao.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;

/**
 * MeterMdisDao.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 11. 22. v1.0        문동규   MDIS 관련 method 를 기존 Dao(MeterDao) 에서 분리
 * </pre>
 */
public interface MeterMdisDao extends GenericDao<Meter, Integer> {
    public List<Object> getMetersByMcuName(String name);
    
    public Meter get(String mdsId);
    
    public Meter getInstallProperty(String installProperty);
    
    /**
     * 검침실패한 미터 목록을 조회한다.
     * @param param
     * @return totalCount 전체건수,list 페이징데이터
     */
    public Map<String,Object> getMeteringFailureMeter(Map<String,Object> param);
    
	/**
	 * 설치일자,공급사 기준 전체 미터갯수를 조회한다.
	 * @param params
	 * @return
	 */
	public Integer getMeterCount(Map<String,Object> params);
	
	/**
	 * LP_XX(전기,가스,수도,열량) 에 검침된 데이터가 특정일자가 전체 누락된
	 * 미터의 갯수를 조회한다.
	 * (누락건이 없는 미터수 + 부분누락된 미터수)를 조회하여 전체 미터수에서 빼서 계산한다.
	 * @param params
	 * @return
	 */
	public Integer getAllMissingMeterCount(Map<String,Object> params);
	
	/**
	 * LP_XX(전기,가스,수도,열량) 에 검침된 데이터가 부분적(특정시간,시간의 특정주기)으로 누락된
	 * 미터의 갯수를 조회한다.
	 * 조회기간에 현재일자가 포함되어있을경우
	 * 1.조회시작일~조회종료일전일
	 * 2.조회종료일,00시~ 현재시간 전시간
	 * 3.조회종료일,현재시간
	 * 세가지 조건으로 각각의 쿼리문을 수행한다.
	 * @param params
	 * @return
	 */
	public Integer getPatialMissingMeterCount(Map<String,Object> params);
	
	/**
	 * 일별,시간별로 LP(전기,가스,수도,열량) 에서 누락된 미터갯수를 조회한다.
	 * 조회일자가 현재일자일경우
	 * 1.00시~ 현재시간 전시간
	 * 2.현재시간
	 * 두가지 조건으로 각각의 쿼리문을 수행한다.
	 * @param params
	 * @return
	 */
	public List<Object> getMissingMetersByHour(Map<String,Object> params);
    

	/**
	 * 일별로 LP(전기,가스,수도,열량) 에서 누락된 미터갯수를 조회한다.
	 * 부분누락,전체누락이 아닌 갯수를 조회하여 전체 미터갯수에서 뺀다음 누락건수를 계산한다.
	 * 
	 * 조회기간에 현재일자가 포함되어있을경우
	 * 1.조회시작일~조회종료일전일
	 * 2.조회종료일,00시~ 현재시간 전시간
	 * 3.조회종료일,현재시간
	 * 세가지 조건으로 각각의 쿼리문을 수행한다.
	 * @param params
	 * @return
	 */
	public List<Object> getMissingMetersByDay(Map<String,Object> params);
	
	/**
	 * 검침데이터(LP) 에 누락된 미터목록을 조회한다.
	 * @param params
	 * @return
	 */
	public List<Object> getMissingMeters(Map<String,Object> params);
		

    // Mini Gadget
    public List<Object> getMiniChartMeterTypeByLocation(Map<String, Object> condition);   
    public List<Object> getMiniChartMeterTypeByCommStatus(Map<String, Object> condition);
                           
    public List<Object> getMiniChartLocationByMeterType(Map<String, Object> condition);				
    public List<Object> getMiniChartLocationByCommStatus(Map<String, Object> condition);				
                           
    public List<Object> getMiniChartCommStatusByMeterType(Map<String, Object> condition);
    public List<Object> getMiniChartCommStatusByLocation(Map<String, Object> condition);
    

    // Max Gadget
    public List<Object> getMeterSearchChart(Map<String, Object> condition);    
    public List<Object> getMeterSearchGrid(Map<String, Object> condition); 
    
    public List<Object> getMeterLogChart(Map<String, Object> condition);    
    public List<Object> getMeterLogGrid(Map<String, Object> condition);
    
    public List<Object> getMeterCommLog(Map<String, Object> condition);
    public List<Object> getMeterOperationLog(Map<String, Object> condition);
    
    public Map<String, Object> getMeterSearchCondition();
    
    public List<Meter> getMeterWithGpio(HashMap<String, Object> condition);

    public List<Meter> getMeterWithoutGpio(HashMap<String, Object> condition);
    
    public List<Meter> getMeterHavingModem(Integer id);
    
    public List<Object> getMeteringDataByMeterChart(Map<String, Object> condition);
    public List<Object> getMeteringDataByMeterGrid(Map<String, Object> condition);
    
    // ModemMax Gadget
    public List<Object> getMeterListByModem(Map<String, Object> condition);
    public List<Object> getMeterListByNotModem(Map<String, Object> condition);    
    
 // VEE에서 사용
    public Integer getMeterVEEParamsCount(HashMap<String, Object> hm);
    
    
    // 고객-계약
    public List<Object> getMeterListForContract(Map<String, Object> condition);
    
	public List<Object> getGroupMember(Map<String, Object> condition);
	
	/**
	 * 
	 * @param deviceSerial 모뎀 시리얼번호
	 * @param modemPort    미터와 매핑된 모뎀 포트번호 (디폴트 0)
	 * @return
	 */
	public Meter getMeterByModemDeviceSerial(String deviceSerial, int modemPort);
	
	public List<Object> getMeterSupplierList();


    /**
     * method name : getMeterSearchChartMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 chart 데이터를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Object> getMeterSearchChartMdis(Map<String, Object> condition);

    /**
     * method name : getMeterSearchGridMdis<b/>
     * method Desc : MDIS - Meter Management 화면에서 미터기 정보 리스트를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Object> getMeterSearchGridMdis(Map<String, Object> condition);

    /**
     * method name : getMeterMdisExportExcelData<b/>
     * method Desc : MDIS - Meter Management 가젯의 Export Excel Data 를 조회한다.
     *
     * @param condition
     * @return
     */
    public List<Map<String, Object>> getMeterMdisExportExcelData(Map<String, Object> conditionMap);
}