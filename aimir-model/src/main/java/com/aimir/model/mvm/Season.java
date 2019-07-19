package com.aimir.model.mvm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.aimir.annotation.ColumnInfo;

/**
 * 계절 기준 정보
 * @author 조창희
 *
 */
@Entity
@Table(name="AIMIRSEASON", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Season {

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEASON_SEQ")
    @SequenceGenerator(name="SEASON_SEQ", sequenceName="SEASON_SEQ", allocationSize=1) 
	private Integer id;
	
	@Column(length=4)
	@ColumnInfo(name="시작 기준년")
	private String syear;	
	
    @Column(length=2)
    @ColumnInfo(name="시작 기준달")
    private String smonth;
    
    @Column(length=2)
    @ColumnInfo(name="시작 기준일")
    private String sday;   
   
    @Column(length=4)
    @ColumnInfo(name="종료 기준년")
    private String eyear;   
    
    @Column(length=2)
    @ColumnInfo(name="종료 기준달")
    private String emonth;   
    
    @Column(length=2)
    @ColumnInfo(name="종료 기준일")
    private String eday;
    
    @Column(length=4,name="START_YEAR")
    @ColumnInfo(name="검침시작 기준년")
    private String startYear;
    
    @Column(length=100,unique = true)
    @ColumnInfo(name="계절명")
	private String name;

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSyear() {
        return syear;
    }

    public void setSyear(String syear) {
        this.syear = syear;
    }

    public String getSmonth() {
        return smonth;
    }

    public void setSmonth(String smonth) {
        this.smonth = smonth;
    }

    public String getEyear() {
        return eyear;
    }

    public void setEyear(String eyear) {
        this.eyear = eyear;
    }

    public String getEmonth() {
        return emonth;
    }

    public void setEmonth(String emonth) {
        this.emonth = emonth;
    }
    
    public String getStartYear() {
        return startYear;
    }

    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSday() {
        return sday;
    }

    public void setSday(String sday) {
        this.sday = sday;
    }

    public String getEday() {
        return eday;
    }

    public void setEday(String eday) {
        this.eday = eday;
    }
    
	
}
