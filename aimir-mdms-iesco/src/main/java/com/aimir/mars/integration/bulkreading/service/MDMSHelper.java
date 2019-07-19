package com.aimir.mars.integration.bulkreading.service;

import java.io.StringWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.mars.integration.bulkreading.xml.cim.MeterReadingsType;
import com.aimir.mars.integration.bulkreading.xml.cim.header.MessageHeaderType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs.ML;
import com.aimir.mars.integration.bulkreading.xml.cim.meterreading.*;

import com.aimir.mars.util.MarsProperty;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

import ch.iec.tc57._2011.enddeviceevents.Asset;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvent;
import ch.iec.tc57._2011.enddeviceevents.EndDeviceEvents;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsEventMessageType;
import ch.iec.tc57._2011.enddeviceeventsmessage.EndDeviceEventsPayloadType;
import ch.iec.tc57._2011.schema.message.EventMessage;
import ch.iec.tc57._2011.schema.message.EventMessageType;
import ch.iec.tc57._2011.schema.message.HeaderType;
import ch.iec.tc57._2011.schema.message.PayloadType;
import ch.iec.tc57._2011.schema.message.Verb;

public class MDMSHelper {

	private static final Logger log = LoggerFactory.getLogger(MDMSHelper.class);
	private static final String MDMS_XML_DIR = MarsProperty.getProperty("mdms.xml.path");
	private static final String REVISION = MarsProperty.getProperty("mdms.xml.revision","1.0.0");
	private static final String SOURCE = MarsProperty.getProperty("mdms.xml.source","HES");
	private static final String CONTEXT = MarsProperty.getProperty("mdms.xml.context","PRODUCTION");	
	
	private volatile static MDMSHelper instance = null;
	private volatile static JAXBContext context;

	public synchronized static MDMSHelper getInstance() {
		if (instance == null) {
			instance = new MDMSHelper();
		}
		return instance;
	}

	private MDMSHelper() {
		try {
			context = JAXBContext.newInstance(MeterReadingsType.class, EndDeviceEventsEventMessageType.class);
		} catch (JAXBException e1) {
			e1.printStackTrace();
		}
	}

	public JAXBContext getContext() {
		return context;
	}
	
	/**
	 * initMeasurementData 초기화
	 * @param initialMeasurementDataList
	 * @param mcIdN
	 * @param stDt
	 * @param enDt
	 * @param ch
	 */
	public void setInitialMeasurementData(InitialMeasurementDataList initialMeasurementDataList, String mcIdN, String stDt, String enDt, int ch) {
		
		DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData initMeasurementData = new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData();
		DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE preVEE = new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE();
		DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs msrs = new DeviceListType.Device.InitialMeasurementDataList.InitialMeasurementData.PreVEE.Msrs();
		
		preVEE.setMcIdN(mcIdN);
		preVEE.setStDt(getTimestamp(stDt));
		preVEE.setEnDt(getTimestamp(enDt));
		preVEE.setMsrs(msrs);
		
		initMeasurementData.setPreVEE(preVEE);		
		initialMeasurementDataList.getInitialMeasurementData().add(ch, initMeasurementData);
	}
	
	/**
	 * addMLData
	 * @param initialMeasurementDataList
	 * @param ml
	 * @param ch
	 */
	public void addMLData(InitialMeasurementDataList initialMeasurementDataList, ML ml, String enDt, int ch) {
		
		initialMeasurementDataList.getInitialMeasurementData().get(ch).getPreVEE().setEnDt(getTimestamp(enDt));
		Msrs msrs = initialMeasurementDataList.getInitialMeasurementData().get(ch).getPreVEE().getMsrs();
		msrs.getML().add(ml);
	}
	
	public void setEndDeviceEventList(EndDeviceEvents endDeviceEvents, String meterId, String meterEventId, String eventDate) {
		
		EndDeviceEvent endDeviceEvent = new EndDeviceEvent();
		endDeviceEvent.setMRID(meterEventId);
		endDeviceEvent.setCreatedDateTime(getTimestamp(eventDate));
		endDeviceEvent.setIssuerID(SOURCE);
		endDeviceEvent.setSeverity("1");
		
		Asset asset = new Asset();
		asset.setMRID(meterId);
		
		endDeviceEvent.setAssets(asset);
		
		endDeviceEvents.getEndDeviceEvent().add(endDeviceEvent);
	}
	
