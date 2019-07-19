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
 * 리포트별 수신받을 목록 (사용자별 주소록) 
 *
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_CONTACTS")
public class ReportContacts extends BaseObject implements JSONString{
	
	private static final long serialVersionUID = 4733964579723265355L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_CONTACTS_SEQ")
    @SequenceGenerator(name="REPORT_CONTACTS_SEQ", sequenceName="REPORT_CONTACTS_SEQ", allocationSize=1) 

    @ColumnInfo(name="PK", descr="PK")
	private Integer id;
    
	@Column(name="NAME",length=100, nullable=false)
    private String name;
    
	@Column(name="EMAIL",length=50)
	private String email;	// email
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OPERATOR_ID", nullable=false)
    @ColumnInfo(name="사용자정보")
	private Operator operator;
	
	@Column(name="OPERATOR_ID", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="GROUP_ID")
    private ReportContactsGroup group;
	
	@Column(name="GROUP_ID", nullable=true, updatable=false, insertable=false)
	private Integer reportContactsGroupId;
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	@XmlTransient
	public ReportContactsGroup getGroup() {
		return group;
	}

	public void setGroup(ReportContactsGroup group) {
		this.group = group;
	}

	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getReportContactsGroupId() {
        return reportContactsGroupId;
    }

    public void setReportContactsGroupId(Integer reportContactsGroupId) {
        this.reportContactsGroupId = reportContactsGroupId;
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
	        + ", email:'" + ((this.email == null)? "":this.email)
	        + ", operator:'" + ((this.operator == null)? "":this.operator.getName())
	        + ", group:'" + ((this.group == null)? "":this.group.getName())
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public String toJSONString() {
	    return "ReportContacts "+toJSONString();
	}

}
