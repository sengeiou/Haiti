package com.aimir.model.system;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * 지역 관리 Zone 정보 
 * BEMS에서 빌딩 내 층이나 지역 구분을 위한 지역 정보
 * 
 * @author 박종성(elevas)
 * 
 */
@Entity
@Table(name="ZONE", uniqueConstraints=@UniqueConstraint(columnNames={"name","location_id"}))
public class Zone extends BaseObject implements JSONString {
	private static final long serialVersionUID = 4780327854320048764L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="ZONE_SEQ")
	@SequenceGenerator(name="ZONE_SEQ", sequenceName="ZONE_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;
	
	@Column(nullable=false,length=40)
	@ColumnInfo(name="Zone명")
	private String name;
	
	@ColumnInfo(name="순번")
	private Integer orderNo;
	
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id", nullable=false)
    @ColumnInfo(name="지역")
    @ReferencedBy(name="name")
	private Location location;
	
	@Column(name="location_id", nullable=true, updatable=false, insertable=false)
	private Integer locationId;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	@ColumnInfo(name="상위Zone")
	@ReferencedBy(name="name")
	private Zone parent;
	
	@Column(name="parent_id", nullable=true, updatable=false, insertable=false)
	private Integer parentId;
	
	@OneToMany(mappedBy="parent", cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
	@ColumnInfo(name="하위관리Zone")
	@OrderBy("orderNo asc")
	private Set<Zone> children = new HashSet<Zone>(0);
	
	public Zone() {
	}
	public Zone(Integer id) {
		this.id = id;
	}
	public Zone(String name) {
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
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@XmlTransient
	public Zone getParent() {
		return parent;
	}
	
	public void setParent(Zone parent) {
		this.parent = parent;
	}
	public Set<Zone> getChildren() {
		return children;
	}
	public void setChildren(Set<Zone> children) {
		this.children = children;
	}
	public void addChildLocation(Zone child) {
		if (child == null)
			throw new IllegalArgumentException("Null child Zone");
		
		if (child.getParent() != null)
			child.getParent().getChildren().remove(child);
		
		child.setParent(this);
		children.add(child);
	}
	
	public Integer getLocationId() {
        return locationId;
    }
    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
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
        return "Zone " + toJSONString();
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
    	
    		Iterator<Zone> it = this.children.iterator();
    		if(it.hasNext()) {
    			while(it.hasNext()) {
    				Zone location = (Zone) it.next();
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
}
