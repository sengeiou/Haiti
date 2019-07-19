package com.aimir.fep.protocol.nip.frame.payload;

public class DlmsMeter{
    private String headerObis;
    private String headerClass;
    private String headerAttr;
    private Integer headerLength;
    private String tagTag;
    private String tagDataOrLenData;
    
	public String getHeaderObis() {
		return headerObis;
	}
	public void setHeaderObis(String headerObis) {
		this.headerObis = headerObis;
	}
	public String getHeaderClass() {
		return headerClass;
	}
	public void setHeaderClass(String headerClass) {
		this.headerClass = headerClass;
	}
	public String getHeaderAttr() {
		return headerAttr;
	}
	public void setHeaderAttr(String headerAttr) {
		this.headerAttr = headerAttr;
	}
	public Integer getHeaderLength() {
        return headerLength;
    }
    public void setHeaderLength(Integer headerLength) {
        this.headerLength = headerLength;
    }
    public String getTagTag() {
		return tagTag;
	}
	public void setTagTag(String tagTag) {
		this.tagTag = tagTag;
	}
	public String getTagDataOrLenData() {
		return tagDataOrLenData;
	}
	public void setTagDataOrLenData(String tagDataOrLenData) {
		this.tagDataOrLenData = tagDataOrLenData;
	}
}