/**
 * MdisMeter.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * MdisMeter.java Description 
 *
 * 
 * Date          Version     Author   Description
 * 2011. 12. 13  v1.0        문동규   모델 생성
 * 2012. 05. 10  v1.1        문동규   package 위치변경(mvm -> device)
 *
 */
@Entity
@Table(name="MDIS_METER")
public class MdisMeter extends BaseObject implements JSONString {

    private static final long serialVersionUID = 7085782000933550055L;

    @Id
    @Column(name="meter_id")
    private Integer id;

    //@OneToOne
    //@JoinColumn(name="meter_id", nullable = false)
    //private Meter meter;

    @Column(name="tamp_bypass")
    @ColumnInfo(name="Tamp Bypass", descr="Tampering Bypass status. 0:Normal, 1:Issued")
    private Integer tampBypass;

    @Column(name="tamp_earth_ld")
    @ColumnInfo(name="Tamp earth load", descr="Tampering earth load status. 0:Normal, 1:Issued")
    private Integer tampEarthLd;

    @Column(name="tamp_reverse")
    @ColumnInfo(name="Tamp reverse", descr="Tampering reverse status. 0:Normal, 1:Issued")
    private Integer tampReverse;

    @Column(name="tamp_cover_op")
    @ColumnInfo(name="Tamp cover open", descr="Tampering Terminal cover open status. 0:Normal, 1:Issued")
    private Integer tampCoverOp;

    @Column(name="tamp_front_op")
    @ColumnInfo(name="Tamp Front open", descr="Tampering Front cover open status. 0:Normal, 1:Issued")
    private Integer tampFrontOp;

    @Column(name="prepaid_alert_level1")
    @ColumnInfo(name="Prepaid alert level1", descr="Prepaid alert level1 Setting(remaining energy). 0 – 99,999,999")
    private Integer prepaidAlertLevel1;

    @Column(name="prepaid_alert_level2")
    @ColumnInfo(name="Prepaid alert level2", descr="Prepaid alert level2 Setting(remaining energy). 0 – 99,999,999")
    private Integer prepaidAlertLevel2;

    @Column(name="prepaid_alert_level3")
    @ColumnInfo(name="Prepaid alert level3", descr="Prepaid alert level3 Setting(remaining energy). 0 – 99,999,999")
    private Integer prepaidAlertLevel3;

    @Column(name="prepaid_alert_start")
    @ColumnInfo(name="Prepaid alert start", descr="Prepaid alert buzzer starts time Setting. 0 – 65,535")
    private Integer prepaidAlertStart;

    @Column(name="prepaid_alert_off")
    @ColumnInfo(name="Prepaid alert off", descr="Prepaid alert relay off Setting(remaining energy). 0 – 99,999,999")
    private Integer prepaidAlertOff;

    @Column(name="meter_direction", length=2)
    @ColumnInfo(name="Metering Direction", descr="00: reception, 01: transmission, 02: reception - transmission, 03: reception + transmission")
    private String meterDirection;

    @Column(name="meter_time", length=14)
    @ColumnInfo(name="Meter time", descr="Meter time. YYYYMMDDhhmmss")
    private String meterTime;

    @Column(name="lcd_disp_content", length=128)
    @ColumnInfo(name="LCD Display contents", descr="LCD Display contents")
    private String lcdDispContent;

    @Column(name="meter_kind", length=2)
    @ColumnInfo(name="Metering kind", descr="Metering kind. 0 : postpaid, 1 : prepaid")
    private String meterKind;
    
	@Column(name="CPU_RESET_RAM")
    @ColumnInfo(name="CPU Reset Count (RAM)", descr="0 – 255")
    private Integer cpuResetRam;
    
    @Column(name="CPU_RESET_ROM")
    @ColumnInfo(name="CPU Reset Count (ROM)", descr="0 – 255")
    private Integer cpuResetRom;
    
    @Column(name="WDT_RESET_RAM")
    @ColumnInfo(name="WDT Reset Count (RAM)", descr="0 – 255")
    private Integer wdtResetRam;
    
