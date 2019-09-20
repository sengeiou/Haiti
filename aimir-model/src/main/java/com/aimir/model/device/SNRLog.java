package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
//@Index(name="IDX_SNR_LOG_01", columnNames={"DEVICE_TYPE", "DEVICE_ID", "YYYYMMDD", "HHMMSS"})
public class SNRLog extends BaseObject {

    private static final long serialVersionUID = 3742831176414187373L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SNR_LOG_SEQ")
    @SequenceGenerator(name="SNR_LOG_SEQ", sequenceName="SNR_LOG_SEQ", allocationSize=1)
    private Long id;

    @Column(name="device_type")
    @ColumnInfo(name="장비유형 PLCIU")
    @Enumerated(EnumType.STRING)
    private ModemType deviceType;

    @Column(name="device_id", length=20, nullable=false)
    @ColumnInfo(name="장비아이디, 비지니스 키가됨")
    private String deviceId;

    @Column(name="YYYYMMDD", length=8, nullable=false)
    private String yyyymmdd;

    @Column(name="HHMMSS", length=6, nullable=false)
    private String hhmmss;

    @Column(name="SNR")
    @ColumnInfo(name="snr(dB)")
    private Double snr;

    @Column(name="DCU_ID")
    @ColumnInfo(name="dcu id(gateway id)")
    private String dcuid;

    public SNRLog(){
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public ModemType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(ModemType deviceType) {
        this.deviceType = deviceType;
    }

    public void setDeviceType(String deviceType) {
        setDeviceType(ModemType.valueOf(deviceType));
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }

    public String getHhmmss() {
        return hhmmss;
    }

    public void setHhmmss(String hhmmss) {
        this.hhmmss = hhmmss;
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