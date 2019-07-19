package com.aimir.fep.trap.extgw;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.trap.common.Failure;
import com.aimir.fep.trap.common.OperationTask;
import com.aimir.notification.FMPTrap;



/**
 * safecon export
 *
 * @author Y.S Kim
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class SafeConExportTask extends OperationTask
{
    private Log log = LogFactory.getLog(SafeConExportTask.class);
    private FMPTrap alarm = null;
    private Object result = null;

    /**
     * constructor
     *
     * @param alarm - AU FMPTrap
     */
    public SafeConExportTask(FMPTrap alarm)
    {
        this.alarm = alarm;
    }

    /**
     * execute task
     */
    public void access() throws Failure
    { 
        try
        {
            log.debug("[SafeCon] FMPTrap : " + alarm.toString());
        }
        catch(Exception ex)
        {
            log.error("SafeConExporTask failed : " ,ex);
            throw new Failure();
        }
    }

    /**
     * get result
     * @return data - result object
     */
    public Object getResult()
    {
        return this.result;
    }
}
