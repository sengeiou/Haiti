package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 리포트 생성 시 필요한 여러 조건 타입과 입력한 조건데이터 
 *
 * @author goodjob
 *
 */
@Entity
@Table(name = "REPORT_PARAMETERDATA")
public class ReportParameterData extends BaseObject implements JSONString{

	private static final long serialVersionUID = 600040362671747255L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_PARAMETERDATA_SEQ")
	@SequenceGenerator(name="REPORT_PARAMETERDATA_SEQ", sequenceName="REPORT_PARAMETERDATA_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;
    
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REPORTPARAMETER_ID")
    private ReportParameter reportParameter; 
	
	@Column(name="VALUE", length=255)
	private String value;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REPORTSCHEDULE_ID")
    private ReportSchedule reportSchedule;
	
	@Column(name="REPORTSCHEDULE_ID", nullable=true, updatable=false, insertable=false)
	private Integer reportScheduleId;
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public ReportParameter getReportParameter() {
		return reportParameter;
	}

	public void setReportParameter(ReportParameter reportParameter) {
		this.reportParameter = reportParameter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ReportSchedule getReportSchedule() {
		return reportSchedule;
	}

	public void setReportSchedule(ReportSchedule reportSchedule) {
		this.reportSchedule = reportSchedule;
	}

	public Integer getReportScheduleId() {
        return reportScheduleId;
    }

    public void setReportScheduleId(Integer reportScheduleId) {
        this.reportScheduleId = reportScheduleId;
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
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + ", reportParameter:'" + ((this.reportParameter == null)? "":this.reportParameter.getId())
	        + ", value:" + ((this.value == null)? "":this.value)
	        + ", reportSchedule:'" + ((this.reportSchedule == null)? "":this.reportSchedule.getId())
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public String toJSONString() {
	    return "ReportParameterData "+toJSONString();
	}

}
