/**
 * (@)# EMnVCommandBatch.java
 *
 * 2015. 7. 29.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.OperatorDao;
import com.aimir.fep.protocol.emnv.frame.EMnVImgCRC16;
import com.aimir.fep.tool.EMnVCommandBatch.EMnVCommandType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Operator;
import com.aimir.model.system.Supplier;
import com.aimir.util.CalendarUtil;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;
import com.aimir.util.TimeUtil;

/**
 * @author nuri
 *
 */
public class EMnVCommandBatch {
    private static Logger log = LoggerFactory.getLogger(EMnVCommandBatch.class);

    private final int CORE_POOL_SIZE = 5;      // 10개부터는 db connection 에러남.
    private final int MAXIMUM_POOL_SIZE = 20;
    private final int KEEP_ALIVE_TIME = 1;
    private final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MINUTES;
    private final int AWAIT_TIME_OUT = 1;
    private final TimeUnit AWAIT_TIME_OUT_TIME_UNIT = TimeUnit.MINUTES;
    private boolean isNowRunning = false;
    private int SMS_RESULT_WAIT_SECOND = 20;              // SMS 보낸뒤 리턴값 기다리는 주기. 

    List<Map<String, String>> successList = new LinkedList<Map<String, String>>();
    List<Map<String, String>> failList = new LinkedList<Map<String, String>>();
    ThreadPoolExecutor executor = null;

    private static EMnVCommandType commandType;
    private static String fileName = "EMnVCommandBatch_list.txt";
    private List<String> targetList;
    private static String param1 = null;
    private static String param2 = null;
    private static String interval = null;  // SMS 보내는 주기.

    //@Resource(name="transactionManager")
    ApplicationContext ctx;
    JpaTransactionManager txmanager;
    TransactionStatus txstatus;

    /**
     * Command Type
     */
    public enum EMnVCommandType {
          OTA("cmdOTAStart")
        , SERVER_IP("cmdServerIp")
        , SERVER_PORT("cmdServerPort")
        , LP_INTERVAL("cmdLPInterval")
        , HW_RESET_INTERVAL("cmdHWResetInterval")
        , NV_RESET("cmdNVReset")
        , M_NUMBER("cmdMNumber")          // 사용하지 않아 구현하지 않았음.
        , HW_RESET("cmdHWReset")
        , EVENT_LOG("cmdEventLog")        // 사용하지 않아 구현하지 않았음.          
        , KEY_CHANGE("cmdKeyChange")
        , ON_DEMAND("cmdOnDemand")
        , METER_TIMESYNC("cmdSetMeterTime")
        , METER_SCAN("cmdSetMeterScan")
        , INVERTER_INFO("cmdInverterInfo")
        , INVERTER_SETUP("cmdInverterSetup")
        , UNKNOWN("Unknown");

        private String cmd;

        private EMnVCommandType(String cmd) {
            this.cmd = cmd;
        }

        public String getCmd() {
            return cmd;
        }

        public static EMnVCommandType getItem(String cmd) {
            for (EMnVCommandType fc : EMnVCommandType.values()) {
                if (fc.cmd.equals(cmd)) {
                    return fc;
                }
            }
            return UNKNOWN;
        }
    }
    
    
    /*
     * 
     *           테스트 코드 . 추후 삭제할것.
     * 
     * 
     */
    public void executeTest() {
        commandType = EMnVCommandType.getItem("cmdOnDemand");
        param1 = "0";
        param2 = "0";
        
//      commandType = EMnVCommandType.getItem("cmdSetMeterTime");

        execute();
    }

    //@Override
    //public void execute(JobExecutionContext context) {
    public void execute() {
        if (isNowRunning) {
            log.info("########### EMnV CommandBatch Task[{}] is already running...", commandType);
            return;
        }

        isNowRunning = true;
        Date startDate = new Date();
        long startTime = startDate.getTime();

        log.info("########### START EMnV Command[{}] - {} ###############"
                , new Object[]{commandType ,CalendarUtil.getDatetimeString(startDate, "yyyy-MM-dd HH:mm:ss")});
        
        setTargetList();

        if (targetList == null || targetList.size() <= 0) {
            log.warn("Have no taget list. please check target list.");
        } else {
            successList.clear();
            failList.clear();
            
            switch (commandType) {
            case OTA:
                if (param1 == null || param1.equals("")) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType cmdOTAStart -param1 [F/W File name]");
                }else{
                    otaStart();
                }
                break;
            case SERVER_IP:
                if (param1 == null || param1.equals("")) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdServerIp] -param1 [parameter]");
                }else{
                    normalModemCommandStart(commandType, "8.1.15", param1); // Server IP Change
                }
                break;
            case SERVER_PORT:
                if (param1 == null || param1.equals("")) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdServerPort] -param1 [parameter]");
                }else{
                    normalModemCommandStart(commandType, "8.1.17", param1); // Server Port Change
                }
                break;
            case LP_INTERVAL:
                if (param1 == null || param1.equals("")) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdLPInterval] -param1 [parameter]");
                }else{
                    normalModemCommandStart(commandType, "8.1.19", param1); // LP Interval Change
                }
                break;
            case HW_RESET_INTERVAL:
                if (param1 == null || param1.equals("")) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdHWResetInterval] -param1 [parameter]");
                }else{
                    normalModemCommandStart(commandType, "8.1.21", param1); // H/W Reset Interval Change
                }
                break;
            case NV_RESET:
                if (param1 != null) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdNVReset]");
                }else{
                    normalModemCommandStart(commandType, "8.1.22", null); // NV Reset
                }
                break;
            case M_NUMBER:
                log.info("This Command is Unavailable not yet.");
                break;
            case HW_RESET:
                if (param1 != null) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdHWReset]");
                }else{
                    normalModemCommandStart(commandType, "8.1.25", null); // H/W Reset
                }
                break;
            case EVENT_LOG:
                log.info("This Command is Unavailable not yet.");
                break;
            case KEY_CHANGE:
                log.info("기능구현되어있음. 관리자에게 문의바람.");
                // 추후 사용시 아래 주석 풀어서 사용할것.
