package com.aimir.fep.meter.parser.plc;

import java.io.ByteArrayOutputStream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aimir.fep.util.DataUtil;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * @author kaze
 * <pre>
 * &lt;complexType name="plcDataFrame">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="command" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="DId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="data" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="errCode" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="protocolDirection" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="protocolVersion" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *         &lt;element name="SId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="sof" type="{http://www.w3.org/2001/XMLSchema}byte"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "plcDataFrame", propOrder = {
    "command",
    "dId",
    "data",
    "errCode",
    "length",
    "protocolDirection",
    "protocolVersion",
    "sId",
    "sof"
})
public class PLCDataFrame implements java.io.Serializable{
	private static Log log = LogFactory.getLog(PLCDataFrame.class);

	private byte sof;
	private byte protocolDirection; // protocol direction
	private byte protocolVersion; // protocol version
	@XmlElement(name = "DId")
	private String dId; // destination id
	@XmlElement(name = "SId")
	private String sId; // source id
	private int length; // data length(command ~ crc)
	private byte command; // operation command
	private byte[] data;
	private byte errCode;

	/**
	 * Copy Constructor
	 *
	 * @param pLCDataFrame a <code>PLCDataFrame</code> object
	 */
	public PLCDataFrame(PLCDataFrame pLCDataFrame)
	{
	    this.sof = pLCDataFrame.sof;
	    this.protocolDirection = pLCDataFrame.protocolDirection;
	    this.protocolVersion = pLCDataFrame.protocolVersion;
	    this.dId = pLCDataFrame.dId;
	    this.sId = pLCDataFrame.sId;
	    this.length = pLCDataFrame.length;
	    this.command = pLCDataFrame.command;
	    this.data = pLCDataFrame.data;
	    this.errCode = pLCDataFrame.errCode;
	}




	public PLCDataFrame() {

	}

	/**
	 * @param sof
	 * @param protocolDirection
	 * @param protocolVersion
	 * @param id
	 * @param id2
	 * @param length
	 * @param command
	 */
	public PLCDataFrame(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, byte command) {
		this.sof = sof;
		this.protocolDirection = protocolDirection;
		this.protocolVersion = protocolVersion;
		this.dId = dId;
		this.sId = sId;
		this.length = length;
		this.command = command;
	}

	/**
	 * @param sof
	 * @param protocolDirection
	 * @param protocolVersion
	 * @param dId
	 * @param sId
	 * @param length
	 * @param command
	 * @param data
	 */
	public PLCDataFrame(byte sof, byte protocolDirection, byte protocolVersion, String dId, String sId, int length, byte command, byte[] data) {
		this.sof = sof;
		this.protocolDirection = protocolDirection;
		this.protocolVersion = protocolVersion;
		this.dId = dId;
		this.sId = sId;
		this.length = length;
		this.command = command;
		this.data = data;
	}

	/**
	 * Create Nak PLC Data Frame
	 *
	 * @param frame
	 * @param errCode
	 */
	public PLCDataFrame(PLCDataFrame frame, byte errCode) {
		this.sof = PLCDataConstants.SOF;
		this.protocolDirection = PLCDataConstants.PROTOCOL_DIRECTION_FEP_IRM;
		this.protocolVersion = frame.getProtocolVersion();
		// TODO dId와 sId를 바꿔 보내는게 맞을까?
		this.dId = frame.getSId();
		this.sId = frame.getDId();
		this.length = PLCDataConstants.NAK_LEN;
		this.command = PLCDataConstants.COMMAND_a;
		this.data = new byte[] { errCode };
	}

	public byte getSof() {
		return sof;
	}

	public void setSof(byte sof) {
		this.sof = sof;
	}

	public byte getProtocolDirection() {
		return protocolDirection;
	}

	public void setProtocolDirection(byte protocolDirection) {
		this.protocolDirection = protocolDirection;
	}

