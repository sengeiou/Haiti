package com.aimir.model.integration;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * 
 * @author 
 */
@Entity
@Table(name = "WS_METERCONFIG_LOG")
public class WSMeterConfigLog extends BaseObject {

	private static final long serialVersionUID = -4001922865648737811L;

	@EmbeddedId public WSMeterConfigLogPk id;	

	@Column(name="deviceId", length=20 )
	private String deviceId;

    @Column(length=20)
    @ColumnInfo(name="")
    private String modemId;
    
    @Column(length=20)
    @ColumnInfo(name="")
    private String deviceType;
    
    @ColumnInfo(name="", descr="sensor type (code:zru, zeupls, mmiu, ieiu, zeupls, zmu, ihd, acd, hmu)")
    @Enumerated(EnumType.STRING)
    @Column(name="MODEM_TYPE",length=32)
    private ModemType modemType;   
    
    @ColumnInfo(name="", descr="protocol type (CDMA,GSM,GPRS,PSTN,LAN,ZigBee,WiMAX,Serial,PLC,Bluetooth,SMS)")
    @Enumerated(EnumType.STRING)
    @Column(name="PROTOCOL_TYPE",length=32)
    private Protocol protocolType;
    
    @Column(name="ERRORCODE")
    @ColumnInfo(name="에러코드")
    private Integer errorCode;
    
    @Column(name="ATTRIBUTE_NO", length=256)
	@ColumnInfo(name="", descr="")
    private String attributeNo;
    
    @Column(name="CLASS_ID", length=256)
    @ColumnInfo(name="")
    private String classId;
    
    @Column(name="OBIS_CODE", length=256)
    @ColumnInfo(name="")
    private String obisCode;
    
    @Column(length=100)
    @ColumnInfo(name="")
    private String operator;
    
    @Column(name="STATE")
    @ColumnInfo(name="")
    private Integer state;
    
    @Column(length=256)
    @ColumnInfo(name="")
    private String command;
   
    
    @Column(name="WRITE_DATE",length=14, nullable=false)
    @ColumnInfo(name="")
    private String writeDate;
    
    
    @Column(name="UPDATE_DATE",length=14)
    @ColumnInfo(name="")
    private String updateDate;
    
    
    @Column(name="DESCRIPTION",length=256)
    @ColumnInfo(name="")
    private String description;
    
    
    @Column(name="parameter" ,length=500)
    @ColumnInfo(name="")
    private String parameter;
    

//    @OneToMany(fetch=FetchType.LAZY)
//    @JoinColumns({
//        @JoinColumn(name="trId", referencedColumnName="trId"),
//        @JoinColumn(name="deviceId", referencedColumnName="deviceId")
//        })
//    private List<WSMeterconfigResult> results;
    
    public WSMeterConfigLog() {
        id = new WSMeterConfigLogPk();
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }



    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }



    public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getAttributeNo() {
		return attributeNo;
	}

	public void setAttributeNo(String attributeNo) {
		this.attributeNo = attributeNo;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getObisCode() {
		return obisCode;
	}

	public void setObisCode(String obisCode) {
		this.obisCode = obisCode;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

    public void setTrId(String trId) {
        id.setTrId(trId);
    }
    
    public String getTrId() {
        return id.getTrId();
    }
    
    public String getRequestDate() {
    	return id.getRequestDate();
    }

    public void setRequestDate(String requestDate) {
    	id.setRequestDate(requestDate);
    }
//	@XmlTransient
//    public List<WSMeterconfigResult> getResults() {
//        return results;
//    }
//
//    public void setResults(List<WSMeterconfigResult> results) {
//        this.results = results;
//    }

    public String getModemId() {
		return modemId;
	}

	public void setModemId(String modemId) {
		this.modemId = modemId;
	}

	public ModemType getModemType() {
		return modemType;
	}

	public void setModemType(ModemType modemType) {
		this.modemType = modemType;
	}

	public Protocol getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(Protocol protocolType) {
		this.protocolType = protocolType;
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
        return "WsMeterconfigLog " + toJSONString();
    }

    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "trid:'" + this.id.getTrId()
            + "', deviceId:'" + this.deviceId
            + "', deviceType:'" + this.deviceType
            + "', attributeNo:" + this.attributeNo
            + "', classId:"  + this.classId
            + "', obisCode:" + this.obisCode
            + "', command:'" + this.command
            + "', state:'" + this.state
            + "', errorCode:'" + this.errorCode
            + "', operator:'" + this.operator
            + "', requestDate:'" + this.id.getRequestDate()
            + "', writeDate:'" + this.writeDate
            + "', updateDate:'" + this.updateDate
            + "'}");
        
        return str.toString();
    }
}
