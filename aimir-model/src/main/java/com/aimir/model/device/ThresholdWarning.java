// INSERT SP-193
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

import org.eclipse.persistence.annotations.Index;
import org.eclipse.persistence.annotations.Indexes;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;
import com.aimir.model.system.Supplier;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>ThresholdWarning - </p>
 * ipv6address was changed to ipaddr. because device ip can be v4 or v6.
 * <pre>
 * </pre>
 *
 */
@Entity
@Table(name="THRESHOLDWARNING")
@Indexes({
    @Index(name="IDX_THRESHOLDWARNING_01", columnNames={"THRESHOLD_ID", "VALUE"}),
    @Index(name="IDX_THRESHOLDWARNING_02", columnNames={"ID_ADDR", "THRESHOLD_ID"})
})
public class ThresholdWarning extends BaseObject implements JSONString {

	private static final long serialVersionUID = -87595803050175852L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="THRESHOLDWARNING_SEQ")
	@SequenceGenerator(name="THRESHOLDWARNING_SEQ", sequenceName="THRESHOLDWARNING_SEQ", allocationSize=1) 
	private Integer id;	//	ID(PK)

    @Column(name="IP_ADDR", length=64)
    private String ipAddr;
    
	@Column(name="DEVICE_TYPE")
    private Integer deviceType;
	
	@Column(name = "DEVICEID")
	private Integer deviceid;
	
	@Column(name = "THRESHOLD_ID")
	private Integer threshold_id;
	
	@Column(name = "VALUE")
	private Integer value;

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

	public String getIpAddr() {
		return ipAddr;
	}
	
	public void setIpAddr(String value) {
		this.ipAddr = value;
	}
	
	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer value) {
		this.deviceType = value;
	}

	public Integer getDeviceId() {
		return deviceid;
	}

	public void setDeviceId(Integer value) {
		this.deviceid = value;
	}

	public Integer getThresholdId() {
		return threshold_id;
	}

	public void setThresholdId(Integer value) {
		this.threshold_id = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
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
				   .key("ipv6Address").value((this.ipAddr == null)? "null":this.ipAddr)
				   .key("deviceType").value((this.deviceType == null)? "null":this.deviceType)
				   .key("deviceid").value((this.deviceid == null)? "null":this.deviceid)
				   .key("threshold_id").value((this.threshold_id == null)? "null":this.threshold_id)
				   .key("value").value((this.value == null)? "null":this.value)
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
		return "ThresholdWarning [id=" + id + ", ipAddr=" + ipAddr
				+ ", deviceType=" + deviceType + ", deviceid=" + deviceid
				+ ", threshold_id=" + threshold_id 
				+ ", value=" + value
				+ "]";
	}
	
//	ThresholdWarning()
//	{
//		
//	}
//
//	public ThresholdWarning(int type, int deviceid, int threshold_id)
//	{
//		this.deviceType = type;
//		this.deviceid = deviceid;
//		this.threshold_id = threshold_id;
//		this.count = 1;
//		
//	}
	
}