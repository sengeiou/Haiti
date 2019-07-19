package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Zigbee Unit for Alarm Interface (알람발생, 경보 등의 발생을 위한 모뎀 미터링은 수행하지 않음)</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@DiscriminatorValue("ZMU")
public class ZMU extends Modem {

    private static final long serialVersionUID = 5010587279925267175L;
    
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false),descr="AES-128 Key Data - 250 (securityKey -> linkKey)")
    @Column(name="LINK_KEY", length=32)
    private String linkKey;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false),descr="AES-128 Key Data")
    @Column(name="NETWORK_KEY", length=32)
    private String networkKey;
    
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=true, devicecontrol=true), descr="")
    @Column(name="NEED_JOIN_SET")
    private Boolean needJoinSet;//
    
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false),descr="Channel Id")
    @Column(name="CHANNEL_ID", length=10)
    private Integer channelId;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false),descr="Pan ID")
    @Column(name="PAN_ID", length=10)
    private Integer panId;
 
    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false),descr="8 Bytes")
    @Column(name="EXTPAN_ID", length=16)
    private String extPanId;    

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=true, devicecontrol=true),descr="0:Auto Scan And Join, 1:Channel, PanID Manually")
    @Column(name="MANUAL_ENABLE")
    private Boolean manualEnable;

    @ColumnInfo(name="",view=@Scope(create=false, read=true, update=true, devicecontrol=true), descr="0 : AES-128 Key Disable, 1:AES-128 Key Enable")
    @Column(name="SECURITY_ENABLE")
    private Boolean securityEnable;

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
    
}