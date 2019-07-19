package com.aimir.schedule.task;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.DecimalPattern;
import com.aimir.model.system.Language;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

/**
 * 잔액을 체크하여 0이하가 되면 cut off를 실시하고 SMS로 통보한다. MOE에 적용
 * 
 * @author elevas
 *
 */
@Transactional
public class MOEBalanceMonitorDemo1Task extends ScheduleTask {

	protected static Log log = LogFactory.getLog(MOEBalanceMonitorDemo1Task.class);

	@Resource(name = "transactionManager")
	HibernateTransactionManager txManager;

	@Autowired
	ContractDao contractDao;
	
	private boolean isNowRunning = false;

	public static void main(String[] args) {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "spring-balancemonitor.xml" });
		DataUtil.setApplicationContext(ctx);

		MOEBalanceMonitorDemo1Task task = ctx.getBean(MOEBalanceMonitorDemo1Task.class);
		task.execute(null);
		System.exit(0);
	}

	@Override
	public void execute(JobExecutionContext context) {
		if(isNowRunning){
			log.info("########### MOEBalanceMonitorDemo1Task is already running...");
			return;
		}
		isNowRunning = true;
		log.info("############################# MOE Balance Demo1 Scheduler Start ##################################");

		TransactionStatus txStatus = null;
		Map<String, List<Contract>> grpContracts = new HashMap<String, List<Contract>>();
		Protocol protocolType = Protocol.LAN;

		try {
			txStatus = txManager.getTransaction(null);
			List<Object> contracts = contractDao.getContractIdByContractNo("nuri_contract2");

			if (contracts == null || contracts.size() <= 0) {
				log.info("Available Contract is not exist");
				
                txStatus = null;
                isNowRunning = false;
                log.info("############################# MOE Balance Demo1 Scheduler End ##################################");

				return;
			}

			Contract _contract = (Contract) contracts.get(0);
			_contract.setCurrentCredit(-1.0);
			contractDao.update(_contract);

			List<Contract> contractList = new ArrayList<Contract>();
			contractList.add(_contract);
			Modem modem = _contract.getMeter().getModem();
			protocolType = modem.getProtocolType();

			if (protocolType == Protocol.REVERSEGPRS) { // Iraq MOE용 REVERSEGPRS 프로토콜.
				grpContracts.put(modem.getDeviceSerial(), contractList);
			} else {
				log.debug("Protocol ==> " + modem.getProtocolType());
				grpContracts.put(modem.getMcu().getSysID(), contractList);
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				} catch (Exception ee) {
				}
			}
			log.error(e, e);
		}

		String keyId = null;
		ThreadPoolExecutor executor = new ThreadPoolExecutor(5, 5, 3, TimeUnit.MINUTES, new LinkedBlockingQueue());
		for (Iterator<String> i = grpContracts.keySet().iterator(); i.hasNext();) {
			keyId = i.next();
			try {
				executor.execute(new MOEBalanceDemoThread(protocolType, keyId, grpContracts.get(keyId)));
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		try {
			executor.shutdown();
			while (!executor.isTerminated()) {
			}
		} catch (Exception e) {
		}

		// contract 잔액정보 업데이트
		// 잔액과 임계치 비교
		// 임계치보다 작으면 고객 통보
		// 잔액이 0 이고 emergency available 이 false, emergency credit 이 수동인 경우 차단
		// 차단후 고객 통보
		// 잔액이 0 이고 emergency available 이 true, emergency credit 이 자동인 경우 고객정보를 emergency credit 모드로 업데이트
		// emergnecy credit 모드를 고객 통보
		log.info("############################# MOE Balance Demo1 Scheduler End ##################################");
		isNowRunning = false;
	}
}

class MOEBalanceDemo1Thread implements Runnable {
	private static Log log = LogFactory.getLog(MOEBalanceDemo1Thread.class);

	HibernateTransactionManager txManager = (HibernateTransactionManager) DataUtil.getBean("transactionManager");

	private String targetName;
	private Protocol protocolType;
	private String keyId;
	private List<Contract> contractList;
	Properties messageProp = null;

	MOEBalanceDemo1Thread(Protocol protocolType, String mcuId, List<Contract> contractList) {
		this.protocolType = protocolType;
		this.keyId = mcuId;
		this.contractList = contractList;

		if (protocolType == Protocol.REVERSEGPRS) {
			targetName = "MODEM";
		} else {
			targetName = "DCU";
		}
	}

