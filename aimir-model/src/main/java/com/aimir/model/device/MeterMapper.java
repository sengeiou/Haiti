package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/*
 * CEB 전용으로 생성 및 사용
 * DCU/MODEM에서 읽는 OBIS 기반 mds_id와 미터에 적혀 있는 mds_id가 다르기 때문에
 * 일부 5,000개 미터에 대한 정보를 mapping 시켜줄 수 있는 테이블 생성
 */

@Entity
@Table(name = "METER_MAPPER")
public class MeterMapper extends BaseObject  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8099886595517516152L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_METER_MAPPER")
	@SequenceGenerator(name="SEQ_METER_MAPPER", sequenceName="SEQ_METER_MAPPER", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="modem_device_serial")
	@ColumnInfo(descr="미터에 연결된 모뎀 device serial")
	private String modemDeviceSerial;
	
	@Column(name="meter_printed_mdsId")
	@ColumnInfo(descr="미터명판에 인쇄된 meter serial")
	private String meterPrintedMdsId;
	
	@Column(name="meter_obis_mdsId")
	@ColumnInfo(descr="dcu/modem에서 obis을 통해 읽어들이는 meter serial")
	private String meterObisMdsId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getModemDeviceSerial() {
		return modemDeviceSerial;
	}

	public void setModemDeviceSerial(String modemDeviceSerial) {
		this.modemDeviceSerial = modemDeviceSerial;
	}

	public String getMeterPrintedMdsId() {
		return meterPrintedMdsId;
	}

	public void setMeterPrintedMdsId(String meterPrintedMdsId) {
		this.meterPrintedMdsId = meterPrintedMdsId;
	}

	public String getMeterObisMdsId() {
		return meterObisMdsId;
	}

	public void setMeterObisMdsId(String meterObisMdsId) {
		this.meterObisMdsId = meterObisMdsId;
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
	
}
