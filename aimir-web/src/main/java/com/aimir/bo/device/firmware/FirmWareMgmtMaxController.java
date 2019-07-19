/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmWareMainController
 * 작성일자/작성자 : 2010.12.06 최창희
 * @see 
 * firmWareMainGadget.jsp
 *
 * 펌웨어 관리자 페이지 Component
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.bo.device.firmware;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.aimir.bo.common.CommandProperty;
import com.aimir.bo.system.vendormodel.JsonTree;
import com.aimir.constants.CommonConstants;
import com.aimir.esapi.AimirAuthenticator;
import com.aimir.esapi.AimirUser;
import com.aimir.model.device.FirmwareBoard;
import com.aimir.model.device.FirmwareCodi;
import com.aimir.model.device.FirmwareMCU;
import com.aimir.model.device.FirmwareModem;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.FirmWareManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.DeviceModelManager;
import com.aimir.service.system.RoleManager;
import com.aimir.support.AimirFilePath;
import com.aimir.support.FileUploadHelper;
import com.aimir.util.CommonUtils;
import com.aimir.util.StringUtil;


@Controller
public class FirmWareMgmtMaxController {

    @Autowired
    FirmWareManager firmWareManager;

	@Autowired
	CodeManager codeManager;    

	@Autowired
	DeviceModelManager deviceModelManager;

    @Autowired
	AimirFilePath aimirFilePath;

    @Autowired
    RoleManager roleManager;

    private static Log log = LogFactory.getLog(FirmWareMgmtMaxController.class);

