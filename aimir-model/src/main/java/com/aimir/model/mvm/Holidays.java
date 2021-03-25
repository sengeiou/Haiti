package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;


@Entity
@Table(name="HOLIDAYS")
public class Holidays extends BaseObject {
	private static final long serialVersionUID = -4080624901220266580L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_HOLIDAYS")
    @SequenceGenerator(name="SEQ_HOLIDAYS", sequenceName="SEQ_HOLIDAYS", allocationSize=1)	
	@Column(name = "id")
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
	
	@Column(name="month")
    private Integer month;
	
	@Column(name="day")
    private Integer day;
	
    @Column(name="holiday_name", length=100)    
    private String holidayName;
    
    @Column(name="type", length=30)    
    private String type;
    
    @Column(name="comments", length=40)    
    private String comments;
    
    @Column(name="writedate", length=14)    
    private String writedate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getMonth() {
		return month;
	}

	public void setMonth(Integer month) {
		this.month = month;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

	public String getHolidayName() {
		return holidayName;
	}

	public void setHolidayName(String holidayName) {
		this.holidayName = holidayName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getWritedate() {
		return writedate;
	}

	public void setWritedate(String writedate) {
		this.writedate = writedate;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
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
    
    
}
