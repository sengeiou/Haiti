package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.device.Meter;
import com.aimir.model.system.OnDemandReadingOrder;

public interface OnDemandReadingOrderDao extends
		GenericDao<OnDemandReadingOrder, Integer> {

	/**
	 * referenceId를 통해서 OnDemandReadingOrder 정보를 가져온다.
	 * 
	 * @param referenceId
	 *            OnDemandReadingOrder.referenceId
	 * @return
	 */
	public List<OnDemandReadingOrder> getOnDemandReadingOrder(Long referenceId);

	/**
	 * OnDemandReadingOrder 검색을 한다.
	 * 
	 * @param req OnDemandReadingOrder
	 * @param additionalCondition
	 *            추가 검색 조건을 입력한다.
	 * @return
	 */
	public List<OnDemandReadingOrder> searchOnDemandReadingOrder(
			OnDemandReadingOrder req, String additionalCondition);

	/**
	 * OnDemandReadingOrder 취소하는 거다. OrderStatus.CANCEL
	 * 
	 * @param req OnDemandReadingOrder
	 * @return 1(Ok), 2(Fail), 3(Cannot cancel)
	 */
	public int deleteOnDemandReadingOrder(OnDemandReadingOrder req);

	/**
	 * OnDemandReadingOrder 부분 수정.
	 * 
	 * @param odro OnDemandReadingOrder
	 * @param fieldName
	 * @param fieldValue
	 * @return count
	 */
	public int updateOnDemandReadingOrder(OnDemandReadingOrder odro,
			String[] fieldName, Object[] fieldValue);

	/**
	 * 조건절 쿼리를 입력받아 처리 (where 제외)
	 * @param condition where 안에 들어갈 sql query
	 * @return
	 */
	public List<OnDemandReadingOrder> listOnDemandReadingOrder(String condition);

	/**
	 * 검침값 가져오기 
	 * @param meter
	 * @param meterValueDate
	 * @return
	 */
    public Map<String, Double> getHistoricalMeteringData(Meter meter, String meterValueDate);
}