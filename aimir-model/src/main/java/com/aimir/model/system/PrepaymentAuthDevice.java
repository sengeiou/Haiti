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

import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * PrepaymentAuthDevice.java Description 
 *
 * 
 * Date          Version    Author   Description
 * 2011. 8. 9.   v1.0       eunmiae  선불고객의 Emergency Credit Mode전환 인증 디바이스 정보 관리       
 *
 */
@Entity
@Table(name = "PREPAYMENT_AUTH_DEVICE")
public class PrepaymentAuthDevice  extends BaseObject{

	static final long serialVersionUID = -6848689678057273926L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PREPAYMENT_AUTH_DEVICE_SEQ")
	@SequenceGenerator(name="PREPAYMENT_AUTH_DEVICE_SEQ", sequenceName="PREPAYMENT_AUTH_DEVICE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;	//	ID(PK)
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="contract_id")
    @ColumnInfo(name="계약정보")
    private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;

	@Column(nullable=false)
	@ColumnInfo(name="인증키", descr="모바일 또는 PC의 맥어드레스 등")
	private String authKey;	// business key
	
	@ColumnInfo(name="별명", descr="인증 디바이스의 별명")	
	private String friendlyName;
	
	@Column(name="WRITE_DATE",length=14,nullable=false)
	private String writeDate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    @Override
	public String toString()
	{
        JSONStringer jsonString = new JSONStringer();

        jsonString.object()
                          .key("id").value(this.id)
                          .key("authKey").value(this.authKey)
                          .key("friendlyName").value(this.friendlyName)
                          .key("writeDate").value(this.writeDate)
                          .key("contract").value((this.getContract() == null) ? "null" : contract.getId())
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
}
