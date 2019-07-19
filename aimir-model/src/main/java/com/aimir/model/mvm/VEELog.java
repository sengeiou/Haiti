package com.aimir.model.mvm;

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
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.EditItem;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Contract;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <pre>
 * Metering Data Validataion, Editing, Estimation Log
 * Validation - 검침데이터의 유효성을 체크
 * Editing - 검침데이터의 수정 
 * Estimation - 누락된 검침데이터에 대한 과거 기준이나 이전 사용량을 기준으로 한 예측
 * </pre>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name="VEE_LOG")
public class VEELog extends BaseObject {

	private static final long serialVersionUID = 5053549357502768215L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="VEE_LOG_SEQ")
    @SequenceGenerator(name="VEE_LOG_SEQ", sequenceName="VEE_LOG_SEQ", allocationSize=1) 
	private Long id;
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="supplier_id", nullable=false)
	@ReferencedBy(name="name")
	private Supplier supplier;
	
	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
    
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contract_id")
	@ReferencedBy(name="contractNumber")
	private Contract contract;
	
	@Column(name="contract_id", nullable=true, updatable=false, insertable=false)
	private Integer contractId;
    
    @Column(name="mdev_type",length=20)
    @Enumerated(EnumType.STRING)
	@ColumnInfo(name="장비 아이디", descr="")
	private DeviceType mdevType;// MCU(0), Modem(1), Meter(2);
	
	@Column(name="mdev_id",length=20)
	@ColumnInfo(name="장비 아이디", descr="")
	private String mdevId;
	
	@Column(columnDefinition="INTEGER default 0")
	@ColumnInfo(name="DST", descr="Summer Time ex ) +1 -1 +0")
	private Integer dst;		
	
    @ColumnInfo(name="채널")
    private Integer channel;

    @ColumnInfo(name="지역아이디", descr="지역 테이블의 ID나  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
    @Column(name="YYYYMMDD",length=8)
    private String yyyymmdd;
    
    @Column(name="HH",length=2)
    private String hh;
    
    @Column(name="WRITE_DATE",length=14)
    private String writeDate;

    @Column(name="TABLE_NAME")
    private String tableName;
    
    @Column(name="ATTR_NAME")
    private String attrName;
    
    @Column(name="BEFORE_VALUE")
    private String beforeValue;
    
    @Column(name="AFTER_VALUE")
    private String afterValue;
        
    @ColumnInfo(name="상태", descr="성공 실패 여부. (응답이 없는 Operation인 경우는 명령이 잘 내려갔으면 성공, 응답이 있는 Operation인 경우는 응답이 잘 와야 성공.)(0:Success, 1:Failed, 2:Invalid Argument, 3:Communication Failure)")
    @Column(name="RESULT", nullable=false)
	private ResultStatus result;
    
    @ColumnInfo(name="수행자 타입", descr="수행자의 타입을 정의(0:System, 1:Operator)")
    @Column(name="OPERATOR_TYPE")
    private OperatorType operatorType;    
    
    @Column(name="OPERATOR")
    private String operator;

	@ColumnInfo(name="", descr="")
    @Column(name="DESCR")
    private String descr;
	
	@Column(name = "edit_item")
	@ColumnInfo(descr="타입 수정한 항목에 대한 방법 Verified(0),AutomaticEstimated(1), UserDefinedEstimated(2),RuleBasedEstimated(3), IndividualEdited(4)")
	private EditItem editItem;	

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }    

	public DeviceType getMDevType() {
		return mdevType;
	}

	public void setMDevType(String mdevType){
		this.mdevType = DeviceType.valueOf(mdevType);
	}
	
	public String getMDevId() {
		return mdevId;
	}
	public void setMDevId(String mdevId) {
		this.mdevId = mdevId;
	}
	
	public Integer getDst() {
		return dst;
	}
	public void setDst(Integer dst) {
		this.dst = dst;
	}

    public Integer getChannel() {
        return channel;
    }
    public void setChannel(Integer channel) {
        this.channel = channel;
    }
    
	public String getWriteDate() {
		return writeDate;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public ResultStatus getResult() {
		return result;
	}

	public void setResult(String result) {			
		this.result = ResultStatus.valueOf(result);
	}

	public OperatorType getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = OperatorType.valueOf(operatorType);
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    @XmlTransient
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @XmlTransient
    public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}

	public String getYyyymmdd() {
		return yyyymmdd;
	}

	public void setYyyymmdd(String yyyymmdd) {
		this.yyyymmdd = yyyymmdd;
	}

	public String getHh() {
		return hh;
	}

	public void setHh(String hh) {
		this.hh = hh;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getBeforeValue() {
		return beforeValue;
	}

	public void setBeforeValue(String beforeValue) {
		this.beforeValue = beforeValue;
	}

	public String getAfterValue() {
		return afterValue;
	}

	public void setAfterValue(String afterValue) {
		this.afterValue = afterValue;
	}

	public void setResult(ResultStatus result) {
		this.result = result;
	}

	public void setOperatorType(OperatorType operatorType) {
		this.operatorType = operatorType;
	}

	public EditItem getEditItem() {
		return editItem;
	}

	public void setEditItem(String editItem) {
		this.editItem = EditItem.valueOf(editItem);
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getContractId() {
        return contractId;
    }

    public void setContractId(Integer contractId) {
        this.contractId = contractId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }
}
