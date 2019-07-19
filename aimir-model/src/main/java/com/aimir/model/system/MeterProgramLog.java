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
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.constants.CommonConstants.DefaultCmdResult;
import com.aimir.model.BaseObject;
import com.aimir.model.device.Meter;

/**
 * 미터 프로그램 (SW) 혹은 미터 설정 변경 시 기록되는 이력 정보
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="METERPROGRAMLOG")
public class MeterProgramLog extends BaseObject implements JSONString {
    
    private static final long serialVersionUID = 4425586603710572606L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="METERPROGRAMLOG_SEQ")
    @SequenceGenerator(name="METERPROGRAMLOG_SEQ", sequenceName="METERPROGRAMLOG_SEQ", allocationSize=1) 
    private Integer id;

    @Column(name="APPLIED_DATE", length=16)
    private String appliedDate;
    
    @ColumnInfo(name="미터 프로그램 설정", view=@Scope(create=true, read=true, update=true), descr="TOU, 시간동기화, DST 설정 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="METERPROGRAM_ID", nullable=false)
    private MeterProgram meterProgram;
    
    @Column(name="METERPROGRAM_ID", nullable=false, updatable=false, insertable=false)
    private Integer meterProgramId;
    
    @ColumnInfo(name="미터", view=@Scope(create=true, read=true, update=true), descr="미터 테이블의 ID 혹은  NULL")
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="METER_ID", nullable=false)
    @ReferencedBy(name="mdsId")
    private Meter meter;
    
    @Column(name="METER_ID", nullable=false, updatable=false, insertable=false)
    private Integer meterId;
    
    @ColumnInfo(name="", descr="Result")
    @Enumerated(EnumType.STRING)
    @Column(name="RESULT")
    private DefaultCmdResult result;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(String appliedDate) {
        this.appliedDate = appliedDate;
    }

    @XmlTransient
    public MeterProgram getMeterProgram() {
        return meterProgram;
    }

    public void setMeterProgram(MeterProgram meterProgram) {
        this.meterProgram = meterProgram;
    }

    @XmlTransient
    public Meter getMeter() {
        return meter;
    }

    public void setMeter(Meter meter) {
        this.meter = meter;
    }

    public DefaultCmdResult getResult() {
        return result;
    }

    public void setResult(DefaultCmdResult result) {
        this.result = result;
    }

    public Integer getMeterProgramId() {
        return meterProgramId;
    }

    public void setMeterProgramId(Integer meterProgramId) {
        this.meterProgramId = meterProgramId;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 0;
        hashCode = prime * hashCode + ((appliedDate == null) ? 0 : appliedDate.hashCode());
        hashCode = prime * hashCode + ((meter == null) ? 0:meter.hashCode());
        hashCode = prime * hashCode + ((result == null) ? 0:result.hashCode());
        hashCode = prime * hashCode + ((meterProgram == null) ? 0:meterProgram.hashCode());
        
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        MeterProgramLog other = (MeterProgramLog) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (appliedDate == null) {
            if (other.appliedDate != null)
                return false;
        } else if (!appliedDate.equals(other.appliedDate))
            return false;
        if (meter == null) {
            if (other.meter != null)
                return false;
        } else if (!meter.equals(other.meter))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        if (meterProgram == null) {
            if (other.meterProgram != null)
                return false;
        } else if (!meterProgram.equals(other.meterProgram))
            return false;
        
        return true;
    }

    @Override
    public String toString() {
        return "TouProfile [appliedDate=" + appliedDate + ", meter=" + meter.toString() + 
                ", meterProgram=" + meterProgram.toString() + ", result=" + result + "]";
    }

    public String toJSONString() {
        JSONStringer js = null;

        try {
            js = new JSONStringer();
            js.object().key("id").value((this.id == null)? "":this.id)
                       .key("appliedDate").value((this.appliedDate == null)? "":this.appliedDate)
                       .key("meter").value((this.meter == null)? "":this.meter.toJSONString())
                       .key("meterProgram").value((this.meterProgram == null)? "":this.meterProgram.toJSONString())
                       .key("result").value((this.result == null)? "":this.result)
                       .endObject();
            
                      
        } catch (Exception e) {
            System.out.println(e);
        }
        return js.toString();
    }
}