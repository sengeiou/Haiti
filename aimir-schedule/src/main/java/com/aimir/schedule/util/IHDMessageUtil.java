package com.aimir.schedule.util;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.aimir.dao.device.IHDDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.GroupDao;
import com.aimir.dao.system.GroupMemberDao;
import com.aimir.dao.system.HomeGroupDao;
import com.aimir.fep.trap.data.IHD_RequestDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.IHD;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.GroupMember;
import com.aimir.model.system.HomeGroup;
import com.aimir.schedule.command.CmdOperationUtil;

@Component
public class IHDMessageUtil {
	private static Log log = LogFactory.getLog(IHDMessageUtil.class);
	
	@Autowired
	MeterDao meterDao;
	@Autowired
	GroupMemberDao groupMemberDao;
	@Autowired
	GroupDao groupDao;
	@Autowired
	HomeGroupDao homeGroupDao;
	@Autowired
	ModemDao modemDao;
	@Autowired
	CodeDao codeDao;
	@Autowired
	IHDDao ihdDao;
	@Autowired
	CmdOperationUtil cmdOperationUtil;
	
	/**
	 * getEventMessage
	 * 
	 * @return String type Data
	 */
	public void getEventMessage(String meterId, String title, String data){
		log.debug("meterId : [" + meterId + "], data : [" + data + "]");
//		createDateTime	0x01
//		event_message	0x03
		
		// Meter Id로 Group Id 조회.
		Meter tmpMeter = meterDao.get(meterId);
		HashMap<String, String> hm = getIHDbyMeterId(tmpMeter.getMdsId());
		String ihdId = hm.get("ihd");
		String mcuId = hm.get("mcu");
			
		String rtnStr 	= "";
		Date date = new Date();
		Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");		
		rtnStr += getTypeFrame("01", formatter.format(date));
		rtnStr += getTypeFrame("02", title);
		rtnStr += getTypeFrame("03", data);
		log.debug("33X03 : " + data);
		IHD_RequestDataFrame rf = new IHD_RequestDataFrame();
		try {
			sendDCU(rf.getBytes("53","49","33", rtnStr), mcuId, ihdId);
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
	

	/**
	 * getTypeFrame
	 * desc : EventMessage에서 보내는 데이터는 코드가 존재하지 않는다.
	 * 
	 * @param type
	 * @param data
	 * @return DATA필드의 Type 프레임 리턴(Type(1), TypeLength(1), Data(가변))
	 */
	public String getTypeFrame(String type, String data){
		
		if(data.length()<1){
			return "";
		}
		String returnStr 	= "";
		byte[] dataBytes 	= data.getBytes();
		String dataSize		= String.format("%02X", DataUtil.getByteToInt(dataBytes.length));

		returnStr += type;
		returnStr += dataSize;
		returnStr += Hex.decode(dataBytes);
		
		
		return returnStr.replaceAll(" ","");
	}

	public HashMap<String, String> getIHDbyMeterId(String meterId){
		String mcuId = "";
		String ihdId = "";
		// Meter Id로 Group Id 조회.
		int groupId = groupMemberDao.getGroupIdbyMember(meterId);
		if(groupId != -1){
			//Group 조회
			HomeGroup hg = homeGroupDao.get(groupId);
			MCU mcu = hg.getHomeGroupMcu();
			if(mcu != null && mcu.getSysID() != null) mcuId = mcu.getSysID();
			//IHD 조회
			
			Set<GroupMember> gmList = groupMemberDao.getGroupMemberById(groupId);
			Iterator<GroupMember> lt = gmList.iterator();
			GroupMember g = null;
			Modem tmpModem = null;
			
			while(lt.hasNext()) {
				g = lt.next();
				tmpModem = modemDao.get(g.getMember().trim());
			
				if(tmpModem != null && tmpModem.getDeviceSerial() != null) {
					ihdId = tmpModem.getDeviceSerial();
					continue;
				}
			}
		}
		
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put("ihd", ihdId);
		hm.put("mcu", mcuId);
		return hm;
	}
	
	public void sendDCU(byte[] data, String mcuId, String sensorId){
		try {			
//			MCU mcu = mcuDao.get(mcuId);
//			log.debug("sendDCU, mcuId : " + mcuId + ", mcuSysId : " + mcu.getSysID());
			log.debug("HexDump : \n" + Hex.getHexDump(data));
			cmdOperationUtil.cmdSendIHDData(mcuId, sensorId, data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.debug(e.getMessage());
		}
	}
	
}
