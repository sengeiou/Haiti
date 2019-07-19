package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.ContractCapacity;

public interface ContractCapacityDao extends GenericDao<ContractCapacity, Integer>{
	
	/**
	 * method name : getContractCapacityList
	 * method Desc :  페이지 단위로 ContractCapacity 목록을 리턴한다.
	 * 
	 * @param page page number
	 * @param count data count
	 * 
	 * @return List of ContractCapacity @see com.aimir.model.system.ContractCapacity
	 */
	public List<ContractCapacity> getContractCapacityList(int page, int count);
	
	/**
	 * method name : getContractCapacityList
	 * method Desc : ContractCapacity 전체 목록을 리턴한다.
	 * 
	 * @return List of ContractCapacity @see com.aimir.model.system.ContractCapacity
	 */
	public List<ContractCapacity> getContractCapacityList();

	/**
	 * method name : contractEnergyCombo
	 * method Desc : 지역별 ContractCapacity 정보를 맵핑하여 리턴한다.
	 * 
	 * @return List Of Object {
	 *  		CONTRACTCAPACITY_ID as id,
	 *  		loc.name as name }
	 */
	public List<Object> contractEnergyCombo();
	
	/**
	 * method name : contractEnergyPeakDemandGauge
	 * method Desc : Gauge Chart Data 표현을 위한 Peak Demand 정보
	 * 
	 * @param condition
	 * <ul>
	 * <li> contractCapacityId : ContractCapacity.id
	 * </ul>
	 * 
	 * @return List Of Object { ID,
	 * 							CAPACITY,
	 * 							THRESHOLD1,
	 * 							THRESHOLD2,
	 * 							THRESHOLD3}
	 */
	public List<Object> contractEnergyPeakDemandGauge(Map<String, Object> condition);
	
	/**
	 * method name : contractEnergyPeakDemand
	 * method Desc : Lp Data에 대한 시간과 각 주기별 Lp Data 에 대한 값을 취득
	 * 
	 * @param condition
	 * <ul>
	 * <li> contractCapacityId : ContractCapacity.id
	 * <li> startDate : yyyymmddhh
	 * <li> endDate : yyyymmddhh
	 * </ul>
	 * 
	 * @return List Of Object { lp.VALUE_00, ~ lp.VALUE_59, lp.YYYYMMDDHH}
	 * 
	 */
	public List<Object> contractEnergyPeakDemand(Map<String, Object> condition);
	
	/**
	 * method name : getThreshold
	 * method Desc : 입력한 조건으로  CONTRACTCAPACITY를 업데이트하고 결과를 가져온다.
	 * 
	 * @param condition
	 * <ul>
	 * <li> threshold1 : threshold1
	 * <li> threshold2 : threshold2
	 * <li> threshold3 : threshold3
	 * <li> contractCapacityId : ContractCapacity.id
	 * </ul>
	 * 
	 * @return
	 */
	public List<Object> getThreshold(Map<String, Object> condition);
	
	/**
	 * method name : contractEnergyExistCheck
	 * method Desc : 서비스타입과 로케이션 아이디로 해당 계약정보가 존재하는지 확인하여 개수를 리턴한다.
	 * 
	 * @param serviceTypeId  TARIFFTYPE.serviceType.id
	 * @param locationId Location.id
	 * @return
	 */
	public List<Object> contractEnergyExistCheck(Integer serviceTypeId,Integer locationId);

	
}