//              if (param1 == null || param1.equals("")) {
//                  log.warn("Please check input Options.");
//                  log.warn("ex) -f [Modem List file name] -commandType [cmdKeyChange] -param1 [parameter]");
//              }else{
//                  normalModemCommandStart(commandType, "8.1.24", param1); // Key Change
//              }
                break;  
            case ON_DEMAND:
                if(param1 == null || param2 == null){
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Meter List file name] -commandType cmdOnDemand -param1 [start LPIndex] -param2 [end LPIndex]");
                }else{
                    int startLpIndex = Integer.parseInt(param1);
                    int endLPIndex = Integer.parseInt(param2);
                    int smsInterval = interval == null ? 5 : Integer.parseInt(interval);
                    
                    if (endLPIndex < startLpIndex) {
                        log.warn("Please check StartLPIndex or EndLPIndex Options.");
                    }else{
                        selectiveLPStart(startLpIndex, endLPIndex, smsInterval);
                    }
                }
                break;  
            case METER_TIMESYNC:
                if (param1 != null) {
            log.error("#################### param1={}", param1);
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdSetMeterTime]");
                }else{
                    normalMeterCommandStart(commandType, "8.1.3"); // Meter Time Synchronization
                }
                break;  
            case METER_SCAN:
                if (param1 != null) {
                    log.warn("Please check input Options.");
                    log.warn("ex) -f [Modem List file name] -commandType [cmdSetMeterScan]");
                }else{
                    normalMeterCommandStart(commandType, "8.1.32"); // Meter Scan
                }
                break;  
            case INVERTER_INFO:
                log.info("This Command is Unavailable not yet.");
                break;  
            case INVERTER_SETUP:
                log.info("This Command is Unavailable not yet.");
                break;  
            case UNKNOWN:
                
                break;  
            default:
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        log.info("FINISHED EMnV Command[{}] - Elapse Time : {}s", commandType, (endTime - startTime) / 1000.0f);

        log.info("########### END EMnV CommandBatch Task[{}] ############", commandType);
        isNowRunning = false;
        
        System.exit(0);
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
            log.info("Target List({}) ===> {}", targetList.size(), targetList.toString());
        } else {
            log.info("[{}] file not found", fileName);
        }
    }
    
    private void springInit() throws Exception{
        ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring.xml" });
        DataUtil.setApplicationContext(ctx);
    }
    
    
    /**
     * Selective LP Command
     */
    private void selectiveLPStart(int startLpIndex, int endLPIndex, int smsInterval) {
        try {
            springInit();
            log.info("################# EXCUTE_SEND_LP_IDX_WAIT_MINUTES = {} ###########", smsInterval);
            txmanager = (JpaTransactionManager)ctx.getBean("transactionManager");
            txstatus = txmanager.getTransaction(null);
            
            /*
             * Code 생성
             */
            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
            Code operationCode = codeDao.getCodeIdByCodeObject("8.1.1"); // On Demand Metering
            Code targetTypeCode = codeDao.getCodeIdByCodeObject("1.3.1.1"); // EnergyMeter
            
            /*
             * Meter Search
             */
            List<Meter> mList = new LinkedList<Meter>();
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("mdsId", targetList.toArray(), null, Restriction.IN));
            condition.add(new Condition("mdsId", null, null, Restriction.ORDERBY));
            mList = meterDao.findByConditions(condition);

            /*
             * 잘못된 미터 걸러내기 : 중복된 모뎀을 사용하는 미터는 Command수행에서 제외시키고 다음차례에 진행하도록 목록을 출력해준다.
             *  - 맵핑되지 않은미터, 인버터도 걸러낸다.
             */
            List<Meter> firstTargetList = new LinkedList<Meter>();
            List<Meter> secondTargetList = new LinkedList<Meter>();
            List<Meter> emptyTargetList = new LinkedList<Meter>();
            List<Meter> inverterList = new LinkedList<Meter>();
            
            List<String> tempList = new LinkedList<String>();           
            for(Meter meter: mList){
                if(MeterType.valueOf(meter.getMeterType().getName()) == MeterType.Inverter){
                    inverterList.add(meter);
                }else{
                    if(meter.getModem() != null){
                        String modemDeviceSerial = meter.getModem().getDeviceSerial();
                        if(!tempList.contains(modemDeviceSerial)){
                            tempList.add(modemDeviceSerial);
                            firstTargetList.add(meter);
                        }else{
                            secondTargetList.add(meter);
                        }                       
                    }else{
                        emptyTargetList.add(meter);
                    }
                }
            }
            
            txmanager.commit(txstatus);
            
            if (mList == null || mList.size() <= 0) {
                log.info("Meter List is null. please check your Meter list.");
            } else {
                Collection<EMnVCommandCallable> callList = new LinkedList<EMnVCommandCallable>();
                
                // LP 0을 한번더 실행하는방식으로 변경되었기때문에 +1을 해줌.
                endLPIndex++;
                
                for (int i = startLpIndex; i <= endLPIndex; i++) {
                    /*
                     * 제일 마지막에 Selective LP 0 을 한번 더 실행하도록 함.
                     */
                    int excueteLpIndex = i;
                    if(i == endLPIndex){
                        excueteLpIndex = 0;
                    }
                    
                    
                    callList.clear();
                    successList.clear();
                    failList.clear();
                    
                    for (Meter meter : firstTargetList) {
                        callList.add(new EMnVCommandCallable(EMnVCommandType.ON_DEMAND, operationCode, targetTypeCode, meter, excueteLpIndex));
                    }
                    
                    // Excute Job.
                    executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
                    List<Future<Map<String, String>>> futureL = executor.invokeAll(callList, 3, TimeUnit.MINUTES);  // tasks, long timeout, TimeUnit unit

                    TimeUnit.SECONDS.sleep(SMS_RESULT_WAIT_SECOND);
                    for (Future<Map<String, String>> resultFuture : futureL) {
                        Map<String, String> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // the maximum time to wait
                        if (resultFuture.isDone()) {
                            if (ht.containsKey("error_create") || ht.containsKey("FAIL") || ht.containsKey("ERROR") || ht.containsKey("STATUS")) { // excute Fail.
                                failList.add(ht);
                            } else { // excute Success.
                                successList.add(ht);
                            }
                        } else if (resultFuture.isCancelled()) {
                            log.info("###### Future is Cancelled ==> " + ht.get("TARGET"));
                            failList.add(ht);
                        } else {
                            Map<String, String> unknownCancelMap = new HashMap<String, String>();
                            unknownCancelMap.put("ERROR", resultFuture.toString());
                            failList.add(unknownCancelMap);
                        }
                    }

                    /**
                     * Logging
                     */
                    log.info(" ");
                    log.info("=========== EXCUTE SUCCESS LIST ({}), LPIndex={} ==========", successList.size(), excueteLpIndex);
                    int count = 1;
                    if (successList != null && 0 < successList.size()) {
                        for (Map<String, String> sMap : successList) {
                            log.info("{}. {}", count, sMap.toString());
                            count++;
                        }
                    } else {
                        log.info("There is no list of successful.");
                    }
                    log.info("========================================");
                    
                    count = 1;
                    log.info("=========== EXCUTE FAIL LIST ({}), LPIndex={} ==========", failList.size(), excueteLpIndex);
                    if (failList != null && 0 < failList.size()) {
                        for (Map<String, String> fMap : failList) {
                            log.info("{}. {}", count, fMap.toString());
                            count++;                            
                        }
                    } else {
                        log.info("There is no list of failure.");
                    }
                    log.info("========================================");

                    TimeUnit.MINUTES.sleep(Integer.parseInt(interval));
                }
                
                /*
                 * 동일한 모뎀을 사용하는 중복된 미터가 있을경우 리스트를 출력해준다.
                 */
                int count = 1;
                if(0 < secondTargetList.size()){
                    log.info("");
                    log.info("=========== 중복된 Modem LIST ({}) ==========", secondTargetList.size());
                    
                    for (Meter meter : secondTargetList) {
                        log.info("{}. Meter={} / Modem={}", new Object[]{count, meter.getMdsId(), meter.getModem().getDeviceSerial()});
                        count++;
                    }
                    log.info("========================================");
                }
                
                /*
                 * 모뎀맵핑이 안된 미터가 있을경우 리스트를 출력해준다.
                 */
                if(0 < emptyTargetList.size()){
                    log.info("");
                    log.info("=========== 맵핑이 되지않은 Meter LIST ({}) ==========", emptyTargetList.size());
                    count = 1;
                    for (Meter meter : emptyTargetList) {
                        log.info("{}. Meter={}", count, meter.getMdsId());
                        count++;
                    }
                    log.info("========================================");
                }
                
                /*
                 * 인버터터가 있을경우 리스트를 출력해준다.
                 */
                if(0 < inverterList.size()){
                    log.info("");
                    log.info("=========== Inverter LIST ({}) ==========", inverterList.size());
                    count = 1;
                    for (Meter meter : inverterList) {
                        log.info("{}. Inverter={}", count, meter.getMdsId());
                        count++;
                    }
                    log.info("========================================");
                }
            }

        } catch (Exception e) {
            log.error("Exception-", e);
        } finally {
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                        if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                            log.error("Pool did not terminate");
                        }
                    }
                } catch (InterruptedException ie) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("END.");       
    }


    /**
     * OTA
     */
    private void otaStart() {
        try {
            springInit();
            
            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
            
            /*
             * F/W 파일 저장
             */
            Path path = FileSystems.getDefault().getPath(param1).toRealPath(LinkOption.NOFOLLOW_LINKS);
            
            byte[] fileBinary = Files.readAllBytes(path);
            String[] fileInfo = path.getFileName().toString().split(",");
            String prodName = fileInfo[0];
            String modemModel = fileInfo[1];
            String fileName = fileInfo[2];
            String fwVersion =fileName.substring(0, fileName.lastIndexOf("."));
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            
            byte[] imgCrc16 = new EMnVImgCRC16().getImgCRC16(fileBinary, (char)0x0000);
            
            //파일 저장
            String osName = System.getProperty("os.name");
            String homePath = "";
            if(osName != null && !"".equals(osName) && osName.toLowerCase().indexOf("window") >= 0){
                homePath = prop.getProperty("kemco.firmware.window.dir");
                if(homePath == null || homePath.equals("")){
                    throw new NoSuchFileException("kemco.firmware.window.dir");
                }
            }else{
                homePath = prop.getProperty("kemco.firmware.dir");
                if(homePath == null || homePath.equals("")){
                    throw new NoSuchFileException("kemco.firmware.dir");
                }
            }
            
            String finalFilePath = makeFirmwareDirectoryForEMnV(homePath, prodName + "/" + modemModel + "/",  fwVersion, ext, true);

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("fw_path", finalFilePath);                          // 서버에 저장된 파일경로
            paramMap.put("fw_size", Long.toString(Files.size(path)));       // 파일사이즈
            paramMap.put("fw_version", fileName);                            // 버전
            paramMap.put("fw_crc", Hex.decode(imgCrc16));                    // CRC16
            
            log.info(String.format("[Save firmware]"
                    + " filePath=" + paramMap.get("fw_path") + " fileSize=" + paramMap.get("fw_size")
                    + " fileName=" + paramMap.get("fw_version") + " FW_CRC16=" + paramMap.get("fw_crc")));
            
            Path newPath = Paths.get(finalFilePath);
            Files.write(newPath, fileBinary);
            
            txmanager = (JpaTransactionManager)ctx.getBean("transactionManager");
            txstatus = txmanager.getTransaction(null);
            
            /*
             * Code 생성
             */
            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
            Code operationCode = codeDao.getCodeIdByCodeObject("8.2.13");     // OTA
            Code targetTypeCode = codeDao.getCodeIdByCodeObject("1.2.1.201"); // LTE
            
            /*
             * Modem Search
             */
            List<Modem> mList = new LinkedList<Modem>();
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("deviceSerial", targetList.toArray(), null, Restriction.IN));
            condition.add(new Condition("deviceSerial", null, null, Restriction.ORDERBY));
            mList = modemDao.findByConditions(condition);
            
            txmanager.commit(txstatus);
            
            if (mList == null || mList.size() <= 0) {
                log.info("Modem List is null. please check your Modem list.");
            } else {
                log.info("Modem List size = {}", mList.size());
                
                Collection<EMnVCommandCallable> callList = new LinkedList<EMnVCommandCallable>();
                
                for (Modem m : mList) {
                    callList.add(new EMnVCommandCallable(EMnVCommandType.OTA, operationCode, targetTypeCode, m, paramMap));
                }
                
                // Excute Job.
                executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
                List<Future<Map<String, String>>> futureL = executor.invokeAll(callList, 3, TimeUnit.MINUTES);  // tasks, long timeout, TimeUnit unit

                TimeUnit.SECONDS.sleep(SMS_RESULT_WAIT_SECOND);
                for (Future<Map<String, String>> resultFuture : futureL) {
                    Map<String, String> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // the maximum time to wait
                    if (resultFuture.isDone()) {
                        if (ht.containsKey("error_create") || ht.containsKey("FAIL") || ht.containsKey("ERROR") || ht.containsKey("STATUS")) { // excute Fail.
                            failList.add(ht);
                        } else { // excute Success.
                            successList.add(ht);
                        }
                    } else if (resultFuture.isCancelled()) {
                        log.info("###### Future is Cancelled ==> " + ht.get("TARGET"));
                        failList.add(ht);
                    } else {
                        Map<String, String> unknownCancelMap = new HashMap<String, String>();
                        unknownCancelMap.put("ERROR", resultFuture.toString());
                        failList.add(unknownCancelMap);
                    }
                }

                /**
                 * Logging
                 */
                log.info(" ");
                log.info("=========== EXCUTE SUCCESS LIST ({}) ==========", successList.size());
                int count = 1;
                if (successList != null && 0 < successList.size()) {
                    for (Map<String, String> sMap : successList) {
                        log.info("{}. {}", count, sMap.toString());
                        count++;
                    }
                } else {
                    log.info("There is no list of successful.");
                }
                log.info("========================================");
                
                count = 1;
                log.info("=========== EXCUTE FAIL LIST ({}) ==========", failList.size());
                if (failList != null && 0 < failList.size()) {
                    for (Map<String, String> fMap : failList) {
                        log.info("{}. {}", count, fMap.toString());
                        count++;                            
                    }
                } else {
                    log.info("There is no list of failure.");
                }
                log.info("========================================");
            }

        } catch (NoSuchFileException ne){
            log.error("Can not found FirmWare File. - ", ne);
        } catch (IOException ie){ 
            log.error("File access Error - {}", ie);
        } catch (Exception e) {
            log.error("Exception-", e);
        } finally {
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                        if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                            log.error("Pool did not terminate");
                        }
                    }
                } catch (InterruptedException ie) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("END.");       
    }
    
    /**
     * Normal Meter Command 
     * 
     * Meter Time Sync
     * Meter Scan
     */
    private void normalMeterCommandStart(EMnVCommandType commandType, String opCode) {
        try {
            springInit();
            
            txmanager = (JpaTransactionManager)ctx.getBean("transactionManager");
            txstatus = txmanager.getTransaction(null);
            
            /*
             * Code 생성
             */
            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
            Code operationCode = codeDao.getCodeIdByCodeObject(opCode);           // Meter Time Synchronization or Meter Scan
            Code targetTypeCode = codeDao.getCodeIdByCodeObject("1.3.1.1");       // EnergyMeter
            
            /*
             * Meter Search
             */
            List<Meter> mList = new LinkedList<Meter>();
            MeterDao meterDao = DataUtil.getBean(MeterDao.class);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("mdsId", targetList.toArray(), null, Restriction.IN));
            condition.add(new Condition("mdsId", null, null, Restriction.ORDERBY));
            mList = meterDao.findByConditions(condition);

            /*
             * 잘못된 미터 걸러내기 : 중복된 모뎀을 사용하는 미터는 Command수행에서 제외시키고 다음차례에 진행하도록 목록을 출력해준다.
             *  - 맵핑되지 않은미터, 인버터도 걸러낸다.
             */
            List<Meter> firstTargetList = new LinkedList<Meter>();
            List<Meter> secondTargetList = new LinkedList<Meter>();
            List<Meter> emptyTargetList = new LinkedList<Meter>();
            List<Meter> inverterList = new LinkedList<Meter>();
            
            List<String> tempList = new LinkedList<String>();           
            for(Meter meter: mList){
                if(MeterType.valueOf(meter.getMeterType().getName()) == MeterType.Inverter){
                    inverterList.add(meter);
                }else{
                    if(meter.getModem() != null){
                        String modemDeviceSerial = meter.getModem().getDeviceSerial();
                        if(!tempList.contains(modemDeviceSerial)){
                            tempList.add(modemDeviceSerial);
                            firstTargetList.add(meter);
                        }else{
                            secondTargetList.add(meter);
                        }                       
                    }else{
                        emptyTargetList.add(meter);
                    }
                }
            }
            
            txmanager.commit(txstatus);
            
            if (mList == null || mList.size() <= 0) {
                log.info("Meter List is null. please check your Meter list.");
            } else {
                Collection<EMnVCommandCallable> callList = new LinkedList<EMnVCommandCallable>();

                for (Meter meter : firstTargetList) {
                    callList.add(new EMnVCommandCallable(commandType, operationCode, targetTypeCode, meter, null));
                }
                
                // Excute Job.
                executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
                List<Future<Map<String, String>>> futureL = executor.invokeAll(callList, 3, TimeUnit.MINUTES);  // tasks, long timeout, TimeUnit unit

                TimeUnit.SECONDS.sleep(SMS_RESULT_WAIT_SECOND);
                for (Future<Map<String, String>> resultFuture : futureL) {
                    Map<String, String> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // the maximum time to wait
                    if (resultFuture.isDone()) {
                        if (ht.containsKey("error_create") || ht.containsKey("FAIL") || ht.containsKey("ERROR") || ht.containsKey("STATUS")) { // excute Fail.
                            failList.add(ht);
                        } else { // excute Success.
                            successList.add(ht);
                        }
                    } else if (resultFuture.isCancelled()) {
                        log.info("###### Future is Cancelled ==> " + ht.get("TARGET"));
                        failList.add(ht);
                    } else {
                        Map<String, String> unknownCancelMap = new HashMap<String, String>();
                        unknownCancelMap.put("ERROR", resultFuture.toString());
                        failList.add(unknownCancelMap);
                    }
                }

                /**
                 * Logging
                 */
                log.info(" ");
                log.info("=========== EXCUTE SUCCESS LIST ({}) ==========", successList.size());
                int count = 1;
                if (successList != null && 0 < successList.size()) {
                    for (Map<String, String> sMap : successList) {
                        log.info("{}. {}", count, sMap.toString());
                        count++;
                    }
                } else {
                    log.info("There is no list of successful.");
                }
                log.info("========================================");
                
                
                log.info("=========== EXCUTE FAIL LIST ({}) ==========", failList.size());
                if (failList != null && 0 < failList.size()) {
                    count = 1;
                    for (Map<String, String> fMap : failList) {
                        log.info("{}. {}", count, fMap.toString());
                        count++;                            
                    }
                } else {
                    log.info("There is no list of failure.");
                }
                log.info("========================================");
                
                /*
                 * 동일한 모뎀을 사용하는 중복된 미터가 있을경우 리스트를 출력해준다.
                 */
                
                if(0 < secondTargetList.size()){
                    log.info("");
                    log.info("=========== 중복된 Modem LIST ({}) ==========", secondTargetList.size());
                    count = 1;
                    for (Meter meter : secondTargetList) {
                        log.info("{}. Meter={} / Modem={}", new Object[]{count, meter.getMdsId(), meter.getModem().getDeviceSerial()});
                        count++;
                    }
                    log.info("========================================");
                }
                
                /*
                 * 모뎀맵핑이 안된 미터가 있을경우 리스트를 출력해준다.
                 */
                if(0 < emptyTargetList.size()){
                    log.info("");
                    log.info("=========== 맵핑이 되지않은 Meter LIST ({}) ==========", emptyTargetList.size());
                    count = 1;
                    for (Meter meter : emptyTargetList) {
                        log.info("{}. Meter={}", count, meter.getMdsId());
                        count++;
                    }
                    log.info("========================================");
                }
                
                /*
                 * 인버터터가 있을경우 리스트를 출력해준다.
                 */
                if(0 < inverterList.size()){
                    log.info("");
                    log.info("=========== Inverter LIST ({}) ==========", inverterList.size());
                    count = 1;
                    for (Meter meter : inverterList) {
                        log.info("{}. Inverter={}", count, meter.getMdsId());
                        count++;
                    }
                    log.info("========================================");
                }
            }

        } catch (Exception e) {
            log.error("Exception-", e);
        } finally {
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                        if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                            log.error("Pool did not terminate");
                        }
                    }
                } catch (InterruptedException ie) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("END.");       
    }

    
    /**
     * Normal Modem Command
     * 
     */
    private void normalModemCommandStart(EMnVCommandType commandType, String opCode, Object param) {
        try {
            springInit();
            
            txmanager = (JpaTransactionManager)ctx.getBean("transactionManager");
            txstatus = txmanager.getTransaction(null);
            
            /*
             * Code 생성
             */
            CodeDao codeDao = DataUtil.getBean(CodeDao.class);
            Code operationCode = codeDao.getCodeIdByCodeObject(opCode); 
            Code targetTypeCode = codeDao.getCodeIdByCodeObject("1.2.1.201"); // LTE
            
            /*
             * Modem Search
             */
            List<Modem> mList = new LinkedList<Modem>();
            ModemDao modemDao = DataUtil.getBean(ModemDao.class);
            Set<Condition> condition = new HashSet<Condition>();
            condition.add(new Condition("deviceSerial", targetList.toArray(), null, Restriction.IN));
            condition.add(new Condition("deviceSerial", null, null, Restriction.ORDERBY));
            mList = modemDao.findByConditions(condition);
            
            txmanager.commit(txstatus);
            
            if (mList == null || mList.size() <= 0) {
                log.info("Modem List is null. please check your Modem list.");
            } else {
                Collection<EMnVCommandCallable> callList = new LinkedList<EMnVCommandCallable>();
                
                for (Modem m : mList) {
                    callList.add(new EMnVCommandCallable(commandType, operationCode, targetTypeCode, m, param));
                }
                
                // Excute Job.
                executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, new LinkedBlockingQueue<Runnable>());
                List<Future<Map<String, String>>> futureL = executor.invokeAll(callList, 3, TimeUnit.MINUTES);  // tasks, long timeout, TimeUnit unit

                TimeUnit.SECONDS.sleep(SMS_RESULT_WAIT_SECOND);
                for (Future<Map<String, String>> resultFuture : futureL) {
                    Map<String, String> ht = resultFuture.get(AWAIT_TIME_OUT, AWAIT_TIME_OUT_TIME_UNIT); // the maximum time to wait
                    if (resultFuture.isDone()) {
                        if (ht.containsKey("error_create") || ht.containsKey("FAIL") || ht.containsKey("ERROR") || ht.containsKey("STATUS")) { // excute Fail.
                            failList.add(ht);
                        } else { // excute Success.
                            successList.add(ht);
                        }
                    } else if (resultFuture.isCancelled()) {
                        log.info("###### Future is Cancelled ==> " + ht.get("TARGET"));
                        failList.add(ht);
                    } else {
                        Map<String, String> unknownCancelMap = new HashMap<String, String>();
                        unknownCancelMap.put("ERROR", resultFuture.toString());
                        failList.add(unknownCancelMap);
                    }
                }

                /**
                 * Logging
                 */
                log.info(" ");
                log.info("=========== EXCUTE SUCCESS LIST ({}) ==========", successList.size());
                int count = 1;
                if (successList != null && 0 < successList.size()) {
                    for (Map<String, String> sMap : successList) {
                        log.info("{}. {}", count, sMap.toString());
                        count++;
                    }
                } else {
                    log.info("There is no list of successful.");
                }
                log.info("========================================");
                
                count = 1;
                log.info("=========== EXCUTE FAIL LIST ({}) ==========", failList.size());
                if (failList != null && 0 < failList.size()) {
                    for (Map<String, String> fMap : failList) {
                        log.info("{}. {}", count, fMap.toString());
                        count++;                            
                    }
                } else {
                    log.info("There is no list of failure.");
                }
                log.info("========================================");
            }

        } catch (NoSuchFileException ne){
            log.error("Can not found FirmWare File. - ", ne);
        } catch (IOException ie){ 
            log.error("File access Error - {}", ie);
        } catch (Exception e) {
            log.error("Exception-", e);
        } finally {
            if (executor != null) {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                        if (!executor.awaitTermination(AWAIT_TIME_OUT, TimeUnit.SECONDS)) {
                            log.error("Pool did not terminate");
                        }
                    }
                } catch (InterruptedException ie) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("END.");       
    }

    private String makeFirmwareDirectoryForEMnV(String homePath, String subPath, String fileName, String ext, boolean deletable) {
        File file = null;
        StringBuilder firmwareDir = new StringBuilder();
        firmwareDir.append(homePath);
        firmwareDir.append("/");
        firmwareDir.append(subPath);
        
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
            return makeFirmwareDirectoryForEMnV(homePath, subPath, fileName, ext, deletable);
        }
        return file.getPath();
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            String nextArg = args[i];

            if(nextArg.startsWith("-commandType")){
                commandType = EMnVCommandType.getItem(args[i + 1]);
            }else if(nextArg.startsWith("-interval")){
                interval = (args[i + 1]).equals("${interval}") ? "5" : args[i + 1];
            } else if (nextArg.startsWith("-param1")) {
                param1 = (args[i + 1]).equals("${param1}") ? null : args[i + 1];
            } else if (nextArg.startsWith("-param2")) {
                param2 = (args[i + 1]).equals("${param2}") ? null : args[i + 1];
            } else {
                log.error("Please input Options.");
                log.error("ex) -f [file name] -commandType [command name] -param1 [first parameter] -param2 [second parameter]");
                System.exit(0);
            }
            
            if(commandType == null || commandType.equals("")){
                log.error("Please Check Command name.");
                System.exit(0);
            }
        }

        EMnVCommandBatch batch = new EMnVCommandBatch();
        batch.execute();
    }

}



