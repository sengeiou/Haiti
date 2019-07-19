package com.aimir.fep.trap.actions.PH;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;

/**
 * Event ID : EV_PH_200.100.0
 * 
 * 200.100
 *
 * @author tatsumi
 * @version $Rev: 1 $, $Date: 2017-09-29 10:00:00 +0900 $,
 */
@Component
public class EV_PH_200_100_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_PH_200_100_0_Action.class);
    @Autowired
    MCUDao mcuDao;

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(Modem Tamper Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
        log.debug("EV_PH_200_100_0_Action : EventCode[" + trap.getCode()
        +"] MCU["+trap.getMcuId()+"]");

        MCU mcu = mcuDao.get(trap.getMcuId());
        
		/*
		 * Event Data
		 */
		List<String> EventDataList = new ArrayList<String>();
		String EventDataHex = null;
		for (int i = 0;; i++) {
			if (i > 0)
				EventDataHex = event.getEventAttrValue("streamEntry." + i + ".hex");
			else
				EventDataHex = event.getEventAttrValue("streamEntry.hex");

			if (EventDataHex == null || "".equals(EventDataHex))
				break;
			else
				EventDataList.add(EventDataHex);
		}

		for (String EventData : EventDataList) {
			int pos = 0;
			
			// CODE
			String code = EventData.substring(pos, 3);
			pos += 3;
			log.debug("CODE : [" + code + "]");

			// SRCTYPE
			String srctype = EventData.substring(pos, pos + 1);
			pos += 1;
			log.debug("SRCTYPE : [" + srctype + "]");

			// SRCID
			String srcid = EventData.substring(pos, pos + 8);
			pos += 8;
			log.debug("SRCID : [" + srcid + "]");

			// TIME
			String time = EventData.substring(pos, pos + 7);
			pos += 7;
			log.debug("TIME : [" + time + "]");

			// CNT
			String cnt = EventData.substring(pos, pos + 2);
			pos += 2;
			log.debug("CNT : [" + cnt + "]");

			// DATA
			String data = EventData.substring(pos, pos + (EventData.length() - pos));
			log.debug("DATA : [" + data + "]");
		}

        event.setActivatorIp(mcu.getIpv6Addr() != null ? mcu.getIpv6Addr():mcu.getIpAddr());
        event.setActivatorId(mcu.getSysID());
        event.setActivatorType(TargetClass.DCU);
        event.setSupplier(mcu.getSupplier());
        event.setLocation(mcu.getLocation());

        String message = "Untransferred";             
        event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));

        log.debug("Event Action Compelte");
	}
}
