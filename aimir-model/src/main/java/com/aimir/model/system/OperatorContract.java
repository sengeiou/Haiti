/**
 * OperatorContract.java Copyright NuriTelecom Limited 2011
 */
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
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * OperatorContract.java Description 
 * 회원가입시 선택한 계약정보를 저장하는 테이블
 * 
 * Date          Version     Author   Description
 * 2011. 3. 23.   v1.0       eunmiae  신규작성       
 *
 */
@Entity
@Table(name = "OPERATOR_CONTRACT")
public class OperatorContract extends BaseObject implements JSONString  {

	static final long serialVersionUID = 490087347027305569L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OPERATOR_CONTRACT_SEQ")
	@SequenceGenerator(name = "OPERATOR_CONTRACT_SEQ", sequenceName = "OPERATOR_CONTRACT_SEQ", allocationSize = 1)
	/* Primary Key */
    private Integer id;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    @ReferencedBy(name = "loginId")
    /* Operator Id */
    private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @ReferencedBy(name = "contractNumber")
    /* Contract Id */
    private Contract contract;
    
    @Column(name="contract_id", nullable=true, updatable=false, insertable=false)
    private Integer contractId;
 
    @Column(name = "CUSTOMER_NUMBER")
    @ColumnInfo(name="고객번호", descr="고객 번호")    
    private String customerNumber;

	@Column(name = "CONTRACT_STATUS",columnDefinition= "char(1) default '1'")
    @ColumnInfo(name=" 계약번호의 상태", descr="유효(1), 삭제(0)")
    private Integer contractStatus;

	@Column(name = "WRITE_DATE", length=14)
    @ColumnInfo(name="회원정보 등록 날짜", descr="yyyymmddhhmmss")
	private String writeDate;

	@Column(name = "UPDATE_DATE", length=14)
    @ColumnInfo(name="회원정보 갱신 날짜", descr="yyyymmddhhmmss")
	private String updateDate;

	@Column(name = "FRIENDLYNAME", length=100)
    @ColumnInfo(name="별명", descr="별명")
	private String friendlyName;

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	@XmlTransient
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

    public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public Integer getContractStatus() {
		return contractStatus;
	}

	public void setContractStatus(Integer contractStatus) {
		this.contractStatus = contractStatus;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    /* 
	 * @see net.sf.json.JSONString#toJSONString()
	 */
	public String toJSONString() {
        JSONStringer jsonString = new JSONStringer();

        jsonString.object()
                          .key("id").value(this.id)
                          .key("operator").value(this.operator)
                          .key("contract").value(this.contract)
                          .key("contractStatus").value(this.contractStatus)
                          .key("customerNumber").value(this.customerNumber)
                          .key("writeDate").value(this.writeDate)
                          .key("friendlyName").value(this.friendlyName)
                          .key("updateDate").value(this.updateDate)
                  .endObject();

		return jsonString.toString();
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
		// TODO Auto-generated method stub
		return null;
	}
}
