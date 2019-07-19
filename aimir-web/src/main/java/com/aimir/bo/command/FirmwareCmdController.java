package com.aimir.bo.command;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import org.apache.activemq.command.ActiveMQQueue;

import com.aimir.bo.common.CommandProperty;
import com.aimir.bo.common.MD5Sum;
import com.aimir.bo.device.firmware.FirmWareHelper;
import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.CommandType;
import com.aimir.constants.CommonConstants.FW_EQUIP;
import com.aimir.constants.CommonConstants.FW_OTA;
import com.aimir.constants.CommonConstants.FW_STATE;
import com.aimir.constants.CommonConstants.FW_TRIGGER;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareMCUDao;
import com.aimir.dao.device.FirmwareModemDao;
import com.aimir.dao.device.FirmwareTriggerDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareHistory;
import com.aimir.model.device.FirmwareModem;
import com.aimir.model.device.FirmwareTrigger;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Role;
import com.aimir.model.system.Supplier;
import com.aimir.schedule.command.CmdOperationUtil;
import com.aimir.service.device.FirmWareManager;
import com.aimir.service.device.MCUManager;
import com.aimir.service.device.MeterManager;
import com.aimir.service.device.ModemManager;
import com.aimir.service.device.OperationLogManager;
import com.aimir.service.system.CodeManager;
import com.aimir.service.system.OperatorManager;
import com.aimir.util.DateTimeUtil;
/**
 * 펌웨어(OTA) 업그레이드를 위한 JSP에서 커맨드를 호출하여 결과를 리턴받기 위한 컨트롤러
 * 펌웨어 관련 커맨드 전용 컨트롤러
 * @author goodjob
 *
 * @param <V>
 */

@Controller
public class FirmwareCmdController<V> {

	private static Log log = LogFactory.getLog(FirmwareCmdController.class);
	
	@Autowired
    MeterManager meterManager;
	
	@Autowired
	ModemManager modemManager;
	
	@Autowired
    MCUManager mcuManager;
	
    @Autowired
    OperationLogManager operationLogManager; 
    
    @Autowired
    OperatorManager operatorManager;
    
    @Autowired
    CodeManager codeManager;
    
    @Autowired
    MeterDao meterDao;

    @Autowired
    FirmwareDao firmwareDao;
	
    @Autowired
    FirmWareManager firmWareManager;
    
    @Autowired
    FirmwareMCUDao firmwareMCUDao;
    
    @Autowired
    FirmwareModemDao firmwareModemDao;
    
    @Autowired
    FirmwareTriggerDao frTrgDao;
    
    @Autowired
    DeviceModelDao deviceDao;
    
    @Autowired
    CmdOperationUtil cmdOperationUtil;
    
    @Autowired
    HibernateTransactionManager transactionManager;
    
    protected boolean commandAuthCheck(String loginId, CommandType cmdType, String command){

		Operator operator = operatorManager.getOperatorByLoginId(loginId);
		
		Role role = operator.getRole();
		Set<Code> commands = role.getCommands();
		Code codeCommand = null;
		if(role.getCustomerRole()){
			return false;
		}

		if(CommandType.DeviceRead.equals(cmdType)){
			String commandAuth = role.getMtrAuthority();
			if(!commandAuth.equals("c")){//command 허용권한 체크
				return false;
			}
			for (Iterator<Code> i = commands.iterator(); i.hasNext(); ) {
				codeCommand = (Code)i.next();	            
		        if (codeCommand.getCode().equals(command)) return true;
		    }
		}

		if(CommandType.DeviceWrite.equals(cmdType)){
			String commandAuth = role.getSystemAuthority();
			if(!commandAuth.equals("c")){//command 허용권한 체크
				return false;
			}
			for (Iterator<Code> i = commands.iterator(); i.hasNext(); ) {
				codeCommand = (Code)i.next();	            
		        if (codeCommand.getCode().equals(command)) return true;
		    }
		}
		return false;
    }
    
	@RequestMapping(value="/gadget/device/command/cmdModemVersion")
	public ModelAndView cmdModemVersion(@RequestParam(value="target" ,required=false) 	String target
									, @RequestParam(value="triggerId" ,required=false) String triggerId
									, @RequestParam(value="loginId" ,required=false) String loginId) {

       	ResultStatus status = ResultStatus.FAIL;
       	
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		if(target == null || "".equals(target)){
			status = ResultStatus.INVALID_PARAMETER;
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
			return mav;
		}

	    String rtnStr = "";
       	MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if(supplier == null){
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		try {
			cmdOperationUtil.getModemVersion(triggerId, mcu.getSysID());
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
		}
	    
	    Code operationCode = codeManager.getCodeByCode("8.3.10");//TODI FIND COMMAND CODE
	    if(operationCode != null){
		    operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name())?status.name():rtnStr);   
	    }

        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	
	@RequestMapping(value="/gadget/device/command/cmdDistributionState")
	public ModelAndView cmdDistributionState(@RequestParam(value="target" ,required=false) 	String target
									, @RequestParam(value="triggerId" ,required=false) String triggerId
									, @RequestParam(value="loginId" ,required=false) String loginId
									, @RequestParam(value="varsys_id" ,required=false) String sys_id
									) {

       	ResultStatus status = ResultStatus.FAIL;
       	
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		if(target == null || "".equals(target)){
			status = ResultStatus.INVALID_PARAMETER;
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
			return mav;
		}

	    String rtnStr = "";
	    String mcu_id = firmWareManager.getIDbyMcuSysId(sys_id);
       	MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));

		try {
			cmdOperationUtil.cmdDistributionState(mcu.getSysID(), triggerId);
			status = ResultStatus.SUCCESS;
			rtnStr = getPropertyMessage("aimir.firmware.msg04");
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
		}
	    
	    Code operationCode = codeManager.getCodeByCode("8.3.10");//TODI FIND COMMAND CODE
/*	    if(operationCode != null){
		    operationLogManager.saveOperationLog(mcu.getMcuType(), mcu.getSysID(), loginId, operationCode, status.getCode(), rtnStr);   
	    }*/

        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	@RequestMapping(value="/gadget/device/command/cmdDistributionCancel")
	public ModelAndView cmdDistributionCancel(@RequestParam(value="target" ,required=false) 	String target
									, @RequestParam(value="triggerId" ,required=false) String triggerId
									, @RequestParam(value="loginId" ,required=false) String loginId
									, @RequestParam(value="varsys_id" ,required=false) String sys_id
									) {

       	ResultStatus status = ResultStatus.FAIL;
       	
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		if(target == null || "".equals(target)){
			status = ResultStatus.INVALID_PARAMETER;
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
			return mav;
		}

	    String rtnStr = "";
	    String mcu_id = firmWareManager.getIDbyMcuSysId(sys_id);
       	MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));

		try {
			cmdOperationUtil.cmdDistributionCancel(mcu.getSysID(), triggerId);
			status = ResultStatus.SUCCESS;
			rtnStr = getPropertyMessage("aimir.firmware.msg04");
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
		}
	    
