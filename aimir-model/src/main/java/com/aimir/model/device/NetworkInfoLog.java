package com.aimir.model.device;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aimir.model.BaseObject;
import com.aimir.util.DateTimeUtil;

import net.sf.json.JSONString;

/**
 * POC의 핑 테스트와 네트워크 정보를 기록한다.
 *  
 *  @author 박종성
* <pre>
 * &lt;complexType name="networkInfoLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="antAttenuation" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="hops" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="loss" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="rtt" type="{http://www.w3.org/2001/XMLSchema}rtt" minOccurs="0"/>
 *         &lt;element name="ttl" type="{http://www.w3.org/2001/XMLSchema}ttl" minOccurs="0"/>
 *         &lt;element name="packetSize" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="rssi" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="linkBudget" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="txPower" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="temperature" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="weather" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="coapResponseTime" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="obisCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obisResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obisValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="obisResponseTime" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="ipAddr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "networkInfoLog", propOrder = {
    "id",
    "antAttenuation",
    "hops",
    "loss",
    "rtt",
    "ttl",
    "packetSize",
    "rssi",
    "lqi",
    "etx",
    "cpuUsage",
    "memoryUsage",
    "totalTxSize",
    "linkBudget",
    "txPower",
    "temperature",
    "weather",
    "coapResponseTime",
    "obisCode",
    "obisResult",
    "obisValue",
    "obisResponseTime",
    "ipAddr",
    "imei",
    "imsi",
    "cellId",
    "frequency",
    "mtClock",
    "mobileNetworkType",
    "currentNetworkStatus",
    "lastConnectionStatus"
})
@Entity
@Table(name="NETWORKINFOLOG")
public class NetworkInfoLog extends BaseObject implements JSONString{

	private static final long serialVersionUID = 4218162564823781897L;

	@EmbeddedId public NetworkInfoLogPk id;
	
	@Column
	private Double antAttenuation;
	
	@Column
	private String hops;
	
	@Column
	private Double loss;
	
	@Column
	private Double rtt;
	
	@Column
	private Double ttl;
	
	@Column
	private Integer packetSize;
	
	@Column
	private Double rssi;
	
	@Column
	private Double lqi;
	
	@Column
	private Double etx;

    @Column
    private Integer cpuUsage;

    @Column
    private Integer memoryUsage;
    
    @Column
    private String totalTxSize;
	
	@Column
	private Double linkBudget;
	
	@Column
	private Double txPower;
	
	@Column
	private Double temperature;
	
	@Column
	private String weather;
	
	@Column
	private Double coapResponseTime;
	
	@Column
	private String obisCode;
	
	@Column
	private String obisResult;
	
	@Column
	private String obisValue;
	
	@Column
	private Double obisResponseTime;
	
	@Column
	private String ipAddr;
	
	@Column
	private String imei;	
	
	@Column
	private String imsi;
	
	@Column
	private String cellId;
	
	@Column
	private String frequency;
	
	@Column
	private String mtClock;
	
	@Column
    private String mobileNetworkType;
	
	@Column
	private String currentNetworkStatus;

	@Column
	private String lastConnectionStatus;

	public NetworkInfoLog() {
	    this.id = new NetworkInfoLogPk();
	}
	
	public NetworkInfoLogPk getId() {
        return id;
    }
    public void setId(NetworkInfoLogPk id) {
        this.id = id;
    }
    public Double getAntAttenuation() {
        return antAttenuation;
    }
    public void setAntAttenuation(Double antAttenuation) {
        this.antAttenuation = antAttenuation;
    }
    public String getHops() {
        return hops;
    }
    public void setHops(String hops) {
        this.hops = hops;
    }
    public Double getLoss() {
        return loss;
    }
    public void setLoss(Double loss) {
        this.loss = loss;
    }
    public Double getRtt() {
        return rtt;
    }
    public void setRtt(Double rtt) {
        this.rtt = rtt;
    }
    public Double getRssi() {
        return rssi;
    }
    public void setRssi(Double rssi) {
        this.rssi = rssi;
    }
    public Double getLinkBudget() {
        return linkBudget;
    }
    public void setLinkBudget(Double linkBudget) {
        this.linkBudget = linkBudget;
    }
    public Double getTxPower() {
        return txPower;
    }
    public void setTxPower(Double txPower) {
        this.txPower = txPower;
    }
    public Double getTemperature() {
        return temperature;
    }
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    public String getWeather() {
        return weather;
    }
    public void setWeather(String weather) {
        this.weather = weather;
    }
    public String getTargetNode() {
        return this.id.getTargetNode();
    }
    public void setTargetNode(String targetNode) {
        this.id.setTargetNode(targetNode);
    }
    public String getDateTime() {
        return this.id.getDateTime();
    }
    public void setDateTime(String dateTime) {
        this.id.setDateTime(dateTime);
    }
    public String getCommand() {
        return this.id.getCommand();
    }
    public void setCommand(String command) {
        this.id.setCommand(command);
    }
    
    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Double getTtl() {
        return ttl;
    }

    public void setTtl(Double ttl) {
        this.ttl = ttl;
    }

    public Integer getPacketSize() {
        return packetSize;
    }

    public void setPacketSize(Integer packetSize) {
        this.packetSize = packetSize;
    }

    public String getObisCode() {
        return obisCode;
    }

    public void setObisCode(String obisCode) {
        this.obisCode = obisCode;
    }

    public String getObisResult() {
        return obisResult;
    }

    public void setObisResult(String obisResult) {
        this.obisResult = obisResult;
    }

    public String getObisValue() {
        return obisValue;
    }

    public void setObisValue(String obisValue) {
        this.obisValue = obisValue;
    }

    public Double getCoapResponseTime() {
        return coapResponseTime;
    }

    public void setCoapResponseTime(Double coapResponseTime) {
        this.coapResponseTime = coapResponseTime;
    }

    public Double getObisResponseTime() {
        return obisResponseTime;
    }

    public void setObisResponseTime(Double obisResponseTime) {
        this.obisResponseTime = obisResponseTime;
    }

    public Double getLqi() {
		return lqi;
	}

	public void setLqi(Double lqi) {
		this.lqi = lqi;
	}

	public Double getEtx() {
		return etx;
	}

	public void setEtx(Double etx) {
		this.etx = etx;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getImsi() {
		return imsi;
	}

	public void setImsi(String imsi) {
		this.imsi = imsi;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getMtClock() {
		return mtClock;
	}

	public void setMtClock(String mtClock) {
		this.mtClock = mtClock;
	}

	public String getMobileNetworkType() {
		return mobileNetworkType;
	}

	public void setMobileNetworkType(String mobileNetworkType) {
		this.mobileNetworkType = mobileNetworkType;
	}

	public Integer getCpuUsage() {
		return cpuUsage;
	}

	public void setCpuUsage(Integer cpuUsage) {
		this.cpuUsage = cpuUsage;
	}

	public Integer getMemoryUsage() {
		return memoryUsage;
	}

	public void setMemoryUsage(Integer memoryUsage) {
		this.memoryUsage = memoryUsage;
	}

	public String getTotalTxSize() {
		return totalTxSize;
	}

	public void setTotalTxSize(String totalTxSize) {
		this.totalTxSize = totalTxSize;
	}

	public String getCurrentNetworkStatus() {
		return currentNetworkStatus;
	}

	public void setCurrentNetworkStatus(String currentNetworkStatus) {
		this.currentNetworkStatus = currentNetworkStatus;
	}

	public String getLastConnectionStatus() {
		return lastConnectionStatus;
	}

	public void setLastConnectionStatus(String lastConnectionStatus) {
		this.lastConnectionStatus = lastConnectionStatus;
	}

	public String toJSONString() {

		String retValue = "";
		String dateTime = getDateTime();

		SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy HH:mm:ss");
		try {
		    dateTime = sdf.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(dateTime));
		    
		    if(mtClock != null && !"".equals(mtClock) && mtClock.length() == 14) {
		    	 mtClock = sdf.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(mtClock));
		    }		   
		}
		catch (ParseException e) {}
		
	    retValue = "{"
	        + "\"targetNode\":\"" + getTargetNode() 
	        + "\",\"dateTime\":\"" + dateTime 
	        + "\",\"command\":\"" + getCommand() 
		    + "\",\"antAttenuation\":\"" + this.antAttenuation
		    + "\",\"hops\":\"" + this.hops
		    + "\",\"loss\":\"" + this.loss
		    + "\",\"rssi\":\"" + this.rssi
		    + "\",\"rtt\":\"" + this.rtt
		    + "\",\"ttl\":\"" + this.ttl
		    + "\",\"packetSize\":\"" + this.packetSize
		    + "\",\"rssi\":\""+this.rssi
		    + "\",\"lqi\":\"" + this.lqi
		    + "\",\"etx\":\"" + this.etx
		    + "\",\"cpuUsage\":\"" + this.cpuUsage
		    + "\",\"memoryUsage\":\"" + this.memoryUsage
		    + "\",\"totalTxSize\":\"" + this.totalTxSize
		    + "\",\"txPower\":\"" + this.txPower
		    + "\",\"temperature\":\"" + this.temperature
		    + "\",\"weather\":\"" + this.weather
		    + "\",\"linkBudget\":\"" + this.linkBudget
		    + "\",\"coapResponseTime\":\"" + this.coapResponseTime
		    + "\",\"obisCode\":\"" + this.obisCode
		    + "\",\"obisResult\":\"" + this.obisResult
		    + "\",\"obisValue\":\"" + this.obisValue
		    + "\",\"obisResponseTime\":\"" + this.obisResponseTime
		    + "\",\"ipAddr\":\"" + this.ipAddr
		    + "\",\"imei\":\"" + this.imei
		    + "\",\"imsi\":\"" + this.imsi
		    + "\",\"cellId\":\"" + this.cellId 
		    + "\",\"frequency\":\"" + this.frequency
		    + "\",\"mtClock\":\"" + this.mtClock
		    + "\",\"mobileNetworkType\":\"" + this.mobileNetworkType
		    + "\",\"currentNetworkStatus\":\"" + this.currentNetworkStatus
		    + "\",\"lastConnectionStatus\":\"" + this.lastConnectionStatus
		    + "\"}";

	    return retValue;
	}
    public String toString() {
        return toJSONString();
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
