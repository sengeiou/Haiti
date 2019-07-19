package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.aimir.constants.CommonConstants.DateType;
import com.aimir.model.BasePk;

@Embeddable
public class EnergySavingGoalPk2 extends BasePk {

    private static final long serialVersionUID = -4245931962576495928L;

    @Column(name="START_DATE",length=8,nullable=false)
	private String startDate;	

	@Column(name="CREATE_DATE", length=8, nullable=false)
	private String createDate;

	@Column(name="SUPPLY_TYPE", nullable=false)
	private Integer supplyType;
	
	@Column(name="DATE_TYPE", nullable=false)
	@Enumerated(EnumType.STRING)
	private DateType dateType;
	
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(Integer supplyType) {
        this.supplyType = supplyType;
    }

    public DateType getDateType() {
        return dateType;
    }

    public void setDateType(DateType dateType) {
        this.dateType = dateType;
    }
	
}