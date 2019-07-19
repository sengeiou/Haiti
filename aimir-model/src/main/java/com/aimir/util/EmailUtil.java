package com.aimir.util;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EmailUtil
{
    private static Log log = LogFactory.getLog(EmailUtil.class);

    /**
     * JBoss Email Util
     * @param fromMail From email
     * @param toEmail  To email ex) abc@abc.net or abc1@abc.net;abc2@abc.net;abc3@abc.net
     * @param subject
     * @param html
     * @return
     */
    public static int sendData(String fromMail,String toEmail,String subject,String html)
    {
        return sendData(fromMail,toEmail,null,null,subject,html);
    }
    /**
     * JBoss Email Util
     * @param fromMail From email
     * @param toEmail  To email ex) abc@abc.net or abc1@abc.net;abc2@abc.net;abc3@abc.net
     * @param ccEmail  To email ex) abc@abc.net or abc1@abc.net;abc2@abc.net;abc3@abc.net
     * @param bccEmail  To email ex) abc@abc.net or abc1@abc.net;abc2@abc.net;abc3@abc.net
     * @param subject
     * @param html
     * @return
     */
    public static int sendData(String fromMail,String toEmail,String ccEmail, String bccEmail,String subject,String html)
    {
        Session session = null;
        try {
            session = (Session)PortableRemoteObject.narrow(new InitialContext().lookup("java:/Mail"), Session.class);
        } catch (javax.naming.NamingException e) {
            log.error(e);
        }
                                 
        try {
            MimeMessage m = new MimeMessage(session);
            Address from = new InternetAddress(fromMail);
            m.setFrom(from);
            Address[] to = null;
            if(toEmail.indexOf(";")>-1)
            {
                ArrayList<InternetAddress> l = new ArrayList<InternetAddress>();
                String[] temp = toEmail.split(";");
                for(int i=0;i<temp.length;i++)
                {
                    l.add(new InternetAddress(temp[i]));
                }
                to = (InternetAddress[]) l.toArray(new InternetAddress[0]);
            }else
            {
                to = new InternetAddress[] {new InternetAddress(toEmail)};
            }
            Address[] cc = null;
            if(ccEmail != null && ccEmail.indexOf(";")>-1)
            {
                ArrayList<InternetAddress> l = new ArrayList<InternetAddress>();
                String[] temp = ccEmail.split(";");
                for(int i=0;i<temp.length;i++)
                {
                    l.add(new InternetAddress(temp[i]));
                }
                cc = (InternetAddress[]) l.toArray(new InternetAddress[0]);
            }else if(ccEmail != null)
            {
                cc = new InternetAddress[] {new InternetAddress(ccEmail)};
            }
            Address[] bcc = null;
            if(bccEmail != null && bccEmail.indexOf(";")>-1)
            {
                ArrayList<InternetAddress> l = new ArrayList<InternetAddress>();
                String[] temp = bccEmail.split(";");
                for(int i=0;i<temp.length;i++)
                {
                    l.add(new InternetAddress(temp[i]));
                }
                bcc = (InternetAddress[]) l.toArray(new InternetAddress[0]);
            }else if(bccEmail != null)
            {
                bcc = new InternetAddress[] {new InternetAddress(bccEmail)};
            }

            m.setRecipients(Message.RecipientType.TO, to);
            if(ccEmail!=null)
                m.setRecipients(Message.RecipientType.CC, cc);
            if(bccEmail!=null)
                m.setRecipients(Message.RecipientType.BCC, bcc);
            m.setSubject(subject);
            m.setSentDate(new Date());
            m.setContent(html, "text/html");
            Transport.send(m);
            return 0;
        } catch (javax.mail.MessagingException e) {
            log.error("Can't send message : "+toEmail,e);
            return 1;
        }
    }
}
