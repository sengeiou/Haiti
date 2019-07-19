package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.MeterProgramLog;

public interface MeterProgramLogDao extends GenericDao<MeterProgramLog, Integer> {
    /**
     * method name : findbyMeter<b/>
     * method Desc : Meter 정보로 MeterProgramLog 내역을 조회한다.
     * 
     * @see MeterProgramLogDao#findbyMeterId(Integer)
     * <p>내부적으로 findbyMeterId 를 호출한다.</p>
     * @param meter
     * @return List of MeterProgramLog @see com.aimir.model.system.MeterProgramLog
     */
    public List<MeterProgramLog> findbyMeter(Meter meter);

    /**
     * method name : findbyMeterId<b/>
     * method Desc : meter id 의 조회 조건에, 중복되지 않은 가장 최근 log목록을 조회한다. 
     * 
     * @param meterId Meter.id
     * @return List of MeterProgramLog @see com.aimir.model.system.MeterProgramLog
     */
    public List<MeterProgramLog> findbyMeterId(Integer meterId);

    /**
     * method name : getMeterProgramLogList<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 를 조회한다.
     *
     * @param conditionMap
	 * <ul>
	 * <li> configId : MeterProgram.id
	 * <li> page : page number
	 * <li> pageSize : page size
	 * </ul>
	 * 
     * @param isCount total count 여부
     * @return List of Map if isCount is true then return {total,count}
     *                     else return {
     *                     				LAST_MODIFIED_DATE,
     *                     				METERPROGRAM_ID,
     *                     				METERPROGRAM_KIND,
     *                     				SUCCESS_COUNT,
     *                     				FAILURE_COUNT,
     *                     				TRY_COUNT}
     */
    @Deprecated
    public List<Map<String, Object>> getMeterProgramLogList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getMeterProgramLogListRenew<b/>
     * method Desc : Vendor Model 맥스가젯의 Meter Program 탭에서 Meter Program Log 를 조회한다.
     *
     * @param conditionMap
     * <ul>
     * <li> configId : MeterProgram.meterConfig.id
     * <li> page : page number
     * <li> limit : page size
     * </ul>
     * 
     * @param isCount total count 여부
     * @return List of Map if isCount is true then return {total,count}
     *                     else return {
     *                                  lastModifiedDate,
     *                                  meterProgramId,
     *                                  meterProgramKind,
     *                                  successCount,
     *                                  failureCount,
     *                                  tryCount}
     */
    public List<Map<String, Object>> getMeterProgramLogListRenew(Map<String, Object> conditionMap, boolean isCount);
}