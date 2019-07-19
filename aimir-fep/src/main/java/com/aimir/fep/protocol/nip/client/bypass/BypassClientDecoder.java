/**
 * (@)# NIBypassClientDecoder.java
 *
 * 2016. 6. 1.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.protocol.nip.client.bypass;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author simhanger
 *
 */
public class BypassClientDecoder extends CumulativeProtocolDecoder {
	private static Logger logger = LoggerFactory.getLogger(BypassClientDecoder.class);
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		// 로그 확인 편하도록....
		logger.info("    ");
		logger.info("    ");
		logger.info("    ");
		logger.info("############################## 로그확인 시작 ################################################");


		
		
        byte[] b = new byte[in.limit()];
        in.rewind();
        in.get(b, 0, b.length);
        out.write(b);
        
        
        
        /*
         * 
         * 추후 데이터가 다 오지 않을경우 처리하는 로직 넣을것.
         * 
         * 
         */
        
        return true;
	}

}
