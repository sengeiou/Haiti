package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.constants.CommonConstants.DateType;
import com.aimir.model.BaseObject;
/**
 * 에너지 절감 목표 2
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "ENERGY_SAVING_GOAL2")
public class EnergySavingGoal2 extends BaseObject {

	private static final long serialVersionUID = -1594259724448629680L;

	@EmbeddedId public EnergySavingGoalPk2 id;
    
    @Column(name="SAVING_GOAL", length=20)
    @ColumnInfo(name="절감목표")
    private Double savingGoal;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="AVG_USAGE_ID")
    @ColumnInfo(name="평균사용량")
    private AverageUsage averageUsage;
    
    @Column(name="AVG_USAGE_ID", nullable=true, updatable=false, insertable=false)
    private Integer averageUsageId;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SUPPLIER_ID", nullable=false)
    @ColumnInfo(name="공급사")
    private Supplier supplier;
    
    @Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    public EnergySavingGoal2() {
        id = new EnergySavingGoalPk2();
    }
    
    public EnergySavingGoalPk2 getId() {
        return id;
    }

    public void setId(EnergySavingGoalPk2 id) {
        this.id = id;
    }

    public Double getSavingGoal() {
        return savingGoal;
    }

    public void setSavingGoal(Double savingGoal) {
        this.savingGoal = savingGoal;
    }

    public String getCreateDate() {
        return id.getCreateDate();
    }
    
    public void setCreateDate(String createDate) {
        id.setCreateDate(createDate);
    }
    
    public String getStartDate() {
        return id.getStartDate();
    }
    
    public void setStartDate(String startDate) {
        id.setStartDate(startDate);
    }
    
    public DateType getDateType() {
        return id.getDateType();
    }
    
    public void setDateType(DateType dateType) {
        this.id.setDateType(dateType);
    }
    
    public Integer getSupplyType() {
        return id.getSupplyType();
    }
    
    public void setSupplyType(Integer supplyType) {
        this.id.setSupplyType(supplyType);
    }
    
    @XmlTransient
    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    @XmlTransient
    public AverageUsage getAverageUsage() {
        return averageUsage;
    }

    public void setAverageUsage(AverageUsage averageUsage) {
        this.averageUsage = averageUsage;
    }

    public Integer getAverageUsageId() {
        return averageUsageId;
    }

    public void setAverageUsageId(Integer averageUsageId) {
        this.averageUsageId = averageUsageId;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
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
    public String toString() {
        return "EnergySavingGoal " + toJSONString();
    }

    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "startDate:'" + this.id.getStartDate()
            + "', createDate:'" + this.id.getCreateDate()
            + "', savingGoal:'" + this.savingGoal
            + "'}");
        
        return str.toString();
    }
}
