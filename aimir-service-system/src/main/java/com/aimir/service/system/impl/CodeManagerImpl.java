package com.aimir.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.CodeDao;
import com.aimir.model.system.Code;
import com.aimir.service.system.CodeManager;
import com.aimir.util.StringUtil;

@WebService(endpointInterface = "com.aimir.service.system.CodeManager")
@Service(value = "codeManager")
@Transactional(readOnly=false)
public class CodeManagerImpl implements CodeManager {

    Log logger = LogFactory.getLog(CodeManagerImpl.class);

    @Autowired
    CodeDao dao;

    public void setCodeDao(CodeDao dao) {
        this.dao = dao;
    }

    // 모든 리스트
    public List<Code> getParents() {
        List<Code> results = dao.getParents();
        return results;
    }

    public Code getCode(Integer id) {
        return dao.get(id);
    }

	// 하위노드저장하기
	public String saveCode(Map<String, Object> codeMap) {

		String name = (String) codeMap.get("name");
		String code = (String) codeMap.get("code");
		String descr = (String) codeMap.get("descr");
		int parentNodeId = Integer.parseInt(StringUtil.nullToZero(codeMap
				.get("parentNodeId")));

		String saveResult = "fail";

		Code isexistCode = dao.getCodeIdByCodeObject(code);// 중복코드 체크

		if (isexistCode.getId() == null) {
	
			Code parent = dao.get(parentNodeId);
		
			Code newChild = new Code();
			newChild.setName(name);
			newChild.setCode(code);
			newChild.setDescr(descr);

			if(parent !=null){
				newChild.setParent(parent);
				parent.addChildCode(newChild);
				dao.codeUpdate(parent);
			}
			
			dao.add(newChild);
			saveResult = "success";

		} else {
			saveResult = "duplicate";
		}

        return saveResult;
    }
	
	// 하위노드수정하기
		public String updateCode(Map<String, Object> codeMap) {

			
			int id = Integer.parseInt(StringUtil.nullToZero(codeMap.get("id")));
			String name = (String) codeMap.get("name");
			String code = (String) codeMap.get("code");
			String descr = (String) codeMap.get("descr");

			String saveResult = "fail";
			
			Code updateCode =  dao.get(id);
			
			int childCount = dao.childCodeCheck(code, updateCode.getId());
			if (childCount == 0) {
				updateCode.setName(name);
				updateCode.setCode(code);
				updateCode.setDescr(descr);
				dao.saveOrUpdate(updateCode);

				saveResult = "success";

			} else {
				saveResult = "duplicate";
			}

	        return saveResult;
	    }

    //하위저장하기
    public int saveChildCode(Code child) throws Exception {

        int count = 0 ;
        int childCount = dao.childCodeCheck(child.getCode(), child.getId());

        Code parent = dao.get(child.getId());

        if ( childCount == 0 ) {
            try {
                dao.codeParentAdd(child);
                parent.addChildCode(child);
                dao.codeUpdate(parent);
            } catch ( Exception e ) {
                throw new Exception("Save Failed!");
            }
        } else
            count = 1;

        return count;

    }

    //수정하기
    public int updateCodes(Code code) throws Exception {
        int count = 0;
        int parentCount = dao.parentCodeCheck(code.getCode(), code.getId());
        int childCount = dao.childCodeCheck(code.getCode(), code.getId());
        if ( parentCount == 0 && childCount == 0 )
            dao.updateCode(code);
        else
            count = 1;

        return count;
    }

    //하위 코드도 함께 삭제
    public void codeDelete(Integer id) {
        Code code = dao.get(id);
        dao.codeDelete(code);
    }

    public List<Code> getChildCodes(String parentCode) {
        return dao.getChildCodes(parentCode);
    }
    
    public List<Code> getChildCodesOrderBy(String parentCode, String orderBy) {
        return dao.getChildCodes(parentCode, orderBy);
    }

    /**
     * method name : getChildCodesSelective
     * method Desc : parent 의 code 값으로 Code List 를 리턴. parameter 로 넘어온 코드(들)를 제외하고 조회함.
     *
     * @param parentCode Code.parent.code
     * @param excludeCodes String ','값으로 구분된 조회시 제외할 code 들
     * @return List of Code  @see com.aimir.model.system.Code
     */
    public List<Code> getChildCodesSelective(String parentCode, String excludeCodes) {
        return dao.getChildCodesSelective(parentCode, excludeCodes);
    }

