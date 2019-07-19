package com.aimir.fep.tool;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Message;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

public class MQSend implements Runnable {
    private static Log log = LogFactory.getLog(MQSend.class);
    private String fileName = "";

    
    public String getFileName() {
		return fileName;
	}

	public void setFilePath(String fileName) {
		this.fileName = fileName;
	}

	public static void main(String[] args) {
    	String activemq = "localhost";
    	String filePath = "";
        int threadCount = 10;

    	if(args.length > 2 && !"".equals(args[0]) && args[0] != null) {
    		log.info("args["+args[0]+"]");
    		activemq = args[0];
    		filePath = args[1];
    		threadCount = Integer.parseInt(args[2]);
    	}    	
    	//String amqDomain = "org.apache.activemq";
    	//String runningQueue = null;
    	
    	//String queueName = "ServiceData.DFData";
    	//String mqIp = null;
    	
        //String filename = null;
        int i = 1;
		ApplicationContext ctx = new ClassPathXmlApplicationContext(new String[] { "/config/spring-fep-schedule.xml" });
		DataUtil.setApplicationContext(ctx);

		File dir = new File(filePath);
        if (!dir.exists()) {
            log.error("["+filePath+"]Directory not found");
            return;
        }
        File[] files = dir.listFiles();
        
        List<File> fileList = new ArrayList<File>();
        for (File f : files) {
        	if(f.getAbsolutePath().lastIndexOf("dat") != -1 
        			|| f.getAbsolutePath().lastIndexOf("zlib") != -1
        			|| f.getAbsolutePath().lastIndexOf("log") != -1){
                fileList.add(f);
        	}

        }
        
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File arg0, File arg1) {
                return (int)(arg0.lastModified() - arg1.lastModified());
            }
        });
        
		ExecutorService pool = Executors.newFixedThreadPool(threadCount);
		try
		{
		    for (File f : fileList.toArray(new File[0])) {
		        log.info("[" + (i++) + "/" + files.length +"] filename[" + f.getName() + "]");
		        MQSend mqsend = new MQSend();
		        mqsend.setFilePath(f.getAbsolutePath());
		        pool.execute(mqsend);
		    }
		    pool.awaitTermination(120, TimeUnit.SECONDS);
		} catch (Exception ex)
        {
        	pool.shutdown();
            log.error("failed ", ex);
        }
		finally {
			System.exit(0);
		}
    }

	@Override
	public void run() {

	    ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);        
	    boolean kafkaEnable = Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"));
	    //FileChannel inChannel = null;
	    //JmsTemplate jmsTemplate = DataUtil.getBean(JmsTemplate.class);      
	    try {
	        if(kafkaEnable){
	            if (fileName.lastIndexOf("dat") != -1 || fileName.lastIndexOf("zlib") != -1){
                	
                    //inChannel = new RandomAccessFile(fileName, "r").getChannel();
                    //MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
                    //byte[] data = new byte[(byte)inChannel.size()];
                    //buffer.get(data, 0, data.length);
                	Message message = new Message();
                    message.setFilename(fileName);                    
                    message.setSenderIp("localhost");
                    message.setReceiverIp("localhost");
                    message.setSenderId("1234");
                    message.setReceiverId("4321");
                    message.setSendBytes(0L);
                    message.setRcvBytes(0L);
                    message.setStartDateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    message.setEndDateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    message.setTotalCommTime(0);
                    message.setDataType(ProcessorHandler.SERVICE_DATAFILEDATA);
                    message.setNameSpace("SP");
                    //message.setData(data);
                    
                    handler.putServiceData(ProcessorHandler.SERVICE_DATAFILEDATA, message);
                } else if(fileName.lastIndexOf("log") != -1){
                	Message message = new Message();
                    message.setFilename(fileName);                    
                    message.setSenderIp("localhost");
                    message.setReceiverIp("localhost");
                    message.setSenderId("1234");
                    message.setReceiverId("4321");
                    message.setSendBytes(0L);
                    message.setRcvBytes(0L);
                    message.setStartDateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    message.setEndDateTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
                    message.setTotalCommTime(0);
                    message.setDataType(ProcessorHandler.SERVICE_MEASUREMENTDATA);
                    message.setNameSpace("SP");
                    message.setData(deserialze(fileName).getMdData());                    
                    handler.putServiceData(ProcessorHandler.SERVICE_MEASUREMENTDATA, message);
                }
                
        	}else{
                if (fileName.lastIndexOf("dat") != -1 || fileName.lastIndexOf("zlib") != -1){
                    handler.putServiceData(ProcessorHandler.SERVICE_DATAFILEDATA, fileName);
                }else if(fileName.lastIndexOf("log") != -1){
                	log.warn("Not support");
                	/*
                    jmsTemplate.send(ProcessorHandler.SERVICE_MEASUREMENTDATA, new MessageCreator() {
                        public javax.jms.Message createMessage(Session session) throws JMSException {
                            return session.createObjectMessage(filename);
                        }
                    });
                    */
                }
        	}

        }
        catch (Exception e) {
            log.error(e, e);
        } 
		
	}
	
    private byte[] readDataByFileChannel(String filename) throws Exception
    {
    	FileChannel inChannel = null;
        byte[] data = null;
        try {          
            inChannel = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
            data = new byte[(int) inChannel.size()];
            buffer.get(data, 0, data.length);

        }       
        finally {
            try {
                if(inChannel != null) {
                    inChannel.close();
                }
                //(new File(filename)).delete();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return data;
    }
    
	public MDData deserialze(String filename) {

		MDData mdData = null;
        ObjectInputStream ois = null;

		try {
			ois = new ObjectInputStream(new FileInputStream(filename));
			mdData = (MDData) ois.readObject();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
            if ( ois != null ) try{ois.close();} catch(Exception e) { }
        }

		return mdData;

	}

}
