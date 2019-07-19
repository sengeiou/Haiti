package com.aimir.model.system;

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
import com.aimir.constants.CommonConstants.PeakType;
import com.aimir.model.mvm.Season;

/**
 * Tariff (계약종별 요금(요율) 전기 기준 클래스)
 * Tariff 클래스를 상속 받아 전기 관련 요금표 관련 정보를 추가하였다.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "TARIFF_EM")
public class TariffEM extends Tariff {

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TARIFF_EM_SEQ")
	@SequenceGenerator(name="TARIFF_EM_SEQ", sequenceName="TARIFF_EM_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@Column(name="supply_size_min")
	@ColumnInfo(descr="공급 사이즈 최소 ")
	private Double supplySizeMin;
	
	@Column(name="condition1")
	@ColumnInfo(descr="공급 사이즈 최소에 대한 조건 ")
	private String condition1;
	
	@Column(name="supply_size_max")
	@ColumnInfo(descr="공급 사이즈 최대")
	private Double supplySizeMax;
	
	@Column(name="condition2")
	@ColumnInfo(descr="공급 사이즈 최대에 대한 조건 ")
	private String condition2;
	
	@Column(name="supply_size_unit")
	@ColumnInfo(descr="공급 사이즈 단위 전기의 경우 kva")
	private String supplySizeUnit;
	
	@Column(name="service_charge")
	@ColumnInfo(descr="payable every month for each electricity account based on a daily rate (in Rands) and the number of days in the month.")
	private Double serviceCharge;
	
	@Column(name="admin_charge")
	@ColumnInfo(descr="payable on each point of delivery, which based on a daily rate (in Rands) and the number of days in the month.")
	private Double adminCharge;

	@Column(name="transmission_network_charge")
	@ColumnInfo(descr="payable each month, based on the annual utilised capacity of each point of delivery, based on the voltage of the supply, the transmissionzone and the utilised capacity applicable during all time periods")
	private Double transmissionNetworkCharge;
	
	@Column(name="distribution_network_charge")
	@ColumnInfo(descr="recovers Distribution network costs, varies on a monthly basis and is charged on the chargeable demand.")
	private Double distributionNetworkCharge;
	
	@Column(name="energy_demand_charge")
	@ColumnInfo(descr="기본요금,계약전력(kW) 혹은 공급사이즈 에 따른 기본 요금, a charge per premise that recovers peak energy costs, and is seasonally differentiated and based on the chargeable demand.")
	private Double energyDemandCharge;
	
	@Column(name="active_energy_charge")
	@ColumnInfo(descr="유효 사용량(kWh)에 따른 요금 , per kWh of electrical energy used in the month. This may be a single rate or an inclining block rate that has different charges depending on the monthly consumption")
	private Double activeEnergyCharge;
	
	@Column(name="reactive_energy_charge")
	@ColumnInfo(descr="무효사용량(kvarh)")
	private Double reactiveEnergyCharge;
	
	@Column(name="maxDemand")
	@ColumnInfo(descr="ECG SLT Tariff에 사용되는 요금")
	private Double maxDemand;
	
	@Column(name="START_HOUR")
	private String startHour;
	
	@Column(name="END_HOUR")
	private String endHour;
	/**
	 * contribution to cross-subsidies to rural and Home light
     * tariffs, applied to the total active energy supplied in the month
	 */
	@Column(name="ers")
	@ColumnInfo(name="electrification and rural subsidy", descr="a charge transparently indicating the contribution towards socio-economic subsidies.a c/kWh charge payable on the total active energy")
	private Double ers;
	
	@Column(name="rate_rebalancing_levy")
	@ColumnInfo(descr="government levy charged to non-renewable generators based on the energy they produce.")
	private Double rateRebalancingLevy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "season_id")
	@ReferencedBy(name="name")
	@ColumnInfo(descr="Season 타입 Season에 따라 요율이 틀려 질 수 있음 널일 경우 모든 계절에 해당")
	private Season season;	
	
	@Column(name="season_id", nullable=true, updatable=false, insertable=false)
	private Integer seasonId;
	
	@Column(name="peak_type")
	@ColumnInfo(descr="TOU Rate 타입 (off peak peak critical peak) 시간대에 따라 요율이 틀려 질 수 있음")
	@Enumerated(EnumType.STRING)
	private PeakType peakType;
	
	public TariffEM() {		
	}
	public TariffEM(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	
		
	public Double getSupplySizeMin() {
		return supplySizeMin;
	}
	public void setSupplySizeMin(Double supplySizeMin) {
		this.supplySizeMin = supplySizeMin;
	}
	public String getCondition1() {
		return condition1;
	}
	public void setCondition1(String condition1) {
		this.condition1 = condition1;
	}
	public Double getSupplySizeMax() {
		return supplySizeMax;
	}
	public void setSupplySizeMax(Double supplySizeMax) {
		this.supplySizeMax = supplySizeMax;
	}
	public String getCondition2() {
		return condition2;
	}
	public void setCondition2(String condition2) {
		this.condition2 = condition2;
	}
	public String getSupplySizeUnit() {
		return supplySizeUnit;
	}
	public void setSupplySizeUnit(String supplySizeUnit) {
		this.supplySizeUnit = supplySizeUnit;
	}
	public Double getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(Double serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public Double getAdminCharge() {
		return adminCharge;
	}
	public void setAdminCharge(Double adminCharge) {
		this.adminCharge = adminCharge;
	}
	
	public Double getTransmissionNetworkCharge() {
		return transmissionNetworkCharge;
	}
	public void setTransmissionNetworkCharge(Double transmissionNetworkCharge) {
		this.transmissionNetworkCharge = transmissionNetworkCharge;
	}
	public Double getDistributionNetworkCharge() {
		return distributionNetworkCharge;
	}
	public void setDistributionNetworkCharge(Double distributionNetworkCharge) {
		this.distributionNetworkCharge = distributionNetworkCharge;
	}
	public Double getEnergyDemandCharge() {
		return energyDemandCharge;
	}
	public void setEnergyDemandCharge(Double energyDemandCharge) {
		this.energyDemandCharge = energyDemandCharge;
	}
	public Double getActiveEnergyCharge() {
		return activeEnergyCharge;
	}
	public void setActiveEnergyCharge(Double activeEnergyCharge) {
		this.activeEnergyCharge = activeEnergyCharge;
	}

	public Double getReactiveEnergyCharge() {
		return reactiveEnergyCharge;
	}
	public void setReactiveEnergyCharge(Double reactiveEnergyCharge) {
		this.reactiveEnergyCharge = reactiveEnergyCharge;
	}
	
	public Double getMaxDemand() {
		return maxDemand;
	}
	public void setMaxDemand(Double maxDemand) {
		this.maxDemand = maxDemand;
	}
		
	public String getStartHour() {
		return startHour;
	}
	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}
	public String getEndHour() {
		return endHour;
	}
	public void setEndHour(String endHour) {
		this.endHour = endHour;
	}
	public Double getErs() {
		return ers;
	}
	public void setErs(Double ers) {
		this.ers = ers;
	}
	
	public Double getRateRebalancingLevy() {
		return rateRebalancingLevy;
	}
	public void setRateRebalancingLevy(Double rateRebalancingLevy) {
		this.rateRebalancingLevy = rateRebalancingLevy;
	}
	
	@XmlTransient
	public Season getSeason() {
		return season;
	}
	public void setSeason(Season season) {
		this.season = season;
	}
	public PeakType getPeakType() {
		return peakType;
	}
	public void setPeakType(String peakType) {
		this.peakType = PeakType.valueOf(peakType);
	}
    public Integer getSeasonId() {
        return seasonId;
    }
    public void setSeasonId(Integer seasonId) {
        this.seasonId = seasonId;
    }	
}
