package com.aimir.fep;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.dao.device.FirmwareDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.fep.bypass.decofactory.consts.HdlcConstants;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.decoframe.SORIA_DLMSFrame;
import com.aimir.fep.bypass.decofactory.decorator.NestedDLMSDecoratorForSORIA;
import com.aimir.fep.bypass.decofactory.decorator.NestedHDLCDecoratorForSORIA;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.nip.client.actions.NICommandAction;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.CRC16;
import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FrameUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.Firmware;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssueHistory;
import com.aimir.model.system.Location;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.DateTimeUtil;

public class Test {
	private static Logger logger = LoggerFactory.getLogger(Test.class);
    
	@org.junit.Test
	public void dateTest() {
		logger.debug("=> " + DateTimeUtil.getDateString(1556590355685L)); 
		System.out.println(DateTimeUtil.getDateString(1556590355685L));
	}
	
	public void dateTimeTest() throws ParseException {
		//String dateTime = "20190128010203";
		String dateTime = DateTimeUtil.getCurrentDateTimeByFormat(null);
		
		String add3Day = DateTimeUtil.getPreDay(dateTime, -3);
		
		String yyyy = add3Day.substring(0, 4);
		String month = add3Day.substring(4, 6);
		String dd = add3Day.substring(6, 8);
		String hh = add3Day.substring(8, 10);
		String minute = add3Day.substring(10,12);
		String ss = add3Day.substring(12, 14);
		
		logger.debug("yyyy={}, month={}, dd={}, hh={}, minute={}, ss={}"
				, yyyy, month, dd, hh, minute, ss);
		
		byte[] dateTimeBytes = DataUtil.getTimeStamp7Bytes(add3Day);
        byte[] crc = CRCUtil.Calculate_ZigBee_Crc(dateTimeBytes,(char)0x0000);
        DataUtil.convertEndian(crc);
		
		logger.debug("yyyymmddhh={}. add3Day={}, Hex={}, size={}, crc={}"
				, dateTime, add3Day, Hex.decode(dateTimeBytes), dateTimeBytes.length, Hex.decode(crc));
	}
	
	
	
	public void filterTest() {
		List<String> commandFilter = new ArrayList<String>();
		commandFilter.add("banana");
		commandFilter.add("apple");
		
		AsyncCommandLog asyncCmdLog = new AsyncCommandLog();
		asyncCmdLog.setCommand("banana1");
		
        /** Command Filter validation */
        if(commandFilter != null && !commandFilter.contains(asyncCmdLog.getCommand())) {
        	System.out.println("[Command Filtering] AsyncCommand = "+ asyncCmdLog.getCommand() +", Command Filter => " + commandFilter.toString());
        	return;
        }
        System.out.println("정상실행");
	}
	
//	public void HDLCDecodeTest() {
//		String str = "7EA87A0302FFF642F2E6E700C401400001040204090C07E2020E03140000008000000600000019060000000011000204090C07E2020E03150000008000000600";
//		
//		INestedFrame frame = new NestedHDLCDecoratorForSORIA(new NestedDLMSDecoratorForSORIA(new SORIA_DLMSFrame()));
//		//frame.decode(null, DataUtil.readByteString(str), null);
//		frame.decode(DataUtil.readByteString(str), null, null);
//		
//	}
	
	public void parsingTest() {
		double modemFwVer = Double.parseDouble("1.37");
		System.out.println(modemFwVer);
		if(modemFwVer <= 1.22) {
			logger.debug("asdf");
			System.out.println("asdfsssss");
		}	
	}
	
	public void logbackTest(){
		logger.debug("## test - {}", "음화화");
		
	    // print internal state
//	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//	    StatusPrinter.print(lc);
//	    
	    
	}
	
	public void commandTest(){
		String data = "C3FF0210010026000B12000000294722064E414D522D503131374C5400000000000000000001300040007000001005001600013639373036333134303131343735393300000000";
		
		Command command = new Command();
		command.decode(DataUtil.readByteString(data));
	}
	