    @Column(name="WDT_RESET_ROM")
    @ColumnInfo(name="WDT Reset Count (ROM)", descr="0 – 255")
    private Integer wdtResetRom;
    
    @Column(name="LP1_TIMING")
    @ColumnInfo(name="LP1 timing (min)", descr="15 or 30 or 60")
    private Integer lp1Timing;
    
    @Column(name="LP2_PATTERN", length=2)
    @ColumnInfo(name="LP2 Pattern", descr="“A” or “B”.") 
    private String lp2Pattern;
    
    @Column(name="LP2_TIMING")
    @ColumnInfo(name="LP2 timing (min)", descr="5 or 10 or 15 or 30 or 60")
    private Integer lp2Timing;

    @Column(name="QUALITY_SIDE", length=2)
    @ColumnInfo(name="Quality side", descr="00 : Main, 01 : Neutral")
    private String qualitySide;

    @Column(name="QUALITY_ACTIVEPOWER_A")
    @ColumnInfo(name="Active Power 1(kW)", descr="-2147483648 – 2147483647")
    private Double qualityActivePowerA;
    
    @Column(name="QUALITY_ACTIVEPOWER_B")
    @ColumnInfo(name="Active Power 3(kW)", descr="-2147483648 – 2147483647")
    private Double qualityActivePowerB;
    
    @Column(name="QUALITY_REACTIVEPOWER_A")
    @ColumnInfo(name="Reactive Power 1(kVAR)", descr="-2147483648 – 2147483647")
    private Double qualityReactivePowerA;
    
    @Column(name="QUALITY_REACTIVEPOWER_B")
    @ColumnInfo(name="Reactive Power 3(kVAR)", descr="-2147483648 – 2147483647")
    private Double qualityReactivePowerB;
    
    @Column(name="QUALITY_VOL_A")
    @ColumnInfo(name="Voltage 1 (V)", descr="0 – 2147483647")
    private Double qualityVolA;
    
    @Column(name="QUALITY_VOL_B")
    @ColumnInfo(name="Voltage 3 (V)", descr="0 – 2147483647")
    private Double qualityVolB;
    
    @Column(name="QUALITY_CURRENT_A")
    @ColumnInfo(name="Current 1 (A)", descr="-2147483648 – 2147483647")
    private Double qualityCurrentA;
    
    @Column(name="QUALITY_CURRENT_B")
    @ColumnInfo(name="Current 3 (A)", descr="-2147483648 – 2147483647")
    private Double qualityCurrentB;
    
    @Column(name="QUALITY_KVA_A")
    @ColumnInfo(name="Apparent Power 1 (kVA)", descr="-2147483648 – 2147483647")
    private Double qualityKvaA;
    
    @Column(name="QUALITY_KVA_B")
    @ColumnInfo(name="Apparent Power 3 (kVA)", descr="-2147483648 – 2147483647")
    private Double qualityKvaB;
    
    @Column(name="QUALITY_PF_A")
    @ColumnInfo(name="Power Factor 1 (%)", descr="-2147483648 – 2147483647")
    private Double qualityPfA;
    
    @Column(name="QUALITY_PF_B")
    @ColumnInfo(name="Power Factor 3 (%)", descr="-2147483648 – 2147483647")
    private Double qualityPfB;
    
    @Column(name="QUALITY_FREQUENCY_A")
    @ColumnInfo(name="Frequency 1 (Hz)", descr="0 – 2147483647")
    private Double qualityFrequencyA;
    
    @Column(name="QUALITY_FREQUENCY_B")
    @ColumnInfo(name="Frequency 3 (Hz)", descr="0 – 2147483647")
    private Double qualityFrequencyB;

    @Column(name="LCD_DISP_SCROLL", length=2)
    @ColumnInfo(name="Display Scroll type", descr="00 : Manual Scroll, 01 : Auto Scroll, 02 : Manual/Auto Scroll")
    private String lcdDispScroll;

