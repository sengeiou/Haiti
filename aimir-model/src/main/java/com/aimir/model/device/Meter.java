package com.aimir.model.device;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> 전기, 가스,수도 , 열량, 등의 미터 정보의 공통 속성을 정의한 클래스 </p>
 * 
 * <pre>
 * 전기,수도,가스,열량,대용량가스 
 * 모뎀이 M-Bus인 경우 또는 여러개의 미터를 가질 수 있는 모뎀의 경우 포트로 구분한다. 
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="modem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}baseObject">
 *       &lt;sequence>
 *         &lt;element name="address" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amiNetworkAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="amiNetworkDepth" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="condition" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="deleteDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="distTrfmrSubstationId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="endDeviceId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="expirationDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="friendlyName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="gpioX" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gpioY" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="gpioZ" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="hwVerstion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ihdId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="installDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="installDateHidden" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="installDateUpdate" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="installDateSiteImg" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="installId" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="installProperty" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="isManualMeter" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="lastMeteringValue" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="lastReadDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="lastTimesyncDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="locationId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="lpInterval" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="manufacturedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="mdsId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meterCaution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meterError" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="meterTypeCodeId" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="modelId" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="modemId" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="modemPort" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="prepaymentMeter" type="{http://server.ws.command.fep.aimir.com/}boolean" minOccurs="0"/>
 *         &lt;element name="protocolVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pulseConstant" type="{http://server.ws.command.fep.aimir.com/}double" minOccurs="0"/>
 *         &lt;element name="purchasePrice" type="{http://server.ws.command.fep.aimir.com/}double" minOccurs="0"/>
 *         &lt;element name="qualifiedDate" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="shortId" type="{http://server.ws.command.fep.aimir.com/}int" minOccurs="0"/>
 *         &lt;element name="supplierId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="swName" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="swUpdateDate" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="swVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="timeDiff" type="{http://server.ws.command.fep.aimir.com/}long" minOccurs="0"/>
 *         &lt;element name="usageThreshold" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="writeDate" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="phase" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *         &lt;element name="msa" type="{http://server.ws.command.fep.aimir.com/}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "meter", propOrder = {
        "address",
        "amiNetworkAddress",
        "amiNetworkDepth",
        "conditions",
        "deleteDate",
        "distTrfmrSubstationId",
        "distTrfmrSubstationMeter_A_id",
        "distTrfmrSubstationMeter_B_id",
        "distTrfmrSubstationMeter_C_id",
        "endDeviceId",
        "expirationDate",
        "friendlyName",
        "gpioX",
        "gpioY",
        "gpioZ",
        "hwVersion",
        "id",
        "ihdId",
        "installDate",
        "installDateHidden",
        "installDateUpdate",
        "installedSiteImg",
        "installId",
        "installProperty",
        "isManualMeter",
        "lastMeteringValue",
        "lastReadDate",
        "lastTimesyncDate",
        "locationId",
        "lpInterval",
        "manufacturedDate",
        "mdsId",
        "meterCaution",
        "meterError",
        "meterStatusCodeId",
        "meterTypeCodeId",
        "modelId",
        "modemId",
        "modemPort",
        "prepaymentMeter",
        "protocolVersion",
        "pulseConstant",
        "purchasePrice",
        "qualifiedDate",
        "shortId",
        "supplierId",
        "swName",
        "swUpdateDate",
        "swVersion",
        "timeDiff",
        "usageThreshold",
        "writeDate",
        "gs1",
        "phase",
        "msa"
})
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="METER",discriminatorType=DiscriminatorType.STRING)
@Table(name="METER")
// @Cache(type=CacheType.SOFT)
@Indexes({
    @Index(name="IDX_METER_01", columnNames={"modem_id"}),
    @Index(name="IDX_METER_02", columnNames={"gs1"})
})
public class Meter extends BaseObject implements JSONString, IAuditable {
    
	private static final long serialVersionUID = 4425586603710572606L;

	/**
	 * 시스템이 부여한 시퀀스 아이디
	 */
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="METER_SEQ")
    @SequenceGenerator(name="METER_SEQ", sequenceName="METER_SEQ", allocationSize=1) 
	private Integer id;

