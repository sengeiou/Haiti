package com.aimir.dao.mvm;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingMonthEM;
import com.aimir.model.mvm.SAP;

public interface SAPDao extends GenericDao<SAP, Integer>{
	
	/**
	 * 미터 정보를 이용해 데이터를 조회한다.
	 * @param meterId
	 * @return
	 */
	public SAP getSAP(String meterSerial);
	
	/**
	 * SAP 에 연관된 BillingMonthEM 정보를 모두 조회한다.<br>
	 * 연관 정보는 Meter 정보를 이용해서 조인된다. 
	 * @param sap_id
	 * @return
	 */
	public List<BillingMonthEM> getBillingMonthEMs(Integer sap_id);
	
//	/**
//	 * SAP 파일 출력을 위해 데이터를 조회하여 SAPWrap 객체로 반환한다.
//	 * @param meterId
//	 * @return
//	 */
//	public SAPWrap getSapWrap(Integer id);
}
