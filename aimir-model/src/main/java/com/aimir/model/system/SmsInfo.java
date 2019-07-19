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

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * @fileName SmsInfo.java
 * SMS를 보내고 난 후의 정보를 저장 (본래 Contract 테이블의 notification 정보를 저장하기 위함)
 * @author jiae
 */
@Entity
public class SmsInfo extends BaseObject{
    private static final long serialVersionUID = -4348862749074118175L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SMSINFO_SEQ")
    @SequenceGenerator(name="SMSINFO_SEQ", sequenceName="SMSINFO_SEQ", allocationSize=1)
    private Integer id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
    @ReferencedBy(name="contractNumber")
    private Contract contract;
    
    @Column(name="contract_id", nullable=false, updatable=false, insertable=false)
    private Integer contractId;
    
    @Column(name="LAST_NOTIFICATION_DATE", length=14)
	@ColumnInfo(descr="마지막 잔액 통보 일자 (YYYYMMDDHHMMSS)")	
	private String lastNotificationDate;
    
	@Column(name="SMS_NUMBER")
	@ColumnInfo(descr="SMS를 보내고 리턴받은 messageId")
	private String smsNumber;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	public String getLastNotificationDate() {
		return lastNotificationDate;
	}
	public void setLastNotificationDate(String lastNotificationDate) {
		this.lastNotificationDate = lastNotificationDate;
	}
	public String getSmsNumber() {
		return smsNumber;
	}
	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}
	@Override
    public String toString()
    {
        return "SmsInfo " + toJSONString();
    }
    
    public String toJSONString() {

        String str = "";
        
        str = "{"
            + "id:'" + this.id
            + "', contractId:'" + this.contractId
            + "', lastNotificationDate'" + this.lastNotificationDate
            + "', smsNumber'" + this.smsNumber
            + "'}";
        
        return str;
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