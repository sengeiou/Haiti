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

import org.eclipse.persistence.annotations.Index;

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
 * 전기의 전력 품질(Voltage, Current)  , Voltage Level, Voltage Imbalance 정보를 저장한다.
 * 전기 미터의 경우 검침주기별 전력 품질데이터를 의미한다. 
 * 전기미터의 경우에만 PowerQualty데이터가 존재하게 되며 
 * 
 * 주키 정보는 검침데이터 정보들과 마찬가지고 미터, 계약, 소비지역아이디, 날짜시간정보등이 된다. 
 * 
 * 
 * Percent Voltage Unbalance = 100 *  Maximum Voltage Deviation From Average Voltage / Average Voltage
 * Example: 

 * With voltages of voltage A(220), voltage B(215) and voltage B(210), the average is 215, the maximum deviation from the average is 5, and the percent 
 * unbalance = 100 X 5/215 = 2.3 percent.
 *   
 *   
 *   즉 
 *   
 *  Unbalance% =  100 * Average of Three Phase-to-Phase Voltages/Average of Three Phase-to-Phase Voltages
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "POWER_QUALITY")
@Index(name="IDX_POWER_QUALITY_01", columnNames={"YYYYMMDDHHMM", "SUPPLIER_ID"})
public class PowerQuality extends BaseObject{   

    private static final long serialVersionUID = -6083987782436504060L;

    @EmbeddedId public PowerQualityPk id;
    
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
    
    @Column(name="yyyymmdd",length=8,nullable=false)
    private String yyyymmdd;
    @Column(name="hhmm",length=4, nullable=false)
    private String hhmm;

    @Column(name="writeDate",length=14,nullable=false)
    private String writeDate;
    
    private Double vol_a;
    private Double vol_b;
    private Double vol_c;
    private Double curr_a;
    private Double curr_b;
    private Double curr_c;
    private Double vol_angle_a;
    private Double vol_angle_b;
    private Double vol_angle_c;
    private Double curr_angle_a;
    private Double curr_angle_b;
    private Double curr_angle_c;
    private Double vol_thd_a;
    private Double vol_thd_b;
    private Double vol_thd_c;
    private Double curr_thd_a;
    private Double curr_thd_b;
    private Double curr_thd_c;
    private Double tdd_a;
    private Double tdd_b;
    private Double tdd_c;
    private Double pf_a;
    private Double pf_b;
    private Double pf_c;
    private Double pf_total;
    private Double distortion_pf_a;
    private Double distortion_pf_b;
    private Double distortion_pf_c;
    private Double distortion_pf_total;
    private Double kw_a;
    private Double kw_b;
    private Double kw_c;
    private Double kvar_a;
    private Double kvar_b;
    private Double kvar_c;
    private Double kva_a;
    private Double kva_b;
    private Double kva_c;
    private Double distortion_kva_a;
    private Double distortion_kva_b;
    private Double distortion_kva_c;
    private Double vol_1st_harmonic_mag_a;
    private Double vol_1st_harmonic_mag_b;
    private Double vol_1st_harmonic_mag_c;
    private Double curr_1st_harmonic_mag_a;
    private Double curr_1st_harmonic_mag_b;
    private Double curr_1st_harmonic_mag_c;
    private Double vol_2nd_harmonic_mag_a;
    private Double vol_2nd_harmonic_mag_b;
    private Double vol_2nd_harmonic_mag_c;
    private Double curr_2nd_harmonic_mag_a;
    private Double curr_2nd_harmonic_mag_b;
    private Double curr_2nd_harmonic_mag_c;
    private Double vol_2nd_harmonic_a;
    private Double vol_2nd_harmonic_b;
    private Double vol_2nd_harmonic_c;
    private Double curr_harmonic_a;
    private Double curr_harmonic_b;
    private Double curr_harmonic_c;
    private Double line_frequency;
    private Double system_pf_angle;
    private Double ph_fund_vol_a;
    private Double ph_fund_vol_b;
    private Double ph_fund_vol_c;
    private Double ph_vol_pqm_a;
    private Double ph_vol_pqm_b;
    private Double ph_vol_pqm_c;
    private Double vol_seq_z;
    private Double vol_seq_p;
    private Double vol_seq_n;
    private Double ph_fund_curr_a;
    private Double ph_fund_curr_b;
    private Double ph_fund_curr_c;
    private Double ph_curr_pqm_a;
    private Double ph_curr_pqm_b;
    private Double ph_curr_pqm_c;
    private Double curr_seq_z;
    private Double curr_seq_p;
    private Double curr_seq_n;  
    
