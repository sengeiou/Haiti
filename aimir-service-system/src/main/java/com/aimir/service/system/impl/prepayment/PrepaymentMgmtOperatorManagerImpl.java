package com.aimir.service.system.impl.prepayment;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ContractStatus;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.service.system.prepayment.PrepaymentMgmtOperatorManager;
import com.aimir.util.CalendarUtil;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service(value = "prepaymentMgmtOperatorManager")
public class PrepaymentMgmtOperatorManagerImpl implements PrepaymentMgmtOperatorManager {

    protected static Log log = LogFactory.getLog(PrepaymentMgmtOperatorManagerImpl.class);

    @Autowired
    ContractDao contractDao; 

    @Autowired
    SupplierDao supplierDao; 

    @Autowired
    ContractChangeLogDao contractChangeLogDao;

    @Autowired
    OperatorDao operatorDao;

    @Autowired
    LocationDao locationDao; 

    /**
     * method name : getEmergencyCreditContractList
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getEmergencyCreditContractList(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = contractDao.getEmergencyCreditContractList(conditionMap, false);
        
        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        String currentDateTime = null;
        String limitDate = null;
        
        
        for (Map<String, Object> map : result) {
            if (map.get("lastTokenDate") == null) continue;
            map.put("lastTokenDate", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)map.get("lastTokenDate")).substring(0, 8), lang, country));
            
            if (map.get("emergencyCreditStartTime") == null || StringUtil.nullToBlank(map.get("emergencyCreditStartTime")).isEmpty()) continue;
            try {
                currentDateTime = TimeUtil.getCurrentTime();
                limitDate = CalendarUtil.getDate((String)map.get("emergencyCreditStartTime"), Calendar.DAY_OF_MONTH, (Integer)map.get("emergencyCreditMaxDuration"));
                
                map.put("limitDate", TimeLocaleUtil.getLocaleDateByMediumFormat(limitDate, lang, country));
                map.put("limitDuration", TimeUtil.getDayDuration(currentDateTime, limitDate));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * method name : getEmergencyCreditContractListTotalCount
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getEmergencyCreditContractListTotalCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = contractDao.getEmergencyCreditContractList(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : getPrepaymentContractStatusChartData
     * method Desc : 관리자 선불관리 미니가젯의 선불고객 Pie Chart Data 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getPrepaymentContractStatusChartData(Map<String, Object> conditionMap) {
        Map<String, Object> result = new HashMap<String, Object>();
        Integer supplierId = (Integer)conditionMap.get("supplierId");
        String today = null;
        try {
            today = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        conditionMap.put("today", today);
        
        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        result.put("today", TimeLocaleUtil.getLocaleDate(today.substring(0, 8), lang, country));
        
        DecimalFormat dfMd = DecimalUtil.getMDStyle(supplier.getMd());

        List<Map<String, Object>> list = contractDao.getPrepaymentContractStatusChartData(conditionMap);
        BigDecimal totalCount = new BigDecimal("0");
        Map<String, ContractStatus> constMap = new HashMap<String, ContractStatus>();

        for (ContractStatus cs : ContractStatus.values()) {
            constMap.put(cs.getCode(), cs);
        }

        Integer count = 0;
        for (Map<String, Object> map : list) {
            if (map.get("contractStatus") == null) {
                count = DecimalUtil.ConvertNumberToInteger(map.get("cnt"));
                result.put("unknown", count);
                result.put("unknownFormat", dfMd.format(count));
                totalCount = totalCount.add(new BigDecimal(count));
            } else {
                count = DecimalUtil.ConvertNumberToInteger(map.get("cnt"));
                switch(constMap.get((String)map.get("contractStatus"))) {
                    case NORMAL:
                        result.put("normal", count);
                        result.put("normalFormat", dfMd.format(count));
                        totalCount = totalCount.add(new BigDecimal(count));
                        break;
                    case SUSPENDED:
                        result.put("suspended", count);
                        result.put("suspendedFormat", dfMd.format(count));
                        totalCount = totalCount.add(new BigDecimal(count));
                        break;
                    case PAUSE:
                        result.put("pause", count);
                        result.put("pauseFormat", dfMd.format(count));
                        totalCount = totalCount.add(new BigDecimal(count));
                        break;
                    case STOP:
                        result.put("stop", count);
                        result.put("stopFormat", dfMd.format(count));
                        totalCount = totalCount.add(new BigDecimal(count));
                        break;
                    case CANCEL:
                        result.put("cancel", count);
                        result.put("cancelFormat", dfMd.format(count));
                        totalCount = totalCount.add(new BigDecimal(count));
                        break;
                }
            }

            if (!result.containsKey("normal")) {
                result.put("normal", 0);
                result.put("normalFormat", "0");
            }
            if (!result.containsKey("suspended")) {
                result.put("suspended", 0);
                result.put("suspendedFormat", "0");
            }
            if (!result.containsKey("pause")) {
                result.put("pause", 0);
                result.put("pauseFormat", "0");
            }
            if (!result.containsKey("stop")) {
                result.put("stop", 0);
                result.put("stopFormat", "0");
            }
            if (!result.containsKey("cancel")) {
                result.put("cancel", 0);
                result.put("cancelFormat", "0");
            }
            if (!result.containsKey("unknown")) {
                result.put("unknown", 0);
                result.put("unknownFormat", "0");
            }
        }
        
        result.put("totalCount", totalCount.intValue());
        result.put("totalCountFormat", dfMd.format(totalCount.intValue()));
        return result;
    }

    /**
     * method name : getPrepaymentContractList
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getPrepaymentContractList(Map<String, Object> conditionMap) {
        conditionMap.put("locationCondition", getLeafLocationIds((Integer)conditionMap.get("locationId"), (Integer)conditionMap.get("supplierId")));
        List<Map<String, Object>> result = contractDao.getPrepaymentContractList(conditionMap, false);

        Supplier supplier = supplierDao.get((Integer)conditionMap.get("supplierId"));
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
        DecimalFormat mdf = DecimalUtil.getDecimalFormat(supplier.getMd());

        String currentDateTime = null;
        String limitDate = null;

        for (Map<String, Object> map : result) {

            if (!StringUtil.nullToBlank(map.get("lastTokenDate")).isEmpty()) {
                map.put("lastTokenDate", TimeLocaleUtil.getLocaleDateByMediumFormat(((String)map.get("lastTokenDate")).substring(0, 8), lang, country));
            }

            map.put("prepaymentPowerDelay", mdf.format(StringUtil.nullToDoubleZero((Double)map.get("prepaymentPowerDelay"))));
            map.put("currentCredit", cdf.format(StringUtil.nullToDoubleZero((Double)map.get("currentCredit"))));

            // Emergency Credit Mode 인 경우 남은 유효기간을 계산
          //if (StringUtil.nullToBlank(map.get("creditTypeCode")).equals("2.2.2")) {  아래로 수정
            if (StringUtil.nullToBlank(map.get("creditTypeCode")).equals("2.2.2") && map.get("emergencyCreditStartTime") != null) {
                try {
                    currentDateTime = TimeUtil.getCurrentTime();
                    limitDate = CalendarUtil.getDate((String)map.get("emergencyCreditStartTime"), Calendar.DAY_OF_MONTH, (Integer)map.get("emergencyCreditMaxDuration"));
                    map.put("emergencyCreditMaxDate", TimeLocaleUtil.getLocaleDateByMediumFormat(limitDate, lang, country));
                    map.put("emergencyCreditMaxDuration", TimeUtil.getDayDuration(currentDateTime, limitDate));
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * method name : getPrepaymentContractListTotalCount
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트의 Total Count를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getPrepaymentContractListTotalCount(Map<String, Object> conditionMap) {
        conditionMap.put("locationCondition", getLeafLocationIds((Integer)conditionMap.get("locationId"), (Integer)conditionMap.get("supplierId")));
        List<Map<String, Object>> result = contractDao.getPrepaymentContractList(conditionMap, true);
        
        return ((Long)result.get(0).get("total")).intValue();
    }

    /**
     * method name : updateEmergencyCreditInfo
     * method Desc : 관리자 선불관리 Emergency Credit 정보를 저장한다.
     *
     * @param conditionMap
     */
    @Transactional
    public void updateEmergencyCreditInfo(Map<String, Object> conditionMap) {

        String contractNumber = (String)conditionMap.get("contractNumber");
        Boolean autoChange = (Boolean)conditionMap.get("autoChange");
        Integer duration = (Integer)conditionMap.get("duration");
        Double limitPower = (Double)conditionMap.get("limitPower");
        Integer operatorId = (Integer)conditionMap.get("operatorId");

        Contract contract = contractDao.findByCondition("contractNumber", contractNumber);
        Operator operator = operatorDao.getOperatorById(operatorId);

        // 변경값이 있을 경우 ContractChangeLog 에 insert
        if (!StringUtil.nullToBlank(contract.getEmergencyCreditAutoChange()).equals(StringUtil.nullToBlank(autoChange))) {
            addContractChangeLog(contract, operator, "emergencyCreditAutoChange", contract.getEmergencyCreditAutoChange(), autoChange);
        }
        
        if (!StringUtil.nullToZero(contract.getEmergencyCreditMaxDuration()).equals(StringUtil.nullToZero(duration))) {
            addContractChangeLog(contract, operator, "emergencyCreditMaxDuration", contract.getEmergencyCreditMaxDuration(), duration);
        }

        if (!StringUtil.nullToDoubleZero(contract.getPrepaymentPowerDelay()).equals(StringUtil.nullToDoubleZero(limitPower))) {
            addContractChangeLog(contract, operator, "prepaymentPowerDelay", contract.getPrepaymentPowerDelay(), limitPower);
        }

        contract.setEmergencyCreditAutoChange(autoChange);
        contract.setEmergencyCreditMaxDuration(duration);
        contract.setPrepaymentPowerDelay(limitPower);

        contractDao.update(contract);
    }

    /**
     * method name : getLeafLocationId
     * method Desc : 해당 지역의 하위 지역들을 구한다. 
     *
     * @param locationId
     * @param supplierId
     * @return sql 조건문에 사용하기 위해 ,로 구분된 string
     */
    private Integer[] getLeafLocationIds(Integer locationId, Integer supplierId) {
        if(StringUtil.nullToBlank(locationId).isEmpty()) return null;
        
        List<Integer> tempList = null;
        Set<Integer> locations = new HashSet<Integer>();

        tempList = locationDao.getLeafLocationId(locationId, supplierId);
        locations.addAll(tempList);

        return locations.toArray(new Integer[tempList.size()]);
    }

    /**
     * method name : addContractChangeLog
     * method Desc : ContractChangeLog 에 데이터 insert
     *
     * @param contract
     * @param operator
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
//        contractChangeLog.setDescr(descr);

        contractChangeLogDao.add(contractChangeLog);
    }
}