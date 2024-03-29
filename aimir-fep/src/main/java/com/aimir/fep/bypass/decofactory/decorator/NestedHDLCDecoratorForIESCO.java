package com.aimir.fep.bypass.decofactory.decorator;

import java.math.BigInteger;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.DlmsConstants;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcAddressType;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcFrameType;
import com.aimir.fep.bypass.decofactory.consts.HdlcFCS16;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class NestedHDLCDecoratorForIESCO extends NestFrameDecorator {
	private static Logger logger = LoggerFactory.getLogger(NestedHDLCDecoratorForIESCO.class);	
	private final String bufferKey = "XDLMS_INFORMATION_BUFFER";
	private byte[] gdDLMSFrame = null;

	private int destAddressLength = encodeOfAddress(HdlcAddressType.DEST).length;
	private int sourceAddressLength = encodeOfAddress(HdlcAddressType.SRC).length;

	private int receciveCount = 0;
	private int sendCount = -1;

	private boolean rrFrame = false; // SP-737

	private enum HdlcPiece {
		START_STOP_FLAG("7E"), FRAME_FORMAT("A0"), UNKNOWN("00");

		private String value;

		private HdlcPiece(String value) {
			this.value = value;
		}

		public byte[] getBytes() {
			return DataUtil.readByteString(this.value);
		}
	}

	/**
	 * @param nestedFrame
	 */
	public NestedHDLCDecoratorForIESCO(INestedFrame nestedFrame) {
		super(nestedFrame);
	}

	public byte[] encodeOfAddress(HdlcAddressType type) {
		/*	참조 구현해야함.
		 * 	int CHdlcEncoder::encodeAddr(codec::CMessageStream& stream, UINT addr)
				{
				    int len = 0;
				    if(addr < 0x80) // one byte address
				    {
				        len = 1;
				        stream.put((BYTE)(((addr&0x7F)<<1) | 0x01));
				    }
				    else if(addr < 0x4000)  // two bytes address
				    {
				        len = 2;
				        stream.put((WORD)((((addr&0x7F00)<<1)&0xFE00) | ((addr&0x7F)<<1) | 0x01));
				    }
				    else // four bytes address
				    {
				        len = 4;
				        stream.put((UINT)((((addr&0x7F000000)<<1)&0xFE000000) | (((addr&0x7F0000)<<1)&0xFE0000) |
				                    (((addr&0x7F00)<<1)&0xFE00) | ((addr&0x7F)<<1) | 0x01));
				    }

				    return len;
				}*/

		byte[] result = null;
		switch (type) {
		case DEST:
			// 임시로 고정된값 세팅 : 0x03
			result = new byte[] { (byte) 0x03 };  
			break;
		case SRC:
			// 임시로 고정된값 세팅 : 0x03
			result = new byte[] { (byte) 0x03 };
			break;
		case KAIFA_CUSTOM_SRC:
			result = new byte[] { (byte) 0x21 };			
			break;
		}

		return result;
	}

	public void decodeOfAddress() {
		/*	참조 구현해야함.	
		 * int CHdlcDecoder::decodeAddr(codec::CMessageStream& stream, UINT& addr)
				{
				    BYTE baddr = stream.get();

				    addr = (baddr>>1);
				    if(baddr & 0x01) return 1;

				    baddr = stream.get();
				    addr = (addr << 8) | (baddr>>1);
				    if(baddr & 0x01) return 2;

				    baddr = stream.get();
				    addr = (addr << 8) | (baddr>>1);
				    baddr = stream.get();
				    addr = (addr << 8) | (baddr>>1);
				    return 4;
				}*/
	}

	@Override
	//public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
	public byte[] encode(HdlcFrameType hdlcType, DlmsConstants.XDLMS_APDU dlmsApdu, Procedure procedure, HashMap<String, Object> param, String command) {
		logger.info("## Excute NestedHDLCDecorator Encoding [{}]", hdlcType.name());

		// if re-sending frame, return gdNIFrame  SP-737 
		if ( param != null && param.get("hdlcResendFrame")!= null){
			int retry = (int)param.get("hdlcResendFrame");
			if ( retry > 0){
				logger.info("Retry Sending, so return before sended frame:{}", Hex.decode(gdDLMSFrame));
				return gdDLMSFrame;
			}
		}
		try {
			gdDLMSFrame = new byte[] {};

			/**
			 * FRAME TYPE & LENGTH
			 */
			gdDLMSFrame = DataUtil.append(gdDLMSFrame, HdlcPiece.FRAME_FORMAT.getBytes()); // FrameType
			gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[1]); // FrameLength. Start & Stop 의 2바이트 제외한 크기

			/**
			 * ADDRESS
			 */
			gdDLMSFrame = DataUtil.append(gdDLMSFrame, encodeOfAddress(HdlcAddressType.DEST));
			if(command != null && (command.equals("cmdSORIAGetMeterKey")||command.equals("cmdSORIASetMeterSerial") )){
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, encodeOfAddress(HdlcAddressType.KAIFA_CUSTOM_SRC));
			}else{
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, encodeOfAddress(HdlcAddressType.SRC));
			}

			/**
			 * CONTROL
			 */
			if (hdlcType == HdlcFrameType.SNRM) {
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { hdlcType.getValue() });
			} else 	if (hdlcType == HdlcFrameType.DISC) {
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { hdlcType.getValue() });
			} else if (hdlcType == HdlcFrameType.RR) {
				//sendCount--;
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[] { HdlcConstants.getRRCount(receciveCount, sendCount) });
			} else { // AARQ, ACTION_REQ, GET_REQ
				sendCount++;
				byte[] control = new byte[] { HdlcConstants.getRSCount(receciveCount, sendCount) };
				String binaryString = String.format("%08d", new BigInteger(Integer.toBinaryString(DataUtil.getIntToByte(control[0]))));
				
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, control);
				logger.debug("[HDLC:ENCODE] R:S = [{}][{}] RS Count={}", Hex.decode(control), binaryString, HdlcConstants.getRSCount(control[0]));

				/*
				 * Count reset.
				 */
				if (8 <= sendCount) {
					sendCount = 0;
				}
			}
			
			/*
			 * RR 일경우 Information이 없음.
			 */
			if (hdlcType != HdlcFrameType.RR) {
				/**
				 * HCS
				 */
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, new byte[2]); // HCS

				/**
				 * DLMS FRAME Encoding
				 */
				byte[] dlmsEncodeArray = super.encode(hdlcType, dlmsApdu, procedure, param, command);
				
				if (dlmsEncodeArray == null) {
					throw new Exception("DLMS Encoding Error");
				}
				gdDLMSFrame = DataUtil.append(gdDLMSFrame, dlmsEncodeArray);

				// FRAME LENGTH 계산
				gdDLMSFrame[1] = DataUtil.getByteToInt(gdDLMSFrame.length + 2); // FrameLength. 2는 FCS 2byte

				try {
					// HCS 계산
					int temp = 3 + destAddressLength + sourceAddressLength;
					byte[] hcs = HdlcFCS16.getFCS(DataUtil.select(gdDLMSFrame, 0, temp)); // FRAME_FORMAT ~ CONTROL
					gdDLMSFrame[temp] = hcs[0];
					gdDLMSFrame[++temp] = hcs[1];				
				} catch (Exception e) {
					logger.error("HDLC FCS Error - {}", e);
					return null;
				}
			} else {
				// FRAME LENGTH 계산
				gdDLMSFrame[1] = DataUtil.getByteToInt(gdDLMSFrame.length + 2); // FrameLength. 2는 FCS 2byte
			}


			/**
			 * FCS
			 */
			gdDLMSFrame = DataUtil.append(gdDLMSFrame, HdlcFCS16.getFCS(gdDLMSFrame));

			/**
			 * START & STOP FLAG
			 */
			byte[] startFlag = new byte[] {};
			startFlag = DataUtil.append(startFlag, HdlcPiece.START_STOP_FLAG.getBytes());
			gdDLMSFrame = DataUtil.append(startFlag, gdDLMSFrame);
			gdDLMSFrame = DataUtil.append(gdDLMSFrame, HdlcPiece.START_STOP_FLAG.getBytes());
		} catch (Exception e) {
			logger.error("HDLC Encoding Error - {}", e);
			gdDLMSFrame = null;
		}

		return gdDLMSFrame;
	}

	@Override
	public String toByteString() {
		return Hex.decode(gdDLMSFrame);
	}

	@Override
	public boolean decode(MultiSession session, byte[] frame, Procedure procedure, String command) {
		logger.info("## Excute NestedHDLCDecorator Decoding...");
		boolean result = false;
		int pos = 0;

		try {
			byte[] headFlag = new byte[1];
			System.arraycopy(frame, pos, headFlag, 0, headFlag.length);
			pos += headFlag.length;
			logger.debug("[HDLC] HEAD_FLAG = [{}]", Hex.decode(headFlag));

			byte[] frameType = new byte[1];
			System.arraycopy(frame, pos, frameType, 0, frameType.length);
			pos += frameType.length;
			/*
			 * HDLC Segmentation 처리
			 */
			int fType = DataUtil.getIntToByte(frameType[0]);
			if (DataUtil.getIntToByte((byte) 0xA8) <= fType) {
				setHDLCSegmented(true);
			} else {
				setHDLCSegmented(false);
			}
			logger.debug("[HDLC] FRAME_TYPE = [{}] Frame Segmented = {}", Hex.decode(frameType), isHDLCSegmented());			
				

			byte[] frameLengthSub = new byte[1];
			System.arraycopy(frame, pos, frameLengthSub, 0, frameLengthSub.length);
			pos += frameLengthSub.length;
			logger.debug("[HDLC] FRAME_LENGTH = [{}]", Hex.decode(frameLengthSub));

			byte[] fLength = DataUtil.append(frameType, frameLengthSub);
			int length = DataUtil.getIntTo2Byte(fLength);
			String byteString = String.format("%016d", new BigInteger(Integer.toBinaryString(length)));
			int rLength =  Integer.parseInt((String) byteString.subSequence(5, 16), 2);	
			
			logger.debug("[HDLC] FRAME_LENGTH = [{}][{}] => 전체 Length={}", Hex.decode(fLength), byteString, rLength);
			setHDLCFrameLength(rLength); // UA의 데이터크기를 확인하기위해필요
			
			// DEST ADDRESS : DEST ADDRESS로 받지만 내용은 SOURCE ADDRESS에 저장
			byte[] srcAddress = new byte[sourceAddressLength];
			System.arraycopy(frame, pos, srcAddress, 0, srcAddress.length);
			pos += srcAddress.length;
			logger.debug("[HDLC] DEST_ADDRESS = [{}]", Hex.decode(srcAddress));

			// SOURCE ADDRESS
			byte[] destAddress;
			destAddress = new byte[destAddressLength];
			System.arraycopy(frame, pos, destAddress, 0, destAddress.length);
			pos += destAddress.length;
			logger.debug("[HDLC] SRC_ADDRESS = [{}]", Hex.decode(destAddress));

			byte[] control = new byte[1];
			System.arraycopy(frame, pos, control, 0, control.length);
			pos += control.length;

//			// CONTROL Type Setting
//			if (control[0] == 0x73) { // UA
//				setType(DataUtil.getIntToByte(control[0]));
//				logger.debug("[HDLC] CONTROL = [{}] => [{}]", Hex.decode(control), HdlcObjectType.getItem(getType()));
//			} else {
//				setType(0); // 초기화
//				String binaryString = String.format("%08d", new BigInteger(Integer.toBinaryString(DataUtil.getIntToByte(control[0]))));
//				
//				int[] rsCount = HdlcConstants.getRSCount(control[0]);
//				logger.debug("[HDLC] R:S = [{}][{}] RS Count={}", Hex.decode(control), binaryString, rsCount);
//				
//				setMeterRSCount(rsCount);
//				// INSERT START SP-737
//				rrFrame = false;
//				if ( "0001".equals(binaryString.substring(4, 8))){
//					logger.debug("[HDLC] RRFRAME");
//					rrFrame = true;
//				}
//				// INSERT END   SP-737
//			}
			
			setHDLCFrameType(HdlcFrameType.getItem(control[0]));

			if (getHDLCFrameType() == HdlcFrameType.I) {
				String binaryString = String.format("%08d", new BigInteger(Integer.toBinaryString(DataUtil.getIntToByte(control[0]))));
				
				int[] rsCount = HdlcConstants.getRSCount(control[0]);
				logger.debug("[HDLC] R:S = [{}][{}] RS Count={}", Hex.decode(control), binaryString, rsCount);
				
				/*
				 * RS Counter.
				 */
				setMeterRSCount(rsCount);
				receciveCount++;
				if (8 <= receciveCount) {
					receciveCount = 0; // Count reset.
				}
			} else {
				logger.debug("[HDLC] CONTROL = [{}] => [{}]", Hex.decode(control), getHDLCFrameType().name());  // UA, DISC
			}
			
			/*
			 * Information이 있는지 확인.
			 */
			if(3 < (frame.length - pos)){
				byte[] hcs = new byte[2];
				System.arraycopy(frame, pos, hcs, 0, hcs.length);
				pos += hcs.length;
				logger.debug("[HDLC] HCS = [{}]", Hex.decode(hcs));

				// 9 : frameType + frameLength + arcAddress + descAddress + Control + hcs + fcs
				int temp = 7 + destAddressLength + sourceAddressLength;
				byte[] information = new byte[rLength - temp];
				
				System.arraycopy(frame, pos, information, 0, information.length);
				pos += information.length;
				logger.debug("[HDLC] INFORMATION = [{}]", Hex.decode(information));

				/*
				 * HDLC Segmentation 처리
				 */
				if (isHDLCSegmented()) {
					byte[] informationBuffer = (byte[]) session.getAttribute(bufferKey);					
					if(informationBuffer == null){
						informationBuffer = new byte[]{};
						informationBuffer = DataUtil.append(informationBuffer, information);
						
						session.setAttribute(bufferKey, informationBuffer);						
					}else{
						 informationBuffer = DataUtil.append(informationBuffer, information);
					}

					logger.debug("[RR] Segmented frme buffering. size = {}byte, data = {}", informationBuffer.length, Hex.decode(informationBuffer));
				} else if(isHDLCSegmented() == false){
					logger.debug("has buffer?? = {}",  (session.getAttribute(bufferKey) != null ? "Y" : "N"));
					if(session.getAttribute(bufferKey) != null){ // 버퍼가 있을경우 붙여서 진행
						information = DataUtil.append((byte[])session.getAttribute(bufferKey), information);
						
						logger.debug("[RR] Merged Segmented Information frme. size = {}byte, data = {}", information.length, Hex.decode(information));
						session.removeAttribute(bufferKey);
					}
					
					/**
					 * DLMS Decoding
					 */
					if (!super.decode(session, information, procedure, command)) {
						throw new Exception("DLMS Decoding Error");
					}					
				}
			}

			byte[] fcs = new byte[2];
			System.arraycopy(frame, pos, fcs, 0, fcs.length);
			pos += fcs.length;
			logger.debug("[HDLC] FCS = [{}]", Hex.decode(fcs));
			byte[] tailFlag = new byte[1];
			System.arraycopy(frame, pos, tailFlag, 0, tailFlag.length);
			pos += tailFlag.length;
			logger.debug("[HDLC] TAIL_FLAG = [{}]", Hex.decode(tailFlag));

			result = true;
		} catch (Exception e) {
			logger.error("HDLC Decoding Error - {}", e);
			result = false;
		}

		return result;
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		// TODO Auto-generated method stub
		return super.customDecode(procedure, data);
	}
	

	/**
	 * SP-737
	 * @return the rrFrame
	 */
//	public boolean isRrFrame() {
//		return rrFrame;
//	}
//
//	/**
//	 * SP-737
//	 * @param rrFrame the rrFrame to set
//	 */
//	public void setRrFrame(boolean rrFrame) {
//		this.rrFrame = rrFrame;
//	}

}
