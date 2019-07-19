package com.aimir.fep.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.ReadHeadersInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

public class SoapInActionInInterceptor extends AbstractSoapInterceptor  {

	private static Log log = LogFactory.getLog(SoapInActionInInterceptor.class);
	
	public SoapInActionInInterceptor() {
		super(Phase.USER_PROTOCOL);
		addBefore(ReadHeadersInterceptor.class.getName());
		//addAfter(LoggingOutInterceptor.class.getName());
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		// TODO Auto-generated method stub
		
	}

}
