package com.aimir.fep.logger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import com.aimir.fep.logger.snowflake.SnowflakeGeneration;

public class SoapOutActionInInterceptor extends AbstractSoapInterceptor  {

	private static Log log = LogFactory.getLog(SoapOutActionInInterceptor.class);
	
	public SoapOutActionInInterceptor() {
		//super(Phase.USER_PROTOCOL);
		super(Phase.POST_LOGICAL);
		//addBefore(ReadHeadersInterceptor.class.getName());
		addAfter(LoggingOutInterceptor.class.getName());
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		// TODO Auto-generated method stub
        Map<String, List> headers = (Map<String, List>) message.get(Message.PROTOCOL_HEADERS);
        
        try {
        	if(headers == null) {
        		headers = new HashMap<String, List>();
        	}
        	headers.put("Sequence", Collections.singletonList(SnowflakeGeneration.getId()));
            message.put(Message.PROTOCOL_HEADERS, headers);
        } catch (Exception ce) {
            throw new Fault(ce);
        }
		SnowflakeGeneration.deleteId();
	}

}
