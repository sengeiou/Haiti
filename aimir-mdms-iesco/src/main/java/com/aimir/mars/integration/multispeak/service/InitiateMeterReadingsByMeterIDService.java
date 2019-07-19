package com.aimir.mars.integration.multispeak.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.aimir.mars.integration.multispeak.client.MRServerSoap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfMeterReading1;
import org.multispeak.version_4.ArrayOfReadingValue;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.ExpirationTime;
import org.multispeak.version_4.InitiateMeterReadingsByMeterID;
import org.multispeak.version_4.MeterID;
import org.multispeak.version_4.MeterReading;
import org.multispeak.version_4.ReadingStatusCode;
import org.multispeak.version_4.ReadingValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.MeterData.Map.Entry;
import com.aimir.mars.integration.multispeak.client.CBServerSoap;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.CmdController;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Supplier;
import com.aimir.service.device.MeterManager;
import com.aimir.service.system.SupplierManager;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

@Service
@Transactional
public class InitiateMeterReadingsByMeterIDService extends AbstractService {

    private static Log log = LogFactory
            .getLog(InitiateMeterReadingsByMeterIDService.class);

    final static DecimalFormat dformat = new DecimalFormat("0.###");

    @Autowired
    private CmdController cmdController;
    @Autowired
    private MeterManager meterDao;
    @Autowired
    private SupplierManager supplierDao;
    @Autowired
    private CBServerSoap cbServerSoap;
    @Autowired
    private MRServerSoap mrServerSoap;