    /**
     * @param       : jsp Call param
     * @exception   : 
     * @Date        : 2010/12/10
     * @Description : firmware Gaget Main 페이지.
     * jsp : firmwareMgmtMax.jsp
     */
	@RequestMapping(value="/gadget/device/firmware/firmwareMgmtMaxGadget")
    public ModelAndView firmWareMainGadget() {
		//log.error(this.getClass().getName()+".firmWareMainGadget() start..");
		ModelAndView mav = new ModelAndView("/gadget/device/firmware/firmwareMgmtMax");
		AimirAuthenticator instance = (AimirAuthenticator) ESAPI.authenticator();
		AimirUser user = (AimirUser) instance.getUserFromSession();

		Role role = roleManager.getRole(user.getRoleData().getId());
		Map<String, Object> authMap = CommonUtils.getAllAuthorityByRole(role);

		mav.addObject("cmdAuth", authMap.get("command"));  // Command 권한(command = true)
        return mav;
    }

    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 모델별 Tree ,Codi추가
    * @return : 모델리스트 
    */
	@SuppressWarnings({ "rawtypes", "unused" })
    @RequestMapping(value = "/gadget/device/firmware/firmwaretree")
	public ModelAndView getModelTree(@RequestParam("supplierId") int supplierId) {
		//log.error(this.getClass().getName()+".getModelTree() start..");
		ModelAndView mav = new ModelAndView("jsonView");

		List<JsonTree> jsonTrees = new ArrayList<JsonTree>();
		List<Code> children = codeManager.getChildCodes("1");

		List modelList = null;
		boolean itemAdd = false;

		for (Code code : children) {
			JsonTree jsonTree = new JsonTree();	
			List devicetypeList  =null;
			
			devicetypeList = getChildList(code);
			
			if(devicetypeList.size() ==0)
				continue;
				
			jsonTree.setData(code);
			//int f = devicetypeList.size();
			//devicetypeList.remove(f);
			modelList = getChildList(supplierId, devicetypeList);

			List vendors = new ArrayList();
			List models = new ArrayList();
			List configs = new ArrayList();
			getChildList(modelList, vendors, models, configs);

			jsonTree.setChildren3(configs);
			jsonTree.setChildren2(models);
			jsonTree.setChildren1(vendors);
			jsonTree.setChildren(devicetypeList);

			jsonTrees.add(jsonTree);

		}
		JsonTree unknownJsonTree = getUnknown(supplierId);

		jsonTrees.add(unknownJsonTree);

		// if (itemAdd) {
		mav.addObject("jsonTrees", jsonTrees);
		// } else {
		// mav.addObject("jsonTrees", new ArrayList<JsonTree>());
		// }
		return mav;
	}
	
    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포 상세페이지/PaingBar 생성
    * @return : DivinnerHtml String
    */
	@RequestMapping(value="/gadget/device/firmware/getFirmwareList")
    public ModelAndView getFirmwareList(
    								@RequestParam("equip_model_id") String equip_model_id,
    								@RequestParam("supplierId") String supplierId,    								
    								@RequestParam("currPage") String currPage 								
    								){
		//log.error(this.getClass().getName()+".getFirmwareList() start..");
		StringTokenizer equipStr = new java.util.StringTokenizer(equip_model_id,"_");
		String equip_kind = "";
		String devicemodel_id = "";
		String equip_type = "";
		int curPage = Integer.parseInt(currPage);
		int pageCnt = 5;//start에서 2개 더 출력 between과 비슷한 개념 즉 start num에서 5개를 더 출력
		int tokInt = 0;
		
		if(equip_model_id.indexOf("Codi") > -1){
			equip_kind = "Codi";
			equip_type = "Codi";
			if(equip_model_id.indexOf("_") > -1){
				equip_model_id = equip_model_id.replace("Codi", "");
				devicemodel_id = equip_model_id.replace("_", "");
			}else{
				devicemodel_id = equip_model_id.replace("Codi", "");	
			}
			
		}else{
	        while(equipStr.hasMoreTokens()){
	        	String str = equipStr.nextToken();
	        	if(tokInt == 0) equip_kind = str;
	        	else if(tokInt == 1) devicemodel_id = str;
	        	else equip_type = str;
	        	tokInt++;
	        }
		}

		/**
		 * evicemodel_id 조건으로 배포 리스트 조회 (Paging처리를 위해서 전체 COUNT를 가지고 온다.)
		 **/
		int rowCnt = Integer.parseInt(firmWareManager.getFirmwareListCNT(equip_kind,devicemodel_id,supplierId,equip_type));
		
	    //Paging 처리를 미리 만듦
		//totalCnt를 변수에 담아서 가지고 다녀도 되고,, 호출 시마다 데이터를 불러 와도 됨.. 버퍼가 그리 많지 않아 그냥 불러옴
		//rowCnt , curPage, pageCnt, rowCnt2
	    FirmWarePageTreat pp = new FirmWarePageTreat (rowCnt,curPage,pageCnt,rowCnt,"basic_html"); 
	    String paingStr = pp.getNavigation();		
	    
		String firstResults = String.valueOf(pp.getStart());
		String maxResults = String.valueOf(pageCnt);//start에서 2개 더 출력 between과 비슷한 개념 즉 start num에서 5개를 더 출력 

		/**
		 * evicemodel_id 조건으로 배포 리스트 조회 
		 * */
		List<Object> firmwareList = firmWareManager.getFirmwareList(equip_kind,devicemodel_id,firstResults,maxResults,supplierId,equip_type);
/*		String mcuBuild = "";
		if(equip_kind.equals("Modem")){
			mcuBuild = firmWareManager.getMcuIdbyModemModelID(devicemodel_id,supplierId,equip_type);	
		}*/
		
	    
		int i = 0;
	    for (Object obj : firmwareList) {
	        Object[] objs = (Object[])obj;
	        
	        int len = objs.length;
	        Object[] objs2 = new Object[len+6];
	        String sys_sw_revision 	= "";//--build
	        String sys_sw_version 	= "";
	        String sys_hw_version 	= "";
	        String firmware_id 		= "";
	        String arm 				= "";
	        String writer 			= "";
	        

	        for (int k = 0; k < len; k++) {
	        	objs2[k] = String.valueOf(objs[k]);
	        	if(k == 0){
	        		sys_hw_version = (String.valueOf(objs[k])).equals("")?"unKown":String.valueOf(objs[k]);
	        	}else if(k == 1){
	        		sys_sw_version = (String.valueOf(objs[k])).equals("")?"unKown":String.valueOf(objs[k]);
	        	}else if(k == 2){
	        		sys_sw_revision = String.valueOf(objs[k]);
	        	}else if(k == 3){
	        		arm = String.valueOf(objs[k]);
	        	}else if(k == 5){
	        		firmware_id = String.valueOf(objs[k]);
	        	}else if(k == 8){
	        		writer = String.valueOf(objs[k]);
	        	}
	        }
	         
	         //불러온 값을 새로운 object에 입력한다.(total, error등의 통계값을 가지고 와서 add 하기 위함.
		     HashMap<String, Object> param = new HashMap<String, Object>();
		     param.put("hw_version", sys_hw_version);
		     param.put("sw_version", sys_sw_version);
		     param.put("build", sys_sw_revision);	 
		     param.put("firmware_id", firmware_id);
		     param.put("supplierId", supplierId);
		     param.put("equip_kind", equip_kind);
		     param.put("devicemodel_id", devicemodel_id);
		     param.put("equip_type", equip_type);
		     param.put("arm", arm);
		     param.put("writer", writer);
	        
		 	/**
		 	 * 배포 리스트 조회에서 Equip Total 을 따로 조회 하는 쿼리 
		 	 * */
		     String equipCnt = getEquipCnt(param);
		     List<Object> staticList = getStatisticsStr(param);
		     int totleng = len;		
	         objs2[totleng] = equipCnt ;
	         
	         if(staticList.size()>0){
	        	 //state 값을 배열에 추가
		         for (Object statobj : staticList) {
			 	        Object[] statobjs = (Object[])statobj;
			 	        int statlen = statobjs.length;

			 	        for (int k = 1; k < statlen; k++) {
			 	        	totleng++;
			 	        	objs2[totleng] = (String.valueOf(statobjs[k])) ;	   
			 	        }
			      }	         
	         }else{
	        	 //state값이 없을경우 모두 0으로 세팅
	        	 for(int ii=0; ii< 5 ; ii++){
	        		 totleng++;
	        		 objs2[totleng] = 0 ;
	        	 }
	         }
	        firmwareList.set(i, objs2);
	        i++;
	     }
		//int devicecd  = CommonConstants.DeviceType.valueOf("MCU").getCode();
	    //FirmWareMgmMax.jsp에서 innerHtml로 사용할 html문구를 만들어 던짐(jsp에서 만드는 것보다 controller에서 만들어 주는것이 더 깔끔하고, 사용하기 편함).
	    StringBuffer firmwareListDiv = new StringBuffer();
	    //innerHtml을 FirmWareMakeInnerHtml 에서 관리.. make해서 return 받아 옴
	    FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml();
	    firmwareListDiv = fwm.getFirmwareList(firmwareList,equip_type);
	    
		ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("firmwareListMakeHtml", firmwareListDiv.toString());    
        mav.addObject("firmwareListpaingStrMakeHtml", paingStr);
        mav.addObject("searchResult", firmwareList.size());
        
		return mav;
    }	

    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포파일관리/PaingBar 생성
    * @return : DivinnerHtml String
    */
	@RequestMapping(value="/gadget/device/firmware/getFirmwareFileMgmList")
    public ModelAndView getFirmwareFileMgmList(
    								@RequestParam("equip_model_id") String equip_model_id,
    								@RequestParam("supplierId") String supplierId,    								
    								@RequestParam("currPage") String currPage 								
    								){
		//log.error(this.getClass().getName()+".getFirmwareFileMgmList() start..");
		StringTokenizer equipStr = new java.util.StringTokenizer(equip_model_id,"_");
		String equip_kind = "";
		String devicemodel_id = "";
		String equip_type = "";
		int curPage = Integer.parseInt(currPage);
		int pageCnt = 5;//start에서 2개 더 출력 between과 비슷한 개념 즉 start num에서 5개를 더 출력
		int tokInt = 0;
		
		
		if(equip_model_id.indexOf("Codi") > -1){
			equip_kind = "Codi";
			equip_type = "Codi";
			if(equip_model_id.indexOf("_") > -1){
				equip_model_id = equip_model_id.replace("Codi", "");
				devicemodel_id = equip_model_id.replace("_", "");
			}else{
				devicemodel_id = equip_model_id.replace("Codi", "");	
			}
			
		}else{
	        while(equipStr.hasMoreTokens()){
	        	String str = equipStr.nextToken();
	        	if(tokInt == 0) equip_kind = str;
	        	else if(tokInt == 1) devicemodel_id = str;
	        	else equip_type = str;
	        	tokInt++;
	        }
		}

		/**
		 * evicemodel_id 조건으로 배포 리스트 조회 (Paging처리를 위해서 전체 COUNT를 가지고 온다.)
		 **/
		int rowCnt = Integer.parseInt(firmWareManager.getFirmwareFileMgmListCNT(equip_kind,devicemodel_id,supplierId,equip_type));
		
	    //Paging 처리를 미리 만듦
		//totalCnt를 변수에 담아서 가지고 다녀도 되고,, 호출 시마다 데이터를 불러 와도 됨.. 버퍼가 그리 많지 않아 그냥 불러옴
		//rowCnt , curPage, pageCnt, rowCnt2
	    FirmWarePageTreat pp = new FirmWarePageTreat (rowCnt,curPage,pageCnt,rowCnt,"basic_html2"); 
	    String paingStr = pp.getNavigation();		
	    
		String firstResults = String.valueOf(pp.getStart());
		String maxResults = String.valueOf(pageCnt);//start에서 2개 더 출력 between과 비슷한 개념 즉 start num에서 5개를 더 출력 

		/**
		 * evicemodel_id 조건으로 배포 리스트 조회 
		 * */  
		List<Object> firmwareMgmList = firmWareManager.getFirmwareFileMgmList(equip_kind,devicemodel_id,firstResults,maxResults,supplierId,equip_type); 
		//int devicecd  = CommonConstants.DeviceType.valueOf("MCU").getCode();
	    //FirmWareMgmMax.jsp에서 innerHtml로 사용할 html문구를 만들어 던짐(jsp에서 만드는 것보다 controller에서 만들어 주는것이 더 깔끔하고, 사용하기 편함).
	    StringBuffer firmwareListDiv = new StringBuffer();
	    //innerHtml을 FirmWareMakeInnerHtml 에서 관리.. make해서 return 받아 옴
	    FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml();
	    firmwareListDiv = fwm.getFirmwareMngList(firmwareMgmList,equip_type,curPage,pp.getRowCnt());
	    
		ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("firmwareListMakeHtml", firmwareListDiv.toString());    
        mav.addObject("firmwareListpaingStrMakeHtml", paingStr); 
        mav.addObject("searchResult", firmwareMgmList.size());
        
		return mav;
    }
	
    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포파일관리/PaingBar 생성
    * @return : DivinnerHtml String
    */
	@RequestMapping(value="/gadget/device/firmware/getMcuBuild")
    public ModelAndView getMcuBuild(
    								@RequestParam("equip_type") String equip_type,
    								@RequestParam("supplierId") String supplierId,    								
    								@RequestParam("oldHwVersion") String oldHwVersion, 								
    								@RequestParam("oldFwVersion") String oldFwVersion, 								
    								@RequestParam("oldBuild") String oldBuild, 								
    								@RequestParam("model_id") String model_id, 								
    								@RequestParam("equip_kind") String equip_kind 								
    								){
		//log.error(this.getClass().getName()+".getMcuBuild() start..");
		
	     HashMap<String, Object> param = new HashMap<String, Object>();
	     param.put("hw_version", oldHwVersion);
	     param.put("sw_version", oldFwVersion);
	     param.put("build", oldBuild);	 
	     param.put("supplierId", supplierId);
	     param.put("model_id", model_id);
	     param.put("equip_type", equip_type);
	     param.put("equip_kind", equip_kind);
		
		String mcuBuild = firmWareManager.getMcuBuildByFirmware(param);
		
		log.info("mcuBuild=============================================="+mcuBuild);
	    
		ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("mcuBuild", mcuBuild);
        
		return mav;
    }
	
    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포내역 TriggerList Step1/PaingBar 생성
    * @return : DivinnerHtml String
    */
	@RequestMapping(value="/gadget/device/firmware/getTriggerListStep1")
    public ModelAndView getTriggerListStep1(
										@RequestParam("trEquip_kind") String trEquip_kind,
										@RequestParam("trEquip_Type") String trEquip_Type,    								
										@RequestParam("trVendor") String trVendor,
										@RequestParam("trModel") String trModel,
										@RequestParam("trState") String trState,
										@RequestParam("trTriggerID") String trTriggerID,
										@RequestParam("trEquipID") String trEquipID,
										@RequestParam("trfromDate") String trfromDate,
										@RequestParam("trtoDate") String trtoDate,
										@RequestParam("trLocationId") String trLocationId, 								
										@RequestParam("curPage") String varcurPage 								
    								){
		//log.error(this.getClass().getName()+".getTriggerListStep1() start..");
		ModelAndView mav = new ModelAndView("jsonView");
		
		int curPage = Integer.parseInt(varcurPage);//항상 0보다 큰 값으로 시작해야함 0page는 없기때문
		int pageCnt = 5;//start에서 2개 더 출력 between과 비슷한 개념 즉 start num에서 5개를 더 출력
		int rowCnt = 0;
		String paingStr = "";
		List<Object> triggerStep1List = null;
		StringBuffer triggerStep1ListDiv = new StringBuffer();
		
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("equip_kind",trEquip_kind);	    
        condition.put("equip_type",trEquip_Type);	    
        condition.put("equip_vendor",trVendor);	    
        condition.put("equip_model",trModel);	    
        condition.put("state",trState);	    
        condition.put("trId",trTriggerID);	    
        condition.put("equip_id",trEquipID);	    
        condition.put("fromDate",trfromDate);	    
        condition.put("toDate",trtoDate);	    
        condition.put("locationId",trLocationId);	   
		
		try {
			rowCnt = Integer.parseInt(firmWareManager.getTriggerListStep1CNT(condition));
			FirmWarePageTreat pp = new FirmWarePageTreat (rowCnt,curPage,pageCnt,rowCnt,"triggerstep1"); 
		    paingStr = pp.getNavigation();		
	        condition.put("firstResults",String.valueOf(pp.getStart()));	    
	        condition.put("maxResults",String.valueOf(pageCnt));
			triggerStep1List = firmWareManager.getTriggerListStep1(condition); 
		    //innerHtml을 FirmWareMakeInnerHtml 에서 관리.. make해서 return 받아 옴
		    FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml();
		    triggerStep1ListDiv = fwm.getTriggerListStep1(triggerStep1List,condition,pp,curPage);	        	
		} catch (NumberFormatException e) {
			e.printStackTrace();
			mav.addObject("rtnStr", null);
		} catch (Exception e) {
			e.printStackTrace();
	        mav.addObject("rtnStr", null);
		}
	    
		mav.addObject("rtnStr","success");
        mav.addObject("triggerListStep1InnerHtml", triggerStep1ListDiv.toString());    
        mav.addObject("paingStr", paingStr);    
		return mav;
    }
	
    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포내역 TriggerList>TriggerInfo Step2 생성
    * @return : DivinnerHtml String
    */
	@SuppressWarnings("rawtypes")
    @RequestMapping(value="/gadget/device/firmware/getTriggerListStep2")
    public ModelAndView getTriggerListStep2(
										@RequestParam("trTriggerID") String trTriggerID,
										@RequestParam("trLocationId") String trLocationId, 								
										@RequestParam("trEquip_Kind") String trEquip_Kind,								
										@RequestParam("trExec") String trExec,					
										@RequestParam("trequip_type") String trequip_type,
										@RequestParam(value="cmdAuth", required=false) String cmdAuth
    								){
		//log.error(this.getClass().getName()+".getTriggerListStep2() start..");
		ModelAndView mav = new ModelAndView("jsonView");
		
		List<Object> triggerStep2List = null;
		StringBuffer triggerStep2ListDiv = new StringBuffer();
		String triggerStep3ListDiv = "";
		String triggerStep3ListDivSucc = "";
		String triggerStep3ListDivCancel = "";
		String triggerStep3ListDivError = ""; 
		String triggerStep3ListDivExec = "";
		
        HashMap<String, Object> condition = new HashMap<String, Object>();
        condition.put("tr_Id",trTriggerID);	    
        condition.put("locationId",trLocationId);	   
        condition.put("equip_kind",trEquip_Kind);
        condition.put("trExec",trExec);	   
        condition.put("equip_type",trequip_type);	   
		
		try {
			triggerStep2List = firmWareManager.getTriggerListStep2(condition); 
		    //innerHtml을 FirmWareMakeInnerHtml 에서 관리.. make해서 return 받아 옴
//		    FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml();
		    FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml(cmdAuth);
		    ArrayList returnArr = fwm.getTriggerListStep2(triggerStep2List,condition);
		    triggerStep2ListDiv = (StringBuffer) returnArr.get(0);
		    ArrayList al = fwm.getTriggerListStep3((ArrayList) returnArr.get(1),trequip_type);
		    triggerStep3ListDiv = String.valueOf(al.get(0));
		    triggerStep3ListDivSucc = String.valueOf(al.get(1));
		    triggerStep3ListDivCancel = String.valueOf(al.get(2));
		    triggerStep3ListDivError = String.valueOf(al.get(3));
		    triggerStep3ListDivExec = String.valueOf(al.get(4));
		    
		} catch (NumberFormatException e) {
			e.printStackTrace();
			mav.addObject("rtnStr", null);
		} catch (Exception e) {
			e.printStackTrace();
	        mav.addObject("rtnStr", null);
		}
	    
		mav.addObject("rtnStr","success");
        mav.addObject("triggerListStep2InnerHtml", triggerStep2ListDiv.toString());    
        mav.addObject("triggerListStep3InnerHtml", triggerStep3ListDiv);    
        mav.addObject("triggerStep3ListDivSucc", triggerStep3ListDivSucc);    
        mav.addObject("triggerStep3ListDivCancell", triggerStep3ListDivCancel);    
        mav.addObject("triggerStep3ListDivError", triggerStep3ListDivError);    
        mav.addObject("triggerStep3ListDivExec", triggerStep3ListDivExec);    
		return mav;
    }

    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포내역 TriggerList>TriggerInfo Step2 초기화
    * @return : DivinnerHtml String
    */
	@RequestMapping(value="/gadget/device/firmware/resetInnerHtml")
    public ModelAndView resetInnerHtml(		
    								@RequestParam("htmlGubun") String gubun
    								){
		//log.error(this.getClass().getName()+".resetInnerHtml() start..");
		ModelAndView mav = new ModelAndView("jsonView");
		
	 
	    //innerHtml을 FirmWareMakeInnerHtml 에서 관리.. make해서 return 받아 옴
		
		String returnInnerHtml = "";
		if(gubun.equals("triggerListStep1Div")){
			returnInnerHtml = FirmWareMakeInnerHtml.triggerListStep1Div;
		}else if(gubun.equals("triggerListStep2Div")){
			returnInnerHtml = FirmWareMakeInnerHtml.triggerListStep2Div;
		}else if(gubun.equals("triggerListStep3Div")){
			returnInnerHtml = FirmWareMakeInnerHtml.triggerListStep3Div;
		}
		
		
	    
		mav.addObject("rtnStr","success");
        mav.addObject("returnInnerHtml", returnInnerHtml);    
      
		return mav;
    }	

    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : firmware 배포내역 TriggerList>TriggerInfo Step2 생성
    * @return : DivinnerHtml String
    */
	@RequestMapping(value="/gadget/device/firmware/goRedistStep2")
    public ModelAndView goRedistStep2(
										@RequestParam("varGubun") String gubun,
										@RequestParam("varTargetFirmware") String targetFirmware, 								
										@RequestParam("varTr_id") String tr_id,							
										@RequestParam("varEquip_kind") String equip_kind,
										@RequestParam("vartrigger_step") String trigger_step, 								
										@RequestParam("vartrigger_state") String trigger_state, 								
										@RequestParam("varota_step") String ota_step,						
										@RequestParam("varota_state") String ota_state, 
										@RequestParam("varmcu_id") String mcu_id,
										@RequestParam("varequip_type") String equip_type
    								){
		//log.error(this.getClass().getName()+".goRedistStep2() start..");
		List<Object> reDistList = null;
		ModelAndView mav = new ModelAndView("jsonView");
		HashMap<String, Object> condition = new HashMap<String, Object>();
		condition.put("gubun", gubun);
		condition.put("target_firmware", targetFirmware);
		condition.put("tr_id", tr_id);
		condition.put("equip_kind", equip_kind);
		condition.put("trigger_step", trigger_step);
		condition.put("trigger_state", trigger_state);
		condition.put("ota_step", ota_step);
		condition.put("ota_state", ota_state);
		condition.put("mcu_id", mcu_id);
		condition.put("equip_type", equip_type);
		
		try{
			reDistList = firmWareManager.getReDistList(condition);
			mav.addObject("rtnStr", "success");
			mav.addObject("reDistList", reDistList);
		}catch(Exception e){
			e.printStackTrace();
			mav.addObject("rtnStr", "fail");
		}
		
		

		return mav;
    }	
	
	
	/**
     * @param       : json Call Param
     * @exception   : 
     * @Date        : 2010/12/10
     * @Description : firmware 추가 페이지 파일 업로드
     * jsp: firmwraeMgmtMaxGaget.jsp
     */
	@SuppressWarnings({ "unused", "null" })
    @RequestMapping(value="/gadget/device/firmware/file_upload.do", method = RequestMethod.POST)
    public ModelAndView file_upload(HttpServletRequest request, HttpServletResponse response)throws ServletRequestBindingException, IOException{
			ModelAndView mav = new ModelAndView("gadget/device/firmware/firmWareMainFileResponseForm");
			//log.error(this.getClass().getName()+".file_upload() start..");
			String contextRoot = new HttpServletRequestWrapper(request).getRealPath("/");
		
			response.setHeader("Content-Transfer-Encoding", "binary;");//binary 형식으로 넘겨줘야 하기에 header에 binary 선언.
			
			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest)request;
			MultipartFile multipartFile = multiReq.getFile("newfile");
			