	public void smiTEst(){
		String imageVersion = "06010075";
		if(imageVersion != null && 0 < imageVersion.indexOf(".")){
			String[] versionArray = imageVersion.split("\\.");
			imageVersion = (String.format("%02d", Integer.parseInt(versionArray[0])) + String.format("%02d", Integer.parseInt(versionArray[1])));
		}else if(imageVersion != null && 2 <= imageVersion.length()){
			imageVersion = imageVersion.substring(imageVersion.length() - 4, imageVersion.length());
		}
		logger.debug("fw = {}", imageVersion);
		
		byte[] hexVersion = Hex.encode(imageVersion);
		logger.debug("Hex={}, size={}", Hex.decode(hexVersion), hexVersion.length);
		
		
		try {
//			byte[] lastVersion = new byte[2];
//			System.arraycopy(hexVersion, 2, lastVersion, 0, 2);
//			logger.debug("lastHex={}", Hex.decode(lastVersion));
			
			Vector<SMIValue> datas = new Vector<SMIValue>();
			datas.add(DataUtil.getSMIValue(new WORD(hexVersion)));
			logger.debug("smi = {}", datas.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void exceptionTest(){
		String exceptionMessage = null;
		try {
			String aaa = null;	
			String bbb = aaa.trim();
			
		} catch (Exception e) {
			logger.error("Excute DCU OTA error - " + e.toString(), e);
			exceptionMessage = e.toString();
		}
		
		String errMsg = (exceptionMessage == null ? "Unknown Error" : exceptionMessage);
		logger.debug("==> {}", errMsg);
		
		//logger.debug("# fw_path={}", params.get("fw_path"));
	}
	
	public void subStTEst(){
		String seqNumber = "06010072";
		String version = seqNumber.substring(seqNumber.length() - 4, seqNumber.length());
		byte[] convertVer = DataUtil.readByteString(version);
		
		System.out.println("==> " + Hex.decode(convertVer));
	}
	
	public void versionconvert(){
		String fwVersion = "1.24";
		//String fwVersion = "06010072";
		byte[] convertVer = null;
		
		if(fwVersion.length() < 2){
			logger.error("invalid length FW Version.");
		}else if(0 < fwVersion.indexOf(".")){ // 점 있음
			String[] versionArray = fwVersion.split("\\.");
			logger.debug("version [0]={}, [1]={}", versionArray[0], versionArray[1]);
			
			byte[] a = DataUtil.readByteString(String.format("%02d", Integer.parseInt(versionArray[0])));
			logger.debug("Version A={}", Hex.decode(a));
			byte[] b = DataUtil.readByteString(String.format("%02d", Integer.parseInt(versionArray[1])));
			logger.debug("Version B={}", Hex.decode(b));
			convertVer = new byte[]{a[0], b[0]};
		}else{  // 점없음
			String version = fwVersion.substring(fwVersion.length() - 2, fwVersion.length());
			convertVer = DataUtil.readByteString(version);
		}
		
		logger.debug("FWVersion = {}, Converted version = {}", fwVersion, Hex.decode(convertVer));
	}
	
	public void suString(){
		String fwFileName = "KFPP_V06010072";
		String model = "MA304T3";
		
		System.out.println("fwFileNameSub=" + fwFileName.substring(0, 6));
		System.out.println("model=" + model.substring(0, 3));
	}
	

	@org.junit.Test
	public void convertIdTest(){
		// MCU BYTE : D56D91B8
        byte[] mcu = DataUtil.readByteString("D56D91B8");
        System.out.println("1 - " + Hex.getHexDump(mcu));
        
        DataUtil.convertEndian(mcu);
        System.out.println("2 - " + Hex.getHexDump(mcu));
        
        //int mcuId = DataUtil.getIntTo4Byte(mcu);
        long mcuId = DataUtil.getLongToBytes(mcu);
        System.out.println("3 - " + mcuId);  // MCU[-1198428715] 
                                                     //2147483647
                                                     //3096538581
        
        long mcuIds = convertInt(mcu);
        System.out.println("4 - " + mcuIds);
	}
	
    public long convertInt(byte[] values)
    {
        long value = 0;
        value |= (values[0] & 0xff);
        value <<= 8;
        value |= (values[1] & 0xff);
        value <<= 8;
        value |= (values[2] & 0xff);
        value <<= 8;
        value |= (values[3] & 0xff);
        
        return value;
    }
	

	public void waitTest(){		
		try {
			logger.debug("111");
			waitForJobFinish(1);
			logger.debug("222");
		} catch (Exception e) {
			logger.debug("333");
			logger.debug(e.getMessage());
		}
		logger.debug("444");
	}
	
	boolean isCommandAciontFinished = false;
	private boolean waitForJobFinish(int commandWaitingTime) throws Exception{
		boolean result = false;
        long stime = System.currentTimeMillis();
        long ctime = 0;
        
        logger.debug("시작");
		while(!isCommandAciontFinished) {
			logger.debug("aaa");
			waitResponse();
			logger.debug("bbb");
		    ctime = System.currentTimeMillis();
		    if(((ctime - stime)/1000) > (commandWaitingTime * 60)){
		    	//log.debug("HandlerName=[" + getHandlerName() + "]getResponse:: SESSION IDLE COUNT["+session.getIdleCount(IdleStatus.BOTH_IDLE)+"]");
		    	
		    	logger.debug("### 여기출력됨 - ctime=" + ctime + ", stime=" + stime + ", commandWaitingTime=" + commandWaitingTime);
		    	
		    	throw new Exception("[Timeout:"+commandWaitingTime +"]");
		     }		    	 
		    logger.debug("ccc");
		 }
		 return result;
	}
	
	private Object resMonitor = new Object();
    public void waitResponse() {
        synchronized(resMonitor) { 
            try { 
            	resMonitor.wait(500);
            } catch(InterruptedException ie) {
            	ie.printStackTrace();
            }
        }
    }
	
	
	
	
	
	
	
	public void test1() {
		System.out.println(this.getClass().getResource(".").getPath());
		DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"src/test/resources/config/spring.xml"}));
		
		String otaType = "GroupOTARetry";
		String firmwareVersion = "0094";
		String firmwareFileName = "NAMR-P214SR-korea-0094";
		String deviceModelName = "NAMR-P214SR";
		String locationName = "SSYS";
		String issueDate = "20161014165124";
		Firmware firmware;
		Location location;

//		TransactionStatus txstatus = null;
//		txstatus = txmanager.getTransaction(null);

        JpaTransactionManager txManager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = null;
        txstatus = txManager.getTransaction(null);
		
		LocationDao locationDao = DataUtil.getBean(LocationDao.class);
		FirmwareDao firmwareDao = DataUtil.getBean(FirmwareDao.class);
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);
		FirmwareIssueDao firmwareIssueDao = DataUtil.getBean(FirmwareIssueDao.class);
		
