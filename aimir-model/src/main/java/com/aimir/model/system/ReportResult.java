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
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.model.BaseObject;

/**
 * 리포트 결과 저장 클래스 
 *
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_RESULT")
public class ReportResult extends BaseObject implements JSONString{

    private static final long serialVersionUID = 2006444959367402034L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_REPORT_RESULT")
    @SequenceGenerator(name="SEQ_REPORT_RESULT", sequenceName="SEQ_REPORT_RESULT", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;    
    
	@Column(name="RESULT_LINK",length=255)
    private String resultLink;

    @Column(name="RESULT_FILE_LINK",length=255)
    @ColumnInfo(name="Export File Link")
    private String resultFileLink;

	@Column(name="WRITE_TIME",length=14)
	private String writeTime;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OPERATOR_ID", nullable=false)
    @ColumnInfo(name="사용자정보")
	private Operator operator;
	
	@Column(name="OPERATOR_ID", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@Column(name="FAIL_REASON",length=255)
	private String failReason;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="REPORTSCHEDULE_ID", nullable=false)
	private ReportSchedule reportSchedule;	
	
	@Column(name="REPORTSCHEDULE_ID", nullable=true, updatable=false, insertable=false)
	private Integer reportScheduleId;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="RESULT")
	private ResultStatus result;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

    public String getResultLink() {
		return resultLink;
	}

	public void setResultLink(String resultLink) {
		this.resultLink = resultLink;
	}

    public String getResultFileLink() {
        return resultFileLink;
    }

    public void setResultFileLink(String resultFileLink) {
        this.resultFileLink = resultFileLink;
    }

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	@XmlTransient
	public ReportSchedule getReportSchedule() {
		return reportSchedule;
	}

	public void setReportSchedule(ReportSchedule reportSchedule) {
		this.reportSchedule = reportSchedule;
	}

	public ResultStatus getResult() {
		return result;
	}

	public void setResult(Integer result) {
		
        for (int i = 0; i < ResultStatus.values().length; i++) {
            if (ResultStatus.values()[i].getCode().equals(result)) {
        		this.result = ResultStatus.values()[i];
            }
        }
	}
	

	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getReportScheduleId() {
        return reportScheduleId;
    }

    public void setReportScheduleId(Integer reportScheduleId) {
        this.reportScheduleId = reportScheduleId;
    }

    @Override
	public String toString()
	{
	    return "ReportResult "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + ", resultLink:'" + ((this.resultLink == null)? "":this.resultLink)
            + ", resultFileLink:'" + ((this.resultFileLink == null)? "":this.resultFileLink)
	        + ", writeTime:" + ((this.writeTime == null)? "":this.writeTime)
	        + ", operator:'" + ((this.operator == null)? "":this.operator.getId())
	        + ", failReason:" + ((this.failReason == null)? "":this.failReason)
	        + ", reportSchedule:'" + ((this.reportSchedule == null)? "":this.reportSchedule.getId())
	        + ", result:" + ((this.result == null)? "0":this.result)
	        + "'}";
	    
	    return retValue;
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
