package com.aimir.fep.protocol.nip.frame.payload;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class Firmware extends PayloadFrame {
	private int targetType;

	public enum UpgradeCommand {
		  UpgradeStartRequest((int) 3)
		, UpgradeStartResponse((int) 4)
		, UpgradeData((int) 5)
		, UpgradeEndRequest((int) 6)
		, UpgradeEndResponse((int) 7)
		, UpgradeImageInstallRequest((int) 8)
		, UpgradeImageInstallResponse((int) 9);
		private int code;

		UpgradeCommand(int code) {
			this.code = code;
		}

		public int getCode() {
			return this.code;
		}
	}

	public enum ImageCode {
		NoError((byte) 0x00), CRCFail((byte) 0x01), UnknownError((byte) 0xFF);

		private byte code;

		ImageCode(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}
	}

	private UpgradeCommand _upgradeCommand;
	//Upgrade Start Request / Response
	private String upgradeSequenceNumber;
	private int address;

	private int length;
	private byte[] data;

	private long imageLength;
	private String crc;

	private ImageCode _imageCode;
	
	// INSERT START SP-681
	private String fwVersion;
	private String fwModel;
	//private String installTime;  -> delete at NI Protocol 5.70
	// INSERT END SP-681
	
	private byte[] optionalDataCRC; // SP-1100

	public void setUpgradeCommand(int code) {
		for (UpgradeCommand f : UpgradeCommand.values()) {
			if (f.getCode() == code) {
				_upgradeCommand = f;
				break;
			}
		}
	}

	public void setImageCode(byte code) {
		for (ImageCode f : ImageCode.values()) {
			if (f.getCode() == code) {
				_imageCode = f;
				break;
			}
		}
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public UpgradeCommand get_upgradeCommand() {
		return _upgradeCommand;
	}

	public void set_upgradeCommand(UpgradeCommand _upgradeCommand) {
		this._upgradeCommand = _upgradeCommand;
	}

	public String getUpgradeSequenceNumber() {
		return upgradeSequenceNumber;
	}

	public void setUpgradeSequenceNumber(String upgradeSequenceNumber) {
		this.upgradeSequenceNumber = upgradeSequenceNumber;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void newData(int cnt) {
		this.data = new byte[cnt];
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public long getImageLength() {
		return imageLength;
	}

	public void setImageLength(long imageLength) {
		this.imageLength = imageLength;
	}

	public String getCrc() {
		return crc;
	}

	public void setCrc(String crc) {
		this.crc = crc;
	}

	public ImageCode get_imageCode() {
		return _imageCode;
	}

	public void set_imageCode(ImageCode _imageCode) {
		this._imageCode = _imageCode;
	}

	// INSERT START SP-681
	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String ver) {
		this.fwVersion = ver;
	}

	public String getFwModel() {
		return fwModel;
	}

	public void setFwModel(String model) {
		this.fwModel = model;
	}

	public byte[] getOptionalDataCRC() {
		return optionalDataCRC;
	}

	public void setOptionalDataCRC(byte[] optionalDataCRC) {
		this.optionalDataCRC = optionalDataCRC;
	}
	
// -> delete at NI Protocol 5.70
//	public String getInstallTime() {
//		return installTime;
//	}
//
//	public void setInstallTime(String time) {
//		this.installTime = time;
//	}	
	// INSERT END SP-681	



	@Override
	public void decode(byte[] bx) {
		int pos = 0;
		byte[] b = new byte[1];
		System.arraycopy(bx, pos, b, 0, b.length);
		pos += b.length;
		String frameOption = DataUtil.getBit(b[0]);
		setTargetType(Integer.parseInt(DataUtil.getBitToInt(frameOption.substring(0, 2), "%d")));
		setUpgradeCommand(Integer.parseInt(DataUtil.getBitToInt(frameOption.substring(4, 8), "%d")));

		switch (_upgradeCommand) {
		case UpgradeStartResponse:
			b = new byte[4];
			System.arraycopy(bx, pos, b, 0, b.length);

			if (Hex.decode(b).equals("00000000") || Hex.decode(b).equals("FFFFFFFF")) {
				setAddress(0);
			} else {
				setAddress(DataUtil.getIntTo4Byte(b));
			}

			break;
		case UpgradeEndResponse:
		case UpgradeImageInstallResponse:
			b = new byte[1];
			System.arraycopy(bx, pos, b, 0, b.length);
			setImageCode(b[0]);
			break;
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Firmware[");
		sb.append("targetType=" + targetType);
		sb.append(", upgradeCommand=" + _upgradeCommand);
		sb.append(", upgradeSequenceNumber=" + upgradeSequenceNumber);
		sb.append(", address=" + address);
		sb.append(", length=" + length);
		sb.append(", imageLength=" + imageLength);
		sb.append(", crc=" + crc);
		sb.append(", ImageCode=" + _imageCode);	
		
		// INSERT START SP-681
		sb.append(", FW Version=" + (fwVersion != null ? fwVersion  : ""));
		sb.append(", FW Model Name=" + (fwModel != null ? fwModel  : ""));
		//sb.append(", Install Time=" + (installTime != null ? installTime  : ""));  -> delete at NI Protocol 5.70
		// INSERT END SP-681
		
		sb.append(", Optional Data CRC=" + (optionalDataCRC != null ? Hex.decode(optionalDataCRC) : "")); // SP-1100
		
		sb.append(", data=" + (getData() != null ? getData().length + "byte" : ""));
		sb.append("]");
		
		return sb.toString();
//		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

	
	@Override
	public byte[] encode() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte frameOption = (byte) (DataUtil.getByteToInt(getTargetType()) | DataUtil.getByteToInt(_upgradeCommand.getCode()));
		out.write(frameOption);

		switch (_upgradeCommand) {
		case UpgradeStartRequest:
			
			/** Upgrade Sequence Number */
			String seqNumber = getUpgradeSequenceNumber();
			if ((seqNumber != null) && (seqNumber.length() > 2)) {
				byte[] convertVer = null;
				
				if(0 < seqNumber.indexOf(".")){ // DCU or Modem
					String[] versionArray = seqNumber.split("\\.");
					byte[] a = DataUtil.readByteString(String.format("%02d", Integer.parseInt(versionArray[0])));
					byte[] b = DataUtil.readByteString(String.format("%02d", Integer.parseInt(versionArray[1])));
					convertVer = new byte[]{a[0], b[0]};
				}else{  // Meter
					String version = seqNumber.substring(seqNumber.length() - 4, seqNumber.length());
					convertVer = DataUtil.readByteString(version);
				}
				
				log.debug("Attribute1. UpgradeSequenceNumber = " + seqNumber + ", HEX Converted UpgradeSequenceNumber = " + Hex.decode(convertVer));
				out.write(convertVer);
			}
			
			/** FW Version (Optional) */
			// INSERT START SP-681
			byte[] fwVersionBytes = null;
			if ((fwVersion != null) && (fwVersion.length() > 2)) {
				if(0 < fwVersion.indexOf(".")){ // DCU or Modem
					String[] versionArray = fwVersion.split("\\.");
					byte[] a = DataUtil.readByteString(String.format("%02d", Integer.parseInt(versionArray[0])));
					byte[] b = DataUtil.readByteString(String.format("%02d", Integer.parseInt(versionArray[1])));
					fwVersionBytes = new byte[]{a[0], b[0]};
				}else{  // Meter
					String version = fwVersion.substring(fwVersion.length() - 4, fwVersion.length());
					fwVersionBytes = DataUtil.readByteString(version);
				}
				
				log.debug("Attribute2. FW Version = " + fwVersion + ", HEX Converted version = " + Hex.decode(fwVersionBytes));
				out.write(fwVersionBytes);
			}
			
			/** FW Model Name (Optional) */
			byte[] fwModelBytes = null;
			if ((fwModel != null) && (fwModel.length()>0)) {
				fwModelBytes = new byte[20];
				Arrays.fill(fwModelBytes, (byte)0);
				System.arraycopy(fwModel.getBytes(), 0, fwModelBytes, 0, fwModel.getBytes().length);
				
				log.debug("Attribute3. FW Model Name = " + fwModel + ", HEX Converted fwModel = " + Hex.decode(fwModelBytes));  // 미터의 경우는 펌웨어파일명으로 들어가게됨.
				out.write(fwModelBytes);
			}			
			// INSERT END SP-681
			
			/** Optional Data CRC */
			// SP-1100
			if(fwVersionBytes != null && fwModelBytes != null) {
				byte[] optionalData = DataUtil.append(fwVersionBytes, fwModelBytes);
		        byte[] optionalDataCRC = CRCUtil.Calculate_ZigBee_Crc(optionalData,(char)0x0000);
		        DataUtil.convertEndian(optionalDataCRC);
				this.setOptionalDataCRC(optionalDataCRC);
				
				log.debug("Attribute4. Optional Data CRC = " + Hex.decode(optionalDataCRC));
				out.write(optionalDataCRC);
			}
			
			break;
		case UpgradeData:
			out.write(DataUtil.get4ByteToInt(getAddress()));
			out.write(DataUtil.get2ByteToInt(getLength()));
			out.write(getData());
			break;
		case UpgradeEndRequest:
		case UpgradeImageInstallRequest:				// DELETE SP-681 -> restore at NI Protocol 5.70
			out.write(DataUtil.get4ByteToInt(getImageLength()));
			out.write(DataUtil.readByteString(getCrc()));
			break;
//			// INSERT START SP-681 --> > restore at NI Protocol 5.70
//		case UpgradeImageInstallRequest:
//			out.write(DataUtil.get4ByteToInt(getImageLength()));
//			out.write(DataUtil.readByteString(getCrc()));
//			
//			if ((installTime != null) && (installTime.length()>=10)) {
//				log.debug("OTA Advanced Option. Install Time[" + installTime + "]");
//				out.write(DataUtil.get2ByteToInt(Integer.parseInt(installTime.substring(0,4))));	// YYYY
//				out.write(DataUtil.getByteToInt(Integer.parseInt(installTime.substring(4,6))));		// MM
//				out.write(DataUtil.getByteToInt(Integer.parseInt(installTime.substring(6,8))));		// DD
//				out.write(DataUtil.getByteToInt(Integer.parseInt(installTime.substring(8,10))));	// hh
//			}
//			break;
//			// INSERT END SP-681

		default:
			break;
		}
		byte[] bx = out.toByteArray();
		//log.debug(Hex.decode(bx));
		out.close();
		return bx;
	}

	@Override
	public void setCommandFlow(byte code) {
	}

	@Override
	public void setCommandType(byte code) {
	}

	@Override
	public byte[] getFrameTid() {
		return null;
	}

	@Override
	public void setFrameTid(byte[] code) {
	}
}

