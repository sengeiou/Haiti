package com.aimir.fep.trap.extgw;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.SchedulerConfigException;
import org.quartz.simpl.SimpleThreadPool;

import com.aimir.fep.trap.common.OperationTask;
import com.aimir.notification.FMPTrap;

/**
 * IF9 Gate way
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class IF9Gateway
{
    private Log log = LogFactory.getLog(IF9Gateway.class);
    private static IF9Gateway instance= null;
    private SimpleThreadPool pool = null;
    
    /**
     * get IF9 Gateway instance(singletone)
     */
    public static IF9Gateway getInstance()
    {
        if(instance == null)
        {
            instance = new IF9Gateway();
        }

        return instance;
    }

    /**
     * constructor
     */
    private IF9Gateway()
    {
        pool = new SimpleThreadPool();
        try {
            pool.setThreadCount(20);
            pool.initialize();
        }
        catch (SchedulerConfigException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * sent task into Alarm External Gateway Queue
     * @param alarm - AU FMPTrap
     */
    public void processing(FMPTrap alarm)
    {
        try
        {
            OperationTask task = getTargetTask(alarm);
            // so.putOperationTask(task);
            pool.runInThread(task);
        }catch(Exception ex)
        {
            log.error("IF9Gateway failed ", ex);
        }
    }

    private OperationTask getTargetTask(FMPTrap alarm) throws Exception
    {
        /*
         * source type 1 : safecon
         * source type 2 : menix
         * other : unknown
         */
        String sourceType = alarm.getSourceType();
        if (sourceType == null)
        {
            log.debug("alarm source type is null");
            throw new Exception("alarm source type is null");
        }
        else if (sourceType.equals("1"))
        {
            return new SafeConExportTask(alarm);
        }
        else if (sourceType.equals("2"))
        {
            return new MenixExportTask(alarm);
        }
        else
        {
            log.debug("unknown source type");
            throw new Exception("unknown source type");
        }
    }
}
