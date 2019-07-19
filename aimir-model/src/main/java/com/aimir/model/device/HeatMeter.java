package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.model.system.Code;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>열량계 정보</p>
 * 
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("HeatMeter")
public class HeatMeter extends Meter {

    private static final long serialVersionUID = -4615216004762158479L;

    @ColumnInfo(name="난방 면적",view=@Scope(create=true, read=true, update=true))
    @Column(name="HEATING_AREA")
    private Double heatingArea;    

    @ColumnInfo(name="열량계타입",view=@Scope(create=true, read=true, update=true), descr="is unheat(0) or heat(1)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="HEAT_TYPE")
    @ReferencedBy(name="code")
    private Code heatType;   // unheat(C)거나 heat(H)거나
    
    @Column(name="HEAT_TYPE", nullable=true, updatable=false, insertable=false)
    private Integer heatTypeCodeId;

    @ColumnInfo(name="기계실 번호",view=@Scope(create=true, read=true, update=true))
    @Column(name="APPARATUS_ROOM_NUMBER")
    private Integer apparatusRoomNumber; 

    @ColumnInfo(name="단위 펄스당 유량",view=@Scope(create=false, read=true, update=false))
    @Column(name="FLOW_PER_UNIT_PULSE")
    private Integer flowPerUnitPulse;

    @ColumnInfo(name="압력 센서 유무",view=@Scope(create=true, read=true, update=true))
    @Column(name="INSTALLED_PRESS_SENSOR")
    private Boolean installedPressSensor;

    @ColumnInfo(name="표준 규격",view=@Scope(create=false, read=true, update=false))
    @Column(name="STANDARD")
    private String standard;
    
    @ColumnInfo(name="기계실 번호",view=@Scope(create=true, read=true, update=true))
    @Column(name="NUM_OF_ROOM")
    private Integer numOfRoom;
    
    @ColumnInfo(name="검침 단위", view=@Scope(create=false, read=true, update=false))
    @Column(name="METERING_UNIT")
    private String meteringUnit;
    

    public Double getHeatingArea() {
		return heatingArea;
	}

	public void setHeatingArea(Double heatingArea) {
		this.heatingArea = heatingArea;
	}

	@XmlTransient
	public Code getHeatType() {
		return heatType;
	}

	public void setHeatType(Code heatType) {
		this.heatType = heatType;
	}

	public Integer getApparatusRoomNumber() {
		return apparatusRoomNumber;
	}

	public void setApparatusRoomNumber(Integer apparatusRoomNumber) {
		this.apparatusRoomNumber = apparatusRoomNumber;
	}

	public Integer getFlowPerUnitPulse() {
		return flowPerUnitPulse;
	}

	public void setFlowPerUnitPulse(Integer flowPerUnitPulse) {
		this.flowPerUnitPulse = flowPerUnitPulse;
	}

	public Boolean getInstalledPressSensor() {
		return installedPressSensor;
	}

	public void setInstalledPressSensor(Boolean installedPressSensor) {
		this.installedPressSensor = installedPressSensor;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public Integer getNumOfRoom() {
		return numOfRoom;
	}

	public void setNumOfRoom(Integer numOfRoom) {
		this.numOfRoom = numOfRoom;
	}

	public String getMeteringUnit() {
		return meteringUnit;
	}

	public void setMeteringUnit(String meteringUnit) {
		this.meteringUnit = meteringUnit;
	}

    public Integer getHeatTypeCodeId() {
        return heatTypeCodeId;
    }

    public void setHeatTypeCodeId(Integer heatTypeCodeId) {
        this.heatTypeCodeId = heatTypeCodeId;
    }
}