	public byte getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(byte protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public String getDId() {
		return dId;
	}

	public void setDId(String id) {
		dId = id;
	}

	public String getSId() {
		return sId;
	}

	public void setSId(String id) {
		sId = id;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public byte getCommand() {
		return command;
	}

	public void setCommand(byte command) {
		this.command = command;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte getErrCode() {
		return errCode;
	}

	public void setErrCode(byte errCode) {
		this.errCode = errCode;
	}

	public static PLCDataFrame decode(IoBuffer bytebuffer) throws Exception {
		PLCDataFrame frame = new PLCDataFrame();
		try {
			byte[] sof = new byte[PLCDataConstants.SOF_LEN];
			byte[] protocolDirection = new byte[PLCDataConstants.DIR_LEN]; // protocol direction
			byte[] protocolVersion = new byte[PLCDataConstants.VER_LEN]; // protocol version
			byte[] dId = new byte[PLCDataConstants.DID_LEN]; // destination id
			byte[] sId = new byte[PLCDataConstants.SID_LEN]; // source id
			byte[] length = new byte[PLCDataConstants.LENGTH_LEN]; // data length(command ~ crc)
			byte[] command = new byte[PLCDataConstants.COMMAND_LEN]; // operation command
			byte[] data;

			if (bytebuffer.get(0) != PLCDataConstants.SOF) {
				log.error("PLCDataFrame SOF[" + bytebuffer.get(1) + "] IS INVALID!");
			}
			else {
				bytebuffer.get(sof, 0, sof.length);

				bytebuffer.get(protocolDirection, 0, protocolDirection.length);

				bytebuffer.get(protocolVersion, 0, protocolVersion.length);

				bytebuffer.get(dId, 0, dId.length);
				DataUtil.convertEndian(PLCDataConstants.isConvert, dId);

				bytebuffer.get(sId, 0, sId.length);
				DataUtil.convertEndian(PLCDataConstants.isConvert, sId);

				bytebuffer.get(length, 0, length.length);
				DataUtil.convertEndian(!PLCDataConstants.isConvert, length);
				//log.debug("Length "+DataUtil.getIntTo2Byte(length));

				bytebuffer.get(command, 0, command.length);

				data = new byte[DataUtil.getIntTo2Byte(length) - PLCDataConstants.COMMAND_LEN - PLCDataConstants.CRC_LEN];
				bytebuffer.get(data, 0, data.length);
				DataUtil.convertEndian(PLCDataConstants.isConvert, data);

				frame.setSof(sof[0]);
				frame.setProtocolDirection(protocolDirection[0]);
				frame.setProtocolVersion(protocolVersion[0]);
				frame.setDId(new String(dId).trim());
				frame.setSId(new String(sId).trim());
				frame.setLength(DataUtil.getIntTo2Byte(length));
				frame.setCommand(command[0]);
				frame.setData(data);
			}
			return frame;
		}
		catch (Exception ex) {
			log.error("PLCDataFrame failed : ", ex);
		}
		return frame;
	}

	public byte[] encode() {
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		try {
			bao.write(new byte[]{sof}, 0, PLCDataConstants.SOF_LEN);
			bao.write(new byte[]{protocolDirection}, 0, PLCDataConstants.DIR_LEN);
			bao.write(new byte[]{protocolVersion}, 0, PLCDataConstants.VER_LEN);

			if(dId!=null) {
			    log.debug("DID: "+dId);
			}else {
			    log.debug("DID IS NULL");
			}
			bao.write(DataUtil.getFixedLengthByte(dId!=null ? dId:"", PLCDataConstants.DID_LEN));
			bao.write(DataUtil.getFixedLengthByte(sId!=null ? sId:"", PLCDataConstants.SID_LEN));
			byte convertLength[]=DataUtil.get2ByteToInt(length);
			DataUtil.reverse(convertLength);
			bao.write(convertLength, 0, PLCDataConstants.LENGTH_LEN);
			bao.write(new byte[]{command}, 0, PLCDataConstants.COMMAND_LEN);
		if(data!=null) {
			bao.write(data,0,data.length);
		}
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return bao.toByteArray();

	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append("PLCDataFrame ( ")
	        .append(super.toString()).append(TAB)
	        .append("sof = ").append(this.sof).append(TAB)
	        .append("protocolDirection = ").append(this.protocolDirection).append(TAB)
	        .append("protocolVersion = ").append(this.protocolVersion).append(TAB)
	        .append("dId = ").append(this.dId).append(TAB)
	        .append("sId = ").append(this.sId).append(TAB)
	        .append("length = ").append(this.length).append(TAB)
	        .append("command = ").append(this.command).append(TAB)
	        .append("data = ").append(this.data).append(TAB)
	        .append("errCode = ").append(this.errCode).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}
