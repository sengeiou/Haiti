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

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.ReportParameterType;
import com.aimir.model.BaseObject;

/**
 * 파라미터와 관련된 리포트정보 
 *
 * 리포트의 조건 타입  (지역, 날짜 , 미터아이디, 장비 타입 등등)
 *
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_PARAMETER")
public class ReportParameter extends BaseObject implements JSONString{

	private static final long serialVersionUID = -7279589417110347376L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_PARAMETER_SEQ")
    @SequenceGenerator(name="REPORT_PARAMETER_SEQ", sequenceName="REPORT_PARAMETER_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REPORT_ID")
    private Report report;
	
	@Column(name="REPORT_ID", nullable=true, updatable=false, insertable=false)
	private Integer reportId;
	
	@Column(name="PARAMETER_TYPE",length=100)
	@Enumerated(EnumType.STRING)
    private ReportParameterType parameterType;    
    
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Report getReport() {
		return report;
	}

	public void setReport(Report report) {
		this.report = report;
	}

	public ReportParameterType getParameterType() {
		return this.parameterType;
	}

	public void setParameterType(String parameterType) {
		
		if(parameterType != null && !"".equals(parameterType)){
			this.parameterType = ReportParameterType.valueOf(parameterType);
		}		
	}
	
	public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
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
	        + ", report:'" + ((this.report == null)? "":this.report.getId())
	        + ", parameterType:'" + ((this.parameterType == null)? "":this.parameterType.name())
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public String toJSONString() {
	    return "ReportParameter "+toJSONString();
	}

}
