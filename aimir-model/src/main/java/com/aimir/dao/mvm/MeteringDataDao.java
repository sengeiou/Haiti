package com.aimir.dao.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.MeteringData;

public interface MeteringDataDao extends GenericDao<MeteringData, Integer>{

	public Map<String, Object> getTotalCountByLocation(Map<String,Object> condition);
	public String getSuccessCountByLocation(Map<String,Object> condition);
	public String getSuccessCountByLocationJeju(Map<String,Object> condition);

	@Deprecated
	public String getFailureCountByCause(Map<String,Object> condition, int cause);

    /**
     * method name : getFailureCountByCauses<b/>
     * method Desc : MeteringFail 가젯의 Cause1/Cause2 Count 를 조회한다.<b/>
     *               1. meteringdata_em 테이블에 없는 meter 테이블 데이터의 LAST_READ_DATE 를 검색.<b/>
     *               2. LAST_READ_DATE 의 값이 현재일과 하루이상 차이가 나면 Cause1(통신장애)<b/>
     *               3. LAST_READ_DATE 의 값이 현재일과 같으면 Cause2(포멧에러)
     *
     * @param condition
     * @return
     */
	@Deprecated
    public Map<String, String> getFailureCountByCauses(Map<String, Object> condition);

    @Deprecated
	public String getFailureCountByEtc(Map<String,Object> condition);
	public Integer getCommPermitMeterCount(Map<String,Object> params);
	public Integer getPermitMeterCount(Map<String, Object> params);
	public Integer getTotalGatheredMeterCount(Map<String, Object> params);	
//	public Integer getSLAMeterCount(Map<String, Object> params);
	public List<Object> getLastRegisterMeteringData(Map<String, Object> preCondition);
}
