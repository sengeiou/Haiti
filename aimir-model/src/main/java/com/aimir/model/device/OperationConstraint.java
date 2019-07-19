package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * 각각의 operation에 대한 제약 사항들을 기술해둔다.
 * 
 * 예) ondemand 오퍼레이션에 대해 mcu revision이 2800 이상이고 sensor 타입이 zru 여야한다면
 * type        field            condition        value
 * ----------------------------------------------------
 * mcu         revision         >=               2800
 * sensor      otherSensorType  =                1
 * 
 * 위와 같이 제약 사항을 기술한다.
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="OPERATION_CONSTRAINT")
public class OperationConstraint extends BaseObject {

    private static final long serialVersionUID = 859969180452148272L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPERATION_CONSTRAINT_SEQ")
    @SequenceGenerator(name="OPERATION_CONSTRAINT_SEQ", sequenceName="OPERATION_CONSTRAINT_SEQ", allocationSize=1) 
    private Integer id;

//    @Version
//    Integer version;    

    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnInfo(name="오퍼레이션타입", descr="meter, mcu, modem등 오퍼레이션을 소유할 수 있는 대상의 코드(EnergyMeter,GasMeter,WaterMeter,Sensor,MCU...등등)")
    @JoinColumn(name="DEVICE_TYPE", nullable=false)
    @ReferencedBy(name="code")
    private Code deviceTypeCode;
	
	@Column(name="DEVICE_TYPE", nullable=true, updatable=false, insertable=false)
	private Integer deviceTypeCodeId;
    
	@ManyToOne(fetch = FetchType.LAZY)
	@ColumnInfo(name="명령코드", descr="오퍼레이션의 코드(하나의 오퍼레이션 코드는 여러 개의 model을 지닐 수 있음), 예: Ondemand - 1.1, UnitScaning - 1.2")
    @JoinColumn(name="OPERATION_CODE", nullable=false)
    @ReferencedBy(name="code")
    private Code operationCode;
    
    @Column(name="OPERATION_CODE", nullable=true, updatable=false, insertable=false)
    private Integer operationCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @ColumnInfo(name="모델", descr="해당 오퍼레이션을 가지는 대상의 모델")
    @JoinColumn(name="MODEL_ID")
    @ReferencedBy(name="name")
    private DeviceModel model;
	
	@Column(name="MODEL_ID", nullable=true, updatable=false, insertable=false)
	private Integer modelId;

    @ColumnInfo(name="필드1", descr="제약 사항. 예) ondemand operation 이 mcu revision 2800이상일 때만 실행된다면  field에는 revision을 넣도록 한다")
    @Column(name="FIELD1")
    private String field1;

	@ColumnInfo(name="조건1", descr="제약 사항을 체크해볼 대상. 예) ondemand operation이 mcu revision 2800이상일 때만 실행된다면 condition에 >=를 넣도록 한다.")
    @Column(name="CONDITION1")
    private String condition1;

    @ColumnInfo(name="값1", descr="Operation을 시도한 Operator")
    @Column(name="VALUE1")
    private String value1;
    
    @ColumnInfo(name="필드2", descr="제약 사항. 예) ondemand operation 이 mcu revision 2800이상일 때만 실행된다면  field에는 revision을 넣도록 한다")
    @Column(name="FIELD2")
    private String field2;

	@ColumnInfo(name="조건2", descr="제약 사항을 체크해볼 대상. 예) ondemand operation이 mcu revision 2800이상일 때만 실행된다면 condition에 >=를 넣도록 한다.")
    @Column(name="CONDITION2")
    private String condition2;

	@ColumnInfo(name="값2", descr="Operation을 시도한 Operator")
    @Column(name="VALUE2")
    private String value2;

    @ColumnInfo(name="", descr="제약에 대한 설명")
    @Column(name="DESCRIPTION")
    private String desc;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Code getDeviceTypeCode() {
		return deviceTypeCode;
	}

	public void setDeviceTypeCode(Code deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}	

	@XmlTransient
    public Code getOperationCode() {
		return operationCode;
	}

	public void setOperationCode(Code operationCode) {
		this.operationCode = operationCode;
	}

	@XmlTransient
	public DeviceModel getModel() {
		return model;
	}

	public void setModel(DeviceModel model) {
		this.model = model;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getCondition1() {
		return condition1;
	}

	public void setCondition1(String condition1) {
		this.condition1 = condition1;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}


    public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getCondition2() {
		return condition2;
	}

	public void setCondition2(String condition2) {
		this.condition2 = condition2;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
    
    public Integer getDeviceTypeCodeId() {
        return deviceTypeCodeId;
    }

    public void setDeviceTypeCodeId(Integer deviceTypeCodeId) {
        this.deviceTypeCodeId = deviceTypeCodeId;
    }

    public Integer getOperationCodeId() {
        return operationCodeId;
    }

    public void setOperationCodeId(Integer operationCodeId) {
        this.operationCodeId = operationCodeId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
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