			//jsp 에서 가지고온 paramt세팅
			Boolean  isOverwrite =  Boolean.parseBoolean(multiReq.getParameter("isOverwrite").equals("")?"false":multiReq.getParameter("isOverwrite"));//overwrite할지 여부, defualt는 false
			Boolean  isOverwriteCheck = Boolean.parseBoolean(multiReq.getParameter("isOverwriteCheck").equals("")?"false":multiReq.getParameter("isOverwriteCheck"));
			String  build = multiReq.getParameter("addFrmBuild");
			String  hwVersion = multiReq.getParameter("addFrmHwVersion");
			String  fwVersion = multiReq.getParameter("addFrmSwVersion");
			String  vendor = multiReq.getParameter("addFrmVendor");
			String  model = multiReq.getParameter("addFrmModel");
			String  installStartDate = multiReq.getParameter("realInstallStartDate");
			//String  constBuild = multiReq.getParameter("addFrmConstBuild");
			String  title = multiReq.getParameter("addFrmTitle");
			String  content = multiReq.getParameter("addFrmContent");
			String  arm = multiReq.getParameter("addFrmArm").equals("0")?"false":"true";
			String  equip_kind = multiReq.getParameter("addFrmEquip_kind");
			String  equip_type = multiReq.getParameter("addFrmEquip_type");
			String  supplierId = multiReq.getParameter("supplierId");
			String  firmwareId = multiReq.getParameter("addFrmFirmwareid");
			String  modelId = multiReq.getParameter("model_id");
			
			int equip_kindCd = 0;
			int equip_typeCd = 0;
			if(equip_kind.equals("Modem")){
				equip_kindCd = CommonConstants.DeviceType.valueOf(equip_kind).getCode();
				equip_typeCd = CommonConstants.ModemType.valueOf(equip_type).getCode();
			}else if(equip_kind.equals("MCU")){
				equip_kindCd = CommonConstants.DeviceType.valueOf(equip_kind).getCode();
				equip_typeCd = CommonConstants.McuType.valueOf(equip_type).getCode();
			}
			
	        String osName = System.getProperty("os.name");
		    String firmwareDir = "";
		    String firmwareTempDir = "";
	        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
	        	firmwareDir = CommandProperty.getProperty("firmware.window.tooldir");
	        	firmwareTempDir = CommandProperty.getProperty("firmware.window.tempdir");
	        }else{
	        	firmwareDir = CommandProperty.getProperty("firmware.tooldir");
	        	firmwareTempDir = CommandProperty.getProperty("firmware.tempdir");
	        }
			//파일을 저장할 디렉토리와 임시디렉토리를 properties에서 가지고 온다.

			String orignFilename = multipartFile.getOriginalFilename();//로컬에서 선택한 파일명
           
			//저장시킬 디렉토리가 없으면 미리 생성한다 ( 임시 디렉토리에서 firmwareDir 디렉토리로 copy 할것이기 때문)
	        File firmwareDirFile = new File(firmwareDir);
	        if (!firmwareDirFile.exists()){
	            firmwareDirFile.mkdir();
	        }
	        
	        File firmwareTempDirFile = new File(firmwareTempDir);
	        if (!firmwareTempDirFile.exists()){
	        	firmwareTempDirFile.mkdir();
	        }
			
			//먼저 임시폴더에 데이터를 저장시킨다. 하단에서 파일을 다른곳으로 옮김.
			File uFile =new File(FileUploadHelper.makePath(firmwareTempDir, orignFilename));
			multipartFile.transferTo(uFile);

			//로컬에서 선택한 파일명의 확장자를 가지고 온다.
			String extension  = orignFilename.substring(orignFilename.indexOf("."));



	        //경로+파일명을 변수로 가지고 간다(아래에서 copy실행할려면 전체 경로가 필요하기 때문)
		    String fullFilename = (firmwareDir+File.separator+orignFilename).replace(" ", "");
	        String firmwareRealDir = firmwareDir;
	        
		    //binaryfilename이름의 맨 앞자리에 디폴트로 선언하기 위해 properties에서 값을 가지고 온다(별의미는 없는 값)
			String firmwareDefFilename = CommandProperty.getProperty("firmware.filename.defaultStart");//"SWAMM"; //★★properties를 확인하여  수정 을 요함...
			String oldFileNameConst = null; //하단 파일 DIFF 때 사용할 변수 

	        //파일명 재정의 (SWAMM_0_3___2.0_3.1_4120_false.tar.gz)
			//장비 종류, 타입, 벤더, 모델, 하드웨어버전, 소프트웨어버전, 빌드, arm
			StringBuffer  strBuff = new StringBuffer();
			if(!firmwareDefFilename.equals("") || firmwareDefFilename != null) strBuff.append(firmwareDefFilename+"_");
			strBuff.append(equip_kindCd+"_");
			strBuff.append(equip_typeCd+"_");
			if(!vendor.equals("") || vendor != null) strBuff.append(vendor.replace(" ", "")+"_");
			if(!model.equals("") || model != null) strBuff.append(model.replace(" ", "")+"_");
			oldFileNameConst = (strBuff.toString()).substring(0,strBuff.length()-1);
			if( hwVersion != null || !hwVersion.equals("")) strBuff.append(hwVersion+"_");
			if( fwVersion != null || !fwVersion.equals("")) strBuff.append(fwVersion+"_");
			if( build != null || !build.equals("")) strBuff.append(build+"_");
			if( arm != null || !arm.equals("") ) strBuff.append(arm);
			
			firmwareRealDir += File.separator + strBuff.toString();
			File firmwareRealDirFile = new File(firmwareRealDir);
			
			//SWAMM_0_4_1111_111112_2.0_3.1_3696_false 형식으로 폴더를 생성
			//폴더가없으면 생성 , 있으면 물어보고 지우고 생성할지, 안할지 결정해야 함.
			if(isOverwrite){
				//화면상 덮어쓸까요?에서 yes를 눌렀을 때 
		            firmwareRealDirFile.delete();
		            firmwareRealDirFile.mkdir();
			}else{//isOverwriteCheck 가 초기값:false 일경우 반드시 덮어쓸지 말지 물어보는 리턴을 실행한다.
				//기존에 폴더가 있는지 물어본다. 없을때는 return시킬 필요가 없다.
				if(firmwareRealDirFile.exists()){
					if(!isOverwrite){
						mav.addObject("savefilename", "|");
						mav.addObject("isOverwriteCheck", "true");
						return mav;
			        }			
				}else{//폴더가 기존에 존재하지 않으니 당연히 폴더를 생성 
					firmwareRealDirFile.delete();
		            firmwareRealDirFile.mkdir();
				}
			}	
			
	        File firmwareRealFile = null;
	        String savefilename = "";
	        if(fullFilename.toLowerCase().indexOf("tar.gz")>-1){
				// File Copy & Temp File Delete
	        	savefilename = strBuff.toString() + ".tar.gz";
				FileUploadHelper.copy(FileUploadHelper.makePath(firmwareTempDir, orignFilename), FileUploadHelper.makePath(firmwareRealDir, savefilename.replace(" ","")));	
				FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(firmwareTempDir, orignFilename));	
	        }else if(fullFilename.toLowerCase().indexOf("ebl")>-1){
	        	// File Copy & Temp File Delete
	        	savefilename = strBuff.toString() + ".ebl";
				FileUploadHelper.copy(FileUploadHelper.makePath(firmwareTempDir, orignFilename), FileUploadHelper.makePath(firmwareRealDir, savefilename.replace(" ","")));	
				FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(firmwareTempDir, orignFilename));	
	        }else{
	        	// File Copy & Temp File Delete
	        	savefilename = strBuff.toString() + orignFilename.substring(orignFilename.lastIndexOf("."),orignFilename.length());
				FileUploadHelper.copy(FileUploadHelper.makePath(firmwareTempDir, orignFilename), FileUploadHelper.makePath(firmwareRealDir, savefilename.replace(" ","")));	
				FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(firmwareTempDir, orignFilename));		        	
	        }
	        //하단 diff 작업을 위해 파일 객체를 생성했음.
	        firmwareRealFile = new File(firmwareRealDir + File.separator + savefilename);

	        /**
	         * 파일 업로드 valid체크 후 펌웨어 테이블에 insert 작업 시작
	         **/
	        String yyyymmdd = installStartDate;
	        
			// ESAPI.setAuthenticator((Authenticator)new AimirAuthenticator());
	        AimirAuthenticator instance = (AimirAuthenticator)ESAPI.authenticator();        
	        AimirUser user = (AimirUser)instance.getUserFromSession();
	        
	        Supplier supplier = user.getRoleData().getSupplier();
