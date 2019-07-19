package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;


/**
 * @desc Code Model  Value Object
 * 
 *
 */
public class CodeVO
{
	private Integer id;		//코드 id
	private String code;	//코드값
	private String name;	//코드명
	private String shortName;	//코트명 단축 Name
	

	private String descr;	//코드설명
	private Integer order = 0; 
	private Code parent;	//부모 코드
	private Integer parentId;
	private Set<Code> children = new HashSet<Code>();
	
	

	
	public String getShortName()
	{
		return shortName;
	}


	public void setShortName(String shortName)
	{
		this.shortName = shortName;
	}


	public Integer getId()
	{
		return id;
	}


	public void setId(Integer id)
	{
		this.id = id;
	}


	public String getCode()
	{
		return code;
	}


	public void setCode(String code)
	{
		this.code = code;
	}


	public String getName()
	{
		return name;
	}


	public void setName(String name)
	{
		this.name = name;
	}


	public String getDescr()
	{
		return descr;
	}


	public void setDescr(String descr)
	{
		this.descr = descr;
	}


	public Integer getOrder()
	{
		return order;
	}


	public void setOrder(Integer order)
	{
		this.order = order;
	}


	public Code getParent()
	{
		return parent;
	}


	public void setParent(Code parent)
	{
		this.parent = parent;
	}


	public Integer getParentId()
	{
		return parentId;
	}


	public void setParentId(Integer parentId)
	{
		this.parentId = parentId;
	}


	public Set<Code> getChildren()
	{
		return children;
	}


	public void setChildren(Set<Code> children)
	{
		this.children = children;
	}
	
	
	
	
	

}
