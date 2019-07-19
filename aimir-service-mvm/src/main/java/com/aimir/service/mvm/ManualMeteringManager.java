package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.service.mvm.bean.MeteringListData;

/**
 * 수검침 서비스
 * 
 * @author Yi Hanghee(javarouka@gmail.com, javarouka@nuritelecom.co.kr)
 */
public interface ManualMeteringManager {
	
	/**
	 * 수검침 결과 데이터를 조회한다.
	 * 
	 * @param params 검색 조건 파라미터 맵
	 * @param dayType 검색 날자 타입
	 * @param meterType 미터 타입
	 * @param supplierId 공급자 아이디
	 * @return 결과 리스트
	 */
	@Deprecated
	public List<MeteringListData> getManualMeteringData(
			Map<String, String> params, String dayType, String meterType, String supplierId);
	public List<Map<String, Object>> getManualMeteringData(Map<String, Object> params);
	public List<?> getManualMetering(Map<String, Object> params);
	
	/**
	 * 수검침 결과 데이터의 총 합을 조회한다.
	 * 
	 * @param params 검색 조건 파라미터 맵
	 * @param dayType 검색 날자 타입
	 * @param meterType 미터 타입
	 * @param supplierId 공급자 아이디
	 * @return 결과 맵
	 */
	@Deprecated
	public Map<String, String> getManualMeteringDataTotal(
			Map<String, String> params, String dayType, String meterType, String supplierId);
	public Integer getManualMeteringDataTotal(Map<String, Object> params);
	public Long getManualMeteringTotal(Map<String, Object> params);
	
	/**
	 * 수검침 미터의 에니지 사용량, 탄소배출량 통계를 일별, 주별, 월별, 시즌별로 통계치를 낸다
	 * 
	 * @param mdsId 미터아이디
	 * @param energyType 타입
	 * @return 통계치 맵
	 */
	public Map<String, List<Map<String, String>>> getManualMeteringStatisticsByMdsId(
			String mdsId, String energyType);
	
	/**
	 * 수검침 내역을 업데이트한다.
	 * 
	 * @param data 업데이트할 내역
	 * @return 업데이트된 내역
	 */
	public boolean updateManualMeteringData(String mdsId, String dayType,
			int supplierId, String meteringDate, double meteringValue, Set<String> errSet);
}