    @Column(name="LCD_DISP_CYCLE_POST")
    @ColumnInfo(name="Cyclic time (sec) for Postpaid meter", descr="1-15")
    private Integer lcdDispCyclePost;

    @Column(name="LCD_DISP_CONTENT_POST", length=16)
    @ColumnInfo(name="Display items (16bytes) for Postpaid meter", descr="16bytes")
    private String lcdDispContentPost;

    @Column(name="LCD_DISP_CYCLE_PRE")
    @ColumnInfo(name="Cyclic time (sec) for Prepaid meter", descr="1-15")
    private Integer lcdDispCyclePre;

    @Column(name="LCD_DISP_CONTENT_PRE", length=16)
    @ColumnInfo(name="Display items (16bytes) for Prepaid meter", descr="16bytes")
    private String lcdDispContentPre;

    @Column(name="PREPAID_DEPOSIT")
    @ColumnInfo(name="Prepaid value. [KWh]", descr="-999999999 ~ 999999999")
    private Integer prepaidDeposit;

    public Integer getCpuResetRam() {
		return cpuResetRam;
	}

	public void setCpuResetRam(Integer cpuResetRam) {
		this.cpuResetRam = cpuResetRam;
	}

	public Integer getCpuResetRom() {
		return cpuResetRom;
	}

	public void setCpuResetRom(Integer cpuResetRom) {
		this.cpuResetRom = cpuResetRom;
	}

	public Integer getWdtResetRam() {
		return wdtResetRam;
	}

	public void setWdtResetRam(Integer wdtResetRam) {
		this.wdtResetRam = wdtResetRam;
	}

	public Integer getWdtResetRom() {
		return wdtResetRom;
	}

	public void setWdtResetRom(Integer wdtResetRom) {
		this.wdtResetRom = wdtResetRom;
	}

	public Integer getLp1Timing() {
		return lp1Timing;
	}

	public void setLp1Timing(Integer lp1Timing) {
		this.lp1Timing = lp1Timing;
	}

	public String getLp2Pattern() {
		return lp2Pattern;
	}

	public void setLp2Pattern(String lp2Pattern) {
		this.lp2Pattern = lp2Pattern;
	}

	public Integer getLp2Timing() {
		return lp2Timing;
	}

	public void setLp2Timing(Integer lp2Timing) {
		this.lp2Timing = lp2Timing;
	}

	public Double getQualityActivePowerA() {
		return qualityActivePowerA;
	}

	public void setQualityActivePowerA(Double qualityActivePowerA) {
		this.qualityActivePowerA = qualityActivePowerA;
	}

	public Double getQualityActivePowerB() {
		return qualityActivePowerB;
	}

	public void setQualityActivePowerB(Double qualityActivePowerB) {
		this.qualityActivePowerB = qualityActivePowerB;
	}

	public Double getQualityReactivePowerA() {
		return qualityReactivePowerA;
	}

	public void setQualityReactivePowerA(Double qualityReactivePowerA) {
		this.qualityReactivePowerA = qualityReactivePowerA;
	}

	public Double getQualityReactivePowerB() {
		return qualityReactivePowerB;
	}

	public void setQualityReactivePowerB(Double qualityReactivePowerB) {
		this.qualityReactivePowerB = qualityReactivePowerB;
	}

	public Double getQualityVolA() {
		return qualityVolA;
	}

	public void setQualityVolA(Double qualityVolA) {
		this.qualityVolA = qualityVolA;
	}

	public Double getQualityVolB() {
		return qualityVolB;
	}

	public void setQualityVolB(Double qualityVolB) {
		this.qualityVolB = qualityVolB;
	}

	public Double getQualityCurrentA() {
		return qualityCurrentA;
	}

	public void setQualityCurrentA(Double qualityCurrentA) {
		this.qualityCurrentA = qualityCurrentA;
	}

	public Double getQualityCurrentB() {
		return qualityCurrentB;
	}

