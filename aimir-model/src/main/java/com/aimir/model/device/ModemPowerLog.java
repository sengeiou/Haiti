package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

import org.eclipse.persistence.annotations.Index;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>모뎀의 배터리 파워 로그</p>
 * 
 * (Repeator, ZEUPLS, 등은 태양열(Solar) 건전지 혹은 배터리 타입이므로 배터리 정보가 올라옴)
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="MODEM_POWER_LOG")
@Index(name="IDX_MODEM_POWER_LOG_01", columnNames={"device_Type", "device_Id", "yyyymmdd", "hhmmss"})
public class ModemPowerLog extends BaseObject {

	private static final long serialVersionUID = 569890129366544288L;

    @EmbeddedId public ModemPowerLogPk id;
	
	@Column(name="BATTERY_VOLT")
	@ColumnInfo(name="배터리 전압")
	private Double batteryVolt;
	
	@Column(name="VOLTAGE_CURRENT")
	@ColumnInfo(name="배터리 전류")
	private Double voltageCurrent;
	
	@Column(name="VOLTAGE_OFFSET")
	@ColumnInfo(name="전압 인덱스")
	private Integer voltageOffset;
	
	@Column(name="SOLAR_ADV")
	private Double solarADV;
	
	@Column(name="SOLAR_CHGBV")
	private Double solarCHGBV;
	
	@Column(name="SOLAR_BCDV")
	private Double solarBCDV;
	
	@Column(name="RESET_COUNT")
	@ColumnInfo(name="리셋 횟수")
	private Long resetCount;
	
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
	
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
	public ModemPowerLog(){
		id = new ModemPowerLogPk();
	}

	public ModemPowerLogPk getId() {
		return id;
	}
	public void setId(ModemPowerLogPk id) {
		this.id = id;
	}
	public ModemType getDeviceType() {
		return id.getDeviceType();
	}
	public void setDeviceType(String deviceType) {
		id.setDeviceType(deviceType);
	}
	public String getDeviceId() {
		return id.getDeviceId();
	}
	public void setDeviceId(String deviceId) {
		id.setDeviceId(deviceId);
	}
	public String getYyyymmdd() {
		return id.getYyyymmdd();
	}
	public void setYyyymmdd(String yyyymmdd) {
		id.setYyyymmdd(yyyymmdd);
	}
	public String getHhmmss() {
		return id.getHhmmss();
	}
	public void setHhmmss(String hhmmss) {
		id.setHhmmss(hhmmss);
	}
	public Double getBatteryVolt() {
		return batteryVolt;
	}
	public void setBatteryVolt(Double batteryVolt) {
		this.batteryVolt = batteryVolt;
	}
	public Double getVoltageCurrent() {
		return voltageCurrent;
	}
	public void setVoltageCurrent(Double voltageCurrent) {
		this.voltageCurrent = voltageCurrent;
	}
	public Integer getVoltageOffset() {
		return voltageOffset;
	}
	public void setVoltageOffset(Integer voltageOffset) {
		this.voltageOffset = voltageOffset;
	}
	public Double getSolarADV() {
		return solarADV;
	}
	public void setSolarADV(Double solarADV) {
		this.solarADV = solarADV;
	}
	public Double getSolarCHGBV() {
		return solarCHGBV;
	}
	public void setSolarCHGBV(Double solarCHGBV) {
		this.solarCHGBV = solarCHGBV;
	}
	public Double getSolarBCDV() {
		return solarBCDV;
	}
	public void setSolarBCDV(Double solarBCDV) {
		this.solarBCDV = solarBCDV;
	}
	public Long getResetCount() {
		return resetCount;
	}
	public void setResetCount(Long resetCount) {
		this.resetCount = resetCount;
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
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

}