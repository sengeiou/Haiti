package com.aimir.model.view;

import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;
import com.aimir.util.DecimalUtil;
import com.aimir.util.TimeLocaleUtil;

import net.sf.json.JSONString;

public class MeteringDayView implements JSONString {
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(name="yyyymmdd",length=8,nullable=false)
	private String yyyymmdd;	
	
    @ColumnInfo(name="채널")
    private Integer channel;
    
    @Column(name="mdev_type",length=20)
    @Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
    
	@Column(columnDefinition="INTEGER default 0", length=2)
	@ColumnInfo(name="DST", descr="Summer Time ex ) +1 -1 +0")
	private Integer dst;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ColumnInfo(name="미터") 
	@ReferencedBy(name="mdsId")
	private Meter meter;
	
	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
	private Integer meterId;
	
	@ColumnInfo(name="검침타입 0:정기검침, 1:온디맨드, 2:실패검침 ")
	private Integer meteringType;	
	
	@ColumnInfo(name="meter id의 마지막 한자리")
	private Integer mdev_id_last;	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "modem_id")
    @ColumnInfo(name="모뎀번호")
    @ReferencedBy(name="deviceSerial")
	private Modem modem;
	
	@Column(name="modem_id", nullable=true, updatable=false, insertable=false)
	private Integer modemId;
	
	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;
	
	@Column(name = "device_type")
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;
	
	@Column(length=14)
	@ColumnInfo(name="데이터 작성시간")
	private String writeDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
    @ReferencedBy(name="contractNumber")
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="엔드 디바이스 ")
    @ReferencedBy(name="serialNumber")
	private EndDevice enddevice;
	
	@Column(name="enddevice_id", nullable=true, updatable=false, insertable=false)
	private Integer endDeviceId;
	
	@ColumnInfo(name="countValue", descr="LP에서 합쳐진 개수")
	private Integer c_value;
	
	@ColumnInfo(name="검침시작값")
	private Double baseValue;
	
	@ColumnInfo(name="일 총사용량")
	private Double total_value;
	
	@ColumnInfo(name="검침값", descr="00~01(h)")
	private Double value_00;	
	@ColumnInfo(name="검침값", descr="01~02(h)")
	private Double value_01;
	@ColumnInfo(name="검침값", descr="02~03(h)")
	private Double value_02;
	@ColumnInfo(name="검침값", descr="03~04(h)")
	private Double value_03;
	@ColumnInfo(name="검침값", descr="04~05(h)")
	private Double value_04;
	@ColumnInfo(name="검침값", descr="05~06(h)")
	private Double value_05;
	@ColumnInfo(name="검침값", descr="06~07(h)")
	private Double value_06;
	@ColumnInfo(name="검침값", descr="07~08(h)")
	private Double value_07;
	@ColumnInfo(name="검침값", descr="08~09(h)")
	private Double value_08;
	@ColumnInfo(name="검침값", descr="09~10(h)")
	private Double value_09;
	@ColumnInfo(name="검침값", descr="10~11(h)")
	private Double value_10;
	@ColumnInfo(name="검침값", descr="11~12(h)")
	private Double value_11;
	@ColumnInfo(name="검침값", descr="12~13(h)")
	private Double value_12;
	@ColumnInfo(name="검침값", descr="13~14(h)")
	private Double value_13;
	@ColumnInfo(name="검침값", descr="14~15(h)")
	private Double value_14;
	@ColumnInfo(name="검침값", descr="15~16(h)")
	private Double value_15;
	@ColumnInfo(name="검침값", descr="16~17(h)")
	private Double value_16;
	@ColumnInfo(name="검침값", descr="17~18(h)")
	private Double value_17;
	@ColumnInfo(name="검침값", descr="18~19(h)")
	private Double value_18;
	@ColumnInfo(name="검침값", descr="19~20(h)")
	private Double value_19;
	@ColumnInfo(name="검침값", descr="20~21(h)")
	private Double value_20;
	@ColumnInfo(name="검침값", descr="21~22(h)")
	private Double value_21;
	@ColumnInfo(name="검침값", descr="22~23(h)")
	private Double value_22;
	@ColumnInfo(name="검침값", descr="23~24(h)")
	private Double value_23;
	public String getMdevId() {
		return mdevId;
	}
	public void setMdevId(String mdevId) {
		this.mdevId = mdevId;
	}
	public String getYyyymmdd() {
		return yyyymmdd;
	}
	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public DeviceType getMdevType() {
		return mdevType;
	}
	public void setMdevType(DeviceType mdevType) {
		this.mdevType = mdevType;
	}
	public void setMdevType(String mdevType){
		this.mdevType = DeviceType.valueOf(mdevType);
	}
	public Integer getDst() {
		return dst;
	}
	public void setDst(Integer dst) {
		this.dst = dst;
	}
	
	@XmlTransient
	public Meter getMeter() {
		return meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}
	public Integer getMeterId() {
		return meterId;
	}
	public void setMeterId(Integer meterId) {
		this.meterId = meterId;
	}
	public Integer getMeteringType() {
		return meteringType;
	}
	public void setMeteringType(Integer meteringType) {
		this.meteringType = meteringType;
	}
	public Integer getMdev_id_last() {
		return mdev_id_last;
	}
	public void setMdev_id_last(Integer mdev_id_last) {
		this.mdev_id_last = mdev_id_last;
	}
	
	@XmlTransient
	public Modem getModem() {
		return modem;
	}
	public void setModem(Modem modem) {
		this.modem = modem;
	}
	public Integer getModemId() {
		return modemId;
	}
	public void setModemId(Integer modemId) {
		this.modemId = modemId;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public DeviceType getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	
	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	
	@XmlTransient
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	public Integer getContractId() {
		return contractId;
	}
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
	}
	
	@XmlTransient
	public EndDevice getEnddevice() {
		return enddevice;
	}
	public void setEnddevice(EndDevice enddevice) {
		this.enddevice = enddevice;
	}
	public Integer getEndDeviceId() {
		return endDeviceId;
	}
	public void setEndDeviceId(Integer endDeviceId) {
		this.endDeviceId = endDeviceId;
	}
	public Integer getC_value() {
		return c_value;
	}
	public void setC_value(Integer c_value) {
		this.c_value = c_value;
	}
	public Double getBaseValue() {
		return baseValue;
	}
	public void setBaseValue(Double baseValue) {
		this.baseValue = baseValue;
	}
	public Double getTotal() {
		return getTotal_value();
	} 
	public Double getTotal_value() {
		return total_value;
	}
	public void setTotal_value(Double total_value) {
		this.total_value = total_value;
	}
	public Double getValue_00() {
		return value_00;
	}
	public void setValue_00(Double value_00) {
		this.value_00 = value_00;
	}
	public Double getValue_01() {
		return value_01;
	}
	public void setValue_01(Double value_01) {
		this.value_01 = value_01;
	}
	public Double getValue_02() {
		return value_02;
	}
	public void setValue_02(Double value_02) {
		this.value_02 = value_02;
	}
	public Double getValue_03() {
		return value_03;
	}
	public void setValue_03(Double value_03) {
		this.value_03 = value_03;
	}
	public Double getValue_04() {
		return value_04;
	}
	public void setValue_04(Double value_04) {
		this.value_04 = value_04;
	}
	public Double getValue_05() {
		return value_05;
	}
	public void setValue_05(Double value_05) {
		this.value_05 = value_05;
	}
	public Double getValue_06() {
		return value_06;
	}
	public void setValue_06(Double value_06) {
		this.value_06 = value_06;
	}
	public Double getValue_07() {
		return value_07;
	}
	public void setValue_07(Double value_07) {
		this.value_07 = value_07;
	}
	public Double getValue_08() {
		return value_08;
	}
	public void setValue_08(Double value_08) {
		this.value_08 = value_08;
	}
	public Double getValue_09() {
		return value_09;
	}
	public void setValue_09(Double value_09) {
		this.value_09 = value_09;
	}
	public Double getValue_10() {
		return value_10;
	}
	public void setValue_10(Double value_10) {
		this.value_10 = value_10;
	}
	public Double getValue_11() {
		return value_11;
	}
	public void setValue_11(Double value_11) {
		this.value_11 = value_11;
	}
	public Double getValue_12() {
		return value_12;
	}
	public void setValue_12(Double value_12) {
		this.value_12 = value_12;
	}
	public Double getValue_13() {
		return value_13;
	}
	public void setValue_13(Double value_13) {
		this.value_13 = value_13;
	}
	public Double getValue_14() {
		return value_14;
	}
	public void setValue_14(Double value_14) {
		this.value_14 = value_14;
	}
	public Double getValue_15() {
		return value_15;
	}
	public void setValue_15(Double value_15) {
		this.value_15 = value_15;
	}
	public Double getValue_16() {
		return value_16;
	}
	public void setValue_16(Double value_16) {
		this.value_16 = value_16;
	}
	public Double getValue_17() {
		return value_17;
	}
	public void setValue_17(Double value_17) {
		this.value_17 = value_17;
	}
	public Double getValue_18() {
		return value_18;
	}
	public void setValue_18(Double value_18) {
		this.value_18 = value_18;
	}
	public Double getValue_19() {
		return value_19;
	}
	public void setValue_19(Double value_19) {
		this.value_19 = value_19;
	}
	public Double getValue_20() {
		return value_20;
	}
	public void setValue_20(Double value_20) {
		this.value_20 = value_20;
	}
	public Double getValue_21() {
		return value_21;
	}
	public void setValue_21(Double value_21) {
		this.value_21 = value_21;
	}
	public Double getValue_22() {
		return value_22;
	}
	public void setValue_22(Double value_22) {
		this.value_22 = value_22;
	}
	public Double getValue_23() {
		return value_23;
	}
	public void setValue_23(Double value_23) {
		this.value_23 = value_23;
	}

	@Override
    public String toJSONString() {
    	String meteringDate = (yyyymmdd == null) ? "" : getYyyymmdd();
    	DecimalFormat df = null;
    	if(supplier != null) {
    		String lang = supplier.getLang().getCode_2letter();
        	String country = supplier.getCountry().getCode_2letter();
        	if(lang != null && country != null) {
	        	if(meteringDate.length() > 0) {
	        		meteringDate = TimeLocaleUtil.getLocaleDate(meteringDate, lang, country);
	        	}
        	}
        	df = DecimalUtil.getDecimalFormat(supplier.getMd());
    	}
    	return "{"
	        + "'supplierId':'" + ((supplier == null) ? "" : supplier.getId()) 
	        + "','meter':" + ((meter == null) ? "{}" : meter.toJSONString())
	        + ",'contractId':'" + ((contractId == null) ? "" : contractId) 
	        + "','modemId':'" + ((modemId == null) ? "" : modemId)
	        + "','endDeviceId':'" + ((endDeviceId == null) ? "" : endDeviceId)
	        + ",'total':'" + ((total_value == null) ? "0" : ((df == null) ? total_value : df.format(total_value)) )
	        + "','meteringdate':'" + meteringDate
	        + "','baseValue':'" + ((baseValue == null) ? "0" : ((df == null) ? baseValue : df.format(baseValue)) )
	        + "'}";
    }    
}
