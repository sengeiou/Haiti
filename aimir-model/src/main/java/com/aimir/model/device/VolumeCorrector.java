package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>대용량가스 미터 보정기</p>
 * <pre>
 * 가스 미터의 온도와 압력에 대한 정확한 값을 측정하기 위해 가스미터의 측정값을 보정하고자 하는 용도로 쓰인다.
 * </pre>
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("VolumeCorrector")
public class VolumeCorrector extends Meter {

    private static final long serialVersionUID = -3137419555912819689L;

    @ColumnInfo(name="변환 타입", view=@Scope(create=false, read=true, update=false))
    @Column(name="CONVERTER_TYPE")
    private String converterType;
    
    @ColumnInfo(name="사이트명",view=@Scope(create=false, read=true, update=false))
    @Column(name="SITE_NAME")
    private String siteName;
    
    @ColumnInfo(name="압력단위",view=@Scope(create=false, read=true, update=false))
    @Column(name="PRESSURE_UNIT")
    private String pressureUnit;
    
    @ColumnInfo(name="온도 단위",view=@Scope(create=false, read=true, update=false))
    @Column(name="TEMPERATURE_UNIT")
    private String temperatureUnit;
    
    @ColumnInfo(name="부피 단위",view=@Scope(create=false, read=true, update=false))
    @Column(name="VOLUMNE_UNIT")
    private String volumeUnit;

    @ColumnInfo(name="압축계수",view=@Scope(create=false, read=true, update=false))
    @Column(name="COMPRESS_FACTOR")
    private Double compressFactor;
    
    @ColumnInfo(name="가스 상대 비중",view=@Scope(create=false, read=true, update=false))
    @Column(name="GASRELATIVE_DENSITY")
    private Double gasRelativeDensity;
    
    @ColumnInfo(name="펄스 가중치",view=@Scope(create=false, read=true, update=false))
    @Column(name="PULSE_WEIGHT")
    private Double pulseWeight;
    
    @ColumnInfo(name="미터 계수",view=@Scope(create=false, read=true, update=false))
    @Column(name="METER_FACTOR")
    private Double meterFactor;
    
    @ColumnInfo(name="비중",view=@Scope(create=false, read=true, update=false))
    @Column(name="SPECIFIC_GRAVITY")
    private Double specificGravity;
    
    @ColumnInfo(name="기본 압력",view=@Scope(create=false, read=true, update=false))
    @Column(name="BASE_PRESSURE")
    private Double basePressure;
    
    @ColumnInfo(name="기본 온도",view=@Scope(create=false, read=true, update=false))
    @Column(name="BASE_TEMPERATURE")
    private Double baseTemperature;
    
    @ColumnInfo(name="최저 압력 하한",view=@Scope(create=false, read=true, update=false))
    @Column(name="LOWEST_LIMIT_PRESSURE")
    private Double lowestLimitPressure;
    
    @ColumnInfo(name="최고 압력 상한",view=@Scope(create=false, read=true, update=false))
    @Column(name="UPPER_LIMIT_PRESSURE")
    private Double upperLimitPressure;
    
    @ColumnInfo(name="최저 온도 하한",view=@Scope(create=false, read=true, update=false))
    @Column(name="LOWEST_LIMIT_TEMPERATURE")
    private Double lowestLimitTemperature;
    
    @ColumnInfo(name="최고 온도 상한",view=@Scope(create=false, read=true, update=false))
    @Column(name="UPPER_LIMIT_TEMPERATURE")
    private Double upperLimitTemperature;
    
    @ColumnInfo(name="비보정 값",view=@Scope(create=false, read=true, update=false))
    @Column(name="UNCORRECTED_USAGE_COUNT")
    private Double uncorrectedUsageCount;
    
    @ColumnInfo(name="보정값",view=@Scope(create=false, read=true, update=false))
    @Column(name="CORRETED_USAGE_COUNT")
    private Double corretedUsageCount;
    
    @ColumnInfo(name="비보정 계수",view=@Scope(create=false, read=true, update=false))
    @Column(name="UNCORRETED_USAGE_INDEX")
    private Double uncorretedUsageIndex;
    
