package com.aimir.mars.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CacheAndWriteOutputStream;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.aimir.util.DateTimeUtil;

public class SoapFileDumpOutInterceptor extends AbstractSoapInterceptor {

    public SoapFileDumpOutInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        System.out.println("out: " + message);
        System.out.println("out: " + message.keySet());
        for(Header head:message.getHeaders()) {
            System.out.println("head : " +head);
        }
        System.out.println("dest : " + message.getDestination());

        HttpServletRequest httpRequest = null;
        if (message.get(AbstractHTTPDestination.HTTP_REQUEST) != null) {
            httpRequest = (HttpServletRequest) message
                    .get(AbstractHTTPDestination.HTTP_REQUEST);
        }
        String soapActionOrg = null;
        String soapAction = null;
        if(httpRequest != null && httpRequest.getHeader("soapaction")!=null) {
            soapActionOrg = httpRequest.getHeader("soapaction");
            if (soapActionOrg.indexOf("\"") > -1) {
                soapAction = soapActionOrg.substring(
                        soapActionOrg.lastIndexOf("/") + 1,
                        soapActionOrg.lastIndexOf("\""));
            } else {
                soapAction = soapActionOrg
                        .substring(soapActionOrg.lastIndexOf("/") + 1);
            }
        } else if(message
                .get(MessageContext.WSDL_OPERATION)!=null) {
            soapActionOrg = ((QName) message.get(MessageContext.WSDL_OPERATION)).toString();
            soapAction = soapActionOrg
                    .substring(soapActionOrg.lastIndexOf("}") + 1);
        } else if (message.get(Message.PROTOCOL_HEADERS) != null
                && ((Map<String, List<String>>) message
                        .get(Message.PROTOCOL_HEADERS))
                                .get("SOAPAction") != null
                && ((Map<String, List<String>>) message
                        .get(Message.PROTOCOL_HEADERS)).get("SOAPAction")
                                .size() > 0) {
            Map<String, List<String>> m = (Map<String, List<String>>) message
                    .get(Message.PROTOCOL_HEADERS);
            soapActionOrg = (String) ((Map<String, List<String>>) message
                    .get(Message.PROTOCOL_HEADERS)).get("SOAPAction").get(0);
            if (soapActionOrg.indexOf("\"") > -1) {
                soapAction = soapActionOrg.substring(
                        soapActionOrg.lastIndexOf("/") + 1,
                        soapActionOrg.lastIndexOf("\""));
            } else {
                soapAction = soapActionOrg
                        .substring(soapActionOrg.lastIndexOf("/") + 1);
            }
        } else {
            soapAction = "unknown";
        }

        try {
            soapAction = soapAction.replaceAll("\"", "");
            OutputStream os = message.getContent(OutputStream.class);
            CacheAndWriteOutputStream cwos = new CacheAndWriteOutputStream(os);
            message.setContent(OutputStream.class, cwos);
            FileOutputStream fos = new FileOutputStream("/tmp/soap_"
                    + DateTimeUtil
                            .getCurrentDateTimeByFormat("yyyyMMddHHmmssSSS")
                    + "_ou_" + soapAction + ".xml");
            cwos.registerCallback(new LoggingOutCallBack(fos));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    class LoggingOutCallBack implements CachedOutputStreamCallback {
        FileOutputStream fos;

        public LoggingOutCallBack(FileOutputStream fos) {
            this.fos = fos;
        }

        @Override
        public void onClose(CachedOutputStream cos) {
            try {
                if (cos != null && fos != null) {
                    IOUtils.copy(cos.getInputStream(), fos);
                    fos.flush();
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFlush(CachedOutputStream arg0) {
        }
    }
}