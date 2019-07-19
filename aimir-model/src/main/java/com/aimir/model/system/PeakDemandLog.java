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
import com.aimir.constants.CommonConstants.PeakAndDemandThreshold;
import com.aimir.constants.CommonConstants.ResultStatus;

import com.aimir.model.system.PeakDemandScenario;
import com.aimir.util.TimeLocaleUtil;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2012</p>
 * 
 * Peak Demand DR 수행결과 저장.
 * for BEMS.
 * 
 * @author bmhan.
 * @date 2012-07-16
 */

@Entity
@Table(name="PEAKDEMAND_LOG")
public class PeakDemandLog extends BaseObject implements JSONString {

	private static final long serialVersionUID = -3726084692444191291L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PEAKDEMAND_LOG_SEQ")
	@SequenceGenerator(name="PEAKDEMAND_LOG_SEQ", sequenceName="PEAKDEMAND_LOG_SEQ", allocationSize=1)
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

	@Column(name="scenario_id", nullable=false,updatable=false, insertable=false)
	private Integer scenarioId;
		
	@Column(name="run_time", length=14, nullable=false)
	@ColumnInfo(name="수행시간", descr="수행된 시간 : YYYYMMDDHHMMSS")
	private String runTime;

	@ColumnInfo(name="수행결과", descr="SUCCESS:0, FAIL:1 ...")
	@Column(name="result")
	private ResultStatus result;
	
	public ResultStatus getResult(){
		return result;
	}

	public void setResult(String result) {
		if(result != null && !"".equals(result)){
			this.result = ResultStatus.valueOf(result);
		}
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

	public String getRunTime() {
		return runTime;
	}

	
	public void setThresholdLevel(Integer thresholdLevel) {
		this.thresholdLevel = thresholdLevel;
	}
	
	public void setRunTime(String runTime) {
		this.runTime = runTime;
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
		if(o instanceof PeakDemandLog) {
			PeakDemandLog pa = (PeakDemandLog) o;
   			return this.hashCode() == pa.hashCode();
   		}
   		else {
   			return false;
   		}
	} 
	@Override 
	public int hashCode() { 
		return (getClass() + "[" + this.id + "]" + this.getRunTime()).hashCode();
    }
	@Override 
	public String toString() { 
		return getClass() + " ## [" + this.id + "]" + this.getRunTime();
    }

	@Override
	public String toJSONString() {
		String time = runTime;
		String level = PeakAndDemandThreshold.GOOD.toString();
		if(scenario != null && scenario.getOperator() != null) {
			Supplier supplier = scenario.getOperator().getSupplier();
			
			// Supplier가 있다면, 날자 포매팅을 적용한다.
	    	if(supplier != null) {
	    		String lang = supplier.getLang().getCode_2letter();
	        	String country = supplier.getCountry().getCode_2letter();
	        	if(lang != null && country != null) {
		        	if(time.length() > 0) time = TimeLocaleUtil.getLocaleDate(time, lang, country);
	        	}
	    	}
		}
		if(level != null) {			
			if(thresholdLevel == 2) level = PeakAndDemandThreshold.WARNING.toString();
			else if(thresholdLevel == 3) level = PeakAndDemandThreshold.CRITICAL.toString();
			else level = PeakAndDemandThreshold.GOOD.toString();
		}
		return "{"
	        + "id:'" + id 
	        + "',hashCode:'" + hashCode() 
	        + "',level:'" + ((level == null) ? "" : level) 
	        + "',scenario:" + ((scenario == null) ? "{}" : scenario.toJSONString())
	        + ",runTime:'" + ((time == null) ? "" : time) 
	        + "',result:'" + ((result == null) ? "" : result.toString())
	        + "'}";
	}
}
