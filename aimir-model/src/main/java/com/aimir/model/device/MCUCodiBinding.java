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
 * @author goodjob
 *
 */
@Entity
@Table(name="MCU_CODI_BINDING")
public class MCUCodiBinding extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 1128234476311014557L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_CODI_BINDING_SEQ")
    @SequenceGenerator(name="MCU_CODI_BINDING_SEQ", sequenceName="MCU_CODI_BINDING_SEQ", allocationSize=1) 
	private Integer id;
	
    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_BIND_INDEX") 
	private Integer codiBindIndex;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_BIND_TYPE") 
	private Integer codiBindType;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_BIND_LOCAL") 
	private Integer codiBindLocal;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_BIND_REMOTE") 
	private Integer codiBindRemote;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_BIND_ID") 
	private String codiBindID;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_LAST_HEARD") 
	private Integer codiLastHeard;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCodiBindIndex() {
        return codiBindIndex;
    }

    public void setCodiBindIndex(Integer codiBindIndex) {
        this.codiBindIndex = codiBindIndex;
    }

    public Integer getCodiBindType() {
        return codiBindType;
    }

    public void setCodiBindType(Integer codiBindType) {
        this.codiBindType = codiBindType;
    }

    public Integer getCodiBindLocal() {
        return codiBindLocal;
    }

    public void setCodiBindLocal(Integer codiBindLocal) {
        this.codiBindLocal = codiBindLocal;
    }

    public Integer getCodiBindRemote() {
        return codiBindRemote;
    }

    public void setCodiBindRemote(Integer codiBindRemote) {
        this.codiBindRemote = codiBindRemote;
    }

    public String getCodiBindID() {
        return codiBindID;
    }

    public void setCodiBindID(String codiBindID) {
        this.codiBindID = codiBindID;
    }

    public Integer getCodiLastHeard() {
        return codiLastHeard;
    }

    public void setCodiLastHeard(Integer codiLastHeard) {
        this.codiLastHeard = codiLastHeard;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result
                + ((codiBindID == null) ? 0 : codiBindID.hashCode());
        result = prime * result
                + ((codiBindIndex == null) ? 0 : codiBindIndex.hashCode());
        result = prime * result
                + ((codiBindLocal == null) ? 0 : codiBindLocal.hashCode());
        result = prime * result
                + ((codiBindRemote == null) ? 0 : codiBindRemote.hashCode());
        result = prime * result
                + ((codiBindType == null) ? 0 : codiBindType.hashCode());
        result = prime * result
                + ((codiLastHeard == null) ? 0 : codiLastHeard.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MCUCodiBinding other = (MCUCodiBinding) obj;
        if (codiBindID == null) {
            if (other.codiBindID != null)
                return false;
        } else if (!codiBindID.equals(other.codiBindID))
            return false;
        if (codiBindIndex == null) {
            if (other.codiBindIndex != null)
                return false;
        } else if (!codiBindIndex.equals(other.codiBindIndex))
            return false;
        if (codiBindLocal == null) {
            if (other.codiBindLocal != null)
                return false;
        } else if (!codiBindLocal.equals(other.codiBindLocal))
            return false;
        if (codiBindRemote == null) {
            if (other.codiBindRemote != null)
                return false;
        } else if (!codiBindRemote.equals(other.codiBindRemote))
            return false;
        if (codiBindType == null) {
            if (other.codiBindType != null)
                return false;
        } else if (!codiBindType.equals(other.codiBindType))
            return false;
        if (codiLastHeard == null) {
            if (other.codiLastHeard != null)
                return false;
        } else if (!codiLastHeard.equals(other.codiLastHeard))
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
        return "MCUCodiBinding [codiBindID=" + codiBindID + ", codiBindIndex="
                + codiBindIndex + ", codiBindLocal=" + codiBindLocal
                + ", codiBindRemote=" + codiBindRemote + ", codiBindType="
                + codiBindType + ", codiLastHeard=" + codiLastHeard + ", id="
                + id + "]";
    }

	@Override
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(id)
		    .key("codiBindID").value(codiBindID)
		   .key("codiBindIndex").value(codiBindIndex)
		   .key("codiBindLocal").value(codiBindLocal)
		   .key("codiBindRemote").value(codiBindRemote)
		   .key("codiLastHeard").value(codiLastHeard);
		  
		   js.endObject();

    	} catch (Exception e) {
    		e.printStackTrace();
    		System.out.println(e);
    		
    	}
    	return js.toString();
	}
	
	@Override
	public String getInstanceName() {
	    return this.getCodiBindID();
	}
}