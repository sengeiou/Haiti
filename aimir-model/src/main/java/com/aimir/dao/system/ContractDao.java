/**
 * ContractDao.java Copyright NuriTelecom Limited 2011
 */

package com.aimir.dao.system;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Contract;
import com.aimir.util.Condition;

/**
 * ContractDao.java Description
 *
 *
 * Date          Version     Author   Description
 * 2011. 4. 11.   v1.0       김상연         Contract List 조회 (조건별)
 *
 */
/**
 * @author MieUn
 *
 */
public interface ContractDao extends GenericDao<Contract, Integer>{

    /**
     * method name : getMyEnergy
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @param customerId Contract.customer.id
     * @param serviceTypeId energy service type code id
     * @return Array of {Contract.id, Contract.contractNumber}
     */
    public List<Object> getMyEnergy(int customerId, int serviceTypeId);

    /**
     * method name : getContractIdByCustomerNo
     * method Desc : customerNo에 속한 Contract의 ID 목록을 리턴한다.
     *
     * @param customerNo Array of {Contract.customer.customerNo }
     * @return Array of  {Contract.id}
     */
    public List<Object> getContractIdByCustomerNo(String[] customerNo);

    /**
     * method name : getContractIdByContractNo
     * method Desc : contractNo에 속한 Contract의 목록을 리턴한다.
     *
     * @param contractNo Contract.contractNo
     * @return Array of {Contract} @see com.aimir.model.system.Contract
     */
    public List<Object> getContractIdByContractNo(String contractNo);
    
    /**
     * method name : getContractForSAWSPOS
     * method Desc : Spasa POS 웹서비스 연계시 사용
     *
     * @param condition
     */
    public List<Contract> getContractForSAWSPOS(Map<String, Object> condition);

    /**
     * method name : getContractIdByContractNoLike
     * method Desc : contractNo의 like 조건에 해당하는 Contract List를 리턴한다.
     *
     * @param contractNo Contract.contractNo
     * @return Array of {Contract} @see com.aimir.model.system.Contract
     */
    public List<Object> getContractIdByContractNoLike(String contractNo);

    /**
     * method name : getContractIdByCustomerName
     * method Desc : customerName의 like 조건에 해당하는 Contract ID 목록을 리턴한다.
     *
     * @param customerName Contract.customer.name
     * @return Array of  {Contract.id}
     */
    public List<Object> getContractIdByCustomerName(String customerName);

    /**
     * method name : getContractIdByGroup
     * method Desc : AIMIRGroup에서 Contract 타입에 해당하는 그룹의 멤버를 조회한다.
     *
     * @param group Group.id
     * @return Array of  {Contract.id}
     */
    public List<Object> getContractIdByGroup(String group);

    /**
     * method name : getContractIdByTariffIndex
     * method Desc : 계약종별에 조건에 해당하는 Contract 객체의 id 목록을 리턴한다.
     *
     * @param tariffIndex Contract.tariffIndex.id
     * @return Array of  {Contract.id}
     */
    public List<Object> getContractIdByTariffIndex(int tariffIndex);

    /**
     * method name : getContractIdWithCustomerName
     * method Desc : 계약종별에 조건에 해당하는  계약번호 ContractNumber와 고객명의 목록을 리턴한다.
     *
     * @param contractNo Array of  {Contract.contractNo}
     * @return Array of {Contract.contractNumber, Contract.customer.name}
     */
    public List<Object> getContractIdWithCustomerName(String[] contractNo);

    /**
     * method name : getContractByListCondition
     * method Desc : 여러 조건에 해당하는 Contract List를 리턴한다.
     *
     * @param set
     * @return  Array of {Contract} @see com.aimir.model.system.Contract
     */
    public List<Contract> getContractByListCondition(Set<Condition> set);