	@Override
	public void run() {
		log.info("########## start thread " + targetName + "[" + keyId + "] Contract_size[" + contractList.size() + "] ##########");
		for (Contract c : contractList)
			checkBalance(c.getId());
		log.info("########## End thread " + targetName + "[" + keyId + "] Contract_size[" + contractList.size() + "] ##########");
	}

	private Properties getMessageProp(Supplier supplier) {
		try {
			if (messageProp == null) {
				messageProp = new Properties();
				LanguageDao languageDao = DataUtil.getBean(LanguageDao.class);
				Language la = languageDao.get(supplier.getLangId());
				String lang = (la.getCode_2letter() == null) ? "en" : la.getCode_2letter();
				InputStream ips = getClass().getClassLoader().getResourceAsStream("lang/message_" + lang + ".properties");
				if (ips == null) {
					ips = getClass().getClassLoader().getResourceAsStream("message_en.properties");
				}
				messageProp.load(ips);
			}
		} catch (Exception e) {
			log.debug(e);
		}

		return this.messageProp;
	}

	/**
	 * method name : checkBalance method Desc : 잔액을 체크해서 0일 경우 Emergency Credit Mode 로 변경, 혹은 차단한다.
	 *
	 * @param contract
	 */
	private void checkBalance(int contractId) {
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);
			GroupMemberDao groupMemberDao = DataUtil.getBean(GroupMemberDao.class);
			ContractDao contractDao = DataUtil.getBean(ContractDao.class);
			SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);

			Contract contract = contractDao.get(contractId);
			Meter meter = contract.getMeter();
			Code meterStatus = meter.getMeterStatus();
			meterStatus.getCode();
			Modem modem = meter.getModem();
			Supplier supplier = supplierDao.get(meter.getSupplierId());
			Code meterType = meter.getMeterType();
			meterType.getCode();
			Code creditType = contract.getCreditType();
			creditType.getCode();
			Code contractStatus = contract.getStatus();
			contractStatus.getCode();

			String mcuSysId;
			if (modem.getProtocolType() == Protocol.REVERSEGPRS) {
				mcuSysId = null;
			} else {
				mcuSysId = modem.getMcu().getSysID();
			}

			// IHD 그룹인지 판별할때 필요
			Map<String, Object> conditionMap = new HashMap<String, Object>();
			log.debug("contract.getMdsId():" + meter.getMdsId());
			log.debug("contract.getMeter().getId():" + meter.getId());

			Integer groupId = groupMemberDao.getGroupIdbyMember(meter.getMdsId().toString());

			log.debug("groupId : [" + groupId + "]");
			conditionMap.put("groupId", groupId.toString());

			Double credit = (contract.getCurrentCredit() == null ? 0d : contract.getCurrentCredit()); // 잔액
			ResultStatus status = ResultStatus.FAIL;
			// String rtnStr = "";
			boolean isCutOff = false; // 미터 차단 실행 여부

			txManager.commit(txStatus);

			log.debug("credit : [" + credit + "], PrepaymentThreshold : [" + contract.getPrepaymentThreshold() + "]");

			// 잔액이 임계치보다 작은 경우 고객에게 통보
			if (credit > 0 && credit < (contract.getPrepaymentThreshold() == null ? 0 : contract.getPrepaymentThreshold())) {
				// message event 시작 : 잔액이 임계치보다 작습니다. 충전해주십시오.

				// 선불고객인지 판별
				/*
				 * if("2.2.1".equals(contract.getCreditType().getCode()) || "2.2.2".equals(contract.getCreditType().getCode())){ //그룹멤버인지 판별 if(groupId != -1) { String groupType = groupDao.getGroupTypeByGroup(conditionMap); //IHD 그룹인지 판별 if("IHD".equals(groupType)) { // 현재 잔액이 XXX Rand 입니다. 충전하지 않으면, 에너지 공급이 차단됩니다. log.debug("GroupType : [" + groupType + "]"); IHDMessageUtil ihdMessage = DataUtil.getBean(IHDMessageUtil.class); ihdMessage.getEventMessage(meter.getMdsId(), "Low Balance",
				 * "Your current balance is " + credit + " Rand. If you don't recharge your balance, the power will be blocked."); } } }
				 */
				// message event 종료
			}

			// 잔액이 0 일 경우
			if (credit <= 0) {
				if (Code.PREPAYMENT.equals(creditType.getCode()) && (contract.getEmergencyCreditAutoChange() == null || !contract.getEmergencyCreditAutoChange())) {
					// 잔액이 0 이하 이고 emergency credit 이 수동인 경우 차단

					// 미터가 차단되어 있는지 체크
					// 이 값은 아래 relay off가 성공하고 미터 상태가 이미 cut off 가 아닐때 sms를 보내도록 하기 위한 것이다.
					if (meterStatus != null && meterStatus.getCode().equals(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode())) {
						isCutOff = true;
					}

					// message event 시작 : 미터를 차단하겠습니다.
					// prepay 고객일때
					/*
					 * String paymentCode = contract.getCreditType() == null ? "" : contract.getCreditType().getCode(); if("2.2.1".equals(paymentCode) || "2.2.2".equals(paymentCode)) { if(!("".equals(groupId)) || groupId != -1) { String groupType = groupDao.getGroupTypeByGroup(conditionMap); //IHD 그룹인 경우 if("IHD".equals(groupType)) { // 잔액이 부족하여 에너지 공급을 차단합니다. IHDMessageUtil ihdMessage = DataUtil.getBean(IHDMessageUtil.class); ihdMessage.getEventMessage(contract.getMeter().getMdsId(),
					 * "Zero Balance", "Your current goes to zero or negative, the power will be blocked soon."); } } }
					 */
					// message event 종료

					// Relay Switch CmdOperationUtil 호출
					try {
						// if (meter != null && modem != null && mcu != null) {
						if (meter != null && modem != null) {
							txStatus = txManager.getTransaction(null);
							CmdOperationUtil cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
							Map<String, Object> result = cmdOperationUtil.relayValveOff(mcuSysId, meter.getMdsId());
							txManager.commit(txStatus);

							Object[] values = result.values().toArray(new Object[0]);

							JsonParser jparser = new JsonParser();
							JsonArray ja = null;
							for (Object o : values) {
								ja = jparser.parse((String) o).getAsJsonArray();
								for (int i = 0; i < ja.size(); i++) {
									if (ja.get(i).getAsJsonObject().get("name").getAsString().equals("Result")) {
										status = ResultStatus.SUCCESS;
										break;
									}
								}
								if (status == ResultStatus.SUCCESS)
									break;
							}

							log.debug("Relay Off Status [" + status + "]");

							// Operation Log에 기록
							saveOperationLog(supplier, meterType, meter.getMdsId(), "schedule", status.getCode(), status.name());
						}
					} catch (Exception e) {
						status = ResultStatus.FAIL;
						log.error(e, e);
					}

					// message event 시작 : 미터 차단이 성공했을 때
					if (status == ResultStatus.SUCCESS) {
						// CASE : IHD
						/*
						 * if("2.2.1".equals(contract.getCreditType().getCode()) || "2.2.2".equals(paymentCode)) { if(!("".equals(groupId)) || groupId != 0) { String groupType = groupDao.getGroupTypeByGroup(conditionMap); //IHD 그룹인 경우 paymentCode = contract.getCreditType() == null ? "" : contract.getCreditType().getCode(); if("IHD".equals(groupType)) { //에너지 공급을 차단 하였습니다. 충전하시면 공급이 재개 됩니다. IHDMessageUtil ihdMessage = DataUtil.getBean(IHDMessageUtil.class);
						 * ihdMessage.getEventMessage(contract.getMeter().getMdsId(), "Shutdown Power", "Your supply was blocked. Please recharge your account to resume supply."); } } }
						 */

						// CASE : SMS
						/*
						 * Your supply was blocked. Please recharge your account to resume supply.
						 */
						if (!isCutOff) {
							// SMSNotification(contract, supplier.getCd(), "Votre alimentation etait bloque. s`il vous plait rechargez votre compte.");
							SMSNotification(contract, supplier.getCd());
						}

					}

					// // CmdOperationUtil 호출:생성 후 개발 부분 Start
					// if (contract.getServiceTypeCode().getCode().equals("3.1")) { // Electricity
					// } else if (contract.getServiceTypeCode().getCode().equals("3.3")) { // Gas
					// } else if (contract.getServiceTypeCode().getCode().equals("3.2")) { // Water
					// }

				} else if (contract.getEmergencyCreditAutoChange() != null && contract.getEmergencyCreditAutoChange()) {
					// 잔액이 0 이하 이고 emergency credit 이 자동인 경우 고객정보를 emergency credit 모드로 업데이트
					if (contract.getEmergencyCreditStartTime() == null || "".equals(contract.getEmergencyCreditStartTime())) {
						// EmergencyCreditType으로 변경
						log.info("ContractId[" + contract.getId() + "] is change EmergencyCreditType");
						CodeDao codeDao = DataUtil.getBean(CodeDao.class);
						Code newCreditType = codeDao.findByCondition("code", Code.EMERGENCY_CREDIT);
						changeCreditType(contract, "creditType", creditType, newCreditType);
					} else {
						// EmergencyDuration을 체크 후 기간이 지났을 경우 EmernencyCredit을 Manual로 변경
						Boolean isEmergencyCreditContract = checkEmergencyDuration(contract);
						if (!isEmergencyCreditContract) {
							// Manual로 변경
							log.info("ContractId[" + contract.getId() + "] is change PrepayType");
							CodeDao codeDao = DataUtil.getBean(CodeDao.class);
							Code newCreditType = codeDao.findByCondition("code", Code.PREPAYMENT);
							changeCreditType(contract, "creditType", creditType, newCreditType);
						}
					}
				}
			}
			// 잔액이 0보다 크고 미터 상태가 cut off 이거나 계약 상태가 임시중단 상태이면 relay on 시도한다.
			else {
				String cutoffCode = CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()).getCode();
				String suspended = CommonConstants.ContractStatus.SUSPENDED.getCode();
				String pauseCode = CommonConstants.ContractStatus.PAUSE.getCode();
				boolean cutoffnotsuspended = meterStatus != null && meterStatus.getCode().equals(cutoffCode);
				cutoffnotsuspended &= (contractStatus != null && !contractStatus.getCode().equals(suspended));

				if (cutoffnotsuspended || (contractStatus != null && contractStatus.getCode().equals(pauseCode))) {
					isCutOff = true;
				}

				if (isCutOff) {
					try {
						txStatus = txManager.getTransaction(null);
						CmdOperationUtil cmdOperationUtil = DataUtil.getBean(CmdOperationUtil.class);
						cmdOperationUtil.relayValveOn(mcuSysId, meter.getMdsId());
						txManager.commit(txStatus);

						// Operation Log에 기록
						saveOperationLog(supplier, meterType, meter.getMdsId(), "schedule", status.getCode(), status.name());
					} catch (Exception e) {
					}
				}
			}

			log.info("end check balance for [" + meter.getMdsId() + ", " + contract.getContractNumber() + "]");
		} catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				} catch (Exception ee) {
				}
			}
			log.error(e, e);
		}
	}

	private Boolean checkEmergencyDuration(Contract contract) {
		Boolean isEmergencyCredit = true;

		String emergencyCreditStartTime = contract.getEmergencyCreditStartTime();
		Integer emergencyCreditDuration = contract.getEmergencyCreditMaxDuration();
		log.info("emergencyType contractId[" + contract.getId() + "], startTime[" + emergencyCreditStartTime + "], duration[" + emergencyCreditDuration + "]");

		try {
			long afterDurationDate = emergencyCreditStartTime == null ? 0 : Long.parseLong(TimeUtil.getAddedDay(emergencyCreditStartTime, emergencyCreditDuration));
			long today = Long.parseLong(TimeUtil.getCurrentTime());
			if (afterDurationDate < today) {
				isEmergencyCredit = false;
			} else {
				isEmergencyCredit = true;
			}
		} catch (Exception e) {
			log.error(e, e);
		}
		return isEmergencyCredit;
	}

	/**
	 * method name : changeCreditType method Desc : ContractChangeLog 에 데이터 insert
	 *
	 * @param contract
	 * @param field
	 * @param beforeValue
	 * @param afterValue
	 */
	private void changeCreditType(Contract contract, String field, Code oldCreditType, Code newCreditType) {
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);
			log.info("newCreditType[" + newCreditType.getName() + "], oldCreditType[" + oldCreditType.getName() + "]");
			// ContractChangeLog Insert

			ContractDao contractDao = DataUtil.getBean(ContractDao.class);
			Contract emergencyContract = contract;
			emergencyContract.setCreditType(newCreditType);

			if (oldCreditType != null && Code.EMERGENCY_CREDIT.equals(oldCreditType.getCode()) && !Code.EMERGENCY_CREDIT.equals(newCreditType.getCode())) {
				contract.setEmergencyCreditAutoChange(null);
				contract.setEmergencyCreditMaxDuration(null);
				contract.setEmergencyCreditStartTime(null);
			} else {
				emergencyContract.setEmergencyCreditStartTime(TimeUtil.getCurrentTime());
			}

			contractDao.update(emergencyContract);
			ContractChangeLogDao contractChangeLogDao = DataUtil.getBean(ContractChangeLogDao.class);
			ContractChangeLog contractChangeLog = new ContractChangeLog();

			contractChangeLog.setContract(contract);
			contractChangeLog.setCustomer(contract.getCustomer());
			contractChangeLog.setStartDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			contractChangeLog.setChangeField(field);

			if (oldCreditType == null) {
				contractChangeLog.setBeforeValue(null);
			} else {
				contractChangeLog.setBeforeValue(StringUtil.nullToBlank(oldCreditType));
			}

			if (newCreditType == null) {
				contractChangeLog.setAfterValue(null);
			} else {
				contractChangeLog.setAfterValue(StringUtil.nullToBlank(newCreditType));
			}

			// contractChangeLog.setOperator(operator);
			contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			// contractChangeLog.setDescr(descr);

			contractChangeLogDao.add(contractChangeLog);
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.warn(e, e);

			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				} catch (Exception te) {
				}
			}
		}
	}

	private void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Integer status, String errorReason) {
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);
			OperationLogDao operationLogDao = DataUtil.getBean(OperationLogDao.class);
			CodeDao codeDao = DataUtil.getBean(CodeDao.class);
			Code operationCode = codeDao.getCodeIdByCodeObject("8.1.4");

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar today = Calendar.getInstance();
			String currDateTime = sdf.format(today.getTime());

			OperationLog log = new OperationLog();

			log.setOperatorType(1);// operator
			log.setOperationCommandCode(operationCode);
			log.setYyyymmdd(currDateTime.substring(0, 8));
			log.setHhmmss(currDateTime.substring(8, 14));
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

			txManager.commit(txStatus);
		} catch (Exception e) {
			log.warn(e, e);

			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				} catch (Exception te) {
				}
			}
		}
	}

	/**
	 * method name : SMSNotification method Desc : Charge에 대한 SMS 통보
	 * 
	 * @param contract
	 *            , message
	 */
	// private void SMSNotification(Contract contract, DecimalPattern cdFormat, String message) {
	private void SMSNotification(Contract contract, DecimalPattern cdFormat) {
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);
			CodeDao codeDao = DataUtil.getBean(CodeDao.class);
			ContractDao contractDao = DataUtil.getBean(ContractDao.class);
			SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
			Supplier supplier = supplierDao.get(contract.getSupplierId());

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
			condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
			condition.put("smsYn", true);
			condition.put("contractId", contract.getId());
			List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);

			if (contractInfo.size() > 0) {
				String mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
				String currentCredit = contractInfo.get(0).get("CURRENTCREDIT") == null ? "0" : contractInfo.get(0).get("CURRENTCREDIT").toString();

				DecimalFormat cdf = DecimalUtil.getDecimalFormat(cdFormat);
				String text = null;
				// text = message
				// + "\n Nom Du Client : " + contractInfo.get(0).get("CUSTOMERNAME")
				// + "\n Type Client: " + contractInfo.get(0).get("SERVICETYPE")
				// + "\n Courant Credit : " + cdf.format(Double.parseDouble(currentCredit)).toString();

				text = getMessageProp(supplier).getProperty("aimir.sms.balance.threshold.msg") + "\n " + getMessageProp(supplier).getProperty("aimir.sms.customer.name") + " : " + contractInfo.get(0).get("CUSTOMERNAME") + "\n " + getMessageProp(supplier).getProperty("aimir.sms.supplier.type") + " : " + contractInfo.get(0).get("SERVICETYPE") + "\n " + getMessageProp(supplier).getProperty("aimir.sms.credit.current") + " : " + cdf.format(Double.parseDouble(currentCredit)).toString();

				log.debug("[" + contract.getContractNumber() + "][" + mobileNo + "] Send SMS ==> " + text);

				Properties prop = new Properties();
				prop.load(getClass().getClassLoader().getResourceAsStream("config/schedule.properties"));

				String smsClassPath = prop.getProperty("smsClassPath");
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();

				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				String messageId = (String) m.invoke(obj, mobileNo, text, prop);

				if (!"".equals(messageId)) {
					log.info("contractId [ "+ contract.getId() +"],	SMS messageId [" + messageId + "]");
//					contract.setSmsNumber(messageId);
//					contractDao.updateSmsNumber(contract.getId(), messageId);
				}
			}
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.warn(e, e);

			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				} catch (Exception te) {
				}
			}
		}
	}
}