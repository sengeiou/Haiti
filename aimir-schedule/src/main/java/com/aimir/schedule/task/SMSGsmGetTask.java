package com.aimir.schedule.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import org.quartz.JobExecutionContext;

import com.aimir.fep.util.FrameUtil;

public class SMSGsmGetTask extends ScheduleTask {
    private static Log log = LogFactory.getLog(SMSGsmGetTask.class);
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    private final String ATZ = "ATZ";
    private final char CR = '\r';
    private final char LF = '\n';
    private final String CRLF = "\r\n";
    private final char CTRLZ = 0x1A;
    private final char DQUATA = 0x22;
    
    @Override
    public void execute(JobExecutionContext context) {
        Socket socket  = null;
        InputStream in = null;
        OutputStream out = null;
        
        try {
            socket = new Socket();
            // 터미널 서버에 연결
            socket.connect(new InetSocketAddress("", 1));
            in = socket.getInputStream();
            out = socket.getOutputStream();
            
            // 모뎀 초기화
            log.info(ATZ+CRLF);
            out.write(FrameUtil.getByteBuffer(ATZ+CRLF).array());
            log.info(getResponse(in));
            
            log.info("AT+CSCA?");
            out.write(FrameUtil.getByteBuffer("AT+CSCA?"+CRLF).array());
            log.info(getResponse(in));
            
            // text mode
            log.info("AT+CMGF=1");
            out.write(FrameUtil.getByteBuffer("AT+CMGF=1"+CRLF).array());
            log.info(getResponse(in));
            
            log.info("AT+CMGL=\"ALL\"");
            out.write(FrameUtil.getByteBuffer("AT+CMGL=" +DQUATA + "ALL" + DQUATA).array());
            log.info(getResponse(in));
            
            // 메시지가 확인되면 추출하여 SMPP 시물레이터로 전송한다.
        }
        catch (Exception e) {
            log.error(e, e);
        }
        finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (Exception e) {}
            }
        }
    }

    private String getResponse(InputStream in) throws IOException {
        byte[] res = new byte[1000];
        in.read(res);
        
        return new String(res);
    }
    
    private void sendSMPP(String msg) {
        SMPPSession session = new SMPPSession();
        try {
            session.connectAndBind("187.1.10.58", 2775, new BindParameter(BindType.BIND_TX, 
                    "smppclient1", "password", "cp", TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN, null));
        } catch (IOException e) {
            log.error("Failed connect and bind to host", e);
        }
        
        try {
            String messageId = session.submitShortMessage("CMT", TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.UNKNOWN, "1616", TypeOfNumber.INTERNATIONAL, 
                    NumberingPlanIndicator.UNKNOWN, "628176504657", new ESMClass(), 
                    (byte)0, (byte)1,  timeFormatter.format(new Date()), null, 
                    new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE), (byte)0, 
                    new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false), 
                    (byte)0, msg.getBytes());
            log.info("Message submitted, message_id is " + messageId);
        }
        catch (PDUException e) {
            // Invalid PDU parameter
            log.error("Invalid PDU parameter", e);
        }
        catch (ResponseTimeoutException e) {
            // Response timeout
            log.error("Response timeout", e);
        }
        catch (InvalidResponseException e) {
            // Invalid response
            log.error("Receive invalid response", e);
        }
        catch (NegativeResponseException e) {
            // Receiving negative response (non-zero command_status)
            log.error("Receive negative response", e);
        }
        catch (IOException e) {
            log.error("IO error occur", e);
        }
        finally {
            session.unbindAndClose();
        }
    }
}
