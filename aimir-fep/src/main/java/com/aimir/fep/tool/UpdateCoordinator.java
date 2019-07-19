package com.aimir.fep.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;

import com.aimir.dao.device.MCUDao;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MCUCodi;

@Service
public class UpdateCoordinator {
    private static Log log = LogFactory.getLog(UpdateCoordinator.class);
    
    @Autowired
    MCUDao mcuDao;    
	
    @Resource(name="transactionManager")
    JpaTransactionManager txmanager;	
    
    private String		_csvFile = "";
    private String		_execTime = "";
    
    public static void main(String[] args) {
        
		String dev =  "";
        if (args[1].length() > 0) {
        	dev = args[1];
        }
        
		if (dev.equals("DEV")) {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule-dev.xml" });
	        DataUtil.setApplicationContext(ctx);
	        UpdateCoordinator task = ctx.getBean(UpdateCoordinator.class);
	        log.info("======================== UpdateCoordinator start. ========================");
	        task.execute(args);
	        log.info("======================== UpdateCoordinator end. ========================");
		} else {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
	        DataUtil.setApplicationContext(ctx);
	        UpdateCoordinator task = ctx.getBean(UpdateCoordinator.class);
	        log.info("======================== UpdateCoordinator start. ========================");
	        task.execute(args);
	        log.info("======================== UpdateCoordinator end. ========================");
		}        

		System.exit(0);
        
    }
          
    public void execute(String[] args) {

		log.info("ARG_0[" + args[0] + "]");
 
        if (args[0].length() > 0) {
        	_csvFile = args[0];
        }
        
        try {
//    		String fileName1 = getPreffix(_csvFile) + "_result1.csv";
//    		String fileName2 = getPreffix(_csvFile) + "_result2.csv";
//    		File file = new File(fileName1);
//            if (file.exists()){
//            	file.delete();
//            }        	
//    		file = new File(fileName2);
//            if (file.exists()){
//            	file.delete();
//            }        	

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	        Date date = new Date();
        	_execTime=sdf.format(date).toString();
        	
        	List<String[]> csvDataList = readFileToList(_csvFile);        	
        	
        	for(String[] data: csvDataList) {
        		// DSO(0), DCU ID(1), Serial number(2), Network(3), DCU FW Ver.(4), 
        		// right_v4_eth(5), IPv4(6), IPv6(7), Coordinator ID(8), Coord. FW(9), Coord. BD(10), 
        		// Last Updated(11)
        		if ((data[4].contains("v1.0")) || 
        			(data[4].contains("v1.1"))) {
        			MCU mcu = mcuDao.get(data[1]);
        			if (mcu == null) {
	        			log.debug("DCU does not found.DCU[" + data[1] + "] CODI_ID[" + data[8] + "]");
	        			makeResultFile(data, "DCU does not found.");
        				continue;
        			}
	        		MCUCodi mcuc = mcu.getMcuCodi();
	        		if (mcuc == null){
	        			if ((data[8]== null) || (data[8].length()==0)) {
		        			log.debug("Coordinator ID does not specified.DCU[" + data[1] + "]");
		        			makeResultFile(data, "oordinator ID does not specified.");
	        				continue;
	        			}
	        			mcuc = new MCUCodi();
	        			mcuc.setMcu(mcu);
		    			mcuc.setCodiID(data[8]);
		    			double version = (Integer.valueOf(data[9].substring(0, 1))*10) +
		    					         (Integer.valueOf(data[9].substring(1, 2))) +
		    					         (Integer.valueOf(data[9].substring(2, 3))*0.1) +
		    					         (Integer.valueOf(data[9].substring(3, 4))*0.01);  
		    			mcuc.setCodiFwVer(String.valueOf(version));
		    			int build = Integer.decode("0x" + data[10]);
		    			mcuc.setCodiFwBuild(String.valueOf(build));
	
		        		mcu.setMcuCodi(mcuc);
		        		mcuDao.update_requires_new(mcu);        	
	        			log.debug("New registered.DCU[" + data[1] + "] CODI_ID[" + data[8] + "]");
	        			makeResultFile(data, "New registered.");
	        		} else {
	        			log.debug("Already exists.DCU[" + data[1] + "] CODI_ID[" + data[8] + "]");
	        			makeResultFile(data, "Already exists.");
	        		}

        		}
        		else {
        			log.debug("DCU FW Ver v1.2 or higher.DCU[" + data[1] + "] CODI_ID[" + data[8] + "]");        			
        			log.debug(data.toString());        			
        		}

        	}
        	
	       
	    }
        catch (Exception e) {
        	log.debug(e.getMessage());
        }
                
    }  

    private List<String[]> readFileToList(String filePath) throws Exception {
        File f = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(f));   
        List<String[]> list = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
        	if (line.substring(0, 1).equals("#") == false) {
        		String[] array = line.split(",", 0);
        		String[] data = array;
        		for (int i=0; i<array.length; i++) {
        			data[i]=array[i].replace("\"", "");
        		}
        		list.add(data);
        	}
        }    	
                
        br.close();
        return list;
    }

	synchronized private void makeResultFile(String[] data, String result){
		String fileName1 = getPreffix(_csvFile) + "_" + _execTime + "_result1.csv";
		String fileName2 = getPreffix(_csvFile) + "_" + _execTime + "_result2.csv";
		
        try{
        	File file = new File(fileName1) ;
        	file.createNewFile();
        	FileOutputStream fos = new FileOutputStream(fileName1, true);
        	OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        	BufferedWriter bw = new BufferedWriter(osw);
        	
        	StringBuffer buffer = new StringBuffer();
        
        	// Data
    		StringBuffer dataBuffer = new StringBuffer();

    		dataBuffer.append("\"" + data[0] + "\"" + ",");
    		dataBuffer.append("\"" + data[1] + "\"" + ",");
    		dataBuffer.append("\"" + data[2] + "\"" + ",");
    		dataBuffer.append("\"" + data[3] + "\"" + ",");
    		dataBuffer.append("\"" + data[4] + "\"" + ",");
    		dataBuffer.append("\"" + data[5] + "\"" + ",");
    		dataBuffer.append("\"" + data[6] + "\"" + ",");
    		dataBuffer.append("\"" + data[7] + "\"" + ",");
    		dataBuffer.append("\"" + data[8] + "\"" + ",");
    		dataBuffer.append("\"" + data[9] + "\"" + ",");
    		dataBuffer.append("\"" + data[10] + "\"" + ",");
    		dataBuffer.append("\"" + data[11] + "\"" );
    		
            bw.write(dataBuffer.toString());
        	bw.newLine();        	
        	bw.close();
        	
        	file = new File(fileName2) ;
        	file.createNewFile();
        	fos = new FileOutputStream(fileName2, true);
        	osw = new OutputStreamWriter(fos, "UTF-8");
        	bw = new BufferedWriter(osw);
        	
    		dataBuffer.append("," + "\"" + result + "\"" );
    		
            bw.write(dataBuffer.toString());
        	bw.newLine();        	
        	bw.close();
        	
        	
        }catch(Exception e){
            log.error("makeResultFile error - " + e, e);
        }
	}    

	public static String getPreffix(String fileName) {
	    if (fileName == null)
	        return null;
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(0, point);
	    } 
	    return fileName;
	}	
}
