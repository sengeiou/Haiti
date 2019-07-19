package com.aimir.model.system;

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
import javax.persistence.UniqueConstraint;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.DLMSDataType;
import com.aimir.model.BaseObject;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2015</p>
 * <p>OBIS 코드 정보 클래스</p>
 * Model별로 쓰이는 Obis코드 관리
 * 
 * @author Jiae
 * 
 * Date          Version     Author   Description
 * 2016. 2. 23.   v1.1       lucky    Change "access" field name for reserved word.
 * 
 */

@Entity
@Table(name = "OBISCODE", uniqueConstraints=@UniqueConstraint(columnNames={"OBIS_CODE","class_Id","attribute_No","devicemodel_id"}))
public class OBISCode extends BaseObject implements JSONString{ 	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3713988559688444944L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="OBISCODE_SEQ")
	@SequenceGenerator(name="OBISCODE_SEQ", sequenceName="OBISCODE_SEQ", allocationSize=1)	
    @ColumnInfo(name="PK", descr="PK")
	private Long id;

	@Column(name="OBIS_CODE", nullable=false)
	private String obisCode;
	
	@Column(name="class_Name")
	private String className;
	
	@Column(name="class_Id", nullable=false)
	private String classId;
	
	@Column(name="attribute_name")
	private String attributeName;
	
	@Column(name="attribute_No", nullable=false)
	private String attributeNo;
	
	@Enumerated(EnumType.STRING)
	private DLMSDataType dataType;
	
	@Column(name="access_right")
	@ColumnInfo(descr="obis code attribute right 종류 ex) R/RW (읽기/읽기,쓰기)")
	private String accessRight ;	//Read/Write

	@Column
	private String descr;	//코드설명

    @ColumnInfo(name="미터 모델", view=@Scope(create=true, read=true, update=true), descr="미터 제조사 모델의 ID 혹은  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="devicemodel_id")
    @ReferencedBy(name="name")
    private DeviceModel model;
    
    @Column(name="devicemodel_id", nullable=true, updatable=false, insertable=false)
    private Integer modelId;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}	
	
	public DeviceModel getModel() {
		return model;
	}

	public void setModel(DeviceModel model) {
		this.model = model;
	}

	public Integer getModelId() {
		return modelId;
	}

	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

	public String getObisCode() {
		return obisCode;
	}
	public void setObisCode(String obisCode) {
		this.obisCode = obisCode;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeNo() {
		return attributeNo;
	}

	public void setAttributeNo(String attributeNo) {
		this.attributeNo = attributeNo;
	}

	public DLMSDataType getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		if(dataType == null) {
			this.dataType = null;
		} else {
			this.dataType = DLMSDataType.valueOf(dataType);
		}
	}

	public String getAccessRight() {
		return accessRight;
	}

	public void setAccessRight(String accessRight) {
		this.accessRight = accessRight;
	}

	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}	
    @Override
	public String toString()
	{
	    return "Code "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id 
	        + "',obisCode:'" + this.obisCode 
	        + "',className:'" + this.className
	        + "',classId:'" + this.classId
	        + "',attributeName:'" + this.attributeName 
	        + "',attributeNo:'" + this.attributeNo
	        + "',dataType:'" + this.dataType
	        + "',accessRight:'" + this.accessRight 
	        + "',descr:'" + this.descr
	        + "',modelId:'" + this.modelId 
	        + "'}";
	    
	    return retValue;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        OBISCode other = (OBISCode) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (obisCode == null) {
            if (other.obisCode != null)
                return false;
        } else if (!obisCode.equals(other.obisCode))
            return false;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (classId == null) {
            if (other.classId != null)
                return false;
        } else if (!classId.equals(other.classId))
            return false;
        if (attributeName == null) {
            if (other.attributeName != null)
                return false;
        } else if (!attributeName.equals(other.attributeName))
            return false;
        if (attributeNo == null) {
            if (other.attributeNo != null)
                return false;
        } else if (!attributeNo.equals(other.attributeNo))
            return false;
        if (dataType == null) {
            if (other.dataType != null)
                return false;
        } else if (!dataType.equals(other.dataType))
            return false;
        if (accessRight == null) {
            if (other.accessRight != null)
                return false;
        } else if (!accessRight.equals(other.accessRight))
            return false;
        if (descr == null) {
            if (other.descr != null)
                return false;
        } else if (!descr.equals(other.descr))
            return false;
        if (modelId == null) {
            if (other.modelId != null)
                return false;
        } else if (!modelId.equals(other.modelId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
