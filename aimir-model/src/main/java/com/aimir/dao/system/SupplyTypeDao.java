package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.SupplyType;

public interface SupplyTypeDao extends GenericDao<SupplyType, Integer>{

	/**
	 * method name : getSupplyTypeBySupplierId
	 * method Desc : 공급사 아이디에 해당하는 공급타입(SupplyType) 리스트를 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return List of SupplyType @see com.aimir.model.system.SupplyType
	 */
	public List<SupplyType> getSupplyTypeBySupplierId(Integer supplierId);
	
	/**
	 * method name : checkSupplyType
	 * method Desc : 공급사 아이디에 해당하는 공급타입(SupplyType) 리스트를 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @param typeId SupplyType.typeCode.id (Code.id)
	 * 
	 * @return 일치하는 목록이 1개이상 존재하면 false를 리턴하고 없으면 true를 리턴
	 */
	public boolean checkSupplyType(Integer supplierId, Integer typeId);
	
	/**
	 * method name : getSupplyTypeBySupplierId
	 * method Desc : 공급사 아이디에 해당하는 공급타입(SupplyType) 리스트를 리턴한다.
	 * 
	 * @param supplierId Supplier.id
	 * @return List of SupplyType @see com.aimir.model.system.SupplyType
	 */
	public List<SupplyType> getSupplyTypeList(Integer supplierId);
	
	public List<SupplyType> getSupplyTypeBySupplierIdTypeId(Integer supplierId, Integer typeId);
}
