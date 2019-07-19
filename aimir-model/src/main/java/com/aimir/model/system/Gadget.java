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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import com.aimir.model.BaseObject;

/**
 * <p>AIMIR System UserInterface의 가젯(Gadget)을 구성하는 정보</p><br>
 * 
 * Gadget은 Widget과 비슷한 개념으로 하나의 창으로 이루어진 서비스 UI 이며 독립된 단위로 존재한다.<br>
 * 가젯은 여러개의 대시보드에 속하며 하나의 대시보드가 동일한 가젯을 여러개 가질 수 있다.<br>
 * 해당 클래스에서는 가젯애 대한 URL 정보, 가젯 명, 가젯에 대한 설명, 가젯의 고유코드, 가젯의 크기 정보를 가진다.<br>
 * 또한 가젯을 분류할수 있는 태그 정보도 가진다.<br>
 * 시스템에서 관리할 가젯정보 <br>
 * 공급사와 상관없이 모든 가젯을 관리할 수 있어야 한다. <br>
 * 가젯은 Role별로 존재한다. <br>
 * 
 * @author YeonKyoung Park(goodjob)
 *
 */
@Entity
@Table(name="GADGET")
public class Gadget extends BaseObject implements JSONString{

	private static final long serialVersionUID = -1268818013697105032L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,  generator="GADGET_SEQ")
	@SequenceGenerator(name="GADGET_SEQ", sequenceName="GADGET_SEQ", allocationSize=1)
	private Integer id;
	
	@Column(unique=true, nullable=false)
	private String name;
	
	private String descr;
	private String miniUrl;
	private String maxUrl;
	private String iconSrc;
	//2011. 03.29 kskim
	private String gadgetCode;
	
	@Column(columnDefinition="INTEGER default 350")
	private Integer miniHeight = 350;	
	
	@Column(columnDefinition="INTEGER default 800")
	private Integer fullHeight = 800;
	
	//@OneToMany(cascade=CascadeType.ALL)
	//@JoinColumn(name="gadget_id")
	//private Set<DashboardGadget> dashboardGadgets = new HashSet<DashboardGadget>(0);
	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinColumn(name="gadget_id")
	private Set<Tag> tags = new HashSet<Tag>(0);
	
	//2010.0208 김민수 추가  
	public Gadget() {		
	}
	public Gadget(Integer id) {
		this.id = id;
	}
	//end
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public String getMiniUrl() {
		return miniUrl;
	}
	public void setMiniUrl(String miniUrl) {
		this.miniUrl = miniUrl;
	}	
	public String getMaxUrl() {
		return maxUrl;
	}
	public void setMaxUrl(String maxUrl) {
		this.maxUrl = maxUrl;
	}	
	public String getIconSrc() {
		return iconSrc;
	}
	public void setIconSrc(String iconSrc) {
		this.iconSrc = iconSrc;
	}
	
	public Integer getMiniHeight() {
		return miniHeight;
	}

	public void setMiniHeight(Integer miniHeight) {
		this.miniHeight = miniHeight;
	}

	public Integer getFullHeight() {
		return fullHeight;
	}

	public void setFullHeight(Integer fullHeight) {
		this.fullHeight = fullHeight;
	}

	////2011. 03.29 kskim
	public void setGadgetCode(String gadgetCode){
		this.gadgetCode = gadgetCode;
	}
	public String getGadgetCode(){
		return gadgetCode;
	}
	
	//public Set<DashboardGadget> getDashboardGadgets() {
	//	return dashboardGadgets;
	//}
	//public void setDashboardGadgets(Set<DashboardGadget> dashboardGadgets) {
	//	this.dashboardGadgets = dashboardGadgets;
	//}
	@XmlTransient
	public Set<Tag> getTags() {
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}	
	
	@Override
	public String toString()
	{
	    return "Gadget "+toJSONString();
	}
	
    public String toJSONString() {
    	JSONStringer js = null;

    	try {
    		js = new JSONStringer();
    		js.object().key("id").value(this.id)
    		    	   .key("name").value(((this.name == null)? "":this.name))
    		    	   .key("descr").value(((this.descr == null)? "":this.descr))
    		    	   .key("miniUrl").value(((this.miniUrl == null)? "":this.miniUrl))
    				   .key("maxUrl").value(((this.maxUrl == null)? "":this.maxUrl))  
     				   .key("iconSrc").value(((this.iconSrc == null)? "":this.iconSrc)) 				   
    				   .key("miniHeight").value(((this.miniHeight == null)? "null":this.miniHeight))
    				   .key("fullHeight").value(((this.fullHeight == null)? "null":this.fullHeight))
    				   .key("gadgetCode").value(((this.gadgetCode == null)? "null":this.gadgetCode));  	

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
