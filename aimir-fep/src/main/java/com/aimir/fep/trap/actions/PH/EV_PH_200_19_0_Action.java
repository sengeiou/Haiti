package com.aimir.fep.trap.actions.PH;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;

/**
 * Event : evtModemRejected, Event ID : 200.19.0
 * 
 */
@Service
public class EV_PH_200_19_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_PH_200_19_0_Action.class);
	@Autowired
	MCUDao mcuDao;

	public enum RejectReason {
		NETWORK_FULL(1, "Network Full"), UNKNOWN_REASON(-1, "Unknown Reason");

		private int code;
		private String message;

		RejectReason(int code, String message) {
			this.code = code;
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public static RejectReason getItem(int code) {
			for (RejectReason fc : RejectReason.values()) {
				if (fc.code == code) {
					return fc;
				}
			}
			return null;
		}
	}

	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventCode[" + trap.getCode() + "] MCU[" + trap.getMcuId() + "]");

		String modemId = event.getEventAttrValue("moSPId");
		String reasonCode = StringUtil.nullToString(event.getEventAttrValue("byteEntry"), "-1");
		
		RejectReason reason = RejectReason.getItem(Integer.parseInt(reasonCode));
		String message = "[" + modemId + "]Modem is rejected from MCU[" + trap.getMcuId() + "], Message=[" + reason.getMessage() + "]";

		log.debug("MODEM_ID[" + modemId + "], Code=" + reasonCode + "REASON=" + reason);

		MCU mcu = mcuDao.get(trap.getMcuId());
		event.setActivatorIp(mcu.getIpv6Addr() != null ? mcu.getIpv6Addr() : mcu.getIpAddr());
		event.setActivatorId(mcu.getSysID());
		event.setActivatorType(TargetClass.DCU);
		event.setSupplier(mcu.getSupplier());
		event.setLocation(mcu.getLocation());
		event.append(EventUtil.makeEventAlertAttr("moSPId", "java.lang.String", modemId));
		event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", message));

		log.debug("Event Action Compelte");
	}
}
