package com.aimir.service.system.prepayment;

import java.util.Map;

/**
 * PrepaymentChargeManager.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2013. 10. 28. v1.0        문동규   Creating Customer Manager Service
 * </pre>
 */
public interface CreatingCustomerMgmtManager {

    /**
     * method name : saveCreatingCustomer<b/>
     * method Desc : Creating Customer Manager 가젯에서 선불고객을 저장한다.
     *
     * @param conditionMap
     * @return
     */
    public void saveCreatingCustomer(Map<String, Object> conditionMap);

    /**
     * method name : saveBulkCreatingCustomerByExcelXLS<b/>
     * method Desc : Creating Customer Manager 가젯 Bulk Tab 에서 선불고객정보를 xls 파일로 받아서 저장한다.
     *
     * @param excel
     * @param supplierId
     * @return
     */
    public Map<String, Object> saveBulkCreatingCustomerByExcelXLS(String excel, Integer supplierId);

    /**
     * method name : saveBulkCreatingCustomerByExcelXLSX<b/>
     * method Desc : Creating Customer Manager 가젯 Bulk Tab 에서 선불고객정보를 xlsx 파일로 받아서 저장한다.
     *
     * @param excel
     * @param supplierId
     * @return
     */
    public Map<String,Object> saveBulkCreatingCustomerByExcelXLSX(String excel, Integer supplierId);

    /**
     * method name : sendCertificationSMS<b/>
     * method Desc : Creating Customer Manager 가젯에서 휴대폰번호를 인증한다.
     *
     * @param conditionMap
     * @return
     */
    public Boolean sendCertificationSMS(Map<String, Object> conditionMap);
}