/**
 * EMnV Command Callable
 * @author nuri
 *
 */
class EMnVCommandCallable implements Callable<Map<String, String>> {
    private static Logger logger = LoggerFactory.getLogger(EMnVCommandCallable.class);
    private String loginId = "admin";
    EMnVCommandType commandType;
    Code operationCode;   
    Code targetTypeCode;  // EnergyMeter , LTE
    Object target;
    Object params;

    public EMnVCommandCallable(EMnVCommandType commandType, Code operationCode, Code targetTypeCode, Object target, Object params) {
        this.commandType = commandType;
        this.operationCode = operationCode;
        this.targetTypeCode = targetTypeCode;
        this.target = target;
        this.params = params;
    }

    @Override
    public Map<String, String> call() throws Exception {
        Map<String, String> result = new LinkedHashMap<String, String>();
        
        JpaTransactionManager txmanager = (JpaTransactionManager)DataUtil.getBean("transactionManager");
        TransactionStatus txstatus = txmanager.getTransaction(null);
        
        try {
            switch (commandType) {
            case OTA:
                result = ota();
                break;
            case SERVER_IP:
                result = normalModemCommand("server_ip");
                break;
            case SERVER_PORT:
                result = normalModemCommand("server_port");
                break;
            case LP_INTERVAL:
                result = normalModemCommand("cmdLPInterval");
                break;
            case HW_RESET_INTERVAL:
                result = normalModemCommand("hw_reset_interval");
                break;
            case NV_RESET:
                result = normalModemCommand(null);
                break;
            case M_NUMBER:
//              operationCode = codeDao.getCodeIdByCodeObject("8.2.23"); // Mobile number Read
                break;
            case HW_RESET:
                result = normalModemCommand(null);
                break;
            case EVENT_LOG:
//              operationCode = codeDao.getCodeIdByCodeObject("8.2.5"); // Event Log
                break;
            case KEY_CHANGE:
                result = normalModemCommand("key");
                break;  
            case ON_DEMAND:
                result = selectiveLP();
                break;  
            case METER_TIMESYNC:
                result = normalMeterCommand();
                break;  
            case METER_SCAN:
                result = normalMeterCommand();
                break;  
            case INVERTER_INFO:
//              operationCode = codeDao.getCodeIdByCodeObject("8.1.33"); // Inverter Information
                break;                  
            case INVERTER_SETUP:
//              operationCode = codeDao.getCodeIdByCodeObject("8.1.34"); // Inverter Setup
                break;  
            case UNKNOWN:
                
                break;  
            default:
                break;
            }
            
            txmanager.commit(txstatus);
        } catch (NullPointerException e) {
            if (txstatus != null) {
                txmanager.rollback(txstatus);
            }
            result.put("error_create", e.getMessage());
        } catch (Exception e) {
            if (txstatus != null) {
                txmanager.rollback(txstatus);
            }
            result.put("error_create", e.getMessage());
        } finally{
            txstatus = null;
            txmanager = null;
        }
        
        return result;
    }
    
