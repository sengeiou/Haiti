package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>가스 미터 정보</p>
 * 
 * @author goodjob
 *
 */
@Entity
@DiscriminatorValue("GasMeter")
public class GasMeter extends Meter {

    private static final long serialVersionUID = -5141779583419587993L;

    /**
     * 가스 미터의 벤더, 모델이 극동 MC2000 모델의 경우에만 적용 
     *    
     * 0bit : Case Open(0:Normal, 1:Tamper)
     * 1bit : Tamper(0:Normal, 1:Tamper)
     * 2bit : 진동(0:Normal, 1:진동)
     * 3bit : 차단불안정
     * 4bit : 개방작동 불량 
     * 5bit : 차단작동 불량 
     * 6~7bit : reserved
     */
    @ColumnInfo(name="알람 상태", view=@Scope(create=false, read=true, update=true, devicecontrol=true),descr="Alarm Status")
    @Column(name="ALARM_STATUS")
    private Integer alarmStatus; 

    @ColumnInfo(name="초기값", view=@Scope(create=true, read=true, update=true),descr="검침기 설치시 미터의 초기값")
    @Column(name="INIT_PULSE")
	private Double initPulse;
    
    @ColumnInfo(name="보정값", view=@Scope(create=true, read=true, update=true, devicecontrol=true),descr="장비 설정시 연결된 모뎀에 설정해 줘야 함,모뎀에서 올라오는 지침값과 실제 미터의 지침값과의 격차")
    @Column(name="CORRECT_PULSE")
    private Double correctPulse;    

    @ColumnInfo(name="", view=@Scope(create=true, read=true, update=true),descr="센서를 설치할 당시의 지침값")
    @Column(name="CURRENT_PULSE", length=10)
    private Double currentPulse;

    public Integer getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(Integer alarmStatus) {
		this.alarmStatus = alarmStatus;
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

    public void setCurrentPulse(Double currentPulse) {
        this.currentPulse = currentPulse;
    }

    public Double getCurrentPulse() {
        return currentPulse;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                + ((correctPulse == null) ? 0 : correctPulse.hashCode());
        result = prime * result
                + ((currentPulse == null) ? 0 : currentPulse.hashCode());
        result = prime * result
                + ((initPulse == null) ? 0 : initPulse.hashCode());
        result = prime * result
                + ((alarmStatus == null) ? 0 : alarmStatus.hashCode());
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
        GasMeter other = (GasMeter) obj;
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
        if (alarmStatus == null) {
            if (other.alarmStatus != null)
                return false;
        } else if (!alarmStatus.equals(other.alarmStatus))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "GasMeter [correctPulse=" + correctPulse + ", currentPulse="
                + currentPulse + ", initPulse=" + initPulse + ", alarmStatus="
                + alarmStatus + "]";
    }
}