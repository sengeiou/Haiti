package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.BaseObject;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * PowerQualityStatus
 * 미터에 설정된 전력품질 임계치를 벗어나는 경우 기록하는 상태정보를 저장한다.
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "POWER_QUALITY_STATUS")
public class PowerQualityStatus  extends BaseObject implements JSONString  {

	private static final long serialVersionUID = -901087925672522754L;

	@EmbeddedId public PowerQualityStatusPk id;

	@Column(name="yyyymmdd",length=8,nullable=false)
	@ColumnInfo(descr="해당 시점의 미터시간(날짜)")
	private String yyyymmdd;
	@Column(name="hhmmss",length=6, nullable=false)
	@ColumnInfo(descr="해당 시점의 미터시간")
	private String hhmmss;
	
	@Column(name="writeDate",length=14,nullable=false)
	@ColumnInfo(descr="작성일 (서버시간)")
	private String writeDate;		
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ColumnInfo(name="미터") 
	@ReferencedBy(name="mdsId")
	private Meter meter;
	
	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
	private Integer meterId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contract_id")
    @ColumnInfo(name="계약")
    @ReferencedBy(name="contractNumber")
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "modem_id")
    @ColumnInfo(name="모뎀번호")
    @ReferencedBy(name="deviceSerial")
	private Modem modem;
	
	@Column(name="modem_id", nullable=true, updatable=false, insertable=false)
	private Integer modemId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "enddevice_id")
    @ColumnInfo(name="엔드 디바이스 ")
    @ReferencedBy(name="serialNumber")
	private EndDevice enddevice;
	
	@Column(name="enddevice_id", nullable=true, updatable=false, insertable=false)
	private Integer endDeviceId;

	@Column(name="device_id",length=20)
	@ColumnInfo(name="통신장비 아이디", descr="집중기아이디 혹은 모뎀아이디 수검침일경우 장비 아이디 없기 때문에 널 허용")
	private String deviceId;

	@Column(name = "device_type")
	@Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비타입", descr="집중기, 모뎀")
	private DeviceType deviceType;
    
	@ColumnInfo(descr="voltage sag count 누적 카운트 의미")
	private Integer vol_a_sag_cnt;
	@ColumnInfo(descr="duration 타입은 sec 단위까지 기록")
	private Long vol_a_sag_dur;

	private Integer vol_b_sag_cnt;
	private Long vol_b_sag_dur;
	private Integer vol_c_sag_cnt;
	private Long vol_c_sag_dur;
	private Integer vol_sag_cnt;
	private Long vol_sag_dur;
	@ColumnInfo(descr="voltage sag 의 진행상태 (in progress(true), out progress(false)")
	private Boolean vol_sag_ing;
	private Integer vol_a_swell_cnt;
	private Long vol_a_swell_dur;
	private Integer vol_b_swell_cnt;
	private Long vol_b_swell_dur;
	private Integer vol_c_swell_cnt;
	private Long vol_c_swell_dur;
	private Integer vol_swell_cnt;
	private Long vol_swell_dur;
	private Boolean vol_swell_ing;
	private Integer vol_cut_cnt;
	private Long vol_cut_dur;
	private Boolean vol_cut_ing;
	private Integer vol_flicker_cnt;
	private Long vol_flicker_dur;
	private Boolean vol_flicker_ing;
	private Integer vol_fluctuation_cnt;
	private Long vol_fluctuation_dur;
	private Boolean vol_fluctuation_ing;
	private Integer low_vol_cnt;
	private Long low_vol_dur;
	private Boolean low_vol_ing;
	private Integer high_vol_cnt;
	private Long high_vol_dur;
	private Boolean high_vol_ing;
	private Integer high_frequency_cnt;
	private Long high_frequency_dur;
	private Boolean high_frequency_ing;
	private Integer polarity_cross_phase_cnt;
	private Long polarity_cross_phase_dur;
	private Boolean polarity_cross_phase_ing;
	private Integer reverse_pwr_cnt;
	private Long reverse_pwr_dur;
	private Boolean reverse_pwr_ing;
	private Integer low_curr_cnt;
	private Long low_curr_dur;
	private Boolean low_curr_ing;
	private Integer over_curr_cnt;
	private Long over_curr_dur;
	private Boolean over_curr_ing;
	private Integer pfactor_cnt;
	private Long pfactor_dur;
	private Boolean pfactor_ing;
	private Integer harmonic_cnt;
	private Long harmonic_dur;
	private Boolean harmonic_ing;
	private Integer thd_curr_cnt;
	private Long thd_curr_dur;
	private Boolean thd_curr_ing;
	private Integer thd_vol_cnt;
	private Long thd_vol_dur;
	private Boolean thd_vol_ing;
	private Integer tdd_cnt;
	private Long tdd_dur;
	private Boolean tdd_ing;
	private Integer distortion_a_cnt;
	private Long distortion_a_dur;
	private Integer distortion_b_cnt;
	private Long distortion_b_dur;
	private Integer distortion_c_cnt;
	private Long distortion_c_dur;
	private Integer distortion_cnt;
	private Long distortion_dur;
	private Boolean distortion_ing;
	private Integer imbalance_vol_cnt;
	private Long imbalance_vol_dur;
	private Boolean imbalance_vol_ing;
	private Integer imbalance_curr_cnt;
	private Long imbalance_curr_dur;
	private Boolean imbalance_curr_ing;
	private Integer service_vol_cnt;
	private Long service_vol_dur;
	private Boolean service_vol_ing;
	private Integer high_neutral_curr_cnt;
	private Long high_neutral_curr_dur;
	private Boolean high_neutral_curr_ing;	
	
	public PowerQualityStatus(){
		id = new PowerQualityStatusPk();
	}
	
	public PowerQualityStatusPk getId() {
		return id;
	}
	public void setId(PowerQualityStatusPk id) {
		this.id = id;
	}
	
	public DeviceType getMDevType() {
		return id.getMDevType();
	}
	
	public void setMDevType(String mdevType) {
		this.id.setMDevType(mdevType);
	}
	public String getMDevId() {
		return id.getMDevId();
	}
	public void setMDevId(String mdevId) {
		this.id.setMDevId(mdevId);
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	
	@XmlTransient
	public Meter getMeter() {
		return this.meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	@XmlTransient
	public Contract getContract() {
		return this.contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}

	@XmlTransient
	public Modem getModem() {
		return modem;
	}

	public void setModem(Modem modem) {
		this.modem = modem;
	}
	
	@XmlTransient
	public EndDevice getEnddevice() {
		return enddevice;
	}

	public void setEnddevice(EndDevice enddevice) {
		this.enddevice = enddevice;
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
	public String getYyyymmddhhmmss() {
		return this.id.getYyyymmddhhmmss();
	}
	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.id.setYyyymmddhhmmss(yyyymmddhhmmss);
	}
	public String getWriteDate() {
		return writeDate;
	}
	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}	
	
	public Integer getVol_a_sag_cnt() {
		return vol_a_sag_cnt;
	}
	public void setVol_a_sag_cnt(Integer vol_a_sag_cnt) {
		this.vol_a_sag_cnt = vol_a_sag_cnt;
	}
	public Long getVol_a_sag_dur() {
		return vol_a_sag_dur;
	}
	public void setVol_a_sag_dur(Long vol_a_sag_dur) {
		this.vol_a_sag_dur = vol_a_sag_dur;
	}
	public Integer getVol_b_sag_cnt() {
		return vol_b_sag_cnt;
	}
	public void setVol_b_sag_cnt(Integer vol_b_sag_cnt) {
		this.vol_b_sag_cnt = vol_b_sag_cnt;
	}
	public Long getVol_b_sag_dur() {
		return vol_b_sag_dur;
	}
	public void setVol_b_sag_dur(Long vol_b_sag_dur) {
		this.vol_b_sag_dur = vol_b_sag_dur;
	}
	public Integer getVol_c_sag_cnt() {
		return vol_c_sag_cnt;
	}
	public void setVol_c_sag_cnt(Integer vol_c_sag_cnt) {
		this.vol_c_sag_cnt = vol_c_sag_cnt;
	}
	public Long getVol_c_sag_dur() {
		return vol_c_sag_dur;
	}
	public void setVol_c_sag_dur(Long vol_c_sag_dur) {
		this.vol_c_sag_dur = vol_c_sag_dur;
	}
	public Integer getVol_sag_cnt() {
		return vol_sag_cnt;
	}
	public void setVol_sag_cnt(Integer vol_sag_cnt) {
		this.vol_sag_cnt = vol_sag_cnt;
	}
	public Long getVol_sag_dur() {
		return vol_sag_dur;
	}
	public void setVol_sag_dur(Long vol_sag_dur) {
		this.vol_sag_dur = vol_sag_dur;
	}
	public Boolean getVol_sag_ing() {
		return vol_sag_ing;
	}
	public void setVol_sag_ing(Boolean vol_sag_ing) {
		this.vol_sag_ing = vol_sag_ing;
	}
	public Integer getVol_a_swell_cnt() {
		return vol_a_swell_cnt;
	}
	public void setVol_a_swell_cnt(Integer vol_a_swell_cnt) {
		this.vol_a_swell_cnt = vol_a_swell_cnt;
	}
	public Long getVol_a_swell_dur() {
		return vol_a_swell_dur;
	}
	public void setVol_a_swell_dur(Long vol_a_swell_dur) {
		this.vol_a_swell_dur = vol_a_swell_dur;
	}
	public Integer getVol_b_swell_cnt() {
		return vol_b_swell_cnt;
	}
	public void setVol_b_swell_cnt(Integer vol_b_swell_cnt) {
		this.vol_b_swell_cnt = vol_b_swell_cnt;
	}
	public Long getVol_b_swell_dur() {
		return vol_b_swell_dur;
	}
	public void setVol_b_swell_dur(Long vol_b_swell_dur) {
		this.vol_b_swell_dur = vol_b_swell_dur;
	}
	public Integer getVol_c_swell_cnt() {
		return vol_c_swell_cnt;
	}
	public void setVol_c_swell_cnt(Integer vol_c_swell_cnt) {
		this.vol_c_swell_cnt = vol_c_swell_cnt;
	}
	public Long getVol_c_swell_dur() {
		return vol_c_swell_dur;
	}
	public void setVol_c_swell_dur(Long vol_c_swell_dur) {
		this.vol_c_swell_dur = vol_c_swell_dur;
	}
	public Integer getVol_swell_cnt() {
		return vol_swell_cnt;
	}
	public void setVol_swell_cnt(Integer vol_swell_cnt) {
		this.vol_swell_cnt = vol_swell_cnt;
	}
	public Long getVol_swell_dur() {
		return vol_swell_dur;
	}
	public void setVol_swell_dur(Long vol_swell_dur) {
		this.vol_swell_dur = vol_swell_dur;
	}
	public Boolean getVol_swell_ing() {
		return vol_swell_ing;
	}
	public void setVol_swell_ing(Boolean vol_swell_ing) {
		this.vol_swell_ing = vol_swell_ing;
	}
	public Integer getVol_cut_cnt() {
		return vol_cut_cnt;
	}
	public void setVol_cut_cnt(Integer vol_cut_cnt) {
		this.vol_cut_cnt = vol_cut_cnt;
	}
	public Long getVol_cut_dur() {
		return vol_cut_dur;
	}
	public void setVol_cut_dur(Long vol_cut_dur) {
		this.vol_cut_dur = vol_cut_dur;
	}
	public Boolean getVol_cut_ing() {
		return vol_cut_ing;
	}
	public void setVol_cut_ing(Boolean vol_cut_ing) {
		this.vol_cut_ing = vol_cut_ing;
	}
	public Integer getVol_flicker_cnt() {
		return vol_flicker_cnt;
	}
	public void setVol_flicker_cnt(Integer vol_flicker_cnt) {
		this.vol_flicker_cnt = vol_flicker_cnt;
	}
	public Long getVol_flicker_dur() {
		return vol_flicker_dur;
	}
	public void setVol_flicker_dur(Long vol_flicker_dur) {
		this.vol_flicker_dur = vol_flicker_dur;
	}
	public Boolean getVol_flicker_ing() {
		return vol_flicker_ing;
	}
	public void setVol_flicker_ing(Boolean vol_flicker_ing) {
		this.vol_flicker_ing = vol_flicker_ing;
	}
	public Integer getVol_fluctuation_cnt() {
		return vol_fluctuation_cnt;
	}
	public void setVol_fluctuation_cnt(Integer vol_fluctuation_cnt) {
		this.vol_fluctuation_cnt = vol_fluctuation_cnt;
	}
	public Long getVol_fluctuation_dur() {
		return vol_fluctuation_dur;
	}
	public void setVol_fluctuation_dur(Long vol_fluctuation_dur) {
		this.vol_fluctuation_dur = vol_fluctuation_dur;
	}
	public Boolean getVol_fluctuation_ing() {
		return vol_fluctuation_ing;
	}
	public void setVol_fluctuation_ing(Boolean vol_fluctuation_ing) {
		this.vol_fluctuation_ing = vol_fluctuation_ing;
	}
	public Integer getLow_vol_cnt() {
		return low_vol_cnt;
	}
	public void setLow_vol_cnt(Integer low_vol_cnt) {
		this.low_vol_cnt = low_vol_cnt;
	}
	public Long getLow_vol_dur() {
		return low_vol_dur;
	}
	public void setLow_vol_dur(Long low_vol_dur) {
		this.low_vol_dur = low_vol_dur;
	}
	public Boolean getLow_vol_ing() {
		return low_vol_ing;
	}
	public void setLow_vol_ing(Boolean low_vol_ing) {
		this.low_vol_ing = low_vol_ing;
	}
	public Integer getHigh_vol_cnt() {
		return high_vol_cnt;
	}
	public void setHigh_vol_cnt(Integer high_vol_cnt) {
		this.high_vol_cnt = high_vol_cnt;
	}
	public Long getHigh_vol_dur() {
		return high_vol_dur;
	}
	public void setHigh_vol_dur(Long high_vol_dur) {
		this.high_vol_dur = high_vol_dur;
	}
	public Boolean getHigh_vol_ing() {
		return high_vol_ing;
	}
	public void setHigh_vol_ing(Boolean high_vol_ing) {
		this.high_vol_ing = high_vol_ing;
	}
	public Integer getHigh_frequency_cnt() {
		return high_frequency_cnt;
	}
	public void setHigh_frequency_cnt(Integer high_frequency_cnt) {
		this.high_frequency_cnt = high_frequency_cnt;
	}
	public Long getHigh_frequency_dur() {
		return high_frequency_dur;
	}
	public void setHigh_frequency_dur(Long high_frequency_dur) {
		this.high_frequency_dur = high_frequency_dur;
	}
	public Boolean getHigh_frequency_ing() {
		return high_frequency_ing;
	}
	public void setHigh_frequency_ing(Boolean high_frequency_ing) {
		this.high_frequency_ing = high_frequency_ing;
	}
	public Integer getPolarity_cross_phase_cnt() {
		return polarity_cross_phase_cnt;
	}
	public void setPolarity_cross_phase_cnt(Integer polarity_cross_phase_cnt) {
		this.polarity_cross_phase_cnt = polarity_cross_phase_cnt;
	}
	public Long getPolarity_cross_phase_dur() {
		return polarity_cross_phase_dur;
	}
	public void setPolarity_cross_phase_dur(Long polarity_cross_phase_dur) {
		this.polarity_cross_phase_dur = polarity_cross_phase_dur;
	}
	public Boolean getPolarity_cross_phase_ing() {
		return polarity_cross_phase_ing;
	}
	public void setPolarity_cross_phase_ing(
			Boolean polarity_cross_phase_ing) {
		this.polarity_cross_phase_ing = polarity_cross_phase_ing;
	}
	public Integer getReverse_pwr_cnt() {
		return reverse_pwr_cnt;
	}
	public void setReverse_pwr_cnt(Integer reverse_pwr_cnt) {
		this.reverse_pwr_cnt = reverse_pwr_cnt;
	}
	public Long getReverse_pwr_dur() {
		return reverse_pwr_dur;
	}
	public void setReverse_pwr_dur(Long reverse_pwr_dur) {
		this.reverse_pwr_dur = reverse_pwr_dur;
	}
	public Boolean getReverse_pwr_ing() {
		return reverse_pwr_ing;
	}
	public void setReverse_pwr_ing(Boolean reverse_pwr_ing) {
		this.reverse_pwr_ing = reverse_pwr_ing;
	}
	public Integer getLow_curr_cnt() {
		return low_curr_cnt;
	}
	public void setLow_curr_cnt(Integer low_curr_cnt) {
		this.low_curr_cnt = low_curr_cnt;
	}
	public Long getLow_curr_dur() {
		return low_curr_dur;
	}
	public void setLow_curr_dur(Long low_curr_dur) {
		this.low_curr_dur = low_curr_dur;
	}
	public Boolean getLow_curr_ing() {
		return low_curr_ing;
	}
	public void setLow_curr_ing(Boolean low_curr_ing) {
		this.low_curr_ing = low_curr_ing;
	}
	public Integer getOver_curr_cnt() {
		return over_curr_cnt;
	}
	public void setOver_curr_cnt(Integer over_curr_cnt) {
		this.over_curr_cnt = over_curr_cnt;
	}
	public Long getOver_curr_dur() {
		return over_curr_dur;
	}
	public void setOver_curr_dur(Long over_curr_dur) {
		this.over_curr_dur = over_curr_dur;
	}
	public Boolean getOver_curr_ing() {
		return over_curr_ing;
	}
	public void setOver_curr_ing(Boolean over_curr_ing) {
		this.over_curr_ing = over_curr_ing;
	}
	public Integer getPfactor_cnt() {
		return pfactor_cnt;
	}
	public void setPfactor_cnt(Integer pfactor_cnt) {
		this.pfactor_cnt = pfactor_cnt;
	}
	public Long getPfactor_dur() {
		return pfactor_dur;
	}
	public void setPfactor_dur(Long pfactor_dur) {
		this.pfactor_dur = pfactor_dur;
	}
	public Boolean getPfactor_ing() {
		return pfactor_ing;
	}
	public void setPfactor_ing(Boolean pfactor_ing) {
		this.pfactor_ing = pfactor_ing;
	}
	public Integer getHarmonic_cnt() {
		return harmonic_cnt;
	}
	public void setHarmonic_cnt(Integer harmonic_cnt) {
		this.harmonic_cnt = harmonic_cnt;
	}
	public Long getHarmonic_dur() {
		return harmonic_dur;
	}
	public void setHarmonic_dur(Long harmonic_dur) {
		this.harmonic_dur = harmonic_dur;
	}
	public Boolean getHarmonic_ing() {
		return harmonic_ing;
	}
	public void setHarmonic_ing(Boolean harmonic_ing) {
		this.harmonic_ing = harmonic_ing;
	}
	public Integer getThd_curr_cnt() {
		return thd_curr_cnt;
	}
	public void setThd_curr_cnt(Integer thd_curr_cnt) {
		this.thd_curr_cnt = thd_curr_cnt;
	}
	public Long getThd_curr_dur() {
		return thd_curr_dur;
	}
	public void setThd_curr_dur(Long thd_curr_dur) {
		this.thd_curr_dur = thd_curr_dur;
	}
	public Boolean getThd_curr_ing() {
		return thd_curr_ing;
	}
	public void setThd_curr_ing(Boolean thd_curr_ing) {
		this.thd_curr_ing = thd_curr_ing;
	}
	public Integer getThd_vol_cnt() {
		return thd_vol_cnt;
	}
	public void setThd_vol_cnt(Integer thd_vol_cnt) {
		this.thd_vol_cnt = thd_vol_cnt;
	}
	public Long getThd_vol_dur() {
		return thd_vol_dur;
	}
	public void setThd_vol_dur(Long thd_vol_dur) {
		this.thd_vol_dur = thd_vol_dur;
	}
	public Boolean getThd_vol_ing() {
		return thd_vol_ing;
	}
	public void setThd_vol_ing(Boolean thd_vol_ing) {
		this.thd_vol_ing = thd_vol_ing;
	}
	public Integer getTdd_cnt() {
		return tdd_cnt;
	}
	public void setTdd_cnt(Integer tdd_cnt) {
		this.tdd_cnt = tdd_cnt;
	}
	public Long getTdd_dur() {
		return tdd_dur;
	}
	public void setTdd_dur(Long tdd_dur) {
		this.tdd_dur = tdd_dur;
	}
	public Boolean getTdd_ing() {
		return tdd_ing;
	}
	public void setTdd_ing(Boolean tdd_ing) {
		this.tdd_ing = tdd_ing;
	}
	public Integer getDistortion_a_cnt() {
		return distortion_a_cnt;
	}
	public void setDistortion_a_cnt(Integer distortion_a_cnt) {
		this.distortion_a_cnt = distortion_a_cnt;
	}
	public Long getDistortion_a_dur() {
		return distortion_a_dur;
	}
	public void setDistortion_a_dur(Long distortion_a_dur) {
		this.distortion_a_dur = distortion_a_dur;
	}
	public Integer getDistortion_b_cnt() {
		return distortion_b_cnt;
	}
	public void setDistortion_b_cnt(Integer distortion_b_cnt) {
		this.distortion_b_cnt = distortion_b_cnt;
	}
	public Long getDistortion_b_dur() {
		return distortion_b_dur;
	}
	public void setDistortion_b_dur(Long distortion_b_dur) {
		this.distortion_b_dur = distortion_b_dur;
	}
	public Integer getDistortion_c_cnt() {
		return distortion_c_cnt;
	}
	public void setDistortion_c_cnt(Integer distortion_c_cnt) {
		this.distortion_c_cnt = distortion_c_cnt;
	}
	public Long getDistortion_c_dur() {
		return distortion_c_dur;
	}
	public void setDistortion_c_dur(Long distortion_c_dur) {
		this.distortion_c_dur = distortion_c_dur;
	}
	public Integer getDistortion_cnt() {
		return distortion_cnt;
	}
	public void setDistortion_cnt(Integer distortion_cnt) {
		this.distortion_cnt = distortion_cnt;
	}
	public Long getDistortion_dur() {
		return distortion_dur;
	}
	public void setDistortion_dur(Long distortion_dur) {
		this.distortion_dur = distortion_dur;
	}
	public Boolean getDistortion_ing() {
		return distortion_ing;
	}
	public void setDistortion_ing(Boolean distortion_ing) {
		this.distortion_ing = distortion_ing;
	}
	public Integer getImbalance_vol_cnt() {
		return imbalance_vol_cnt;
	}
	public void setImbalance_vol_cnt(Integer imbalance_vol_cnt) {
		this.imbalance_vol_cnt = imbalance_vol_cnt;
	}
	public Long getImbalance_vol_dur() {
		return imbalance_vol_dur;
	}
	public void setImbalance_vol_dur(Long imbalance_vol_dur) {
		this.imbalance_vol_dur = imbalance_vol_dur;
	}
	public Boolean getImbalance_vol_ing() {
		return imbalance_vol_ing;
	}
	public void setImbalance_vol_ing(Boolean imbalance_vol_ing) {
		this.imbalance_vol_ing = imbalance_vol_ing;
	}
	public Integer getImbalance_curr_cnt() {
		return imbalance_curr_cnt;
	}
	public void setImbalance_curr_cnt(Integer imbalance_curr_cnt) {
		this.imbalance_curr_cnt = imbalance_curr_cnt;
	}
	public Long getImbalance_curr_dur() {
		return imbalance_curr_dur;
	}
	public void setImbalance_curr_dur(Long imbalance_curr_dur) {
		this.imbalance_curr_dur = imbalance_curr_dur;
	}
	public Boolean getImbalance_curr_ing() {
		return imbalance_curr_ing;
	}
	public void setImbalance_curr_ing(Boolean imbalance_curr_ing) {
		this.imbalance_curr_ing = imbalance_curr_ing;
	}
	public Integer getService_vol_cnt() {
		return service_vol_cnt;
	}
	public void setService_vol_cnt(Integer service_vol_cnt) {
		this.service_vol_cnt = service_vol_cnt;
	}
	public Long getService_vol_dur() {
		return service_vol_dur;
	}
	public void setService_vol_dur(Long service_vol_dur) {
		this.service_vol_dur = service_vol_dur;
	}
	public Boolean getService_vol_ing() {
		return service_vol_ing;
	}
	public void setService_vol_ing(Boolean service_vol_ing) {
		this.service_vol_ing = service_vol_ing;
	}
	public Integer getHigh_neutral_curr_cnt() {
		return high_neutral_curr_cnt;
	}
	public void setHigh_neutral_curr_cnt(Integer high_neutral_curr_cnt) {
		this.high_neutral_curr_cnt = high_neutral_curr_cnt;
	}
	public Long getHigh_neutral_curr_dur() {
		return high_neutral_curr_dur;
	}
	public void setHigh_neutral_curr_dur(Long high_neutral_curr_dur) {
		this.high_neutral_curr_dur = high_neutral_curr_dur;
	}
	public Boolean getHigh_neutral_curr_ing() {
		return high_neutral_curr_ing;
	}
	public void setHigh_neutral_curr_ing(Boolean high_neutral_curr_ing) {
		this.high_neutral_curr_ing = high_neutral_curr_ing;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getModemId() {
        return modemId;
    }

    public void setModemId(Integer modemId) {
        this.modemId = modemId;
    }

    public Integer getEndDeviceId() {
        return endDeviceId;
    }

    public void setEndDeviceId(Integer endDeviceId) {
        this.endDeviceId = endDeviceId;
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

	@Override
	public String toJSONString() {
		// TODO Auto-generated method stub
		return null;
	}

}
