package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ZEUPLSDao;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.ZEUPLS;
import com.aimir.model.system.Code;
import com.aimir.schedule.command.CmdManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

/**
 * @author goodjob
 *
 */
@Service
@Transactional
public class SetSensorAlarmFlagTask
{
    private static Log log = LogFactory.getLog(SetSensorAlarmFlagTask.class);
    int alarmFlag = 0;
    int beforeDay = -1;
    
    @Autowired
    MCUDao mcuDao;
    
    @Autowired
    ZEUPLSDao zeuplsDao;
    
    public void execute()
    {
    
        try {
            Set<Condition> condition = new HashSet<Condition>();

    		Code indoor = CommonConstants.getMcuTypeByName("Indoor");
    		Code outdoor = CommonConstants.getMcuTypeByName("Outdoor");
    		
            condition.add(new Condition("mcuType.id", new Object[]{indoor.getId(), outdoor.getId()}, null, Restriction.IN));
            condition.add(new Condition("networkStatus", new Object[]{"1"}, null, Restriction.EQ));
			List<MCU> mcuList = mcuDao.findByConditions(condition);
            
            //for (int i = 0; i < mcuList.size(); i++) {
             //   new RecoverThread(mcuList.get(i)).start();
            //}
        }
        catch (Exception e) {
            log.error(e);
        }
    }   

    
    class RecoverThread extends Thread {
        
    	private MCU mcu = null;
        
        RecoverThread(MCU mcu) {
            this.mcu = mcu;
        }
        
        public void run()
        {
            try {
                CommandWS gw = CmdManager.getCommandWS(mcu.getProtocolType().getName());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, beforeDay);
                String yyyymmddhhmmss = sdf.format(cal.getTime());
                
                Set<Condition> condition = new HashSet<Condition>();

        		Code indoor = CommonConstants.getMcuTypeByName("Indoor");
        		Code outdoor = CommonConstants.getMcuTypeByName("Outdoor");
        		
                condition.add(new Condition("mcu.id", new Object[]{mcu.getId()}, null, Restriction.IN));
                condition.add(new Condition("installDate", null, null, Restriction.NOTNULL));
                condition.add(new Condition("alarmFlag", null, null, Restriction.NULL));
                condition.add(new Condition("alarmFlag", new Object[]{"0"}, null, Restriction.EQ));
                condition.add(new Condition("lastLinkTime", new Object[]{yyyymmddhhmmss}, null, Restriction.GE));
                
                List<ZEUPLS> modems = zeuplsDao.findByConditions(condition);           
                
                
                byte[] amrMask = {0x00, 0x02};
                byte[] amrData = {
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00,
                    0x00,
                    0x00, 0x00,
                    0x00, 0x00,
                    0x00,
                    0x00,
                    0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                    0x00, 0x00,
                    0x00, 0x00, 0x00, 0x00,
                    0x00,
                    DataUtil.getByteToInt(alarmFlag)
                };
                for (int i = 0; i < modems.size(); i++) {
                    
                    ZEUPLS zeupls = modems.get(i);
                    log.debug("MCU[" + mcu.getSysID() + "] METER[" + zeupls.getMeter().size() + 
                            "] MODEM[" + zeupls.getDeviceSerial() + "] TRY TO SET ALARMFLAG START");
                        
                    // command set amr data
                    try {
                        gw.cmdSetModemAmrData(mcu.getSysID(), zeupls.getDeviceSerial(), amrMask, amrData);
                        log.debug("MCU[" + mcu.getSysID() + "] METER[" + zeupls.getMeter().size() + 
                                "] MODEM[" + zeupls.getDeviceSerial() + "] TRY TO SET ALARMFLAG SUCCESS");
                        
  
                        zeupls.setAlarmFlag(1);
                        zeuplsDao.update(zeupls);
                    }
                    catch (Exception uoe) {
                        log.error("MCU[" + mcu.getSysID() + "] METER[" + zeupls.getMeter().size() + 
                                "] MODEM[" + zeupls.getDeviceSerial() + "] TRY TO SET ALARMFLAG" + 
                                " FAIL FOR[" + uoe.getMessage() + "]");
                        if (uoe.getMessage().indexOf("OUT_OF_BINDING") > -1) {

                            zeupls.setAlarmFlag(0);
                            zeuplsDao.update(zeupls);
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}