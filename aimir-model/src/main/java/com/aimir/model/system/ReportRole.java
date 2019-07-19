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
 * 로그인한 사용자의 롤에 포함된 리포트를 가져와야 함 
 *
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_ROLE")
public class ReportRole extends BaseObject implements JSONString{

	private static final long serialVersionUID = -5568580053327219840L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_ROLE_SEQ")
    @SequenceGenerator(name="REPORT_ROLE_SEQ", sequenceName="REPORT_ROLE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="REPORT_ID")
    private Report report;
	
	@Column(name="REPORT_ID", nullable=true, updatable=false, insertable=false)
	private Integer reportId;
    
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ROLE_ID")
	private Role role;	
	
	@Column(name="ROLE_ID", nullable=true, updatable=false, insertable=false)
	private Integer roleId;

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

	@XmlTransient
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Integer getReportId() {
        return reportId;
    }

    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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
	        + ", role:'" + ((this.role == null)? "":this.role.getName())
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public String toJSONString() {
	    return "ReportRole "+toJSONString();
	}

}
