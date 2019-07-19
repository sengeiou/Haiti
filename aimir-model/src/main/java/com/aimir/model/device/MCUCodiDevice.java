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
 * <p>DCU Coordinator Device Information Class</p>
 * 
 * @author goodjob
 *
 */
@Entity
@Table(name="MCU_CODI_DEVICE")
public class MCUCodiDevice extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = 7882934323689187591L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MCU_CODI_DEVICE_SEQ")
    @SequenceGenerator(name="MCU_CODI_DEVICE_SEQ", sequenceName="MCU_CODI_DEVICE_SEQ", allocationSize=1) 
	private Integer id;
	
    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_INDEX") 
	private Integer codiIndex;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_ID") 
	private String codiID;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_DEVICE") 
	private String codiDevice;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_BAUD_RATE") 
	private Integer codiBaudRate;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_PARITY_BIT") 
	private Integer codiParityBit;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_DATA_BIT") 
	private Integer codiDataBit;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_STOP_BIT") 
	private Integer codiStopBit;

    @ColumnInfo(name="", view=@Scope(create=false, read=false, update=false, devicecontrol=false), descr="")
    @Column(name="CODI_RTS_CTS") 
	private Integer codiRtsCts;

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

    public String getCodiDevice() {
        return codiDevice;
    }

    public void setCodiDevice(String codiDevice) {
        this.codiDevice = codiDevice;
    }

    public Integer getCodiBaudRate() {
        return codiBaudRate;
    }

    public void setCodiBaudRate(Integer codiBaudRate) {
        this.codiBaudRate = codiBaudRate;
    }

    public Integer getCodiParityBit() {
        return codiParityBit;
    }

    public void setCodiParityBit(Integer codiParityBit) {
        this.codiParityBit = codiParityBit;
    }

    public Integer getCodiDataBit() {
        return codiDataBit;
    }

    public void setCodiDataBit(Integer codiDataBit) {
        this.codiDataBit = codiDataBit;
    }

    public Integer getCodiStopBit() {
        return codiStopBit;
    }

    public void setCodiStopBit(Integer codiStopBit) {
        this.codiStopBit = codiStopBit;
    }

    public Integer getCodiRtsCts() {
        return codiRtsCts;
    }

    public void setCodiRtsCts(Integer codiRtsCts) {
        this.codiRtsCts = codiRtsCts;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result
                + ((codiBaudRate == null) ? 0 : codiBaudRate.hashCode());
        result = prime * result
                + ((codiDataBit == null) ? 0 : codiDataBit.hashCode());
        result = prime * result
                + ((codiDevice == null) ? 0 : codiDevice.hashCode());
        result = prime * result + ((codiID == null) ? 0 : codiID.hashCode());
        result = prime * result
                + ((codiIndex == null) ? 0 : codiIndex.hashCode());
        result = prime * result
                + ((codiParityBit == null) ? 0 : codiParityBit.hashCode());
        result = prime * result
                + ((codiRtsCts == null) ? 0 : codiRtsCts.hashCode());
        result = prime * result
                + ((codiStopBit == null) ? 0 : codiStopBit.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MCUCodiDevice other = (MCUCodiDevice) obj;
        if (codiBaudRate == null) {
            if (other.codiBaudRate != null)
                return false;
        } else if (!codiBaudRate.equals(other.codiBaudRate))
            return false;
        if (codiDataBit == null) {
            if (other.codiDataBit != null)
                return false;
        } else if (!codiDataBit.equals(other.codiDataBit))
            return false;
        if (codiDevice == null) {
            if (other.codiDevice != null)
                return false;
        } else if (!codiDevice.equals(other.codiDevice))
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
        if (codiParityBit == null) {
            if (other.codiParityBit != null)
                return false;
        } else if (!codiParityBit.equals(other.codiParityBit))
            return false;
        if (codiRtsCts == null) {
            if (other.codiRtsCts != null)
                return false;
        } else if (!codiRtsCts.equals(other.codiRtsCts))
            return false;
        if (codiStopBit == null) {
            if (other.codiStopBit != null)
                return false;
        } else if (!codiStopBit.equals(other.codiStopBit))
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
        return "MCUCodiDevice [codiBaudRate=" + codiBaudRate + ", codiDataBit="
                + codiDataBit + ", codiDevice=" + codiDevice + ", codiID="
                + codiID + ", codiIndex=" + codiIndex + ", codiParityBit="
                + codiParityBit + ", codiRtsCts=" + codiRtsCts
                + ", codiStopBit=" + codiStopBit + ", id=" + id + "]";
    }
    
    @Override
	public String toJSONString() {
		JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(id)
		    .key("codiBaudRate").value(codiBaudRate)
		   .key("codiDataBit").value(codiDataBit)
		   .key("codiDevice").value(codiDevice)
		   .key("codiID").value(codiID)
		   .key("codiIndex").value(codiIndex)
		   .key("codiParityBit").value(codiParityBit)
		   .key("codiRtsCts").value(codiRtsCts)
		   .key("codiStopBit").value(codiStopBit);
		  
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
