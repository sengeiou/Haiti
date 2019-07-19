package com.aimir.model.system;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2012</p>
 * 
 * Peak Demand Event와 DR 시나리오에 대한 연결 정보 및 적용여부 설정
 * for BEMS.
 * 
 * @author bmhan.
 * @date 2012-07-16
 */

@Entity
@Table(name="PEAKDEMAND_SETTING")
public class PeakDemandSetting extends BaseObject implements JSONString {

	private static final long serialVersionUID = -3726084692444191291L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PEAKDEMAND_SETTING_SEQ")
	@SequenceGenerator(name="PEAKDEMAND_SETTING_SEQ", sequenceName="PEAKDEMAND_SETTING_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
	
	@Column(name="threshold_level", nullable=false)
	@ColumnInfo(name="임계치 레벨")
	private Integer thresholdLevel;

	@ColumnInfo(name="시나리오", descr="시나리오 정보") 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="scenario_id")
	@ReferencedBy(name="name")
	private PeakDemandScenario scenario;

	@Column(name="scenario_id", nullable=true, updatable=false, insertable=false)
	private Integer scenarioId;
		
	@Column(name="isAction")
	@ColumnInfo(name="적용여부", descr="true : 적용, false : 미적용")
	private boolean isAction;

	@Column(name="modify_time", length=14, nullable=false)
	@ColumnInfo(name="수정시간", descr="마지막으로 수정된 시간 : YYYYMMDDHHMMSS")
	private String modifyTime;

	@ColumnInfo(name="수행자", descr="Setting 수행자 / 즉, 로그인 정보") 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id")
	@ReferencedBy(name="loginId")
	private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	public Integer getOperatorId() {
        return operatorId;
    }
	
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
		
	public Integer getScenarioId() {
        return scenarioId;
    }
	
    public void setScenarioId(Integer scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    
	public Integer getThresholdLevel(){
		return thresholdLevel;
	}

	public PeakDemandScenario getScenario() {
		return scenario;
	}

	public boolean getIsAction() {
		return isAction;
	}
	
	public String getModifyTime() {
		return modifyTime;
	}

	public Operator getOperator() {
		return operator;
	}
	
	public void setThresholdLevel(Integer thresholdLevel) {
		this.thresholdLevel = thresholdLevel;
	}
	
	public void setIsAction(String isAction) {
	    this.isAction = Boolean.parseBoolean(isAction);
	}
	
	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	public void setScenario(PeakDemandScenario scenario) {
		this.scenario = scenario;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) { 
		if(o instanceof PeakDemandSetting) {
			PeakDemandSetting pa = (PeakDemandSetting) o;
   			return this.hashCode() == pa.hashCode();
   		}
   		else {
   			return false;
   		}
	} 
	@Override 
	public int hashCode() { 
		return (getClass() + "[" + this.id + "]").hashCode();
    }
	@Override 
	public String toString() { 
		return getClass() + " ## [" + this.id + "]";
    }

	@Override
	public String toJSONString() {
		return "{"
	        + "id:'" + id 
	        + "',hashCode:'" + hashCode() 
	        + "',thresholdLevel:'" + ((thresholdLevel == null) ? "" : thresholdLevel) 
	        + "',scenario:" + ((scenario == null) ? "{}" : scenario.toJSONString())
	        + ",isAction:'" + ((isAction) ? "true" : isAction) 
	        + "',modifyTime:'" + ((modifyTime == null) ? "" : modifyTime) 
	        + "'}";
	}
}
