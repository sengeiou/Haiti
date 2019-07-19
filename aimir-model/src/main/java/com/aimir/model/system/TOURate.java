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
import com.aimir.model.BaseObject;
import com.aimir.model.mvm.Season;

/**
 * 해당 클래스는 Time of use rate에 따른  에너지 사용량을 구분하기 위해 필요하다.
 * TOU Rate 정보 (계약종별 시간대 기준 )
 * Time Of Use Rate 정보로 시간대별 Peak Type 정보를 나타낸다.
 * 계절별, 시간대별 Peak Type 을 나타낸다.
 * Peak 시간대별 시작시간과 종료 시간 정보도 가진다.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "TOU_RATE")
public class TOURate extends BaseObject{

	private static final long serialVersionUID = -2147829927962950650L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="TOU_RATE_SEQ")
	@SequenceGenerator(name="TOU_RATE_SEQ", sequenceName="TOU_RATE_SEQ", allocationSize=1) 
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "tarifftype_id")
	@ReferencedBy(name="code")
	private TariffType tariffType;
	
	@Column(name="tarifftype_id", nullable=true, updatable=false, insertable=false)
	private Integer tariffTypeId;
	
	@ColumnInfo(descr="peak type => OFF_PEAK(0),PEAK(1),CRITICAL_PEAK(2)")
	@Column(name="peak_type", nullable=false)
	@Enumerated(EnumType.STRING)
	private PeakType peakType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "season_id")
	@ReferencedBy(name="name")
	private Season season;
	
	@Column(name="season_id", nullable=true, updatable=false, insertable=false)
	private Integer seasonId;
	
	@ColumnInfo(descr="로컬 상으로 표기하는 이름 영문 혹은 국가별 ")
	@Column(name="local_name")
	private String localName;
	
	@ColumnInfo(descr="해당 PEAK의 시작 시간 format '00'시")
	@Column(name="start_time", length=2)
	private String startTime;
	
	@ColumnInfo(descr="해당PEAK의 시작 시간 format '07'시")
	@Column(name="end_time", length=2)
	private String endTime;
	
	public TOURate() {
		
	}
	
	public TOURate(Integer id) {
		this.id = id;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
		
	@XmlTransient
	public TariffType getTariffType() {
		return tariffType;
	}
	public void setTariffType(TariffType tariffType) {
		this.tariffType = tariffType;
	}	

    public PeakType getPeakType() {
		return peakType;
	}

	public void setPeakType(String peakType) {
		this.peakType = PeakType.valueOf(peakType);
	}

	@XmlTransient
	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
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
