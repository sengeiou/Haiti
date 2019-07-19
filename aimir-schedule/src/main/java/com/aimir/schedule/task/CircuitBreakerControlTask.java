package com.aimir.schedule.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CircuitBreakerCondition;
import com.aimir.constants.CommonConstants.CircuitBreakerStatus;
import com.aimir.constants.CommonConstants.GroupType;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.CircuitBreakerLogDao;
import com.aimir.dao.device.CircuitBreakerSettingDao;
import com.aimir.dao.device.EnergyMeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.CircuitBreakerLog;
import com.aimir.model.device.CircuitBreakerSetting;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.Condition;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.Condition.Restriction;

@Transactional
public class CircuitBreakerControlTask {

    protected static Log log = LogFactory.getLog(CircuitBreakerControlTask.class);
    
    @Autowired
    CircuitBreakerSettingDao circuitBreakerSettingDao;
    
    @Autowired
    CircuitBreakerLogDao circuitBreakerLogDao;
    
    @Autowired
    EnergyMeterDao meterDao;
    
    @Autowired
    ContractDao contractDao;

    @Autowired
    CodeDao codeDao;
    
	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

    public void excute() {
    	log.info("############################# Emergency Switch off Scheduler Start ##################################");
        
        Map<String, String> paramMap = new HashMap<String, String>();
        
        String dateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").substring(0,8);


        paramMap.put("groupType", GroupType.Meter.name());
        
        
	    Set<Condition> condition = new HashSet<Condition>();
	    condition.add(new Condition("writeTime", new Object[]{dateTime+"%"}, null, Restriction.LIKE));
	    condition.add(new Condition("status", new Object[]{CircuitBreakerStatus.Deactivation}, null, Restriction.EQ));
	    condition.add(new Condition("targetType", new Object[]{GroupType.Meter}, null, Restriction.EQ));      
	    condition.add(new Condition("condition", new Object[]{CircuitBreakerCondition.Emergency}, null, Restriction.EQ));  
        
        CircuitBreakerSetting circuitBreakerSetting = circuitBreakerSettingDao.findByCondition("condition", CircuitBreakerCondition.Emergency);
        
        if(circuitBreakerSetting.getAutomaticActivation() == null || !circuitBreakerSetting.getAutomaticActivation()){
        	return;
        }
        
        List<CircuitBreakerLog> cbs = circuitBreakerLogDao.findByConditions(condition);

        if (cbs == null || cbs.size() <= 0) {
            log.info("Available Meter is not exist");
            return;
        }

        ResultStatus status = ResultStatus.SUCCESS;
        boolean isCutOff = false;     // 미터 차단 실행여부.
        String currentDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        String startDateTime = null;        // Emergency Mode 시작일자
        String endDateTime = null;

        for (CircuitBreakerLog circuitBreakerLog : cbs) {
        	
            isCutOff = false;
            
        	if(circuitBreakerLog.getCondition().equals(CircuitBreakerCondition.Emergency)){

                startDateTime = circuitBreakerLog.getWriteTime();
                Calendar cal = Calendar.getInstance();
                try {
					cal.setTime(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(startDateTime));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
                cal.add(Calendar.SECOND, circuitBreakerSetting.getRecoveryTime());

                try {
					endDateTime = DateTimeUtil.getFormatTime(cal);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}

                // Emergency Credit Mode 남은 기간 체크
                if (currentDateTime.compareTo(endDateTime) < 0) {
                    continue;
                }
                
                EnergyMeter meter = meterDao.findByCondition("mdsId", circuitBreakerLog.getTarget());

                // 미터가 차단되어 있는지 체크
                if (meter.getMeterStatus() != null &&
               		 (meter).getMeterStatus().getCode().equals(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()))) {
//                       ((EnergyMeter) meter).getSwitchStatus().equals(CircuitBreakerStatus.Deactivation.getCode())) {
               	
                    isCutOff = true;
                }
                
                if (isCutOff) {
                    // 공급을 재개
                    // CommandOperationUtil 호출:생성 후 개발 부분 Start
                    // Relay Switch CmdOperationUtil 호출
                    if (meter != null && meter.getModel() != null
                            && meter.getModem() != null && meter.getModem().getMcu() != null) {
                        
                        status = ResultStatus.SUCCESS;
                        
                        try {
                            Map<String, Object> mapResult = 
                                    cmdOperationUtil.relayValveOn(meter.getModem().getMcu().getSysID(), meter.getMdsId());
                            
                            Object[] values = mapResult.values().toArray(new Object[0]);
                            
                            for (Object o : values) {
                                if (((String)o).contains("failReason")) {
                                    status = ResultStatus.FAIL;
                                    break;
                                }
                            }
                        }
                        catch (Exception e) {
                            log.error(e, e);
                            status = ResultStatus.FAIL;
                        }
                    }
                }
        	}

        }
    	log.info("############################# Emergency switch off Monitor Scheduler End ##################################");
    }
   

	public void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar today = Calendar.getInstance();
        String currDateTime = sdf.format(today.getTime());

		OperationLog log = new OperationLog();

		log.setOperatorType(1);//operator
		log.setOperationCommandCode(operationCode);
		log.setYyyymmdd(currDateTime.substring(0,8));
		log.setHhmmss(currDateTime.substring(8,14));
		log.setYyyymmddhhmmss(currDateTime);
		log.setDescription("");
		log.setErrorReason(errorReason);
		log.setResultSrc("");
		log.setStatus(status);
		log.setTargetName(targetName);
		log.setTargetTypeCode(targetTypeCode);
		log.setUserId(userId);
		log.setSupplier(supplier);
		operationLogDao.add(log);
	}

}