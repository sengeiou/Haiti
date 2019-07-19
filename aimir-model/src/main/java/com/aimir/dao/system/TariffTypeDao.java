package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.model.system.TariffType;

public interface TariffTypeDao extends GenericDao<TariffType, Integer> {

	/**
     * method name : getTariffTypeBySupplier
     * method Desc : 계약 종별 타입 목록을 리턴
     * 
	 * @param serviceType  TariffType.serviceTypeCode.code
	 * @param supplierId Supplier.id
	 * @return List of TariffType @see com.aimir.model.system.TariffType
	 */
	public List<TariffType> getTariffTypeBySupplier(String serviceType, Integer supplierId);
	
	/**
     * method name : getTariffTypeList
     * method Desc : 계약 종별 타입 목록을 리턴
     * 
	 * @param supplierId Supplier.id
	 * @param serviceType TariffType.serviceTypeCode.id
	 * @return List of TariffType @see com.aimir.model.system.TariffType
	 */
	public List<TariffType> getTariffTypeList(Integer supplierId, Integer serviceType);
	
	/**
     * method name : getTariffTypeIdByName
     * method Desc : 계약 종별 타입 목록을 리턴
     * 
	 * @param String name
	 * @return List of TariffType @see com.aimir.model.system.TariffType
	 */
	public List<TariffType> getTariffTypeByName(String name);
	
	/**
     * method name : updateData
     * method Desc : TariffType 데이터를 업데이트 한다.
     * 
	 * @param TariffType tariffType
	 * @return int
	 */
	public int updateData(TariffType tariffType);
	
	/**
     * method name : getLastCode
     * method Desc : 가장 큰 Code값을 조회해온다.
     * 
	 * @param 
	 * @return Integer
	 */
	public Integer getLastCode();
	
	/**
	 * @MethodName addTariffType
	 * @Date 2014. 1. 8.
	 * @param String tariffTypeName
	 * @param Code serviceType 
	 * @param Supplier supplierId
	 * @return
	 * @Modified
	 * @Description 기존에 조건에 있는 tariffType이 있다면, 있는 것으로 반환하고, 없으면 새로 생성해서 반환한다. 
	 */
	public TariffType addTariffType(String tariffTypeName, Code serviceType, Supplier supplier); 
}