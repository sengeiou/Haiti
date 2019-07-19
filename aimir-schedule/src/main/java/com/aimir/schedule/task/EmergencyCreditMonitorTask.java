package com.aimir.schedule.task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

@Transactional
public class EmergencyCreditMonitorTask {

    protected static Log log = LogFactory.getLog(EmergencyCreditMonitorTask.class);

    @Autowired
    ContractDao contractDao;

    @Autowired
    ContractChangeLogDao contractChangeLogDao;

    @Autowired
    CodeDao codeDao;
    
    @Autowired
    MeterDao meterDao;
    
	@Autowired
	OperationLogDao operationLogDao;

	@Autowired
	CmdOperationUtil cmdOperationUtil;

    public void excute() {
    	log.info("############################# Emergency Credit Monitor Scheduler Start ##################################");
        // Emergency Credit 모드인 계약정보
        List<Contract> contracts = contractDao.getEmergencyCreditMonitorContract();

        if (contracts == null || contracts.size() <= 0) {
            log.info("Available Contract is not exist");
            return;
        }

        ResultStatus status = ResultStatus.SUCCESS;
        Meter meter = null;
        boolean isCutOff = false;     // 미터 차단 실행여부.
        String currentDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
        String startDateTime = null;        // Emergency Credit Mode 시작일자
        String startTime = null;
        String endDate = null;              // Emergency Credit Mode 마지막일자
        String endDateTime = null;
        String rtnStr = "";

        for (Contract contract : contracts) {

            isCutOff = false;
            startDateTime = contract.getEmergencyCreditStartTime();
            startTime = startDateTime.substring(8);

            endDate = CalendarUtil.getDate(startDateTime, Calendar.DAY_OF_MONTH, contract.getEmergencyCreditMaxDuration());
            endDateTime = endDate + startTime;

            // Emergency Credit Mode 남은 기간 체크
            if (currentDateTime.compareTo(endDateTime) < 0) {
                continue;
            }

            meter = contract.getMeter();

            // 미터가 차단되어 있는지 체크
            if (meter.getMeterStatus() != null &&
            		 (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatus(MeterStatus.CutOff.name())))) {
//                        ((EnergyMeter) meter).getSwitchStatus().equals(CircuitBreakerStatus.Deactivation.getCode())) {
            	
                isCutOff = true;
            }

            if (!isCutOff) {
                // 공급을 차단
                // CommandOperationUtil 호출:생성 후 개발 부분 Start
                // Relay Switch CmdOperationUtil 호출
                if (contract.getMeter() != null && contract.getMeter().getModel() != null
                        && contract.getMeter().getModem() != null && contract.getMeter().getModem().getMcu() != null) {
                    status = ResultStatus.SUCCESS;
                    
                    try {
                        Map<String, Object> resultMap = cmdOperationUtil.relayValveOff(meter.getModem().getMcu().getSysID(), meter.getMdsId());
                        
                        Object[] values = resultMap.values().toArray(new Object[0]);
                        
                        for (Object o : values) {
                            if (((String)o).contains("failReason")) {
                                status = ResultStatus.FAIL;
                            }
                        }
                        
                        // Operation Log에 기록
                        Code operationCode = codeDao.getCodeIdByCodeObject("8.1.4");
                        if (operationCode != null) {
                            this.saveOperationLog(contract.getSupplier(), contract.getMeter().getMeterType(), contract
                                    .getMeter().getMdsId(), "schedule", operationCode, status.getCode(), status.name());
                        }
                    }
                    catch (Exception e) {
                        log.error(e, e);
                        status = ResultStatus.FAIL;
                    }
                }
            }
          
            // CommandOperationUtil 호출:생성 후 개발 부분 End
            // 차단후 Credit type Prepay로 변경
            // 선불코드 조회
            Code code = codeDao.findByCondition("code", "2.2.1");

            // ContractChangeLog Insert
            addContractChangeLog(contract, "creditType", contract.getCreditType().getCode(), code.getCode());

            // Contract Update
            contract.setCreditType(code);
            contractDao.update(contract);

            // 고객에게 유효기간이 끝났음을 통보
            // 통보 프레임워크 호출 시작

            // 통보 프레임워크 호출 종료
        }
    	log.info("############################# Emergency Credit Monitor Scheduler End ##################################");
    }

    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param field
     * @param beforeValue
     * @param afterValue
     */
    private void addContractChangeLog(Contract contract, String field, Object beforeValue, Object afterValue) {
        // ContractChangeLog Insert
        ContractChangeLog contractChangeLog = new ContractChangeLog();

        contractChangeLog.setContract(contract);
        contractChangeLog.setCustomer(contract.getCustomer());
        contractChangeLog.setStartDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
        contractChangeLog.setChangeField(field);

        if (beforeValue == null) {
            contractChangeLog.setBeforeValue(null);
        } else {
            contractChangeLog.setBeforeValue(StringUtil.nullToBlank(beforeValue));
        }

        if (afterValue == null) {
            contractChangeLog.setAfterValue(null);
        } else {
            contractChangeLog.setAfterValue(StringUtil.nullToBlank(afterValue));
        }

//        contractChangeLog.setOperator(operator);
        contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
//        contractChangeLog.setDescr(descr);

        contractChangeLogDao.add(contractChangeLog);
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