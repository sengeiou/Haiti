package com.aimir.fep.protocol.nip.client.bypass;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HSWClientDecoder extends ProtocolDecoderAdapter{
	private static Logger logger = LoggerFactory.getLogger(HSWClientDecoder.class);
	
	@Override
	public void decode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// 로그 확인 편하도록....
		logger.info("    ");
		logger.info("    ");
		logger.info("    ");
		logger.info("############################## 로그확인 시작 ################################################");


		
		
        byte[] b = new byte[in.limit()];
        in.rewind();
        in.get(b, 0, b.length);
        out.write(b);
	}

}
