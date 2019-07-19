package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.mvm.ChannelConfig;

/**
 * 미터장비 설정정보 
 *미터의 설정을 의미 
 전기 이외에 가스나 수도의 경우 deviceModel - deviceConfig의 관계가 1:1이 될 수 있음 meter SW가 없는 경우 

 */
@Entity
public class MeterConfig extends DeviceConfig implements JSONString {

	private static final long serialVersionUID = -6477122472800161311L;
	
	@Column(length=20)
    @ColumnInfo(name="미터등급")
    private String meterClass;
    
    @Column(length=20)
    @ColumnInfo(name="미터프로토콜")
    private String meterProtocol;
    
    @Column(length=20)
    @ColumnInfo(name="")
    private String phase;
    
    @Column(length=20)
    @ColumnInfo(name="파워공급스펙")
    private String powerSupplySpec;
    
    @Column(name="PULSE_CONST", length=20)
    @ColumnInfo(name="펄스상수")
    private Double pulseConst;
    
    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="meterconfig_id")
    @ColumnInfo(name="미터채널")    
    private Set<ChannelConfig> channels = new HashSet<ChannelConfig>(0);

    @Column(name="LP_INTERVAL")
    @ColumnInfo(name="LP주기")
    private Integer lpInterval;
    
    @Column(name="ADAPTER_CLASS_NAME")
    @ColumnInfo(name="사이트에서 요구하는 특화된 기능을 수행하기 위한 어댑터 클래스명")
    private String adapterClassName;

    public String getMeterClass() {
        return meterClass;
    }
    public void setMeterClass(String meterClass) {
        this.meterClass = meterClass;
    }
    
    public String getMeterProtocol() {
        return meterProtocol;
    }
    public void setMeterProtocol(String meterProtocol) {
        this.meterProtocol = meterProtocol;
    }
    
    public String getPhase() {
        return phase;
    }
    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getPowerSupplySpec() {
        return powerSupplySpec;
    }
    public void setPowerSupplySpec(String powerSupplySpec) {
        this.powerSupplySpec = powerSupplySpec;
    }
    

    public Double getPulseConst() {
        return pulseConst;
    }
    public void setPulseConst(Double pulseConst) {
        this.pulseConst = pulseConst;
    }
    
    @XmlTransient
    public Set<ChannelConfig> getChannels() {
        return channels;
    }
    public void setChannel(Set<ChannelConfig> channels) {
        this.channels = channels;
    }
    
    /**
     * @return the lpInterval
     */
    public Integer getLpInterval() {
        return lpInterval;
    }
    /**
     * @param lpInterval the lpInterval to set
     */
    public void setLpInterval(Integer lpInterval) {
        this.lpInterval = lpInterval;
    }
    public String getAdapterClassName() {
        return adapterClassName;
    }
    public void setAdapterClassName(String adapterClassName) {
        this.adapterClassName = adapterClassName;
    }
    @Override
    public String toString()
    {
        return "MeterConfig "+toJSONString();
    }
    public String toJSONString() {
        
        String retValue = "";
        
        retValue = "{"
            + "id:'" + this.getId() 
            + "',deviceModel:'" + ((getDeviceModel() == null)? "null":getDeviceModel().getId()) 
            + "',name:'" + this.getName() 
            + "',meterClass:'" + this.meterClass
            + "',meterProtocol:'" + this.meterProtocol 
            + "',phase:'" + this.phase 
            + "',powerSupplySpec:'" + this.powerSupplySpec 
            + "',pulseConst:'" + this.pulseConst 
            + "',lpInterval:'" + this.lpInterval
            + "',parserName:'" + this.getParserName()
            + "',saverName:'" + this.getSaverName()
            + "',ondemandParserName:'" + this.getOndemandParserName()
            + "',ondemandSaverName:'" + this.getOndemandSaverName()
            + "',adapterClassName:'" + this.getAdapterClassName()
            + "',channel:'" + this.channels.toString()
            + "',channelCnt:'" + this.channels.size()
            //+ "',lpInterval:'" + this.lpInterval 
            + "'}";
        return retValue;
    }
    @Override
    public boolean equals(Object o) {
        // TODO Auto-generated method stub
        return false;
    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public String getInstanceName() {
        return this.getName();
    }
}
