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
import javax.persistence.Transient;
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
 * 각각의 모델별로 실행가능한 오퍼레이션들을 관리하기 위한 테이블
 * 제약 사항에 상관없이 해당 모델이 제공하는 모든 오퍼레이션들을 등록해둔다.
 * </pre>
 * 
 * @author YeonKyoung Park(goodjob)
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="OPERATION_LIST")
public class OperationList extends BaseObject {

	private static final long serialVersionUID = 7875345457374864812L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OPERATION_LIST_SEQ")
    @SequenceGenerator(name="OPERATION_LIST_SEQ", sequenceName="OPERATION_LIST_SEQ", allocationSize=1) 
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
	@ColumnInfo(name="명령코드", descr="오퍼레이션의 코드(하나의 오퍼레이션 코드는 여러 개의 model을 지닐 수 있음), 코드 8 Command 참조")
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

    @ColumnInfo(name="레벨", descr="해당 오퍼레이션에 대한 로깅 레벨 (각 오퍼레이션 별로 로깅을 남길지 남지기 않을지 설정 가능)")
    @Column(name="LEVELS", nullable=false)
    private Integer level;

    @ColumnInfo(name="", descr="명령에 대한 설명")
    @Column(name="DESCRIPTION")
    private String desc;
    
    @ColumnInfo(name="인자유형", descr="오퍼레이션의 인자 유형이 다양하다. 가령 ondemand의 경우 lp를 가져오는 방법이 일기준 또는 개수기준일 수 있다. default(0) 일기준")
    @Column(name="PARAM_TYPE", nullable=true)
    private Integer paramType;

    @Transient
    private String equipment;    

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

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

    public String getEquipment() {
		return equipment;
	}

	public void setEquipment(String equipment) {
		this.equipment = equipment;
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

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }
    
    public Integer getParamType() {
        return this.paramType;
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