/*			//현재 날짜
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String y = String.valueOf(cal.get(Calendar.YEAR));
			String m = String.valueOf(cal.get(Calendar.MONTH)+1);
			if(m.length() == 1) m = "0"+ m;
			String d = String.valueOf(cal.get(Calendar.DATE));
			
			String writeDate = y+m+d;*/
			
			StringBuffer fwId = new StringBuffer();
			
			fwId.append(equip_kindCd+"_");
			fwId.append(StringUtil.nullCheck(String.valueOf(equip_typeCd), "0")+"_");
			fwId.append(StringUtil.nullCheck(vendor, "") +"_");
			fwId.append(StringUtil.nullCheck(model, "") +"_");
			fwId.append(hwVersion+"_");
			fwId.append(fwVersion+"_");
			fwId.append(build+"_");
			fwId.append(arm);
			/*
			String fwId = data.getEquipKind() + "_" + StringUtil.nullCheck(data.getEquipType(), "0") + "_"
			+ StringUtil.nullCheck(data.getEquipVendor(), "") + "_"
			+ StringUtil.nullCheck(data.getEquipModel(), "") + "_"
			+ data.getHwVersion() + "_" + data.getFwVersion() + "_"
			+ data.getBuild()+ "_" + data.isArm();
		*/	
	        /******Firmware******/
			FirmwareMCU firmwaremcu = null;
			FirmwareModem firmwaremodem = null;
			FirmwareCodi firmwarecodi = null;
			if(equip_kind.equals("MCU")){
				firmwaremcu = new FirmwareMCU();
				firmwaremcu.setMcuType(equip_type);
				firmwaremcu.setArm(Boolean.parseBoolean(arm));
				firmwaremcu.setBinaryFileName(firmwareRealFile.getName());
				firmwaremcu.setBuild(build);
				firmwaremcu.setEquipKind(String.valueOf(equip_kind));
				firmwaremcu.setEquipVendor(vendor);
				firmwaremcu.setFwVersion(fwVersion);
				firmwaremcu.setHwVersion(hwVersion);
				firmwaremcu.setReleasedDate(yyyymmdd);
				firmwaremcu.setEquipType(equip_type);
				firmwaremcu.setSupplier(supplier);
				firmwaremcu.setFirmwareId(fwId.toString());
				firmwaremcu.setDevicemodel_id(Integer.parseInt(modelId));
			}else if(equip_kind.equals("Modem")){
				firmwaremodem = new FirmwareModem();
				firmwaremodem.setModemType(equip_type);
				firmwaremodem.setArm(Boolean.parseBoolean(arm));
				firmwaremodem.setBinaryFileName(firmwareRealFile.getName());
				firmwaremodem.setBuild(build);
				firmwaremodem.setEquipKind(String.valueOf(equip_kind));
				firmwaremodem.setEquipVendor(vendor);
				firmwaremodem.setFwVersion(fwVersion);
				firmwaremodem.setHwVersion(hwVersion);
				firmwaremodem.setReleasedDate(yyyymmdd);
				firmwaremodem.setEquipType(equip_type);
				firmwaremodem.setSupplier(supplier);
				firmwaremodem.setFirmwareId(fwId.toString());
				firmwaremodem.setDevicemodel_id(Integer.parseInt(modelId));
				//firmwaremodem.setModel
				//firmwaremodem.setStackName
			}else if(equip_kind.equals("Codi")){
				firmwarecodi = new FirmwareCodi();
				firmwarecodi.setArm(Boolean.parseBoolean(arm));
				firmwarecodi.setBinaryFileName(firmwareRealFile.getName());
				firmwarecodi.setBuild(build);
				firmwarecodi.setEquipKind(String.valueOf(equip_kind));
				firmwarecodi.setEquipVendor(vendor);
				firmwarecodi.setFwVersion(fwVersion);
				firmwarecodi.setHwVersion(hwVersion);
				firmwarecodi.setReleasedDate(yyyymmdd);
				firmwarecodi.setEquipType(equip_type);
				firmwarecodi.setSupplier(supplier);
				firmwarecodi.setFirmwareId(fwId.toString());
				//firmwarecodi.setDevicemodel_id(Integer.parseInt(modelId));
			}
			
	      	/*
			 * ★★ FIRMWARE 테이블에 MCU_TYPE 컬럼이 있지만. EQUYP_TYPE 컬럼과 중복되어 보여 추가하지 않았음. 확인 필요..
			 * */
			/******FirmwareBoard******/
	        FirmwareBoard firmwareBoard = new FirmwareBoard();
			firmwareBoard.setContent(content);
			firmwareBoard.setTitle(title);
			firmwareBoard.setWriteDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
			firmwareBoard.setReadCount(0);
			firmwareBoard.setSupplier(supplier);
			if(equip_kind.equals("MCU")){
				firmwareBoard.setFirmware(firmwaremcu);
			}else if(equip_kind.equals("Modem")){
				firmwareBoard.setFirmware(firmwaremodem);
			}else if(equip_kind.equals("Codi")){
				firmwareBoard.setFirmware(firmwarecodi);
			}
			try {
				firmwareBoard.setOperator((Operator)user.getOperator(new Operator()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/*2.0 AddFirmware.java 기능 */
			//constraints 분리 작업 2.0(3.1_3696_false),2.0(3.1_4155_false),
			//codi는 encodeCodiVer 작업필요 
			
			/*****
			 *   ★★ CONSTRAINTS INSERT작업은 박연경과장 에게 다시 물어보고 작업할것
			 * 	   필요할지 안필요할지 확정되지 않았음........<박연경 과장 요청>
			 * ******/
/*			
			StringTokenizer equipStr = new java.util.StringTokenizer(constBuild,",");
			ArrayList arrCnstList = new ArrayList();
	        while(equipStr.hasMoreTokens()){
	        	String str = equipStr.nextToken();
	        	arrCnstList.add(str); //str: 2.0(3.1_3696_false) 형식으로 분리되 add 함.
	        }
*/	        /*	
	        for(int i=0; i<arrCnstList.size(); i++){
	        	String str = ((String) arrCnstList.get(i)).trim();
	        	String cnHwVersion = str.substring(0,str.indexOf("("));
	        	String cnFwVersion = str.substring(str.indexOf("(")+1,str.indexOf("_"));
	        	String cnBuild = str.substring(str.indexOf("_")+1,str.lastIndexOf("_"));
		        *//******FirmwareConstraints******//*
		        FirmwareConstraints firmwareConst = new FirmwareConstraints();
		        firmwareConst.setBuild(cnBuild);
		        firmwareConst.setHwVersion(cnHwVersion);
		        firmwareConst.setFwVersion(cnFwVersion);
		        firmwareConst.setFirmwareId(firmwareId);

		        //firmwareConst.setContainMent(containMent);
		        //firmwareConst.setFirmwareconstraintsId(firmwareconstraintsId);		        
		        //firmwareConst.setInstanceName(instanceName)//FIRMWAREID로 기능을 대체 할 수 있을거 같음. MODEL에 컬럼 선언 하였지만. 

	        }*/
			
			boolean result = false;
			try{
				
				//펌웨어테이블에 같은 버젼이 있는지 체크
				String isExistFW = firmWareManager.checkExistFirmware(equip_kind,equip_type,hwVersion,fwVersion,build,arm,vendor, modelId);
				
				System.out.println("isExistFW============================="+isExistFW);
				
				if(!isExistFW.equals("")||isExistFW==null){//Overwrite는 insert가 아닌 update 하여야 함.
					String ckfwId = isExistFW.substring(0,isExistFW.indexOf("|"));
					String ckfwBoardId = isExistFW.substring(isExistFW.indexOf("|")+1, isExistFW.length());
					if(equip_kind.equals("MCU")){					
						firmwaremcu.setId(Integer.parseInt(ckfwId));
					    firmwareBoard.setFirmware(firmwaremcu);
					    firmwareBoard.setId(Integer.parseInt(ckfwBoardId));
						firmWareManager.updateFirmWareMCU(firmwaremcu,firmwareBoard);
					}else if(equip_kind.equals("Modem")){
						firmwaremodem.setId(Integer.parseInt(ckfwId));
					    firmwareBoard.setFirmware(firmwaremodem);
					    firmwareBoard.setId(Integer.parseInt(ckfwBoardId));
						firmWareManager.updateFirmWareModem(firmwaremodem,firmwareBoard);
					}else if(equip_kind.equals("Codi")){
						firmwarecodi.setId(Integer.parseInt(ckfwId));
					    firmwareBoard.setFirmware(firmwarecodi);
					    firmwareBoard.setId(Integer.parseInt(ckfwBoardId));
						firmWareManager.updateFirmWareCodi(firmwarecodi,firmwareBoard);
					}					
					
				}else{
					if(equip_kind.equals("MCU")){
						firmWareManager.addFirmWareMCU(firmwaremcu,firmwareBoard);
					}else if(equip_kind.equals("Modem")){
						firmWareManager.addFirmWareModem(firmwaremodem,firmwareBoard);
					}else if(equip_kind.equals("Codi")){
						firmWareManager.addFirmWareCodi(firmwarecodi,firmwareBoard);
					}					
				}
				result = true;
			}catch (Exception e) {
				e.printStackTrace();
				//등록한 파일 삭제 로직 추가 필요
				FileUploadHelper.removeExistingFile(FileUploadHelper.makePath(firmwareRealDir, savefilename));	
				mav.addObject("savefilename", "");
				mav.addObject("isOverwriteCheck", "펌웨어테이블에 데이터를 입력시 에러가 발생하였습니다.");
				return mav;
			}
			if(result){
				// diff generate
				String newFileName = null;
				if(savefilename.toLowerCase().indexOf("tar.gz")>-1){
					 GZIPInputStream inGZIP = new GZIPInputStream(new FileInputStream(firmwareRealFile));
					 //System.out.println("firmwareRealFile.getName()="+firmwareRealFile.getName());
					 OutputStream outTar = new FileOutputStream(firmwareRealFile.getAbsolutePath().replaceAll(".tar.gz",".tar"));
		                byte[] buf = new byte[1024];
		                int len;
		                while ((len = inGZIP.read(buf)) > 0) {
		                    outTar.write(buf, 0, len);
		                }
		                inGZIP.close();
		                outTar.close();
		                newFileName = firmwareDir + File.separator +  firmwareRealFile.getName().replaceAll(".tar.gz","")
                        + File.separator + firmwareRealFile.getName().replaceAll(".tar.gz",".tar");
				}else if(savefilename.toLowerCase().indexOf("ebl")>-1 || savefilename.toLowerCase().indexOf("bin")>-1){
	            	//----------------
	            	// Save As Gzip
	            	//----------------
					boolean enableGzip = "true".equals(CommandProperty.getProperty("firmware.enableGzip")) ? true:false;
	            	if(enableGzip){
	            		FileInputStream fis = new FileInputStream(firmwareRealFile);
	            		FileOutputStream fos = new FileOutputStream(firmwareRealFile.getAbsolutePath() + ".gz");
	            		GZIPOutputStream gos = new GZIPOutputStream(fos);
	            		byte[] buf = new byte[1024];	
	            		for (int i; (i = fis.read(buf)) != -1;) {
	            			gos.write(buf, 0, i);
	            		}
	            		gos.close();
	            		fis.close();
	            	}
	                newFileName = firmwareDir + File.separator + firmwareRealFile.getName().replaceAll(".ebl","").replaceAll(".bin","")
	                            + File.separator + firmwareRealFile.getName();
	            }else{
	                newFileName = firmwareDir + File.separator + firmwareRealFile.getName().replaceAll(firmwareRealFile.getName().substring(firmwareRealFile.getName().lastIndexOf("."),firmwareRealFile.getName().length()),"")
                    + File.separator + firmwareRealFile.getName();
	            }
				
				/**
				 * ★★ DIFF GENERATE 관련 로직은 주석처리 (박연경과장, 정훈 과장 요청으로 배포시만 생성하기로 하였음)
				 */
	            //DIFF GENERATE
				/**
				 * ★★ Constraints 관련 로직은 주석처리 
				 */
	            //if(arrCnstList.size() > 0) {
	                //if(constBuild !=null && !constBuild.trim().equals("")) {
	                         //String[] splConstraints = constBuild.split("[,]");
	                         //for(int i=0;i<splConstraints.length;i++){
                 				//String[] temp = splConstraints[i].split("[()_]");

				 /*String oldFileName = "/home/aimir/aimiramm/fw/SWAMM_0_4_NURITelecom_NZCI211_1.0_3.0_2808_false/SWAMM_0_4_NURITelecom_NZCI211_1.0_3.0_2808_false.tar";//firmwareDir + File.separator + oldFileNameTemp + File.separator + oldFileNameTemp + newFileName.substring(newFileName.lastIndexOf("."),newFileName.length());
                 String diffFileName = "/home/aimir/aimiramm/fw/SWAMM_0_4_NURITelecom_NZCI211_1.0_3.0_2808_false/SWAMM_0_4_NURITelecom_NZCI211_1.0_3.0_2808_false.diff";//newFileName.replaceAll(newFileName.substring(newFileName.lastIndexOf("."),newFileName.length()),"")+"_FROM_" + oldFileNameTemp + ".diff";
				  */
                 //★★  old file name형식은 Constraints 가 어떻게 정해지냐에 따라 정해짐 현재로서는 하드코딩으로 정해놨음.
				/*  String oldFileName = "/home/aimir/aimiramm/fw/SWAMM_0_4_NURITelecom_NZCI211_1.0_3.0_2808_false/SWAMM_0_4_NURITelecom_NZCI211_1.0_3.0_2808_false.tar";
                 String diffFileName = firmwareRealDir + newFileName.substring(newFileName.lastIndexOf("\\"),newFileName.lastIndexOf("."))+"_FROM_" + oldFileNameConst + ".diff";
                 
                 try {
					DiffGeneratorUtil.makeDiff(oldFileName.replaceAll(" ", ""), newFileName, diffFileName);
				} catch (Exception e) {
					e.printStackTrace();
					mav.addObject("savefilename", "");
					mav.addObject("isOverwriteCheck", "DIFF generate시 에러가 발생하였습니다.");
					return mav;
				}*/
	                         //}
	               //}
	            //}
			}

			mav.addObject("savefilename", firmwareRealFile.getName()+"|");
			mav.addObject("isOverwriteCheck", "endSuccess");
			return mav;
    }	
		
	/**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : 지역별 model설치 갯수를 클릭 했을때 장비리스트를 트리에 추가해서 뿌려줌.
    * @return : List<object>
    */
	@SuppressWarnings("unused")
    @RequestMapping(value="/gadget/device/firmware/getDistriButeMcuIdList")
    public ModelAndView getDistriButeMcuIdList(
    								@RequestParam("equip_kind") String equip_kind,
    								@RequestParam("locaionId") String locaionId,
    								@RequestParam("devicemodel_id") String devicemodel_id,    								
    								@RequestParam("swVersion") String swVersion,    								
    								@RequestParam("hwVersion") String hwVersion,   								
    								@RequestParam("swRevision") String swRevision,   								
    								@RequestParam("supplierId") String supplierId,   								
    								@RequestParam("equip_type") String equip_type   								
    								){
		//log.error(this.getClass().getName()+".getDistriButeMcuIdList() start..");
	     HashMap<String, Object> param = new HashMap<String, Object>();
	     //devicemodel_id = "1";
	/*     hwVersion = "2.0";
	     swVersion = "3.1";
	     swRevision = "4096";*/
	     
	     param.put("devicemodel_id", devicemodel_id);
	     param.put("hwVersion", hwVersion);
	     param.put("swVersion", swVersion);	 
	     param.put("swRevision", swRevision);
	     param.put("equip_kind", equip_kind);
	     param.put("locaionId", locaionId);
	     param.put("supplierId", supplierId);
	     param.put("equip_type", equip_type);
	     //locaionId = "8";
	 	/**
	 	 * 해당지역 MCU ID와 MCU별 장비 리스트 (modem, codi는 id가 트리 형태로 다시 표시 된다. )
	 	 **/
	     List<Object> distributeModelList = firmWareManager.getdistributeMcuIdDivList(param, locaionId, "");
	     FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml();
	     StringBuffer makeDivList = getdistributeMcuIdDivList(distributeModelList,param);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("distributeModelList",makeDivList.toString());
        
		return mav;
    }	
    /**
    *
    * @param       : json Call Param
    * @exception   : 
    * @Date        : 2010/12/16
    * @Description : firmware 배포checkbox선택시 Location을 innerHtml형식으로 생성
    * @return : DivinnerHtml String
    */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping(value="/gadget/device/firmware/setLocationForm")
    public ModelAndView setLocationForm(
    								@RequestParam("equip_model_id") String equip_model_id,
    								@RequestParam("paramStr") String paramStr,						
    								@RequestParam("supplierId") String supplierId,						
    								@RequestParam("equip_type") String equip_type						
    								){
		//log.error(this.getClass().getName()+".setLocationForm() start..");
		HashMap<String, Object> param = new HashMap<String, Object>();
		String equip_kind = "";
		String devicemodel_id = "";
		String swVersion = "";
		String hwVersion = "";
		String swRevision = "";
		String fileName = "";
		
		StringTokenizer equipStr = new java.util.StringTokenizer(equip_model_id,"_");
		//SWAMM_1_1_5_11_1.2_2.1_15_false.ebl|1.2|2.1|15
		
    	if(paramStr.indexOf("|") == 0){
    		paramStr = "empty"+paramStr;
    	}
		StringTokenizer paramStrTok = new java.util.StringTokenizer(paramStr,"|");

		int tokInt = 0;
        while(equipStr.hasMoreTokens()){
        	String str = equipStr.nextToken();
        	if(tokInt == 0) equip_kind = str;
        	else if(tokInt == 1) devicemodel_id = str;
        	else equip_type = str;
        	tokInt++;
        }	
        tokInt = 0;
        while(paramStrTok.hasMoreTokens()){
        	String str = paramStrTok.nextToken();
        	if(tokInt == 0) fileName = str;
        	else if(tokInt == 1) hwVersion = str;
        	else if(tokInt == 2) swVersion = str;
        	else if(tokInt == 3) swRevision = str;
        	tokInt++;
        }

        if(fileName.equals("empty")){
        	fileName = "";
        }
        param.put("equip_kind", equip_kind);
        param.put("devicemodel_id", devicemodel_id);
        param.put("swVersion", swVersion);
        param.put("hwVersion", hwVersion);
        param.put("swRevision", swRevision);
        param.put("fileName", fileName);
        param.put("supplierId", supplierId);
        param.put("equip_type", equip_type);
        
        //전국 location id를 가지고 온다.
		List<Location> locationList = firmWareManager.getLocationAllList();		
		List<Location> locationList2 = new ArrayList();
		locationList2 = reSetChildList(locationList,param);
		locationList2 = sumNumLocation(locationList2);
		
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("locationList", locationList2);
        
		return mav;
	}
	
	 /**
     *  tree에서 조회된 mcu_id에 해당하는 모뎀 리스트 조회  
     *
     * @param       : FirmWaremainGadget.jsp에서 넘어온 param  
     * @exception   : ActionException, Exception 컨트롤 변수 세팅 에러
     * @Date        : 2010/12/07
     * @Description : getDistriButeModemList()함수에서 호출 
     */	
	@RequestMapping(value="/gadget/device/firmware/setDistriButeModemList")
    public ModelAndView setDistriButeModemList(
    									    @RequestParam("equip_model_id")    String equip_model_id, 
    									    @RequestParam("mcu_Id")    String mcuId, 
    									    @RequestParam("hwVersion")    String hwVersion,
    									    @RequestParam("swVersion")    String swVersion,
    										@RequestParam("swRevision")    String swRevision,
		    								@RequestParam("locaionId")   String  locaionId,
		    								@RequestParam("equip_kind")   String  equip_kind,
		    								@RequestParam("supplierId")   String  supplierId,
		    								@RequestParam("equip_type")   String  equip_type
		    								){
		//log.error(this.getClass().getName()+".setDistriButeModemList() start..");
		HashMap<String, Object> param = new HashMap<String, Object>();

		String devicemodel_id = "";
		StringTokenizer equipStr = new java.util.StringTokenizer(equip_model_id,"_");

		int tokInt = 0;
        while(equipStr.hasMoreTokens()){
        	String str = equipStr.nextToken();
        	//if(tokInt == 0) equip_kind = str;
        	if(tokInt == 1) devicemodel_id = str;
        	//else equip_type = str;
        	tokInt++;
        }	
        tokInt = 0;
		
		/*
		 * 테스트 후 아래 코드 삭제 
		 * 
		 * */
/*	     hwVersion = "2.0";
	     swVersion = "3.1";
	     swRevision = "4096";
	     mcuId = "9";
	     locaionId = "8";*/
		
        param.put("equip_kind", equip_kind);
        param.put("swVersion", swVersion);
        param.put("hwVersion", hwVersion);
        param.put("swRevision", swRevision);
        param.put("locaionId", locaionId);
        param.put("supplierId", supplierId);
        param.put("equip_type", equip_type);
        param.put("devicemodel_id", devicemodel_id);
        param.put("mcuId", mcuId);
        
    	/**
    	 * codi, modem만 실행 됨
    	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. 
    	 **/
        List<Object> locationList = firmWareManager.getDistriButeModemList(param, mcuId);
        StringBuffer modemDivList = getdistributeModemDivList(locationList,param);
        
        ModelAndView mav = new ModelAndView("jsonView");
        mav.addObject("modemDivList",modemDivList.toString());
        
		return mav;
    }	
	
    /**
    *
    * @param       : List<Location> 
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : Location 이 SetMap형태로 되어 잇어.. set으로 값을 재가공 하게 되면
    * 해당 테이블에도 값이 반영이 되어버린다. Location 객체를 똑같이 하나 더만들어서.. 값을 다시 재 입력하여
    * Controller에서 값을 마음대로 변경 하기 위한 함수 이다. 
    * 
    */
	@SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Location> reSetChildList(List<Location> locationList,HashMap<String, Object> param){
		//log.error(this.getClass().getName()+".reSetChildList() start..");
		List<Location> reTurnList = new ArrayList();
		
        String devicemodel_id = String.valueOf(param.get("devicemodel_id"));
        String swVersion = String.valueOf(param.get("swVersion"));
        String hwVersion = String.valueOf(param.get("hwVersion"));
        String swRevision = String.valueOf(param.get("swRevision"));
        String equip_kind = String.valueOf(param.get("equip_kind"));
		
		for(int i=0 ; i<locationList.size();i++){
			Location loc1 = new Location();
			String mcuidCnt = firmWareManager.getDistriButeMcuIdCnt(param,String.valueOf(locationList.get(i).getId()),locationList.get(i).getName());
			String locaionId = String.valueOf(locationList.get(i).getId());
			
			loc1.setName( locationList.get(i).getName()+"<em  onclick=\"javascript:getDistriButeMcuIdList('"+equip_kind+"','"+locaionId+"','"+devicemodel_id+"','"+swVersion+"','"+hwVersion+"','"+swRevision+"');\">("+mcuidCnt+")</em>");
			loc1.setId( locationList.get(i).getId());
			loc1.setOrderNo(locationList.get(i).getOrderNo());
			loc1.setParent(locationList.get(i).getParent());
			
			if(locationList.get(i).getChildren().size()>0){
				Set childrenSet = new HashSet();
				childrenSet = reSetChildSet(locationList.get(i).getChildren(),param);
				 loc1.setChildren(childrenSet);
			}
			reTurnList.add(loc1);
		}
		
		return reTurnList;
	}
	
    /**
    *
    * @param       : Set<Location> child
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : Location 이 SetMap형태로 되어 잇어.. set으로 값을 재가공 하게 되면
    * 해당 테이블에도 값이 반영이 되어버린다. Location 객체를 똑같이 하나 더만들어서.. 값을 다시 재 입력하여
    * Controller에서 값을 마음대로 변경 하기 위한 함수 이다. 재귀함수 호출...
    * 
    */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private Set<Location> reSetChildSet(Set<Location> locationList,HashMap<String, Object> param){
		//log.error(this.getClass().getName()+".reSetChildSet() start..");
        String devicemodel_id = String.valueOf(param.get("devicemodel_id"));
        String swVersion = String.valueOf(param.get("swVersion"));
        String hwVersion = String.valueOf(param.get("hwVersion"));
        String swRevision = String.valueOf(param.get("swRevision"));
        String equip_kind = String.valueOf(param.get("equip_kind"));
        
		Set childrenSet = new HashSet();
		Set<Location> setLocation = locationList;
		 for (Iterator iter = setLocation.iterator(); iter.hasNext();){
			 Location loca = (Location) iter.next();
			 Location loc2 = new Location();
			 
			 /**
			 * 해당지역  ID와 MCU별 장비 수 
			 **/
			 String mcuidCnt = firmWareManager.getDistriButeMcuIdCnt(param,String.valueOf(loca.getId()),loca.getName());
			 String locaionId = String.valueOf(loca.getId());
			 
			 loc2.setName(loca.getName()+"<em  onclick=\"javascript:getDistriButeMcuIdList('"+equip_kind+"','"+locaionId+"','"+devicemodel_id+"','"+swVersion+"','"+hwVersion+"','"+swRevision+"');\">("+mcuidCnt+")</em>");
			 loc2.setId(loca.getId());
			 loc2.setOrderNo(loca.getOrderNo());
			 loc2.setParent(loca.getParent());
			 
			 if(loca.getChildren().size() > 0 ){
				 Set childrenSet2 = new HashSet();
				 childrenSet2 = reSetChildSet(loca.getChildren(),param);//재귀함수 호출 
				 loc2.setChildren(childrenSet2);
			 }
			 childrenSet.add(loc2);
		 }
		
		return childrenSet;
	}
	
	/**
    *
    * @param       : List<Location> 
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : 지역별 모뎀 count를 역순으로 계산.. 
    * 
    */
	private List<Location> sumNumLocation(List<Location> locationList){
		//log.error(this.getClass().getName()+".sumNumLocation() start..");
		for(int i=0 ; i<locationList.size();i++){
			
			if(locationList.get(i).getChildren().size()>0){
				 int topId = locationList.get(i).getId();
				 locationList = selfSumNumLocation(locationList.get(i).getChildren(),locationList,topId);
			}
		}
		
		return locationList;
	}
	
	/**
    *
    * @param       : List<Location> 
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : 지역별 모뎀 count를 계산
    * 
    */
	@SuppressWarnings("rawtypes")
    ArrayList returnArray2 =  new ArrayList();
	@SuppressWarnings("rawtypes")
    ArrayList returnArray4 =  new ArrayList();
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
    private List<Location> selfSumNumLocation(Set<Location> locationList,List<Location> org_locationList,int topId){
		//log.error(this.getClass().getName()+".selfSumNumLocation() start..");
		
		 for (Iterator iter = locationList.iterator(); iter.hasNext();){
			 Location loca = (Location) iter.next();
//			System.out.println("name---------------->"+loca.getName());
//			System.out.println("name---------------->"+extractStr(loca.getName()));
//			returnArray2.add(loca.getName());
//			 int checkInt = extractStr(loca.getName());
//			  System.out.println("---------------------------------------------------------loca.getChildren().size()="+loca.getChildren().size());
//			  System.out.println("---------------------------------------------------------checkInt="+checkInt);
//			  System.out.println("---------------------------------------------------------locationList.size()="+locationList.size());
//			 if(loca.getChildren().size() > 0 || (checkInt>0 && locationList.size()==1)){
			 if(loca.getChildren().size() > 0){
				 returnArray4.add(loca.getName());
				 selfSumNumLocation(loca.getChildren(),org_locationList,topId);//재귀함수 호출 
			 }else{//Tree 각각의node끝
				 
//				 System.out.println("last name =="+loca.getName());
//				 System.out.println("추출갯수 =="+extractStr(loca.getName()));
				 
				 if(extractStr(loca.getName()) > 0){
					 
//					 System.out.println("returnArray4.size()="+returnArray4.size());
					 
//					 String tmpString = (String) returnArray4.get(returnArray4.size()-1); 
/*					 System.out.println("tmpString "+extractName(tmpString));
					 System.out.println("loca.getName() "+loca.getParent().getName());*/
					 
					 
					 returnArray2.clear();
					 for(int j=0;j<returnArray4.size();j++){
						 returnArray2.add(returnArray4.get(j));
					 }
				 }else{
					 returnArray2.clear();
				 }
				 
/*				 for(int r=0; r<returnArray2.size();r++){
					 System.out.println("의 상위 트리 "+returnArray2.get(r));
				 }*/
				 org_locationList = findLocationName(org_locationList,topId,loca.getName(),extractStr(loca.getName()),returnArray2);
				 returnArray2.clear();
			 }
		 }
		 returnArray4.clear();
		return org_locationList;
	}
	
	/**
    *
    * @param       : List<Location> 
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : 지역별 모뎀 count를 계산 +(?)
    * 
    */
	@SuppressWarnings("rawtypes")
    private List<Location> findLocationName(List<Location> org_locationList,int topId, String locName, int modemCnt,ArrayList returnArray2){
		//log.error(this.getClass().getName()+".findLocationName() start..");
		for(int i=0 ; i<org_locationList.size();i++){
			
			if(org_locationList.get(i).getId() == topId){//최상위가 최하위에 맞는 node인지 비교..
                 //각각의 최하위 node를 구해서.. 최상위 node에 합계
				 //ex: 제주도의 송당리,남원리,덕천리 각각 1개씩 있으면..  송당리 갯수 더하고 findLocationChildName()함수 호출, 남원리 갯수 더하고 findLocationChildName 호출...
				int sumInt = extractStr(org_locationList.get(i).getName());
				int TmpmodemCnt = modemCnt+sumInt;
				String functionStr = extractScriptStr(org_locationList.get(i).getName());
				String tmpName = extractName(org_locationList.get(i).getName());
				String newName = "<em  onclick=\"javascript:getDistriButeMcuIdList("+functionStr+");\">("+TmpmodemCnt+")</em>";
				org_locationList.get(i).setName(tmpName+newName);
				
				if(org_locationList.get(i).getChildren().size()>0){
					findLocationChildName(org_locationList.get(i).getChildren(),locName,modemCnt,returnArray2);
				}
				
			}
		}


		return org_locationList;
	}
	
	/**
    *
    * @param       : List<Location> 
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : 지역별 모뎀 count를 계산 +(?)
    * 
    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private Set<Location> findLocationChildName(Set<Location> locationList,String locName,int modemCnt, ArrayList returnArray2){
		//log.error(this.getClass().getName()+".findLocationChildName() start..");
			Set childrenSet = new HashSet();
			Set<Location> setLocation = locationList;
			
			 for (Iterator iter = setLocation.iterator(); iter.hasNext();){
				 Location loca = (Location) iter.next();
				 if(loca.getChildren().size() > 0 ){
					 
					 			for(int i=0; i<returnArray2.size(); i++){
					 				if(extractName(String.valueOf(returnArray2.get(i))).equals(extractName(loca.getName()))){
										 int sumInt = extractStr(loca.getName());
										 int TmpmodemCnt = modemCnt+sumInt;
										 String functionStr = extractScriptStr(loca.getName());
										 String tmpName = extractName(loca.getName());
										 String newName = "<em  onclick=\"javascript:getDistriButeMcuIdList("+functionStr+");\">("+TmpmodemCnt+")</em>";
										 loca.setName(tmpName+newName);
										 loca.setChildren(findLocationChildName(loca.getChildren(),locName,modemCnt,returnArray2));					 				
					 					
					 				}
					 			}
				 }
				 childrenSet.add(loca);
			 }
			
			return childrenSet;
	}
    
	/**
    *
    * @param       : String Location Name
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : Location Name에 숫자만 추출 함.
    * 
    */
	private int extractStr(String locName){
		//log.error(this.getClass().getName()+".extractStr() start..");
		String extStr = locName.substring(locName.indexOf(">")+1,locName.indexOf(")<")+1);
		String extStr2 = extStr.substring(1,extStr.indexOf(")"));
		int extInt = Integer.parseInt(extStr2);
		
		return extInt;
	}

	/**
    *
    * @param       : String Location Name
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : 스크립트 value만 추출 함.
    * 
    */
	private String extractScriptStr(String locName){
		//log.error(this.getClass().getName()+".extractScriptStr() start..");
		String extStr = locName.substring(locName.indexOf("('"),locName.indexOf("')")+1);
		       extStr = extStr.substring(1,extStr.length());
		
		return extStr;
	}
	
	/**
    *
    * @param       : String Location Name
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : name
    * 
    */
	private String extractName(String locName){
		//log.error(this.getClass().getName()+".extractName() start..");
		String extStr = "";
		if(!locName.equals("")){
			extStr = locName.substring(0,locName.indexOf("<"));
		}
		
		return extStr;
	} 
	
	
    /**
    *
    * @param       : getFirmwareList Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : getFirmwareList함수에서 통계 쿼리 add 
    * 
    */
	@SuppressWarnings("unused")
    private List<Object> getStatisticsStr(Map<String, Object> param){
		//log.error(this.getClass().getName()+".getStatisticsStr() start..");
		String equipCnt = firmWareManager.getEquipCnt(param);
		List<Object> statisticStr = firmWareManager.getStatisticsStr(param);
		return statisticStr;
	}	
	
    /**
    *
    * @param       : getFirmwareList Call Param
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : getFirmwareList함수에서 통계 쿼리 add 
    * 
    */
	private String getEquipCnt(Map<String, Object> param){
		//log.error(this.getClass().getName()+".getEquipCnt() start..");
		String equipCnt = firmWareManager.getEquipCnt(param);
		return equipCnt;
	}	

	
    /**
    *
    * @param       : jsp json Call Param
    * @exception   : 
    * @Date        : 2011/01/04
    * @Description : 배포 >> 배포상태 정보  
    */
	@RequestMapping(value="/gadget/device/firmware/distributeStatus")
    public ModelAndView buildMCUDetailView(
						    		@RequestParam("top_equip_kind") String top_equip_kind,    
						    		@RequestParam("top_equip_type") String top_equip_type,
						    		@RequestParam("supplierId") String supplierId,
    								@RequestParam("firmware_Id") String firmware_Id,		
    								@RequestParam("hw_version") String hw_version, 
    								@RequestParam("fw_version") String fw_version,
    								@RequestParam("build") String build,
    								@RequestParam("gubun") String gubun,
    								@RequestParam(value="cmdAuth", required=false) String cmdAuth
    								){
		//log.error(this.getClass().getName()+".buildMCUDetailView() start..");
	    HashMap<String, Object> param = new HashMap<String, Object>();
	    param.put("equip_kind", top_equip_kind);
	    param.put("equip_type", top_equip_type);
	    param.put("firmware_Id", firmware_Id);
	    param.put("hw_version", hw_version);	 
	    param.put("fw_version", fw_version);
	    param.put("build", build);
	    param.put("gubun", gubun);
		
		StringBuffer makeHtmlTop = new StringBuffer();
		StringBuffer makeHtmlLocationTriger = new StringBuffer();
		
		//List<Object> topDetailList = firmWareManager.distributeWriterStatus(param);
		List<Object> locatonDetailList = null;
		//List<Object> triggeridDetailList = null;
		
		/*for (Object obj : topDetailList) {
	        Object[] objs = (Object[])obj;
			
			 * 작성자 정보 추가 필요...
			 * 
		}*/
			
		locatonDetailList = firmWareManager.distributeStatus(param);
//		FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml();
		FirmWareMakeInnerHtml fwm = new FirmWareMakeInnerHtml(cmdAuth);
		makeHtmlLocationTriger = fwm.getMakeHtmlLocationTriger(locatonDetailList,param);
		
		ModelAndView mav = new ModelAndView("jsonView");
		mav.addObject("makeHtmlTop", makeHtmlTop.toString());
		mav.addObject("makeHtmlLocationTriger", makeHtmlLocationTriger.toString());
        
		return mav;
    }
	
    /**
    * @param       : 
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : makeTree를 위한 내부 함수
    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private List getChildList(Object parent) {
		//log.error(this.getClass().getName()+".getChildList() start..");
		List retList = new ArrayList();
		if (parent instanceof List) {
			List parents = (List) parent;
			for (int i = 0; i < parents.size(); i++) {
				Object parentObj = parents.get(i);
				retList.add(getChildList(parentObj));
			}

		} else if (parent instanceof DeviceVendor) {
			DeviceVendor vendor = (DeviceVendor) parent;
			retList = deviceModelManager.getDeviceModels(vendor.getId());
		} else if (parent instanceof DeviceModel) {
			DeviceModel model = (DeviceModel) parent;
			retList.add(model.getDeviceConfig());
		} else if (parent instanceof Code) {
			Code code = (Code) parent;
			Set<Code> codeChild = code.getChildren();
			Iterator iterator = codeChild.iterator();
			boolean add = false;
			String compareCode = "";
			while(iterator.hasNext()){				
				Code iCode = (Code)iterator.next();
				compareCode = iCode.getName();
		        if(iCode.getName().endsWith("Type")||iCode.getName().endsWith("CodiBaudRate")){ //Type에 Codi추가   
		        	add = true;
		        	break;
		        }
			}
			if(add){
				if(compareCode.equals("CodiBaudRate")){
					ArrayList t = new ArrayList();
					t.add(code);
					retList = t;//Codi는 submenu를 추가 하지 않기 위해.
				}else{
					retList = codeManager.getChildCodes(code.getCode() + ".1");
				}
			}
		}
		return retList;
	}

    /**
	    * @param       : 
	    * @exception   : 
	    * @Date        : 2010/12/10
	    * @Description : makeTree를 위한 내부 함수
	    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private List getChildList(int supplierId, Object parent) {
		//log.error(this.getClass().getName()+".getChildList() start..");
		List retList = new ArrayList();
		if (parent instanceof List) {
			List parents = (List) parent;
			for (int i = 0; i < parents.size(); i++) {
				Object parentObj = parents.get(i);
				retList.add(getChildList(supplierId, parentObj));					
			}
		} else if (parent instanceof Code) {
			Code code = (Code) parent;
			retList = deviceModelManager.getDeviceModelByTypeId(supplierId,	code.getId());
			//System.out.println(retList);
		}
		return retList;
	}
	
    /**
	    * @param       : 
	    * @exception   : 
	    * @Date        : 2010/12/10
	    * @Description : makeTree를 위한 내부 함수
	    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private void getChildList(List models, List resultVendor, List resultModel,	List resultConfig) {
		//log.error(this.getClass().getName()+".getChildList() start..");
		if (models == null)
			return;

		if (models.size() > 0) {

			if (models.get(0) instanceof List) {
				for (int i = 0; i < models.size(); i++) {
					List modelType = (List) models.get(i);
					List modelList = new ArrayList();
					List vendors = new ArrayList();
					List configs = new ArrayList();

					resultVendor.add(vendors);
					resultModel.add(modelList);
					resultConfig.add(configs);
					
					getChildList(modelType, vendors, modelList, configs);

				}

			} else if (models.get(0) instanceof DeviceModel) {
				getModelSplitList(models, resultVendor, resultModel,resultConfig);

			}
		} else {

		}

	}

    /**
	    * @param       : 
	    * @exception   : 
	    * @Date        : 2010/12/10
	    * @Description : makeTree를 위한 내부 함수
	    */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
    private void getModelSplitList(List models, List resultVendor,List resultModel, List resultConfig) {
		//log.error(this.getClass().getName()+".getModelSplitList() start..");
		int oldVendorId = -1;
		List modelList = new ArrayList();
		List configs = new ArrayList();

		HashMap vendorsHashMap = new HashMap();

		int index = 0;
		for (int i = 0; i < models.size(); i++) {
			DeviceModel modelType = (DeviceModel) models.get(i);
			if (!vendorsHashMap.containsKey(modelType.getDeviceVendor().getId())) {
				vendorsHashMap.put(modelType.getDeviceVendor().getId(), index);
				resultVendor.add(modelType.getDeviceVendor());
				index++;
			}
		}

		List[] modelListArray = new ArrayList[vendorsHashMap.size()];
		List[] configsArray = new ArrayList[vendorsHashMap.size()];

		for (int i = 0; i < models.size(); i++) {
			DeviceModel modelType = (DeviceModel) models.get(i);

			int vendorIndex = (Integer) vendorsHashMap.get(modelType
					.getDeviceVendor().getId());

			
			if (modelListArray[vendorIndex] != null) {
				modelListArray[vendorIndex].add(modelType);
				configsArray[vendorIndex].add(modelType.getDeviceConfig());
			} else {
				modelListArray[vendorIndex] = new ArrayList();
				configsArray[vendorIndex] = new ArrayList();
				modelListArray[vendorIndex].add(modelType);
				configsArray[vendorIndex].add(modelType.getDeviceConfig());
			}
		}
		for (int i = 0; i < resultVendor.size(); i++) {
			resultConfig.add(configsArray[i]);
			resultModel.add(modelListArray[i]);
		}
	}
	
	 /**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : (배포 트리에서 지역별 카운트 클릭시 해당 id 값을 추가 하는DIV 내부함수
    * @return : DivinnerHtml StringBuffer
    */
	@SuppressWarnings("unused")
    public StringBuffer getdistributeMcuIdDivList(List<Object> distributeModelList,Map<String, Object> param){
		//log.error(this.getClass().getName()+".getdistributeMcuIdDivList() start..");
		StringBuffer returnDivList = new StringBuffer();
		
//		for(int t =0 ; t<distributeModelList.size(); t++){

	    for (Object obj : distributeModelList) {
	        Object[] objs = (Object[])obj;
//				ArrayList arr = (ArrayList) distributeModelList.get(t);
				String valueStr = String.valueOf(objs[0]);
	        
	        if(String.valueOf(param.get("equip_kind")).equals("MCU")||String.valueOf(param.get("equip_type")).equals("Codi")||String.valueOf(param.get("equip_type")).equals("MMIU")||String.valueOf(param.get("equip_type")).equals("IEIU")){//mcu_id까지만...
	        	String locaionId = String.valueOf(param.get("locaionId"));
	        	String idStr = "mcuId_node_"+locaionId+"_"+objs[0];
	        	returnDivList.append("<li id=\""+idStr+"\" value=\""+String.valueOf(objs[0])+"\"><a class=\"clicked checked\"><ins>&nbsp;</ins>"+valueStr+"</a><br/></li>");	        	
	        }else if(String.valueOf(param.get("equip_kind")).equals("Modem")&&!String.valueOf(param.get("equip_type")).equals("MMIU")&&!String.valueOf(param.get("equip_type")).equals("IEIU")){//모뎀 출력
	        	String mcuId = String.valueOf(objs[0]);
	        	/**
	        	 * codi, modem만 실행 됨
	        	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. (id count를 미리 출력_보여주기 위함)
	        	 **/
	        	String modemCnt = firmWareManager.getDistriButeModemListCnt(param,mcuId);
	        	String hwVersion = String.valueOf(param.get("hwVersion"));
	        	String swVersion = String.valueOf(param.get("swVersion"));
	        	String swRevision = String.valueOf(param.get("swRevision"));
	        	String locaionId = String.valueOf(param.get("locaionId"));
	        	String equip_kind = String.valueOf(param.get("equip_kind"));
	        	String idStr = "mcuId_node_"+locaionId+"_"+String.valueOf(objs[0]);
	        	String id = String.valueOf(objs[1]);
	        	
	        	String rdm = Integer.toString((int)(Math.random()*100+1));
	        	
	        	valueStr = valueStr+"<em onclick=\"javascript:getDistriButeModemList('"+mcuId+"','"+hwVersion+"','"+swVersion+"','"+swRevision+"','"+locaionId+"','"+equip_kind+"','"+rdm+"');\">("+modemCnt+")</em>";
	        	returnDivList.append("<li id=\""+ idStr +"\" value=\""+mcuId+"\" ><a class=\"clicked unchecked\"><ins>&nbsp;</ins>"+valueStr+"</a><br/><div style=\"MARGIN-TOP: 0px\" class=\"last leaf\" id=\"div_"+rdm+"_"+ mcuId +"\"></div></li>");
	        	
	        }
	        
		}
		
/*		for (Object obj : distributeModelList) {
	        Object[] objs = (Object[])obj;
	        String valueStr = String.valueOf(objs[0]);
	        
	        if(String.valueOf(param.get("equip_kind")).equals("MCU")||String.valueOf(param.get("equip_kind")).equals("Codi")||String.valueOf(param.get("equip_type")).equals("MMIU")||String.valueOf(param.get("equip_type")).equals("IEIU")){//mcu_id까지만...
	        	String locaionId = String.valueOf(param.get("locaionId"));
	        	String idStr = "mcuId_node_"+locaionId+"_"+String.valueOf(objs[0]);
	        	returnDivList.append("<li id=\""+idStr+"\" value=\""+String.valueOf(objs[0])+"\"><a class=\"clicked checked\"><ins>&nbsp;</ins>"+valueStr+"</a><br/></li>");	        	
	        }else if(String.valueOf(param.get("equip_kind")).equals("Modem")&&!String.valueOf(param.get("equip_type")).equals("MMIU")&&!String.valueOf(param.get("equip_type")).equals("IEIU")){//모뎀 출력
	        	String mcuId = String.valueOf(objs[0]);
	        	*//**
	        	 * codi, modem만 실행 됨
	        	 * mcu별 장비 수를 클릭 하면 id가 Tree형태로 다시 표시 됨. (id count를 미리 출력_보여주기 위함)
	        	 **//*
	        	String modemCnt = firmWareManager.getDistriButeModemListCnt(param,mcuId);
	        	String hwVersion = String.valueOf(param.get("hwVersion"));
	        	String swVersion = String.valueOf(param.get("swVersion"));
	        	String swRevision = String.valueOf(param.get("swRevision"));
	        	String locaionId = String.valueOf(param.get("locaionId"));
	        	String equip_kind = String.valueOf(param.get("equip_kind"));
	        	String idStr = "mcuId_node_"+locaionId+"_"+String.valueOf(objs[0]);
	        	
	        	
	        	valueStr = valueStr+"<em onclick=\"javascript:getDistriButeModemList('"+mcuId+"','"+hwVersion+"','"+swVersion+"','"+swRevision+"','"+locaionId+"','"+equip_kind+"');\">("+modemCnt+")</em>";
	        	returnDivList.append("<li id=\""+ idStr +"\" value=\""+mcuId+"\" ><a class=\"clicked unchecked\"><ins>&nbsp;</ins>"+valueStr+"</a><br/><div style=\"MARGIN-TOP: 0px\" class=\"last leaf\" id=\"div_"+ mcuId +"\"></div></li>");
	        }
		}*/
		return returnDivList;
	}
	
	 /**
    *
    * @param       : List
    * @exception   : 
    * @Date        : 2010/12/10
    * @Description : tree에서 mcuid클릭하면 해당 모뎀 리스트를 가지고 오는데 그 값을 div로 가공 (MMIU,IEIU 제외)
    * @return : DivinnerHtml StringBuffer
    */
	public StringBuffer getdistributeModemDivList(List<Object> distributeModelList,Map<String, Object> param){
		//log.error(this.getClass().getName()+".getdistributeModemDivList() start..");
		StringBuffer returnDivList = new StringBuffer();
		String idStr =String.valueOf(param.get("locaionId"))+"_"+ String.valueOf(param.get("mcuId"));
		for (Object obj : distributeModelList) {
	        Object[] objs = (Object[])obj;
	            String valueStr = String.valueOf(objs[1]);
	            String modem_mcu_id = String.valueOf(objs[2]);
	        	returnDivList.append("<li id=\"last_node_"+idStr+"_"+valueStr+"_"+modem_mcu_id+"\" value=\""+valueStr+"\" ><a class=\"clicked checked\"><ins>&nbsp;</ins>"+valueStr+"</a><br/></li>");
		}
		return returnDivList;
	}

	
    /**
	    * @param       : 
	    * @exception   : 
	    * @Date        : 2010/12/10
	    * @Description : makeTree를 위한 내부 함수
	    */
	@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
    private JsonTree getUnknown(int supplierId) {
		//log.error(this.getClass().getName()+".getUnknown() start..");
		JsonTree jsonTree = new JsonTree();
		List config = new ArrayList();
		Code code = new Code();
		code.setName("Unknown");
		code.setId(999999);

		List<DeviceModel> unknownList = new ArrayList();//deviceModelManager.getDeviceModelByTypeIdUnknown(supplierId);
		
		List vendors = new ArrayList();
		List models = new ArrayList();
		List configs = new ArrayList();
		//getChildList(unknownList, vendors, models, configs);

		jsonTree.setData(code);
		jsonTree.setChildren(addArrayList(code));
		jsonTree.setChildren1(addArrayList(vendors));
		jsonTree.setChildren2(addArrayList(models));
		jsonTree.setChildren3(addArrayList(configs));

		return jsonTree;
	}	
	
    /**
	    * @param       : 
	    * @exception   : 
	    * @Date        : 2010/12/10
	    * @Description : makeTree를 위한 내부 함수
	    */
	@SuppressWarnings({ "rawtypes", "unchecked" })
    private List addArrayList(Object list) {
		List arrayList = new ArrayList();
		arrayList.add(list);
		return arrayList;
	}	
}