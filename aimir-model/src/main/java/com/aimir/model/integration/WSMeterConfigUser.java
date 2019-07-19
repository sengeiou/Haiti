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
@Indexes({
	@Index(name="IDX_WS_METERCONFIG_USER_01", columnNames={"USERID"}),
})
@Table(name="WS_METERCONFIG_USER")
public class WSMeterConfigUser  extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 6762197046322697441L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="WS_METERCONFIG_USER_SEQ")
	@SequenceGenerator(name="WS_METERCONFIG_USER_SEQ", sequenceName="WS_METERCONFIG_USER_SEQ", allocationSize=1)	
	private Integer id;	    
    
	@Column(name="userid",length=100, nullable=false, unique=true)
	@ColumnInfo(name="", descr="")
	private String userId;
	
	@Column(name="password",length=255)
	@ColumnInfo(descr="")
	private String password;
	
	@Column(name="description",length=255)
	@ColumnInfo(descr="")
	private String description;

	@Column(name = "write_date", length = 14)
	@ColumnInfo(name = "", descr = "")
	private String writeDate;

	@Column(name = "update_date", length = 14)
	@ColumnInfo(name = "", descr = "")
	private String updateDate;
	
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
			js.object().key("userId").value((this.userId == null)? "":this.userId)
			.key("description").value((this.description == null)? "":this.description)
			.key("writeDate").value((this.writeDate == null)? "":this.writeDate)
			.key("updateDate").value((this.updateDate == null)? "":this.updateDate)
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
	
	public Integer getId() {
		return id;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
}
	
