package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * BEMS에서 아즈빌과 연동하는 데이터에 대한 로그 저장
 * 
 * @author 신인호(inhoshin)
 */
@Entity
@Table(name = "POINT")
public class Point extends BaseObject {

	private static final long serialVersionUID = 6925279936233352839L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="POINT_SEQ")
	@SequenceGenerator(name="POINT_SEQ", sequenceName="POINT_SEQ", allocationSize=1) 
    private Integer id;
	
	
    @Column(name="CREATE_DATE", length=14)
    @ColumnInfo(name="생성일")
    private String createDate;
    
    @Column(name="TIMEVALUE", length=14)
    @ColumnInfo(name="아즈빌 기록 시간")
    private String timeValue;
    
    @Column(name="NAME", length=30)
    @ColumnInfo(name="포인트명")
    private String name;
    
    @Column(name="VALUE")
    @ColumnInfo(name="검침값")
    private Integer value;
    
    @Column(name="STATUS")
    @ColumnInfo(name="status")
    private Integer status;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    
    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setValue(Integer value) {
        this.value = value;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateDate() {
        return createDate;
    }
    
    public String getTimeValue() {
        return timeValue;
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getValue() {
        return value;
    }
    
    public Integer getStatus() {
        return status;
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
