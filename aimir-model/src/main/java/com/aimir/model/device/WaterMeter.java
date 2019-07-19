package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>수도 미터 정보</p>
 * 
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("WaterMeter")
public class WaterMeter extends Meter {

    private static final long serialVersionUID = -5655825859390876579L;

    /*
     * 0bit : Case Open(0:Normal, 1:Tamper)
1bit : Tamper(0:Normal, 1:Tamper)
2bit : 진동(0:Normal, 1:진동)
3bit : 차단불안정
4bit : 개방작동 불량 
5bit : 차단작동 불량 
6~7bit : reserved
     */
    @ColumnInfo(name="알람 상태", view=@Scope(create=false, read=true, update=true, devicecontrol=true),descr="Alarm Status")
    @Column(name="ALARM_STATUS")
    private Integer alarmStatus; 

    @ColumnInfo(name="초기값", view=@Scope(create=true, read=true, update=true), descr="검침기 설치시 미터의 초기값")
    @Column(name="INIT_PULSE")
	private Double initPulse;
    
    @ColumnInfo(name="보정값", view=@Scope(create=true, read=true, update=true), descr="모뎀에서 올라오는 지침값과 실제 미터의 지침값과의 격차")
    @Column(name="CORRECT_PULSE")
    private Double correctPulse;

    @ColumnInfo(name="", view=@Scope(create=false, read=true, update=false),descr="센서를 설치할 당시의 지침값")
    @Column(name="CURRENT_PULSE", length=10)
    private Integer currentPulse;
    
    @ColumnInfo(name="지상/지하 설치", view=@Scope(create=true, read=true, update=true), descr="지상/지하 설치 위치 여부 지하 : true")
    @Column(name="UNDER_GROUND")
    private Boolean underGround;
    
    @ColumnInfo(name="미터 구경 caliber", view=@Scope(create=true, read=true, update=true),descr="미터 구경")
    @Column(name="METER_SIZE")
    private Double meterSize;
    
    @ColumnInfo(name="유속", view=@Scope(create=true, read=true, update=true),descr="수도 유속")
    @Column(name="QMAX")
    private Double QMax;   
    
    @ColumnInfo(name="밸브번호", view=@Scope(create=true, read=true, update=true),descr="밸브번호")
    @Column(name="VALVE_SERIAL")
    private String valveSerial;
    
	public void setAlarmStatus(Integer alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public Integer getAlarmStatus() {
		return alarmStatus;
	}

    public Double getInitPulse() {
		return initPulse;
	}

	public void setInitPulse(Double initPulse) {
		this.initPulse = initPulse;
	}

	public Double getCorrectPulse() {
		return correctPulse;
	}

	public void setCorrectPulse(Double correctPulse) {
		this.correctPulse = correctPulse;
	}

    public void setCurrentPulse(Integer currentPulse) {
        this.currentPulse = currentPulse;
    }

    public Integer getCurrentPulse() {
        return currentPulse;
    }    

	public Boolean getUnderGround() {
		return underGround;
	}

	public void setUnderGround(Boolean underGround) {
		this.underGround = underGround;
	}

	public Double getMeterSize() {
		return meterSize;
	}

	public void setMeterSize(Double meterSize) {
		this.meterSize = meterSize;
	}

	public Double getQMax() {
		return QMax;
	}

	public void setQMax(Double max) {
		QMax = max;
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((QMax == null) ? 0 : QMax.hashCode());
        result = prime * result
                + ((correctPulse == null) ? 0 : correctPulse.hashCode());
        result = prime * result
                + ((currentPulse == null) ? 0 : currentPulse.hashCode());
        result = prime * result
                + ((initPulse == null) ? 0 : initPulse.hashCode());
        result = prime * result
                + ((meterSize == null) ? 0 : meterSize.hashCode());
        result = prime * result
                + ((underGround == null) ? 0 : underGround.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WaterMeter other = (WaterMeter) obj;
        if (QMax == null) {
            if (other.QMax != null)
                return false;
        } else if (!QMax.equals(other.QMax))
            return false;
        if (correctPulse == null) {
            if (other.correctPulse != null)
                return false;
        } else if (!correctPulse.equals(other.correctPulse))
            return false;
        if (currentPulse == null) {
            if (other.currentPulse != null)
                return false;
        } else if (!currentPulse.equals(other.currentPulse))
            return false;
        if (initPulse == null) {
            if (other.initPulse != null)
                return false;
        } else if (!initPulse.equals(other.initPulse))
            return false;
        if (meterSize == null) {
            if (other.meterSize != null)
                return false;
        } else if (!meterSize.equals(other.meterSize))
            return false;
        if (underGround == null) {
            if (other.underGround != null)
                return false;
        } else if (!underGround.equals(other.underGround))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "WaterMeter [QMax=" + QMax + ", correctPulse=" + correctPulse
                + ", currentPulse=" + currentPulse + ", initPulse=" + initPulse
                + ", meterSize=" + meterSize + ", underGround=" + underGround
                + "]";
    }

	/**
	 * @param valveSerial the valveSerial to set
	 */
	public void setValveSerial(String valveSerial) {
		this.valveSerial = valveSerial;
	}

	/**
	 * @return the valveSerial
	 */
	public String getValveSerial() {
		return valveSerial;
	}

}