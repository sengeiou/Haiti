package com.aimir.service.system;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.apache.cxf.annotations.WSDLDocumentation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.model.system.TariffType;

@WSDLDocumentation("Tariff Type Management Service")
@WebService(name="TariffTypeService", targetNamespace="http://aimir.com/services")
@SOAPBinding(style=Style.DOCUMENT, use=Use.LITERAL, parameterStyle=ParameterStyle.WRAPPED)
public interface TariffTypeManager {

	/**
     * method name : getAll
     * method Desc : 계약종별 전체 목록을 리턴한다.
     * 
	 * @return List of TariffType @see com.aimir.model.system.TariffType
	 */
	@WebMethod
	@WebResult(name="AllTariffList")
	public List<TariffType> getAll();
	
	/**
     * method name : getTariffTypeBySupplier
     * method Desc : 계약 종별 타입 목록을 리턴
     * 
	 * @param serviceType  TariffType.serviceTypeCode.code
	 * @param supplierId Supplier.id
	 * @return List of TariffType @see com.aimir.model.system.TariffType
	 */
	@WebMethod
	@WebResult(name="TariffTypeBySupplierList")
	public List<TariffType> getTariffTypeBySupplier(
			@WebParam(name="serviceType") String serviceType, 
			@WebParam(name="supplierId") Integer supplierId);
	
	/**
     * method name : getTariffTypeList
     * method Desc : 계약 종별 타입 목록을 리턴
     * 
	 * @param supplier Supplier.id
	 * @param serviceType TariffType.serviceTypeCode.id
	 * @return List of TariffType @see com.aimir.model.system.TariffType
	 */
	@WebMethod
	@WebResult(name="TariffTypeList")
	public List<TariffType> getTariffTypeList(
			@WebParam(name="supplier") Integer supplier , 
			@WebParam(name="serviceType") Integer serviceType);
	
	/**
     * method name : getTariffType
     * method Desc : TariffType id에 해당하는 TariffType 정보를 리턴
     * 
	 * @param id TariffType.id
	 * @return  @see com.aimir.model.system.TariffType
	 */
	@WebMethod
	@WebResult(name="TariffTypeInstance")
	public TariffType getTariffType(@WebParam(name="id") Integer id);
	/**
     * method name : delete
     * method Desc : TariffType 내용을 삭제한다.
     * 
	 * @param TariffType
	 * @return  
	 */
	@WebMethod
	@WebResult(name="delete")
	@Transactional(readOnly=false)
	public void delete(@WebParam(name="tariffType") TariffType tariffType);

    /**
     * method name : getTariffSupplySizeComboData<b/>
     * method Desc : TariffEM 의 SupplySize ComboData 를 조회한다.
     *
     * @param conditionMap
     * @return
     */
    public Map<String, Object> getTariffSupplySizeComboData(Map<String, Object> conditionMap);
}