	public void setQualityCurrentB(Double qualityCurrentB) {
		this.qualityCurrentB = qualityCurrentB;
	}

	public Double getQualityKvaA() {
		return qualityKvaA;
	}

	public void setQualityKvaA(Double qualityKvaA) {
		this.qualityKvaA = qualityKvaA;
	}

	public Double getQualityKvaB() {
		return qualityKvaB;
	}

	public void setQualityKvaB(Double qualityKvaB) {
		this.qualityKvaB = qualityKvaB;
	}

	public Double getQualityPfA() {
		return qualityPfA;
	}

	public void setQualityPfA(Double qualityPfA) {
		this.qualityPfA = qualityPfA;
	}

	public Double getQualityPfB() {
		return qualityPfB;
	}

	public void setQualityPfB(Double qualityPfB) {
		this.qualityPfB = qualityPfB;
	}

	public Double getQualityFrequencyA() {
		return qualityFrequencyA;
	}

	public void setQualityFrequencyA(Double qualityFrequencyA) {
		this.qualityFrequencyA = qualityFrequencyA;
	}

	public Double getQualityFrequencyB() {
		return qualityFrequencyB;
	}

	public void setQualityFrequencyB(Double qualityFrequencyB) {
		this.qualityFrequencyB = qualityFrequencyB;
	}

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the meter
     */
//    public Meter getMeter() {
//        return meter;
//    }

    /**
     * @param meter the meter to set
     */
//    public void setMeter(Meter meter) {
//        this.meter = meter;
//    }

    /**
     * @return the tampBypass
     */
    public Integer getTampBypass() {
        return tampBypass;
    }

    /**
     * @param tampBypass the tampBypass to set
     */
    public void setTampBypass(Integer tampBypass) {
        this.tampBypass = tampBypass;
    }

    /**
     * @return the tampEarthLd
     */
    public Integer getTampEarthLd() {
        return tampEarthLd;
    }

    /**
     * @param tampEarthLd the tampEarthLd to set
     */
    public void setTampEarthLd(Integer tampEarthLd) {
        this.tampEarthLd = tampEarthLd;
    }

    /**
     * @return the tampReverse
     */
    public Integer getTampReverse() {
        return tampReverse;
    }

    /**
     * @param tampReverse the tampReverse to set
     */
    public void setTampReverse(Integer tampReverse) {
        this.tampReverse = tampReverse;
    }

    /**
     * @return the tampCoverOp
     */
    public Integer getTampCoverOp() {
        return tampCoverOp;
    }

    /**
     * @param tampCoverOp the tampCoverOp to set
     */
    public void setTampCoverOp(Integer tampCoverOp) {
        this.tampCoverOp = tampCoverOp;
    }

    /**
     * @return the tampFrontOp
     */
    public Integer getTampFrontOp() {
        return tampFrontOp;
    }

    /**
     * @param tampFrontOp the tampFrontOp to set
     */
    public void setTampFrontOp(Integer tampFrontOp) {
        this.tampFrontOp = tampFrontOp;
    }

    /**
     * @return the prepaidAlertLevel1
     */
    public Integer getPrepaidAlertLevel1() {
        return prepaidAlertLevel1;
    }

    /**
     * @param prepaidAlertLevel1 the prepaidAlertLevel1 to set
     */
    public void setPrepaidAlertLevel1(Integer prepaidAlertLevel1) {
        this.prepaidAlertLevel1 = prepaidAlertLevel1;
    }

    /**
     * @return the prepaidAlertLevel2
     */
    public Integer getPrepaidAlertLevel2() {
        return prepaidAlertLevel2;
    }

    /**
     * @param prepaidAlertLevel2 the prepaidAlertLevel2 to set
     */
    public void setPrepaidAlertLevel2(Integer prepaidAlertLevel2) {
        this.prepaidAlertLevel2 = prepaidAlertLevel2;
    }

    /**
     * @return the prepaidAlertLevel3
     */
    public Integer getPrepaidAlertLevel3() {
        return prepaidAlertLevel3;
    }

