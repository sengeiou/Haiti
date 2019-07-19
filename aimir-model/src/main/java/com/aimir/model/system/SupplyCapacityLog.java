package com.aimir.model.system;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.model.BaseObject;

/**
 * 공급 계약정보 변경내역
 * 지역별 공급용량 이력 관리
 * 주로 BEMS에서 빌딩 구역별 계약정보 변경시에 이력으로 저장하기 위해 사용하는 클래스 정보
 *  
 * @author 강소이(soyikang)
 */
@Entity
public class SupplyCapacityLog extends BaseObject implements JSONString {

	private static final long serialVersionUID = 7543353742991087045L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUPPLYCAPACITYLOG_SEQ")
	@SequenceGenerator(name = "SUPPLYCAPACITYLOG_SEQ", sequenceName = "SUPPLYCAPACITYLOG_SEQ", allocationSize = 1)
	private Long id;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "supplier_id", nullable = true)
	private Supplier supplier;

	@Column(name="supplier_id", nullable=true, updatable=false, insertable=false)
	private Integer supplierId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "location_id", nullable = true)
	private Location location;
	
	@Column(name="location_id", nullable=true, updatable=false, insertable=false)
	private Integer locationId;

	@Column(length = 255, nullable = true)
	private String contractCapacity;
	
	@Column(length = 255, nullable = true)
	private String supplyTypeLocation;
	
	@Column(name = "CONTRACT_NUMBER")
	private String contractNumber;
	
	@Column(name = "supplyType")
	private String supplyType;
	
	@Column(length = 14, nullable = false)
	private String writeDatetime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	@XmlTransient
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getContractCapacity() {
		return contractCapacity;
	}

	public void setContractCapacity(String contractCapacity) {
		this.contractCapacity = contractCapacity;
	}
	
	public String getSupplyTypeLocation() {
		return supplyTypeLocation;
	}

	public void setSupplyTypeLocation(String supplyTypeLocation) {
		this.supplyTypeLocation = supplyTypeLocation;
	}
	
	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getWriteDatetime() {
		return writeDatetime;
	}

	public void setWriteDatetime(String writeDatetime) {
		this.writeDatetime = writeDatetime;
	}

	public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String toString() {
		return "SupplyCapacityLog " + toJSONString();
	}

	public String toJSONString() {

		String retValue = "";

		retValue = "{"
				+ "id:'"
				+ this.id
				+ "',supplier:'"
				+ ((this.supplier == null) ? "" : this.supplier.getName())
				+ "',contractNumber:'"+((this.contractNumber == null) ? "" : this.contractNumber)
				+ "',supplyType:'"+ ((this.supplyType == null) ? "" : this.supplyType)
				+ "',contractCapacity:'"
				+ ((this.contractCapacity == null) ? "": this.contractCapacity)
				+ "',writeDatetime:'"+ this.writeDatetime
				+ "',location:'"+ ((this.supplyTypeLocation == null) ? "null": supplyTypeLocation) + "'}";

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
