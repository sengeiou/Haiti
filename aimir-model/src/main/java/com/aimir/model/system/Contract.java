package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.constants.CommonConstants.ServiceType2;
import com.aimir.model.BaseObject;
import com.aimir.model.device.Meter;

/**
 * <p>에너지 사용자 (Energy Consumer)와 Utility(에너지 공급사) 간의 계약과 관련된 정보</p>
 * 계약번호(Contract Number)가 유일한 식별자가 되며
 * 계약용량, 공급상태, 과금과 관련된 정보를 가지고 있다.<br>
 * 에너지 사용자가 여러개의 에너지원을 가질수 있다. 이 관계는 Customer와 Contract의 관계로 존재한다.<br>
 * <p>
 * 에너지 계약정보와 미터 (에너지 계량)와 1:1관계가 존재한다.
 * </P>
 * 각 단위 사용량별 탄소 배출량 정보등의 정보를 가진다.<br>
 * 고객과 공급사간의 계약관계를 표현하지만 미터별로 다를 수 있기 때문에 미터와의 관계로 표현한다. <br>
 *  <p>
 * 계약 정보에는 <br>
 * - 계약종류 (공급사 코드 참고) <br>
 * - 결제종류 (후불, 선불) <br>
 *     선불시 잔액  <br>
 * - 계약 에너지 사용 여부 <br>
 * - 신용 모드 (credit, dept) <br>
 * - 기타 (할인률, 대가족제도, 세자녀 이상 등) <br>
 *  </p>
 * 선결제 <br>
 * 사용량과 tariff 정보를 이용하여 사용금액을 계산하여 저장한다. 잔액이 마이너스(debt) 상태가 되면 경고를 보내고 공급을 차단한다. <br>
 * 공급 차단 오퍼레이션이 안될 수도 있기 때문에 재시도할 수 있도록 한다. <br>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */

@Entity
@Table(name="CONTRACT")
public class Contract extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 180346987413635833L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_CONTRACT")
	@SequenceGenerator(name="SEQ_CONTRACT", sequenceName="SEQ_CONTRACT", allocationSize=1)
	private Integer id;
	
	@Column(name="CONTRACT_NUMBER", unique=true, nullable=false)
    private String contractNumber;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="customer_id", nullable=true)
	@ReferencedBy(name="customerNo")
	private Customer customer;	
	
	@Column(name="customer_id", nullable=true, updatable=false, insertable=false)
	private Integer customerId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "servicetype_id", nullable=false)
	@ReferencedBy(name="code")
	@ColumnInfo(descr="계약의 서비스 타입이 전기,가스,수도,열량 중 어느것인지를 의미(3.x)")
	private Code serviceTypeCode;
	
	@Column(name="servicetype_id", nullable=true, updatable=false, insertable=false)
	private Integer serviceTypeCodeId;
	
	private Integer contractIndex;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "meter_id")
	@ReferencedBy(name="mdsId")
	private Meter meter;
	
	@Column(name="meter_id", nullable=true, updatable=false, insertable=false)
	private Integer meterId;
	
	@Column(name="preMdsId", nullable=true)
	private String preMdsId;
	
	@Column(name="BILL_DATE", columnDefinition="INTEGER default 1")
	@ColumnInfo(descr="과금일 계약별로 과금일자가 틀릴 수 있음, 매월 1일 25일 등등 이렇게 설정할 수 있음")
	private Integer billDate;
	
	//TODO Location Constraint가 name, supplier 두개라서
	// ReferenceBy시 2개의 조건이 들어가야 하는데
	// 현재 기초데이터 관리에서는 1개의 조건만 넣을 수 있음
	// 이부분 보완 필요
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "location_id")
	@ReferencedBy(name="name")
	private Location location;		//공급지역
	
	@Column(name="location_id", nullable=true, updatable=false, insertable=false)
	private Integer locationId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "tariffIndex_id")
	@ReferencedBy(name="code")
	private TariffType tariffIndex;		//계약종별entity
	
	@Column(name="tariffIndex_id", nullable=true, updatable=false, insertable=false)
	private Integer tariffIndexId;
	
	private Double contractDemand;		//계약용량
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "status_id")
	@ReferencedBy(name="code")
	@ColumnInfo(descr="공급 상태 2.1.x")
	private Code status;			//공급상태 : 2.1.*
	
	@Column(name="status_id", nullable=true, updatable=false, insertable=false)
	private Integer statusCodeId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "credittype_id")
	@ReferencedBy(name="code")
	private Code creditType;		//지불타입(선/후불)
	
	@Column(name="credittype_id", nullable=true, updatable=false, insertable=false)
	private Integer creditTypeCodeId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "creditstatus_id")
	@ReferencedBy(name="code")
	private Code creditStatus;		//선불일경우 지불상태 
	
	@Column(name="creditstatus_id", nullable=true, updatable=false, insertable=false)
	private Integer creditStatusCodeId;
	
	private Integer prepaymentThreshold;	//선불일 경우 잔액최소임계치
	private Double prepaymentPowerDelay;   //선불인 경우 차단에 도달하는 kWh
	@Column(length=14)
	private String emergencyCreditStartTime;//emergency credit start date time

	////////////////////////////////////////////////
	//이하, 선불 고객 관련 속성 START
	////////////////////////////////////////////////	
	private String keyNum;			//지불이 선불일 경우 카드키넘버
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "keytype_id")
	@ReferencedBy(name="code")
	private Code keyType;			//지불=선불 카드타입
	
	@Column(name="keytype_id", nullable=true, updatable=false, insertable=false)
	private Integer keyTypeCodeId;

