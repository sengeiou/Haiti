/**
 * EnergySavingGoalManager.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.energySavingGoal;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.Contract;
import com.aimir.model.system.EnergySavingTarget;
import com.aimir.model.system.Notification;

/**
 * EnergySavingGoalManager.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 6. 3.   v1.0       김상연         에너지 절감 목표
 *
 */

public interface EnergySavingGoalManager {

	/**
	 * method name : getMaxDay
	 * method Desc : 해당 계약의 최종 일자 조회
	 *
	 * @param contract
	 * @return
	 */
	String getMaxDay(Contract contract);

	/**
	 * method name : getSavingTarget
	 * method Desc : 목표 금액 조회
\	 *
	 * @param operatorContractId
	 * @param basicDay
	 * @return
	 */
	Double getSavingTarget(int operatorContractId, String basicDay);

	/**
	 * method name : getMaxBill
	 * method Desc : 최고 금액 조회
	 *
	 * @param contract
	 * @param yyyymmdd 
	 * @return
	 */
	Double getMaxBill(Contract contract, String yyyymmdd);

	/**
	 * method name : getLastMonthBill
	 * method Desc : 전월 금액 조회
	 *
	 * @param contract 계약정보
	 * @param lastBillDay 전달 과금일
	 * @param basicDay 과금일 주기(month to bill)
	 * @return
	 */
	Double getLastMonthBill(Contract contract, String lastBillDay, String basicDay);

	/**
	 * method name : getLastYearSameMonthBill
	 * method Desc : 작년 동월 금액 조회
	 *
	 * @param contract 계약정보
	 * @param lastBillDay 전달 과금일
	 * @param basicDay 과금일 주기(month to bill)
	 * @return
	 */
	Double getLastYearSameMonthBill(Contract contract, String lastBillDay, String basicDay);

	/**
	 * method name : getForecastBill
	 * method Desc : 이번달 예측 요금 조회
	 *
	 * @param contract 계약정보
	 * @param lastBillDay 전달 과금일
	 * @param basicDay 과금일 주기(month to bill)
	 * @return
	 */
	Double getForecastBill(Contract contract, String lastBillDay, String basicDay);

	/**
	 * method name : saveSavingTarget
	 * method Desc : 목표 금액 등록
	 *
	 * @param operatorContractId
	 * @param savingTarget
	 * @param basicDay 
	 */
	boolean saveSavingTarget(int operatorContractId, Double savingTarget, String basicDay);

	/**
	 * method name : getContractGrid
	 * method Desc : 해당 계약 그리드 데이터 조회
	 *
	 * @param operatorContractId
	 * @param basicDay 
	 * @return
	 */
	List<Map<String, Object>> getContractGrid(int operatorContractId, String basicDay);

	/**
	 * method name : saveNoticeTarget
	 * method Desc : 통보 설정 정보 저장
	 *
	 * @param operatorContractId
	 * @param maxDay
	 * @param notification
	 */
	boolean saveNoticeTarget(int operatorContractId, String basicDay, Notification notification);

	/**
	 * method name : getEnergySavingTarget
	 * method Desc : EnergySavingTarget 조회(조건 : EnergySavingTarget)
	 *
	 * @param energySavingTarget
	 * @return
	 */
	List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget);
	
	List<EnergySavingTarget> getEnergySavingTarget(EnergySavingTarget energySavingTarget, String toDay);
	
	List<Map<String, Object>> getEnergySavingResultsYearComboBox(int operatorContractId, String basicDay);
	
	List<Map<String, Object>> getEnergySavingResultsMonthComboBox(int operatorContractId, String selYear);

	/**
	 * method name : deleteByOperatorContractId
	 * method Desc : operatorContractID에 해당하는 정보를 삭제한다.
	 *
	 * @param entity
	 */
	public void deleteByOperatorContractId(int operatorContractId);
}
