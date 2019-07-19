package com.aimir.model.device;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.LoadType;
import com.aimir.model.system.AimirGroup;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * Load Shed AimirGroup<br>
 * Demand Response 허용된 고객 그룹 대상<br>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@DiscriminatorValue("LoadShedGroup")
public class LoadShedGroup extends AimirGroup {

	private static final long serialVersionUID = 5064442695778529580L;

    @ColumnInfo(descr="해당 그룹(대상)에 대한 총 공급 전력 합")
    @Column(name="SUPPLY_CAPACITY")
	private Double supplyCapacity;
	
    @ColumnInfo(name="", descr="공급전력 임계치 (%)")
    @Column(name="SUPPLY_THRESHOLD")
	private Double supplyThreshold;
	
    @ColumnInfo(descr="임계치 체크주기(분)")
    @Column(name="CHECK_INTERVAL")
	private Integer checkInterval;
	
    @ColumnInfo(descr="Load Shed Type : Emergency(0), 	Schedule(1),OnDemand(2)")
    @Column(name="LOAD_TYPE")
    @Enumerated(EnumType.STRING)
	private LoadType loadType;
	
    @ColumnInfo(descr="해당 결과에 대해 이벤트로그로 기록할지 결정")
    @Column(name="TRACE_LOG")
	private Boolean traceLog;    
    
    @OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="loadshedschedule_id")
	private Set<LoadShedSchedule> loadShedSchedules = new HashSet<LoadShedSchedule>(0);

	public Double getSupplyCapacity() {
		return supplyCapacity;
	}

	public void setSupplyCapacity(Double supplyCapacity) {
		this.supplyCapacity = supplyCapacity;
	}

	public Double getSupplyThreshold() {
		return supplyThreshold;
	}

	public void setSupplyThreshold(Double supplyThreshold) {
		this.supplyThreshold = supplyThreshold;
	}

	public Integer getCheckInterval() {
		return checkInterval;
	}

	public void setCheckInterval(Integer checkInterval) {
		this.checkInterval = checkInterval;
	}

	public LoadType getLoadType() {
		return loadType;
	}

	public void setLoadType(String loadType) {
		this.loadType = LoadType.valueOf(loadType);
	}

	public Boolean getTraceLog() {
		return traceLog;
	}

	public void setTraceLog(Boolean traceLog) {
		this.traceLog = traceLog;
	}

	@XmlTransient
	public Set<LoadShedSchedule> getLoadShedSchedules() {
		return loadShedSchedules;
	}

	public void setLoadShedSchedules(Set<LoadShedSchedule> loadShedSchedules) {
		this.loadShedSchedules = loadShedSchedules;
	}

}
