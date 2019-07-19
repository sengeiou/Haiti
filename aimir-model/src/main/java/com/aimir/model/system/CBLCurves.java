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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.annotation.Scope;
import com.aimir.model.BaseObject;
import com.aimir.model.device.Meter;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * Demand Response의 실시간 가격 정보를 반영한 모델 정보
 * 현재 제주도 Smart Place에 적용되엉 있는 시스템에서 사용한다.
 * CBLCurves
 * </pre>
 * @author 김재식(kaze)
 *
 */
@Entity
@Table(name = "CBLCURVES")
public class CBLCurves  extends BaseObject{

	private static final long serialVersionUID = -6501081960477276283L;

	@Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="CBLCURVES_SEQ")
	@SequenceGenerator(name="CBLCURVES_SEQ", sequenceName="CBLCURVES_SEQ", allocationSize=1)
	@ColumnInfo(name="PK", descr="PK")
	private Integer id;

	@ColumnInfo(name="고객 id", view=@Scope(create=true, read=true, update=true), descr="고객 id")
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CUSTOMER_ID")
    @ReferencedBy(name="customerNo" )
    private Customer customer;
	
	@Column(name="CUSTOMER_ID", nullable=true, updatable=false, insertable=false)
	private Integer customerId;

	@ColumnInfo(name="meter id", view=@Scope(create=true, read=true, update=true), descr="meter id")
	@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="METER_ID")
    @ReferencedBy(name="mdsId" )
    private Meter meter;
	
	@Column(name="METER_ID", nullable=true, updatable=false, insertable=false)
	private Integer meterId;

	@ColumnInfo(name="unit", view=@Scope(create=false, read=false, update=false), descr="화폐 단위")
    @Column(name="UNIT")
    private String unit;

	@ColumnInfo(name="price type", view=@Scope(create=false, read=false, update=false), descr="요금제 종류(tou,rtp,rtp2p,cpp)")
    @Column(name="PRICE_TYPE")
    private String priceType;

	@ColumnInfo(name="price start date", view=@Scope(create=false, read=false, update=false), descr="가격 적용 시작 시간")
    @Column(name="PRICE_START_DATE")
    private String priceStartDate;

	@ColumnInfo(name="price end date", view=@Scope(create=false, read=false, update=false), descr="가격 적용 종료 시간")
    @Column(name="PRICE_END_DATE")
    private String priceEndDate;

	@ColumnInfo(name="cblp", view=@Scope(create=false, read=false, update=false), descr="2part-rtp 적용을 위한 기준 사용량 단위는 kWh")
    @Column(name="CBLP")
    private Float CBLP;

	@ColumnInfo(name="low price", view=@Scope(create=false, read=false, update=false), descr="CBL 라인 이하에 적용되는 단가")
    @Column(name="LOW_PRICE")
    private Float lowPrice;

	@ColumnInfo(name="high price", view=@Scope(create=false, read=false, update=false), descr="CBL 라인 이하에 적용되는 단가")
    @Column(name="HIGH_PRICE")
    private Float highPrice;

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the customer
	 */
	@XmlTransient
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the meter
	 */
	@XmlTransient
	public Meter getMeter() {
		return meter;
	}

	/**
	 * @param meter the meter to set
	 */
	public void setMeter(Meter meter) {
		this.meter = meter;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}

	/**
	 * @return the priceType
	 */
	public String getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	/**
	 * @return the priceStartDate
	 */
	public String getPriceStartDate() {
		return priceStartDate;
	}

	/**
	 * @param priceStartDate the priceStartDate to set
	 */
	public void setPriceStartDate(String priceStartDate) {
		this.priceStartDate = priceStartDate;
	}

	/**
	 * @return the priceEndDate
	 */
	public String getPriceEndDate() {
		return priceEndDate;
	}

	/**
	 * @param priceEndDate the priceEndDate to set
	 */
	public void setPriceEndDate(String priceEndDate) {
		this.priceEndDate = priceEndDate;
	}

	/**
	 * @return the cBLP
	 */
	public Float getCBLP() {
		return CBLP;
	}

	/**
	 * @param cBLP the cBLP to set
	 */
	public void setCBLP(Float cBLP) {
		CBLP = cBLP;
	}

	/**
	 * @return the lowPrice
	 */
	public Float getLowPrice() {
		return lowPrice;
	}

	/**
	 * @param lowPrice the lowPrice to set
	 */
	public void setLowPrice(Float lowPrice) {
		this.lowPrice = lowPrice;
	}

	/**
	 * @return the highPrice
	 */
	public Float getHighPrice() {
		return highPrice;
	}

	/**
	 * @param highPrice the highPrice to set
	 */
	public void setHighPrice(Float highPrice) {
		this.highPrice = highPrice;
	}

	public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getMeterId() {
        return meterId;
    }

    public void setMeterId(Integer meterId) {
        this.meterId = meterId;
    }

    /* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		CBLCurves other = (CBLCurves) obj;
		if (CBLP == null) {
			if (other.CBLP != null)
				return false;
		} else if (!CBLP.equals(other.CBLP))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (highPrice == null) {
			if (other.highPrice != null)
				return false;
		} else if (!highPrice.equals(other.highPrice))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (lowPrice == null) {
			if (other.lowPrice != null)
				return false;
		} else if (!lowPrice.equals(other.lowPrice))
			return false;
		if (meter == null) {
			if (other.meter != null)
				return false;
		} else if (!meter.equals(other.meter))
			return false;
		if (priceEndDate == null) {
			if (other.priceEndDate != null)
				return false;
		} else if (!priceEndDate.equals(other.priceEndDate))
			return false;
		if (priceStartDate == null) {
			if (other.priceStartDate != null)
				return false;
		} else if (!priceStartDate.equals(other.priceStartDate))
			return false;
		if (priceType == null) {
			if (other.priceType != null)
				return false;
		} else if (!priceType.equals(other.priceType))
			return false;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 0;
		result = prime * result + ((CBLP == null) ? 0 : CBLP.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((highPrice == null) ? 0 : highPrice.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lowPrice == null) ? 0 : lowPrice.hashCode());
		result = prime * result + ((meter == null) ? 0 : meter.hashCode());
		result = prime * result + ((priceEndDate == null) ? 0 : priceEndDate.hashCode());
		result = prime * result + ((priceStartDate == null) ? 0 : priceStartDate.hashCode());
		result = prime * result + ((priceType == null) ? 0 : priceType.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CBLCurves [CBLP=" + CBLP + ", customer=" + customer + ", highPrice=" + highPrice + ", id=" + id
				+ ", lowPrice=" + lowPrice + ", meter=" + meter + ", priceEndDate=" + priceEndDate
				+ ", priceStartDate=" + priceStartDate + ", priceType=" + priceType + ", unit=" + unit + "]";
	}
}
