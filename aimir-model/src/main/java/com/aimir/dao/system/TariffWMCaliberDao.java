package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.TariffWMCaliber;

public interface TariffWMCaliberDao extends GenericDao<TariffWMCaliber, Integer> {

	/**
     * method name : getChargeMgmtList
     * method Desc : 조회조건에 해당하는 TariffWMCaliber 목록을 리턴한다.
     * 
	 * @param condition
	 * <ul>
	 * <li> supplierId : Supplier.id
	 * <li> yyyymmdd : yyyyMMdd
	 * </ul>
	 * 
	 * @return List Of Map<String Object>
	 */
	public List<Map<String, Object>> getChargeMgmtList(Map<String, Object> condition);
	
	/**
     * method name : updateData
     * method Desc : TariffWMCaliber 객체를 업데이트한다.
     * 
	 * @param tariff TariffWMCaliber 객체
	 * @return
	 * @throws Exception
	 */
	public int updateData(TariffWMCaliber tariff) throws Exception;
	
	/**
     * method name : getTariffWMCaliberByCaliber
     * method Desc : 조회조건에 해당하는 TariffWMCaliber 객체를 리턴한다.
     * 
	 * @param params
	 * <ul>
	 * <li> caliber : caliber
	 * <li> supplierId : Supplier.id
	 * </ul> 
	 * 
	 * @return @see com.aimir.model.system.TariffWMCaliber
	 */
	public TariffWMCaliber getTariffWMCaliberByCaliber(Map<String,Object> params);
}