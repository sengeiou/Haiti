/**
 * 
 */
package com.aimir.fep.protocol.fmp.server;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import com.aimir.fep.protocol.fmp.client.FMPClientCloser;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.GeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class FMPProtocolPartialKit {
	private static Log log = LogFactory.getLog(FMPProtocolPartialKit.class);

	private IoSession session;
	private ArrayList<byte[]> frameList;
	private int lastSeq = -1;
	private int startSeq = 0;
	private int endSeq = 0;
	private int thisSeq = -1;
	private ControlDataFrame wck = null;

	public FMPProtocolPartialKit(IoSession session, ArrayList<byte[]> frameList) {
		this.session = session;
		this.frameList = frameList;
	}

	/**
	 * Get Last Sequence
	 * 
	 * @return
	 */
	public int getLastSeq() {
		if (-1 == lastSeq) {
			if (frameList != null && 0 < frameList.size()) {
				byte[] mbx = (byte[]) frameList.get(frameList.size() - 1);
				lastSeq = DataUtil.getIntToByte(mbx[1]);
			} else {
				log.warn("Have not frameList. please check frame list.");
			}
		}

		return lastSeq;
	}

	/**
	 * Send Frame
	 */
	public void executeStart() {
		if (frameList != null && 0 < frameList.size()) {
			for (int i = 0; i < frameList.size(); i++) {

				if (startSeq <= i) { // 시작 시퀀스보다 클경우만 데이터 전송
					log.debug("### Total Size=" + frameList.size() + ". FrameSeq = (" + i + "/" + (frameList.size() - 1) + ")");

					write(frameList.get(i));

					if (isNextWCK()) {
						endSeq = thisSeq;
						wck = FrameUtil.getWCK(startSeq, endSeq);
						((GeneralDataFrame) wck).setAttrByte(GeneralDataConstants.ATTR_ACK);
						session.write(wck);

						log.debug("[WCK] startSEQ : " + startSeq + ", endSEQ : " + endSeq);
						break;
					}
				} else {
					log.debug("### [skip] aready sended frame. Total Size=" + frameList.size() + ". FrameSeq = (" + i + "/" + (frameList.size() - 1) + ")");
				}
			}
		} else {
			log.warn("Have not frameList. please check frame list.");
		}
	}

	private boolean isNextWCK() {
		boolean result = false;

		if (1 < frameList.size()) {
			if (((thisSeq + 1) % GeneralDataConstants.FRAME_WINSIZE) == 0 || thisSeq == getLastSeq()) {
				log.debug("==> Next Seq=" + (thisSeq + 1) + ", Next Sequence is WCK, Total Last Seq = " + getLastSeq() + ", FRAME_WINSIZE =" + GeneralDataConstants.FRAME_WINSIZE);
				result = true;
			}
		}

		return result;
	}

	/**
	 * write Frame
	 * 
	 * @param data
	 */
	private void write(byte[] data) {
		if (session != null && session.isConnected()) {
			thisSeq = DataUtil.getIntToByte(data[1]);
			session.write(data);
			log.info("Sended : seq=[" + thisSeq + "], target=[" + session.getRemoteAddress() + "], length=[" + data.length + "], Hex=[" + Hex.decode(data) + "]");
			FrameUtil.waitSendFrameInterval();
		} else {
			log.error("can't send data. Session is null or disconnected.");
		}
	}

	/**
	 * ACK Processing
	 * 
	 * @param ackSeq
	 * @throws Exception
	 */
	public void excuteAck(int ackSeq) throws Exception {
		log.debug("Excute Ack processing. AckSeq=[" + ackSeq + "], StartSeq=" + startSeq + ", EndSeq=" + endSeq);

		if (ackSeq != endSeq) {
			log.error("[Partial] Ack Sequence and End Sequence is not same.");
			throw new Exception("[Partial] Ack Sequence and End Sequence is not same.");
		} else {
			if (ackSeq == lastSeq) {
				log.debug("[Partial] All Frame list send finished.");
				excuteStop();
			} else {
				startSeq = ++endSeq;
				endSeq = 0;
				executeStart();
			}
		}
	}

	/**
	 * NAK Processing
	 * 
	 * @param nakSeq
	 */
	public void excuteNak(int nakSeq) {
		log.debug("Excute Send NAK FrameSeq = " + nakSeq);
		write(frameList.get(nakSeq));

		log.debug("[WCK] startSEQ : " + startSeq + ", endSEQ : " + endSeq);
		session.write(wck);
	}

	/**
	 * Stop Processing
	 */
	public void excuteStop() {
		new FMPClientCloser(session).closeAfterSendEOT();
	}

}
