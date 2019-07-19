package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name = "FirmwareBoard")
public class FirmwareBoard extends BaseObject {

	private static final long serialVersionUID = 6329237071695179917L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="FIRMWAREBOARD_SEQ")
    @SequenceGenerator(name="FIRMWAREBOARD_SEQ", sequenceName="FIRMWAREBOARD_SEQ", allocationSize=1)
	private Integer id;

	@ColumnInfo(name="Firmware Id", descr="펌웨어 ID 혹은  NULL")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="FIRMWARE_ID")
    @ReferencedBy(name="firmwareId")
    private Firmware firmware;
	
	@Column(name="FIRMWARE_ID", nullable=true, updatable=false, insertable=false)
	private Integer firmwareId;

	@ColumnInfo(name="공급사 아이디", descr="공급사 테이블의 ID 혹은  NULL")
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name")
    private Supplier supplier;
	
	@Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;

    @ColumnInfo(name="Title", view=@Scope(create=true, read=true, update=false), descr="제목")
    @Column(name="TITLE", nullable=false, unique=false)
	private java.lang.String title;

    @ColumnInfo(name="Content", view=@Scope(create=true, read=true, update=false), descr="내용")
    @Column(name="CONTENT", nullable=true, unique=false)
    private java.lang.String content;
    
	@ColumnInfo(name="작성자 ID", descr="작성자 ID 혹은  NULL")
	@OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OPERATOR_ID")
    @ReferencedBy(name="loginId")
	private Operator operator;
	
	@Column(name="OPERATOR_ID", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;

    @ColumnInfo(name="Trigger ID", view=@Scope(create=true, read=true, update=false), descr="배포 ID")
    @Column(name="TRIGGER_ID", nullable=true, unique=false)
    private java.lang.String triggerId;

    @ColumnInfo(name="작성 날짜", view=@Scope(create=true, read=true, update=false), descr="작성 날짜")
    @Column(name="WRITEDATE",length=14 , nullable=false, unique=false)
    private java.lang.String writeDate;

    @ColumnInfo(name="읽은 횟수", view=@Scope(create=true, read=true, update=true), descr="읽은 횟수")
    @Column(name="READCOUNT", nullable=true, unique=false)
    private java.lang.Integer readCount;

	public Integer getId() {
		return id;
	}

	@XmlTransient
	public Firmware getFirmware() {
		return firmware;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public java.lang.String getTitle() {
		return title;
	}

	public java.lang.String getContent() {
		return content;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public java.lang.String getTriggerId() {
		return triggerId;
	}

	public java.lang.String getWriteDate() {
		return writeDate;
	}

	public java.lang.Integer getReadCount() {
		return readCount;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setFirmware(Firmware firmware) {
		this.firmware = firmware;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public void setTitle(java.lang.String title) {
		this.title = title;
	}

	public void setContent(java.lang.String content) {
		this.content = content;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public void setTriggerId(java.lang.String triggerId) {
		this.triggerId = triggerId;
	}

	public void setWriteDate(java.lang.String writeDate) {
		this.writeDate = writeDate;
	}

	public void setReadCount(java.lang.Integer readCount) {
		this.readCount = readCount;
	}

	public Integer getFirmwareId() {
        return firmwareId;
    }

    public void setFirmwareId(Integer firmwareId) {
        this.firmwareId = firmwareId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ ((firmware == null) ? 0 : firmware.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((operator == null) ? 0 : operator.hashCode());
		result = prime * result
				+ ((readCount == null) ? 0 : readCount.hashCode());
		result = prime * result
				+ ((supplier == null) ? 0 : supplier.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result
				+ ((triggerId == null) ? 0 : triggerId.hashCode());
		result = prime * result
				+ ((writeDate == null) ? 0 : writeDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		FirmwareBoard other = (FirmwareBoard) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (firmware == null) {
			if (other.firmware != null)
				return false;
		} else if (!firmware.equals(other.firmware))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (readCount == null) {
			if (other.readCount != null)
				return false;
		} else if (!readCount.equals(other.readCount))
			return false;
		if (supplier == null) {
			if (other.supplier != null)
				return false;
		} else if (!supplier.equals(other.supplier))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (triggerId == null) {
			if (other.triggerId != null)
				return false;
		} else if (!triggerId.equals(other.triggerId))
			return false;
		if (writeDate == null) {
			if (other.writeDate != null)
				return false;
		} else if (!writeDate.equals(other.writeDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FirmwareBoard [content=" + content + ", firmware=" + firmware
				+ ", id=" + id + ", opeator=" + operator + ", readCount="
				+ readCount + ", supplier=" + supplier + ", title=" + title
				+ ", triggerId=" + triggerId + ", writeDate=" + writeDate + "]";
	}
   
}