/*	    Code operationCode = codeManager.getCodeByCode("8.3.10");//TODI FIND COMMAND CODE
	    if(operationCode != null){
		    operationLogManager.saveOperationLog(mcu.getMcuType(), mcu.getSysID(), loginId, operationCode, status.getCode(), rtnStr);   
	    }*/

        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	@RequestMapping(value="/gadget/device/command/cmdFirmwareUpdate")
	public ModelAndView cmdFirmwareUpdate(@RequestParam(value="target" ,required=false) 	String target
									, @RequestParam(value="loginId" ,required=false) String loginId) {

       	ResultStatus status = ResultStatus.FAIL;
       	
        ModelAndView mav = new ModelAndView("jsonView");
        /*
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.21")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		*/
		if(target == null || "".equals(target)){
			status = ResultStatus.INVALID_PARAMETER;
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
			return mav;
		}

	    String rtnStr = "";
       	MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if(supplier == null){
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		try {
			cmdOperationUtil.cmdFirmwareUpdate(mcu.getSysID());
			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
		}
	    
	    Code operationCode = codeManager.getCodeByCode("8.3.21");
	    if(operationCode != null){
		    operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name())?status.name():rtnStr);   
	    }

        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	@RequestMapping(value="/gadget/device/command/cmdDistributionMMIU")
	public ModelAndView cmdDistributionMMIU(
										@RequestParam(value="loginId" ,required=false) String loginId,
										@RequestParam("top_equip_kind") String top_equip_kind,    
							    		@RequestParam("top_equip_type") String top_equip_type,
							    		@RequestParam("supplierId") String supplierId,
							    		@RequestParam("checkOTAListResult") String checkOTAListResult,
							    		@RequestParam("transferType") String ptransferType,
							    		@RequestParam("installType") String pinstallType,
							    		@RequestParam("otaThreadCount") String potaThreadCount,
							    		@RequestParam("maxRetryCount") String pmaxRetryCount,
							    		@RequestParam("multicastWriteCount") String pmulticastWriteCount,
							    		@RequestParam("sendEUI642") String sendEUI642,
							    		@RequestParam("saveHistory") String saveHistory,
							    		@RequestParam("fromFileNm") String fromFileNm,
							    		@RequestParam("toFileNm") String toFileNm,
							    		@RequestParam("oldHwVersion") String oldHwVersion,
							    		@RequestParam("oldFwVersion") String oldFwVersion,
							    		@RequestParam("oldBuild") String oldBuild,
							    		@RequestParam("newHwVersion") String newHwVersion,
							    		@RequestParam("newFwVersion") String newFwVersion,
							    		@RequestParam("newBuild") String newBuild,
							    		@RequestParam("equipList") String equipList,
							    		@RequestParam("oldFirmwareId") String fromFirmwareid, 
							    		@RequestParam("newFirmwareId") String toFirmwareid, 
							    		@RequestParam("oldArm") String fromArm, 
							    		@RequestParam("newArm") String toArm,
							    		@RequestParam("vendor") String vendor,
							    		@RequestParam("model") String model,					    		
							    		@RequestParam("model_id") String devicemodel_id					    		
									) {
		
		
		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");		System.out.println("===========================1");
		log.debug("===========================1");
		
        ModelAndView mav = new ModelAndView("jsonView");
		/*if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}*/
		
		if(equipList.length() == 0){
			mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg01"));     
	        return mav;			
		}

	    String rtnStr = "";
		System.out.println("===========================2");
		log.debug("===========================2");
		try {
			
			String equipKind = top_equip_kind;
//			int equipKindCd = CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
			String mcuId = "";
			
			try {
				equipList = URLDecoder.decode(equipList,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}
			
		   	ArrayList arrEquipList = new ArrayList();
		   	
			//if(equipKind.equals("Modem")){
				/*
				 최종 MODEM일 경우 형식으로 들어갈 것임. 
				arrEquipList	ArrayList<E>  (id=7692)	
				[
				 [31010_000D6F00003A2F3C, 31010_000D6F000030EF0C], 
				 [51010_000D6F00003A31B5, 51010_000D6F000030F1B9, 51010_000D6F000030EE4E, 51010_000D6F00003A31BF, 51010_000D6F0000307940, 51010_000D6F00003A2F97], 
				 [21010_000D6F000030EF3B, 21010_000D6F00003A316E, 21010_000D6F00003A3672, 21010_000D6F00003A2F40, 21010_000D6F000030790B, 21010_000D6F00003A3257]
				]
				 */
				
//				1. last_node는 사용자가 개개별로 선택한 값이다. 먼저 선택하여 배열에 넣는다.
				ArrayList lastnodeList = new ArrayList();
				StringTokenizer st1 = new java.util.StringTokenizer(equipList,"|");
			   	while(st1.hasMoreTokens()){
			   		String tmpStr1 = String.valueOf(st1.nextToken());
			   		if(tmpStr1.indexOf("last_node_") > -1){
			   			tmpStr1 = tmpStr1.replace("last_node_", "");
			   			lastnodeList.add(tmpStr1);
			   		}
			 	}
			   	
//			   	2. mcuId_node값을 추출한다. 개개별 선택한 값의 같은  지역에서 전체 sys_id를 선택한 값이 있을 경우 .
				ArrayList mcuIdnodeList = new ArrayList();
				StringTokenizer st2 = new java.util.StringTokenizer(equipList,"|");
			   	while(st2.hasMoreTokens()){
			   		String tmpStr2 = String.valueOf(st2.nextToken());
			   		if(tmpStr2.indexOf("mcuId_node_") > -1){
			   			tmpStr2 = tmpStr2.replace("mcuId_node_", "");
//			   			lastnodeList 와 비교해서 같은 값이 있으면 lastnode에서 각각 선택한 node이기 때문에 뺀다.
			   			boolean check1 = true;
			   			for(int i=0; i< lastnodeList.size(); i++){
			   				String tmpStr4 = String.valueOf(lastnodeList.get(i)); 
			   				tmpStr4 = tmpStr4.substring(0,tmpStr4.lastIndexOf("_"));
			   				if(tmpStr4.equals(tmpStr2)){
			   					check1 = false;
			   				}
			   			}
			   			if(check1){
			   				mcuIdnodeList.add(tmpStr2);			   				
			   			}
			   		}
			 	}
			   	
//			   	3. mcuIdnodeList,lastnodeList 는 각지역의 최상위를 선택한거로 간주 한다.(sys_id 정보가 없기 때문에 지역으로 sys_id를 가지고 와야 한다)  
//			   	   mcuIdnodeList,lastnodeList 에서 추출한 지역을 뺀 지역을 추출
				ArrayList remainLocationNodeList = new ArrayList();
				ArrayList tmplastRemainLocationNodeList = new ArrayList();
				ArrayList lastRemainLocationNodeList = new ArrayList();
				StringTokenizer st3 = new java.util.StringTokenizer(equipList,"|");
				while(st3.hasMoreTokens()){
					String tmpStr3 = String.valueOf(st3.nextToken());
					
//					경우의 수가 있어.. 형식에 맞게 변경한다.
					if(tmpStr3.indexOf("last_node_")>-1){
						tmpStr3 = tmpStr3.replace("last_node_", "");
						tmpStr3 = tmpStr3.substring(0,tmpStr3.indexOf("_"));//지역코드
					}else if(tmpStr3.indexOf("mcuId_node_")>-1){
						tmpStr3 = tmpStr3.replace("mcuId_node_", "");
						tmpStr3 = tmpStr3.substring(0,tmpStr3.indexOf("_"));//지역코드
					}else if(tmpStr3.indexOf("top_")>-1){
						tmpStr3 = tmpStr3.replace("top_", "top");
						tmpStr3 = tmpStr3.substring(tmpStr3.lastIndexOf("_")+1,tmpStr3.length());//지역코드
					}else{
						tmpStr3 = tmpStr3.substring(tmpStr3.lastIndexOf("_")+1,tmpStr3.length());//지역코드	
					}
					
					boolean check3 = true;
//					lastnodeList 비교
					if(lastnodeList.size()>0){
						for(int i=0; i<lastnodeList.size(); i++){
							String tmpStr4 = String.valueOf(lastnodeList.get(i));
							tmpStr4 = tmpStr4.substring(0,tmpStr4.indexOf("_"));						
							if(tmpStr3.equals(tmpStr4)){
								check3 = false;
							}
						}
					}

//					mcuIdnodeList 비교
					if(mcuIdnodeList.size()>0){
						for(int i=0; i<mcuIdnodeList.size(); i++){
							String tmpStr5 = String.valueOf(mcuIdnodeList.get(i));
							tmpStr5 = tmpStr5.substring(0,tmpStr5.indexOf("_"));						
							if(tmpStr3.equals(tmpStr5)){
								check3 = false;
							}
						}
					}
					
					if(check3){
						remainLocationNodeList.add(tmpStr3); //mcuIdnodeList,lastnodeList에 같지 않은 지역만 등록 된다.
					}
				}
				
//				remainLocationNodeList에서 최 하위 Node만을 추출한다. 거기서 다시 상위에서 선택한 lastnodeList,mcuIdnodeList의 지역을 제외 시키면  보낼 지역만 남게 된다.
				for(int i=0; i< remainLocationNodeList.size(); i++){
					int tmpInt1 = Integer.parseInt(String.valueOf(remainLocationNodeList.get(i)));//지역코드		
//					하위 노드 id값을 가지고 온다[6, 7, 8, 16, 21, 18, 19, 20]
					List<Integer> getChildrenList = firmWareManager.getChildren(tmpInt1);
					
					for(int j=0;j<getChildrenList.size();j++){
						boolean addCheck = true;
						String tmpInt2 = String.valueOf(getChildrenList.get(j));
						if(lastnodeList.size()>0){
							for(int k=0 ; k<lastnodeList.size(); k++){
								String tmpInt3 = String.valueOf(lastnodeList.get(k));
								tmpInt3 = tmpInt3.substring(0,tmpInt3.indexOf("_"));
								if(tmpInt2.equals(tmpInt3)){
									addCheck = false;
								}
							}
						}
//						getChildrenList 에 같은것이 없었다면 
						if(addCheck){
							if(mcuIdnodeList.size()>0){
								for(int k=0 ; k<mcuIdnodeList.size(); k++){
									String tmpInt3 = String.valueOf(mcuIdnodeList.get(k));
									tmpInt3 = tmpInt3.substring(0,tmpInt3.indexOf("_"));
									if(tmpInt2.equals(tmpInt3)){
										addCheck = false;
									}
								}
							}
						}
//						lastnodeList, mcuIdnodeList 에 같은것이 없었다면 
						if(addCheck){
							tmplastRemainLocationNodeList.add(tmpInt2);								
						}
					}
				}
				
//				tmplastRemainLocationNodeList 에는 중복된 주소들도 있을것이다.  중복된 리스트를 뺀다.
				for(int i=0 ; i< tmplastRemainLocationNodeList.size(); i++){
					String tmpStr6 = String.valueOf(tmplastRemainLocationNodeList.get(i));
					boolean check = true;
					if(i==0){
						check = true;
					}else{
						for(int k=0 ; k<lastRemainLocationNodeList.size();k++){
							String tmpStr7 = String.valueOf(lastRemainLocationNodeList.get(k));
							if(tmpStr6.equals(tmpStr7)){
								check = false;
							}
						}
					}
					
					if(check){
						lastRemainLocationNodeList.add(tmpStr6);
					}
				}
				System.out.println("===========================3");
				log.debug("===========================3");
				HashMap<String, Object> parama = new HashMap<String, Object>();
				parama.put("devicemodel_id", devicemodel_id);
				parama.put("hwVersion", oldHwVersion);
				parama.put("swVersion", oldFwVersion);	 
				parama.put("swRevision", oldBuild);
				parama.put("equip_kind", equipKind);
				parama.put("supplierId", supplierId);
				parama.put("equip_type", top_equip_type);
				
//				지역별 mcu_id 리스트를 가지고 온다.
				if(lastRemainLocationNodeList.size() > 0){
					for(int i=0 ; i< lastRemainLocationNodeList.size();i++){
						String tmpStrLoc = String.valueOf(lastRemainLocationNodeList.get(i));	
						List<Object> mcuIdList = firmWareManager.getdistributeMcuIdDivList(parama, tmpStrLoc, "");
						List<Object> mcuIdDeviceSerialList = new ArrayList();

						if(mcuIdList.size()>0){
							for(int k=0 ; k< mcuIdList.size(); k++){
							     String mcu_id = String.valueOf(mcuIdList.get(k));
//								 arrEquipList 로 넘겨 준다.
								if(mcuIdList.size()>0){
									ArrayList al3 = new ArrayList();
									  for (Object obj2 : mcuIdList) {
									        Object[] objs2 = (Object[])obj2;
									        String total_Mcu_id = String.valueOf(objs2[0]);
									        al3.add(total_Mcu_id);
										}
/*									for(int p=0 ; p< mcuIdList.size(); p++){
									        String total_Mcu_id = String.valueOf(mcuIdList.get(p));
									        al3.add(total_Mcu_id);
									}*/
									arrEquipList.add(al3);
								}
										
							}
						}
					}
				}
//				mcuIdnodeList에 있는 데이터도 modem일경우 device_serial을 받아오고 아니면 mcu_id만 따로 빼낸다.
				if(mcuIdnodeList.size()>0){
					ArrayList tmpmcuIdnodeList = new ArrayList();
					for(int i=0; i<mcuIdnodeList.size(); i++){
						String tmpStr8 = String.valueOf(mcuIdnodeList.get(i));
						String tmpStrLocation = tmpStr8.substring(0,tmpStr8.indexOf("_"));
						String tmpStrMcuid = tmpStr8.substring(tmpStr8.indexOf("_")+1,tmpStr8.length());
						tmpmcuIdnodeList.add(tmpStrMcuid);
						//tmpmcuIdnodeList.add(tmpStr8);
					}
					arrEquipList.add(tmpmcuIdnodeList);	
				}
				
				
				
//				lastnodeList 도 보내지는 형식으로 변형하여 arrEquipList에 add한다
				if(lastnodeList.size()>0){
					ArrayList tmplastnodeList = new ArrayList();
					for(int i=0; i<lastnodeList.size(); i++){
						String tmpStr8 = String.valueOf(lastnodeList.get(i));
						tmpStr8 = tmpStr8.substring(tmpStr8.indexOf("_")+1,tmpStr8.length());
						tmplastnodeList.add(tmpStr8);
					}
					arrEquipList.add(tmplastnodeList);					
				}
				System.out.println("");
			//}

			
			if(arrEquipList.size()==0){
				mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg05"));        
		        return mav;			
			}
			
			
			
		   	//int uniqTriggerId=0; 
	        FirmwareTrigger ftrigger = null;
	        FirmwareHistory fhistory = null;
		   	//지역별 EquipList
		   	for(int i=0 ; i<arrEquipList.size(); i++){
		   		ArrayList  tempSendArrayEquipList = (ArrayList)arrEquipList.get(i);
		   		//ArrayList  sendArrayEquipList = new ArrayList();
		        //String triggerId = FirmWareHelper.getNewTriggerId(uniqTriggerId);
	   		    
		   		for(int j=0; j<tempSendArrayEquipList.size(); j++){
			   		String target  = String.valueOf(tempSendArrayEquipList.get(j));
		   			if(target == null || "".equals(target)){
		   		        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
		   				return mav;
		   			}
//		   			String mcu_id = firmWareManager.getIDbyMcuSysId(target);
//		   			MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));
		   			
			   		//sendArrayEquipList.add(tempSendArrayEquipList.get(j));

		            HashMap<String, Object> param = new HashMap<String, Object>();
					ftrigger = new FirmwareTrigger();
					fhistory= new FirmwareHistory();
					//HashMap
					param.put("equip_kind", top_equip_kind);
					param.put("equip_id", target);
					param.put("arm", toArm);
					//FirmwareTrigger
					ftrigger.setCreateDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
	            	ftrigger.setSrcFirmware(fromFirmwareid);							
	            	ftrigger.setSrcFWBuild(oldBuild);
	            	ftrigger.setSrcFWVer(oldFwVersion);
	            	ftrigger.setSrcHWVer(oldHwVersion);
	            	ftrigger.setTargetFirmware(toFirmwareid);
	            	ftrigger.setTargetFWBuild(newBuild);
	            	ftrigger.setTargetFWVer(newFwVersion);
	            	ftrigger.setTargetHWVer(newHwVersion);
					//FirmwareHistory
					fhistory.setEquipId(target);
					fhistory.setEquipKind(equipKind);
					fhistory.setEquipType(top_equip_type);
					fhistory.setEquipModel(model);
					fhistory.setEquipVendor(vendor);
					fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
					fhistory.setOtaState(FW_STATE.Unknown);   
					fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
					fhistory.setTriggerCnt(i);
					fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
					fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
					//fhistory.setTrId(Long.parseLong(triggerId));
//					fhistory.setMcu(mcu);
		            
		        	/*
		        	 * cmd 호출
		        	 * */
					
					try{
			   			/*Set<Modem> modems = mcu.getModem();
			   			Iterator<Modem> iterator = modems.iterator();
			   			while (iterator.hasNext()) {
			   				int modem_id = iterator.next().getId();
			   				System.out.println(modem_id);
			   				List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modem_id);
			   				for (int k=0; k<meters.size(); k++) {
			                    Meter meter = meters.get(k);
								cmdOperationUtil.cmdDistributionMMIU(meter, toFileNm);			                 
			                }
			   			}*/
			   			
						List<Object> distributeModemIdList = modemManager.getModemIdListByDevice_serial(target);
						for(int l =0 ; l< distributeModemIdList.size() ; l++){
							HashMap<String, Object> param2 = new HashMap<String, Object>();
							int modemID = Integer.parseInt(String.valueOf(distributeModemIdList.get(l)));
			                List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modemID);
			        		System.out.println("===========================5");
			        		log.debug("===========================5");
			                for (int k=0; k<meters.size(); k++) {
			                    Meter meter = meters.get(k);
								cmdOperationUtil.cmdDistributionMMIU(meter, toFileNm);
								System.out.println("===========================ok..");
								log.debug("===========================ok..");
			                }
						}
					}catch(Exception e){
						e.printStackTrace();
						fhistory.setOtaState(FW_STATE.Fail); 
						fhistory.setTriggerState(TR_STATE.Terminate);
						fhistory.setErrorCode(e.getMessage());
						rtnStr = e.getMessage();
					}finally{
						/**
						 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
						 ***/
						firmWareManager.createTrigger(ftrigger);
						fhistory.setTrId(ftrigger.getId());
						firmWareManager.insertFirmHistory(fhistory, param);
					}	
		           //uniqTriggerId++;
          

		   		}
		   	}        
		   	rtnStr = getPropertyMessage("aimir.firmware.msg06");
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
	    	mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg07"));        
	        return mav;  
		}
		
		/**
		 * 모든작업이 완료 후 trigger, history 테이블에 인서트 하여준다.
		 * treigger에서 //장비 타입이 Codi인 경우는 버전 정보를 16진수로 저장해준다
		 * history는  select , insert , update 순
		 * **/
        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }	
	
	/**
	 * SX2 미터의 OTA기능을 원버튼으로 구현한다. SX2 에 맞게 기본 설정등을 알아서 설정한다.<br>
	 * (REINSTALL, 무조건 재설치 옵션이 적용되어 있음.)
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/gadget/device/command/cmd-SX-Distribution")
	public ModelAndView cmd_SX_Distribution(
			HttpServletRequest request, HttpServletResponse response){		

		String fwDownURL = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/gadget/device/firmware/firmwareDown.jsp";
		ModelAndView view = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;
		
        //파일 저장
        String osName = System.getProperty("os.name");
        String homePath = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	homePath = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	homePath = CommandProperty.getProperty("firmware.dir");
        }
		String finalFilePath = null;
		String fileName = null;
		String loginId = null;
		String meterId = null;
		String deviceModel = null;
		byte[] fileBinary = null;
		String filePath = null;
		//파일 형식은 Gzip 형태여야 한다.
		String ext = null;
		
		//파라미터 체크
		if((loginId = request.getParameter("loginId"))==null || 
				(meterId = request.getParameter("meterId"))==null){
			//오류
			view.addObject("rtnStr", "Can not found 'Login ID or Meter ID'");
			view.addObject("status", status.name());
			return view;
		}
		
		//MCU 정보를 읽어온다.
		Integer nMeterId = Integer.parseInt(meterId);
		Meter meter = meterDao.get(nMeterId);
		if(meter == null){
			view.addObject("rtnStr", "Can not found 'Meter'");
			view.addObject("status", status.name());
			return view;
		}
		Modem modem = meter.getModem();
		if(modem==null){
			view.addObject("rtnStr", "Can not found 'Modem'");
			view.addObject("status", status.name());
			return view;
		}
		
		MCU mcu = modem.getMcu();
		if(mcu==null){
			view.addObject("rtnStr", "Can not found 'DCU'");
			view.addObject("status", status.name());
			return view;
		}
		
		Integer mcuId = modem.getMcuId();
		MCU mcu2 = mcuManager.getMCU(mcuId);
		
		// 명령 권한 확인  8.3 : MCU Command
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.20")) {
			// opcode
			view.addObject("rtnStr", "No permission");
			view.addObject("status", status.name());
			return view;
		}
		
		if(modem.getModel() == null) {
			view.addObject("rtnStr", "Can not found 'Model'");
			view.addObject("status", status.name());
			return view;
		}
		deviceModel = "SubSx";
		
		//파일 다운로드
		try {
			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multiReq.getFile("userfile");
			fileBinary = multipartFile.getBytes();
			fileName = multipartFile.getOriginalFilename();
			filePath = fileName.substring(0,fileName.lastIndexOf("."));
			ext = fileName.substring(fileName.lastIndexOf(".")+1);
			
		} catch (Exception e) {
			view.addObject("rtnStr", "Invalid parameter");
			view.addObject("status", status.name());
			return view;
		}

		//파일 저장
		try {
			finalFilePath = makeFirmwareDirectory(homePath, filePath, ext, true);
			log.info(String.format("Save firmware file - %s",finalFilePath));
			saveFile(finalFilePath,fileBinary);
		} catch (Exception e) {
			log.error(e);
			view.addObject("rtnStr", "Save firmware file error");
			view.addObject("status", status.name());
			return view;
		}
		
		//------------------------------------------------------------
		
		//////////////////////////////////////////////////////////////
		//						DB에 펌웨어 정보 등록.					//
		//////////////////////////////////////////////////////////////		
		String build = "11",//request.getParameter("newBuild"),
			   energyeType = "Electricity",
			   vendor = "MITSUBISHI",
			   fwVer=null,
			   hwVer=null,
			   releasedDate=DateTimeUtil.getCurrentDateTimeByFormat("yyMMdd");
		

		vendor = modem.getModel().getDeviceVendor().getName();
		
		//버전 정보 얻어오기.
		fwVer = "11";//request.getParameter("newFwVersion");
		hwVer = "11";//request.getParameter("newHwVersion");

		//날짜 패턴 변경
		String year = DateTimeUtil.getCurrentDateTimeByFormat("yyyy");
		releasedDate = year.substring(0, 2) +  releasedDate;
		

		
		FirmwareModem newFirmware = null;
		Firmware savedFirmware = null;
		FirmwareTrigger ftrigger = null;
		FirmwareHistory fhistory = new FirmwareHistory();
		
		//trigger insert 실패 후 finally에서 history 테이블에서 insert 되는것을 방지 하기 위함
		Boolean saveFirmware = false;
		Boolean saveTrigger = false;
		Boolean rightBinaryURL = false;
		Boolean rightBinaryMD5 = false;
		
		TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        
		try {
			
	        txStatus = transactionManager.getTransaction(txDefine);
	            
			//펌웨어 정보 등록
	        newFirmware = new FirmwareModem();
	        
	        savedFirmware = firmwareDao.getByFirmwareId(filePath);
	        if(savedFirmware != null) {
	        	newFirmware = (FirmwareModem) savedFirmware;
	        }
	        
        	newFirmware.setFirmwareId(filePath);
			newFirmware.setArm(false);
			newFirmware.setBinaryFileName(fileName);
			newFirmware.setFwVersion(fwVer);
			newFirmware.setHwVersion(hwVer);
			newFirmware.setBuild(build);
			newFirmware.setReleasedDate(releasedDate);
			newFirmware.setSupplierId(Integer.parseInt(request.getParameter("supplierId")));
			newFirmware.setDevicemodel_id(modem.getModel().getId());
			newFirmware.setEquipModel(deviceModel);
			newFirmware.setEquipVendor(vendor);
			newFirmware.setEquipKind(CommonConstants.DeviceType.Meter.toString());
			newFirmware.setEquipType(energyeType);
			
			firmWareManager.addFirmWareModem(newFirmware);
			
			saveFirmware = true;

			//트리거 등록
			ftrigger = new FirmwareTrigger();
			ftrigger.setCreateDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			ftrigger.setTargetFirmware(newFirmware.getBinaryFileName());
			ftrigger.setTargetFWBuild(newFirmware.getFwVersion());
			ftrigger.setTargetHWVer(newFirmware.getHwVersion());
			firmWareManager.addFirmWareTrigger(ftrigger);

			//트리거가 제대로 등록 되었을때 
			saveTrigger = true;
			
			//히스토리 등록
			fhistory.setIssueDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			fhistory.setTrId(ftrigger.getId());
			fhistory.setEquipId(modem.getDeviceSerial());
			fhistory.setEquipKind("Modem");
			fhistory.setOtaStep(FW_OTA.DataSend); //확인필요
			fhistory.setOtaState(FW_STATE.Unknown); 
			fhistory.setTriggerState(TR_STATE.Running); 
			fhistory.setTriggerStep(FW_TRIGGER.Start);
			fhistory.setEquipModel(deviceModel);
			fhistory.setEquipVendor(vendor);
			if(modem.getModemType() != null) {
				fhistory.setEquipType(energyeType);
			}
			
			//------------------------------------------------------------
			
			//////////////////////////////////////////////////////////////
			//				OTA 명령 전송을 위한 파라미터 설정.				//
			//////////////////////////////////////////////////////////////		
			
			// 파라미터 설정.
	    	String triggerId = String.valueOf(ftrigger.getId());
	
	    	
	    	/**
	    	 * OTA 유형, OTA를 수행할 대상
	    	 * 0 : Concentrator, 집중기 펌웨어 업그레이드
	    	 * 1 : Sensor, 센서 펌웨어 업그레이드 (리피터 포함)
	    	 * 2 : Coordinator, 코디네이터 펌웨어 업그레이드
	    	 */
	    	int equipKind = FW_EQUIP.Modem.getKind();
	    	/**
	    	 * OTA 유형에 따른 모델명 – 센서에 기록된 모델명 사용
	    	 * “세부 모델명”
	    	 */
	    	String model = deviceModel;
	    	/**
	    	 * 0 : Auto, 50대 기준으로 Multicast/Unicast가 자동 결정됨
	    	 * 1 : Multicast (펌웨어 전송만 Multicast, Verify, Install은 Unicast로 진행)
	    	 * 2 : Unicast
	    	 */
	    	int transferType = 0;
	    	/**
	    	 * 0x01 = 센서 정보 수집 및 버전 확인 (필수)
	    	 * 0x02 = 파일 전송 (Data Send)
	    	 * 0x04 = 전송 이미지 확인(Verify)
	    	 * 0x08 = 설치 (Install)
	    	 * 0x10 = 업그레이드된 버전 확인 (Scan)
	    	 * ALL  = 0x1F (남은 비트는 확장 가능 영역으로 남김)
	    	 */
	    	int otaStep = 0x1f;
	    	/**
	    	 * Multicast일때, 펌웨어를 Multicast로 전송시 같은 프레임을 몇번 전송할것인지 결정한다.
	    	 * 즉, 멀티캐스트 성공율을 높이기 위함이며 값이 증가하면 전송시간이 증가되므로 유의해야 한다.
	    	 * 멀티캐스트가 아닌경우 이값은 (Default=1)로 지정한다.
	    	 */
	    	int multiWriteCount = 1;
	    	//default 10
	    	int maxRetryCount = 3;
	    	//default 1
	    	int otaThreadCount = 1;
	    	/**
	    	 * 0 : AUTO, 같은 버전 설치 안함 (현재 버전이 New Version과 동일하면 설치 안함)
	    	 * 1 : REINSTALL, 무조건 재설치, 설치 테스트 및 다시 내릴 경우, 버전이 같아도 내려짐.
	    	 * 2 : MATCH, Old H/W, S/W, Build Version이 모두 동일하면 New Version으로 설치
	    	 * 3 : FORCE, REINSTALL 처럼 무조건 재설치를 하고 File도 무조건 Base file을 다운 받는다
	    	 */
	    	int installType = 1;
	    	/**
	    	 *  버전정보
	    	 *  0x0103 = 1.3
	    	 *  0이 아닌 임의의 숫자입력
	    	 *  
	    	 */
	    	int oldHwVersion = 11;//getHexVersion(request.getParameter("oldHwVersion"));
	    	int oldFwVersion = 11;//getHexVersion(request.getParameter("oldFwVersion"));
	    	int oldBuild = 11;//Integer.parseInt(request.getParameter("oldBuild").equals("0") ? "0.1" : request.getParameter("oldBuild"));
	    	
	    	int newHwVersion = 11;//getHexVersion(newFirmware.getHwVersion());
	    	int newFwVersion = 11;//getHexVersion(newFirmware.getFwVersion());
	    	int newBuild =  11;//Integer.parseInt((newFirmware.getBuild()).equals("0") ? "0.1" : newFirmware.getBuild());
	    	
			log.info("oldHwVersion : " + oldHwVersion + ", oldFwVersion : " + oldFwVersion + ", oldBuild : " + oldBuild);
			log.info("newHwVersion : " + newHwVersion + ", oldFwVersion : " + oldFwVersion + ", newBuild : " + newBuild);
			
	    	int dotIdx = newFirmware.getBinaryFileName().lastIndexOf(".");
	    	
	    	String fileDirectory  = newFirmware.getBinaryFileName().substring(0,dotIdx);

	    	String fileHomePath =  homePath+File.separator+fileDirectory;
	    	
	    	String filepath = fileHomePath+File.separator+newFirmware.getBinaryFileName();
	    	
	    	//binaryURL : 집중기에서 파일을 다운로드 받을 URL
	    	String binaryURL = null;
	    	File gzipFile = null;
	    	
	    	if(!(".gz".equals(fileName.substring(dotIdx)))) {
		    	//gzip으로 압축하는 부분 , F/W 업데이트 파일이 있던 위치에 gzip 파일을 생성한다.
		    	GZIPOutputStream gzipOut = new GZIPOutputStream(new FileOutputStream(filepath+".gz"));
		    	byte[] outBuffer = new byte[1024];  
		    	FileInputStream in = new FileInputStream(filepath);
		        int len;
		        while ((len = in.read(outBuffer)) > 0) {
		        	gzipOut.write(outBuffer, 0, len);
		        }  
		        	      
		        in.close();
		        gzipOut.close();
		        
		        binaryURL = fwDownURL+"?fileType=binary&fileName=" +
						newFirmware.getBinaryFileName().toLowerCase()+".gz";
		        rightBinaryURL = true;
		        
		        gzipFile = new File(filepath+".gz");
		        
	    	} else {
	    		//이미 gzip형식인 파일일때
	    		binaryURL = fwDownURL+"?fileType=binary&fileName=" +
						newFirmware.getBinaryFileName().toLowerCase();
	    		rightBinaryURL = true;
	    		
	    		gzipFile = new File(filepath);
	    		
	    	}
	    	
	    	if(!gzipFile.exists()){
	    		transactionManager.rollback(txStatus);
	    		view.addObject("rtnStr", "Can not found file");
				view.addObject("status", status.name());
				return view;
	    	}
	        		
	    	String binaryMD5 = MD5Sum.getFileMD5Sum(gzipFile);  	

			rightBinaryMD5 = true;

	    	String diffURL = null;
	    	String diffMD5 = null;
    	
	    	String mcuIdDeviceSerial = modem.getDeviceSerial(); 
	    	
	    	ArrayList<String> equipIdList = new ArrayList<String>();
	    	equipIdList.add(mcuIdDeviceSerial);
	    	
	    	//명령 전송.
			cmdOperationUtil.cmdDistribution(mcu2.getSysID(), triggerId,
					equipKind, model, transferType, otaStep, multiWriteCount,
					maxRetryCount, otaThreadCount, installType, oldHwVersion,
					oldFwVersion, oldBuild, newHwVersion, newFwVersion,
					newBuild , binaryURL, binaryMD5, diffURL, diffMD5,
					equipIdList);
			
			status = ResultStatus.SUCCESS;
			view.addObject("rtnStr", "success");
			view.addObject("status", status.name());
			
			transactionManager.commit(txStatus);
			
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			
			log.error(e,e);
			
			if(!saveFirmware) {
				view.addObject("rtnStr", "Add Firmware Fail.");
			} else if(!saveTrigger) {
				view.addObject("rtnStr", "Add Firmware Trigger Fail.");
			} else if(!rightBinaryURL) {
				view.addObject("rtnStr", "binaryURL error");
			} else if(!rightBinaryMD5) {
				view.addObject("rtnStr", "binaryMD5 check sum error");
			} else {
				view.addObject("rtnStr", e.getMessage());
				fhistory.setErrorCode(e.getMessage());
				fhistory.setOtaState(FW_STATE.Fail); 
				fhistory.setTriggerState(TR_STATE.Terminate); 
				fhistory.setTriggerStep(FW_TRIGGER.End);
			}
			
			view.addObject("status", status.name());
			return view;
			
		} finally {
			if(saveTrigger) {
				try {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("equip_kind", "Modem");
					param.put("equip_id", modem.getDeviceSerial());
					param.put("arm", "0");
					fhistory.setTrId(ftrigger.getId());
					firmWareManager.insertFirmHistory(fhistory, param);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(e,e);
					view.addObject("rtnStr", "Add FirmwareHistory Fail");
					view.addObject("status", status.name());
				}
			}
		}
		return view;
	}
	
	/**
	 * Modem OTA기능을  구현. 
	 * 기본 인자들은 UI에서 입력받아 가져온다.
	 * 
	 */
	@RequestMapping(value="/gadget/device/command/cmd_Modem_Distribution")
	public ModelAndView cmd_Modem_Distribution(
			HttpServletRequest request, HttpServletResponse response){		 

		String fwDownURL = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/gadget/device/firmware/firmwareDown.jsp";
		ModelAndView view = new ModelAndView("jsonView");
		ResultStatus status = ResultStatus.FAIL;
		
        String osName = System.getProperty("os.name");
        String homePath = "";
        if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
        	homePath = CommandProperty.getProperty("firmware.window.dir");
        }else{
        	homePath = CommandProperty.getProperty("firmware.dir");
        }
		String finalFilePath = null;
		String fileName = null;
		String loginId = null;
		String modemId = null;
		String deviceModel = null;
		byte[] fileBinary = null;
		String filePath = null;
		Boolean isDiff = null;
		//파일 형식은 Gzip 형태여야 한다.
		String ext = null;
		
		//파라미터 체크
		if((loginId = request.getParameter("loginId"))==null || 
				(modemId = request.getParameter("modemId"))==null){
			//오류
			view.addObject("rtnStr", "Can not found 'Login ID or Modem ID'");
			view.addObject("status", status.name());
			return view;
		}
		
		//MCU 정보를 읽어온다.
		Integer nModemId = Integer.parseInt(modemId);
		Modem modem = modemManager.getModem(nModemId);
		if(modem==null){
			view.addObject("rtnStr", "Can not found 'Modem'");
			view.addObject("status", status.name());
			return view;
		}
		
		MCU mcu = mcuManager.getMCU(modem.getMcuId());
		if(mcu==null){
			view.addObject("rtnStr", "Can not found 'DCU'");
			view.addObject("status", status.name());
			return view;
		}	
		
		// 명령 권한 확인  8.3 : MCU Command
		if (!commandAuthCheck(loginId, CommandType.DeviceWrite, "8.1.20")) {
			// opcode
			view.addObject("rtnStr", "No permission");
			view.addObject("status", status.name());
			return view;
		}
		
		if(modem.getModel() == null) {
			view.addObject("rtnStr", "Can not found 'Model'");
			view.addObject("status", status.name());
			return view;
		}
		deviceModel = modem.getModel().getName();
		
		//파일 다운로드
		try {
			MultipartHttpServletRequest multiReq = (MultipartHttpServletRequest) request;
			MultipartFile multipartFile = multiReq.getFile("userfile");
			fileBinary = multipartFile.getBytes();
			fileName = multipartFile.getOriginalFilename();
			filePath = fileName.substring(0,fileName.lastIndexOf("."));
			ext = fileName.substring(fileName.lastIndexOf(".")+1);
			
		} catch (Exception e) {
			view.addObject("rtnStr", "Invalid parameter");
			view.addObject("status", status.name());
			return view;
		}

		//파일 저장
		try {
			finalFilePath = makeFirmwareDirectory(homePath, filePath, ext, true);
			log.info(String.format("Save firmware file - %s",finalFilePath));
			saveFile(finalFilePath,fileBinary);
		} catch (Exception e) {
			log.error(e);
			view.addObject("rtnStr", "Save firmware file error");
			view.addObject("status", status.name());
			return view;
		}
		
		//------------------------------------------------------------
		
		//////////////////////////////////////////////////////////////
		//						DB에 펌웨어 정보 등록.					//
		//////////////////////////////////////////////////////////////		
		String build = request.getParameter("newBuild"),
			   modemType = null,
			   vendor = null,
			   fwVer=null,
			   hwVer=null,
			   releasedDate=DateTimeUtil.getCurrentDateTimeByFormat("yyMMdd");
		

		vendor = modem.getModel().getDeviceVendor().getName();
		
		if(modem.getModemType() != null) {
			modemType = modem.getModemType().name();
		}
		
		//버전 정보 얻어오기.
		fwVer = request.getParameter("newFwVersion");
		hwVer = request.getParameter("newHwVersion");

		//날짜 패턴 변경
		String year = DateTimeUtil.getCurrentDateTimeByFormat("yyyy");
		releasedDate = year.substring(0, 2) +  releasedDate;
		

		Firmware savedFirmware = null;
		FirmwareModem newFirmware = null;
		FirmwareTrigger ftrigger = null;
		FirmwareHistory fhistory = new FirmwareHistory();
		
		//trigger insert 실패 후 finally에서 history 테이블에서 insert 되는것을 방지 하기 위함
		Boolean saveFirmware = false;
		Boolean saveTrigger = false;
		Boolean rightBinaryURL = false;
		Boolean rightBinaryMD5 = false;
		Boolean rightDiffURL = false;
		Boolean rightDiffMD5 = false;
		
		TransactionStatus txStatus = null;
        DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
        txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        
		try {
			
	        txStatus = transactionManager.getTransaction(txDefine);
	        //펌웨어 정보 등록
	        newFirmware = new FirmwareModem();
	        
	        savedFirmware = firmwareDao.getByFirmwareId(filePath);
	        if(savedFirmware != null) {
	        	newFirmware = (FirmwareModem) savedFirmware;
	        }
	        
			newFirmware.setFirmwareId(filePath);
			newFirmware.setArm(false);
			newFirmware.setBinaryFileName(fileName);
			newFirmware.setFwVersion(fwVer);
			newFirmware.setHwVersion(hwVer);
			newFirmware.setBuild(build);
			newFirmware.setReleasedDate(releasedDate);
			newFirmware.setSupplierId(Integer.parseInt(request.getParameter("supplierId")));
			newFirmware.setDevicemodel_id(modem.getModel().getId());
			newFirmware.setEquipModel(deviceModel);
			newFirmware.setEquipVendor(vendor);
			newFirmware.setEquipKind(CommonConstants.DeviceType.Modem.toString());
			
			if(modem.getModemType() != null) {
				newFirmware.setEquipType(modemType);
				newFirmware.setModemType(modemType);
			}
			
			firmWareManager.addFirmWareModem(newFirmware);
			saveFirmware = true;

			//트리거 등록
			ftrigger = new FirmwareTrigger();
			ftrigger.setCreateDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			ftrigger.setTargetFirmware(newFirmware.getBinaryFileName());
			ftrigger.setTargetFWBuild(newFirmware.getFwVersion());
			ftrigger.setTargetHWVer(newFirmware.getHwVersion());
			firmWareManager.addFirmWareTrigger(ftrigger);

			//트리거가 제대로 등록 되었을때 
			saveTrigger = true;
			
			//히스토리 등록
			fhistory.setIssueDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
			fhistory.setTrId(ftrigger.getId());
			fhistory.setEquipId(modem.getDeviceSerial());
			fhistory.setEquipKind("Modem");
			fhistory.setOtaStep(FW_OTA.DataSend); //확인필요
			fhistory.setOtaState(FW_STATE.Unknown); 
			fhistory.setTriggerState(TR_STATE.Running); 
			fhistory.setTriggerStep(FW_TRIGGER.Start);
			fhistory.setEquipModel(deviceModel);
			fhistory.setEquipVendor(vendor);
			if(modem.getModemType() != null) {
				fhistory.setEquipType(modemType);
			}
			
			//------------------------------------------------------------
			
			//////////////////////////////////////////////////////////////
			//				OTA 명령 전송을 위한 파라미터 설정.				//
			//////////////////////////////////////////////////////////////		
			
			// 파라미터 설정.
	    	String triggerId = String.valueOf(ftrigger.getId());
	
	    	
	    	/**
	    	 * OTA 유형, OTA를 수행할 대상
	    	 * 0 : Concentrator, 집중기 펌웨어 업그레이드
	    	 * 1 : Sensor, 센서 펌웨어 업그레이드 (리피터 포함)
	    	 * 2 : Coordinator, 코디네이터 펌웨어 업그레이드
	    	 */
	    	int equipKind = FW_EQUIP.Modem.getKind();
	    	/**
	    	 * OTA 유형에 따른 모델명 – 센서에 기록된 모델명 사용
	    	 * “세부 모델명”
	    	 */
	    	String model = deviceModel;
	    	/**
	    	 * 0 : Auto, 50대 기준으로 Multicast/Unicast가 자동 결정됨
	    	 * 1 : Multicast (펌웨어 전송만 Multicast, Verify, Install은 Unicast로 진행)
	    	 * 2 : Unicast
	    	 */
	    	int transferType = Integer.parseInt(request.getParameter("transferType"));
	    	/**
	    	 * 0x01 = 센서 정보 수집 및 버전 확인 (필수)
	    	 * 0x02 = 파일 전송 (Data Send)
	    	 * 0x04 = 전송 이미지 확인(Verify)
	    	 * 0x08 = 설치 (Install)
	    	 * 0x10 = 업그레이드된 버전 확인 (Scan)
	    	 * ALL  = 0x1F (남은 비트는 확장 가능 영역으로 남김)
	    	 */
	    	int otaStep = 0;
	    	try {
	    		String requestOtaStep = request.getParameter("otaStep").toUpperCase().replaceAll("0X","");
	    		
	    		Long tmp2 = Long.parseLong(requestOtaStep, 16);
	    		otaStep = Integer.parseInt(tmp2.toString());
	    	} catch (Exception e) {
				// TODO: handle exception
	    		log.error(e.getMessage());
			}
	    	/**
	    	 * Multicast일때, 펌웨어를 Multicast로 전송시 같은 프레임을 몇번 전송할것인지 결정한다.
	    	 * 즉, 멀티캐스트 성공율을 높이기 위함이며 값이 증가하면 전송시간이 증가되므로 유의해야 한다.
	    	 * 멀티캐스트가 아닌경우 이값은 (Default=1)로 지정한다.
	    	 */
	    	int multiWriteCount = Integer.parseInt(request.getParameter("multiWriteCount"));
	    	//default 10
	    	int maxRetryCount = Integer.parseInt(request.getParameter("maxRetryCount"));
	    	//default 1
	    	int otaThreadCount = Integer.parseInt(request.getParameter("otaThreadCount"));
	    	/**
	    	 * 0 : AUTO, 같은 버전 설치 안함 (현재 버전이 New Version과 동일하면 설치 안함)
	    	 * 1 : REINSTALL, 무조건 재설치, 설치 테스트 및 다시 내릴 경우, 버전이 같아도 내려짐.
	    	 * 2 : MATCH, Old H/W, S/W, Build Version이 모두 동일하면 New Version으로 설치
	    	 * 3 : FORCE, REINSTALL 처럼 무조건 재설치를 하고 File도 무조건 Base file을 다운 받는다
	    	 */
	    	int installType = Integer.parseInt(request.getParameter("installType"));
	    	/**
	    	 *  버전정보
	    	 *  0x0103 = 1.3
	    	 *  0을 제외한 임의의 버전정보입력
	    	 *  
	    	 */
	    	int oldHwVersion = getHexVersion(request.getParameter("oldHwVersion"));
	    	int oldFwVersion = getHexVersion(request.getParameter("oldFwVersion"));
	    	int oldBuild = Integer.parseInt((request.getParameter("oldBuild")).equals("0") ? "1" : request.getParameter("oldBuild"));
	    	
	    	int newHwVersion = getHexVersion(newFirmware.getHwVersion());
	    	int newFwVersion = getHexVersion(newFirmware.getFwVersion());
	    	int newBuild = Integer.parseInt((newFirmware.getBuild()).equals("0") ? "1" : newFirmware.getBuild());
	    	
	    	int dotIdx = newFirmware.getBinaryFileName().lastIndexOf(".");
	    	
	    	String fileDirectory  = newFirmware.getBinaryFileName().substring(0,dotIdx);
	    	
	    	String fileHomePath =  homePath+File.separator+fileDirectory;
	    	
	    	String filepath = fileHomePath+File.separator+newFirmware.getBinaryFileName();
	    	
	    	//binaryURL : 집중기에서 파일을 다운로드 받을 URL
	    	String binaryURL = null;
	    	File gzipFile = null;
	    	
	    	if(!(".gz".equals(fileName.substring(dotIdx)))) {
		    	//gzip으로 압축하는 부분 , F/W 업데이트 파일이 있던 위치에 gzip 파일을 생성한다.
		    	GZIPOutputStream gzipOut = new GZIPOutputStream(new FileOutputStream(filepath+".gz"));
		    	byte[] outBuffer = new byte[1024];  
		    	FileInputStream in = new FileInputStream(filepath);
		        int len;
		        while ((len = in.read(outBuffer)) > 0) {
		        	gzipOut.write(outBuffer, 0, len);
		        }  
		        	      
		        in.close();
		        gzipOut.close();
		        
		        binaryURL = fwDownURL+"?fileType=binary&fileName=" +
						newFirmware.getBinaryFileName().toLowerCase()+".gz";
		        rightBinaryURL = true;
		        
		        gzipFile = new File(filepath+".gz");
		        
	    	} else {
	    		//이미 gzip형식인 파일일때
	    		binaryURL = fwDownURL+"?fileType=binary&fileName=" +
						newFirmware.getBinaryFileName().toLowerCase();
	    		rightBinaryURL = true;
	    		
	    		gzipFile = new File(filepath);
	    		
	    	}
	    	
	    	if(!gzipFile.exists()){
	    		transactionManager.rollback(txStatus);
	    		view.addObject("rtnStr", "Can not found file");
				view.addObject("status", status.name());
				return view;
	    	}
	        		
	    	String binaryMD5 = MD5Sum.getFileMD5Sum(gzipFile);  	

			rightBinaryMD5 = true;

	    	String diffURL = null;
	    	String diffMD5 = null;
	    	isDiff = Boolean.parseBoolean(request.getParameter("isDiff"));
	    	
	    	//diff 파일은 Target File이 있는 위치에 있어야 한다.
	    	//diff 파일의 파일명은 oldFirmwareFileName_FORM_newFirmwareFileName.diff 형식으로 되어 있어야 한다.
	    	if(isDiff) {
	    		String diffFileName = filePath+ "_FROM_"+ request.getParameter("oldBinaryFileName").substring(0,request.getParameter("oldBinaryFileName").lastIndexOf("."))+ ".diff";
	    		File diffFile = new File(fileHomePath+File.separator+diffFileName);
	    		
	    		if(!diffFile.exists()) {
	    			transactionManager.rollback(txStatus);
		    		view.addObject("rtnStr", "Can not found diff File");
					view.addObject("status", status.name());
					return view;
	    		}

		    	//gzip으로 압축하는 부분 , F/W 업데이트 파일이 있던 위치에 gzip 파일을 생성한다.
		    	GZIPOutputStream diffGzipOut = new GZIPOutputStream(new FileOutputStream(fileHomePath+File.separator+diffFileName+".gz"));
		    	byte[] outBuffer = new byte[1024];  
		    	FileInputStream in = new FileInputStream(fileHomePath+File.separator+diffFileName);
		        int len;
		        while ((len = in.read(outBuffer)) > 0) {
		        	diffGzipOut.write(outBuffer, 0, len);
		        }
		        
		        in.close();
		        diffGzipOut.close();
		        
		        diffURL = fwDownURL + "?fileType=diff&fileName="+filePath+ "_FROM_"+ request.getParameter("oldBinaryFileName").substring(0,request.getParameter("oldBinaryFileName").lastIndexOf("."))+ ".diff.gz";
		        rightDiffURL = true;
		        
		        File diffGzipFile = new File(fileHomePath+File.separator+diffFileName+".gz");
			        
	    		diffMD5 = MD5Sum.getFileMD5Sum(diffGzipFile);
				rightDiffMD5 = true;

	    	}
    	
	    	String mcuIdDeviceSerial = modem.getDeviceSerial(); 
	    	
	    	ArrayList<String> equipIdList = new ArrayList<String>();
	    	equipIdList.add(mcuIdDeviceSerial);
	    	
	    	//명령 전송.
//			cmdOperationUtil.cmdDistribution(mcu.getSysID(), triggerId,
//					equipKind, model, transferType, otaStep, multiWriteCount,
//					maxRetryCount, otaThreadCount, installType, oldHwVersion,
//					oldFwVersion, oldBuild, newHwVersion, newFwVersion,
//					newBuild , binaryURL, binaryMD5, diffURL, diffMD5,
//					equipIdList);
			
			status = ResultStatus.SUCCESS;
			view.addObject("rtnStr", "success");
			view.addObject("status", status.name());
			
			transactionManager.commit(txStatus);
			
		} catch (Exception e) {
			transactionManager.rollback(txStatus);
			
			log.error(e,e);
			
			if(!saveFirmware) {
				view.addObject("rtnStr", "Add Firmware Fail.");
			} else if(!saveTrigger) {
				view.addObject("rtnStr", "Add Firmware Trigger Fail.");
			} else if(!rightBinaryURL) {
				view.addObject("rtnStr", "binaryURL error");
			} else if(!rightBinaryMD5) {
				view.addObject("rtnStr", "binaryMD5 check sum error");
			} else if(isDiff) {
				if(!rightDiffURL) {
					view.addObject("rtnStr", "diffURL error");
				} else if(!rightDiffMD5) {
					view.addObject("rtnStr", "diffMD5 check sum error");
				} 
			} else {
				view.addObject("rtnStr", e.getMessage());
				fhistory.setErrorCode(e.getMessage());
				fhistory.setOtaState(FW_STATE.Fail); 
				fhistory.setTriggerState(TR_STATE.Terminate); 
				fhistory.setTriggerStep(FW_TRIGGER.End);
			}
			
			view.addObject("status", status.name());
			return view;
			
		} finally {
			if(saveTrigger) {
				try {
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("equip_kind", "Modem");
					param.put("equip_id", modem.getDeviceSerial());
					param.put("arm", "0");
					fhistory.setTrId(ftrigger.getId());
					firmWareManager.insertFirmHistory(fhistory, param);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error(e,e);
					view.addObject("rtnStr", "Add FirmwareHistory Fail");
					view.addObject("status", status.name());
				}
			}
		}
		return view;
	}
	
	/**
	 * "#.#" 형식의 버전 숫자 포멧을 hex 코드로 변환한다. 0xNN00 = NN에정수, 0x00NN = NN에 소수 
	 * @param v
	 * @return
	 */
	private Integer getHexVersion(String v){
		
		String[] nums = v.split("\\.");
		
		Integer fDecimal = null;
		Integer sDecimal = null;
		if(nums.length<2){
			return 0;
		}
		try{
			fDecimal = (fDecimal = Integer.parseInt(nums[0]))>0xff ? 0xff : fDecimal;
			sDecimal = (sDecimal = Integer.parseInt(nums[1]))>0xff ? 0xff : sDecimal;
		}catch(Exception e){
			return 0;
		}
		Integer hVer = fDecimal << 8;
		hVer = hVer | sDecimal;
		
		System.out.println(hVer);
		
		
		return hVer;
	}

	/**
	 * Firmware Trigger 등록
	 * @param beforFirmware 이전 펌웨어 정보
	 * @param newFirmware 새롭게 OTA할 펌웨어 정보
	 * @return
	 * @throws Exception 
	 */
	/*
	private FirmwareTrigger addFirmwareTrigger(Firmware beforFirmware, Firmware newFirmware) throws Exception {
		FirmwareTrigger ftrigger = new FirmwareTrigger();
			
		ftrigger.setCreateDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
		
		//이전 펌웨어 파일명
		if(beforFirmware != null) {
			ftrigger.setSrcFirmware(beforFirmware.getBinaryFileName());
		}
						
    	//ftrigger.setSrcFWBuild(oldBuild);
    	//ftrigger.setSrcFWVer(oldFwVersion);
    	//ftrigger.setSrcHWVer(oldHwVersion);
    	
    	// 새롭게 적용할 펌웨어 파일명
    	ftrigger.setTargetFirmware(newFirmware.getBinaryFileName());
    	
    	// 빌드번호 정보
    	ftrigger.setTargetFWBuild(newFirmware.getBuild());
    	
    	// 버전정보
    	ftrigger.setTargetFWVer(newFirmware.getFwVersion());
    	
    	// 하드웨어 버전 정보
    	ftrigger.setTargetHWVer(newFirmware.getHwVersion());
		
    	firmWareManager.addFirmWareTrigger(ftrigger);
    	
    	return ftrigger;
	}
	*/

	/**
	 * DB에 펌웨어 파일 정보를 등록한다.
	 * @param fileName
	 * @param modelName
	 * @param build
	 * @param type
	 * @param energyType
	 * @param vendor
	 * @param fwVer
	 * @param hwVer
	 * @param releasedDate
	 * @return
	 */
	private Firmware addFirmware(String fileName, String modelName, String build,
			String type, String energyType, String vendor, String fwVer,
			String hwVer, String releasedDate) {

			Integer device_id = getDeviceId(modelName);
			Firmware fr = firmwareDao.findByCondition("binaryFileName", fileName); 

			if(fr==null){
				fr = new Firmware();
			}
			
			
			fr.setFirmwareId(fileName);
			fr.setArm(false);
			fr.setBinaryFileName(fileName);
			fr.setBuild(build);
			fr.setDevicemodel_id(device_id);// SX2
			fr.setEquipKind(type);
			fr.setEquipModel(modelName);
			fr.setEquipType(energyType);
			fr.setEquipVendor(vendor);

			fr.setFwVersion(fwVer);
			fr.setHwVersion(hwVer);
			fr.setReleasedDate(releasedDate); // yyyyMMdd

			firmWareManager.addFirmWare(fr);

			return fr;
		
	}

	/**
	 * 모델 명으로 조회하여 id값을 리턴한다.
	 * @param modelName
	 * @return
	 */
	private Integer getDeviceId(String modelName) {
		DeviceModel dm = deviceDao.findByCondition("name", "SX2");
		if(dm.getId()!=null){
			return dm.getId();
		}
		return null;
	}

	/**
	 * 파일 저장.
	 * @param finalFilePath 파일 저장 위치
	 * @param fileBinary 저장할 바이너리
	 * @throws IOException
	 */
	private void saveFile(String finalFilePath, byte[] fileBinary) throws IOException {
		FileOutputStream foutStream = new FileOutputStream(finalFilePath,true);
		
		foutStream.write(fileBinary);
		foutStream.flush();
		foutStream.close();
	}

	@RequestMapping(value="/gadget/device/command/cmdDistribution")
	public ModelAndView cmdDistribution(HttpServletRequest request,
										@RequestParam(value="loginId" ,required=false) String loginId,
										@RequestParam("top_equip_kind") String top_equip_kind,    
							    		@RequestParam("top_equip_type") String top_equip_type,
							    		@RequestParam("supplierId") String supplierId,
							    		@RequestParam("checkOTAListResult") String checkOTAListResult,
							    		@RequestParam("transferType") String ptransferType,
							    		@RequestParam("installType") String pinstallType,
							    		@RequestParam("otaThreadCount") String potaThreadCount,
							    		@RequestParam("maxRetryCount") String pmaxRetryCount,
							    		@RequestParam("multicastWriteCount") String pmulticastWriteCount,
							    		@RequestParam("sendEUI642") String sendEUI642,
							    		@RequestParam("saveHistory") String saveHistory,
							    		@RequestParam("fromFileNm") String fromFileNm,
							    		@RequestParam("toFileNm") String toFileNm,
							    		@RequestParam("oldHwVersion") String oldHwVersion,
							    		@RequestParam("oldFwVersion") String oldFwVersion,
							    		@RequestParam("oldBuild") String oldBuild,
							    		@RequestParam("newHwVersion") String newHwVersion,
							    		@RequestParam("newFwVersion") String newFwVersion,
							    		@RequestParam("newBuild") String newBuild,
							    		@RequestParam("equipList") String equipList,
							    		@RequestParam("oldFirmwareId") String fromFirmwareid, 
							    		@RequestParam("newFirmwareId") String toFirmwareid, 
							    		@RequestParam("oldArm") String fromArm, 
							    		@RequestParam("newArm") String toArm,
							    		@RequestParam("vendor") String vendor,
							    		@RequestParam("model") String model,					    		
							    		@RequestParam("model_id") String devicemodel_id					    		
									) {
		
		String fwDownURL = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/gadget/device/firmware/firmwareDown.jsp";
        ModelAndView mav = new ModelAndView("jsonView");
        
        log.info("CONTEXT PATH=========="+request.getContextPath());
        
        /*
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.21")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		*/
		if(toFileNm == null || "".equals(toFileNm)){
			mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg22"));        
	        return mav;	
		}
		if(equipList.equals("")){
			mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg01"));        
	        return mav;			
		}
		
		Code operationCode = null;

	    String rtnStr = "";

		try {
			
			String equipKind = top_equip_kind;
			int equipKindCd =0;
			if(equipKind.indexOf("Codi")>-1){
			     equipKindCd = FW_EQUIP.Coordinator.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
			     operationCode = codeManager.getCodeByCode("8.3.21");
			}else if(equipKind.equals("MCU")){
				equipKindCd = FW_EQUIP.MCU.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
				operationCode = codeManager.getCodeByCode("8.3.21");
			}else if(equipKind.equals("Modem")){
				equipKindCd = FW_EQUIP.Modem.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
				operationCode = codeManager.getCodeByCode("8.2.13");
			}
			
			String mcuId = "";
			
			try {
				equipList = URLDecoder.decode(equipList,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}
			
			
		   	ArrayList arrEquipList = new ArrayList();
		   	
			//if(equipKind.equals("Modem")){
				/*
				 최종 MODEM일 경우 형식으로 들어갈 것임. 
				arrEquipList	ArrayList<E>  (id=7692)	
				[
				 [31010_000D6F00003A2F3C, 31010_000D6F000030EF0C], 
				 [51010_000D6F00003A31B5, 51010_000D6F000030F1B9, 51010_000D6F000030EE4E, 51010_000D6F00003A31BF, 51010_000D6F0000307940, 51010_000D6F00003A2F97], 
				 [21010_000D6F000030EF3B, 21010_000D6F00003A316E, 21010_000D6F00003A3672, 21010_000D6F00003A2F40, 21010_000D6F000030790B, 21010_000D6F00003A3257]
				]
				 */
				
//				1. last_node는 사용자가 개개별로 선택한 값이다. 먼저 선택하여 배열에 넣는다.
				ArrayList lastnodeList = new ArrayList();
				StringTokenizer st1 = new java.util.StringTokenizer(equipList,"|");
			   	while(st1.hasMoreTokens()){
			   		String tmpStr1 = String.valueOf(st1.nextToken());
			   		if(tmpStr1.indexOf("last_node_") > -1){
			   			tmpStr1 = tmpStr1.replace("last_node_", "");
			   			lastnodeList.add(tmpStr1);
			   		}
			 	}
			   	
//			   	2. mcuId_node값을 추출한다. 개개별 선택한 값의 같은  지역에서 전체 sys_id를 선택한 값이 있을 경우 .
				ArrayList mcuIdnodeList = new ArrayList();
				StringTokenizer st2 = new java.util.StringTokenizer(equipList,"|");
			   	while(st2.hasMoreTokens()){
			   		String tmpStr2 = String.valueOf(st2.nextToken());
			   		if(tmpStr2.indexOf("mcuId_node_") > -1){
			   			tmpStr2 = tmpStr2.replace("mcuId_node_", "");
//			   			lastnodeList 와 비교해서 같은 값이 있으면 lastnode에서 각각 선택한 node이기 때문에 뺀다.
			   			boolean check1 = true;
			   			for(int i=0; i< lastnodeList.size(); i++){
			   				String tmpStr4 = String.valueOf(lastnodeList.get(i)); 
			   				tmpStr4 = tmpStr4.substring(0,tmpStr4.lastIndexOf("_"));
			   				if(tmpStr4.equals(tmpStr2)){
			   					check1 = false;
			   				}
			   			}
			   			if(check1){
			   				mcuIdnodeList.add(tmpStr2);			   				
			   			}
			   		}
			 	}
			   	
//			   	3. mcuIdnodeList,lastnodeList 는 각지역의 최상위를 선택한거로 간주 한다.(sys_id 정보가 없기 때문에 지역으로 sys_id를 가지고 와야 한다)  
//			   	   mcuIdnodeList,lastnodeList 에서 추출한 지역을 뺀 지역을 추출
				ArrayList remainLocationNodeList = new ArrayList();
				ArrayList tmplastRemainLocationNodeList = new ArrayList();
				ArrayList lastRemainLocationNodeList = new ArrayList();
				StringTokenizer st3 = new java.util.StringTokenizer(equipList,"|");
				while(st3.hasMoreTokens()){
					String tmpStr3 = String.valueOf(st3.nextToken());
					
//					경우의 수가 있어.. 형식에 맞게 변경한다.
					if(tmpStr3.indexOf("last_node_")>-1){
						tmpStr3 = tmpStr3.replace("last_node_", "");
						tmpStr3 = tmpStr3.substring(0,tmpStr3.indexOf("_"));//지역코드
					}else if(tmpStr3.indexOf("mcuId_node_")>-1){
						tmpStr3 = tmpStr3.replace("mcuId_node_", "");
						tmpStr3 = tmpStr3.substring(0,tmpStr3.indexOf("_"));//지역코드
					}else if(tmpStr3.indexOf("top_")>-1){
						tmpStr3 = tmpStr3.replace("top_", "top");
						tmpStr3 = tmpStr3.substring(tmpStr3.lastIndexOf("_")+1,tmpStr3.length());//지역코드
					}else{
						tmpStr3 = tmpStr3.substring(tmpStr3.lastIndexOf("_")+1,tmpStr3.length());//지역코드	
					}
					
					boolean check3 = true;
//					lastnodeList 비교
					if(lastnodeList.size()>0){
						for(int i=0; i<lastnodeList.size(); i++){
							String tmpStr4 = String.valueOf(lastnodeList.get(i));
							tmpStr4 = tmpStr4.substring(0,tmpStr4.indexOf("_"));						
							if(tmpStr3.equals(tmpStr4)){
								check3 = false;
							}
						}
					}

//					mcuIdnodeList 비교
					if(mcuIdnodeList.size()>0){
						for(int i=0; i<mcuIdnodeList.size(); i++){
							String tmpStr5 = String.valueOf(mcuIdnodeList.get(i));
							tmpStr5 = tmpStr5.substring(0,tmpStr5.indexOf("_"));						
							if(tmpStr3.equals(tmpStr5)){
								check3 = false;
							}
						}
					}
					
					if(check3){
						remainLocationNodeList.add(tmpStr3); //mcuIdnodeList,lastnodeList에 같지 않은 지역만 등록 된다.
					}
				}
				
//				remainLocationNodeList에서 최 하위 Node만을 추출한다. 거기서 다시 상위에서 선택한 lastnodeList,mcuIdnodeList의 지역을 제외 시키면  보낼 지역만 남게 된다.
				for(int i=0; i< remainLocationNodeList.size(); i++){
					int tmpInt1 = Integer.parseInt(String.valueOf(remainLocationNodeList.get(i)));//지역코드		
//					하위 노드 id값을 가지고 온다[6, 7, 8, 16, 21, 18, 19, 20]
					List<Integer> getChildrenList = firmWareManager.getChildren(tmpInt1);
					
					for(int j=0;j<getChildrenList.size();j++){
						boolean addCheck = true;
						String tmpInt2 = String.valueOf(getChildrenList.get(j));
						if(lastnodeList.size()>0){
							for(int k=0 ; k<lastnodeList.size(); k++){
								String tmpInt3 = String.valueOf(lastnodeList.get(k));
								tmpInt3 = tmpInt3.substring(0,tmpInt3.indexOf("_"));
								if(tmpInt2.equals(tmpInt3)){
									addCheck = false;
								}
							}
						}
//						getChildrenList 에 같은것이 없었다면 
						if(addCheck){
							if(mcuIdnodeList.size()>0){
								for(int k=0 ; k<mcuIdnodeList.size(); k++){
									String tmpInt3 = String.valueOf(mcuIdnodeList.get(k));
									tmpInt3 = tmpInt3.substring(0,tmpInt3.indexOf("_"));
									if(tmpInt2.equals(tmpInt3)){
										addCheck = false;
									}
								}
							}
						}
//						lastnodeList, mcuIdnodeList 에 같은것이 없었다면 
						if(addCheck){
							tmplastRemainLocationNodeList.add(tmpInt2);								
						}
					}
				}
				
//				tmplastRemainLocationNodeList 에는 중복된 주소들도 있을것이다.  중복된 리스트를 뺀다.
				for(int i=0 ; i< tmplastRemainLocationNodeList.size(); i++){
					String tmpStr6 = String.valueOf(tmplastRemainLocationNodeList.get(i));
					boolean check = true;
					if(i==0){
						check = true;
					}else{
						for(int k=0 ; k<lastRemainLocationNodeList.size();k++){
							String tmpStr7 = String.valueOf(lastRemainLocationNodeList.get(k));
							if(tmpStr6.equals(tmpStr7)){
								check = false;
							}
						}
					}
					
					if(check){
						lastRemainLocationNodeList.add(tmpStr6);
					}
				}
				
				HashMap<String, Object> parama = new HashMap<String, Object>();
				parama.put("devicemodel_id", devicemodel_id);
				parama.put("hwVersion", oldHwVersion);
				parama.put("swVersion", oldFwVersion);	 
				parama.put("swRevision", oldBuild);
				parama.put("equip_kind", equipKind);
				parama.put("supplierId", supplierId);
				parama.put("equip_type", top_equip_type);
				
//				지역별 mcu_id 리스트를 가지고 온다.
				if(lastRemainLocationNodeList.size() > 0){
					for(int i=0 ; i< lastRemainLocationNodeList.size();i++){
						String tmpStrLoc = String.valueOf(lastRemainLocationNodeList.get(i));
						//System.out.println("################################################tmpStrLoc="+tmpStrLoc);
						List<Object> mcuIdList = firmWareManager.getdistributeMcuIdDivList(parama, tmpStrLoc, "");
						List<Object> mcuIdDeviceSerialList = new ArrayList();

						if(mcuIdList.size()>0){
//							for(int k=0 ; k< mcuIdList.size(); k++){
							for (Object mcuobj : mcuIdList) {
								Object[] mcuobjs2 = (Object[])mcuobj;
//							     String mcu_id = String.valueOf(mcuIdList.get(k));
							     String mcu_id = String.valueOf(mcuobjs2[0]);
//							     modem일경우 device_serial을 가지고 온다.
//								  가지고 온 값을 mcu_id+"_"+device_serial 형식으로 조합해 
//								 arrEquipList 로 넘겨 준다.
							     if(equipKind.equals("Modem")){
									HashMap<String, Object> paramb = new HashMap<String, Object>();
									paramb.put("equip_kind", top_equip_kind);
									paramb.put("swVersion", oldFwVersion);
									paramb.put("hwVersion", oldHwVersion);
									paramb.put("swRevision", oldBuild);
									paramb.put("locaionId", tmpStrLoc);
									paramb.put("supplierId", supplierId);
									paramb.put("equip_type", top_equip_type);
									paramb.put("devicemodel_id", devicemodel_id);
									paramb.put("mcuId", mcu_id);
							        List<Object> deviceSrlList = firmWareManager.getDistriButeModemList(paramb, mcu_id);
							        for (Object obj2 : deviceSrlList) {
							        	 Object[] objs2 = (Object[])obj2;
							        	 String mcuId_DeviceSerial= objs2[0] +"_"+ objs2[1];
							        	 mcuIdDeviceSerialList.add(mcuId_DeviceSerial);
							        }
							     }
								
								if(top_equip_kind.equals("Modem")){
									if(mcuIdDeviceSerialList.size()>0){
										arrEquipList.add(mcuIdDeviceSerialList);
									}
								}else{
									if(mcuIdList.size()>0){
									  ArrayList al3 = new ArrayList();
									  for (Object obj2 : mcuIdList) {
									        Object[] objs2 = (Object[])obj2;
									        String total_Mcu_id = String.valueOf(objs2[0]);
									        al3.add(total_Mcu_id);
										}
/*										for(int p=0 ; p< mcuIdList.size(); p++){
										        String total_Mcu_id = String.valueOf(mcuIdList.get(p));
										        al3.add(total_Mcu_id);
										}*/
										arrEquipList.add(al3);
									}
								}								
							}
						}
					}
				}
//				mcuIdnodeList에 있는 데이터도 modem일경우 device_serial을 받아오고 아니면 mcu_id만 따로 빼낸다.
				if(mcuIdnodeList.size()>0){
					ArrayList tmpmcuIdnodeList = new ArrayList();
					for(int i=0; i<mcuIdnodeList.size(); i++){
						String tmpStr8 = String.valueOf(mcuIdnodeList.get(i));
						String tmpStrLocation = tmpStr8.substring(0,tmpStr8.indexOf("_"));
						String tmpStrMcuid = tmpStr8.substring(tmpStr8.indexOf("_")+1,tmpStr8.length());

						if(top_equip_kind.equals("Modem")){
							HashMap<String, Object> paramb = new HashMap<String, Object>();
							paramb.put("equip_kind", top_equip_kind);
							paramb.put("swVersion", oldFwVersion);
							paramb.put("hwVersion", oldHwVersion);
							paramb.put("swRevision", oldBuild);
							paramb.put("locaionId", tmpStrLocation);
							paramb.put("supplierId", supplierId);
							paramb.put("equip_type", top_equip_type);
							paramb.put("devicemodel_id", devicemodel_id);
							paramb.put("mcuId", tmpStrMcuid);
					        List<Object> deviceSrlList = firmWareManager.getDistriButeModemList(paramb, tmpStrMcuid);
					        for (Object obj2 : deviceSrlList) {
					        	 Object[] objs2 = (Object[])obj2;
					        	 String mcuId_DeviceSerial= objs2[0] +"_"+ objs2[1]+"_"+ objs2[2];
					        	 tmpmcuIdnodeList.add(mcuId_DeviceSerial);
					        }
						}else{
							tmpmcuIdnodeList.add(tmpStrMcuid);
						}
						//tmpmcuIdnodeList.add(tmpStr8);
					}
					arrEquipList.add(tmpmcuIdnodeList);	
				}
				
//				lastnodeList 도 보내지는 형식으로 변형하여 arrEquipList에 add한다
				if(lastnodeList.size()>0){
					ArrayList tmplastnodeList = new ArrayList();
					for(int i=0; i<lastnodeList.size(); i++){
						String tmpStr8 = String.valueOf(lastnodeList.get(i));
						tmpStr8 = tmpStr8.substring(tmpStr8.indexOf("_")+1,tmpStr8.length());
						tmplastnodeList.add(tmpStr8);
					}
					arrEquipList.add(tmplastnodeList);					
				}
				
//				Modem일경우 mcu_id별로 그룹핑 한다.
				if(top_equip_kind.equals("Modem")){
					ArrayList groupMcuList = new ArrayList();
					if(arrEquipList.size()>0){
						ArrayList tmpArrayList  = (ArrayList) arrEquipList.get(0);
						for(int i =0; i<tmpArrayList.size(); i++){
							String tmpStr1 = String.valueOf(tmpArrayList.get(i));
							tmpStr1 = tmpStr1.substring(0,tmpStr1.indexOf("_"));
							if(i==0){
								groupMcuList.add(tmpStr1);
							}else{
								boolean incheck = true;
								for(int j=0 ; j<groupMcuList.size(); j++){
									String tmpStr2 = String.valueOf(groupMcuList.get(j));
									if(tmpStr1.equals(tmpStr2)){
										incheck = false;
									}
								}
								if(incheck){
									groupMcuList.add(tmpStr1);
								}
							}
						}
					}
					
					ArrayList groupArrEquipList = new ArrayList();
					if(groupMcuList.size()>0){
						for(int i=0 ; i<groupMcuList.size(); i++){
							ArrayList subGroupArrEquipList = new ArrayList();
							String tmpStr1  = String.valueOf(groupMcuList.get(i));
							ArrayList tmpArrayList  = (ArrayList) arrEquipList.get(0);
							for(int j=0 ; j<tmpArrayList.size();j++){
								String tmpStr2  = String.valueOf(tmpArrayList.get(j));
								String tmpStr3 = tmpStr2.substring(0,tmpStr2.indexOf("_"));
								if(tmpStr1.equals(tmpStr3)){
									subGroupArrEquipList.add(tmpStr2);
								}
							}
							groupArrEquipList.add(subGroupArrEquipList);
						}
					}
					
					arrEquipList = groupArrEquipList;

//					arrEquipList = null;

				}

			//}

			
			if(arrEquipList.size()==0){
				mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg05"));        
		        return mav;			
			}
		   	
		   	int transferType = Integer.parseInt(ptransferType);
			int multicastWriteCount = Integer.parseInt(pmulticastWriteCount);
			int maxRetryCount = Integer.parseInt(pmaxRetryCount);
			int otaThreadCount = Integer.parseInt(potaThreadCount);
			int installType = Integer.parseInt(pinstallType);
			int otaStep = 0;
			List otaStepArr = new ArrayList();

		    StringTokenizer otaStr = new java.util.StringTokenizer(checkOTAListResult,",");
	        while(otaStr.hasMoreTokens()){
	        	String str = otaStr.nextToken();
	        	otaStepArr.add(str);
	        }
		   	
	        for(int o=0; o<otaStepArr.size(); o++){
	            otaStep|=Integer.parseInt(String.valueOf(otaStepArr.get(o)));
	        }

	        //String model = "";//모뎀인 경우만 벤더 모델을 구해온다
	        String binaryURL = "";
	        String binaryMD5 = "";
	        String diffURL = "";

	        if(fromFileNm != null && !"".equals(fromFileNm)){
		        diffURL = fwDownURL + "?fileType=diff&fileName="+toFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ "_FROM_"+ fromFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ ".diff";	        	
		    }

	        String diffMD5 = "";
	        try {
				binaryURL = FirmWareHelper.getBinaryURL(toFileNm, newBuild, fwDownURL);
				binaryMD5 = FirmWareHelper.getBinaryMD5(toFileNm, newBuild);
				if(fromFileNm != null && !"".equals(fromFileNm)){
					diffMD5 = FirmWareHelper.getDiffMD5(toFileNm,fromFileNm);
				}
				System.out.println("binaryURL---------------------------------------------------------------->"+binaryURL);
				System.out.println("binaryMD5---------------------------------------------------------------->"+binaryMD5);
				System.out.println("diffURL---------------------------------------------------------------->"+diffURL);
				System.out.println("diffMD5---------------------------------------------------------------->"+diffMD5);
				
			} catch (Exception e) {
				e.printStackTrace();
				String t = e.getMessage();

				mav.addObject("rtnStr", "ErrorMessage:"+t);    
				return mav;
			}
		   	
	        FirmwareTrigger ftrigger = null;
	        FirmwareHistory fhistory = null;
		   	//지역별 EquipList
		   	for(int i=0 ; i<arrEquipList.size(); i++){
		   		ArrayList  tempSendArrayEquipList = (ArrayList)arrEquipList.get(i);
		   		ArrayList  sendArrayEquipList = new ArrayList();
		        //String triggerId = FirmWareHelper.getNewTriggerId(uniqTriggerId);
	   		    
		   		for(int j=0; j<tempSendArrayEquipList.size(); j++){
		   			
				   	if(equipKind.equals("MCU")||equipKind.equals("Codi")){
				   		/*
				   		 * MCU, MCUCODI는 MCUID당 CMD를 한번씩 호출  
				   		 * */
				   		System.out.println("MCUID==>"+tempSendArrayEquipList.get(j));
				   		String target  = String.valueOf(tempSendArrayEquipList.get(j));
			   			if(target == null || "".equals(target)){
			   		        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
			   				return mav;
			   			}
			   			String mcu_id = firmWareManager.getIDbyMcuSysId(target);
			   			MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));
				   		sendArrayEquipList.add(tempSendArrayEquipList.get(j));

			            HashMap<String, Object> param = new HashMap<String, Object>();
						ftrigger = new FirmwareTrigger();
						fhistory= new FirmwareHistory();
						//HashMap
						param.put("equip_kind", top_equip_kind);
						param.put("equip_id", target);
						param.put("arm", toArm);
						//FirmwareTrigger
						ftrigger.setCreateDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
		            	ftrigger.setSrcFirmware(fromFirmwareid);
		            	ftrigger.setSrcFWBuild(oldBuild);
		            	ftrigger.setSrcFWVer(oldFwVersion);
		            	ftrigger.setSrcHWVer(oldHwVersion);
		            	ftrigger.setTargetFirmware(toFirmwareid);
		            	ftrigger.setTargetFWBuild(newBuild);
		            	ftrigger.setTargetFWVer(newFwVersion);
		            	ftrigger.setTargetHWVer(newHwVersion);
						//FirmwareHistory
						fhistory.setEquipId(target);
						fhistory.setEquipKind(equipKind);
						fhistory.setEquipType(top_equip_type);
						fhistory.setEquipModel(model);
						fhistory.setEquipVendor(vendor);
						fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
						fhistory.setOtaState(FW_STATE.Unknown);   
						fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
						fhistory.setTriggerCnt(i+1);
						fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
						fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
						//fhistory.setTrId(Long.parseLong(triggerId));
						fhistory.setMcu(mcu);
			            
						boolean triggerSync = false;//trigger insert 실패 후 finally에서 history 테이블에서 insert 되는것을 방지 하기 위함 
			        	/*
			        	 * cmd 호출
			        	 * */
						try{
						firmWareManager.createTrigger(ftrigger);
						triggerSync = true;
						String tr_id = String.valueOf(ftrigger.getId());
						
				        try {

				        	  String mqURL = CommandProperty.getProperty("activemq.broker.url","tcp://localhost:61616");
				        	  ConnectionFactory connectionFactory=new org.apache.activemq.ActiveMQConnectionFactory(mqURL);
				        	  Destination destination=new ActiveMQQueue("ServiceData.CommandData");
				        	  Connection connection=connectionFactory.createConnection();
				        	  Session session=connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
				        	  MessageProducer producer=session.createProducer(destination);
				        	  producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);		        	  
				        	  MapMessage message = session.createMapMessage();
				        	  message.setString("command", "cmdDistribution");
				        	  message.setString("target", target);
				        	  message.setString("tr_id", tr_id);
				        	  message.setInt("equipKindCd", equipKindCd);
				        	  message.setString("model", model);
				        	  message.setInt("transferType", transferType);
				        	  message.setInt("otaStep", otaStep);
				        	  message.setInt("multicastWriteCount", multicastWriteCount);
				        	  message.setInt("maxRetryCount", maxRetryCount);				        	  
				        	  message.setInt("otaThreadCount", otaThreadCount);
				        	  message.setInt("installType", installType);
				        	  message.setInt("oldHwVersion", FirmWareHelper.getIntVersion(oldHwVersion));
				        	  message.setInt("oldFwVersion", FirmWareHelper.getIntVersion(oldFwVersion));
				        	  message.setInt("oldBuild", FirmWareHelper.getIntBuild(oldBuild));
				        	  message.setInt("newHwVersion", FirmWareHelper.getIntVersion(newHwVersion));
				        	  message.setInt("newFwVersion", FirmWareHelper.getIntVersion(newFwVersion));
				        	  message.setInt("newBuild", FirmWareHelper.getIntBuild(newBuild));				        	  
				        	  message.setString("binaryURL", binaryURL);
				        	  message.setString("binaryMD5", binaryMD5);
				        	  message.setString("diffURL", diffURL);
				        	  message.setString("diffMD5", diffMD5);
				        	  message.setObject("sendArrayEquipList",sendArrayEquipList);
				        	  log.info("Sending message: " + message.toString());
				      	      producer.send(message);
				      	      Thread.sleep(1000);
				              session.close();
				        	  connection.close();
				        } catch (Exception e) {
				           log.error(e,e);
				        }
				        
				        /*
	    				cmdOperationUtil.cmdDistribution(target, tr_id, 
	    						equipKindCd, model, 
	    						transferType, otaStep, 
	    						multicastWriteCount, maxRetryCount, 
	    						otaThreadCount, installType, 
	    						FirmWareHelper.getIntVersion(oldHwVersion), FirmWareHelper.getIntVersion(oldFwVersion), 
	    						FirmWareHelper.getIntBuild(oldBuild),FirmWareHelper.getIntVersion(newHwVersion), 
	    						FirmWareHelper.getIntVersion(newFwVersion), FirmWareHelper.getIntBuild(newBuild), 
	    						binaryURL, binaryMD5, diffURL, 
	    						diffMD5, sendArrayEquipList);
	    						*/
						}catch(Exception e){
							//fhistory.setTriggerState(TR_STATE.Delete);//★★  STATE 확인 필요.  
							fhistory.setOtaState(FW_STATE.Fail); 
							fhistory.setTriggerState(TR_STATE.Terminate); 
							fhistory.setTriggerStep(FW_TRIGGER.End);
							fhistory.setErrorCode(e.getMessage());
							rtnStr = e.getMessage();
						}finally{
							/**
							 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
							 ***/
							if(triggerSync){
								fhistory.setTrId(ftrigger.getId());
								firmWareManager.insertFirmHistory(fhistory, param);
							}
						}	
			           //uniqTriggerId++;
          
				   	}else{
				   		/**
				   		 * MODEM은 MCUID하나당 그에 해당하는 EQUIPLIST들을 모아서 CMD를 한번씩 호출  
				   		 **/				   		
				   		String tmepStr = String.valueOf(tempSendArrayEquipList.get(j));
				   		mcuId = tmepStr.substring(0,tmepStr.indexOf("_"));
				   		sendArrayEquipList.add(tmepStr.substring(tmepStr.indexOf("_")+1,tmepStr.length()));
				   	}
		   		}
		   		
		   		if(equipKind.equals("Modem")){
		   			ArrayList modemSendEquipList = new ArrayList();
			   		String modem_mcu_id = "";
			   		System.out.println("mcuId="+mcuId);
			   		for(int j=0; j<sendArrayEquipList.size(); j++){
			   			String strId = String.valueOf(sendArrayEquipList.get(j));
			   			if(strId.indexOf("_")>0){
				   			String device_serial = strId.substring(0,strId.indexOf("_"));
				   			modem_mcu_id = strId.substring(strId.indexOf("_")+1,strId.length());
				   			modemSendEquipList.add(device_serial);
			   			}else{
				   			//String device_serial = strId.substring(0,strId.indexOf("_"));
				   			//modem_mcu_id = strId.substring(strId.indexOf("_")+1,strId.length());
			   				modem_mcu_id = mcuId;
				   			modemSendEquipList.add(strId);
			   			}

			   		}
			   		
		   			
		   			String target  = mcuId;
		   			if(target == null || "".equals(target)){
		   		        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
		   				return mav;
		   			}
		   			String mcu_id = firmWareManager.getIDbyMcuSysId(mcuId);
		   			MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));

		            HashMap<String, Object> param = new HashMap<String, Object>();
					ftrigger = new FirmwareTrigger();
					fhistory= new FirmwareHistory();
					//HashMap
					param.put("equip_kind", top_equip_kind);
					param.put("equip_id", mcuId);
					param.put("arm", toArm);
					//FirmwareTrigger
					ftrigger.setCreateDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
	            	ftrigger.setSrcFirmware(fromFirmwareid);
	            	ftrigger.setSrcFWBuild(oldBuild);
	            	ftrigger.setSrcFWVer(oldFwVersion);
	            	ftrigger.setSrcHWVer(oldHwVersion);
	            	ftrigger.setTargetFirmware(toFirmwareid);
	            	ftrigger.setTargetFWBuild(newBuild);
	            	ftrigger.setTargetFWVer(newFwVersion);
	            	ftrigger.setTargetHWVer(newHwVersion);
					//FirmwareHistory
	            	
					//fhistory.setEquipId(mcuId);
					fhistory.setEquipKind(equipKind);
					fhistory.setEquipType(top_equip_type);
					fhistory.setEquipModel(model);
					fhistory.setEquipVendor(vendor);
					fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
					fhistory.setOtaState(FW_STATE.Unknown);   
					fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
