package com.aimir.model.system;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * 
 * 리포트 종류를 의미 (리포트 디자인 파일 링크 및 리포트 파라미터 조건 정보를 포함)
 *
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name = "REPORT_BOARD")
public class Report extends BaseObject implements JSONString{

    private static final long serialVersionUID = -3015901250343920906L;

    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="REPORT_SEQ")
    @SequenceGenerator(name="REPORT_SEQ", sequenceName="REPORT_SEQ", allocationSize=1)
    @ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@Column(name="NAME", length=255)
    private String name;
    
	@Column(name="METALINK", length=255)
    private String metaLink;
    
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    @JoinColumn(name="report_id")
	private List<ReportParameter> parameter;
    
	@Column(name="DESCRIPTION", length=255)
    private String description;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="parent_id")
	@ColumnInfo(name="상위 아이템")
	@ReferencedBy(name="name")
	private Report parent;
	
	@Column(name="parent_id", nullable=true, updatable=false, insertable=false)
	private Integer parentId;
	
	@OneToMany(mappedBy="parent", cascade=CascadeType.REMOVE, fetch=FetchType.LAZY)
	@ColumnInfo(name="하위 아이템")
	private Set<Report> children = new HashSet<Report>(0);
	
	@Column(name="CATEGORY_ITEM")
	@ColumnInfo(name="하위 아이템 인지 아닌지 false면 실제 리포트이고 true면 리포트에 대한 카테고리를 의미")
	private Boolean categoryItem;
	
	
	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMetaLink() {
		return metaLink;
	}

	public void setMetaLink(String metaLink) {
		this.metaLink = metaLink;
	}

	@XmlTransient
	public List<ReportParameter> getParameter() {
		return parameter;
	}

	public void setParameter(List<ReportParameter> parameter) {
		this.parameter = parameter;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public Report getParent() {
		return parent;
	}

	public void setParent(Report parent) {
		this.parent = parent;
	}

	@XmlTransient
	public Set<Report> getChildren() {
		return children;
	}

	public void setChildren(Set<Report> children) {
		this.children = children;
	}	

	public Boolean getCategoryItem() {
		return categoryItem;
	}

	public void setCategoryItem(Boolean categoryItem) {
		this.categoryItem = categoryItem;
	}

	public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    @Override
	public String toString()
	{
	    return "Report "+toJSONString();
	}
	
	public String toJSONString() {
	    
	    String retValue = "";
		
	    retValue = "{"
	        + "id:'" + this.id
	        + ", name:'" + this.name
	        + ", metaLink:'" + this.metaLink
	        + ", description:'" + this.description
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
