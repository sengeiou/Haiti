package com.aimir.fep.protocol.fmp.processor;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData_1_2;
import com.aimir.fep.protocol.fmp.log.EventLogger;
import com.aimir.fep.util.DataMIBNode;
import com.aimir.fep.util.MIBNode;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;

/**
 * NEvent Service Data Processor
 * 
 * @author goodjob (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2014-08-25 10:00:00 +0900 $,
 */
public class EventProcessor_1_2 extends Processor
{
    private String protocolVersion = "0102"; //protocol ver 1.2
    
    @Autowired
    private EventLogger logger = null;
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public EventProcessor_1_2() throws Exception
    {
    }

    /**
     * processing Event Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public int processing(Object sdata) throws Exception
    {
        if(!(sdata instanceof EventData_1_2))
        {
            log.debug("EventProcessor_1_2 sdata["+sdata
                    +"] is not EventData");
            return 0;
        }
        
        EventData_1_2 ed = (EventData_1_2)sdata;
        
        FMPTrap trap = new FMPTrap();

        trap.setProtocolName("FMP");
        trap.setProtocolVersion(protocolVersion);
        trap.setMcuId(ed.getMcuId());
        trap.setIpAddr(ed.getIpAddr());
        trap.setCode(ed.getNameSpace().toString()+"_"+ed.getOid().toString());
        trap.setSourceType(ed.getSrcType().toString());
        trap.setSourceId(ed.getSrcId().toString());
        if(ed.getSrcType().toString().equals("2")) {
            trap.setSourceId(ed.getMcuId().toString());
        }
        trap.setTimeStamp(ed.getTimeStamp().toString());
        if(ed.getNameSpace() != null && !ed.getNameSpace().equals("")) {
            trap.setNameSpace(ed.getNameSpace().toString());        	
        }
        
        VarBinds vb = new VarBinds();
        SMIValue[] smiValues = ed.getSMIValue();
        String oid;
        FMPVariable variable;
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
        
        StringBuilder sb = new StringBuilder();
        sb.append("FMPTrap - \n");
        sb.append("code = ").append(trap.getCode()).append('\n');
        
        sb.append("dcu = ").append(trap.getMcuId()).append('\n');
        sb.append("ipAddr = ").append(trap.getIpAddr()).append('\n');

        sb.append("sourceType = (" + trap.getSourceType() + ") " + ServiceDataConstants.EventSRCType.getCode(Integer.parseInt(trap.getSourceType())).name()).append('\n');
        sb.append("sourceId = [").append(trap.getSourceId().trim()).append("]\n");
        sb.append("timeStamp = ").append(trap.getTimeStamp()).append('\n');
        if(ed.getNameSpace() != null && !ed.getNameSpace().equals("")) {
        	sb.append("nameSpace = ").append(trap.getNameSpace()).append('\n');        	
        }else {
        	sb.append("nameSpace = ").append("null~!!!").append('\n');
        }
         
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
                MIBNode node = MIBUtil.getInstance(ed.getNameSpace().toString()).getMIBNodeByOid(key);
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

        log.debug(sb.toString());
        logger.sendLog(trap);
        
        return 1;
    }
    
    /**
     * processing Event Service Data
     *
     * @param sdata <code>Object</code> ServiceData
     */
    public void processing(Object sdata, CommLog commLog) throws Exception
    {
        commLog.setSvcTypeCode(CommonConstants.getHeaderSvc("E"));
        commLog.setOperationCode(ProcessorHandler.SERVICE_EVENT_1_2);
        
        processing(sdata);
    }

    @Override
    public void restore() throws Exception {
        logger.init();
        logger.resendLogger();
    }
}
