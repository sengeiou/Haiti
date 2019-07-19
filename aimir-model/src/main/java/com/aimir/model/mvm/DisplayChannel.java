package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.ChannelCalcMethod;
import com.aimir.constants.CommonConstants.DataSVC;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 검침데이터 채널 정보를 담고있는 클래스 
 * name : 채널명 
 * localName :  로컬 디스플레이 명, UI 뷰상에 실제 표시하는 이름 
 *  
 * serviceType : 공급서비스 타입 (전기,가스,수도 등) 
 *  
 * unit : 디스 플레이할 때 단위 예) kWh, kW 
 *  
 * reverseEnergy : 순방향, 역방향 구분 (true='역방향') 역방향인 경우 실제 사용한 에너지에서 빼야 함 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="DISPLAY_CHANNEL")
public class DisplayChannel extends BaseObject implements JSONString {

	private static final long serialVersionUID = 4171191010368646679L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DISPLAY_CHANNEL_SEQ")
	@SequenceGenerator(name="DISPLAY_CHANNEL_SEQ", sequenceName="DISPLAY_CHANNEL_SEQ", allocationSize=1) 
	private Integer id;    
    
	@Column(name="name", nullable=false, unique=true)
	@ColumnInfo(name="채널명")
	private String name;
	
	@Column(name="local_name")
	@ColumnInfo(name="로컬 디스플레이 명, UI 뷰상에 실제 표시하는 이름")
	private String localName;
	
	@Column(name="service_type", nullable=false)
	@ColumnInfo(name="공급 서비스 타입")
	@Enumerated(EnumType.STRING)
	private DataSVC serviceType;
	
	@Column(name="unit")
	@ColumnInfo(name="디스 플레이할 때 단위 예) kWh, kW")
	private String unit;	
	
	@Column(name="reverse_energy")
	@ColumnInfo(descr="순방향, 역방향 구분 (true='역방향') 역방향인 경우 실제 사용한 에너지에서 빼야 함 ")
	private Boolean reverseEnergy;
	
	@Column(name="ch_method", nullable=true)
    @ColumnInfo(name="채널계산방법")
    @Enumerated(EnumType.STRING)
	private ChannelCalcMethod chMethod;
	
	@Column(name="channel_value", nullable=true)
    @ColumnInfo(name="DCU 채널 매핑 값 전기 : 1XXX, 가스 : 2XXX, 수도:3XXX, 유량:4XXX")
    private String channelValue;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public DataSVC getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = DataSVC.valueOf(serviceType);
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Boolean getReverseEnergy() {
		return reverseEnergy;
	}

	public void setReverseEnergy(Boolean reverseEnergy) {
		this.reverseEnergy = reverseEnergy;
	}

	public ChannelCalcMethod getChMethod() {
        return chMethod;
    }

    public void setChMethod(String chMethod) {
        this.chMethod = ChannelCalcMethod.valueOf(chMethod);
    }
    
    public String getChannelValue() {
        return channelValue;
    }

    public void setChannelValue(String channelValue) {
        this.channelValue = channelValue;
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
	    return "DisplayChannel "+toJSONString();
	}

	public String toJSONString() {
        
        String retValue = "";
        
        retValue = "{"
            + "id:'" + this.id 
            + "',name:'" + ((name==null)?"":this.name )
            + "',localName:'" + ((localName==null)?"":this.localName )
            + "',serviceType:'" + ((serviceType==null)?"":this.serviceType.name())
            + "',unit:'" + ((unit==null)?"":this.unit)
            + "',reverseEnergy:'" + ((reverseEnergy==null)?"":this.reverseEnergy)
            + "',chMethod:'" + ((chMethod==null)?"":this.chMethod.name())
            + "',channelValue:'"+((channelValue==null)?"":this.channelValue)
            + "'}";
        
        return retValue;
    }
}
