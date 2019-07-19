package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.model.system.Code;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>전기 미터 정보</p>
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("EnergyMeter")
public class EnergyMeter extends Meter {

    private static final long serialVersionUID = -2627401689224710036L;

    @ColumnInfo(name="미터 결상 타입", view=@Scope(create=true, read=true, update=true), descr="3phase 4wire 등등")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="METERELEMENT_ID")
    @ReferencedBy(name="code")
    private Code meterElement;
    
    @Column(name="METERELEMENT_ID", nullable=true, updatable=false, insertable=false)
    private Integer meterElementCodeId;

    @ColumnInfo(name="현재 변압기 비율", view=@Scope(create=true, read=true, update=true), descr="Current Transformer Ratio")
    @Column(name="CT")
    private Double ct;

    @ColumnInfo(name="전압 변압기 비율", view=@Scope(create=true, read=true, update=true), descr="Voltage Transformer Ratio")
    @Column(name="VT")
    private Double vt;

    @ColumnInfo(name="변압기 비율", view=@Scope(create=true, read=true, update=true), descr="Transformer Factor(ct x vt)")
    @Column(name="TRANSFORMER_RATIO")
    private Double transformerRatio;
    
    @ColumnInfo(name="현재 변압기 비율", view=@Scope(create=true, read=true, update=true), descr="Current Transformer Ratio")
    @Column(name="CT2")
    private Double ct2;

    @ColumnInfo(name="전압 변압기 비율", view=@Scope(create=true, read=true, update=true), descr="Voltage Transformer Ratio")
    @Column(name="VT2")
    private Double vt2;
    
    @ColumnInfo(name="변압기 비율", view=@Scope(create=true, read=true, update=true), descr="Potential Transformer Ratio")
    @Column(name="PT")
    private Double pt;
    
    /**
     * 미터의 모델이 SM110, Kamstrup382, AIDON 5530, 5540에만 적용
     */
    @ColumnInfo(name="스위치 상태", view=@Scope(create=false, read=true, update=true, devicecontrol=true), descr="Switch Status")
    @Column(name="SWITCH_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private CircuitBreakerStatus switchStatus;

    /**
     * 미터의 모델이 SM110, Kamstrup382, AIDON 5530, 5540에만 적용
     */
    @ColumnInfo(name="스위치 활성 상태", view=@Scope(create=false, read=true, update=true, devicecontrol=true))
    @Column(name="SWITCH_ACTIVATE_STATUS")
    private Integer switchActivateStatus;
    
    @ColumnInfo(name="DST 적용 여부",view=@Scope(create=false, read=true, update=false), descr="GE미터 적용")
    @Column(name="DST_APPLY_ON")
    private Boolean dstApplyOn;
    
    @ColumnInfo(name="DST SEASON 적용여부",view=@Scope(create=false, read=true, update=false), descr="GE미터 적용")
    @Column(name="DST_SEASON_On")
    private Boolean dstSeasonOn;
    
	@Column(name="POWER_GRID")
	@ColumnInfo(descr="파워 그리드 망 식별 정보")
	private String powerGrid;    
    
	@XmlTransient
    public Code getMeterElement() {
		return meterElement;
	}

	public void setMeterElement(Code meterElement) {
		this.meterElement = meterElement;
	}

	public Double getCt() {
		return ct;
	}

	public void setCt(Double ct) {
		this.ct = ct;
	}

	public Double getVt() {
		return vt;
	}

	public void setVt(Double vt) {
		this.vt = vt;
	}

	public Double getTransformerRatio() {
		return transformerRatio;
	}

	public void setTransformerRatio(Double transformerRatio) {
		this.transformerRatio = transformerRatio;
	}

	public CircuitBreakerStatus getSwitchStatus() {
		return switchStatus;
	}

	public void setSwitchStatus(Integer switchStatus) {
		
	    for (CircuitBreakerStatus status : CircuitBreakerStatus.values()) {
	        if (status.getCode() == switchStatus)
	        	this.switchStatus = status;
	    }
	}

	public Integer getSwitchActivateStatus() {
		return switchActivateStatus;
	}

	public void setSwitchActivateStatus(Integer switchActivateStatus) {
		this.switchActivateStatus = switchActivateStatus;
	}

	public Boolean getDstApplyOn() {
		return dstApplyOn;
	}

	public void setDstApplyOn(Boolean dstApplyOn) {
		this.dstApplyOn = dstApplyOn;
	}

	public Boolean getDstSeasonOn() {
		return dstSeasonOn;
	}

	public void setDstSeasonOn(Boolean dstSeasonOn) {
		this.dstSeasonOn = dstSeasonOn;
	}

	public String getPowerGrid() {
		return powerGrid;
	}

	public void setPowerGrid(String powerGrid) {
		this.powerGrid = powerGrid;
	}

    public Integer getMeterElementCodeId() {
        return meterElementCodeId;
    }

    public void setMeterElementCodeId(Integer meterElementCodeId) {
        this.meterElementCodeId = meterElementCodeId;
    }

    public Double getCt2() {
        return ct2;
    }

    public void setCt2(Double ct2) {
        this.ct2 = ct2;
    }

    public Double getVt2() {
        return vt2;
    }

    public void setVt2(Double vt2) {
        this.vt2 = vt2;
    }

    public Double getPt() {
        return pt;
    }

    public void setPt(Double pt) {
        this.pt = pt;
    }
    
}