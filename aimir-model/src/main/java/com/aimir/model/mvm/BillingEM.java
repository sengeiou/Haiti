package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.model.device.EndDevice;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

/**
 * 전기 미터의 (TOU를 적용한) 빌링 메타 데이터
 *  
 * Date          Version     Author   Description
 * -              V1.0       YeonKyoung Park(goodjob)         신규작성 
 * 2011. 4. 25.   v1.1       eunmiae  Co2배출량, 마일리지 관련 항목 추가     
 */

@MappedSuperclass
public abstract class BillingEM {

    @EmbeddedId public BillingPk id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable=false)
    @ReferencedBy(name="name")
    private Supplier supplier;  
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="location_id", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
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
    
    @Column(length=14)
    @ColumnInfo(name="데이터 작성시간")
    private String writeDate;

    @ColumnInfo(descr="total 유효사용량")
    @Column(name="activeEnergyRateTot")
    private Double activeEnergyRateTotal;
    
    @ColumnInfo(descr="total 무효사용량")
    @Column(name="reactiveEnergyRateTot")
    private Double reactiveEnergyRateTotal;     
    
    @ColumnInfo(descr="total 유효 전력 최대 ")
    @Column(name="activePwrMaxDmdRateTot")
    private Double activePowerMaxDemandRateTotal;
    
    @ColumnInfo(descr="total 유효 전력 최대  시간")
    @Column(name="activePwrDmdMaxTimeRateTot",length=12) 
    private String activePowerDemandMaxTimeRateTotal;
    
    @ColumnInfo(descr="total 무효 전력 최대 ")
    @Column(name="reactivePwrMaxDmdRateTot")
    private Double reactivePowerMaxDemandRateTotal;
    
    @ColumnInfo(descr="total 무효 전력 최대  시간")
    @Column(name="reactivePwrDmdMaxTimeRateTot",length=12) 
    private String reactivePowerDemandMaxTimeRateTotal;
    
    @ColumnInfo(descr="total 유효 누적 전력 ")
    @Column(name="cummActivePwrDmdRateTot")
    private Double cumulativeActivePowerDemandRateTotal;
    
    @ColumnInfo(descr="total 무효 누적 전력 ")
    @Column(name="cummReactivePwrDmdRateTot")
    private Double cumulativeReactivePowerDemandRateTotal;
    
    @ColumnInfo(descr="Rate1 유효사용량")
    private Double activeEnergyRate1;
    
    @ColumnInfo(descr="Rate1 무효사용량")
    private Double reactiveEnergyRate1;     
    
    @ColumnInfo(descr="Rate1 유효 전력 최대 ")    
    @Column(name="activePwrMaxDmdRate1")
    private Double activePowerMaxDemandRate1 ;
    
    @ColumnInfo(descr="Rate1 유효 전력 최대  시간")
    @Column(name="activePwrDmdMaxTimeRate1",length=12) 
    private String activePowerDemandMaxTimeRate1 ;
    
    @ColumnInfo(descr="Rate1 무효 전력 최대 ")
    @Column(name="reactivePwrMaxDmdRate1")
    private Double reactivePowerMaxDemandRate1 ;
    
    @ColumnInfo(descr="Rate1 무효 전력 최대  시간")
    @Column(name="reactivePwrDmdMaxTimeRate1",length=12) 
    private String reactivePowerDemandMaxTimeRate1 ;
    
    @ColumnInfo(descr="Rate1 유효 누적 전력 ")
    @Column(name="cummActivePwrDmdRate1")
    private Double cumulativeActivePowerDemandRate1 ;
    
    @ColumnInfo(descr="Rate1 무효 누적 전력 ")
    @Column(name="cummReactivePwrDmdRate1")
    private Double cumulativeReactivePowerDemandRate1 ;
    
    @ColumnInfo(descr="Rate2 유효사용량")
    private Double activeEnergyRate2;
    
    @ColumnInfo(descr="Rate2 무효사용량")
    private Double reactiveEnergyRate2;     
    
    @ColumnInfo(descr="Rate2 유효 전력 최대 ")
    @Column(name="activePwrMaxDmdRate2")
    private Double activePowerMaxDemandRate2 ;
    
    @ColumnInfo(descr="Rate2 유효 전력 최대  시간")
    @Column(name="activePwrDmdMaxTimeRate2",length=12) 
    private String activePowerDemandMaxTimeRate2 ;
    
    @ColumnInfo(descr="Rate2 무효 전력 최대 ")
    @Column(name="reactivePwrMaxDmdRate2")
    private Double reactivePowerMaxDemandRate2 ;
    
    @ColumnInfo(descr="Rate2 무효 전력 최대  시간")
    @Column(name="reactivePwrDmdMaxTimeRate2",length=12) 
    private String reactivePowerDemandMaxTimeRate2 ;
    
    @ColumnInfo(descr="Rate2 유효 누적 전력 ")
    @Column(name="cummActivePwrDmdRate2")
    private Double cumulativeActivePowerDemandRate2 ;
    
    @ColumnInfo(descr="Rate2 무효 누적 전력 ")
    @Column(name="cummReactivePwrDmdRate2")
    private Double cumulativeReactivePowerDemandRate2 ;

    @ColumnInfo(descr="Rate3 유효사용량")
    private Double activeEnergyRate3;
    
    @ColumnInfo(descr="Rate3 무효사용량")
    private Double reactiveEnergyRate3;     
    
    @ColumnInfo(descr="Rate3 유효 전력 최대 ")
    @Column(name="activePwrMaxDmdRate3")
    private Double activePowerMaxDemandRate3 ;
    
    @ColumnInfo(descr="Rate3 유효 전력 최대  시간")
    @Column(name="activePwrDmdMaxTimeRate3",length=12) 
    private String activePowerDemandMaxTimeRate3 ;
    
    @ColumnInfo(descr="Rate3 무효 전력 최대 ")
    @Column(name="reactivePwrMaxDmdRate3")
    private Double reactivePowerMaxDemandRate3 ;
    
    @ColumnInfo(descr="Rate3 무효 전력 최대  시간")
    @Column(name="reactivePwrDmdMaxTimeRate3",length=12) 
    private String reactivePowerDemandMaxTimeRate3 ;
    
    @ColumnInfo(descr="Rate3 유효 누적 전력 ")
    @Column(name="cummActivePwrDmdRate3")
    private Double cumulativeActivePowerDemandRate3 ;
    
    @ColumnInfo(descr="Rate3 무효 누적 전력 ")
    @Column(name="cummReactivePwrDmdRate3")
    private Double cumulativeReactivePowerDemandRate3 ;
    
    @ColumnInfo(descr="Rate4 유효사용량")
    private Double activeEnergyRate4;
    
    @ColumnInfo(descr="Rate4 무효사용량")
    private Double reactiveEnergyRate4;     
    
    @ColumnInfo(descr="Rate4 유효 전력 최대 ")
    @Column(name="activePwrMaxDmdRate4")
    private Double activePowerMaxDemandRate4 ;
    
    @ColumnInfo(descr="Rate4 유효 전력 최대  시간")
    @Column(name="activePwrDmdMaxTimeRate4",length=12) 
    private String activePowerDemandMaxTimeRate4 ;
    
    @ColumnInfo(descr="Rate4 무효 전력 최대 ")
    @Column(name="reactivePwrMaxDmdRate4")
    private Double reactivePowerMaxDemandRate4 ;
    
    @ColumnInfo(descr="Rate4 무효 전력 최대  시간")
    @Column(name="reactivePwrDmdMaxTimeRate4",length=12) 
    private String reactivePowerDemandMaxTimeRate4 ;
    
    @ColumnInfo(descr="Rate4 유효 누적 전력 ")
    @Column(name="cummActivePwrDmdRate4")
    private Double cumulativeActivePowerDemandRate4 ;
    
    @ColumnInfo(descr="Rate4 무효 누적 전력 ")
    @Column(name="cummReactivePwrDmdRate4")
    private Double cumulativeReactivePowerDemandRate4 ;
    
    @ColumnInfo(descr="Cumulative total registers KVAH ")
    @Column(name="kvah")
    private Double kVah ;

    private Double activeEnergyImportRateTotal;
    private Double activeEnergyExportRateTotal;

    @Column(name="rtvEnergyLagImpRateTot")
    private Double reactiveEnergyLagImportRateTotal;
    @Column(name="rtvEnergyLeadImpRateTot")
    private Double reactiveEnergyLeadImportRateTotal;   
    @Column(name="rtvEnergyLagExpRateTot")
    private Double reactiveEnergyLagExportRateTotal;
    @Column(name="rtvEnergyLeadExpRateTot")
    private Double reactiveEnergyLeadExportRateTotal;
    
    private Double activeEnergyImportRate1;
    private Double activeEnergyExportRate1;
    private Double reactiveEnergyLagImportRate1;    
    private Double reactiveEnergyLeadImportRate1;   
    private Double reactiveEnergyLagExportRate1;    
    private Double reactiveEnergyLeadExportRate1;
    private Double activeEnergyImportRate2;
    private Double activeEnergyExportRate2;
    private Double reactiveEnergyLagImportRate2;    
    private Double reactiveEnergyLeadImportRate2;   
    private Double reactiveEnergyLagExportRate2;    
    private Double reactiveEnergyLeadExportRate2;
    private Double activeEnergyImportRate3;
    private Double activeEnergyExportRate3;
    private Double reactiveEnergyLagImportRate3;    
    private Double reactiveEnergyLeadImportRate3;   
    private Double reactiveEnergyLagExportRate3;    
    private Double reactiveEnergyLeadExportRate3;
    private Double activeEnergyImportRate4;
    private Double activeEnergyExportRate4;
    private Double reactiveEnergyLagImportRate4;    
    private Double reactiveEnergyLeadImportRate4;   
    private Double reactiveEnergyLagExportRate4;    
    private Double reactiveEnergyLeadExportRate4;
    private Double activePwrDmdMaxImportRateTotal;
    private Double activePwrDmdMaxExportRateTotal;
    
    @Column(name="rtvPwrDmdMaxLagImpRateTot")
    private Double reactivePwrDmdMaxLagImportRateTotal;
    @Column(name="rtvPwrDmdMaxLeadImpRateTot")
    private Double reactivePwrDmdMaxLeadImportRateTotal;
    @Column(name="rtvPwrDmdMaxLagExpRateTot")
    private Double reactivePwrDmdMaxLagExportRateTotal;
    @Column(name="rtvPwrDmdMaxLeadExpRateTot")
    private Double reactivePwrDmdMaxLeadExportRateTotal;
    @Column(name="atvPwrDmdMaxTimeImpRateTot")
    private String activePwrDmdMaxTimeImportRateTotal;
    @Column(name="atvPwrDmdMaxTimeExpRateTot")
    private String activePwrDmdMaxTimeExportRateTotal;
    @Column(name="rtvPwrDmdMaxTimeLagImpRateTot")
    private String reactivePwrDmdMaxTimeLagImportRateTotal;
    @Column(name="rtvPwrDmdMaxTimeLeadImpRateTot")
    private String reactivePwrDmdMaxTimeLeadImportRateTotal;
    @Column(name="rtvPwrDmdMaxTimeLagExpRateTot")
    private String reactivePwrDmdMaxTimeLagExportRateTotal;
    @Column(name="rtvPwrDmdMaxTimeLeadExpRateTot")
    private String reactivePwrDmdMaxTimeLeadExportRateTotal;
    
    private Double activePwrDmdMaxImportRate1;
    private Double activePwrDmdMaxExportRate1;

    @Column(name="rtvPwrDmdMaxLagImpRate1")
    private Double reactivePwrDmdMaxLagImportRate1;
    @Column(name="rtvPwrDmdMaxLeadImpRate1")
    private Double reactivePwrDmdMaxLeadImportRate1;
    @Column(name="rtvPwrDmdMaxLagExpRate1")
    private Double reactivePwrDmdMaxLagExportRate1;
    @Column(name="rtvPwrDmdMaxLeadExpRate1")
    private Double reactivePwrDmdMaxLeadExportRate1;
    
    private String activePwrDmdMaxTimeImportRate1;
    private String activePwrDmdMaxTimeExportRate1;
    
    @Column(name="rtvPwrDmdMaxTimeLagImpRate1")
    private String reactivePwrDmdMaxTimeLagImportRate1; 
    @Column(name="rtvPwrDmdMaxTimeLeadImpRate1")
    private String reactivePwrDmdMaxTimeLeadImportRate1;
    @Column(name="rtvPwrDmdMaxTimeLagExpRate1")
    private String reactivePwrDmdMaxTimeLagExportRate1;
    @Column(name="rtvPwrDmdMaxTimeLeadExpRate1")
    private String reactivePwrDmdMaxTimeLeadExportRate1;

    private Double activePwrDmdMaxImportRate2;
    private Double activePwrDmdMaxExportRate2;

    @Column(name="rtvPwrDmdMaxLagImpRate2")
    private Double reactivePwrDmdMaxLagImportRate2; 
    @Column(name="rtvPwrDmdMaxLeadImpRate2")
    private Double reactivePwrDmdMaxLeadImportRate2;
    @Column(name="rtvPwrDmdMaxLagExpRate2")
    private Double reactivePwrDmdMaxLagExportRate2;
    @Column(name="rtvPwrDmdMaxLeadExpRate2")
    private Double reactivePwrDmdMaxLeadExportRate2;
    
    private String activePwrDmdMaxTimeImportRate2;
    private String activePwrDmdMaxTimeExportRate2;
    
    @Column(name="rtvPwrDmdMaxTimeLagImpRate2")
    private String reactivePwrDmdMaxTimeLagImportRate2;
    @Column(name="rtvPwrDmdMaxTimeLeadImpRate2")
    private String reactivePwrDmdMaxTimeLeadImportRate2;
    @Column(name="rtvPwrDmdMaxTimeLagExpRate2")
    private String reactivePwrDmdMaxTimeLagExportRate2;
    @Column(name="rtvPwrDmdMaxTimeLeadExpRate2")
    private String reactivePwrDmdMaxTimeLeadExportRate2;
    
    private Double activePwrDmdMaxImportRate3;
    private Double activePwrDmdMaxExportRate3;
    
    @Column(name="rtvPwrDmdMaxLagImpRate3")
    private Double reactivePwrDmdMaxLagImportRate3;
    @Column(name="rtvPwrDmdMaxLeadImpRate3")
    private Double reactivePwrDmdMaxLeadImportRate3;
    @Column(name="rtvPwrDmdMaxLagExpRate3")
    private Double reactivePwrDmdMaxLagExportRate3;
    @Column(name="rtvPwrDmdMaxLeadExpRate3")
    private Double reactivePwrDmdMaxLeadExportRate3;

    private String activePwrDmdMaxTimeImportRate3;
    private String activePwrDmdMaxTimeExportRate3;
    
    @Column(name="rtvPwrDmdMaxTimeLagImpRate3")
    private String reactivePwrDmdMaxTimeLagImportRate3; 
    @Column(name="rtvPwrDmdMaxTimeLeadImpRate3")
    private String reactivePwrDmdMaxTimeLeadImportRate3;
    @Column(name="rtvPwrDmdMaxTimeLagExpRate3")
    private String reactivePwrDmdMaxTimeLagExportRate3;
    @Column(name="rtvPwrDmdMaxTimeLeadExpRate3")
    private String reactivePwrDmdMaxTimeLeadExportRate3;
    
    private Double activePwrDmdMaxImportRate4;
    private Double activePwrDmdMaxExportRate4;
    
    @Column(name="rtvPwrDmdMaxLagImpRate4")
    private Double reactivePwrDmdMaxLagImportRate4;
    @Column(name="rtvPwrDmdMaxLeadImpRate4")
    private Double reactivePwrDmdMaxLeadImportRate4;
    @Column(name="rtvPwrDmdMaxLagExpRate4")
    private Double reactivePwrDmdMaxLagExportRate4;
    @Column(name="rtvPwrDmdMaxLeadExpRate4")
    private Double reactivePwrDmdMaxLeadExportRate4;

    private String activePwrDmdMaxTimeImportRate4;
    private String activePwrDmdMaxTimeExportRate4;
    
    @Column(name="rtvPwrDmdMaxTimeLagImpRate4")
    private String reactivePwrDmdMaxTimeLagImportRate4;
    @Column(name="rtvPwrDmdMaxTimeLeadImpRate4")
    private String reactivePwrDmdMaxTimeLeadImportRate4;
    @Column(name="rtvPwrDmdMaxTimeLagExpRate4")
    private String reactivePwrDmdMaxTimeLagExportRate4;
    @Column(name="rtvPwrDmdMaxTimeLeadExpRate4")
    private String reactivePwrDmdMaxTimeLeadExportRate4;
    @Column(name="cummAtvPwrDmdMaxImpRateTot")
    private Double cummActivePwrDmdMaxImportRateTotal;
    @Column(name="cummAtvPwrDmdMaxExpRateTot")
    private Double cummActivePwrDmdMaxExportRateTotal;
    @Column(name="cummRtvPwrDmdMaxLagImpRateTot")
    private Double cummReactivePwrDmdMaxLagImportRateTotal;
    @Column(name="cummRtvPwrDmdMaxLeadImpRateTot")
    private Double cummReactivePwrDmdMaxLeadImportRateTotal;
    @Column(name="cummRtvPwrDmdMaxLagExpRateTot")
    private Double cummReactivePwrDmdMaxLagExportRateTotal;
    @Column(name="cummRtvPwrDmdMaxLeadExpRateTot")
    private Double cummReactivePwrDmdMaxLeadExportRateTotal;

    private Double cummActivePwrDmdMaxImportRate1;
    private Double cummActivePwrDmdMaxExportRate1;
    
    @Column(name="cummRtvPwrDmdMaxLagImpRate1")
    private Double cummReactivePwrDmdMaxLagImportRate1;
    @Column(name="cummRtvPwrDmdMaxLeadImpRate1")
    private Double cummReactivePwrDmdMaxLeadImportRate1;
    @Column(name="cummRtvPwrDmdMaxLagExpRate1")
    private Double cummReactivePwrDmdMaxLagExportRate1;
    @Column(name="cummRtvPwrDmdMaxLeadExpRate1")
    private Double cummReactivePwrDmdMaxLeadExportRate1;

    private Double cummActivePwrDmdMaxImportRate2;
    private Double cummActivePwrDmdMaxExportRate2;

    @Column(name="cummRtvPwrDmdMaxLagImpRate2")
    private Double cummReactivePwrDmdMaxLagImportRate2;
    @Column(name="cummRtvPwrDmdMaxLeadImpRate2")
    private Double cummReactivePwrDmdMaxLeadImportRate2;
    @Column(name="cummRtvPwrDmdMaxLagExpRate2")
    private Double cummReactivePwrDmdMaxLagExportRate2;
    @Column(name="cummRtvPwrDmdMaxLeadExpRate2")
    private Double cummReactivePwrDmdMaxLeadExportRate2;
    
    private Double cummActivePwrDmdMaxImportRate3;
    private Double cummActivePwrDmdMaxExportRate3;
    
    @Column(name="cummRtvPwrDmdMaxLagImpRate3")
    private Double cummReactivePwrDmdMaxLagImportRate3;
    @Column(name="cummRtvPwrDmdMaxLeadImpRate3")
    private Double cummReactivePwrDmdMaxLeadImportRate3;
    @Column(name="cummRtvPwrDmdMaxLagExpRate3")
    private Double cummReactivePwrDmdMaxLagExportRate3;
    @Column(name="cummRtvPwrDmdMaxLeadExpRate3")
    private Double cummReactivePwrDmdMaxLeadExportRate3;
    
    private Double cummActivePwrDmdMaxImportRate4;
    private Double cummActivePwrDmdMaxExportRate4;
    
    @Column(name="cummRtvPwrDmdMaxLagImpRate4")
    private Double cummReactivePwrDmdMaxLagImportRate4;
    @Column(name="cummRtvPwrDmdMaxLeadImpRate4")
    private Double cummReactivePwrDmdMaxLeadImportRate4;
    @Column(name="cummRtvPwrDmdMaxLagExpRate4")
    private Double cummReactivePwrDmdMaxLagExportRate4;
    @Column(name="cummRtvPwrDmdMaxLeadExpRate4")
    private Double cummReactivePwrDmdMaxLeadExportRate4;
    
    private Double cummkVah1Rate1;
    private Double cummkVah1Rate2;
    private Double cummkVah1Rate3;
    private Double cummkVah1Rate4;
    private Double cummkVah1RateTotal;
    
    private Double maxDmdkVah1Rate1;
    private Double maxDmdkVah1Rate2;
    private Double maxDmdkVah1Rate3;
    private Double maxDmdkVah1Rate4;
    private Double maxDmdkVah1RateTotal;
    
    private String maxDmdkVah1TimeRate1;
    private String maxDmdkVah1TimeRate2;
    private String maxDmdkVah1TimeRate3;
    private String maxDmdkVah1TimeRate4;
    private String maxDmdkVah1TimeRateTotal;

    /*
     * ch1 : 유효 사용량 (kWh) ch2: 무효 사용량 (Reactive)
     *  pf   = (float)(ch1/(Math.sqrt(Math.pow(ch1,2)+Math.pow(ch2,2))));

     *   if(ch1 == 0.0 && ch2 == 0.0)
     *       pf = (double) 1.0;
     *    Parsing Transform Results put Data Class 
     *   if(pf < 0.0 || pf > 1.0)
     *       throw new Exception("BILL PF D1T1 FORM1T ERROR : "+pf);
     */
    @ColumnInfo(descr="Power Factor 값")
    private Double pf;

    @ColumnInfo(descr="실제 사용요금 ")
    private Double bill;

    @Column(name = "SEND_RESULT", length=255)
    @ColumnInfo(name="sendResult", descr="외부 연계 시스템 에 전달하는 검침값 결과  전송성공 true, 실패false ")
    private Boolean sendResult;

    /* 2011. 4. 25 v1.1 Co2배출량, 마일리지 관련 항목 추가 ADD START eunmiae */
    @ColumnInfo(descr="Demand Response or 요금 목표 절갑에 따른 절감액 잔여 마일리지")
    private Double remainingmiles;
    
    @ColumnInfo(descr="Demand Response or 요금 목표 절갑에 따른 절감액 신규축적 마일리지")
    private Double newMiles;
    
    @ColumnInfo(descr="Demand Response or 요금 목표 절갑에 따른 절감액 사용한 마일리지")
    private Double usingMiles;

    @ColumnInfo(descr="탄소 요금 절감에 따른 마일리지")
    private Double co2Miles;
    
    @ColumnInfo(descr="기타 할인요금 독립 유공자 등등")
    private Double discountedRates;
    
    @ColumnInfo(descr="TV 수신료 등의 부가 사용요금")
    private Double additionalCosts; 

    @Column(name = "CO2_EMISSIONS", length=10)
    @ColumnInfo(name="co2Emissions", descr="Co2 배출량")   
    private Double co2Emissions;
    
    private Double importkWhPhaseA;
    private Double importkWhPhaseB;
    private Double importkWhPhaseC;

    @Column(name = "USAGE_READ_FROM_DATE", length=14)
    @ColumnInfo(descr="빌링에 정보를 등록하기 위해서 사용량 정보를 읽은 시작 날짜(YYYYMMDDHHMMSS)")
    private String usageReadFromDate;

    @Column(name = "USAGE_READ_TO_DATE", length=14)
    @ColumnInfo(descr="빌링에 정보를 등록하기 위해서 사용량 정보를 읽은 종료 날짜(YYYYMMDDHHMMSS)")
    private String usageReadToDate;

    public String getUsageReadFromDate() {
        return usageReadFromDate;
    }

    public void setUsageReadFromDate(String usageReadFromDate) {
        this.usageReadFromDate = usageReadFromDate;
    }

    public String getUsageReadToDate() {
        return usageReadToDate;
    }

    public void setUsageReadToDate(String usageReadToDate) {
        this.usageReadToDate = usageReadToDate;
    }

    public Double getRemainingmiles() {
        return remainingmiles;
    }

    public void setRemainingmiles(Double remainingmiles) {
        this.remainingmiles = remainingmiles;
    }

    public Double getNewMiles() {
        return newMiles;
    }

    public void setNewMiles(Double newMiles) {
        this.newMiles = newMiles;
    }

    public Double getUsingMiles() {
        return usingMiles;
    }

    public void setUsingMiles(Double usingMiles) {
        this.usingMiles = usingMiles;
    }
    public Double getCo2Miles() {
        return co2Miles;
    }

    public void setCo2Miles(Double co2Miles) {
        this.co2Miles = co2Miles;
    }

    public Double getDiscountedRates() {
        return discountedRates;
    }

    public void setDiscountedRates(Double discountedRates) {
        this.discountedRates = discountedRates;
    }

    public Double getAdditionalCosts() {
        return additionalCosts;
    }

    public void setAdditionalCosts(Double additionalCosts) {
        this.additionalCosts = additionalCosts;
    }

    public Double getCo2Emissions() {
        return co2Emissions;
    }

    public void setCo2Emissions(Double co2Emissions) {
        this.co2Emissions = co2Emissions;
    }
    /* 2011. 4. 25 v1.1 Co2배출량, 마일리지 관련 항목 추가 ADD END eunmiae */

    public BillingEM(){
        id = new BillingPk();
    }

    public void setId(BillingPk id) {
        this.id = id;
    }
    
    public DeviceType getMDevType() {
        return id.getMDevType();
    }
    public void setMDevType(String mdevType){
        this.id.setMDevType(mdevType);
    }
    public String getMDevId() {
        return id.getMDevId();
    }
    public void setMDevId(String mdevId) {
        this.id.setMDevId(mdevId);
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
    
    @XmlTransient
    public Meter getMeter() {
        return meter;
    }
    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    @XmlTransient
    public Contract getContract() {
        return contract;
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
    
    public String getWriteDate() {
        return writeDate;
    }
    public void setWriteDate(String writeDate) {
        this.writeDate = writeDate;
    }

    public Double getActiveEnergyRateTotal() {
        return activeEnergyRateTotal;
    }

    public void setActiveEnergyRateTotal(Double activeEnergyRateTotal) {
        this.activeEnergyRateTotal = activeEnergyRateTotal;
    }

    public Double getReactiveEnergyRateTotal() {
        return reactiveEnergyRateTotal;
    }

    public void setReactiveEnergyRateTotal(Double reactiveEnergyRateTotal) {
        this.reactiveEnergyRateTotal = reactiveEnergyRateTotal;
    }

    public Double getActivePowerMaxDemandRateTotal() {
        return activePowerMaxDemandRateTotal;
    }

    public void setActivePowerMaxDemandRateTotal(
            Double activePowerMaxDemandRateTotal) {
        this.activePowerMaxDemandRateTotal = activePowerMaxDemandRateTotal;
    }

    public String getActivePowerDemandMaxTimeRateTotal() {
        return activePowerDemandMaxTimeRateTotal;
    }

    public void setActivePowerDemandMaxTimeRateTotal(
            String activePowerDemandMaxTimeRateTotal) {
        this.activePowerDemandMaxTimeRateTotal = activePowerDemandMaxTimeRateTotal;
    }

    public Double getReactivePowerMaxDemandRateTotal() {
        return reactivePowerMaxDemandRateTotal;
    }

    public void setReactivePowerMaxDemandRateTotal(
            Double reactivePowerMaxDemandRateTotal) {
        this.reactivePowerMaxDemandRateTotal = reactivePowerMaxDemandRateTotal;
    }

    public String getReactivePowerDemandMaxTimeRateTotal() {
        return reactivePowerDemandMaxTimeRateTotal;
    }

    public void setReactivePowerDemandMaxTimeRateTotal(
            String reactivePowerDemandMaxTimeRateTotal) {
        this.reactivePowerDemandMaxTimeRateTotal = reactivePowerDemandMaxTimeRateTotal;
    }

    public Double getCumulativeDemandRateTotal() {
        return cumulativeActivePowerDemandRateTotal;
    }

    public void setCumulativeDemandRateTotal(Double cumulativeActivePowerDemandRateTotal) {
        this.cumulativeActivePowerDemandRateTotal = cumulativeActivePowerDemandRateTotal;
    }

    public Double getCumulativeReactivePowerDemandRateTotal() {
        return cumulativeReactivePowerDemandRateTotal;
    }

    public void setCumulativeReactivePowerDemandRateTotal(
            Double cumulativeReactivePowerDemandRateTotal) {
        this.cumulativeReactivePowerDemandRateTotal = cumulativeReactivePowerDemandRateTotal;
    }

    public Double getActiveEnergyRate1() {
        return activeEnergyRate1;
    }

    public void setActiveEnergyRate1(Double activeEnergyRate1) {
        this.activeEnergyRate1 = activeEnergyRate1;
    }

    public Double getReactiveEnergyRate1() {
        return reactiveEnergyRate1;
    }

    public void setReactiveEnergyRate1(Double reactiveEnergyRate1) {
        this.reactiveEnergyRate1 = reactiveEnergyRate1;
    }

    public Double getActivePowerMaxDemandRate1() {
        return activePowerMaxDemandRate1;
    }

    public void setActivePowerMaxDemandRate1(Double activePowerMaxDemandRate1) {
        this.activePowerMaxDemandRate1 = activePowerMaxDemandRate1;
    }

    public String getActivePowerDemandMaxTimeRate1() {
        return activePowerDemandMaxTimeRate1;
    }

    public void setActivePowerDemandMaxTimeRate1(
            String activePowerDemandMaxTimeRate1) {
        this.activePowerDemandMaxTimeRate1 = activePowerDemandMaxTimeRate1;
    }

    public Double getReactivePowerMaxDemandRate1() {
        return reactivePowerMaxDemandRate1;
    }

    public void setReactivePowerMaxDemandRate1(Double reactivePowerMaxDemandRate1) {
        this.reactivePowerMaxDemandRate1 = reactivePowerMaxDemandRate1;
    }

    public String getReactivePowerDemandMaxTimeRate1() {
        return reactivePowerDemandMaxTimeRate1;
    }

    public void setReactivePowerDemandMaxTimeRate1(
            String reactivePowerDemandMaxTimeRate1) {
        this.reactivePowerDemandMaxTimeRate1 = reactivePowerDemandMaxTimeRate1;
    }

    public Double getCumulativeDemandRate1() {
        return cumulativeActivePowerDemandRate1;
    }

    public void setCumulativeDemandRate1(Double cumulativeActivePowerDemandRate1) {
        this.cumulativeActivePowerDemandRate1 = cumulativeActivePowerDemandRate1;
    }

    public Double getCumulativeReactivePowerDemandRate1() {
        return cumulativeReactivePowerDemandRate1;
    }

    public void setCumulativeReactivePowerDemandRate1(
            Double cumulativeReactivePowerDemandRate1) {
        this.cumulativeReactivePowerDemandRate1 = cumulativeReactivePowerDemandRate1;
    }

    public Double getActiveEnergyRate2() {
        return activeEnergyRate2;
    }

    public void setActiveEnergyRate2(Double activeEnergyRate2) {
        this.activeEnergyRate2 = activeEnergyRate2;
    }

    public Double getReactiveEnergyRate2() {
        return reactiveEnergyRate2;
    }

    public void setReactiveEnergyRate2(Double reactiveEnergyRate2) {
        this.reactiveEnergyRate2 = reactiveEnergyRate2;
    }

    public Double getActivePowerMaxDemandRate2() {
        return activePowerMaxDemandRate2;
    }

    public void setActivePowerMaxDemandRate2(Double activePowerMaxDemandRate2) {
        this.activePowerMaxDemandRate2 = activePowerMaxDemandRate2;
    }

    public String getActivePowerDemandMaxTimeRate2() {
        return activePowerDemandMaxTimeRate2;
    }

    public void setActivePowerDemandMaxTimeRate2(
            String activePowerDemandMaxTimeRate2) {
        this.activePowerDemandMaxTimeRate2 = activePowerDemandMaxTimeRate2;
    }

    public Double getReactivePowerMaxDemandRate2() {
        return reactivePowerMaxDemandRate2;
    }

    public void setReactivePowerMaxDemandRate2(Double reactivePowerMaxDemandRate2) {
        this.reactivePowerMaxDemandRate2 = reactivePowerMaxDemandRate2;
    }

    public String getReactivePowerDemandMaxTimeRate2() {
        return reactivePowerDemandMaxTimeRate2;
    }

    public void setReactivePowerDemandMaxTimeRate2(
            String reactivePowerDemandMaxTimeRate2) {
        this.reactivePowerDemandMaxTimeRate2 = reactivePowerDemandMaxTimeRate2;
    }

    public Double getCumulativeDemandRate2() {
        return cumulativeActivePowerDemandRate2;
    }

    public void setCumulativeDemandRate2(Double cumulativeActivePowerDemandRate2) {
        this.cumulativeActivePowerDemandRate2 = cumulativeActivePowerDemandRate2;
    }

    public Double getCumulativeReactivePowerDemandRate2() {
        return cumulativeReactivePowerDemandRate2;
    }

    public void setCumulativeReactivePowerDemandRate2(
            Double cumulativeReactivePowerDemandRate2) {
        this.cumulativeReactivePowerDemandRate2 = cumulativeReactivePowerDemandRate2;
    }

    public Double getActiveEnergyRate3() {
        return activeEnergyRate3;
    }

    public void setActiveEnergyRate3(Double activeEnergyRate3) {
        this.activeEnergyRate3 = activeEnergyRate3;
    }

    public Double getReactiveEnergyRate3() {
        return reactiveEnergyRate3;
    }

    public void setReactiveEnergyRate3(Double reactiveEnergyRate3) {
        this.reactiveEnergyRate3 = reactiveEnergyRate3;
    }

    public Double getActivePowerMaxDemandRate3() {
        return activePowerMaxDemandRate3;
    }

    public void setActivePowerMaxDemandRate3(Double activePowerMaxDemandRate3) {
        this.activePowerMaxDemandRate3 = activePowerMaxDemandRate3;
    }

    public String getActivePowerDemandMaxTimeRate3() {
        return activePowerDemandMaxTimeRate3;
    }

    public void setActivePowerDemandMaxTimeRate3(
            String activePowerDemandMaxTimeRate3) {
        this.activePowerDemandMaxTimeRate3 = activePowerDemandMaxTimeRate3;
    }

    public Double getReactivePowerMaxDemandRate3() {
        return reactivePowerMaxDemandRate3;
    }

    public void setReactivePowerMaxDemandRate3(Double reactivePowerMaxDemandRate3) {
        this.reactivePowerMaxDemandRate3 = reactivePowerMaxDemandRate3;
    }

    public String getReactivePowerDemandMaxTimeRate3() {
        return reactivePowerDemandMaxTimeRate3;
    }

    public void setReactivePowerDemandMaxTimeRate3(
            String reactivePowerDemandMaxTimeRate3) {
        this.reactivePowerDemandMaxTimeRate3 = reactivePowerDemandMaxTimeRate3;
    }

    public Double getCumulativeDemandRate3() {
        return cumulativeActivePowerDemandRate3;
    }

    public void setCumulativeDemandRate3(Double cumulativeActivePowerDemandRate3) {
        this.cumulativeActivePowerDemandRate3 = cumulativeActivePowerDemandRate3;
    }

    public Double getCumulativeReactivePowerDemandRate3() {
        return cumulativeReactivePowerDemandRate3;
    }

    public void setCumulativeReactivePowerDemandRate3(
            Double cumulativeReactivePowerDemandRate3) {
        this.cumulativeReactivePowerDemandRate3 = cumulativeReactivePowerDemandRate3;
    }

    public Double getActiveEnergyRate4() {
        return activeEnergyRate4;
    }

    public void setActiveEnergyRate4(Double activeEnergyRate4) {
        this.activeEnergyRate4 = activeEnergyRate4;
    }

    public Double getReactiveEnergyRate4() {
        return reactiveEnergyRate4;
    }

    public void setReactiveEnergyRate4(Double reactiveEnergyRate4) {
        this.reactiveEnergyRate4 = reactiveEnergyRate4;
    }

    public Double getActivePowerMaxDemandRate4() {
        return activePowerMaxDemandRate4;
    }

    public void setActivePowerMaxDemandRate4(Double activePowerMaxDemandRate4) {
        this.activePowerMaxDemandRate4 = activePowerMaxDemandRate4;
    }

    public String getActivePowerDemandMaxTimeRate4() {
        return activePowerDemandMaxTimeRate4;
    }

    public void setActivePowerDemandMaxTimeRate4(
            String activePowerDemandMaxTimeRate4) {
        this.activePowerDemandMaxTimeRate4 = activePowerDemandMaxTimeRate4;
    }

    public Double getReactivePowerMaxDemandRate4() {
        return reactivePowerMaxDemandRate4;
    }

    public void setReactivePowerMaxDemandRate4(Double reactivePowerMaxDemandRate4) {
        this.reactivePowerMaxDemandRate4 = reactivePowerMaxDemandRate4;
    }

    public String getReactivePowerDemandMaxTimeRate4() {
        return reactivePowerDemandMaxTimeRate4;
    }

    public void setReactivePowerDemandMaxTimeRate4(
            String reactivePowerDemandMaxTimeRate4) {
        this.reactivePowerDemandMaxTimeRate4 = reactivePowerDemandMaxTimeRate4;
    }

    public Double getCumulativeDemandRate4() {
        return cumulativeActivePowerDemandRate4;
    }

    public void setCumulativeDemandRate4(Double cumulativeActivePowerDemandRate4) {
        this.cumulativeActivePowerDemandRate4 = cumulativeActivePowerDemandRate4;
    }

    public Double getCumulativeReactivePowerDemandRate4() {
        return cumulativeReactivePowerDemandRate4;
    }

    public void setCumulativeReactivePowerDemandRate4(
            Double cumulativeReactivePowerDemandRate4) {
        this.cumulativeReactivePowerDemandRate4 = cumulativeReactivePowerDemandRate4;
    }

    public Double getCumulativeActivePowerDemandRateTotal() {
        return cumulativeActivePowerDemandRateTotal;
    }

    public void setCumulativeActivePowerDemandRateTotal(
            Double cumulativeActivePowerDemandRateTotal) {
        this.cumulativeActivePowerDemandRateTotal = cumulativeActivePowerDemandRateTotal;
    }

    public Double getCumulativeActivePowerDemandRate1() {
        return cumulativeActivePowerDemandRate1;
    }

    public void setCumulativeActivePowerDemandRate1(
            Double cumulativeActivePowerDemandRate1) {
        this.cumulativeActivePowerDemandRate1 = cumulativeActivePowerDemandRate1;
    }

    public Double getCumulativeActivePowerDemandRate2() {
        return cumulativeActivePowerDemandRate2;
    }

    public void setCumulativeActivePowerDemandRate2(
            Double cumulativeActivePowerDemandRate2) {
        this.cumulativeActivePowerDemandRate2 = cumulativeActivePowerDemandRate2;
    }

    public Double getCumulativeActivePowerDemandRate3() {
        return cumulativeActivePowerDemandRate3;
    }

    public void setCumulativeActivePowerDemandRate3(
            Double cumulativeActivePowerDemandRate3) {
        this.cumulativeActivePowerDemandRate3 = cumulativeActivePowerDemandRate3;
    }

    public Double getCumulativeActivePowerDemandRate4() {
        return cumulativeActivePowerDemandRate4;
    }

    public void setCumulativeActivePowerDemandRate4(
            Double cumulativeActivePowerDemandRate4) {
        this.cumulativeActivePowerDemandRate4 = cumulativeActivePowerDemandRate4;
    }

    public Double getActiveEnergyImportRateTotal() {
        return activeEnergyImportRateTotal;
    }

    public void setActiveEnergyImportRateTotal(Double activeEnergyImportRateTotal) {
        this.activeEnergyImportRateTotal = activeEnergyImportRateTotal;
    }

    public Double getActiveEnergyExportRateTotal() {
        return activeEnergyExportRateTotal;
    }

    public void setActiveEnergyExportRateTotal(Double activeEnergyExportRateTotal) {
        this.activeEnergyExportRateTotal = activeEnergyExportRateTotal;
    }

    public Double getReactiveEnergyLagImportRateTotal() {
        return reactiveEnergyLagImportRateTotal;
    }

    public void setReactiveEnergyLagImportRateTotal(
            Double reactiveEnergyLagImportRateTotal) {
        this.reactiveEnergyLagImportRateTotal = reactiveEnergyLagImportRateTotal;
    }

    public Double getReactiveEnergyLeadImportRateTotal() {
        return reactiveEnergyLeadImportRateTotal;
    }

    public void setReactiveEnergyLeadImportRateTotal(
            Double reactiveEnergyLeadImportRateTotal) {
        this.reactiveEnergyLeadImportRateTotal = reactiveEnergyLeadImportRateTotal;
    }

    public Double getReactiveEnergyLagExportRateTotal() {
        return reactiveEnergyLagExportRateTotal;
    }

    public void setReactiveEnergyLagExportRateTotal(
            Double reactiveEnergyLagExportRateTotal) {
        this.reactiveEnergyLagExportRateTotal = reactiveEnergyLagExportRateTotal;
    }

    public Double getReactiveEnergyLeadExportRateTotal() {
        return reactiveEnergyLeadExportRateTotal;
    }

    public void setReactiveEnergyLeadExportRateTotal(
            Double reactiveEnergyLeadExportRateTotal) {
        this.reactiveEnergyLeadExportRateTotal = reactiveEnergyLeadExportRateTotal;
    }

    public Double getActiveEnergyImportRate1() {
        return activeEnergyImportRate1;
    }

    public void setActiveEnergyImportRate1(Double activeEnergyImportRate1) {
        this.activeEnergyImportRate1 = activeEnergyImportRate1;
    }

    public Double getActiveEnergyExportRate1() {
        return activeEnergyExportRate1;
    }

    public void setActiveEnergyExportRate1(Double activeEnergyExportRate1) {
        this.activeEnergyExportRate1 = activeEnergyExportRate1;
    }

    public Double getReactiveEnergyLagImportRate1() {
        return reactiveEnergyLagImportRate1;
    }

    public void setReactiveEnergyLagImportRate1(Double reactiveEnergyLagImportRate1) {
        this.reactiveEnergyLagImportRate1 = reactiveEnergyLagImportRate1;
    }

    public Double getReactiveEnergyLeadImportRate1() {
        return reactiveEnergyLeadImportRate1;
    }

    public void setReactiveEnergyLeadImportRate1(
            Double reactiveEnergyLeadImportRate1) {
        this.reactiveEnergyLeadImportRate1 = reactiveEnergyLeadImportRate1;
    }

    public Double getReactiveEnergyLagExportRate1() {
        return reactiveEnergyLagExportRate1;
    }

    public void setReactiveEnergyLagExportRate1(Double reactiveEnergyLagExportRate1) {
        this.reactiveEnergyLagExportRate1 = reactiveEnergyLagExportRate1;
    }

    public Double getReactiveEnergyLeadExportRate1() {
        return reactiveEnergyLeadExportRate1;
    }

    public void setReactiveEnergyLeadExportRate1(
            Double reactiveEnergyLeadExportRate1) {
        this.reactiveEnergyLeadExportRate1 = reactiveEnergyLeadExportRate1;
    }

    public Double getActiveEnergyImportRate2() {
        return activeEnergyImportRate2;
    }

    public void setActiveEnergyImportRate2(Double activeEnergyImportRate2) {
        this.activeEnergyImportRate2 = activeEnergyImportRate2;
    }

    public Double getActiveEnergyExportRate2() {
        return activeEnergyExportRate2;
    }

    public void setActiveEnergyExportRate2(Double activeEnergyExportRate2) {
        this.activeEnergyExportRate2 = activeEnergyExportRate2;
    }

    public Double getReactiveEnergyLagImportRate2() {
        return reactiveEnergyLagImportRate2;
    }

    public void setReactiveEnergyLagImportRate2(Double reactiveEnergyLagImportRate2) {
        this.reactiveEnergyLagImportRate2 = reactiveEnergyLagImportRate2;
    }

    public Double getReactiveEnergyLeadImportRate2() {
        return reactiveEnergyLeadImportRate2;
    }

    public void setReactiveEnergyLeadImportRate2(
            Double reactiveEnergyLeadImportRate2) {
        this.reactiveEnergyLeadImportRate2 = reactiveEnergyLeadImportRate2;
    }

    public Double getReactiveEnergyLagExportRate2() {
        return reactiveEnergyLagExportRate2;
    }

    public void setReactiveEnergyLagExportRate2(Double reactiveEnergyLagExportRate2) {
        this.reactiveEnergyLagExportRate2 = reactiveEnergyLagExportRate2;
    }

    public Double getReactiveEnergyLeadExportRate2() {
        return reactiveEnergyLeadExportRate2;
    }

    public void setReactiveEnergyLeadExportRate2(
            Double reactiveEnergyLeadExportRate2) {
        this.reactiveEnergyLeadExportRate2 = reactiveEnergyLeadExportRate2;
    }

    public Double getActiveEnergyImportRate3() {
        return activeEnergyImportRate3;
    }

    public void setActiveEnergyImportRate3(Double activeEnergyImportRate3) {
        this.activeEnergyImportRate3 = activeEnergyImportRate3;
    }

    public Double getActiveEnergyExportRate3() {
        return activeEnergyExportRate3;
    }

    public void setActiveEnergyExportRate3(Double activeEnergyExportRate3) {
        this.activeEnergyExportRate3 = activeEnergyExportRate3;
    }

    public Double getReactiveEnergyLagImportRate3() {
        return reactiveEnergyLagImportRate3;
    }

    public void setReactiveEnergyLagImportRate3(Double reactiveEnergyLagImportRate3) {
        this.reactiveEnergyLagImportRate3 = reactiveEnergyLagImportRate3;
    }

    public Double getReactiveEnergyLeadImportRate3() {
        return reactiveEnergyLeadImportRate3;
    }

    public void setReactiveEnergyLeadImportRate3(
            Double reactiveEnergyLeadImportRate3) {
        this.reactiveEnergyLeadImportRate3 = reactiveEnergyLeadImportRate3;
    }

    public Double getReactiveEnergyLagExportRate3() {
        return reactiveEnergyLagExportRate3;
    }

    public void setReactiveEnergyLagExportRate3(Double reactiveEnergyLagExportRate3) {
        this.reactiveEnergyLagExportRate3 = reactiveEnergyLagExportRate3;
    }

    public Double getReactiveEnergyLeadExportRate3() {
        return reactiveEnergyLeadExportRate3;
    }

    public void setReactiveEnergyLeadExportRate3(
            Double reactiveEnergyLeadExportRate3) {
        this.reactiveEnergyLeadExportRate3 = reactiveEnergyLeadExportRate3;
    }

    public Double getActiveEnergyImportRate4() {
        return activeEnergyImportRate4;
    }

    public void setActiveEnergyImportRate4(Double activeEnergyImportRate4) {
        this.activeEnergyImportRate4 = activeEnergyImportRate4;
    }

    public Double getActiveEnergyExportRate4() {
        return activeEnergyExportRate4;
    }

    public void setActiveEnergyExportRate4(Double activeEnergyExportRate4) {
        this.activeEnergyExportRate4 = activeEnergyExportRate4;
    }

    public Double getReactiveEnergyLagImportRate4() {
        return reactiveEnergyLagImportRate4;
    }

    public void setReactiveEnergyLagImportRate4(Double reactiveEnergyLagImportRate4) {
        this.reactiveEnergyLagImportRate4 = reactiveEnergyLagImportRate4;
    }

    public Double getReactiveEnergyLeadImportRate4() {
        return reactiveEnergyLeadImportRate4;
    }

    public void setReactiveEnergyLeadImportRate4(
            Double reactiveEnergyLeadImportRate4) {
        this.reactiveEnergyLeadImportRate4 = reactiveEnergyLeadImportRate4;
    }

    public Double getReactiveEnergyLagExportRate4() {
        return reactiveEnergyLagExportRate4;
    }

    public void setReactiveEnergyLagExportRate4(Double reactiveEnergyLagExportRate4) {
        this.reactiveEnergyLagExportRate4 = reactiveEnergyLagExportRate4;
    }

    public Double getReactiveEnergyLeadExportRate4() {
        return reactiveEnergyLeadExportRate4;
    }

    public void setReactiveEnergyLeadExportRate4(
            Double reactiveEnergyLeadExportRate4) {
        this.reactiveEnergyLeadExportRate4 = reactiveEnergyLeadExportRate4;
    }

    public Double getActivePwrDmdMaxImportRateTotal() {
        return activePwrDmdMaxImportRateTotal;
    }

    public void setActivePwrDmdMaxImportRateTotal(
            Double activePwrDmdMaxImportRateTotal) {
        this.activePwrDmdMaxImportRateTotal = activePwrDmdMaxImportRateTotal;
    }

    public Double getActivePwrDmdMaxExportRateTotal() {
        return activePwrDmdMaxExportRateTotal;
    }

    public void setActivePwrDmdMaxExportRateTotal(
            Double activePwrDmdMaxExportRateTotal) {
        this.activePwrDmdMaxExportRateTotal = activePwrDmdMaxExportRateTotal;
    }

    public Double getReactivePwrDmdMaxLagImportRateTotal() {
        return reactivePwrDmdMaxLagImportRateTotal;
    }

    public void setReactivePwrDmdMaxLagImportRateTotal(
            Double reactivePwrDmdMaxLagImportRateTotal) {
        this.reactivePwrDmdMaxLagImportRateTotal = reactivePwrDmdMaxLagImportRateTotal;
    }

    public Double getReactivePwrDmdMaxLeadImportRateTotal() {
        return reactivePwrDmdMaxLeadImportRateTotal;
    }

    public void setReactivePwrDmdMaxLeadImportRateTotal(
            Double reactivePwrDmdMaxLeadImportRateTotal) {
        this.reactivePwrDmdMaxLeadImportRateTotal = reactivePwrDmdMaxLeadImportRateTotal;
    }

    public Double getReactivePwrDmdMaxLagExportRateTotal() {
        return reactivePwrDmdMaxLagExportRateTotal;
    }

    public void setReactivePwrDmdMaxLagExportRateTotal(
            Double reactivePwrDmdMaxLagExportRateTotal) {
        this.reactivePwrDmdMaxLagExportRateTotal = reactivePwrDmdMaxLagExportRateTotal;
    }

    public Double getReactivePwrDmdMaxLeadExportRateTotal() {
        return reactivePwrDmdMaxLeadExportRateTotal;
    }

    public void setReactivePwrDmdMaxLeadExportRateTotal(
            Double reactivePwrDmdMaxLeadExportRateTotal) {
        this.reactivePwrDmdMaxLeadExportRateTotal = reactivePwrDmdMaxLeadExportRateTotal;
    }

    public String getActivePwrDmdMaxTimeImportRateTotal() {
        return activePwrDmdMaxTimeImportRateTotal;
    }

    public void setActivePwrDmdMaxTimeImportRateTotal(
            String activePwrDmdMaxTimeImportRateTotal) {
        this.activePwrDmdMaxTimeImportRateTotal = activePwrDmdMaxTimeImportRateTotal;
    }

    public String getActivePwrDmdMaxTimeExportRateTotal() {
        return activePwrDmdMaxTimeExportRateTotal;
    }

    public void setActivePwrDmdMaxTimeExportRateTotal(
            String activePwrDmdMaxTimeExportRateTotal) {
        this.activePwrDmdMaxTimeExportRateTotal = activePwrDmdMaxTimeExportRateTotal;
    }

    public String getReactivePwrDmdMaxTimeLagImportRateTotal() {
        return reactivePwrDmdMaxTimeLagImportRateTotal;
    }

    public void setReactivePwrDmdMaxTimeLagImportRateTotal(
            String reactivePwrDmdMaxTimeLagImportRateTotal) {
        this.reactivePwrDmdMaxTimeLagImportRateTotal = reactivePwrDmdMaxTimeLagImportRateTotal;
    }

    public String getReactivePwrDmdMaxTimeLeadImportRateTotal() {
        return reactivePwrDmdMaxTimeLeadImportRateTotal;
    }

    public void setReactivePwrDmdMaxTimeLeadImportRateTotal(
            String reactivePwrDmdMaxTimeLeadImportRateTotal) {
        this.reactivePwrDmdMaxTimeLeadImportRateTotal = reactivePwrDmdMaxTimeLeadImportRateTotal;
    }

    public String getReactivePwrDmdMaxTimeLagExportRateTotal() {
        return reactivePwrDmdMaxTimeLagExportRateTotal;
    }

    public void setReactivePwrDmdMaxTimeLagExportRateTotal(
            String reactivePwrDmdMaxTimeLagExportRateTotal) {
        this.reactivePwrDmdMaxTimeLagExportRateTotal = reactivePwrDmdMaxTimeLagExportRateTotal;
    }

    public String getReactivePwrDmdMaxTimeLeadExportRateTotal() {
        return reactivePwrDmdMaxTimeLeadExportRateTotal;
    }

    public void setReactivePwrDmdMaxTimeLeadExportRateTotal(
            String reactivePwrDmdMaxTimeLeadExportRateTotal) {
        this.reactivePwrDmdMaxTimeLeadExportRateTotal = reactivePwrDmdMaxTimeLeadExportRateTotal;
    }

    public Double getActivePwrDmdMaxImportRate1() {
        return activePwrDmdMaxImportRate1;
    }

    public void setActivePwrDmdMaxImportRate1(Double activePwrDmdMaxImportRate1) {
        this.activePwrDmdMaxImportRate1 = activePwrDmdMaxImportRate1;
    }

    public Double getActivePwrDmdMaxExportRate1() {
        return activePwrDmdMaxExportRate1;
    }

    public void setActivePwrDmdMaxExportRate1(Double activePwrDmdMaxExportRate1) {
        this.activePwrDmdMaxExportRate1 = activePwrDmdMaxExportRate1;
    }

    public Double getReactivePwrDmdMaxLagImportRate1() {
        return reactivePwrDmdMaxLagImportRate1;
    }

    public void setReactivePwrDmdMaxLagImportRate1(
            Double reactivePwrDmdMaxLagImportRate1) {
        this.reactivePwrDmdMaxLagImportRate1 = reactivePwrDmdMaxLagImportRate1;
    }

    public Double getReactivePwrDmdMaxLeadImportRate1() {
        return reactivePwrDmdMaxLeadImportRate1;
    }

    public void setReactivePwrDmdMaxLeadImportRate1(
            Double reactivePwrDmdMaxLeadImportRate1) {
        this.reactivePwrDmdMaxLeadImportRate1 = reactivePwrDmdMaxLeadImportRate1;
    }

    public Double getReactivePwrDmdMaxLagExportRate1() {
        return reactivePwrDmdMaxLagExportRate1;
    }

    public void setReactivePwrDmdMaxLagExportRate1(
            Double reactivePwrDmdMaxLagExportRate1) {
        this.reactivePwrDmdMaxLagExportRate1 = reactivePwrDmdMaxLagExportRate1;
    }

    public Double getReactivePwrDmdMaxLeadExportRate1() {
        return reactivePwrDmdMaxLeadExportRate1;
    }

    public void setReactivePwrDmdMaxLeadExportRate1(
            Double reactivePwrDmdMaxLeadExportRate1) {
        this.reactivePwrDmdMaxLeadExportRate1 = reactivePwrDmdMaxLeadExportRate1;
    }

    public String getActivePwrDmdMaxTimeImportRate1() {
        return activePwrDmdMaxTimeImportRate1;
    }

    public void setActivePwrDmdMaxTimeImportRate1(
            String activePwrDmdMaxTimeImportRate1) {
        this.activePwrDmdMaxTimeImportRate1 = activePwrDmdMaxTimeImportRate1;
    }

    public String getActivePwrDmdMaxTimeExportRate1() {
        return activePwrDmdMaxTimeExportRate1;
    }

    public void setActivePwrDmdMaxTimeExportRate1(
            String activePwrDmdMaxTimeExportRate1) {
        this.activePwrDmdMaxTimeExportRate1 = activePwrDmdMaxTimeExportRate1;
    }

    public String getReactivePwrDmdMaxTimeLagImportRate1() {
        return reactivePwrDmdMaxTimeLagImportRate1;
    }

    public void setReactivePwrDmdMaxTimeLagImportRate1(
            String reactivePwrDmdMaxTimeLagImportRate1) {
        this.reactivePwrDmdMaxTimeLagImportRate1 = reactivePwrDmdMaxTimeLagImportRate1;
    }

    public String getReactivePwrDmdMaxTimeLeadImportRate1() {
        return reactivePwrDmdMaxTimeLeadImportRate1;
    }

    public void setReactivePwrDmdMaxTimeLeadImportRate1(
            String reactivePwrDmdMaxTimeLeadImportRate1) {
        this.reactivePwrDmdMaxTimeLeadImportRate1 = reactivePwrDmdMaxTimeLeadImportRate1;
    }

    public String getReactivePwrDmdMaxTimeLagExportRate1() {
        return reactivePwrDmdMaxTimeLagExportRate1;
    }

    public void setReactivePwrDmdMaxTimeLagExportRate1(
            String reactivePwrDmdMaxTimeLagExportRate1) {
        this.reactivePwrDmdMaxTimeLagExportRate1 = reactivePwrDmdMaxTimeLagExportRate1;
    }

    public String getReactivePwrDmdMaxTimeLeadExportRate1() {
        return reactivePwrDmdMaxTimeLeadExportRate1;
    }

    public void setReactivePwrDmdMaxTimeLeadExportRate1(
            String reactivePwrDmdMaxTimeLeadExportRate1) {
        this.reactivePwrDmdMaxTimeLeadExportRate1 = reactivePwrDmdMaxTimeLeadExportRate1;
    }

    public Double getActivePwrDmdMaxImportRate2() {
        return activePwrDmdMaxImportRate2;
    }

    public void setActivePwrDmdMaxImportRate2(Double activePwrDmdMaxImportRate2) {
        this.activePwrDmdMaxImportRate2 = activePwrDmdMaxImportRate2;
    }

    public Double getActivePwrDmdMaxExportRate2() {
        return activePwrDmdMaxExportRate2;
    }

    public void setActivePwrDmdMaxExportRate2(Double activePwrDmdMaxExportRate2) {
        this.activePwrDmdMaxExportRate2 = activePwrDmdMaxExportRate2;
    }

    public Double getReactivePwrDmdMaxLagImportRate2() {
        return reactivePwrDmdMaxLagImportRate2;
    }

    public void setReactivePwrDmdMaxLagImportRate2(
            Double reactivePwrDmdMaxLagImportRate2) {
        this.reactivePwrDmdMaxLagImportRate2 = reactivePwrDmdMaxLagImportRate2;
    }

    public Double getReactivePwrDmdMaxLeadImportRate2() {
        return reactivePwrDmdMaxLeadImportRate2;
    }

    public void setReactivePwrDmdMaxLeadImportRate2(
            Double reactivePwrDmdMaxLeadImportRate2) {
        this.reactivePwrDmdMaxLeadImportRate2 = reactivePwrDmdMaxLeadImportRate2;
    }

    public Double getReactivePwrDmdMaxLagExportRate2() {
        return reactivePwrDmdMaxLagExportRate2;
    }

    public void setReactivePwrDmdMaxLagExportRate2(
            Double reactivePwrDmdMaxLagExportRate2) {
        this.reactivePwrDmdMaxLagExportRate2 = reactivePwrDmdMaxLagExportRate2;
    }

    public Double getReactivePwrDmdMaxLeadExportRate2() {
        return reactivePwrDmdMaxLeadExportRate2;
    }

    public void setReactivePwrDmdMaxLeadExportRate2(
            Double reactivePwrDmdMaxLeadExportRate2) {
        this.reactivePwrDmdMaxLeadExportRate2 = reactivePwrDmdMaxLeadExportRate2;
    }

    public String getActivePwrDmdMaxTimeImportRate2() {
        return activePwrDmdMaxTimeImportRate2;
    }

    public void setActivePwrDmdMaxTimeImportRate2(
            String activePwrDmdMaxTimeImportRate2) {
        this.activePwrDmdMaxTimeImportRate2 = activePwrDmdMaxTimeImportRate2;
    }

    public String getActivePwrDmdMaxTimeExportRate2() {
        return activePwrDmdMaxTimeExportRate2;
    }

    public void setActivePwrDmdMaxTimeExportRate2(
            String activePwrDmdMaxTimeExportRate2) {
        this.activePwrDmdMaxTimeExportRate2 = activePwrDmdMaxTimeExportRate2;
    }

    public String getReactivePwrDmdMaxTimeLagImportRate2() {
        return reactivePwrDmdMaxTimeLagImportRate2;
    }

    public void setReactivePwrDmdMaxTimeLagImportRate2(
            String reactivePwrDmdMaxTimeLagImportRate2) {
        this.reactivePwrDmdMaxTimeLagImportRate2 = reactivePwrDmdMaxTimeLagImportRate2;
    }

    public String getReactivePwrDmdMaxTimeLeadImportRate2() {
        return reactivePwrDmdMaxTimeLeadImportRate2;
    }

    public void setReactivePwrDmdMaxTimeLeadImportRate2(
            String reactivePwrDmdMaxTimeLeadImportRate2) {
        this.reactivePwrDmdMaxTimeLeadImportRate2 = reactivePwrDmdMaxTimeLeadImportRate2;
    }

    public String getReactivePwrDmdMaxTimeLagExportRate2() {
        return reactivePwrDmdMaxTimeLagExportRate2;
    }

    public void setReactivePwrDmdMaxTimeLagExportRate2(
            String reactivePwrDmdMaxTimeLagExportRate2) {
        this.reactivePwrDmdMaxTimeLagExportRate2 = reactivePwrDmdMaxTimeLagExportRate2;
    }

    public String getReactivePwrDmdMaxTimeLeadExportRate2() {
        return reactivePwrDmdMaxTimeLeadExportRate2;
    }

    public void setReactivePwrDmdMaxTimeLeadExportRate2(
            String reactivePwrDmdMaxTimeLeadExportRate2) {
        this.reactivePwrDmdMaxTimeLeadExportRate2 = reactivePwrDmdMaxTimeLeadExportRate2;
    }

    public Double getActivePwrDmdMaxImportRate3() {
        return activePwrDmdMaxImportRate3;
    }

    public void setActivePwrDmdMaxImportRate3(Double activePwrDmdMaxImportRate3) {
        this.activePwrDmdMaxImportRate3 = activePwrDmdMaxImportRate3;
    }

    public Double getActivePwrDmdMaxExportRate3() {
        return activePwrDmdMaxExportRate3;
    }

    public void setActivePwrDmdMaxExportRate3(Double activePwrDmdMaxExportRate3) {
        this.activePwrDmdMaxExportRate3 = activePwrDmdMaxExportRate3;
    }

    public Double getReactivePwrDmdMaxLagImportRate3() {
        return reactivePwrDmdMaxLagImportRate3;
    }

    public void setReactivePwrDmdMaxLagImportRate3(
            Double reactivePwrDmdMaxLagImportRate3) {
        this.reactivePwrDmdMaxLagImportRate3 = reactivePwrDmdMaxLagImportRate3;
    }

    public Double getReactivePwrDmdMaxLeadImportRate3() {
        return reactivePwrDmdMaxLeadImportRate3;
    }

    public void setReactivePwrDmdMaxLeadImportRate3(
            Double reactivePwrDmdMaxLeadImportRate3) {
        this.reactivePwrDmdMaxLeadImportRate3 = reactivePwrDmdMaxLeadImportRate3;
    }

    public Double getReactivePwrDmdMaxLagExportRate3() {
        return reactivePwrDmdMaxLagExportRate3;
    }

    public void setReactivePwrDmdMaxLagExportRate3(
            Double reactivePwrDmdMaxLagExportRate3) {
        this.reactivePwrDmdMaxLagExportRate3 = reactivePwrDmdMaxLagExportRate3;
    }

    public Double getReactivePwrDmdMaxLeadExportRate3() {
        return reactivePwrDmdMaxLeadExportRate3;
    }

    public void setReactivePwrDmdMaxLeadExportRate3(
            Double reactivePwrDmdMaxLeadExportRate3) {
        this.reactivePwrDmdMaxLeadExportRate3 = reactivePwrDmdMaxLeadExportRate3;
    }

    public String getActivePwrDmdMaxTimeImportRate3() {
        return activePwrDmdMaxTimeImportRate3;
    }

    public void setActivePwrDmdMaxTimeImportRate3(
            String activePwrDmdMaxTimeImportRate3) {
        this.activePwrDmdMaxTimeImportRate3 = activePwrDmdMaxTimeImportRate3;
    }

    public String getActivePwrDmdMaxTimeExportRate3() {
        return activePwrDmdMaxTimeExportRate3;
    }

    public void setActivePwrDmdMaxTimeExportRate3(
            String activePwrDmdMaxTimeExportRate3) {
        this.activePwrDmdMaxTimeExportRate3 = activePwrDmdMaxTimeExportRate3;
    }

    public String getReactivePwrDmdMaxTimeLagImportRate3() {
        return reactivePwrDmdMaxTimeLagImportRate3;
    }

    public void setReactivePwrDmdMaxTimeLagImportRate3(
            String reactivePwrDmdMaxTimeLagImportRate3) {
        this.reactivePwrDmdMaxTimeLagImportRate3 = reactivePwrDmdMaxTimeLagImportRate3;
    }

    public String getReactivePwrDmdMaxTimeLeadImportRate3() {
        return reactivePwrDmdMaxTimeLeadImportRate3;
    }

    public void setReactivePwrDmdMaxTimeLeadImportRate3(
            String reactivePwrDmdMaxTimeLeadImportRate3) {
        this.reactivePwrDmdMaxTimeLeadImportRate3 = reactivePwrDmdMaxTimeLeadImportRate3;
    }

    public String getReactivePwrDmdMaxTimeLagExportRate3() {
        return reactivePwrDmdMaxTimeLagExportRate3;
    }

    public void setReactivePwrDmdMaxTimeLagExportRate3(
            String reactivePwrDmdMaxTimeLagExportRate3) {
        this.reactivePwrDmdMaxTimeLagExportRate3 = reactivePwrDmdMaxTimeLagExportRate3;
    }

    public String getReactivePwrDmdMaxTimeLeadExportRate3() {
        return reactivePwrDmdMaxTimeLeadExportRate3;
    }

    public void setReactivePwrDmdMaxTimeLeadExportRate3(
            String reactivePwrDmdMaxTimeLeadExportRate3) {
        this.reactivePwrDmdMaxTimeLeadExportRate3 = reactivePwrDmdMaxTimeLeadExportRate3;
    }

    public Double getActivePwrDmdMaxImportRate4() {
        return activePwrDmdMaxImportRate4;
    }

    public void setActivePwrDmdMaxImportRate4(Double activePwrDmdMaxImportRate4) {
        this.activePwrDmdMaxImportRate4 = activePwrDmdMaxImportRate4;
    }

    public Double getActivePwrDmdMaxExportRate4() {
        return activePwrDmdMaxExportRate4;
    }

    public void setActivePwrDmdMaxExportRate4(Double activePwrDmdMaxExportRate4) {
        this.activePwrDmdMaxExportRate4 = activePwrDmdMaxExportRate4;
    }

    public Double getReactivePwrDmdMaxLagImportRate4() {
        return reactivePwrDmdMaxLagImportRate4;
    }

    public void setReactivePwrDmdMaxLagImportRate4(
            Double reactivePwrDmdMaxLagImportRate4) {
        this.reactivePwrDmdMaxLagImportRate4 = reactivePwrDmdMaxLagImportRate4;
    }

    public Double getReactivePwrDmdMaxLeadImportRate4() {
        return reactivePwrDmdMaxLeadImportRate4;
    }

    public void setReactivePwrDmdMaxLeadImportRate4(
            Double reactivePwrDmdMaxLeadImportRate4) {
        this.reactivePwrDmdMaxLeadImportRate4 = reactivePwrDmdMaxLeadImportRate4;
    }

    public Double getReactivePwrDmdMaxLagExportRate4() {
        return reactivePwrDmdMaxLagExportRate4;
    }

    public void setReactivePwrDmdMaxLagExportRate4(
            Double reactivePwrDmdMaxLagExportRate4) {
        this.reactivePwrDmdMaxLagExportRate4 = reactivePwrDmdMaxLagExportRate4;
    }

    public Double getReactivePwrDmdMaxLeadExportRate4() {
        return reactivePwrDmdMaxLeadExportRate4;
    }

    public void setReactivePwrDmdMaxLeadExportRate4(
            Double reactivePwrDmdMaxLeadExportRate4) {
        this.reactivePwrDmdMaxLeadExportRate4 = reactivePwrDmdMaxLeadExportRate4;
    }

    public String getActivePwrDmdMaxTimeImportRate4() {
        return activePwrDmdMaxTimeImportRate4;
    }

    public void setActivePwrDmdMaxTimeImportRate4(
            String activePwrDmdMaxTimeImportRate4) {
        this.activePwrDmdMaxTimeImportRate4 = activePwrDmdMaxTimeImportRate4;
    }

    public String getActivePwrDmdMaxTimeExportRate4() {
        return activePwrDmdMaxTimeExportRate4;
    }

    public void setActivePwrDmdMaxTimeExportRate4(
            String activePwrDmdMaxTimeExportRate4) {
        this.activePwrDmdMaxTimeExportRate4 = activePwrDmdMaxTimeExportRate4;
    }

    public String getReactivePwrDmdMaxTimeLagImportRate4() {
        return reactivePwrDmdMaxTimeLagImportRate4;
    }

    public void setReactivePwrDmdMaxTimeLagImportRate4(
            String reactivePwrDmdMaxTimeLagImportRate4) {
        this.reactivePwrDmdMaxTimeLagImportRate4 = reactivePwrDmdMaxTimeLagImportRate4;
    }

    public String getReactivePwrDmdMaxTimeLeadImportRate4() {
        return reactivePwrDmdMaxTimeLeadImportRate4;
    }

    public void setReactivePwrDmdMaxTimeLeadImportRate4(
            String reactivePwrDmdMaxTimeLeadImportRate4) {
        this.reactivePwrDmdMaxTimeLeadImportRate4 = reactivePwrDmdMaxTimeLeadImportRate4;
    }

    public String getReactivePwrDmdMaxTimeLagExportRate4() {
        return reactivePwrDmdMaxTimeLagExportRate4;
    }

    public void setReactivePwrDmdMaxTimeLagExportRate4(
            String reactivePwrDmdMaxTimeLagExportRate4) {
        this.reactivePwrDmdMaxTimeLagExportRate4 = reactivePwrDmdMaxTimeLagExportRate4;
    }

    public String getReactivePwrDmdMaxTimeLeadExportRate4() {
        return reactivePwrDmdMaxTimeLeadExportRate4;
    }

    public void setReactivePwrDmdMaxTimeLeadExportRate4(
            String reactivePwrDmdMaxTimeLeadExportRate4) {
        this.reactivePwrDmdMaxTimeLeadExportRate4 = reactivePwrDmdMaxTimeLeadExportRate4;
    }

    public Double getCummActivePwrDmdMaxImportRateTotal() {
        return cummActivePwrDmdMaxImportRateTotal;
    }

    public void setCummActivePwrDmdMaxImportRateTotal(
            Double cummActivePwrDmdMaxImportRateTotal) {
        this.cummActivePwrDmdMaxImportRateTotal = cummActivePwrDmdMaxImportRateTotal;
    }

    public Double getCummActivePwrDmdMaxExportRateTotal() {
        return cummActivePwrDmdMaxExportRateTotal;
    }

    public void setCummActivePwrDmdMaxExportRateTotal(
            Double cummActivePwrDmdMaxExportRateTotal) {
        this.cummActivePwrDmdMaxExportRateTotal = cummActivePwrDmdMaxExportRateTotal;
    }

    public Double getCummReactivePwrDmdMaxLagImportRateTotal() {
        return cummReactivePwrDmdMaxLagImportRateTotal;
    }

    public void setCummReactivePwrDmdMaxLagImportRateTotal(
            Double cummReactivePwrDmdMaxLagImportRateTotal) {
        this.cummReactivePwrDmdMaxLagImportRateTotal = cummReactivePwrDmdMaxLagImportRateTotal;
    }

    public Double getCummReactivePwrDmdMaxLeadImportRateTotal() {
        return cummReactivePwrDmdMaxLeadImportRateTotal;
    }

    public void setCummReactivePwrDmdMaxLeadImportRateTotal(
            Double cummReactivePwrDmdMaxLeadImportRateTotal) {
        this.cummReactivePwrDmdMaxLeadImportRateTotal = cummReactivePwrDmdMaxLeadImportRateTotal;
    }

    public Double getCummReactivePwrDmdMaxLagExportRateTotal() {
        return cummReactivePwrDmdMaxLagExportRateTotal;
    }

    public void setCummReactivePwrDmdMaxLagExportRateTotal(
            Double cummReactivePwrDmdMaxLagExportRateTotal) {
        this.cummReactivePwrDmdMaxLagExportRateTotal = cummReactivePwrDmdMaxLagExportRateTotal;
    }

    public Double getCummReactivePwrDmdMaxLeadExportRateTotal() {
        return cummReactivePwrDmdMaxLeadExportRateTotal;
    }

    public void setCummReactivePwrDmdMaxLeadExportRateTotal(
            Double cummReactivePwrDmdMaxLeadExportRateTotal) {
        this.cummReactivePwrDmdMaxLeadExportRateTotal = cummReactivePwrDmdMaxLeadExportRateTotal;
    }

    public Double getCummActivePwrDmdMaxImportRate1() {
        return cummActivePwrDmdMaxImportRate1;
    }

    public void setCummActivePwrDmdMaxImportRate1(
            Double cummActivePwrDmdMaxImportRate1) {
        this.cummActivePwrDmdMaxImportRate1 = cummActivePwrDmdMaxImportRate1;
    }

    public Double getCummActivePwrDmdMaxExportRate1() {
        return cummActivePwrDmdMaxExportRate1;
    }

    public void setCummActivePwrDmdMaxExportRate1(
            Double cummActivePwrDmdMaxExportRate1) {
        this.cummActivePwrDmdMaxExportRate1 = cummActivePwrDmdMaxExportRate1;
    }

    public Double getCummReactivePwrDmdMaxLagImportRate1() {
        return cummReactivePwrDmdMaxLagImportRate1;
    }

    public void setCummReactivePwrDmdMaxLagImportRate1(
            Double cummReactivePwrDmdMaxLagImportRate1) {
        this.cummReactivePwrDmdMaxLagImportRate1 = cummReactivePwrDmdMaxLagImportRate1;
    }

    public Double getCummReactivePwrDmdMaxLeadImportRate1() {
        return cummReactivePwrDmdMaxLeadImportRate1;
    }

    public void setCummReactivePwrDmdMaxLeadImportRate1(
            Double cummReactivePwrDmdMaxLeadImportRate1) {
        this.cummReactivePwrDmdMaxLeadImportRate1 = cummReactivePwrDmdMaxLeadImportRate1;
    }

    public Double getCummReactivePwrDmdMaxLagExportRate1() {
        return cummReactivePwrDmdMaxLagExportRate1;
    }

    public void setCummReactivePwrDmdMaxLagExportRate1(
            Double cummReactivePwrDmdMaxLagExportRate1) {
        this.cummReactivePwrDmdMaxLagExportRate1 = cummReactivePwrDmdMaxLagExportRate1;
    }

    public Double getCummReactivePwrDmdMaxLeadExportRate1() {
        return cummReactivePwrDmdMaxLeadExportRate1;
    }

    public void setCummReactivePwrDmdMaxLeadExportRate1(
            Double cummReactivePwrDmdMaxLeadExportRate1) {
        this.cummReactivePwrDmdMaxLeadExportRate1 = cummReactivePwrDmdMaxLeadExportRate1;
    }

    public Double getCummActivePwrDmdMaxImportRate2() {
        return cummActivePwrDmdMaxImportRate2;
    }

    public void setCummActivePwrDmdMaxImportRate2(
            Double cummActivePwrDmdMaxImportRate2) {
        this.cummActivePwrDmdMaxImportRate2 = cummActivePwrDmdMaxImportRate2;
    }

    public Double getCummActivePwrDmdMaxExportRate2() {
        return cummActivePwrDmdMaxExportRate2;
    }

    public void setCummActivePwrDmdMaxExportRate2(
            Double cummActivePwrDmdMaxExportRate2) {
        this.cummActivePwrDmdMaxExportRate2 = cummActivePwrDmdMaxExportRate2;
    }

    public Double getCummReactivePwrDmdMaxLagImportRate2() {
        return cummReactivePwrDmdMaxLagImportRate2;
    }

    public void setCummReactivePwrDmdMaxLagImportRate2(
            Double cummReactivePwrDmdMaxLagImportRate2) {
        this.cummReactivePwrDmdMaxLagImportRate2 = cummReactivePwrDmdMaxLagImportRate2;
    }

    public Double getCummReactivePwrDmdMaxLeadImportRate2() {
        return cummReactivePwrDmdMaxLeadImportRate2;
    }

    public void setCummReactivePwrDmdMaxLeadImportRate2(
            Double cummReactivePwrDmdMaxLeadImportRate2) {
        this.cummReactivePwrDmdMaxLeadImportRate2 = cummReactivePwrDmdMaxLeadImportRate2;
    }

    public Double getCummReactivePwrDmdMaxLagExportRate2() {
        return cummReactivePwrDmdMaxLagExportRate2;
    }

    public void setCummReactivePwrDmdMaxLagExportRate2(
            Double cummReactivePwrDmdMaxLagExportRate2) {
        this.cummReactivePwrDmdMaxLagExportRate2 = cummReactivePwrDmdMaxLagExportRate2;
    }

    public Double getCummReactivePwrDmdMaxLeadExportRate2() {
        return cummReactivePwrDmdMaxLeadExportRate2;
    }

    public void setCummReactivePwrDmdMaxLeadExportRate2(
            Double cummReactivePwrDmdMaxLeadExportRate2) {
        this.cummReactivePwrDmdMaxLeadExportRate2 = cummReactivePwrDmdMaxLeadExportRate2;
    }

    public Double getCummActivePwrDmdMaxImportRate3() {
        return cummActivePwrDmdMaxImportRate3;
    }

    public void setCummActivePwrDmdMaxImportRate3(
            Double cummActivePwrDmdMaxImportRate3) {
        this.cummActivePwrDmdMaxImportRate3 = cummActivePwrDmdMaxImportRate3;
    }

    public Double getCummActivePwrDmdMaxExportRate3() {
        return cummActivePwrDmdMaxExportRate3;
    }

    public void setCummActivePwrDmdMaxExportRate3(
            Double cummActivePwrDmdMaxExportRate3) {
        this.cummActivePwrDmdMaxExportRate3 = cummActivePwrDmdMaxExportRate3;
    }

    public Double getCummReactivePwrDmdMaxLagImportRate3() {
        return cummReactivePwrDmdMaxLagImportRate3;
    }

    public void setCummReactivePwrDmdMaxLagImportRate3(
            Double cummReactivePwrDmdMaxLagImportRate3) {
        this.cummReactivePwrDmdMaxLagImportRate3 = cummReactivePwrDmdMaxLagImportRate3;
    }

    public Double getCummReactivePwrDmdMaxLeadImportRate3() {
        return cummReactivePwrDmdMaxLeadImportRate3;
    }

    public void setCummReactivePwrDmdMaxLeadImportRate3(
            Double cummReactivePwrDmdMaxLeadImportRate3) {
        this.cummReactivePwrDmdMaxLeadImportRate3 = cummReactivePwrDmdMaxLeadImportRate3;
    }

    public Double getCummReactivePwrDmdMaxLagExportRate3() {
        return cummReactivePwrDmdMaxLagExportRate3;
    }

    public void setCummReactivePwrDmdMaxLagExportRate3(
            Double cummReactivePwrDmdMaxLagExportRate3) {
        this.cummReactivePwrDmdMaxLagExportRate3 = cummReactivePwrDmdMaxLagExportRate3;
    }

    public Double getCummReactivePwrDmdMaxLeadExportRate3() {
        return cummReactivePwrDmdMaxLeadExportRate3;
    }

    public void setCummReactivePwrDmdMaxLeadExportRate3(
            Double cummReactivePwrDmdMaxLeadExportRate3) {
        this.cummReactivePwrDmdMaxLeadExportRate3 = cummReactivePwrDmdMaxLeadExportRate3;
    }

    public Double getCummActivePwrDmdMaxImportRate4() {
        return cummActivePwrDmdMaxImportRate4;
    }

    public void setCummActivePwrDmdMaxImportRate4(
            Double cummActivePwrDmdMaxImportRate4) {
        this.cummActivePwrDmdMaxImportRate4 = cummActivePwrDmdMaxImportRate4;
    }

    public Double getCummActivePwrDmdMaxExportRate4() {
        return cummActivePwrDmdMaxExportRate4;
    }

    public void setCummActivePwrDmdMaxExportRate4(
            Double cummActivePwrDmdMaxExportRate4) {
        this.cummActivePwrDmdMaxExportRate4 = cummActivePwrDmdMaxExportRate4;
    }

    public Double getCummReactivePwrDmdMaxLagImportRate4() {
        return cummReactivePwrDmdMaxLagImportRate4;
    }

    public void setCummReactivePwrDmdMaxLagImportRate4(
            Double cummReactivePwrDmdMaxLagImportRate4) {
        this.cummReactivePwrDmdMaxLagImportRate4 = cummReactivePwrDmdMaxLagImportRate4;
    }

    public Double getCummReactivePwrDmdMaxLeadImportRate4() {
        return cummReactivePwrDmdMaxLeadImportRate4;
    }

    public void setCummReactivePwrDmdMaxLeadImportRate4(
            Double cummReactivePwrDmdMaxLeadImportRate4) {
        this.cummReactivePwrDmdMaxLeadImportRate4 = cummReactivePwrDmdMaxLeadImportRate4;
    }

    public Double getCummReactivePwrDmdMaxLagExportRate4() {
        return cummReactivePwrDmdMaxLagExportRate4;
    }

    public void setCummReactivePwrDmdMaxLagExportRate4(
            Double cummReactivePwrDmdMaxLagExportRate4) {
        this.cummReactivePwrDmdMaxLagExportRate4 = cummReactivePwrDmdMaxLagExportRate4;
    }

    public Double getCummReactivePwrDmdMaxLeadExportRate4() {
        return cummReactivePwrDmdMaxLeadExportRate4;
    }

    public void setCummReactivePwrDmdMaxLeadExportRate4(
            Double cummReactivePwrDmdMaxLeadExportRate4) {
        this.cummReactivePwrDmdMaxLeadExportRate4 = cummReactivePwrDmdMaxLeadExportRate4;
    }

    public BillingPk getId() {
        return id;
    }

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public Double getPf() {
        return pf;
    }

    public void setPf(Double pf) {
        this.pf = pf;
    }

    public Double getBill() {
        return bill;
    }

    public void setBill(Double bill) {
        this.bill = bill;
    }

    public Double getkVah() {
        return kVah;
    }

    public void setkVah(Double kVah) {
        this.kVah = kVah;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }
    
    public Double getCummkVah1Rate1() {
        return cummkVah1Rate1;
    }

    public void setCummkVah1Rate1(Double cummkVah1Rate1) {
        this.cummkVah1Rate1 = cummkVah1Rate1;
    }

    public Double getCummkVah1Rate2() {
        return cummkVah1Rate2;
    }

    public void setCummkVah1Rate2(Double cummkVah1Rate2) {
        this.cummkVah1Rate2 = cummkVah1Rate2;
    }

    public Double getCummkVah1Rate3() {
        return cummkVah1Rate3;
    }

    public void setCummkVah1Rate3(Double cummkVah1Rate3) {
        this.cummkVah1Rate3 = cummkVah1Rate3;
    }

    public Double getCummkVah1Rate4() {
        return cummkVah1Rate4;
    }

    public void setCummkVah1Rate4(Double cummkVah1Rate4) {
        this.cummkVah1Rate4 = cummkVah1Rate4;
    }

    public Double getCummkVah1RateTotal() {
        return cummkVah1RateTotal;
    }

    public void setCummkVah1RateTotal(Double cummkVah1RateTotal) {
        this.cummkVah1RateTotal = cummkVah1RateTotal;
    }

    public Double getMaxDmdkVah1Rate1() {
        return maxDmdkVah1Rate1;
    }

    public void setMaxDmdkVah1Rate1(Double maxDmdkVah1Rate1) {
        this.maxDmdkVah1Rate1 = maxDmdkVah1Rate1;
    }

    public Double getMaxDmdkVah1Rate2() {
        return maxDmdkVah1Rate2;
    }

    public void setMaxDmdkVah1Rate2(Double maxDmdkVah1Rate2) {
        this.maxDmdkVah1Rate2 = maxDmdkVah1Rate2;
    }

    public Double getMaxDmdkVah1Rate3() {
        return maxDmdkVah1Rate3;
    }

    public void setMaxDmdkVah1Rate3(Double maxDmdkVah1Rate3) {
        this.maxDmdkVah1Rate3 = maxDmdkVah1Rate3;
    }

    public Double getMaxDmdkVah1Rate4() {
        return maxDmdkVah1Rate4;
    }

    public void setMaxDmdkVah1Rate4(Double maxDmdkVah1Rate4) {
        this.maxDmdkVah1Rate4 = maxDmdkVah1Rate4;
    }

    public Double getMaxDmdkVah1RateTotal() {
        return maxDmdkVah1RateTotal;
    }

    public void setMaxDmdkVah1RateTotal(Double maxDmdkVah1RateTotal) {
        this.maxDmdkVah1RateTotal = maxDmdkVah1RateTotal;
    }
    

    public Double getImportkWhPhaseA() {
        return importkWhPhaseA;
    }

    public void setImportkWhPhaseA(Double importkWhPhaseA) {
        this.importkWhPhaseA = importkWhPhaseA;
    }

    public Double getImportkWhPhaseB() {
        return importkWhPhaseB;
    }

    public void setImportkWhPhaseB(Double importkWhPhaseB) {
        this.importkWhPhaseB = importkWhPhaseB;
    }

    public Double getImportkWhPhaseC() {
        return importkWhPhaseC;
    }

    public void setImportkWhPhaseC(Double importkWhPhaseC) {
        this.importkWhPhaseC = importkWhPhaseC;
    }
    
    public String getMaxDmdkVah1TimeRate1() {
        return maxDmdkVah1TimeRate1;
    }

    public void setMaxDmdkVah1TimeRate1(String maxDmdkVah1TimeRate1) {
        this.maxDmdkVah1TimeRate1 = maxDmdkVah1TimeRate1;
    }

    public String getMaxDmdkVah1TimeRate2() {
        return maxDmdkVah1TimeRate2;
    }

    public void setMaxDmdkVah1TimeRate2(String maxDmdkVah1TimeRate2) {
        this.maxDmdkVah1TimeRate2 = maxDmdkVah1TimeRate2;
    }

    public String getMaxDmdkVah1TimeRate3() {
        return maxDmdkVah1TimeRate3;
    }

    public void setMaxDmdkVah1TimeRate3(String maxDmdkVah1TimeRate3) {
        this.maxDmdkVah1TimeRate3 = maxDmdkVah1TimeRate3;
    }

    public String getMaxDmdkVah1TimeRate4() {
        return maxDmdkVah1TimeRate4;
    }

    public void setMaxDmdkVah1TimeRate4(String maxDmdkVah1TimeRate4) {
        this.maxDmdkVah1TimeRate4 = maxDmdkVah1TimeRate4;
    }

    public String getMaxDmdkVah1TimeRateTotal() {
        return maxDmdkVah1TimeRateTotal;
    }

    public void setMaxDmdkVah1TimeRateTotal(String maxDmdkVah1TimeRateTotal) {
        this.maxDmdkVah1TimeRateTotal = maxDmdkVah1TimeRateTotal;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
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
    
}
