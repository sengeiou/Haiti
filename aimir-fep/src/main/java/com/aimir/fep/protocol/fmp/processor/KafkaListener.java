package com.aimir.fep.protocol.fmp.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.mina.core.buffer.IoBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerAwareMessageListener;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.Interface;
import com.aimir.fep.logger.snowflake.SnowflakeGeneration;
import com.aimir.fep.meter.parser.plc.PLCData;
import com.aimir.fep.meter.parser.plc.PLCDataFrame;
import com.aimir.fep.protocol.fmp.common.SlideWindow;
import com.aimir.fep.protocol.fmp.common.SlideWindow.COMPRESSTYPE;
import com.aimir.fep.protocol.fmp.datatype.WORD;
import com.aimir.fep.protocol.fmp.frame.GeneralDataFrame;
import com.aimir.fep.protocol.fmp.frame.ServiceDataFrame;
import com.aimir.fep.protocol.fmp.frame.service.DFData;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.frame.service.EventData_1_2;
import com.aimir.fep.protocol.fmp.frame.service.MDData;
import com.aimir.fep.protocol.fmp.frame.service.ServiceData;
import com.aimir.fep.protocol.fmp.log.CommLogger;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Message;
import com.aimir.model.device.CommLog;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

public class KafkaListener {
	private static Logger log = LoggerFactory.getLogger(KafkaListener.class);

	class KafkaListenerThread extends Thread {
		private String topicName;

		KafkaListenerThread(String topicName) {
			this.topicName = topicName;
		}

		@Override
		public void run() {
			MessageListenerContainer jcontainer = messageListenerContainer(topicName);
			jcontainer.setupMessageListener(new ConsumerAwareMessageListener<Integer, String>() {

				@Override
				public void onMessage(ConsumerRecord<Integer, String> record, Consumer<?, ?> consumer) {
					try {
						// convert json message to message
						StringJsonMessageConverter converter = new StringJsonMessageConverter();
						Message msg = (Message) converter.toMessage(record, null, consumer, Message.class).getPayload();
                        SnowflakeGeneration.setSeq(msg.getSequenceLog());
						CommLog commLog = makeCommLog(msg);
						try {
							processing(msg, commLog);
							commLog.setResult(CommonConstants.DefaultCmdResult.SUCCESS.getMessage());
						} catch (Exception e) {
							commLog.setResult(CommonConstants.DefaultCmdResult.FAILURE.getMessage());
							commLog.setErrorReason(e.getMessage());
							log.error("processing error - " + e.getMessage(), e);
						}
						// OPF-366 DF에서 생성한 MD에 대해서는 통신로그 저장을 안한다.
						if (msg.getRcvBytes() > 0) {
							CommLogger commLogger = DataUtil.getBean(CommLogger.class);
							commLogger.sendLog(commLog);
						}
					} catch (Exception e) {
						log.error("onMessage error - " + e.getMessage(), e);
					}finally {
                    	SnowflakeGeneration.deleteId();
					}

					log.debug("############################ [onMessage end] #################################");
					log.debug("");
					log.debug("");
				}
            });
            jcontainer.start();

			log.debug("### TopicName = " + topicName + ", Consumer is running? = " + jcontainer.isRunning());
		}
	}

	/**
	 * constructor
	 *
	 * @throws Exception
	 */
	public KafkaListener() throws Exception {
		if (Boolean.parseBoolean(FMPProperty.getProperty("kafka.enable"))) {
			new KafkaListenerThread(ProcessorHandler.SERVICE_EVENT_1_2).start();
			new KafkaListenerThread(ProcessorHandler.SERVICE_EVENT).start();
			new KafkaListenerThread(ProcessorHandler.SERVICE_MEASUREMENTDATA).start();
			new KafkaListenerThread(ProcessorHandler.SERVICE_DATAFILEDATA).start();
		}
	}

	public MessageListenerContainer messageListenerContainer(String groupName) {
		ConcurrentMessageListenerContainer<Integer, String> container = new ConcurrentMessageListenerContainer<Integer, String>(
				consumerFactory(groupName), new ContainerProperties(groupName));

		int concurrency = 1;
		if (groupName.equals(ProcessorHandler.SERVICE_MEASUREMENTDATA))
			concurrency = Integer.parseInt(FMPProperty.getProperty("MDProcessor.thread.poolSize"));
		else if (groupName.equals(ProcessorHandler.SERVICE_EVENT_1_2)
				|| groupName.equals(ProcessorHandler.SERVICE_EVENT))
			concurrency = Integer.parseInt(FMPProperty.getProperty("EventProcessor.thread.poolSize"));
		else if (groupName.equals(ProcessorHandler.SERVICE_DATAFILEDATA))
			concurrency = Integer.parseInt(FMPProperty.getProperty("DFProcessor.thread.poolSize"));

		container.setConcurrency(concurrency);

		log.debug("## Consumer Count = " + concurrency);

		return container;
	}

