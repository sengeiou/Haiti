package com.aimir.mars.integration.metercontrol.server;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.integration.WSMeterConfigOBISDao;
import com.aimir.dao.integration.WSMeterConfigUserDao;
import com.aimir.mars.integration.metercontrol.util.CmdUtil;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ErrorCode;
import com.aimir.mars.integration.metercontrol.util.MeterControlConstants.ObisInfo;
import com.aimir.mars.util.UserPasswordUtil;
import com.aimir.model.integration.WSMeterConfigOBIS;
import com.aimir.model.integration.WSMeterConfigUser;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



@WebService(serviceName = "UserManagement")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "userManagement")
@HandlerChain(file="/config/handlers.xml")

public class UserManagement {
	protected static Log log = LogFactory.getLog(UserManagement.class);

	@Resource(name="transactionManager")
	HibernateTransactionManager txManager;

	@Autowired
	private WSMeterConfigUserDao wsMeterConfigUserDao;
	
	@Autowired
	private WSMeterConfigOBISDao wsMeterConfigOBISDao;
	
	@Resource
	private WebServiceContext wsContext;

	@Autowired
	private CmdUtil cmdUtil;

	Integer _syncTimeOut = 120;

	private static DefaultTransactionDefinition PropRequired = null;
	static {
		PropRequired =  new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRED);
	}
	

	@WebMethod(operationName = "AddMeterConfigUser")
	public @WebResult(name = "AddMeterConfigUserResult") McResponse addMeterConfigUser(
			@WebParam(name = "user") java.lang.String user,
			@WebParam(name = "password") java.lang.String password,
			@WebParam(name = "permission") java.lang.String permission		
			)
					throws Exception {
		String commandName = "AddMeterConfigUser";
		
		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;
		
    	//Check permission
		res = checkPermission(commandName);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
    	
    	//validate new user
		res = checkUserParameters(commandName, user, password, "");
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
		
    	// Add new user.
		String savePassword = UserPasswordUtil.encrypt(password);			
		String writeDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		String updateDate = writeDate;
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(PropRequired);
			WSMeterConfigUser mcUser = new WSMeterConfigUser();
			
			mcUser.setUserId(user);
			mcUser.setPassword(savePassword);
			mcUser.setWriteDate(writeDate);
			mcUser.setUpdateDate(updateDate);

			wsMeterConfigUserDao.add(mcUser);
			txManager.commit(txStatus);
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());			
			createWsMeterconfigLog(commandName, err);		
			return res;				
		}		
		try {
			txStatus = txManager.getTransaction(PropRequired);
			WSMeterConfigUser mcUser = wsMeterConfigUserDao.get(user);
			WSMeterConfigOBIS newmcOBIS = new WSMeterConfigOBIS();
			
			newmcOBIS.setMeterConfUser(mcUser);
			newmcOBIS.setOBISCode("USER");
			if (permission == null || permission.equals("")) {
				newmcOBIS.setPermission("");
			} else {
				newmcOBIS.setPermission(permission);
			}
			
			wsMeterConfigOBISDao.add(newmcOBIS);
			txManager.commit(txStatus);
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
		}				
		
		res.setErrorCode(err.getCode());
		res.setErrorString(err.getMessage());			
		createWsMeterconfigLog(commandName, err);		
		return res;				
	}    

	@WebMethod(operationName = "UpdateMeterConfigUser")
	public @WebResult(name = "UpdateMeterConfigUserResult") McResponse updateMeterConfigUser(
			@WebParam(name = "user") java.lang.String user,
			@WebParam(name = "password") java.lang.String password,
			@WebParam(name = "permission") java.lang.String permission
			)
					throws Exception {

		String commandName = "UpdateMeterConfigUser";
		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;
		
    	//Check permission
		res = checkPermission(commandName);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
    	
    	//validate new user
		res = checkUserParameters(commandName, user, password, permission);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
		
    	// Update user.
		String newPassword ="";
		if (password != null && !password.equals("")) {
			newPassword = UserPasswordUtil.encrypt(password);			
		}
		String updateDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		TransactionStatus txStatus = null;

		try {
			
			if (!newPassword.equals("")) {
				txStatus = txManager.getTransaction(PropRequired);
				WSMeterConfigUser mcUser = wsMeterConfigUserDao.get(user);
				mcUser.setPassword(newPassword);
				mcUser.setUpdateDate(updateDate);
				wsMeterConfigUserDao.update(mcUser);
				txManager.commit(txStatus);
			}
			
			if (permission != null) {
				txStatus = txManager.getTransaction(PropRequired);
				WSMeterConfigOBIS mcOBIS = wsMeterConfigOBISDao.get(user, "USER", "", "");
				if (!permission.equals(mcOBIS.getPermission())) {
					mcOBIS.setPermission(permission);
					wsMeterConfigOBISDao.update(mcOBIS);
				}
				txManager.commit(txStatus);
			}
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
		}				
				
		res.setErrorCode(err.getCode());
		res.setErrorString(err.getMessage());			
		createWsMeterconfigLog(commandName, err);		
		return res;				
	}    	
	
	@WebMethod(operationName = "DeleteMeterConfigUser")
	public @WebResult(name = "DeleteMeterConfigUserResult") McResponse deleteMeterConfigUser(
			@WebParam(name = "user") java.lang.String user
			)
					throws Exception {

		String commandName = "DeleteMeterConfigUser";
		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;
		
    	//Check permission
		res = checkPermission(commandName);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
    	
    	//validate delete user
		res = checkUserParameters(commandName, user, "", "");
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
		
		// Delete user.
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(PropRequired);
			List<WSMeterConfigOBIS> list = new ArrayList<WSMeterConfigOBIS>();
			list = 	wsMeterConfigOBISDao.getMeterConfigOBISList(user);
	        if ((list != null) && (list.size() > 0)) {			        
			    for (WSMeterConfigOBIS data : list) {			
			    	wsMeterConfigOBISDao.delete(data);
				}
	        }
	        wsMeterConfigUserDao.delete(wsMeterConfigUserDao.get(user));		    
			txManager.commit(txStatus);
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
		}
				
		res.setErrorCode(err.getCode());
		res.setErrorString(err.getMessage());			
		createWsMeterconfigLog(commandName, err);		
		return res;				
	}    

	@WebMethod(operationName = "GetMeterConfigUserList")
	public @WebResult(name = "GetMeterConfigUserListResult") McResponse getMeterConfigUserList(
			)
					throws Exception {
		String commandName = "GetMeterConfigUserList";
		
		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;
		
    	//Check permission
		res = checkPermission(commandName);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
		
    	// Get All user.
		TransactionStatus txStatus = null;
		List<WSMeterConfigUser> userList = new ArrayList<WSMeterConfigUser>(); 
		JSONArray resultArray = new JSONArray();
		try {
			txStatus = txManager.getTransaction(PropRequired);

			userList = wsMeterConfigUserDao.getAll();
			
	        if ((userList != null) && (userList.size() > 0)) {			        
			    for (WSMeterConfigUser user : userList) {			
			    	WSMeterConfigOBIS obis = wsMeterConfigOBISDao.get(user.getUserId(), "USER", "", "");
			    	JSONObject obj = new JSONObject();
			    	obj.put("meterConfUser", user.getUserId());
			    	obj.put("permission", (obis.getPermission() == null ? "" : obis.getPermission()));
			    	resultArray.add(obj);
			    	}
	        }
			
			if ( resultArray != null ) {
				res.setResultValue(resultArray.toString());
			}			
			txManager.commit(txStatus);
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());			
			createWsMeterconfigLog(commandName, err);		
			return res;				
		}		
		
		res.setErrorCode(err.getCode());
		res.setErrorString(err.getMessage());			
		createWsMeterconfigLog(commandName, err);		
		return res;				
	}    

	@WebMethod(operationName = "GetAccessControl")
	public @WebResult(name = "GetAccessControlResult") McResponse getAccessControl(
			@WebParam(name = "user") java.lang.String user
			)
					throws Exception {

		String commandName = "GetAccessControl";
		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;
		JSONArray resultArray = new JSONArray();

    	//Check permission
		res = checkPermission(commandName);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
    	
    	//validate delete user
		res = checkUserParameters(commandName, user, "", "");
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
		
		// Get Permissions
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);
			List<WSMeterConfigOBIS> list = new ArrayList<WSMeterConfigOBIS>();
			list = 	wsMeterConfigOBISDao.getMeterConfigOBISList(user);
	        if ((list != null) && (list.size() > 0)) {			        
			    for (WSMeterConfigOBIS data : list) {
			    	if (data.getPermission() == null || data.getPermission().equals("")) continue;
			    	if (data.getPermission().contains("R")) {
				    	JSONObject obj = new JSONObject();
				    	ObisInfo obisinfo = ObisInfo.getByObisCode(data.getOBISCode(), data.getClassId(), data.getAttributeNo());
				    	obj.put("meterConfUser", data.getMeterConfUser().getUserId());
				    	obj.put("command", "Get" + obisinfo.getCommand());
				    	resultArray.add(obj);
			    	}
			    	if (data.getPermission().contains("W")) {
				    	JSONObject obj = new JSONObject();
				    	ObisInfo obisinfo = ObisInfo.getByObisCode(data.getOBISCode(), data.getClassId(), data.getAttributeNo());
				    	obj.put("meterConfUser", data.getMeterConfUser().getUserId());
				    	obj.put("command", "Set" + obisinfo.getCommand());
				    	resultArray.add(obj);
			    	}
				}
				if ( resultArray != null ) {
					res.setResultValue(resultArray.toString());
				}				
	        }
			txManager.commit(txStatus);
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
		}
				
		res.setErrorCode(err.getCode());
		res.setErrorString(err.getMessage());			
		createWsMeterconfigLog(commandName, err);		
		return res;				
	}    		

	@WebMethod(operationName = "SetAccessControl")
	public @WebResult(name = "SetAccessControlResult") McResponse setAccessControl(
			@WebParam(name = "user") java.lang.String user,
			@WebParam(name = "command") java.lang.String command,
			@WebParam(name = "enable") java.lang.Boolean enable
			)
					throws Exception {

		String commandName = "SetAccessControl";
		McResponse res = new McResponse();
		ErrorCode err = ErrorCode.Success;
		
    	//Check permission
		res = checkPermission(commandName);
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
    	
    	//validate delete user
		res = checkUserParameters(commandName, user, command, "");
		if ( res.getErrorCode() != ErrorCode.Success.getCode() ) {
			return res;
		}
		
		// Set Permission
		ObisInfo obisInfo = ObisInfo.getByCommand( command );
		String   permission = "";
		if (command.startsWith("Get")) {
			permission = "R";
		} else if (command.startsWith("Set")) {
			permission = "W";
		}
		
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(PropRequired);
			WSMeterConfigUser mcUser = wsMeterConfigUserDao.get(user);
			WSMeterConfigOBIS mcOBIS = wsMeterConfigOBISDao.get(user, 
					obisInfo.getObisCode(), obisInfo.getClassId(), obisInfo.getAttributeNo());
			
//			Integer ndisable = 0;
//			if (enable != null && enable.equals("1")) {
//				ndisable = 1;
//			}
			if (enable == null) {
				enable = false;
			}
			// New
			if (mcOBIS == null) {
				WSMeterConfigOBIS newOBIS = new WSMeterConfigOBIS();
//				newOBIS.setMeterConfUserId(mcUser.getId());
				newOBIS.setMeterConfUser(mcUser);
				newOBIS.setOBISCode(obisInfo.getObisCode());
				newOBIS.setClassId(obisInfo.getClassId());
				newOBIS.setAttributeNo(obisInfo.getAttributeNo());
				if (enable == true) {
					newOBIS.setPermission(permission);
				} else {
					newOBIS.setPermission("");
				}
				wsMeterConfigOBISDao.add(newOBIS);
			}			
			// Update
			else {
				if (enable == true) {
					// add
					String oldPermission = (mcOBIS.getPermission() == null ? "" : mcOBIS.getPermission());
					String newPermission = oldPermission;
					if (!oldPermission.contains(permission)) {
						newPermission = oldPermission + permission;
					}
					mcOBIS.setPermission(newPermission);
				} else {
					// del
					String oldPermission = (mcOBIS.getPermission() == null ? "" : mcOBIS.getPermission());
					String newPermission = oldPermission;
					if (oldPermission.contains(permission)) {
						newPermission = oldPermission.replace(permission, "");
					}
					mcOBIS.setPermission(newPermission);
				}
				wsMeterConfigOBISDao.update(mcOBIS);
			}

			txManager.commit(txStatus);				
			
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
		}				
				
		res.setErrorCode(err.getCode());
		res.setErrorString(err.getMessage());
		createWsMeterconfigLog(commandName, err);		
		return res;
	}    		
		
	private McResponse checkPermission(String cmd ){
		
		ErrorCode err = ErrorCode.Success;
		McResponse res = new McResponse();
		String username = (String) wsContext.getMessageContext().get("USERNAME");  		
		TransactionStatus txStatus = null;
		try {
			txStatus = txManager.getTransaction(null);		
	    	WSMeterConfigOBIS mcOBIS = wsMeterConfigOBISDao.get(username, "USER", "", "");
	    	if((mcOBIS==null) || (mcOBIS.getPermission()==null) || (mcOBIS.getPermission().equals(""))){
				err = ErrorCode.PermissionError;
	    	}
	    	else {
				switch ( cmd ) {
					case "AddMeterConfigUser":
				    	if(!mcOBIS.getPermission().contains("A")) {
							err = ErrorCode.PermissionError;
				    	}
						break;
					case "UpdateMeterConfigUser":
				    	if(!mcOBIS.getPermission().contains("U")) {
							err = ErrorCode.PermissionError;
				    	}
						break;
					case "DeleteMeterConfigUser":
				    	if(!mcOBIS.getPermission().contains("D")) {
							err = ErrorCode.PermissionError;
				    	}
						break;
					case "GetMeterConfigUserList":
				    	if(!mcOBIS.getPermission().contains("L")) {
							err = ErrorCode.PermissionError;
				    	}
						break;
					case "GetAccessControl":
				    	if(!mcOBIS.getPermission().contains("G")) {
							err = ErrorCode.PermissionError;
				    	}
						break;
					case "SetAccessControl":
				    	if(!mcOBIS.getPermission().contains("S")) {
							err = ErrorCode.PermissionError;
				    	}
						break;
				}
	    	}

	    	if (txStatus != null) {
	    		txManager.commit(txStatus);
	    	}
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			err = ErrorCode.SystemError;	
		}
		
		if ( err != ErrorCode.Success) {
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());			
			createWsMeterconfigLog(cmd, err);
		}
		return res;
	}
	
	
	private McResponse checkUserParameters(String cmd, String param0, String param1, String param2 ){
		
		ErrorCode err = ErrorCode.Success;
		McResponse res = new McResponse();

		TransactionStatus txStatus = null;
		WSMeterConfigUser mcUser = null;
		try {	    	
			switch ( cmd ) {
			case "AddMeterConfigUser":
				//param0:user, param1:password
		    	if (param0 == null || param0.equals("") ||
		    			param1 == null || param1.equals("") ||
		    			param1.length() > 64
	    			) {
		    		err = ErrorCode.InvalidParameter;
		    	}
		    	else {
					txStatus = txManager.getTransaction(null);    	
			    	mcUser = wsMeterConfigUserDao.get(param0);		    	
			    	if (mcUser != null) {
						err = ErrorCode.UserAlreadyExist;
			    	}
		    	}
				break;
			case "UpdateMeterConfigUser":
				//param0:user, param1:password, param2:permission
		    	if ((param0 == null || param0.equals("")) ||
		        	(param1 == null && param2 == null) ||
		        	(param1 != null && param1.equals("")) ||
		        	(param1 != null && param1.length() > 64)
		        	) {
		    		err = ErrorCode.InvalidParameter;
		    	}
		    	else {
					txStatus = txManager.getTransaction(null);    	
			    	mcUser = wsMeterConfigUserDao.get(param0);		    	
			    	if (mcUser == null) {
						err = ErrorCode.UserNotExist;
			    	}
		    	}
				break;
			case "DeleteMeterConfigUser":
				//param0:user
		    	if (param0 == null || param0.equals("")) {
					err = ErrorCode.InvalidParameter;
		    	}
		    	else {
					txStatus = txManager.getTransaction(null);    	
			    	mcUser = wsMeterConfigUserDao.get(param0);		    	
			    	if (mcUser == null) {
						err = ErrorCode.UserNotExist;
			    	}
		    	}
				break;
			case "GetAccessControl":
				//param0:user
		    	if (param0 == null || param0.equals("")) {
					err = ErrorCode.InvalidParameter;
		    	}
		    	else {
					txStatus = txManager.getTransaction(null);    	
			    	mcUser = wsMeterConfigUserDao.get(param0);		    	
			    	if (mcUser == null) {
						err = ErrorCode.UserNotExist;
			    	}
		    	}
				break;
			case "SetAccessControl":
				//param0:user, param1:target command
		    	if (param0 == null || param0.equals("") ||
    			param1 == null || param1.equals("")	
		    	) {
					err = ErrorCode.InvalidParameter;
		    	}
		    	else {
		    		// Set AccessControl
		    		ObisInfo obisInfo = ObisInfo.getByCommand( param1 );
		    		String   permission = "";
		    		if (param1.startsWith("Get")) {
		    			permission = "R";
		    		} else if (param1.startsWith("Set")) {
		    			permission = "W";
		    		}
		    		if (obisInfo == null || permission == null) {
		    			err = ErrorCode.InvalidParameter;
		    		}
		    		else {			    		
						txStatus = txManager.getTransaction(null);    	
				    	mcUser = wsMeterConfigUserDao.get(param0);		    	
				    	if (mcUser == null) {
							err = ErrorCode.UserNotExist;
				    	}
			    	}
		    	}				
				break;
			}
			
			if (txStatus != null) {
				txManager.commit(txStatus);
			}
		}
		catch (Exception e) {
			if (txStatus != null) {
				try {
					txManager.rollback(txStatus);
				}
				catch (Exception ee) {}
			}
			log.error(e, e);
			err = ErrorCode.SystemError;
		}
				
		if ( err != ErrorCode.Success) {
			res.setErrorCode(err.getCode());
			res.setErrorString(err.getMessage());
			createWsMeterconfigLog(cmd, err);
		}
		return res;
	}
	
	private void createWsMeterconfigLog(String cmd, ErrorCode err)
	{
		String username = (String) wsContext.getMessageContext().get("USERNAME");  		
		String currentTime = null;
		
		try {
			currentTime = TimeUtil.getCurrentTime();
			cmdUtil.createWsMeterconfigLog("","",null,null, "",
					"","","",
					username, TR_STATE.Terminate.getCode(),err.getCode(),
					cmd,"",
					currentTime, currentTime, currentTime);
			}
		catch (Exception e) {
			
		}
	}
	
}
