package com.aimir.schedule.command;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.JmxException;

import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.command.ws.client.CommandWS_Service;
import com.aimir.util.TimeUtil;

public final class CmdPool {
	
    private static Log log = LogFactory.getLog(CmdPool.class);

	private Vector<JMXConnector> free;
	private Vector<JMXConnector> used;

	private int initialCons = 0;
	private int maxCons = 0;
	private int numCons = 0;
	private boolean block;
	private long timeout;
	private boolean reuseCons = false;
	private Protocol protocol = null;

	public CmdPool(int initialCons, int maxCons, int timeout, boolean block, Protocol protocol) throws IOException {

		this.initialCons = initialCons;
		this.maxCons = maxCons;
		this.block = block;
		this.timeout = timeout;

		if (maxCons > 0 && maxCons < initialCons) {
			this.initialCons = maxCons;
		}

		free = new Vector<JMXConnector>(this.initialCons);
		used = new Vector<JMXConnector>(this.initialCons);

		this.protocol = protocol;
		
		while (numCons < this.initialCons) {
			addConnection();
		}
	}

	private void addConnection() throws IOException {
		free.addElement(getNewConnection());
	}
	
	private String getJMXUrlPath(){
		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream(
					"command.properties"));
		} catch (Exception e) {
			
			try{
				//schedule의 경우 aimir-schedule-exec/config/command.properties에 존재
				prop.load(getClass().getClassLoader().getResourceAsStream(
				"config/command.properties"));
			}catch(Exception ex) {
				log.error(ex,ex);
				return "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";
			}
		}
		
		String jmxUrl = null;
		if (protocol != null)
		    jmxUrl = prop.getProperty("fep.jmxrmi." +protocol.name());
		
		if (jmxUrl == null || "".equals(jmxUrl))
		    jmxUrl = prop.getProperty("fep.jmxrmi");
		
		return jmxUrl;
	}
	
	public URL getURL() throws MalformedURLException {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream(
                    "command.properties"));
        } catch (Exception e) {
            
            try{
                //schedule의 경우 aimir-schedule-exec/config/command.properties에 존재
                prop.load(getClass().getClassLoader().getResourceAsStream(
                "config/command.properties"));
            }catch(Exception ex) {
                log.error(ex,ex);
                return CommandWS_Service.WSDL_LOCATION;
            }
        }
        
        String url = null;
        if (protocol != null)
            url = prop.getProperty("fep.ws." +protocol.name());
        
        if (url == null || "".equals(url))
            url = prop.getProperty("fep.ws");
        
        return new URL(url);
    }

	private JMXConnector getNewConnection() throws IOException {
		JMXConnector jmxc = null;
		
		String jmxurl = getJMXUrlPath();
		
	    JMXServiceURL url = new JMXServiceURL(jmxurl);
	    //JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://187.1.10.200:1099/jmxrmi");

		log.info("[" + protocol + "] About to jmxcnect to : " + url.toString());

		jmxc = JMXConnectorFactory.connect(url);
			
		
		++numCons;
		log.info("[" + protocol + "] NumberOf Connections:"+numCons);
		return jmxc;
	}

	public JMXConnector getConnection() throws IOException {
		return getConnection(this.block, timeout);
	}

	public synchronized JMXConnector getConnection(boolean block, long timeout)
			throws IOException {
		log.info("[" + protocol + "] maxCons:"+maxCons+" numCons:"+numCons);
		if (free.isEmpty()) {
			if (maxCons <= 0 || numCons < maxCons) {
				log.info("[" + protocol + "] maxCons:"+maxCons+" numCons:"+numCons);
				addConnection();
			} else if (block) {
				log.info("[" + protocol + "] maxCons:"+maxCons+" numCons:"+numCons);
				try {
					long start = TimeUtil.getCurrentLongTime();
					do {
						wait(timeout);
						if (timeout > 0) {
							timeout -= TimeUtil.getCurrentLongTime() - start;
							if (timeout == 0) {
								timeout = -1;
							}
						}
					} while (timeout >= 0 && free.isEmpty() && maxCons > 0
							&& numCons >= maxCons);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (free.isEmpty()) {
					if (maxCons <= 0 || numCons < maxCons) {
						addConnection();
					} else {
						throw new JmxException("time out");
					}
				}
			} else {// block 되지 않겠다고 했을 때
				throw new JmxException("[" + protocol + "] maximum number");
			}
		}
		JMXConnector jmxc;
		synchronized (used) {
			jmxc = (JMXConnector) free.lastElement();
			free.removeElement(jmxc);
			used.addElement(jmxc);
		}
		return jmxc;
	}

	public synchronized void releaseConnection(JMXConnector jmxc)
			throws IOException {
		boolean reuseThisCon = reuseCons; // 다쓰고나선 release해서 다시돌려준다
		if (used.contains(jmxc)) {
			used.removeElement(jmxc);
			numCons--;
			log.debug("[" + protocol + "]numCons="+numCons);
		} else {
			throw new JmxException("[" + protocol + "] Connection " + jmxc
					+ "didn't come from this Connectionpool");
		}
		try {
			if (reuseThisCon) {
				free.addElement(jmxc);
				numCons++;
			} else {
				jmxc.close();
				log.debug("[" + protocol + "] jmxc close="+numCons);
			}
			notify(); // 돌려주는시점에 notify()된다
		} catch (JmxException e) {
			try {
				jmxc.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			notify();
		}
	}

	public synchronized void closeAll() {
		Enumeration jmxcs = ((Vector) free.clone()).elements();
		while (jmxcs.hasMoreElements()) {
			JMXConnector jmxc = (JMXConnector) jmxcs.nextElement();
			free.removeElement(jmxc);
			numCons--;
			try {
				jmxc.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		jmxcs = ((Vector) used.clone()).elements();
		while (jmxcs.hasMoreElements()) {
			JMXConnector jmxc = (JMXConnector) jmxcs.nextElement();
			used.removeElement(jmxc);
		}
	}

}
