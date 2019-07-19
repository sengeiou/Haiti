/**
 *@(#)ScheduleAdapter.java
 *
 */
package com.aimir.schedule.adapter;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.impl.StdScheduler;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.util.DataUtil;


/**
 * ScheduleAdapter Start Service
 *
 * @author park yeon kyoung (goodjob@nuritelecom.com)
 */
public class ScheduleAdapterDebug
{
    private static Log log = LogFactory.getLog(ScheduleAdapterDebug.class);

    public static void main( String[] args ) throws Exception
    {
        if (args.length < 1 ) {
            log.info("Usage:");
            log.info("ScheduleAapter -DscName SchedulerName -jmxPort AdapterPort -springContext SpringContextFileName");
            return;
        }

//        String scFileName = "spring-quartz.xml";
//
//        for (int i=0; i < args.length; i+=2) {
//
//            String nextArg = args[i];
//
//            if (nextArg.startsWith("-springContext")) {
//                scFileName = new String(args[i+1]);
//            }
//        }

        String scFileName = "spring-quartz-soria.xml"; // For debuging
        
        /**
         * Audit Target Name 설정.
         */
		String scName = System.getProperty("scName");
        if(scName == null || scName.equals("")){
            System.setProperty("aimir.auditTargetName", "SCHEDULER");
        }else{
            System.setProperty("aimir.auditTargetName", scName);
        }

    	DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(scFileName));
    	StdScheduler scheduler = (StdScheduler)DataUtil.getBean("schedulerFactory");
    	ObjectName adapterName = null;
        ScheduleProxy sp = new ScheduleProxy(scheduler);

//        adapterName = new ObjectName("service:name=ScheduleProxy");
        adapterName = new ObjectName("Service:name=ScheduleProxy");
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        mbs.registerMBean(sp, adapterName);
        sp.start();
        
        log.info("########## Scheduler Start #########");
    }




}

