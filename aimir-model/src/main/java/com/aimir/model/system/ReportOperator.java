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
 * Report를 수신받을 사용자 정보
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_OPERATOR")
public class ReportOperator extends BaseObject implements JSONString{

    private static final long serialVersionUID = 1793216353642589310L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_OPERATOR_SEQ")
    @SequenceGenerator(name="REPORT_OPERATOR_SEQ", sequenceName="REPORT_OPERATOR_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="isEmail")
	@ColumnInfo(name="이메일전송여부")
    private Boolean isEmail;
	
	@Column(name="isSms")
	@ColumnInfo(name="SMS전송여부")
	private Boolean isSms;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="OPERATOR_ID")
	private Operator operator;
	
	@Column(name="OPERATOR_ID", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

    public Boolean getIsEmail() {
        return isEmail;
    }

    public void setIsEmail(Boolean isEmail) {
        this.isEmail = isEmail;
    }

    public Boolean getIsSms() {
        return isSms;
    }

    public void setIsSms(Boolean isSms) {
        this.isSms = isSms;
    }

    @XmlTransient
    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    @Override
	public String toString()
	{
	    return "ReportOperator "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + ", isEmail:'" + this.isEmail
	        + ", isSms:'" + this.isSms
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