    public List<Code> getChildren(int parentId) {
        return dao.getChildren(parentId);
    }

    public List<Code> getChildCodesOrder(String parentCode) {
        return dao.getChildCodesOrder(parentCode);
    }

    public List<Code> getCodesByName(String name) {
        return dao.getCodesByName(name);
    }

    public List<Code> getEnergyList(int customerId) {
        return dao.getEnergyList(customerId);
    }

    public Code getCodeByName(String name) {
        return dao.getCodeByName(name);
    }

    public int getCodeIdByCode(String code) {
        return dao.getCodeIdByCode(code);
    }

    public Code getCodeByCode(String code) {
        return dao.getCodeIdByCodeObject(code);
    }

    /**
     * method name : getSicCodeList<b/>
     * method Desc :
     *
     * @param parentCode
     * @return
     */
    public List<Map<String, Object>> getSicCodeList(String parentCode) {
        List<Code> codeList = dao.getChildCodesOrder(parentCode);
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> childMap = new HashMap<String, Object>();
        // Set<Code> children = null;
        List<Code> children = null;

        for (Code code : codeList) {
            resultMap = new HashMap<String, Object>();
            childrenList = new ArrayList<Map<String, Object>>();
            resultMap.put("id", code.getId());
            resultMap.put("code", code.getCode());
            resultMap.put("name", code.getName());
            resultMap.put("descr", code.getDescr());
            resultMap.put("order", code.getOrder());
            resultMap.put("parent", (code.getParent() == null) ? "" : code.getParent().getId());
            // children = dao.getChildCodesOrder(code.getCode());
            children = dao.getChildren(code.getId());

            if (children != null && children.size() > 0) {
                // children = code.getChildren();

                for (Code child : children) {
                    childMap = new HashMap<String, Object>();
                    childMap.put("id", child.getId());
                    childMap.put("code", child.getCode());
                    childMap.put("name", child.getName());
                    childMap.put("descr", child.getDescr());
                    childMap.put("order", child.getOrder());
                    childMap.put("parent", (child.getParent() == null) ? "" : child.getParent().getId());
                    childrenList.add(childMap);
                }

                resultMap.put("children", childrenList);
            }
            result.add(resultMap);
        }

        return result;
    }

//    /**
//     * method name : getCodeListwithChildren<b/>
//     * method Desc : 하위단의 코드까지 모두 조회.
//     *
//     * @param parentCode
//     * @return
//     */
//	public List<Map<String, Object>> getCodeListwithChildren() {
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
//	    Map<String, Object> resultMap = new HashMap<String, Object>();
//
//	    List<Code> codeList = dao.getParents();
//
//	    for (Code code : codeList) {
//	            resultMap = new HashMap<String, Object>();
//	            childrenList = new ArrayList<Map<String, Object>>();
//	            resultMap.put("id", code.getId());
//	            resultMap.put("code", code.getCode());
//	            resultMap.put("name", code.getName());
//	            resultMap.put("descr", code.getDescr());
//	            resultMap.put("order", code.getOrder());
//	            resultMap.put("parent", (code.getParent() == null) ? "" : code.getParent().getId());
//	            childrenList = getCodeChildren(code.getId());
//	            if (childrenList.size() <= 0) {
//	            	resultMap.put("children", null);
//	            	resultMap.put("leaf",true);
//	            } else {
//	            	resultMap.put("children", childrenList);
//	            	resultMap.put("leaf",false);
//	            }
//	            resultMap.put("expanded",false);
//	            resultMap.put("cls","");
//	            resultList.add(resultMap);
//	        }
//		return resultList;
//	}
//	
//	private List<Map<String,Object>> getCodeChildren(Integer rootId){
//		List<Code> childrenrootList = null;
//		Map<String, Object> childMap = new HashMap<String, Object>();
//		List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> tempchildrenList = null;
//		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
//		childrenrootList = dao.getChildren(rootId);
//		 for (Code code : childrenrootList) {
//			 	childMap = new HashMap<String, Object>();
//			 	childrenList = new ArrayList<Map<String, Object>>();
//			 	tempchildrenList = new ArrayList<Map<String, Object>>();
//	            childMap.put("id", code.getId());
//	            childMap.put("code", code.getCode());
//	            childMap.put("name", code.getName());
//	            childMap.put("descr", code.getDescr());
//	            childMap.put("order", code.getOrder());
//	            childMap.put("parent", (code.getParent() == null) ? "" : code.getParent().getId());
//	            tempchildrenList = getCodeChildren(code.getId());
//	            childrenList.addAll(tempchildrenList);
//	            
//	            if (childrenList.size() <= 0) {
//	             	childMap.put("children", null);
//	             	childMap.put("leaf",true);
//	             } else {
//	             	childMap.put("children", childrenList);
//	             	childMap.put("leaf",false);
//	             }
//	            childMap.put("expanded",false);
//	            childMap.put("cls","");
//	            resultList.add(childMap);
//	     }
//		
//		return resultList;
//		
//	}

