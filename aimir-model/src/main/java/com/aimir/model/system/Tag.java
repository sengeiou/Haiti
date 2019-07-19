package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ReferencedBy;

/**
 * 가젯 태그 정보 가젯을 분류하는 태그들에 대해 정의한 테이블
 *
 */
@Entity
@Table(name="TAG")
public class Tag {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="TAG_SEQ")
	@SequenceGenerator(name="TAG_SEQ", sequenceName="TAG_SEQ", allocationSize=1) 
	private Integer id;

	private String tag;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@ReferencedBy(name="name")
	private Gadget gadget;	
	
	@Column(name="gadget_id", nullable=true, updatable=false, insertable=false)
	private Integer gadgetId;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}	
	
	@XmlTransient
	public Gadget getGadget() {
		return gadget;
	}
	public void setGadget(Gadget gadget) {
		this.gadget = gadget;
	}
    public Integer getGadgetId() {
        return gadgetId;
    }
    public void setGadgetId(Integer gadgetId) {
        this.gadgetId = gadgetId;
    }
	
}
