/**
 * ContractManagerImpl.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.service.system.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.extractor.ExcelExtractor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ContractStatus;
import com.aimir.constants.CommonConstants.RegType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.ServiceType2;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.DeviceRegistrationDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.dao.mvm.LpGMDao;
import com.aimir.dao.mvm.LpHMDao;
import com.aimir.dao.mvm.LpWMDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.ContractChangeLogDao;
import com.aimir.dao.system.ContractDao;
import com.aimir.dao.system.CustomerDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.dao.system.TariffTypeDao;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.DeviceRegLog;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.model.system.ContractChangeLog;
import com.aimir.model.system.Customer;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;
import com.aimir.service.system.ContractManager;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DecimalUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * ContractManagerImpl.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0       김상연         Contract 유무 체크
 * 2011. 4. 12.   v1.1        김상연         Contract 검색 (Contract)
 *
 */
@WebService(endpointInterface = "com.aimir.service.system.ContractManager")
@Service(value = "contractManager")
@Transactional
//@RemotingDestination
public class ContractManagerImpl implements ContractManager {

    private static Log logger = LogFactory.getLog(ContractManagerImpl.class);

    @Autowired
    ContractDao dao;

    @Autowired
    ContractDao contractDao;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    MeterDao meterDao;

    @Autowired
    TariffTypeDao tariffTypeDao;

    @Autowired
    LocationDao locationDao;

    @Autowired
    LpEMDao lpEmDao;

    @Autowired
    LpHMDao lpHmDao;

    @Autowired
    LpWMDao lpWmDao;

    @Autowired
    LpGMDao lpGmDao;

    @Autowired
    CodeDao codeDao;

    @Autowired
    SupplierDao supplierDao;

    @Autowired
    DeviceRegistrationDao deviceRegistrationDao;

    @Autowired
    ContractChangeLogDao contractChangeLogDao;

    @Autowired
    OperatorDao operatorDao;

    @SuppressWarnings("unused")
    private String ctxRoot;

    @Override
	@Deprecated
    public void addContract(Contract contract) {
        dao.add(contract);
    }

    /**
     * method name : createContract<b/>
     * method Desc : Contract 를 생성한다.
     *
     * @param contract
     */
    @Override
	public void createContract(Contract contract) {
        dao.add(contract);
    }

    @Override
	public Contract getContract(int customerId) {

        return dao.get(customerId);
    }

    @Override
	public List<Object> getMyEnergy(int customerId, int serviceTypeId) {
        return dao.getMyEnergy(customerId , serviceTypeId);
    }

    @Override
	public Contract getContractByMeterId(int meterId) {
        return dao.findByCondition("meter.id", meterId);
    }

    /**
     * method name : getContractByMeterNo<b/>
     * method Desc :
     *
     * @param meterId
     * @return
     */
    @Override
	public Contract getContractByMeterNo(String meterId) {
        Contract contract = new Contract();
        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("meter", new Object[] {"mt"}, null, Restriction.ALIAS));
        set.add(new Condition("mt.mdsId", new Object[] {meterId}, null, Restriction.EQ));
        List<Contract> contractList = dao.findByConditions(set);

        if (contractList != null && contractList.size() > 0) {
            contract = contractList.get(0);
        }

