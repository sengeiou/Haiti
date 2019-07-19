// INSERT SP-193
package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ReferencedBy;
import com.aimir.constants.CommonConstants.ThresholdName;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>Threshold - </p>
 * <pre>
 * </pre>
 *
 */
@Entity
@Table(name="THRESHOLD")
public class Threshold extends BaseObject implements JSONString {

	private static final long serialVersionUID = -44851368338651025L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="THRESHOLD_SEQ")
	@SequenceGenerator(name="THRESHOLD_SEQ", sequenceName="THRESHOLD_SEQ", allocationSize=1) 
	private Integer id;	//	ID(PK)

	@Column(name = "THRESHOLD_NAME", nullable=false)
	@Enumerated(EnumType.STRING)
	private ThresholdName name;
	
	@Transient
	private String namevalue;
	
	@Column(name = "THRESHOLD_LIMIT")
	private Integer limit;
	
	@Column(name = "THRESHOLD_DURATION", nullable=true)
	private String duration;
	
	@Column(name = "MORE")
	private Integer more;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="supplier_id")
    @ReferencedBy(name="name")
    private Supplier supplier;
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;	
		
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public ThresholdName getThresholdName() {
		return name;
	}

	public void setThresholdName(String name) {
		this.name = ThresholdName.valueOf(name);
	}

	public String getThresholdNameValue() {
		return namevalue;
	}

	public void setThresholdNameValue(String value) {
		this.namevalue = value;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public Integer getMore() {
		return more;
	}

	public void setMore(Integer more) {
		this.more = more;
	}

    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
	@Override
	public String toJSONString() {
		JSONStringer js = null;
		
		js = new JSONStringer();
		js.object().key("id").value(this.id)
				   .key("name").value((this.name == null)? "null":this.name.name())
				   .key("limit").value((this.limit == null)? "null":this.limit)
				   .key("duration").value((this.duration == null)? "null":this.duration)
				   .key("more").value((this.more == null)? "null":this.more)
				   .endObject();

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Threshold [id=" + id
				+ ", threshold_name=" + name + ", threshold_limit=" + limit
				+ ", threshold_duration=" + duration + ", more=" + more 
				+ "]";
	}

	
}