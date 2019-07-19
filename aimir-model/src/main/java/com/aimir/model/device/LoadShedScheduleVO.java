package com.aimir.model.device;

import com.aimir.constants.CommonConstants.ScheduleType;

/**
 * LoadShedSchedule 탭에서 검색 결과를 전달하는 VO
 * @author yuky
 *
 */
public class LoadShedScheduleVO {

	private Integer num;				// 번호
	private String createDate;			// 그룹 생성일
	private String groupName;			// 그룹명	
	private Double supplyCapacity;		// 용량
	private Double supplyThreshold;
	//private ScheduleType scheduleType;	// 스케쥴 종류(0:즉시, 1: 일자,  2:요일별)
	
	private Integer groupId;		// 그룹 아이디
	
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public Double getSupplyCapacity() {
		return supplyCapacity;
	}
	public void setSupplyCapacity(Double supplyCapacity) {
		this.supplyCapacity = supplyCapacity;
	}
	public Double getSupplyThreshold() {
		return supplyThreshold;
	}
	public void setSupplyThreshold(Double supplyThreshold) {
		this.supplyThreshold = supplyThreshold;
	}
	/*public ScheduleType getScheduleType() {
		return scheduleType;
	}
	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}*/
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}
	
}