		try {
			// Firmware 정보
			Set<Condition> firmwareConditions = new HashSet<Condition>();
			firmwareConditions.add(new Condition("fwVersion", new Object[] { firmwareVersion }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("fileName", new Object[] { firmwareFileName }, null, Restriction.EQ));
			firmwareConditions.add(new Condition("equipModel", new Object[] { deviceModelName }, null, Restriction.EQ));
			List<Firmware> firmwareList = firmwareDao.findByConditions(firmwareConditions);
			if (firmwareList != null && 0 < firmwareList.size()) {
				firmware = firmwareList.get(0);
				logger.debug("### Firmware info => {}", firmware.toString());
			} else {
				logger.error("Unknown Firmware. please check firmware file name at Firmware Tap in Firmware Management gadget.");
				return;
			}

			// Location 정보
			List<Location> locationList = locationDao.getLocationByName(locationName);
			if (locationList != null && 0 < locationList.size()) {
				location = locationList.get(0);
				logger.debug("### Location info => {}", location.toString());
			} else {
				logger.error("Unknown Location. please check location name.");
				return;
			}

			// Group OTA History정보
			FirmwareIssue firmwareIssue = null;
			Set<Condition> fIcondition = new HashSet<Condition>();
			fIcondition.add(new Condition("id.locationId", new Object[] { location.getId() }, null, Restriction.EQ));
			fIcondition.add(new Condition("id.firmwareId", new Object[] { Long.valueOf(firmware.getId()) }, null, Restriction.EQ));
			fIcondition.add(new Condition("id.issueDate", new Object[] { issueDate }, null, Restriction.EQ));
			List<FirmwareIssue> firmwareIssueList = firmwareIssueDao.findByConditions(fIcondition);
			if (firmwareIssueList != null && 0 < firmwareIssueList.size()) {
				firmwareIssue = firmwareIssueList.get(0);
				logger.debug("### FirmwareIssue info => {}", firmwareIssue.toString());
			} else {
				logger.error("There is no Group OTA History. Please check Task information.");
				return;
			}

			// Group OTA 목록
			String deviceId = "";
			String targetType;
			String byPass;
			boolean takeOver = false;
			String locaionId = "";
			String firmwareId = "";

			FirmwareIssueHistory firmwareIssueHistory = null;
			Set<Condition> condition = new HashSet<Condition>();
			condition.add(new Condition("id.locationId", new Object[] { firmwareIssue.getLocationId() }, null, Restriction.EQ));
			condition.add(new Condition("id.firmwareId", new Object[] { firmwareIssue.getFirmwareId() }, null, Restriction.EQ));
			condition.add(new Condition("id.issueDate", new Object[] { firmwareIssue.getIssueDate() }, null, Restriction.EQ));
			condition.add(new Condition("resultStatus", new Object[] { "Success" }, null, Restriction.NEQ));
			condition.add(new Condition("resultStatus", null, null, Restriction.NULL));

			List<FirmwareIssueHistory> firmwareIssueHistoryList = firmwareIssueHistoryDao.findByConditions(condition);
			if (firmwareIssueHistoryList != null && 0 < firmwareIssueHistoryList.size()) {

				StringBuilder sb = new StringBuilder();
				for (FirmwareIssueHistory fih : firmwareIssueHistoryList) {
					logger.debug("############# ===> DeviceId = {},  getResultStatus = {}", fih.getDeviceId(), fih.getResultStatus());
					if (fih.getResultStatus() != null && fih.getResultStatus().equals("Success")) {
						logger.info("### Device = [{}] is skip. because [{}] is aready OTA success.", fih.getDeviceId(), fih.getDeviceId());
					} else {
						sb.append(fih.getDeviceId() + ",");
					}

				}
				deviceId = sb.toString();
				deviceId = deviceId.substring(0, deviceId.length() - 1); // 콤마 제거
				targetType = firmwareIssueHistoryList.get(0).getDeviceType().name();
				byPass = firmwareIssueHistoryList.get(0).getUesBypass().toString();
				locaionId = String.valueOf(firmwareIssueHistoryList.get(0).getLocationId());
				firmwareId = String.valueOf(firmwareIssueHistoryList.get(0).getFirmwareId());

				logger.debug("### FirmwareIssueHistory info => TargetType={}, UseBypass={}, DeviceId={}", targetType, byPass, deviceId);
			} else {
				logger.error("There is no OTA Target. Please check Group OTA History.");
				return;
			}

			/*
			 * Group OTA Retry
			 * com.aimir.bo.command.OTACmdController.commandOTAStart() 참조
			 */
			if (firmwareIssueHistoryList != null && 0 < firmwareIssueHistoryList.size()) {
				logger.debug("========= > deviceId={}, targetType={}, takeOver={}, byPass={}, locationId={}, firmwareId={}", deviceId, targetType, Boolean.toString(takeOver), byPass, locaionId, firmwareId);
			} else {
				logger.error("FirmwareIssueHistory list is empty.");
			}

			
			txManager.commit(txstatus);
			
			
		} catch (Exception e) {
			if (txstatus != null) {
				txManager.rollback(txstatus);
			}
			logger.error("Task Excute transaction error - " + e, e);
			return;
		}
		if (txstatus != null) {
			txManager.commit(txstatus);
		}
	}

