package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.MeterProgramKind;
import com.aimir.model.BaseObject;

/**
 * 미터 SW 또는 특정 설정에 관련된 정보
 * 동일한 종류의 미터들에 대해 해당 프로파일을 공통으로 적용하기 위해서 이 클래스의 정보가 필요하다.
 * 
 * @author 박종성(elevas)
 *
 * <pre>
 * &lt;complexType name="meterProgram">
 *   &lt;complexContent>
 *     &lt;extension base="{http://server.ws.command.fep.aimir.com/}baseObject">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="kind" type="{http://server.ws.command.fep.aimir.com/}meterProgramKind" minOccurs="0"/>
 *         &lt;element name="lastModifiedDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="meterConfigId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="settings" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "meterProgram", propOrder = {
    "id",
    "kind",
    "lastModifiedDate",
    "meterConfigId",
    "settings"
})
@Entity
@Table(name="METERPROGRAM")
public class MeterProgram extends BaseObject implements JSONString {
    
    private static final long serialVersionUID = 4425586603710572606L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="METERPROGRAM_SEQ")
    @SequenceGenerator(name="METERPROGRAM_SEQ", sequenceName="METERPROGRAM_SEQ", allocationSize=1)
    private Integer id;

    @XmlTransient
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="METERCONFIG_ID")
    private MeterConfig meterConfig;
    
    @Column(name="METERCONFIG_ID", nullable=false, updatable=false, insertable=false)
    private Integer meterConfigId;
    
    @Column(name="LAST_MODIFIED_DATE", length=16)
    private String lastModifiedDate;
    
    @Lob
    @Column(name="SETTINGS")
    private String settings;
    
    @ColumnInfo(name="미터프로그램종류", descr="미터프로그램종류")
    @Enumerated(EnumType.STRING)
    @Column(name="KIND")
    private MeterProgramKind kind;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMeterConfigId() {
        return meterConfigId;
    }

    public void setMeterConfigId(Integer meterConfigId) {
        this.meterConfigId = meterConfigId;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getSettings() {
        return settings;
    }

    public void setSettings(String settings) {
        this.settings = settings;
    }

    public MeterProgramKind getKind() {
        return kind;
    }

    public void setKind(MeterProgramKind kind) {
        this.kind = kind;
    }

    @XmlTransient
    public MeterConfig getMeterConfig() {
		return meterConfig;
	}

	public void setMeterConfig(MeterConfig meterConfig) {
		this.meterConfig = meterConfig;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 0;
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((settings == null) ? 0 : settings.hashCode());
        result = prime * result + ((meterConfigId == null) ? 0:meterConfigId.hashCode());
        result = prime * result + ((kind == null) ? 0:kind.hashCode());
        
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MeterProgram other = (MeterProgram) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (settings == null) {
            if (other.settings != null)
                return false;
        } else if (!settings.equals(other.settings))
            return false;
        if (meterConfigId == null) {
            if (other.meterConfigId != null)
                return false;
        } else if (!meterConfigId.equals(other.meterConfigId))
            return false;
        if (kind == null) {
            if (other.kind != null)
                return false;
        } else if (!kind.equals(other.kind))
            return false;
        
        return true;
    }

    @Override
    public String toString() {
        return "TouProfile [meterConfigId=" +( meterConfigId == null ? "" :meterConfigId) + 
                ", lastModifiedDate=" + (lastModifiedDate == null ? "" : lastModifiedDate) + 
                ", settings=" + (settings == null ? "" : settings) + 
                ", kind=" + (kind == null ? "" : kind.getName()) + "]";
    }

    public String toJSONString() {
        JSONStringer js = null;

        try {
            js = new JSONStringer();
            js.object().key("id").value((this.id == null)? "":this.id)
                       .key("lastModifiedDate").value((this.lastModifiedDate == null)? "":this.lastModifiedDate)
                       .key("settings").value((this.settings == null)? "":this.settings)
                       .key("meterConfigId").value((this.meterConfigId == null)? 0:this.meterConfigId)
                       .key("kind").value((this.kind == null)? 0:this.kind.getName())
                       .endObject();
            
                      
        } catch (Exception e) {
            System.out.println(e);
        }
        return js.toString();
    }
}