        return contract;
    }

    /**
     * @desc 전체 메터 manager fetch  method
     *
     */
    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @Deprecated
    public List<Meter> getMeterList(Map<String, String> conditionMap)
    {
        //페이징 처리를 위한 부분 추가.extjs 는 0부터 시작
        int tempPage = Integer.parseInt(conditionMap.get("page"));

        tempPage = tempPage - 1;

        //페이지 value  재설정
        conditionMap.put("page", Integer.toString(tempPage));

        List<Meter> meterList = new ArrayList();

        meterList = dao.getMeterList(conditionMap);

        return meterList;
    }

    //매터리스트 총 카운트 fetch mng
    @Override
	@Deprecated
    public String getMeterListDataCount(Map<String, String> conditionMap)
    {
        String meterListDataCount = dao.getMeterListDataCount(conditionMap);

        return meterListDataCount;
    }

    /**
     * method name : getMeterGridList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
	public List<Map<String, Object>> getMeterGridList(Map<String, Object> conditionMap) {
        return dao.getMeterGridList(conditionMap, false);
    }

    /**
     * method name : getMeterGridListDataCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
	public Integer getMeterGridListDataCount(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = dao.getMeterGridList(conditionMap, true);
        Integer count = 0;

        if (result != null && result.size() > 0) {
            if (result.get(0) != null && result.get(0).get("total") != null) {
                count = (Integer)result.get(0).get("total");
            }
        }
        return count;
    }

    @Override
	public void updateContract(Contract contract) {
        dao.update(contract);
    }


    @Override
	public int numberOverlapCheck(String contractNumber) {
        return dao.numberOverlapCheck(contractNumber);
    }

    @Override
	@Deprecated
    public void contractDelete(Integer contractId) {
        //dao.deleteById(id);
        contractChangeLogDao.contractLogDelete(contractId);

        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("contract.id", new Object[] {contractId}, null, Restriction.EQ));

        boolean flag = true;

        if(lpEmDao.findByConditions(set).size() > 0)
            flag = false;
        else if(lpGmDao.findByConditions(set).size() > 0)
            flag = false;
        else if(lpHmDao.findByConditions(set).size() > 0)
            flag = false;
        else if(lpWmDao.findByConditions(set).size() > 0)
            flag = false;

        if(flag) {
            dao.deleteById(contractId);
        } else {
            Contract entity = dao.get(contractId);
            Code code = codeDao.get(codeDao.getCodeIdByCode(ContractStatus.CANCEL.getCode()));
            entity.setStatus(code);
            dao.update(entity);
        }
    }

    /* (non-Javadoc)
     * @see com.aimir.service.system.ContractManager#deleteContract(java.lang.Integer, java.lang.Integer)
     */
    @Override
	public void deleteContract(Integer contractId, Integer operatorId) {
        // remove customer id
        Contract contract = dao.get(contractId);
        Integer customerId = contract.getCustomer().getId();
        dao.clear();
        contract.setCustomer(null);
        dao.update(contract);

        Operator operator = operatorDao.get(operatorId);
        Customer customer = customerDao.get(customerId);
        String currentTime = null;

        try {
            currentTime = TimeUtil.getCurrentTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        addContractChangeLog(customerId, null, "customer", currentTime, currentTime, customer, operator, contract);
    }

    @Override
	public void contractAllDelete(Integer id) {
        dao.contractAllDelete(id);
    }

//    public void contractAllDelete(Integer customerId) {
//        Set<Condition> set = new HashSet<Condition>();
//        set.add(new Condition("customer.id", new Object[] {customerId}, null, Restriction.EQ));
//        List<Contract> contractList = dao.findByConditions(set);
//
//        for (Contract obj : contractList) {
//            this.contractDelete(obj.getId());
//        }
//    }

    @Override
	@Deprecated
    public List<Map<String, Object>> getContracts(Map<String, Object> conditionMap) {

        List<Map<String, Object>> rtnList = null;

        String serviceTypeTab = (String)conditionMap.get("serviceTypeTab");

        String locationId = StringUtil.nullToBlank(conditionMap.get("location"));
        List<Integer> locationIdList = null;

        if (!locationId.isEmpty()) {
            locationIdList = locationDao.getChildLocationId(Integer.valueOf(locationId));
            locationIdList.add(Integer.valueOf(locationId));
            conditionMap.put("locationIdList", locationIdList);
        }

        if("".equals(serviceTypeTab))
            rtnList = makeAllCustomerTabData(dao.getAllCustomerTabData(conditionMap));
        else
            rtnList = dao.getEMCustomerTabData(conditionMap);

        return rtnList;
    }

    /**
     * method name : getCustomerListByType<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
    public List<Map<String, Object>> getCustomerListByType(Map<String, Object> conditionMap) {
        List<Map<String, Object>> result = null;
        Integer locationId = (Integer)conditionMap.get("location");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        result = dao.getCustomerListByType(conditionMap, false);
        return result;
    }

    /**
     * method name : getCustomerListByTypeTotalCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
    public Integer getCustomerListByTypeTotalCount(Map<String, Object> conditionMap) {
        Integer locationId = (Integer)conditionMap.get("location");
        List<Integer> locationIdList = null;

        if (locationId != null) {
            locationIdList = locationDao.getChildLocationId(locationId);
            locationIdList.add(locationId);
            conditionMap.put("locationIdList", locationIdList);
        }

        List<Map<String, Object>> result = dao.getCustomerListByType(conditionMap, true);
        return (Integer)(result.get(0).get("total"));
    }

    @Override
    @Deprecated
    public List<Map<String, Object>> getContractsByParam(String customerNo,
            String customerName, String location, String tariffIndex,
            String contractDemand, String creditType, String mdsId,
            String status, String dr, String startDate, String endDate,
            String serviceTypeTab, String supplierId) {

        Map<String, Object> condition = new HashMap<String, Object>();
        condition .put("customerNo", customerNo);
        condition .put("customerName", customerName);
        condition .put("location", location);
        condition .put("tariffIndex", tariffIndex);
        condition .put("contractDemand", contractDemand);
        condition .put("creditType", creditType);
        condition .put("mdsId", mdsId);
        condition .put("status", status);
        condition .put("dr", dr);
        condition .put("startDate", startDate);
        condition .put("endDate", endDate);
        condition .put("serviceTypeTab", serviceTypeTab);
        condition .put("supplierId", supplierId);

        return getContracts(condition);
    }

    @SuppressWarnings("unused")
    @Deprecated
    private List<Map<String, Object>> makeAllCustomerTabData(List<Object[]> contracts) {

        List<Map<String, Object>> oneDepthList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> twoDepthList = new ArrayList<Map<String, Object>>();
        Map<String, Object> oneDepthMap = null;
        Map<String, Object> twoDepthMap = null;

        String currUserName = "";
        String currUserId = "";
        String userId = null;
        String userName = null;
        String address = null;
        String contractId = null;
        String contractNumber = null;
        String location = null;
        String serviceTypeName = null;
        String meterId = null;
//      String customTypeName = null;
        String sicName = null;
        String serviceType = null;
        String customerNo = null;

        int customerSize = 0;
        Object[] object = null;

        for (int i = 0, size = contracts.size() ; i < size ; i++) {

            object = contracts.get(i);

            if(object[0] != null) { userId = object[0].toString(); } else { userId = ""; }
            if(object[1] != null) { userName = object[1].toString(); } else { userName = ""; }
            if(object[2] != null) { address = object[2].toString(); } else { address = ""; }
            if(object[3] != null) { contractId = object[3].toString(); } else { contractId = ""; }
            if(object[4] != null) { contractNumber = object[4].toString(); } else { contractNumber = ""; }
            if(object[5] != null) { location = object[5].toString(); } else { location = ""; }
            if(object[6] != null) { serviceTypeName = object[6].toString(); } else { serviceTypeName = ""; }
            if(object[7] != null) { meterId = object[7].toString(); } else { meterId = ""; }
            if(object[8] != null) { sicName = object[8].toString(); } else { sicName = ""; }
            if(object[9] != null) { serviceType = object[9].toString(); } else { serviceType = ""; }
            if(object[10] != null) { customerNo = object[10].toString(); } else { customerNo = ""; }

            twoDepthMap = new HashMap<String, Object>();
            twoDepthMap.put("customerId", userId);
//2010-08-03 twoDepth의 고객명 대신 contractNumber 표시 요청
//          twoDepthMap.put("customerName", userName);
            twoDepthMap.put("customerName", contractNumber);
            twoDepthMap.put("address", address);
            twoDepthMap.put("contractId", contractId);
            twoDepthMap.put("contractNumber", contractNumber);
            twoDepthMap.put("location", location);
            twoDepthMap.put("serviceTypeName", serviceTypeName);
            twoDepthMap.put("serviceType", serviceType);
            twoDepthMap.put("meterId", meterId);
            twoDepthMap.put("sicName", sicName);
            twoDepthMap.put("customerNo", customerNo);
            if (i > 0) {
                if (!userName.equals(contracts.get(i-1)[1].toString())) {
                    twoDepthList = new ArrayList<Map<String, Object>>();
                }
            }
            if (!"".equals(contractId)) twoDepthList.add(twoDepthMap);

            //첫번째 행이 하나 일때를 위해 행해준다
            if (!currUserName.equals(userName)) {

                if (!"".equals(currUserName)) {
                    oneDepthMap = new HashMap<String, Object>();
                    oneDepthMap.put("customerName", userName);
                    oneDepthMap.put("customerId", userId);
                    oneDepthMap.put("customerNo", customerNo);
                    oneDepthMap.put("children", twoDepthList);
                    oneDepthList.add(oneDepthMap);

//                  twoDepthList = new ArrayList<Map<String, Object>>();
                }

                currUserName = userName;
                currUserId = userId;
                customerSize++;
            }

            if (i == 0) {
                oneDepthMap = new HashMap<String, Object>();
                oneDepthMap.put("customerName", userName);
                oneDepthMap.put("customerId", userId);
                oneDepthMap.put("customerNo", customerNo);
                oneDepthMap.put("children", twoDepthList);
                oneDepthList.add(oneDepthMap);
            }

            if (size - 1 == i) {
                //첫번째 행을 위해 감싸준다
                if(!currUserName.equals(userName)) {
                    oneDepthMap = new HashMap<String, Object>();
                    oneDepthMap.put("customerName", userName);
                    oneDepthMap.put("children", twoDepthList);
                    oneDepthMap.put("customerId", userId);
                    oneDepthMap.put("customerNo", customerNo);
                    oneDepthMap.put("customerSize", customerSize++);
                    oneDepthList.add(oneDepthMap);
                }
            }
        }
        return oneDepthList;
    }

    /**
     * method name : getContractsTree<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
	public List<Map<String, Object>> getContractsTree(Map<String, Object> conditionMap) {
        List<Map<String, Object>> rtnList = null;

        String serviceTypeTab = (String)conditionMap.get("serviceTypeTab");

        String locationId = StringUtil.nullToBlank(conditionMap.get("location"));
        List<Integer> locationIdList = null;

        if (!locationId.isEmpty()) {
            locationIdList = locationDao.getChildLocationId(Integer.valueOf(locationId));
            locationIdList.add(Integer.valueOf(locationId));
            conditionMap.put("locationIdList", locationIdList);
        }

        if ("".equals(serviceTypeTab)) {
            rtnList = makeAllCustomerTabDataTree(dao.getAllCustomerTabDataTree(conditionMap, false));
        } else {
            rtnList = dao.getEMCustomerTabData(conditionMap);
        }

        return rtnList;
    }

    private List<Map<String, Object>> makeAllCustomerTabDataTree(List<Map<String, Object>> contracts) {

        List<Map<String, Object>> customerList = new ArrayList<Map<String, Object>>();
        Map<String, Object> customerMap = null;
        StringBuilder addr = null;

        for (Map<String, Object> map : contracts) {
            addr = new StringBuilder();
            customerMap = new HashMap<String, Object>();
            customerMap.put("id", map.get("CUSTOMER_ID").toString());
            customerMap.put("customerId", map.get("CUSTOMER_ID"));
            customerMap.put("customerName", map.get("CUSTOMER_NAME"));
            customerMap.put("gs1", map.get("GS1"));

            if (map.get("CUSTOMER_ADDRESS") != null) {
                addr.append(map.get("CUSTOMER_ADDRESS"));
            }
            if (map.get("CUSTOMER_ADDRESS1") != null) {
                if (addr.length() > 0) {
                    addr.append(" ");
                }
                addr.append(map.get("CUSTOMER_ADDRESS1"));
            }
            if (map.get("CUSTOMER_ADDRESS2") != null) {
                if (addr.length() > 0) {
                    addr.append(" ");
                }
                addr.append(map.get("CUSTOMER_ADDRESS2"));
            }
            if (map.get("CUSTOMER_ADDRESS3") != null) {
                if (addr.length() > 0) {
                    addr.append(" ");
                }
                addr.append(map.get("CUSTOMER_ADDRESS3"));
            }

            customerMap.put("address", addr.toString());
            customerMap.put("contractId", "");
            customerMap.put("contractNumber", "");
            customerMap.put("location", "");
            customerMap.put("serviceTypeName", "");
            customerMap.put("serviceType", "");
            customerMap.put("meterId", "");
            customerMap.put("sicName", "");
            customerMap.put("customerNo", map.get("CUSTOMER_NO"));
            customerMap.put("iconCls", "task-master");
            customerList.add(customerMap);
        }

        return customerList;
    }

    /**
     * method name : getContractsChildTree<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
	public List<Map<String, Object>> getContractsChildTree(Map<String, Object> conditionMap) {
        List<Map<String, Object>> contractList = new ArrayList<Map<String, Object>>();
        Map<String, Object> contractMap = null;
        List<Map<String, Object>> rtnList = null;

        String locationId = StringUtil.nullToBlank(conditionMap.get("location"));
        List<Integer> locationIdList = null;

        if (!locationId.isEmpty()) {
            locationIdList = locationDao.getChildLocationId(Integer.valueOf(locationId));
            locationIdList.add(Integer.valueOf(locationId));
            conditionMap.put("locationIdList", locationIdList);
        }

        rtnList = dao.getContractListByCustomer(conditionMap);

        for (Map<String, Object> map : rtnList) {
            contractMap = new HashMap<String, Object>();
            contractMap.put("id", map.get("CUSTOMER_ID").toString() + "_" + map.get("CONTRACT_ID").toString());
            contractMap.put("customerId", map.get("CUSTOMER_ID"));
            contractMap.put("customerName", map.get("CONTRACT_NUMBER"));
            contractMap.put("address", "");
            contractMap.put("contractId", map.get("CONTRACT_ID"));
            contractMap.put("contractNumber", map.get("CONTRACT_NUMBER"));
            contractMap.put("location", map.get("LOCATION_NAME"));
            contractMap.put("serviceTypeName", map.get("SERVICETYPE_NAME"));
            contractMap.put("serviceType", map.get("SERVICETYPE_ID"));
            contractMap.put("meterId", map.get("MDS_ID"));
            contractMap.put("sicName", map.get("SIC_NAME"));
            contractMap.put("customerNo", map.get("CUSTOMER_NO"));
            contractMap.put("iconCls", "task");
            contractMap.put("leaf", true);
            contractMap.put("gs1", map.get("GS1"));
            contractList.add(contractMap);
        }

        return contractList;
    }

    /**
     * method name : getContractCount<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    @Override
	public String getContractCount(Map<String, Object> conditionMap) {

        String count = null;

        String serviceTypeTab = (String)conditionMap.get("serviceTypeTab");
        String locationId = StringUtil.nullToBlank(conditionMap.get("location"));
        List<Integer> locationIdList = null;

        if (!locationId.isEmpty()) {
            locationIdList = locationDao.getChildLocationId(Integer.valueOf(locationId));
            locationIdList.add(Integer.valueOf(locationId));
            conditionMap.put("locationIdList", locationIdList);
        }

        if ("".equals(serviceTypeTab)) {
            List<Map<String, Object>> result = dao.getAllCustomerTabDataTree(conditionMap, true);
            count = result.get(0).get("total").toString();
        } else if ("EM".equals(serviceTypeTab)) {
            count = dao.getEMCustomerTabDataCount(conditionMap);
        } else if ("GM".equals(serviceTypeTab)) {
            count = dao.getEMCustomerTabDataCount(conditionMap);
        } else if ("WM".equals(serviceTypeTab)) {
            count = dao.getEMCustomerTabDataCount(conditionMap);
        }

        return count;
    }

    /**
     * method name : getTotalContractCount<b/>
     * method Desc : Customer Contract 맥스가젯에서 전체 계약 개수를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    @Override
	public Integer getTotalContractCount(Map<String, Object> conditionMap) {
        Integer totalCount = 0;

        String locationId = StringUtil.nullToBlank(conditionMap.get("location"));
        List<Integer> locationIdList = null;

        if (!locationId.isEmpty()) {
            locationIdList = locationDao.getChildLocationId(Integer.valueOf(locationId));
            locationIdList.add(Integer.valueOf(locationId));
            conditionMap.put("locationIdList", locationIdList);
        }

        totalCount = dao.getTotalContractCount(conditionMap);
        return totalCount;
    }

    @Override
	public Map<String, Object> getContractInfo(int contractId) {

        return dao.getContractInfo(contractId);
    }

    @Override
	public Map<String, Object> getContractInfo(int contractId, int supplierId) {
        Map<String, Object> result = dao.getContractInfo(contractId);

        Supplier supplier = supplierDao.get(supplierId);
        String lang = supplier.getLang().getCode_2letter();
        String country = supplier.getCountry().getCode_2letter();
        DecimalFormat df = DecimalUtil.getDecimalFormat(supplier.getMd());

        String contractDemand = StringUtil.nullToBlank(result.get("contractDemand"));
        if(contractDemand != null && contractDemand.length() > 0) {
            result.put("contractDemand", df.format(Double.parseDouble(contractDemand)));
        }

        ServiceType2 serviceType2 = result.get("serviceType2") == null ? null : (ServiceType2) result.get("serviceType2");
        if(serviceType2 != null)
        	result.put("serviceType2", serviceType2.getName());
        
        String contractDate = StringUtil.nullToBlank(result.get("contractDate"));
        if(!contractDate.isEmpty() && !"-".equals(contractDate))
        	result.put("contractDate", TimeLocaleUtil.getLocaleDateByMediumFormat(contractDate, lang, country));

        return result;
    }

    @Override
	public List<Object[]> getContractListByMeter(Map<String, String> conditionMap){
        return dao.getContractListByMeter(conditionMap);
    }

    /* (non-Javadoc)
     * @see com.aimir.service.system.ContractManager#checkContract(int, java.lang.String)
     */
    @Override
	public boolean checkContract(Contract contract) {

        List<Contract> contracts = dao.getContractList(contract);

        for (Contract contract2 : contracts) {

            if (contract2.getId() != null) {

                return true;
            }
        }

        return false;
    }

    @Override
	public List<Contract> getContractByContract(Contract contract) {

        List<Contract> contracts = dao.getContractList(contract);

        return contracts;
    }

    @Override
	public Contract getContractByContractNumber(String contractNumber) {
        return dao.findByCondition("contractNumber", contractNumber);
    }
    
    @Override
	public List<Contract> getContractByContractNumber2(String contractNumber, Integer supplierId) {
    	Set<Condition> condition = new HashSet<Condition>();
    	condition.add(new Condition("contractNumber", new Object[]{contractNumber}, null, Restriction.EQ));
    	condition.add(new Condition("supplierId", new Object[]{supplierId}, null, Restriction.EQ));
    	
    	return dao.getContractByListCondition(condition);
    }

    @Override
    public Map<String,Object> getPartpayInfoByContractNumber(String contractNumber, Integer supplierId) {
    	return dao.getPartpayInfoByContractNumber(contractNumber, supplierId);
    }

    @Override
    public Map<String, Object> readExcelXLS(String excel, int supplierId) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim()); // jhkim trim() 추가

            // Workbook
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

            // Text Extraction
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();

            Row titles = null;

            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }
                Map<String, Object> tempMap = getFileMap(titles, row, supplierId);
                if (tempMap == null) {
                    continue;
                } else {
                    resultList.add(tempMap);
                }

            } // for end : Row

            result.put("file", resultList);
        } catch(IOException ie) {
            ie.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
	public Map<String,Object> readExcelXLSX(String excel, int supplierId) {

        Map<String,Object> result = new HashMap<String,Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim());
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }

            // Workbook
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());

            // Text Extraction
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug( extractor.getText() );

            List<Object> resultList = new ArrayList<Object>();
            Row titles = null;

            // Getting cell contents
