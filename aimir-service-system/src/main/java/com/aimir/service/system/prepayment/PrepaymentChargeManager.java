package com.aimir.service.system.prepayment;

import java.util.List;
import java.util.Map;

import com.aimir.model.system.Contract;

/**
 * PrepaymentChargeManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 2. 15.  v1.0        문동규   Prepayment Charge Service
 * </pre>
 */
public interface PrepaymentChargeManager {

    /**
     * method name : getPrepaymentChargeList<b/>
     * method Desc : Prepayment Charge 화면에서 Prepayment Charge List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getPrepaymentChargeList(Map<String, Object> conditionMap) throws Exception;

    /**
     * method name : getPrepaymentChargeListTotalCount<b/>
     * method Desc : Prepayment Charge 화면에서 Prepayment Charge List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getPrepaymentChargeListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistoryList<b/>
     * method Desc : Prepayment Charge 가젯에서 충전이력 List 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getChargeHistoryList(Map<String, Object> conditionMap);

    /**
     * method name : getChargeHistoryListTotalCount<b/>
     * method Desc : Prepayment Charge 가젯에서 충전이력 List 의 Total Count 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Integer getChargeHistoryListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : getBalanceHistoryList<b/>
     * method Desc : Vending Station 가젯에서 차감에 대한 정보를 볼 수 있도록 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public List<Map<String, Object>> getBalanceHistoryList(Map<String, Object> conditionMap);
    
    /**
     * method name : getBalanceHistoryListTotalCount<b/>
     * method Desc : Vending Station 가젯에서 차감에 대한 정보를 볼 수 있도록 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Object getBalanceHistoryListTotalCount(Map<String, Object> conditionMap);

    /**
     * method name : savePrepaymentCharge<b/>
     * method Desc : Prepayment Charge 화면에서 충전금액을 저장한다.
     *
     * @param conditionMap
     * @return
     */
    public String savePrepaymentCharge(Map<String, Object> conditionMap);

    /**
     * method name : checkChargeAvailable<b/>
     * method Desc : 선불 충전 시 충전가능여부를 체크한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> checkChargeAvailable(Map<String, Object> conditionMap);

    /**
     * @MethodName vendorSavePrepaymentCharge
     * @Date 2013. 7. 4.
     * @param conditionMap
     * @return Map
     * @Modified
     * @Description vendor측에서 선결금 관리
     */
    public Map<String, Object> vendorSavePrepaymentCharge(Map<String, Object> conditionMap);

    /**
     * method name : vendorSavePrepaymentChargeSPASA<b/>
     * method Desc :
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> vendorSavePrepaymentChargeSPASA(Map<String, Object> conditionMap);

    /**
     * method name : getPrepaymentChargeReceiptData<b/>
     * method Desc : Prepayment Charge 영수증 화면에서 충전정보를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getPrepaymentChargeReceiptData(Map<String, Object> conditionMap);

    /**
     * @MethodName getVendorCustomerReceiptData
     * @Date 2013. 7. 19.
     * @param condition
     * @return
     * @Modified
     * @Description : vendor <-> customer 사이에서 발생하는 영수증에 대한 정보를 조회한다.  
     */
    public Map<String, Object> getVendorCustomerReceiptData(Map<String, Object> condition);
    
    /**
     * @MethodName vendorSetContractPrice
     * @Date 2013. 8. 5.
     * @param condition : contractNumber, contractPrice,,,
     * @return
     * @Modified
     * @Description contract 정보에서 미터기 교체로 인한 contractPrice 정보를 입력하도록 수정  
     */
    public String vendorSetContractPrice(Map<String, Object> condition);
    
    /**
     * @MethodName updateBarcode
     * @Date 2013. 10. 11.
     * @param condition
     * @return
     * @Modified
     * @Description prepayment customer에게 barcode 할당 
     */
    public String updateBarcode(Map<String, Object> condition);
    
    /**
     * @MethodName addCasher
     * @Date 2013. 10. 17.
     * @param condition : vendorId, macAddress
     * @return
     * @Modified
     * @Description
     */
    public String addCasher(Map<String, Object> condition);
    
    /**
     * @MethodName deleteCasher
     * @Date 2013. 10. 18.
     * @param condition : id, date
     * @return
     * @Modified
     * @Description
     */
    public String deleteCasher(Map<String, Object> condition);
    
    /**
     * @MethodName cancelTransaction
     * @Date 2014. 2. 5.
     * @param id 로그 아이디
     * @param operatorId 수행자
     * @return
     * @Modified
     * @Description 예치금 결제를 취소한다. 
     */
    public String cancelTransaction(Long id, String operatorId, String reason);

    /**
     * method name : getDepositHistoryList<b/>
     * method Desc : Vendor Prepayment Charge 가젯에서 Charge History 를 조회한다.
     *
     * @param params
     * @return
     */
    public Map<String, Object> getDepositHistoryList(Map<String, Object> params);

    /**
     * @MethodName vendorPrepaymentPayType
     * @Description 지불결제수단 cash or check
     * @return
     */
    public List<Map<String, Object>> vendorPrepaymentPayType();
    
    public void SMSNotification(Contract contract, Double amount, Double preCurrentCredit, Boolean isValid);
    public void SMSNotificationWithText(Contract contract, String text);
    
    /**
     * @MethodName SMSNotificationForECG
     * @Description ECG용 Charging SMS 전송. (ECG에서 요구한 메세지로 적용)
     * 
     * @param contract
     * @param amount
     * @param preCurrentCredit
     * @param isValid
     */
    public void SMSNotificationForECG(Contract contract, Double amount, Double preCurrentCredit, Boolean isValid);
}