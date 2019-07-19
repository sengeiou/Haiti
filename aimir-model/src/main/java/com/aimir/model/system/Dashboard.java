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
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.annotation.ColumnInfo;
import com.aimir.annotation.ReferencedBy;
import com.aimir.model.BaseObject;


/**
 * <p>Copyright NuriTelecom Co.Ltd. since 2009</p>
 * <pre>
 * AIMIR System UserInterface의 메뉴(Tab)을 구성하는 대시보드 정보
 * 대시보드 이름, 대시보드에 연관된 사용자 , 대시보드 표시 순서 , 대시보드별 가젯 최대 개수 및 레이아웃 정보를 가진다.
 * 대시 보드별 권한 정보도 가진다.
 *
 * 대시보드는 여러개의 가젯을 가질 수 있는 1:N 관계 이다.
 * </pre>
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="DASHBOARD")
public class Dashboard extends BaseObject implements JSONString {

	private static final long serialVersionUID = -2949675629892531040L;
	//private static Log log = LogFactory.getLog(Dashboard.class);

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="DASHBOARD_SEQ")
	@SequenceGenerator(name="DASHBOARD_SEQ", sequenceName="DASHBOARD_SEQ", allocationSize=1)
	private Integer id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="operator_id")
	@ReferencedBy(name="name")
	private Operator operator;
	
	@Column(name="operator_id", nullable=true, updatable=false, insertable=false)
	private Integer operatorId;
	
	@ColumnInfo(name="Dashboard name", descr="고객 대시보드 생성으로 인해 unique속성을 제거 하였다.")
	@Column(nullable=false)
	private String name;
	
	@ColumnInfo(name="orderNo", descr="탭(대시보드) 보여지는 순서")
	private Integer orderNo;
	
	@ColumnInfo(name="maxGridX", descr="대시보드의 그리드에서 최대 가로 가젯 수용 갯수")
	private Integer maxGridX;

	@ColumnInfo(name="maxGridY", descr="대시보드의 그리드에서 최대 세로 가젯 수용 갯수")
	private Integer maxGridY;
	
	private String descr;
	
	//@OneToMany
	//@Cascade(value={CascadeType.DELETE, CascadeType.SAVE_UPDATE})
	//@JoinColumn(name="dashboard_id", nullable=false, insertable=false, updatable=false)
	//private Set<DashboardGadget> dashboardGargets = new HashSet<DashboardGadget>(0);
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="dashboard_id")
	private Set<DashboardGadget> dashboardGargets = new HashSet<DashboardGadget>(0);
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="role_id")
	@ReferencedBy(name="name")
	private Role role;
	
	@Column(name="role_id", nullable=true, updatable=false, insertable=false)
	private Integer roleId;
	
	public Dashboard() {
	}
	
	public Dashboard(Integer id) {
		this.id = id;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@XmlTransient
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(Integer orderNo) {
		this.orderNo = orderNo;
	}
	
	public Integer getMaxGridX() {
		return maxGridX;
	}
	public void setMaxGridX(Integer maxGridX) {
		this.maxGridX = maxGridX;
	}
	public Integer getMaxGridY() {
		return maxGridY;
	}
	public void setMaxGridY(Integer maxGridY) {
		this.maxGridY = maxGridY;
	}
	
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	
	@XmlTransient
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	@XmlTransient
	public Set<DashboardGadget> getDashboardGargets() {
		return dashboardGargets;
	}
	public void setDashboardGargets(Set<DashboardGadget> dashboardGargets) {
		this.dashboardGargets = dashboardGargets;
	}
	
	//public void addGadget(Gadget gadget, int orderNo, int height, boolean collapsible, String layout) {
	//	DashboardGadget dashboardGadget = new DashboardGadget(this, gadget, orderNo, height, collapsible, layout);
	//	getDashboardGargets().add(dashboardGadget);
	//}
	
	public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString()
    {
        return "Dashboard " + toJSONString();
    }
	
    public String toJSONString() {
    	JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    		    	   .key("operator").value(((this.operator == null)? "null":operator.getName()))    		    	   
    				   .key("name").value(this.name)
    				   .key("orderNo").value(((this.orderNo == null)? "null":this.orderNo))
    				   .key("maxGridX").value(((this.maxGridX == null)? "null":this.maxGridX))
    				   .key("maxGridY").value(((this.maxGridX == null)? "null":this.maxGridY))   				   
    				   .key("descr").value(((this.descr == null)? "null":this.descr));  	

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
