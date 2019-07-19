package com.aimir.fep.trap.actions.LK;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlertLog;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;

/*
	210.4	evtRouteDiscovery	OID	3	True	Info		Discovery result
	
	1.5	msgType	WORD	2				Route record message type
	1.5	destAddr	WORD	2				Destination address
	1.5	origAddr	WORD	2				Origination address
	1.5	metricType	WORD	2				Metric type
	1.5	routeCode	WORD	2				Route cost
	1.5	hopCount	WORD	2				Hop count
	1.5	weakLink	WORD	2				Weak link
 */
@Component
public class EV_LK_210_4_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_LK_210_4_0_Action.class);


	@Autowired
	MCUDao dcuDao;

	@Autowired
	CodeDao codeDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;

	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[evtRouteDiscovery] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		String mcuId = trap.getMcuId();
		String ipAddr = event.getEventAttrValue("ntwPppIp") == null ? "" : event.getEventAttrValue("ntwPppIp");
		String modemSerial = trap.getSourceId();
		String timeStamp = trap.getTimeStamp();
		String srcType = trap.getSourceType();

		log.debug("mcuId = " + mcuId + ", ipAddr = " + ipAddr + ", modemSeriall = " + modemSerial + ", timeStamp = " + timeStamp + ", srcType = " + srcType);

		String msgType = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry"));
		String destAddr = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry.1"));
		String origAddr = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry.2"));
		String metricType = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry.3"));
		String routeCode = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry.4"));
		String hopCount = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry.5"));
		String weakLink = StringUtil.nullToBlank(event.getEventAttrValue("wordEntry.6"));

		// Logging
		StringBuilder logBuf = new StringBuilder();
		logBuf.append("mcuId[" + mcuId + "] ");
		logBuf.append("ipAddr[" + ipAddr + "] ");
		logBuf.append("msgType[" + msgType + "] ");
		logBuf.append("destAddr[" + destAddr + "] ");
		logBuf.append("origAddr[" + origAddr + "] ");
		logBuf.append("metricType[" + metricType + "] ");
		logBuf.append("routeCode[" + routeCode + "] ");
		logBuf.append("hopCount[" + hopCount + "] ");
		logBuf.append("weakLink[" + weakLink + "] ");
		log.debug(logBuf.toString());

		event.setActivatorType(TargetClass.PLCIU);
		event.setActivatorId(modemSerial);
		event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Route Discovery: " + logBuf.toString()));

		log.debug("evtRouteDiscovery Action Compelte");
	}
}
