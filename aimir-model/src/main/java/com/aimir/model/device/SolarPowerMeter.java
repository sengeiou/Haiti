package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.Scope;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * <p>태양광 발전기 미터(인버터) 정보</p>
 * @author bmhan
 *
 */
@Entity
@DiscriminatorValue("SolarPowerMeter")
public class SolarPowerMeter extends Meter {

    private static final long serialVersionUID = -2627401689224710036L;

	@ColumnInfo(name="설치용량", view=@Scope(create=true, read=true, update=true),descr="설치용량. 단위는 KW") 
	@Column(name="CAPACITY")
	private Integer capacity;	

    public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}
}
