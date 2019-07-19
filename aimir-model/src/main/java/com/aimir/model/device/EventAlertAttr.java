package com.aimir.model.device;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

import net.sf.json.JSONString;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>EventAlertAttr Event Alert Log 발생에 대한 상세 이벤트 발생 내역 속성을 정의</p>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
public class EventAlertAttr extends BaseObject implements JSONString {

	private static final long serialVersionUID = 7154808164976431696L;

	private Long id; 

	@ColumnInfo(name="이벤트코드")
	private String oid;
	
	@ColumnInfo(name="이름")
	private String attrName;	
	
	@ColumnInfo(name="이름")
	private String attrType;	
	
	@ColumnInfo(name="이름")
	private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrType() {
		return attrType;
	}

	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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
        return toJSONString();
    }

	public String toJSONString() {
	    StringBuffer buf = new StringBuffer();
	    buf.append("{\"id\":\"" + getId() + "\",");
	    buf.append("\"oid\":\"" + getOid() + "\",");
	    buf.append("\"attrName\":\"" + getAttrName() + "\",");
	    buf.append("\"attrType\":\"" + getAttrType() + "\",");
	    buf.append("\"value\":\"" + getValue() + "\"}");
		return buf.toString();
	}
}