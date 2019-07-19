/**
 * EnergyBalanceMonitoringController.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.bo.system.energyBalanceMonitoring;

import java.text.ParseException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommonController;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.mvm.MvmDetailViewManager;
import com.aimir.service.mvm.bean.ChannelInfo;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.EBSPhase2Manager;
import com.aimir.service.system.EnergyBalanceMonitoringManager;
import com.aimir.service.system.RoleManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.service.system.impl.EBSDataMapper;
import com.aimir.service.system.impl.ETreeNode;
import com.aimir.service.system.prepayment.PrepaymentMgmtOperatorManager;
import com.aimir.util.CommonUtils;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * EBSPhase2Controller.java Description
 *
 *
 * Date          Version     Author   Description
 * 2015. 3. 9.  v1.0        eunmiae  Energy Balance Monitoring View Controller
 *
 */
@Controller
public class EBSPhase2Controller {

    protected static Log log = LogFactory.getLog(EBSPhase2Controller.class);

    @Autowired
    PrepaymentMgmtOperatorManager prepaymentMgmtOperatorManager;

    @Autowired
    CodeManager codeManager;

    @Autowired
    SupplierManager supplierManager;

    @Autowired
    EnergyBalanceMonitoringManager energyBalanceMonitoringManager;

    @Autowired
    RoleManager roleManager;
    
    @Autowired
    EBSPhase2Manager ebsManager;
 
    @Autowired
    MvmDetailViewManager mvmDetailViewManager;

    @RequestMapping(value="/gadget/system/ebsPhase2Mini")
    public ModelAndView loadPrepaymentMgmtOperatorMini() {
        ModelAndView mav = new ModelAndView("/gadget/system/ebsPhase2Mini");
        
        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        Integer supplierId = user.getRoleData().getSupplier().getId();

        mav.addObject("supplierId", supplierId);
        return mav;
    }

