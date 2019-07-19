package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;
import com.aimir.model.mvm.RealTimeBillingEM;
import com.aimir.model.system.Contract;

public interface RealTimeBillingEMDao extends GenericDao<RealTimeBillingEM, Integer>{

    /**
     * Billing Data Current TOU 조회
     * 
     * @param conditionMap
     * @param isCount
     * @return 조회 결과
     */
    public List<Map<String, Object>> getBillingDataCurrent(Map<String, Object> conditionMap, boolean isCount);

    /**
     * Billing Data Current TOU 리포트 데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    public List<Map<String, Object>> getBillingDataReportCurrent(Map<String, Object> conditionMap);

    /**
     * Billing Data 일별 TOU 리포트 상세데이터 조회
     * 
     * @param conditionMap
     * @return 조회 결과
     */
    public List<Map<String, Object>> getBillingDetailDataCurrent(Map<String, Object> conditionMap);
    
    /**
     * @Methodname getMonthlyMaxDemandByMeter
     * @Date 2013. 11. 29.
     * @Author scmitar1
     * @ModifiedDate 
     * @Description 계약정보와 년월에 따른 최대 수요량
     * @param meter
     * @param yyyymm
     * @return Map: maxDemand 최대수요량, writeDate 최대수요발생일
     */
    public Map<String, Object> getMonthlyMaxDemandByMeter(Meter meter, String yyyymm);
    
    /**
     * 지정한 날짜보다 작은 데이터중 가장 최신의 CummAtvPwrDmdMaxImpRateTot 값을 가지고 온다.
     * @param conditionMap
     */
    public Map<String, Object> getCummAtvPwrDmdMaxImpRateTot(Map<String, Object> conditionMap);
    
    public RealTimeBillingEM getNewestRealTimeBilling(String mdevId, DeviceType mdevType, String yyyymm);
    
    /**
     * method name : getSgdgXam1RealTimeData<b/>
     * method Desc : SgdgXam1테이블에 넣을 데이터들을 조회해온다.
     * 
     * @return
     */
    public List<Map<String, Object>> getSgdgXam1RealTimeData(Map<String, Object> conditionMap);
    
    /**
     * 현재 빌링값을 삭제한다.
     * @param meterId
     * @param yyyymmdd
     */
    public void delete(String meterId, String yyyymmdd);
}
