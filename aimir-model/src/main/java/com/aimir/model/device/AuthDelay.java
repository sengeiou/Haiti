// INSERT SP-121
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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>AuthDelay - </p>
 * <pre>
 * </pre>
 *
 */
@Entity
@Table(name="AUTHDELAY")
public class AuthDelay extends BaseObject implements JSONString {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7286204841628018710L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="AUTHDELAY_SEQ")
	@SequenceGenerator(name="SEQ_AUTHDELAY", sequenceName="SEQ_AUTHDELAY", allocationSize=1)
	private Long id;	//	ID(PK)

    @Column(name="IPADDRESS", length=64)
    private String ipaddress;
    
	@Column(name="ERRORCNT")
    private Integer errorcnt;
	
    @Column(name="LASTDATE", length=14, nullable=true)
    private String lastdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id")
    @ReferencedBy(name="name")
    private Supplier supplier;
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;	
		
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIpAddress() {
		return this.ipaddress;
	}
	
	public void setIpAddress(String value) {
		this.ipaddress = value;
	}
	
	public Integer getErrorCnt() {
		return errorcnt;
	}

	public void setErrorCnt(Integer value) {
		this.errorcnt = value;
	}

	public String getLastDate() {
		return this.lastdate;
	}

	public void setLastDate(String value) {
		this.lastdate = value;
	}

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public AuthDelay(){
    }
    
	@Override
	public String toJSONString() {
		JSONStringer js = null;
		
		js = new JSONStringer();
		js.object().key("id").value(this.id)
				   .key("ipaddress").value((this.ipaddress == null)? "null":this.ipaddress)
				   .key("errorcnt").value((this.errorcnt == null)? "null":this.errorcnt)
				   .key("lastdate").value((this.lastdate == null)? "null":this.lastdate)
				   .endObject();

		return js.toString();
	}

	@Override
    public boolean equals(Object o) {
    	if(o instanceof AuthDelay) {
    		AuthDelay el = (AuthDelay) o;
   			return this.id.equals(el.getId());
   		}
   		else {
   			return false;
   		}
    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthDelay [id=" + id 
				+ ", ipaddress=" + ipaddress 
				+ ", errorcnt=" + errorcnt 
				+ ", lastdate=" + lastdate
				+ "]";
	}
}