    /**
     * @param prepaidAlertLevel3 the prepaidAlertLevel3 to set
     */
    public void setPrepaidAlertLevel3(Integer prepaidAlertLevel3) {
        this.prepaidAlertLevel3 = prepaidAlertLevel3;
    }

    /**
     * @return the prepaidAlertStart
     */
    public Integer getPrepaidAlertStart() {
        return prepaidAlertStart;
    }

    /**
     * @param prepaidAlertStart the prepaidAlertStart to set
     */
    public void setPrepaidAlertStart(Integer prepaidAlertStart) {
        this.prepaidAlertStart = prepaidAlertStart;
    }

    /**
     * @return the prepaidAlertOff
     */
    public Integer getPrepaidAlertOff() {
        return prepaidAlertOff;
    }

    /**
     * @param prepaidAlertOff the prepaidAlertOff to set
     */
    public void setPrepaidAlertOff(Integer prepaidAlertOff) {
        this.prepaidAlertOff = prepaidAlertOff;
    }

    /**
     * @return the meterDirection
     */
    public String getMeterDirection() {
        return meterDirection;
    }

    /**
     * @param meterDirection the meterDirection to set
     */
    public void setMeterDirection(String meterDirection) {
        this.meterDirection = meterDirection;
    }

    /**
     * @return the meterTime
     */
    public String getMeterTime() {
        return meterTime;
    }

    /**
     * @param meterTime the meterTime to set
     */
    public void setMeterTime(String meterTime) {
        this.meterTime = meterTime;
    }

    /**
     * @return the lcdDispContent
     */
    public String getLcdDispContent() {
        return lcdDispContent;
    }

    /**
     * @param lcdDispContent the lcdDispContent to set
     */
    public void setLcdDispContent(String lcdDispContent) {
        this.lcdDispContent = lcdDispContent;
    }

    /**
     * @return the meterKind
     */
    public String getMeterKind() {
        return meterKind;
    }

    /**
     * @param meterKind the meterKind to set
     */
    public void setMeterKind(String meterKind) {
        this.meterKind = meterKind;
    }

    /**
     * @return the qualitySide
     */
    public String getQualitySide() {
        return qualitySide;
    }

    /**
     * @param qualitySide the qualitySide to set
     */
    public void setQualitySide(String qualitySide) {
        this.qualitySide = qualitySide;
    }

    /**
     * @return the lcdDispScroll
     */
    public String getLcdDispScroll() {
        return lcdDispScroll;
    }

    /**
     * @param lcdDispScroll the lcdDispScroll to set
     */
    public void setLcdDispScroll(String lcdDispScroll) {
        this.lcdDispScroll = lcdDispScroll;
    }

    /**
     * @return the lcdDispCyclePost
     */
    public Integer getLcdDispCyclePost() {
        return lcdDispCyclePost;
    }

    /**
     * @param lcdDispCyclePost the lcdDispCyclePost to set
     */
    public void setLcdDispCyclePost(Integer lcdDispCyclePost) {
        this.lcdDispCyclePost = lcdDispCyclePost;
    }

    /**
     * @return the lcdDispContentPost
     */
    public String getLcdDispContentPost() {
        return lcdDispContentPost;
    }

    /**
     * @param lcdDispContentPost the lcdDispContentPost to set
     */
    public void setLcdDispContentPost(String lcdDispContentPost) {
        this.lcdDispContentPost = lcdDispContentPost;
    }

    /**
     * @return the lcdDispCyclePre
     */
    public Integer getLcdDispCyclePre() {
        return lcdDispCyclePre;
    }

    /**
     * @param lcdDispCyclePre the lcdDispCyclePre to set
     */
    public void setLcdDispCyclePre(Integer lcdDispCyclePre) {
        this.lcdDispCyclePre = lcdDispCyclePre;
    }

    /**
     * @return the lcdDispContentPre
     */
    public String getLcdDispContentPre() {
        return lcdDispContentPre;
    }