    @ColumnInfo(name="보정계수",view=@Scope(create=false, read=true, update=false))
    @Column(name="CORRETED_USAGE_INDEX")
    private Double corretedUsageIndex;
    
    @ColumnInfo(name="대기 압력",view=@Scope(create=false, read=true, update=false))
    @Column(name="ATMOSPHERE_PRESSURE")
    private Double atmospherePressure;
    
    @ColumnInfo(name="현재 압력",view=@Scope(create=false, read=true, update=false))
    @Column(name="CURRENT_PRESSURE")
    private Double currentPressure;
    
    @ColumnInfo(name="현재 온도",view=@Scope(create=false, read=true, update=false))
    @Column(name="CURRENT_TEMPERATURE")
    private Double currentTemperature;
    
    @ColumnInfo(name="배터리 전압",view=@Scope(create=false, read=true, update=false))
    @Column(name="BATTERY_VOLTAGE")
    private Double batteryVoltage;
    
    @ColumnInfo(name="n2 질소",view=@Scope(create=false, read=true, update=false))
    @Column(name="N2")
    private Double n2;
    
    @ColumnInfo(name="co2 이산화 탄소",view=@Scope(create=false, read=true, update=false))
    @Column(name="CO2")
    private Double co2;
    
    @ColumnInfo(name="고정 압력",view=@Scope(create=false, read=true, update=false))
    @Column(name="FIXED_PRESSURE")
    private Double fixedPressure;
    
    @ColumnInfo(name="고정 온도",view=@Scope(create=false, read=true, update=false))
    @Column(name="FIXED_TEMPERATURE")
    private Double fixedTemperature;
    
    @ColumnInfo(name="난방 면적",view=@Scope(create=false, read=true, update=false))
    @Column(name="FIXEDFPV")
    private Double fixedFpv;
    
    @ColumnInfo(name="관로", view=@Scope(create=true, read=true, update=true), descr="가스관을 뜻함")
    @Column(name="PIPE_LINE")
    private Integer pipeLine;
    
    @ColumnInfo(name="태그",view=@Scope(create=true, read=true, update=true),descr="가스관의 하나의 경로 줄기 (포트)")
    @Column(name="TAG")
    private Integer tag;
    
    @ColumnInfo(name="전원 공급",view=@Scope(create=false, read=true, update=false))
    @Column(name="POWER_SUPPLY")
    private Integer powerSupply;
    
    @ColumnInfo(name="가스 기동시간",view=@Scope(create=false, read=true, update=false))
    @Column(name="GAS_HOUR")
    private Integer gasHour;
    

    public String getConverterType() {
		return converterType;
	}

