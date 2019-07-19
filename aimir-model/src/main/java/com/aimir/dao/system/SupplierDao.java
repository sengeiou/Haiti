package com.aimir.dao.system;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Supplier;

public interface SupplierDao extends GenericDao<Supplier, Integer> {

	/**
     * method name : getSupplierByName
     * method Desc : 공급사명으로 해당 공급사 정보를 리턴한다.
	 * 
	 * @param supplierName Supplier.name
	 * @return @see com.aimir.model.system.Supplier
	 */
	public Supplier getSupplierByName(String supplierName);
	
	/**
     * method name : getSupplierById
     * method Desc : 공급사 아이디로 해당 공급사 정보를 리턴한다.
     * 
	 * @param supplierId Supplier.id
	 * @return @see com.aimir.model.system.Supplier
	 */
	public Supplier getSupplierById(int supplierId);
	
	/**
     * method name : count
     * method Desc : 공급사 정보 전체 row count를 리턴한다.
     * 
	 * @return row count
	 */
	public Integer count();
}
