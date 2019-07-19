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
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p> 미터 현장 설치 이미지 파일의 경로</p>
 * 
 * @author goodjob
 *
 */
@Entity
public class MeterInstallImg extends BaseObject {
	
	private static final long serialVersionUID = -3947955053420777575L;
	
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="METER_INSTALLIMAGE_SEQ")
    @SequenceGenerator(name="METER_INSTALLIMAGE_SEQ", sequenceName="METER_INSTALLIMAGE_SEQ", allocationSize=1)
	private Long   id;	
    private String orginalName;
    private String currentTimeMillisName;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="METER_ID")
	@ReferencedBy(name="ID")
    private Meter Meter;
	
	@Column(name="METER_ID", nullable=true, updatable=false, insertable=false)
    private Integer meterId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrginalName() {
		return orginalName;
	}

	public void setOrginalName(String orginalName) {
		this.orginalName = orginalName;
	}

	public String getCurrentTimeMillisName() {
		return currentTimeMillisName;
	}

	public void setCurrentTimeMillisName(String currentTimeMillisName) {
		this.currentTimeMillisName = currentTimeMillisName;
	}

	@XmlTransient
	public Meter getMeter() {
		return Meter;
	}

	public void setMeterId(Meter Meter) {
		this.Meter = Meter;
	}

	public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MeterInstallImg other = (MeterInstallImg) obj;
        
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
        	return false;
        
        if (orginalName == null) {
            if (other.orginalName != null)
                return false;
        } else if (!orginalName.equals(other.orginalName))
        	return false;
        
        if (currentTimeMillisName == null) {
            if (other.currentTimeMillisName != null)
                return false;
        } else if (!currentTimeMillisName.equals(other.currentTimeMillisName))
        	return false;
        
        
        return true;
	}

	@Override
	public int hashCode() {
        final int prime = 31;
        
        int result = 0;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((orginalName == null) ? 0 : orginalName.hashCode());
        result = prime * result + ((currentTimeMillisName == null) ? 0 : currentTimeMillisName.hashCode());
        
        return result;
	}

	@Override
	public String toString() {
        return "MeterInstallImg [id=" + id+ ", orginalName=" + getOrginalName()
		+ ", currentTimeMillisName=" + currentTimeMillisName + ", id=" + id + "]";
        
	}

}
