package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>plc modem signal-to-noise ratio</p>
 * 
 * PLC modem ratio 
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="SNR_LOG")
@Index(name="IDX_SNR_LOG_01", columnNames={"DEVICE_TYPE", "DEVICE_ID", "YYYYMMDD", "HHMMSS"})
public class SNRLog extends BaseObject {

	private static final long serialVersionUID = 3742831176414187373L;

	@EmbeddedId public SNRLogPk id;
	
	@Column(name="SNR")
	@ColumnInfo(name="snr(dB)")
	private Double snr;	
	
	@Column(name="DCU_ID")
	@ColumnInfo(name="dcu id(gateway id)")
	private String dcuid;
    
	public SNRLog(){
		id = new SNRLogPk();
	}

	public SNRLogPk getId() {
		return id;
	}
	public void setId(SNRLogPk id) {
		this.id = id;
	}
	public ModemType getDeviceType() {
		return id.getDeviceType();
	}
	public void setDeviceType(String deviceType) {
		id.setDeviceType(deviceType);
	}
	public String getDeviceId() {
		return id.getDeviceId();
	}
	public void setDeviceId(String deviceId) {
		id.setDeviceId(deviceId);
	}
	public String getYyyymmdd() {
		return id.getYyyymmdd();
	}
	public void setYyyymmdd(String yyyymmdd) {
		id.setYyyymmdd(yyyymmdd);
	}
	public String getHhmmss() {
		return id.getHhmmss();
	}
	public void setHhmmss(String hhmmss) {
		id.setHhmmss(hhmmss);
	}

    public Double getSnr() {
		return snr;
	}

	public void setSnr(Double snr) {
		this.snr = snr;
	}

	public String getDcuid() {
		return dcuid;
	}

	public void setDcuid(String dcuid) {
		this.dcuid = dcuid;
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

}