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
 * <p>DCU Coordinator Neighbor Information</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name="MCU_CODI_NEIGHBOR")
public class MCUCodiNeighbor extends BaseObject implements JSONString, IAuditable  {

	private static final long serialVersionUID = 2666353492180729679L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_CODI_NEIGHBOR_SEQ")
    @SequenceGenerator(name="MCU_CODI_NEIGHBOR_SEQ", sequenceName="MCU_CODI_NEIGHBOR_SEQ", allocationSize=1)
	private Integer id;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_INDEX") 
	private Integer codiNeighborIndex;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_SHORT_ID") 
    private Integer codiNeighborShortId;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_LQI") 
    private Integer codiNeighborLqi;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_IN_COST") 
	private Integer codiNeighborInCost;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_OUT_COST") 
	private Integer codiNeighborOutCost;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_AGE") 
	private Integer codiNeighborAge;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_NEIGHBOR_ID") 
	private String codiNeighborId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCodiNeighborIndex() {
        return codiNeighborIndex;
    }

    public void setCodiNeighborIndex(Integer codiNeighborIndex) {
        this.codiNeighborIndex = codiNeighborIndex;
    }

    public Integer getCodiNeighborShortId() {
        return codiNeighborShortId;
    }

    public void setCodiNeighborShortId(Integer codiNeighborShortId) {
        this.codiNeighborShortId = codiNeighborShortId;
    }

    public Integer getCodiNeighborLqi() {
        return codiNeighborLqi;
    }

    public void setCodiNeighborLqi(Integer codiNeighborLqi) {
        this.codiNeighborLqi = codiNeighborLqi;
    }

    public Integer getCodiNeighborInCost() {
        return codiNeighborInCost;
    }

    public void setCodiNeighborInCost(Integer codiNeighborInCost) {
        this.codiNeighborInCost = codiNeighborInCost;
    }

    public Integer getCodiNeighborOutCost() {
        return codiNeighborOutCost;
    }

    public void setCodiNeighborOutCost(Integer codiNeighborOutCost) {
        this.codiNeighborOutCost = codiNeighborOutCost;
    }

    public Integer getCodiNeighborAge() {
        return codiNeighborAge;
    }

    public void setCodiNeighborAge(Integer codiNeighborAge) {
        this.codiNeighborAge = codiNeighborAge;
    }

    public String getCodiNeighborId() {
        return codiNeighborId;
    }

    public void setCodiNeighborId(String codiNeighborId) {
        this.codiNeighborId = codiNeighborId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result
                + ((codiNeighborAge == null) ? 0 : codiNeighborAge.hashCode());
        result = prime * result
                + ((codiNeighborId == null) ? 0 : codiNeighborId.hashCode());
        result = prime
                * result
                + ((codiNeighborInCost == null) ? 0 : codiNeighborInCost
                        .hashCode());
        result = prime
                * result
                + ((codiNeighborIndex == null) ? 0 : codiNeighborIndex
                        .hashCode());
        result = prime * result
                + ((codiNeighborLqi == null) ? 0 : codiNeighborLqi.hashCode());
        result = prime
                * result
                + ((codiNeighborOutCost == null) ? 0 : codiNeighborOutCost
                        .hashCode());
        result = prime
                * result
                + ((codiNeighborShortId == null) ? 0 : codiNeighborShortId
                        .hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MCUCodiNeighbor other = (MCUCodiNeighbor) obj;
        if (codiNeighborAge == null) {
            if (other.codiNeighborAge != null)
                return false;
        } else if (!codiNeighborAge.equals(other.codiNeighborAge))
            return false;
        if (codiNeighborId == null) {
            if (other.codiNeighborId != null)
                return false;
        } else if (!codiNeighborId.equals(other.codiNeighborId))
            return false;
        if (codiNeighborInCost == null) {
            if (other.codiNeighborInCost != null)
                return false;
        } else if (!codiNeighborInCost.equals(other.codiNeighborInCost))
            return false;
        if (codiNeighborIndex == null) {
            if (other.codiNeighborIndex != null)
                return false;
        } else if (!codiNeighborIndex.equals(other.codiNeighborIndex))
            return false;
        if (codiNeighborLqi == null) {
            if (other.codiNeighborLqi != null)
                return false;
        } else if (!codiNeighborLqi.equals(other.codiNeighborLqi))
            return false;
        if (codiNeighborOutCost == null) {
            if (other.codiNeighborOutCost != null)
                return false;
        } else if (!codiNeighborOutCost.equals(other.codiNeighborOutCost))
            return false;
        if (codiNeighborShortId == null) {
            if (other.codiNeighborShortId != null)
                return false;
        } else if (!codiNeighborShortId.equals(other.codiNeighborShortId))
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
        return "MCUCodiNeighbor [codiNeighborAge=" + codiNeighborAge
                + ", codiNeighborId=" + codiNeighborId
                + ", codiNeighborInCost=" + codiNeighborInCost
                + ", codiNeighborIndex=" + codiNeighborIndex
                + ", codiNeighborLqi=" + codiNeighborLqi
                + ", codiNeighborOutCost=" + codiNeighborOutCost
                + ", codiNeighborShortId=" + codiNeighborShortId + ", id=" + id
                + "]";
    }
    
    @Override
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(id)
		    .key("codiNeighborAge").value(codiNeighborAge)
		   .key("codiNeighborId").value(codiNeighborId)
		   .key("codiNeighborInCost").value(codiNeighborInCost)
		   .key("codiNeighborIndex").value(codiNeighborIndex)
		   .key("codiNeighborLqi").value(codiNeighborLqi)
		   .key("codiNeighborOutCost").value(codiNeighborOutCost)
		   .key("codiNeighborShortId").value(codiNeighborShortId);
		  
		   js.endObject();

    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println(e);
    		
    	}
    	return js.toString();
	}
    
    @Override
    public String getInstanceName() {
        return this.getCodiNeighborId();
    }
}
