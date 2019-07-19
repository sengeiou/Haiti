package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DisplayType;
import com.aimir.constants.CommonConstants.MeteringDataClass;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2012</p>
 * 
 * 각 미터의 개별 검침데이터 채널 정보
 * 같은 종류의 미터라도 채널 구성이 다른 경우 필요
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
@Table(name="EACH_METER_CHANNEL_CONFIG")
public class EachMeterChannelConfig extends BaseObject implements JSONString {

	private static final long serialVersionUID = 6748339448528869541L;
	
	@EmbeddedId public EachMeterChannelConfigPk id;	    
    
	@Column(name="data_type", nullable=false)
	@ColumnInfo(descr="검침테이블 클래스 명")
	@Enumerated(EnumType.STRING)
	private MeteringDataClass dataType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="channel_id",nullable=false)
	@ReferencedBy(name="name")
	private DisplayChannel channel;
	
	@Column(name="channel_id", nullable=true, updatable=false, insertable=false)
	private Integer channelId; 
	
	@Column(name="display_type", nullable=false)
	@ColumnInfo(descr="display type (저장, 표시 둘다 혹은 저장만 혹은 계산한데이터를 표시하는 3가지 타입으로 분류한다.")
	@Enumerated(EnumType.STRING)
	private DisplayType displayType;	

	@Column(name="multiplier", nullable=true)
	@ColumnInfo(descr="multiplier")
	private Double multiplier;	
	
	@Column(name="scalar", nullable=true)
	@ColumnInfo(descr="scalar")
	private Integer scalar;
	
	@Column(name="divisor", nullable=true)
	@ColumnInfo(descr="divisor")
	private Integer divisor;

	@Column(name="last_value_format_code", nullable=true)
	@ColumnInfo(descr="last value format code(data format code)")
	private Integer lastValueFormatCode;
	
	@Column(name="lp_value_format_code", nullable=true)
	@ColumnInfo(descr="lp value format code(data format code)")
	private Integer lpValueFormatCode;	
	
	@Column(name="last_value_format_size", nullable=true)
	@ColumnInfo(descr="last value data size")
	private Integer lastValueFormatSize;
	
	@Column(name="lp_value_format_size", nullable=true)
	@ColumnInfo(descr="lp value data size")
	private Integer lpValueFormatSize;	
	
	public EachMeterChannelConfigPk getId() {
		return id;
	}

	public void setId(EachMeterChannelConfigPk id) {
		this.id = id;
	}

	public MeteringDataClass getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = MeteringDataClass.valueOf(dataType);
	}

	@XmlTransient
	public DisplayChannel getChannel() {
		return channel;
	}

	public void setChannel(DisplayChannel channel) {
		this.channel = channel;
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

    public Double getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Double multiplier) {
		this.multiplier = multiplier;
	}

	public Integer getScalar() {
		return scalar;
	}

	public void setScalar(Integer scalar) {
		this.scalar = scalar;
	}

	public Integer getDivisor() {
		return divisor;
	}

	public void setDivisor(Integer divisor) {
		this.divisor = divisor;
	}

	public Integer getLastValueFormatCode() {
		return lastValueFormatCode;
	}

	public void setLastValueFormatCode(Integer lastValueFormatCode) {
		this.lastValueFormatCode = lastValueFormatCode;
	}

	public Integer getLpValueFormatCode() {
		return lpValueFormatCode;
	}

	public void setLpValueFormatCode(Integer lpValueFormatCode) {
		this.lpValueFormatCode = lpValueFormatCode;
	}

	public Integer getLastValueFormatSize() {
		return lastValueFormatSize;
	}

	public void setLastValueFormatSize(Integer lastValueFormatSize) {
		this.lastValueFormatSize = lastValueFormatSize;
	}

	public Integer getLpValueFormatSize() {
		return lpValueFormatSize;
	}

	public void setLpValueFormatSize(Integer lpValueFormatSize) {
		this.lpValueFormatSize = lpValueFormatSize;
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
		return "EachMeterChannelConfig" + toJSONString();
	}
	
    public String toJSONString() {
        
        String retValue = "";
        
        retValue = "{"
            + "id:'" + this.id 
            + "',dataType:'" + this.dataType.name()
            + "',channelId:'" + this.channelId
            + "',displayType:'" + this.displayType.name()
            + "'}";
        
        return retValue;
    }
}
