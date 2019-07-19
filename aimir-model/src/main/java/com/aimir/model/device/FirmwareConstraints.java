package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Firmware 정보에서 Firmware 업그레이드가 가능한 여부에 대한 제약사항을 정의한 클래스</p>
 * 
 * @author kostrich
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="FIRMWARECONSTRAINTS",discriminatorType=DiscriminatorType.STRING)
@Table(name="FIRMWARECONSTRAINTS")
//public class Firmware extends BaseObject implements JSONString{
             
public class FirmwareConstraints extends BaseObject {
	private static final long serialVersionUID = 1529672452930586656L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FIRMWARECONSTRAINTS_SEQ")
    @SequenceGenerator(name="FIRMWARECONSTRAINTS_SEQ", sequenceName="FIRMWARECONSTRAINTS_SEQ", allocationSize=1)
	private Integer id;
	/*
	 *  INSTANCENAME              VARCHAR2(255 BYTE) NOT NULL,
	    HWVERSION                 VARCHAR2(10 BYTE),
	    FWVERSION                 VARCHAR2(10 BYTE),
	    BUILD                     VARCHAR2(10 BYTE),
	    FIRMWAREID                VARCHAR2(100 BYTE),
	    ID                        VARCHAR2(100 BYTE) NOT NULL,
	    CONTAINMENT               VARCHAR2(500 BYTE),
	 * */
	@ColumnInfo(name="FIRMWARECONSTRAINTS Id", view=@Scope(create=true, read=true, update=false), descr="펌웨어컨스트레인트 ID")
    @Column(name="FIRMWARECONSTRAINTS_ID", nullable=false, unique=true)
    private java.lang.String firmwareconstraintsId;

    @ColumnInfo(name="INSTANCENAME", view=@Scope(create=true, read=true, update=false), descr="INSTANCENAME")
    @Column(name="INSTANCENAME", nullable=false, unique=false)
    private java.lang.String  instanceName;

    @ColumnInfo(name="하드웨어 버전", view=@Scope(create=true, read=true, update=false), descr="하드웨어 버전]")
    @Column(name="HW_VERSION", nullable=true, unique=false)
    private java.lang.String hwVersion;
    
    @ColumnInfo(name="펌웨어 버전", view=@Scope(create=true, read=true, update=false), descr="펌웨어 버전")
    @Column(name="FW_VERSION", nullable=true, unique=false)
    private java.lang.String fwVersion; 
    
    @ColumnInfo(name="빌드/리비전 번호", view=@Scope(create=true, read=true, update=false), descr="펌웨어 빌드 또는 리비전 번호")
    @Column(name="BUILD", nullable=true, unique=false)
    private java.lang.String build;
    
    @ColumnInfo(name="Firmware Id", view=@Scope(create=true, read=true, update=false), descr="펌웨어 ID")
    @Column(name="FIRMWARE_ID", nullable=true, unique=true)
    private java.lang.String firmwareId;    
    
    @ColumnInfo(name="CONTAINMENT", view=@Scope(create=true, read=true, update=false), descr="CONTAINMENT")
    @Column(name="CONTAINMENT", nullable=true, unique=false)
    private java.lang.String containMent;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public java.lang.String getFirmwareconstraintsId() {
		return firmwareconstraintsId;
	}

	public void setFirmwareconstraintsId(java.lang.String firmwareconstraintsId) {
		this.firmwareconstraintsId = firmwareconstraintsId;
	}

	public java.lang.String getInstanceName() {
		return instanceName;
	}

	public void setInstanceName(java.lang.String instanceName) {
		this.instanceName = instanceName;
	}

	public java.lang.String getHwVersion() {
		return hwVersion;
	}

	public void setHwVersion(java.lang.String hwVersion) {
		this.hwVersion = hwVersion;
	}

	public java.lang.String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(java.lang.String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public java.lang.String getBuild() {
		return build;
	}

	public void setBuild(java.lang.String build) {
		this.build = build;
	}

	public java.lang.String getFirmwareId() {
		return firmwareId;
	}

	public void setFirmwareId(java.lang.String firmwareId) {
		this.firmwareId = firmwareId;
	}

	public java.lang.String getContainMent() {
		return containMent;
	}

	public void setContainMent(java.lang.String containMent) {
		this.containMent = containMent;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		FirmwareConstraints other = (FirmwareConstraints) obj;
		if (build == null) {
			if (other.build != null)
				return false;
		} else if (!build.equals(other.build))
			return false;
		if (firmwareId == null) {
			if (other.firmwareId != null)
				return false;
		} else if (!firmwareId.equals(other.firmwareId))
			return false;
		if (fwVersion == null) {
			if (other.fwVersion != null)
				return false;
		} else if (!fwVersion.equals(other.fwVersion))
			return false;
		if (hwVersion == null) {
			if (other.hwVersion != null)
				return false;
		} else if (!hwVersion.equals(other.hwVersion))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (firmwareconstraintsId == null) {
			if (other.firmwareconstraintsId != null)
				return false;
		} else if (!firmwareconstraintsId.equals(other.firmwareconstraintsId))
			return false;
		if (instanceName == null) {
			if (other.instanceName != null)
				return false;
		} else if (!instanceName.equals(other.instanceName))
			return false;
		if (containMent == null) {
			if (other.containMent != null)
				return false;
		} else if (!containMent.equals(other.containMent))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + ((build == null) ? 0 : build.hashCode());
		result = prime * result	+ ((firmwareId == null) ? 0 : firmwareId.hashCode());
		result = prime * result	+ ((fwVersion == null) ? 0 : fwVersion.hashCode());
		result = prime * result	+ ((hwVersion == null) ? 0 : hwVersion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((containMent == null) ? 0 : containMent.hashCode());
		result = prime * result + ((instanceName == null) ? 0 : instanceName.hashCode());
		result = prime * result + ((firmwareconstraintsId == null) ? 0 : firmwareconstraintsId.hashCode());
		
		return result;
	}

	@Override
	public String toString() {
		return "FirmwareConstraints [containMent=" + this.containMent 
		+ ", getId=" + this.id + ", hwVersion=" + this.hwVersion 
		+ ", fwVersion=" + this.fwVersion + ", build=" + this.build
		+ ", instanceName=" + this.instanceName
		+ ", firmwareconstraintsId=" + this.firmwareconstraintsId + "]";
	}
	
//	@Override
//	public String toString()
//	{
//	    return "Firmware "+toJSONString();
//	}
//	public String toJSONString() {
//	    
//	    String retValue = "";
//		
//	    retValue = "{"
//	        + "arm:'" + this.arm 
//	        + "',binaryFileName:'" + binaryFileName 
//	        + "',build:'" + this.build
//	        + "',equipKind:'" + this.equipKind 
//	        + "',equipModel:'" + this.equipModel 
//	        + "',equipType:'" + this.equipType 
//	        + "',equipVendor:'" + this.equipVendor 
//	        + "',firmwareId:'" + this.firmwareId 
//	        + "',id:'" + this.id 
//	        + "',releasedDate:'" + this.releasedDate 
//	        + "',supplier:'" + this.supplier
//	        + "',fwVersion:'" + this.fwVersion 
//	        + "',hwVersion:'" + this.hwVersion 
//	        + "'}";
//	    
//	    return retValue;
//	}
}
