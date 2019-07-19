package com.aimir.fep.bypass.actions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.conf.KamstrupCIDMeta.CID;
import com.aimir.fep.protocol.fmp.datatype.OPAQUE;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.STREAM;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.ControlDataConstants;
import com.aimir.fep.protocol.fmp.frame.ControlDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

public class CommandAction_GG extends CommandAction {
    private static Log log = LogFactory.getLog(CommandAction_GG.class);
    
    private String cmd = "";
    
    @Override
    public void executeBypass(byte[] frame, IoSession session) throws Exception {
        if (cmd.equals("cmdSetMeterTime")) {
            if (frame[0] == 0x06) {
                
            }
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
            // session.write(new byte[]{(byte)0x80, (byte)0x3F, (byte)0xB8, (byte)0x02, (byte)0x1B, 
            //        (byte)0xF9, (byte)0x01, (byte)0x04, (byte)0x17, (byte)0x00, 
            //        (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0xB1, (byte)0x7A, (byte)0x0D});
        }
        else if (cmd.equals("cmdGetMaxDemand")) {
            session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
            log.debug(Hex.decode(frame));
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            if (frame[0] == 0x40) {
                byte[] bx = new byte[frame.length - 6];
                System.arraycopy(frame, 3, bx, 0, bx.length);
                Object[] result = CID.GetRegister.getResponse(bx);
                // result[1]이 kVA 값이다.
            }
        }
        // 명령어에 따라서 단계적으로 실행되어야 한다.
    }
    