//    @Version
//    Integer version;

	/**
	 * 미터 아이디
	 * <br>사람 혹은 장비에서 올라오는 값. 반드시 있어야 하는 값이며, 중복 되는 값은 사용할 수 없음
	 */
    @ColumnInfo(name="미터아이디", view=@Scope(create=true, read=true, update=false), descr="사람 혹은 장비에서 올라오는 값. 반드시 있어야 하는 값이며, 중복 되는 값은 사용할 수 없음")
    @Column(name="MDS_ID", nullable=false, unique=true)
    private String mdsId;
    
    /**
     * 미터 설치 정보 (미터시리얼번호 외에 미터에서 관리하는 관리번호등등의 값이 될 수 있음
     */
    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false), descr="미터 설치 정보 (미터시리얼번호 외에 미터에서 관리하는 관리번호등등의 값이 될 수 있음")
    @Column(name="INSTALL_PROPERTY")
    private String installProperty;

    /**
     * 공급사
     */
    @XmlTransient
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    /**
     * 미터 제조 정보
     */
    @XmlTransient
    @ColumnInfo(name="미터 모델", view=@Scope(create=true, read=true, update=true), descr="미터 제조사 모델의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="devicemodel_id")
    @ReferencedBy(name="name")
    private DeviceModel model;
    
    @Column(name="devicemodel_id", nullable=true, updatable=false, insertable=false)
    private Integer modelId;
    
    /**
     * 미터 유형 코드
     * <br>1:전기, 2:수도, 3:가스, 4:열량, 5:보정기
     */
    @XmlTransient
    @ColumnInfo(name="미터 타입", view=@Scope(create=true, read=true, update=false), descr="전기 가스 수도 열량 보정기의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="metertype_id")
    @ReferencedBy(name="code")
    private Code meterType;

    @Column(name="metertype_id", nullable=true, updatable=false, insertable=false)
    private Integer meterTypeCodeId;
    /**
     * MBus 모뎀과 연결된 미터를 구분하기 위한 포트 번호
     */
	@ColumnInfo(name="모뎀포트")
    @Column(name="MODEM_PORT")
    private Integer modemPort;

	/**
	 * 설치일자 yyyymmddhhmmss
	 */
    @ColumnInfo(name="설치일자", view=@Scope(create=true, read=true, update=true), descr="YYYYMMDDHHMMSS")
    @Column(name="INSTALL_DATE")
    private String installDate;
    private String installDateHidden;
    private String installDateUpdate;

	/**
	 * 설치 아이디
	 */
    @ColumnInfo(name="설치아이디", view=@Scope(create=true, read=true, update=true, delete=true))
    @Column(name="INSTALL_ID")
    private String installId;

    /**
     * IHD 아이디
     */
    @ColumnInfo(name="IHD아이디", view=@Scope(create=true, read=true, update=true, delete=true))
    @Column(name="IHD_ID")
    private String ihdId;

    /**
     * 사용량 임계치
     */
    @ColumnInfo(name="사용량 임계치",view=@Scope(create=true, read=true, update=true) )
    @Column(name="USAGE_THRESHOLD")
    private Double usageThreshold;
    
    /**
     * 계약정보
     */
//     @ColumnInfo(name="계약아이디", view=@Scope(create=true, read=true, update=true, delete=true), descr="계약번호")
//     @OneToOne
//     @JoinColumn(name="CONTRACT_NUMBER")
//     @ReferencedBy(name="contractNumber")
    @XmlTransient
    @OneToOne(mappedBy="meter", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Contract contract;
    
    
    /**
     * 검침주기 (5, 15, 30, 60분)
     */
    @ColumnInfo(name="검침 주기",view=@Scope(create=true, read=true, update=true) )
    @Column(name="LP_INTERVAL")
    private Integer lpInterval;

    /**
     * 미터 시간과 서버 시간의 차. 시간차가 많으면 동기화를 해줘야 한다. 미터 관리 가젯
     */
    @ColumnInfo(name="시간차", view=@Scope(create=false, read=true, update=false), descr="미터 시간과 서버 시간의 차")
    @Column(name="TIME_DIFF")
    private Long timeDiff;

    /**
     * 미터와 연결된 모뎀 정보
     */
    @XmlTransient
    @ColumnInfo(name="모뎀", view=@Scope(create=true, read=true, update=true), descr="모뎀 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="MODEM_ID")
    @ReferencedBy(name="deviceSerial")
    private Modem modem;
    
    @Column(name="MODEM_ID", nullable=true, updatable=false, insertable=false)
    private Integer modemId;
    
    /**
     * 미터가 계측하는 대상 장비 (설비 또는 기타)
     */
    @XmlTransient
    @ColumnInfo(name="장비", view=@Scope(create=true, read=true, update=true), descr="장비")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ENDDEVICE_ID")
    @ReferencedBy(name="uuid")
    private EndDevice endDevice;
    
    @Column(name="ENDDEVICE_ID", nullable=true, updatable=false, insertable=false)
    private Integer endDeviceId;

    /**
     * 소프트웨어 버젼. 디지털  방식의 미터인 경우 해당 정보 있음 : Code 1.3.6 참조
     */
    @ColumnInfo(name="소프트웨어 버젼", view=@Scope(create=true, read=true, update=false), descr="디지털  방식의 미터인 경우 해당 정보 있음 : Code 1.3.6 참조")
    @Column(name="SW_VERSION")
    private String swVersion;

    /**
     * 하드웨어 버젼. 디지털  방식의 미터인 경우 해당 정보 있음 : Code 1.3.7 참조
     */
    @ColumnInfo(name="하드웨어 버젼", view=@Scope(create=true, read=true, update=false), descr="디지털  방식의 미터인 경우 해당 정보 있음 : Code 1.3.7 참조")
    @Column(name="HW_VERSION")
    private String hwVersion;

    /**
     * 소프트웨어 명칭
     */
    @ColumnInfo(name="소프트웨어명", view=@Scope(create=true, read=true, update=false), descr="디지털  방식의 미터인 경우 해당 정보 있음")
    @Column(name="SW_NAME")
    private String swName;

    /**
     * 소프트웨어 업데이트 날짜. yyyymmddhhmmss
     */
    @ColumnInfo(name="소프트웨어 업데이트 날짜", view=@Scope(create=true, read=true, update=false), descr="디지털  방식의 미터인 경우 해당 정보 있음")
    @Column(name="SW_UPDATE_DATE")
    private String swUpdateDate;

    /**
     * 설치 이미지. 현장에 설치된 이미지를 실어야 한다.
     */
    @ColumnInfo(name="설치된 이미지 ", view=@Scope(create=true, read=true, update=true), descr="설치후 찍은 이미지의 경로")
    @Column(name="INSTALLED_SITE_IMG")
    private String installedSiteImg;

    /**
     * 미터 에러 발생 원인. 에러에 대한 원인 보여주기 위한 필드이지만 현재 이 정보를 갱신하는 곳이 없다.
     */
    @ColumnInfo(name="미터에서 발생한 경고 ", view=@Scope(create=false, read=true, update=false))
    @Column(name="METER_CAUTION")
    private String meterCaution;

    /**
     * 미터에서 발생한 에러정보. 중요한 에러 정보를 보여주기 위한 필드이지만 현재 이 정보를 갱신하는 곳이 없다.
     */
    @ColumnInfo(name="미터에서 발생한 에러정보", view=@Scope(create=false, read=true, update=false))
    @Column(name="METER_ERROR")
    private String meterError;

    /**
     * 미터가 설치된 위치. 주소와는 다르다. 공급사(빌딩)에서 관리하는 공급(관리)지역
     */
    @XmlTransient
    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL", view=@Scope(create=true, read=true, update=true) )
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LOCATION_ID")
	@ReferencedBy(name="name")
	private Location location;

    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    /**
     * 미터가 설치된 상세 주소
     */
    @ColumnInfo(name="주소", view=@Scope(create=true, read=true, update=true) )
    @Column(name="ADDRESS")
    private String address;

    /**
     * GIS 연동 X 좌표
     */
    @ColumnInfo(name="GPIOX", view=@Scope(create=true, read=true, update=true) )
    @Column(name="GPIOX")
    private Double gpioX;

    /**
     * GIS 연동 Y 좌표
     */
    @ColumnInfo(name="GPIOY", view=@Scope(create=true, read=true, update=true))
    @Column(name="GPIOY")
    private Double gpioY;

    /**
     * GIS 연동 Z 좌표
     */
    @ColumnInfo(name="GPIOZ", view=@Scope(create=true, read=true, update=true))
    @Column(name="GPIOZ")
    private Double gpioZ;

    /**
     * 마지막 통신 날짜. yyyymmddhhmmss
     */
    @ColumnInfo(name="마지막 통신 날짜", view=@Scope(create=false, read=true, update=false), descr="마지막에 통신한 날짜,yyyymmddhhmmss")
    @Column(name="LAST_READ_DATE",length=14)
    private String lastReadDate;
    
    /**
     * 마지막 시간 동기화 날짜. yyyymmddhhmmss
     */
    @ColumnInfo(name="마지막 시간 동기화 날짜", view=@Scope(create=false, read=true, update=false), descr="마지막에 통신한 날짜,yyyymmddhhmmss")
    @Column(name="LAST_TIMESYNC_DATE",length=14)
    private String lastTimesyncDate;
    
    /**
     * 마지막 변경날짜. yyyymmddhhmmss
     */
    @ColumnInfo(name="마지막 변경날짜",view=@Scope(create=false, read=true, update=false), descr="마지막에 정보 변경한 날짜,yyyymmddhhmmss")
    @Column(name="WRITE_DATE",length=14)
    private String writeDate;
    
    /**
     * 최종 검침 값
     */
    @ColumnInfo(name="최종 검침 값", view=@Scope(create=false, read=true, update=false), descr="최근에 검침된 값지침 ")
    @Column(name="LAST_METERING_VALUE")
    private Double lastMeteringValue;

    /**
     * 검침 최초 시작일. yyyymmddhhmmss
     */
	@ColumnInfo(name="검침 최초 시작일", view=@Scope(create=true, read=true, update=true))
    @Column(name="QUALIFIED_DATE", length=8)
    private String qualifiedDate;
    
	/**
	 * 유효기간. yyyymmddhhmmss
	 */
    @ColumnInfo(name="유효기간 ", view=@Scope(create=true, read=true, update=true))
    @Column(name="EXPIRATION_DATE", length=8)
    private String expirationDate;
    
    /**
     * 펄스 상수 또는 에너지 상수(ke). 검침값을 kW로 환산하기 위한 상수이다.
     * <br>예로 펄스식 계량기의 경우 100펄스 당 1kW로 계산한다면 상수값을 100을 입력한다.
     * <br>주의해야할 점은 파서마다 상수를 이용한 환산 방법이 다를 수 있으므로 확인후 정확한 값을 입력해야 한다.
     */
    @ColumnInfo(name="", descr="펄스 상수 : 펄스를 value로 환산하기 위한 값", view=@Scope(create=true, read=true, update=true))
    @Column(name="PULSE_CONSTANT", length=10)
    private Double pulseConstant;

    /**
     * 미터 상태. 미터 유형에 따라 상태값이 다르므로 코드를 참조한다.
     * <br>유형에 따른 미터 상태가 없으면 Code 1.3.3 을 이용한다.
     * <br>수도, 수도의 경우 1.3.1.2.1, 1.3.1.3.1을 참조한다.
     */
    @XmlTransient
    @ColumnInfo(name="미터 상태", descr="코드 테이블의 ID 혹은  NULL : Code 1.3.3 참조", view=@Scope(create=true, read=true, update=true))
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="METER_STATUS")
    @ReferencedBy(name="code")
    private Code meterStatus;
    
    @Column(name="METER_STATUS", nullable=true, updatable=false, insertable=false)
    private Integer meterStatusCodeId;
    /**
     * 미터가 하드웨어적/소프트웨어적으로 선불기능을 포함하고 있는지 아닌지 체크 
     * <br>default : false
     */
    @ColumnInfo(name="미터가 하드웨어적/소프트웨어적으로 선불기능을 포함하고 있는지 아닌지 체크 default : false",view=@Scope(create=true, read=true, update=true) )
    @Column(name="PREPAYMENT_METER")
    private Boolean prepaymentMeter;
    
    /**
     * 미터제조일자. yyyymmddhhmmss
     */
    @ColumnInfo(name="미터 제조일자", view=@Scope(create=true, read=true, update=true), descr="미터 제조일자")
    @Column(name="MANUFACTURED_DATE", length=8)
    private String manufacturedDate;    
	
    /**
     * 구입금액.
     */
	@Column(name="PURCHASE_PRICE")
	@ColumnInfo(descr="구입금액")
	private Double purchasePrice;
	
	/**
     * GS1 - 바코드
     */
	@Column(name="GS1")
	@ColumnInfo(descr="바코드 정보")
	private String gs1;

	/**
	 * 변압기 정보
	 */
	@XmlTransient
	@ColumnInfo(name="Distribution Transformer Substation 아이디", descr="Distribution Transformer Substation 테이블의 ID 혹은  NULL")
	@ManyToOne(fetch = FetchType.LAZY)    
    @JoinColumn(name="DistTrfmrSubstation_ID")
    @ReferencedBy(name="name")
    private DistTrfmrSubstation distTrfmrSubstation;
	
	@Column(name="DistTrfmrSubstation_ID", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationId;
	
	@ColumnInfo(name="shortId", descr="ID of device managed by MCU")
    @Column(name="SHORT_ID")
    private Integer shortId;

	public Meter getDistTrfmrSubstationMeter_A() {
		return distTrfmrSubstationMeter_A;
	}

	public void setDistTrfmrSubstationMeter_A(Meter distTrfmrSubstationMeterA) {
		distTrfmrSubstationMeter_A = distTrfmrSubstationMeterA;
	}

	public Integer getDistTrfmrSubstationMeter_A_id() {
		return distTrfmrSubstationMeter_A_id;
	}

	public void setDistTrfmrSubstationMeter_A_id(Integer distTrfmrSubstationMeterAId) {
		distTrfmrSubstationMeter_A_id = distTrfmrSubstationMeterAId;
	}

	public Meter getDistTrfmrSubstationMeter_B() {
		return distTrfmrSubstationMeter_B;
	}

	public void setDistTrfmrSubstationMeter_B(Meter distTrfmrSubstationMeterB) {
		distTrfmrSubstationMeter_B = distTrfmrSubstationMeterB;
	}

	public Integer getDistTrfmrSubstationMeter_B_id() {
		return distTrfmrSubstationMeter_B_id;
	}

	public void setDistTrfmrSubstationMeter_B_id(Integer distTrfmrSubstationMeterBId) {
		distTrfmrSubstationMeter_B_id = distTrfmrSubstationMeterBId;
	}

	public Meter getDistTrfmrSubstationMeter_C() {
		return distTrfmrSubstationMeter_C;
	}

	public void setDistTrfmrSubstationMeter_C(Meter distTrfmrSubstationMeterC) {
		distTrfmrSubstationMeter_C = distTrfmrSubstationMeterC;
	}

	public Integer getDistTrfmrSubstationMeter_C_id() {
		return distTrfmrSubstationMeter_C_id;
	}

	public void setDistTrfmrSubstationMeter_C_id(Integer distTrfmrSubstationMeterCId) {
		distTrfmrSubstationMeter_C_id = distTrfmrSubstationMeterCId;
	}

	@XmlTransient
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "distTrfmrSubstationMeter_A_id")
	@ReferencedBy(name="mdsId")
	@ColumnInfo(name="Distribution Transformer Substation A phase", descr="Distribution Transformer Substation 미터의 라인 A")
	private Meter distTrfmrSubstationMeter_A;
	
	@Column(name="distTrfmrSubstationMeter_A_id", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationMeter_A_id;

	@XmlTransient
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "distTrfmrSubstationMeter_B_id")
	@ReferencedBy(name="mdsId")
	@ColumnInfo(name="Distribution Transformer Substation B phase", descr="Distribution Transformer Substation 미터의 라인 B")	
	private Meter distTrfmrSubstationMeter_B;
	
	@Column(name="distTrfmrSubstationMeter_B_id", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationMeter_B_id;

	@XmlTransient
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "distTrfmrSubstationMeter_C_id")
	@ReferencedBy(name="mdsId")
	@ColumnInfo(name="Distribution Transformer Substation C phase", descr="Distribution Transformer Substation 미터의 라인 C")	
	private Meter distTrfmrSubstationMeter_C;
	
	@Column(name="distTrfmrSubstationMeter_C_id", nullable=true, updatable=false, insertable=false)
	private Integer distTrfmrSubstationMeter_C_id;
	
    public DistTrfmrSubstation getDistTrfmrSubstation() {
		return distTrfmrSubstation;
	}

	public void setDistTrfmrSubstation(DistTrfmrSubstation distTrfmrSubstation) {
		this.distTrfmrSubstation = distTrfmrSubstation;
	}
	
    @ColumnInfo(name="communication protocol version('IF4', 'TNG')")
    @Column(name="PROTOCOL_VERSION", length=20)
    private String protocolVersion;  //protocol Version
    
    @ColumnInfo(name="AMI Virtual Network Address Depth")
    @Column(name="AMI_NETWORK_DEPTH", length=2)
    private Integer amiNetworkDepth;  //AMI Virtual Network Depth
	
    @ColumnInfo(name="AMI Virtual Network Address")
    @Column(name="AMI_NETWORK_ADDRESS", length=128)
    private String amiNetworkAddress;  //AMI Virtual Network Address
	
	@ColumnInfo(name="수동 미터 여부", view=@Scope(create=true, read=true, update=true))
    @Column(name="is_manual_meter")
	private Integer isManualMeter;
	
	@ColumnInfo(name="미터 별칭", view=@Scope(create=true, read=true, update=true))
    @Column(name="friendly_name")
	private String friendlyName;

    @ColumnInfo(name="Delete Date")
    @Column(name="DELETE_DATE", length=14)
    private String deleteDate;
    
    @ColumnInfo(name="Project Phase, pilot, small-scale, full-scale")
    @Column(name="PHASE", length=50)
    private String phase;
    
    @ColumnInfo(name="Milestone Area MSA1, MSA2, MSA3")
    @Column(name="MSA", length=30)
    private String msa;
    

	public String getFriendlyName() {
		return friendlyName;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}
	
	public Integer getIsManualMeter() {
		return isManualMeter;
	}

	public void setIsManualMeter(Integer isManualMeter) {
		this.isManualMeter = isManualMeter;
	}
	
	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	private String conditions;

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMdsId() {
        return mdsId;
    }

    /**
     * 미터 아이디
     * <br>사람 혹은 장비에서 올라오는 값. 반드시 있어야 하는 값이며, 중복 되는 값은 사용할 수 없음
     * @param mdsId 미터아이디
     */
    public void setMdsId(String mdsId) {
        this.mdsId = mdsId;
    }
    
    public String getInstallProperty() {
		return installProperty;
	}

	public void setInstallProperty(String installProperty) {
		this.installProperty = installProperty;
	}

	public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }    

    public DeviceModel getModel() {
		return model;
	}

	public void setModel(DeviceModel model) {
		this.model = model;
	}	

	public Code getMeterType() {
		return meterType;
	}

	public void setMeterType(Code meterType) {
		this.meterType = meterType;
	}

    public Integer getModemPort() {
        return modemPort;
    }

    public void setModemPort(Integer modemPort) {
        this.modemPort = modemPort;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }
    
    public String getInstallId() {
        return installId;
    }

    public void setInstallId(String installId) {
        this.installId = installId;
    }
    
	public void setInstallDateHidden(String installDate) {
        this.installDateHidden = installDate;
    }
    //temp Data
    public String getInstallDateHidden() {
        return installDateHidden;
    }
    
    //업데이트시 반영할 포멧(포멧팅 처리안함)
    public String getInstallDateUpdate() {
        return installDateUpdate;
    }

    public void setInstallDateUpdate(String installDate) {
        this.installDateUpdate = installDate;
    }

    public String getIhdId() {
        return ihdId;
    }

    public void setIhdId(String ihdId) {
        this.ihdId = ihdId;
    }

    public Double getUsageThreshold() {
        return usageThreshold;
    }

    public void setUsageThreshold(Double usageThreshold) {
        this.usageThreshold = usageThreshold;
    }
    
    public MCU getMcu() {
    	if(modem!=null && modem.getMcu()!=null)
    		return modem.getMcu();
    	else return null;
    }

    public Customer getCustomer() {
    	if(contract!=null && contract.getCustomer()!=null)
    		return contract.getCustomer();
    	else return null;
    }

    public void setCustomer(Customer customer) {
        this.contract.setCustomer(customer);
    }

    public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public Integer getLpInterval() {
        return lpInterval==null ? 15:lpInterval;
    }

    public void setLpInterval(Integer lpInterval) {
        this.lpInterval = lpInterval;
    }

    public Long getTimeDiff() {
        return timeDiff;
    }

    public void setTimeDiff(Long timeDiff) {
        this.timeDiff = timeDiff;
    }

    public Modem getModem() {
        return modem;
    }

    public void setModem(Modem modem) {
        this.modem = modem;
    }

    public String getSwVersion() {
        return swVersion;
    }

    public void setSwVersion(String swVersion) {
        this.swVersion = swVersion;
    }

    public String getHwVersion() {
        return hwVersion;
    }

    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
    }

    public String getSwName() {
        return swName;
    }

    public void setSwName(String swName) {
        this.swName = swName;
    }

    public String getSwUpdateDate() {
        return swUpdateDate;
    }

    public void setSwUpdateDate(String swUpdateDate) {
        this.swUpdateDate = swUpdateDate;
    }

    public String getInstalledSiteImg() {
        return installedSiteImg;
    }

    public void setInstalledSiteImg(String installedSiteImg) {
        this.installedSiteImg = installedSiteImg;
    }

    public String getMeterCaution() {
        return meterCaution;
    }

    public void setMeterCaution(String meterCaution) {
        this.meterCaution = meterCaution;
    }

    public String getMeterError() {
        return meterError;
    }

    public void setMeterError(String meterError) {
        this.meterError = meterError;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastReadDate() {
        return lastReadDate;
    }

    public void setLastReadDate(String lastReadDate) {
        this.lastReadDate = lastReadDate;
    }    
    
    public String getLastTimesyncDate() {
		return lastTimesyncDate;
	}

	public void setLastTimesyncDate(String lastTimesyncDate) {
		this.lastTimesyncDate = lastTimesyncDate;
	}

	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}
	
    public Double getLastMeteringValue() {
		return lastMeteringValue;
	}

	public void setLastMeteringValue(Double lastMeteringValue) {
		this.lastMeteringValue = lastMeteringValue;
	}	
    
    public String getQualifiedDate() {
		return qualifiedDate;
	}

	public void setQualifiedDate(String qualifiedDate) {
		this.qualifiedDate = qualifiedDate;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

    public void setPulseConstant(Double pulseConstant) {
        this.pulseConstant = pulseConstant;
    }

    public Double getPulseConstant() {
        return pulseConstant;
    }

    public Boolean getPrepaymentMeter() {
		return prepaymentMeter;
	}

	public void setPrepaymentMeter(Boolean prepaymentMeter) {
		this.prepaymentMeter = prepaymentMeter;
	}	

	public String getManufacturedDate() {
		return manufacturedDate;
	}

	public void setManufacturedDate(String manufacturedDate) {
		this.manufacturedDate = manufacturedDate;
	}
	
	public Double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(Double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public Integer getAmiNetworkDepth() {
		return amiNetworkDepth;
	}

	public void setAmiNetworkDepth(Integer amiNetworkDepth) {
		this.amiNetworkDepth = amiNetworkDepth;
	}

	public String getAmiNetworkAddress() {
		return amiNetworkAddress;
	}

	public void setAmiNetworkAddress(String amiNetworkAddress) {
		this.amiNetworkAddress = amiNetworkAddress;
	}
	
    public String getGs1() {
        return gs1;
    }

    public void setGs1(String gs1) {
        this.gs1 = gs1;
    }

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result
                + ((getCustomer() == null) ? 0 : getCustomer().hashCode());
        result = prime * result
        		+ ((contract == null) ? 0 : contract.hashCode());
        result = prime * result
                + ((expirationDate == null) ? 0 : expirationDate.hashCode());
        result = prime * result
                + ((hwVersion == null) ? 0 : hwVersion.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((ihdId == null) ? 0 : ihdId.hashCode());
        result = prime * result
                + ((installDate == null) ? 0 : installDate.hashCode());
        result = prime * result
                + ((installId == null) ? 0 : installId.hashCode());
        result = prime
                * result
                + ((installedSiteImg == null) ? 0 : installedSiteImg.hashCode());
        result = prime
                * result
                + ((lastMeteringValue == null) ? 0 : lastMeteringValue
                        .hashCode());
        result = prime * result
                + ((lastReadDate == null) ? 0 : lastReadDate.hashCode());
        result = prime * result
                + ((location == null) ? 0 : location.hashCode());
        result = prime * result
                + ((lpInterval == null) ? 0 : lpInterval.hashCode());
        result = prime * result + ((mdsId == null) ? 0 : mdsId.hashCode());
        result = prime * result
                + ((meterCaution == null) ? 0 : meterCaution.hashCode());
        result = prime * result
                + ((meterError == null) ? 0 : meterError.hashCode());
        result = prime * result
                + ((meterType == null) ? 0 : meterType.hashCode());
        result = prime * result + ((model == null) ? 0 : model.hashCode());
        result = prime * result + ((modem == null) ? 0 : modem.hashCode());
        result = prime * result
                + ((modemPort == null) ? 0 : modemPort.hashCode());
        result = prime * result
                + ((pulseConstant == null) ? 0 : pulseConstant.hashCode());
        result = prime * result
                + ((qualifiedDate == null) ? 0 : qualifiedDate.hashCode());
        result = prime * result
                + ((supplier == null) ? 0 : supplier.hashCode());
        result = prime * result + ((swName == null) ? 0 : swName.hashCode());
        result = prime * result
                + ((swUpdateDate == null) ? 0 : swUpdateDate.hashCode());
        result = prime * result
                + ((swVersion == null) ? 0 : swVersion.hashCode());
        result = prime * result
                + ((timeDiff == null) ? 0 : timeDiff.hashCode());
        result = prime * result
                + ((usageThreshold == null) ? 0 : usageThreshold.hashCode());
//        result = prime * result + ((version == null) ? 0 : version.hashCode());
        result = prime * result
                + ((writeDate == null) ? 0 : writeDate.hashCode());
        result = prime * result
        + ((manufacturedDate == null) ? 0 : manufacturedDate.hashCode()); 
        result = prime * result
        + ((purchasePrice == null) ? 0 : purchasePrice.hashCode()); 
        result = prime * result
                +((shortId == null) ? 0: shortId.hashCode());
        result = prime * result 
        		+ ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
        result = prime * result 
        		+ ((amiNetworkDepth == null) ? 0 : amiNetworkDepth.hashCode());
        result = prime * result 
        		+ ((amiNetworkAddress == null) ? 0 : amiNetworkAddress.hashCode());
        result = prime * result 
        		+ ((gs1 == null) ? 0 : gs1.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        Meter other = (Meter) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        if (getCustomer() == null) {
            if (other.getCustomer() != null)
                return false;
        } else if (!getCustomer().equals(other.getCustomer()))
            return false;
        if (contract == null) {
            if (other.contract != null)
                return false;
        } else if (!contract.equals(other.contract))
            return false;
        if (expirationDate == null) {
            if (other.expirationDate != null)
                return false;
        } else if (!expirationDate.equals(other.expirationDate))
            return false;
        if (hwVersion == null) {
            if (other.hwVersion != null)
                return false;
        } else if (!hwVersion.equals(other.hwVersion))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (ihdId == null) {
            if (other.ihdId != null)
                return false;
        } else if (!ihdId.equals(other.ihdId))
            return false;
        if (installDate == null) {
            if (other.installDate != null)
                return false;
        } else if (!installDate.equals(other.installDate))
            return false;
        if (installId == null) {
            if (other.installId != null)
                return false;
        } else if (!installId.equals(other.installId))
            return false;
        if (installedSiteImg == null) {
            if (other.installedSiteImg != null)
                return false;
        } else if (!installedSiteImg.equals(other.installedSiteImg))
            return false;
        if (lastMeteringValue == null) {
            if (other.lastMeteringValue != null)
                return false;
        } else if (!lastMeteringValue.equals(other.lastMeteringValue))
            return false;
        if (lastReadDate == null) {
            if (other.lastReadDate != null)
                return false;
        } else if (!lastReadDate.equals(other.lastReadDate))
            return false;
        if (location == null) {
            if (other.location != null)
                return false;
        } else if (!location.equals(other.location))
            return false;
        if (lpInterval == null) {
            if (other.lpInterval != null)
                return false;
        } else if (!lpInterval.equals(other.lpInterval))
            return false;
        if (mdsId == null) {
            if (other.mdsId != null)
                return false;
        } else if (!mdsId.equals(other.mdsId))
            return false;
        if (meterCaution == null) {
            if (other.meterCaution != null)
                return false;
        } else if (!meterCaution.equals(other.meterCaution))
            return false;
        if (meterError == null) {
            if (other.meterError != null)
                return false;
        } else if (!meterError.equals(other.meterError))
            return false;
        if (meterType == null) {
            if (other.meterType != null)
                return false;
        } else if (!meterType.equals(other.meterType))
            return false;
        if (model == null) {
            if (other.model != null)
                return false;
        } else if (!model.equals(other.model))
            return false;
        if (modem == null) {
            if (other.modem != null)
                return false;
        } else if (!modem.equals(other.modem))
            return false;
        if (endDevice == null) {
            if (other.endDevice != null)
                return false;
        } else if (!endDevice.equals(other.endDevice))
        if (modemPort == null) {
            if (other.modemPort != null)
                return false;
        } else if (!modemPort.equals(other.modemPort))
            return false;
        if (pulseConstant == null) {
            if (other.pulseConstant != null)
                return false;
        } else if (!pulseConstant.equals(other.pulseConstant))
            return false;
        if (qualifiedDate == null) {
            if (other.qualifiedDate != null)
                return false;
        } else if (!qualifiedDate.equals(other.qualifiedDate))
            return false;
        if (supplier == null) {
            if (other.supplier != null)
                return false;
        } else if (!supplier.equals(other.supplier))
            return false;
        if (swName == null) {
            if (other.swName != null)
                return false;
        } else if (!swName.equals(other.swName))
            return false;
        if (swUpdateDate == null) {
            if (other.swUpdateDate != null)
                return false;
        } else if (!swUpdateDate.equals(other.swUpdateDate))
            return false;
        if (swVersion == null) {
            if (other.swVersion != null)
                return false;
        } else if (!swVersion.equals(other.swVersion))
            return false;
        if (timeDiff == null) {
            if (other.timeDiff != null)
                return false;
        } else if (!timeDiff.equals(other.timeDiff))
            return false;
        if (usageThreshold == null) {
            if (other.usageThreshold != null)
                return false;
        } else if (!usageThreshold.equals(other.usageThreshold))
            return false;
//        if (version == null) {
//            if (other.version != null)
//                return false;
//        } else if (!version.equals(other.version))
//            return false;
        if (manufacturedDate == null) {
            if (other.manufacturedDate != null)
                return false;
        } else if (!manufacturedDate.equals(other.manufacturedDate))
            return false;
        if (writeDate == null) {
            if (other.writeDate != null)
                return false;
        } else if (!writeDate.equals(other.writeDate))
            return false;
        if (purchasePrice == null) {
            if (other.purchasePrice != null)
                return false;
        } else if (!purchasePrice.equals(other.purchasePrice))
            return false;      
        if (shortId == null) {
            if (other.shortId != null)
                return false;
        } else if (!shortId.equals(other.shortId))
            return false;
		if (protocolVersion == null) {
            if (other.protocolVersion != null)
                return false;
        } else if (!protocolVersion.equals(other.protocolVersion))
            return false;
		if (amiNetworkDepth == null) {
            if (other.amiNetworkDepth!= null)
                return false;
        } else if (!amiNetworkDepth.equals(other.amiNetworkDepth))
            return false;
		if (amiNetworkAddress == null) {
            if (other.amiNetworkAddress != null)
                return false;
        } else if (!amiNetworkAddress.equals(other.amiNetworkAddress))
            return false;
		if (gs1 == null) {
            if (other.gs1 != null)
                return false;
        } else if (!gs1.equals(other.gs1))
            return false;
        
        return true;
    }

    @Override
    public String toString() {
        return "Meter [address=" + address + ", customer=" + getCustomer()
        		+ ", contract=" + contract
        		+ ", manufacturedDate=" + manufacturedDate
        		+ ", purchasePrice=" + purchasePrice        		
                + ", expirationDate=" + expirationDate + ", hwVersion="
                + hwVersion + ", id=" + id + ", ihdId=" + ihdId
                + ", installDate=" + installDate +", installId=" + installId + 
                ", installedSiteImg=" + installedSiteImg + ", lastMeteringValue=" + lastMeteringValue
                + ", lastReadDate=" + lastReadDate + ", location=" + location
                + ", lpInterval=" + lpInterval + ", mdsId=" + mdsId
                + ", meterCaution=" + meterCaution + ", meterError="
                + meterError + ", meterType=" + meterType + ", model=" + model
                + ", modem=" + modem + ", modemPort=" + modemPort
                + ", pulseConstant=" + pulseConstant + ", qualifiedDate="
                + qualifiedDate + ", supplier=" + supplier + ", swName="
                + swName + ", swUpdateDate=" + swUpdateDate + ", swVersion="
                + swVersion + ", timeDiff=" + timeDiff + ", usageThreshold="
                + usageThreshold +  ", writeDate=" + writeDate 
                + ", shortId=" + shortId + ", gs1=" + gs1 + "]";
    }

    public void setMeterStatus(Code meterStatus) {
        this.meterStatus = meterStatus;
    }

    public Code getMeterStatus() {
        return meterStatus;
    }

    public void setGpioX(Double gpioX) {
        this.gpioX = gpioX;
    }

    public Double getGpioX() {
        return gpioX;
    }

    public void setGpioY(Double gpioY) {
        this.gpioY = gpioY;
    }

    public Double getGpioY() {
        return gpioY;
    }

    public void setGpioZ(Double gpioZ) {
        this.gpioZ = gpioZ;
    }

    public Double getGpioZ() {
        return gpioZ;
    }

    public EndDevice getEndDevice() {
        return endDevice;
    }

    public void setEndDevice(EndDevice endDevice) {
        this.endDevice = endDevice;
    }

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
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

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getMeterTypeCodeId() {
        return meterTypeCodeId;
    }

    public void setMeterTypeCodeId(Integer meterTypeCodeId) {
        this.meterTypeCodeId = meterTypeCodeId;
    }

    public Integer getMeterStatusCodeId() {
        return meterStatusCodeId;
    }

    public void setMeterStatusCodeId(Integer meterStatusCodeId) {
        this.meterStatusCodeId = meterStatusCodeId;
    }

    public Integer getDistTrfmrSubstationId() {
        return distTrfmrSubstationId;
    }

    public void setDistTrfmrSubstationId(Integer distTrfmrSubstationId) {
        this.distTrfmrSubstationId = distTrfmrSubstationId;
    }

    public Integer getShortId() {
        return shortId;
    }

    public void setShortId(Integer shortId) {
        this.shortId = shortId;
    }

    /**
     * @return the deleteDate
     */
    public String getDeleteDate() {
        return deleteDate;
    }

    /**
     * @param deleteDate the deleteDate to set
     */
    public void setDeleteDate(String deleteDate) {
        this.deleteDate = deleteDate;
    }    

    public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getMsa() {
		return msa;
	}

	public void setMsa(String msa) {
		this.msa = msa;
	}

	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("address").value((this.address == null)? "":this.address)
			    	   .key("contract").value((this.contract == null)? "":this.contract.getContractNumber())
			    	   .key("endDevice").value((this.endDevice == null)? "":this.endDevice.getFriendlyName())
			    	   .key("expirationDate").value((this.expirationDate == null)? "":this.expirationDate)
			    	   .key("gpioX").value((this.gpioX == null)? "":this.gpioX)
			    	   .key("gpioY").value((this.gpioY == null)? "":this.gpioY)
			    	   .key("gpioZ").value((this.gpioZ == null)? "":this.gpioZ)
			    	   .key("hwVersion").value((this.hwVersion == null)? "":this.hwVersion)
			    	   .key("id").value((this.id == null)? "":this.id)
			    	   .key("ihdId").value((this.ihdId == null)? "":this.ihdId)
			    	   .key("installDate").value((this.installDate == null)? "":this.installDate)
			    	   .key("installId").value((this.installId == null)? "":this.installId)
			    	   .key("installedSiteImg").value((this.installedSiteImg == null)? "":this.installedSiteImg)
			    	   .key("installProperty").value((this.installProperty == null)? "":this.installProperty)
			    	   .key("lastMeteringValue").value((this.lastMeteringValue == null)? "":this.lastMeteringValue)
			    	   .key("lastReadDate").value((this.lastReadDate == null)? "":this.lastReadDate)
			    	   .key("location").value((this.location == null)? "":this.location.getName())
			    	   .key("lpInterval").value((this.lpInterval == null)? "":this.lpInterval)
			    	   .key("mdsId").value((this.mdsId == null)? "":this.mdsId)
			    	   .key("meterCaution").value((this.meterCaution == null)? "":this.meterCaution)
			    	   .key("meterError").value((this.meterError == null)? "":this.meterError)
			    	   .key("meterStatus").value((this.meterStatus == null)? "":this.meterStatus)
			    	   .key("meterType").value((this.meterType == null)? "":this.meterType)
			    	   .key("model").value((this.model == null)? "":this.model.getName())
			    	   .key("friendlyName").value((this.friendlyName == null)? "":this.getFriendlyName())
			    	   .key("gs1").value((this.gs1 == null)? "":this.gs1)

			    	   
// Location이 stackOverFlow가 생성될꺼염?
//.key("modem").value((this.modem == null)? "":this.modem)
 			    	   
			    	   .key("modemPort").value((this.modemPort == null)? "":this.modemPort)
			    	   .key("pulseConstant").value((this.pulseConstant == null)? "":this.pulseConstant)
			    	   .key("qualifiedDate").value((this.qualifiedDate == null)? "":this.qualifiedDate)
			    	   .key("supplier").value((this.supplier == null)? "":this.supplier.getName())
			    	   .key("swName").value((this.swName == null)? "":this.swName)
			    	   .key("swUpdateDate").value((this.swUpdateDate == null)? "":this.swUpdateDate)
			    	   .key("swVersion").value((this.swVersion == null)? "":this.swVersion)
			    	   .key("timeDiff").value((this.timeDiff == null)? "":this.timeDiff)
			    	   .key("usageThreshold").value((this.usageThreshold == null)? "":this.usageThreshold)
//			    	   .key("version").value((this.version == null)? "":this.version)
			    	   .key("writeDate").value((this.writeDate == null)? "":this.writeDate)
			    	   .key("shortId").value((this.shortId == null)? "":this.shortId)
			    	   .key("protocolVersion").value(protocolVersion)
			    	   .key("amiNetworkDepth").value(amiNetworkDepth)
			    	   .key("amiNetworkAddress").value(amiNetworkAddress)
			    	   .key("deleteDate").value((this.deleteDate == null) ? "" : this.deleteDate)
			    	   .key("phase").value((this.phase == null) ? "" : this.phase)
			    	   .key("msa").value((this.msa == null) ? "" : this.msa)
    				   .endObject();

    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
	}

	@Override
	public String getInstanceName() {
	    return this.getMdsId();
	}
}