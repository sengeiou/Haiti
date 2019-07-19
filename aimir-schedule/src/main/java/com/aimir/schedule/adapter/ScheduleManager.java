package com.aimir.schedule.adapter;

import java.io.IOException;
import java.util.Properties;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.JmxException;

import com.aimir.fep.util.CmdUtil;
import com.aimir.util.StringUtil;

public class ScheduleManager {
	private static Log log = LogFactory.getLog(CmdUtil.class);

	private JMXConnector jmxc = null;

	public ScheduleProxyMBean getScheduleProxy() throws Exception {
		return getScheduleProxy(null);
	}
	
	public ScheduleProxyMBean getScheduleProxy(String toTargetProperty) throws Exception {

		jmxc = getNewConnection(toTargetProperty);
		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
		//        ObjectName objectName = new ObjectName("service:name=ScheduleProxy");
		ObjectName objectName = new ObjectName("Service:name=ScheduleProxy");
		ScheduleProxyMBean schedule = JMX.newMBeanProxy(mbsc, objectName, ScheduleProxyMBean.class, true);
		return schedule;
	}

	public void close() throws IOException {

		try {
			jmxc.close();
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			jmxc = null;
		}
	}

	private JMXConnector getNewConnection(String toTargetProperty) throws JmxException {
		JMXConnector jmxc = null;

		String jmxurl = getJMXUrlPath(toTargetProperty);

		try {

			JMXServiceURL url = new JMXServiceURL(jmxurl);
			//JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://187.1.10.200:1099/jmxrmi");

			log.info("About to jmxcnect to : " + url.toString());

			jmxc = JMXConnectorFactory.connect(url);

		} catch (Exception e) {
			log.error(e, e);
		}

		return jmxc;
	}

	private String getJMXUrlPath(String toTargetProperty) {
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
			return prop.getProperty(StringUtil.nullToString(toTargetProperty, "schedule.jmxrmi"));
		} catch (Exception e) {

			try {
				//schedule의 경우 aimir-schedule-exec/config/command.properties에 존재
				prop.load(getClass().getClassLoader().getResourceAsStream("config/command.properties"));
				return prop.getProperty(StringUtil.nullToString(toTargetProperty, "fep.jmxrmi"));
			} catch (Exception ex) {
				log.error(ex, ex);
				return "service:jmx:rmi:///jndi/rmi://localhost:1100/jmxrmi";
			}

		}
	}
	
//	private String getJMXUrlPath() {
//		Properties prop = new Properties();
//		try {
//			prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
//			return prop.getProperty("schedule.jmxrmi");
//		} catch (Exception e) {
//
//			try {
//				//schedule의 경우 aimir-schedule-exec/config/command.properties에 존재
//				prop.load(getClass().getClassLoader().getResourceAsStream("config/command.properties"));
//				return prop.getProperty("fep.jmxrmi");
//			} catch (Exception ex) {
//				log.error(ex, ex);
//				return "service:jmx:rmi:///jndi/rmi://localhost:1100/jmxrmi";
//			}
//
//		}
//	}

}
