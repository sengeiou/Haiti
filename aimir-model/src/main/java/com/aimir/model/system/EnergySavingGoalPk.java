package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.aimir.model.BasePk;

@Embeddable
public class EnergySavingGoalPk extends BasePk {

	/**
     * 
     */
    private static final long serialVersionUID = -4245931962576495928L;

    @Column(name="startDate",length=8,nullable=false)
	private String startDate;	

	@Column(name="createDate", length=8, nullable=false)
	private String createDate;

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
	
}