package com.aimir.mars.integration.multispeak.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.multispeak.version_4.ArrayOfErrorObject;
import org.multispeak.version_4.ArrayOfMeterReading1;
import org.multispeak.version_4.ArrayOfReadingValue;
import org.multispeak.version_4.ErrorObject;
import org.multispeak.version_4.GetReadingsByMeterID;
import org.multispeak.version_4.MeterID;
import org.multispeak.version_4.MeterReading;
import org.multispeak.version_4.ReadingStatusCode;
import org.multispeak.version_4.ReadingValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.MeterData.Map.Entry;
import com.aimir.mars.integration.multispeak.client.CBServerSoap;
import com.aimir.mars.integration.multispeak.data.MultiSpeakMessage;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.CmdController;
import com.aimir.mars.util.MarsProperty;
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
public class GetReadingsByMeterIDService extends AbstractService {
    private static Log log = LogFactory
            .getLog(GetReadingsByMeterIDService.class);

    final static DecimalFormat dformat = new DecimalFormat("0.###");

    @Autowired
    private CmdController cmdController;
    @Autowired
    private MeterManager meterDao;
    @Autowired
    private SupplierManager supplierDao;
    @Autowired
    private CBServerSoap cbServerSoap;

    @Override
    public void execute(MultiSpeakMessage message) throws Exception {
        log.debug("GetReadingsByMeterIDService execute start..");

        Object obj = message.getObject();
        Properties prop = new Properties();
        prop.load(getClass().getClassLoader().getResourceAsStream(
                "config/mars.properties"));
        GetReadingsByMeterID request = (GetReadingsByMeterID) obj;

        ArrayOfMeterReading1 arrayOfMeterReads = new ArrayOfMeterReading1();
        MeterID meterID = request.getMeterID();

        String responseURL = prop.getProperty("HES.WS.RESPONSE.CB", "http://172.31.120.46:7003/ssys/services/v1_1/NuriProxy/CB/proxy");
        String transactionID = request.getTransactionID();
 
        log.debug("transactionID="+transactionID);
        log.debug("responseURL="+responseURL);

        String mdsId = meterID.getMeterNo();
        Meter meter = meterDao.getMeter(mdsId);
        Modem modem = null;

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String startDate = sdfDate.format(request.getStartDate().toGregorianCalendar().getTime());
        String endDate = sdfDate.format(request.getEndDate().toGregorianCalendar().getTime());
        Map<?, ?> meterData = null;
        Calendar calStart = DateTimeUtil.getCalendar(startDate);
        Calendar calEnd = DateTimeUtil.getCalendar(endDate);

        if(meterID.getMeterNo().startsWith("testp")) {
            ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();
            Double meteringValue1 =  new Double((int)(10000 * Math.random()));
            Double meteringValue2 =  new Double((int)(100 * Math.random()));
            Double meteringValue3 =  new Double((int)(10000 * Math.random()));
            Double meteringValue4 =  new Double((int)(100 * Math.random()));

            for(;calStart.getTimeInMillis() <= calEnd.getTimeInMillis();) {

                GregorianCalendar gcal = new GregorianCalendar();
                gcal.setTime(calStart.getTime());
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

                calStart.add(Calendar.HOUR_OF_DAY, 1);
            }

            MeterReading meterReading = new MeterReading();
            meterReading.setReadingValues(arrayOfReadingValue);
            meterReading.setMeterID(meterID);
            meterReading.setDeviceID(meterID.getMeterNo());
            arrayOfMeterReads.getMeterReading().add(meterReading);
        } else {
            try{
                modem = meter.getModem();
                SimpleDateFormat sdf14 = null;
                DecimalFormat df = null;
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

                Map<Integer, ReadingValue[]> data = null;

                if(startDate.substring(0, 8).equals(endDate.substring(0, 8))) {
                    data = (Map<Integer, ReadingValue[]>) executeOndemand(meter, mdsId, startDate, endDate, sdf14, df);
                } else {
                    data = new HashMap<Integer, ReadingValue[]> ();
                    Calendar tempCal = DateTimeUtil.getCalendar(startDate.substring(0, 8) + "000000");
                    int idx = 1;
                    for(;tempCal.getTimeInMillis() <= calEnd.getTimeInMillis();) {
                        String tempStartDate = sdfDate.format(tempCal.getTime());
                        tempCal.add(Calendar.DATE, Integer.parseInt(MarsProperty.getProperty("MutliSepak.GetReadingsByMeterId.splitPeriod", "3")) - 1);
                        tempCal.add(Calendar.HOUR_OF_DAY, 23);
                        tempCal.add(Calendar.MINUTE, 59);
                        tempCal.add(Calendar.SECOND, 59);
                        String tempEndDate = sdfDate.format(tempCal.getTime());
                        if(tempCal.getTimeInMillis() >= calEnd.getTimeInMillis()) {
                            tempEndDate = sdfDate.format(calEnd.getTime());
                        }
                        log.debug("ondemand : " + mdsId + "   " + tempStartDate + "   " + tempEndDate);
                        Map<Integer, ReadingValue[]> tempData = (Map<Integer, ReadingValue[]>) executeOndemand(
                                meter, mdsId, tempStartDate, tempEndDate, sdf14, df);
                        log.debug("ondemand result : " + mdsId + "   " + tempStartDate + "   " + tempEndDate + "  count:" + tempData.size());
                        for(ReadingValue[] tempRV : tempData.values()) {
                            data.put(idx++, tempRV);
                        }
                        tempCal.add(Calendar.DATE, 1);
                    }
                }

                ArrayOfReadingValue arrayOfReadingValue = new ArrayOfReadingValue();
                for(ReadingValue[] tempRV : data.values()) {
                    for(int i=0;i<tempRV.length;i++) {
                        if (calStart.getTimeInMillis() <= tempRV[i].getTimeStamp().toGregorianCalendar().getTimeInMillis()
                                && calEnd.getTimeInMillis() >= tempRV[i].getTimeStamp().toGregorianCalendar().getTimeInMillis()
                                && tempRV[i].getValue() != null) {
                            arrayOfReadingValue.getReadingValue().add(tempRV[i]);
                        }
                    }
                }

                MeterReading meterReading = new MeterReading();
                meterReading.setReadingValues(arrayOfReadingValue);
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                arrayOfMeterReads.getMeterReading().add(meterReading);
            }catch(Exception e){
                log.error(e,e);
                MeterReading meterReading = new MeterReading();
                meterReading.setMeterID(meterID);
                meterReading.setDeviceID(meterID.getMeterNo());
                meterReading.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
                arrayOfMeterReads.getMeterReading().add(meterReading);
            }
        }

        Client client = ClientProxy.getClient(cbServerSoap);
        HTTPConduit http = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsParams = new TLSClientParameters();
        tlsParams.setDisableCNCheck(true); // CN Name check ignore...
        http.setTlsClientParameters(tlsParams);
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        http.setClient(httpClientPolicy);

        log.debug("Send GetReadingsByMeterIDService Response..");
        
        ArrayOfErrorObject response = cbServerSoap.GetReadingsByMeterIDAsync(
                arrayOfMeterReads, transactionID);
        log.debug("GetReadingsByMeterIDService ACK Response");

        if (response != null && response.getErrorObject().size() > 0) {
            for (ErrorObject error : response.getErrorObject()) {
                log.info("ErrorObject, ObjectID=[" + error.getObjectID()
                        + "], ErrorString=[" + error.getErrorString()
                        + "], EventTime=[" + error.getEventTime() + "]");
            }
        }

        log.debug("GetReadingsByMeterIDService execute end..");
    }

