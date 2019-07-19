package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import com.aimir.constants.CommonConstants.MeterType;

public interface PowerGenerationManager {

	/**
	 * 발전 현황을 얻는다.
	 * 날자 파라미터가 주어지지 않으면 서버시간으로 현재날짜로 계산된다.
	 * 
	 * @param meterType
	 * @param now
	 * @return
	 */
	public Map<String, Double> getGenerationInfo(MeterType meterType, String now);
	
	/**
	 * 미터별 발전량을 얻는다.
	 * 
	 * @param meterType
	 * @param condition
	 * @return
	 */
	public List<Map<String, Object>> getGenerationValueAmountByMeter(
		MeterType meterType, Map<String, Object> condition);

	/**
	 * 현재 발전량 통계를 조회한다.
	 * 일간, 주간, 월간, 분기간으로 통계한다.
	 * 
	 * @param condition
	 * @return
	 */
	public Map<String, List<Map<String, String>>> getStatistics(MeterType type, Map<String, Object> condition);
}
