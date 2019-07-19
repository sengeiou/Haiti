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
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Code;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>DistributionTransformerSubstation.java Description </p>
 *
 * 
 * Date          Version     Author   Description <br>
 * 2015. 3. 09.   v1.0       eunmiae  Store Energy Balance Monitoring Device Information.  <br>
 *
 */
@Entity
public class EBS_DEVICE extends BaseObject implements JSONString, IAuditable {

	static final long serialVersionUID = 828341386560726667L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="EBS_DEVICE_SEQ")
    @SequenceGenerator(name="EBS_DEVICE_SEQ", sequenceName="EBS_DEVICE_SEQ", allocationSize=1) 
	private Integer id;

//	@ColumnInfo(name="Distribution Transformer Substation Name", descr="Distribution Transformer Substation Name")
//	private String name;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "type_id")
	@ReferencedBy(name="code")
	@ColumnInfo(descr="계약의 서비스 타입이 전기,가스,수도,열량 중 어느것인지를 의미(3.x)")
	private Code typeCd;
	
	@Column(name="type_id", nullable=true, updatable=false, insertable=false)
	private Integer typeId;
	
	@Column(name="meter_id", nullable=true)
	private String meterId;

	@Column(name="top_parent_mid")
	private String topParentMID;	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_type_id", nullable=true)
	@ReferencedBy(name="code")
	@ColumnInfo(descr="계약의 서비스 타입이 전기,가스,수도,열량 중 어느것인지를 의미(3.x)")
	private Code parentTypeCd;
	
	@Column(name="parent_type_id", nullable=true, updatable=false, insertable=false)
	private Integer parentTypeId;	

	@Column(name="parent_mid", nullable=true)
	private String parentMeterId;
	
//	@XmlTransient
//	@ManyToOne
//	@JoinColumn(name="parent_mid")
//	@ColumnInfo(name="상위지역")
//	@ReferencedBy(name="meter_id")
//	private EBS_DEVICE parent;
//		
//	@XmlTransient
//	@OneToMany(mappedBy="parent")		//TODO. 삭제에 대해서만 존재하면 된다.
//	@Cascade(value=CascadeType.DELETE)
//	@ColumnInfo(name="하위관리지역")
//	//@OrderBy("orderNo asc")
//	private Set<EBS_DEVICE> children = new HashSet<EBS_DEVICE>(0);

	@ColumnInfo(name="Threshold", descr="the rate of technical loss at distribution network")
	private Double threshold;
	
    @ColumnInfo(name="",view=@Scope(create=true, read=true, update=true),descr="")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ReferencedBy(name="name")
    private Location location;
 
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
 
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
	private String descr;

	private String address;
	
	@Column(name = "create_dt")
	private String createDt;
	
	@Column(name = "modify_dt")
	private String modifyDt;

	private String orderId;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlTransient
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getTopParentMID() {
		return topParentMID;
	}

	public void setTopParentMID(String topParentMID) {
		this.topParentMID = topParentMID;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Code getTypeCd() {
		return typeCd;
	}

	public void setTypeCd(Code typeCd) {
		this.typeCd = typeCd;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Code getParentTypeCd() {
		return parentTypeCd;
	}

	public void setParentTypeCd(Code parentTypeCd) {
		this.parentTypeCd = parentTypeCd;
	}

	public Integer getParentTypeId() {
		return parentTypeId;
	}

	public void setParentTypeId(Integer parentTypeId) {
		this.parentTypeId = parentTypeId;
	}

	public Double getThreshold() {
		return threshold;
	}

	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}

	/**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }


	public String getMeterId() {
		return meterId;
	}

	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	public String getParentMeterId() {
		return parentMeterId;
	}

	public void setParentMeterId(String parentMeterId) {
		this.parentMeterId = parentMeterId;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getCreateDt() {
		return createDt;
	}

	public void setCreateDt(String createDt) {
		this.createDt = createDt;
	}

	public String getModifyDt() {
		return modifyDt;
	}

	public void setModifyDt(String modifyDt) {
		this.modifyDt = modifyDt;
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
        return "Distribution Transformer Substation " + toJSONString();
	}

	@Override
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    		           .key("threshold").value(this.threshold)
    				   .key("meterId").value(this.meterId)
    				   .key("descr").value(this.descr).endObject();
    			
    	} catch (Exception e) {
    		System.out.println(e);
    	}
    	return js.toString();
	}

	@Override
	public String getInstanceName() {
		// TODO Auto-generated method stub
		return null;
	}	
}
