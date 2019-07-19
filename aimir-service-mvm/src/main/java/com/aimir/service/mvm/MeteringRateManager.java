package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.service.mvm.bean.MeteringFailureData;
import com.aimir.service.mvm.bean.MeteringRateData;

public interface MeteringRateManager {

	/**
	 * 검침데이터 조회 가젯에서 조회할 지역별 검침성공율 목록조회
	 * 지역별 하위지역을 포함하는 구조로 조회한다.
	 * @param params
	 * 	- startDate	    조회 시작일자
	 * 	- endDate	    조회 종료일자
	 *  - dateType	    일별,주별,월별,계절별 구분코드
	 *  - meterType	    전기,가스,수도,열량계 구분코드
	 *  - locationId  조회할 지역 ID	
	 *  - locationType  1:입력된 ID의 하위 지역목록조회
	 * 				   -1:입력된지역의 상위 지역목록조회
	 * 				 null:입력된 ID의 하위 지역목록조회
	 * @return List<MeteringRateData>
	 */
	public List<MeteringRateData> getMeteringRateListByLocationWithChild(Map<String,Object> params);

    /**
     * method name : getMeteringSuccessRateListWithChildren<b/>
     * method Desc : Metering Data 가젯에서 location 별 성공율 grid 데이터를 조회한다. 
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
    public List<MeteringFailureData> getMeteringSuccessRateListWithChildren(Map<String, Object> conditionMap);
}