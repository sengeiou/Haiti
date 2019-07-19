package com.aimir.model.device;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.aimir.annotation.ColumnInfo;

/**
 * <p>
 * Copyright NuriTelecom Co.Ltd. since 2009
 * </p>
 * 
 * <p>
 * EMnV SubGiga Modem
 * </p>
 * 
 */
@Entity
@DiscriminatorValue("EMnVSubGiga")
public class EMnVSubGiga extends SubGiga {
	private static final long serialVersionUID = 1L;

	@ColumnInfo(name = "prodMaker", descr = "제작사")
	@Column(name = "PROD_MAKER")
	private String prodMaker;

	@ColumnInfo(name = "prodMakeDate", descr = "제조년월일")
	@Column(name = "PROD_MAKE_DATE")
	private String prodMakeDate;

	@ColumnInfo(name = "prodSerial", descr = "제조번호")
	@Column(name = "PROD_SERIAL")
	private String prodSerial;

	@ColumnInfo(name = "euiId", descr = "EUI ID")
	@Column(name = "EUI_ID")
	private String euiId;

	public String getProdMaker() {
		return prodMaker;
	}

	public void setProdMaker(String prodMaker) {
		this.prodMaker = prodMaker;
	}

	public String getProdMakeDate() {
		return prodMakeDate;
	}

	public void setProdMakeDate(String prodMakeDate) {
		this.prodMakeDate = prodMakeDate;
	}

	public String getProdSerial() {
		return prodSerial;
	}

	public void setProdSerial(String prodSerial) {
		this.prodSerial = prodSerial;
	}

	public String getEuiId() {
		return euiId;
	}

	public void setEuiId(String euiId) {
		this.euiId = euiId;
	}

}