    @Override
    public void execute(MultiSpeakMessage message) throws Exception {

        if (log.isDebugEnabled())
            log.debug("InitiateMeterReadingsByMeterIDService execute start..");

        Calendar requestedTime = message.getRequestedTime();
        Object obj = message.getObject();
        Properties prop = new Properties();
        prop.load(getClass().getClassLoader().getResourceAsStream(
                "config/mars.properties"));
        InitiateMeterReadingsByMeterID request = (InitiateMeterReadingsByMeterID) obj;

        if (log.isTraceEnabled()) {
            log.trace("Message="+message.toString());
            log.trace("Request="+request.toString());
        }

        ArrayOfMeterReading1 changedMeterReads = new ArrayOfMeterReading1();
        List<MeterID> meterIDs = request.getMeterIDs().getMeterID();

        log.debug("meterIDs size="+meterIDs.size());

        String responseURL = request.getResponseURL();
        String transactionID = request.getTransactionID();
        ExpirationTime expirationTime = request.getExpTime();
        Calendar expirationDateTime = Calendar.getInstance();
        expirationDateTime.setTime(requestedTime.getTime());

        if(responseURL == null || responseURL.equals("")) {
            responseURL = prop.getProperty("HES.WS.RESPONSE.CB", "http://172.31.120.46:7003/ssys/services/v1_1/NuriProxy/CB/proxy");
        }
        log.debug("transactionID="+transactionID);
        log.debug("responseURL="+responseURL);
        log.debug("expirationTime="+expirationTime.getValue());

        /*
         * ExpirationTime value가 float 형이다보니 원하는 시간에 실행이 안될수 있다. 소수점 이하가 없는 경우는
         * 원래 방식대로 계산한다. 소수점 있는 경우는 일단 1년(365일) 1달(30일) 기본으로 한다. 초이하 오차는 무시한다.
         */
        switch (expirationTime.getUnits()) {
        case YEARS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.YEAR,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime
                        .add(Calendar.SECOND, (int) (expirationTime.getValue()
                                * 365 * 30 * 24 * 60 * 60));
            }
            break;
        case MONTHS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.MONTH,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 30 * 24 * 60 * 60));
            }
            break;
        case WEEKS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.DATE,
                        (int) expirationTime.getValue() * 7);
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 7 * 24 * 60 * 60));
            }
            break;
        case DAYS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.DATE,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 24 * 60 * 60));
            }
            break;
        case HOURS:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.HOUR,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 60 * 60));
            }
            break;
        case MINUTES:
            if (expirationTime.getValue() % (int) expirationTime.getValue() == 0) {
                expirationDateTime.add(Calendar.MINUTE,
                        (int) expirationTime.getValue());
            } else {
                expirationDateTime.add(Calendar.SECOND,
                        (int) (expirationTime.getValue() * 60));
            }
            break;
        case SECONDS:
            expirationDateTime.add(Calendar.SECOND,
                    (int) expirationTime.getValue());
            break;
        case MILLISECONDS:
            expirationDateTime.add(Calendar.MILLISECOND,
                    (int) expirationTime.getValue());
            break;
        case OTHER:
        default:
            // OTHER 따로 정의 된게 없어 SECOND로 처리한다. 스펙에 디폴트가 정의 되어있다면 변경 해줘야 한다.
            expirationDateTime.add(Calendar.SECOND,
                    (int) expirationTime.getValue());
            break;
        }
        
        /*
    	Regular("1001"),
    	DST("1002"),
    	LowVoltage("1003"),
    	ReverseEnergyFlow("1004"),
    	NoReadOutage("2001"),
    	NoReadDisconnected("2002"),
    	Missing("3001"),
    	ClockError("3002"),
    	TimeResetOccurred("3003"),
    	ChecksumError("3004"),
    	DeviceFailure("3005"),
    	BadAMIData("3006"),
    	SystemEstimate("4001"),
    	OfficeEstimate("4002")
    	*/

        for (MeterID meterID : meterIDs) {
            String mdsId = meterID.getMeterNo();
            Meter meter = meterDao.getMeter(mdsId);
            Modem modem = null;

            Map<?, ?> meterData = null;
            Double meteringValue1 = null;
            Double meteringValue2 = null;
            Double meteringValue3 = null;
            Double meteringValue4 = null;
            String unit1 = null;
            String unit2 = null;
            String unit3 = null;
            String unit4 = null;
            String meteringTime = null;

            DecimalFormat df = null;
            SimpleDateFormat sdf14 = null;
            if (meter != null && meter.getSupplier() != null) {
                Supplier supplier = meter.getSupplier();
                if(supplier !=null) {
                    String lang = supplier.getLang().getCode_2letter();
                    String country = supplier.getCountry().getCode_2letter();
                    TimeLocaleUtil.setSupplier(supplier);
                    sdf14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                    df = TimeLocaleUtil.getDecimalFormat(supplier);
                }
            } else {
                Supplier supplier = supplierDao.getSuppliers().get(0);
                if(supplier !=null) {
                    String lang = supplier.getLang().getCode_2letter();
                    String country = supplier.getCountry().getCode_2letter();
                    TimeLocaleUtil.setSupplier(supplier);
                    sdf14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                    df = TimeLocaleUtil.getDecimalFormat(supplier);
                }
            }

            if(meterID.getMeterNo().startsWith("testp")) {
                meteringValue1 = 1000 * Math.random();
                meteringValue2 = 1000 * Math.random();
                meteringValue3 = 1000 * Math.random();
                meteringValue4 = 1000 * Math.random();
                unit1 = "kWh";
                unit2 = "kWh";
                unit3 = "kWh";
                unit4 = "kWh";
                meteringTime = TimeUtil.getCurrentTime();
            } else {
                try{
                    modem = meter.getModem();

                    meterData = cmdController.cmdOnDemand( mdsId , TimeUtil.getCurrentDay(), TimeUtil.getCurrentDay(), "METER");
                    log.debug("meterData=" + meterData);
                    if(meterData.get("rawMap")!=null) {
                        if(meterData.get("rawMap") instanceof MeterData.Map) {
                            MeterData.Map m = (MeterData.Map) meterData.get("rawMap");
                            List<Entry> entries = m.getEntry();
    
                            ArrayOfReadingValue arrayOfReadingValue = null;
                            ReadingStatusCode rStatusCode = null;
                            XMLGregorianCalendar timeStamp = null;
                            if(entries.size()>0) {
                                meteringTime = TimeUtil.getCurrentTime();
                            }
                            for(Entry e : entries) {
                                String value = (String)e.getValue();
                                String unit = null;
                                if(value.indexOf("[") > -1) {
                                    unit = value.substring(value.indexOf("[")+1,value.indexOf("]"));
                                }
                                if(e.getKey().equals("Cumulative active energy -import")) {
                                    meteringValue1 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit1 = unit;
                                    } else {
                                        unit1 = "kWh";
                                    }
                                } else if(e.getKey().equals("Cumulative active energy -export")) {
                                    meteringValue2 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit2 = unit;
                                    } else {
                                        unit2 = "kWh";
                                    }
                                } else if(e.getKey().equals("Cumulative reactive energy -import")) {
                                    meteringValue3 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit3 = unit;
                                    } else {
                                        unit3 = "kvarh";
                                    }
                                } else if(e.getKey().equals("Cumulative reactive energy -export")) {
                                    meteringValue4 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit4 = unit;
                                    } else {
                                        unit4 = "kvarh";
                                    }
                                }
                            }
                        } else if(meterData.get("rawMap") instanceof HashMap) {
                            HashMap<String,String> m = (HashMap<String,String>) meterData.get("rawMap");
                            Set<String> keys = m.keySet();
    
                            ArrayOfReadingValue arrayOfReadingValue = null;
                            ReadingStatusCode rStatusCode = null;
                            XMLGregorianCalendar timeStamp = null;
                            if(keys.size()>0) {
                                meteringTime = TimeUtil.getCurrentTime();
                            }
                            for(String key : keys) {
                                String value = (String)m.get(key);
                                String unit = null;
                                if(value.indexOf("[") > -1) {
                                    unit = value.substring(value.indexOf("[")+1,value.indexOf("]"));
                                }
                                if(key.equals("Cumulative active energy -import")) {
                                    meteringValue1 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit1 = unit;
                                    } else {
                                        unit1 = "kWh";
                                    }
                                } else if(key.equals("Cumulative active energy -export")) {
                                    meteringValue2 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit2 = unit;
                                    } else {
                                        unit2 = "kWh";
                                    }
                                } else if(key.equals("Cumulative reactive energy -import")) {
                                    meteringValue3 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit3 = unit;
                                    } else {
                                        unit3 = "kvarh";
                                    }
                                } else if(key.equals("Cumulative reactive energy -export")) {
                                    meteringValue4 = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                                    if(unit!=null) {
                                        unit4 = unit;
                                    } else {
                                        unit4 = "kvarh";
                                    }
                                }
                            }
                        } else {
                            throw new Exception();
                        }
                    }
                }catch(Exception e){
                    log.error(e,e);

                    MeterReading meterReading = new MeterReading();
                    meterReading.setMeterID(meterID);
                    meterReading.setDeviceID(meterID.getMeterNo());
                    meterReading.setErrorString(ValidationError.SYSTEM_ERROR.getName());
                    changedMeterReads.getMeterReading().add(meterReading);
                }
            }
            if(meterID.getMeterNo().startsWith("testp")) {
                ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();

                GregorianCalendar gcal = new GregorianCalendar();
                XMLGregorianCalendar timeStamp = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gcal);
                timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                timeStamp.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

                ReadingStatusCode rStatusCode = new ReadingStatusCode();
                rStatusCode.setValue("1001");

                ReadingValue readingValueCh1 = new ReadingValue();
                readingValueCh1.setTimeStamp(timeStamp);
                readingValueCh1.setUnits("kWh");
                meteringValue1 += 5d * Math.random();
                readingValueCh1.setValue(meteringValue1.toString());
                readingValueCh1.setFieldName("1.0.1.8.0.255");
                readingValueCh1.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh1);

                ReadingValue readingValueCh2 = new ReadingValue();
                readingValueCh2.setTimeStamp(timeStamp);
                readingValueCh2.setUnits("kWh");
                meteringValue2 += 5d * Math.random();
                readingValueCh2.setValue(meteringValue2.toString());
                readingValueCh2.setFieldName("1.0.2.8.0.255");
                readingValueCh2.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh2);

                ReadingValue readingValueCh3 = new ReadingValue();
                readingValueCh3.setTimeStamp(timeStamp);
                readingValueCh3.setUnits("kVarh");
                meteringValue3 += 5d * Math.random();
                readingValueCh3.setValue(meteringValue3.toString());
                readingValueCh3.setFieldName("1.0.3.8.0.255");
                readingValueCh3.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh3);

                ReadingValue readingValueCh4 = new ReadingValue();
                readingValueCh4.setTimeStamp(timeStamp);
                readingValueCh4.setUnits("kVarh");
                meteringValue4 += 5d * Math.random();
                readingValueCh4.setValue(meteringValue4.toString());
                readingValueCh4.setFieldName("1.0.4.8.0.255");
                readingValueCh4.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh4);

                MeterReading meterReading = new MeterReading();
                meterReading.setReadingValues(arrayOfReadingValue);
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                changedMeterReads.getMeterReading().add(meterReading);
            } else if (meteringTime != null && !"".equals(meteringTime)
                    && (meteringValue1 != null || meteringValue2 != null
                            || meteringValue3 != null
                            || meteringValue4 != null)) {

                ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();

                GregorianCalendar gcal = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

                if (meteringTime != null && !meteringTime.equals("")) {
                    Date d = sdf.parse(meteringTime);
                    gcal.setTime(d);
                } else {
                    Date d = sdf.parse(TimeUtil.getCurrentTime());
                    gcal.setTime(d);
                }
                
                XMLGregorianCalendar timeStamp = DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(gcal);
                timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                timeStamp.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);

                ReadingStatusCode rStatusCode = new ReadingStatusCode();
                rStatusCode.setValue("1001");

                if(meteringValue1 != null) {
                    ReadingValue readingValueCh1 = new ReadingValue();
                    readingValueCh1.setTimeStamp(timeStamp);
                    readingValueCh1.setUnits(unit1);
                    readingValueCh1.setValue(meteringValue1.toString());
                    readingValueCh1.setFieldName("1.0.1.8.0.255");
                    readingValueCh1.setReadingStatusCode(rStatusCode);
                    arrayOfReadingValue.getReadingValue().add(readingValueCh1);
                }

                if(meteringValue2 != null) {
                    ReadingValue readingValueCh2 = new ReadingValue();
                    readingValueCh2.setTimeStamp(timeStamp);
                    readingValueCh2.setUnits(unit2);
                    readingValueCh2.setValue(meteringValue2.toString());
                    readingValueCh2.setFieldName("1.0.2.8.0.255");
                    readingValueCh2.setReadingStatusCode(rStatusCode);
                    arrayOfReadingValue.getReadingValue().add(readingValueCh2);
                }

                if(meteringValue3 != null) {
                    ReadingValue readingValueCh3 = new ReadingValue();
                    readingValueCh3.setTimeStamp(timeStamp);
                    readingValueCh3.setUnits(unit3);
                    readingValueCh3.setValue(meteringValue3.toString());
                    readingValueCh3.setFieldName("1.0.3.8.0.255");
                    readingValueCh3.setReadingStatusCode(rStatusCode);
                    arrayOfReadingValue.getReadingValue().add(readingValueCh3);
                }

                if(meteringValue4 != null) {
                    ReadingValue readingValueCh4 = new ReadingValue();
                    readingValueCh4.setTimeStamp(timeStamp);
                    readingValueCh4.setUnits(unit4);
                    readingValueCh4.setValue(meteringValue4.toString());
                    readingValueCh4.setFieldName("1.0.4.8.0.255");
                    readingValueCh4.setReadingStatusCode(rStatusCode);
                    arrayOfReadingValue.getReadingValue().add(readingValueCh4);
                }

                MeterReading meterReading = new MeterReading();
                meterReading.setReadingValues(arrayOfReadingValue);
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                changedMeterReads.getMeterReading().add(meterReading);
            } else {
                MeterReading meterReading = new MeterReading();
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                meterReading.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                changedMeterReads.getMeterReading().add(meterReading);
            }
        }

        Calendar currentTime = Calendar.getInstance();

        if (currentTime.getTimeInMillis() <= expirationDateTime
                .getTimeInMillis() && meterIDs.size() > 0) { 

            Client client = ClientProxy.getClient(cbServerSoap);
            HTTPConduit http = (HTTPConduit) client.getConduit();
            TLSClientParameters tlsParams = new TLSClientParameters();
            tlsParams.setDisableCNCheck(true); // CN Name check ignore...
            http.setTlsClientParameters(tlsParams);
            HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
            httpClientPolicy.setAllowChunking(false);
            http.setClient(httpClientPolicy);

            log.debug("Send InitiateMeterReadingsByMeterIDService Response..");
            
            ArrayOfErrorObject response = mrServerSoap.ReadingChangedNotification(
                    changedMeterReads, transactionID);
            log.debug("InitiateMeterReadingsByMeterIDService ACK Response");

            if (response != null && response.getErrorObject().size() > 0) {
                for (ErrorObject error : response.getErrorObject()) {
                    log.info("ErrorObject, ObjectID=[" + error.getObjectID()
                            + "], ErrorString=[" + error.getErrorString()
                            + "], EventTime=[" + error.getEventTime() + "]");
                }
            }

        }
        log.debug("InitiateMeterReadingsByMeterIDService execute end..");
    }

}
