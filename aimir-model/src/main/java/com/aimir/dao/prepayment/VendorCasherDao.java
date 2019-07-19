package com.aimir.dao.prepayment;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.prepayment.VendorCasher;
import com.aimir.model.system.Operator;

public interface VendorCasherDao extends GenericDao<VendorCasher, Integer>{
	/**
	 * casher의 로그인 기능 
	 * @param vendor
	 * @param casherId
	 * @param hashedPw
	 * @return
	 */
	public Boolean isVaildVendorCasher(Operator vendor, String casherId, String hashedPw);
	
	/**
	 * casherId가 존재하는지 확인
	 * @param casherId
	 * @return
	 */
	public Boolean isVaildCasherForSA(String casherId);
	/**
	 * 특정 vendor 관련 casher 목록 조회
	 * @param condition
	 * @return
	 */
	public Map<String, Object> getCasherList(Map<String, Object> condition);
	

	/**
	 * 로그인 하는 casher 정보 조회 
	 * @param loginId: casher 로그인 id
	 * @param vendor: Operator vendor 객체
	 * @return
	 */
	public VendorCasher getByVendorCasherId(String loginId, Operator vendor);
	
	
	/**
	 * casher 정보 조회. casherId가 고유하다는 전제하에 casher : vendor = 1:1 관계 일때 사용.
	 * spasa pos 웹서비스 연계를 위한 메소드.
	 * @param condition
	 * @return
	 */
	public List<VendorCasher> getCasher(Map<String, Object> condition);
	
	/**
	 * casher 정보 조회. casherName이 고유하다는 전제하에 casher : vendor = 1:1 관계 일때 사용.
     * spasa pos 웹서비스 연계를 위한 메소드.
	 * @param condition
	 * @return
	 */
	public List<VendorCasher> getCasherByName(Map<String, Object> condition);
	
	/**
	 * 특정 macaddress에 대응하는 casher 조회
	 * @param mac : macaddress
	 * @param vendor : vendor 계정
	 * @return
	 */
	public VendorCasher getByMacAddress(String mac, Operator vendor);
	
	/**
	 * 특정 casher의 password를 변경한다
	 * @param condition: casherId, password
	 * @return
	 */
	public String changePassword(Map<String, Object> condition);
	
	/**
	 * getByVendorOperator
	 * @param operator: vendor operator 
	 * @return
	 */
	public List<VendorCasher> getByVendorOperator(Operator operator);
	
	/**
	 * 특정 casher에 대한 macaddress 정보를 수정한다.
	 * @param condition: (String) casherId, mac, (Operator)vendor 
	 * @return
	 */
	public String updateMacAddress( Map<String, Object> condition );
	
	/**
	 * 특정 vending station에 대한 macaddress 정보를 조회한다.
	 * @param vendor
	 * @return
	 */
	public List<String> getMacAddressLIst( Operator vendor );
}
