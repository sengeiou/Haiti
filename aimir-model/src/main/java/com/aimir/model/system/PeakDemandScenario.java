package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

import com.aimir.model.system.PeakDemandSetting;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2012</p>
 * 
 * Peak Demand Event 발생시 수행될 시나리오에 대한 정보
 * for BEMS.
 * 
 * @author bmhan.
 * @date 2012-07-16
 */

@Entity
@Table(name="PEAKDEMAND_SCENARIO")
public class PeakDemandScenario extends BaseObject implements JSONString {

    private static final long serialVersionUID = -3726084692444191291L;
    
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="PEAKDEMAND_SCENARIO_SEQ")
    @SequenceGenerator(name="PEAKDEMAND_SCENARIO_SEQ", sequenceName="PEAKDEMAND_SCENARIO_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
    
    @Column(name="name", nullable=false, unique=true)
    @ColumnInfo(name="시나리오명")
    private String name;

    @Column(name="descr", nullable=true)
    @ColumnInfo(name="설명")
    private String descr;
    
    @Column(name="target", nullable=false)
    @ColumnInfo(name="DR 대상", descr="TAG1,TAG2,...")
    private String target;
    
    @ColumnInfo(name="계약전력") 
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="contractCapacity_id")
    @ReferencedBy(name="contractNumber")
    private ContractCapacity contractCapacity;

    @Column(name="contractCapacity_id", nullable=false, updatable=false, insertable=false)
    private Integer contractCapacityId;
    
    @Column(name="modify_time", length=14, nullable=false)
    @ColumnInfo(name="수정시간", descr="마지막으로 수정된 시간 : YYYYMMDDHHMMSS")
    private String modifyTime;

    @ColumnInfo(name="수행자", descr="Setting 수행자 / 즉, 로그인 정보") 
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="operator_id")
    @ReferencedBy(name="loginId")
    private Operator operator;
    
    @Column(name="operator_id", nullable=true, updatable=false, insertable=false)
    private Integer operatorId;

    @OneToMany(fetch=FetchType.LAZY)
    @JoinColumn(name = "scenario_id")
    @ColumnInfo(name = "DR설정")
    private Set<PeakDemandSetting> peakDemandSettings = new HashSet<PeakDemandSetting>(0);
    
    public Integer getContractCapacityId() {
        return contractCapacityId;
    }
    
    public void setContractCapacityId(Integer contractCapacityId) {
        this.contractCapacityId = contractCapacityId;
    }
    
    public Integer getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }
        
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescr() {
        return descr;
    }
    
    public void setDescr(String descr) {
        this.descr = descr;
    }
        
    public String getTarget(){
        return target;
    }
    
    public void setTarget(String target){
        this.target = target;
    }

    public ContractCapacity getContractCapacity() {
        return contractCapacity;
    }

    public void setContractCapacity(ContractCapacity contractCapacity) {
        this.contractCapacity = contractCapacity;
    }
    
    public String getModifyTime() {
        return modifyTime;
    }

    public Operator getOperator() {
        return operator;
    }
    
    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
    
    public void setOperator(Operator operator) {
        this.operator = operator;
    }
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Set<PeakDemandSetting> getPeakDemandSettings() {
        return peakDemandSettings;
    }

    public void setPeakDemandSettings(Set<PeakDemandSetting> peakDemandSettings) {
        this.peakDemandSettings = peakDemandSettings;
    }

    @Override
    public boolean equals(Object o) {           
        if(o instanceof PeakDemandScenario) {
            PeakDemandScenario pa = (PeakDemandScenario) o;
            return this.hashCode() == pa.hashCode();
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (getClass() + "_" + this.id + "_" + this.name).hashCode(); 
    }

    @Override 
    public String toString() { 
        return getClass() + "## [" + this.id + "] " + this.name;
    }

    @Override
    public String toJSONString() {
        return "{"
            + "id:'" + id 
            + "',hashCode:'" + hashCode() 
            + "',name:'" + ((name == null) ? "" : name) 
            + "',target:'" + ((target == null) ? "" : target) 
            + "',description:'" + ((descr == null) ? "" : descr) 
            + "',contractCapacity:" + ((contractCapacity == null) ? "{}" : contractCapacity.toJSONString())
            + ",modifyTime:'" + ((modifyTime == null) ? "" : modifyTime) 
            + "'}";
    }
}