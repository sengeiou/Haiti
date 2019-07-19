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
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;

/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * AIMIR System UserInterface의 메뉴(Tab)을 구성하는 대시보드 에 연관된 가젯의 연결 정보 
 * 즉 대시보드와 가젯의 맵핑 정보를 가지는 클래스이다.
 * 하나의 대시보드에 보여지는 대시보드에 속해있는 가젯과 가젯의 위치정보 (x,y) 좌표 정보등을 가진다.
 * 특정 대시보드에 속한 가젯 목록 
 * </pre>
 * @author YeonKyoung Park(goodjob)
 *
 */

@Entity
@Table(name="DASHBOARDGADGET", uniqueConstraints=@UniqueConstraint(columnNames={"dashboard_id","gridx","gridy","gadget_id"}))
public class DashboardGadget extends BaseObject implements JSONString {

	private static final long serialVersifonUID = 492349251269798738L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="DASHBOARDGADGET_SEQ")
	@SequenceGenerator(name="DASHBOARDGADGET_SEQ", sequenceName="DASHBOARDGADGET_SEQ", allocationSize=1) 
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="dashboard_id")
	@ReferencedBy(name="name")
	private Dashboard dashboard;
	
	@Column(name="dashboard_id", nullable=true, updatable=false, insertable=false)
	private Integer dashboardId;
	
	//@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="gadget_id")
	@ReferencedBy(name="name")
	private Gadget gadget;
	
	@Column(name="gadget_id", nullable=true, updatable=false, insertable=false)
	private Integer gadgetId;

	@ColumnInfo(name="gridX",descr="대시보드 그리드 안의 가젯의 위치 좌표 X")
	@Column(name="gridx",nullable=false)
	private Integer gridX;
	@ColumnInfo(name="gridY",descr="대시보드 그리드 안의 가젯의 위치 좌표 Y")
	@Column(name="gridy",nullable=false)
	private Integer gridY;
	
	private Boolean collapsible;
	private String layout;	
	
	public DashboardGadget() {
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@XmlTransient
	public Dashboard getDashboard() {
		return dashboard;
	}
	public void setDashboard(Dashboard dashboard) {
		this.dashboard = dashboard;
	}	

	@XmlTransient
	public Gadget getGadget() {
		return gadget;
	}
	public void setGadget(Gadget gadget) {
		this.gadget = gadget;
	}

	public Integer getGridX() {
		return gridX;
	}

	public void setGridX(Integer gridX) {
		this.gridX = gridX;
	}

	public Integer getGridY() {
		return gridY;
	}

	public void setGridY(Integer gridY) {
		this.gridY = gridY;
	}
	
	public Boolean isCollapsible() {
		return collapsible;
	}
	public void setCollapsible(Boolean collapsible) {
		this.collapsible = collapsible;
	}
	
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	
	public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Integer getGadgetId() {
        return gadgetId;
    }

    public void setGadgetId(Integer gadgetId) {
        this.gadgetId = gadgetId;
    }

    @Override
	public String toString()
	{
	    return "DashboardGadget "+toJSONString();
	}
	
    public String toJSONString() {
    	JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    		    	   .key("dashboard").value(((this.dashboard == null)? "null":dashboard.getName()))
    		    	   .key("gadget").value(((this.gadget == null)? "null":gadget.getName()))
    		    	   .key("gridX").value(((this.gridX == null)? "null":this.gridX))
    				   .key("gridY").value(((this.gridX == null)? "null":this.gridY))  
     				   .key("collapsible").value(((this.collapsible == null)? "null":this.collapsible)) 				   
    				   .key("layout").value(((this.layout == null)? "null":this.layout));  	

    		js.endObject();
    	} catch (Exception e) {
    		//log.warn(e,e);
    	}
    	return js.toString();
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
