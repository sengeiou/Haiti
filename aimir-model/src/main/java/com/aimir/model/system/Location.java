package com.aimir.model.system;

import java.util.HashSet;
import java.util.Iterator;
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
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.annotations.DeleteAll;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * 공급사 관리지역 정보 
 * 공급지역 
 * 공급사 입장에서는 어느 특정 지역이 될 수 있지만 빌딩/공장의 경우 건물을 표현할 수 있다. 
 * 예로 우리라이온스밸리가 있고 3개(A,B,C) 동으로 구분되고 각 층을 표현한다. 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="location">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}baseObject">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="orderNo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="parentId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="supplierId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "location", propOrder = {
    "id",
    "name",
    "orderNo",
    "parentId",
    "supplierId",
    "geocode"
})
@Entity
@Table(name="LOCATION", uniqueConstraints=@UniqueConstraint(columnNames={"geocode","supplier_id"}))
public class Location extends BaseObject implements JSONString {
    private static final long serialVersionUID = 4780327854320048764L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="LOCATION_SEQ")
    @SequenceGenerator(name="LOCATION_SEQ", sequenceName="LOCATION_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
	
    @Column(nullable=false,length=40)
    @ColumnInfo(name="지역명")
    private String name;
    
    @ColumnInfo(name="순번")
    private Integer orderNo;
	
    @XmlTransient
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supplier_id", nullable=false)
    @ColumnInfo(name="공급사")
    @ReferencedBy(name="name")
    private Supplier supplier;
	
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    @XmlTransient
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parent_id")
    @ColumnInfo(name="상위지역")
    @ReferencedBy(name="name")
    private Location parent;
	
    @Column(name="parent_id", nullable=true, updatable=false, insertable=false)
    private Integer parentId;
	
    @XmlTransient
    @OneToMany(mappedBy="parent", fetch=FetchType.LAZY)		//TODO. 삭제에 대해서만 존재하면 된다.
    @ColumnInfo(name="하위관리지역")
    @OrderBy("orderNo asc")
    @DeleteAll
    private Set<Location> children = new HashSet<Location>(0);
	
//	private Set<SupplyType> supplyTypes = new HashSet<SupplyType>();
    @XmlTransient
    @OneToMany(mappedBy = "location", fetch=FetchType.LAZY)
    @ColumnInfo(name = "지역공급서비스")
    private Set<SupplyTypeLocation> supplyTypeLocations = new HashSet<SupplyTypeLocation>(0);
	
    @Column(nullable=true,length=30)
    @ColumnInfo(name="지역코드")
    private String geocode;
	
    public Location() {
    }
    public Location(Integer id) {
        this.id = id;
    }
    public Location(String name) {
        this.name = name;
    }
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
    public Integer getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @XmlTransient
    public Location getParent() {
        return parent;
    }
    public void setParent(Location parent) {
        this.parent = parent;
    }
	
    @XmlTransient
    public Set<Location> getChildren() {
        return children;
    }
    public void setChildren(Set<Location> children) {
        this.children = children;
    }
    public void addChildLocation(Location child) {
        if (child == null)
            throw new IllegalArgumentException("Null child Location");
    	
        if (child.getParent() != null)
            child.getParent().getChildren().remove(child);
    	
        child.setParent(this);
        children.add(child);
    }
	
    @XmlTransient
    public Set<SupplyTypeLocation> getSupplyTypeLocations() {
        return supplyTypeLocations;
    }
    public void setSupplyTypeLocations(Set<SupplyTypeLocation> supplyTypeLocations) {
        this.supplyTypeLocations = supplyTypeLocations;
    }

    public Integer getSupplierId() {
        return supplierId;
    }
    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }
    public Integer getParentId() {
        return parentId;
    }
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    
    public String getGeocode() {
        return geocode;
    }
    public void setGeocode(String geocode) {
        this.geocode = geocode;
    }
    
    @Override
    public String toString()
    {
        return "Location " + toJSONString();
    }
	
    public String toJSONString() {
        JSONStringer js = null;

        try {
            js = new JSONStringer();
            js.object().key("id").value(this.id)
            .key("name").value(this.name)
            .key("orderNo").value((this.orderNo == null)? "":this.orderNo)
            .key("parent").value((this.parent == null)? "":parent.getName())
            .key("children").array();
    	
            Iterator<Location> it = null;
            if(this.children != null && this.children.size() > 0){
                it = this.children.iterator();
            }

            if(it != null && it.hasNext()) {
                while(it.hasNext()) {
                    Location location = (Location) it.next();
                    js.value(JSONSerializer.toJSON(location.toJSONString()));
                }
                js.endArray();
            } else {
                js.endArray();
            }
            js.endObject();
        } catch (Exception e) {
            System.out.println(e);
        }
        return js.toString();
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
	
//	@Transient
//	public List<LocationService> getLocationServicesBySupplier() {
//		List<LocationService> lsbs = new ArrayList<LocationService>();
//		
//		for (LocationService service : getLocationServices()) {
//			if (service.getSupplier().equals(supplier))		
//				lsbs.add(service);
//		}
//		return lsbs;
//	}

/*	
	@ManyToMany
    @JoinTable(name="LOCATION_SUPPLYTYPE", 
    		   joinColumns = {@JoinColumn(name="LOCATION_ID")},
    		   inverseJoinColumns = {@JoinColumn(name="SUPPLYTYPE_ID")})
	@ColumnInfo(name="서비스타입")
	public Set<SupplyType> getSupplyTypes() {
		return supplyTypes;
	}
	public void setSupplyTypes(Set<SupplyType> supplyTypes) {
		this.supplyTypes = supplyTypes;
	}*/

//	@OneToMany
//	public Set<LocationSupplier> getLocationSuppliers() {
//		return locationSuppliers;
//	}
//	public void setLocationSuppliers(Set<LocationSupplier> locationSuppliers) {
//		this.locationSuppliers = locationSuppliers;
//	}
	
//	private Location parent;
	

	
}