    @RequestMapping(value="/gadget/system/ebsPhase2Max")
    public ModelAndView loadPrepaymentMgmtOperatorMax() {
        ModelAndView mav = new ModelAndView("/gadget/system/ebsPhase2Max");

        // ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();
        AimirUser user = (AimirUser)instance.getUserFromSession();
        mav.addObject("supplierId", user.getRoleData().getSupplier().getId());

        Role role = roleManager.getRole(user.getRoleData().getId());
        Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

        mav.addObject("editAuth", authMap.get("cud"));  // 수정권한(write/command = true)

        try {
            String currentDate = TimeUtil.getCurrentDay();
            String prevDate = TimeUtil.getPreDay(TimeUtil.getCurrentDay() + "000000").substring(0, 8);
            mav.addObject("_basicDate", prevDate);
            
            int du = TimeUtil.getDayDuration(currentDate, prevDate);
            String dpMax = null;
            if (du >= 0) {
                dpMax = "+" + du + "d";
            } else {
                dpMax = du + "d";
            }
            mav.addObject("_dpMax", dpMax);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mav;
    }

	  /**
	  * method name : getEbsMonitoringList<b/>
	  * method Desc : EBS Monitoring List 를 조회한다.
	  *
	  * @param supplierId
	  * @param EBS Device Type
	  * @param locationId
	  * @param meter Id
	  * @return
	  */
	 @RequestMapping(value = "/gadget/system/getEbsMonitoringList")
	 public ModelAndView getEbsMonitoringList(
			 @RequestParam("supplierId") Integer supplierId,
	         @RequestParam(value = "type", required = false) String type,
	         @RequestParam("meterId") String meterId,
	         @RequestParam("locationId") Integer locationId,
	         @RequestParam() String searchType,
	         @RequestParam() String yyyymmdd) {
		 
	     ModelAndView mav = new ModelAndView("jsonView");
	     HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	
	     int page = Integer.parseInt(request.getParameter("page"));
	     int limit = Integer.parseInt(request.getParameter("limit"));
	
	     Map<String, Object> conditionMap = new HashMap<String, Object>();
	     conditionMap.put("page", page);
	     conditionMap.put("limit", limit);
	     conditionMap.put("supplierId", supplierId);
	     conditionMap.put("type", type);
	     conditionMap.put("meterId", meterId);
	     conditionMap.put("searchType", searchType);
	     conditionMap.put("yyyymmdd", yyyymmdd);
	     //conditionMap.put("locationId", locationId);
	     List<Map<String, EBSDataMapper>> result = null;
	     try{
	    	 result = ebsManager.getEbsMonitoringList(conditionMap);

	     }catch(Exception e){
	    	 log.error(e, e);
	     }

	     mav.addObject("result", result);
	     mav.addObject("totalCount", ebsManager.getEbsDeviceListCount(conditionMap));

	     return mav;
	 }
	 
	    @RequestMapping("/gadget/system/getChannelList.do")
	    public ModelAndView getChannelList( @RequestParam("meterId") String meterId,HttpServletRequest request, HttpServletResponse response) {
	    	 ModelAndView mav = new ModelAndView("jsonView");

			List<ChannelInfo> channelInfo = mvmDetailViewManager.getChannelInfo(meterId, "EM");
		
			mav.addObject("channelList", channelInfo);

	    	return mav;
	    }
	  /**
	  * method name : getEbsMonitoringList<b/>
	  * method Desc : EBS Monitoring List 를 조회한다.
	  *
	  * @param supplierId
	  * @param EBS Device Type
	  * @param locationId
	  * @param meter Id
	  * @return
	  */
	 @RequestMapping(value = "/gadget/system/getEbsMonitoringTree")
	 public ModelAndView getEbsMonitoringTree(
	         @RequestParam(value = "type", required = false) String type,
	         @RequestParam("meterId") String meterId,
	         @RequestParam() String searchType,
	         @RequestParam() String yyyymmdd,
	         @RequestParam("channel") Integer channel) {
		 
	     ModelAndView mav = new ModelAndView("jsonView");
	     HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	
//	     int page = Integer.parseInt(request.getParameter("page"));
//	     int limit = Integer.parseInt(request.getParameter("limit"));
	
	     Map<String, Object> conditionMap = new HashMap<String, Object>();
//	     conditionMap.put("page", page);
//	     conditionMap.put("limit", limit);

	     conditionMap.put("type", type);
	     conditionMap.put("meterId", meterId);
	     conditionMap.put("searchType", searchType);
	     conditionMap.put("yyyymmdd", yyyymmdd);
	     conditionMap.put("channel", channel);
	     //conditionMap.put("locationId", locationId);
	     List<ETreeNode> result = null;
	     List<ETreeNode> treeData = null;
	     try{
	    	
	    	 result = ebsManager.getEbsMonitoringTree(conditionMap);

	     }catch(Exception e){
	    	 log.error(e, e);
	     }

	     mav.addObject("result", result);
	   //  mav.addObject("totalCount", ebsManager.getEbsDeviceListCount(conditionMap));

	     return mav;
	 }

	  /**
	  * method name : getEbsMonitoringList<b/>
	  * method Desc : EBS Monitoring List 를 조회한다.
	  *
	  * @param supplierId
	  * @param EBS Device Type
	  * @param locationId
	  * @param meter Id
	  * @return
	  */
	 @RequestMapping(value = "/gadget/system/getEbsMonitoringChart")
	 public ModelAndView getEbsMonitoringChart(
			 @RequestParam("supplierId") Integer supplierId,
			 @RequestParam("ebsDeviceId") Integer ebsDeviceId,
	         @RequestParam(value = "type", required = false) String type,
	         @RequestParam("meterId") String meterId,
	         @RequestParam("locationId") Integer locationId,
	         @RequestParam() String searchType,
	         @RequestParam() String yyyymmdd) {

	     ModelAndView mav = new ModelAndView("jsonView");
	     HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();

	     int page = Integer.parseInt(request.getParameter("page"));
	     int limit = Integer.parseInt(request.getParameter("limit"));

	     Map<String, Object> conditionMap = new HashMap<String, Object>();
	     conditionMap.put("page", page);
	     conditionMap.put("limit", limit);
	     
	     conditionMap.put("supplierId", supplierId);
	     conditionMap.put("type", type);
	     conditionMap.put("meterId", meterId);
	     conditionMap.put("searchType", searchType);
	     conditionMap.put("yyyymmdd", yyyymmdd);

	     List<Map<String, EBSDataMapper>> result = null;
	     try{
	    	 result = ebsManager.getEbsMonitoringList(conditionMap);

	     }catch(Exception e){
	    	 log.error(e, e);
	     }
	     
	     mav.addObject("result", result);
	     mav.addObject("totalCount", ebsManager.getEbsDeviceListCount(conditionMap));
	
	     return mav;
	 }
    
    /**
	  * method name : getEbsDeviceList<b/>
	  * method Desc : EBS Device List 를 조회한다.
	  *
	  * @param supplierId
	  * @param EBS Device Type
	  * @param locationId
	  * @param meter Id
	  * @return
	  */
	 @RequestMapping(value = "/gadget/system/getEbsDeviceList")
	 public ModelAndView getEbsDeviceList(@RequestParam("supplierId") Integer supplierId,
	         @RequestParam("type") String type,
	         @RequestParam("meterId") String meterId,
	         @RequestParam("locationId") Integer locationId) {
	     ModelAndView mav = new ModelAndView("jsonView");
	     HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	
	     int page = Integer.parseInt(request.getParameter("page"));
	     int limit = Integer.parseInt(request.getParameter("limit"));
	
	     Map<String, Object> conditionMap = new HashMap<String, Object>();
	     conditionMap.put("page", page);
	     conditionMap.put("limit", limit);
	     conditionMap.put("supplierId", supplierId);
	     conditionMap.put("type", type);
	     conditionMap.put("meterId", meterId);
	     conditionMap.put("locationId", locationId);
	 
	
	     List<Map<String, Object>> result = ebsManager.getEbsDeviceList(conditionMap);
	     mav.addObject("result", result);
	     mav.addObject("totalCount", ebsManager.getEbsDeviceListCount(conditionMap));
	
	     return mav;
	 }

	  /**
	  * method name : getEbsMainIncomerList<b/>
	  * method Desc : EBS Device List 를 조회한다.
	  *
	  * @param supplierId
	  * @param EBS Device Type
	  * @param locationId
	  * @param meter Id
	  * @return
	  */
	 @RequestMapping(value = "/gadget/system/getEbsMainIncomerList")
	 public ModelAndView getEbsMainIncomerList(@RequestParam("supplierId") Integer supplierId,
	         @RequestParam("type") String type,
	         @RequestParam("meterId") String meterId,
	         @RequestParam("locationId") Integer locationId) {
	     ModelAndView mav = new ModelAndView("jsonView");
	     HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
	
	     int page = Integer.parseInt(request.getParameter("page"));
	     int limit = Integer.parseInt(request.getParameter("limit"));
	
	     Map<String, Object> conditionMap = new HashMap<String, Object>();
	     conditionMap.put("page", page);
	     conditionMap.put("limit", limit);
	     conditionMap.put("supplierId", supplierId);
	     conditionMap.put("type", type);
	     conditionMap.put("meterId", meterId);
	     conditionMap.put("locationId", locationId);
	 
	
	     List<Map<String, Object>> result = ebsManager.getEbsDeviceList(conditionMap);
	     mav.addObject("result", result);
	     mav.addObject("totalCount", ebsManager.getEbsDeviceListCount(conditionMap));
	
	     return mav;
	 }
	 
   /**
   * method name : insertEbsDts<b/>
   * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 정보를 저장한다.
   *
   * @param supplierId
   * @param dtsName
   * @param threshold
   * @param locationId
   * @param address
   * @param description
   * @return
   */
  @RequestMapping(value = "/gadget/system/insertEbsDevice")
  public ModelAndView insertEbsDevice(
		  @RequestParam("supplierId") Integer supplierId,
          @RequestParam("type") Integer type,
          @RequestParam("meterId") String meterId,
          @RequestParam("parentMeterId") String parentMeterId,
          @RequestParam("threshold") Double threshold,
          @RequestParam("locationId") Integer locationId,
          @RequestParam("address") String address,
          @RequestParam("description") String description) {
      ModelAndView mav = new ModelAndView("jsonView");

      Map<String, Object> conditionMap = new HashMap<String, Object>();
      conditionMap.put("supplierId", supplierId);
      conditionMap.put("type", type);
      conditionMap.put("meterId", meterId);
      conditionMap.put("parentMeterId", parentMeterId);
      conditionMap.put("threshold", threshold);
      conditionMap.put("locationId", locationId);
      conditionMap.put("address", address);
      conditionMap.put("description", description);

      try{
    	  ebsManager.insertEbsDevice(conditionMap);
    	  mav.addObject("result", "success");
      }catch(Exception e){
    	  log.error(e, e);
    	  mav.addObject("result", "fail");
      }

      return mav;
  }
  
  /**
   * method name : insertEbsDts<b/>
   * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 정보를 저장한다.
   *
   * @param supplierId
   * @param dtsName
   * @param threshold
   * @param locationId
   * @param address
   * @param description
   * @return
   */
  @RequestMapping(value = "/gadget/system/modifyEbsDevice")
  public ModelAndView modifyEbsDevice(
		  @RequestParam("supplierId") Integer supplierId,
		  @RequestParam("id") Integer id,
          @RequestParam("meterId") String meterId,
          @RequestParam("parentMeterId") String parentMeterId,
          @RequestParam("threshold") Double threshold,
          @RequestParam("locationId") Integer locationId,
          @RequestParam("address") String address,
          @RequestParam("description") String description) {
      ModelAndView mav = new ModelAndView("jsonView");

      Map<String, Object> conditionMap = new HashMap<String, Object>();
      conditionMap.put("supplierId", supplierId);
      conditionMap.put("id", id);
      conditionMap.put("meterId", meterId);
      conditionMap.put("parentMeterId", parentMeterId);
      conditionMap.put("threshold", threshold);
      conditionMap.put("locationId", locationId);
      conditionMap.put("address", address);
      conditionMap.put("description", description);

      try{
    	  ebsManager.modifyEbsDevice(conditionMap);
    	  mav.addObject("result", "success");
      }catch(Exception e){
    	  log.error(e, e);
    	  mav.addObject("result", "fail");
      }

      return mav;
  }
  
  
  
  /**
   * method name : insertEbsDts<b/>
   * method Desc : Energy Balance Monitoring 맥스가젯에서 DTS 정보를 저장한다.
   *
   * @param supplierId
   * @param dtsName
   * @param threshold
   * @param locationId
   * @param address
   * @param description
   * @return
   */
  @RequestMapping(value = "/gadget/system/deleteEbsDevice")
  public ModelAndView deleteEbsDevice(HttpServletRequest request,
			HttpServletResponse response) {
      ModelAndView mav = new ModelAndView("jsonView");
      int successCnt = 0;
      String[] delEbsIds = null;
      try{
    	  Enumeration<String> paramKey = request.getParameterNames();
    	  String keyStr;
    	  while(paramKey.hasMoreElements()){
    		  keyStr = "";
    		  keyStr = paramKey.nextElement();

    		  delEbsIds = request.getParameterValues(keyStr);
    		  for(int i=0; i<delEbsIds.length; i++) {
    			  boolean result = ebsManager.deleteEbsDevice(Integer.parseInt(delEbsIds[i]));
    			  if(result) {
    				  successCnt++;
    			  }
    		  }
    	  }
      }catch(Exception e){
    	  log.error(e, e);
      }
      mav.addObject("successCnt",successCnt);
      return mav;
  }  

  @RequestMapping(value = "/gadget/system/getEbsMeterList2")
  public ModelAndView getMeterList2(
		  @RequestParam("supplierId") Integer supplierId,
		  @RequestParam(value = "vendorName", required = false) String vendorName,
          @RequestParam(value = "deviceModelId", required = false) Integer deviceModelId,
          @RequestParam(value = "mdsId", required = false) String mdsId,
          @RequestParam(value = "selGubun", required = false) String selGubun) {

      ModelAndView mav = new ModelAndView("jsonView");
      HttpServletRequest request = ESAPI.httpUtilities().getCurrentRequest();
      int page = Integer.parseInt(request.getParameter("page"));
	  int limit = Integer.parseInt(request.getParameter("limit"));
	     
      Map<String, Object> conditionMap = new HashMap<String, Object>();
      conditionMap.put("supplierId", supplierId);
      conditionMap.put("vendorName", vendorName);
      conditionMap.put("deviceModelId", deviceModelId);
      conditionMap.put("mdsId", mdsId);
      conditionMap.put("selGubun", selGubun);
      conditionMap.put("page", page);
	  conditionMap.put("limit", limit);
//      conditionMap.put("locationId", locationId);
  
      try{
    	  List<Map<String, Object>> result = ebsManager.getEbsMeterList(conditionMap);
    	  mav.addObject("totalCount", ebsManager.getEbsMeterListTotalCnt(conditionMap));
          mav.addObject("result", result);
      }catch(Exception e){
    	  log.error(e, e);
      }

      return mav;
  }
}