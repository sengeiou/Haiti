package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DisplayType;
import com.aimir.constants.CommonConstants.MeteringDataClass;
import com.aimir.model.BaseObject;
import com.aimir.model.system.MeterConfig;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 검침데이터 채널 정보
 * <pre>
 * dataType : 검침테이블 클래스명 
 * channelIndex : 채널 저장 및 표시하는 순서 
 * channel : DisplayChannel에서 참조하는 채널 아이디 
 * displayType : "display type (저장, 표시 둘다 혹은 저장만 혹은 계산한데이터를 표시하는 3가지 타입으로 분류한다. 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name="CHANNEL_CONFIG", uniqueConstraints = @UniqueConstraint(columnNames = {"data_type","channel_id","meterconfig_id"}))
public class ChannelConfig extends BaseObject implements JSONString {

    private static final long serialVersionUID = 6748339448528869541L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CHANNEL_CONFIG_SEQ")
    @SequenceGenerator(name="CHANNEL_CONFIG_SEQ", sequenceName="CHANNEL_CONFIG_SEQ", allocationSize=1) 
    private Integer id;    
    
    @Column(name="data_type", nullable=false)
    @ColumnInfo(descr="검침테이블 클래스 명")
    @Enumerated(EnumType.STRING)
    private MeteringDataClass dataType;
    
    @Column(name="channel_index", nullable=false)
    @ColumnInfo(descr="채널 저장 및 표시하는 순서")
    private Integer channelIndex;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="channel_id",nullable=false)
    @ReferencedBy(name="name")
    private DisplayChannel channel;
    
    @Column(name="channel_id", nullable=true, updatable=false, insertable=false)
    private Integer channelId; 
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="meterconfig_id", nullable=false)
    @ReferencedBy(name="name")
    private MeterConfig meterConfig;
    
    @Column(name="meterconfig_id", nullable=true, updatable=false, insertable=false)
    private Integer meterConfigId;
    
    @Column(name="display_type", nullable=false)
    @ColumnInfo(descr="display type (저장, 표시 둘다 혹은 저장만 혹은 계산한데이터를 표시하는 3가지 타입으로 분류한다.")
    @Enumerated(EnumType.STRING)
    private DisplayType displayType;    

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public MeteringDataClass getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = MeteringDataClass.valueOf(dataType);
    }

    public Integer getChannelIndex() {
        return channelIndex;
    }

    public void setChannelIndex(Integer channelIndex) {
        this.channelIndex = channelIndex;
    }

    @XmlTransient
    public DisplayChannel getChannel() {
        return channel;
    }

    public void setChannel(DisplayChannel channel) {
        this.channel = channel;
    }

    @XmlTransient
    public MeterConfig getMeterConfig() {
        return meterConfig;
    }

    public void setMeterConfig(MeterConfig meterConfig) {
        this.meterConfig = meterConfig;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public void setDisplayType(String displayType) {
        this.displayType = DisplayType.valueOf(displayType);
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public Integer getMeterConfigId() {
        return meterConfigId;
    }

    public void setMeterConfigId(Integer meterConfigId) {
        this.meterConfigId = meterConfigId;
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
    public String toString() {
        return "ChannelConfig" + toJSONString();
    }
    
    public String toJSONString() {
        
        String retValue = "";
        
        retValue = "{"
            + "id:'" + this.id 
            + "',dataType:'" + this.dataType.name()
            + "',channelIndex:'" + this.channelIndex 
            + "',channelId:'" + this.channelId
            + "',meterConfigId:'" + this.meterConfigId
            + "',displayType:'" + this.displayType.name()
            + "'}";
        
        return retValue;
    }
}