	public ConsumerFactory<Integer, String> consumerFactory(String groupName) {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupName);
		// props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
		// Integer.parseInt(FMPProperty.getProperty("kafka.consumer.max.poll.records","500")));
		// props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG,
		// Integer.parseInt(FMPProperty.getProperty("kafka.consumer.max.poll.interval.ms","60"))*1000);
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaProducer.brokerAddress);
		// props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
		// Boolean.parseBoolean(FMPProperty.getProperty("kafka.consumer.auto.commit")));
		// props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
		// Integer.parseInt(FMPProperty.getProperty("kafka.consumer.auto.commit.interval")));
		// props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 15000);
		// props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 4*1024*1024);
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		// props.put("isolation.level", "read_commited");
		// props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
		// "org.apache.kafka.clients.consumer.RoundRobinAssignor");

		// simhanger 수정
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
				Boolean.parseBoolean(FMPProperty.getProperty("kafka.consumer.auto.commit", "true")));
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, String.valueOf(
				(Integer.parseInt(FMPProperty.getProperty("kafka.consumer.auto.commit.interval", "5")) * 1000))); // default
																													// 5s
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,
				String.valueOf((Integer.parseInt(FMPProperty.getProperty("session.timeout.ms", "10")) * 1000))); // default
																													// 10s
		props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG,
				String.valueOf((Integer.parseInt(FMPProperty.getProperty("heartbeat.interval.ms", "3")) * 1000))); // default
																													// 3s

		log.debug("## Kafka Listener properties => " + StringUtil.objectToJsonString(props));

		return new DefaultKafkaConsumerFactory<>(props);
	}

	private void processing(Message msg, CommLog commLog) throws Exception {
		log.debug("processing msg = " + msg.toString());
        if (msg.getDataType().equals(ProcessorHandler.SERVICE_DATA)) {
            receivedServiceDataFrame(msg, commLog);
            if (msg.getFilename() != null && !"".equals(msg.getFilename())) {

                Path file = new File(msg.getFilename()).toPath();
                try {
                	Files.delete(file);
					log.debug("[ProcessorHandler.SERVICE_DATA] DELETE File[" + msg.getFilename() + "]");
                } catch (IOException x) {
					log.error("[ProcessorHandler.SERVICE_DATA] Delete failed. file[" + msg.getFilename()
							+ "] exception => " + x.getMessage(), x);
				}
			}
		} else if (msg.getDataType().equals(ProcessorHandler.SERVICE_MEASUREMENTDATA)) {
            MDProcessor mp = DataUtil.getBean(MDProcessor.class);
            MDData sd = new MDData();
            sd.setCnt(new WORD(1));
            // byte[] b = new byte[msg.getData().length - 2];
            // System.arraycopy(msg.getData(), 2, b, 0, b.length);
            sd.setMdData(msg.getData());
            sd.setNS(msg.getNameSpace());
            sd.setIpAddr(msg.getSenderIp());
            sd.setMcuId(msg.getSenderId());
            mp.processing(sd, commLog);
            if (msg.getFilename() != null && !"".equals(msg.getFilename())) {

                Path file = new File(msg.getFilename()).toPath();
                try {
                	Files.delete(file);
					log.debug("[ProcessorHandler.SERVICE_MEASUREMENTDATA] DELETE File[" + msg.getFilename() + "]");
                } catch (IOException x) {
					log.warn("[ProcessorHandler.SERVICE_MEASUREMENTDATA] Deletion failed " + msg.getFilename());
                } 
            }
        }
        else if (msg.getDataType().equals(ProcessorHandler.SERVICE_PLC)) {
            receivedPLCDataFrame(msg, commLog);
        }
        else if (msg.getDataType().equals(ProcessorHandler.SERVICE_DATAFILEDATA)) {
            DFProcessor dp = DataUtil.getBean(DFProcessor.class);
            dp.processing(msg.getFilename(), commLog);
        }
    }
    
	private void receivedServiceDataFrame(Message msg, CommLog commLog) throws Exception {
        String nameSpace = msg.getNameSpace();
        String ipaddr = msg.getSenderIp();
        IoBuffer buf = IoBuffer.wrap(msg.getData());
        ServiceDataFrame frame = (ServiceDataFrame)GeneralDataFrame.decode(nameSpace, buf);
        
        ServiceData sd = ServiceData.decode(nameSpace, frame, ipaddr);
        
        if(sd != null)
        {
            log.debug("\nServiceData :" + sd.getType() );
            log.debug("\nRECEIVED SERVICE DATA \n"+sd);
            // log.debug(sd);
            if (sd instanceof EventData) {
                EventProcessor ep = DataUtil.getBean(EventProcessor.class);
                ep.processing(sd, commLog);
            }
            else if (sd instanceof EventData_1_2) {
                EventProcessor_1_2 ep_1_2 = DataUtil.getBean(EventProcessor_1_2.class);
                ep_1_2.processing(sd, commLog);
            }
            else if (sd instanceof DFData) {
                DFProcessor dp = DataUtil.getBean(DFProcessor.class);
                dp.processing(sd, commLog);
            }
            else if (sd instanceof MDData) {
                MDProcessor mp = DataUtil.getBean(MDProcessor.class);
                mp.processing(sd, commLog);
            }
        }
        else {
            log.debug("ServiceData is null");
        }
    }

    private CommLog makeCommLog(Message msg) {
        CommLog commLog = new CommLog();
        
        commLog.setSenderIp(msg.getSenderIp());
        commLog.setSenderId(msg.getSenderId());
        commLog.setReceiverId(msg.getReceiverId());
        commLog.setReceiverTypeCode(CommonConstants.getSenderReceiver("1")); // FEP
        commLog.setSendBytes((int)msg.getSendBytes()); //ENQ+ACK
        commLog.setRcvBytes((int)msg.getRcvBytes());//included EOT that received
        commLog.setInterfaceCode(CommonConstants.getInterface(Interface.IF4.name()));
        commLog.setReceiver(System.getProperty("fepName"));
        // Communication Success
        commLog.setCommResult(1);
        log.debug("startTime["+commLog.getStartTime()+"] endTime["+commLog.getEndTime()+"]");
        try {
        	commLog.setStartDateTime(msg.getStartDateTime());
            commLog.setStartDate(msg.getStartDateTime().substring(0,8));
            commLog.setStartTime(msg.getStartDateTime().substring(8,14));
            commLog.setEndTime(msg.getEndDateTime());
            
            long startLongTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getStartDateTime()).getTime();
            long endLongTime = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(commLog.getEndTime()).getTime();
            
			if (endLongTime - startLongTime > 0) {
				commLog.setTotalCommTime((int) (endLongTime - startLongTime));
			} else {
				commLog.setTotalCommTime(0);
			}
		} catch (Exception e) {
			log.error("make comm log warning." + e.getMessage(), e);
		}
        return commLog;
    }
    
    /**
     * @param session
     * @param pdf
     * @throws Exception
     */
	private void receivedPLCDataFrame(Message msg, CommLog commLog) throws Exception {
        IoBuffer buf = IoBuffer.wrap(msg.getData());
        PLCDataFrame frame = PLCDataFrame.decode(buf);
        String ipaddr = msg.getSenderIp();
        ipaddr = ipaddr.substring(ipaddr.indexOf("/")+1, ipaddr.indexOf(":"));
        PLCData pd = PLCData.decode(frame, ipaddr);
    }
    
    private String saveSlideWindow(byte[] bx) {
        FileOutputStream fos = null;

        String fileName = null;
        try {
            int compressTypeCode = DataUtil.getIntToByte(bx[0]);
            COMPRESSTYPE compressType = SlideWindow.getCompressType(compressTypeCode);
            fileName = getFileName(compressType.getName());

            fos = new FileOutputStream(fileName);

            int off = 0;
            // 압축을 하지 않은 것은 압축유형(1), 길이(4)를 제거하고 생성한다.
            if (compressType == COMPRESSTYPE.DAT)
                off = 5;

            log.debug("CompressType:"+compressType.getName()+" compress code=["+compressTypeCode+"]");
            log.debug("Compress Header:"+Hex.decode(DataUtil.select(bx, 0, 13)));
            fos.write(bx, off, bx.length-off);
		} catch (Exception ex) {
			log.error("Save slide error - " + ex.getMessage(), ex);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
				}
            }
        }
        
        return fileName;
    }

	private String getFileName(String compressType) throws Exception {
		File file = new File(FMPProperty.getProperty("protocol.slidewindow.dir"));
		if (!file.exists()) {
			file.mkdirs();
		}

		String fileName = null;
		while (fileName == null || file.exists()) {
			fileName = file.getAbsolutePath() + File.separator + (new Date()).getTime() + "." + compressType;
			file = new File(fileName);
		}
		log.info(fileName);
		return fileName;
	}
}
