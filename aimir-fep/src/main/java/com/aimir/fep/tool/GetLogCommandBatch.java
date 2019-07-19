package com.aimir.fep.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.protocol.nip.command.ModemEventLog;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.NIAttributeId;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Modem;

import net.sf.json.JSONObject;

/**
 * @author innnbang
 *
 */
@Service
public class GetLogCommandBatch {
	private static Log log = LogFactory.getLog(GetLogCommandBatch.class);
	private static String fileName = "getLogModemList.txt";
	private List<String> targetList;
	ApplicationContext ctx;
    
	public static void main(String[] arg) {
		log.info("logCount: " +	arg[1]);
		int count = Integer.parseInt(arg[1]);
		
		// INSERT START SP-681
		log.info("logOffset: " +	arg[2]);
		int offset = Integer.parseInt(arg[2]);
		// INSERT END SP-681
		
		GetLogCommandBatch forJob = new GetLogCommandBatch();
		forJob.setTargetList();
		// UPDATE START SP-681
//		forJob.getLogStart(count);
		forJob.getLogStart(count, offset);
		// UPDATE END SP-681
		
		System.exit(0);	
	}
	
	// UPDATE START SP-681
//	private void getLogStart(int logCount){
	private void getLogStart(int logCount, int logOffset){
	// UPDATE END SP-681
		try {
			springInit();
		} catch (Exception e1) {
			log.error(e1);
		}
		
		CommandGW cgw = (CommandGW)DataUtil.getBean(CommandGW.class);
		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		String[] deviceSerialArr = targetList.toArray(new String[targetList.size()]);
        List<Integer> mdsId_list = new ArrayList<Integer>();
        Modem modem = null;
        
        long time = System.currentTimeMillis(); 
		SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = dayTime.format(new Date(time));
		String resultStr = "--------------------------------------------------------------------------------------------\n"
				+ "Execute date: " + date 
				+ "\n--------------------------------------------------------------------------------------------";
		
            for(String deviceSerial : deviceSerialArr){
            	modem = modemDao.get(deviceSerial);
            	if(modem == null){
            		log.warn("!!!!!!!!!! Invalid Modem Serial : " + deviceSerial + " !!!!!!!!!!");
            	}else if(modem.getProtocolType() == Protocol.SMS){
            		log.warn("!!!!!!!!!! MBB Modem is not available !!!!!!!!!!");
            	}else{
            		mdsId_list.add(modem.getId());
            	}
            }
        
        Map<String, String> result = new HashMap<String, String>();
        int i =0;
        String deviceSerial="";
		// INSERT START SP-758
        String attrParam="";
        
        if (logOffset < 0) {
        	attrParam = Hex.decode(DataUtil.get2ByteToInt(logCount));
        }
        else {
        	// For FW Version is 1.2
        	attrParam = Hex.decode(DataUtil.get2ByteToInt(logCount)) +
        			Hex.decode(DataUtil.get2ByteToInt(logOffset));        	
        }
        // INSERT END SP-758

	        for(Integer mdsid : mdsId_list){
	        	deviceSerial = deviceSerialArr[i];
	        	resultStr += "\n### Target : " +deviceSerial + " ### \n";
	        	log.info("###Target :" + deviceSerial + "###");
				try {
					// UPDATE START SP-758
//					// UPDATE START SP-681
////					result=cgw.getModemEventLog(mdsid.toString(), logCount);
//					result=cgw.getModemEventLog(mdsid.toString(), logCount, logOffset);
//					// UPDATE END SP-681
//					if(result.containsKey("eventLogs")){
//						resultStr += result.get("eventLogs") + "\n";
//						log.info(result.get("eventLogs"));
//					}else{
//						resultStr += "[Fail] communication error \n";
//						log.info("[Fail][" + deviceSerial + "] communication error");
//					}
					result=cgw.cmdExecDmdNiCommand(mdsid.toString(), 
							"GET", 
							Hex.decode(NIAttributeId.ModemEventLog.getCode()),
							attrParam);
					if(result.containsKey("AttributeData")){
						ModemEventLog modemEventLog = new ModemEventLog();
						JSONObject jo = JSONObject.fromObject(result.get("AttributeData"));
						
						
						byte[] bx = Hex.encode(String.valueOf(jo.get("Value")));
						modemEventLog.decode(bx);
													
						resultStr += modemEventLog.toString() + "\n";
						log.info(modemEventLog.toString());
					}else{
						resultStr += "[Fail] communication error \n";
						log.info("[Fail][" + deviceSerial + "] communication error");
					}
					// UPDATE END SP-758
				} catch (Exception e) {
					log.error(e);
				}
				i++;
	        }
	        makeResultFile(resultStr+"--------------------------------------------------------------------------------------------\n\n");
	        
        
        
	}
	
	private void setTargetList() {
        InputStream fileInputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (fileInputStream != null) {
            targetList = new LinkedList<String>();

            @SuppressWarnings("resource")
            Scanner scanner = new Scanner(fileInputStream);
            while (scanner.hasNextLine()) {
                String target = scanner.nextLine().trim();
                if(!target.equals("")){
                    targetList.add(target);                 
                }
            }
            log.info("Target List:" + "("+targetList.size()+")" + targetList.toString());
        } else {
            log.info("File not found");
        }
    }
	
	private void springInit() throws Exception{
        ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-getLog.xml" });
        DataUtil.setApplicationContext(ctx);
    }
	
	private void makeResultFile(String resultStr){
		String fileName = "./log/GetLogResult.txt";
        try{
            File file = new File(fileName) ;
            FileWriter fw = new FileWriter(file, true) ;
            fw.write(resultStr);
            fw.flush();
            fw.close(); 
        }catch(Exception e){
            log.error(e);
        }
	}
}
