package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> 장비에 관련된 오퍼레이션을 수행한 내역에 대한 로그 클래스 </p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name="OPERATION_LOG")
//@Indexes({
//    @Index(name="IDX_OPERATION_LOG_01", columnNames={"YYYYMMDDHHMMSS"})
//})
public class OperationLog extends BaseObject {

	private static final long serialVersionUID = -2057362708625647099L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_OPERATION_LOG")
    @SequenceGenerator(name="SEQ_OPERATION_LOG", sequenceName="SEQ_OPERATION_LOG", allocationSize=1)
	private Long id;

    @ColumnInfo(name="명령 실행날짜", descr="Operation을 시도한 날짜(YYYYMMDD)")
    @Column(name="YYYYMMDD", nullable=false, length=8)
	private String yyyymmdd;

    @ColumnInfo(name="명령 실행시간", descr="Operation을 시도한 시간(HHMMSS)")
    @Column(name="HHMMSS", length=6)
    private String hhmmss;
    
    @ColumnInfo(name="명령 실행날짜시간", descr="Operation을 시도한 날짜(YYYYMMDDHHMMSS)")
    @Column(name="YYYYMMDDHHMMSS", nullable=false, length=14)
	private String yyyymmddhhmmss;

    @ColumnInfo(name="명령 대상 코드의 아이디", descr="코드에 정의된 명령 대상 코드의 아이디(Code의 Target Class : 7.13참조)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TARGET_TYPE_CODE")
    @ReferencedBy(name="name")
	private Code targetTypeCode;
    
    @Column(name="TARGET_TYPE_CODE", nullable=true, updatable=false, insertable=false)
    private Integer targetTypeCodeId;

    @ColumnInfo(name="명령 대상의 명칭", descr="코드 아이디를 찾지 못할 경우를 대비하여 명령 대상의 명칭을 기록")
    @Column(name="TARGET_NAME", nullable=false)
	private String targetName;

    @ColumnInfo(name="수행자 아이디", descr="명령을 실행한 수행자의 아이디")
    @Column(name="USER_ID")
    private String userId;

    @ColumnInfo(name="수행자 타입", descr="수행자의 타입을 정의(0:System, 1:Operator)")
    @Column(name="OPERATOR_TYPE")
    private Integer operatorType;

