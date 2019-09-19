/**
 * OperatorContract.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.model.integration;

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

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;


@Entity
//@Indexes({
//	@Index(name="IDX_WS_METERCONFIG_OBIS_01", columnNames={"WS_METERCONFIG_USER_ID", "OBIS_CODE", "CLASS_ID", "ATTRIBUTE_NO"}, unique = true),
//})
@Table(name="WS_METERCONFIG_OBIS")
public class WSMeterConfigOBIS  extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 1093034428694248354L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WS_METERCONFIG_OBIS_SEQ")
	@SequenceGenerator(name="WS_METERCONFIG_OBIS_SEQ", sequenceName="WS_METERCONFIG_OBIS_SEQ", allocationSize=1)	
	private Integer id;	    

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ws_meterconfig_user_id")
    private WSMeterConfigUser meterConfUser;
	
	@Column(name="ws_meterconfig_user_id", nullable=true, unique=false, updatable=false, insertable=false)
	private Integer meterConfUserId;
    	

	@Column(name="obis_code",length=255)
	@ColumnInfo(name="", descr="")
	private String obisCode;
	
	@Column(name="class_id",length=255)
	@ColumnInfo(descr="")
	private String classId;
	
	@Column(name="attribute_no",length=255)
	@ColumnInfo(descr="")
	private String attributeNo;

	@Column(name="permission",length=255)
	@ColumnInfo(descr="")
	private String permission;

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJSONString() {
		JSONStringer js = null;

		try {
			js = new JSONStringer();
			js.object().key("meterConfUserId").value((this.meterConfUserId == null)? "":this.meterConfUserId)
			.key("obisCode").value((this.obisCode == null)? "":this.obisCode)
			.key("classId").value((this.classId == null)? "":this.classId)
			.key("attributeNo").value((this.attributeNo == null)? "":this.attributeNo)
			.key("permission").value((this.permission == null)? "":this.permission)
			.endObject();
		} catch (Exception e) {
			System.out.println(e);
		}
		return js.toString();
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
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

	public WSMeterConfigUser getMeterConfUser() {
		return meterConfUser;
	}

	public void setMeterConfUser(WSMeterConfigUser meterConfUser) {
		this.meterConfUser = meterConfUser;
	}

	public Integer getMeterConfUserId() {
		return meterConfUserId;
	}

	public void setMeterConfUserId(Integer meterConfUserId) {
		this.meterConfUserId = meterConfUserId;
	}
	

	public String getOBISCode() {
		return obisCode;
	}

	public void setOBISCode(String obisCode) {
		this.obisCode = obisCode;
	}
	
	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}
	
	public String getAttributeNo() {
		return attributeNo;
	}

	public void setAttributeNo(String attributeNo) {
		this.attributeNo = attributeNo;
	}
	
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}
	

	
}
	
