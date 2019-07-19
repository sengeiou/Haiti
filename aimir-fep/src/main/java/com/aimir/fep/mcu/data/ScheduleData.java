package com.aimir.fep.mcu.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * MCU Schedule Data
 * @author TEN
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheduleData", propOrder = {
    "map",
    "sysId"
})
public class ScheduleData implements java.io.Serializable {

	/**
	 * auto generated
	 */
	private static final long serialVersionUID = -7011033348994857102L;
	
	private String sysId = null;
	@XmlElement(required = true)
    private Map map = null;
	
	public ScheduleData() {
		map = new Map();
	}
	
	/**
	 * @param _mcuId MCU.id (integer)
	 * @param _sysId MCU.sysId (string)
	 */
	public ScheduleData(String _sysId) {
		this.sysId = _sysId;
		map = new Map();
	}
	
	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public Map getMap() {
		return map;
	}

	public void setMap(Map map) {
		this.map = map;
	}
	
	public void setDataToMap(java.util.Map<String, Object> _single) {
		for (Iterator i = _single.keySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = new Map.Entry();
            entry.setKey(i.next());
            entry.setValue(_single.get(entry.getKey()));
            this.map.getEntry().add(entry);
        }
	}
	

	
	@Override
	public String toString() {
		return "ScheduleData [sysId=" + sysId + ", map=" + map + "]";
	}


	@XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "entry"
    })
    public static class Map {

        protected List<ScheduleData.Map.Entry> entry;

        public List<ScheduleData.Map.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<ScheduleData.Map.Entry>();
            }
            return this.entry;
        }

        @Override
        public String toString() {
            return "Map [entry=" + entry + "]";
        }

        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "key",
            "value"
        })
        public static class Entry {

            protected Object key;
            protected Object value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setKey(Object value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link Object }
             *     
             */
            public Object getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link Object }
             *     
             */
            public void setValue(Object value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return "Entry [key=" + key + ", value=" + value + "]";
            }

        }
    }

}
