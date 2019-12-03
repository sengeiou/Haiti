package com.aimir.schedule.interceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.binding.soap.interceptor.ReadHeadersInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;

import com.aimir.util.AimirThreadLocal;

public class CmdInInterceptor extends AbstractSoapInterceptor  {

	private static Log log = LogFactory.getLog(CmdInInterceptor.class);
    
	public CmdInInterceptor() {
		super(Phase.USER_PROTOCOL);
		addBefore(ReadHeadersInterceptor.class.getName());
		//addAfter(LoggingOutInterceptor.class.getName());
	}
	
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		// TODO Auto-generated method stub
		try {
			String header = message.get(Message.PROTOCOL_HEADERS).toString();
			Pattern seqPattern = Pattern.compile("Sequence=\\[[a-zA-Z0-9]*\\]");
			Matcher seqMatcher = seqPattern.matcher(header);
			if (seqMatcher.find()) {
				String result = seqMatcher.group().toString();
				AimirThreadLocal.sequence.set(result.substring(10, result.length()-1));
			}
		} catch (Exception e) {
			log.error(e);
		}
	}

}