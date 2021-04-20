package com.aimir.service.system.impl.prepayment;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CasherStatus;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.device.WaterMeterDao;
import com.aimir.dao.mvm.BillingBlockTariffDao;
import com.aimir.dao.prepayment.AddBalanceWSChargingDao;
import com.aimir.dao.prepayment.VendorCasherDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.DepositHistoryDao;
import com.aimir.dao.system.FixedVariableDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.MonthlyBillingLogDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.PrepaymentLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.Meter;
import com.aimir.model.device.OperationLog;
import com.aimir.model.prepayment.DepositHistory;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Customer;
import com.aimir.model.system.FixedVariable;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.PrepaymentLog;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.system.prepayment.PrepaymentChargeManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;


/**
 * PrepaymentChargeManagerImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 2. 15.  v1.0        문동규   Prepayment Charge Service Impl
 * </pre>
 */
@Service(value = "prepaymentChargeManager")
public class PrepaymentChargeManagerImpl implements PrepaymentChargeManager {
    protected static Log log = LogFactory.getLog(PrepaymentChargeManagerImpl.class);
    
    @Autowired
    SupplierDao supplierDao; 

    @Autowired
    ContractDao contractDao;

    @Autowired
    ContractChangeLogDao contractChangeLogDao;

    @Autowired
    TariffTypeDao tariffTypeDao;

    @Autowired
    OperatorDao operatorDao;

    @Autowired
    PrepaymentLogDao prepaymentLogDao; 
    
    @Autowired
    BillingBlockTariffDao billingBlockTariffDao; 

    @Autowired
    MeterDao meterDao;

    @Autowired
    CodeDao codeDao;

    @Autowired
    WaterMeterDao waterMeterDao;

    @Autowired
    OperationLogDao operationLogDao;

    @Autowired
    AddBalanceWSChargingDao addBalanceWSChargingDao;

    @Autowired
    HibernateTransactionManager transactionManager;

    @Autowired
    DepositHistoryDao depositHistoryDao;
    
    @Autowired
    VendorCasherDao vendorCasherDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;

    @Autowired
    LocationDao locationDao;
    
    @Autowired
    FixedVariableDao fixedVariableDao;


    /**
     * method name : getPrepaymentChargeList<b/>
     * method Desc : Prepayment Charge 가젯에서 Prepayment Charge List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getPrepaymentChargeList(Map<String, Object> conditionMap) throws Exception {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        List<Map<String, Object>> result = contractDao.getPrepaymentChargeContractList(conditionMap, false);
        
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());

        for (Map<String, Object> map : result) {
            map.put("lastTokenDate", TimeLocaleUtil.getLocaleDate((String)map.get("lastTokenDate"), lang, country));
            map.put("currentCredit", ((map.get("currentCredit") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("currentCredit"))) : cdf.format(0d)));
            map.put("currentArrears", ((map.get("currentArrears") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("currentArrears"))) : cdf.format(0d)));
            map.put("currentArrears2", ((map.get("currentArrears2") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("currentArrears2"))) : cdf.format(0d)));
        }
        
        return result;
    }

    /**
     * method name : getPrepaymentChargeListTotalCount<b/>
     * method Desc : Prepayment Charge 가젯에서 Prepayment Charge List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public Integer getPrepaymentChargeListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = contractDao.getPrepaymentChargeContractList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    /**
     * method name : getChargeHistoryList<b/>
     * method Desc : Prepayment Charge 가젯에서 충전이력 List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getChargeHistoryList(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        List<Map<String, Object>> result = prepaymentLogDao.getChargeHistoryList(conditionMap, false);

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());

        for (Map<String, Object> map : result) {
            map.put("lastTokenDate", TimeLocaleUtil.getLocaleDate((String)map.get("lastTokenDate"), lang, country));
//            map.put("currentCredit", ((map.get("currentCredit") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("currentCredit"))) : cdf.format(0d)));
            map.put("balance", ((map.get("balance") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("balance"))) : cdf.format(0d)));
            map.put("vat", ((map.get("vat") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("vat"))) : cdf.format(0d)));
            map.put("arrears", ((map.get("arrears") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("arrears"))) : cdf.format(0d)));
            map.put("arrears2", ((map.get("arrears2") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("arrears2"))) : cdf.format(0d)));
            map.put("chargedArrears", ((map.get("chargedArrears") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("chargedArrears"))) : cdf.format(0d)));
            map.put("chargedArrears2", ((map.get("chargedArrears2") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("chargedArrears2"))) : cdf.format(0d)));
            map.put("chargedCredit", ((map.get("chargedCredit") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("chargedCredit"))) : cdf.format(0d)));
        }

        return result;
    }

    /**
     * method name : getChargeHistoryListTotalCount<b/>
     * method Desc : Prepayment Charge 가젯에서 충전이력 List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public Integer getChargeHistoryListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = prepaymentLogDao.getChargeHistoryList(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }
    
    /**
     * method name : getBalanceHistoryList<b/>
     * method Desc : Vending Station 가젯에서 차감에 대한 정보를 볼 수 있도록 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getBalanceHistoryList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = billingBlockTariffDao.getBalanceHistoryList(conditionMap, false);
        
        Integer supplierId = (Integer)conditionMap.get("supplierId");

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        for (Map<String, Object> map : result) {
            map.put("writeDate", TimeLocaleUtil.getLocaleDate((String)map.get("writeDate"), lang, country));
            String lpTime = (map.get("yyyymmdd") != null && map.get("hhmmss") != null) ? (String)map.get("yyyymmdd") + (String)map.get("hhmmss") : null;
            if(lpTime == null) {
                lpTime = map.get("yyyymmdd") != null ? (String)map.get("yyyymmdd") : null;
            }
            
            map.put("lpTime", lpTime != null ? TimeLocaleUtil.getLocaleDate(lpTime, lang, country) : "");
            map.put("activeImport", ((map.get("activeImport") != null) ? mdf.format(DecimalUtil.ConvertNumberToDouble(map.get("activeImport"))) : ""));
            map.put("activeExport", ((map.get("activeExport") != null) ? mdf.format(DecimalUtil.ConvertNumberToDouble(map.get("activeExport"))) : ""));
            map.put("accUsage", ((map.get("accUsage") != null) ? mdf.format(DecimalUtil.ConvertNumberToDouble(map.get("accUsage"))) : ""));
            map.put("accBill", ((map.get("accBill") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("accBill"))) : ""));
            map.put("usage", ((map.get("usage") != null) ? mdf.format(DecimalUtil.ConvertNumberToDouble(map.get("usage"))) : ""));
            map.put("bill", ((map.get("bill") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("bill"))) : ""));
            map.put("balance", ((map.get("balance") != null) ? cdf.format(DecimalUtil.ConvertNumberToDouble(map.get("balance"))) : ""));
        }


        return result;
    }
    
    /**
     * method name : getBalanceHistoryListTotalCount<b/>
     * method Desc : Vending Station 가젯에서 차감에 대한 정보를 볼 수 있도록 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Object getBalanceHistoryListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = billingBlockTariffDao.getBalanceHistoryList(conditionMap, true);
        return result.get(0).get("total");
    }

    /**
     * method name : savePrepaymentCharge<b/>
     * method Desc : Prepayment Charge 가젯에서 충전금액을 저장한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public String savePrepaymentCharge(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String lastTokenId = StringUtil.nullToBlank(conditionMap.get("lastTokenId"));
        Double contractDemand = (Double)conditionMap.get("contractDemand");
        Integer tariffCode = (Integer)conditionMap.get("tariffCode");
        Double amount = (Double)conditionMap.get("amount");
        String result = null;

        String supplierName = null;
        String dateTime = null;
        try {
            dateTime = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }

        Supplier supplier = supplierDao.get(supplierId);
        supplierName = supplier.getName();

        try {
            result = addBalanceCharging(supplierName, dateTime, contractNumber, mdsId, 
                    lastTokenId, amount, contractDemand, tariffCode, "", "", operatorId);
        } catch (Exception e) {
            log.error(e, e);
            e.printStackTrace();
            result = "fail";
        }

        return result;
    }

    /**
     * method name : checkChargeAvailable<b/>
     * method Desc : 선불 충전 시 충전가능여부를 체크한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> checkChargeAvailable(Map<String, Object> conditionMap) {
        String contractNumber = (String)conditionMap.get("contractNumber");
        Map<String, Object> result = new HashMap<String, Object>();
        
        List<Object> list = contractDao.getContractIdByContractNo(contractNumber);

        if (list != null && list.size() > 0) {
            Contract contract = (Contract)list.get(0);
            Boolean check = contract.getChargeAvailable();
            
            if (check != null && check == true) {
                result.put("result", true);
            } else {
                result.put("result", false);
            }
        } else {
            result.put("result", false);
        }
        return result;
    }

    public Map<String, Object> vendorSavePrepaymentCharge(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String result = null;
        Double deposit = null;
        String supplierName = null;
        String dateTime = null;
        Map<String, Object> ret = new HashMap<String, Object>();
        
        try {
            dateTime = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        Supplier supplier = supplierDao.get(supplierId);
        supplierName = supplier.getName();
        conditionMap.put("dateTime", dateTime);
        conditionMap.put("supplierName", supplierName);

        Map<String, Object> resultMap = vendorBalanceCharging(conditionMap);
        result = (String) resultMap.get("result");
        
        if (result.equals("success")) {
            Operator operator = operatorDao.get((Integer) conditionMap.get("operatorId"));
            deposit = operator.getDeposit();
            Long prepaymentLogId = (Long) resultMap.get("prepaymentLogId");
            ret.put("credit", resultMap.get("credit"));
            ret.put("isCutOff", resultMap.get("isCutOff"));
            ret.put("deposit", deposit);
            ret.put("prepaymentLogId", prepaymentLogId);
        }
        ret.put("result", result);
        return ret;
    }

    /**
     * method name : vendorSavePrepaymentChargeSPASA<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> vendorSavePrepaymentChargeSPASA(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String result = null;
        Double deposit = null;
        String supplierName = null;
        String dateTime = null;
        Map<String, Object> ret = new HashMap<String, Object>();
        
        try {
            dateTime = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
            e.printStackTrace();
        }
        Supplier supplier = supplierDao.get(supplierId);
        supplierName = supplier.getName();
        conditionMap.put("dateTime", dateTime);
        conditionMap.put("supplierName", supplierName);

        Map<String, Object> resultMap = vendorBalanceChargingSPASA(conditionMap);
        result = (String) resultMap.get("result");
        
        if (result.equals("success")) {
            Operator operator = operatorDao.get((Integer) conditionMap.get("operatorId"));
            deposit = operator.getDeposit();
            Long prepaymentLogId = (Long) resultMap.get("prepaymentLogId");
            ret.put("credit", resultMap.get("credit"));
            ret.put("isCutOff", resultMap.get("isCutOff"));
            ret.put("deposit", deposit);
            ret.put("prepaymentLogId", prepaymentLogId);
        }
        ret.put("result", result);
        return ret;
    }

    /**
     * method name : addBalanceCharging<br/>
     * method Desc : 충전화면에서 입력한 금액을 충전. 웹서비스 로직과 동일함.
     *
     * @param supplierName      Utility ID - 공급사 아이디
     * @param dateTime          Date & Time - 충전 날짜
     * @param contractNumber    Contract ID - 고객의 계약번호
     * @param mdsId             Meter Serial Number - 미터 시리얼 번호
     * @param accountId         Account ID - 충전 아이디
     * @param amount            Amount - 충전액
     * @param powerLimit        Power Limit(kWh) - 전력 사용량
     * @param tariffCode        Tariff Code - 과금 분류 코드(요금 코드)
     * @param source            source - 충전 방식(온라인, 카드)<b/>
     *                          값이 있으면 Contract.keyType 에 저장 
     * @param encryptionKey     Encryption Key - 암호화 시 인증 키<b/>
     *                          값이 있으면 Contract 조회 시 Contract.keyNum 값 체크
     * @param operatorId        Operator ID
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unused")
    private String addBalanceCharging(String supplierName, String dateTime, String contractNumber, String mdsId, String accountId,
            Double amount, Double powerLimit, Integer tariffCode, String source, String encryptionKey, Integer operatorId) throws Exception {
        
        String rtnStr = "";
        StringBuilder sb = new StringBuilder();

        sb.append("\n supplierName[" + supplierName + "]");
        sb.append("\n dateTime[" + dateTime + "]");
        sb.append("\n contractNumber[" + contractNumber + "]");
        sb.append("\n mdsId[" + mdsId + "]");
        sb.append("\n accountId[" + accountId + "]");
        sb.append("\n amount[" + amount + "]");
        sb.append("\n powerLimit[" + powerLimit + "]");
        sb.append("\n tariffCode[" + tariffCode + "]");
        sb.append("\n source[" + source + "]");
        sb.append("\n encryptionKey[" + encryptionKey + "]");

        log.info(sb.toString());

        // mandatory data check
        if (StringUtil.nullToBlank(supplierName).isEmpty() || StringUtil.nullToBlank(dateTime).isEmpty() || 
                StringUtil.nullToBlank(contractNumber).isEmpty() || StringUtil.nullToBlank(mdsId).isEmpty() ||
                StringUtil.nullToBlank(accountId).isEmpty() || StringUtil.nullToBlank(amount).isEmpty() ||
                StringUtil.nullToBlank(powerLimit).isEmpty() || StringUtil.nullToBlank(tariffCode).isEmpty()) {
            return "fail : mandatory data is required";
        }

        TransactionStatus txStatus = null;
/*        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_SERIALIZABLE);*/

