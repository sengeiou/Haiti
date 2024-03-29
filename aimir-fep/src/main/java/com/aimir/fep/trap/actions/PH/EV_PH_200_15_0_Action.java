package com.aimir.fep.trap.actions.PH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Util;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.device.SubGiga;
import com.aimir.notification.FMPTrap;
import com.google.gson.JsonObject;

/**
 * Event ID : 200.15.0 evtModemJoined
 *
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-05-24 15:59:15 +0900 $,
 */
@Service
public class EV_PH_200_15_0_Action implements EV_Action
{
    private static Log log = LogFactory.getLog(EV_PH_200_15_0_Action.class);
    
    @Autowired
    JpaTransactionManager txmanager;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    LocationDao locationDao;
    
    @Autowired
    SupplierDao supplierDao;
    
    @Autowired
    CodeDao codeDao;
    
    @Autowired
    ModemDao modemDao;
    
    @Autowired
    CommandGW gw;
    
    /**
     * execute event action
     *
     * @param trap - FMP Trap(MCU Event)
     * @param event - Event Data
     */
    public void execute(FMPTrap trap, EventAlertLog event) throws Exception
    {
        log.debug("EventCode[" + trap.getCode()
                +"] MCU["+trap.getMcuId()+"]");

        // Initialize
        String mcuId = trap.getMcuId();
        String ipAddr = trap.getIpAddr();
        MCU mcu = mcuDao.get(mcuId);

        log.debug("IP[" + ipAddr + "]");
        String modemId = event.getEventAttrValue("moSPId");
        
        //1.9.0	authType	INT	1					Authentication Protocol Type(0: 3Pass, 1: PANA)        
      
        String authType = "0";
        String panaAuthResultCode = "Unknown";
        String panaAuthResult = "";       
        
        authType = event.getEventAttrValue("intEntry") != null && !"".equals(event.getEventAttrValue("intEntry")) ? event.getEventAttrValue("intEntry") : "0";
        panaAuthResultCode = event.getEventAttrValue("byteEntry") != null && !"".equals(event.getEventAttrValue("byteEntry")) ? event.getEventAttrValue("byteEntry") : "Unknown";
    	if(panaAuthResultCode.equals("0")){
    		panaAuthResult = "Success";
    	}else{
    		panaAuthResult = "Fail, ERROCODE=["+panaAuthResultCode+"]";
    	}
        
        log.debug("MODEM_ID[" + modemId + "]");
        
        TransactionStatus txstatus = null;
        try {
            txstatus = txmanager.getTransaction(null);
            
            // get modem and if null, create modem
            Modem modem = modemDao.get(modemId);
            
            if (modem == null) {
                modem = new SubGiga();
    
                modem.setDeviceSerial(modemId);
                modem.setModemType(ModemType.SubGiga.name());
                modem.setProtocolType(Protocol.IP.name());
                modem.setSupplier(mcu.getSupplier());
                modem.setLocation(mcu.getLocation());
                modem.setNameSpace("PH");
                modem.setProtocolVersion("0102");
                modem.setMcu(mcu);
                
                if (mcu != null) {
                    ((SubGiga)modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), modemId));
                }
                
                //TODO ipv6 from mcu
                modemDao.add(modem);
            }
            else {
                // SP-460
                MCU orgMcu = modem.getMcu();
                if(orgMcu != null){
                    String orgMcuSysId = orgMcu.getSysID();
                    if(orgMcuSysId != null){
                        if(mcuId.equals(orgMcuSysId)){
                            //set the authType to 'abnormal' and skip
                            authType = AuthType.ABNORMAL.getAuthType();
                            throw new Exception();
                        }
                    }
                }

                //modem.setMcu(mcu);
                
                //if (mcu != null) {
                //    ((SubGiga)modem).setIpv6Address(Util.getIPv6(mcu.getIpv6Addr(), modemId));
                //}
                
                //modemDao.update(modem);
                
                //Since the modem can be moved again even if it is joined, the connection information is sure to be received after receiving the modem install event.
                //Therefore, this update logic is disabled.
            }
            
            
            EventAlertAttr ea = null;
            
            if(AuthType.getAuthType(authType).equals(AuthType.ECDSA_3PASS)){
                // modemId를 가지고 oac에 등록 여부를 판단한다.
                ea = EventUtil.makeEventAlertAttr("message",
                        "java.lang.String", "Join Modem");
            }else if(AuthType.getAuthType(authType).equals(AuthType.PANA)){
                // modemId를 가지고 oac에 등록 여부를 판단한다.
                ea = EventUtil.makeEventAlertAttr("message",
                        "java.lang.String", "Join Modem, Pana Auth "+panaAuthResult);
            }        

            if(ea != null){
                event.append(ea);
            }

            event.setActivatorId(modemId);
            event.setActivatorType(TargetClass.Modem);
            
            txmanager.commit(txstatus);
        }
        catch (Exception e) {
            //SP-460
            authType = AuthType.ABNORMAL.getAuthType();

            if (txstatus != null) txmanager.rollback(txstatus);
        }
        
        if(AuthType.getAuthType(authType).equals(AuthType.ECDSA_3PASS)){
            // oac 결과를 집중기로 전송한다.
            boolean verifyDevice = 
                    Boolean.parseBoolean(
                            FMPProperty.getProperty("protocol.security.oacserver.verify.device"));
            if (verifyDevice) {
//                String oacServer = FMPProperty.getProperty("protocol.security.oacserver.webservice.ipaddress");
//                HttpClient httpClient = new HttpClient();
//                GetMethod get = new GetMethod(oacServer+"/api/get_pin_argument/"+modemId);
//                httpClient.executeMethod(get);
//                String jbody = get.getResponseBodyAsString();
//                JsonParser jparser = new JsonParser();
//                JsonElement jelement = jparser.parse(jbody);
//                JsonObject jobj = jelement.getAsJsonObject();
//                int result = jobj.get("result_code").getAsInt();
//                log.info("MODEM_JOIN[" + modemId + "] OAC_RESULT[" + result + "]");
//                get.releaseConnection();
                OacServerApi api  = new OacServerApi();
        		JsonObject json = api.oacServerApi("get_pin_argument/" + modemId, null);
        		int result = 0;
    	    	if ( json != null && json.get("result_code") != null ){ 
    		    	result  = json.get("result_code").getAsInt();
    		    	if ( result  == 0 ){ // success
    		    		String pin_arg = json.get("pin_arg").getAsString();
    		    	}
    		    	log.info("MODEM_JOIN[" + modemId + "] OAC_RESULT[" + result + "]");
    	    	}
                // 0:최초 상태, 1:OAC 확인, 2:인증
                // Check modem on OAC, if exist, send 1
                gw.cmdAuthSPModem(modemId, result == 0? 1:0, mcuId);
            }
            /*
            else {
                // 0:최초 상태, 1:OAC 확인, 2:인증
                // Check modem on OAC, if exist, send 1
                gw.cmdAuthSPModem(modemId, 1, mcuId);
            }
            */
        }  else{
            // SP-460
            log.debug("Modem already joined the same DCU. ");
        }

        log.debug("Modem Joined Event Action Compelte");
    }
    
    public enum AuthType
    {
        ECDSA_3PASS("0"),
        PANA("1"),
        ABNORMAL("98");

        private String code;
        
        AuthType(String code){
            this.code = code;
        }
        public String getAuthType() {
            return code;
        }
        
        public static AuthType getAuthType(String authType){
            for(AuthType m : AuthType.values()){
                if(m.getAuthType().equals(authType))
                    return m;
            }
            return AuthType.ECDSA_3PASS;
        }

    }
}