//	private Integer chargedCredit;		//선불일 경우 충전총액
//	private Integer currentCredit;		//선불일 경우 잔액
	private Double chargedCredit;		//선불일 경우 충전총액
	private Double currentCredit;		//선불일 경우 잔액
	
	private String lastTokenDate;	//마지막 충전한 시간
	private String lastTokenId;		//충전 세션키
	
	@ColumnInfo(descr="Emergency Credit 가능 여부")
	private Boolean emergencyCreditAvailable;	//
	
	@ColumnInfo(descr="Emergency Credit 자동/수동 전환 True =자동전환")
	private Boolean emergencyCreditAutoChange;
	
	@ColumnInfo(descr="Emergency Credit 최대기간 (Days)")
	private Integer emergencyCreditMaxDuration;
	private Integer lastChargeCnt;		//최종 충전횟수
	
	@Column(name="CONTRACT_DATE", length=14)
	@ColumnInfo(descr="계약일자")
	private String contractDate;

	@Column(name="CONTRACT_PRICE")
	@ColumnInfo(descr="계약금액")
	private Double contractPrice;
	
	@Column(name="THRESHOLD1")
	@ColumnInfo(descr="임계치1")
	private Double threshold1;
	
	@Column(name="THRESHOLD2")
    @ColumnInfo(descr="임계치2")
    private Double threshold2;
	
	@Column(name="THRESHOLD3")
    @ColumnInfo(descr="임계치3")
    private Double threshold3;

	//@ColumnInfo(descr="선불 인증을 위한 장비 아이디")
	//private String prepayMobileDevId;

	//private Integer prepaymentCheckPeriod;	//선불일경우 잔액체크주기 */ 

	// Emergency Credit Mode전환 인증장비 아이디
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="contract_id")
	private Set<PrepaymentAuthDevice> devices = new HashSet<PrepaymentAuthDevice>(0); 

	// 잔액 통보 주기 설정은 아래와 같다.
	// 1. 주기 : Daily
	//  1.1 간격 : 1 or 2 or ....6
	//  1.2 시간 : 12:00 AM ~ 12:00PM
	//-----------------------------------
	// 2. 주기 : Weekly
	//  2.1 간격 : 1 or 2 or ..4
	//  2.2 요일 : 월 or 화 or 수 or .... 일
	//  2.3 시간 : 12:00 AM ~ 12:00PM

	@Column(name="NOTIFICATION_PERIOD")
	@ColumnInfo(descr="통보 주기, 1:Daily, 2:Weekly")
	private Integer notificationPeriod;

	@Column(name="NOTIFICATION_INTERVAL")
	@ColumnInfo(descr="통보 간격")
	private Integer notificationInterval;

	@Column(name="NOTIFICATION_TIME")
	@ColumnInfo(descr="통보 시간 , 시간 : 0, 1, 2, ,,,,, , 23")
	private Integer notificationTime;

	@Column(name="NOTIFICATION_WEEKLY_MON")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 월요일 통보")
	private Boolean notificationWeeklyMon;

	@Column(name="NOTIFICATION_WEEKLY_TUE")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 화요일 통보")
	private Boolean notificationWeeklyTue;
	
	@Column(name="NOTIFICATION_WEEKLY_WED")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 수요일 통보")
	private Boolean notificationWeeklyWed;

	@Column(name="NOTIFICATION_WEEKLY_THU")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 목요일 통보")
	private Boolean notificationWeeklyThu;

	@Column(name="NOTIFICATION_WEEKLY_FRI")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 금요일 통보")
	private Boolean notificationWeeklyFri;
	
	@Column(name="NOTIFICATION_WEEKLY_SAT")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 토요일 통보")
	private Boolean notificationWeeklySat;
	
	@Column(name="NOTIFICATION_WEEKLY_SUN")
	@ColumnInfo(descr="통보 주기가 주별일 경우, true : 일요일 통보")
	private Boolean notificationWeeklySun;

	@Column(name="LAST_NOTIFICATION_DATE", length=14)
	@ColumnInfo(descr="마지막 잔액 통보 일자 (YYYYMMDDHHMMSS)")	
	private String lastNotificationDate;
	
	@Column(name="SMS_NUMBER")
	@ColumnInfo(descr="SMS를 보내고 리턴받은 messageId")
	private String smsNumber;

	@Column(name = "DELAY_DAY", length=255)
	@ColumnInfo(name="delayDay", descr="외부 연계 시스템에 빌링 정보 누락된 고객일 경우, delay일자를 저장하는 필드")
	private String delayDay;
    
	@Column(name = "APPLY_DATE", length=255)
	@ColumnInfo(name="applyDate", descr="계약 적용 날짜, 또는 SLA기준 일)")
	private String applyDate;

	@Column(name = "OLDARREARS", columnDefinition ="float default 0")
	@ColumnInfo(name="OLDARREARS")
	private Double oldArrears;
	
	@Column(name = "CURRENTARREARS", columnDefinition ="float default 0")
	@ColumnInfo(name="CURRENTARREARS", descr="미납금")
	private Double currentArrears;
	
	@Column(name = "CURRENTARREARS2", columnDefinition ="float default 0")
	@ColumnInfo(name="CURRENTARREARS2", descr="미납금2")
	private Double currentArrears2;
	
	@Column(name = "TOTALAMOUNTPAID")
	@ColumnInfo(name="TOTALAMOUNTPAID", descr="고객이 지불한 총 금액")
	private Double totalAmountPaid;

	/**
	 * 분할납부시 사용
	 * FIRSTARREARS, ARREARS_CONTRACT_COUNT, ARREARS_PAYMENT_COUNT
	 * 
	 */
	@Column(name = "FIRSTARREARS")
	@ColumnInfo(name="FIRSTARREARS", descr="처음 입력한 미납금 (미납금 지불 금액(초기미수금을 arrearsContractCount로 나눈 값)을 알기위함), 분할납부시 사용")
	private Double firstArrears;
	
	@Column(name = "ARREARS_CONTRACT_COUNT")
	@ColumnInfo(name="ARREARS_CONTRACT_COUNT", descr="미납금 지불 계약 횟수, 분할납부시 사용")
	private Integer arrearsContractCount;
	
	@Column(name = "ARREARS_PAYMENT_COUNT")
	@ColumnInfo(name="ARREARS_PAYMENT_COUNT", descr="미납금 지불 완료 횟수, 분할납부시 사용, 0이 default, 정상적으로 분할납부 완료시 해당 값은 null")
	private Integer arrearsPaymentCount;

	/**
	 * @deprecated This attribute is moved to Meter
	 */
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "distTrfmrSubstationMeter_A_id")
	@ReferencedBy(name="mdsId")
	@ColumnInfo(name="Distribution Transformer Substation A phase", descr="Distribution Transformer Substation 미터의 라인 A")
	private Meter distTrfmrSubstationMeter_A;
	
	@Deprecated
	@Column(name="distTrfmrSubstationMeter_A_id", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationMeter_A_id;

	@Deprecated
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "distTrfmrSubstationMeter_B_id")
	@ReferencedBy(name="mdsId")
	@ColumnInfo(name="Distribution Transformer Substation B phase", descr="Distribution Transformer Substation 미터의 라인 B")
	private Meter distTrfmrSubstationMeter_B;
	
	@Deprecated
	@Column(name="distTrfmrSubstationMeter_B_id", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationMeter_B_id;

	@Deprecated
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "distTrfmrSubstationMeter_C_id")
	@ReferencedBy(name="mdsId")
	@ColumnInfo(name="Distribution Transformer Substation C phase", descr="Distribution Transformer Substation 미터의 라인 C")
	private Meter distTrfmrSubstationMeter_C;
	
	@Deprecated
	@Column(name="distTrfmrSubstationMeter_C_id", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationMeter_C_id;

	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "sic_id")
    @ReferencedBy(name="code")
	private Code sic;
	
	@Column(name="sic_id", nullable=true, updatable=false, insertable=false)
	private Integer sicCodeId;
	
	@Column(name="RECEIPT_NUMBER")
	private String receiptNumber;
	
	@Column(name="AMOUNT_PAID")
	private Double amountPaid;
	
	@Column(name="SERVICETYPE2")
	@Enumerated(EnumType.STRING)
	@ColumnInfo(descr="1: New Service, 2: Seperate Meter, 3: Additional Load, 4:Replacement")
	private ServiceType2 serviceType2;
	
	private String barcode;

	@Column(name="PREPAY_START_TIME", length=14)
	@ColumnInfo(name="Prepay start time", descr="선불 시작일시")  
	private String prepayStartTime;

    @Column(name="CHARGE_AVAILABLE", columnDefinition="INTEGER default 1", length=1)
    @ColumnInfo(name="Charge available", descr="충전 가능여부")
	private Boolean chargeAvailable;

    @Column(name="ADDRESS1")
    @ColumnInfo(name="address1")
    private String address1;
    
    @Column(name="ADDRESS2")
    @ColumnInfo(name="address2")
    private String address2;
    
    @Column(name="ADDRESS3")
    @ColumnInfo(name="address3")
    private String address3;
    
    @Column(name="SERVICE_POINT_ID")
    @ColumnInfo(name="ServicePointId")
    private String servicePointId;
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id")
	private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	@ColumnInfo(descr="계약을 생성한 operator를 저장")
	private Integer operatorId;
    
	@Column(name="CASH_POINT")
    @ColumnInfo(name="충전포인트", descr="가나 충전소의 포인트")
    private Integer cashPoint;
	
	public String getReceiptNumber() {
		return receiptNumber;
	}
	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}
	public Double getAmountPaid() {
		return amountPaid;
	}
	public void setAmountPaid(Double amountPaid) {
		this.amountPaid = amountPaid;
	}
	public ServiceType2 getServiceType2() {
		return serviceType2;
	}
	public void setServiceType2(String serviceType2) {
	    this.serviceType2 = ServiceType2.valueOf(serviceType2);
	}
	
	@XmlTransient
	@Deprecated
	public Meter getDistTrfmrSubstationMeter_A() {
		return distTrfmrSubstationMeter_A;
	}
	@Deprecated
	public void setDistTrfmrSubstationMeter_A(Meter distTrfmrSubstationMeterA) {
		distTrfmrSubstationMeter_A = distTrfmrSubstationMeterA;
	}
	
	@XmlTransient
	@Deprecated
	public Meter getDistTrfmrSubstationMeter_B() {
		return distTrfmrSubstationMeter_B;
	}
	@Deprecated
	public void setDistTrfmrSubstationMeter_B(Meter distTrfmrSubstationMeterB) {
		distTrfmrSubstationMeter_B = distTrfmrSubstationMeterB;
	}
	
	@XmlTransient
	@Deprecated
	public Meter getDistTrfmrSubstationMeter_C() {
		return distTrfmrSubstationMeter_C;
	}
	@Deprecated
	public void setDistTrfmrSubstationMeter_C(Meter distTrfmrSubstationMeterC) {
		distTrfmrSubstationMeter_C = distTrfmrSubstationMeterC;
	}

	public String getApplyDate() {
		return applyDate;
	}
	public void setApplyDate(String applyDate) {
		this.applyDate = applyDate;
	}
	public String getDelayDay() {
		return delayDay;
	}
	public void setDelayDay(String delayDay) {
		this.delayDay = delayDay;
	}
	////////////////////////////////////////////////
	//상기, 선불 고객 관련 속성 END
	////////////////////////////////////////////////
	public String getLastNotificationDate() {
		return lastNotificationDate;
	}
	public void setLastNotificationDate(String lastNotificationDate) {
		this.lastNotificationDate = lastNotificationDate;
	}
	
	@XmlTransient
	public Set<PrepaymentAuthDevice> getDevices() {
		return devices;
	}
	public void setDevices(Set<PrepaymentAuthDevice> devices) {
		this.devices = devices;
	}
	
	public Integer getNotificationPeriod() {
		return notificationPeriod;
	}
	public void setNotificationPeriod(Integer notificationPeriod) {
		this.notificationPeriod = notificationPeriod;
	}
	public Integer getNotificationInterval() {
		return notificationInterval;
	}
	public void setNotificationInterval(Integer notificationInterval) {
		this.notificationInterval = notificationInterval;
	}
	public Integer getNotificationTime() {
		return notificationTime;
	}
	public void setNotificationTime(Integer notificationTime) {
		this.notificationTime = notificationTime;
	}
	public Boolean getNotificationWeeklyMon() {
		return notificationWeeklyMon;
	}
	public void setNotificationWeeklyMon(Boolean notificationWeeklyMon) {
		this.notificationWeeklyMon = notificationWeeklyMon;
	}
	public Boolean getNotificationWeeklyTue() {
		return notificationWeeklyTue;
	}
	public void setNotificationWeeklyTue(Boolean notificationWeeklyTue) {
		this.notificationWeeklyTue = notificationWeeklyTue;
	}
	public Boolean getNotificationWeeklyWed() {
		return notificationWeeklyWed;
	}
	public void setNotificationWeeklyWed(Boolean notificationWeeklyWed) {
		this.notificationWeeklyWed = notificationWeeklyWed;
	}
	public Boolean getNotificationWeeklyThu() {
		return notificationWeeklyThu;
	}
	public void setNotificationWeeklyThu(Boolean notificationWeeklyThu) {
		this.notificationWeeklyThu = notificationWeeklyThu;
	}
	public Boolean getNotificationWeeklyFri() {
		return notificationWeeklyFri;
	}
	public void setNotificationWeeklyFri(Boolean notificationWeeklyFri) {
		this.notificationWeeklyFri = notificationWeeklyFri;
	}
	public Boolean getNotificationWeeklySat() {
		return notificationWeeklySat;
	}
	public void setNotificationWeeklySat(Boolean notificationWeeklySat) {
		this.notificationWeeklySat = notificationWeeklySat;
	}
	public Boolean getNotificationWeeklySun() {
		return notificationWeeklySun;
	}
	public void setNotificationWeeklySun(Boolean notificationWeeklySun) {
		this.notificationWeeklySun = notificationWeeklySun;
	}
	public String getSmsNumber() {
		return smsNumber;
	}
	public void setSmsNumber(String smsNumber) {
		this.smsNumber = smsNumber;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}	
	
	public String getContractNumber() {
		return contractNumber;
	}
	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}
	public String getContractDate() {
		return contractDate;
	}
	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}
	public Double getContractPrice() {
		return contractPrice;
	}
	public void setContractPrice(Double contractPrice) {
		this.contractPrice = contractPrice;
	}
	
	@XmlTransient
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}
	
	@XmlTransient
	public Code getServiceTypeCode() {
		return serviceTypeCode;
	}
	public void setServiceTypeCode(Code serviceTypeCode) {
		this.serviceTypeCode = serviceTypeCode;
	}
	public Integer getContractIndex() {
		return contractIndex;
	}
	public void setContractIndex(Integer contractIndex) {
		this.contractIndex = contractIndex;
	}
	
	@XmlTransient
	public Meter getMeter() {
		return meter;
	}
	public void setMeter(Meter meter) {
		this.meter = meter;
	}	
	
	public Integer getBillDate() {
		return billDate;
	}
	public void setBillDate(Integer billDate) {
		this.billDate = billDate;
	}
	
	@XmlTransient
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	
	@XmlTransient
	public TariffType getTariffIndex() {
		return tariffIndex;
	}
	public void setTariffIndex(TariffType tariffIndex) {
		this.tariffIndex = tariffIndex;
	}
	public Double getContractDemand() {
		return contractDemand;
	}
	public void setContractDemand(Double contractDemand) {
		this.contractDemand = contractDemand;
	}
	
	@XmlTransient
	public Code getStatus() {
		return status;
	}
	public void setStatus(Code status) {
		this.status = status;
	}
	
	@XmlTransient
	public Code getCreditType() {
		return creditType;
	}
	public void setCreditType(Code creditType) {
		this.creditType = creditType;
	}
	
	@XmlTransient
	public Code getCreditStatus() {
		return creditStatus;
	}
	public void setCreditStatus(Code creditStatus) {
		this.creditStatus = creditStatus;
	}
