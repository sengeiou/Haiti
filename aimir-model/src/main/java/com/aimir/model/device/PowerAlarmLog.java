package com.aimir.model.device;

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
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.LineType;
import com.aimir.constants.CommonConstants.PowerEventStatus;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;

import org.eclipse.persistence.annotations.Index;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Power Alarm Log </p>
 * <pre>
 * 정전 발생한 미터들에 대한 발생 로그 정전/복구 결상/결상복구 에 관한 이력을 남김 
 * </pre>
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="POWER_ALARM_LOG")
@Index(name="IDX_POWER_ALARM_LOG_01", columnNames={"meter_id","type_id","supplier_id", "openTime", "closeTime"})
public class PowerAlarmLog extends BaseObject implements JSONString {

	private static final long serialVersionUID = 7921488105951923562L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="POWER_ALARM_LOG_SEQ")
    @SequenceGenerator(name="POWER_ALARM_LOG_SEQ", sequenceName="POWER_ALARM_LOG_SEQ", allocationSize=1) 
	private Long id;	//	ID(PK)
	
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ColumnInfo(name="미터") 
	@ReferencedBy(name="mdsId")
	private Meter meter;
	
	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
	private Integer meterId;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable=false)
    @ColumnInfo(name="공급사")
    @ReferencedBy(name="name")
    private Supplier supplier;
	
	@Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@Column(length=14)
	@ColumnInfo(name="발생시간", descr="YYYYMMDDHHMMSS")
	private String openTime;
	
	@Column(length=14)
	@ColumnInfo(name="종료시간", descr="YYYYMMDDHHMMSS")
	private String closeTime;
	
	@Column(length=14)
	@ColumnInfo(name="서버저장시간", descr="YYYYMMDDHHMMSS")
	private String writeTime;
	
	@ColumnInfo(name="지속시간")
	private Long duration;
	
	@ColumnInfo(name="알림메세지")
	private String message;
	
	@ColumnInfo(name="상태", descr="정전 지속 여부 open/close")
	@Enumerated(EnumType.STRING)
	private PowerEventStatus status;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TYPE_ID")
	@ColumnInfo(name="타입", descr="long power outage, short power outage")
	@ReferencedBy(name="name")
	private Code typeCode;
	
	@Column(name="TYPE_ID", nullable=true, updatable=false, insertable=false)
	private Integer typeCodeId;

	@ColumnInfo(descr="계획정전 여부")
	private Boolean plannedPO;

	@ColumnInfo(descr="대량정전 여부")
	private Boolean massivePowerOutage;
	
	@ColumnInfo(descr="정전이 아니고 결선인 경우 발생한 결선 A,B,C ")
	@Enumerated(EnumType.STRING)
	private LineType lineType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	

	@XmlTransient
	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public String getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public PowerEventStatus getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = PowerEventStatus.valueOf(status);
	}
		
	@XmlTransient
	public Code getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(Code typeCode) {
		this.typeCode = typeCode;
	}
	
	public Boolean getPlannedPO() {
		return plannedPO;
	}

	public void setPlannedPO(Boolean plannedPO) {
		this.plannedPO = plannedPO;
	}
	
	public Boolean getMassivePowerOutage() {
		return massivePowerOutage;
	}

	public void setMassivePowerOutage(Boolean massivePowerOutage) {
		this.massivePowerOutage = massivePowerOutage;
	}

	public LineType getLineType() {
		return lineType;
	}

	public void setLineType(String lineType) {
		this.lineType = LineType.valueOf(lineType);
	}

	public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getTypeCodeId() {
        return typeCodeId;
    }

    public void setTypeCodeId(Integer typeCodeId) {
        this.typeCodeId = typeCodeId;
    }

    public String toJSONString() {
		JSONStringer js = null;		
		js = new JSONStringer();
		js.object().key("id").value(this.id)
				   .key("meter").value((this.meter == null)? "null":this.meter.getId())
				   .key("supplier").value((this.supplier == null)? "null":this.supplier.getId())
				   .key("openTime").value((this.openTime == null)? "null":this.openTime)
				   .key("closeTime").value((this.closeTime == null)? "null":this.closeTime)
				   .key("writeTime").value((this.writeTime == null)? "null":this.writeTime)
				   .key("duration").value((this.duration == null)? "null":this.duration)
				   .key("message").value((this.message == null)? "null":this.message)
				   .key("status").value((this.status == null)? "null":this.status.name())
				   .key("typeCode").value((this.typeCode == null)? "null":this.typeCode.getId())
				   .key("plannedPO").value((this.plannedPO == null)? "null":this.plannedPO)
				   .key("massivePowerOutage").value((this.massivePowerOutage == null)? "null":this.massivePowerOutage)
				   .key("lineType").value((this.lineType == null)? "null":this.lineType.getName())
				   .endObject();

		return js.toString();
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
        // TODO Auto-generated method stub
        return null;
    }

}