package com.aimir.mars.integration.multispeak.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.multispeak.version_4.ArrayOfMeterReading1;
import org.multispeak.version_4.ArrayOfReadingValue;
import org.multispeak.version_4.MeterID;
import org.multispeak.version_4.MeterReading;
import org.multispeak.version_4.ReadingStatusCode;
import org.multispeak.version_4.ReadingValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.data.MeterData.Map.Entry;
import com.aimir.mars.integration.multispeak.client.CBServerSoap;
import com.aimir.mars.integration.multispeak.util.MultiSpeakConstants.ValidationError;
import com.aimir.mars.util.CmdController;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

public class GetLatestReadingByMeterIDService {

    private static Log log = LogFactory
            .getLog(GetLatestReadingByMeterIDService.class);

    final static DecimalFormat dformat = new DecimalFormat("#0.000000");

    @Autowired
    private CmdController cmdController;

    @Autowired
    private MeterDao meterDao;

    @Autowired
    private CBServerSoap cbServerSoap;

    public ArrayOfMeterReading1 execute(MeterID meterID) throws Exception {
        log.debug("GetLatestReadingByMeterIDService execute start..");
        String startDate = TimeUtil.getCurrentDay();
        String endDate = TimeUtil.getCurrentDay();

        ArrayOfMeterReading1 arrayOfMeterReads = new ArrayOfMeterReading1();
        String mdsId = meterID.getMeterNo();
        Meter meter = meterDao.get(mdsId);
        Modem modem = meter.getModem();

        Map<?, ?> meterData = null;
        Double meteringValue1 = null;
        Double meteringValue2 = null;
        Double meteringValue3 = null;
        Double meteringValue4 = null;
        String meteringTime = null;

        try{
            modem = meter.getModem();

            meterData = cmdController.cmdOnDemand( mdsId ,TimeUtil.getCurrentDay(), TimeUtil.getCurrentDay(), "METER");
            log.debug("meterData=" + meterData);
            MeterData.Map m = (MeterData.Map) meterData.get("rawMap");
            List<Entry> entries = m.getEntry();
            if(entries.size()>0) {
                meteringTime = TimeUtil.getCurrentTime();
            }
            for(Entry e : entries) {
                if(e.getKey().equals("Cumulative active energy -import")) {
                    meteringValue1 = Double.parseDouble((String)e.getValue());
                } else if(e.getKey().equals("Cumulative active energy -export")) {
                    meteringValue2 = Double.parseDouble((String)e.getValue());
                } else if(e.getKey().equals("Cumulative reactive energy -import")) {
                    meteringValue3 = Double.parseDouble((String)e.getValue());
                } else if(e.getKey().equals("Cumulative reactive energy -export")) {
                    meteringValue4 = Double.parseDouble((String)e.getValue());
                }
            }

        }catch(Exception e){
            log.error(e,e);

            MeterReading meterReading = new MeterReading();
            meterReading.setMeterID(meterID);
            meterReading.setDeviceID(meterID.getMeterNo());
            meterReading.setErrorString(ValidationError.COMMUNICATION_FAILURE.getName());
            arrayOfMeterReads.getMeterReading().add(meterReading);
            return arrayOfMeterReads;
        }

        if (meteringTime != null && !"".equals(meteringTime)
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
                readingValueCh1.setUnits("kWh");
                meteringValue1 += 5d * Math.random();
                readingValueCh1.setValue(meteringValue1.toString());
                readingValueCh1.setFieldName("1.0.1.8.0.255");
                readingValueCh1.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh1);
            }

            if(meteringValue2 != null) {
                ReadingValue readingValueCh2 = new ReadingValue();
                readingValueCh2.setTimeStamp(timeStamp);
                readingValueCh2.setUnits("kWh");
                meteringValue2 += 5d * Math.random();
                readingValueCh2.setValue(meteringValue2.toString());
                readingValueCh2.setFieldName("1.0.2.8.0.255");
                readingValueCh2.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh2);
            }

            if(meteringValue3 != null) {
                ReadingValue readingValueCh3 = new ReadingValue();
                readingValueCh3.setTimeStamp(timeStamp);
                readingValueCh3.setUnits("kVarh");
                meteringValue3 += 5d * Math.random();
                readingValueCh3.setValue(meteringValue3.toString());
                readingValueCh3.setFieldName("1.0.3.8.0.255");
                readingValueCh3.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh3);
            }

            if(meteringValue4 != null) {
                ReadingValue readingValueCh4 = new ReadingValue();
                readingValueCh4.setTimeStamp(timeStamp);
                readingValueCh4.setUnits("kVarh");
                meteringValue4 += 5d * Math.random();
                readingValueCh4.setValue(meteringValue4.toString());
                readingValueCh4.setFieldName("1.0.4.8.0.255");
                readingValueCh4.setReadingStatusCode(rStatusCode);
                arrayOfReadingValue.getReadingValue().add(readingValueCh4);
            }

            MeterReading meterReading = new MeterReading();
            meterReading.setReadingValues(arrayOfReadingValue);
            meterReading.setMeterID(meterID);
            meterReading.setDeviceID(meterID.getMeterNo());
            arrayOfMeterReads.getMeterReading().add(meterReading);
        }

        log.debug("GetLatestReadingByMeterIDService execute end..");
        return arrayOfMeterReads;
    }
}
