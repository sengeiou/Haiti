package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.PowerOnOffOrder;

public interface PowerOnOffOrderDao extends GenericDao<PowerOnOffOrder, Integer> {

	/**
	 * referenceId를 통해서 PowerOnOffOrder 정보를 가져온다.
	 * 
	 * @param referenceId
	 *            PowerOnOffOrder.referenceId
	 * @return
	 */
	public List<PowerOnOffOrder> getPowerOnOffOrder(Long referenceId);

	/**
	 * PowerOnOffOrder 검색을 한다.
	 * 
	 * @param req PowerOnOffOrder
	 * @param additionalCondition
	 *            추가 검색 조건을 입력한다.
	 * @return
	 */
	public List<PowerOnOffOrder> searchPowerOnOffOrder(
			PowerOnOffOrder req, String additionalCondition);

	/**
	 * PowerOnOffOrder 취소하는 거다. OrderStatus.CANCEL
	 * 
	 * @param req PowerOnOffOrder
	 * @return 1(Ok), 2(Fail), 3(Cannot cancel)
	 */
	public int deletePowerOnOffOrder(PowerOnOffOrder req);

	/**
	 * PowerOnOffOrder 부분 수정.
	 * 
	 * @param odro PowerOnOffOrder
	 * @param fieldName
	 * @param fieldValue
	 * @return count
	 */
	public int updatePowerOnOffOrder(PowerOnOffOrder odro,
			String[] fieldName, Object[] fieldValue);

	/**
	 * 조건절 쿼리를 입력받아 처리 (where 제외)
	 * @param condition where 안에 들어갈 sql query
	 * @return
	 */
	public List<PowerOnOffOrder> listPowerOnOffOrder(String condition);
}
