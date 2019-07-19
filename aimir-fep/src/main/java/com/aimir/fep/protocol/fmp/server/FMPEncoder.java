package com.aimir.fep.protocol.fmp.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.meter.parser.plc.PLCDataConstants;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;

/**
 * Encodes MCU Communication Stream into General Data Frame.
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */

public class FMPEncoder extends ProtocolEncoderAdapter {
	private static Log log = LogFactory.getLog(FMPEncoder.class);

	private void waitAck(IoSession session, int sequence) throws Exception {
		FMPProtocolHandler handler = (FMPProtocolHandler) session.getHandler();
		handler.waitAck(session, sequence);
	}

	/**
	 * encode FMP Protocol Frame
	 *
	 * @param session
	 *            <code>ProtocolSession</code> session
	 * @param message
	 *            <code>Object</code> GeneralDataFrame or ByteBuffer
	 * @param out
	 *            <code>ProtocolEncoderOutput</code> save encoded byte stream
	 * @throws ProtocolViolationException
	 *             indicating that is was error of Encode
	 */
	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws ProtocolEncoderException {
		try {
			log.info("Encode [Start]");

			if (message instanceof ServiceDataFrame) {
				log.warn("####### if you can this log. please send me email.(simhanger@nuritelecom.com) ######");
				log.warn("####### if you can this log. please send me email.(simhanger@nuritelecom.com) ######");
				log.warn("####### if you can this log. please send me email.(simhanger@nuritelecom.com) ######");

				//            	log.debug("#### =====> ServiceDataFrame encoding.");
				//
				//                GeneralDataFrame frame = (GeneralDataFrame)message;
				//                
				//                if(isUseCompress){
				//                	frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS);
				//                	log.debug("set Compress mode attr Hex=" + Hex.decode(new byte[]{frame.getAttr()}));
				//                }else{
				//                	frame.setAttrByte(GeneralDataConstants.ATTR_COMPRESS, false);
				//                	log.debug("set Compress mode attr Hex=" + Hex.decode(new byte[]{frame.getAttr()}));
				//                }
				//
				//                byte[] bx = frame.encodeWithCompress(); //zlib
				//                byte[] mbx = null;
				//                IoBuffer buf = null;
				//                //ArrayList<?> framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
				//                
				//                ArrayList<?> framelist = null;
				//                if(frame.isAttrByte(GeneralDataConstants.ATTR_COMPRESS)){
				//                    framelist = FrameUtil.makeMultiEncodedFrame(bx, session, true, frame.getUnCompressedLength());        	
				//                }else{
				//                    framelist = FrameUtil.makeMultiEncodedFrame(bx, session);
				//                }
				//                
				//                session.setAttribute("sendframes", framelist);
				//                int lastIdx = framelist.size() - 1;
				//                mbx = (byte[])framelist.get(lastIdx);
				//                int lastSequence = DataUtil.getIntToByte(mbx[1]);
				//                log.debug("lastSequence ==> " + lastSequence);
				//                
				//                Iterator<?> iter = framelist.iterator();
				//                int cnt = 0;
				//                int seq = 0;
				//                boolean isLastSeq = false;
				//                ControlDataFrame wck = null;
				//                while(iter.hasNext())
				//                {
				//                    mbx = (byte[])iter.next();
				//                    seq = DataUtil.getIntToByte(mbx[1]);
				//                    buf = IoBuffer.allocate(mbx.length);
				//                    buf.put(mbx,0,mbx.length);
				//                    buf.flip();
				//
				//                    log.info("Sended : seq=[" + seq + "], ["+session.getRemoteAddress() +"] " + buf.limit() + " : " + buf.getHexDump());
				//                    //session.write(buf);
				//                    out.write(buf);
				//                    out.flush();
				//                    FrameUtil.waitSendFrameInterval();
				//                    
				//                    isLastSeq = (seq == lastSequence ? true : false);
				//                    if(session.getAttribute("mfStartSeq") == null){
				//                        session.setAttribute("mfStartSeq", seq); // Multi Frame Start Sequence.                    	
				//                    }
				//                    
				//                    log.debug("IsLastSequence ? = " + isLastSeq + ", cnt=" + cnt + ", seq = " + seq + ", FRAME_WINSIZE = " + GeneralDataConstants.FRAME_WINSIZE);
				//                    
				//                    if(1 < framelist.size()){  // Partial frame �ϰ�츸 �ش�.
				//                        if(((cnt+1) % GeneralDataConstants.FRAME_WINSIZE) == 0 || isLastSeq == true)
				//                        {
				//                        	log.debug("[WCK] startSEQ : " + session.getAttribute("mfStartSeq") + ", endSEQ : " + seq);
				//                            wck =FrameUtil.getWCK(Integer.parseInt(String.valueOf(session.getAttribute("mfStartSeq"))), seq);
				//                            session.write(wck);
				//                            session.setAttribute("wck", wck);
				//                            waitAck(session,cnt);
				//                            session.removeAttribute("mfStartSeq");
				//                        }                    	
				//                    }
				//                    
				//                    cnt++;
				//                }
				//                
				//                log.debug("SVC Field (ex. 0x43='C', 0x45='E')=> " + Hex.decode((new byte[]{ frame.getSvc()})));
				//                if(frame.getSvc() != GeneralDataConstants.SVC_C)
				//                {
				//                	log.debug("#########!!!!!!!!!! ���� �����~!!! ��¥? ###");
				//                	log.debug("#########!!!!!!!!!! ���� �����~!!! ��¥? ###");
				//                	log.debug("#########!!!!!!!!!! ���� �����~!!! ��¥? ###");
				//                	
				//                	log.debug("cnt=" + cnt + ", seq = " + seq + ", FRAME_WINSIZE = " + GeneralDataConstants.FRAME_WINSIZE);
				//                    if((cnt % GeneralDataConstants.FRAME_WINSIZE) != 0)
				//                    {
				//                        if(cnt > 1)
				//                        {
				//                            log.debug("WCK : start : " + (seq -(seq%GeneralDataConstants.FRAME_WINSIZE)) + ", end : " + seq);
				//                            wck =FrameUtil.getWCK(seq - ( seq % GeneralDataConstants.FRAME_WINSIZE), seq);
				//                            session.write(wck);
				//                            session.setAttribute("wck",wck);
				//                        }
				//                        waitAck(session,cnt-1);
				//                    }
				//                }
				//                FrameUtil.waitSendFrameInterval();
				//                session.removeAttribute("wck");
			} else if (message instanceof ControlDataFrame) {
				log.debug("#### =====> ControlDataFrame encoding.");

				ControlDataFrame frame = (ControlDataFrame) message;
				byte[] bx = frame.encode();
				byte[] crc = FrameUtil.getCRC(bx);
				IoBuffer buff = IoBuffer.allocate(bx.length + crc.length);
				buff.put(bx);
				buff.put(crc);
				buff.flip();
				log.info("Sended : [" + session.getRemoteAddress() + "] " + buff.limit() + " : " + buff.getHexDump());
				out.write(buff);
				out.flush();
			} else if (message instanceof IoBuffer) {
				log.debug("#### =====> IoBuffer encoding.");

				IoBuffer buffer = (IoBuffer) message;
				log.info("Sended : [" + session.getRemoteAddress() + "] " + buffer.limit() + " : " + buffer.getHexDump());
				out.write(buffer);
			} else if (message instanceof PLCDataFrame) {
				log.debug("#### =====> PLCDataFrame encoding.");

				byte[] bx = ((PLCDataFrame) message).encode();
				byte[] crc = FrameUtil.getCRC(bx, 1, bx.length - 1);
				IoBuffer buffer = IoBuffer.allocate(bx.length + 3);
				buffer.put(bx);
				buffer.put(crc);
				buffer.put(PLCDataConstants.EOF);
				buffer.flip();
				log.debug("Sended[" + session.getRemoteAddress() + "] Command[" + DataUtil.getPLCCommandStr(((PLCDataFrame) message).getCommand()) + "]: " + buffer.limit() + " : " + buffer.getHexDump());
				out.write(buffer);
			}
			// AMU
			else if (message instanceof AMUGeneralDataFrame) {
				log.debug("#### =====> AMUGeneralDataFrame encoding.");

				AMUGeneralDataFrame frame = (AMUGeneralDataFrame) message;
				int sendSeq = frame.getSequence();

				session.setAttribute(AMUGeneralDataConstants.TX_SEQ, (Integer) sendSeq);
				log.debug("[Send ] Seqensce :" + sendSeq);
				byte[] bx = frame.encode();
				IoBuffer buffer = IoBuffer.allocate(bx.length);
				buffer.put(bx);
				buffer.flip();
				log.debug("Sended[" + session.getRemoteAddress() + "] Command[" + frame.getAmuFrameControl().getFrameTypeMessage() + "]: " + buffer.limit() + " : " + buffer.getHexDump());
				out.write(buffer);
				log.info("AMUGeneralDataFrame Encode [End]");
			} else if (message instanceof byte[]) {
				log.debug("#### =====> byte[] encoding.");
				byte[] data = (byte[]) message;
				IoBuffer buffer = IoBuffer.allocate(data.length, false);
				buffer.put(data, 0, data.length);
				buffer.flip();

				out.write(buffer);
				out.flush();

				log.debug("#### =====> byte[" + data.length + "] writting.");
			}
			log.info("Encode [End]");
		} catch (Exception ex) {
			log.error("encode failed [" + message + "] - " + ex.getMessage(), ex);
			throw new ProtocolEncoderException(ex.getMessage());
		}
	}
}
