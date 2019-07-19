package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.model.BaseObject;

/**
 * 공지사항 에 대한 정보
 * AIMIR System에서 로그인한 사용자들이 전체 공지사항에 대한 내용을 볼 수 있게
 * 공지내용 정보를 가지고 있는 클래스 이다.
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "NOTICE")
public class Notice extends BaseObject implements JSONString{

	private static final long serialVersionUID = 325930741994679915L;
		
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="NOTICE_SEQ")
	@SequenceGenerator(name="NOTICE_SEQ", sequenceName="NOTICE_SEQ", allocationSize=1) 
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="subject", length=200)
	@ColumnInfo(name="제목")
    private String subject;  
	
	@Column(name="writer", length=30)
	@ColumnInfo(name="작성자")
	private String writer;
	
	@Column(name="content", length=2000)
	@ColumnInfo(name="내용")
	private String content;
	
	@Column(name="category", length=20)
	@ColumnInfo(name="카테고리")
	private String category;
	
	@Column(name="hits", length=10)
	@ColumnInfo(name="조회수")
	private String hits;
	
	@Column(name="writedate")
	@ColumnInfo(name="작성날자")
    private String writeDate;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSubject() {
		return subject;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getWriter() {
		return writer;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}

	public void setHits(String hits) {
		this.hits = hits;
	}

	public String getHits() {
		return hits;
	}

	public void setWriteDate(String writeDate) {
		this.writeDate = writeDate;
	}

	public String getWriteDate() {
		return writeDate;
	}
	
	@Override
	public String toString()
	{
	    return "Notice "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + "',category:'" + this.category
	        + "',content:'" + this.content 
	        + "',writeDate:'" + this.writeDate 
	        + "',hits:'" + this.hits  
	        + "',subject:'" + this.subject  
	        + "',writer:'" + this.writer  
	        + "'}";
	    
	    return retValue;
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