        try {
            txStatus = transactionManager.getTransaction(null);

            // 웹서비스로그 저장
            /*
            AddBalanceWSCharging addBalanceWSCharging = new AddBalanceWSCharging();

            addBalanceWSCharging.setSupplierName(supplierName);
            addBalanceWSCharging.setDateTime(dateTime);
            addBalanceWSCharging.setContractNumber(contractNumber);
            addBalanceWSCharging.setMdsId(mdsId);
            addBalanceWSCharging.setAccountId(contractNumber);
            addBalanceWSCharging.setAmount(amount);
            addBalanceWSCharging.setPowerLimit(powerLimit);
            addBalanceWSCharging.setTariffCode(tariffCode);
            addBalanceWSCharging.setSource(source);
            addBalanceWSCharging.setEncryptionKey(encryptionKey);

            addBalanceWSChargingDao.add(addBalanceWSCharging);
             */
            Meter meter = meterDao.get(mdsId);

            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);

            if (!StringUtil.nullToBlank(encryptionKey).isEmpty()) {
                conditionMap.put("keyNum", encryptionKey);
            }

            // select Contract
            List<Contract> listCont = contractDao.getPrepaymentContract(conditionMap);

            if (listCont != null && listCont.size() > 0) {
                contract = listCont.get(0);
            } else {
                transactionManager.commit(txStatus);
                return "fail : invalid contract info";
            }
            Double preCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
            Double currentCredit = new BigDecimal(StringUtil.nullToZero(contract.getCurrentCredit())).add(new BigDecimal(amount)).doubleValue();
            boolean isCutOff = false;    // 차단여부

            // 잔액이 플러스인 경우만 미터기 차단해제 적용.
            if (meter != null && meter.getModel() != null && currentCredit > 0d) {
                // CommandOperationUtil 생성 후 개발 부분 Start
                if (meter.getPrepaymentMeter() != null && meter.getPrepaymentMeter()) { // Prepayment Meter 인 경우 온라인 충전

                } else { // Soft Credit 인 경우 충전
                    ResultStatus status = ResultStatus.FAIL;
                    String modelName = meter.getModel().getName();

                    // 차단되어 있는지 체크
                    if (meter.getMeterStatus() != null &&
                            meter.getMeterStatus().equals(CommonConstants.getMeterStatusByName(MeterStatus.CutOff.name()))) {
                        isCutOff = true;
                    }

                    if (isCutOff) {
                        try {
                            status = ResultStatus.SUCCESS;
                            
                            Map<String, Object> resultMap = 
                                    cmdOperationUtil.relayValveOn(meter.getModem().getMcu().getSysID(), meter.getMdsId());
                            
                            Object[] values = resultMap.values().toArray(new Object[0]);
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

                        if(status == ResultStatus.SUCCESS) {
                            this.SMSNotification(contract, amount, preCredit, isCutOff);
                        }

                        Code operationCode = codeDao.getCodeIdByCodeObject("8.1.4");
                        Supplier supplier = supplierDao.getSupplierByName(supplierName);

                        if (operationCode != null) {
                            String currDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                            OperationLog log = new OperationLog();
                            log.setOperatorType(1);//operator
                            log.setOperationCommandCode(operationCode);
                            log.setYyyymmdd(currDateTime.substring(0,8));
                            log.setHhmmss(currDateTime.substring(8,14));
                            log.setYyyymmddhhmmss(currDateTime);
                            log.setDescription("");
                            log.setErrorReason(status.name());
                            log.setResultSrc("");
                            log.setStatus(status.getCode());
                            log.setTargetName(mdsId);
                            log.setTargetTypeCode(meter.getMeterType());
                            //log.setUserId(CommandOperator.WEBSERVICE.getOperatorName());
                            log.setSupplier(supplier);
                            operationLogDao.add(log);
                        }
                    }
                }
                // CommandOperationUtil 생성 후 개발 부분 End
            }

            Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;
            Code keyCode = null;
            Operator operator = operatorDao.get(operatorId);
            // insert ContractChangeLog
            addContractChangeLog(contract, operator, "lastTokenDate", contract.getLastTokenDate(), dateTime);
            addContractChangeLog(contract, operator, "chargedCredit", contract.getChargedCredit(), amount.toString());
            addContractChangeLog(contract, operator, "currentCredit", contract.getCurrentCredit(), currentCredit.toString());
            addContractChangeLog(contract, operator, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString());

            contract.setLastTokenDate(dateTime);
            contract.setChargedCredit(amount);
            contract.setCurrentCredit(currentCredit);
            contract.setLastChargeCnt(lastChargeCnt);

            // update Contract
            contractDao.update(contract);

            // insert PrepaymentLog
            PrepaymentLog prepaymentLog = new PrepaymentLog();

            prepaymentLog.setCustomer(contract.getCustomer());
            prepaymentLog.setContract(contract);
            prepaymentLog.setKeyNum(encryptionKey);
            prepaymentLog.setKeyType(keyCode);
            prepaymentLog.setChargedCredit(amount);
            prepaymentLog.setLastTokenDate(dateTime);
            prepaymentLog.setLastTokenId(accountId);
            prepaymentLog.setOperator(operator);
            Integer emergencyYn = null;
            if (contract.getEmergencyCreditAvailable() != null) {
                emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
            }
            prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
            prepaymentLog.setPowerLimit(powerLimit);
            prepaymentLog.setBalance(currentCredit);
            prepaymentLog.setLocation(contract.getLocation());
            prepaymentLog.setTariffIndex(contract.getTariffIndex());

            prepaymentLogDao.add(prepaymentLog);

            // Notification 통보 프레임 워크 생성 후 개발 Start
            if(!isCutOff) {
                this.SMSNotification(contract, amount, preCredit, isCutOff);
            }
            // Notification 통보 프레임 워크 생성 후 개발 End

            transactionManager.commit(txStatus);

            return "success";
        } catch (Exception e) {

            if (transactionManager != null) {
                transactionManager.rollback(txStatus);
            }

            e.printStackTrace();

            log.error(e.getMessage(), e);

            return "fail : " + e.getMessage();
        }
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
    private void addContractChangeLog(Contract contract, Operator operator, String field, Object beforeValue, Object afterValue) {

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

        contractChangeLog.setOperator(operator);
        contractChangeLog.setWriteDatetime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

        contractChangeLogDao.add(contractChangeLog);
    }
    
