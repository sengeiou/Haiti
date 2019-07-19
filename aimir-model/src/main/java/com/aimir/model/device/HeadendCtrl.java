package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;

/**
 * HeadendCtrl.java Description 
 * 
 * Date          Version     Author   			Description
 * 2012. 08. 13  v1.0        김지애(jiae)         모델 생성
 *
 */

@Entity
@Table(name="MDIS_HEADEND_CTRL")
public class HeadendCtrl {
	@EmbeddedId public HeadendPK id;
	
	@Column(name="PARAM1",length=64)
	@ColumnInfo(descr="Command parameter1(If CtrlID is 'ST', Param1 is timeout(s))")
	private String param1;
	
	@Column(name="PARAM2",length=64)
	@ColumnInfo(descr="Command parameter2(If CtrlID is 'ST', Param2 is retry count)")
	private String param2;
	
	@Column(name="PARAM3",length=64)
	@ColumnInfo(descr="Command parameter3")
	private String param3;
	
	@Column(name="PARAM4",length=64)
	@ColumnInfo(descr="Command parameter4")
	private String param4;
	
	@Column(name="PARAM5",length=64)
	@ColumnInfo(descr="Command parameter5")
	private String param5;
	
	@Column(name="RESULT1",length=64)
	@ColumnInfo(descr="Command result param1(If CtrlId is 'ST', Result is common result/ 0:Normal, 1:issue)")
	private String result1;
	
	@Column(name="RESULT2",length=64)
	@ColumnInfo(descr="Command result param2")
	private String result2;
	
	@Column(name="RESULT3",length=64)
	@ColumnInfo(descr="Command result param3")
	private String result3;
	
	@Column(name="RESULT4",length=64)
	@ColumnInfo(descr="Command result param4")
	private String result4;
	
	@Column(name="RESULT5",length=64)
	@ColumnInfo(descr="Command result param5")
	private String result5;
	
	@Column(name="STATUS")
	@ColumnInfo(descr="CommandStatus(-1:Error, 0:Initialize(Only registered), 1:Send command(Aimir->Headend), 2:Return values(Headend->Aimir))")
	private int status;
	
	public String getParam1() {
		return param1;
	}
	public void setParam1(String param1) {
		this.param1 = param1;
	}
	public String getParam2() {
		return param2;
	}
	public void setParam2(String param2) {
		this.param2 = param2;
	}
	public String getParam3() {
		return param3;
	}
	public void setParam3(String param3) {
		this.param3 = param3;
	}
	public String getParam4() {
		return param4;
	}
	public void setParam4(String param4) {
		this.param4 = param4;
	}
	public String getParam5() {
		return param5;
	}
	public void setParam5(String param5) {
		this.param5 = param5;
	}
	public String getResult1() {
		return result1;
	}
	public void setResult1(String result1) {
		this.result1 = result1;
	}
	public String getResult2() {
		return result2;
	}
	public void setResult2(String result2) {
		this.result2 = result2;
	}
	public String getResult3() {
		return result3;
	}
	public void setResult3(String result3) {
		this.result3 = result3;
	}
	public String getResult4() {
		return result4;
	}
	public void setResult4(String result4) {
		this.result4 = result4;
	}
	public String getResult5() {
		return result5;
	}
	public void setResult5(String result5) {
		this.result5 = result5;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public HeadendCtrl() {
		id = new HeadendPK();
	}
	public void setId(HeadendPK id) {
		this.id = id;
	}
	
	
}