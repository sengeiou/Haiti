package com.aimir.service.mvm.bean;

public class MvmDetailViewData {
	
	// 채널 여러개 선택 신규로직 start =======================
	
	private Integer channel;
	private String channelName;
	private String value;
	private String maxValue;
	private String minValue;
	private String avgValue;
	private String sumValue;
	
	private String decimalValue;
	private String maxDecimalValue;
	private String minDecimalValue;
	private String avgDecimalValue;
	private String sumDecimalValue;
	
	private String date;
	private String localeDate;
	
	public String getLocaleDate() {
		return localeDate;
	}
	public void setLocaleDate(String localeDate) {
		this.localeDate = localeDate;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public Integer getChannel() {
		return channel;
	}
	public void setChannel(Integer channel) {
		this.channel = channel;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
	public String getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	public String getMinValue() {
		return minValue;
	}
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	public String getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(String avgValue) {
		this.avgValue = avgValue;
	}
	public String getSumValue() {
		return sumValue;
	}
	public void setSumValue(String sumValue) {
		this.sumValue = sumValue;
	}
	
	// 채널 여러개 선택 신규로직 end =======================
	
	
	private String firstCol;
	//private String value;
	private String co2;
	private String status;
	private Double maxvalue;
	private Double avgvalue;
	
	
	public String getFirstCol() {
        return firstCol;
    }
    public void setFirstCol(String firstCol) {
        this.firstCol = firstCol;
    }
    
    
    public String getCo2() {
        return co2;
    }
    public void setCo2(String co2) {
        this.co2 = co2;
    }
    /*
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    */
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Double getMaxvalue() {
        return maxvalue;
    }
    public void setMaxvalue(Double maxvalue) {
        this.maxvalue = maxvalue;
    }
    
    public Double getAvgvalue() {
        return avgvalue;
    }
    public void setAvgvalue(Double avgvalue) {
        this.avgvalue = avgvalue;
    }
	public String getMaxDecimalValue() {
		return maxDecimalValue;
	}
	public void setMaxDecimalValue(String maxDecimalValue) {
		this.maxDecimalValue = maxDecimalValue;
	}
	public String getMinDecimalValue() {
		return minDecimalValue;
	}
	public void setMinDecimalValue(String minDecimalValue) {
		this.minDecimalValue = minDecimalValue;
	}
	public String getAvgDecimalValue() {
		return avgDecimalValue;
	}
	public void setAvgDecimalValue(String avgDecimalValue) {
		this.avgDecimalValue = avgDecimalValue;
	}
	public String getSumDecimalValue() {
		return sumDecimalValue;
	}
	public void setSumDecimalValue(String sumDecimalValue) {
		this.sumDecimalValue = sumDecimalValue;
	}
	public String getDecimalValue() {
		return decimalValue;
	}
	public void setDecimalValue(String decimalValue) {
		this.decimalValue = decimalValue;
	}
	
	
}
