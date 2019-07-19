package com.aimir.model.system;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 사용자가 생성한 리포트 생성 스케줄 정보
 *
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_SCHEDULE")
public class ReportSchedule extends BaseObject implements JSONString{

	private static final long serialVersionUID = -6702093265114195542L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_SCHEDULE_SEQ")
    @SequenceGenerator(name="REPORT_SCHEDULE_SEQ", sequenceName="REPORT_SCHEDULE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;
    
	@Column(name="NAME",length=100)
    private String name;
	
	@Column(name="WRITE_TIME",length=14)
	private String writeTime;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name = "REPORTSCHEDULE_ID")
	@ColumnInfo(name="관리구역")
	private List<ReportParameterData> parameterData;
    
	@Column(name="CRON_FORMAT",length=255)
	private String cronFormat;	
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OPERATOR_ID", nullable=false)
    @ColumnInfo(name="사용자정보")
	private Operator operator;
	
	@Column(name="OPERATOR_ID", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@Column(name="EMAIL",length=1024)
	private String email;	// email
	
	@Column(name="IS_USED")
	private Boolean used;
	
	@Column(name="EXPORT_FORMAT")
	private String exportFormat;	
	

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

	public String getWriteTime() {
		return writeTime;
	}

	public void setWriteTime(String writeTime) {
		this.writeTime = writeTime;
	}

	@XmlTransient
	public List<ReportParameterData> getParameterData() {
		return parameterData;
	}

	public void setParameterData(List<ReportParameterData> parameterData) {
		this.parameterData = parameterData;
	}

	public String getCronFormat() {
		return cronFormat;
	}

	public void setCronFormat(String cronFormat) {
		this.cronFormat = cronFormat;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    public String getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
	}

	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
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
	        + ", name:'" + ((this.name == null)? "":this.name)
	        + ", writeTime:" + ((this.writeTime == null)? "":this.writeTime)
	        + ", cronFormat:'" + ((this.cronFormat == null)? "":this.cronFormat)
	        + ", operator:'" + ((this.operator == null)? "":this.operator.getName())
	        + ", email:'" + ((this.email == null)? "":this.email)
	        + ", used:'" + ((this.used == null)? "false":this.used)
	        + ", exportFormat:'" + ((this.exportFormat == null)? "":this.exportFormat)
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public String toJSONString() {
	    return "ReportSchedule "+toJSONString();
	}

}
