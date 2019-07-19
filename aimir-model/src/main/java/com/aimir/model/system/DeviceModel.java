package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
import com.aimir.model.device.OperationList;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * AIMIR System 에서 사용하는 Device들의 모델 정보를 나타낸다.
 * 장비 제조사에 속해있는 모델정보 
 * Device의 벤더 정보, Device의 고유 코드 , Device 타입, 모델명, 이미지 경로, 장비와 연관된 고유 설정 정보등을 가진다.
 * 본 정보는 AIMIR System에서 Device들의 일반적인 모델 정보를 보여주기에 앞서 Device별로 고유한 오퍼레이션이나
 * 데이터를 해석할 때 활용한다.
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="DEVICEMODEL")
// @Cache(type=CacheType.SOFT)
public class DeviceModel extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 5105750097434192498L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DEVICEMODEL_SEQ")
	@SequenceGenerator(name="DEVICEMODEL_SEQ", sequenceName="DEVICEMODEL_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
	@JoinColumn(name="devicevendor_id")
	@ColumnInfo(name="제조사")
	@ReferencedBy(name="name")
	private DeviceVendor deviceVendor;
	
	@Column(name="devicevendor_id", nullable=true, updatable=false, insertable=false)
	private Integer deviceVendorId;

	@Column(nullable=false)
	@ColumnInfo(name="모델 코드")
	private Integer code;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="type_id")
	@ColumnInfo(name="장비타입", descr="")
	@ReferencedBy(name="code")
	private Code deviceType;			//굳이 entity로 만들 필요는 없어 보여 code로 대체 
	
	@Column(name="type_id", nullable=true, updatable=false, insertable=false)
	private Integer deviceTypeCodeId;
	
	@OneToOne(mappedBy="deviceModel", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    // @ReferencedBy(name="name")
	//설정정보를 찾기 위해선 모델타입을 알아야 한다.(모델타입에 따라 검색하는 테이블이 달라진다)-->상속
	private DeviceConfig deviceConfig;
	
	@Column(nullable=false, length=100, unique=true)
	@ColumnInfo(name="모델명")
	private String name;
	
	@Column(length=200)
	@ColumnInfo(name="이미지")
	private String image;	
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="MODEL_ID") // 각각의 모델별로 실행가능한 오퍼레이션 목록
	private Set<OperationList> operationList = new HashSet<OperationList>(0);

	@ColumnInfo(name="비고")
	@Column(name="description", length=300)
	private String description;
	
	@ColumnInfo(name="미터 시리얼 정규식")
	@Column(columnDefinition="varchar(200) default '[0-9]*'")
	private String mdsIdPattern="[0-9]*";

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlTransient
	public DeviceVendor getDeviceVendor() {
		return deviceVendor;
	}
	public void setDeviceVendor(DeviceVendor deviceVendor) {
		this.deviceVendor = deviceVendor;
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	@XmlTransient
	public Code getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(Code deviceType) {
		this.deviceType = deviceType;
	}
	
	@XmlTransient
	public DeviceConfig getDeviceConfig() {
		return deviceConfig;
	}
	public void setDeviceConfig(DeviceConfig deviceConfig) {
		this.deviceConfig = deviceConfig;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	
	@XmlTransient
	public Set<OperationList> getOperationList() {
		return operationList;
	}
	public void setOperationList(Set<OperationList> operationList) {
		this.operationList = operationList;
	}

	/**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDeviceVendorId() {
        return deviceVendorId;
    }
    public void setDeviceVendorId(Integer deviceVendorId) {
        this.deviceVendorId = deviceVendorId;
    }
    public Integer getDeviceTypeCodeId() {
        return deviceTypeCodeId;
    }
    public void setDeviceTypeCodeId(Integer deviceTypeCodeId) {
        this.deviceTypeCodeId = deviceTypeCodeId;
    }
    
    public String getMdsIdPattern() {
        return mdsIdPattern;
    }
    public void setMdsIdPattern(String mdsIdPattern) {
        this.mdsIdPattern = mdsIdPattern;
    }
    @Override
	public String toString()
	{
	    return "DeviceModel "+toJSONString();
	}
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',deviceVendor:'" + ((this.deviceVendor == null)? "null":deviceVendor.getId()) 
	        + "',deviceType:'" + ((this.deviceType == null)? "null":deviceType.getId()) 
	       // + "',deviceConfig:'" + ((this.deviceConfig == null)? "null":deviceConfig) 
	        + "',code:'" + this.code 
	        + "',name:'" + this.name 
	        + "',image:'" + this.image 
            + "',description:'" + this.description 
            + "',mdsIdPattern:'" + this.mdsIdPattern
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
