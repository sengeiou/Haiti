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
 * EMnV Zigbee
 * </p>
 * 
 * 
 *
 */
@Entity
@DiscriminatorValue("EMnVZigBee")
public class EMnVZigBee extends Modem {
	private static final long serialVersionUID = 1L;

	@ColumnInfo(name = "", descr = "Channel Id")
	@Column(name = "CHANNEL_ID", length = 10)
	private Integer channelId;

	@ColumnInfo(name = "", descr = "Pan ID")
	@Column(name = "PAN_ID", length = 10)
	private Integer panId;

	@ColumnInfo(name = "prodMaker", descr = "제작사")
	@Column(name = "PROD_MAKER")
	private String prodMaker;

	@ColumnInfo(name = "prodMakeDate", descr = "제조년월일")
	@Column(name = "PROD_MAKE_DATE")
	private String prodMakeDate;

	@ColumnInfo(name = "prodSerial", descr = "제조번호")
	@Column(name = "PROD_SERIAL")
	private String prodSerial;

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

	public Integer getPanId() {
		return panId;
	}

	public void setPanId(Integer panId) {
		this.panId = panId;
	}

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

}