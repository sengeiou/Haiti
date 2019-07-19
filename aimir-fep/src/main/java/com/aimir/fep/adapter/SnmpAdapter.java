package com.aimir.fep.adapter;

import java.lang.management.ManagementFactory;
import java.util.Date;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.snmp.SnmpManagerAdapter;
import com.aimir.fep.util.DataUtil;

/**
 * @author Hansejin
 */
public class SnmpAdapter {

    private static Log logger = LogFactory.getLog(SnmpAdapter.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";

    private String fepName;
    private String community;
    private String trapPort;
    private String subPort;

    public void init(String _community, String _trapPort, String _subPort) throws Exception {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);

        this.community = _community;
        this.trapPort = _trapPort;
        this.subPort = _subPort;

        logger.info("init::name=["+fepName+ "] community=["+community+"] trapPort=["+trapPort+"] subPort=["+subPort+"]");
    }

    public void startService() throws Exception {
        logger.info("Register SnAdapter");

        registerSnAdapter();

        logger.info("["+fepName+"] Manager is Ready for Service...");
    }

    public void registerSnAdapter() throws Exception {
        String name = "SNMPAdapter";
        if (trapPort != null && !"".equals(trapPort)) {
            logger.info("Register SnAdapter.class");

            SnmpManagerAdapter adapter = new SnmpManagerAdapter();
            adapter.setPort(Integer.parseInt(trapPort));

            ObjectName adapterName = new ObjectName(ADAPTER_DOMAIN + ":name=" + name + ", port=" + trapPort);

            registerMBean(adapter, adapterName);
            adapter.start();

            logger.info("SNMPAdapter ready.");
        }
    }
    
    private void registerMBean(Object obj, ObjectName objName)
    throws Exception
    {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(obj, objName);
    }

    public ObjectName registerMBean(String dom, String name, String clsname)
    throws Exception
    {
        ObjectName objName = null;

        objName = new ObjectName(dom+":name="+name);
        registerMBean(Class.forName(clsname).newInstance(), objName);
        return objName;
    }


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        Date startDate = new Date();
        long startTime = startDate.getTime();

        String community = "asm";
        String trapPort = "162";
        String subPort = "163";

//        for (int i = 0; i < args.length; i++) {
//            logger.debug("### args[{}] => {}", i, args[i]);
//        }

        for (int i = 0; i < args.length; i += 2) {
            String nextArg = args[i];
            if (nextArg.startsWith("-community") ){
                community = new String(args[i+1]);
            } else if (nextArg.startsWith("-trapPort")) {
                trapPort = new String(args[i + 1]);
            } else if (nextArg.startsWith("-subPort")) {
                subPort = new String(args[i + 1]);
            }
        }

        try{
            //Audit Target Name
            String fepName = System.getProperty("name");
            if (fepName == null || fepName.equals("")) {
                System.setProperty("aimir.auditTargetName", "SNMP-Trapd");
            } else {
                System.setProperty("aimir.auditTargetName", fepName);
            }

            //ctx
            ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "/config/spring-snmp.xml" });
            DataUtil.setApplicationContext(applicationContext);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        SnmpAdapter fep = new SnmpAdapter();
        try {
            fep.init(community,trapPort, subPort);
            fep.startService();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(1);
        }

        long endTime = System.currentTimeMillis();
        logger.info("######## SNMPAdapter-Starting Elapse Time : [" + (endTime - startTime) / 1000.0f + "]s");
      
    }

}