//					fhistory.setTriggerCnt(i+1);
					fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
					fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
					//fhistory.setTrId(Long.parseLong(triggerId));
					fhistory.setMcu(mcu.getMcu());
					boolean triggerSync = false;//trigger insert 실패 후 finally에서 history 테이블에서 insert 되는것을 방지 하기 위함
		        	/*
		        	 * cmd 호출
		        	 * */
		            try{
					firmWareManager.createTrigger(ftrigger);
					triggerSync = true;
    				String tr_id = String.valueOf(ftrigger.getId());
    				

					cmdOperationUtil.cmdDistribution(mcuId, tr_id, 
    						equipKindCd, model, 
    						transferType, otaStep, 
    						multicastWriteCount, maxRetryCount, 
    						otaThreadCount, installType, 
    						FirmWareHelper.getIntVersion(oldHwVersion), FirmWareHelper.getIntVersion(oldFwVersion), 
    						FirmWareHelper.getIntBuild(oldBuild),FirmWareHelper.getIntVersion(newHwVersion), 
    						FirmWareHelper.getIntVersion(newFwVersion), FirmWareHelper.getIntBuild(newBuild), 
    						binaryURL, binaryMD5, diffURL, 
    						diffMD5, modemSendEquipList);
//					diffMD5, sendArrayEquipList);
					}catch(Exception e){
						fhistory.setTriggerState(TR_STATE.Terminate); 
						fhistory.setTriggerStep(FW_TRIGGER.End);
						fhistory.setOtaState(FW_STATE.Fail);
						fhistory.setErrorCode(e.getMessage());
						rtnStr = e.getMessage();
					}finally{
						/**
						 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
						 ***/
						if(triggerSync){
							fhistory.setTrId(ftrigger.getId());
							firmWareManager.insertModemFirmHistory(fhistory, param , sendArrayEquipList);							
						}
					}	
		           //uniqTriggerId++;
		   		}
		   	}        
		   	rtnStr = getPropertyMessage("aimir.firmware.msg06");
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
	    	mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg07"));        
	        return mav;  
		}

		/**
		 * 모든작업이 완료 후 trigger, history 테이블에 인서트 하여준다.
		 * treigger에서 //장비 타입이 Codi인 경우는 버전 정보를 16진수로 저장해준다
		 * history는  select , insert , update 순
		 * **/
        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	@Deprecated
	@RequestMapping(value="/gadget/device/command/cmdPackageDistribution")
	public ModelAndView cmdPackageDistribution(
			                        //@RequestParam(value="target" ,required=false) 	String target,
									HttpServletRequest request,
									@RequestParam(value="loginId" ,required=false) String loginId,
									@RequestParam("top_equip_kind") String top_equip_kind,    
						    		@RequestParam("top_equip_type") String top_equip_type,
						    		@RequestParam("supplierId") String supplierId,
						    		@RequestParam("checkOTAListResult") String checkOTAListResult,
						    		@RequestParam("transferType") String ptransferType,
						    		@RequestParam("installType") String pinstallType,
						    		@RequestParam("otaThreadCount") String potaThreadCount,
						    		@RequestParam("maxRetryCount") String pmaxRetryCount,
						    		@RequestParam("multicastWriteCount") String pmulticastWriteCount,
						    		@RequestParam("sendEUI642") String sendEUI642,
						    		@RequestParam("saveHistory") String saveHistory,
						    		@RequestParam("fromFileNm") String fromFileNm,
						    		@RequestParam("toFileNm") String toFileNm,
						    		@RequestParam("oldHwVersion") String oldHwVersion,
						    		@RequestParam("oldFwVersion") String oldFwVersion,
						    		@RequestParam("oldBuild") String oldBuild,
						    		@RequestParam("newHwVersion") String newHwVersion,
						    		@RequestParam("newFwVersion") String newFwVersion,
						    		@RequestParam("newBuild") String newBuild,
						    		@RequestParam("equipList") String equipList, 
						    		@RequestParam("oldFirmwareId") String fromFirmwareid, 
						    		@RequestParam("newFirmwareId") String toFirmwareid, 
						    		@RequestParam("oldArm") String fromArm, 
						    		@RequestParam("newArm") String toArm,
						    		@RequestParam("vendor") String vendor,
						    		@RequestParam("model") String model,
						    		@RequestParam("model_id") String devicemodel_id
						    		) {		

		String fwDownURL = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/gadget/device/firmware/firmwareDown.jsp";
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}

	    String rtnStr = "";

		try {
		
			String equipKind = top_equip_kind;
//			int equipKindCd = CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
			int equipKindCd =0;
			if(equipKind.indexOf("Codi")>-1){
			     equipKindCd = FW_EQUIP.Coordinator.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
			}else if(equipKind.equals("MCU")){
				equipKindCd = FW_EQUIP.MCU.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
			}else if(equipKind.equals("Modem")){
				equipKindCd = FW_EQUIP.Modem.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
			}
			
			//int equipType = 0;
/*			
			if(equipKind.equals("MCU")){
				equipType = CommonConstants.McuType.valueOf(top_equip_type).getCode();//
			}else if(equipKind.equals("Modem")){
				equipType = CommonConstants.ModemType.valueOf(top_equip_type).getCode();//
			}else if(equipKind.equals("Codi")){

			}*/
			
			String mcuId = "";
			
			try {
				equipList = URLDecoder.decode(equipList,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}
			
		   	ArrayList arrEquipList = new ArrayList();
		   	
			//if(equipKind.equals("Modem")){
				/*
				 최종 MODEM일 경우 형식으로 들어갈 것임. 
				arrEquipList	ArrayList<E>  (id=7692)	
				[
				 [31010_000D6F00003A2F3C, 31010_000D6F000030EF0C], 
				 [51010_000D6F00003A31B5, 51010_000D6F000030F1B9, 51010_000D6F000030EE4E, 51010_000D6F00003A31BF, 51010_000D6F0000307940, 51010_000D6F00003A2F97], 
				 [21010_000D6F000030EF3B, 21010_000D6F00003A316E, 21010_000D6F00003A3672, 21010_000D6F00003A2F40, 21010_000D6F000030790B, 21010_000D6F00003A3257]
				]
				 */
				
//				1. last_node는 사용자가 개개별로 선택한 값이다. 먼저 선택하여 배열에 넣는다.
				ArrayList lastnodeList = new ArrayList();
				StringTokenizer st1 = new java.util.StringTokenizer(equipList,"|");
			   	while(st1.hasMoreTokens()){
			   		String tmpStr1 = String.valueOf(st1.nextToken());
			   		if(tmpStr1.indexOf("last_node_") > -1){
			   			tmpStr1 = tmpStr1.replace("last_node_", "");
			   			lastnodeList.add(tmpStr1);
			   		}
			 	}
			   	
//			   	2. mcuId_node값을 추출한다. 개개별 선택한 값의 같은  지역에서 전체 sys_id를 선택한 값이 있을 경우 .
				ArrayList mcuIdnodeList = new ArrayList();
				StringTokenizer st2 = new java.util.StringTokenizer(equipList,"|");
			   	while(st2.hasMoreTokens()){
			   		String tmpStr2 = String.valueOf(st2.nextToken());
			   		if(tmpStr2.indexOf("mcuId_node_") > -1){
			   			tmpStr2 = tmpStr2.replace("mcuId_node_", "");
//			   			lastnodeList 와 비교해서 같은 값이 있으면 lastnode에서 각각 선택한 node이기 때문에 뺀다.
			   			boolean check1 = true;
			   			for(int i=0; i< lastnodeList.size(); i++){
			   				String tmpStr4 = String.valueOf(lastnodeList.get(i)); 
			   				tmpStr4 = tmpStr4.substring(0,tmpStr4.lastIndexOf("_"));
			   				if(tmpStr4.equals(tmpStr2)){
			   					check1 = false;
			   				}
			   			}
			   			if(check1){
			   				mcuIdnodeList.add(tmpStr2);			   				
			   			}
			   		}
			 	}
			   	
//			   	3. mcuIdnodeList,lastnodeList 는 각지역의 최상위를 선택한거로 간주 한다.(sys_id 정보가 없기 때문에 지역으로 sys_id를 가지고 와야 한다)  
//			   	   mcuIdnodeList,lastnodeList 에서 추출한 지역을 뺀 지역을 추출
				ArrayList remainLocationNodeList = new ArrayList();
				ArrayList tmplastRemainLocationNodeList = new ArrayList();
				ArrayList lastRemainLocationNodeList = new ArrayList();
				StringTokenizer st3 = new java.util.StringTokenizer(equipList,"|");
				while(st3.hasMoreTokens()){
					String tmpStr3 = String.valueOf(st3.nextToken());
					
//					경우의 수가 있어.. 형식에 맞게 변경한다.
					if(tmpStr3.indexOf("last_node_")>-1){
						tmpStr3 = tmpStr3.replace("last_node_", "");
						tmpStr3 = tmpStr3.substring(0,tmpStr3.indexOf("_"));//지역코드
					}else if(tmpStr3.indexOf("mcuId_node_")>-1){
						tmpStr3 = tmpStr3.replace("mcuId_node_", "");
						tmpStr3 = tmpStr3.substring(0,tmpStr3.indexOf("_"));//지역코드
					}else if(tmpStr3.indexOf("top_")>-1){
						tmpStr3 = tmpStr3.replace("top_", "top");
						tmpStr3 = tmpStr3.substring(tmpStr3.lastIndexOf("_")+1,tmpStr3.length());//지역코드
					}else{
						tmpStr3 = tmpStr3.substring(tmpStr3.lastIndexOf("_")+1,tmpStr3.length());//지역코드	
					}
					
					boolean check3 = true;
//					lastnodeList 비교
					if(lastnodeList.size()>0){
						for(int i=0; i<lastnodeList.size(); i++){
							String tmpStr4 = String.valueOf(lastnodeList.get(i));
							tmpStr4 = tmpStr4.substring(0,tmpStr4.indexOf("_"));						
							if(tmpStr3.equals(tmpStr4)){
								check3 = false;
							}
						}
					}

//					mcuIdnodeList 비교
					if(mcuIdnodeList.size()>0){
						for(int i=0; i<mcuIdnodeList.size(); i++){
							String tmpStr5 = String.valueOf(mcuIdnodeList.get(i));
							tmpStr5 = tmpStr5.substring(0,tmpStr5.indexOf("_"));						
							if(tmpStr3.equals(tmpStr5)){
								check3 = false;
							}
						}
					}
					
					if(check3){
						remainLocationNodeList.add(tmpStr3); //mcuIdnodeList,lastnodeList에 같지 않은 지역만 등록 된다.
					}
				}
				
//				remainLocationNodeList에서 최 하위 Node만을 추출한다. 거기서 다시 상위에서 선택한 lastnodeList,mcuIdnodeList의 지역을 제외 시키면  보낼 지역만 남게 된다.
				for(int i=0; i< remainLocationNodeList.size(); i++){
					int tmpInt1 = Integer.parseInt(String.valueOf(remainLocationNodeList.get(i)));//지역코드		
//					하위 노드 id값을 가지고 온다[6, 7, 8, 16, 21, 18, 19, 20]
					List<Integer> getChildrenList = firmWareManager.getChildren(tmpInt1);
					
					for(int j=0;j<getChildrenList.size();j++){
						boolean addCheck = true;
						String tmpInt2 = String.valueOf(getChildrenList.get(j));
						if(lastnodeList.size()>0){
							for(int k=0 ; k<lastnodeList.size(); k++){
								String tmpInt3 = String.valueOf(lastnodeList.get(k));
								tmpInt3 = tmpInt3.substring(0,tmpInt3.indexOf("_"));
								if(tmpInt2.equals(tmpInt3)){
									addCheck = false;
								}
							}
						}
//						getChildrenList 에 같은것이 없었다면 
						if(addCheck){
							if(mcuIdnodeList.size()>0){
								for(int k=0 ; k<mcuIdnodeList.size(); k++){
									String tmpInt3 = String.valueOf(mcuIdnodeList.get(k));
									tmpInt3 = tmpInt3.substring(0,tmpInt3.indexOf("_"));
									if(tmpInt2.equals(tmpInt3)){
										addCheck = false;
									}
								}
							}
						}
//						lastnodeList, mcuIdnodeList 에 같은것이 없었다면 
						if(addCheck){
							tmplastRemainLocationNodeList.add(tmpInt2);								
						}
					}
				}
				
//				tmplastRemainLocationNodeList 에는 중복된 주소들도 있을것이다.  중복된 리스트를 뺀다.
				for(int i=0 ; i< tmplastRemainLocationNodeList.size(); i++){
					String tmpStr6 = String.valueOf(tmplastRemainLocationNodeList.get(i));
					boolean check = true;
					if(i==0){
						check = true;
					}else{
						for(int k=0 ; k<lastRemainLocationNodeList.size();k++){
							String tmpStr7 = String.valueOf(lastRemainLocationNodeList.get(k));
							if(tmpStr6.equals(tmpStr7)){
								check = false;
							}
						}
					}
					
					if(check){
						lastRemainLocationNodeList.add(tmpStr6);
					}
				}
				
				HashMap<String, Object> parama = new HashMap<String, Object>();
				parama.put("devicemodel_id", devicemodel_id);
				parama.put("hwVersion", oldHwVersion);
				parama.put("swVersion", oldFwVersion);	 
				parama.put("swRevision", oldBuild);
				parama.put("equip_kind", equipKind);
				parama.put("supplierId", supplierId);
				parama.put("equip_type", top_equip_type);
				
//				지역별 mcu_id 리스트를 가지고 온다.
				if(lastRemainLocationNodeList.size() > 0){
					for(int i=0 ; i< lastRemainLocationNodeList.size();i++){
						String tmpStrLoc = String.valueOf(lastRemainLocationNodeList.get(i));	
						List<Object> mcuIdList = firmWareManager.getdistributeMcuIdDivList(parama, tmpStrLoc, "");
						List<Object> mcuIdDeviceSerialList = new ArrayList();

						if(mcuIdList.size()>0){
//							for(int k=0 ; k< mcuIdList.size(); k++){
						    for (Object obj : mcuIdList) {
						        Object[] objs = (Object[])obj;
//							     String mcu_id = String.valueOf(mcuIdList.get(k));
							     String mcu_id = String.valueOf(objs[0]);
//							     modem일경우 device_serial을 가지고 온다.
//								  가지고 온 값을 mcu_id+"_"+device_serial 형식으로 조합해 
//								 arrEquipList 로 넘겨 준다.
							     if(equipKind.equals("Modem")){
									HashMap<String, Object> paramb = new HashMap<String, Object>();
									paramb.put("equip_kind", top_equip_kind);
									paramb.put("swVersion", oldFwVersion);
									paramb.put("hwVersion", oldHwVersion);
									paramb.put("swRevision", oldBuild);
									paramb.put("locaionId", tmpStrLoc);
									paramb.put("supplierId", supplierId);
									paramb.put("equip_type", top_equip_type);
									paramb.put("devicemodel_id", devicemodel_id);
									paramb.put("mcuId", mcu_id);
							        List<Object> deviceSrlList = firmWareManager.getDistriButeModemList(paramb, mcu_id);
							        for (Object obj2 : deviceSrlList) {
							        	 Object[] objs2 = (Object[])obj2;
							        	 String mcuId_DeviceSerial= objs2[0] +"_"+ objs2[1]+"_"+ objs2[2];
							        	 mcuIdDeviceSerialList.add(mcuId_DeviceSerial);
							        }
							     }
								
								if(top_equip_kind.equals("Modem")){
									if(mcuIdDeviceSerialList.size()>0){
										arrEquipList.add(mcuIdDeviceSerialList);
									}
								}else{
									if(mcuIdList.size()>0){
										ArrayList al3 = new ArrayList();
										
										  for (Object obj2 : mcuIdList) {
										        Object[] objs2 = (Object[])obj2;
										        String total_Mcu_id = String.valueOf(objs2[0]);
										        al3.add(total_Mcu_id);
											}
/*										for(int p=0 ; p< mcuIdList.size(); p++){
										        String total_Mcu_id = String.valueOf(mcuIdList.get(p));
										        al3.add(total_Mcu_id);
										}*/
										arrEquipList.add(al3);
									}
								}								
							}
						}
					}
				}
//				mcuIdnodeList에 있는 데이터도 modem일경우 device_serial을 받아오고 아니면 mcu_id만 따로 빼낸다.
				if(mcuIdnodeList.size()>0){
					ArrayList tmpmcuIdnodeList = new ArrayList();
					for(int i=0; i<mcuIdnodeList.size(); i++){
						String tmpStr8 = String.valueOf(mcuIdnodeList.get(i));
						String tmpStrLocation = tmpStr8.substring(0,tmpStr8.indexOf("_"));
						String tmpStrMcuid = tmpStr8.substring(tmpStr8.indexOf("_")+1,tmpStr8.length());

						if(top_equip_kind.equals("Modem")){
							HashMap<String, Object> paramb = new HashMap<String, Object>();
							paramb.put("equip_kind", top_equip_kind);
							paramb.put("swVersion", oldFwVersion);
							paramb.put("hwVersion", oldHwVersion);
							paramb.put("swRevision", oldBuild);
							paramb.put("locaionId", tmpStrLocation);
							paramb.put("supplierId", supplierId);
							paramb.put("equip_type", top_equip_type);
							paramb.put("devicemodel_id", devicemodel_id);
							paramb.put("mcuId", tmpStrMcuid);
					        List<Object> deviceSrlList = firmWareManager.getDistriButeModemList(paramb, tmpStrMcuid);
					        for (Object obj2 : deviceSrlList) {
					        	 Object[] objs2 = (Object[])obj2;
					        	 String mcuId_DeviceSerial= objs2[0] +"_"+ objs2[1]+"_"+ objs2[2];
					        	 tmpmcuIdnodeList.add(mcuId_DeviceSerial);
					        }
						}else{
							tmpmcuIdnodeList.add(tmpStrMcuid);
						}
						//tmpmcuIdnodeList.add(tmpStr8);
					}
					arrEquipList.add(tmpmcuIdnodeList);	
				}
				
				
				
//				lastnodeList 도 보내지는 형식으로 변형하여 arrEquipList에 add한다
				if(lastnodeList.size()>0){
					ArrayList tmplastnodeList = new ArrayList();
					for(int i=0; i<lastnodeList.size(); i++){
						String tmpStr8 = String.valueOf(lastnodeList.get(i));
						tmpStr8 = tmpStr8.substring(tmpStr8.indexOf("_")+1,tmpStr8.length());
						tmplastnodeList.add(tmpStr8);
					}
					arrEquipList.add(tmplastnodeList);					
				}
				
//				Modem일경우 mcu_id별로 그룹핑 한다.
				if(top_equip_kind.equals("Modem")){
					ArrayList groupMcuList = new ArrayList();
					if(arrEquipList.size()>0){
						ArrayList tmpArrayList  = (ArrayList) arrEquipList.get(0);
						for(int i =0; i<tmpArrayList.size(); i++){
							String tmpStr1 = String.valueOf(tmpArrayList.get(i));
							tmpStr1 = tmpStr1.substring(0,tmpStr1.indexOf("_"));
							if(i==0){
								groupMcuList.add(tmpStr1);
							}else{
								boolean incheck = true;
								for(int j=0 ; j<groupMcuList.size(); j++){
									String tmpStr2 = String.valueOf(groupMcuList.get(j));
									if(tmpStr1.equals(tmpStr2)){
										incheck = false;
									}
								}
								if(incheck){
									groupMcuList.add(tmpStr1);
								}
							}
						}
					}
					
					ArrayList groupArrEquipList = new ArrayList();
					if(groupMcuList.size()>0){
						for(int i=0 ; i<groupMcuList.size(); i++){
							ArrayList subGroupArrEquipList = new ArrayList();
							String tmpStr1  = String.valueOf(groupMcuList.get(i));
							ArrayList tmpArrayList  = (ArrayList) arrEquipList.get(0);
							for(int j=0 ; j<tmpArrayList.size();j++){
								String tmpStr2  = String.valueOf(tmpArrayList.get(j));
								String tmpStr3 = tmpStr2.substring(0,tmpStr2.indexOf("_"));
//								System.out.println("tmpStr1:"+tmpStr1+"=tmpStr3:"+tmpStr3);
								if(tmpStr1.equals(tmpStr3)){
									subGroupArrEquipList.add(tmpStr2);
								}
							}
							groupArrEquipList.add(subGroupArrEquipList);
						}
					}
					
					arrEquipList = groupArrEquipList;

//					arrEquipList = null;
					System.out.println("");
				}
				
				System.out.println("");
			//}

		   	
			if(arrEquipList.size()==0){
				mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg05"));        
		        return mav;			
			}
		   	
		   	
		   	int transferType = Integer.parseInt(ptransferType);
			int multicastWriteCount = Integer.parseInt(pmulticastWriteCount);
			int maxRetryCount = Integer.parseInt(pmaxRetryCount);
			int otaThreadCount = Integer.parseInt(potaThreadCount);
			int installType = Integer.parseInt(pinstallType);
			int otaStep = 0;
			List otaStepArr = new ArrayList();

		    StringTokenizer otaStr = new java.util.StringTokenizer(checkOTAListResult,",");
	        while(otaStr.hasMoreTokens()){
	        	String str = otaStr.nextToken();
	        	otaStepArr.add(str);
	        }
		   	
	        for(int o=0; o<otaStepArr.size(); o++){
	            otaStep|=Integer.parseInt(String.valueOf(otaStepArr.get(o)));
	        }

	        //String model = "";//모뎀인 경우만 벤더 모델을 구해온다
	        String binaryURL = "";
	        String binaryMD5 = "";
	        String diffURL = fwDownURL + "?fileType=diff&fileName="+toFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ "_FROM_"+ fromFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ ".diff";
	        String diffMD5 = "";
	        try {
				binaryURL = FirmWareHelper.getBinaryURL(toFileNm, newBuild, fwDownURL);
				binaryMD5 = FirmWareHelper.getBinaryMD5(toFileNm, newBuild);
				diffMD5 = FirmWareHelper.getDiffMD5(toFileNm,fromFileNm);
			} catch (Exception e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}
		   	
	        //int uniqTriggerId=0; 
	        FirmwareTrigger ftrigger = null;
	        FirmwareHistory fhistory = null;
		   	//지역별 EquipList
		   	for(int i=0 ; i<arrEquipList.size(); i++){
		   		ArrayList  tempSendArrayEquipList = (ArrayList)arrEquipList.get(i);
		   		ArrayList  sendArrayEquipList = new ArrayList();
		        //String triggerId = FirmWareHelper.getNewTriggerId(uniqTriggerId);	   	
		   		for(int j=0; j<tempSendArrayEquipList.size(); j++){
				   	if(equipKind.equals("MCU")||equipKind.equals("Codi")){
				   		String target = String.valueOf(tempSendArrayEquipList.get(j));
						if(target == null || "".equals(target)){
					        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
							return mav;
						}
				       	//MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
				   		sendArrayEquipList.add(tempSendArrayEquipList.get(j));
				   		
			            HashMap<String, Object> param = new HashMap<String, Object>();
						ftrigger = new FirmwareTrigger();
						fhistory= new FirmwareHistory();
						//HashMap
						param.put("equip_kind", top_equip_kind);
						param.put("equip_id", target);
						param.put("arm", toArm);
						//FirmwareTrigger
						ftrigger.setCreateDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
		            	ftrigger.setSrcFirmware(fromFirmwareid);
		            	ftrigger.setSrcFWBuild(oldBuild);
		            	ftrigger.setSrcFWVer(oldFwVersion);
		            	ftrigger.setSrcHWVer(oldHwVersion);
		            	ftrigger.setTargetFirmware(toFirmwareid);
		            	ftrigger.setTargetFWBuild(newBuild);
		            	ftrigger.setTargetFWVer(newFwVersion);
		            	ftrigger.setTargetHWVer(newHwVersion);
		            	
						//FirmwareHistory
						fhistory.setEquipId(target);
						fhistory.setEquipKind(equipKind);
						fhistory.setEquipType(top_equip_type);
						fhistory.setEquipModel(model);
						fhistory.setEquipVendor(vendor);
						//fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
						fhistory.setOtaState(FW_STATE.Unknown);   
						fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
//						fhistory.setTriggerCnt(i+1);
						fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
						fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
						//fhistory.setMcu(mcu);
			            
						/*
						 * cmd 호출
						 * */
			            //의미는 없지만 배열 형식을 맞추기 위해
						String[] equipArr = new String[sendArrayEquipList.size()];
						for(int k=0 ; k<sendArrayEquipList.size() ;k++){
							equipArr[k] = (String) sendArrayEquipList.get(k);
						}
						
						boolean triggerSync = false;//trigger insert 실패 후 finally history 테이블에서 insert 되는것을 방지 하기 위함
						
						try{
							firmWareManager.createTrigger(ftrigger);
							triggerSync = true;
							String tr_id = String.valueOf(ftrigger.getId());
							/*
								cmdPackageDistribution(String mcuId, int equipType, 
								String triggerId, String oldHwVersion, 
								String oldSwVersion, String oldBuildNumber, 
								String newHwVersion, String newSwVersion, 
								String newBuildNumber, String binaryMD5, 
								String binaryUrl, String diffMD5, 
								String diffUrl, String[] equipList, 
								int otaType, int modemType, 
								String modemTypeStr, int dataType, 
								int otaLevel, int otaRetry) 							 
							 * */
							cmdOperationUtil.cmdPackageDistribution(target, equipKindCd, 
								tr_id, oldHwVersion, 
								oldFwVersion, oldBuild, 
								newHwVersion, newFwVersion, 
								newBuild, binaryMD5, 
								binaryURL, diffMD5, 
								diffURL, equipArr, 
								0, 0, //★★ otaType,meterType 확인필요 
								top_equip_type, transferType,//★★ dataType 확인필요 
								otaStep, maxRetryCount);//★★otaLevel, otaRetry  확인필요
						}catch(Exception e){
							fhistory.setTriggerState(TR_STATE.Terminate); 
							fhistory.setTriggerStep(FW_TRIGGER.End); 
							fhistory.setOtaState(FW_STATE.Fail);
							fhistory.setErrorCode(e.getMessage());
							rtnStr = e.getMessage();
						}finally{
							/**
							 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
							 ***/
							if(triggerSync){
								fhistory.setTrId(ftrigger.getId());
								firmWareManager.insertFirmHistory(fhistory, param);								
							}
						}	
			            
				   		//uniqTriggerId++;
				   	}else{
				   		String tmepStr = String.valueOf(tempSendArrayEquipList.get(j));
				   		mcuId = tmepStr.substring(0,tmepStr.indexOf("_"));
				   		sendArrayEquipList.add(tmepStr.substring(tmepStr.indexOf("_")+1,tmepStr.length()));
				   	}
				   	
		   		}

			   	if(equipKind.equals("Modem")){
			   		ArrayList modemSendEquipList = new ArrayList();
			   		String modem_mcu_id = "";
			   		
			   		for(int j=0; j<sendArrayEquipList.size(); j++){
			   			String strId = String.valueOf(sendArrayEquipList.get(j));
			   			if(strId.indexOf("_")>0){
				   			String device_serial = strId.substring(0,strId.indexOf("_"));
				   			modem_mcu_id = strId.substring(strId.indexOf("_")+1,strId.length());
				   			modemSendEquipList.add(device_serial);
			   			}else{
				   			//String device_serial = strId.substring(0,strId.indexOf("_"));
				   			//modem_mcu_id = strId.substring(strId.indexOf("_")+1,strId.length());
			   				modem_mcu_id = mcuId;
				   			modemSendEquipList.add(strId);
			   			}

			   		}
			   		
/*			   		for(int j=0; j<sendArrayEquipList.size(); j++){
			   			String strId = String.valueOf(sendArrayEquipList.get(j));
			   			String device_serial = strId.substring(0,strId.indexOf("_"));
			   			modem_mcu_id = strId.substring(strId.indexOf("_")+1,strId.length());
			   			modemSendEquipList.add(device_serial);
			   		}*/
			   		
			   		String target = mcuId;
					if(target == null || "".equals(target)){
				        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
						return mav;
					}
					String mcu_id = firmWareManager.getIDbyMcuSysId(target);
			       	MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));
			       	
		            HashMap<String, Object> param = new HashMap<String, Object>();
					ftrigger = new FirmwareTrigger();
					fhistory= new FirmwareHistory();
					
					//HashMap
					param.put("equip_kind", top_equip_kind);
					param.put("equip_id", mcuId);
					param.put("arm", toArm);
					//FirmwareTrigger
					ftrigger.setCreateDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
	            	ftrigger.setSrcFirmware(fromFirmwareid);
	            	ftrigger.setSrcFWBuild(oldBuild);
	            	ftrigger.setSrcFWVer(oldFwVersion);
	            	ftrigger.setSrcHWVer(oldHwVersion);
	            	ftrigger.setTargetFirmware(toFirmwareid);
	            	ftrigger.setTargetFWBuild(newBuild);
	            	ftrigger.setTargetFWVer(newFwVersion);
	            	ftrigger.setTargetHWVer(newHwVersion);
					//FirmwareHistory
					fhistory.setEquipId(mcuId);
					fhistory.setEquipKind(equipKind);
					fhistory.setEquipType(top_equip_type);
					fhistory.setEquipModel(model);
					fhistory.setEquipVendor(vendor);
					fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
					fhistory.setOtaState(FW_STATE.Unknown);   
					fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
					fhistory.setTriggerCnt(i+1);
					fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
					fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
//					fhistory.setTrId(Long.parseLong(triggerId));
//					fhistory.setMcu(mcu);
					fhistory.setMcu(mcu.getMcu());
					boolean triggerSync = false;//trigger insert 실패 후 finally history 테이블에서 insert 되는것을 방지 하기 위함
					/**
					 * cmd 호출
					 **/
					try{
						
						String[] equipArr = new String[modemSendEquipList.size()];
						
//						for(int j=0 ; j<sendArrayEquipList.size() ;j++){
						for(int j=0 ; j<modemSendEquipList.size() ;j++){
							equipArr[j] = (String) modemSendEquipList.get(j);
						}
						
						firmWareManager.createTrigger(ftrigger);
						triggerSync = true;
						String tr_id = String.valueOf(ftrigger.getId());
						System.out.println("tr_id------------------------------>"+tr_id);
						cmdOperationUtil.cmdPackageDistribution(mcuId, equipKindCd, 
								tr_id, oldHwVersion, 
								oldFwVersion, oldBuild, 
								newHwVersion, newFwVersion, 
								newBuild, binaryMD5, 
								binaryURL, diffMD5, 
								diffURL, equipArr, 
								0, 0, //★★ transferType,equipType 확인필요 
								top_equip_type, transferType,//★★ dataType 확인필요 
								otaStep, maxRetryCount);//★★otaLevel, otaRetry  확인필요
						
					}catch(Exception e){
						fhistory.setTriggerState(TR_STATE.Terminate); 
						fhistory.setTriggerStep(FW_TRIGGER.End); 
						fhistory.setOtaState(FW_STATE.Fail);
						fhistory.setErrorCode(e.getMessage());
						rtnStr = e.getMessage();
					}finally{
						/**
						 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
						 ***/
						if(triggerSync){
							fhistory.setTrId(ftrigger.getId());
							firmWareManager.insertModemFirmHistory(fhistory, param,sendArrayEquipList);							
						}
					}		
			   		//uniqTriggerId++;
			   	}		   		
		   	}        
			rtnStr = getPropertyMessage("aimir.firmware.msg06");
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
	    	e.printStackTrace();
		}
        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	
	//재배포
	@RequestMapping(value="/gadget/device/command/cmdReDistribution")
	public ModelAndView cmdReDistribution(
											HttpServletRequest request,
											@RequestParam(value="loginId" ,required=false) String loginId,
											@RequestParam("top_equip_kind") String top_equip_kind,    
								    		@RequestParam("top_equip_type") String top_equip_type,
								    		@RequestParam("supplierId") String supplierId,
								    		@RequestParam("transferType") String ptransferType,
								    		@RequestParam("installType") String pinstallType,
								    		@RequestParam("otaThreadCount") String potaThreadCount,
								    		@RequestParam("maxRetryCount") String pmaxRetryCount,
								    		@RequestParam("multicastWriteCount") String pmulticastWriteCount,
								    		@RequestParam("sendEUI642") String sendEUI642,
								    		@RequestParam("saveHistory") String saveHistory,
								    		@RequestParam("fromFileNm") String fromFileNm,
								    		@RequestParam("toFileNm") String toFileNm,
								    		@RequestParam("equipList") String equipList, 
								    		@RequestParam("vendor") String vendor,
								    		@RequestParam("model") String model,
								    		@RequestParam("device_serial") String device_serial,
								    		@RequestParam("tr_id") String tr_id,
								    		@RequestParam("src_firmware") String src_firmware,
								    		@RequestParam("target_firmware") String target_firmware,
								    		@RequestParam("retrigger_cnt") String trigger_cnt,
								    		@RequestParam("retrigger_history") String trigger_history,
								    		@RequestParam("remcu_id") String sys_id,
								    		@RequestParam("modem_mcu_id") String modem_mcu_id
									) {

		String fwDownURL = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/gadget/device/firmware/firmwareDown.jsp";
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		String rtnStr = "";
		
//		int equipKindCd = CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		log.info("Equip Type="+top_equip_type);
		int equipKindCd =0;
		if(top_equip_type.indexOf("Codi")>-1){
		     equipKindCd = FW_EQUIP.Coordinator.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}else if(top_equip_type.equals("MCU")){
			equipKindCd = FW_EQUIP.MCU.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}else if(top_equip_type.equals("Modem") || ModemType.isModemType(top_equip_type)){
			equipKindCd = FW_EQUIP.Modem.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}
		
        StringTokenizer split_srcFirmware = new java.util.StringTokenizer(src_firmware,"_");
        StringTokenizer split_targetFirmware = new java.util.StringTokenizer(target_firmware,"_");
        ArrayList srcFirmwareArr =  new ArrayList();
        ArrayList targetFirmwareArr =  new ArrayList();
        
        while(split_srcFirmware.hasMoreTokens()){
        	String str =  split_srcFirmware.nextToken();
        	srcFirmwareArr.add(str);
        }
        while(split_targetFirmware.hasMoreTokens()){
        	String str =  split_targetFirmware.nextToken();
        	targetFirmwareArr.add(str);
        }
		String oldHwVersion = String.valueOf(srcFirmwareArr.get(4));
		String oldFwVersion = String.valueOf(srcFirmwareArr.get(5));
		String oldBuild = String.valueOf(srcFirmwareArr.get(6));
		String newHwVersion = String.valueOf(targetFirmwareArr.get(4));
		String newFwVersion = String.valueOf(targetFirmwareArr.get(5));
		String newBuild = String.valueOf(targetFirmwareArr.get(6));

		try {
			
			try {
				equipList = URLDecoder.decode(equipList,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}
			
		   	int transferType = Integer.parseInt(ptransferType);
			int multicastWriteCount = Integer.parseInt(pmulticastWriteCount);
			int maxRetryCount = Integer.parseInt(pmaxRetryCount);
			int otaThreadCount = Integer.parseInt(potaThreadCount);
			int installType = 1;//retry는 항상 1
			int otaStep = 0x1f;

	        //String model = "";//모뎀인 경우만 벤더 모델을 구해온다
	        String binaryURL = "";
	        String binaryMD5 = "";
	        String diffURL = fwDownURL + "?fileType=diff&fileName="+toFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ "_FROM_"+ fromFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ ".diff"; //★★프로퍼티 파일 변경 필요.
	        String diffMD5 = "";
	        try {
				binaryURL = FirmWareHelper.getBinaryURL(toFileNm, newBuild, fwDownURL);
				binaryMD5 = FirmWareHelper.getBinaryMD5(toFileNm, newBuild);
				diffMD5 = FirmWareHelper.getDiffMD5(toFileNm,fromFileNm);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e,e);
				String t = e.getMessage();

				mav.addObject("rtnStr", "ErrorMessage:"+t);    
				return mav;
			}
		   	
	        int uniqTriggerId=0; 
	        FirmwareHistory fhistory = null;
	   		
	   		String target  = sys_id;
   			if(target == null || "".equals(target)){
   		        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
   				return mav;
   			}
   			
			fhistory= new FirmwareHistory();

			//FirmwareHistory
			fhistory.setEquipId(target);
			fhistory.setEquipKind(top_equip_kind);
			fhistory.setEquipType(top_equip_type);
			fhistory.setEquipModel(model);
			fhistory.setEquipVendor(vendor);
			//fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
			fhistory.setOtaState(FW_STATE.Unknown);   
			fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
			fhistory.setTriggerCnt(Integer.parseInt(trigger_cnt));
			fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
			fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
			fhistory.setTrId(Long.parseLong(tr_id));
			fhistory.setTriggerHistory(trigger_history);
			if(top_equip_kind.equals("Modem")){
				String mcu_id = firmWareManager.getIDbyMcuSysId(sys_id);
				MCU mcu = mcuManager.getMCU(Integer.parseInt(mcu_id));
				fhistory.setMcu(mcu);				
			}
			
			ArrayList deviceArrayList  = new ArrayList();
			
			if(top_equip_kind.equals("Modem")){
				StringTokenizer device_serial_token = new java.util.StringTokenizer(device_serial,",");
		        while(device_serial_token.hasMoreTokens()){
		        	String str =  device_serial_token.nextToken();
		        	if(str.length() != 0){
			        	deviceArrayList.add(str);		        		
		        	}
		        }
		        
		        
			}


	   		ArrayList  sendArrayEquipList = new ArrayList();
			if(top_equip_kind.equals("Modem")){
				sendArrayEquipList = deviceArrayList;
			}else{
				sendArrayEquipList.add(target);
			}
        	/*
        	 * cmd 호출
        	 * */
			try{
			cmdOperationUtil.cmdDistribution(sys_id, tr_id, 
					equipKindCd, model, 
					transferType, otaStep, 
					multicastWriteCount, maxRetryCount, 
					otaThreadCount, installType, 
					FirmWareHelper.getIntVersion(oldHwVersion), FirmWareHelper.getIntVersion(oldFwVersion), 
					FirmWareHelper.getIntBuild(oldBuild),FirmWareHelper.getIntVersion(newHwVersion), 
					FirmWareHelper.getIntVersion(newFwVersion), FirmWareHelper.getIntBuild(newBuild), 
					binaryURL, binaryMD5, diffURL, 
					diffMD5, sendArrayEquipList);
			}catch(Exception e){
				fhistory.setTriggerState(TR_STATE.Terminate); 
				fhistory.setTriggerStep(FW_TRIGGER.End);  
				fhistory.setOtaState(FW_STATE.Fail); 
				fhistory.setErrorCode(e.getMessage());
				log.error(e,e);
				rtnStr = e.getMessage();
			}finally{
				/**
				 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
				 ***/
				FirmwareTrigger ftrigger = firmWareManager.getFirmwareTrigger(tr_id);
				fhistory.setTrId(ftrigger.getId());
				firmWareManager.updateFirmwareHistory(fhistory,sendArrayEquipList);
			}	
			rtnStr = getPropertyMessage("aimir.firmware.msg06");
		} catch (Exception e) {
			log.error(e,e);
	    	rtnStr = e.getMessage();
	    	mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg06"));        
	        return mav;  
		}
		
        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	//재배포
	@RequestMapping(value="/gadget/device/command/cmdPackageReDistribution")
	public ModelAndView cmdPackageReDistribution(
									HttpServletRequest request,
									@RequestParam(value="loginId" ,required=false) String loginId,
									@RequestParam("top_equip_kind") String top_equip_kind,    
						    		@RequestParam("top_equip_type") String top_equip_type,
						    		@RequestParam("supplierId") String supplierId,
						    		@RequestParam("transferType") String ptransferType,
						    		@RequestParam("installType") String pinstallType,
						    		@RequestParam("otaThreadCount") String potaThreadCount,
						    		@RequestParam("maxRetryCount") String pmaxRetryCount,
						    		@RequestParam("multicastWriteCount") String pmulticastWriteCount,
						    		@RequestParam("sendEUI642") String sendEUI642,
						    		@RequestParam("saveHistory") String saveHistory,
						    		@RequestParam("fromFileNm") String fromFileNm,
						    		@RequestParam("toFileNm") String toFileNm,
						    		@RequestParam("equipList") String equipList, 
						    		@RequestParam("vendor") String vendor,
						    		@RequestParam("model") String model,
						    		@RequestParam("device_serial") String device_serial,
						    		@RequestParam("tr_id") String tr_id,
						    		@RequestParam("src_firmware") String src_firmware,
						    		@RequestParam("target_firmware") String target_firmware,
						    		@RequestParam("retrigger_cnt") String trigger_cnt,
						    		@RequestParam("retrigger_history") String trigger_history,
						    		@RequestParam("remcu_id") String sys_id,
						    		@RequestParam("modem_mcu_id") String modem_mcu_id
						    		) {		

		String fwDownURL = "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/gadget/device/firmware/firmwareDown.jsp";
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		
//		int equipKindCd = CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		int equipKindCd =0;
		if(top_equip_type.indexOf("Codi")>-1){
		     equipKindCd = FW_EQUIP.Coordinator.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}else if(top_equip_type.equals("MCU")){
			equipKindCd = FW_EQUIP.MCU.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}else if(top_equip_type.equals("Modem") || ModemType.isModemType(top_equip_type)){
			equipKindCd = FW_EQUIP.Modem.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}
		
        StringTokenizer split_srcFirmware = new java.util.StringTokenizer(src_firmware,"_");
        StringTokenizer split_targetFirmware = new java.util.StringTokenizer(target_firmware,"_");
        ArrayList srcFirmwareArr =  new ArrayList();
        ArrayList targetFirmwareArr =  new ArrayList();
        
        while(split_srcFirmware.hasMoreTokens()){
        	String str =  split_srcFirmware.nextToken();
        	srcFirmwareArr.add(str);
        }
        while(split_targetFirmware.hasMoreTokens()){
        	String str =  split_targetFirmware.nextToken();
        	targetFirmwareArr.add(str);
        }
		String oldHwVersion = String.valueOf(srcFirmwareArr.get(4));
		String oldFwVersion = String.valueOf(srcFirmwareArr.get(5));
		String oldBuild = String.valueOf(srcFirmwareArr.get(6));
		String newHwVersion = String.valueOf(targetFirmwareArr.get(4));
		String newFwVersion = String.valueOf(targetFirmwareArr.get(5));
		String newBuild = String.valueOf(targetFirmwareArr.get(6));
	    String rtnStr = "";
	    ArrayList arrEquipList = new ArrayList();

		try {
		
			String equipKind = top_equip_kind;
			String mcuId = "";
			
			try {
				equipList = URLDecoder.decode(equipList,"UTF-8");
			    arrEquipList.add(equipList);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}

		   	int transferType = Integer.parseInt(ptransferType);
			int multicastWriteCount = Integer.parseInt(pmulticastWriteCount);
			int maxRetryCount = Integer.parseInt(pmaxRetryCount);
			int otaThreadCount = Integer.parseInt(potaThreadCount);
			int installType = 1;//retry는 항상 1로 세팅하며 나머지는 default로 세팅한다.
			int otaStep = 0x1f;
			
	        String binaryURL = "";
	        String binaryMD5 = "";
	        String diffURL = fwDownURL + "?fileType=diff&fileName="+toFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ "_FROM_"+ fromFileNm.replaceAll(".ebl","").replaceAll(".tar.gz","")+ ".diff";//★★프로퍼티 파일 변경 필요.
	        String diffMD5 = "";
	        try {
				binaryURL = FirmWareHelper.getBinaryURL(toFileNm, newBuild, fwDownURL);
				binaryMD5 = FirmWareHelper.getBinaryMD5(toFileNm, newBuild);
				diffMD5 = FirmWareHelper.getDiffMD5(toFileNm,fromFileNm);
			} catch (Exception e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}
		   	
	        //int uniqTriggerId=0; 
	        FirmwareHistory fhistory = new FirmwareHistory();
	        
	        //String triggerId = FirmWareHelper.getNewTriggerId(uniqTriggerId);
	        
			ArrayList deviceArrayList  = new ArrayList();
			
			if(top_equip_kind.equals("Modem")){
				StringTokenizer device_serial_token = new java.util.StringTokenizer(device_serial,",");
		        while(device_serial_token.hasMoreTokens()){
		        	String str =  device_serial_token.nextToken();
		        	if(str.length() != 0){
			        	deviceArrayList.add(str);		        		
		        	}
		        }
		        
/*		        if(arrEquipList.size() > 0){
		        	for(int i =0 ; i < arrEquipList.size(); i++){
		        		deviceArrayList.add(arrEquipList.get(i));
		        	}
		        }*/
			}


	   		ArrayList  sendArrayEquipList = new ArrayList();
			if(top_equip_kind.equals("Modem")){
				sendArrayEquipList = deviceArrayList;
			}else{
				sendArrayEquipList.add(sys_id);
			}
	        
	        
//			의미없는 작업이지만 배열 형식으로 맞추기 위해.
			String[] equipArr = new String[sendArrayEquipList.size()];
			for(int j=0 ; j<sendArrayEquipList.size() ;j++){
				equipArr[j] = (String) sendArrayEquipList.get(j);
			}
			

	   		String target = sys_id;
			if(target == null || "".equals(target)){
		        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
				return mav;
			}
	   		
//			FirmwareHistory
			fhistory.setEquipId(target);
			fhistory.setEquipKind(equipKind);
			fhistory.setEquipType(top_equip_type);
			fhistory.setEquipModel(model);
			fhistory.setEquipVendor(vendor);
			fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
			fhistory.setOtaState(FW_STATE.Unknown);   
			fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
			fhistory.setTriggerCnt(Integer.parseInt(trigger_cnt));
			fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
			fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
			if(top_equip_kind.equals("Modem")){
				String mcu_id = firmWareManager.getIDbyMcuSysId(sys_id);
		       	MCU mcu = mcuManager.getMCU(Integer.parseInt(modem_mcu_id));
		       	fhistory.setMcu(mcu);
			}
			fhistory.setTriggerHistory(trigger_history);
			

            
			/*
			 * cmd 호출
			 * */
			try{
			cmdOperationUtil.cmdPackageDistribution(sys_id, equipKindCd, 
						tr_id, oldHwVersion, 
						oldFwVersion, oldBuild, 
						newHwVersion, newFwVersion, 
						newBuild, binaryMD5, 
						binaryURL, diffMD5, 
						diffURL, equipArr, 
						0, 0, //★★ transferType,equipType 확인필요 
						top_equip_type, 0,//★★ dataType 확인필요 
						0, 0);//★★otaLevel, otaRetry  확인필요
			
				}catch(Exception e){
				fhistory.setTriggerState(TR_STATE.Terminate); 
				fhistory.setTriggerStep(FW_TRIGGER.End);
				fhistory.setOtaState(FW_STATE.Fail);
				fhistory.setErrorCode(e.getMessage());
				rtnStr = e.getMessage();
			}finally{
				/**
				 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
				 ***/
				FirmwareTrigger ftrigger = firmWareManager.getFirmwareTrigger(tr_id);
				fhistory.setTrId(ftrigger.getId());
				firmWareManager.updateFirmwareHistory(fhistory,sendArrayEquipList);
			}	
		        
			rtnStr = getPropertyMessage("aimir.firmware.msg06");
		} catch (Exception e) {
	    	//rtnStr = e.getMessage();
			rtnStr = getPropertyMessage("aimir.firmware.msg06");
		}
        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }		
	
	@RequestMapping(value="/gadget/device/command/cmdReDistributionMMIU")
	public ModelAndView cmdReDistributionMMIU(
									@RequestParam(value="loginId" ,required=false) String loginId,
									@RequestParam("top_equip_kind") String top_equip_kind,    
						    		@RequestParam("top_equip_type") String top_equip_type,
						    		@RequestParam("supplierId") String supplierId,
						    		@RequestParam("transferType") String ptransferType,
						    		@RequestParam("installType") String pinstallType,
						    		@RequestParam("otaThreadCount") String potaThreadCount,
						    		@RequestParam("maxRetryCount") String pmaxRetryCount,
						    		@RequestParam("multicastWriteCount") String pmulticastWriteCount,
						    		@RequestParam("sendEUI642") String sendEUI642,
						    		@RequestParam("saveHistory") String saveHistory,
						    		@RequestParam("fromFileNm") String fromFileNm,
						    		@RequestParam("toFileNm") String toFileNm,
						    		@RequestParam("equipList") String equipList, 
						    		@RequestParam("vendor") String vendor,
						    		@RequestParam("model") String model,
						    		@RequestParam("device_serial") String device_serial,
						    		@RequestParam("tr_id") String tr_id,
						    		@RequestParam("src_firmware") String src_firmware,
						    		@RequestParam("target_firmware") String target_firmware,
						    		@RequestParam("retrigger_cnt") String trigger_cnt,
						    		@RequestParam("retrigger_history") String trigger_history,
						    		@RequestParam("remcu_id") String sys_id,
						    		@RequestParam("modem_mcu_id") String modem_mcu_id
						    		) {
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		
//		int equipKindCd = CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		int equipKindCd =0;
		if(top_equip_type.indexOf("Codi")>-1){
		     equipKindCd = FW_EQUIP.Coordinator.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}else if(top_equip_type.equals("MCU")){
			equipKindCd = FW_EQUIP.MCU.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}else if(top_equip_type.equals("Modem") || ModemType.isModemType(top_equip_type)){
			equipKindCd = FW_EQUIP.Modem.getKind();//CommonConstants.DeviceType.valueOf(top_equip_kind).getCode();
		}
		
        StringTokenizer split_srcFirmware = new java.util.StringTokenizer(src_firmware,"_");
        StringTokenizer split_targetFirmware = new java.util.StringTokenizer(target_firmware,"_");
        ArrayList srcFirmwareArr =  new ArrayList();
        ArrayList targetFirmwareArr =  new ArrayList();
        
        while(split_srcFirmware.hasMoreTokens()){
        	String str =  split_srcFirmware.nextToken();
        	srcFirmwareArr.add(str);
        }
        while(split_targetFirmware.hasMoreTokens()){
        	String str =  split_targetFirmware.nextToken();
        	targetFirmwareArr.add(str);
        }
		String oldHwVersion = String.valueOf(srcFirmwareArr.get(4));
		String oldFwVersion = String.valueOf(srcFirmwareArr.get(5));
		String oldBuild = String.valueOf(srcFirmwareArr.get(6));
		String newHwVersion = String.valueOf(targetFirmwareArr.get(4));
		String newFwVersion = String.valueOf(targetFirmwareArr.get(5));
		String newBuild = String.valueOf(targetFirmwareArr.get(6));
	    String rtnStr = "";
	    ArrayList arrEquipList = new ArrayList();

		try {
		
			String equipKind = top_equip_kind;
			String mcuId = "";
			
			try {
				equipList = URLDecoder.decode(equipList,"UTF-8");
			    arrEquipList.add(equipList);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
		        mav.addObject("rtnStr", String.valueOf(e));    
				return mav;
			}

		   	int transferType = Integer.parseInt(ptransferType);
			int multicastWriteCount = Integer.parseInt(pmulticastWriteCount);
			int maxRetryCount = Integer.parseInt(pmaxRetryCount);
			int otaThreadCount = Integer.parseInt(potaThreadCount);
			int installType = 1;//retry는 항상 1로 세팅하며 나머지는 default로 세팅한다.
			int otaStep = 0x1f;
			
	        //int uniqTriggerId=0; 
	        FirmwareHistory fhistory = new FirmwareHistory();
	        
	        //String triggerId = FirmWareHelper.getNewTriggerId(uniqTriggerId);
	        
			ArrayList deviceArrayList  = new ArrayList();
			



	   		ArrayList  sendArrayEquipList = new ArrayList();
			sendArrayEquipList.add(sys_id);

	        
	        
//			의미없는 작업이지만 배열 형식으로 맞추기 위해.
			String[] equipArr = new String[sendArrayEquipList.size()];
			for(int j=0 ; j<sendArrayEquipList.size() ;j++){
				equipArr[j] = (String) sendArrayEquipList.get(j);
			}
			

	   		String target = sys_id;
			if(target == null || "".equals(target)){
		        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
				return mav;
			}
	   		
//			FirmwareHistory
			fhistory.setEquipId(target);
			fhistory.setEquipKind(equipKind);
			fhistory.setEquipType(top_equip_type);
			fhistory.setEquipModel(model);
			fhistory.setEquipVendor(vendor);
			fhistory.setIssueDate(FirmWareHelper.getCurrentYYYYMMDDHHMMSS());
			fhistory.setOtaState(FW_STATE.Unknown);   
			fhistory.setOtaStep(FW_OTA.DataSend);//★★ FW_OTA 값 확인 필요. 
			fhistory.setTriggerCnt(Integer.parseInt(trigger_cnt));
			fhistory.setTriggerState(TR_STATE.Running);//★★ FW_OTA 값 확인 필요. 
			fhistory.setTriggerStep(FW_TRIGGER.Start);//★★ FW_TRIGGER 값 확인 필요.
			  
			//String mcu_id = firmWareManager.getIDbyMcuSysId(sys_id);
			//MCU mcu =mcuManager.getMCU(Integer.parseInt(mcu_id));
//		       	MCU mcu = mcuManager.getMCU(Integer.parseInt(modem_mcu_id));
		    //fhistory.setMcu(mcu);
			fhistory.setTriggerHistory(trigger_history);
			

            
			/*
			 * cmd 호출
			 * */
			try{

	   			/*Set<Modem> modems = mcu.getModem();
	   			Iterator<Modem> iterator = modems.iterator();
	   			while (iterator.hasNext()) {
	   				int modem_id = iterator.next().getId();
	   				System.out.println(modem_id);
	   				List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modem_id);
	   				for (int k=0; k<meters.size(); k++) {
	                    Meter meter = meters.get(k);
						cmdOperationUtil.cmdDistributionMMIU(meter, toFileNm);			                 
	                }
	   			}*/
				List<Object> distributeModemIdList = modemManager.getModemIdListByDevice_serial(target);
//				List<Object> distributeModemIdList = modemManager.getModemListByMCUsysID(target);
				for(int l =0 ; l< distributeModemIdList.size() ; l++){
					HashMap<String, Object> param2 = new HashMap<String, Object>();
					int modemID = Integer.parseInt(String.valueOf(distributeModemIdList.get(l)));
	                List<Meter> meters = (List<Meter>) meterDao.getMeterHavingModem(modemID);
					
	                for (int k=0; k<meters.size(); k++) {
	                    Meter meter = meters.get(k);
						cmdOperationUtil.cmdDistributionMMIU(meter, toFileNm);			                 
	                }
				}
			
			}catch(Exception e){
				fhistory.setTriggerState(TR_STATE.Terminate); 
				fhistory.setTriggerStep(FW_TRIGGER.End);
				fhistory.setOtaState(FW_STATE.Fail);
				fhistory.setErrorCode(e.getMessage());
				rtnStr = e.getMessage();
			}finally{
				/**
				 * cmd 호출 성공 후 1.Trigger 인서트 ,  2.History 인서트 또는 업데이트 
				 ***/
				FirmwareTrigger ftrigger = firmWareManager.getFirmwareTrigger(tr_id);
				fhistory.setTrId(ftrigger.getId());
				firmWareManager.updateFirmwareHistory(fhistory,sendArrayEquipList);
			}	
		        
			rtnStr = getPropertyMessage("aimir.firmware.msg06");
		} catch (Exception e) {
	    	//rtnStr = e.getMessage();
			rtnStr = getPropertyMessage("aimir.firmware.msg06");
		}
        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }		
	
	
	@RequestMapping(value="/gadget/device/command/cmdInstallFile")
	public ModelAndView cmdInstallFile(@RequestParam(value="target" ,required=false) 	String target
									, @RequestParam(value="fileName" ,required=false) String fileName
									, @RequestParam(value="type" ,required=false) int type
									, @RequestParam(value="reservationTime" ,required=false) String reservationTime
									, @RequestParam(value="loginId" ,required=false) String loginId) {

       	ResultStatus status = ResultStatus.FAIL;
       	
        ModelAndView mav = new ModelAndView("jsonView");
		if(!commandAuthCheck(loginId, CommandType.DeviceWrite,  "8.3.10")){
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg02"));        
	        return mav;
		}
		if(target == null || "".equals(target)){
			status = ResultStatus.INVALID_PARAMETER;
	        mav.addObject("rtnStr", getPropertyMessage("aimir.firmware.msg03"));    
			return mav;
		}

	    String rtnStr = "";
       	MCU mcu = mcuManager.getMCU(Integer.parseInt(target));
		Supplier supplier = mcu.getSupplier();
		if(supplier == null){
			Operator operator = operatorManager.getOperatorByLoginId(loginId);
			supplier = operator.getSupplier();
		}
		try {

			cmdOperationUtil.cmdInstallFile(mcu.getSysID(), fileName, type, reservationTime);

			status = ResultStatus.SUCCESS;
		} catch (Exception e) {
	    	rtnStr = e.getMessage();
		}
	    
	    Code operationCode = codeManager.getCodeByCode("8.3.10");//TODI FIND COMMAND CODE
	    if(operationCode != null){
		    operationLogManager.saveOperationLog(supplier, mcu.getMcuType(), mcu.getSysID(), loginId, operationCode, status.getCode(), "SUCCESS".equals(status.name())?status.name():rtnStr);   
	    }

        mav.addObject("rtnStr", rtnStr);        
        return mav;        
    }
	
	/**
	 * 2011-05-18 kskim.<br>
	 * property message 의 locale 에 해당하는 key message를 반환한다.
	 * @param key 가져올 property message key.
	 * @return property message(key에 대응하는 message가 없을경우 key명칭을 반환)
	 */
	public String getPropertyMessage(String key){
		if (key==null)return null;
		Locale locale = LocaleContextHolder.getLocale();
		ResourceBundle bundle = ResourceBundle.getBundle("message",	locale);
		String message = null;
		try{
			message = bundle.getString(key);
		}catch(Exception e){
			message = "??"+key+"??";
		}
		return message == null ? key:message;
	}
	
	/**
	 * 겹치지 않는 파일 Path 를 만들어 리턴한다. 포함된 디렉토리는 자동 생성된다.
	 * @param homePath 홈 디렉토리
	 * @param fileName 파일명(확장자 제외)
	 * @param ext 파일 확장자('.' 제외)
	 * @param deletable 중복된 파일 삭제
	 * @return
	 */
	private String makeFirmwareDirectory(String homePath, String fileName,
			String ext, boolean deletable) {
		File file = null;
		StringBuilder firmwareDir = new StringBuilder();
		firmwareDir.append(homePath);
		firmwareDir.append("/");
		firmwareDir.append(fileName);
		
		file = new File(firmwareDir.toString());
		if(!file.exists()){
			file.mkdirs();
		}
		firmwareDir.append("/");
		firmwareDir.append(fileName);
		firmwareDir.append(".");
		firmwareDir.append(ext);
		
		file = new File(firmwareDir.toString());

		boolean result = false;
		if(deletable && file.exists()){
			result = file.delete();
		}else{
			result = true;
		}
		
		if(!result){
			//새로운 이름 규칙은 기존 이름+(n) 방식이다.
			if(fileName.matches(".*\\([0-9]*\\)")){
				//기존 파일 이름이 중복 규칙에 의해 만들어진 파일 명이라면 숫자를 증가시켜 이름을 다시 만든다.
				int number = Integer.valueOf(fileName.replaceAll(".*\\(([0-9]*)\\)", "$1"));
				fileName = fileName.replaceAll("(.*)\\([0-9]*\\)", String.format("$1(%d)", number++));
			}else{
				// 파일 이름에 중복 이름 규칙을 적용한다.
				fileName = String.format("%s(0)", fileName);
			}
			
			//중복되는지 제귀하여 확인한다.
			return makeFirmwareDirectory(homePath,fileName,ext,deletable);
		}
		return file.getPath();
	}
}
