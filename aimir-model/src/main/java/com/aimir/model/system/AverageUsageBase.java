package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * 에너지 절감 목표를 위한 평균 사용량 기반이 되는 년도와 사용량
 * 에너지 절감 목표의 기준이 되는 평균을 구하기 위한 년 사용량 정보 
 * meteringYear 가 2009 년이면 200901~200912까지의 월 사용량의 총 합이 된다. supplyType을 통해 전기/가스/수도를 구분한다. 
 *
 * 적용일, 생성일, 공급유형, 검침년도가 키이다. 
 * </pre>
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "AVERAGE_USAGE_BASE")
public class AverageUsageBase extends BaseObject {

	private static final long serialVersionUID = 5724116943119871870L;

	@EmbeddedId public AverageUsageBasePk id;
    
    @Column(name="USAGE_VALUE", length=20)
    @ColumnInfo(name="년사용량")
    private Double usageValue;
    
    @Column(name="CO2_VALUE", length=20)
    @ColumnInfo(name="탄소배출량")
    private Double co2Value;
    
    public AverageUsageBase() {
        id = new AverageUsageBasePk();
    }
    
    public AverageUsageBasePk getId() {
        return id;
    }

    public void setId(AverageUsageBasePk id) {
        this.id = id;
    }

    public Double getUsageValue() {
        return usageValue;
    }

    public void setUsageValue(Double usageValue) {
        this.usageValue = usageValue;
    }

    public Double getCo2Value() {
        return co2Value;
    }

    public void setCo2Value(Double co2Value) {
        this.co2Value = co2Value;
    }

    public Integer getAvgUsageId() {
        return id.getAvgUsageId();
    }
    
    public void setAvgUsageId(Integer avgUsageId) {
        id.setAvgUsageId(avgUsageId);
    }
    
    public Integer getSupplyType() {
        return id.getSupplyType();
    }
    
    public void setSupplyType(Integer supplyType) {
        id.setSupplyType(supplyType);
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
        return "AverageUsageBase " + toJSONString();
    }

    public String toJSONString() {

        StringBuffer str = new StringBuffer();
        
        str.append("{"
            + "avgUsageId:'" + this.id.getAvgUsageId()
            + "', supplyType:'" + this.id.getSupplyType()
            + "', usageValue:'" + this.usageValue
            + "', usageYear:'" + this.id.getUsageYear()
            + "'}");
        
        return str.toString();
    }
}
