package com.aimir.fep.protocol.mrp.client;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class SMSCumulativeTextDecoder extends CumulativeProtocolDecoder {

    private static Log log = LogFactory.getLog(SMSCumulativeTextDecoder.class);
    
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		CharsetDecoder decoder = Charset.forName("US-ASCII").newDecoder();
		String command = (String) session.getAttribute("ATCOMMAND");
		String response = (String) session.getAttribute("RESPONSE");
		int start = in.position();
		while(in.hasRemaining()){
			String message = in.getString(decoder);
			log.debug("doDecode="+message);			

			if(message.indexOf("\r\n") > 0){
				if(command == null){
					out.write(message);
	                in.position(in.limit());
					return true;
				}

				if(response != null){
					if(message.indexOf(response) >= 0){
						out.write(message);
						session.removeAttribute(command);
						session.removeAttribute(response);
						return true;
					}else{
						if(message.indexOf(command) >= 0 && message.length() > command.length()){
							out.write(message);
							session.removeAttribute(command);
							session.removeAttribute(response);
							return true;
						}
					}
				}else{
					out.write(message);
					session.removeAttribute(command);
					return true;
				}
			}			

		}		
		
		in.position(start);
		return false;
	}
	
}