    @ColumnInfo(name="수행 명령 코드", descr="실행한 명령 코드(Code의 Command : 8참조)")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="OPERATION_COMMAND_CODE")
    @ReferencedBy(name="code")
	private Code operationCommandCode;
    
    @Column(name="OPERATION_COMMAND_CODE", nullable=true, updatable=false, insertable=false)
    private Integer operationCommandCodeId;

    @ColumnInfo(name="설명", descr="실행한 Operation에 대한 상세")
    @Column(name="DESCRIPTION")
    private String description;

    @ColumnInfo(name="상태", descr="Operation을 내린 후 Operation의 성공 실패 여부. (응답이 없는 Operation인 경우는 명령이 잘 내려갔으면 성공, 응답이 있는 Operation인 경우는 응답이 잘 와야 성공.)(255 : Waiting , 0:Success, 1:Failed, 2:Invalid Argument, 3:Communication Failure)")
    @Column(name="STATUS", columnDefinition="INTEGER default 255")
	private Integer status;

    @ColumnInfo(name="에러 사유", descr="Operation 응답이 Error인 경우 그 이유를 기술")
    @Column(name="ERROR_REASON")
	private String errorReason;

    @ColumnInfo(name="결과저장 링크", descr="결과가 HTML형태로 저장되어 특정 디렉토리에 저장")
    @Column(name="RESULT_SRC")
    private String resultSrc;

    @ColumnInfo(name="계약번호", descr="고객의 오퍼레이션일 경우, 계약번호를 등록한다.")
    @Column(name="CONTRACTNUMBER")
    private String contractNumber;    
	
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;

    public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
		return yyyymmddhhmmss;
	}

	public void setYyyymmddhhmmss(String yyyymmddhhmmss) {
		this.yyyymmddhhmmss = yyyymmddhhmmss;
	}

	@XmlTransient
	public Code getTargetTypeCode() {
        return targetTypeCode;
    }

    public void setTargetTypeCode(Code targetTypeCode) {
        this.targetTypeCode = targetTypeCode;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setOperatorType(Integer operatorType) {
        this.operatorType = operatorType;
    }

    public Integer getOperatorType() {
        return operatorType;
    }

    @XmlTransient
    public Code getOperationCommandCode() {
        return operationCommandCode;
    }

    public void setOperationCommandCode(Code operationCommandCode) {
        this.operationCommandCode = operationCommandCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(String errorReason) {
        this.errorReason = errorReason;
    }

    public String getResultSrc() {
		return resultSrc;
	}

	public void setResultSrc(String resultSrc) {
		this.resultSrc = resultSrc;
	}
	
	@XmlTransient
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public Integer getTargetTypeCodeId() {
        return targetTypeCodeId;
    }

    public void setTargetTypeCodeId(Integer targetTypeCodeId) {
        this.targetTypeCodeId = targetTypeCodeId;
    }

    public Integer getOperationCommandCodeId() {
        return operationCommandCodeId;
    }

    public void setOperationCommandCodeId(Integer operationCommandCodeId) {
        this.operationCommandCodeId = operationCommandCodeId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result
                + ((description == null) ? 0 : description.hashCode());
        result = prime * result
                + ((errorReason == null) ? 0 : errorReason.hashCode());
        result = prime * result + ((hhmmss == null) ? 0 : hhmmss.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime
                * result
                + ((operationCommandCode == null) ? 0 : operationCommandCode
                        .hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result
                + ((targetName == null) ? 0 : targetName.hashCode());
        result = prime * result
                + ((targetTypeCode == null) ? 0 : targetTypeCode.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result
                + ((yyyymmdd == null) ? 0 : yyyymmdd.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        OperationLog other = (OperationLog) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (errorReason == null) {
            if (other.errorReason != null)
                return false;
        } else if (!errorReason.equals(other.errorReason))
            return false;
        if (hhmmss == null) {
            if (other.hhmmss != null)
                return false;
        } else if (!hhmmss.equals(other.hhmmss))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (operationCommandCode == null) {
            if (other.operationCommandCode != null)
                return false;
        } else if (!operationCommandCode.equals(other.operationCommandCode))
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        if (targetName == null) {
            if (other.targetName != null)
                return false;
        } else if (!targetName.equals(other.targetName))
            return false;
        if (targetTypeCode == null) {
            if (other.targetTypeCode != null)
                return false;
        } else if (!targetTypeCode.equals(other.targetTypeCode))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        if (yyyymmdd == null) {
            if (other.yyyymmdd != null)
                return false;
        } else if (!yyyymmdd.equals(other.yyyymmdd))
            return false;
        if (yyyymmddhhmmss == null) {
            if (other.yyyymmddhhmmss != null)
                return false;
        } else if (!yyyymmddhhmmss.equals(other.yyyymmddhhmmss))
            return false;
        return true;
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("OperationLog [description=");
    	sb.append(description);
    	sb.append(", errorReason=");
    	sb.append(errorReason);
    	sb.append(", hhmmss=");
    	sb.append(hhmmss);
    	sb.append(", id=");
    	sb.append(id);
    	sb.append(", operationCommandCode=");
    	sb.append(operationCommandCode);
    	sb.append(", status=");
    	sb.append(status);
    	sb.append(", targetName=");
    	sb.append(targetName);
    	sb.append(", targetTypeCode=");
    	sb.append(targetTypeCode);
    	sb.append(", userId=");
    	sb.append(userId);
    	sb.append(", yyyymmddhhmmss=");
    	sb.append(yyyymmddhhmmss);
    	sb.append(", yyyymmdd=");
    	sb.append(yyyymmdd);
    	sb.append("]");
        return sb.toString();
    }
}