//	public void setPrepaymentCheckPeriod(Integer prepaymentCheckPeriod) {
//		this.prepaymentCheckPeriod = prepaymentCheckPeriod;
//	}
//	public Integer getPrepaymentCheckPeriod() {
//		return prepaymentCheckPeriod;
//	}
//	public void setPrepayMobileDevId(String prepayMobileDevId) {
//		this.prepayMobileDevId = prepayMobileDevId;
//	}
//	public String getPrepayMobileDevId() {
//		return prepayMobileDevId;
//	}	
	public Integer getPrepaymentThreshold() {
		return prepaymentThreshold;
	}
	public void setPrepaymentThreshold(Integer prepaymentThreshold) {
		this.prepaymentThreshold = prepaymentThreshold;
	}
	public String getKeyNum() {
		return keyNum;
	}
	public void setKeyNum(String keyNum) {
		this.keyNum = keyNum;
	}
	
	@XmlTransient
	public Code getKeyType() {
		return keyType;
	}
	public void setKeyType(Code keyType) {
		this.keyType = keyType;
	}
	public Double getChargedCredit() {
		return chargedCredit;
	}
	public void setChargedCredit(Double chargedCredit) {
		this.chargedCredit = chargedCredit;
	}
	public Double getCurrentCredit() {
		return currentCredit;
	}
	public void setCurrentCredit(Double currentCredit) {
		this.currentCredit = currentCredit;
	}
	public String getLastTokenDate() {
		return lastTokenDate;
	}
	public void setLastTokenDate(String lastTokenDate) {
		this.lastTokenDate = lastTokenDate;
	}
	public String getLastTokenId() {
		return lastTokenId;
	}
	public void setLastTokenId(String lastTokenId) {
		this.lastTokenId = lastTokenId;
	}
	public Boolean getEmergencyCreditAvailable() {
		return emergencyCreditAvailable;
	}
	public void setEmergencyCreditAvailable(Boolean emergencyCreditAvailable) {
		this.emergencyCreditAvailable = emergencyCreditAvailable;
	}
	public Integer getLastChargeCnt() {
		return lastChargeCnt;
	}
	public void setLastChargeCnt(Integer lastChargeCnt) {
		this.lastChargeCnt = lastChargeCnt;
	}

	public String toString()
	{
	    return "Contract "+toJSONString();
	}
	public String toJSONString() {
	    String retValue = "";
        
	    retValue = "{"	        
	        + "id:'" + this.id	     
	        + "',contractNumber:'" + this.contractNumber
	        + "',customer:'" + ((this.customer == null)? "null":this.customer.getId())
	        + "',supplier:'" + ((this.supplier == null)? "null":this.supplier.getId())
	        + "',tariffIndex:'" + ((this.tariffIndex == null)? "null":this.tariffIndex.getId())	// 계약종별	        
	        + "',serviceTypeCode:'" + ((this.serviceTypeCode == null)? "null":this.serviceTypeCode.getId())
	        + "',location:'" + ((this.location == null)? "null":this.location.getId())
	        + "',contractDemand:'" + ((this.contractDemand == null)? "":this.contractDemand)
	        + "',status:'" + ((this.status == null)? "null":this.status.getId())
	        + "',creditType:'" + ((this.creditType == null)? "null":this.creditType.getId())
	        + "',creditStatus:'" + ((this.creditStatus == null)? "null":this.creditStatus.getId())
	        + "',currentCredit:'" + this.currentCredit 
	        + "',sic:'" + ((this.sic == null) ? "null":this.sic.getId())
	        + "',prepaymentThreshold:'" + this.prepaymentThreshold
	        + "',receiptNumber:'" + this.receiptNumber
	        + "',amountPaid:'" + ((this.amountPaid == null)? "":this.amountPaid)
	        + "',serviceType2:'" + this.serviceType2
	        + "',currentArrears:'" + ((this.currentArrears == null)? "":this.currentArrears)
	        + "',currentArrears2:'" + ((this.currentArrears2 == null)? "":this.currentArrears2)
	        + "',totalAmountPaid:'" + ((this.totalAmountPaid == null)? "":this.totalAmountPaid)
	        + "',chargeAvailable:'" + ((this.chargeAvailable == null)? "":this.chargeAvailable)
	        + "',threshold1:'" + ((this.threshold1 == null)? 0.0:this.threshold1)
	        + "',threshold2:'" + ((this.threshold2 == null)? 0.0:this.threshold2)
	        + "',threshold3:'" + ((this.threshold3 == null)? 0.0:this.threshold3)
	        + "',oldArrears:'" + ((this.oldArrears == null)? "" :this.oldArrears)
	        + "',firstArrears:'" + ((this.firstArrears == null)? "" :this.firstArrears)
	        + "',arrearsContractCount:'" + ((this.arrearsContractCount == null)? "":this.arrearsContractCount) 
	        + "',arrearsPaymentCount:'" + ((this.arrearsPaymentCount == null)? "":this.arrearsPaymentCount)
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
	public void setEmergencyCreditAutoChange(Boolean emergencyCreditAutoChange) {
		this.emergencyCreditAutoChange = emergencyCreditAutoChange;
	}
	public Boolean getEmergencyCreditAutoChange() {
		return emergencyCreditAutoChange;
	}
	public void setEmergencyCreditMaxDuration(Integer emergencyCreditMaxDuration) {
		this.emergencyCreditMaxDuration = emergencyCreditMaxDuration;
	}
	public Integer getEmergencyCreditMaxDuration() {
		return emergencyCreditMaxDuration;
	}
	public void setPrepaymentPowerDelay(Double prepaymentPowerDelay) {
		this.prepaymentPowerDelay = prepaymentPowerDelay;
	}
	public Double getPrepaymentPowerDelay() {
		return prepaymentPowerDelay;
	}
	public void setEmergencyCreditStartTime(String emergencyCreditStartTime) {
		this.emergencyCreditStartTime = emergencyCreditStartTime;
	}
	public String getEmergencyCreditStartTime() {
		return emergencyCreditStartTime;
	}
	
	@XmlTransient
	public Code getSic() {
        return sic;
    }
    public void setSic(Code sic) {
        this.sic = sic;
    }
    
    public Integer getCustomerId() {
        return customerId;
    }
    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
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
	public String getPreMdsId() {
		return preMdsId;
	}
	public void setPreMdsId(String preMdsId) {
		this.preMdsId = preMdsId;
	}
    public Integer getLocationId() {
        return locationId;
    }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }
    public Integer getTariffIndexId() {
        return tariffIndexId;
    }
    public void setTariffIndexId(Integer tariffIndexId) {
        this.tariffIndexId = tariffIndexId;
    }
	@Deprecated
    public Integer getDistTrfmrSubstationMeter_A_id() {
        return distTrfmrSubstationMeter_A_id;
    }
	@Deprecated
    public void setDistTrfmrSubstationMeter_A_id(
            Integer distTrfmrSubstationMeter_A_id) {
        this.distTrfmrSubstationMeter_A_id = distTrfmrSubstationMeter_A_id;
    }
	@Deprecated
    public Integer getDistTrfmrSubstationMeter_B_id() {
        return distTrfmrSubstationMeter_B_id;
    }
	@Deprecated
    public void setDistTrfmrSubstationMeter_B_id(
            Integer distTrfmrSubstationMeter_B_id) {
        this.distTrfmrSubstationMeter_B_id = distTrfmrSubstationMeter_B_id;
    }
	@Deprecated
    public Integer getDistTrfmrSubstationMeter_C_id() {
        return distTrfmrSubstationMeter_C_id;
    }
	@Deprecated
    public void setDistTrfmrSubstationMeter_C_id(
            Integer distTrfmrSubstationMeter_C_id) {
        this.distTrfmrSubstationMeter_C_id = distTrfmrSubstationMeter_C_id;
    }
    
    public Integer getServiceTypeCodeId() {
        return serviceTypeCodeId;
    }
    public void setServiceTypeCodeId(Integer serviceTypeCodeId) {
        this.serviceTypeCodeId = serviceTypeCodeId;
    }
    public Integer getStatusCodeId() {
        return statusCodeId;
    }
    public void setStatusCodeId(Integer statusCodeId) {
        this.statusCodeId = statusCodeId;
    }
    public Integer getCreditTypeCodeId() {
        return creditTypeCodeId;
    }
    public void setCreditTypeCodeId(Integer creditTypeCodeId) {
        this.creditTypeCodeId = creditTypeCodeId;
    }
    public Integer getCreditStatusCodeId() {
        return creditStatusCodeId;
    }
    public void setCreditStatusCodeId(Integer creditStatusCodeId) {
        this.creditStatusCodeId = creditStatusCodeId;
    }
    public Integer getKeyTypeCodeId() {
        return keyTypeCodeId;
    }
    public void setKeyTypeCodeId(Integer keyTypeCodeId) {
        this.keyTypeCodeId = keyTypeCodeId;
    }
    public Integer getSicCodeId() {
        return sicCodeId;
    }
    public void setSicCodeId(Integer sicCodeId) {
        this.sicCodeId = sicCodeId;
    }
    public Double getOldArrears() {
		return oldArrears;
	}
	public void setOldArrears(Double oldArrears) {
		this.oldArrears = oldArrears;
	}
    public Double getCurrentArrears() {
		return currentArrears;
	}
	public void setCurrentArrears(Double arrears) {
		this.currentArrears = arrears;
	}
    public Double getCurrentArrears2() {
		return currentArrears2;
	}
	public void setCurrentArrears2(Double arrears2) {
		this.currentArrears2 = arrears2;
	}
    public Double getTotalAmountPaid() {
		return totalAmountPaid;
	}
	public void setTotalAmountPaid(Double totalAmountPaid) {
		this.totalAmountPaid = totalAmountPaid;
	}
	public Double getFirstArrears() {
		return firstArrears;
	}
	public void setFirstArrears(Double firstArrears) {
		this.firstArrears = firstArrears;
	}
	public Integer getArrearsContractCount() {
		return arrearsContractCount;
	}
	public void setArrearsContractCount(Integer arrearsContractCount) {
		this.arrearsContractCount = arrearsContractCount;
	}
	public Integer getArrearsPaymentCount() {
		return arrearsPaymentCount;
	}
	public void setArrearsPaymentCount(Integer arrearsPaymentCount) {
		this.arrearsPaymentCount = arrearsPaymentCount;
	}
	/**
     * @return the prepayStartTime
     */
    public String getPrepayStartTime() {
        return prepayStartTime;
    }

    /**
     * @param prepayStartTime the prepayStartTime to set
     */
    public void setPrepayStartTime(String prepayStartTime) {
        this.prepayStartTime = prepayStartTime;
    }

    /**
     * @return the chargeAvailable
     */
    public Boolean getChargeAvailable() {
        return chargeAvailable;
    }

    /**
     * @param chargeAvailable the chargeAvailable to set
     */
    public void setChargeAvailable(Boolean chargeAvailable) {
        this.chargeAvailable = chargeAvailable;
    }

    @Override
	public String getInstanceName() {
	    return getContractNumber();
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
    public Double getThreshold1() {
        return threshold1;
    }
    public void setThreshold1(Double threshold1) {
        this.threshold1 = threshold1;
    }
    public Double getThreshold2() {
        return threshold2;
    }
    public void setThreshold2(Double threshold2) {
        this.threshold2 = threshold2;
    }
    public Double getThreshold3() {
        return threshold3;
    }
    public void setThreshold3(Double threshold3) {
        this.threshold3 = threshold3;
    }
    public void setServiceType2(ServiceType2 serviceType2) {
        this.serviceType2 = serviceType2;
    }
    public String getAddress1() {
        return address1;
    }
    public void setAddress1(String address1) {
        this.address1 = address1;
    }
    public String getAddress2() {
        return address2;
    }
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getAddress3() {
        return address3;
    }
    public void setAddress3(String address3) {
        this.address3 = address3;
    }
    public String getServicePointId() {
        return servicePointId;
    }
    public void setServicePointId(String servicePointId) {
        this.servicePointId = servicePointId;
    }
	@XmlTransient
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
    public Integer getCashPoint() {
        return cashPoint;
    }
    public void setCashPoint(Integer cashPoint) {
        this.cashPoint = cashPoint;
    }
}
