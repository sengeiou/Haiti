package com.aimir.model.device;

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
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>DistributionTransformerSubstation.java Description </p>
 *
 * 
 * Date          Version     Author   Description <br>
 * 2012. 2. 14.   v1.0       enj      Distribution Transfomer Substation 정보 관리   <br>
 *
 */
@Entity
public class DistTrfmrSubstation extends BaseObject implements JSONString, IAuditable {

	static final long serialVersionUID = 828341386560726667L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DISTTRFMRSUBSTATION_SEQ")
    @SequenceGenerator(name="DISTTRFMRSUBSTATION_SEQ", sequenceName="DISTTRFMRSUBSTATION_SEQ", allocationSize=1) 
	private Integer id;

	@ColumnInfo(name="Distribution Transformer Substation Name", descr="Distribution Transformer Substation Name")
	private String name;

	@ColumnInfo(name="Threshold", descr="the rate of technical loss at distribution network")
	private Double threshold;
	
    @ColumnInfo(name="",view=@Scope(create=true, read=true, update=true),descr="")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="LOCATION_ID")
    @ReferencedBy(name="name")
    private Location location;
 
    @Column(name="LOCATION_ID", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
	@ColumnInfo(name="미터아이디", descr="미터 테이블의 ID 혹은  NULL")
	@OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name="DistTrfmrSubstation_id")
    private Set<Meter> meter = new HashSet<Meter>(0);
 
    @ColumnInfo(name="공급사아이디", view=@Scope(create=true, read=true, update=true), descr="공급사 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID")
    @ReferencedBy(name="name" )
    private Supplier supplier;
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;

	private String description;

	private String address;

    public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlTransient
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@XmlTransient
	public Set<Meter> getMeter() {
		return meter;
	}

	public void setMeter(Set<Meter> meter) {
		this.meter = meter;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
    				   .key("name").value(this.name)
    				   .key("description").value(this.description).endObject();
    			
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
