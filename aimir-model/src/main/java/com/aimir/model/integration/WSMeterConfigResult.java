package com.aimir.model.integration;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
import com.aimir.util.StringUtil;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 */
@Entity
@Table(name = "WS_METERCONFIG_RESULT")
public class WSMeterConfigResult extends BaseObject {

	private static final long serialVersionUID = 6867740005975984744L;

	@EmbeddedId public WSMeterConfigResultPk id;
    
    @Column(name="deviceId", length=20)
    private String deviceId;	
    
    @Column(name="COMMAND", length=256)
    @ColumnInfo(name="")
    private String command;
    
    @Column(length=500)
    private String resultValue;
    
    @Column(name="WRITE_DATE",length=14, nullable=false)
    private String writeDate;
    

	@Column(name="num",length=16,nullable=false)
	private Integer num;
	
    public WSMeterConfigResult() {
        id = new WSMeterConfigResultPk();
    }
    
    public void setTrId(String trId) {
        id.setTrId(trId);
    }
    
    public String getTrId() {
        return id.getTrId();
    }
    
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    
    public void setNum(Integer num) {
        this.num = num;
    }
    
    public Integer getNum() {
        return num;
    }
    
    public String getRequestDate() {
    	return id.getRequestDate();
    }

    public void setRequestDate(String requestDate) {
    	id.setRequestDate(requestDate);
    }
    public String getResultValue() {
        return resultValue;
    }

    public void setResultValue(String resultValue) {
        this.resultValue = resultValue;
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
        return "WsMeterconfigResult : " + toJSONString();
    }


    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "trid:'" + this.id.getTrId()
            + "', deviceId:'" + this.deviceId
            + "', num:'" + this.num
            + "', resultValue:'" + this.resultValue
            + "', command:'" + this.command
            + "'}");
        
        return str.toString();
    }
}
