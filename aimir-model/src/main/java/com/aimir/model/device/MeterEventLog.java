package com.aimir.model.device;

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
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

import org.eclipse.persistence.annotations.Index;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Meter Event Log</p>
 * 
 * <pre>미터에서 발생한 이벤트 내역을 저장하는 클래스</pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="METEREVENT_LOG")
@Index(name="IDX_METEREVENT_LOG_01", columnNames={"METEREVENT_ID", "ACTIVATOR_ID", "YYYYMMDD", "OPEN_TIME"})
public class MeterEventLog extends BaseObject implements JSONString {

	private static final long serialVersionUID = 3704829716152527781L;

    @EmbeddedId public  MeterEventLogPk  id;    //  ID(PK)
    
    @Column(length=8, nullable=false)
    @ColumnInfo(name="발생날짜", descr="YYYYMMDD")
    private String yyyymmdd;
    
    @Column(length=14, nullable=false)
    @ColumnInfo(name="서버저장시간", descr="YYYYMMDDHHMMSS")
    private String writeTime;
    
    @ColumnInfo(name="알림메세지")
    @Column(length=255)
    private String message;

    @Column(name = "ACTIVATOR_TYPE", nullable=false)
    @ColumnInfo(name="발생대상타입")
    @Enumerated(EnumType.STRING)
    private TargetClass activatorType;
    
    @Column(name = "INTEGRATED",columnDefinition= "INTEGER default 0")
    @ColumnInfo(name="외부 시스템 연계 여부 성공 : true(1), 실패 false(0)")
    private Boolean integrated;
    
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;    
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
	public MeterEventLog(){
		id = new MeterEventLogPk();
	}

    public MeterEventLogPk getId() {
		return id;
	}

	public void setId(MeterEventLogPk id) {
		this.id = id;
	}

	public String getMeterEventId() {
		return this.id.getMeterEventId();
	}

	public void setMeterEventId(String meterEventId) {
		this.id.setMeterEventId(meterEventId);
	}

	public String getYyyymmdd() {
		return yyyymmdd;
	}

	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}

	public String getOpenTime() {
        return this.id.getOpenTime();
    }

    public void setOpenTime(String openTime) {
        this.id.setOpenTime(openTime);
    }


    public String getWriteTime() {
        return writeTime;
    }

    public void setWriteTime(String writeTime) {
        this.writeTime = writeTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TargetClass getActivatorType() {
        return activatorType;
    }
    
    public void setActivatorType(String activatorType) {
        this.activatorType = TargetClass.valueOf(activatorType);
    }

    public String getActivatorId() {
        return this.id.getActivatorId();
    }

    public void setActivatorId(String activatorId) {
        this.id.setActivatorId(activatorId);
    }
    
	public Boolean getIntegrated() {
		return integrated;
	}

	public void setIntegrated(Boolean integrated) {
		this.integrated = integrated;
	}
	

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String toJSONString() {
        JSONStringer js = null;
        
        js = new JSONStringer();
        js.object().key("id").value(this.id)
                   .key("meterEvent").value((this.id.getMeterEventId() == null)? "null":this.id.getMeterEventId())
                   .key("yyyymmdd").value((this.yyyymmdd == null)? "null":this.yyyymmdd)
                   .key("openTime").value((this.id.getOpenTime() == null)? "null":this.id.getOpenTime())
                   .key("writeTime").value((this.writeTime == null)? "null":this.writeTime)
                   .key("message").value((this.message == null)? "null":this.message)
                   .key("activatorType").value((this.activatorType == null)? "null":this.activatorType.name())
                   .key("activatorId").value((this.id.getActivatorId() == null)? "null":this.id.getActivatorId())
                   .key("integrated").value((this.integrated == null)? "false":this.integrated)
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
        return toJSONString();
    }
}