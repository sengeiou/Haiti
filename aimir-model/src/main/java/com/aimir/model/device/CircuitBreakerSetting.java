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

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 전기 공급 차단/해제 설정 (Circuit Break Control 설정)<br>
 * Aidon Meter MCCB, GE Relay Switch , Kamstrup CID 구현 설정<br>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
public class CircuitBreakerSetting extends BaseObject {

	private static final long serialVersionUID = -4056488859213228129L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CIRCUIT_BREAKER_SETTING_SEQ")
	@SequenceGenerator(name="CIRCUIT_BREAKER_SETTING_SEQ", sequenceName="CIRCUIT_BREAKER_SETTING_SEQ", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
	
	@Column(name="condition_method", nullable=false)
    @ColumnInfo(name="차단해제 방법, 비상, 선불, 임계치 등등 ")
    @Enumerated(EnumType.STRING)
	private CircuitBreakerCondition condition;

	@Column(name="blocking_threshold")
	@ColumnInfo(name="차단 시 도달 임계치 ")
	private Double blockingThreshold;
	
	@ColumnInfo(name="알람 발생 시 도달 임계치 ")
	@Column(name="alarm_threshold")
	private Double alarmThreshold;
	
	@Column(name="automatic_deactivation")
	@ColumnInfo(name="자동차단 여부 ")
	private Boolean automaticDeactivation;
	
	@ColumnInfo(name="자동복구 여부 ")
	@Column(name="automatic_activation")
	private Boolean automaticActivation;
	
	@Column(name="recovery_time")
	@ColumnInfo(name="복구 대기 시간")
	private Integer recoveryTime;
	
	@ColumnInfo(name="복구 대기 시간 시간 단위")
	@Column(name="time_unit")
	private Integer timeUnit;
	
	@ColumnInfo(name="알람 발생 여부")
	@Column(name="alarm")
	private Boolean alarm;
	
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public CircuitBreakerCondition getCondition() {
		return condition;
	}

	public void setCondition(CircuitBreakerCondition condition) {
		this.condition = condition;
	}

	public Double getBlockingThreshold() {
		return blockingThreshold;
	}

	public void setBlockingThreshold(Double blockingThreshold) {
		this.blockingThreshold = blockingThreshold;
	}

	public Double getAlarmThreshold() {
		return alarmThreshold;
	}

	public void setAlarmThreshold(Double alarmThreshold) {
		this.alarmThreshold = alarmThreshold;
	}

	public Boolean getAutomaticDeactivation() {
		return automaticDeactivation;
	}

	public void setAutomaticDeactivation(Boolean automaticDeactivation) {
		this.automaticDeactivation = automaticDeactivation;
	}

	public Boolean getAutomaticActivation() {
		return automaticActivation;
	}

	public void setAutomaticActivation(Boolean automaticActivation) {
		this.automaticActivation = automaticActivation;
	}

	public Integer getRecoveryTime() {
		return recoveryTime;
	}

	public void setRecoveryTime(Integer recoveryTime) {
		this.recoveryTime = recoveryTime;
	}

	public Integer getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(Integer timeUnit) {
		this.timeUnit = timeUnit;
	}

	public Boolean getAlarm() {
		return alarm;
	}

	public void setAlarm(Boolean alarm) {
		this.alarm = alarm;
	}	

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public String toString() {
		// TODO Auto-generated method stub
		return null;
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
