package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>DCU Coordinator Memory Information</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name="MCU_CODI_MEMORY")
public class MCUCodiMemory extends BaseObject implements JSONString, IAuditable  {

	private static final long serialVersionUID = -5215221068228365527L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_CODI_MEMORY_SEQ")
    @SequenceGenerator(name="MCU_CODI_MEMORY_SEQ", sequenceName="MCU_CODI_MEMORY_SEQ", allocationSize=1) 
	private Integer id;
	
    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_INDEX") 
	private Integer codiIndex;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ID") 
	private String codiID;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ADDRESS_TABLE_SIZE") 
	private Integer codiAddressTableSize;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_WHOLE_ADDRESS_TABLE_SIZE") 
	private Integer codiWholeAddressTableSize;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_TABLE_SIZE") 
	private Integer codiNeighborTableSize;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_SOURCE_ROUTE_TABLE_SIZE") 
	private Integer codiSourceRouteTableSize;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ROUTE_TABLE_SIZE") 
	private Integer codiRouteTableSize;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_MAX_HOPS") 
	private Integer codiMaxHops;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_PACKET_BUFFER_COUNT") 
	private Integer codiPacketBufferCount;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_SOFTWARE_VERSION") 
	private Integer codiSoftwareVersion;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_KEY_TABLE_SIZE") 
	private Integer codiKeyTableSize;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_MAX_CHILDREN") 
	private Integer codiMaxChildren;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCodiIndex() {
        return codiIndex;
    }

    public void setCodiIndex(Integer codiIndex) {
        this.codiIndex = codiIndex;
    }

    public String getCodiID() {
        return codiID;
    }

    public void setCodiID(String codiID) {
        this.codiID = codiID;
    }

    public Integer getCodiAddressTableSize() {
        return codiAddressTableSize;
    }

    public void setCodiAddressTableSize(Integer codiAddressTableSize) {
        this.codiAddressTableSize = codiAddressTableSize;
    }

    public Integer getCodiWholeAddressTableSize() {
        return codiWholeAddressTableSize;
    }

    public void setCodiWholeAddressTableSize(Integer codiWholeAddressTableSize) {
        this.codiWholeAddressTableSize = codiWholeAddressTableSize;
    }

    public Integer getCodiNeighborTableSize() {
        return codiNeighborTableSize;
    }

    public void setCodiNeighborTableSize(Integer codiNeighborTableSize) {
        this.codiNeighborTableSize = codiNeighborTableSize;
    }

    public Integer getCodiSourceRouteTableSize() {
        return codiSourceRouteTableSize;
    }

    public void setCodiSourceRouteTableSize(Integer codiSourceRouteTableSize) {
        this.codiSourceRouteTableSize = codiSourceRouteTableSize;
    }

    public Integer getCodiRouteTableSize() {
        return codiRouteTableSize;
    }

    public void setCodiRouteTableSize(Integer codiRouteTableSize) {
        this.codiRouteTableSize = codiRouteTableSize;
    }

    public Integer getCodiMaxHops() {
        return codiMaxHops;
    }

    public void setCodiMaxHops(Integer codiMaxHops) {
        this.codiMaxHops = codiMaxHops;
    }

    public Integer getCodiPacketBufferCount() {
        return codiPacketBufferCount;
    }

    public void setCodiPacketBufferCount(Integer codiPacketBufferCount) {
        this.codiPacketBufferCount = codiPacketBufferCount;
    }

    public Integer getCodiSoftwareVersion() {
        return codiSoftwareVersion;
    }

    public void setCodiSoftwareVersion(Integer codiSoftwareVersion) {
        this.codiSoftwareVersion = codiSoftwareVersion;
    }

    public Integer getCodiKeyTableSize() {
        return codiKeyTableSize;
    }

    public void setCodiKeyTableSize(Integer codiKeyTableSize) {
        this.codiKeyTableSize = codiKeyTableSize;
    }

    public Integer getCodiMaxChildren() {
        return codiMaxChildren;
    }

