package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="Firmware",discriminatorType=DiscriminatorType.STRING)
@Table(name="Firmware")
public class Firmware extends BaseObject {
	private static final long serialVersionUID = 1329672452910583447L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FIRMWARE_SEQ")
    @SequenceGenerator(name="FIRMWARE_SEQ", sequenceName="FIRMWARE_SEQ", allocationSize=1)
	private Integer id;

    @ColumnInfo(name="Firmware Id", view=@Scope(create=true, read=true, update=false), descr="펌웨어 ID")
    @Column(name="FIRMWARE_ID", nullable=false, unique=true)
    private java.lang.String firmwareId;

    @ColumnInfo(name="공급사아이디", descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name")
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @ColumnInfo(name="Equipment Kind", view=@Scope(create=true, read=true, update=false), descr="장비 종류")
    @Column(name="EQUIP_KIND", nullable=true, unique=false)
    private String equipKind;

    @ColumnInfo(name="Equipment Type", view=@Scope(create=true, read=true, update=false), descr="장비 타입")
    @Column(name="EQUIP_TYPE", nullable=true, unique=false)
    private String equipType;
    
    @ColumnInfo(name="Equipment Vendor", view=@Scope(create=true, read=true, update=false), descr="장비 벤더")
    @Column(name="EQUIP_VENDOR", nullable=true, unique=false)
    private String equipVendor;

    @ColumnInfo(name="Equipment Model", view=@Scope(create=true, read=true, update=false), descr="장비 모델")
    @Column(name="EQUIP_MODEL", nullable=true, unique=false)
    private String equipModel;
    
    @ColumnInfo(name="Arm", view=@Scope(create=true, read=true, update=false), descr="ARM 사용 여부")
    @Column(name="ARM", nullable=false, unique=false)
    private Boolean arm;

    @ColumnInfo(name="하드웨어 버전", view=@Scope(create=true, read=true, update=false), descr="하드웨어 버전]")
    @Column(name="HW_VERSION", nullable=false, unique=false)
    private java.lang.String hwVersion;

    @ColumnInfo(name="펌웨어 버전", view=@Scope(create=true, read=true, update=false), descr="펌웨어 버전")
    @Column(name="FW_VERSION", nullable=false, unique=false)
    private java.lang.String fwVersion;

    @ColumnInfo(name="빌드/리비전 번호", view=@Scope(create=true, read=true, update=false), descr="펌웨어 빌드 또는 리비전 번호")
    @Column(name="BUILD", nullable=false, unique=false)
    private java.lang.String build;

    @ColumnInfo(name="펌웨어 릴리즈 날짜", view=@Scope(create=true, read=true, update=false), descr="펌웨어 릴리즈 날짜")
    @Column(name="RELEASED_DATE", length=14)
    private java.lang.String releasedDate;

    @ColumnInfo(name="바이너리파일명", view=@Scope(create=true, read=true, update=false), descr="펌웨어 바이너리 파일명")
    @Column(name="BINARYFILENAME", nullable=false, unique=false)
    private java.lang.String binaryFileName;
    
    @ColumnInfo(name="펌웨어 버전 + 파일명", view=@Scope(create=true, read=true, update=false), descr="펌웨어 버전 + 파일명")
    @Column(name="FILENAME", nullable=true, unique=false)
    private java.lang.String fileName;

    @ColumnInfo(name="DEVICEMODEL_ID", view=@Scope(create=true, read=true, update=false), descr="DEVICEMODEL_ID")
    @Column(name="DEVICEMODEL_ID", nullable=true, unique=false)
    private Integer devicemodel_id;
    
    @ColumnInfo(name="CHECK_SUM", view=@Scope(create=true, read=true, update=true), descr="check-sum")
    @Column(name="CHECK_SUM", nullable=true, unique=false)
    private String checkSum;
    
    @ColumnInfo(name="CRC", view=@Scope(create=true, read=true, update=true), descr="cyclical redundancy check")
    @Column(name="CRC", nullable=true, unique=false)
    private String crc;
    
    @ColumnInfo(name="IMAGE_KEY", view=@Scope(create=true, read=true, update=true), descr="image key")
    @Column(name="IMAGE_KEY", nullable=true, unique=false)
    private String imageKey;
    
    @ColumnInfo(name="FILE_PATH", view=@Scope(create=true, read=true, update=true), descr="file path")
    @Column(name="FILE_PATH", nullable=true, unique=false)
    private String filePath;

    @ColumnInfo(name="FILE_URL_PATH", view=@Scope(create=true, read=true, update=true), descr="file url path")
    @Column(name="FILE_URL_PATH", nullable=true, unique=false)
    private String fileUrlPath;
    
	public Integer getDevicemodel_id() {
		return devicemodel_id;
	}

	public void setDevicemodel_id(Integer devicemodelId) {
		devicemodel_id = devicemodelId;
	}

	public Integer getId() {
		return id;
	}

	public java.lang.String getFirmwareId() {
		return firmwareId;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public String getEquipKind() {
		return equipKind;
	}

	public String getEquipType() {
		return equipType;
	}

	public String getEquipVendor() {
		return equipVendor;
	}

	public String getEquipModel() {
		return equipModel;
	}

	public Boolean isArm() {
		return arm;
	}

	public java.lang.String getHwVersion() {
		return hwVersion;
	}

	public java.lang.String getFwVersion() {
		return fwVersion;
	}

	public java.lang.String getBuild() {
		return build;
	}

	public java.lang.String getReleasedDate() {
		return releasedDate;
	}

	public java.lang.String getBinaryFileName() {
		return binaryFileName;
	}
	
	public java.lang.String getFileName() {
		return fileName;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Boolean getArm() {
		return arm;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFirmwareId(String firmwareId) {
		this.firmwareId = firmwareId;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(String checkSum) {
		this.checkSum = checkSum;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public String getFilePath() {
		return filePath;
	}
	
	public String getFileUrlPath() {
		return fileUrlPath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setFileUrlPath(String fileUrlPath) {
		this.fileUrlPath = fileUrlPath;
	}

	public void setEquipKind(String equipKind) {
		this.equipKind = equipKind;
	}

	public void setEquipType(String equipType) {
		this.equipType = equipType;
	}

	public void setEquipVendor(String equipVendor) {
		this.equipVendor = equipVendor;
	}

	public void setEquipModel(String equipModel) {
		this.equipModel = equipModel;
	}

	public void setArm(Boolean arm) {
		this.arm = arm;
	}

	public void setHwVersion(java.lang.String hwVersion) {
		this.hwVersion = hwVersion;
	}

	public void setFwVersion(java.lang.String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public void setBuild(java.lang.String build) {
		this.build = build;
	}

	public void setReleasedDate(java.lang.String releasedDate) {
		this.releasedDate = releasedDate;
	}

	public void setBinaryFileName(java.lang.String binaryFileName) {
		this.binaryFileName = binaryFileName;
	}
	
	public void setFileName(java.lang.String fileName) {
		this.fileName = fileName;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		Firmware other = (Firmware) obj;
		if (arm != other.arm)
			return false;
		if (binaryFileName == null) {
			if (other.binaryFileName != null)
				return false;
		} else if (!binaryFileName.equals(other.binaryFileName))
			return false;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (build == null) {
			if (other.build != null)
				return false;
		} else if (!build.equals(other.build))
			return false;
		if (equipKind == null) {
			if (other.equipKind != null)
				return false;
		} else if (!equipKind.equals(other.equipKind))
			return false;
		if (equipModel == null) {
			if (other.equipModel != null)
				return false;
		} else if (!equipModel.equals(other.equipModel))
			return false;
		if (equipType == null) {
			if (other.equipType != null)
				return false;
		} else if (!equipType.equals(other.equipType))
			return false;
		if (equipVendor == null) {
			if (other.equipVendor != null)
				return false;
		} else if (!equipVendor.equals(other.equipVendor))
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
		if (releasedDate == null) {
			if (other.releasedDate != null)
				return false;
		} else if (!releasedDate.equals(other.releasedDate))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;

		if (checkSum == null) {
			if (other.checkSum != null)
				return false;
		} else if (!checkSum.equals(other.checkSum))
			return false;
		if (crc == null) {
			if (other.crc != null)
				return false;
		} else if (!crc.equals(other.crc))
			return false;
		if (imageKey == null) {
			if (other.imageKey != null)
				return false;
		} else if (!imageKey.equals(other.imageKey))
			return false;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (fileUrlPath == null) {
			if (other.fileUrlPath != null)
				return false;
		} else if (!fileUrlPath.equals(other.fileUrlPath))
			return false;
		
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + (arm ? 1231 : 1237);
		result = prime * result + ((binaryFileName == null) ? 0 : binaryFileName.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((build == null) ? 0 : build.hashCode());
		result = prime * result
				+ ((equipKind == null) ? 0 : equipKind.hashCode());
		result = prime * result
				+ ((equipModel == null) ? 0 : equipModel.hashCode());
		result = prime * result
				+ ((equipType == null) ? 0 : equipType.hashCode());
		result = prime * result
				+ ((equipVendor == null) ? 0 : equipVendor.hashCode());
		result = prime * result
				+ ((firmwareId == null) ? 0 : firmwareId.hashCode());
		result = prime * result
				+ ((fwVersion == null) ? 0 : fwVersion.hashCode());
		result = prime * result
				+ ((hwVersion == null) ? 0 : hwVersion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((releasedDate == null) ? 0 : releasedDate.hashCode());
		result = prime * result
				+ ((supplier == null) ? 0 : supplier.hashCode());
		
		result = prime * result
				+ ((checkSum == null) ? 0 : checkSum.hashCode());
		result = prime * result
				+ ((crc == null) ? 0 : crc.hashCode());
		result = prime * result
				+ ((imageKey == null) ? 0 : imageKey.hashCode());
		result = prime * result
				+ ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result
				+ ((fileUrlPath == null) ? 0 : fileUrlPath.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Firmware [arm=" + arm + ", binaryFileName=" + binaryFileName + ", fileName=" + fileName
				+ ", build=" + build + ", equipKind=" + equipKind
				+ ", equipModel=" + equipModel + ", equipType=" + equipType
				+ ", equipVendor=" + equipVendor + ", firmwareId=" + firmwareId
				+ ", fwVersion=" + fwVersion + ", hwVersion=" + hwVersion
				+ ", id=" + id + ", releasedDate=" + releasedDate
				+ ", supplier=" + supplier
				
				+ ", checkSum=" + checkSum
				+ ", crc=" + crc
				+ ", imageKey=" + imageKey
				+ ", filePath=" + filePath
				+ ", fileUrlPath=" + fileUrlPath
				+ "]";
	}
}
