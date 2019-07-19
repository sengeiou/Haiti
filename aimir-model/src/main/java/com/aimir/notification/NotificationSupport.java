package com.aimir.notification;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.MessageNotWriteableException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;


/** Notification과 JMS 메시지 간의 변환에 사용할 수 있는 메소드를 제공한다.
 * Notification을 JMS 메시지로 변환할 때는 다음의 규칙을 따른다.<p>
 *
 * <ui>
 *		<li>Notificaion은 JMS ObjectMessage로 변환 된다.
 *		<li>Notification object는 ObjectMessage의 body에 저장된다.
 *		<li>Notification의 프라퍼티 중 primitive type은
 * 			JMS 메시지의 프라퍼티로도 저장되어 selector를
 *			통한 filtering에 사용할 수 있다.
 * </ui>
 *
 * @author <a href="mailto:jaehwang@nuritelecom.com">Jae-Hwang Kim</a>
 */
public class NotificationSupport {
    @SuppressWarnings("unused")
    private static Log _log = LogFactory.getLog(NotificationSupport.class);

	private static PropertyDescriptor[] getPropertyDescriptors(Class<? extends Notification> cl)
											throws IntrospectionException{
		BeanInfo bi =
			Introspector.getBeanInfo(cl, Notification.class.getSuperclass());
		PropertyDescriptor[] propertyDescriptors = null;
		propertyDescriptors = bi.getPropertyDescriptors();
		return propertyDescriptors;
	}

	private static Object getPropertyValue(Object bean, Method method) {
		try {
			return method.invoke(bean,(Object[])null);
		} catch(Exception e) {
			return null;
		}
	}

	/** JMS Message를 Notification으로 변환한다.
	 * <code>
	 * 		return message.getObject();
	 * </code>
	 */
	public static Notification createNotification(Message message)
														throws Exception {
        if (message == null) {
            throw new Exception("Wrong message: " + "message is null");
        }

        Object obj = null;
        if (message instanceof ObjectMessage) {
			obj = ((ObjectMessage)message).getObject();
		}

		if(obj instanceof Notification) {
			return (Notification)obj;
		} else {
			throw new Exception("Wrong class: "+
								"Message body should have Notification object");
		}
	}

	/** Notification을 JMS Message로 변환 한다.
	 */
	public static ObjectMessage createMessage
					(Notification notification, Session session)
					throws	IntrospectionException,
							JMSException,
							MessageFormatException,
							MessageNotWriteableException{

		PropertyDescriptor[] propertyDescriptor =
							getPropertyDescriptors(notification.getClass());

		ObjectMessage message =
						session.createObjectMessage((Serializable)notification);

		for(int i=0; i<propertyDescriptor.length; i++) {
			Method method = propertyDescriptor[i].getReadMethod();
			String name = propertyDescriptor[i].getName();
			Object val = getPropertyValue(notification, method);
			try {
				message.setObjectProperty(name, val);
			} catch(MessageFormatException e) {
				// Ignore
			}
		}
        /* 20040317 : rewriter
        * Persistent Message로 인해서 DB Error가 많이 나기 때문에
        * Non_persistent Message로 변경한다.
        *
        */
        message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);

		return message;
	}
}
