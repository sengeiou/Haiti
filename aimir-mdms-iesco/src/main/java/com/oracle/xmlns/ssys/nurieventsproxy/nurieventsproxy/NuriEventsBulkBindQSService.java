package com.oracle.xmlns.ssys.nurieventsproxy.nurieventsproxy;

import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

/**
 * OSB Service
 *
 * This class was generated by Apache CXF 3.1.5
 * 2016-08-31T16:21:32.782+02:00
 * Generated source version: 3.1.5
 * 
 */
@WebServiceClient(name = "NuriEvents_bulk_bindQSService", 
                  wsdlLocation = "file:NuriEventsProxy.wsdl",
                  targetNamespace = "http://xmlns.oracle.com/SSYS/NuriEventsProxy/NuriEventsProxy") 
public class NuriEventsBulkBindQSService extends Service {

    public final static URL WSDL_LOCATION;

    public final static QName SERVICE = new QName("http://xmlns.oracle.com/SSYS/NuriEventsProxy/NuriEventsProxy", "NuriEvents_bulk_bindQSService");
    public final static QName NuriEventsBulkBindQSPort = new QName("http://xmlns.oracle.com/SSYS/NuriEventsProxy/NuriEventsProxy", "NuriEvents_bulk_bindQSPort");
    static {
        URL url = null;
        try {
            url = new URL("file:NuriEventsProxy.wsdl");
        } catch (MalformedURLException e) {
            java.util.logging.Logger.getLogger(NuriEventsBulkBindQSService.class.getName())
                .log(java.util.logging.Level.INFO, 
                     "Can not initialize the default wsdl from {0}", "file:NuriEventsProxy.wsdl");
        }
        WSDL_LOCATION = url;
    }

    public NuriEventsBulkBindQSService(URL wsdlLocation) {
        super(wsdlLocation, SERVICE);
    }

    public NuriEventsBulkBindQSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public NuriEventsBulkBindQSService() {
        super(WSDL_LOCATION, SERVICE);
    }
    
    public NuriEventsBulkBindQSService(WebServiceFeature ... features) {
        super(WSDL_LOCATION, SERVICE, features);
    }

    public NuriEventsBulkBindQSService(URL wsdlLocation, WebServiceFeature ... features) {
        super(wsdlLocation, SERVICE, features);
    }

    public NuriEventsBulkBindQSService(URL wsdlLocation, QName serviceName, WebServiceFeature ... features) {
        super(wsdlLocation, serviceName, features);
    }    




    /**
     *
     * @return
     *     returns NuriEvents
     */
    @WebEndpoint(name = "NuriEvents_bulk_bindQSPort")
    public NuriEvents getNuriEventsBulkBindQSPort() {
        return super.getPort(NuriEventsBulkBindQSPort, NuriEvents.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns NuriEvents
     */
    @WebEndpoint(name = "NuriEvents_bulk_bindQSPort")
    public NuriEvents getNuriEventsBulkBindQSPort(WebServiceFeature... features) {
        return super.getPort(NuriEventsBulkBindQSPort, NuriEvents.class, features);
    }

}
