package com.aimir.model.system;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * 
 * Gadget 객체의 가상 객체
 * Entity, Table 객체가 아니라 서비스에서 데이터 가공을 위해 Gadget 객체를 조작하기 쉽게 가공한 객체
 * 
 * @author 허윤(unimath)
 *
 */
public class DashboardGadgetVO {

	private int id;
	private String name;
	private String descr;
	private String iconSrc;
	private int gadgetId;
	private int gridX;
	private int gridY;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getIconSrc() {
		return iconSrc;
	}
	public void setIconSrc(String iconSrc) {
		this.iconSrc = iconSrc;
	}
	public int getGadget_id() {
		return gadgetId;
	}
	public void setGadget_id(int gadgetId) {
		this.gadgetId = gadgetId;
	}
	public int getGridX() {
		return gridX;
	}
	public void setGridX(int gridX) {
		this.gridX = gridX;
	}
	public int getGridY() {
		return gridY;
	}
	public void setGridY(int gridY) {
		this.gridY = gridY;
	}
	
}
