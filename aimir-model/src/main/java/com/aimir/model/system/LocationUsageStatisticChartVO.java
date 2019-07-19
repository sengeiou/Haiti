package com.aimir.model.system;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

import org.apache.commons.lang.StringUtils;

public class LocationUsageStatisticChartVO implements JSONString {

	String[] date = null;
	String[] emTotal = null;
	String[] gmTotal = null;
	String[] wmTotal = null;
	String[] total = null;
	
	String[] preDate = null;
	String[] preEmTotal = null;
	String[] preGmTotal = null;
	String[] preWmTotal = null;
	String[] preTotal = null;

	String[] emCo2 = null;
	String[] gmCo2 = null;
	String[] wmCo2 = null;
	
	String[] preEmCo2 = null;
	String[] preGmCo2 = null;
	String[] preWmCo2 = null;
	
	String searchDateType="";
	
	public void setSearchDateType(String searchDateType){
		this.searchDateType = searchDateType;
	}
	public void setDate(String[] date){
		this.date = date;
	}
	public void setEmTotal(String[] emTotal){
		this.emTotal = emTotal;
	}
	public void setGmTotal(String[] gmTotal){
		this.gmTotal = gmTotal;
	}
	
	public void setWmTotal(String[] wmTotal){
		this.wmTotal = wmTotal;
	}
	public void setEmCo2(String[] emCo2){
		this.emCo2 = emCo2;
	}
	public void setGmCo2(String[] gmCo2){
		this.gmCo2 = gmCo2;
	}
	
	public void setWmCo2(String[] wmCo2){
		this.wmCo2 = wmCo2;
	}
	public void setPreEmCo2(String[] preEmCo2){
		this.preEmCo2 = preEmCo2;
	}
	public void setPreGmCo2(String[] preGmCo2){
		this.preGmCo2 = preGmCo2;
	}
	
	public void setPreWmCo2(String[] preWmCo2){
		this.preWmCo2 = preWmCo2;
	}
	
	public void setPreDate(String[] preDate){
		this.preDate = preDate;
	}
	public void setPreEmTotal(String[] preEmTotal){
		this.preEmTotal = preEmTotal;
	}
	public void setPreGmTotal(String[] preGmTotal){
		this.preGmTotal = preGmTotal;
	}
	
	public void setPreWmTotal(String[] preWmTotal){
		this.preWmTotal = preWmTotal;
	}
	
	public Double getCo2Total(int i){
		Double ec =Double.parseDouble(emCo2[i]);
		Double gc =Double.parseDouble(gmCo2[i]);
		Double wc =Double.parseDouble(wmCo2[i]);
		
		return ec+gc+wc;
	}
	
	public Double getPreCo2Total(int i){
		Double ec =Double.parseDouble(preEmCo2[i]);
		Double gc =Double.parseDouble(preGmCo2[i]);
		Double wc =Double.parseDouble(preWmCo2[i]);
		
		return ec+gc+wc;
	}
	public String toString() {
		return "LocationUsageStatisticChartVO:" + toJSONString();
	}
	public String toJSONString() {
		JSONStringer js = null;

		try {
			js = new JSONStringer();
			js.object().key("searchDateType").value(this.searchDateType).key("usage").array();
			Double usageMax = 10d;
			Double usageMin = 0d;
			System.out.println("emTotal.length:"+emTotal.length);
			for (int i = 0; i < emTotal.length; i++) {
				System.out.println("date["+i+"]:"+date[i]);
				System.out.println("emTotal["+i+"]:"+emTotal[i]);
				System.out.println("gmTotal["+i+"]:"+gmTotal[i]);
				System.out.println("wmTotal["+i+"]:"+wmTotal[i]);
				System.out.println("preEmTotal["+i+"]:"+preEmTotal[i]);
				System.out.println("preGmTotal["+i+"]:"+preGmTotal[i]);
				System.out.println("preWmTotal["+i+"]:"+preWmTotal[i]);
				js.value(JSONSerializer.toJSON("{date:'" + date[i] + "',em:'"
						+ StringUtils.defaultIfEmpty(emTotal[i], "0") + "',gm:'"
						+ StringUtils.defaultIfEmpty(gmTotal[i], "0")
						+ "',wm:'"
						+ StringUtils.defaultIfEmpty(wmTotal[i], "0")+ "',preem:'"
						+ StringUtils.defaultIfEmpty(preEmTotal[i], "0") + "',pregm:'"
						+ StringUtils.defaultIfEmpty(preGmTotal[i], "0")
						+ "',prewm:'"
						+ StringUtils.defaultIfEmpty(preWmTotal[i], "0")+"'}"));
				
				Double em = Double.parseDouble(emTotal[i]);
				Double gm = Double.parseDouble(gmTotal[i]);
				Double wm = Double.parseDouble(wmTotal[i]);
				Double pem = Double.parseDouble(preEmTotal[i]);
				Double pgm = Double.parseDouble(preGmTotal[i]);
				Double pwm = Double.parseDouble(preWmTotal[i]);
				if(usageMax<em){
					usageMax =em;
				}
				if(usageMax<gm){
					usageMax =gm;
				}
				if(usageMax<wm){
					usageMax =wm;
				}
				
				if(usageMin>em){
					usageMin =em;
				}
				if(usageMin>gm){
					usageMin =gm;
				}
				if(usageMin>wm){
					usageMin =wm;
				}
				
				if(usageMax<pem){
					usageMax =pem;
				}
				if(usageMax<pgm){
					usageMax =pgm;
				}
				if(usageMax<pwm){
					usageMax =pwm;
				}
				
				if(usageMin>pem){
					usageMin =pem;
				}
				if(usageMin>pgm){
					usageMin =pgm;
				}
				if(usageMin>pwm){
					usageMin =pwm;
				}
			}

			js.endArray();
			js.key("co2").array();
			Double co2Max = 10d;
			Double co2Min = 0d;
			System.out.println("emCo2.length:"+emCo2.length);
			for (int i = 0; i < emCo2.length; i++) {
				System.out.println("emCo2["+i+"]:"+emCo2[i]);
				Double c= getCo2Total(i);
				Double p= getPreCo2Total(i);
				js.value(JSONSerializer.toJSON("{date:'" + date[i] + "',co2:'"
						+ c+"',preCo2:'"
						+ p+"'}"));
				if(co2Max <c){
					co2Max=c;
				}
				
				if(co2Max <p){
					co2Max=p;
				}
				if(co2Min >p){
					co2Min=p;
				}
				
				if(co2Min >c){
					co2Min=c;
				}
			}
			js.endArray();
			
			js.key("usageMax").value(usageMax).key("usageMin")
			.value(usageMin).key("co2Max").value(co2Max).key(
					"co2Min").value(co2Min);
			js.endObject();
		} catch (Exception e) {
			System.out.println(e);
		}
		return js.toString();
	}

}
