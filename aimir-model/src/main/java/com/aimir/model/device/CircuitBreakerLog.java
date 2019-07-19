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
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>전기 공급 차단/해제 로그 (Circuit Break Control)</p>
 * Aidon Meter MCCB, GE Relay Switch , Kamstrup CID 구현 로그<br>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
public class CircuitBreakerLog extends BaseObject {

	private static final long serialVersionUID = -3426136427255726166L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CIRCUIT_BREAKER_LOG_SEQ")
    @SequenceGenerator(name="CIRCUIT_BREAKER_LOG_SEQ", sequenceName="CIRCUIT_BREAKER_LOG_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Long id;

	@Column(name="target_type", nullable=false)
    @ColumnInfo(name="장비유형 Location,Operator,Contract,MCU,Modem,Meter,EndDevice")
    @Enumerated(EnumType.STRING)
    private GroupType targetType;
	
    @Column(name="target_id", nullable=false)
    @ColumnInfo(name="타겟 아이디(미터,그룹,지역 등이 됨), 비지니스 키가됨")
    private String target;
	
	@Column(name="write_time", length=14, nullable=false)
	@ColumnInfo(name="서버저장시간", descr="YYYYMMDDHHMMSS")
	private String writeTime;
	
	@Column(name="status", nullable=false)
	@Enumerated(EnumType.STRING)
	private CircuitBreakerStatus status;	
	
	@Column(name="condition_method", nullable=false)
    @ColumnInfo(name="차단해제 방법, 비상, 선불, 임계치 등등 ")
    @Enumerated(EnumType.STRING)
	private CircuitBreakerCondition condition;

	@Column(name="result")
	@ColumnInfo(descr="실행결과")
	private ResultStatus result;
	
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GroupType getTargetType() {
		return targetType;
	}

	public void setTargetType(GroupType targetType) {
		this.targetType = targetType;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}

	public CircuitBreakerStatus getStatus() {
		return status;
	}

	public void setStatus(CircuitBreakerStatus status) {
		this.status = status;
	}

	public CircuitBreakerCondition getCondition() {
		return condition;
	}

	public void setCondition(CircuitBreakerCondition condition) {
		this.condition = condition;
	}

	public ResultStatus getResult() {
		return result;
	}

	public void setResult(ResultStatus result) {
		this.result = result;
	}

	public String toString() {
		// TODO Auto-generated method stub
		return null;
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
}
