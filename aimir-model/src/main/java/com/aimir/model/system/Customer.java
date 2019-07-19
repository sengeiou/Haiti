package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ReferencedBy;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 에너지 사용자 (Energy Consumer) 의 고객 정보 
 *  AIMIR SYSTEM에 접속하는 고객 로그인 정보및 개인 식별 정보(puc Number, 주민번호, 아이핀) 등의 고유 정보를 가진다.
 *  고객주소 및 모바일번호, 연락처 등의 정보와 이메일 수신, SMS수신, Demand Response의 동의 여부에 대한 정보를 가진다.
 *
 * 에너지 사용자가 여러개의 에너지원을 가질수 있다. 이 관계는 Customer와 Contract의 관계로 존재한다.
 *  
 * BEMS/FEMS는 사용하지 않는다.  
 * 빌딩/공장 자체가 고객이면서 공급사이다. 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="CUSTOMER")
public class Customer extends User implements JSONString{

	private static final long serialVersionUID = 5237626783859853929L;

	@Column(unique=true, length=100, nullable=false)
	private String customerNo;	//주민번호 혹은 식별 아이다 pucNumber 암호화 필요
	
	private String mobileNo;
	
	// Land Line for South Africa
	private String telephoneNo;

	//지번, 우편번호, 주소1, 주소2 address 항목이 많은 이유 (zipcode?)
	// Postal Code&Suburb for Soth Africa
	private String address;		
	
	// Erf number for South Africa
	private String address3;
	
	private String familyCnt;	//가족수
	private String co2MileId;	//타사이트 로그인ID
	private Boolean demandResponse;	// DR고객 여부
	
	/**
	 * @deprecated This attribute is moved to Contract
	 */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "customtype_id")
	@ReferencedBy(name="code")
	@Deprecated
	private Code customTypeCode;			//SIC 산업분류코드
	
	@Deprecated
	@Column(name="customtype_id", nullable=true, updatable=false, insertable=false)
	private Integer customTypeCodeId;
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="customer_id")
	private Set<Contract> contracts = new HashSet<Contract>();
	
	// Add the below fields for South Africa - Start 2012/04/03
	private String identityOrCompanyRegNo;
	private String initials;
	private String vatNo;
	private String workTelephone;
	private String postalAddressLine1;
	private String postalAddressLine2;
	private String postalSuburb;
	private String postalCode;
	// Add the above fields for South Africa - End 2012/04/03

	public String getIdentityOrCompanyRegNo() {
		return identityOrCompanyRegNo;
	}

	public void setIdentityOrCompanyRegNo(String identityOrCompanyRegNo) {
		this.identityOrCompanyRegNo = identityOrCompanyRegNo;
	}

	public String getInitials() {
		return initials;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public String getVatNo() {
		return vatNo;
	}

	public void setVatNo(String vatNo) {
		this.vatNo = vatNo;
	}

	public String getWorkTelephone() {
		return workTelephone;
	}

	public void setWorkTelephone(String workTelephone) {
		this.workTelephone = workTelephone;
	}

	public String getPostalAddressLine1() {
		return postalAddressLine1;
	}

	public void setPostalAddressLine1(String postalAddressLine1) {
		this.postalAddressLine1 = postalAddressLine1;
	}

	public String getPostalAddressLine2() {
		return postalAddressLine2;
	}

	public void setPostalAddressLine2(String postalAddressLine2) {
		this.postalAddressLine2 = postalAddressLine2;
	}

	public String getPostalSuburb() {
		return postalSuburb;
	}

	public void setPostalSuburb(String postalSuburb) {
		this.postalSuburb = postalSuburb;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public Customer() {
	}
	
	public Customer(int id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String getCustomerNo() {
		return customerNo;
	}
	public void setCustomerNo(String customerNo) {
		this.customerNo = customerNo;
	}

	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getTelephoneNo() {
		return telephoneNo;
	}
	public void setTelephoneNo(String telephoneNo) {
		this.telephoneNo = telephoneNo;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getFamilyCnt() {
		return familyCnt;
	}
	public void setFamilyCnt(String familyCnt) {
		this.familyCnt = familyCnt;
	}

	public String getCo2MileId() {
		return co2MileId;
	}
	public void setCo2MileId(String co2MileId) {
		this.co2MileId = co2MileId;
	}

	@XmlTransient
	@Deprecated
	public Code getCustomTypeCode() {
		return customTypeCode;
	}

	@Deprecated
	public void setCustomTypeCode(Code customTypeCode) {
		this.customTypeCode = customTypeCode;
	}

	@XmlTransient
	public Set<Contract> getContracts() {
		return contracts;
	}
	public void setContracts(Set<Contract> contracts) {
		this.contracts = contracts;
	}

	public Boolean getDemandResponse() {
		return demandResponse;
	}

	public void setDemandResponse(Boolean demandResponse) {
		this.demandResponse = demandResponse;
	}

	@Deprecated
	public Integer getCustomTypeCodeId() {
        return customTypeCodeId;
    }

	@Deprecated
    public void setCustomTypeCodeId(Integer customTypeCodeId) {
        this.customTypeCodeId = customTypeCodeId;
    }

	@Override
	public String toString()
	{
	    return "Customer "+toJSONString();
	}
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',customerNo:'" + this.customerNo 
	        + "',name:'" + this.name 
	        + "',loginId:'" + this.loginId 
	        + "',address:'" + this.address  
	        + "',address1:'" + this.address1  
	        + "',address2:'" + this.address2  
	        + "',address3:'" + this.address3  
	        + "',familyCnt:'" + this.familyCnt  
	        + "',co2MileId:'" + this.co2MileId  
	        + "',telephoneNo:'" + this.telephoneNo
	        + "',mobileNo:'" + this.mobileNo
	        + "',email:'" + this.getEmail()  
	        + "',smsYn:'" + this.smsYn  
	        + "',emailYn:'" + this.emailYn
	        + "',demandResponse:'" + this.demandResponse
	        + "',customTypeCode:'" + ((this.customTypeCode == null)? "null":this.customTypeCode.getId())
	        + "',identityOrCompanyRegNo:'" + this.identityOrCompanyRegNo
	        + "',initials:'" + this.initials
	        + "',vatNo:'" + this.vatNo
	        + "',workTelephone:'" + this.workTelephone
	        + "',postalAddressLine1:'" + this.postalAddressLine1
	        + "',postalAddressLine2:'" + this.postalAddressLine2
	        + "',postalSuburb:'" + this.postalSuburb
	        + "',postalCode:'" + this.postalCode
	        + "',supplier:'" + ((this.supplier == null)? "null":this.supplier.getId())
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
