package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.service.mvm.bean.FailureMeteringData;
import com.aimir.service.mvm.bean.MeteringFailureData;

public interface MeteringFailureManager {
	/**
	 * 검침실패가젯에서 조회할 지역별검침실패율 목록조회
	 * 지역별 하위지역을 포함하는 구조로 조회한다.
	 * @param inputDate	 조회기준일자
	 * @param dateType	 일별,주별,월별,계절별 구분코드
	 * @param meterType	 전기,가스,수도,열량계 구분코드
	 * @param locationId 조회할 지역 ID	
	 * @param locationType 1:입력된 ID의 하위 지역목록조회
	 * 					   -1:입력된지역의 상위 지역목록조회
	 * 					 null:입력된 ID의 하위 지역목록조회
	 * @return List<MeteringFailureData>
	 */
	public List<Map<String,Object>> getMeteringFailureRateListByLocationWithChild(Map<String,Object> params);

	/**
	 * 검침실패가젯에서 조회할 지역별검침실패율 목록조회
	 * 지역별 하위지역을 포함하는 구조로 조회한다.
	 * @param inputDate	 조회기준일자
	 * @param dateType	 일별,주별,월별,계절별 구분코드
	 * @param meterType	 전기,가스,수도,열량계 구분코드
	 * @param locationId 조회할 지역 ID	
	 * @param locationType 1:입력된 ID의 하위 지역목록조회
	 * 					   -1:입력된지역의 상위 지역목록조회
	 * 					 null:입력된 ID의 하위 지역목록조회
	 * @return List<MeteringFailureData>
	 */
	public List<Map<String,Object>> getMeteringFailureRateListByLocation(Map<String,Object> params);

	
	/**
	 * 검심실패한 미터정보 목록을 조회한다.
	 * @param 
	 * @return
	 */
	public Map<String, Object> getMeteringFailureMeter(Map<String,Object> params);
    
	/**
	 * 검심실패한 미터의 조회기간내 사용량을 조회한다.
	 * @param paramMap
	 * @return
	 */
	public List<FailureMeteringData> getMeteringFailureMeteringData(Map<String,Object> params);

    /**
     * method name : getMeteringCountListPerLocation<b/>
     * method Desc : location 별 검침 성공/실패 개수를 조회한다.
     *
     * @param params
     * <ul>
     * <li> searchStartDate : String - 조회시작일자 (yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자 (yyyyMMdd)
     * <li> meterType : meter type (ex. EnrgyMeter)
     * <li> locationId : Location.id - location id
     * <li> supplierId : Supplier.id - supplier id
     * </ul>
     * 
     * @return List of {@link com.aimir.service.mvm.bean.MeteringFailureData}
     */
    public List<MeteringFailureData> getMeteringCountListPerLocation(Map<String,Object> params);

    /**
     * method name : getMeteringFailureRateListWithChildren<b/>
     * method Desc :
     *
     * @param conditionMap
     * <ul>
     * <li> searchStartDate : String - 조회시작일자 (yyyyMMdd)
     * <li> searchEndDate : String - 조회종료일자 (yyyyMMdd)
     * <li> meterType : meter type (ex. EnergyMeter)
     * <li> supplierId : Supplier.id - supplier id
     * </ul>
     * 
     * @return List of {@link com.aimir.service.mvm.bean.MeteringFailureData}
     */
    public List<MeteringFailureData> getMeteringFailureRateListWithChildren(Map<String, Object> conditionMap);
}