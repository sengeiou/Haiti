package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.model.BaseObject;

/**
 * 가나 ECG 빌링 생성 및 결과 정보
 * @author elevas
 *
 */
@Entity
@Table(name = "ECGBillingIntegration")
public class ECGBillingIntegration extends BaseObject implements JSONString{
	
	private static final long serialVersionUID = -6594382001300312223L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ECGBillingIntegration_SEQ")
    @SequenceGenerator(name="ECGBillingIntegration_SEQ", sequenceName="ECGBillingIntegration_SEQ", allocationSize=1) 
	private Integer batchNo;
	
	/**
	 * Scheduled Meter Reading Date
	 * <br>Bill Date
	 */
	@Column(name="meterReadingDate")
	private String meterReadingDate;

	/**
	 * Total meter count
	 */
	@Column(name="totalMeterCount")
	private Integer totalMeterCount;

	/**
	 * Total reading count
	 */
	@Column(name="totalReadingCount")
	private Integer totalReadingCount;

	/**
	 * 생성 일시
	 */
	@Column(name="writeDate")
	private String writeDate;
	
	/**
	 * 파일 전송 여부
	 */
	@Column(name="sendResult")
	private Boolean sendResult;
	
	/**
	 * 파일명
	 * @return
	 */
	@Column(name="fileName")
	private String fileName;
	
    public Integer getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(Integer batchNo) {
        this.batchNo = batchNo;
    }

    public String getMeterReadingDate() {
        return meterReadingDate;
    }

    public void setMeterReadingDate(String meterReadingDate) {
        this.meterReadingDate = meterReadingDate;
    }

    public Integer getTotalMeterCount() {
        return totalMeterCount;
    }

    public void setTotalMeterCount(Integer totalMeterCount) {
        this.totalMeterCount = totalMeterCount;
    }

    public Integer getTotalReadingCount() {
        return totalReadingCount;
    }

    public void setTotalReadingCount(Integer totalReadingCount) {
        this.totalReadingCount = totalReadingCount;
    }

    public String getWriteDate() {
        return writeDate;
    }

    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
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
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
