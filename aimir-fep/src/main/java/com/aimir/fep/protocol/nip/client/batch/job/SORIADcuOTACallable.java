/**
 * 
 */
package com.aimir.fep.protocol.nip.client.batch.job;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.client.batch.excutor.IBatchCallable;
import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_RESULT_CONSTANTS;
import com.aimir.fep.protocol.nip.client.batch.excutor.CallableBatchExcutor.CBE_STATUS_CONSTANTS;

/**
 * @author simhanger
 *
 */
@Deprecated
public class SORIADcuOTACallable implements IBatchCallable {
	private static Logger logger = LoggerFactory.getLogger(SORIADcuOTACallable.class);

	private CommandGW gw;
	private String mcuSysId;
	private OTAType otaType;
	private String imageIdentifier;
	private String fwPath;
	private String checkSum;
	private List<String> filterValue;

	public SORIADcuOTACallable(CommandGW gw, String mcuSysId, OTAType otaType, String imageIdentifier, String fwPath, String checkSum, List<String> filterValue) {
		this.gw = gw;
		this.mcuSysId = mcuSysId;
		this.otaType = otaType;
		this.imageIdentifier = imageIdentifier;
		this.fwPath = fwPath;
		this.checkSum = checkSum;
		this.filterValue = filterValue;
	}

	@Override
	public Map<CBE_RESULT_CONSTANTS, Object> call() throws Exception {
		boolean resultState = false;

		logger.debug("[SORIADcuOTACallable][{}] Excute START => [{}]", mcuSysId, filterValue.toString());
		@SuppressWarnings("rawtypes")
		Hashtable resultTable = gw.cmdReqToDCUNodeUpgrade(mcuSysId, otaType, imageIdentifier, fwPath, checkSum, filterValue);
		logger.debug("[SORIADcuOTACallable][{}] Excute END => [{}]", mcuSysId, filterValue.toString());

		if (resultTable != null && resultTable.size() > 0) {
			resultState = true;
			logger.debug("cmdReqToDCUNodeUpgrade Result = " + resultTable.toString());
		} else {
			logger.debug("cmdReqToDCUNodeUpgrade Result = null");
		}

		/**
		 * OTA 시 불필요하다고 하여 사용하지 않도록 주석처리함. 추후 불필요시 완전 삭제할것. 2016-09-04 simhanger
		 */
		//		if (resultTable != null && resultTable.size() > 0) {
		//			Iterator<String> keys = resultTable.keySet().iterator();
		//			String keyVal = null;
		//			int requestId = -1;
		//			
		//			while (keys.hasNext()) {
		//				keyVal = (String) keys.next();
		//				requestId = Integer.parseInt(resultTable.get(keyVal).toString());
		//					
		//				cmdCtrlUpgradeRequest(mcuSysId, requestId, 1);				
		//			}
		//			
		//			resultState = true;
		//		}

		Map<CBE_RESULT_CONSTANTS, Object> result = new HashMap<CBE_RESULT_CONSTANTS, Object>();
		result.put(CBE_RESULT_CONSTANTS.TARGET_ID, mcuSysId);
		result.put(CBE_RESULT_CONSTANTS.RESULT_STATE, resultState == true ? CBE_STATUS_CONSTANTS.SUCCESS : CBE_STATUS_CONSTANTS.FAIL);
		result.put(CBE_RESULT_CONSTANTS.RESULT_VALUE, resultState == true ? "OTA Request - Success" : "OTA Request - Fail");

		return result;
	}
}
