package com.aimir.fep.protocol.fmp.processor;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.protocol.fmp.datatype.FMPVariable;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataConstants;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.amu.AMUFramePayLoadConstants;
import com.aimir.fep.protocol.fmp.frame.amu.EventDataPowerOutage;
import com.aimir.fep.protocol.fmp.log.AMUEventLogger;
import com.aimir.fep.util.DataMIBNode;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.device.CommLog;
import com.aimir.notification.FMPTrap;
import com.aimir.notification.VarBinds;
import com.aimir.util.TimeUtil;

/**
 * AMU Protocol Event Frame Data Processor
 *
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 2. 23. 오후 1:19:25$
 */
public class AMUEventProcessor extends Processor {

	private String protocolVersion = 
        FMPProperty.getProperty("protocol.version","0100");
	
	@Autowired
    private AMUEventLogger amuEventLogger;
    
    
    /**
     * constructor
     *
     * @throws Exception
     */
    public AMUEventProcessor() throws Exception
    {
    	// spring-listener.xml에 등록된 Log Name
    	// amuEventLogger.init();
    }
    
    /**
     * processing AMU Event Data
     *
     * @param sdata <code>Object</code> AMUGeneralDataFrame
     */
    public int processing(Object sdata) throws Exception
    {
        
        if(!(sdata instanceof AMUGeneralDataFrame))
        {
            log.debug("AMUEventProcessor sdata["+sdata
                    +"] is not AMUGeneralDataFrame");
            return 0;
        }
        
        AMUGeneralDataFrame gdf = (AMUGeneralDataFrame)sdata;
        
        if( gdf.getAmuFrameControl().getFrameType() != AMUGeneralDataConstants.FRAMETYPE_EVENT){
            log.debug("AMUEventProcessor sdata["+sdata+"] is not Event Frame");
            return 0;
        }
        
        /* ********************* Save Event Log *************************** */
        FMPTrap trap = new FMPTrap();
        trap.setProtocolName("AMU");
        trap.setProtocolVersion(protocolVersion);
        trap.setMcuId(getAMUMcuId(gdf));                
        trap.setIpAddr(new String(gdf.getDest_addr()));
        
        MIBUtil mibUtil = MIBUtil.getInstance();
        String oidNm = AMUFramePayLoadConstants.getEventIdentifierName(gdf.getAmuFramePayLoad().getIdentifier());
        trap.setCode(mibUtil.getOid(oidNm).toString());
        
        trap.setSourceType(gdf.getAmuFrameControl().getSourceType());                       // Event가 발생한 source의 타입
        trap.setSourceId(gdf.getAmuFrameControl().getSourceAddr(gdf.getSource_addr()));     // Event가 발생한 source의 ID
        trap.setTimeStamp(TimeUtil.getCurrentTime());   //현재시간

        VarBinds vb = new VarBinds();
        Object key = AMUFramePayLoadConstants.VARIABLE_OID;
        OCTET oidValue = new OCTET(gdf.getAmuFramePayLoad().getPayLoadData(), gdf.getAmuFramePayLoad().getPayLoadData().length, true);
        
        vb.put(key, oidValue);
        trap.setVarBinds(vb);

        StringBuffer sb = new StringBuffer();
        sb.append("AMUFMPTrap - \n");
        sb.append("code = ").append(trap.getCode()).append('\n');
        sb.append("sourceType = ").append(trap.getSourceType()).append('\n');
        sb.append("sourceId = [").append(trap.getSourceId().trim()).append("]\n");
        sb.append("timeStamp = ").append(trap.getTimeStamp()).append('\n');  
        sb.append("oid=").append(AMUFramePayLoadConstants.VARIABLE_OID).append(", type=");
        
        vb = trap.getVarBinds();
        FMPVariable variable= (FMPVariable)vb.get(key);
        String type = null;
        String val = null;
        if(variable != null)
        {
            type = variable.getSyntaxString();
            val = variable.toString();
        }else
        {
            DataMIBNode node =
                (DataMIBNode)MIBUtil.getInstance().getMIBNodeByOid((String)key);
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
            }else{
                type = "java.lang.String";
                val = "";
            }
        }
        sb.append("oid=").append(key).append(", type=");
        sb.append(type).append(", val=").append(val).append('\n');
       
        log.debug(sb.toString());
        amuEventLogger.sendLog(trap);
        