	public void setConverterType(String converterType) {
		this.converterType = converterType;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getPressureUnit() {
		return pressureUnit;
	}

	public void setPressureUnit(String pressureUnit) {
		this.pressureUnit = pressureUnit;
	}

	public String getTemperatureUnit() {
		return temperatureUnit;
	}

	public void setTemperatureUnit(String temperatureUnit) {
		this.temperatureUnit = temperatureUnit;
	}

	public String getVolumeUnit() {
		return volumeUnit;
	}

	public void setVolumeUnit(String volumeUnit) {
		this.volumeUnit = volumeUnit;
	}

	public Double getCompressFactor() {
		return compressFactor;
	}

	public void setCompressFactor(Double compressFactor) {
		this.compressFactor = compressFactor;
	}

	public Double getGasRelativeDensity() {
		return gasRelativeDensity;
	}

	public void setGasRelativeDensity(Double gasRelativeDensity) {
		this.gasRelativeDensity = gasRelativeDensity;
	}

	public Double getPulseWeight() {
		return pulseWeight;
	}

	public void setPulseWeight(Double pulseWeight) {
		this.pulseWeight = pulseWeight;
	}

	public Double getMeterFactor() {
		return meterFactor;
	}

	public void setMeterFactor(Double meterFactor) {
		this.meterFactor = meterFactor;
	}

	public Double getSpecificGravity() {
		return specificGravity;
	}

	public void setSpecificGravity(Double specificGravity) {
		this.specificGravity = specificGravity;
	}

	public Double getBasePressure() {
		return basePressure;
	}

	public void setBasePressure(Double basePressure) {
		this.basePressure = basePressure;
	}

	public Double getBaseTemperature() {
		return baseTemperature;
	}

	public void setBaseTemperature(Double baseTemperature) {
		this.baseTemperature = baseTemperature;
	}

	public Double getLowestLimitPressure() {
		return lowestLimitPressure;
	}

	public void setLowestLimitPressure(Double lowestLimitPressure) {
		this.lowestLimitPressure = lowestLimitPressure;
	}

	public Double getUpperLimitPressure() {
		return upperLimitPressure;
	}

	public void setUpperLimitPressure(Double upperLimitPressure) {
		this.upperLimitPressure = upperLimitPressure;
	}

	public Double getLowestLimitTemperature() {
		return lowestLimitTemperature;
	}

	public void setLowestLimitTemperature(Double lowestLimitTemperature) {
		this.lowestLimitTemperature = lowestLimitTemperature;
	}

	public Double getUpperLimitTemperature() {
		return upperLimitTemperature;
	}

	public void setUpperLimitTemperature(Double upperLimitTemperature) {
		this.upperLimitTemperature = upperLimitTemperature;
	}

	public Double getUncorrectedUsageCount() {
		return uncorrectedUsageCount;
	}

	public void setUncorrectedUsageCount(Double uncorrectedUsageCount) {
		this.uncorrectedUsageCount = uncorrectedUsageCount;
	}

	public Double getCorretedUsageCount() {
		return corretedUsageCount;
	}

	public void setCorretedUsageCount(Double corretedUsageCount) {
		this.corretedUsageCount = corretedUsageCount;
	}

	public Double getUncorretedUsageIndex() {
		return uncorretedUsageIndex;
	}

	public void setUncorretedUsageIndex(Double uncorretedUsageIndex) {
		this.uncorretedUsageIndex = uncorretedUsageIndex;
	}

	public Double getCorretedUsageIndex() {
		return corretedUsageIndex;
	}

	public void setCorretedUsageIndex(Double corretedUsageIndex) {
		this.corretedUsageIndex = corretedUsageIndex;
	}

	public Double getAtmospherePressure() {
		return atmospherePressure;
	}

	public void setAtmospherePressure(Double atmospherePressure) {
		this.atmospherePressure = atmospherePressure;
	}

	public Double getCurrentPressure() {
		return currentPressure;
	}

	public void setCurrentPressure(Double currentPressure) {
		this.currentPressure = currentPressure;
	}

	public Double getCurrentTemperature() {
		return currentTemperature;
	}

	public void setCurrentTemperature(Double currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	public Double getBatteryVoltage() {
		return batteryVoltage;
	}

	public void setBatteryVoltage(Double batteryVoltage) {
		this.batteryVoltage = batteryVoltage;
	}

	public Double getN2() {
		return n2;
	}

	public void setN2(Double n2) {
		this.n2 = n2;
	}

	public Double getCo2() {
		return co2;
	}

	public void setCo2(Double co2) {
		this.co2 = co2;
	}

	public Double getFixedPressure() {
		return fixedPressure;
	}

	public void setFixedPressure(Double fixedPressure) {
		this.fixedPressure = fixedPressure;
	}

	public Double getFixedTemperature() {
		return fixedTemperature;
	}

	public void setFixedTemperature(Double fixedTemperature) {
		this.fixedTemperature = fixedTemperature;
	}

	public Double getFixedFpv() {
		return fixedFpv;
	}

	public void setFixedFpv(Double fixedFpv) {
		this.fixedFpv = fixedFpv;
	}

	public Integer getPipeLine() {
		return pipeLine;
	}

	public void setPipeLine(Integer pipeLine) {
		this.pipeLine = pipeLine;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	public Integer getPowerSupply() {
		return powerSupply;
	}

	public void setPowerSupply(Integer powerSupply) {
		this.powerSupply = powerSupply;
	}

	public Integer getGasHour() {
		return gasHour;
	}

	public void setGasHour(Integer gasHour) {
		this.gasHour = gasHour;
	}    
}