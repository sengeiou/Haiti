package com.aimir.bo.system.code;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Code;
import com.aimir.model.system.Role;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.RoleManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.StringUtil;

@Controller
//@SessionAttributes(types = Code.class)
public class CodeController {

    @Autowired
    CodeManager codeManager;

    @Autowired
    RoleManager roleManager;

    @RequestMapping(value={ "/gadget/system/code_manager_mini.do" , "/gadget/system/code_manager_max.do" } )
    public ModelMap findAll(ModelMap model) {
        ModelMap modelMap = new ModelMap(codeManager.getParents());

        AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
        AimirUser user = (AimirUser) instance.getUserFromSession();

        Role role = roleManager.getRole(user.getRoleData().getId());

        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);
        modelMap.addAttribute("editAuth", authMap.get("cud"));

        return modelMap;
    }
    
    @RequestMapping("/gadget/system/getCodeListwithChildren.do")
    public ModelAndView getCodeListwithChildren() {
    	ModelAndView mav = new ModelAndView("jsonView");
    	 mav.addObject("result", codeManager.getCodeListwithChildren());

        return mav;
    }
    
    @RequestMapping("/gadget/system/addCodeTreeNode.do")
    public ModelAndView addCodeTreeNode(@RequestParam(value="name", required = true) String name,
    		@RequestParam(value="code", required = true) String code,
    		@RequestParam(value="descr", required = true) String descr,
    		@RequestParam(value="parentNodeId", required = true) String parentNodeId) { 
    	ModelAndView mav = new ModelAndView("jsonView");
    	
    	Map<String,Object> codeMap =  new HashMap<String,Object>();
    	
    	codeMap.put("name", name);
    	codeMap.put("code", code);
    	codeMap.put("descr", descr);
    	if("result".equals(parentNodeId)){
    		codeMap.put("parentNodeId", null);
    	}else{
    		codeMap.put("parentNodeId", parentNodeId);
    	}
    	
    	try {
			String result = codeManager.saveCode(codeMap);
			mav.addObject("result", result);
		} catch (Exception e) {
			mav.addObject("result", "fail" );
		}
    	
        return mav;
    }
    
    @RequestMapping("/gadget/system/updateCodeTreeNode.do")
    public ModelAndView updateCodeTreeNode(@RequestParam(value="id", required = true) String id,
    		@RequestParam(value="name", required = true) String name,
    		@RequestParam(value="code", required = true) String code,
    		@RequestParam(value="descr", required = true) String descr,
    		@RequestParam(value="parentNodeId", required = true) String parentNodeId) { 
    	ModelAndView mav = new ModelAndView("jsonView");
    	Map<String,Object> codeMap =  new HashMap<String,Object>();

    	String tempId = (StringUtil.isDigit(id))?id:null;
    	
    	codeMap.put("id", tempId);
    	codeMap.put("name", name);
    	codeMap.put("code", code);
    	codeMap.put("descr", descr);

    	if("result".equals(parentNodeId)){
    		codeMap.put("parentNodeId", null);
    	}else{
    		codeMap.put("parentNodeId", parentNodeId);
    	}
    	
    	try {
    		String result; 
    		if("".equals(tempId) || tempId == null){
    			result=  codeManager.saveCode(codeMap);	
    		}else{
    			result=  codeManager.updateCode(codeMap);	
    		}
			
			mav.addObject("result", result);
		} catch (Exception e) {
			mav.addObject("result", "fail" );
		}
        return mav;
    }
    
    @RequestMapping("/gadget/system/deleteCodeTreeNode.do")
    public ModelAndView deleteCodeTreeNode(@RequestParam(value="id", required = true) String codeId) {
    	ModelAndView mav = new ModelAndView("jsonView");
    	try{
    		if(codeId != null || "".equals(codeId)){
    		codeManager.deleteCodeTreeNode(codeId);
    		 mav.addObject("result", "success" );
    		}
    	}catch(Exception e){
    		 mav.addObject("result", "fail");
        }

        return mav;
    }

    //삭제 처리시
    @RequestMapping("/delete.do")
    public String deleteCode(@RequestParam("id") int codeId) throws Exception {
        //codeManager.delete(codeId);
        return "redirect:codes.do";
    }

    //검색하기
//  @RequestMapping(value="/gadget/code_manager_mini.do", params={"name"})
//  public ModelMap findCodes(@RequestParam("name") String name) throws Exception {
//      return new ModelMap(codeManager.getCodesByName(name));
//  }

    @RequestMapping("/gadget/system/getChildCode.do")
    public ModelAndView getChildCode(@RequestParam("code") String code) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("code", codeManager.getChildCodes(code));

        return mav;
    }

    @RequestMapping("/gadget/system/getChildCodeOrderBy.do")
    public ModelAndView getChildCodeOrderBy(
    		@RequestParam("code") String code
    		,@RequestParam("orderBy") String orderBy) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("code", codeManager.getChildCodesOrderBy(code, orderBy));

        return mav;
    }
    
    @RequestMapping("/gadget/system/getChildCodeSelective.do")
    public ModelAndView getChildCodeSelective(@RequestParam("code") String code,
            @RequestParam("excludeCode") String excludeCode) throws Exception {
        ModelAndView mav = new ModelAndView("jsonView");
        List<Code> result = codeManager.getChildCodesSelective(code, excludeCode);
        mav.addObject("code", result);
        return mav;
    }

    @RequestMapping(value="/gadget/system/getSicCodes.do")
    public ModelAndView getSicCodes(HttpServletRequest request, HttpServletResponse response) {
        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("sicCodes", codeManager.getSicCodeList(Code.SIC));
        return mav;
    }
}