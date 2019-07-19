package com.aimir.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.system.SupplierDao;
import com.aimir.model.system.Supplier;
/**
 * 국가별로 금액을 표현하는 방법에 차이가 있다. 예를 들어 한국에서는 \ 로 표현하지만, 미국에서는 $ 으로 표현한다.
 */

public class CurrencyLocaleUtil {
	
	private Locale locale;
	
	private String currencySymbol;
	private NumberFormat nf;

	
    private SupplierDao supplierDao;
    
    @Autowired
    public void setSupplierDao(SupplierDao _supplierDao) {
    	this.supplierDao = _supplierDao;
    }
    
	/**
	 * supplierId 를 넘기면 공급사 정보에서 locale 정보를 얻어오는 생성자.
	 * @param supplierId
	 */
	public CurrencyLocaleUtil(SupplierDao getSupplierDao, Integer supplierId){
		this.supplierDao = getSupplierDao;
		
		Supplier supplier = supplierDao.get(supplierId);
		
		String lang 	= supplier.getLang().getCode_2letter();
		String country 	= supplier.getCountry().getCode_2letter();
				
		// 지역
		locale 			= new Locale(lang, country);
		
		// 통화모양
		Currency cr = Currency.getInstance(locale);
		currencySymbol  = cr.getSymbol().toString();
		
		// 숫자 포맷
		nf = NumberFormat.getCurrencyInstance(locale);
			
	}
	
	
	public CurrencyLocaleUtil(Integer supplierId){
		Supplier supplier = null;
		supplier = this.supplierDao.get(supplierId);
		
		String lang 	= supplier.getLang().getCode_2letter();
		String country 	= supplier.getCountry().getCode_2letter();
				
		// 지역
		locale 			= new Locale(lang, country);
		
		// 통화모양
		Currency cr = Currency.getInstance(locale);
		currencySymbol  = cr.getSymbol().toString();
		
		// 숫자 포맷
		nf = NumberFormat.getCurrencyInstance(locale);
			
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// 통화모양 
	public String getCurrecnySymbol(Object obj){
        return currencySymbol;
    }

	// 통화로 변경
	public String getCurrecny(Object obj){
        return nf.format(obj);
    }
	
	// 통화, 숫자포맷 모두 삭제
	public String removeCurrency(String str){
		return str.replaceAll(currencySymbol, "").replaceAll(",", "");
    }
	
    
}