    @Override
    public void execute(String cmd, SMIValue[] smiValues, IoSession session) 
    throws Exception
    {
        try {
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            // GG 101.2.1 모뎀/미터 식별 응답이 오면 커맨드를 실행한다.
            if (cmd.equals("cmdIdentifyDevice")) {
                bd.setModemId(smiValues[0].getVariable().toString());
                bd.setMeterId(smiValues[1].getVariable().toString());
                
                // 비동기 내역을 조회한다.
                JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
                TransactionStatus txstatus = null;
                AsyncCommandLogDao acld = DataUtil.getBean(AsyncCommandLogDao.class);
                List<AsyncCommandLog> acllist = null;
                try{
					txstatus = txmanager.getTransaction(null);
					//모뎀/미터 시리얼 번호로 조회 (최근에 실행한 순서대로 불러옴)
	                Map<String,Object> mapCondition = new HashMap<String, Object>();
	                mapCondition.put("meterId", bd.getMeterId());
	                mapCondition.put("modemId", bd.getModemId());
	                mapCondition.put("state", TR_STATE.Waiting.getCode());
	    			
	                acllist = acld.getLogListByCondition(mapCondition);
					txmanager.commit(txstatus);
                } catch (Exception e) {
                	if (txstatus != null) txmanager.commit(txstatus);
				}
                
                log.debug("ASYNC_SIZE[" + acllist.size () + "]");
                if (acllist.size() > 0) {
                    // 명령 건수가 1개여야 한다. 어차피 여러개의 명령 요청이 있더라도 한번 하고 나면 끊어지기 때문에 연속으로 처리할 수 없다.
                    // 전부 처리된 것으로 변경한다.
                    AsyncCommandLog acl = null;
                    for (int i = 0; i < acllist.size(); i++) {
                        acl = acllist.get(i);
                        if (i == 0)
                            acl.setState(TR_STATE.Success.getCode());
                        else
                            acl.setState(TR_STATE.Terminate.getCode());
                        acld.update(acl);
                    }
                    
                    // 마지막 커맨드를 실행한다.
                    acl = acllist.get(0);
                    
                    Set<Condition> condition = new LinkedHashSet<Condition>();
                    condition.add(new Condition("id.trId", new Object[]{acl.getTrId()}, null, Restriction.EQ));
                    condition.add(new Condition("id.mcuId", new Object[]{acl.getMcuId()}, null, Restriction.EQ));
                    AsyncCommandParamDao acpd = DataUtil.getBean(AsyncCommandParamDao.class);
                    List<AsyncCommandParam> acplist = acpd.findByConditions(condition);
                    
                    for (AsyncCommandParam p : acplist.toArray(new AsyncCommandParam[0])) {
                        bd.addArg(p.getParamValue());
                    }
                    Method method = this.getClass().getMethod(acl.getCommand(), IoSession.class);
                    method.invoke(this, session);
                }
                else {
                    // 실행할 명령이 없으면 EOT 호출하고 종료
                    session.write(new ControlDataFrame(ControlDataConstants.CODE_EOT));
                }
            }
            else if (cmd.equals("cmdOTAStart")) {
                log.debug("modemId[" + bd.getModemId() + "] meterId[" + bd.getMeterId() + "]");
                bd.setModemModel(smiValues[0].getVariable().toString());
                bd.setFwVersion(smiValues[1].getVariable().toString());
                bd.setBuildno(smiValues[2].getVariable().toString());
                bd.setHwVersion(smiValues[3].getVariable().toString());
                bd.setPacket_size(Integer.parseInt(smiValues[4].getVariable().toString()));
                
                // TODO 위 정보를 모뎀에 갱신한다.
                cmdSendImage(session);
            }
            else if (cmd.equals("cmdSendImage")) {
                cmdSendImage(session);
            }
            else if (cmd.equals("cmdOTAEnd")) {
                // 상태값을 받아서 실패하면 다시 시도하도록 한다.
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
            }
            else if (cmd.equals("cmdReadModemConfiguration")) {
                // 모뎀 설정 정보를 갱신한다.
            }
            else if (cmd.equals("cmdRelayStatus")) {
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
                setMeterStatus(((BypassDevice)session.getAttribute(session.getRemoteAddress())).getMeterId(), status);
            }
            else if (cmd.equals("cmdRelayDisconnect")) {
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
                setMeterStatus(((BypassDevice)session.getAttribute(session.getRemoteAddress())).getMeterId(), status);
            }
            else if (cmd.equals("cmdRelayReconnect")) {
                int status = Integer.parseInt(smiValues[0].getVariable().toString());
                setMeterStatus(((BypassDevice)session.getAttribute(session.getRemoteAddress())).getMeterId(), status);
            }
            else if(cmd.equals("cmdUploadMeteringData")){

                OPAQUE mdv = (OPAQUE) smiValues[0].getVariable();
                log.debug("Get Meter : return ClassName[" + mdv.getClsName() +
                          "] MIB[" + mdv.getMIBName() + "]");
                
                MDData mdData = new MDData(new WORD(1));
                mdData.setMcuId("0");
                mdData.setMdData(mdv.encode());
                ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);
                handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, mdData);
            }
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
    
    /*
     * OTA 시작 명령
     */
    public void cmdOTAStart(IoSession session)
    throws Exception
    {
        // ota 비동기 이력의 인자에서 파일 경로를 가져온다.
        ByteArrayOutputStream out = null;
        FileInputStream in = null;
        try {
            BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
            // BypassDevice의 args에서 파일명을 가져와야 한다.
            File file = new File((String)bd.getArgs().get(0));
            out = new ByteArrayOutputStream();
            in = new FileInputStream(file);
            
            int len = 0;
            byte[] b = new byte[1024];
            while ((len=in.read(b)) != -1) {
                out.write(b, 0, len);
            }
            
            long filelen = file.length();
            // sendImage 에서 사용하기 위해 바이너리를 전역 변수에 넣는다.
            bd.setFw_bin(out.toByteArray());
            // sendImage에서 바이너리를 읽어올 수 있도록 하기 위해 전역 변수에 넣는다.
            bd.setFw_in(new ByteArrayInputStream(bd.getFw_bin(), 0, bd.getFw_bin().length));
            // int crc = DataUtil.getIntTo2Byte(FrameUtil.getCRC(bd.getFw_bin()));
            byte[] crc = CRCUtil.Calculate_ZigBee_Crc(bd.getFw_bin(), (char)0x0000);
            DataUtil.convertEndian(crc);
            
            String ns = (String)session.getAttribute("nameSpace");
            List<SMIValue> params = new ArrayList<SMIValue>();
            
            params.add(DataUtil.getSMIValueByObject(ns, "cmdModemFwImageLength", Long.toString(filelen)));
            params.add(DataUtil.getSMIValueByObject(ns, "cmdModemFwImageCRC", Integer.toString(DataUtil.getIntTo2Byte(crc))));
            sendCommand(session, "cmdOTAStart", params);
        }
        finally {
            if (out != null) out.close();
            if (in != null) in.close();
        }
    }
    