    /**
     * mx2 미터용으로 추가함<br>
     * 2012-04-20<br>
     * kskim
     */
    private Double line_AB;
    private Double line_CA;
    private Double line_BC;
    
    public PowerQuality(){
        id = new PowerQualityPk();
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

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
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

    public void setDeviceType(String deviceType) {
        this.deviceType = DeviceType.valueOf(deviceType);
    }

    public String getYyyymmdd() {
        return yyyymmdd;
    }
    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
    }
    public String getHhmm() {
        return hhmm;
    }
    public void setHhmm(String hhmm) {
        this.hhmm = hhmm;
    }
    
    public Integer getDst() {
        return id.getDst();
    }
    public void setDst(Integer dst) {
        this.id.setDst(dst);
    }

    public String getWriteDate() {
        return writeDate;
    }
    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }
    public Double getVol_a() {
        return vol_a;
    }
    public void setVol_a(Double vol_a) {
        this.vol_a = vol_a;
    }
    public Double getVol_b() {
        return vol_b;
    }
    public void setVol_b(Double vol_b) {
        this.vol_b = vol_b;
    }
    public Double getVol_c() {
        return vol_c;
    }
    public void setVol_c(Double vol_c) {
        this.vol_c = vol_c;
    }
    public Double getCurr_a() {
        return curr_a;
    }
    public void setCurr_a(Double curr_a) {
        this.curr_a = curr_a;
    }
    public Double getCurr_b() {
        return curr_b;
    }
    public void setCurr_b(Double curr_b) {
        this.curr_b = curr_b;
    }
    public Double getCurr_c() {
        return curr_c;
    }
    public void setCurr_c(Double curr_c) {
        this.curr_c = curr_c;
    }
    public Double getVol_angle_a() {
        return vol_angle_a;
    }
    public void setVol_angle_a(Double vol_angle_a) {
        this.vol_angle_a = vol_angle_a;
    }
    public Double getVol_angle_b() {
        return vol_angle_b;
    }
    public void setVol_angle_b(Double vol_angle_b) {
        this.vol_angle_b = vol_angle_b;
    }
    public Double getVol_angle_c() {
        return vol_angle_c;
    }
    public void setVol_angle_c(Double vol_angle_c) {
        this.vol_angle_c = vol_angle_c;
    }
    public Double getCurr_angle_a() {
        return curr_angle_a;
    }
    public void setCurr_angle_a(Double curr_angle_a) {
        this.curr_angle_a = curr_angle_a;
    }
    public Double getCurr_angle_b() {
        return curr_angle_b;
    }
    public void setCurr_angle_b(Double curr_angle_b) {
        this.curr_angle_b = curr_angle_b;
    }
    public Double getCurr_angle_c() {
        return curr_angle_c;
    }
    public void setCurr_angle_c(Double curr_angle_c) {
        this.curr_angle_c = curr_angle_c;
    }
    public Double getVol_thd_a() {
        return vol_thd_a;
    }
    public void setVol_thd_a(Double vol_thd_a) {
        this.vol_thd_a = vol_thd_a;
    }
    public Double getVol_thd_b() {
        return vol_thd_b;
    }
    public void setVol_thd_b(Double vol_thd_b) {
        this.vol_thd_b = vol_thd_b;
    }
    public Double getVol_thd_c() {
        return vol_thd_c;
    }
    public void setVol_thd_c(Double vol_thd_c) {
        this.vol_thd_c = vol_thd_c;
    }
    public Double getCurr_thd_a() {
        return curr_thd_a;
    }
    public void setCurr_thd_a(Double curr_thd_a) {
        this.curr_thd_a = curr_thd_a;
    }
    public Double getCurr_thd_b() {
        return curr_thd_b;
    }
    public void setCurr_thd_b(Double curr_thd_b) {
        this.curr_thd_b = curr_thd_b;
    }
    public Double getCurr_thd_c() {
        return curr_thd_c;
    }
    public void setCurr_thd_c(Double curr_thd_c) {
        this.curr_thd_c = curr_thd_c;
    }
    public Double getTdd_a() {
        return tdd_a;
    }
    public void setTdd_a(Double tdd_a) {
        this.tdd_a = tdd_a;
    }
    public Double getTdd_b() {
        return tdd_b;
    }
    public void setTdd_b(Double tdd_b) {
        this.tdd_b = tdd_b;
    }
    public Double getTdd_c() {
        return tdd_c;
    }
    public void setTdd_c(Double tdd_c) {
        this.tdd_c = tdd_c;
    }
    public Double getPf_a() {
        return pf_a;
    }
    public void setPf_a(Double pf_a) {
        this.pf_a = pf_a;
    }
    public Double getPf_b() {
        return pf_b;
    }
    public void setPf_b(Double pf_b) {
        this.pf_b = pf_b;
    }
    public Double getPf_c() {
        return pf_c;
    }
    public void setPf_c(Double pf_c) {
        this.pf_c = pf_c;
    }
    public Double getPf_total() {
        return pf_total;
    }
    public void setPf_total(Double pf_total) {
        this.pf_total = pf_total;
    }
    public Double getDistortion_pf_a() {
        return distortion_pf_a;
    }
    public void setDistortion_pf_a(Double distortion_pf_a) {
        this.distortion_pf_a = distortion_pf_a;
    }
    public Double getDistortion_pf_b() {
        return distortion_pf_b;
    }
    public void setDistortion_pf_b(Double distortion_pf_b) {
        this.distortion_pf_b = distortion_pf_b;
    }
    public Double getDistortion_pf_c() {
        return distortion_pf_c;
    }
    public void setDistortion_pf_c(Double distortion_pf_c) {
        this.distortion_pf_c = distortion_pf_c;
    }
    public Double getDistortion_pf_total() {
        return distortion_pf_total;
    }
    public void setDistortion_pf_total(Double distortion_pf_total) {
        this.distortion_pf_total = distortion_pf_total;
    }
    public Double getKw_a() {
        return kw_a;
    }
    public void setKw_a(Double kw_a) {
        this.kw_a = kw_a;
    }
    public Double getKw_b() {
        return kw_b;
    }
    public void setKw_b(Double kw_b) {
        this.kw_b = kw_b;
    }
    public Double getKw_c() {
        return kw_c;
    }
    public void setKw_c(Double kw_c) {
        this.kw_c = kw_c;
    }
    public Double getKvar_a() {
        return kvar_a;
    }
    public void setKvar_a(Double kvar_a) {
        this.kvar_a = kvar_a;
    }
    public Double getKvar_b() {
        return kvar_b;
    }
    public void setKvar_b(Double kvar_b) {
        this.kvar_b = kvar_b;
    }
    public Double getKvar_c() {
        return kvar_c;
    }
    public void setKvar_c(Double kvar_c) {
        this.kvar_c = kvar_c;
    }
    public Double getKva_a() {
        return kva_a;
    }
    public void setKva_a(Double kva_a) {
        this.kva_a = kva_a;
    }
    public Double getKva_b() {
        return kva_b;
    }
    public void setKva_b(Double kva_b) {
        this.kva_b = kva_b;
    }
    public Double getKva_c() {
        return kva_c;
    }
    public void setKva_c(Double kva_c) {
        this.kva_c = kva_c;
    }
    public Double getDistortion_kva_a() {
        return distortion_kva_a;
    }
    public void setDistortion_kva_a(Double distortion_kva_a) {
        this.distortion_kva_a = distortion_kva_a;
    }
    public Double getDistortion_kva_b() {
        return distortion_kva_b;
    }
    public void setDistortion_kva_b(Double distortion_kva_b) {
        this.distortion_kva_b = distortion_kva_b;
    }
    public Double getDistortion_kva_c() {
        return distortion_kva_c;
    }
    public void setDistortion_kva_c(Double distortion_kva_c) {
        this.distortion_kva_c = distortion_kva_c;
    }
    public Double getVol_1st_harmonic_mag_a() {
        return vol_1st_harmonic_mag_a;
    }
    public void setVol_1st_harmonic_mag_a(Double vol_1st_harmonic_mag_a) {
        this.vol_1st_harmonic_mag_a = vol_1st_harmonic_mag_a;
    }
    public Double getVol_1st_harmonic_mag_b() {
        return vol_1st_harmonic_mag_b;
    }
    public void setVol_1st_harmonic_mag_b(Double vol_1st_harmonic_mag_b) {
        this.vol_1st_harmonic_mag_b = vol_1st_harmonic_mag_b;
    }
    public Double getVol_1st_harmonic_mag_c() {
        return vol_1st_harmonic_mag_c;
    }
    public void setVol_1st_harmonic_mag_c(Double vol_1st_harmonic_mag_c) {
        this.vol_1st_harmonic_mag_c = vol_1st_harmonic_mag_c;
    }
    public Double getCurr_1st_harmonic_mag_a() {
        return curr_1st_harmonic_mag_a;
    }
    public void setCurr_1st_harmonic_mag_a(Double curr_1st_harmonic_mag_a) {
        this.curr_1st_harmonic_mag_a = curr_1st_harmonic_mag_a;
    }
    public Double getCurr_1st_harmonic_mag_b() {
        return curr_1st_harmonic_mag_b;
    }
    public void setCurr_1st_harmonic_mag_b(Double curr_1st_harmonic_mag_b) {
        this.curr_1st_harmonic_mag_b = curr_1st_harmonic_mag_b;
    }
    public Double getCurr_1st_harmonic_mag_c() {
        return curr_1st_harmonic_mag_c;
    }
    public void setCurr_1st_harmonic_mag_c(Double curr_1st_harmonic_mag_c) {
        this.curr_1st_harmonic_mag_c = curr_1st_harmonic_mag_c;
    }
    public Double getVol_2nd_harmonic_mag_a() {
        return vol_2nd_harmonic_mag_a;
    }
    public void setVol_2nd_harmonic_mag_a(Double vol_2nd_harmonic_mag_a) {
        this.vol_2nd_harmonic_mag_a = vol_2nd_harmonic_mag_a;
    }
    public Double getVol_2nd_harmonic_mag_b() {
        return vol_2nd_harmonic_mag_b;
    }
    public void setVol_2nd_harmonic_mag_b(Double vol_2nd_harmonic_mag_b) {
        this.vol_2nd_harmonic_mag_b = vol_2nd_harmonic_mag_b;
    }
    public Double getVol_2nd_harmonic_mag_c() {
        return vol_2nd_harmonic_mag_c;
    }
    public void setVol_2nd_harmonic_mag_c(Double vol_2nd_harmonic_mag_c) {
        this.vol_2nd_harmonic_mag_c = vol_2nd_harmonic_mag_c;
    }
    public Double getCurr_2nd_harmonic_mag_a() {
        return curr_2nd_harmonic_mag_a;
    }
    public void setCurr_2nd_harmonic_mag_a(Double curr_2nd_harmonic_mag_a) {
        this.curr_2nd_harmonic_mag_a = curr_2nd_harmonic_mag_a;
    }
    public Double getCurr_2nd_harmonic_mag_b() {
        return curr_2nd_harmonic_mag_b;
    }
    public void setCurr_2nd_harmonic_mag_b(Double curr_2nd_harmonic_mag_b) {
        this.curr_2nd_harmonic_mag_b = curr_2nd_harmonic_mag_b;
    }
    public Double getCurr_2nd_harmonic_mag_c() {
        return curr_2nd_harmonic_mag_c;
    }
    public void setCurr_2nd_harmonic_mag_c(Double curr_2nd_harmonic_mag_c) {
        this.curr_2nd_harmonic_mag_c = curr_2nd_harmonic_mag_c;
    }
    public Double getVol_2nd_harmonic_a() {
        return vol_2nd_harmonic_a;
    }
    public void setVol_2nd_harmonic_a(Double vol_2nd_harmonic_a) {
        this.vol_2nd_harmonic_a = vol_2nd_harmonic_a;
    }
    public Double getVol_2nd_harmonic_b() {
        return vol_2nd_harmonic_b;
    }
    public void setVol_2nd_harmonic_b(Double vol_2nd_harmonic_b) {
        this.vol_2nd_harmonic_b = vol_2nd_harmonic_b;
    }
    public Double getVol_2nd_harmonic_c() {
        return vol_2nd_harmonic_c;
    }
    public void setVol_2nd_harmonic_c(Double vol_2nd_harmonic_c) {
        this.vol_2nd_harmonic_c = vol_2nd_harmonic_c;
    }
    public Double getCurr_harmonic_a() {
        return curr_harmonic_a;
    }
    public void setCurr_harmonic_a(Double curr_harmonic_a) {
        this.curr_harmonic_a = curr_harmonic_a;
    }
    public Double getCurr_harmonic_b() {
        return curr_harmonic_b;
    }
    public void setCurr_harmonic_b(Double curr_harmonic_b) {
        this.curr_harmonic_b = curr_harmonic_b;
    }
    public Double getCurr_harmonic_c() {
        return curr_harmonic_c;
    }
    public void setCurr_harmonic_c(Double curr_harmonic_c) {
        this.curr_harmonic_c = curr_harmonic_c;
    }
    public Double getLine_frequency() {
        return line_frequency;
    }
    public void setLine_frequency(Double line_frequency) {
        this.line_frequency = line_frequency;
    }
    public Double getSystem_pf_angle() {
        return system_pf_angle;
    }
    public void setSystem_pf_angle(Double system_pf_angle) {
        this.system_pf_angle = system_pf_angle;
    }
    public Double getPh_fund_vol_a() {
        return ph_fund_vol_a;
    }
    public void setPh_fund_vol_a(Double ph_fund_vol_a) {
        this.ph_fund_vol_a = ph_fund_vol_a;
    }
    public Double getPh_fund_vol_b() {
        return ph_fund_vol_b;
    }
    public void setPh_fund_vol_b(Double ph_fund_vol_b) {
        this.ph_fund_vol_b = ph_fund_vol_b;
    }
    public Double getPh_fund_vol_c() {
        return ph_fund_vol_c;
    }
    public void setPh_fund_vol_c(Double ph_fund_vol_c) {
        this.ph_fund_vol_c = ph_fund_vol_c;
    }
    public Double getPh_vol_pqm_a() {
        return ph_vol_pqm_a;
    }
    public void setPh_vol_pqm_a(Double ph_vol_pqm_a) {
        this.ph_vol_pqm_a = ph_vol_pqm_a;
    }
    public Double getPh_vol_pqm_b() {
        return ph_vol_pqm_b;
    }
    public void setPh_vol_pqm_b(Double ph_vol_pqm_b) {
        this.ph_vol_pqm_b = ph_vol_pqm_b;
    }
    public Double getPh_vol_pqm_c() {
        return ph_vol_pqm_c;
    }
    public void setPh_vol_pqm_c(Double ph_vol_pqm_c) {
        this.ph_vol_pqm_c = ph_vol_pqm_c;
    }
    public Double getVol_seq_z() {
        return vol_seq_z;
    }
    public void setVol_seq_z(Double vol_seq_z) {
        this.vol_seq_z = vol_seq_z;
    }
    public Double getVol_seq_p() {
        return vol_seq_p;
    }
    public void setVol_seq_p(Double vol_seq_p) {
        this.vol_seq_p = vol_seq_p;
    }
    public Double getVol_seq_n() {
        return vol_seq_n;
    }
    public void setVol_seq_n(Double vol_seq_n) {
        this.vol_seq_n = vol_seq_n;
    }
    public Double getPh_fund_curr_a() {
        return ph_fund_curr_a;
    }
    public void setPh_fund_curr_a(Double ph_fund_curr_a) {
        this.ph_fund_curr_a = ph_fund_curr_a;
    }
    public Double getPh_fund_curr_b() {
        return ph_fund_curr_b;
    }
    public void setPh_fund_curr_b(Double ph_fund_curr_b) {
        this.ph_fund_curr_b = ph_fund_curr_b;
    }
    public Double getPh_fund_curr_c() {
        return ph_fund_curr_c;
    }
    public void setPh_fund_curr_c(Double ph_fund_curr_c) {
        this.ph_fund_curr_c = ph_fund_curr_c;
    }
    public Double getPh_curr_pqm_a() {
        return ph_curr_pqm_a;
    }
    public void setPh_curr_pqm_a(Double ph_curr_pqm_a) {
        this.ph_curr_pqm_a = ph_curr_pqm_a;
    }
    public Double getPh_curr_pqm_b() {
        return ph_curr_pqm_b;
    }
    public void setPh_curr_pqm_b(Double ph_curr_pqm_b) {
        this.ph_curr_pqm_b = ph_curr_pqm_b;
    }
    public Double getPh_curr_pqm_c() {
        return ph_curr_pqm_c;
    }
    public void setPh_curr_pqm_c(Double ph_curr_pqm_c) {
        this.ph_curr_pqm_c = ph_curr_pqm_c;
    }
    public Double getCurr_seq_z() {
        return curr_seq_z;
    }
    public void setCurr_seq_z(Double curr_seq_z) {
        this.curr_seq_z = curr_seq_z;
    }
    public Double getCurr_seq_p() {
        return curr_seq_p;
    }
    public void setCurr_seq_p(Double curr_seq_p) {
        this.curr_seq_p = curr_seq_p;
    }
    public Double getCurr_seq_n() {
        return curr_seq_n;
    }
    public void setCurr_seq_n(Double curr_seq_n) {
        this.curr_seq_n = curr_seq_n;
    }
    
    public String getYyyymmddhhmm() {
        return id.getYyyymmddhhmm();
    }
    public void setYyyymmddhhmm(String yyyymmddhhmm) {
        this.id.setYyyymmddhhmm(yyyymmddhhmm);
    }
    public Double getLine_AB() {
        return line_AB;
    }

    public void setLine_AB(Double lineAB) {
        line_AB = lineAB;
    }

    public Double getLine_CA() {
        return line_CA;
    }

    public void setLine_CA(Double lineCA) {
        line_CA = lineCA;
    }

    public Double getLine_BC() {
        return line_BC;
    }

    public void setLine_BC(Double lineBC) {
        line_BC = lineBC;
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

}