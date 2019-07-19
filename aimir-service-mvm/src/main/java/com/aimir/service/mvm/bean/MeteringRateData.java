package com.aimir.service.mvm.bean;

import java.util.List;

public class MeteringRateData{
	private String locationName;	// 지역명
	private String locationId;		// 지역ID
    private String totalCount;		// 전체 METER 건수
    private String successCount;	// 실패 METER 건수
    private String success;		    // 검침율
	private String label;			// 검침율%(성공건수/전체건수)
	private String parent;			// 부모 지역ID
	private List<MeteringRateData> children;	// 자식 검침성공데이터
	
	//조회조건
	private String startDate;
	private String endDate;
	private String meterType;
	private String supplierId;
	
	public String getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getMeterType() {
		return meterType;
	}
	public void setMeterType(String meterType) {
		this.meterType = meterType;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getSuccessCount() {
		return successCount;
	}
	public void setSuccessCount(String successCount) {
		this.successCount = successCount;
	}
	public String getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public List<MeteringRateData> getChildren() {
		return children;
	}
	public void setChildren(List<MeteringRateData> children) {
		this.children = children;
	}	
}
	