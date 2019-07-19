package com.aimir.mars.integration.multispeak.server;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ArrayOfAssessmentLocation;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ErrorObject;
import org.springframework.stereotype.Service;

import com.aimir.fep.util.DataUtil;
import com.aimir.mars.integration.multispeak.service.AssessmentLocationChangedNotificationService;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;

@WebService(serviceName = "OA_Server", targetNamespace = "http://www.multispeak.org/Version_4.1_Release")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "oA_Server")
public class OA_Server {

    protected static Log log = LogFactory.getLog(OA_Server.class);

    /**
     * Publisher notifies subscriber that assessmentLocations have been
     * published or updated. Subscriber returns information about failed
     * transactions using an array of errorObjects. The message header attribute
     * 'registrationID' should be added to all publish messages to indicate to
     * the subscriber under which registrationID they received this notification
     * data.
     */
    @WebMethod(operationName = "AssessmentLocationChangedNotification", action = "http://www.multispeak.org/Version_4.1_Release/AssessmentLocationChangedNotification")
    public @WebResult(name = "AssessmentLocationChangedNotificationResult", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfErrorObject AssessmentLocationChangedNotification(
            @WebParam(name = "locations", targetNamespace = "http://www.multispeak.org/Version_4.1_Release") ArrayOfAssessmentLocation locations)
            throws Exception {

        ArrayOfErrorObject resp = null;

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(Calendar.getInstance().getTime());
        XMLGregorianCalendar eventTime = DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(cal);
        eventTime.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
        eventTime.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

        if (locations == null) {
            resp = new ArrayOfErrorObject();
            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [locations] is null.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            resp.getErrorObject().add(eo);
            return resp;
        }

        if (locations.getAssessmentLocation() == null
                || locations.getAssessmentLocation().isEmpty()) {
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.INVALID_PARAMETER.getName()
                    + " [AssessmentLocation] is empty.");
            eo.setNounType("");
            eo.setEventTime(eventTime);
            resp.getErrorObject().add(eo);
            return resp;
        }

        try {
            AssessmentLocationChangedNotificationService service = DataUtil
                    .getBean(
                            AssessmentLocationChangedNotificationService.class);
            resp = service.execute(locations);

            if (resp == null) {
                resp = new ArrayOfErrorObject();
            }
        } catch (Exception e) {
            log.error(e, e);
            resp = new ArrayOfErrorObject();

            ErrorObject eo = new ErrorObject();
            eo.setErrorString(ValidationError.SYSTEM_ERROR.getName());
            eo.setNounType("");
            eo.setEventTime(eventTime);

            resp.getErrorObject().add(eo);
        }

        return resp;
    }

}