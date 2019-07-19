/**
 * MeterCtrl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * MeterCtrl.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 11. 4.   v1.0       enj      Data Link App check database table   
 * </pre>
 */
@Entity
@Table(name="METERCTRL")
public class MeterCtrl extends BaseObject implements JSONString {

	private static final long serialVersionUID = 724980946603365810L;

	@EmbeddedId public MeterCtrlPk id;
	/* Command parameter1.
	If CtrlID is ”TMS”, Param1 will be  Date.(Data format is  ‘YYYYMMDDhhmmss’)
	
	If CtrlID is “PA”, Param1 will be add value to power prepaid. 
	(value = 0 - 99999999 )
	
	If CtrlID is “PS”, Param1 will be prepaid rate value.  ( 0 - 65535 )
	*/
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meter_id", updatable=false, insertable=false)
    @ColumnInfo(name="Meter ID") 
    @ReferencedBy(name="mdsId")
    private Meter meter;
	
	@ColumnInfo(name="Command parameter1")
	@Column(name="PARAM1",length=64)
	private String param1;
	
	@ColumnInfo(name="Command parameter2", descr="Not used.(Preliminary Items)")
	@Column(name="PARAM2",length=64)
	private String param2;
	
	@ColumnInfo(name="Command parameter3", descr="Not used.(Preliminary Items)")
	@Column(name="PARAM3",length=64)
	private String param3;
	
	@ColumnInfo(name="Command parameter4", descr="Not used.(Preliminary Items)")
	@Column(name="PARAM4",length=64)
	private String param4;
	
	@ColumnInfo(name="Command parameter5", descr="Not used.(Preliminary Items)")
	@Column(name="PARAM5",length=64)
	private String param5;
	
	/*Command result param 1
	If CtrlID is ”OD”, Result1 will be  ElectricEnergy(0 ? 99999999)
	
	If CtrlID is “RS”, Result1 will be RelayStatus.
	0 : Open (supply), 
	1 : Close(not supply) )
	
	If CtrlID is “RN” or “RF”, Result1 will be  Relay Command result.
	“00” : Success, 
	“01” : Could not control switch, 
	“02” : Switch charge power., 
	“99” : Invalid Setting.
	
	If CtrlID is “TMS”, Result1 will be Time Settings Command result.
	“00” : Success, 
	“01” : Failure, 
	
	If CtrlID is “SW”, Result1 will be SW version. 
	
	If CtrlID is “TS”, Result1 will be Get Tampering Status Command result(bypass result).
	“0” : Normal 
	“1” : Issue 
	
	If CtrlID is “TC”, Result1 will be Clear Tampering Status Command result.
	“00” : Success, 
	“01” : Failure, 
	
	If CtrlID is “PA”, Result1 will be Add the value to power prepaid Command result.
	“00” : Success, 
	“01” : Failure, 
	
	If CtrlID is “PS”, Result1 will be Set prepaid rate Command result.
	“00” : Success, 
	“01” : Failure, 
	 */
	@ColumnInfo(name="Command result param 0")
	@Column(name="RESULT1",length=64)
	private String result1;
	
	/*Command result param 2

	If CtrlID is ”OD”, Result2 will be  ElectricEnergy unit (“Wh”)
	
	If CtrlID is “TS”, Result2 will be Get Tampering Status Command result (earthload result).
	“0” : Normal 
	“1” : Issue 
	 */
	@ColumnInfo(name="Command result param 2")
	@Column(name="RESULT2",length=64)
	private String result2;
	
	/*
	Command result param 3
	
	If CtrlID is “TS”, Result3 will be Get Tampering Status Command result (reverse result).
	“0” : Normal 
	“1” : Issue
	 */
	@ColumnInfo(name="Command result param 3")
	@Column(name="RESULT3",length=64)
	private String result3;
	
	/*
	 * Command result param 4
	If CtrlID is “TS”, Result4 will be Get Tampering Status Command result (terminal cover status).
	“0” : Normal 
	“1” : Issue ( Terminal cover was opened ).
	 */
	@ColumnInfo(name="Command result param 4")
	@Column(name="RESULT4",length=64)
	private String result4;
	
	/*Command result param 5
	If CtrlID is “TS”, Result5 will be Get Tampering Status Command result (front cover status).
	“0” : Normal 
	“1” : Issue ( Front cover was opened )
	 */
	@ColumnInfo(name="Command result param 5")
	@Column(name="RESULT5",length=64)
	private String result5;	
	
    @ColumnInfo(name="Command Status", descr="-1:Error, 0:Initialize(Only registered), 1:Send command(AIMIR -> DCU), 2:Return values(DCU -> AIMIR)")
    @Column(name="STATUS", columnDefinition="INTEGER default 0")
    private Integer status;

    public MeterCtrl(){
        id = new MeterCtrlPk();
    }

    public String getCtrlId() {
		return id.getCtrlId();
	}

	public void setCtrlId(String ctrlId) {
		id.setCtrlId(ctrlId);
	}

	@XmlTransient
	public Meter getMeter() {
		return meter;
	}

	public void setMeter(Meter meter) {
	    id.setMeterId(meter.getId());
		this.meter = meter;
	}

	public String getWriteDate() {
		return id.getWriteDate();
	}

	public void setWriteDate(String writeDate) {
		id.setWriteDate(writeDate);
	}
	
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

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getMeterId() {
        return id.getMeterId();
    }

    public void setMeterId(Integer meterId) {
        id.setMeterId(meterId);
    }

    /* 
	 * @see net.sf.json.JSONString#toJSONString()
	 */
	public String toJSONString() {
        JSONStringer jsonString = new JSONStringer();
        jsonString.object()
//                          .key("ctrlId").value(id.ctrlId)
//                          .key("meter").value(this.meter)
                          .key("param1").value(this.param1)
                          .key("param2").value(this.param2)
                          .key("param3").value(this.param3)
                          .key("param4").value(this.param4)
                          .key("param5").value(this.param5)
                          .key("result1").value(this.result1)
                          .key("result2").value(this.result2)
                          .key("result3").value(this.result3)
                          .key("result4").value(this.result4)
                          .key("result5").value(this.result5)
                          .key("status").value(this.status)
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
		return "MeterCtrl "+toJSONString();
	}

}