package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <p>시스템 코드 정보 클래스</p>
 * AIMIR System상에서 공통으로 쓰는 코드 정보를 정의한 클래스<br>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */

@Entity
@Table(name = "CODE")
public class Code extends BaseObject implements JSONString{ 	
	//공급상태	
	public final static String STATUS = "2.1";
	//지불타입
	public final static String PAYMENT = "2.2";
	//지불상태
	public final static String CREDITSTATUS = "2.2.1.2";
	//에너지
	public final static String ENERGY = "3";	
	//명령실행 
	public final static String COMMAND = "8";
	public final static String METER_COMMAND = "8.1";
	public final static String MODEM_COMMAND = "8.2";
	public final static String MCU_COMMAND = "8.3";
	
	//장비
	public final static String EQUIPMENT = "1";
	// MCU HW VERSION
	public final static String MCU_HW_VERSION = "1.1.2";
	// MCU SWVERSION
	public final static String MCU_SW_VERSION = "1.1.3";
	// MCU TYPE
	public final static String MCU_TYPE = "1.1.1";
	// MCU Status
	public final static String MCU_STATUS = "1.1.4";	
	// Target
	public final static String TAGET_CLASS = "7.13";
	// 랭킹 개수
	public final static String RANKINGRANGE = "6.19";
	// 랭킹 개수
	public final static String OPERATION = "8";
	
	public final static String MODEM_TYPE 			= "1.2.1";
	public final static String MODEM_SW_REVISION 	= "1.2.3";
	public final static String MODEM_HW_VERSION 	= "1.2.4";
	public final static String MODEM_SW_VERSION 	= "1.2.6";
	public final static String MODEM_SLEEP_MODE 	= "1.2.7";
	
	public final static String METER_TYPE       = "1.3.1";
	public final static String METER_STATUS     = "1.3.3";
	public final static String METER_HW_VERSION = "1.3.6";
	public final static String METER_SW_VERSION = "1.3.7";
	
	/**
	 * @deprecated SIC로 변경함. 2012.04.13
	 */
	@Deprecated
	public final static String CUSTOMER_TYPE = "11";
	
	public final static String SIC = "14";

	public final static String ENDDEVICE_CATEGORY = "13";
	
	// EndDevice Install Status
	public final static String IN_PROCESS = "1.9.3.1";

	// EndDevice Install Status
	public final static String COMPLETED = "1.9.3.3";

	// Payment Type : 선불 (참고, 위에 설정된 코드2.2가 PREPAY로 정의되있어서 PREPAYMENT로 정의하였습니다.
	// 2.2는 PREPAY가 아니고 Payment임 수정이 필요하다.
	public final static String POSTPAY = "2.2.0";
	public final static String PREPAYMENT = "2.2.1";
	public final static String EMERGENCY_CREDIT = "2.2.2";
	// 프로토콜
	public final static String PROTOCOL = "4.6";
	
	// Energy Balance Monitoring Device - Main Incomer
	public final static String MAIN_INCOMER = "19.1.1";

	private static final long serialVersionUID = -1322900068244541952L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CODE_SEQ")	
	@SequenceGenerator(name="CODE_SEQ", sequenceName="CODE_SEQ", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;		//코드 id
	@Column(unique=true, nullable=false)
	private String code;	//코드값
	@Column(nullable=false)
	private String name;	//코드명
	private String descr;	//코드설명
	@Column(name="codeorder")
	private Integer order = 0; 

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	@ReferencedBy(name="code")
	private Code parent;	//부모 코드
	
	@Column(name="parent_id", nullable=true, updatable=false, insertable=false)
	private Integer parentId;

	@OneToMany(mappedBy="parent", fetch=FetchType.LAZY)
	private Set<Code> children = new HashSet<Code>();
	
	public Code() {		
	}		

	public Code(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}	
	
	@XmlTransient
	public Code getParent() {
		return parent;
	}
	public void setParent(Code parent) {
		this.parent = parent;
	}

	@XmlTransient
	public Set<Code> getChildren() {
		return children;
	}	
	public void setChildren(Set<Code> children) {
		this.children = children;
	}
	
	public void addChildCode(Code child) {
		if (child == null)
			throw new IllegalArgumentException("Null child Code");
		
		if (child.getParent() != null)
			child.getParent().getChildren().remove(child);
		
		child.setParent(this);
		children.add(child);	
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}	
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}

	public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
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
	        + "',parent:'" + ((this.parent == null)? "null":parent.getId()) 
	        + "',code:'" + this.code 
	        + "',name:'" + this.name 
	        + "',descr:'" + this.descr 
	        + "',order:'" + this.order 
	        + "'}";
	    
	    return retValue;
	}

	@Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        Code other = (Code) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (code == null) {
            if (other.code != null)
                return false;
        } else if (!code.equals(other.code))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (descr == null) {
            if (other.descr != null)
                return false;
        } else if (!descr.equals(other.descr))
            return false;
        if (order == null) {
            if (other.order != null)
                return false;
        } else if (!order.equals(other.order))
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
