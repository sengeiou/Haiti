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

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

import org.eclipse.persistence.annotations.Index;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>미터의 시간을 동기화(서버시간 혹은 DCU 시간)한 이력을 저장하는 클래스</p>
 * <pre>
 * SM110, kv2c등 미터의 경우 검침 데이터에 미터 타임 싱크 로그가 포함되어 있거나 커맨드를 통해 미터의 시간을 설정할 수 있다. 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="METERTIMESYNC_LOG")
public class MeterTimeSyncLog extends BaseObject {

	private static final long serialVersionUID = 7753145626321047234L;
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="METERTIMESYNC_LOG_SEQ")
    @SequenceGenerator(name="METERTIMESYNC_LOG_SEQ", sequenceName="METERTIMESYNC_LOG_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ReferencedBy(name="mdsId")
	private Meter meter;	

	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
    private Integer meterId;
	
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
    @Column(name="WRITE_DATE", length=14, nullable=false)
    private String writeDate;
    
    @Column(name="METER_DATE", length=14, nullable=false)
    @Index(name="IDX_METER_DATE")
    private String meterDate;
    
    @Column(name="BEFORE_DATE", length=14, nullable=false)
    private String beforeDate;
    
    @Column(name="AFTER_DATE", length=14, nullable=false)
    private String afterDate;
        
    @ColumnInfo(name="상태", descr="성공 실패 여부. (응답이 없는 Operation인 경우는 명령이 잘 내려갔으면 성공, 응답이 있는 Operation인 경우는 응답이 잘 와야 성공.)(0:Success, 1:Failed, 2:Invalid Argument, 3:Communication Failure)")
    @Column(name="RESULT", nullable=false)
    @Enumerated(EnumType.ORDINAL)
	private ResultStatus result;
    
    @ColumnInfo(name="수행자 타입", descr="수행자의 타입을 정의(0:System, 1:Operator)")
    @Column(name="OPERATOR_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private OperatorType operatorType;    
    
    @Column(name="OPERATOR")
    private String operator;

    @Column(name="TIME_DIFF")
    private Long timeDiff;

	@ColumnInfo(name="", descr="")
    @Column(name="DESCR")
    private String descr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    @XmlTransient
    public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getMeterDate() {
		return meterDate;
	}

	public void setMeterDate(String meterDate) {
		this.meterDate = meterDate;
	}

	public String getBeforeDate() {
		return beforeDate;
	}

	public void setBeforeDate(String beforeDate) {
		this.beforeDate = beforeDate;
	}

	public String getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(String afterDate) {
		this.afterDate = afterDate;
	}

	public ResultStatus getResult() {
		return result;
	}

	public void setResult(String result) {			
		this.result = ResultStatus.valueOf(result);
	}

	public OperatorType getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = OperatorType.valueOf(operatorType);
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Long getTimeDiff() {
		return timeDiff;
	}

	public void setTimeDiff(Long timeDiff) {
		this.timeDiff = timeDiff;
	}

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}
