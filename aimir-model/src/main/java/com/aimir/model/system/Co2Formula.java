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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 각 에너지 원별  CO2를 발생량을 계산하는 공식 정보를 가지고 있는 모델 정보<br>
 * 에너지 타입으로 전기, 가스,수도, 열량 등이 있으며<br>
 * 각 단위 사용량별 탄소 배출량 정보등의 정보를 가진다.<br>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "CO2FORMULA")
public class Co2Formula extends BaseObject {

	private static final long serialVersionUID = -3425979378837002300L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CO2FORMULA_SEQ")
	@SequenceGenerator(name="CO2FORMULA_SEQ", sequenceName="CO2FORMULA_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@ColumnInfo(name="탄소배출량이름")
    private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "type_id")
	@ColumnInfo(name="서비스타입", descr="공급서비스 타입 전기,가스,수도,열 등")
	@ReferencedBy(name="code")
	private Code supplyTypeCode;
	
	@Column(name="type_id", nullable=true, updatable=false, insertable=false)
	private Integer supplyTypeCodeId;
	
	@ColumnInfo(name="사용량")
	private Double unitUsage;
	
	@ColumnInfo(name="탄소배출량")
	private Double co2emissions;
	
	@ColumnInfo(name="탄소배출량")
	private Double co2factor;
	
	@ColumnInfo(name="단위")
	private String unit;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
	public Code getSupplyTypeCode() {
		return supplyTypeCode;
	}

	public void setSupplyTypeCode(Code code) {
		this.supplyTypeCode = code;
	}

	public Double getUnitUsage() {
		return unitUsage;
	}

	public void setUnitUsage(Double unitUsage) {
		this.unitUsage = unitUsage;
	}

	public Double getCo2emissions() {
		return co2emissions;
	}

	public void setCo2emissions(Double co2emissions) {
		this.co2emissions = co2emissions;
	}

	public Double getCo2factor() {
		return co2factor;
	}

	public void setCo2factor(Double co2factor) {
		this.co2factor = co2factor;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

    public Integer getSupplyTypeCodeId() {
        return supplyTypeCodeId;
    }

    public void setSupplyTypeCodeId(Integer supplyTypeCodeId) {
        this.supplyTypeCodeId = supplyTypeCodeId;
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
String str = "";
        
        str = "{"
            + "id:'" + this.id
            + "', name:'" + ((this.name != null)? this.getName():"")
            + "', supplyType:'" + ((this.supplyTypeCode == null)? "null":this.getSupplyTypeCode().getId())
            + "', co2factor:'" + ((this.co2factor > 0)? this.getCo2factor():"")
            + "', unitUsage:'" + ((this.unitUsage > 0)? this.getUnitUsage():"")
            + "', co2emissions:'" + ((this.co2emissions > 0)? this.getCo2emissions():"")
            + "', unit:'" + ((this.unit == null)? "null":this.unit)
            + "'}";
        
        return str;
	}
	
}
