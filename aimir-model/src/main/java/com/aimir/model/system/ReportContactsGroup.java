package com.aimir.model.system;

import java.util.ArrayList;
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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;


import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 리포트 수신자들에 대한 그룹 정보
 * 리포트 수신자를 그룹화 하여 분류할 수 있게 하는 정보
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_CONTACTS_GROUP")
public class ReportContactsGroup extends BaseObject implements JSONString{

	private static final long serialVersionUID = -3705677131890391138L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_CONTACTS_GROUP_SEQ")
	@SequenceGenerator(name="REPORT_CONTACTS_GROUP_SEQ", sequenceName="REPORT_CONTACTS_GROUP_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;
    
	@Column(name="NAME",length=100,nullable=false)
    private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="OPERATOR_ID", nullable=false)
    @ColumnInfo(name="사용자정보")
	private Operator operator;
	
	@Column(name="OPERATOR_ID", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="GROUP_ID")
	@OrderBy("id")
	private List<ReportContacts> reportContacts = new ArrayList<ReportContacts>(0);

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

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	@XmlTransient
	public List<ReportContacts> getReportContacts() {
		return reportContacts;
	}

	public void setReportContacts(List<ReportContacts> reportContacts) {
		this.reportContacts = reportContacts;
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
	        + ", operator:'" + ((this.operator == null)? "":this.operator.getName())
	        + "'}";
	    
	    return retValue;
	}

	@Override
	public String toJSONString() {
	    return "ReportContactsGroup "+toJSONString();
	}

}
