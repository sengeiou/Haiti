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
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 *  에너지 절감 목표
 *  에너지 절감 목표 
 * - 목표 : 평균 사용량에서 몇 % 절감 
 * - 적용일 : 목표 적용일 (yyyyMMdd) 
 * - 생성일 : 목표 생성일 
 * - 년평균 
 * - 월평균 
 * - 주평균 
 * - 일평균 
 * 
 * 적용일과 생성일이 키이다. 따라서, 한날 동일한 적용일로 여러 개의 목표 설정을 할 수 없다. 
 * </pre>
 *
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "ENERGY_SAVING_GOAL")
public class EnergySavingGoal extends BaseObject {

	private static final long serialVersionUID = -1594259724448629680L;

	@EmbeddedId public EnergySavingGoalPk id;
    
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
    
    @Column(name="SUPPLIER_ID", nullable=true, updatable=false, insertable=false)
    private Integer supplierId;
    
    public EnergySavingGoal() {
        id = new EnergySavingGoalPk();
    }
    
    public EnergySavingGoalPk getId() {
        return id;
    }

    public void setId(EnergySavingGoalPk id) {
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
