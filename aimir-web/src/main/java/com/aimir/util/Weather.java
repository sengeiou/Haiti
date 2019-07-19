package com.aimir.util;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

@XmlRootElement(name="wid")
@XmlAccessorType(XmlAccessType.FIELD)
public class Weather implements JSONString {
	
	@XmlRootElement(name="data")
	static class Data implements JSONString {
		private Integer seq;
		private Integer hour;
		private Integer day;
		private Double temp;
		private Double tmx;
		private Double tmn;
		private Integer sky;
		private Integer pty;
		private String wfKor;
		private String wfEn;
		private Double pop;
		private Double r12;
		private Double s12;
		private Double ws;
		private Double wd;
		private String wdKor;
		private String wdEn;
		private Double reh;
		
		public Integer getSeq() {
			return seq;
		}
		public void setSeq(Integer seq) {
			this.seq = seq;
		}
		public Integer getHour() {
			return hour;
		}
		public void setHour(Integer hour) {
			this.hour = hour;
		}
		public Integer getDay() {
			return day;
		}
		public void setDay(Integer day) {
			this.day = day;
		}
		public Double getTemp() {
			return temp;
		}
		public void setTemp(Double temp) {
			this.temp = temp;
		}
		public Double getTmx() {
			return tmx;
		}
		public void setTmx(Double tmx) {
			this.tmx = tmx;
		}
		public Double getTmn() {
			return tmn;
		}
		public void setTmn(Double tmn) {
			this.tmn = tmn;
		}
		public Integer getSky() {
			return sky;
		}
		public void setSky(Integer sky) {
			this.sky = sky;
		}
		public Integer getPty() {
			return pty;
		}
		public void setPty(Integer pty) {
			this.pty = pty;
		}
		public String getWfKor() {
			return wfKor;
		}
		public void setWfKor(String wfKor) {
			this.wfKor = wfKor;
		}
		public String getWfEn() {
			return wfEn;
		}
		public void setWfEn(String wfEn) {
			this.wfEn = wfEn;
		}
		public Double getPop() {
			return pop;
		}
		public void setPop(Double pop) {
			this.pop = pop;
		}
		public Double getR12() {
			return r12;
		}
		public void setR12(Double r12) {
			this.r12 = r12;
		}
		public Double getS12() {
			return s12;
		}
		public void setS12(Double s12) {
			this.s12 = s12;
		}
		public Double getWs() {
			return ws;
		}
		public void setWs(Double ws) {
			this.ws = ws;
		}
		public Double getWd() {
			return wd;
		}
		public void setWd(Double wd) {
			this.wd = wd;
		}
		public String getWdKor() {
			return wdKor;
		}
		public void setWdKor(String wdKor) {
			this.wdKor = wdKor;
		}
		public String getWdEn() {
			return wdEn;
		}
		public void setWdEn(String wdEn) {
			this.wdEn = wdEn;
		}
		public Double getReh() {
			return reh;
		}
		public void setReh(Double reh) {
			this.reh = reh;
		}
		@Override
		public String toJSONString() {
			JSONStringer js = new JSONStringer();
    		js.object()
    			.key("seq").value(this.seq)
    			.key("hour").value(this.hour)
    			.key("day").value(this.day)
    			.key("tmx").value(this.tmx)
    			.key("tmn").value(this.tmn)
    			.key("sky").value(this.sky)
    			.key("pty").value(this.pty)
    			.key("wfKor").value(this.wfKor)
    			.key("wfEn").value(this.wfEn)
    			.key("pop").value(this.pop)
    			.key("r12").value(this.r12)
    			.key("s12").value(this.s12)
    			.key("ws").value(this.ws)
    			.key("wd").value(this.wd)
    			.key("wdKor").value(this.wdKor)
    			.key("wdEn").value(this.wdEn)
    			.key("reh").value(this.reh)
    			.endObject();
	    	return js.toString();
		}
	}
	
	private String tm;
	private Integer ts;
	private Integer x;
	private Integer y;
	
	private List<Weather.Data> data = new ArrayList<Weather.Data>();

	public String getTm() {
		return tm;
	}

	public void setTm(String tm) {
		this.tm = tm;
	}

	public Integer getTs() {
		return ts;
	}

	public void setTs(Integer ts) {
		this.ts = ts;
	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public List<Weather.Data> getData() {
		return data;
	}

	public void setData(List<Weather.Data> data) {
		this.data = data;
	}

	@Override
	public String toJSONString() {
		JSONStringer js = new JSONStringer();
	
		js.object()
			.key("tm").value(this.tm)
			.key("ts").value(this.ts)
			.key("x").value(this.x)
			.key("y").value(this.y);
		
		js.key("data").array();
		int size = data.size();
		for(int i = 0; i < size; i++) {
			js.value(data.get(i));
		}
		js.endArray();
		
		return js.endObject().toString();
	}
}
