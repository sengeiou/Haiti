package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 리포트 생성 후 사용자 결과
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name = "REPORT_OPERATOR_RESULT")
public class ReportOperatorResult extends BaseObject implements JSONString{

    private static final long serialVersionUID = -787640010409233655L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_OPERATOR_RESULT_SEQ")
    @SequenceGenerator(name="REPORT_OPERATOR_RESULT_SEQ", sequenceName="REPORT_OPERATOR_RESULT_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="isEmail")
	@ColumnInfo(name="이메일전송여부")
    private Boolean isEmail;
	
	@Column(name="isSms")
	@ColumnInfo(name="SMS전송여부")
	private Boolean isSms;
	
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

    @Override
	public String toString()
	{
	    return "ReportOperatorResult "+toJSONString();
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
