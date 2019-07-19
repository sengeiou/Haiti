package com.aimir.model.system;

import java.util.HashSet;
import java.util.Set;

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
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;

import com.aimir.annotation.ColumnInfo;
import com.aimir.audit.IAuditable;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * BEMS(Building Energy Management System)에서 사용하는 모델 클래스이며
 * 빌딩공급 지역별 계약용량과 목표 달성을 위한 임계치 정보를 가지고 있다.
 * 계약용량 
 * BEMS에서 빌딩 한개 또는 여러 동에 대해서 계약용량이 가능하므로 공급지역(LocationSupplier)과 관계를 가진다. 
 * 
 * @author 박종성(elevas)
 *
 */
@Entity
public class ContractCapacity extends BaseObject implements JSONString, IAuditable {

	private static final long serialVersionUID = -3659014116254300422L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONTRACTCAPACITY_SEQ")
	@SequenceGenerator(name = "CONTRACTCAPACITY_SEQ", sequenceName = "CONTRACTCAPACITY_SEQ", allocationSize = 1)
	@ColumnInfo(name = "PK", descr = "PK")
	private Integer id;

	@ColumnInfo(name = "계약용량")
	private Double capacity;

	@ColumnInfo(name = "임계치1")
	private Double threshold1;

	@ColumnInfo(name = "임계치2")
	private Double threshold2;

	@ColumnInfo(name = "임계치3")
	private Double threshold3;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "contractTypeCode_id")
	private TariffType contractTypeCode;

	@Column(name="contractTypeCode_id", nullable=true, updatable=false, insertable=false)
	private Integer tariffTypeId;
	
	@Column(name = "CONTRACT_DATE", length = 14)
	@ColumnInfo(descr = "계약일자")
	private String contractDate;

	@Column(name = "CONTRACT_NUMBER", unique = true, nullable = false)
	private String contractNumber;

	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name = "contractcapacity_id")
	@ColumnInfo(name = "공급유형지역")
	private Set<SupplyTypeLocation> supplyTypeLocations = new HashSet<SupplyTypeLocation>(
			0);

	public ContractCapacity() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Double getCapacity() {
		return capacity;
	}

	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}

	@XmlTransient
	public TariffType getContractTypeCode() {
		return contractTypeCode;
	}

	public void setContractTypeCode(TariffType contractTypeCode) {
		this.contractTypeCode = contractTypeCode;
	}

	public Double getThreshold1() {
		return threshold1;
	}

	public void setThreshold1(Double threshold1) {
		this.threshold1 = threshold1;
	}

	public Double getThreshold2() {
		return threshold2;
	}

	public void setThreshold2(Double threshold2) {
		this.threshold2 = threshold2;
	}

	public Double getThreshold3() {
		return threshold3;
	}

	public void setThreshold3(Double threshold3) {
		this.threshold3 = threshold3;
	}

	@XmlTransient
	public Set<SupplyTypeLocation> getSupplyTypeLocations() {
		return supplyTypeLocations;
	}

	public void setSupplyTypeLocations(
			Set<SupplyTypeLocation> supplyTypeLocations) {
		this.supplyTypeLocations = supplyTypeLocations;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public String getContractDate() {
		return contractDate;
	}

	public void setContractDate(String contractDate) {
		this.contractDate = contractDate;
	}

	public Integer getTariffTypeId() {
        return tariffTypeId;
    }

    public void setTariffTypeId(Integer tariffTypeId) {
        this.tariffTypeId = tariffTypeId;
    }

    @Override
	public String toString() {
		return "ContractCapacity Service " + toJSONString();
	}

	public String toJSONString() {
		String str = "";
		
		String tmp = "";
		String supply = "[]";
		String contractLocations = "";
		if(supplyTypeLocations != null) {			
			for (SupplyTypeLocation loc : supplyTypeLocations) {
				tmp += "," + loc.toJSONString();
				if(loc.getLocation() != null && loc.getLocation().getName() != null) {
					contractLocations += "," + loc.getLocation().getName();
				}
			}
			if(contractLocations.length()>0) {
				contractLocations = contractLocations.substring(1);
			}
			if(tmp.length()>0) {
				tmp = tmp.substring(1);
				supply = "["+ tmp +"]";
			}
		}
		str = "{"
				+ "id:'"
				+ this.id
				+ "', capacity:'"
				+ this.capacity
				+ "', supplyTypeLocations:"
				+ supply
				+ ", contractDate:'"
				+ this.contractDate
				+ "', contractNumber:'"
				+ this.contractNumber
				+ "', contractLocations:'"
				+ contractLocations
				+ "',contractTypeCode:'"
				+ ((this.contractTypeCode == null) ? "null"
						: this.contractTypeCode.getId()) + "', threshold1:'"
				+ this.threshold1 + "', threshold2:'" + this.threshold2
				+ "', threshold3:'" + this.threshold3 + "'}";

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

	@Override
	public String getInstanceName() {
	    return this.getContractNumber();
	}
}