    /*
     * 펌웨어 바이너리를 보내는 명령
     * 한 패킷 전송 후 응답을 받고 다음 패킷을 보내야 하므로 offset과 fw_in이 전역 변수로 선언되었다.
     */
    public void cmdSendImage(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        log.debug("offset[" + bd.getOffset() + "]");
        String ns = (String)session.getAttribute("nameSpace");
        byte[] b = new byte[bd.getPacket_size()];
        int len = -1;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if ((len = bd.getFw_in().read(b)) != -1) {
            out.write(b, 0, len);
            List<SMIValue> params = new ArrayList<SMIValue>();
            params.add(DataUtil.getSMIValueByObject(ns, "cmdImageAddress", Integer.toString(bd.getOffset())));
            params.add(DataUtil.getSMIValueByObject(ns, "cmdImageSize", Integer.toString(len)));
            // 바이트 스트립을 문자열로 변환 후 바이트로 변환시 원본이 손상되어 STREAM을 바로 사용하도록 한다.
            params.add(new SMIValue(DataUtil.getOIDByMIBName(ns, "cmdImageData"), new STREAM(out.toByteArray())));
            bd.setOffset(bd.getOffset() + len);
            sendCommand(session, "cmdSendImage", params);
        }
        out.close();
        
        // 전송이 끝나면 종료 명령을 보낸다.
        if (bd.getOffset() == bd.getFw_bin().length) {
            bd.getFw_in().close();
            sendCommand(session, "cmdOTAEnd", null);
        }
    }
    
    public void cmdUploadMeteringData(IoSession session) throws Exception
    {
        List<SMIValue> params = new ArrayList<SMIValue>();
        sendCommand(session, "cmdUploadMeteringData", params);
    }
    
