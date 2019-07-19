package com.aimir.model.system;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;
/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * 에너지 절감 목표를 위한 평균 사용량
 * BEMS에서 전체 사용량에 대한 에너지 목표를 설정하기 위한 기준 으로 사용 되는 정보이다.
 * </pre>
 * @author 박종성(elevas)
 */
@Entity
@Table(name = "AVERAGE_USAGE")
public class AverageUsage extends BaseObject {

	private static final long serialVersionUID = 6925279936233352839L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="AVERAGE_USAGE_SEQ")
	@SequenceGenerator(name="AVERAGE_USAGE_SEQ", sequenceName="AVERAGE_USAGE_SEQ", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
    
    @Column(name="CREATE_DATE", length=8)
    @ColumnInfo(name="생성일")
    private String createDate;
    
    @Column(name="DESCR", length=255)
    @ColumnInfo(name="설명")
    private String descr;
    
    @Column(name="AVG_USAGE_YEAR")
    @ColumnInfo(name="년평균사용량")
    private Double avgUsageYear;
    
    @Column(name="AVG_USAGE_MONTH")
    @ColumnInfo(name="월평균사용량")
    private Double avgUsageMonth;
    
    @Column(name="AVG_USAGE_WEEK")
    @ColumnInfo(name="주평균사용량")
    private Double avgUsageWeek;
    
    @Column(name="AVG_USAGE_DAY")
    @ColumnInfo(name="일평균사용량")
    private Double avgUsageDay;
    
    @Column(name="AVG_CO2_YEAR")
    @ColumnInfo(name="년평균탄소배출량")
    private Double avgCo2Year;
    
    @Column(name="AVG_CO2_MONTH")
    @ColumnInfo(name="월평균탄소배출량")
    private Double avgCo2Month;
    
    @Column(name="AVG_CO2_WEEK")
    @ColumnInfo(name="주평균탄소배출량")
    private Double avgCo2Week;
    
    @Column(name="AVG_CO2_DAY")
    @ColumnInfo(name="일평균탄소배출량")
    private Double avgCo2Day;
    
    @Column(name="USED")
    @ColumnInfo(name="사용유무")
    private Boolean used;
    
    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name="AVG_USAGE_ID", referencedColumnName="id")
        })
    private List<AverageUsageBase> bases; 
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Double getAvgUsageYear() {
        return avgUsageYear;
    }

    public void setAvgUsageYear(Double avgUsageYear) {
        this.avgUsageYear = avgUsageYear;
    }

    public Double getAvgUsageMonth() {
        return avgUsageMonth;
    }

    public void setAvgUsageMonth(Double avgUsageMonth) {
        this.avgUsageMonth = avgUsageMonth;
    }

    public Double getAvgUsageWeek() {
        return avgUsageWeek;
    }

    public void setAvgUsageWeek(Double avgUsageWeek) {
        this.avgUsageWeek = avgUsageWeek;
    }

    public Double getAvgUsageDay() {
        return avgUsageDay;
    }

    public void setAvgUsageDay(Double avgUsageDay) {
        this.avgUsageDay = avgUsageDay;
    }

    public Double getAvgCo2Year() {
        return avgCo2Year;
    }

    public void setAvgCo2Year(Double avgCo2Year) {
        this.avgCo2Year = avgCo2Year;
    }

    public Double getAvgCo2Month() {
        return avgCo2Month;
    }

    public void setAvgCo2Month(Double avgCo2Month) {
        this.avgCo2Month = avgCo2Month;
    }

    public Double getAvgCo2Week() {
        return avgCo2Week;
    }

    public void setAvgCo2Week(Double avgCo2Week) {
        this.avgCo2Week = avgCo2Week;
    }

    public Double getAvgCo2Day() {
        return avgCo2Day;
    }

    public void setAvgCo2Day(Double avgCo2Day) {
        this.avgCo2Day = avgCo2Day;
    }
    
    public Boolean getUsed() {
        return used;
    }

    public void setUsed(Boolean used) {
        this.used = used;
    }

    @XmlTransient
    public List<AverageUsageBase> getBases() {
        return bases;
    }
    
    public String getBasesToString() {
    	
    	String basesString = "";
    	
    	if( !bases.isEmpty() && bases.size() > 0 ){
    		
    		for(int i=0; i < bases.size(); i++){
    			
    			String temp = bases.get(i).getId().getUsageYear();
    			if( basesString.indexOf( temp ) < 0 ){
    				
    				if( i > 0 ) {
    					
    					basesString = basesString + "," + temp;
    				}else {
    					
    					basesString = basesString + temp;
    				}
    			}
    		}
    	}
    	
    	return basesString;
    }

    public void setBases(List<AverageUsageBase> bases) {
        this.bases = bases;
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
        // TODO Auto-generated method stub
        return null;
    }

}