    /**
     * method name : getCodeListwithChildren<b/>
     * method Desc : 하위단의 코드까지 모두 조회.
     *
     * @param parentCode
     * @return
     */
	public List<Map<String, Object>> getCodeListwithChildren() {
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
	    Map<String, Object> resultMap = new HashMap<String, Object>();

	    List<Code> codeList = dao.getCodeList();
	    List<Code> parentCodeList = new ArrayList<Code>();
	    List<Code> childCodeList = new ArrayList<Code>();
	    List<Code> selChildCodeList = null;

	    for (Code code : codeList) {
	    	if (code.getParent() == null) {
	    		parentCodeList.add(code);
	    	} else {
	    		childCodeList.add(code);
	    	}
	    }
	    
	    for (Code code : parentCodeList) {
	            resultMap = new HashMap<String, Object>();
	            childrenList = new ArrayList<Map<String, Object>>();
	            selChildCodeList = new ArrayList<Code>();
	            resultMap.put("id", code.getId());
	            resultMap.put("code", code.getCode());
	            resultMap.put("name", code.getName());
	            resultMap.put("descr", code.getDescr());
	            resultMap.put("order", code.getOrder());
	            resultMap.put("parent", (code.getParent() == null) ? "" : code.getParent().getId());

	            childrenList = getCodeChildren(childCodeList, code.getId());
	            if (childrenList.size() <= 0) {
	            	resultMap.put("children", null);
	            	resultMap.put("leaf",true);
	            } else {
	            	resultMap.put("children", childrenList);
	            	resultMap.put("leaf",false);
	            }
	            resultMap.put("expanded",false);
	            resultMap.put("cls","");
	            resultList.add(resultMap);
	        }
		return resultList;
	}
	
	private List<Map<String,Object>> getCodeChildren(List<Code> childCodeList, Integer parentId){
		List<Code> childrenrootList = new ArrayList<Code>();

        for (Code chcode : childCodeList) {
        	if (chcode.getParentId().equals(parentId)) {
        		childrenrootList.add(chcode);
        	}
        }

		Map<String, Object> childMap = new HashMap<String, Object>();
		List<Map<String, Object>> childrenList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> tempchildrenList = null;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
//		childrenrootList = dao.getChildren(rootId);
		 for (Code code : childrenrootList) {
			 	childMap = new HashMap<String, Object>();
			 	childrenList = new ArrayList<Map<String, Object>>();
			 	tempchildrenList = new ArrayList<Map<String, Object>>();
	            childMap.put("id", code.getId());
	            childMap.put("code", code.getCode());
	            childMap.put("name", code.getName());
	            childMap.put("descr", code.getDescr());
	            childMap.put("order", code.getOrder());
	            childMap.put("parent", (code.getParent() == null) ? "" : code.getParent().getId());
	            tempchildrenList = getCodeChildren(childCodeList, code.getId());
	            childrenList.addAll(tempchildrenList);
	            
	            if (childrenList.size() <= 0) {
	             	childMap.put("children", null);
	             	childMap.put("leaf",true);
	             } else {
	             	childMap.put("children", childrenList);
	             	childMap.put("leaf",false);
	             }
	            childMap.put("expanded",false);
	            childMap.put("cls","");
	            resultList.add(childMap);
	     }
		
		return resultList;
		
	}
	
	public Code getCodeByCondition(Map<String, Object> condition){
		return dao.getCodeByCondition(condition);
	}

	public void deleteCodeTreeNode(String codeId){
		String deleteresult = "FAIL";
        Code code = dao.get(Integer.parseInt(codeId));

       dao.codeDelete(code);
	}
	
	private Boolean searchCodeName(String codeName, String searchName){
		
		return null;
	}
}