    public void cmdResetModem(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int delayTime = 10;
        
        if (bd.getArgs().size() > 0) {
            delayTime = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdResetModemDelayTime", Integer.toString(delayTime)));
        sendCommand(session, "cmdResetModem", params);
    }
    
    public void cmdFactorySetting(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int code = 0x0314;
        
        if (bd.getArgs().size() > 0) {
            code = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdFactorySettingCode", Integer.toString(code)));
        sendCommand(session, "cmdFactorySetting", params);
    }
    
    public void cmdReadModemConfiguration(IoSession session) throws Exception
    {
        sendCommand(session, "cmdReadModemConfiguration", null);
    }
    
    public void cmdSetTime(IoSession session) throws Exception
    {
        String timestamp = DateTimeUtil.getDateString(new Date());
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetTimestamp", timestamp));
        sendCommand(session, "cmdSetTime", params);
    }
    
    public void cmdSetModemResetInterval(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int interval = 60;
        
        if (bd.getArgs().size() > 0) {
            interval = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdModemResetIntervalMinute", Integer.toString(interval)));
        sendCommand(session, "cmdModemResetInterval", params);
    }
    
    public void cmdSetMeteringInterval(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int interval = 15;
        
        if (bd.getArgs().size() > 0) {
            interval = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetMeteringIntervalMinute", Integer.toString(interval)));
        sendCommand(session, "cmdSetMeteringInterval", params);
    }
    
    public void cmdSetServerIpPort(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String ip = "187.1.10.58";
        int port = 8000;
        
        if (bd.getArgs().size() > 1) {
            ip = (String)bd.getArgs().get(0);
            port = (Integer)bd.getArgs().get(1);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetServerIp", ip));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetServerPort", Integer.toString(port)));
        sendCommand(session, "cmdSetServerIpPort", params);
    }
    
    public void cmdSetApn(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        String apnAddress = "test";
        String apnId = "test";
        String apnPassword = "test";
        
        if (bd.getArgs().size() > 2) {
            apnAddress = (String)bd.getArgs().get(0);
            apnId = (String)bd.getArgs().get(1);
            apnPassword = (String)bd.getArgs().get(2);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetApnAddress", apnAddress));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetApnID", apnId));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetApnPassword", apnPassword));
        sendCommand(session, "cmdSetApn", params);
    }
    
    public void cmdSetMeterTime(IoSession session) throws Exception
    {
        cmd = "cmdSetMeterTime";
        
        cmdSetBypassStart(session);
        byte[][] req = KamstrupCIDMeta.getRequest(new String[]{"SetClock","",""});
        log.debug("REQ[" + Hex.decode(req[0]) + "] VAL[" + Hex.decode(req[1]) + "]");
        session.write(KamstrupCIDMeta.makeKmpCmd(req[0], req[1]));
        // session.write(new byte[]{(byte)0x80, (byte)0x3F, (byte)0x10, 
        //         (byte)0x02, (byte)0x04, (byte)0xBA, (byte)0x00, 
        //        (byte)0xC7, (byte)0xD7, (byte)0x07, (byte)0x0D});
    }
    
    public void cmdGetMaxDemand(IoSession session) throws Exception
    {
        cmd = "cmdGetMaxDemand";
        
        cmdSetBypassStart(session);
        byte[][] req = KamstrupCIDMeta.getRequest(new String[]{"GetRegister","1326"});
        log.debug("REQ[" + Hex.decode(req[0]) + "] VAL[" + Hex.decode(req[1]) + "]");
        session.write(KamstrupCIDMeta.makeKmpCmd(req[0], req[1]));
        // session.write(new byte[]{(byte)0x80, (byte)0x3F, (byte)0x10, 
        //         (byte)0x02, (byte)0x04, (byte)0xBA, (byte)0x00, 
        //        (byte)0xC7, (byte)0xD7, (byte)0x07, (byte)0x0D});
    }
    
    public void cmdSetBypassStart(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int timeout = 10;
        
        if (bd.getArgs().size() > 0) {
            timeout = (Integer)bd.getArgs().get(0);
        }
        
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdSetBypassStartTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdSetBypassStart", params);
    }
    
    public void cmdOndemandMetering(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int offset = 0;
        int count = 1;
        
        if (bd.getArgs().size() > 1) {
        	if(bd.getArgs().get(0) instanceof String) {
        		offset = Integer.parseInt((String)bd.getArgs().get(0));
        	} else {
        		offset = (Integer)bd.getArgs().get(0);
        	}
        	
        	if(bd.getArgs().get(1) instanceof String) {
        		count = Integer.parseInt((String)bd.getArgs().get(1));
        	} else {
        		count = (Integer)bd.getArgs().get(1);
        	}
            
            
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOndemandOffset", Integer.toString(offset)));
        params.add(DataUtil.getSMIValueByObject(ns, "cmdOndemandCount", Integer.toString(count)));
        sendCommand(session, "cmdOndemandMetering", params);
    }
    
    public void cmdRelayStatus(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        
        String ns = (String)session.getAttribute("nameSpace");
        sendCommand(session, "cmdRelayStatus", null);
    }
    
    public void cmdRelayDisconnect(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int timeout = 5;
        
        if (bd.getArgs().size() > 0) {
            timeout = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdRelayDisconnectTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdRelayDisconnect", params);
    }
    
    public void cmdRelayReconnect(IoSession session) throws Exception
    {
        BypassDevice bd = (BypassDevice)session.getAttribute(session.getRemoteAddress());
        int timeout = 5;
        
        if (bd.getArgs().size() > 0) {
            timeout = (Integer)bd.getArgs().get(0);
        }
        String ns = (String)session.getAttribute("nameSpace");
        List<SMIValue> params = new ArrayList<SMIValue>();
        params.add(DataUtil.getSMIValueByObject(ns, "cmdRelayReconnectTimeout", Integer.toString(timeout)));
        sendCommand(session, "cmdRelayReconnect", params);
    }
}