        return 1;
    }
    
    /**
     * processing AMU Event Data
     *
     * @param sdata <code>Object</code> AMUGeneralDataFrame
     */
    public void processing(Object sdata, CommLog commLog) throws Exception
    {
    	
        if(!(sdata instanceof AMUGeneralDataFrame))
        {
            log.debug("AMUEventProcessor sdata["+sdata
                    +"] is not AMUGeneralDataFrame");
            return;
        }
        
        AMUGeneralDataFrame gdf = (AMUGeneralDataFrame)sdata;
        
        if( gdf.getAmuFrameControl().getFrameType() != AMUGeneralDataConstants.FRAMETYPE_EVENT){
        	log.debug("AMUEventProcessor sdata["+sdata+"] is not Event Frame");
            return;
        }
        
        /* ********************* Save Event Log *************************** */
        FMPTrap trap = new FMPTrap();
        trap.setProtocolName("AMU");
        trap.setProtocolVersion(protocolVersion);
        trap.setMcuId(getAMUMcuId(gdf));				
        trap.setIpAddr(new String(gdf.getDest_addr()));
        
        MIBUtil mibUtil = MIBUtil.getInstance();
        String oidNm = AMUFramePayLoadConstants.getEventIdentifierName(gdf.getAmuFramePayLoad().getIdentifier());
        trap.setCode(mibUtil.getOid(oidNm).toString());
        
        trap.setSourceType(gdf.getAmuFrameControl().getSourceType());						// Event가 발생한 source의 타입
        trap.setSourceId(gdf.getAmuFrameControl().getSourceAddr(gdf.getSource_addr()));		// Event가 발생한 source의 ID
        trap.setTimeStamp(TimeUtil.getCurrentTime());	//현재시간

        VarBinds vb = new VarBinds();
        Object key = AMUFramePayLoadConstants.VARIABLE_OID;
        OCTET oidValue = new OCTET(gdf.getAmuFramePayLoad().getPayLoadData(), gdf.getAmuFramePayLoad().getPayLoadData().length, true);
        
        vb.put(key, oidValue);
        trap.setVarBinds(vb);

        StringBuffer sb = new StringBuffer();
        sb.append("AMUFMPTrap - \n");
        sb.append("code = ").append(trap.getCode()).append('\n');
        sb.append("sourceType = ").append(trap.getSourceType()).append('\n');
        sb.append("sourceId = [").append(trap.getSourceId().trim()).append("]\n");
        sb.append("timeStamp = ").append(trap.getTimeStamp()).append('\n');  
        sb.append("oid=").append(AMUFramePayLoadConstants.VARIABLE_OID).append(", type=");
        
        vb = trap.getVarBinds();
        FMPVariable variable= (FMPVariable)vb.get(key);
        String type = null;
        String val = null;
        if(variable != null)
        {
            type = variable.getSyntaxString();
            val = variable.toString();
        }else
        {
            DataMIBNode node =
                (DataMIBNode)MIBUtil.getInstance().getMIBNodeByOid((String)key);
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
            }else{
                type = "java.lang.String";
                val = "";
            }
        }
        sb.append("oid=").append(key).append(", type=");
        sb.append(type).append(", val=").append(val).append('\n');
       
        log.debug(sb.toString());
        amuEventLogger.sendLog(trap);
    }

    @Override
    public void restore() throws Exception {
        amuEventLogger.init();
        amuEventLogger.resendLogger();
    }
    
    /**
     * get AMU MCU ID
     * 
     * No address, IP address , EUI64ID , Mobile Nunber 중
     * IP address, Mobile Nunber외 No address ,EUI64ID Error 처리
     * EUI64ID는 Sernor에서 올라오는 Data로 전화번호가 없으므로 일단은 Error처리
     * @param agdf
     * @return
     * @throws Exception 
     */
    private String getAMUMcuId(AMUGeneralDataFrame agdf) throws Exception{
    	
    	byte[] sourceAddr 	= agdf.getSource_addr();
    	byte sourceAddrType = agdf.getAmuFrameControl().getSourceAddrType();
    	log.debug("Source Addr Type :  " + sourceAddrType + ": " + agdf.getAmuFrameControl().getSourceAddrDesc());
    	/**
    	 * Event Power OutAge만 따로 처리 해야되는건지 확인 필요
    	 */
    	if(agdf.getAmuFramePayLoad().getIdentifier() == AMUFramePayLoadConstants.FrameIdentifier.EVENT_POWER_OUTAGE){
    		EventDataPowerOutage po = (EventDataPowerOutage) agdf.getAmuFramePayLoad();
    		return po.getMcuID();
    	}else{
    		
    		switch(sourceAddrType){
			case (byte)0x01 :
				return 	DataUtil.encodeIpAddrToStr(sourceAddr);
			case (byte)0x03 :
				return	DataUtil.encodeMobileAddrToStr(sourceAddr);
			default :
				throw new Exception("Cant't Found AMU MCU ID");
    		}
    	}
    	
    }
}