    /**
     * method name : SMSNotificationForECG
     * method Desc : Charge 에 대한 SMS 통보 (ECG용)
     * @param contract
     */
    @Override
    public void SMSNotificationForECG(Contract contract, Double amount, Double preCurrentCredit, Boolean isValid) {
        Map<String,String> returnMap = null;
        String text = null;
        String mobileNo = null;
        try {
            Integer contractId = contract.getId();
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            condition.put("contractId", contractId);
            List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
            log.info("contractInfo Size : " + contractInfo.size());
            if(contractInfo.size() > 0) {
                log.info("make smsMsg");
                mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
                Supplier supplier = supplierDao.get(contract.getSupplierId());
                
                Properties messageProp = new Properties();
                String lang = supplier.getLang().getCode_2letter();
                InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
                if(ip == null){
                    ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");              
                }
                messageProp.load(ip);

                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                Double renewCurrentCredit = preCurrentCredit + amount;

                /*
                 *  SMS Message Sample 
                 *  if(isValid) {
                 *      Your balance is now available. The power is supplied again.
                 *      Supply Type : Electricity
                 *      Charge Amount : 100
                 *      Current Credit : 200
                 *  } else {
                 *      Customer:Tom
                 *      Meter:123456789
                 *      22/04/2016 Charge Amount:100
                 *      22/04/2016 Current Credit:200
                 *  } 
                 *  
                }*/
                if(isValid) {
                    text =  messageProp.getProperty("aimir.sms.balance.charge.msg.ecg")
                            + "\n " + messageProp.getProperty("aimir.sms.supplier.type") + " : " + contractInfo.get(0).get("SERVICETYPE")
                            + "\n " + messageProp.getProperty("aimir.sms.balance.charge.amount") + " : " + amount.toString()
                            + "\n " + messageProp.getProperty("aimir.sms.credit.current") + " : " +  cdf.format(renewCurrentCredit).toString();
                } else {
                    String date = TimeLocaleUtil.getLocaleDate(TimeUtil.getCurrentDay() , supplier.getLang().getCode_2letter(), supplier.getCountry().getCode_2letter());
                    
                    text = messageProp.getProperty("aimir.customer") + ":" + contractInfo.get(0).get("CUSTOMERNAME")
                            + "\n" + messageProp.getProperty("aimir.meter") + ":" + contractInfo.get(0).get("METERID")
                            + "\n" + date + " " + messageProp.getProperty("aimir.sms.balance.charge.amount") + ":" + amount.toString()
                            + "\n" + date + " " + messageProp.getProperty("aimir.sms.credit.current") + ":" + cdf.format(renewCurrentCredit).toString();
                }               
                new Exception();
                log.info("make smsMsg");
                log.info("########## txt = "+text+" ##########");
            }
            if(text != null && mobileNo != null) {
                CommonSMSSend(text,mobileNo,contractId);
            }
        } catch (Exception e) {
            log.error(e,e);
        }
        
    }
    
    public void CommonSMSSend(String text, String mobileNo, Integer contractId) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
            log.info("prop load : "+prop.containsKey("smsClassPath"));
            
            if(contractId != null) {
                log.info("########## txt = "+text+" ##########");
                log.info("START SMS");
                String smsClassPath = prop.getProperty("smsClassPath");
                log.info("smsClassPath : "+smsClassPath);
                SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
                Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
                String messageId = (String) m.invoke(obj, mobileNo, text, prop);
                log.info("FINISHED SMS");
                
                if(!"".equals(messageId)) {
                    log.info("contractId [ "+ contractId +"],   SMS messageId [" + messageId + "]");
                    //contractDao.updateSmsNumber(contract.getId(), messageId);
                }
            }
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    
    
