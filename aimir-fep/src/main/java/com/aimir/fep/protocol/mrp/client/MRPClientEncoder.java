package com.aimir.fep.protocol.mrp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderException;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
/**
 * Encodes MCU Communication Stream  into General Data Frame.
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class MRPClientEncoder extends ProtocolEncoderAdapter
{
    private static Log log = LogFactory.getLog(MRPClientEncoder.class);

    /**
     * encode MRP Protocol Frame
     *
     * @param session <code>IoSession</code> session
     * @param message <code>Object</code> GeneralDataFrame or IoBuffer
     * @param out <code>ProtocolEncoderOutput</code> save encoded byte stream
     * @throws ProtocolViolationException  indicating that is was error of Encode
     */
    public void encode( IoSession session, Object message,
                       ProtocolEncoderOutput out )
            throws ProtocolEncoderException
    { 
        try 
        {
            if(message instanceof IoBuffer) 
            { 
                IoBuffer buffer =  (IoBuffer)message; 
                log.debug("Sended["+session.getRemoteAddress()
                       +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            }
            else if(message instanceof byte[])
            {
                byte[] bx = (byte[])message;
                IoBuffer buffer = IoBuffer.allocate(bx.length);
                buffer.put(bx);
                buffer.flip();
                log.debug("Sended["+session.getRemoteAddress() 
                        +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            }
            else if (message instanceof ControlDataFrame) {
                ControlDataFrame cdf = (ControlDataFrame)message;
                byte[] bx = cdf.encode();
                IoBuffer buffer = IoBuffer.allocate(bx.length);
                buffer.put(bx);
                buffer.flip();
                log.debug("Sended["+session.getRemoteAddress() 
                        +"] : " + buffer.limit() + " : " 
                        + buffer.getHexDump()); 
                out.write(buffer); 
            }
            else
            {
                log.debug("Sended Message  does not Supported format[" + message.getClass().getName() + "]");
            }
        }catch(Exception ex)
        {
            log.error("encode failed " + message, ex); 
            throw new ProtocolEncoderException( ex.getMessage());
        }
    }

}
