package com.aimir.model.prepayment;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * VerifyPrepaymentCustomerWS.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2012. 3. 8.   v1.0       enj       Log of "Verify Prepayment Customer" WebService  
 *
 */
@Entity
@Table(name = "VERIFY_PREPAYMENT_CUSTOMER_WS")
public class VerifyPrepaymentCustomerWS extends BaseObject implements JSONString {

	private static final long serialVersionUID = 2870215764049497358L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VERIFY_PREPAYCUSTOMER_WS_SEQ")
    @SequenceGenerator(name="VERIFY_PREPAYCUSTOMER_WS_SEQ", sequenceName="VERIFY_PREPAYCUSTOMER_WS_SEQ", allocationSize=1)
	private Integer id;

	@Column(name = "supplier_name", nullable=false)
	@ColumnInfo(name="Utility ID", descr="공급사 아이디")
	private String supplierName;
	
	@Column(name = "customer_number", nullable=false)
	@ColumnInfo(name="Customer Number", descr="고객 번호")	
	private String customerNumber;

	@Column(name = "transaction_id")
	@ColumnInfo(name="Transaction ID", descr="처리 아이디")
	private String transactionId;
	
	@Column(name = "write_date")
	@ColumnInfo(name="Write Date", descr="처리 일자")
	private String writeDate;
	
	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
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

	@Override
	public String toJSONString() {
		// TODO Auto-generated method stub
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    		           .key("supplierName").value(this.supplierName)
    				   .key("customerNumber").value(this.customerNumber).endObject();
    			
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
	}
}
