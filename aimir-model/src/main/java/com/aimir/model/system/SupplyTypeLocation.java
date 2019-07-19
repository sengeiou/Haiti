package com.aimir.model.system;

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

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * 공급사 지역공급서비스
 * 동일한 지역에 여러 공급사가 있을 수 있음. 
 * SSE의 경우 전기/가스를 서비스하고 있는데 실제로는 다른 회사를 묶어서 처리하는 것으로 이러한 경우는 SSE_Energy, SSE_Gas 식으로 공급사를 분리해야 함.  
 *  
 * 빌딩/공장에서 각 건물의 계약전력 정보로 사용할 수 있다
 * 
 */
@Entity
public class SupplyTypeLocation extends BaseObject implements JSONString, IAuditable {

    private static final long serialVersionUID = -3659014116254300422L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SUPPLYTYPELOCATION_SEQ")
    @SequenceGenerator(name="SUPPLYTYPELOCATION_SEQ", sequenceName="SUPPLYTYPELOCATION_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="supplytype_id", nullable=false)
    @ColumnInfo(name="서비스 타입")
    @ReferencedBy(name="id")
    private SupplyType supplyType;
    
    @Column(name="supplytype_id", nullable=true, updatable=false, insertable=false)
    private Integer supplyTypeId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="location_id", nullable=false)
    @ColumnInfo(name="관리지역")
    @ReferencedBy(name="name")
    private Location location;
    
    @Column(name="location_id", nullable=true, updatable=false, insertable=false)
    private Integer locationId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="contractcapacity_id")
    @ColumnInfo(name="계약용량")
    @ReferencedBy(name="contractNumber")
    private ContractCapacity contractCapacity;
    
    @Column(name="contractcapacity_id", nullable=true, updatable=false, insertable=false)
    private Integer contractCapacityId;
    
    public SupplyTypeLocation() {
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @XmlTransient
    public SupplyType getSupplyType() {
        return supplyType;
    }
    public void setSupplyType(SupplyType supplyType) {
        this.supplyType = supplyType;
    }
    
    @XmlTransient
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    @XmlTransient
    public ContractCapacity getContractCapacity() {
        return contractCapacity;
    }

    public void setContractCapacity(ContractCapacity contractCapacity) {
        this.contractCapacity = contractCapacity;
    }

    public Integer getSupplyTypeId() {
        return supplyTypeId;
    }

    public void setSupplyTypeId(Integer supplyTypeId) {
        this.supplyTypeId = supplyTypeId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getContractCapacityId() {
        return contractCapacityId;
    }

    public void setContractCapacityId(Integer contractCapacityId) {
        this.contractCapacityId = contractCapacityId;
    }

    @Override
    public String toString()
    {
        return "Location Service " + toJSONString();
    }
    
    public String toJSONString() {
        String str = "";      
        String locationName = null;
        if(location != null) {
            locationName = location.getName();
        }
        str = "{"
            + "id:'" + this.id
            + "',locationId:'" + ((this.locationId == null)? "null":this.locationId)
            + "',locationName:'" + ((locationName == null)? "null":locationName)
            + "',constractCapacity:'" + ((this.contractCapacity == null)? "null":this.contractCapacity.getCapacity())
            + "',supplyType:'" + ((this.supplyType.getTypeCode() == null)? "null":this.supplyType.getTypeCode().getName())
            + "'}";
        
        return str;
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

    @Override
    public String getInstanceName() {
        if (this.getLocation() != null && this.getSupplyType() != null)
            return this.getLocation().getName()+":"+this.getSupplyType().getInstanceName();
        else return "";
    }
}
