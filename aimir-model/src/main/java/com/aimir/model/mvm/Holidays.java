package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.annotation.ColumnInfo;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "holidays", propOrder = {
    "id",
    "month",
    "day",
    "holiday_name",
    "type",
    "comments",
    "writedate"
})

@Entity
@Table(name="HOLIDAYS")
public class Holidays {

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_HOLIDAYS")
    @SequenceGenerator(name="SEQ_HOLIDAYS", sequenceName="SEQ_HOLIDAYS", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
    private Integer id;
	
	@Column(name="month", nullable=true, updatable=false, insertable=false)
    private Integer month;
	
	@Column(name="day", nullable=true, updatable=false, insertable=false)
    private Integer day;
	
    @Column(nullable=false,length=100)    
    private String holiday_name;
    
    @Column(nullable=false,length=30)    
    private String type;
    
    @Column(nullable=false,length=40)    
    private String comments;
    
    @Column(nullable=false,length=14)    
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

	public String getHoliday_name() {
		return holiday_name;
	}

	public void setHoliday_name(String holiday_name) {
		this.holiday_name = holiday_name;
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
    
    
}
