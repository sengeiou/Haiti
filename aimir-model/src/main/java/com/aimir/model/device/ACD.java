package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>ACD - Appliance Control Device (전기 장치나 기타 Device의 전원 공급등 에너지 공급을 차단하는 장치)</p>
 * 
 * 댁내나 외부에 이동 가능하며 워터펌프(수영장) 가전기기 등등 에 장착되어 사용<br>
 * 
 * @author YeonKyoung Park(goodjob)
 * 
 */
@Entity
@DiscriminatorValue("ACD")
public class ACD extends Modem {

	private static final long serialVersionUID = 2266248374642664605L;
    
    @ColumnInfo(name="", descr="AES-128 Key Data - 250 (securityKey -> linkKey)")
    @Column(name="LINK_KEY", length=32)
    private String linkKey;

    @ColumnInfo(name="", descr="AES-128 Key Data")
    @Column(name="NETWORK_KEY", length=32)
    private String networkKey;
    
    @ColumnInfo(name="", descr="")
    @Column(name="NEED_JOIN_SET")
    private Boolean needJoinSet;//
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Flag For a QC Test Program")
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

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Day (Mask 4 Bytes)")
    @Column(name="METERING_DAY", length=255)
    private String meteringDay;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Metering Schedule Hour (Mask 12 Bytes)")
    @Column(name="METERING_HOUR", length=255)
    private String meteringHour;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="Periodically Reset Time By a Day.")
    @Column(name="FIXED_RESET", length=5)
    private String fixedReset;    

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="0:Auto Scan And Join, 1:Channel, PanID Manually")
    @Column(name="MANUAL_ENABLE")
    private Boolean manualEnable;

    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="0 : AES-128 Key Disable, 1:AES-128 Key Enable")
    @Column(name="SECURITY_ENABLE")
    private Boolean securityEnable;    
    
    @ColumnInfo(name="", view=@Scope(read=true, update=true, devicecontrol=true), descr="모뎀이 올려주는 lp데이터가 오늘부터 몇일 전의 데이터인지 정의")
    @Column(name="LP_CHOICE", length=2)
    private Integer lpChoice;
    
    @ColumnInfo(name="demandResponse", descr="Demand Response 여부 플러그 켜짐/꺼짐 여부")
    @Column(name="DEMAND_RESPONSE")
    private Boolean demandResponse;

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

	public String getMeteringDay() {
		return meteringDay;
	}

	public void setMeteringDay(String meteringDay) {
		this.meteringDay = meteringDay;
	}

	public String getMeteringHour() {
		return meteringHour;
	}

	public void setMeteringHour(String meteringHour) {
		this.meteringHour = meteringHour;
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

	public Integer getLpChoice() {
		return lpChoice;
	}

	public void setLpChoice(Integer lpChoice) {
		this.lpChoice = lpChoice;
	}
	

	public Boolean getDemandResponse() {
		return demandResponse;
	}

	public void setDemandResponse(Boolean demandResponse) {
		this.demandResponse = demandResponse;
	}

}