    private Map<String, String> ota() throws Exception{
        logger.debug("OTA Starting...");
        
        @SuppressWarnings("unchecked")
        HashMap<String, String> paramMap = (HashMap<String, String>) params;
        logger.debug("OTA Parameter = ", paramMap.toString());
        
        Modem modem = (Modem)target;
        Supplier supplier = modem.getSupplier();
        
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("TARGET", modem.getDeviceSerial());
        
        // 모뎀의 Protocol Type이 SMS인경우만 처리
        if (modem != null && modem.getProtocolType() == Protocol.SMS) {
            String mobileNo = modem.getDeviceSerial();
            String cmd = commandType.getCmd();

            /*
             * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
             * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
             */
            AsyncCommandLogDao acDao = DataUtil.getBean(AsyncCommandLogDao.class);
            Long maxTrId = acDao.getMaxTrId(mobileNo); // EMnV 시스템은 MCUID에 모바일넘버가 들어가 있음.
            String trnxId;
            if (maxTrId != null) {
                trnxId = String.format("%08d", maxTrId.intValue() + 1);
            } else {
                trnxId = "00000001";
            }

            try {
                saveAsyncCommandForEMnV(modem.getDeviceSerial(), Long.parseLong(trnxId), cmd, paramMap, TimeUtil.getCurrentTime());
            } catch (Exception e) {
                logger.debug("AsyncCommand Save Fail", e.toString());
            }

            String messageId = sendSMSForKEMCO(trnxId, mobileNo, cmd);
            ResultStatus status = ResultStatus.SUCCESS;
            String rtnStr = "";

            if (messageId.equals("fail")) {
                status = ResultStatus.FAIL;
                rtnStr = "FAIL: Modem=" + mobileNo + " Send SMS Fail ["+cmd+"]";
                result.put("FAIL", rtnStr);
            } else if (messageId.equals("error")) {
                status = ResultStatus.FAIL;
                rtnStr = "ERROR: Modem=" + mobileNo + " Communication Fail ["+cmd+"]";
                result.put("ERROR", rtnStr);
            } else {
                rtnStr = "SUCCESS: Modem=" + mobileNo + " Send SMS Result ["+cmd+"]";
                result.put("SUCCESS", rtnStr);
            }

            if (operationCode != null) {
                saveOperationLog(supplier, targetTypeCode, mobileNo, loginId, operationCode, status.getCode(), rtnStr);
            }
        } else {
            result.put("STATUS", "Wrong Modem or Wrong Protocol Type.");
        }

        logger.debug("OTA result ==> " + result.toString());
        
        return result;
    }
    
    
    private Map<String, String> selectiveLP() throws Exception{
        String lpIndex = String.valueOf(params);
        logger.debug("Selective LP [{}] Starting...", lpIndex);
        
        Meter meter = (Meter)target;
        String mdsid = meter.getMdsId();
        ModemDao modemDao = DataUtil.getBean(ModemDao.class);
        Modem modem = modemDao.get(meter.getModemId());
        Supplier supplier = meter.getSupplier();

        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("TARGET", mdsid);
        
        if (supplier == null) {
            OperatorDao operatorDao = DataUtil.getBean(OperatorDao.class);
            Operator operator = operatorDao.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        // 모뎀의 Protocol Type이 SMS인경우만 처리
        else if (modem != null && modem.getProtocolType() == Protocol.SMS) {
            String mobileNo = modem.getDeviceSerial();
            String cmd = commandType.getCmd();

            /* HDLC Dest Address 생성
             * 
                Buffer[0] = 0x01 << 1;  // Dest Address
                Buffer[1] = (UCHAR)((((미터 끝에 두자리 + 0x10) << 1) & 0x00FF) + 1);// Dest Address
             */
            String idTail = mdsid.substring(mdsid.length() - 2, mdsid.length());
            byte[] destHDLCAddress = new byte[2];
            destHDLCAddress[0] = 0x01 << 1;
            destHDLCAddress[1] = (byte) ((((DataUtil.getByteToInt(Integer.parseInt(idTail)) + 0x10) << 1) & 0x00FF) + 1);// Dest Address

            /*
             * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
             * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
             */
            AsyncCommandLogDao acDao = DataUtil.getBean(AsyncCommandLogDao.class);
            Long maxTrId = acDao.getMaxTrId(mobileNo); // EMnV 시스템은 MCUID에 모바일넘버가 들어가 있음.
            String trnxId;
            if (maxTrId != null) {
                trnxId = String.format("%08d", maxTrId.intValue() + 1);
            } else {
                trnxId = "00000001";
            }

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("hdlc_address", Hex.decode(destHDLCAddress)); // HDLC dest Address 
            paramMap.put("lp_idx", lpIndex); // LP Index

            try {
                saveAsyncCommandForEMnV(modem.getDeviceSerial(), Long.parseLong(trnxId), cmd, paramMap, TimeUtil.getCurrentTime());
            } catch (Exception e) {
                logger.debug("AsyncCommand Save Fail", e.toString());
            }

            String messageId = sendSMSForKEMCO(trnxId, mobileNo, cmd);
            ResultStatus status = ResultStatus.SUCCESS;
            String rtnStr = "";

            if (messageId.equals("fail")) {
                status = ResultStatus.FAIL;
                rtnStr = "FAIL: Meter=" + mdsid + ", Modem=" + mobileNo + " Send SMS Fail ["+cmd+"] LPIndex-" + lpIndex;
                result.put("FAIL", rtnStr);
            } else if (messageId.equals("error")) {
                status = ResultStatus.FAIL;
                rtnStr = "ERROR: Meter=" + mdsid + ", Modem=" + mobileNo + " Communication Fail ["+cmd+"] LPIndex-" + lpIndex;
                result.put("ERROR", rtnStr);
            } else {
                status = ResultStatus.SUCCESS;
                rtnStr = "SUCCESS: Meter=" + mdsid + ", Modem=" + mobileNo + " Send SMS Success ["+cmd+"] LPIndex-" + lpIndex;
                result.put("SUCCESS", rtnStr);
            }

            if (operationCode != null) {
                saveOperationLog(supplier, targetTypeCode, mdsid, loginId, operationCode, status.getCode(), rtnStr);
            }
        } else {
            result.put("STATUS", "Wrong Modem or Wrong Protocol Type.");
        }

        logger.debug("Selective LP result ==> " + result.toString());
        
        return result;
    }
    
    private Map<String, String> normalMeterCommand() throws Exception{
        logger.debug("{} Starting...", commandType.name());
        
        Meter meter = (Meter)target;
        String mdsid = meter.getMdsId();
        ModemDao modemDao = DataUtil.getBean(ModemDao.class);
        Modem modem = modemDao.get(meter.getModemId());
        Supplier supplier = meter.getSupplier();

        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("TARGET", mdsid);
        
        if (supplier == null) {
            OperatorDao operatorDao = DataUtil.getBean(OperatorDao.class);
            Operator operator = operatorDao.getOperatorByLoginId(loginId);
            supplier = operator.getSupplier();
        }

        // 모뎀의 Protocol Type이 SMS인경우만 처리
        else if (modem != null && modem.getProtocolType() == Protocol.SMS) {
            String mobileNo = modem.getDeviceSerial();
            String cmd = commandType.getCmd();

            /* HDLC Dest Address 생성
             * 
                Buffer[0] = 0x01 << 1;  // Dest Address
                Buffer[1] = (UCHAR)((((미터 끝에 두자리 + 0x10) << 1) & 0x00FF) + 1);// Dest Address
             */
            String idTail = mdsid.substring(mdsid.length() - 2, mdsid.length());
            byte[] destHDLCAddress = new byte[2];
            destHDLCAddress[0] = 0x01 << 1;
            destHDLCAddress[1] = (byte) ((((DataUtil.getByteToInt(Integer.parseInt(idTail)) + 0x10) << 1) & 0x00FF) + 1);// Dest Address

            /*
             * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
             * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
             */
            AsyncCommandLogDao acDao = DataUtil.getBean(AsyncCommandLogDao.class);
            Long maxTrId = acDao.getMaxTrId(mobileNo); // EMnV 시스템은 MCUID에 모바일넘버가 들어가 있음.
            String trnxId;
            if (maxTrId != null) {
                trnxId = String.format("%08d", maxTrId.intValue() + 1);
            } else {
                trnxId = "00000001";
            }

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("hdlc_address", Hex.decode(destHDLCAddress)); // HDLC dest Address 

            try {
                saveAsyncCommandForEMnV(modem.getDeviceSerial(), Long.parseLong(trnxId), cmd, paramMap, TimeUtil.getCurrentTime());
            } catch (Exception e) {
                logger.debug("AsyncCommand Save Fail", e.toString());
            }

            String messageId = sendSMSForKEMCO(trnxId, mobileNo, cmd);
            ResultStatus status = ResultStatus.SUCCESS;
            String rtnStr = "";

            if (messageId.equals("fail")) {
                status = ResultStatus.FAIL;
                rtnStr = "FAIL: Meter=" + mdsid + ", Modem=" + mobileNo + " Send SMS Fail ["+cmd+"]";
                result.put("FAIL", rtnStr);
            } else if (messageId.equals("error")) {
                status = ResultStatus.FAIL;
                rtnStr = "ERROR: Meter=" + mdsid + ", Modem=" + mobileNo + " Communication Fail ["+cmd+"]";
                result.put("ERROR", rtnStr);
            } else {
                status = ResultStatus.SUCCESS;
                rtnStr = "SUCCESS: Meter=" + mdsid + ", Modem=" + mobileNo + " Send SMS Success ["+cmd+"]";
                result.put("SUCCESS", rtnStr);
            }

            if (operationCode != null) {
                saveOperationLog(supplier, targetTypeCode, mdsid, loginId, operationCode, status.getCode(), rtnStr);
            }
        } else {
            result.put("STATUS", "Wrong Modem or Wrong Protocol Type.");
        }

        logger.debug("{} result ==> {}", commandType.name(), result.toString());
        
        return result;
    }

    
    private Map<String, String> normalModemCommand(String paramName) throws Exception{
        logger.debug("{} Starting... Command Parameter={}", commandType.name(), params);
        
        Modem modem = (Modem)target;
        Supplier supplier = modem.getSupplier();
        
        Map<String, String> result = new LinkedHashMap<String, String>();
        result.put("TARGET", modem.getDeviceSerial());
        
        // 모뎀의 Protocol Type이 SMS인경우만 처리
        if (modem != null && modem.getProtocolType() == Protocol.SMS) {
            String mobileNo = modem.getDeviceSerial();
            String cmd = commandType.getCmd();

            /*
             * 서버에서 모뎀으로 SMS를 보낸뒤 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지
             * 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
             */
            AsyncCommandLogDao acDao = DataUtil.getBean(AsyncCommandLogDao.class);
            Long maxTrId = acDao.getMaxTrId(mobileNo); // EMnV 시스템은 MCUID에 모바일넘버가 들어가 있음.
            String trnxId;
            if (maxTrId != null) {
                trnxId = String.format("%08d", maxTrId.intValue() + 1);
            } else {
                trnxId = "00000001";
            }
            
            Map<String, String> paramMap = null;
            // 파라미터가 없는 Command도 있음.
            if(paramName != null){
                paramMap = new HashMap<String, String>();
                paramMap.put(paramName, String.valueOf(params));                
            }

            try {
                saveAsyncCommandForEMnV(modem.getDeviceSerial(), Long.parseLong(trnxId), cmd, paramMap, TimeUtil.getCurrentTime());
            } catch (Exception e) {
                logger.debug("AsyncCommand Save Fail", e.toString());
            }

            String messageId = sendSMSForKEMCO(trnxId, mobileNo, cmd);
            ResultStatus status = ResultStatus.SUCCESS;
            String rtnStr = "";

            if (messageId.equals("fail")) {
                status = ResultStatus.FAIL;
                rtnStr = "FAIL: Modem=" + mobileNo + " Send SMS Fail ["+cmd+"]";
                result.put("FAIL", rtnStr);
            } else if (messageId.equals("error")) {
                status = ResultStatus.FAIL;
                rtnStr = "ERROR: Modem=" + mobileNo + " Communication Fail ["+cmd+"]";
                result.put("ERROR", rtnStr);
            } else {
                rtnStr = "SUCCESS: Modem=" + mobileNo + " Send SMS Result ["+cmd+"]";
                result.put("SUCCESS", rtnStr);
            }

            if (operationCode != null) {
                saveOperationLog(supplier, targetTypeCode, mobileNo, loginId, operationCode, status.getCode(), rtnStr);
            }
        } else {
            result.put("STATUS", "Wrong Modem or Wrong Protocol Type.");
        }

        logger.debug("{} result ==> {}", commandType.name(), result.toString());
        
        return result;
    }
    
    public void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar today = Calendar.getInstance();
        String currDateTime = sdf.format(today.getTime());

        OperationLogDao operationLogDao = DataUtil.getBean(OperationLogDao.class);
        OperationLog opLog = new OperationLog();

        opLog.setOperatorType(0);//system
        opLog.setOperationCommandCode(operationCode);
        opLog.setYyyymmdd(currDateTime.substring(0, 8));
        opLog.setHhmmss(currDateTime.substring(8, 14));
        opLog.setYyyymmddhhmmss(currDateTime);
        opLog.setDescription("");
        opLog.setErrorReason(errorReason);
        opLog.setResultSrc("");
        opLog.setStatus(status);
        opLog.setTargetName(targetName);
        opLog.setTargetTypeCode(targetTypeCode);
        opLog.setUserId(userId);
        opLog.setSupplier(supplier);

    //  logger.debug("operation log : {}", opLog.toString()); 이거 주석풀면 에러남! ㅡ.ㅡ;;;

        operationLogDao.add(opLog);
    }

