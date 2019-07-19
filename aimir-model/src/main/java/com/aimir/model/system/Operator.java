/**
 * OperatorContract.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.audit.IAuditable;

/**
 * Operator.java Description 
 * 시스템 로그인을 위한 사용자 정보 
 * 
 * Date          Version     Author   Description
 * -              V1.0       강소이         신규작성
 * 2011. 3. 23.   v1.1       eunmiae  HEMS 회원정보관리를 위한 항목 추가       
 * 2011. 4. 14.   v1.2       eunmiae  pucNumber 길이 100-> 250으로 변경     
 *
 */
@Entity
public class Operator  extends User implements JSONString, IAuditable {

	private static final long serialVersionUID = 6284113740604163761L;
	
	@OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="operator_id")
    protected Set<Dashboard> dashboards = new HashSet<Dashboard>(0);
	
	@Column(name="DEPOSIT", columnDefinition="float default 0")
	@ColumnInfo(name="충전가능예치금", descr="가나 벤딩포인트의 예치금을 위한 필드")
	private Double deposit;
	
	@Column(name="CASH_POINT")
    @ColumnInfo(name="충전포인트", descr="가나 충전소의 포인트")
    private Integer cashPoint;

	@XmlTransient
    public Set<Dashboard> getDashboards() {
        return dashboards;
    }
    public void setDashboards(Set<Dashboard> dashboards) {
        this.dashboards = dashboards;
    }
    
    public Double getDeposit() {
        return deposit;
    }

    public void setDeposit(Double deposit) {
        this.deposit = deposit;
    }
    
	public Integer getCashPoint() {
        return cashPoint;
    }
	
    public void setCashPoint(Integer cashPoint) {
        this.cashPoint = cashPoint;
    }
    @Override
	public String toString()
	{
	    return "Operator "+toJSONString();
	}
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id  
	        + "',role:'" + ((this.role == null)? "null" : this.role.getId()) 
	        + "',loginId:'" + this.loginId 
	        + "',password:'" + this.password 
	        + "',name:'" + this.name 
	        + "',aliasName:'" + this.aliasName
	        + "',email:'" + this.email
	        + "',telNo:'" + this.telNo
	        + "',supplier:'" + ((this.supplier == null)? "null" : this.supplier.getId())
	        + "',showDefaultDashboard:'" + this.showDefaultDashboard
	        + "',lastPasswordChangeTime:'" + this.lastPasswordChangeTime
	        + "',lastLoginTime:'" + this.lastLoginTime
	        + "',loginDenied:'" + this.loginDenied
	        + "',deniedReason:'" + this.deniedReason
	        + "',failedLoginCount:'" + this.failedLoginCount
	        + "',locale:'" + this.locale
	        /* 2011. 3. 23 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD START eunmiae */
	        + "',pucNumber:'" + this.pucNumber
	        + "',operatorStatus:'" + this.operatorStatus
	        + "',zipCode:'" + this.zipCode
	        + "',address1:'" + this.address1
	        + "',address2:'" + this.address2
	        + "',mobileNumber:'" + this.mobileNumber
	        + "',smsYn:'" + this.smsYn
	        + "',emailYn:'" + this.emailYn
	        + "',writeDate:'" + this.writeDate
	        + "',updateDate:'" + this.updateDate
	        /* 2011. 3. 23 v1.1 HEMS 회원정보관리를 위한 항목 추가 ADD END eunmiae */
	        + "',location:'" + ((this.location == null)? "null" : this.location.getId())
	        + "',useLocation:'" + this.useLocation
	        + "',deposit:'" + this.deposit
	        + "',cashPoint:'" + this.cashPoint
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
    
    @Override
    public String getInstanceName() {
        return this.getName();
    }
}
	