    /**
     * method name : getContractCountByStatusCode
     * method Desc : 오늘 날짜에 신규 추가된 공급사 아이디에 해당하는 계약 관련 정보 리턴
     *
     * @param params
     *              today (yyyyMMddHHmmss) Contract.contractDate
     *              serviceType (energy service type) Contract.serviceTypeCode.code
     *              supplierId (Supplier.id)
     * {@code}
     *              String today       = (String)params.get("today");
     *              String serviceType = (String)params.get("serviceType");
     *              int supplierId     = (Integer)params.get("supplierId");
     *
     * @return Array of {Contract.status.code as statusCode,
     *                   Contract.status.name as statusName,
     *                  COUNT(*) as statusCount }
     */
    public List<Object> getContractCountByStatusCode(Map<String,Object> params);

    /**
     * method name : getContractCountForToday
     * method Desc : 오늘 날짜에 신규 추가된 공급사 아이디에 해당하는 계약 관련 정보 카운트 리턴
     *
     * @param params
     *              today (yyyyMMddHHmmss) Contract.contractDate
     *              serviceType (energy service type) Contract.serviceTypeCode.code
     *              supplierId (Supplier.id)
     * {@code}
     *              String today       = (String)params.get("today");
     *              String serviceType = (String)params.get("serviceType");
     *              int supplierId     = (Integer)params.get("supplierId");
     *
     * @return Array of {Contract.status.code as statusCode,
     *                   Contract.status.name as statusName,
     *                  COUNT(*) as statusCount }
     */
    public List<Object> getContractCountForToday(Map<String,Object> params);

    /**
     * method name : getContractCountByTariffType
     * method Desc : 계약날짜와 상태 타입, 서비스타입, 공급사 아이디로 계약 종류별 카운트 리턴
     *
     * @param params
     *              today (yyyyMMddHHmmss) Contract.contractDate
     *              type Contract.status.code
     *              serviceType (energy service type) Contract.serviceTypeCode.code
     *              supplierId (Supplier.id)
     * {@code}
     *         String today       = (String)params.get("today");
     *         String type        = (String)params.get("type");
     *         String serviceType = (String)params.get("serviceType");
     *         int supplierId     = (Integer)params.get("supplierId");
     * @return Array of { TariffType.name as tariffType, COUNT(*) as tariffCount}
     */
    public List<Object> getContractCountByTariffType(Map<String,Object> params);
    
    public Map<String,Object> getPartpayInfoByContractNumber(String contractNumber, Integer supplierId);

    /**
     * method name : numberOverlapCheck
     * method Desc : 계약번호가 Contract 정보에 중북되는지 확인
     *
     * @param contractNumber Contract.contractNumber
     * @return 중복되는 값이 없으면 0이 리턴된다.
     */
    public int numberOverlapCheck(String contractNumber);
    
    /**
     * method name : meterOverlapCheck<b/>
     * method Desc : 미터가 계약된 상태인지를 판별
     * 
     * @param Integer
     * @return Integer
     */
    public int meterOverlapCheck(Integer meterId);

    /**
     * method name : contractAllDelete
     * method Desc : Customer.id에 해당하는 Contract 정보가 모두 삭제 된다.
     *
     * @param id Contract.customer.id
     */
    public void contractAllDelete(Integer id);

    /**
     * method name : getAllCustomerTabData
     * method Desc : 조회조건에 해당하는 고객 및 계약정보 데이터 목록
     *
     *
     * @param conditionMap
     * <ul>
     * <li> customerNo : Customer.customerNo - customer no
     * <li> customerName : Customer.name - customer name
     * <li> mdsId : Meter.mdsId - mds id
     * <li> customType : Contract.sic.id - sic id
     * <li> address : Customer.address - address
     * <li> serviceType : Contract.serviceTypeCode.id - service type
     * <li> supplierId : Supplier.id - supplier id
     * <li> page : String - page number
     * <li> pageSize : String - row size per page
     * <li> locationIdList : List<Integer> - location id list
     * </ul>
     *
     * @return List of Array {
     *                        Customer.id,
     *                        Customer.name,
     *                        Customer.address,
     *                        Contract.id,
     *                        Contract.contractNumber,
     *                        Location.name,
     *                        Contract.serviceTypeCode.name,
     *                        Meter.mdsId,
     *                        Contract.sic.name,
     *                        Contract.serviceTypeCode.id,
     *                        Customer.customerNo
     *                       }
     */
    @Deprecated
    public List<Object[]> getAllCustomerTabData(Map<String, Object> conditionMap);

