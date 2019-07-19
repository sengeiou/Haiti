package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> IHD - In Home Display (홈 안의 디스플레이 장치) </p>
 * 
 * <pre>가정내의 에너지 사용량 정보 및 기타 에너지와관련된 정보를 디스플레이 하고 </pre>
 * <pre>Demand Response, Home Energy Management를 핤 수 있게 하는 장치 </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 */

@Entity
@DiscriminatorValue("IHD")
public class IHD extends Modem {

	private static final long serialVersionUID = 1106442427071674141L;
    
    @ColumnInfo(name="", descr="AES-128 Key Data - 250 (securityKey -> linkKey)")
    @Column(name="LINK_KEY", length=32)
    private String linkKey;

    @ColumnInfo(name="", descr="AES-128 Key Data")
    @Column(name="NETWORK_KEY", length=32)
    private String networkKey;
    
    @ColumnInfo(name="", descr="")
    @Column(name="NEED_JOIN_SET")
    private Boolean needJoinSet;//
    
    @ColumnInfo(name="", descr="Flag For a QC Test Program")
    @Column(name="TEST_FLAG", length=1)
    private Boolean testFlag;
    
    @ColumnInfo(name="", descr="Channel Id")
    @Column(name="CHANNEL_ID", length=10)
    private Integer channelId;

    @ColumnInfo(name="", descr="Pan ID")
    @Column(name="PAN_ID", length=10)
    private Integer panId;
 
    @ColumnInfo(name="", descr="8 Bytes")
    @Column(name="EXTPAN_ID", length=16)
    private String extPanId;    

    @ColumnInfo(name="", descr="Periodically Reset Time By a Day.")
    @Column(name="FIXED_RESET", length=5)
    private String fixedReset;    

    @ColumnInfo(name="", descr="0:Auto Scan And Join, 1:Channel, PanID Manually")
    @Column(name="MANUAL_ENABLE")
    private Boolean manualEnable;

    @ColumnInfo(name="", descr="0 : AES-128 Key Disable, 1:AES-128 Key Enable")
    @Column(name="SECURITY_ENABLE")
    private Boolean securityEnable;  
    
    @ColumnInfo(name="billDate", descr="과금일")
    @Column(name="BILL_DATE")
    private Integer billDate;

	@ColumnInfo(name="peakDemandThreshold", descr="과부하시 경보 이벤트 임계치(0%~100%)")
    @Column(name="PEAK_DEMAND_THRESHOLD")
    private Integer peakDemandThreshold;
	
	@ColumnInfo(name="gasUsageThreshold", descr="과부하시 경보 이벤트 임계치(0%~100%)")
    @Column(name="GAS_THRESHOLD")
    private Integer gasThreshold;

	@ColumnInfo(name="waterUsageThreshold", descr="과부하시 경보 이벤트 임계치(0%~100%)")
    @Column(name="WATER_THRESHOLD")
    private Integer waterThreshold;

	public String getLinkKey() {
		return linkKey;
	}

	public void setLinkKey(String linkKey) {
		this.linkKey = linkKey;
	}

	public String getNetworkKey() {
		return networkKey;
	}

	public void setNetworkKey(String networkKey) {
		this.networkKey = networkKey;
	}

	public Boolean getNeedJoinSet() {
		return needJoinSet;
	}

	public void setNeedJoinSet(Boolean needJoinSet) {
		this.needJoinSet = needJoinSet;
	}

	public Boolean getTestFlag() {
		return testFlag;
	}

	public void setTestFlag(Boolean testFlag) {
		this.testFlag = testFlag;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Integer getPanId() {
		return panId;
	}

	public void setPanId(Integer panId) {
		this.panId = panId;
	}

	public String getExtPanId() {
		return extPanId;
	}

	public void setExtPanId(String extPanId) {
		this.extPanId = extPanId;
	}

	public String getFixedReset() {
		return fixedReset;
	}

	public void setFixedReset(String fixedReset) {
		this.fixedReset = fixedReset;
	}

	public Boolean getManualEnable() {
		return manualEnable;
	}

	public void setManualEnable(Boolean manualEnable) {
		this.manualEnable = manualEnable;
	}

	public Boolean getSecurityEnable() {
		return securityEnable;
	}

	public void setSecurityEnable(Boolean securityEnable) {
		this.securityEnable = securityEnable;
	}	
    
    public Integer getBillDate() {
		return billDate;
	}

	public void setBillDate(Integer billDate) {
		this.billDate = billDate;
	}

	public Integer getPeakDemandThreshold() {
		return peakDemandThreshold;
	}

	public void setPeakDemandThreshold(Integer peakDemandThreshold) {
		this.peakDemandThreshold = peakDemandThreshold;
	}
	
	
	public Integer getGasThreshold() {
		return gasThreshold;
	}

	public void setGasThreshold(Integer gasThreshold) {
		this.gasThreshold = gasThreshold;
	}

	public Integer getWaterThreshold() {
		return waterThreshold;
	}

	public void setWaterThreshold(Integer waterThreshold) {
		this.waterThreshold = waterThreshold;
	}

}