    private void saveAsyncCommandForEMnV(String deviceSerial, Long trId, String cmd, Map<String, String> param, String currentTime) {
        AsyncCommandLogDao acDao = DataUtil.getBean(AsyncCommandLogDao.class);
        AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
        asyncCommandLog.setTrId(trId);
        asyncCommandLog.setMcuId(deviceSerial);
        asyncCommandLog.setDeviceType(McuType.MMIU.name());
        asyncCommandLog.setDeviceId(deviceSerial);
        asyncCommandLog.setCommand(cmd);
        asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
        asyncCommandLog.setState(1);
        asyncCommandLog.setOperator(OperatorType.SYSTEM.name());
        asyncCommandLog.setCreateTime(currentTime);
        asyncCommandLog.setRequestTime(currentTime);
        asyncCommandLog.setLastTime(null);

        acDao.add(asyncCommandLog);

        Integer num = 0;
        if (param != null && param.size() > 0) {
            //parameter가 존재할 경우.
            AsyncCommandParamDao paramDao = DataUtil.getBean(AsyncCommandParamDao.class);
            Integer maxNum = paramDao.getMaxNum(deviceSerial, trId);

            if (maxNum != null) {
                num = maxNum + 1;
            }

            Iterator<String> iter = param.keySet().iterator();
            while (iter.hasNext()) {
                String key = iter.next();

                AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
                asyncCommandParam.setMcuId(deviceSerial);
                asyncCommandParam.setNum(num);
                asyncCommandParam.setParamType(key);
                asyncCommandParam.setParamValue((String) param.get(key));
                asyncCommandParam.setTrId(trId);

                paramDao.add(asyncCommandParam);
                num += 1;
            }
        }
    }

