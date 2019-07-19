package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BasePk;

/**
 *  <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 *  <pre>
 * 에너지 절감 목표를 위한 평균 사용량 AverageUsageBase Entity Class에 대한 Primary Key 정보
 * 복합키를 사용하기 때문에 XXXXPk 클래스로 별도 선언함.
 * </pre>
 * @author 박종성(elevas)
 */
@Embeddable
public class AverageUsageBasePk extends BasePk {

    private static final long serialVersionUID = -3777254222843823030L;

    @Column(name="AVG_USAGE_ID",length=8,nullable=false)
	private Integer avgUsageId;	

	@Column(name="SUPPLY_TYPE", length=2, nullable=false)
	private Integer supplyType;
	
	@Column(name="USAGE_YEAR", length=4, nullable=false)
    @ColumnInfo(name="년")
    private String usageYear;
	
    public Integer getAvgUsageId() {
        return avgUsageId;
    }

    public void setAvgUsageId(Integer avgUsageId) {
        this.avgUsageId = avgUsageId;
    }

    public Integer getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(Integer supplyType) {
        this.supplyType = supplyType;
    }
    
    public String getUsageYear() {
        return usageYear;
    }

    public void setUsageYear(String usageYear) {
        this.usageYear = usageYear;
    }
}