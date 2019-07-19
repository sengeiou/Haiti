package com.aimir.mars.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.SoapVersion;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import com.aimir.util.DateTimeUtil;

public class SoapFileDumpInInterceptor extends AbstractSoapInterceptor {

    public SoapFileDumpInInterceptor() {
        super(Phase.RECEIVE);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        System.out.println("in: " + message);
        System.out.println("in: " + message.keySet());
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
        if (httpRequest != null
                && httpRequest.getHeader("soapaction") != null) {
            soapActionOrg = httpRequest.getHeader("soapaction");
            if (soapActionOrg.indexOf("\"") > -1) {
                soapAction = soapActionOrg.substring(
                        soapActionOrg.lastIndexOf("/") + 1,
                        soapActionOrg.lastIndexOf("\""));
            } else {
                soapAction = soapActionOrg
                        .substring(soapActionOrg.lastIndexOf("/") + 1);
            }
        } else if (message.get(MessageContext.WSDL_OPERATION) != null) {
            soapActionOrg = ((QName) message.get(MessageContext.WSDL_OPERATION))
                    .toString();
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
            InputStream is = message.getContent(InputStream.class);
            CachedOutputStream os = new CachedOutputStream();
            IOUtils.copy(is, os);
            os.flush();
            message.setContent(InputStream.class, os.getInputStream());
            is.close();

            FileOutputStream fos = new FileOutputStream("/tmp/soap_"
                    + DateTimeUtil
                            .getCurrentDateTimeByFormat("yyyyMMddHHmmssSSS")
                    + "_in_" + soapAction + ".xml");
            IOUtils.copy(os.getInputStream(), fos);
            fos.flush();
            fos.close();
            os.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}