    private String sendSMSForKEMCO(String trnxId, String mobileNo, String command) {
        String result = "";

        // SMS Link Request Frame Format - Seperator
        String seperator = "ENJ"; // Length - 3
        String encription = "0"; // Length - 1
        String trId = trnxId; // Length - 8
        String serverIp = ""; // Length - N
        String serverPort = ""; // Length - N
        String smsMsg = "";

        try {
            Properties prop = new Properties();
            prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
            String smsClassPath = prop.getProperty("kemco.smsClassPath");
            serverIp = prop.getProperty("kemco.server.sms.serverIpAddr") == null ? "" : prop.getProperty("kemco.server.sms.serverIpAddr").trim();
            serverPort = prop.getProperty("kemco.server.sms.serverPort") == null ? "" : prop.getProperty("kemco.server.sms.serverPort").trim();
            encription = prop.getProperty("kemco.server.sms.encription") == null ? "false" : prop.getProperty("kemco.server.sms.encription").trim();

            if (encription.equals("true")) {
                encription = "1";
            } else {
                encription = "0";
            }

            if ("".equals(serverIp) || "".equals(serverPort)) {
                result = "error";
                logger.debug("========>>> [{}] Message Send Error: Invalid Ip Address or port!", command);
            } else {
                smsMsg = seperator + encription + trId + serverIp + "," + serverPort;
                SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
                Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
                result = (String) m.invoke(obj, mobileNo.replace("-", "").trim(), smsMsg, prop);
                
                logger.debug("========>>> [{}] Message Send={}, RESULT={}", new Object[]{command, smsMsg, result});
            }
        } catch (Exception e) {
            logger.debug("Exception-", e);
        }
        return result;
    }

}