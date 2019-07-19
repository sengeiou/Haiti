package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import net.sf.json.JSONString;

import com.aimir.model.BaseObject;

/**
 * @fileName Memo.java
 * 개별 로그인 사용자의 메모 기록을 위한 메모장에서 쓰는 정보를 가지고 있는 클래스
 * 
 * @author 김용현(kizaorion)
 * @version 1.0
 */	

@Entity
public class Memo extends BaseObject implements JSONString {

	private static final long serialVersionUID = 6400795868129603969L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="MEMO_SEQ")
	@SequenceGenerator(name="MEMO_SEQ", sequenceName="MEMO_SEQ", allocationSize=1)
    private Integer Id;
	
	@Column(nullable=false)
	private Long userId;
	private String in_date;
	private String coord;
	@Column(nullable=false, length=800)
	private String cont;
	

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public String getIn_date() {
		return in_date;
	}

	public void setIn_date(String inDate) {
		in_date = inDate;
	}

	public String getCont() {
		return cont;
	}

	public void setCont(String cont) {
		this.cont = cont;
	}

	public String getCoord() {
		return coord;
	}

	public void setCoord(String coord) {
		this.coord = coord;
	}

	@Override
    public String toString()
    {
        return "Memo " + toJSONString();
    }
    
    public String toJSONString() {
    	
        String str = "";
        
        str = "{"
        	+ "Id:'" + this.Id
            + "', userId:'" + this.userId
            + "', in_date:'" + this.in_date
            + "', cont:'" + ((this.cont == null)? "null":this.cont)
            + "', coord:'" + this.coord
            + "'}";
        
        return str;
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