    /**
     * @param lcdDispContentPre the lcdDispContentPre to set
     */
    public void setLcdDispContentPre(String lcdDispContentPre) {
        this.lcdDispContentPre = lcdDispContentPre;
    }

    /**
     * @return the prepaidDeposit
     */
    public Integer getPrepaidDeposit() {
        return prepaidDeposit;
    }

    /**
     * @param prepaidDeposit the prepaidDeposit to set
     */
    public void setPrepaidDeposit(Integer prepaidDeposit) {
        this.prepaidDeposit = prepaidDeposit;
    }

    public String toString() {
        return "MdisMeter "+toJSONString();
    }

    public String toJSONString() {
        String retValue = "";

        retValue = "{"          
            + "id:'" + this.id       
//            + "',meter:'" + ((this.meter == null)? "":this.meter.getId())
            + "',tampBypass:'" + this.tampBypass
            + "',tampEarthLd:'" + this.tampEarthLd
            + "',tampReverse:'" + this.tampReverse
            + "',tampCoverOp:'" + this.tampCoverOp
            + "',tampFrontOp:'" + this.tampFrontOp
            + "',prepaidAlertLevel1:'" + this.prepaidAlertLevel1
            + "',prepaidAlertLevel2:'" + this.prepaidAlertLevel2
            + "',prepaidAlertLevel3:'" + this.prepaidAlertLevel3
            + "',prepaidAlertStart:'" + this.prepaidAlertStart
            + "',prepaidAlertOff:'" + this.prepaidAlertOff
            + "',meterDirection:'" + this.meterDirection
            + "',meterTime:'" + this.meterTime
            + "',lcdDispContent:'" + this.lcdDispContent
            + "',meterKind:'" + ((this.meterKind != null) ? this.meterKind : "")
            + "',cpuResetRam:'" + this.cpuResetRam
            + "',cpuResetRom:'" + this.cpuResetRom
            + "',wdtResetRam:'" + this.wdtResetRam
            + "',wdtResetRom:'" + this.wdtResetRom
            + "',lp1Timing:'" + this.lp1Timing
            + "',lp2Pattern:'" + this.lp2Pattern
            + "',lp2Timing:'" + this.lp2Timing
            + "',qualitySide:'" + this.qualitySide
            + "',qualityActivePowerA:'" + this.qualityActivePowerA
            + "',qualityActivePowerB:'" + this.qualityActivePowerB
            + "',qualityReactivePowerA:'" + this.qualityReactivePowerA
            + "',qualityReactivePowerB:'" + this.qualityReactivePowerB
            + "',qualityVolA:'" + this.qualityVolA
            + "',qualityVolB:'" + this.qualityVolB
            + "',qualityCurrentA:'" + this.qualityCurrentA
            + "',qualityCurrentB:'" + this.qualityCurrentB
            + "',qualityKvaA:'" + this.qualityKvaA
            + "',qualityKvaB:'" + this.qualityKvaB 
            + "',qualityPfA:'" + this.qualityPfA
            + "',qualityPfB:'" + this.qualityPfB
            + "',qualityFrequencyA:'" + this.qualityFrequencyA
            + "',qualityFrequencyB:'" + this.qualityFrequencyB
            + "',lcdDispScroll:'" + ((this.lcdDispScroll != null) ? this.lcdDispScroll : "")
//            + "',lcdDispCyclePost:'" + ((this.lcdDispCyclePost != null) ? this.lcdDispCyclePost : "")
            + "',lcdDispCyclePost:'" + this.lcdDispCyclePost
            + "',lcdDispContentPost:'" + ((this.lcdDispContentPost != null) ? this.lcdDispContentPost : "")
            + "',lcdDispCyclePre:'" + this.lcdDispCyclePre
            + "',lcdDispContentPre:'" + ((this.lcdDispContentPre != null) ? this.lcdDispContentPre : "")
            + "',prepaidDeposit:'" + this.prepaidDeposit
            + "'}";

        return retValue;
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