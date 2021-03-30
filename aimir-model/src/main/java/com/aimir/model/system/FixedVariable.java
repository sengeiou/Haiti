package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.aimir.model.BaseObject;

@Entity
@Table(name="FIXED_VARIABLE")
public class FixedVariable extends BaseObject {

	private static final long serialVersionUID = -4590658900967225187L;
	
	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SEQ_FIXEDVARIABLE")
    @SequenceGenerator(name="SEQ_FIXEDVARIABLE", sequenceName="SEQ_FIXEDVARIABLE", allocationSize=1)	
    private Integer id;
	
	
	@Column(name="name")
	private String name;
	
	@Column(name="tarifftype_id") 
	private Integer tarifftypeId;
		
	@Column(name="unit")
	private String unit;
	
	@Column(name="amount")
	private String amount;
	
	@Column(name="applydate", length = 14)
	private String applyDate;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTarifftypeId() {
		return tarifftypeId;
	}

	public void setTarifftypeId(Integer tarifftypeId) {
		this.tarifftypeId = tarifftypeId;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
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
