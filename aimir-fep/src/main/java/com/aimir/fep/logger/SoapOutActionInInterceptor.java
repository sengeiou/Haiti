package com.aimir.fep.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.phase.Phase;

import com.aimir.fep.logger.snowflake.SnowflakeGeneration;

public class SoapOutActionInInterceptor extends AbstractSoapInterceptor  {

	private static Log log = LogFactory.getLog(SoapOutActionInInterceptor.class);
	
	public SoapOutActionInInterceptor() {
		super(Phase.USER_PROTOCOL);
		//addBefore(ReadHeadersInterceptor.class.getName());
		addAfter(LoggingOutInterceptor.class.getName());
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		// TODO Auto-generated method stub
		SnowflakeGeneration.deleteId();
	}

}
