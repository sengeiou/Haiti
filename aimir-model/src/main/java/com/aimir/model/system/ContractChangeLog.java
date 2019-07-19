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
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * Energy Consumer(에너지 소비자)의 계약과 관련된 정보 변경 시 변경된 정보 타입 및 변경전, 변경 후 정보가 이력으로 기록된다.
 * Contract 클래스의 변경이력을 나타내는 모델 클래스이다.
 *  
 *  @author 강소이
 */
@Entity
@Table(name="CONTRACTCHANGELOG")
public class ContractChangeLog extends BaseObject implements JSONString{

	private static final long serialVersionUID = 1163873437479103729L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CONTRACTCHANGELOG_SEQ")
	@SequenceGenerator(name="CONTRACTCHANGELOG_SEQ", sequenceName="CONTRACTCHANGELOG_SEQ", allocationSize=1) 
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="customer_id", nullable=false)
	private Customer customer;
	
	@Column(name="customer_id", nullable=true, updatable=false, insertable=false)
	private Integer customerId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="contract_id", nullable=false)
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
	
	@Column(length=14)
	@ColumnInfo(descr="계약이 변경이 실제 적용되는 날짜")
	private String startDatetime;
	
	@Column(length=100, nullable=false)
	private String changeField;
	private String beforeValue;
	private String afterValue;
	
	//@Column(length=100, nullable=false)	
	//private Operator writer;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id")
	private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@Column(length=14, nullable=false)
	@ColumnInfo(descr="작성 날짜, 업데이트 한 서버시간")
	private String writeDatetime;
	private String descr;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@XmlTransient
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getStartDatetime() {
		return startDatetime;
	}

	public void setStartDatetime(String startDatetime) {
		this.startDatetime = startDatetime;
	}

	public String getChangeField() {
		return changeField;
	}

	public void setChangeField(String changeField) {
		this.changeField = changeField;
	}

	public String getBeforeValue() {
		return beforeValue;
	}

	public void setBeforeValue(String beforeValue) {
		this.beforeValue = beforeValue;
	}

	public String getAfterValue() {
		return afterValue;
	}

	public void setAfterValue(String afterValue) {
		this.afterValue = afterValue;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getWriteDatetime() {
		return writeDatetime;
	}

	public void setWriteDatetime(String writeDatetime) {
		this.writeDatetime = writeDatetime;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String toString()
	{
	    return "PrepaymentLog "+toJSONString();
	}
	
	public String toJSONString() {
		
		String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',customer:'" + ((this.customer == null)? "null" : this.customer.getId()) 
	        + "',contract:'" + ((this.contract == null)? "null" : this.contract.getId()) 
		    + "',startDatetime:'" + this.startDatetime
		    + "',changeField:'" + this.changeField
		    + "',beforeValue:'" + this.beforeValue
		    + "',afterValue:'" + this.afterValue
		    + "',operator:'" + ((this.operator == null)? "null" : this.operator.getId())
		    + "',writeDatetime:'" + this.writeDatetime
		    + "',descr:'" + this.descr
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
