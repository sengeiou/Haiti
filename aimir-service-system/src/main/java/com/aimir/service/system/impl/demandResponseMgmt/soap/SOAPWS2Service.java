
package com.aimir.service.system.impl.demandResponseMgmt.soap;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "SOAPWS2Service", targetNamespace = "http://www.openadr.org/DRAS/DRASClientSOAP/", wsdlLocation = "http://cdp.openadr.com/SOAPClientWS/nossl/soap2?wsdl")
public class SOAPWS2Service
    extends Service
{

    private final static URL SOAPWS2SERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(SOAPWS2Service.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = SOAPWS2Service.class.getResource(".");
//            url = new URL(baseUrl, "http://cdp.openadr.com/SOAPClientWS/nossl/soap2?wsdl");
            url = new URL(baseUrl, "http://cdp.openadr.com/SOAPClientWS/soap2?wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'http://cdp.openadr.com/SOAPClientWS/nossl/soap2?wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        SOAPWS2SERVICE_WSDL_LOCATION = url;
    }

    public SOAPWS2Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public SOAPWS2Service() {
        super(SOAPWS2SERVICE_WSDL_LOCATION, new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "SOAPWS2Service"));
    }

    /**
     * 
     * @return
     *     returns DRASClientSOAP
     */
    @WebEndpoint(name = "DRASClientSOAPPort")
    public DRASClientSOAP getDRASClientSOAPPort() {
        return super.getPort(new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "DRASClientSOAPPort"), DRASClientSOAP.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns DRASClientSOAP
     */
    @WebEndpoint(name = "DRASClientSOAPPort")
    public DRASClientSOAP getDRASClientSOAPPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.openadr.org/DRAS/DRASClientSOAP/", "DRASClientSOAPPort"), DRASClientSOAP.class, features);
    }

}
