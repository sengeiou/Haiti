package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BasePk;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * @author eunmiae
 *
 */
@Embeddable
public class MeterCtrlPk extends BasePk{

	private static final long serialVersionUID = -6803170807267943352L;
	
	/*
    Meter Control ID
	“OD” : OnDemand metering
	“RS” : Relay Ststus 
	“RN” : Relay On
	“RF” : Relay Off
	“TMS” : Time Settings
	“SW” : Get sw version
	“TS” : Get tampering status
	“TC” : Clear tampering status
	“PA” : Add the value to power prepaid 
	“PS” : Set prepaid rate
	 */
	@Column(name="CTRLID",length=8)
	@ColumnInfo(name="CtrlID", descr="Meter Control ID")
	private String ctrlId;
	
	@Column(name="meter_id", nullable=false)
    private Integer meterId;
	
	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	@Column(name="WRITE_DATE",length=14)
	private String writeDate;

	public String getCtrlId() {
		return ctrlId;
	}

	public void setCtrlId(String ctrlId) {
		this.ctrlId = ctrlId;
	}
	
	public void setMeterId(Integer meterId) {
	    this.meterId = meterId;
	}
	
	public Integer getMeterId() {
	    return this.meterId;
	}
}
