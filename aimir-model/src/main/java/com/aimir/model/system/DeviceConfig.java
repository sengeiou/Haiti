package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * 장비 모델에 한정된 특정 구성 정보 
 * 장비 모델별 설정명 ( “모델명 + config”) 로 일반적으로 식별 가능하게 설정
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
// @Cache(type=CacheType.SOFT)
public abstract class DeviceConfig extends BaseObject implements IAuditable {

	private static final long serialVersionUID = -3681214830764043814L;
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DEVICECONFIG_SEQ")
	@SequenceGenerator(name="DEVICECONFIG_SEQ", sequenceName="DEVICECONFIG_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK & FK")
	private Integer id;	

	@Column(unique=true, length=100, nullable=false)
	@ColumnInfo(name="설정명")
	private String name;

	@OneToOne(fetch=FetchType.LAZY)
	@ColumnInfo(name="장비 모델")
	@JoinColumn(name="devicemodel_fk")
	@ReferencedBy(name="name")
	private DeviceModel deviceModel;
	
	@Column(name="devicemodel_fk", nullable=true, updatable=false, insertable=false)
	private Integer deviceModelId;
	
	@Column(name="PARSER_NAME", nullable=true)
    @ColumnInfo(name="parserName", descr="데이터를 해석할 parser이름")
    private String parserName;

    @Column(name="SAVER_NAME", nullable=true)
    @ColumnInfo(name="saverName", descr="검침데이타 저장 클래스")
    private String saverName;
    
    @Column(name="ONDEMAND_PARSER_NAME", nullable=true)
    @ColumnInfo(name="ondemandParserName", descr="데이터를 해석할 parser이름")
    private String ondemandParserName;

    @Column(name="ONDEMAND_SAVER_NAME", nullable=true)
    @ColumnInfo(name="ondemandSaverName", descr="검침데이타 저장 클래스")
    private String ondemandSaverName;
    

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
	
	@XmlTransient
	public DeviceModel getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}
    public Integer getDeviceModelId() {
        return deviceModelId;
    }
    public void setDeviceModelId(Integer deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public String getParserName() {
        return parserName;
    }
    public void setParserName(String parserName) {
        this.parserName = parserName;
    }
    
    public String getSaverName() {
        return saverName;
    }
    public void setSaverName(String saverName) {
        this.saverName = saverName;
    }
    public String getOndemandParserName() {
        return ondemandParserName;
    }
    public void setOndemandParserName(String ondemandParserName) {
        this.ondemandParserName = ondemandParserName;
    }
    public String getOndemandSaverName() {
        return ondemandSaverName;
    }
    public void setOndemandSaverName(String ondemandSaverName) {
        this.ondemandSaverName = ondemandSaverName;
    }
    
    
}
