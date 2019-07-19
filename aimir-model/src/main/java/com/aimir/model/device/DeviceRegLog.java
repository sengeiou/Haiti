package com.aimir.model.device;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.model.BaseObject;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>장비 등록 이력 로그</p>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Table(name = "DEVICEREG_LOG")
public class DeviceRegLog extends BaseObject {

	private static final long serialVersionUID = 8026288713320305491L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DEVICEREG_LOG_SEQ")
    @SequenceGenerator(name="DEVICEREG_LOG_SEQ", sequenceName="DEVICEREG_LOG_SEQ", allocationSize=1)
	private Long id;
	
    @Column(name="CREATE_DATE", length=14)
    @ColumnInfo(name="등록 날짜")
	private String createDate;	
    
    @Column(length=20)
    @ColumnInfo(name="장비유형")
    @Enumerated(EnumType.STRING)
    private TargetClass deviceType;  
    
    @Column(name="device_name", length=20)
    @ColumnInfo(name="장비아이디 장비의 비지니스키 MCU : sysId, meter : mdsId, modem : DeviceSerial")
    private String deviceName;    

    @ColumnInfo(name="장비 모델")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="devicemodel_id")
    @ReferencedBy(name="name")
    private DeviceModel deviceModel;
    
    @Column(name="devicemodel_id", nullable=true, updatable=false, insertable=false)
    private Integer deviceModelId;
    
    @Column(length=255)
    @ColumnInfo(name="실행 결과")
    @Enumerated(EnumType.STRING)
    private ResultStatus result;
    
    @Column(name="REG_TYPE")
    @Enumerated(EnumType.STRING)
    @ColumnInfo(name="등록 타입   Auto,시스템상에서 설치시 자동등록	Manual,//단건 등록Bulk,//Batch Integration//외부시스템에 의한 연계")
    private RegType regType;
    
    @Column(length=255)
    @ColumnInfo(name="설명")
    private String message;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="operator_id")
    @ColumnInfo(name="등록한 사용자 시스템이 등록하는경우 사용자 인터페이스에서 등록하는 하는 경우를 제외하고 널로 들어가게 됨")
    private Operator operator;	
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @ColumnInfo(descr = "Shipment file name")
    @Column(name = "SHIPMENT_FILE_NAME")
    private String shipmentFileName;
    
    @Column(name = "TOTAL_COUNT")
    private String totalCount;
    
    @Column(name = "SUCCESS_COUNT")
    private String successCount;
    
    @Column(name = "FAIL_COUNT")
    private String failCount;

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public TargetClass getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(TargetClass deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	@XmlTransient
	public DeviceModel getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceModel deviceModel) {
		this.deviceModel = deviceModel;
	}

	public ResultStatus getResult() {
		return result;
	}

	public void setResult(ResultStatus result) {
		this.result = result;
	}

	public RegType getRegType() {
		return regType;
	}

	public void setRegType(RegType regType) {
		this.regType = regType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}	

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public Integer getDeviceModelId() {
        return deviceModelId;
    }

    public void setDeviceModelId(Integer deviceModelId) {
        this.deviceModelId = deviceModelId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getShipmentFileName() {
		return shipmentFileName;
	}

	public void setShipmentFileName(String shipmentFileName) {
		this.shipmentFileName = shipmentFileName;
	}
    
	public String getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(String totalCount) {
		this.totalCount = totalCount;
	}

	public String getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(String successCount) {
		this.successCount = successCount;
	}
	
	public String getFailCount() {
		return failCount;
	}

	public void setFailCount(String failCount) {
		this.failCount = failCount;
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
        return "";
    }

}