	public void setPreVEE(PreVEE preVEE, String mcIdN, String stDt, String enDt, Msrs msrs) {
		
		preVEE.setMcIdN(mcIdN);
		preVEE.setStDt(getTimestamp(stDt));
		preVEE.setEnDt(getTimestamp(enDt));
		preVEE.setMsrs(msrs);
	}
	
	public String getSDateTime(String yyyymmddhhmmss) {
		
		String sdate = "";
		GregorianCalendar c = new GregorianCalendar();
		
		try {			
			
			Date date = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(yyyymmddhhmmss);			
			c.setTime(date);
			
			sdate = TimeUtil.getDateUsingFormat(c.getTimeInMillis(), "yyyy-MM-dd-HH.mm.ss");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sdate;
	}
	
	public String getEDateTime(String yyyymmddhhmmss, int interval) {
		
		String edate = "";
		GregorianCalendar c = new GregorianCalendar();
		
		try {			
			
			Date date = DateTimeUtil.getDateFromYYYYMMDDHHMMSS(TimeUtil.getAddMinute(yyyymmddhhmmss, interval));
			c.setTime(date);
			
			edate = TimeUtil.getDateUsingFormat(c.getTimeInMillis(), "yyyyMMddHHmmss");
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return edate;
	}
	
	
	/**
	 * getMessageString object -> xml string
	 * 
	 * @param getMessageString
	 * @return
	 */
	public String getMessageString(Object message) {

		String str = null;

		try {
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			StringWriter sw = new StringWriter();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(message, sw);
			str = sw.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return str;
	}

	
	public XMLGregorianCalendar getTimestamp() {

		Calendar calendar = new GregorianCalendar();

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		XMLGregorianCalendar exgcal = null;
		try {
			exgcal = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar(year, month, day, hour, minute));

			// 연, 월, 일, 시, 분
		} catch (DatatypeConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return exgcal;
	}
	
	public XMLGregorianCalendar getTimestamp(String date) {
		
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(4, 6)) - 1;
		int day = Integer.parseInt(date.substring(6, 8));
		int hour = Integer.parseInt(date.substring(8, 10));
		int minute = (date.length() >= 12) ? Integer.parseInt(date.substring(10, 12)) : 0;
		
		XMLGregorianCalendar exgcal = null;
		try {

			GregorianCalendar cal = new GregorianCalendar(year, month, day, hour, minute);
			exgcal = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar(year, month, day, hour, minute));

			exgcal.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

			// 연, 월, 일, 시, 분
		} catch (DatatypeConfigurationException e) {
			log.error("e", e);
		}

		return exgcal;
	}
	
	/**
	 * setMeterReadingsMessage
	 * 
	 * @param _header
	 * @param map
	 * @return
	 */
	public MeterReadingsType setMeterReadingsMessage(MessageHeaderType _header, DeviceListType deviceLists) {

		MeterReadingsType message = new MeterReadingsType();

		MessageHeaderType header = new MessageHeaderType();
		header.setVerb(Verb.created.name());
		header.setNoun(_header.getNoun());
		header.setRevision(REVISION);
		header.setContext(CONTEXT);
		header.setTimestamp(instance.getTimestamp());
		header.setSource(SOURCE);
		header.setAckRequired("false");
		message.setHeader(header);
		message.getDeviceList().add(deviceLists);

		return message;
	}
	
	/**
	 * setMeterEventsMessage
	 * 
	 * @param _header
	 * @param map
	 * @return
	 */
	public EndDeviceEventsEventMessageType setEndDeviceEventsEventMessage(HeaderType _header, EndDeviceEvents endDeviceEvents) {

		EndDeviceEventsEventMessageType message = new EndDeviceEventsEventMessageType();
		
		HeaderType header = new HeaderType();
		header.setVerb(Verb.created.name());
		header.setNoun(_header.getNoun());
		header.setRevision(REVISION);
		header.setTimestamp(instance.getTimestamp());
		header.setSource(SOURCE);
		
		EndDeviceEventsPayloadType payload = new EndDeviceEventsPayloadType();
		payload.setEndDeviceEvents(endDeviceEvents);
		
		message.setHeader(header);
		message.setPayload(payload);
		
		return message;
	}
	
}