//          for( int i=0; i<wb.getNumberOfSheets(); i++) {
                for( Row row : wb.getSheetAt(0) ) {

                    if( row.getRowNum() == 0 ) {
                        titles = row;
                        continue;
                    }

                    Map<String, Object> tempMap = getFileMap(titles, row, supplierId);
                    if(tempMap == null){
                        continue;
                    }else{
                        resultList.add(tempMap);
                    }

                } // for end : Row
//          } // for end : Sheet

            result.put("file", resultList);

        }catch(IOException ie){
            ie.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

        return result;
    }

    @SuppressWarnings("unused")
    private Map<String, Object> getFileMap(Row titles, Row row, int supplierId) throws IOException {

        Map<String, Object> returnData = new HashMap<String, Object>();
        String colName = null;
        String colValue = null;
        String status = "Success";
        int cnt = 0;

        for (Cell cell : row) {
            if(titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")){
            	continue;
            }
            
            cell.setCellType(1);
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString().trim();
            colValue = cell.getRichStringCellValue().getString().trim();
            boolean bool = inValidNull(colValue);

            if (colName.equals("customerNumber")) {
//                Customer tmpCustomer = customerDao.findByCondition("customerNo", colValue.toString());
//                if (tmpCustomer == null) {
//                    status = "Failure";
//                    if (!("".equals(colValue)))
//                        colValue = colValue + " (!)";
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;
                    Customer tmpCustomer = customerDao.findByCondition("customerNo", colValue.toString());

                    if (tmpCustomer == null) {
                        status = "Failure";
                        if (!("".equals(colValue))) {
                            colValue = colValue + " (!)";
                        }
                    }
                }
            } else if (colName.equals("supplier")) {
//                if (colValue.toString() == null || colValue.toString() == "") {
//                    status = "Failure";
//                } else {
//                    Supplier tmpSupplier = supplierDao.findByCondition("name", colValue.toString());
//
//                    if (tmpSupplier == null) {
//                        status = "Failure";
//                        if (!("".equals(colValue)))
//                            colValue = colValue + " (!)";
//                    }
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;
                    Supplier tmpSupplier = supplierDao.findByCondition("name", colValue.toString());

                    if (tmpSupplier == null) {
                        status = "Failure";
                        if (!("".equals(colValue)))
                            colValue = colValue + " (!)";
                    }
                }
            } else if (colName.equals("meaNumber")) { // for MEA, 계약번호
//                if (numberOverlapCheck(colValue) > 0) {
//                    status = "Failure";
//                    if (!("".equals(colValue)))
//                        colValue = colValue + " (!)";
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;

                    if (status == "Success" && numberOverlapCheck(colValue) > 0) {
                        status = "Update";
                    }
                }
            } else if (colName.equals("tariffIndex")) {
                TariffType tmpTariffType = null;
                try {
                    tmpTariffType = getTariffTypeByCode(colValue.toString());
                    if(tmpTariffType == null){
                    	colValue = colValue + " (!)";
                    }
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    }
                }
            } else if (colName.equals("serviceTypeCode")) {
//                Code tmpCode = getCodeByValue(colValue.toString()); // Code
//                if (tmpCode == null) {
//                    status = "Failure";
//                    if (!("".equals(colValue)))
//                        colValue = colValue + " (!)";
//                } else {
//                    colValue = tmpCode.getName();
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;
                    Code tmpCode = getCodeByValue(colValue.toString()); // Code

                    if (tmpCode == null) {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    } else {
                        colValue = tmpCode.getName();
                    }
                }
            } else if (colName.equals("supplyStatus")) {
//                Code tmpCode = getCodeByValue(colValue.toString()); // Code
//                if (tmpCode == null) {
//                    status = "Failure";
//                    if (!("".equals(colValue)))
//                        colValue = colValue + " (!)";
//                } else {
//                    colValue = tmpCode.getName();
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;
                    Code tmpCode = getCodeByValue(colValue.toString()); // Code

                    if (tmpCode == null) {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    } else {
                        colValue = tmpCode.getName();
                    }
                }
            } else if (colName.equals("location")) {
//                Location tmpLocation = getLocation(colValue.toString());
//                if (tmpLocation == null) {
//                    status = "Failure";
//                    if (!("".equals(colValue)))
//                        colValue = colValue + " (!)";
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;
                    Location tmpLocation = getLocation(colValue.toString());

                    if (tmpLocation == null) {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    }
                }
            } else if (colName.equals("contractDemand")) {
            	if(!colValue.equals("")){
                    Double tmpDouble = null;
                    try {
                        tmpDouble = Double.parseDouble(colValue.toString());
                    } catch(Exception e) {
//                        if (colValue == null || "".equals(colValue)) {
//                        } else {
//                            status = "Failure";
//                            colValue = colValue + " (!)";
//                        }
                        status = "Failure";
                        colValue = colValue + " (!)";
                    }            		
            	}
            } else if (colName.equals("paymentType")) {
//                Code tmpCode = getCodeByValue(colValue.toString()); // Code
//                if (tmpCode == null) {
//                    status = "Failure";
//                    if (!("".equals(colValue)))
//                        colValue = colValue + " (!)";
//                } else {
//                    colValue = tmpCode.getName();
//                }
//                if (!bool)
//                    cnt++;
                if (!bool) {
                    cnt++;
                    Code tmpCode = getCodeByValue(colValue.toString()); // Code

                    if (tmpCode == null) {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    } else {
                        colValue = tmpCode.getName();
                    }
                }
            } else if (colName.equals("paymentStatus")) {
                Code tmpCode = getCodeByValue(colValue.toString()); // Code
                if (tmpCode == null) {
//                    if (colValue == null || "".equals(colValue)) {
//                    } else {
//                        status = "Failure";
//                        colValue = colValue + " (!)";
//                    }
                    status = "Failure";
                    colValue = colValue + " (!)";
                } else {
                    colValue = tmpCode.getName();
                }
            } else if (colName.equals("prepaymentThreshold")) {
            	if(!colValue.equals("")){
                    Double tmpDouble = null;
                    try {
                        tmpDouble = Double.parseDouble(colValue.toString());
                    } catch(Exception e) {
//                        if (colValue == null || "".equals(colValue)) {
//                        } else {
//                            status = "Failure";
//                            colValue = colValue + " (!)";
//                        }
                        status = "Failure";
                        colValue = colValue + " (!)";
                    }            		
            	}
            } else if (colName.equals("meterDeviceSerial")) {
                Meter tmpMeter = null;
                boolean isFailure = false;
                try {
                    tmpMeter = meterDao.get(colValue.toString());

                    if (tmpMeter != null) {
                        Integer meterContractCnt = contractDao.meterOverlapCheck(tmpMeter.getId());
                        if (meterContractCnt > 0) {
                        	colValue = colValue + " (!) Duplicate contract.";
                            isFailure = true;
                        }
                    } else {
                    	colValue = colValue + " (!) No Meter.";
                        isFailure = true;
                    }
                } catch(Exception e) {
//                    if (colValue == null || "".equals(colValue)) {
//                    } else {
//                        status = "Failure";
//                        colValue = colValue + " (!)";
//                    }
                    isFailure = true;
                    colValue = colValue + " (!)";
                }

                if (isFailure) {
                    status = "Failure";
                }
            } else if (colName.equals("SIC")) {
            	if(!colValue.equals("")){
                    Code tmpCode = getCodeByValue(colValue.toString()); // Code
                    if (tmpCode == null) {
//                        if (colValue == null || "".equals(colValue)) {
//                        } else {
//                            status = "Failure";
//                            colValue = colValue + " (!)";
//                        }
                        status = "Failure";
                        colValue = colValue + " (!)";
                    } else {
                        colValue = tmpCode.getName();
                    }	
            	}
            } else if (colName.equals("cashPoint")) {
            	if(!colValue.equals("")){
                    Integer cashPoint = null;
                    try {
                    	cashPoint = Integer.parseInt(colValue.toString());
                    } catch(Exception e) {
                        status = "Failure";
                        colValue = colValue + " (!)";
                    }            		
            	}
            } 
            
            returnData.put(colName, colValue);
        } // for end : Cell

        // customerNumber, contractNumber, serviceTypeCode, location, supplyStatus, supplier, paymentType 값이 null일경우 실패
        if (cnt != 7) {
            status = "Failure";
        }
        returnData.put("Status", status);
        if (inValidList(returnData))
            return null;
        return returnData;
    }

    @Override
	@SuppressWarnings("unused")
    public Map<String, Object> saveExcelXLS(String excel, int supplierId) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim()); // jhkim trim() 추가

            // Workbook
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(file));

            // Text Extraction
            ExcelExtractor extractor = new ExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();
            int totCnt = 0;
            int overLapCnt = 0;
            Row titles = null;

            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }
                if (listNullCheck(titles, row) != null) {
                    totCnt++;
                    ResultStatus insertResult = ResultStatus.SUCCESS;
                    Map<String, Object> map = getContractList(titles, row, supplierId);

                    if (map == null) {
                        overLapCnt++;
                        continue;
                    }

                    Boolean isUpdate = (Boolean)map.get("isUpdate");
                    Contract contract = (Contract)map.get("contract");

                    try {
//                        // supplier 공백이 있을경우 로그인할때의 공급사 아이디로 지정
//                        if (contract.getSupplier() == null) {
//                            contract.setSupplier(supplierDao.get(supplierId));
//                        }
//                        // 충전가능 default true
//                        contract.setChargeAvailable(Boolean.TRUE);
//                        contract.setContractDate(TimeUtil.getCurrentTime());
//                        contractDao.add(contract);
                        if (isUpdate) {
                            Contract updContract = contractDao.findByCondition("contractNumber", contract.getContractNumber());

                            mergeExcelContract(contract, updContract);
                            contractDao.update(updContract);
                        } else {
                            // supplier 공백이 있을경우 로그인할때의 공급사 아이디로 지정
                            if (contract.getSupplier() == null) {
                                contract.setSupplier(supplierDao.get(supplierId));
                            }
                            // 충전가능 default true
                            contract.setChargeAvailable(Boolean.TRUE);
                            contract.setContractDate(TimeUtil.getCurrentTime());
                            
                            //추가 정보저장
                            contract.setNotificationInterval(1);
                            contract.setNotificationPeriod(1);
                            contract.setNotificationTime(13);
                            contract.setServiceType2(ServiceType2.NewService);                            
                            
                            contractDao.add(contract);
                        }
                    } catch(Exception e) {
                        insertResult = ResultStatus.FAIL;
                        logger.error(e.toString(), e);
                        throw new Exception("저장에 실패 하였습니다.");
                    } finally {
                        if (!isUpdate) {
                            Map<String, Object> logData = new HashMap<String, Object>();

                            // 로그 저장
                            logData.put("deviceType", TargetClass.Contract);
                            logData.put("deviceName", contract.getContractNumber());
                            logData.put("deviceModel", null);
                            logData.put("resultStatus", insertResult);
                            logData.put("regType", RegType.Bulk);
                            logData.put("supplier", contract.getSupplier());

                            insertDeviceRegLog(logData);
                        }
                    }
                }
            } // for end : Row

            result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt));
        } catch(IOException ie) {
            ie.printStackTrace();
            logger.error(ie.toString(), ie);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }

        return result;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertDeviceRegLog(Map<String, Object> insertData) {
        // DeviceLog 객체 생성
        DeviceRegLog deviceRegLog = new DeviceRegLog();

        try {
            deviceRegLog.setCreateDate(TimeUtil.getCurrentTime());
        } catch(ParseException e) {
            e.printStackTrace();
        }
        deviceRegLog.setDeviceType((TargetClass)insertData.get("deviceType"));
        deviceRegLog.setDeviceName((String)insertData.get("deviceName"));
        deviceRegLog.setDeviceModel((DeviceModel)insertData.get("deviceModel"));
        deviceRegLog.setResult((ResultStatus)insertData.get("resultStatus"));
        deviceRegLog.setRegType((RegType)insertData.get("regType"));
        if ((Supplier)insertData.get("supplier") != null) {
            deviceRegLog.setSupplier((Supplier)insertData.get("supplier"));
        }

        // DeviceLog 등록
        deviceRegistrationDao.add(deviceRegLog);
        deviceRegistrationDao.flushAndClear();
    }

    @Override
	@SuppressWarnings("unused")
    public Map<String, Object> saveExcelXLSX(String excel, int supplierId) {

        Map<String, Object> result = new HashMap<String, Object>();

        try {
            ctxRoot = excel.substring(0, excel.lastIndexOf("/"));

            // check file
            File file = new File(excel.trim());
            if (!file.exists() || !file.isFile() || !file.canRead()) {
                throw new IOException(excel.trim());
            }

            // Workbook
            XSSFWorkbook wb = new XSSFWorkbook(excel.trim());

            // Text Extraction
            XSSFExcelExtractor extractor = new XSSFExcelExtractor(wb);
            extractor.setFormulasNotResults(true);
            extractor.setIncludeSheetNames(false);

            // Excel 전체 출력
            logger.debug(extractor.getText());

            List<Object> resultList = new ArrayList<Object>();
            int totCnt = 0;
            int overLapCnt = 0;
            Row titles = null;
            // Getting cell contents
            for (Row row : wb.getSheetAt(0)) {

                if (row.getRowNum() == 0) {
                    titles = row;
                    continue;
                }
                if (listNullCheck(titles, row) != null) {
                    totCnt++;
                    ResultStatus insertResult = ResultStatus.SUCCESS;
                    Map<String, Object> map = getContractList(titles, row, supplierId);

                    if (map == null) {
                        overLapCnt++;
                        continue;
                    }

                    Boolean isUpdate = (Boolean)map.get("isUpdate");
                    Contract contract = (Contract)map.get("contract");

                    try {
                        if (isUpdate) {
                            Contract updContract = contractDao.findByCondition("contractNumber", contract.getContractNumber());

                            mergeExcelContract(contract, updContract);
                            contractDao.update(updContract);
                        } else {
                            // supplier 공백이 있을경우 로그인할때의 공급사 아이디로 지정
                            if (contract.getSupplier() == null) {
                                contract.setSupplier(supplierDao.get(supplierId));
                            }
                            // 충전가능 default true
                            contract.setChargeAvailable(Boolean.TRUE);
                            contract.setContractDate(TimeUtil.getCurrentTime());
                            
                            //추가 정보저장
                            contract.setNotificationInterval(1);
                            contract.setNotificationPeriod(1);
                            contract.setNotificationTime(13);
                            contract.setServiceType2(ServiceType2.NewService);    
                            
                            contractDao.add(contract);
                        }
                    } catch(Exception e) {
                        insertResult = ResultStatus.FAIL;
                        logger.error(e.toString(), e);
                        throw new Exception("저장에 실패 하였습니다.");
                    } finally {
                        if (!isUpdate) {
                            Map<String, Object> logData = new HashMap<String, Object>();

                            // 로그 저장
                            logData.put("deviceType", TargetClass.Contract);
                            logData.put("deviceName", contract.getContractNumber());
                            logData.put("deviceModel", null);
                            logData.put("resultStatus", insertResult);
                            logData.put("regType", RegType.Bulk);
                            logData.put("supplier", contract.getSupplier());

                            insertDeviceRegLog(logData);
                        }
                    }
                }
            } // for end : Row

            result.put("resultMsg", "Total: " + totCnt + ", Success: " + (totCnt - overLapCnt));
        } catch(IOException ie) {
            ie.printStackTrace();
            logger.error(ie.toString(), ie);
        } catch(Exception e) {
            e.printStackTrace();
            logger.error(e.toString(), e);
        }

        return result;
    }

    @SuppressWarnings("unused")
    private Map<String, Object> getContractList(Row titles, Row row, int supplierId) {
        Map<String, Object> map = new HashMap<String, Object>();
        String colName = null;
        String colValue = null;
        Contract contract = new Contract();
        boolean isUpdate = false;
        int cnt = 0;

        for (Cell cell : row) {
        	if(titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")){
        		continue;
        	}
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();

            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    colValue = cell.getRichStringCellValue().getString();
                    break;

                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        colValue = cell.getDateCellValue().toString();
                    } else {
                        Long roundVal = Math.round(cell.getNumericCellValue());
                        Double doubleVal = cell.getNumericCellValue();
                        if (doubleVal.equals(roundVal.doubleValue())) {
                            colValue = String.valueOf(roundVal);
                        } else {
                            colValue = String.valueOf(doubleVal);
                        }
                    }
                    break;

                case Cell.CELL_TYPE_BOOLEAN:
                    colValue = String.valueOf(cell.getBooleanCellValue());
                    break;

                case Cell.CELL_TYPE_FORMULA:
                    colValue = cell.getCellFormula();
                    break;

                default:
                    colValue = "";
            }
            
            if(colValue != null) colValue = colValue.trim();

            if (colName.equals("customerNumber")) {
                Customer tmpCustomer = customerDao.findByCondition("customerNo", colValue.toString());
                if (tmpCustomer != null) {
                    contract.setCustomer(tmpCustomer);
                    cnt++;
                } else {
                    return null;
                }

            } else if (colName.equals("supplier")) {
                if (colValue == null || colValue == "") {
                    Supplier tmpSupplier = supplierDao.get(supplierId);
                } else {
                    Supplier tmpSupplier = supplierDao.findByCondition("name", colValue.toString());
                    if (tmpSupplier != null) {
                        contract.setSupplier(tmpSupplier);
                        cnt++;
                    } else {
                        return null;
                    }
                }
            } else if (colName.equals("meaNumber")) {
//                if (numberOverlapCheck(colValue) == 0) {
//                    contract.setContractNumber(colValue.toString());
//                    cnt++;
//                } else {
//                    return null;
//                }
                if (StringUtil.nullToBlank(colValue).isEmpty()) {
                    return null;
                }

                if (numberOverlapCheck(colValue) > 0) {
                    isUpdate = true;
                }
                contract.setContractNumber(colValue.toString());
                cnt++;
            } else if (colName.equals("tariffIndex")) {
                TariffType tmpTariffType;
                try {
                    tmpTariffType = getTariffTypeByCode(colValue.toString());
                    contract.setTariffIndex(tmpTariffType);
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        return null;
                    }
                }
            } else if (colName.equals("serviceTypeCode")) {
                Code tmpCode = getCodeByValue(colValue.toString());
                if (tmpCode != null) {
                    contract.setServiceTypeCode(tmpCode);
                    cnt++;
                } else {
                    return null;
                }
            } else if (colName.equals("supplyStatus")) {
                Code tmpCode = getCodeByValue(colValue.toString());
                if (tmpCode != null) {
                    contract.setStatus(tmpCode);
                    cnt++;
                } else {
                    return null;
                }
            } else if (colName.equals("location")) {
                Location tmpLocation = getLocation(colValue.toString());
                if (tmpLocation != null) {
                    contract.setLocation(tmpLocation);
                    cnt++;
                } else {
                    return null;
                }
            } else if (colName.equals("contractDemand")) {
                Double tmpDouble = null;
                try {
                    tmpDouble = Double.parseDouble(colValue.toString());
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        return null;
                    }
                }
                contract.setContractDemand(tmpDouble);
            } else if (colName.equals("paymentType")) {
                Code tmpCode = getCodeByValue(colValue.toString());
                if (tmpCode != null) {
                    contract.setCreditType(tmpCode);
                    cnt++;

                    if (tmpCode.getId() == codeDao.getCodeIdByCode(Code.PREPAYMENT)) {
                        // 선불 시작일시 추가
                        try {
                            contract.setPrepayStartTime(TimeUtil.getCurrentTime());
                        } catch(ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    return null;
                }
            } else if (colName.equals("paymentStatus")) {
                Code tmpCode = getCodeByValue(colValue.toString());
                try {
                    contract.setCreditStatus(tmpCode);
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        return null;
                    }
                }
            } else if (colName.equals("prepaymentThreshold")) {
                int tmpDouble = 0;
                try {
                    tmpDouble = Integer.parseInt(colValue.toString());
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        return null;
                    }
                }
                contract.setPrepaymentThreshold(tmpDouble);
            } else if (colName.equals("meterDeviceSerial")) {
                Meter tmpMeter = meterDao.get(colValue.toString());

                if (tmpMeter != null) {
                    Integer meterContractCnt = contractDao.meterOverlapCheck(tmpMeter.getId());
                    if (meterContractCnt > 0) {
                        return null;
                    }
                    try {
                        contract.setMeter(tmpMeter);
                    } catch(Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            } else if (colName.equals("SIC")) {
                Code tmpCode = getCodeByValue(colValue.toString()); // Code
                try {
                    contract.setSic(tmpCode);
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        return null;
                    }
                }
            }else if (colName.equals("cashPoint")) {
                Integer cashPoint = null;
                try {
                	cashPoint = Integer.parseInt(colValue.toString());
                } catch(Exception e) {
                    if (colValue == null || "".equals(colValue)) {
                    } else {
                        return null;
                    }
                }
                contract.setCashPoint(cashPoint);
            }
        }

        if (cnt != 7) {
            return null;
        }
        
        map.put("isUpdate", isUpdate);
        map.put("contract", contract);
        return map;
    }

    private Map<String,Object> listNullCheck(Row titles, Row row) throws IOException{

        Map<String,Object> returnData = new HashMap<String,Object>();
        String colName  = null;
        String colValue = null;

        for( Cell cell : row ) {
        	if(titles.getCell(cell.getColumnIndex()) == null || titles.getCell(cell.getColumnIndex()).equals("")){
        		continue;
        	}
            cell.setCellType(1);
            colName = titles.getCell(cell.getColumnIndex()).getRichStringCellValue().getString();
            colValue = cell.getRichStringCellValue().getString();
            returnData.put(colName, colValue);
        } // for end : Cell
        if(inValidList(returnData)) return null;
        return returnData;
    }

    public Code getCodeByValue(String codeValue){
        Code code = codeDao.findByCondition("code", codeValue);
        return code;
    }

    public TariffType getTariffTypeByCode(String code){
        TariffType tariffType = null;
        try {
            tariffType = tariffTypeDao.findByCondition("code", Integer.parseInt(code));
        } catch (Exception e) {
            return null;
        }

        return tariffType;
    }

    public Location getLocation(String name){
        Location location = locationDao.findByCondition("name", name);
        return location;
    }

    public boolean inValidList(Map<String, Object> map){
        boolean bool = true;
        if(map.get("customerNumber") != null){
            if(!inValidNull(map.get("customerNumber").toString())){
                bool = false;
            }
        }

        if(map.get("supplier") != null){
            if(!inValidNull(map.get("supplier").toString())) {
                bool = false;
            }
        }

        if(map.get("paymentType") != null) {
        	if(!inValidNull(map.get("paymentType").toString())) {
                bool = false;
            }
        }

        if(map.get("meaNumber") != null){
            if(!inValidNull(map.get("meaNumber").toString())){
                bool = false;
            }
        }

        if(map.get("tariffIndex") != null){
            if(!inValidNull(map.get("tariffIndex").toString())){
                bool = false;
            }
        }

        if(map.get("serviceTypeCode") != null){
            if(!inValidNull(map.get("serviceTypeCode").toString())){
                bool = false;
            }
        }

        if(map.get("status") != null){
            if(!inValidNull(map.get("status").toString())){
                bool = false;
            }
        }

        if(map.get("location") != null){
            if(!inValidNull(map.get("location").toString())){
                bool = false;
            }
        }

        if(map.get("contractDemand") != null){
            if(!inValidNull(map.get("contractDemand").toString())){
                bool = false;
            }
        }

        if(map.get("creditType") != null){
            if(!inValidNull(map.get("creditType").toString())){
                bool = false;
            }
        }

        if(map.get("creditStatus") != null){
            if(!inValidNull(map.get("creditStatus").toString())){
                bool = false;
            }
        }

        if(map.get("currentCredit") != null){
            if(!inValidNull(map.get("currentCredit").toString())){
                bool = false;
            }
        }

        return bool;
    }

    /**
     * 공백 및 null 검사
     * @param str
     * @return null, "" 일경우 true 반환
     */
    public boolean inValidNull(String str) {
        boolean bool = false;
//        if(str.equals(""))  bool = true;
//        if(str == null)     bool = true;
        if (StringUtil.nullToBlank(str).isEmpty()) {
            bool = true;
        }
        return bool;
    }

    @Override
    public String getContractCountByParam(String customerNo,
            String customerName, String location, String tariffIndex,
            String contractDemand, String creditType, String mdsId,
            String status, String dr, String startDate, String endDate,
            String serviceTypeTab, String supplierId) {
        Map<String, Object> condition = new HashMap<String, Object>();
        condition .put("customerNo", customerNo);
        condition .put("customerName", customerName);
        condition .put("location", location);
        condition .put("tariffIndex", tariffIndex);
        condition .put("contractDemand", contractDemand);
        condition .put("creditType", creditType);
        condition .put("mdsId", mdsId);
        condition .put("status", status);
        condition .put("dr", dr);
        condition .put("startDate", startDate);
        condition .put("endDate", endDate);
        condition .put("serviceTypeTab", serviceTypeTab);
        condition .put("supplierId", supplierId);

        return getContractCount(condition);
    }


    /**
     * @desc    미터id의 계약 여부를 체크
     *
     * @param meterId
     * @return
     */
    @Override
	@Deprecated
    public int checkContractedMeterYn(String meterId) {
        int result = 0;

        if (meterId != "-1" || !meterId.equals("-1")) {
            result = dao.checkContractedMeterYn(meterId);
        } else {
            result = -1;
        }

        return result;
    }

    /**
     * method name : checkContractMeterYn<b/>
     * method Desc : 계약된 Meter 아이디 인지 여부를 체크한다.
     *
     * @param meterId Meter.mdsId - 미터 아이디
     * @return
     */
    @Override
	public int checkContractMeterYn(String meterId) {
        int result = 0;

        if (meterId != "-1" || !meterId.equals("-1")) {
            result = dao.checkContractMeterYn(meterId);
        } else {
            result = -1;
        }

        return result;
    }

    /**
     * method name : getCheckContractByMeterId<b/>
     * method Desc : Contract 등록/수정 시 선택한 Meter 가 현재 다른 Contract 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterId
     * @param contractId
     * @return
     */
    @Override
	@Deprecated
    public Map<String, Object> getCheckContractByMeterId(Integer meterId, Integer contractId) {
        Map<String, Object> result = new HashMap<String, Object>();
        Contract contract = dao.findByCondition("meter.id", meterId);
        boolean hasMeter = false;

        // 해당 Meter 와 연결된 Contract 가 존재할 경우
        if (contract != null && contract.getId() != null) {
            if (contractId != null) {   // Update 인 경우
                if (!contractId.equals(contract.getId())) {
                    hasMeter = true;
                }
            } else {    // Insert 인 경우
                hasMeter = true;
            }
        }

        if (hasMeter) {
            result.put("exist", "true");
            result.put("customerName", (contract.getCustomer() != null) ? contract.getCustomer().getName() : "");
            result.put("contractNumber", contract.getContractNumber());
        } else {
            result.put("exist", "false");
        }
        return result;
    }

    /**
     * method name : getCheckContractByMeterNo<b/>
     * method Desc : Contract 등록/수정 시 선택한 Meter 가 현재 다른 Contract 에 연결되어 있는지 여부를 체크한다.
     *
     * @param meterNo
     * @param contractId
     * @return
     */
    @Override
	public Map<String, Object> getCheckContractByMeterNo(String meterNo, Integer contractId) {
        Map<String, Object> result = new HashMap<String, Object>();

        Meter meter = meterDao.get(meterNo);

        // meter 존재 유무 체크
        if (meter == null) {
            result.put("exist", "false");
            return result;
        } else {
            result.put("exist", "true");
        }

        Contract contract = dao.findByCondition("meter.id", meter.getId());
        boolean hasContract = false;    // 기존 Contract 와 연결여부

        // 해당 Meter 와 연결된 Contract 가 존재할 경우
        if (contract != null && contract.getId() != null) {
            if (contractId != null) {   // Update 인 경우
                if (!contractId.equals(contract.getId())) {
                    hasContract = true;
                }
            } else {    // Insert 인 경우
                hasContract = true;
            }
        }

        if (hasContract) {
            result.put("hasContract", "true");
            result.put("customerName", (contract.getCustomer() != null) ? contract.getCustomer().getName() : "");
            result.put("contractNumber", contract.getContractNumber());
        } else {
            result.put("hasContract", "false");
        }
        return result;
    }

    /**
     * method name : getCheckContractNumber<b/>
     * method Desc : Contract 등록/수정 시 입력한 Contract Number 가 현재 다른 Customer 에 연결되어 있는지 여부를 체크한다.
     *
     * @param contractNumber
     * @return
     */
    @Override
	public Map<String, Object> getCheckContractNumber(String contractNumber) {
        Map<String, Object> result = new HashMap<String, Object>();
        Contract contract = dao.findByCondition("contractNumber", contractNumber);
        Boolean isExist = false;
        Boolean isLinked = false;

        // 해당 Contract 와 연결된 Customer 가 존재할 경우
        if (contract != null && contract.getId() != null) {
            isExist = true;

            if (contract.getCustomer() != null) {
                isLinked = true;
            }
        }

        result.put("exist", isExist.toString());
        result.put("linked", isLinked.toString());

        if (isExist) {
            result.put("customerName", (contract.getCustomer() != null) ? contract.getCustomer().getName() : "");
            result.put("contractId", contract.getId());
            result.put("tariffType", (contract.getTariffIndex() != null) ? contract.getTariffIndex().getId() : "");
            result.put("meterId", (contract.getMeter() != null) ? contract.getMeter().getId() : "");
            result.put("mdsId", (contract.getMeter() != null) ? contract.getMeter().getMdsId() : "");
            result.put("serviceType", (contract.getServiceTypeCode() != null) ? contract.getServiceTypeCode().getId() : "");
            result.put("status", (contract.getStatus() != null) ? contract.getStatus().getId() : "");
            result.put("locationId", (contract.getLocation() != null) ? contract.getLocation().getId() : "");
            result.put("locationName", (contract.getLocation() != null) ? contract.getLocation().getName() : "");
            result.put("creditType", (contract.getCreditType() != null) ? contract.getCreditType().getId() : "");
            result.put("contractDemand", contract.getContractDemand());
            result.put("sicId", (contract.getSic() != null) ? contract.getSic().getId() : "");
            result.put("sicName", (contract.getSic() != null) ? contract.getSic().getName() : "");
            result.put("creditStatus", (contract.getCreditStatus() != null) ? contract.getCreditStatus().getId() : "");
            result.put("prepaymentThreshold", contract.getPrepaymentThreshold());
            result.put("amountPaid", contract.getAmountPaid());
            result.put("receiptNumber", contract.getReceiptNumber());
            result.put("serviceType2", (contract.getServiceType2() != null) ? contract.getServiceType2().name() : "");
            result.put("currentArrears", contract.getCurrentArrears());
            result.put("oldArrears", contract.getOldArrears());
            result.put("arrearsContractCount", contract.getArrearsContractCount());
            result.put("arrearsPaymentCount", contract.getArrearsPaymentCount());
            result.put("chargeAvailable", contract.getChargeAvailable());
            
            result.put("threshold1", contract.getThreshold1());
            result.put("threshold2", contract.getThreshold2());
            result.put("threshold3", contract.getThreshold3());
            
        }
        return result;
    }

    /**
     * method name : insertContract<b/>
     * method Desc :
     *
     * @param conditionMap
     */
    @Override
	public Contract insertContract(Map<String, Object> conditionMap) {
        Contract contract = (Contract)conditionMap.get("contract");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer customerId = (Integer)conditionMap.get("customerId");
        Integer prevContractId = (Integer)conditionMap.get("prevContractId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String serviceType2 = StringUtil.nullToBlank(conditionMap.get("serviceType2"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String contractNumber = StringUtil.nullToBlank(conditionMap.get("contractNumber"));
        String startDatetime = StringUtil.nullToBlank(conditionMap.get("startDatetime"));
        Boolean isPartpayment = (Boolean)conditionMap.get("isPartpayment");
        String initArrears = StringUtil.nullToBlank(conditionMap.get("initArrears"));
        Operator operator = operatorDao.get(operatorId);

        // Update 화면에서 신규등록할 경우 기존 Contract 의 customer 를 null 로 수정한다.
        if (prevContractId != null) {
            Customer prevCustomer = customerDao.get(customerId);
            Contract prevContract = dao.get(prevContractId);

            addContractChangeLog(customerId, null, "customer", startDatetime, startDatetime, prevCustomer, operator, prevContract);

            Contract updPrevContract = dao.get(prevContractId);
            updPrevContract.setCustomer(null);
            dao.update(updPrevContract);
            dao.flushAndClear();
        }

        if (customerId != null) {
            Customer tmpCustomer = customerDao.get(customerId);
            contract.setCustomer(tmpCustomer);
        }

        // 계약일자에 오늘날짜를 설정한다.
        contract.setContractDate(startDatetime);

        Code sicCode = null;
        if (sicId != null) {
            sicCode = codeDao.get(sicId);
        }
        contract.setSic(sicCode);

        Meter meter = meterDao.get(mdsId);
        contract.setMeter(meter);

        contract.setContractNumber(contractNumber);

        int prepaymentCodeId = codeDao.getCodeIdByCode(Code.PREPAYMENT);
        int emergencyCodeId = codeDao.getCodeIdByCode(Code.EMERGENCY_CREDIT);
        //고객 선불 가젯의 SMS 통보시 필요한 기본 설정 - period : Daily, interval : 1, time : 13시
        if (contract.getCreditType().getId() == prepaymentCodeId || contract.getCreditType().getId() == emergencyCodeId) {
	        if(contract.getNotificationPeriod() == null ) {
	        	contract.setNotificationPeriod(1);
	        }

	        if(contract.getNotificationInterval() == null) {
	        	contract.setNotificationInterval(1);
	        }

	        if(contract.getNotificationTime() == null) {
	        	contract.setNotificationTime(13);
	        }
	        // 선불 시작일시 추가
	        contract.setPrepayStartTime(startDatetime);
        }

        contract.setServiceType2(serviceType2);

        // Contract 의 중복 여부를 체크한다..
        int meterCnt = this.checkContractMeterYn(mdsId);

        // 이미 계약된 미터일 경우.
        if (meterCnt > 0) {
            // 기존 계약을 가지고 온다.
            Contract prevMeterContract = this.getContractByMeterNo(mdsId);
            prevMeterContract.setMeter(null);
            prevMeterContract.setPreMdsId(mdsId);
            dao.update(prevMeterContract);
            dao.flushAndClear();
        }

        // 새로운 계약을 추가.
        Contract newContract = dao.add(contract);

        addContractChangeLog("", newContract.getId(), "id", startDatetime, startDatetime, newContract.getCustomer(), operator, newContract);

		if(newContract.getCurrentArrears() != null) {
			addContractChangeLog("", newContract.getCurrentArrears(), "currentArrears", startDatetime, startDatetime, newContract.getCustomer(), operator, newContract);
		}
		
        try {
        	//분할납부이 경우 적용
        	//담당자가 고객의 Arrears를 등록했을 때 초기 미수금(initArrears)보다 큰 미수금이면 고객에게 SMS 메세지를 전송한다.
	    	if(isPartpayment && (contract.getCreditType().getId() == prepaymentCodeId || contract.getCreditType().getId() == emergencyCodeId) &&
	    			(!"".equals(initArrears) && (newContract.getCurrentArrears() > Double.parseDouble(initArrears)))) {
	    		
	    		if(newContract.getArrearsContractCount() != null) {
	    			addContractChangeLog("", newContract.getArrearsContractCount(), "arrearsContractCount", startDatetime, startDatetime, newContract.getCustomer(), operator, newContract);
	    		}
	    		
				Supplier supplier = supplierDao.getSupplierById(newContract.getSupplier().getId());
		        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
		        
				Properties messageProp = new Properties();
				Properties prop = new Properties();
				
		        String lang = supplier.getLang().getCode_2letter();
		        InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
		        if(ip == null){
			        	ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
		        }
		        messageProp.load(ip);
		        
	    		prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
	    		logger.info("prop load : "+prop.containsKey("smsClassPath"));
		        
				String text =  messageProp.getProperty("aimir.alert.arrears").replace("$SUPPLIER", supplier.getName())
						.replace("$AMOUNT", cdf.format(newContract.getCurrentArrears()) + messageProp.getProperty("aimir.price.unit"));
		        
				this.SMSNotification(newContract, text);
	    		
				logger.info("FINISHED SMS");
	    	}
        } catch (Exception e) {
        	logger.error(e,e);
		}
		
        return newContract;
    }

    /**
     * method name : modifyContract<b/>
     * method Desc :
     *
     * @param conditionMap
     */
    @Override
	public void modifyContract(Map<String, Object> conditionMap) {
        Contract contract = (Contract)conditionMap.get("contract");
        Integer sicId = (Integer)conditionMap.get("sicId");
        Integer locationId2 = (Integer)conditionMap.get("locationId2");
        Integer customerId = (Integer)conditionMap.get("customerId");
        Integer prevContractId = (Integer)conditionMap.get("prevContractId");
        Integer operatorId = (Integer)conditionMap.get("operatorId");
        String serviceType2 = StringUtil.nullToBlank(conditionMap.get("serviceType2"));
        String mdsId = StringUtil.nullToBlank(conditionMap.get("mdsId"));
        String startDatetime = StringUtil.nullToBlank(conditionMap.get("startDatetime"));
        String writeDatetime = StringUtil.nullToBlank(conditionMap.get("writeDatetime"));
        String barcode = StringUtil.nullToBlank(conditionMap.get("barcode"));
        Boolean isPartpayment = (Boolean)conditionMap.get("isPartpayment");
        Double initArrears = (Double)conditionMap.get("initArrears");

        //세션에서 받을 operator id
        Operator operator = operatorDao.get(operatorId);
        //id 로 저장되어 있는 값 db 에서 가져오기 : beforeValue
        Contract beforeContract = null;     // 이전 Contract 정보 (동일 contract id)
        Contract prevContract = null;       // Contract 를 변경한 경우 이전 Contract 정보 (다른 contract id)

        beforeContract = dao.get(contract.getId());

        Customer customer = customerDao.get(customerId);
        Customer prevCustomer = null;       // Contract 를 변경한 경우 이전 Customer 정보
        boolean changeContract = (prevContractId != null && !prevContractId.equals(contract.getId()));

        if (changeContract) {
            prevContract = dao.get(prevContractId);
            prevCustomer = prevContract.getCustomer();
        }

        contract.setCustomer(customer);

        Meter meter = meterDao.get(mdsId);
        contract.setMeter(meter);

        //서비스타입 변경 시 ContractChangeLog 저장
        Integer beforeServiceTypeId = (beforeContract.getServiceTypeCode() != null) ? beforeContract.getServiceTypeCode().getId() : null;

        if ((beforeServiceTypeId == null && contract.getServiceTypeCode() != null) || (beforeServiceTypeId != null && contract.getServiceTypeCode() == null)
                || (beforeServiceTypeId != null && contract.getServiceTypeCode() != null && !beforeServiceTypeId.equals(contract.getServiceTypeCode().getId()))) {
            addContractChangeLog(beforeServiceTypeId, ((contract.getServiceTypeCode() != null) ? contract.getServiceTypeCode()
                    .getId() : null), "serviceType", startDatetime, writeDatetime, customer, operator, contract);
        }

        // 공급지역 변경 시
        Integer beforeLocationId = (beforeContract.getLocation() != null) ? beforeContract.getLocation().getId() : null;

        if ((beforeLocationId == null && locationId2 != null) || (beforeLocationId != null && locationId2 == null)
                || (beforeLocationId != null && locationId2 != null && !beforeLocationId.equals(locationId2))) {
            addContractChangeLog(beforeLocationId, locationId2, "location", startDatetime, writeDatetime, customer, operator, contract);
        }

        // 계약종별 변경 시
        Integer beforeTariffIndex = (beforeContract.getTariffIndex() != null) ? beforeContract.getTariffIndex().getId() : null;

        if ((beforeTariffIndex == null && contract.getTariffIndex() != null) || (beforeTariffIndex != null && contract.getTariffIndex() == null)
                || (beforeTariffIndex != null && contract.getTariffIndex() != null && !beforeTariffIndex.equals(contract.getTariffIndex().getId()))) {
            addContractChangeLog(beforeTariffIndex, ((contract.getTariffIndex() != null) ? contract.getTariffIndex().getId()
                    : null), "tariffIndex", startDatetime, writeDatetime, customer, operator, contract);
        }

        // 계약용량 변경 시
        Double beforeContractDemand = (beforeContract.getContractDemand() != null) ? beforeContract.getContractDemand() : null;

        if ((beforeContractDemand == null && contract.getContractDemand() != null) || (beforeContractDemand != null && contract.getContractDemand() == null)
                || (beforeContractDemand != null && contract.getContractDemand() != null && !beforeContractDemand.equals(contract.getContractDemand()))) {
            addContractChangeLog(beforeContractDemand, contract.getContractDemand(), "contractDemand", startDatetime, writeDatetime, customer, operator, contract);
        }

        // 공급상태 변경 시
        Integer beforeStatus = (beforeContract.getStatus() != null) ? beforeContract.getStatus().getId() : null;

        if ((beforeStatus == null && contract.getStatus() != null) || (beforeStatus != null && contract.getStatus() == null)
                || (beforeStatus != null && contract.getStatus() != null && !beforeStatus.equals(contract.getStatus().getId()))) {
            addContractChangeLog(beforeStatus, ((contract.getStatus() != null) ? contract.getStatus().getId() : null),
                    "status", startDatetime, writeDatetime, customer, operator, contract);
        }

        // 지불타입 변경 시
        Integer beforeCreditType = (beforeContract.getCreditType() != null) ? beforeContract.getCreditType().getId() : null;

        if ((beforeCreditType == null && contract.getCreditType() != null) || (beforeCreditType != null && contract.getCreditType() == null)
                || (beforeCreditType != null && contract.getCreditType() != null && !beforeCreditType.equals(contract.getCreditType().getId()))) {
            addContractChangeLog(beforeCreditType, ((contract.getCreditType() != null) ? contract.getCreditType().getId() : null), "creditType", startDatetime, writeDatetime, customer, operator, contract);
        }

        // Contract Number 변경 시
        if (changeContract) {
            addContractChangeLog(prevCustomer.getId(), null, "customer", startDatetime, writeDatetime, prevCustomer, operator, prevContract);

            Contract updPrevContract = dao.get(prevContractId);
            updPrevContract.setCustomer(null);
            dao.update(updPrevContract);
            dao.flushAndClear();
        }

        // Customer 변경 시
        if (beforeContract.getCustomer() != null) {
            Integer beforeCustomerId = beforeContract.getCustomerId();

            if (!customerId.equals(beforeCustomerId)) {
                addContractChangeLog(beforeCustomerId, customerId, "customer", startDatetime, writeDatetime, customer, operator, contract);
            }
        } else {
            addContractChangeLog(null, customerId, "customer", startDatetime, writeDatetime, customer, operator, contract);
        }

        // Meter 변경 시
        Integer prevMeterId = beforeContract.getMeterId();

        if ((prevMeterId == null && contract.getMeter() != null) || (prevMeterId != null && contract.getMeter() == null)
                || (prevMeterId != null && contract.getMeter() != null && !prevMeterId.equals(contract.getMeter().getId()))) {
            addContractChangeLog(prevMeterId, ((contract.getMeter() != null) ? contract.getMeter().getId() : null), "meter", startDatetime, writeDatetime, customer, operator, contract);

            if (contract.getMeter() != null) {
                // Contract 의 중복 여부를 체크한다..
                int meterCnt = this.checkContractMeterYn(mdsId);

                // 이미 계약된 미터일 경우.기존 계약의 미터를 null 로 처리
                if (meterCnt > 0) {
                    // 미터와 연결된 기존 계약을 가지고 온다.
                    Contract prevMeterContract = this.getContractByMeterNo(mdsId);
                    prevMeterContract.setMeter(null);
                    prevMeterContract.setPreMdsId(mdsId);
                    dao.update(prevMeterContract);
                    dao.flushAndClear();
                }
            }
        }

        Code sicCode = null;

        if (sicId != null) {
            sicCode = codeDao.get(sicId);
        }
        contract.setSic(sicCode);

        // SIC 변경 시
        Integer beforeSicId = (beforeContract.getSic() != null) ? beforeContract.getSicCodeId() : null;

        if ((beforeSicId == null && sicId != null) || (beforeSicId != null && sicId == null)
                || (beforeSicId != null && sicId != null && !beforeSicId.equals(sicId))) {
            addContractChangeLog(beforeSicId, sicId, "sic", startDatetime, writeDatetime, customer, operator, contract);
        }

//        if (contract.getCreditType() != null && contract.getCreditType().getCode().equals("2.2.1")) {
//            // 지불상태 변경 시
//            Integer beforeCreditStatus = (beforeContract.getCreditStatus() != null) ? beforeContract.getCreditStatus().getId() : null;
//
//            if ((beforeCreditStatus == null && contract.getCreditStatus() != null) || (beforeCreditStatus != null && contract.getCreditStatus() == null)
//                    || (beforeCreditStatus != null && contract.getCreditStatus() != null && !beforeCreditStatus.equals(contract.getCreditStatus().getId()))) {
//                addContractChangeLog(beforeCreditStatus, ((contract.getCreditStatus() != null) ? contract.getCreditStatus().getId() : null), "creditStatus", startDatetime, writeDatetime, customer, operator, contract);
//            }
//
//            // prepaymentThreshold 변경 시
//            Integer prevPrepaymentThreshold = (beforeContract.getPrepaymentThreshold() != null) ? beforeContract.getPrepaymentThreshold() : null;
//
//            if ((prevPrepaymentThreshold == null && contract.getPrepaymentThreshold() != null) || (prevPrepaymentThreshold != null && contract.getPrepaymentThreshold() == null)
//                    || (prevPrepaymentThreshold != null && contract.getPrepaymentThreshold() != null && !prevPrepaymentThreshold.equals(contract.getPrepaymentThreshold()))) {
//                addContractChangeLog(prevPrepaymentThreshold, contract.getPrepaymentThreshold(), "prepaymentThreshold", startDatetime, writeDatetime, customer, operator, contract);
//            }
//        }

        // Credit Type 이 prepay 인 경우
        if (contract.getCreditType() != null) {
            Code tmpCode = codeDao.get(contract.getCreditType().getId());
            contract.setCreditType(tmpCode);

            if (tmpCode.getCode().equals(Code.PREPAYMENT)) {    // prepay 인 경우
                // 지불상태 변경 시
                Integer beforeCreditStatus = (beforeContract.getCreditStatus() != null) ? beforeContract.getCreditStatus().getId() : null;

                if ((beforeCreditStatus == null && contract.getCreditStatus() != null) || (beforeCreditStatus != null && contract.getCreditStatus() == null)
                        || (beforeCreditStatus != null && contract.getCreditStatus() != null && !beforeCreditStatus.equals(contract.getCreditStatus().getId()))) {
                    addContractChangeLog(beforeCreditStatus, ((contract.getCreditStatus() != null) ? contract.getCreditStatus().getId() : null), "creditStatus", startDatetime, writeDatetime, customer, operator, contract);
                }

                // prepaymentThreshold 변경 시
                Integer prevPrepaymentThreshold = (beforeContract.getPrepaymentThreshold() != null) ? beforeContract.getPrepaymentThreshold() : null;

                if ((prevPrepaymentThreshold == null && contract.getPrepaymentThreshold() != null) || (prevPrepaymentThreshold != null && contract.getPrepaymentThreshold() == null)
                        || (prevPrepaymentThreshold != null && contract.getPrepaymentThreshold() != null && !prevPrepaymentThreshold.equals(contract.getPrepaymentThreshold()))) {
                    addContractChangeLog(prevPrepaymentThreshold, contract.getPrepaymentThreshold(), "prepaymentThreshold", startDatetime, writeDatetime, customer, operator, contract);
                }
            }
        }

        // Update Contract
        Contract contract1 = dao.get(contract.getId());
        Double preContractArrears = contract1.getCurrentArrears() == null ? 0 : contract1.getCurrentArrears();

        contract1.setCustomer(contract.getCustomer());
        contract1.setTariffIndex(contract.getTariffIndex());
        contract1.setServiceTypeCode(contract.getServiceTypeCode());

        // location
        if ( locationId2 != null ) {
	        Location tempLocation = locationDao.get(locationId2);
	        contract1.setLocation(tempLocation);
        }
        
        contract1.setPreMdsId(contract.getPreMdsId());
        contract1.setSic(contract.getSic());
        contract1.setMeter(contract.getMeter());
        contract1.setContractDemand(contract.getContractDemand());
        contract1.setStatus(contract.getStatus());
//        contract1.setReceiptNumber(contract.getReceiptNumber());
//        contract1.setAmountPaid(contract.getAmountPaid());
        contract1.setServiceType2(serviceType2);
        contract1.setThreshold1(contract.getThreshold1());
        contract1.setThreshold2(contract.getThreshold2());
        contract1.setThreshold3(contract.getThreshold3());

        int postpaymentCodeId = codeDao.getCodeIdByCode(Code.POSTPAY);
        int prepaymentCodeId = codeDao.getCodeIdByCode(Code.PREPAYMENT);
        int emergencyCodeId = codeDao.getCodeIdByCode(Code.EMERGENCY_CREDIT);

        // credit type 이 prepay 인 경우
//        if (contract.getCreditType() != null && contract.getCreditType().getCode().equals("2.2.1")) {
//            contract1.setCreditStatus(contract.getCreditStatus());
//            contract1.setPrepaymentThreshold(contract.getPrepaymentThreshold());
//            if ( !barcode.isEmpty() ) {
//            	contract1.setBarcode(barcode);
//            }
//        }
        if (contract.getCreditType() != null && contract.getCreditType().getId() == prepaymentCodeId) {
            contract1.setCreditStatus(contract.getCreditStatus());
            contract1.setPrepaymentThreshold(contract.getPrepaymentThreshold());

            if (!barcode.isEmpty()) {
                contract1.setBarcode(barcode);
            }
            
            contract1.setOldArrears(contract.getOldArrears());
            
            contract1.setCurrentArrears(contract.getCurrentArrears());
            addContractChangeLog(preContractArrears, contract.getCurrentArrears(), "currentArrears", startDatetime, writeDatetime, customer, operator, contract);
            
            if(isPartpayment) {
            	Integer arrearsPaymentCount = contract1.getArrearsPaymentCount();
            	Integer tempPaymentCount = arrearsPaymentCount == null ? 0 : arrearsPaymentCount;
            	Integer preContractCount = contract.getArrearsContractCount();
            	contract1.setArrearsPaymentCount(tempPaymentCount);
            	Double preFirstArrears = contract.getFirstArrears();
	            //고객이 미수금을 한번도 지불한 적이 없을경우 업데이트가능
	            if((tempPaymentCount == 0 || tempPaymentCount == null)) {
		            contract1.setArrearsContractCount(contract.getArrearsContractCount());
	            	contract1.setFirstArrears(contract.getCurrentArrears());
	            }
	            addContractChangeLog(preFirstArrears, contract1.getFirstArrears(), "firstArrears", startDatetime, writeDatetime, customer, operator, contract);	            
	            addContractChangeLog(preContractCount, contract.getArrearsContractCount(), "arrearsContractCount", startDatetime, writeDatetime, customer, operator, contract);
	            addContractChangeLog(arrearsPaymentCount, tempPaymentCount, "arrearsPaymentCount", startDatetime, writeDatetime, customer, operator, contract);
            }

            // 후불 -> 선불인 경우 선불 시작일시 저장
            if (contract1.getCreditType() != null && contract1.getCreditType().getId() == postpaymentCodeId) {
                contract1.setPrepayStartTime(startDatetime);
            }

            contract1.setChargeAvailable(contract.getChargeAvailable());
        } else if(contract.getCreditType() != null && contract.getCreditType().getId() == emergencyCodeId){
        	// 후불 -> emergency 인 경우 선불 시작일시 저장
            if (contract1.getCreditType() != null && contract1.getCreditType().getId() == postpaymentCodeId) {
                contract1.setPrepayStartTime(startDatetime);
            }
        }

        contract1.setCreditType(contract.getCreditType());

        dao.update(contract1);
        dao.flushAndClear();
        
        //분할납부일 경우 적용
        //선불고객이면서 고객의 미수금이 입력되었을 때 고객에게 SMS 문자를 전송한다. 이때 미수금은 initial Credit(initArrears)를 제외하고 판단한다
        Double currentArrears = contract.getCurrentArrears() == null ? 0.0 : contract.getCurrentArrears();
        if(isPartpayment && (contract.getCreditType() != null && contract.getCreditType().getId() == prepaymentCodeId) &&
        		!(preContractArrears.equals(contract.getCurrentArrears())) && (currentArrears > initArrears)) {
    		try {
	    		Supplier supplier = supplierDao.getSupplierById(contract1.getSupplierId());
		        DecimalFormat cdf = DecimalUtil.getDecimalFormat(supplier.getCd());
		        
				Properties messageProp = new Properties();
				Properties prop = new Properties();
		    		
		        String lang = supplier.getLang().getCode_2letter();
		        InputStream ip = getClass().getClassLoader().getResourceAsStream("message_"+ lang +".properties");
		        if(ip == null){
		        	ip = getClass().getClassLoader().getResourceAsStream("message_en.properties");	        	
		        }
		        messageProp.load(ip);
		        
	    		prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
	    		logger.info("prop load : "+prop.containsKey("smsClassPath"));
		        
				String text =  messageProp.getProperty("aimir.alert.arrears").replace("$SUPPLIER", supplier.getName())
						.replace("$AMOUNT", cdf.format(contract.getCurrentArrears()) + messageProp.getProperty("aimir.price.unit"));
				
				this.SMSNotification(contract1, text);
				
    		} catch (Exception e) {
    			logger.error(e,e);
			}
    	}
    }

    /**
     * method name : addContractChangeLog<b/>
     * method Desc :
     *
     * @param befValue
     * @param aftValue
     * @param changeField
     * @param startDatetime
     * @param writeDatetime
     * @param customer
     * @param operator
     * @param contract
     */
    private void addContractChangeLog(Object befValue, Object aftValue, String changeField, String startDatetime,
            String writeDatetime, Customer customer, Operator operator, Contract contract) {
        ContractChangeLog contractChangeLog = new ContractChangeLog();
        contractChangeLog.setBeforeValue(befValue == null ? "" : befValue.toString());
        contractChangeLog.setAfterValue(aftValue == null ? "" : aftValue.toString());
        contractChangeLog.setChangeField(changeField);
        contractChangeLog.setStartDatetime(startDatetime);
        contractChangeLog.setWriteDatetime(writeDatetime);
        contractChangeLog.setCustomer(customer);
        contractChangeLog.setOperator(operator);
        contractChangeLog.setContract(contract);

        contractChangeLogDao.add(contractChangeLog);
    }

    /**
     * ServiceType2  조회
     */
	@Override
	public List<Object> getServiceType2() {
		List<Object> returnList = new ArrayList<Object>();

		for (ServiceType2 s : ServiceType2.values()) {
        	Map<String, Object> resultMap = null;
        	resultMap = new HashMap<String, Object>();
        	resultMap.put("id", s.name());
        	resultMap.put("name", s.getName());
        	returnList.add(resultMap);
        }

		return returnList;
	}

	@Override
	public List<Map<String, Object>> getContractByloginId(String loginId, String meterType) {
        return dao.getContractByloginId(loginId, meterType);
    }

    /**
     * method name : mergeExcelContract<b/>
     * method Desc : Contract Bulk 등록에서 Update 시 입력값이 있는 경우 기존값을 Update 한다.
     *
     * @param contract 입력 데이터
     * @param updContract 기존 데이터
     * @return
     */
    private Contract mergeExcelContract(Contract contract, Contract updContract) {
        updContract.setCustomer(contract.getCustomer());
        updContract.setServiceTypeCode(contract.getServiceTypeCode());
        updContract.setStatus(contract.getStatus());
        updContract.setLocation(contract.getLocation());
        updContract.setCreditType(contract.getCreditType());

        if (contract.getSupplier() != null) {
            updContract.setSupplier(contract.getSupplier());
        }

        if (contract.getTariffIndex() != null) {
            updContract.setTariffIndex(contract.getTariffIndex());
        }

        if (contract.getContractDemand() != null) {
            updContract.setContractDemand(contract.getContractDemand());
        }

        if (contract.getCreditStatus() != null) {
            updContract.setCreditStatus(contract.getCreditStatus());
        }

        if (contract.getPrepaymentThreshold() != null) {
            updContract.setPrepaymentThreshold(contract.getPrepaymentThreshold());
        }

        if (contract.getMeter() != null) {
            updContract.setMeter(contract.getMeter());
        }

        if (contract.getSic() != null) {
            updContract.setSic(contract.getSic());
        }

        return updContract;
    }
    
    /**
     * method name : SMSNotification
     * method Desc : 고객에게 SMS 통보
     * @param contract
     * @return String messageId
     */
    private String SMSNotification(Contract contract, String text) {
    	String messageId=null;
    	Properties prop = new Properties();
    	try {
    		prop.load(getClass().getClassLoader().getResourceAsStream("command.properties"));
    		logger.info("prop load : "+prop.containsKey("smsClassPath"));

			Customer customer = customerDao.get(contract.getCustomer().getId());
			Boolean smsYn = customer.getSmsYn() == 1 ? true : false;
			Boolean existMobileNo = true;
			if(customer.getMobileNo() == null || customer.getMobileNo().length() <= 0) {
				existMobileNo = false;
			}
			 
			logger.info("customer's sms setting : " + smsYn + ", customer's mobileNo exist : " + existMobileNo);
			if(smsYn && existMobileNo) {
				String mobileNo = customer.getMobileNo().replace("-", "");
				
				System.out.println("########## txt = "+text+" ##########");
				
				logger.info("START SMS");
				String smsClassPath = prop.getProperty("smsClassPath");
				logger.info("smsClassPath : "+smsClassPath);
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				messageId = (String) m.invoke(obj, mobileNo, text, prop);
				logger.info("FINISHED SMS");
				
			}
    	} catch (Exception e) {
    		logger.error(e,e);
		}
    	
		return messageId;
    }
}