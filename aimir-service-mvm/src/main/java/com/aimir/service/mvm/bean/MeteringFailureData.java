package com.aimir.service.mvm.bean;

import java.util.List;

/**
 * 검침실패율을 지역별로 조회하기 위한 BEAN
 * @author 이호정
 *
 */
public class MeteringFailureData {
	private String state="unchecked";
	private String isBranch;
	private String locationName;	// 지역명
	private String locationId;		// 지역ID
    private String totalCount;		// 전체 METER 건수
    private String successCount;	// 성공 METER 건수
    private String failureCount;    // 실패 METER 건수
    private String failureCountByCause1;// 실패유형1 미터 건수
    private String failureCountByCause2;// 실패유형2 미터 건수
    private String failureCountByEtc;
    private String failureCause;
    private String success;		// 실패율
    private String successRate;     // 성공율
    private String failureRate;     // 실패율
	private String label;			// 실패율%(실패건수/전체건수)
	private String parent;			// 부모 지역ID
	private List<MeteringFailureData> children;	// 자식 검침실패데이터
	private Boolean leaf;
	
	private String successFailYn;
	//조회조건
	private String supplierId;
	private String startDate;
	private String endDate;
	private String meterType;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getIsBranch() {
		return isBranch;
	}
	public void setIsBranch(String isBranch) {
		this.isBranch = isBranch;
	}
	public String getSuccessFailYn() {
		return successFailYn;
	}
	public void setSuccessFailYn(String successFailYn) {
		this.successFailYn = successFailYn;
	}
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
	//jhkim
	public String getFailureCountByCause1() {
		return failureCountByCause1;
	}
	public void setFailureCountByCause1(String failureCountByCause1) {
		this.failureCountByCause1 = failureCountByCause1;
	}
	public String getFailureCountByCause2() {
		return failureCountByCause2;
	}
	public void setFailureCountByCause2(String failureCountByCause2) {
		this.failureCountByCause2 = failureCountByCause2;
	}
	public String getFailureCountByEtc() {
		return failureCountByEtc;
	}
	public void setFailureCountByEtc(String failureCountByEtc) {
		this.failureCountByEtc = failureCountByEtc;
	}
	
	public String getFailureCause() {
		return failureCause;
	}
	public void setFailureCause(String failureCause) {
		this.failureCause = failureCause;
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
	public List<MeteringFailureData> getChildren() {
		return children;
	}
	public void setChildren(List<MeteringFailureData> children) {
		this.children = children;
	}
	
	public Boolean getLeaf() {
		return leaf;
	}
	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	/**
     * @return the failureCount
     */
    public String getFailureCount() {
        return failureCount;
    }

    /**
     * @param failureCount the failureCount to set
     */
    public void setFailureCount(String failureCount) {
        this.failureCount = failureCount;
    }

    /**
     * @return the successRate
     */
    public String getSuccessRate() {
        return successRate;
    }

    /**
     * @param successRate the successRate to set
     */
    public void setSuccessRate(String successRate) {
        this.successRate = successRate;
    }

    /**
     * @return the failureRate
     */
    public String getFailureRate() {
        return failureRate;
    }

    /**
     * @param failureRate the failureRate to set
     */
    public void setFailureRate(String failureRate) {
        this.failureRate = failureRate;
    }
}