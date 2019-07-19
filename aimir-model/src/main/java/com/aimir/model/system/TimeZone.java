package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.model.BaseObject;

/**
 * TimeZone
 * Locale 정보 적용을 위한 timezone 정보
 * DST 정보, TimeZone 이름 정보를 가진다.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="AIMIRTIMEZONE")
public class TimeZone extends BaseObject{

	private static final long serialVersionUID = 5297838307633234511L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TIMEZONE_SEQ")
	@SequenceGenerator(name="TIMEZONE_SEQ", sequenceName="TIMEZONE_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(unique=true, nullable=false)
	private String name;	//timezone 이름 예) Asia/Seoul
	private String continent; // 대륙
	private String city;	// 도시명
	@Column(name="STD_ABBR") 
	private String stdAbbr;	
	@Column(name="STD_NAME") 
	private String stdName;	// 표준명
	@Column(name="DST_ABBR")
	private String dstAbbr;	
	@Column(name="DST_NAME")
	private String dstName;	//DST 명
	@Column(name="GMT_OFFSET")
	private String gmtOffset;	// GMT offset ex> 한국은 +9 영국은 0
	@Column(name="DST_ADJUSTMENT")
	private String dstAdjustment;	// DST 조정
	@Column(name="DST_STARTDATERULE")
	private String dstStartDateRule;	 //DST 시작 룰
	@Column(name="DST_ENDDATERULE")
	private String dstEndDateRule;//DST 종료 룰
	private String startTime; //DST 시작시간
	private String endTime;//DST 종료시간
	
	@Column(name="use_enable")
	private Boolean use; // 사용여부 enable/disable

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContinent() {
		return continent;
	}
	public void setContinent(String continent) {
		this.continent = continent;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getStdAbbr() {
		return stdAbbr;
	}
	public void setStdAbbr(String stdAbbr) {
		this.stdAbbr = stdAbbr;
	}
	public String getStdName() {
		return stdName;
	}
	public void setStdName(String stdName) {
		this.stdName = stdName;
	}
	public String getDstAbbr() {
		return dstAbbr;
	}
	public void setDstAbbr(String dstAbbr) {
		this.dstAbbr = dstAbbr;
	}
	public String getDstName() {
		return dstName;
	}
	public void setDstName(String dstName) {
		this.dstName = dstName;
	}
	public String getGmtOffset() {
		return gmtOffset;
	}
	public void setGmtOffset(String gmtOffset) {
		this.gmtOffset = gmtOffset;
	}
	public String getDstAdjustment() {
		return dstAdjustment;
	}
	public void setDstAdjustment(String dstAdjustment) {
		this.dstAdjustment = dstAdjustment;
	}
	public String getDstStartDateRule() {
		return dstStartDateRule;
	}
	public void setDstStartDateRule(String dstStartDateRule) {
		this.dstStartDateRule = dstStartDateRule;
	}
	public String getDstEndDateRule() {
		return dstEndDateRule;
	}
	public void setDstEndDateRule(String dstEndDateRule) {
		this.dstEndDateRule = dstEndDateRule;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public Boolean getUse() {
		return use;
	}
	public void setUse(Boolean use) {
		this.use = use;
	}
	
	@Override
    public String toString()
    {
        return "TimeZone " + toJSONString();
    }
    
    public String toJSONString() {

        String str = "";
        
        str = "{"
            + "id:'" + this.id
            + "', name:'" + this.name
            + "'}";
        
        return str;
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
}
