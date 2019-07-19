package com.aimir.schedule.task;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.util.GroupInfo;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Deprecated
@Service
@Transactional
public class RecollectMeteringByMMIUTask 
{
    private static Log log = LogFactory.getLog(RecollectMeteringByMMIUTask.class);
    
  
    @Autowired
    ModemDao modemDao;

	public void execute() {

		Map<String, String> TFBeforeHour = DateTimeUtil.calcDate(Calendar.HOUR, -24);
		String TFDate = TFBeforeHour.get("date").replace("-", "") + TFBeforeHour.get("time").replace(".", "");

		Code mmiu = CommonConstants.getModemTypeByName(ModemType.MMIU.name());

		Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("lastCommDate", new Object[]{TFDate}, null, Restriction.GE));
        condition.add(new Condition("mcuType.id", new Object[]{mmiu.getId()}, null, Restriction.EQ));
        condition.add(new Condition("sysHwVersion", new Object[]{}, null, Restriction.NOTNULL));
        condition.add(new Condition("sysSwVersion", new Object[]{}, null, Restriction.NOTNULL));
        List<Modem> list = modemDao.findByConditions(condition);

        try{

            for (Modem modem : list) {
                //(new RecollectThread(modem)).start();
            }

        }
        catch (Exception e)
        {
            log.error("RecollectMeteringTask" + e.getMessage(), e);
        }
        log.info("Executing task  ending at " + new Date());
    }
    
    class RecollectThread extends Thread {
        private Modem modem;
        
        RecollectThread(Modem modem) {
            this.modem = modem;
        }
        
        public void run() {
        	Set<Condition>set = new HashSet<Condition>();
        	set.add(new Condition("modem.id",new Object[]{modem.getId()},null,Restriction.EQ));
        	set.add(new Condition("hwVer",new Object[]{null},null,Restriction.NULL));
            List<Modem> list = modemDao.findByConditions(set);
            
            String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd").substring(0,8);

            if(list != null && list.size() > 0){

                try{
                	//CmdOperationUtil.doOnDemand(modem.getMeter(), 0, OperatorType.SYSTEM.name());
                }catch(Exception e){
                	log.error(e,e);
                }

            }else{

            }
        }
    }
}
