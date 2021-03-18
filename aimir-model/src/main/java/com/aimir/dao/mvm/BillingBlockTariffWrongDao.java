package com.aimir.dao.mvm;

import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.BillingBlockTariffWrong;

public interface BillingBlockTariffWrongDao extends GenericDao<BillingBlockTariffWrong, Integer>{

	public BillingBlockTariffWrong getBillingBlockTariffWrong(Map<String, Object> condition);
	
	public int updateComplateBillingBlockWrong(String mdevId);
	
	/*
	 * 미터 또는 계약정보가 삭제되거나 미터에 연결된 모뎀이 없는 경우 빌링 오류내용을  완료처리로 변경한다.
	 */
	public Integer udpateBillingFail();
}
