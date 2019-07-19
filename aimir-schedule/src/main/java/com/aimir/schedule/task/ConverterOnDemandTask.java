package com.aimir.schedule.task;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.command.ws.client.CommandWS;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.schedule.command.CmdManager;
import com.aimir.util.DateTimeUtil;

/**
 * On Demand Task of Converter Type Modem.
 * @author kskim
 *
 */
@Transactional
public class ConverterOnDemandTask {
	protected static Log log = LogFactory.getLog(ConverterOnDemandTask.class);
	
	//onDemand 명령을 수행할 미터 모델 명, CSV포멧
	private String modelCsv;
	
	public String getModelCsv() {
		return modelCsv;
	}

	public void setModelCsv(String modelCsv) {
		this.modelCsv = modelCsv;
	}

	@Autowired
	private SessionFactory sessionFactory;
	
	public void excute(){
		CmdManager cmdManager = new CmdManager();	
		CommandWS commandWS;
		
		log.debug(String.format("Support Models : %s", modelCsv));
		Session session = null;
		try {
    		if(this.modelCsv!=null){
    			String[] models = this.modelCsv.split(",");
    			
    			session = sessionFactory.openSession();
    			Criteria criteria = session.createCriteria(Modem.class);
    			criteria.createAlias("meter", "m");
    			criteria.createAlias("m.model", "mo");
    			
    			//포함하는 모든 미터 모델들 설정.
    			criteria.add(Restrictions.in("mo.name", models));
    			
    			//모뎀타입 Converter 설정.
    			criteria.add(Restrictions.eq("modemType", ModemType.Converter_Ethernet.name()));
    			
    			List<?> modems = criteria.list();
    			
    			for (Object object : modems) {
    				if(object instanceof Modem){
    					Modem modem = (Modem)object;
    					Meter meter = modem.getMeter().iterator().next();
    					
    					log.debug(String.format("OnDemand of Converter Modem / MeterID : %s, Modem Serial : %s", meter
    							.getMdsId(), modem.getDeviceSerial()));
    
    					try {
    					    commandWS = cmdManager.getCommandWS(modem.getProtocolType());
    						//주기적인 검침 이기때문에 날짜 조건을 오늘 날짜로 한다.
    						String today = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd");
    						
    						//시작 시간과 종료 시간을 설정.
    						String fromDate = today + "000000";
    						String toDate = today + "235959";
    						
    						
    						commandWS.cmdOnDemandMeter2(null, meter.getMdsId(), modem
    								.getDeviceSerial(), "0", fromDate, toDate);
    					} catch (Exception e) {
    						log.error(e);
    					}
    				}
    			}		
    		}
		}
		finally {
            if (session != null) session.close();
        }
	}
}
