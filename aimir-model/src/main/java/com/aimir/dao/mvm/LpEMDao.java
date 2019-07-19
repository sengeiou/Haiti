package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.DataGaps;
import com.aimir.model.mvm.LpEM;
import com.aimir.util.Condition;

public interface LpEMDao extends GenericDao<LpEM, Integer>{

    public List<LpEM> getLpEMsByListCondition(Set<Condition> list);
    public List<Object> getLpEMsCountByListCondition(Set<Condition> set);
    public List<Object> getLpEMsMaxMinSumAvg(Set<Condition> conditions, String div) ;

    public void updateLpEM(LpEM lpem);

    //2010 . 04.05 add : 김민수
	public List<DataGaps> search( int supplier, String tabType, String startDate, String endDate,
			int totalCount, String meter, String deviceCodeType, String deviceId);

	// 2012.03.28 by elevas
	public List<Object> getLpEMs(String mdevType, String mdevId, int dst, String startYYYYMMDDHH, String endYYYYMMDDHH);
	
	/*
	 * 2010.09.07 양철민
	 */
	public List<Object> getLpEMsByNoSendedACD();
	public List<Object> getLpEMsByNoSended();
	public List<Object> getLpEMsByNoSendedDummy(String selectedDate);
	public List<Object> getLpEMsByNoSended(String mdevType);
	public List<Object> getLpEMsByNoSended(String mdevType,String lpTableName);
	public void updateSendedResult(LpEM lpem);
	/**
	 * method name : updateSendedResultByCondition<b/>
     * method Desc : sendResult값을 조회 조건에 따라 업데이트 한다.
     * 
	 * @param conditionMap
	 */
	public void updateSendedResultByCondition(Map<String, Object> conditionMap);
	public List<Object> getTotalCummulValue(String mdevId, String yyyymmdd,int hh, int mm, int interval);
	public List<Object> getHourCummulValueNoSelf(String mdevId, String yyyymmdd,int hh, int mm, int interval);
	public List<Object> getMinCummulValueNoSelf(String mdevId, String yyyymmdd,int hh, int mm, int interval);
	public List<Object> getConsumptionEmCo2LpValuesParentId(Map<String, Object> condition);
	public List<Object> getConsumptionEmLpValuesParentId(Map<String, Object> condition);
	public int getLpInterval( String mdevId );

    /**
     * method name : getMeterDetailInfoLpData<b/>
     * method Desc : MDIS - Meter Management 맥스가젯의 Detail Information 탭에서 LpEM 데이터를 조회한다.
     *
     * @param conditionMap
     * @param channel
     * @return
     */
    public Double getMeterDetailInfoLpData(Map<String, Object> conditionMap, Integer channel);
    
    /**
     * 가장 최근 값을 가져온다.
     * @param meterId
     * @return
     */
    public List<LpEM> getLastData(Integer meterId);
    public List<LpEM> getLastData(String mdsId);
	
    /**
	 * @Methodname getMonthlyUsageByMeter
	 * @Date 2014. 02. 04.
	 * @Author scmitar1
	 * @ModifiedDate 
	 * @Description 특정 contract, 채널에 대하여 월간 사용이력을 구한다.
	 * @param meter
	 * @param yyyymmddhh
	 * @param channels
	 * @return
	 */    
    public List<LpEM> getLpEMByMeter(Meter meter, String yyyymmddhh, Integer... channels);
 
    /**
     * @Methodname getProperLpEMByMeter
     * @Date 2014. 5. 12.
     * @Author scmitar1
     * @ModifiedDate 
     * @Description 특정 contract, 채널에 대하여 월간 사용이력을 구한다. (지정한 yyyymmddhh에서 1주일간의 데이터를 반환한다.) 
     * @param meter
     * @param yyyymmddhh
     * @param channels
     * @return
     */
    public List<LpEM> getProperLpEMByMeter(Meter meter, String yyyymmddhh, Integer... channels);
    
    /**
     * 특정일의 LP를 지운다.
     * @param meterId
     * @param yyyymmdd
     */
    public void delete(String meterId, String yyyymmdd);
    
    /**
     * 해당 미터의 오래된(bDate보다 오래된)LP 삭제
     * @param mdsId
     * @param bDate
     */
	public void oldLPDelete(String mdsId, String bDate);
	
	
	/**
	 * Get the metering rate of LP by DCU's SYS_ID.
	 * @return
	 */
	public List<Object> getLpReportByDcuSys();
	
	/**
	 * Get the metering rate of Meter by DCU's SYS_ID.
	 * @Description 하루에 한건이라도 올린 미터를 기준으로 검침율 조사 
	 * @return
	 */
	public List<Object> getMeterReportByDcuSys();
}