    /**
     * method name : SMSNotification
     * method Desc : Charge 에 대한 SMS 통보
     * @param contract
     */
    public void SMSNotification(Contract contract, Double amount, Double preCurrentCredit, Boolean isValid) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
            log.info("prop load : "+prop.containsKey("smsClassPath"));
            
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            condition.put("contractId", contract.getId());
            List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
            log.info("contractInfo Size : " + contractInfo.size());
            if(contractInfo.size() > 0) {
                log.info("in contractInfo");
                String mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
                Supplier supplier = supplierDao.get(contract.getSupplierId());
                
                Properties messageProp = new Properties();
                String lang = supplier.getLang().getCode_2letter();
                InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
                if(ip == null){
                    ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");              
                }
                messageProp.load(ip);

                DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                Double renewCurrentCredit = preCurrentCredit + amount;
                String text = null;
                
/*              if(isValid) {
                    text =  "Your balance is now available. The power is supplied again."
                            + "\n Supply Type : " + contractInfo.get(0).get("SERVICETYPE")
                            + "\n Charge Amount : " + amount.toString()
                            + "\n Current Credit : " +  cdf.format(renewCurrentCredit).toString();
                } else {
                    text = "Customer Name : " + contractInfo.get(0).get("CUSTOMERNAME")
                            + "\n Supply Type : " + contractInfo.get(0).get("SERVICETYPE")
                            + "\n Charge Amount : " + amount.toString()
                            + "\n Current Credit : " +  cdf.format(renewCurrentCredit).toString();
                }*/

                if(isValid) {
                    text =  messageProp.getProperty("aimir.sms.balance.charge.msg")
                            + "\n " + messageProp.getProperty("aimir.sms.supplier.type") + " : " + contractInfo.get(0).get("SERVICETYPE")
                            + "\n " + messageProp.getProperty("aimir.sms.balance.charge.amount") + " : " + amount.toString()
                            + "\n " + messageProp.getProperty("aimir.sms.credit.current") + " : " +  cdf.format(renewCurrentCredit).toString();
                } else {
                    text = messageProp.getProperty("aimir.sms.customer.name") + " : " + contractInfo.get(0).get("CUSTOMERNAME")
                            + "\n " + messageProp.getProperty("aimir.sms.supplier.type") + " : " + contractInfo.get(0).get("SERVICETYPE")
                            + "\n " + messageProp.getProperty("aimir.sms.balance.charge.amount") + " : " + amount.toString()
                            + "\n " + messageProp.getProperty("aimir.sms.credit.current") + " : " + cdf.format(renewCurrentCredit).toString();
                }               
                
                log.info("########## txt = "+text+" ##########");
                
                log.info("START SMS");
                String smsClassPath = prop.getProperty("smsClassPath");
                log.info("smsClassPath : "+smsClassPath);
                SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
                Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
                String messageId = (String) m.invoke(obj, mobileNo, text, prop);
                log.info("FINISHED SMS");
                
                if(!"".equals(messageId)) {
                    log.info("contractId [ "+ contract.getId() +"], SMS messageId [" + messageId + "]");
                    //contractDao.updateSmsNumber(contract.getId(), messageId);
                }
                
            }
        } catch (Exception e) {
            log.error(e,e);
        }
    }
    
    /**
     * method name : SMSNotification
     * method Desc : Charge 에 대한 SMS 통보
     * @param contract
     */
    public void SMSNotificationWithText(Contract contract, String text) {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
            log.info("prop load : "+prop.containsKey("smsClassPath"));
            
            Map<String, Object> condition = new HashMap<String, Object>();
            condition.put("prepayCreditId", codeDao.getCodeIdByCode("2.2.1"));
            condition.put("emergencyICreditId", codeDao.getCodeIdByCode("2.2.2"));
            condition.put("smsYn", true);
            condition.put("contractId", contract.getId());
            List<Map<String, Object>> contractInfo = contractDao.getContractSMSYN(condition);
            log.info("contractInfo Size : " + contractInfo.size());
            if(contractInfo.size() > 0) {
                log.info("in contractInfo");
                String mobileNo = contractInfo.get(0).get("MOBILENO").toString().replace("-", "");
                Supplier supplier = supplierDao.get(contract.getSupplierId());
                
                Properties messageProp = new Properties();
                String lang = supplier.getLang().getCode_2letter();
                InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
                if(ip == null){
                    ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");              
                }
                messageProp.load(ip);
                
                log.info("########## txt = "+text+" ##########");
                
                log.info("START SMS");
                String smsClassPath = prop.getProperty("smsClassPath");
                log.info("smsClassPath : "+smsClassPath);
                SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
                Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
                String messageId = (String) m.invoke(obj, mobileNo, text, prop);
                log.info("FINISHED SMS");
                
                if(!"".equals(messageId)) {
                    log.info("contractId [ "+ contract.getId() +"], SMS messageId [" + messageId + "]");
                    //contractDao.updateSmsNumber(contract.getId(), messageId);
                }
                
            }
        } catch (Exception e) {
            log.error(e,e);
        }
    }

    private Map<String, Object> vendorBalanceCharging(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        Boolean isVendor = (Boolean) condition.get("isVendor");
        String supplierName = StringUtil.nullToBlank(condition.get("supplierName"));
        String casherId = StringUtil.nullToBlank(condition.get("casherId"));
        Integer contractId = (Integer) condition.get("contractId");
        String contractNumber = StringUtil.nullToBlank(condition.get("contractNumber"));
        String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
        String accountId = StringUtil.nullToBlank(condition.get("accountId"));
        Double amount = (Double) condition.get("amount");	//실제 충전 금액
        Double arrears = (Double) condition.get("arrears");
        Double arrears2 = (Double) condition.get("arrears2");
        Double contractDemand = (Double) condition.get("contractDemand");
        Integer operatorId = (Integer) condition.get("operatorId");
        Double contractPrice = (Double) condition.get("contractPrice");
        Boolean isPartpayment = (Boolean) condition.get("isPartpayment");
        Boolean partpayReset = (Boolean) condition.get("partpayReset");
        Integer payTypeId = (Integer) condition.get("payTypeId");
        Double totalAmountPaid = (Double) condition.get("totalAmountPaid");	//고객이 지불한 총 금액 (amount+vat+arrears1+2)
        Double vat = (Double) condition.get("vat");
        
        String rtnStr = "";
        
        TransactionStatus txStatus = null;
        log.info(condition);
        
        try {
            txStatus = transactionManager.getTransaction(null);
            
            // 웹서비스로그 저장
            /*
            AddBalanceWSCharging addBalanceCharging = new AddBalanceWSCharging();
            
            addBalanceCharging.setSupplierName(supplierName);
            addBalanceCharging.setDateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            addBalanceCharging.setContractNumber(contractNumber);
            addBalanceCharging.setMdsId(mdsId);
            addBalanceCharging.setAccountId(contractNumber);
            addBalanceCharging.setAmount(totalAmount);
            addBalanceCharging.setPowerLimit(contractDemand);
            addBalanceCharging.setTariffCode(tariffCode);

            addBalanceWSChargingDao.add(addBalanceCharging);
            log.info("addBalanceWS insert is completed");
            */
            Meter meter = meterDao.get(mdsId);
            
            Contract contract = null; 
            Map<String, Object> conditionMap = new HashMap<String, Object>();
            
            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);
            
            // select Contract
            contract = contractDao.get(contractId);

            if (contract == null) {
                transactionManager.commit(txStatus);
                result.put("result", "fail : Invalid contract Info");
                return result;
            }
            Operator updateOperator = operatorDao.get(operatorId);
            Double currentDeposit = updateOperator.getDeposit();

            if ( isVendor && (currentDeposit == null || currentDeposit < amount) ) {
                // 잔고 부족 
                transactionManager.rollback(txStatus);
                result.put("result", "fail : Insufficient Quota Balance");
                return result;
            }

            String lastChargeDate = StringUtil.nullToBlank(contract.getLastTokenDate());
            Double preCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
            Double currentCredit = new BigDecimal(StringUtil.nullToZero(contract.getCurrentCredit())).add(new BigDecimal(amount)).doubleValue();
            Double preArrears = StringUtil.nullToDoubleZero(contract.getCurrentArrears());
            Double preArrears2 = StringUtil.nullToDoubleZero(contract.getCurrentArrears2());
            Double currnetArrears = new BigDecimal(StringUtil.nullToDoubleZero(contract.getCurrentArrears())).subtract(new BigDecimal(arrears)).doubleValue();
            Double currnetArrears2 = new BigDecimal(StringUtil.nullToDoubleZero(contract.getCurrentArrears2())).subtract(new BigDecimal(arrears2)).doubleValue();

            //지수부 표현으로 화면표시 방지
            NumberFormat f= NumberFormat.getInstance();
            f.setGroupingUsed(true);
            String val = f.format(currnetArrears);
            currnetArrears = Double.parseDouble(val.replace(",", ""));
            String val2 = f.format(currnetArrears2);
            currnetArrears2 = Double.parseDouble(val2.replace(",", ""));

            Boolean isCutOff = false;    // 차단여부

            if(meter != null && meter.getModel() != null && currentCredit > 0d && meter.getMeterStatusCodeId() != null) {
                // CommandOperationUtil 생성 후 개발 부분 Start
                Code code = codeDao.get(meter.getMeterStatusCodeId());
                 isCutOff = code.getCode().equals("1.3.3.4");
            }
            // CommandOperationUtil 생성 후 개발 부분 End
            
            Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;
            Code keyCode = null;
            Operator operator = operatorDao.get(operatorId);
            // insert ContractChangeLog
            addContractChangeLog(contract, operator, "lastTokenDate", contract.getLastTokenDate(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            addContractChangeLog(contract, operator, "chargedCredit", contract.getChargedCredit(), amount);
            addContractChangeLog(contract, operator, "currentCredit", contract.getCurrentCredit(), currentCredit.toString());
            addContractChangeLog(contract, operator, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString());

            // chargeCredit 안에 포함 init Credit이 포함되어 결제 되므로 contract에서는 contract Price를 null로 바꾼다.
            if(contractPrice != null && contractPrice != 0d) {
                contract.setContractPrice(null);
            }
            
            contract.setLastTokenDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            contract.setChargedCredit(amount);
            contract.setCurrentCredit(currentCredit);
            contract.setCurrentArrears(currnetArrears);
            contract.setCurrentArrears2(currnetArrears2);
            contract.setTotalAmountPaid(totalAmountPaid);
            contract.setLastChargeCnt(lastChargeCnt);
            
            Integer tempPaymentCount = null;
            Integer tempContractCount = null;
            Double tempFirstArrears = null;
            Integer preArrearsPaymentCount = null;
            if(isPartpayment && preArrears > 0) {
                preArrearsPaymentCount = contract.getArrearsPaymentCount();
                //charge하는 경우 타는 소스이므로 첫 charge시에는 arrearsPaymentCount가 1이되어야 한다.
                tempPaymentCount = preArrearsPaymentCount == null ? 1 : preArrearsPaymentCount + 1;
                tempContractCount = contract.getArrearsContractCount();
                tempFirstArrears = contract.getFirstArrears();
                contract.setArrearsPaymentCount(tempPaymentCount);
                if(partpayReset) {
                    //분할납부금액 초기화
                    contract.setFirstArrears(currnetArrears);
                    contract.setArrearsContractCount(contract.getArrearsContractCount()-contract.getArrearsPaymentCount());
                    contract.setArrearsPaymentCount(0);
                    
                    addContractChangeLog(contract, operator, "firstArrears", tempFirstArrears, contract.getFirstArrears());
                    addContractChangeLog(contract, operator, "arrearsContractCount", tempContractCount, contract.getArrearsContractCount());
                }
            }
            
            //미수금을 모두 갚았을 경우 혹은 미수금이 없을 경우
            if(isPartpayment) {
                if((currnetArrears <= 0 || currnetArrears == null) || tempContractCount == null) {
                    contract.setArrearsPaymentCount(null);
                    addContractChangeLog(contract, operator, "arrearsPaymentCount", preArrearsPaymentCount, contract.getArrearsPaymentCount());
                } else {
                    addContractChangeLog(contract, operator, "arrearsPaymentCount", preArrearsPaymentCount, contract.getArrearsPaymentCount());
                }
            }
            
//          if(isPartpayment && (currnetArrears <= 0 || currnetArrears == null) || tempContractCount == null) {
//              contract.setArrearsPaymentCount(null);
//              addContractChangeLog(contract, operator, "arrearsPaymentCount", tempPaymentCount, contract.getArrearsPaymentCount());
//          }
            
            contract.setCashPoint(operator.getCashPoint());

            // insert PrepaymentLog
            PrepaymentLog prepaymentLog = new PrepaymentLog();
            if((isPartpayment && preArrears > 0) && tempContractCount != null && tempPaymentCount != null) {
                prepaymentLog.setPartpayInfo(tempPaymentCount+"/"+tempContractCount);
            }

            // 미터가 교체되고 처음 결제 되는 경우 로그에 미터 교체 비용관련 항목이 추가된다.
            if(contractPrice != null && contractPrice != 0d) {
                prepaymentLog.setInitCredit(contractPrice);
            }
            
            Integer daysFromCharge;
            if(lastChargeDate != null  && !lastChargeDate.equals("") ) {
                daysFromCharge = TimeUtil.getDayDuration(lastChargeDate, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            } else {
                daysFromCharge = 0;
            }

            VendorCasher vendorCasher = vendorCasherDao.getByVendorCasherId(casherId, operator);

            prepaymentLog.setVendorCasher(vendorCasher);
            prepaymentLog.setDaysFromCharge(daysFromCharge);
            prepaymentLog.setCustomer(contract.getCustomer());
            prepaymentLog.setContract(contract);
            prepaymentLog.setKeyType(keyCode);
            prepaymentLog.setChargedCredit(amount);
            prepaymentLog.setChargedArrears(arrears);
            prepaymentLog.setChargedArrears2(arrears2);
            prepaymentLog.setLastTokenDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
//            prepaymentLog.setLastTokenId(""+DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            prepaymentLog.setOperator(operator);
            Integer emergencyYn = null;
            if (contract.getEmergencyCreditAvailable() != null) {
                emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
            }
            prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
            prepaymentLog.setPowerLimit(contractDemand);
            prepaymentLog.setPreBalance(preCredit);
            prepaymentLog.setBalance(currentCredit);
            prepaymentLog.setTotalAmountPaid(totalAmountPaid);
            prepaymentLog.setVat(vat);
            prepaymentLog.setPreArrears(preArrears);
            prepaymentLog.setPreArrears2(preArrears2);
            prepaymentLog.setArrears(currnetArrears);
            prepaymentLog.setArrears2(currnetArrears2);
            prepaymentLog.setLocation(contract.getLocation());
            prepaymentLog.setTariffIndex(contract.getTariffIndex());
            prepaymentLog.setPayType(codeDao.get(payTypeId));
            prepaymentLogDao.add(prepaymentLog);
            
            
            log.info("prepaymentLog has been added");
            
            DepositHistory dh = new DepositHistory();
            dh.setOperator(updateOperator);
            dh.setContract(contract);
            dh.setCustomer(contract.getCustomer());
            dh.setMeter(meter);
            dh.setChangeDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
            dh.setChargeCredit(amount);
            dh.setDeposit(updateOperator.getDeposit());
            dh.setPrepaymentLog(prepaymentLog);
            
            depositHistoryDao.add(dh);    

            // operator update
            if ( isVendor ) {
                updateOperator.setDeposit(currentDeposit - amount);
            }
            operatorDao.update(updateOperator);
            log.info("operator update is completed");

            // update Contract
            contractDao.update(contract);
            
            log.info("contractInfo has been updated");

            log.info("depositHistory has been added");
            
            transactionManager.commit(txStatus);
            
            //SNS 전송
            //this.SMSNotification(contract, amount, preCredit, isCutOff);

            rtnStr = "success";
            result.put("result", rtnStr);
            result.put("prepaymentLogId", prepaymentLog.getId());
            result.put("isCutOff", isCutOff);
            result.put("credit", currentCredit);
            return result;
        } catch (Exception e) {
            log.error(e, e); 
            
            if (txStatus != null && !txStatus.isCompleted()) {
                transactionManager.rollback(txStatus);
            }

            e.printStackTrace();
            rtnStr = "fail";
            
            result.put("result", rtnStr);
            return result;
        } 
    }

    /**
     * method name : vendorBalanceChargingSPASA<b/>
     * method Desc :
     *
     * @param condition
     * @return
     */
    private Map<String, Object> vendorBalanceChargingSPASA(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        Boolean isVendor = (Boolean)condition.get("isVendor");
        String supplierName = StringUtil.nullToBlank(condition.get("supplierName"));
        String casherId = StringUtil.nullToBlank(condition.get("casherId"));
        String dateTime = StringUtil.nullToBlank(condition.get("dateTime"));
        Integer contractId = (Integer)condition.get("contractId");
        String contractNumber = StringUtil.nullToBlank(condition.get("contractNumber"));
        String mdsId = StringUtil.nullToBlank(condition.get("mdsId"));
        String lastTokenId = StringUtil.nullToBlank(condition.get("lastTokenId"));
        String authCode = StringUtil.nullToBlank(condition.get("authCode"));
        Double amount = (Double)condition.get("amount");
        Double arrears = (Double)condition.get("arrears");
        Double totalAmount = StringUtil.nullToDoubleZero(amount) + StringUtil.nullToDoubleZero(arrears);
        Double contractDemand = (Double)condition.get("contractDemand");
        Integer tariffCode = (Integer)condition.get("tariffCode");
        Integer operatorId = (Integer)condition.get("operatorId");
        Double contractPrice = (Double)condition.get("contractPrice");
        String rtnStr = "";

        TransactionStatus txStatus = null;
        log.info(condition);

        try {
            txStatus = transactionManager.getTransaction(null);

            // 웹서비스로그 저장
            /*
            AddBalanceWSCharging addBalanceCharging = new AddBalanceWSCharging();

            addBalanceCharging.setSupplierName(supplierName);
            addBalanceCharging.setDateTime(dateTime);
            addBalanceCharging.setContractNumber(contractNumber);
            addBalanceCharging.setMdsId(mdsId);
            addBalanceCharging.setAccountId(contractNumber);
            addBalanceCharging.setAmount(totalAmount);
            addBalanceCharging.setPowerLimit(contractDemand);
            addBalanceCharging.setTariffCode(tariffCode);

            addBalanceWSChargingDao.add(addBalanceCharging);
            log.info("addBalanceWS insert is completed");
             */
            Meter meter = meterDao.get(mdsId);

            Contract contract = null;
            Map<String, Object> conditionMap = new HashMap<String, Object>();

            conditionMap.put("contractNumber", contractNumber);
            conditionMap.put("supplierName", supplierName);
            conditionMap.put("mdsId", mdsId);

            // select Contract
            contract = contractDao.get(contractId);

            if (contract == null) {
                transactionManager.commit(txStatus);
                result.put("result", "fail : Invalid contract Info");
                return result;
            }
            Operator updateOperator = operatorDao.get(operatorId);
            Double currentDeposit = updateOperator.getDeposit();

            if (isVendor && (currentDeposit == null || currentDeposit < totalAmount)) {
                // 잔고 부족
                transactionManager.rollback(txStatus);
                result.put("result", "fail : Insufficient Quota Balance");
                return result;
            } else {
                // operator update
                if (isVendor) {
                    updateOperator.setDeposit(currentDeposit - totalAmount);
                }
                operatorDao.update(updateOperator);
                log.info("operator update is completed");
            }

            String lastChargeDate = StringUtil.nullToBlank(contract.getLastTokenDate());
            Double preCredit = StringUtil.nullToDoubleZero(contract.getCurrentCredit());
            Double currentCredit = new BigDecimal(StringUtil.nullToZero(contract.getCurrentCredit())).add(
                    new BigDecimal(amount)).doubleValue();
            Double preArrears = StringUtil.nullToDoubleZero(contract.getCurrentArrears());
            Double currnetArrears = new BigDecimal(StringUtil.nullToDoubleZero(contract.getCurrentArrears())).subtract(
                    new BigDecimal(arrears)).doubleValue();
            Boolean isCutOff = false; // 차단여부

            if (meter != null && meter.getModel() != null && currentCredit > 0d && meter.getMeterStatusCodeId() != null) {
                // CommandOperationUtil 생성 후 개발 부분 Start
                Code code = codeDao.get(meter.getMeterStatusCodeId());
                isCutOff = code.getCode().equals("1.3.3.4");
            }
            // CommandOperationUtil 생성 후 개발 부분 End

            Integer lastChargeCnt = new Integer(StringUtil.nullToZero(contract.getLastChargeCnt())) + 1;
            Code keyCode = null;
            Operator operator = operatorDao.get(operatorId);
            // insert ContractChangeLog
            addContractChangeLog(contract, operator, "lastTokenDate", contract.getLastTokenDate(), dateTime);
            addContractChangeLog(contract, operator, "chargedCredit", contract.getChargedCredit(), totalAmount);
            addContractChangeLog(contract, operator, "currentCredit", contract.getCurrentCredit(), currentCredit.toString());
            addContractChangeLog(contract, operator, "lastChargeCnt", contract.getLastChargeCnt(), lastChargeCnt.toString());

            // chargeCredit 안에 포함 init Credit이 포함되어 결제 되므로 contract에서는 contract Price를 null로 바꾼다.
            if (contractPrice != null && contractPrice != 0d) {
                contract.setContractPrice(null);
            }

            contract.setLastTokenDate(dateTime);
            contract.setChargedCredit(totalAmount);
            contract.setCurrentCredit(currentCredit);
            contract.setCurrentArrears(currnetArrears);
            contract.setLastChargeCnt(lastChargeCnt);

            // update Contract
            contractDao.update(contract);

            log.info("contractInfo has been updated");

            // insert PrepaymentLog
            PrepaymentLog prepaymentLog = new PrepaymentLog();

            // 미터가 교체되고 처음 결제 되는 경우 로그에 미터 교체 비용관련 항목이 추가된다.
            if (contractPrice != null && contractPrice != 0d) {
                prepaymentLog.setInitCredit(contractPrice);
            }

            Integer daysFromCharge;
            if (lastChargeDate != null && !lastChargeDate.equals("")) {
                daysFromCharge = TimeUtil.getDayDuration(lastChargeDate, dateTime);
            } else {
                daysFromCharge = 0;
            }

            VendorCasher vendorCasher = vendorCasherDao.getByVendorCasherId(casherId, operator);

            prepaymentLog.setVendorCasher(vendorCasher);
            prepaymentLog.setDaysFromCharge(daysFromCharge);
            prepaymentLog.setCustomer(contract.getCustomer());
            prepaymentLog.setContract(contract);
            prepaymentLog.setKeyType(keyCode);
            prepaymentLog.setChargedCredit(amount);
            prepaymentLog.setChargedArrears(arrears);
            prepaymentLog.setLastTokenDate(dateTime);
            prepaymentLog.setOperator(operator);
            
            // Unique한 Transaction No.(=prepaymentlog 테이블의 ID로 설정한다. by eunmiae 03 Feb 2015
            String trNo = String.valueOf(prepaymentLogDao.getNextVal());
            prepaymentLog.setLastTokenId(trNo);
            log.info("Transanction Number : " + trNo);

            
            Integer emergencyYn = null;
            if (contract.getEmergencyCreditAvailable() != null) {
                emergencyYn = (contract.getEmergencyCreditAvailable()) ? 1 : 0;
            }
            prepaymentLog.setEmergencyCreditAvailable(emergencyYn);
            prepaymentLog.setPowerLimit(contractDemand);
            prepaymentLog.setPreBalance(preCredit);
            prepaymentLog.setBalance(currentCredit);
            prepaymentLog.setPreArrears(preArrears);
            prepaymentLog.setArrears(currnetArrears);
            prepaymentLog.setAuthCode(authCode);
            prepaymentLog.setLocation(contract.getLocation());
            prepaymentLog.setTariffIndex(contract.getTariffIndex());
            prepaymentLogDao.add(prepaymentLog);

            log.info("prepaymentLog has been added");

            DepositHistory dh = new DepositHistory();
            dh.setOperator(updateOperator);
            dh.setContract(contract);
            dh.setCustomer(contract.getCustomer());
            dh.setMeter(meter);
            dh.setChangeDate(dateTime);
            dh.setChargeCredit(totalAmount);
            dh.setDeposit(updateOperator.getDeposit());
            dh.setPrepaymentLog(prepaymentLog);

            depositHistoryDao.add(dh);
            // SNS 전송
            this.SMSNotification(contract, amount, preCredit, isCutOff);

            log.info("depositHistory has been added");

            transactionManager.commit(txStatus);
            rtnStr = "success";
            result.put("result", rtnStr);
            result.put("prepaymentLogId", prepaymentLog.getId());
            result.put("isCutOff", isCutOff);
            result.put("credit", currentCredit);
            return result;
        } catch(Exception e) {
            log.error(e, e);

            if (transactionManager != null) {
                transactionManager.rollback(txStatus);
            }

            e.printStackTrace();
            rtnStr = "fail";

            result.put("result", rtnStr);
            return result;
        }
    }

    /**
     * method name : getPrepaymentChargeReceiptData<b/>
     * method Desc : Prepayment Charge 영수증 화면에서 충전정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
    public Map<String, Object> getPrepaymentChargeReceiptData(Map<String, Object> conditionMap) {
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        Integer contractId = (Integer)conditionMap.get("contractId");
        Long prepaymentLogId = (Long)conditionMap.get("prepaymentLogId");
        Map<String, Object> result = new HashMap<String, Object>();
        
        Contract contract = contractDao.get(contractId);
        
        PrepaymentLog prepaymentLog = prepaymentLogDao.get(prepaymentLogId);

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());

        // TODO - 영수증 출력포멧이 정해지면 그에 맞춰서 가져오는 값을 지정해야 함.
        String accountId = contract.getLastTokenId();
        String mdsId = (contract.getMeter() != null) ? contract.getMeter().getMdsId() : "";
        String contractNumber = contract.getContractNumber();
        Double chargedCredit = prepaymentLog.getChargedCredit();
        String lastTokenDate = contract.getLastTokenDate();
        // sample
        String billDate = null;
        String billMonth = null;

        if (contract.getBillDate() != null) {
            try {
                String currentDate = TimeUtil.getCurrentDay();
                billDate = currentDate.substring(0,6) + StringUtil.frontAppendNStr('0', contract.getBillDate().toString(), 2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Locale locale = new Locale(lang, country);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", locale);
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMM yyyy", locale);

            try {
                billMonth = sdf2.format(sdf.parse(billDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        // samples
        result.put("accountId", (accountId != null) ? accountId : "");
        result.put("mdsId", (mdsId != null) ? mdsId : "");
        result.put("contractNumber", (contractNumber != null) ? contractNumber : "");
        result.put("chargedCredit", (chargedCredit != null) ? cdf.format(chargedCredit) : cdf.format(0d));
        result.put("lastTokenDate", (lastTokenDate != null) ? TimeLocaleUtil.getLocaleDate(lastTokenDate, lang, country) : "");
        result.put("billDate", (billDate != null) ? TimeLocaleUtil.getLocaleDate(billDate, lang, country) : "");
        result.put("billMonth", (billMonth != null) ? billMonth : "");

        return result;
    }

    /**
     * @MethodName getVendorCustomerReceiptData
     * @Date 2013. 7. 19.
     * @param condition
     * @return
     * @Modified
     * @Description : vendor <-> customer 사이에서 발생하는 영수증에 대한 정보를 조회한다.  
     */    
    @Override
    public Map<String, Object> getVendorCustomerReceiptData(Map<String, Object> condition) {
        Map<String, Object> result = new HashMap<String, Object>();
        Integer supplierId = (Integer)condition.get("supplierId");
        Integer contractId = (Integer)condition.get("contractId");
        Long prepaymentLogId = (Long)condition.get("prepaymentLogId");
        
        PrepaymentLog prepaymentLog = prepaymentLogDao.get(prepaymentLogId);
        Integer operatorId = prepaymentLog.getOperatorId();
        Operator operator = null;
        if (operatorId != null) {
            operator = operatorDao.get(operatorId); 
        }        

        // 결제 관련 임의로 계정을 admin으로 지정한다.
        if (operator == null) {
            operator = operatorDao.getOperatorByLoginId("admin");
        }
        Supplier supplier = supplierDao.get(supplierId);
        Contract contract = contractDao.get(contractId);
        
        Double totalAmountPaid = StringUtil.nullToDoubleZero(prepaymentLog.getTotalAmountPaid());
        Double chargedCredit = StringUtil.nullToDoubleZero(prepaymentLog.getChargedCredit());
        Double chargedArrears = StringUtil.nullToDoubleZero(prepaymentLog.getChargedArrears());
        Double chargedArrears2 = StringUtil.nullToDoubleZero(prepaymentLog.getChargedArrears2());
        Double preArrears = StringUtil.nullToDoubleZero(prepaymentLog.getPreArrears());
        Double preArrears2 = StringUtil.nullToDoubleZero(prepaymentLog.getPreArrears2());
        Double arrears = StringUtil.nullToDoubleZero(prepaymentLog.getArrears());
        Double arrears2 = StringUtil.nullToDoubleZero(prepaymentLog.getArrears2());
        Double vat = StringUtil.nullToDoubleZero(prepaymentLog.getVat());
        Integer daysFromCharge = prepaymentLog.getDaysFromCharge();
        
        BigDecimal bdChargedCredit = new BigDecimal(chargedCredit);
//        Double totalAmount = ((chargedCredit == null) ? 0D : chargedCredit) + ((chargedArrears == null) ? 0D : chargedArrears);
//        Double totalAmount = bdChargedCredit.add(bdChargedArrears).doubleValue();
        VendorCasher casher = prepaymentLog.getVendorCasher();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());
        
        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();
        String date = "";
        String dateByYyyymmdd = "";
        
        String vendorDistinct = "";
        String customerName = "";
        String customerNumber = "";
        String address = "";
        String customerAddr = "";
        String meterId = "";
        String gs1 = "";
        String lastMeterId = "";
        String district = "";
        String tarrif = "";
        String casherId = "";
        String casherName = "";
        String lastTokenId = "";    
        
        if (operator.getLocation() != null) {
            vendorDistinct = operator.getLocation().getName();
        }
        
        if (prepaymentLog != null && prepaymentLog.getCustomer() != null) {
            Customer customer = prepaymentLog.getCustomer();
            customerName = customer.getName();
            customerNumber = customer.getCustomerNo();
            date = TimeLocaleUtil.getLocaleDate(prepaymentLog.getLastTokenDate(), lang, country);
            dateByYyyymmdd = TimeLocaleUtil.getLocaleDate(prepaymentLog.getLastTokenDate().substring(0,8), lang, country);
            lastTokenId = prepaymentLog.getLastTokenId();
        }
        
        if (contract != null && contract.getMeter() != null) {
            meterId = contract.getMeter().getMdsId();
            gs1 = contract.getMeter().getGs1();
            lastMeterId = contract.getMeter().getInstallProperty();
            customerName = contract.getCustomer().getName();
        }
        
        if (contract != null && contract.getTariffIndex() != null) {
            TariffType tarrifType = tariffTypeDao.get(contract.getTariffIndexId());
            if (tarrifType != null) {
                tarrif = tarrifType.getName();
            }
        }
        
        if (casher != null) {
            casherId = casher.getCasherId();
            casherName = casher.getName();
        }
        
        if (contract != null && contract.getLocation() != null) {
            address = contract.getLocation().getName();

            Location tempLoc = contract.getLocation();
            Location districLoc = null;
            List<Location> tempLocList = new ArrayList<Location>();
            
            for (int i = 0; i < 20; i++) {
                if (tempLoc.getParent() != null) {
                    tempLocList.add(tempLoc.getParent());
                    tempLoc = tempLoc.getParent();
                } else {
                    break;
                }
            }

            if (tempLocList.size() > 2) {
                districLoc = tempLocList.get(tempLocList.size()-3);
            } else {
                districLoc = contract.getLocation();
            }
 
            district = districLoc.getName();
        }
        
        if(contract != null && contract.getCustomer() != null) {
            customerAddr = contract.getCustomer().getAddress();
        }

        // 일반 영수증의 경우
        result.put("daysFromCharge", daysFromCharge);
        result.put("vendorName", operator.getName());
        result.put("vendorLocation", vendorDistinct);
        result.put("logId", prepaymentLogId);
        result.put("date", date);
        result.put("dateByYyyymmdd", dateByYyyymmdd);
        result.put("customer", customerName);
        result.put("customerNumber", customerNumber);
        result.put("meter", meterId);
        result.put("gs1", gs1);
        result.put("lastTokenId", lastTokenId);
        result.put("vat", cdf.format(vat));
        result.put("contractNumber", contract.getContractNumber());
        result.put("activity", tarrif);
        result.put("distinct", district);
        result.put("address", address);
        result.put("customerAddr", customerAddr);
        result.put("amount", cdf.format(chargedCredit));
        result.put("totalAmountPaid", cdf.format(totalAmountPaid));
        result.put("casherId", casherId);
        result.put("casherName", casherName);
        result.put("currentBalance", cdf.format(prepaymentLog.getBalance()));
        result.put("preBalance", prepaymentLog.getPreBalance());
        result.put("payType", prepaymentLog.getPayType() != null ? prepaymentLog.getPayType().getName() : "Cash"); // default : cash

        // 결제 영수증 미수금이 있는 경우 
        if ((prepaymentLog.getPreArrears() != null && prepaymentLog.getPreArrears() != 0d) 
        	|| (prepaymentLog.getPreArrears2() != null && prepaymentLog.getPreArrears2() != 0d)) {
            result.put("arrears", chargedArrears);
            result.put("arrears2", chargedArrears2);
            result.put("currentArrears", arrears);
            result.put("currentArrears2", arrears2);
            result.put("preArrears", preArrears);
            result.put("preArrears2", preArrears2);
            
            
            result.put("lastMeter", lastMeterId);
            
            if(prepaymentLog.getInitCredit() != null && prepaymentLog.getInitCredit() != 0d) {
                Double initCredit = prepaymentLog.getInitCredit();
                bdChargedCredit = bdChargedCredit.subtract(new BigDecimal(initCredit));
                result.put("amount", bdChargedCredit.doubleValue());
                result.put("initCredit", initCredit);
            }
        }

        condition.put("lastTokenDate", prepaymentLog.getLastTokenDate());

        return result; 
    }

    private String feeValueformat(DecimalFormat cdf, Double value) {
        String returnValue = "";
        Double valueD = StringUtil.nullToDoubleZero(value);
        if(valueD > 0) {
            returnValue = "("+cdf.format(valueD)+")";
        } else {
//          returnValue = String.valueOf(cdf.format(Math.abs(valueD)));
            returnValue = String.valueOf(cdf.format(valueD));
        }
        return returnValue;
    }
    
    /**
     * @MethodName reoperateMeter
     * @Date 2013. 8. 5.
     * @param meter
     * @param contract
     * @param supplierName
     * @param amount
     * @return
     * @Modified
     * @Description 요금 납부와 관련하여 미터기를 재동작하도록 수행
     */
    @SuppressWarnings("unused")
    @Deprecated
    private Map<String, Object> reoperateMeter(Meter meter, Contract contract, String supplierName, Double amount) {
        Map<String, Object> retMap = new HashMap<String, Object> ();
        String rtnStr = null;
        boolean isCutOff = false;
        CommandGW gw = new CommandGW();
        String mdsId = meter.getMdsId();
        
        if (meter.getPrepaymentMeter() != null && meter.getPrepaymentMeter()) { // Prepayment Meter 인 경우 온라인 충전
            
        } else { // Soft Credit 인 경우 충전
        ResultStatus status = ResultStatus.FAIL;
        String modelName = meter.getModel().getName();

        // 차단되어 있는지 체크 
        // 1.3.3.4 = CutOff
        if (meter.getMeterStatus() != null &&
                meter.getMeterStatus().getCode().equals("1.3.3.4")) {
            isCutOff = true;
        }

        if (isCutOff) {
            try {
                status = ResultStatus.SUCCESS;
                
                Map<String, Object> resultMap = 
                        cmdOperationUtil.relayValveOn(meter.getModem().getMcu().getSysID(), meter.getMdsId());
                
                Object[] values = resultMap.values().toArray(new Object[0]);
                
                for (Object o : values) {
                    rtnStr += (String)o + " ";
                    
                    if (((String)o).contains("failReason")) {
                        status = ResultStatus.FAIL;
                        break;
                    }
                }
            } catch (Exception e) {
                rtnStr = e.getMessage();
            }
            if(status == ResultStatus.SUCCESS) {
                this.SMSNotification(contract, amount, StringUtil.nullToDoubleZero(contract.getCurrentCredit()), isCutOff);
            }
            
            Code operationCode = codeDao.getCodeIdByCodeObject("8.1.4");
            Supplier supplier = supplierDao.getSupplierByName(supplierName);

            if (operationCode != null) {
                String currDateTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                OperationLog log = new OperationLog();

                log.setOperatorType(1);//operator
                log.setOperationCommandCode(operationCode);
                log.setYyyymmdd(currDateTime.substring(0,8));
                log.setHhmmss(currDateTime.substring(8,14));
                log.setYyyymmddhhmmss(currDateTime);
                log.setDescription("");
                log.setErrorReason(status.name());
                log.setResultSrc("");
                log.setStatus(status.getCode());
                log.setTargetName(mdsId);
                log.setTargetTypeCode(meter.getMeterType());
                //log.setUserId(CommandOperator.WEBSERVICE.getOperatorName());
                log.setSupplier(supplier);
                operationLogDao.add(log);
            }
        }
    }
        retMap.put("rtnStr", rtnStr);
        retMap.put("isCutOff", isCutOff);
        return retMap;
    }
    
    @Transactional
    public String vendorSetContractPrice(Map<String, Object> condition) {
        String result = "";
        try {
            String contractNumber = StringUtil.nullToBlank(condition.get("contractNumber"));
            Double contractPrice = StringUtil.nullToDoubleZero((Double) condition.get("contractPrice"));
            Contract contract = contractDao.findByCondition("contractNumber", contractNumber);
            contract.setContractPrice(contractPrice);
            contractDao.update(contract);
            result = "success";
        } catch (Exception e) {
            result= "fail";
            e.printStackTrace();
        }
        return result;
    }
    
    @Transactional
    public String updateBarcode(Map<String, Object> condition) {
        String result = "fail: ";
        Integer contractId = (Integer) condition.get("contractId");
        String barcode = StringUtil.nullToBlank(condition.get("barcode"));
        
        try {
            Contract contract = contractDao.get(contractId);
            if (contract != null && !barcode.equals("")) {
                contract.setBarcode(barcode);
                contractDao.update(contract);
                result = "success";             
            } else if ( barcode.equals("") ) {
                result += "Input barcode value to update ";
            } else {
                result += "It's invalid contract";
            }
        } catch (Exception e) {
            result = "fail";
            e.printStackTrace();
        }
        return result;
    }
    
    @Transactional
    public String addCasher(Map<String, Object> condition) {
        String result = "";
        String vend = StringUtil.nullToBlank(condition.get("vendor"));
        Operator vendor = operatorDao.getOperatorByLoginId(vend);
        
        String casherId = StringUtil.nullToBlank(condition.get("id"));
        String name = StringUtil.nullToBlank(condition.get("name"));
        String password = StringUtil.nullToBlank(condition.get("password"));
        Boolean isManager = (Boolean) condition.get("isManager");
        String lastUpdateDate = StringUtil.nullToBlank(condition.get("lastUpdateDate"));
        
        try {
            VendorCasher casher =  new VendorCasher();
            
            casher.setCasherId(casherId);
            casher.setName(name);
            casher.setPassword(password);
            casher.setStatus(CasherStatus.WORK.getCode());
            casher.setIsManager(isManager);
            casher.setLastUpdateDate(lastUpdateDate);
            casher.setIsFirst(new Boolean(true));
            if ( vendor != null ) {
                casher.setVendor(vendor);
            }
            vendorCasherDao.add(casher);
            
        result = "success";
        } catch (Exception e) {
            result += "can't add casher";
            e.printStackTrace();
        }
        return result;
    }
    
    @Transactional
    public String deleteCasher(Map<String, Object> condition) {
        String result = "failed:";
        
        try {
            Integer id = (Integer) condition.get("id");
            String date = StringUtil.nullToBlank(condition.get("date"));
            
            VendorCasher casher = vendorCasherDao.get(id);
            casher.setLastUpdateDate(date);
            casher.setStatus(CasherStatus.QUIT.getCode());
            
            vendorCasherDao.update(casher);
            result = "success";
        } catch (Exception e) {
            result = "can't delete casher";
            e.printStackTrace();
        }
        return result; 
    }

    @Transactional(rollbackFor=Exception.class)
    public String cancelTransaction(Long id, String operatorId, String reason) {
        String rtn = "success";
        Contract contract = null;
        Double totalAmount = null;
        Properties prop = new Properties();
        
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
            
            String isPartpaymentStr = prop.getProperty("partpayment.use");
            Boolean isPartpayment = (isPartpaymentStr == null || "".equals(isPartpaymentStr)) ? false : Boolean.parseBoolean(isPartpaymentStr);
            
            PrepaymentLog prepayLog = prepaymentLogDao.get(id);
            Boolean isCancelled = prepayLog.getIsCanceled() == null ? false : prepayLog.getIsCanceled();
            //Cancel된 내역인지 다시 한번 확인
            if(!isCancelled) {
                prepayLog.setIsCanceled(true);
                prepayLog.setCancelDate(TimeUtil.getCurrentTime());
                if(!reason.isEmpty()) {
                    prepayLog.setCancelReason(reason);
                }
                prepaymentLogDao.update(prepayLog);
                Operator operator = operatorDao.getOperatorByLoginId(operatorId);
                Operator commitedVendor = prepayLog.getOperator();
    
                Integer contractId = prepayLog.getContractId();
                contract = contractDao.get(contractId);
    
                Double arrears = (Double) ObjectUtils.defaultIfNull(contract.getCurrentArrears() + prepayLog.getChargedArrears(), null);
                Double arrears2 = (Double) ObjectUtils.defaultIfNull(contract.getCurrentArrears2() + prepayLog.getChargedArrears2(), null);
                Double balance = (Double) ObjectUtils.defaultIfNull(contract.getCurrentCredit() - prepayLog.getChargedCredit(), null);
                Double total = StringUtil.nullToDoubleZero(balance) + StringUtil.nullToDoubleZero(arrears);
//                totalAmount = StringUtil.nullToDoubleZero(prepayLog.getChargedArrears()) + StringUtil.nullToDoubleZero(prepayLog.getChargedCredit()); 
                totalAmount = (Double) ObjectUtils.defaultIfNull(contract.getTotalAmountPaid(), null);
                        
                if ( commitedVendor.getRole().getName().equals("vendor")) {
                    commitedVendor = prepayLog.getOperator();
                    commitedVendor.setDeposit(commitedVendor.getDeposit() + totalAmount );      
                    operatorDao.update(commitedVendor);
                }
    
                Double preArrears = contract.getCurrentArrears();
                Double preArrears2 = contract.getCurrentArrears2();
                Double preBalance = contract.getCurrentCredit();
                Double preChargedCredit = contract.getChargedCredit();
                
                addContractChangeLog(contract, operator, "currentCredit", preBalance, balance);
                addContractChangeLog(contract, operator, "currentArrears", preArrears, arrears);
                addContractChangeLog(contract, operator, "currentArrears2", preArrears2, arrears2);
                addContractChangeLog(contract, operator, "chargedCredit", preChargedCredit, -totalAmount);
                
                //분할납부사용중이면서 해당 로그에 arrears를 charge했던 로그를 취소하는 경우
                if(isPartpayment && prepayLog.getChargedArrears() > 0 ) {
                    //reset Logic
                    Double preFirstArrears = contract.getFirstArrears();
                    Integer preContractCount = contract.getArrearsContractCount();
                    Integer prePaymentCount = contract.getArrearsPaymentCount();
                    
                    Integer tempContractCount = preContractCount == null ? 0 : preContractCount;
                    Integer tempPaymentCount = prePaymentCount == null ? 0 : prePaymentCount;
                    Integer newContractCount = tempContractCount-tempPaymentCount+1;
                    
                    contract.setFirstArrears(arrears);
                    contract.setArrearsContractCount(newContractCount);
                    contract.setArrearsPaymentCount(0);
                    
                    addContractChangeLog(contract, operator, "firstArrears", preFirstArrears, arrears);
                    addContractChangeLog(contract, operator, "arrearsContractCount", preContractCount, newContractCount);
                    addContractChangeLog(contract, operator, "arrearsPaymentCount", prePaymentCount, 0);

                }
                
                //분할납부가 끝난고객의 경우
                contract.setCurrentCredit(balance);
                contract.setCurrentArrears(arrears);
                contract.setCurrentArrears2(arrears2);
                //수정 전 소스의 프로세스대로 수정했으나 본래 모델에 정의된 본래 사용의도와 동일하지 않음.
                contract.setChargedCredit(-totalAmount);
                contractDao.update(contract);
                
                try {
                    //충전 취소 후 SMS 전송
                    Supplier supplier = supplierDao.get(contract.getSupplierId());
                    DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
                    
                    Properties messageProp = new Properties();
                        
                    String lang = supplier.getLang().getCode_2letter();
                    InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
                    if(ip == null){
                        ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");              
                    }
                    messageProp.load(ip);
                    
                    log.info("prop load : "+prop.containsKey("smsClassPath"));
                    
                    String text =  messageProp.getProperty("aimir.alert.cancelCharge").replace("$AMOUNT", cdf.format(totalAmount)) + messageProp.getProperty("aimir.price.unit");
                    this.SMSNotificationWithText(contract, text);
                    
                } catch (Exception e) {
                    log.error(e,e);
                }
                
            } else {
                rtn = "cancelData";
            }
        } catch( Exception e ) {
            rtn = "failed";
        }
        

        return rtn;
    }

    /**
     * method name : getDepositHistoryList<b/>
     * method Desc : Vendor Prepayment Charge 가젯에서 Charge History 를 조회한다.
     *
     * @param params
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getDepositHistoryList(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();

        // Vendor Prepayment Charge 의 Charge History 탭에서 사용.
        Integer locationId = (Integer)params.get("locationId");

        List<Integer> rootLocList = locationDao.getRoot();
        int rootLocSize = rootLocList == null ? 0 : rootLocList.size();
        
        if (locationId != null && (!locationDao.isRoot(locationId) || rootLocSize > 1)) {
            List<Integer> locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            params.put("locationIdList", locationIdList);
        }
        
        Map<String, Object> historyMap = depositHistoryDao.getDepositHistoryList(params);
        List<Map<String, Object>> list = (List<Map<String, Object>>) historyMap.get("list");
        Integer count = (Integer) historyMap.get("count");
        int supplierId = (Integer) params.get("supplierId");
        Supplier supplier = supplierDao.get(supplierId);
        DecimalFormat df = new DecimalFormat("###,###,##0.00");

        if (supplier != null) {
//            df = new DecimalFormat(supplier.getCd().getPattern()); 
        }

        String country = supplier.getCountry().getCode_2letter();
        String lang = supplier.getLang().getCode_2letter();

        List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = null;

        for (Map<String, Object> hmap : list) {
            map = new HashMap<String, Object>();
            map.put("id", hmap.get("depositHistoryId"));

            if (hmap.get("historyOpId") != null) {
                map.put("vendor", hmap.get("historyOpLoginId"));
            }
            if (hmap.get("historyContractId") != null) {
                map.put("contractNo", hmap.get("historyContractNumber"));
                map.put("contractId", hmap.get("historyContractId"));
            }
            if (hmap.get("historyCustomerId") != null) {
                map.put("customerId", hmap.get("historyCustomerNo"));
                map.put("customerName", hmap.get("historyCustomerName"));
                map.put("address", hmap.get("historyCustomerAddress"));
            }
            if (hmap.get("historyMeterId") != null) {
                map.put("meter", hmap.get("historyMeterMdsId"));
            }
            if (hmap.get("changeDate") != null && !((String)hmap.get("changeDate")).equals("")) {
                map.put("changeDate", TimeLocaleUtil.getLocaleDate((String)hmap.get("changeDate"), lang, country));
            }

            Double chargeCredit = (Double)hmap.get("chargeCredit");
            Double chargeDeposit = (Double)hmap.get("netValue");

            map.put("chargeCredit", chargeCredit != null ? df.format(chargeCredit) : null);
            if(hmap.get("payType") != null){
                map.put("payType", hmap.get("payType").toString());             
            }

            map.put("chargeDeposit", chargeDeposit != null ? df.format(chargeDeposit) : null);
            if (hmap.get("prepaymentLogId") != null && hmap.get("vendorCasherId") != null) {
                map.put("casher", hmap.get("vcCasherId"));
                map.put("isCanceled", hmap.get("isCanceled"));
                map.put("cancelDate", hmap.get("cancelDate"));
            }

            map.put("prepaymentLogId", hmap.get("prepaymentLogId"));
            map.put("deposit", df.format(StringUtil.nullToDoubleZero((Double)hmap.get("deposit"))));
            mapList.add(map);
        }
        result.put("count", count);
        result.put("list", mapList);
        return result;
    }

    /**
     * 지불타입 - Code 의 codeorder 에 따라 정렬
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Map<String, Object>> vendorPrepaymentPayType() {
        List<Code> codeList = codeDao.getChildCodes("18");
        List<Map<String, Object>> codeMapList = new ArrayList<Map<String, Object>>();
        
        if(0 < codeList.size()){
            Object codeMapArray[] = new Object[codeList.size()];

            for(Code code : codeList){
                Map<String, Object> codeInfo = new HashMap<String, Object>();
                codeInfo.put("id", code.getId());
                codeInfo.put("name", code.getName());
                codeInfo.put("code", code.getCode());
                
                codeMapArray[code.getOrder()] = codeInfo;
            }           
            
            for(Object obj : codeMapArray){
                codeMapList.add((Map<String, Object>)obj);
            }
        }
        
        return codeMapList;
    }

	@Override
	public Map<String, Object> getVatByFixedVariable(String name, Integer tariffId, String applydate) {
		Map<String, Object> result = new HashMap<String, Object>();
		FixedVariable vat = fixedVariableDao.getFixedVariableDao("CHARGE_TAX", null, applydate);
		String vatAmount = vat.getAmount();
		String vatUnit = vat.getUnit();
		result.put("vatAmount", vatAmount);
		result.put("vatUnit", vatUnit);
		return result;
	}
    
}