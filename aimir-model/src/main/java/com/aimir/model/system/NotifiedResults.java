package com.aimir.model.system;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 통보 결과 정보 클래스
 * 
 * @author 박종성(elevas)
 *
 */
@Entity
@Table(name = "NOTIFIED_RESULTS")
public class NotifiedResults extends BaseObject implements JSONString{

	private static final long serialVersionUID = 7413005689194042727L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NOTIFIED_RESULTS_SEQ")
	@SequenceGenerator(name="NOTIFIED_RESULTS_SEQ", sequenceName="NOTIFIED_RESULTS_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="noticeDate", length=14)
	@ColumnInfo(name="생성일")
	private String noticeDate;
	
	@Column(name="title", length=200)
    @ColumnInfo(name="제목")
    private String title;
	
	@Column(name="contents", length=2000)
	@ColumnInfo(name="통보내용")
	private String contents;
	
	@Column(name="totalEmail")
	@ColumnInfo(name="총이메일건수")
	private Integer totalEmail;
	
	@Column(name="totalSms")
	@ColumnInfo(name="총SMS건수")
	private Integer totalSms;
	
	@Column(name="successEmail")
	@ColumnInfo(name="이메일성공건수")
    private Integer successEmail;
	
	@Column(name="successSms")
	@ColumnInfo(name="SMS성공건수")
	private Integer successSms;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="REPORT_ID")
    private Report report;
	
	@Column(name="REPORT_ID", nullable=true, updatable=false, insertable=false)
	private Integer reportId;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="report_result_id", referencedColumnName="id")
        })
    private List<ReportOperatorResult> reportOperatorResults;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="report_result_id", referencedColumnName="id")
        })
    private List<ReportFile> reportFiles;
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Integer getTotalEmail() {
        return totalEmail;
    }

    public void setTotalEmail(Integer totalEmail) {
        this.totalEmail = totalEmail;
    }

    public Integer getTotalSms() {
        return totalSms;
    }

    public void setTotalSms(Integer totalSms) {
        this.totalSms = totalSms;
    }

    public Integer getSuccessEmail() {
        return successEmail;
    }

    public void setSuccessEmail(Integer successEmail) {
        this.successEmail = successEmail;
    }

    public Integer getSuccessSms() {
        return successSms;
    }

    public void setSuccessSms(Integer successSms) {
        this.successSms = successSms;
    }

    @XmlTransient
    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    @XmlTransient
    public List<ReportOperatorResult> getReportOperatorResults() {
        return reportOperatorResults;
    }

    public void setReportOperatorResults(
            List<ReportOperatorResult> reportOperatorResults) {
        this.reportOperatorResults = reportOperatorResults;
    }

    @XmlTransient
    public List<ReportFile> getReportFiles() {
        return reportFiles;
    }

    public void setReportFiles(List<ReportFile> reportFiles) {
        this.reportFiles = reportFiles;
    }

    public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    @Override
	public String toString()
	{
	    return "NotifiedResults "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + ", title:'" + this.title
	        + ", noticeDate:'" + this.noticeDate
	        + ", contents:'" + this.contents
	        + ", totalEmail:'" + this.totalEmail
	        + ", totalSms:'" + this.totalSms
	        + ", successEmail:'" + this.successEmail
	        + ", successSms:'" + this.successSms
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
