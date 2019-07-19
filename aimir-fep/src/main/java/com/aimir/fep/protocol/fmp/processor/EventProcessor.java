package com.aimir.fep.protocol.fmp.processor;

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.bems.sender.DataSender;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.log.EventLogger;
import com.aimir.fep.util.DataMIBNode;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBNode;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;

/**
 * Event Service Data Processor
 * 
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2009-11-21 15:59:15 +0900 $,
 */
public class EventProcessor extends Processor
{
    private static Log log = LogFactory.getLog(EventProcessor.class);
    private String protocolVersion = 
        FMPProperty.getProperty("protocol.version","0100");
    
    @Autowired
    private EventLogger logger;
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public EventProcessor() throws Exception
    {
    }

    /**
     * processing Event Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public void processing(Object v, CommLog commLog) throws Exception
    {
        commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("E"));
        commLog.setOperationCode(ProcessorHandler.SERVICE_EVENT);
        commLog.setCnt(""+processing(v));
    }

    /**
     * processing Event Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public int processing(Object sdata) throws Exception
    {
        if(!(sdata instanceof EventData))
        {
            log.debug("EventProcessor sdata["+sdata
                    +"] is not EventData");
            return 0;
        }
        
        EventData ed = (EventData)sdata;
        
        try{
            DataSender ds = (DataSender) DataUtil.getBean("eventDataSender");
            Log edLog = LogFactory.getLog("eventDataSender");
            synchronized(ds){
                boolean result = ds.send(ed);
                edLog.info(
                String.format("Send EventData, OID=%s, status=%s, host=%s",
                        ed.getCode().getValue(), 
                        result?"success":"fail",
                                ds.getHostname())
                );
            }
        }catch(Exception be){
            log.debug(be);
        }
        
        FMPTrap trap = new FMPTrap();

        trap.setProtocolName("FMP");
        trap.setProtocolVersion(protocolVersion);
        trap.setMcuId(ed.getMcuId());
        trap.setIpAddr(ed.getIpAddr());
        trap.setCode(ed.getCode().toString());
        trap.setSourceType(ed.getSrcType().toString());
        trap.setSourceId(ed.getSrcId().toString());
        if(ed.getSrcType().toString().equals("2")) {
            trap.setSourceId(ed.getMcuId().toString());
        }
        trap.setTimeStamp(ed.getTimeStamp().toString());

        VarBinds vb = new VarBinds();
        SMIValue[] smiValues = ed.getSMIValue();
        String oid;
        FMPVariable variable;
        Object obj;
        //이벤트에 들어오는 smivalues 리스트
        int idx = 1;
        for(int i = 0 ; i < smiValues.length ; i++)
        {
            //oid = mibUtil.getName(smiValues[i].getOid().toString());
            oid = smiValues[i].getOid().toString();
            variable = smiValues[i].getVariable();
            //중복되는 oid가 smivalues에 실려서 오는 경우 x.x.x.x의 네자리 형태의 OID로 만들어준다
            if(vb.containsKey(oid)) {
                oid = oid+"."+idx++;
            }
            vb.put(oid,variable);
        }
        trap.setVarBinds(vb);

        StringBuffer sb = new StringBuffer();
        sb.append("FMPTrap - \n");
        sb.append("code = ").append(trap.getCode()).append('\n');
        sb.append("sourceType = ").append(trap.getSourceType()).append('\n');
        sb.append("sourceId = [").append(trap.getSourceId().trim()).append("]\n");
        sb.append("timeStamp = ").append(trap.getTimeStamp()).append('\n');
        vb = trap.getVarBinds();
        Iterator iter = vb.keySet().iterator();
        String type = null;
        String val = null;
        while(iter.hasNext())
        {
            String key =(String)iter.next();
            variable= (FMPVariable)vb.get(key);
            if(variable != null)
            {
                type = variable.getSyntaxString();
                val = variable.toString();
            }else
            {
                MIBNode node =
                    MIBUtil.getInstance().getMIBNodeByOid(key);
                if(node!= null)
                {
                    String vtype = ((DataMIBNode)node).getType();
                    variable = FMPVariable.getFMPVariableObject(vtype);
                    if(variable == null) {
                        type = "java.lang.String";
                    }
                    else {
                        type = variable.getSyntaxString();
                    }
                    val = "";
                } else
                {
                    type = "java.lang.String";
                    val = "";
                }
            }
            sb.append("oid=").append(key).append(", type=");
            sb.append(type).append(", val=").append(val).append('\n');
        }

        log.info(sb.toString());
        logger.sendLog(trap);
        
        return 1;
    }

    @Override
    public void restore() throws Exception {
        logger.init();
        logger.resendLogger();
    }
}