	public void abstTest() {

		ConcurrentHashMap<String, NICommandAction> commandActionMap = new ConcurrentHashMap<String, NICommandAction>();
		try {
			commandActionMap.put("cmdModemOTAStart_SP", (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_cmdModemOTAStart_Action_SP").newInstance());
			// Sample - 추후 필요시 사용할것
			commandActionMap.put("cmdSample1_SP", (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_Sample1_Action_SP").newInstance());
			commandActionMap.put("cmdSample2_GD", (NICommandAction) Class.forName("com.aimir.fep.protocol.nip.client.actions.NI_Sample2_Action_GD").newInstance());

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void otatest() {
		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		//		EV_SP_200_64_0_Action action = new EV_SP_200_64_0_Action();
		//		action.makeEvent(TargetClass.EnergyMeter, target.getMeterId(), TargetClass.EnergyMeter, openTime, "HES");
		//		action.updateOTAHistory(target.getMeterId(), DeviceType.Meter, openTime);
		//		

	}

	public void testtiem() {
		String message = "Firmware Updated. Current Modem Time = " + DateTimeUtil.getCurrentDateTimeByFormat1("yyyyMMddHHmmss");
		System.out.println(message);
	}

	public void testaaa() {

		int[] result = HdlcConstants.getRSCount((byte) 0x1E);
	}

	public void testt() {
		String str = "07E0 08 11 03 08 00 00 FF800000";

		try {
			String result = DataUtil.getDateTimeByDLMS_OCTETSTRING12(DataUtil.readByteString(str));

			System.out.println("==> " + result);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public void test() {
		CommandGW gw = new CommandGW();
		try {
			gw.cmdGetMeterFWVersion("TopologyTest_1");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