    private Object executeOndemand(Meter meter, String mdsId, String startDate, String endDate, SimpleDateFormat sdf14, DecimalFormat df) throws Exception {
        Map<?, ?> meterData = null;
        meterData = cmdController.cmdOnDemand( mdsId , startDate, endDate, "METER");
        log.debug("meterData=" + meterData);

        Map<Integer, ReadingValue[]> data = new HashMap<Integer, ReadingValue[]> ();
        if(meterData.get("rawMap") instanceof MeterData.Map) {
            MeterData.Map m = (MeterData.Map) meterData.get("rawMap");
            Double meteringValue = null;

            if(m==null) {
                throw new Exception("MeterData is empty.");
            }
            List<Entry> entries = m.getEntry();
            int idx = 0;
            String key = null;
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
            for(Entry e : entries) {
                key = (String) e.getKey();
                if(key.startsWith("Cumulative")) {
                    continue;
                }
                idx = Integer.parseInt(key.substring(key.lastIndexOf("-") + 1));
                ReadingValue[] rv = data.get(idx);
                if(rv == null ) {
                    rv = new ReadingValue[4];
                    rv[0] = new ReadingValue();
                    rv[1] = new ReadingValue();
                    rv[2] = new ReadingValue();
                    rv[3] = new ReadingValue();
                }
                if (e.getKey().equals(
                        "Energy Load Profile0 : Date-" + idx)) {
                    Date d = sdf14.parse((String) e.getValue());
                    GregorianCalendar gcal = new GregorianCalendar();
                    gcal.setTime(d);
                    XMLGregorianCalendar timeStamp = DatatypeFactory.newInstance()
                            .newXMLGregorianCalendar(gcal);
                    timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                    timeStamp.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
                    rv[0].setTimeStamp(timeStamp);
                    rv[1].setTimeStamp(timeStamp);
                    rv[2].setTimeStamp(timeStamp);
                    rv[3].setTimeStamp(timeStamp);
                } else if (e.getKey().equals(
                        "Energy Load Profile0 : ActiveEnergyImport-" + idx)) {
                    String value = (String)e.getValue();
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[0].setFieldName("1.0.1.8.0.255");
                    rv[0].setUnits("kWh");
                    rv[0].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[0].setReadingStatusCode(rStatusCode);
                } else if (e.getKey().equals(
                        "Energy Load Profile0 : ActiveEnergyExport-" + idx)) {
                    String value = (String)e.getValue();
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[1].setFieldName("1.0.2.8.0.255");
                    rv[1].setUnits("kWh");
                    rv[1].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[1].setReadingStatusCode(rStatusCode);
                } else if (e.getKey().equals(
                        "Energy Load Profile0 : ReactiveEnergyImport-" + idx)) {
                    String value = (String)e.getValue();
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[2].setFieldName("1.0.3.8.0.255");
                    rv[2].setUnits("kVarh");
                    rv[2].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[2].setReadingStatusCode(rStatusCode);
                } else if (e.getKey().equals(
                        "Energy Load Profile0 : ReactiveEnergyExport-" + idx)) {
                    String value = (String)e.getValue();
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[3].setFieldName("1.0.4.8.0.255");
                    rv[3].setUnits("kVarh");
                    rv[3].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[3].setReadingStatusCode(rStatusCode);
                }
                data.put(idx, rv);
            }
        } else if(meterData.get("rawMap") instanceof HashMap) {
            HashMap<String,String> m = (HashMap<String,String>) meterData.get("rawMap");
            Double meteringValue = null;

            if(m==null) {
                throw new Exception("MeterData is empty.");
            }
            Set<String> keys = m.keySet();
            int idx = 0;
            for(String key : keys) {
                String value = (String)m.get(key);
                if(key.startsWith("Cumulative")) {
                    continue;
                }
                idx = Integer.parseInt(key.substring(key.lastIndexOf("-") + 1));
                ReadingValue[] rv = data.get(idx);
                if(rv == null ) {
                    rv = new ReadingValue[4];
                    rv[0] = new ReadingValue();
                    rv[1] = new ReadingValue();
                    rv[2] = new ReadingValue();
                    rv[3] = new ReadingValue();
                }
                if (key.equals(
                        "Energy Load Profile0 : Date-" + idx)) {
                    Date d = sdf14.parse(value);
                    GregorianCalendar gcal = new GregorianCalendar();
                    gcal.setTime(d);
                    XMLGregorianCalendar timeStamp = DatatypeFactory.newInstance()
                            .newXMLGregorianCalendar(gcal);
                    timeStamp.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
                    timeStamp.setMillisecond(DatatypeConstants.FIELD_UNDEFINED);
                    rv[0].setTimeStamp(timeStamp);
                    rv[1].setTimeStamp(timeStamp);
                    rv[2].setTimeStamp(timeStamp);
                    rv[3].setTimeStamp(timeStamp);
                } else if (key.equals(
                        "Energy Load Profile0 : ActiveEnergyImport-" + idx)) {
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[0].setFieldName("1.0.1.8.0.255");
                    rv[0].setUnits("kWh");
                    rv[0].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[0].setReadingStatusCode(rStatusCode);
                } else if (key.equals(
                        "Energy Load Profile0 : ActiveEnergyExport-" + idx)) {
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[1].setFieldName("1.0.2.8.0.255");
                    rv[1].setUnits("kWh");
                    rv[1].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[1].setReadingStatusCode(rStatusCode);
                } else if (key.equals(
                        "Energy Load Profile0 : ReactiveEnergyImport-" + idx)) {
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[2].setFieldName("1.0.3.8.0.255");
                    rv[2].setUnits("kVarh");
                    rv[2].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[2].setReadingStatusCode(rStatusCode);
                } else if (key.equals(
                        "Energy Load Profile0 : ReactiveEnergyExport-" + idx)) {
                    if(value.indexOf(" ") > 0) {
                        meteringValue = df.parse(value.substring(0, value.indexOf(" "))).doubleValue();
                    } else {
                        meteringValue = df.parse(value).doubleValue();
                    }
                    rv[3].setFieldName("1.0.4.8.0.255");
                    rv[3].setUnits("kVarh");
                    rv[3].setValue(meteringValue.toString());
                    ReadingStatusCode rStatusCode = new ReadingStatusCode();
                    rStatusCode.setValue("1001");
                    rv[3].setReadingStatusCode(rStatusCode);
                }
                data.put(idx, rv);
            }
        }
        return data;
    }
}
