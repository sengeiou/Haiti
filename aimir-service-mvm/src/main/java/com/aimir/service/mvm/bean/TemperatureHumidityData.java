package com.aimir.service.mvm.bean;

import org.apache.commons.lang.StringUtils;

import net.sf.json.JSONSerializer;
import net.sf.json.JSONString;
import net.sf.json.util.JSONStringer;

public class TemperatureHumidityData implements JSONString {

	String[] label = null;

	String tempMax = null;
	String tempMin = null;
	String huMax = null;
	String huMin = null;
	String[] tempMaxvalue = null;
	String[] tempMinvalue = null;
	String[] huMaxvalue = null;
	String[] huMinvalue = null;

	String[] outerTempMaxvalue = null;
	String[] outerTempMinvalue = null;
	String[] outerHuMaxvalue = null;
	String[] outerHuMinvalue = null;
	String searchDateType = "";
	public static final int inner = 0;
	public static final int outer = 1;

	public TemperatureHumidityData(String searchDateType) {
		this.searchDateType = searchDateType;
	}

	public void setTempMax(String tempMax) {

		this.tempMax = tempMax;
	}

	public void setTempMin(String tempMin) {

		this.tempMin = tempMin;
	}

	public void setHuMax(String huMax) {

		this.huMax = huMax;
	}

	public void setHuMin(String huMin) {

		this.huMin = huMin;
	}

	public void setLabel(String[] label) {

		this.label = label;
	}

	public void setTempMaxvalue(String[] tempMaxvalue) {

		this.tempMaxvalue = tempMaxvalue;
	}

	public void setTempMinvalue(String[] tempMinvalue) {

		this.tempMinvalue = tempMinvalue;
	}

	public void setHuMaxvalue(String[] huMaxvalue) {

		this.huMaxvalue = huMaxvalue;
	}

	public void setHuMinvalue(String[] huMinvalue) {

		this.huMinvalue = huMinvalue;
	}

	public void setOuterTempMaxvalue(String[] outerTempMaxvalue) {

		this.outerTempMaxvalue = outerTempMaxvalue;
	}

	public void setOuterTempMinvalue(String[] outerTempMinvalue) {

		this.outerTempMinvalue = outerTempMinvalue;
	}

	public void setOuterHuMaxvalue(String[] outerHuMaxvalue) {

		this.outerHuMaxvalue = outerHuMaxvalue;
	}

	public void setOuterHuMinvalue(String[] outerHuMinvalue) {

		this.outerHuMinvalue = outerHuMinvalue;
	}

	
	public String toString() {
		return "TemperatureHumidityData:" + toJSONString();
	}

	public String toJSONString() {
		JSONStringer js = null;

		try {
			js = new JSONStringer();
			js.object().key("searchDateType").value(this.searchDateType).key(
					"temperature").array();
			Double dTempMax = 10d;
			Double dTempMin = 0d;
			
			for (int i = 0; i < tempMaxvalue.length; i++) {
				
				String tMax = StringUtils.defaultIfEmpty(this.tempMaxvalue[i],
						"0");
				String tMin = StringUtils.defaultIfEmpty(this.tempMinvalue[i],
						"0");
				String otMax = StringUtils.defaultIfEmpty(
						this.outerTempMaxvalue[i], "0");
				String otMin = StringUtils.defaultIfEmpty(
						this.outerTempMinvalue[i], "0");
				Double dtMax = Double.parseDouble(tMax);
				Double dtMin = Double.parseDouble(tMin);
				Double dotMax = Double.parseDouble(otMax);
				Double dotMin = Double.parseDouble(otMin);
				if (dTempMax < dtMax) {
					dTempMax = dtMax;
				}

				if (dTempMax < dotMax) {
					dTempMax = dotMax;
				}

				if (dTempMin > dtMin) {

					dTempMin = dtMin;

				}
				if (dTempMin > dotMin) {

					dTempMin = dotMin;

				}
				js.value(JSONSerializer.toJSON("{label:'" + label[i]
						+ "',innerMax:'" + tMax + "',innerMin:'" + tMin
						+ "',outerMax:'" + otMax + "',outerMin:'" + otMin+"'}"

				));

			}
			js.endArray();
			js.key("tempMax").value(dTempMax + "").key("tempMin").value(
					dTempMin + "");
			js.key("humidity").array();
			Double dHuMax = 10d;
			Double dHuMin = 0d;
			for (int i = 0; i < huMaxvalue.length; i++) {
				
				String hMax = StringUtils.defaultIfEmpty(this.huMaxvalue[i],
						"0");
				String hMin = StringUtils.defaultIfEmpty(this.huMinvalue[i],
						"0");
				String ohMax = StringUtils.defaultIfEmpty(
						this.outerHuMaxvalue[i], "0");
				String ohMin = StringUtils.defaultIfEmpty(
						this.outerHuMaxvalue[i], "0");

				Double dhMax = Double.parseDouble(hMax);
				Double dhMin = Double.parseDouble(hMin);
				Double dohMax = Double.parseDouble(ohMax);
				Double dohMin = Double.parseDouble(ohMin);
				if (dHuMax < dhMax) {
					dHuMax = dhMax;
				}

				if (dHuMax < dohMax) {
					dHuMax = dohMax;
				}

				if (dHuMin > dhMin) {

					dHuMin = dhMin;

				}
				if (dHuMin > dohMin) {

					dHuMin = dohMin;

				}
				js.value(JSONSerializer.toJSON("{label:'" + label[i]
						+ "',innerMax:'" + hMax + "',innerMin:'" + hMin
						+ "',outerMax:'" + ohMax + "',outerMin:'" + ohMin+"'}"));
			}

			js.endArray();
			js.key("huMax").value(dHuMax + "").key("huMin").value(dHuMin + "");
			js.endObject();
		} catch (Exception e) {
			System.out.println(e);
		}
		return js.toString();
	}
}
