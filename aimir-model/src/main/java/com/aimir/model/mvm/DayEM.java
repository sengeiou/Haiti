package com.aimir.model.mvm;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;
/**
 * 전기 미터의 일별 데이터
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "DAY_EM")
//@Indexes({
//		@Index(name="IDX_DAY_EM_01", columnNames={"mdev_type", "mdev_id", "dst", "yyyymmdd", "channel"}) //DB정규화로 인해 해당 인덱스 불필요
//        })
public class DayEM extends MeteringDay {

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("mdevId : ").append(getMDevId()).append(", ");
		buffer.append("YYYYMMDD : ").append(getYyyymmdd()).append(", ");
		buffer.append("HH : ").append(getHh()).append(", ");
		buffer.append("channel : ").append(getChannel()).append(", ");
		buffer.append("mdevType : ").append(getMDevType()).append(", ");
		buffer.append("DST : ").append(getDst()).append(", ");
		buffer.append("ModemId : ").append(getModemId()).append(", ");
		buffer.append("DeviceID : ").append(getDeviceId()).append(", ");
		buffer.append("DeviceType : ").append(getDeviceType()).append(", ");
		buffer.append("BaseValue : ").append(getBaseValue()).append(", ");
		buffer.append("value : ").append(getValue()).append(", ");
		buffer.append("WriteDate : ").append(getWriteDate()).append(", ");
		buffer.append("Supplier_Id : ").append(getSupplierId()).append(", ");
		buffer.append("Ch_Method : ").append(getCh_method()).append(", ");
		buffer.append("Contract_ID : ").append(getContractId());
		
		return buffer.toString();
	}

}
