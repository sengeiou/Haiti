package com.aimir.dao.system;

import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Code;

public interface CodeDao extends GenericDao<Code, Integer> {

    /**
     * method name : getParents
     * method Desc : parent에 해당하는 코드 목록을 리턴
     *
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getParents();

    /**
     * @desc 하위노드를 가지고온다 by parentId
     * @param parentId
     * @return
     */
    public List<Code> getChildren(int parentId) ;

    /**
     * method name : parentCodeCheck
     * method Desc : 해당 조건에 해당하는 카운트를 리턴
     *
     * @param code Code.code
     * @param id Code.id
     * @return
     */
    public int parentCodeCheck(String code, Integer id);

    /**
     * method name : childCodeCheck
     * method Desc : 해당 조건에 해당하는 카운트를 리턴
     *
     * @param code Code.code
     * @param id Code.id
     * @return
     */
    public int childCodeCheck(String code, Integer id);

    /**
     * method name : getCodeIdByCode
     * method Desc : Code객체의 아이디를 리턴
     *
     * @param code Code.code
     * @return Code.id
     */
    public int getCodeIdByCode(String code);

    /**
     * method name : getCodeIdByCodeObject
     * method Desc : code값으로 Code객체 리턴
     *
     * @param code Code.code
     * @return @see com.aimir.model.system.Code
     */
    public Code getCodeIdByCodeObject(String code);

    /**
     * method name : updateCode
     * method Desc : Code객체 업데이트
     *
     * @param code
     * @throws Exception
     */
    public void updateCode(Code code) throws Exception;

    /**
     * method name : getChildCodes
     * method Desc : parent의 code값으로 Code List를 리턴
     *
     * @param parentCode Code.parent.code
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getChildCodes(String parentCode);
    public List<Code> getChildCodesOrder(String parentCode);

    /**
     * method name : getChildCodesSelective
     * method Desc : parent 의 code 값으로 Code List 를 리턴. parameter 로 넘어온 코드(들)를 제외하고 조회함.
     *
     * @param parentCode Code.parent.code
     * @param excludeCodes String ','값으로 구분된 조회시 제외할 code 들
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getChildCodesSelective(String parentCode, String excludeCodes);

    /**
     * method name : getCodesByName
     * method Desc : 코드명으로  코드를 찾아옴
     *
     * @param name Code.name
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getCodesByName(String name);

    /**
     * method name : getCodeByName
     * method Desc :
     *
     * @param name
     * @return  @see com.aimir.model.system.Code
     */
    public Code getCodeByName(String name);

    /**
     * method name : getCodeWithChildByName
     * method Desc : 코드명으로 코드와 child에 해당하는 코드를 찾아옴
     *
     * @param name Code.name
     * @return  @see com.aimir.model.system.Code
     */
    public Code getCodeWithChildByName(String name);


    //TODO  해당 메소드 사용 비추천 방식에 문제가 많음
    /**
     * method name : getEnergyList
     * method Desc : 에너지 사용타입에 대한 리스트 코드를 반환
     *
     * @param customerId Contract.customer.id
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getEnergyList(int customerId);


    /**
     * method name : getLeafCode
     * method Desc : 특정코드의 모든 하위 코드중에서 최하위코드의 ID 목록을 조회한다.
     *
     * 조회조건은 code 의 ID
     * @param codeId Code.id
     * @return List<Integer>
     */
    public List<Integer> getLeafCode(Integer codeId);

    /**
     * method name : getChildren
     * method Desc : 코드 ID에 해당하는 하위 코드 목록을 조회한다.
     *
     * @param parentId Code.parent.id
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getChildren(Integer parentId);

    /**
     * method name : getSicChildrenCodeList<b/>
     * method Desc : SIC Load Profile 가젯에서 2 level SIC Code list 를 조회한다.
     *
     * @return
     */
    public List<Code> getSicChildrenCodeList();
    
    /**
     * method name :getCodeByCondition
     * method Desc :
     */
    public Code getCodeByCondition(Map<String, Object> condition);

    /**
     * method name : getMeterStatusCodeByName<b/>
     * method Desc : MeterStatus Name 으로 Code 를 조회한다. Parent Code Name 은 default 로 "MeterStatus"
     *
     * @param meterStatusName Meter Status Name
     * @return Code @see com.aimir.model.system.Code
     */
    public Code getMeterStatusCodeByName(String meterStatusName);

    /**
     * method name : getMeterStatusCodeByName<b/>
     * method Desc : MeterStatus Name 과 Parent Code Name 으로 Code 를 조회한다.
     *
     * @param meterStatusName Meter Status Name
     * @param parentName Parent Code Name
     * @return Code @see com.aimir.model.system.Code
     */
    public Code getMeterStatusCodeByName(String meterStatusName, String parentName);
    
    public List<Code> getCodeList();
    public List<Code> getChildCodes(String parentCode, String orderBy);
}