    /**
     * method name : getTotalContractCount<b/>
     * method Desc : Customer Contract 맥스가젯에서 전체 계약 개수를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getTotalContractCount(Map<String, Object> conditionMap);

    /**
     * method name : getAllCustomerTabData
     * method Desc : 조회조건에 해당하는 고객 및 계약정보 데이터 목록
     *
     *
     * @param conditionMap
     * <ul>
     * <li> customerNo : Customer.customerNo - customer no
     * <li> customerName : Customer.name - customer name
     * <li> mdsId : Meter.mdsId - mds id
     * <li> customType : Contract.sic.id - sic id
     * <li> address : Customer.address - address
     * <li> serviceType : Contract.serviceTypeCode.id - service type
     * <li> supplierId : Supplier.id - supplier id
     * <li> page : String - page number
     * <li> pageSize : String - row size per page
     * <li> locationIdList : List<Integer> - location id list
     * </ul>
     * @param isCount
     *
     * @return List of Map {
     *                        Customer.id,
     *                        Customer.name,
     *                        Customer.address,
     *                        Customer.customerNo
     *                       }
     */
    public List<Map<String, Object>> getAllCustomerTabDataTree(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getContractListByCustomer<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractListByCustomer(Map<String, Object> conditionMap);

    /**
     * method name : getAllCustomerTabDataCount
     * method Desc : 조회조건에 해당하는 고객 및 계약정보 데이터 목록에 대한 카운트 @see getAllCustomerTabData
     *
     * @param conditionMap
     * <ul>
     * <li> customerNo : Customer.customerNo - customer no
     * <li> customerName : Customer.name - customer name
     * <li> mdsId : Contract.meter.mdsId - mds id
     * <li> customType : Contract.sic.id - sic id
     * <li> address : Customer.address - address
     * <li> serviceType : Contract.serviceTypeCode.id - service type
     * <li> supplierId : Supplier.id - supplier id
     * <li> page : String - page number
     * <li> pageSize : String - row size per page
     * <li> locationIdList : List<Integer> - location id list
     * </ul>
     *
     * @return count : String - list count
     */
    @Deprecated
    public String getAllCustomerTabDataCount(Map<String, Object> conditionMap);

    /**
     * method name : getContractInfo
     * method Desc : 계약정보 아이디로 계약정보 조회
     *
     * @param contractId Contract.id
     * @return Map<String,Object>
     */
    public Map<String, Object> getContractInfo(int contractId);

    /**
     * method name : getEMCustomerTabData
     * method Desc : 계약정보 아이디로 계약정보 조회 서비스 타입이 전기인 고객
     *
     * @param conditionMap
     * <ul>
     * <li> customerNo : Customer.customerNo - customer no
     * <li> customerName : Customer.name - customer name
     * <li> tariffIndex : TariffType.id - tariff type id
     * <li> contractDemand : Contract.contractDemand - contract demand
     * <li> creditType : Contract.creditType.id - credit type id
     * <li> mdsId : Meter.mdsId - mds id
     * <li> status : Contract.status.id - status id
     * <li> dr : Customer.demandResponse - demand response
     * <li> customType : Contract.sic.id - sic id
     * <li> startDate : String - start date (yyyyMMdd)
     * <li> endDate : String - end date (yyyyMMdd)
     * <li> serviceTypeTab : String - meter type
     * <li> supplierId : Supplier.id - supplier id
     * <li> page : String - page number
     * <li> pageSize : String - row size per page
     * <li> locationIdList : List<Integer> - location id list
     * </ul>
     *
     * @return List of Map {
     *                      CONTRACT_NUMBER : Contract.contractNumber - contract number
     *                      CUSTNAME : Customer.name - customer name
     *                      LOCNAME : Location.name - location name
     *                      TARIFFNAME : TariffType.name - tariff name
     *                      CONTRACTDEMAND : Contract.contractDemand - contract demand
     *                      CREDITTYPENAME : Contract.creditType.name - credit type name
     *                      MDS_ID : Meter.mdsId - mds id
     *                      STATUSNAME : Contract.status.name - contract status name
     *                      DEMANDRESPONSE : Customer.demandResponse - demand response y/n
     *                      SICNAME : Contract.sic.name - sic name
     *                      EMAIL : Customer.email - email address
     *                      TELEPHONENO : Customer.telephoneNo - telephone number
     *                      MOBILENO : Customer.mobileNo - mobile number
     *                      CUSTOMERID : Customer.id - customer id
     *                      CONTRACTID : Contract.id - contract id
     *                      SERVICETYPE : Contract.serviceType.id - service type id
     *                      CUSTOMERNO : Customer.customerNo - customer no
     *                     }
     */
    public List<Map<String, Object>> getEMCustomerTabData(Map<String, Object> conditionMap);

    /**
     * @desc : all meter list fetch dao interface
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public List getMeterList(Map<String, String> conditionMap);

    /**
     * @desc all meter list tot count fetch dao interface
     * @return
     */
    @Deprecated
    public String getMeterListDataCount(Map<String, String> conditionMap);

    /**
     * method name : getMeterGridList<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getMeterGridList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getEMCustomerTabDataCount
     * method Desc : 계약정보 아이디로 계약정보 조회 서비스 타입이 전기인 고객 의 총 카운트
     *
     * @param conditionMap
     * <ul>
     * <li> customerNo : Customer.customerNo - customer no
     * <li> customerName : Customer.name - customer name
     * <li> tariffIndex : TariffType.id - tariff type id
     * <li> contractDemand : Contract.contractDemand - contract demand
     * <li> creditType : Contract.creditType.id - credit type id
     * <li> mdsId : Meter.mdsId - mds id
     * <li> status : Contract.status.id - status id
     * <li> dr : Customer.demandResponse - demand response
     * <li> customType : Contract.sic.id - sic id
     * <li> startDate : String - start date (yyyyMMdd)
     * <li> endDate : String - end date (yyyyMMdd)
     * <li> serviceTypeTab : String - meter type
     * <li> supplierId : Supplier.id - supplier id
     * <li> page : String - page number
     * <li> pageSize : String - page size
     * <li> locationIdList : List<Integer> - location id list
     * </ul>
     *
     * @return count - list count
     */
    public String getEMCustomerTabDataCount(Map<String, Object> conditionMap);

    /**
     * method name : getContractByCustomerId
     * method Desc : 고객 정보의 아이디로 계약정보 목록을 리턴
     *
     * @param customerId Customer.id
     * @return @see com.aimir.model.system.Contract
     */
    public List<Contract> getContractByCustomerId(int customerId);

    /**
     * method name : getContractByMeterId
     * method Desc : 미터 정보 아이디로 계약정보 목록을 리턴
     *
     * @param meterId Meter.id
     * @return @see com.aimir.model.system.Contract
     */
    public List<Contract> getContractByMeterId(int meterId);

    /**
     * method name : getContractListByMeter
     * method Desc : 미터 정보 조건으로 계약 관련 정보들을 리턴
     *
     * @param conditionMap
     * {@code}
     *         String customerNo    = StringUtil.nullToBlank(conditionMap.get("customerNo"));
     *         String customerName  = StringUtil.nullToBlank(conditionMap.get("customerName"));
     *         String locationId    = StringUtil.nullToBlank(conditionMap.get("location"));
     *         String mdsId         = StringUtil.nullToBlank(conditionMap.get("mdsId"));
     *         String customType    = StringUtil.nullToBlank(conditionMap.get("customerType"));
     *         String address       = StringUtil.nullToBlank(conditionMap.get("address"));
     *         String serviceType   = StringUtil.nullToBlank(conditionMap.get("serviceType"));
     *         String supplierId    = StringUtil.nullToBlank(conditionMap.get("supplierId"));
     *
     * @return List of {cust.ID,
     *                  cust.NAME,
     *                  cust.ADDRESS,
     *                  c.CONTID,
     *                  c.CONTRACT_NUMBER,
     *                  c.LOCNAME,
     *                  c.SERVICETYPENAME,
     *                  c.MDS_ID,
     *                  cd2.NAME as SICNAME,
     *                  c.SERVICETYPE_ID,
     *                  cust.CUSTOMERNO,
     *                  c.CHECKED}
     */
    public List<Object[]> getContractListByMeter(Map<String, String> conditionMap);

    /**
     * method name : getSupplyCapacity
     * method Desc : 미터 정보 조건으로 계약 관련 정보들을 리턴
     *
     * @param paramMap
     * {@code}
     *         int supplierId = Integer.parseInt(paramMap.get("supplierId"));
     *         String serviceTypeCode = paramMap.get("serviceType");
     *         int page = Integer.parseInt(paramMap.get("page"));
     *         int pageSize = Integer.parseInt(paramMap.get("pageSize"));
     *
     * @return List of Map
     *      {false' as checked,
     *       Contract.customer.name as name,
     *       Contract.contractNumber as contractNumber,
     *       '0' as status,
     *       Contract.id}
     */
    public List<Map<String, String>> getSupplyCapacity(Map<String, String> paramMap);

    /**
     * method name : getGroupMember
     * method Desc : 그룹 관리 중 멤버 리스트 조회
     *
     * @param condition Supplier.id, Contract.contractNumber
     * {@code}
     *      String member = StringUtil.nullToBlank(condition.get("member")); //member is contract's contractNumber
     *      Integer.parseInt((String)condition.get("supplierId"))
     *
     * @return List of Object {Contract.id, Contract.contract_number}
     */
    @Deprecated
    public List<Object> getGroupMember(Map<String, Object> condition);

    /**
     * method name : getMemberSelectData<b/>
     * method Desc : Group Management 가젯에서 Member 로 등록 가능한 Contract 리스트를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Object> getMemberSelectData(Map<String, Object> conditionMap);

    /**
     * method name : getContractList
     * method Desc : Contract List 조회 (조건별)
     *
     * @param contract
     * @return @see com.aimir.model.system.Contract
     */
    public List<Contract> getContractList(Contract contract);

    /**
     * method name : getPrepaymentContract
     * method Desc : 선불 웹서비스에서 조회하는 계약 리스트
     *
     * @param condition
     * {@code}
     *      String contractNumber = (String)conditionMap.get("contractNumber");
     *      String supplierName = (String)conditionMap.get("supplierName");
     *      String mdsId = (String)conditionMap.get("mdsId");
     *      String keyNum = (String)conditionMap.get("keyNum");
     *      Boolean emergencyCreditYn = (Boolean)conditionMap.get("emergencyCreditYn");
     *
     * @return List of @see com.aimir.model.system.Contract
     */
    public List<Contract> getPrepaymentContract(Map<String, Object> condition);

    /**
     * method name : getPrepaymentContractBalanceInfo
     * method Desc : 선불 웹서비스에서 조회하는 현재 잔액정보
     *
     * @param conditionMap
     *         String contractNumber = (String)conditionMap.get("contractNumber");
     *         String supplierName = (String)conditionMap.get("supplierName");
     *         String mdsId = (String)conditionMap.get("mdsId");
     *         String keyNum = (String)conditionMap.get("keyNum");
     *
     * @return List of Map {c.supplier.name AS supplierName,
     *                      c.contractNumber AS contractNumber,
     *                      c.meter.mdsId AS mdsId,
     *                      c.creditType.code AS creditTypeCode,
     *                      c.currentCredit AS currentCredit,
     *                      c.emergencyCreditAvailable AS emergencyYn,
     *                      c.switchStatus}
     *
     */
    public List<Map<String, Object>> getPrepaymentContractBalanceInfo(Map<String, Object> conditionMap);

    /**
     * method name : getBalanceMonitorContract
     * method Desc : 잔액모니터링 스케줄러에서 조회하는 선불계약 리스트
     *
     * @return List of @see com.aimir.model.system.Contract
     */
    public List<Contract> getBalanceMonitorContract();

    /**
     * method name : getEmergencyCreditMonitorContract
     * method Desc : Emergency Credit 모니터링 스케줄러에서 조회하는 Emergency Credit 모드인 계약 리스트
     *
     * @return @see com.aimir.model.system.Contract
     */
    public List<Contract> getEmergencyCreditMonitorContract();

    /**
     * method name : getEmergencyCreditContractList
     * method Desc : 관리자 선불관리 미니가젯의 Emergency Credit Mode 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer page = (Integer)conditionMap.get("page");
     *         Integer limit = (Integer)conditionMap.get("limit");
     *
     * @param isCount total count 여부
     *
     *
     * @return if parameter isCount is true then return count (cnt) only
     *         else List of Map
     *         {c.id AS contractId,
     *          c.contractNumber AS contractNumber,
     *          c.customer.name AS customerName,
     *          c.customer.address2 AS address,
     *          c.lastTokenDate AS lastTokenDate,
     *          c.emergencyCreditStartTime AS emergencyCreditStartTime,
     *          c.emergencyCreditMaxDuration AS emergencyCreditMaxDuration,
     *          c.creditType.code AS creditTypeCode,
     *          c.creditType.name AS creditTypeName,
     *          c.status.code AS statusCode,
     *          c.status.name AS statusName }
     */
    public List<Map<String, Object>> getEmergencyCreditContractList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getPrepaymentContractStatusChartData
     * method Desc : 관리자 선불관리 미니가젯의 선불고객 Pie Chart Data 를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         Integer page = (Integer)conditionMap.get("page");
     *         Integer limit = (Integer)conditionMap.get("limit");
     *
     * @param isCount total count 여부
     *
     * @return if parameter isCount is true then return count (cnt) only
     *         else List of Map
     *         {c.id AS contractId,
     *          c.contractNumber AS contractNumber,
     *          c.customer.name AS customerName,
     *          c.customer.address2 AS address,
     *          c.lastTokenDate AS lastTokenDate,
     *          c.emergencyCreditStartTime AS emergencyCreditStartTime,
     *          c.emergencyCreditMaxDuration AS emergencyCreditMaxDuration,
     *          c.creditType.code AS creditTypeCode,
     *          c.creditType.name AS creditTypeName,
     *          c.status.code AS statusCode,
     *          c.status.name AS statusName }
     */
    public List<Map<String, Object>> getPrepaymentContractStatusChartData(Map<String, Object> conditionMap);

    /**
     * method name : getPrepaymentContractList
     * method Desc : 관리자 선불관리 맥스가젯의 선불계약 고객 리스트를 조회한다.
     *
     * @param conditionMap
     * {@code}
     *         String contractNumber = (String) conditionMap.get("contractNumber");
     *         String customerName = (String) conditionMap.get("customerName");
     *         String statusCode = (String) conditionMap.get("statusCode");
     *         Integer[] locationCondition = (Integer[]) conditionMap.get("locationCondition");
     *         String serviceTypeCode = (String) conditionMap.get("serviceTypeCode");
     *         Integer page = (Integer) conditionMap.get("page");
     *         Integer limit = (Integer) conditionMap.get("limit");
     *
     * @param isCount total count 여부
     *
     * @return if parameter isCount is true then return count (cnt) only
     *         else List of Map
     *         {c.contractNumber AS contractNumber,
     *          c.customer.name AS customerName,
     *          c.customer.address2 AS address,
     *          c.serviceTypeCode.code AS serviceTypeCode,
     *          c.serviceTypeCode.name AS serviceTypeName,
     *          c.creditType.code AS creditTypeCode,
     *          c.creditType.name AS creditTypeName,
     *          c.tariffIndex.name AS tariffTypeName,
     *          c.prepaymentPowerDelay AS prepaymentPowerDelay,
     *          c.lastTokenDate AS lastTokenDate,
     *          c.currentCredit AS currentCredit,
     *          c.status.name AS statusName,
     *          c.emergencyCreditStartTime AS emergencyCreditStartTime,
     *          c.emergencyCreditMaxDuration AS emergencyCreditMaxDuration }
     */
    public List<Map<String, Object>> getPrepaymentContractList(Map<String, Object> conditionMap, boolean isCount);

    /**
     * method name : getContractBySicId
     * method Desc : SIC 산업분류코드로 Contract list 를 조회한다.
     *
     * @param codeId Contract.sic.id - SIC ID
     *
     * @return List of @see com.aimir.model.system.Contract
     */
    public List<Object> getContractBySicId(Integer codeId);

    /**
     * method name : updateSendResult
     * method Desc : contract 정보의 delayday를 업데이트 한다. 전송여부를 체크하기 위함이다.
     *
     * @param contractId Contract.id
     * @param delayDay Contract.delayDay
     */
    @Deprecated
    public void updateSendResult(int contractId, String delayDay);

    /**
     * method name : getDeliveryDelayContratInfo
     * method Desc : contract 정보의 serviceType.id가 일치하는 목록을 조회한다.
     *
     * @param serviceTypeId Contract.serviceType.id
     * @return List of Map
     *              { Contract.id as ID,
     *                Contract.delay_day as DELAYDAY,
     *                Contract.bill_Date as BILLDATE }
     */
    public List<Map<String, Object>> getDeliveryDelayContratInfo(int serviceTypeId);

    /**
     * method name : getContractIdByCustomerNo
     * method Desc : contract 정보의 customerNo  와 공급사 명으로 Contract 정보 조회 목록 리턴
     *
     * @param customerNo Contract.customer.customerNo
     * @param supplierName Contract.supplier.name
     *
     * @return List of @see com.aimir.model.system.Contract
     */
    public List<Contract> getContractIdByCustomerNo(String customerNo, String supplierName);

    /**
     * method name : getPrepaymentContract
     * method Desc : 전체 계약정보중이 prepayment 계약 고객의 정보만 리턴
     *
     * @return List of Map
     *      { c.contractNumber as CONTRACTNUMBER, c.supplier.id as SUPPLIERID }
     */
    public List<Map<String, Object>> getPrepaymentContract();
    
    /**
     * method name : getPrepaymentContract
     * method Desc : 미터와 매핑된 선불 고객 아이디를 가져온다.
     *
     * @return List of Map
     *      { c.id as id }
     */
    public List<Integer> getPrepaymentContract(String serviceType);
    
    public List<Contract> getContract(String payType, String serviceType);

    /**
     * method name : idOverlapCheck
     * method Desc : 계약정보에서 contractNo가 중복인 것을 체크
     *
     * @param contractNo Contract.contractNo
     *
     * @return 일치하지 않으면 0을 리턴
     */
    public int idOverlapCheck(String contractNo);

    /**
     * @desc 계약된 미터 아이디인지 여부를 체크
     * @param meterId
     */
    @Deprecated
    public int checkContractedMeterYn(String meterId);

    /**
     * method name : checkContractMeterYn<b/>
     * method Desc : 계약된 Meter 아이디 인지 여부를 체크한다.
     *
     * @param meterId Meter.mdsId - 미터 아이디
     * @return
     */
    public int checkContractMeterYn(String meterId);

    /**
     * @desc 기존이 계약의 meter_id 값을 null로 처리
     */
    @Deprecated
    public void updateContractByMeterId(String meterId);

    /**
     * method name : getSicContractCountList<b/>
     * method Desc : SIC Load Profile 가젯에서 SIC Code 별 계약 건수를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getSicContractCountList(Map<String, Object> conditionMap);

    /**
     * method name : getCustomerListByType<b/>
     * method Desc :
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getCustomerListByType(Map<String, Object> conditionMap, boolean isCount);


    /**
     * method name : getPrepaymentChargeContractList<b/>
     * method Desc : Prepayment Charge 화면에서 Prepayment Charge Contract List 를 조회한다.
     *
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getPrepaymentChargeContractList(Map<String, Object> conditionMap, boolean isCount);
    
    
    /**
     * method name : getContractSMSYN<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractSMSYN(Map<String, Object> conditionMap);
    
    /**
     * method name : getContractSMSYNWithGroup<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractSMSYNWithGroup(Map<String, Object> conditionMap);
    
    /**
     * method name : getContractSMSYNNOTGroup<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractSMSYNNOTGroup(Map<String, Object> conditionMap);
    
    /**
     * method name : getContractUsageSMSNOTGroup<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractUsageSMSNOTGroup(Map<String, Object> conditionMap);
    
    /**
     * method name : getContractUsageSMSGroup<b/>
     * method Desc : 
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getContractUsageSMSGroup(Map<String, Object> conditionMap);
    
    /**
     * @MethodName getMdsIdFromContractNumber
     * @Date 2013. 9. 17.
     * @param conditionMap(startContract, endContract)
     * @return
     * @Modified
     * @Description
     */
    public List<Map<String, Object>> getMdsIdFromContractNumber(Map<String, Object> conditionMap);

    /**
     * @MethodName getECGContract
     * @Date 2013. 11. 18.
     * @return
     * @Modified
     * @Description TariffName이 "Residential"이고, "Non Residential"인 contract 조회
     */
    public List<Contract> getECGContract();
    
    public List<Contract> getECGContractByNotCalculation(Map<String, Object> conditionMap);
    
    /**
     * @MethodName getECGContract
     * @Date 2013. 11. 20.
     * @param locationId
     * @return
     * @Modified
     * @Description TariffName이 "Residential"이고, "Non Residential"이며 특정한 location에 대한 contract 조회
     */
    public List<Contract> getECGContract(List<Integer> locationId);
    
    /**
     * method name : getContractCount<b/>
     * method Desc : 계약의 개수를 조회
     *
     * @param condition
     * @return
     */
    public Integer getContractCount(Map<String, String> condition);
    
    /**
     * @MethodName updateStatus
     * @Date 2014. 4. 25.
     * @param contract
     * @param code
     * @Modified
     * @Description update시 status만 수정(only column update)
     */
    public void updateStatus(int contractId, Code code);
    
    /**
     * @MethodName updateCreditType
     * @Date 2014. 4. 25.
     * @param contract
     * @param code
     * @return 
     * @Modified
     * @Description update시 creditType만 수정(only column update)
     */
    public void updateCreditType(int contractId, Code code); 
    
    /**
     * @MethodName updateSmsNumber
     * @Date 2014. 4. 25.
     * @param contract
     * @param msg
     * @Modified
     * @Description update시 smsNumber만 수정(only column update)
     */
    public void updateSmsNumber(int contractId, String msg);
    
    /**
     * @MethodName updateCurrentCredit
     * @Date 2015. 4. 1.
     * @param contractId
     * @param current credit
     * @param msg
     * @Modified
     * @Description current credit 수정
     */
    public void updateCurrentCredit(int contractId, double currentCredit);
    
    /**
     * @MethodName updateSMSPriod
     * @Date 2015. 3. 31.
     * @param contract
     * @param msg
     * @param lastNotificationDate
     * @Modified
     * @Description update시 smsNumber만 수정(only column update)
     */
    public void updateSMSPriod(int contractId, String msgId, String lastNotificationDate);
    
    public List<Map<String, Object>> getContractByloginId(String loginId, String meterType);
    
    /**
     * Get prepaid contract list to send SMS
     * @param conditionMap
     * @param isCount
     * @return
     */
    public List<Map<String, Object>> getPrepaidCustomerListForSMS(Map<String, Object> conditionMap);

    /**
     * getRequestDataForUSSDPOS
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getRequestDataForUSSDPOS(Map<String, Object> conditionMap);
    
    /*
     * SMS 전송할 대상 리스트
     */
    public List<Contract> getReqSendSMSList();
    
    /*
     * Emergency 기간이 지난 계약을 선불로 되돌린다.
     */
    public void updateExpiredEmergencyCredit();
}