    public void setCodiMaxChildren(Integer codiMaxChildren) {
        this.codiMaxChildren = codiMaxChildren;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime
                * result
                + ((codiAddressTableSize == null) ? 0 : codiAddressTableSize
                        .hashCode());
        result = prime * result + ((codiID == null) ? 0 : codiID.hashCode());
        result = prime * result
                + ((codiIndex == null) ? 0 : codiIndex.hashCode());
        result = prime
                * result
                + ((codiKeyTableSize == null) ? 0 : codiKeyTableSize.hashCode());
        result = prime * result
                + ((codiMaxChildren == null) ? 0 : codiMaxChildren.hashCode());
        result = prime * result
                + ((codiMaxHops == null) ? 0 : codiMaxHops.hashCode());
        result = prime
                * result
                + ((codiNeighborTableSize == null) ? 0 : codiNeighborTableSize
                        .hashCode());
        result = prime
                * result
                + ((codiPacketBufferCount == null) ? 0 : codiPacketBufferCount
                        .hashCode());
        result = prime
                * result
                + ((codiRouteTableSize == null) ? 0 : codiRouteTableSize
                        .hashCode());
        result = prime
                * result
                + ((codiSoftwareVersion == null) ? 0 : codiSoftwareVersion
                        .hashCode());
        result = prime
                * result
                + ((codiSourceRouteTableSize == null) ? 0
                        : codiSourceRouteTableSize.hashCode());
        result = prime
                * result
                + ((codiWholeAddressTableSize == null) ? 0
                        : codiWholeAddressTableSize.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MCUCodiMemory other = (MCUCodiMemory) obj;
        if (codiAddressTableSize == null) {
            if (other.codiAddressTableSize != null)
                return false;
        } else if (!codiAddressTableSize.equals(other.codiAddressTableSize))
            return false;
        if (codiID == null) {
            if (other.codiID != null)
                return false;
        } else if (!codiID.equals(other.codiID))
            return false;
        if (codiIndex == null) {
            if (other.codiIndex != null)
                return false;
        } else if (!codiIndex.equals(other.codiIndex))
            return false;
        if (codiKeyTableSize == null) {
            if (other.codiKeyTableSize != null)
                return false;
        } else if (!codiKeyTableSize.equals(other.codiKeyTableSize))
            return false;
        if (codiMaxChildren == null) {
            if (other.codiMaxChildren != null)
                return false;
        } else if (!codiMaxChildren.equals(other.codiMaxChildren))
            return false;
        if (codiMaxHops == null) {
            if (other.codiMaxHops != null)
                return false;
        } else if (!codiMaxHops.equals(other.codiMaxHops))
            return false;
        if (codiNeighborTableSize == null) {
            if (other.codiNeighborTableSize != null)
                return false;
        } else if (!codiNeighborTableSize.equals(other.codiNeighborTableSize))
            return false;
        if (codiPacketBufferCount == null) {
            if (other.codiPacketBufferCount != null)
                return false;
        } else if (!codiPacketBufferCount.equals(other.codiPacketBufferCount))
            return false;
        if (codiRouteTableSize == null) {
            if (other.codiRouteTableSize != null)
                return false;
        } else if (!codiRouteTableSize.equals(other.codiRouteTableSize))
            return false;
        if (codiSoftwareVersion == null) {
            if (other.codiSoftwareVersion != null)
                return false;
        } else if (!codiSoftwareVersion.equals(other.codiSoftwareVersion))
            return false;
        if (codiSourceRouteTableSize == null) {
            if (other.codiSourceRouteTableSize != null)
                return false;
        } else if (!codiSourceRouteTableSize
                .equals(other.codiSourceRouteTableSize))
            return false;
        if (codiWholeAddressTableSize == null) {
            if (other.codiWholeAddressTableSize != null)
                return false;
        } else if (!codiWholeAddressTableSize
                .equals(other.codiWholeAddressTableSize))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "MCUCodiMemory [codiAddressTableSize=" + codiAddressTableSize
                + ", codiID=" + codiID + ", codiIndex=" + codiIndex
                + ", codiKeyTableSize=" + codiKeyTableSize
                + ", codiMaxChildren=" + codiMaxChildren + ", codiMaxHops="
                + codiMaxHops + ", codiNeighborTableSize="
                + codiNeighborTableSize + ", codiPacketBufferCount="
                + codiPacketBufferCount + ", codiRouteTableSize="
                + codiRouteTableSize + ", codiSoftwareVersion="
                + codiSoftwareVersion + ", codiSourceRouteTableSize="
                + codiSourceRouteTableSize + ", codiWholeAddressTableSize="
                + codiWholeAddressTableSize + ", id=" + id + "]";
    }
    
    @Override
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(id)
		    .key("codiAddressTableSize").value(codiAddressTableSize)
		   .key("codiID").value(codiID)
		   .key("codiIndex").value(codiIndex)
		   .key("codiKeyTableSize").value(codiKeyTableSize)
		   .key("codiMaxChildren").value(codiMaxChildren)
		   .key("codiMaxHops").value(codiMaxHops)
		   .key("codiNeighborTableSize").value(codiNeighborTableSize)
		   .key("codiPacketBufferCount").value(codiPacketBufferCount)
		   .key("codiRouteTableSize").value(codiRouteTableSize)
		   .key("codiSoftwareVersion").value(codiSoftwareVersion)
		   .key("codiSourceRouteTableSize").value(codiSourceRouteTableSize)
		   .key("codiWholeAddressTableSize").value(codiWholeAddressTableSize);
		  
		   js.endObject();

    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println(e);
    		
    	}
    	return js.toString();
	}
    
    @Override
    public String getInstanceName() {
        return this.getCodiID();
    }
}