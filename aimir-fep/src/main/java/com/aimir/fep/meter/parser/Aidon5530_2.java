package com.aimir.fep.meter.parser;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing Aidon 5530 meter data
 * 
 * +Meter Constant (2bytes)
 * +LP Ponter (1byte)
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-12-13 15:59:15 +0900 $,
 */
public class Aidon5530_2 extends MeterDataParser implements ModemParser,
		java.io.Serializable {
	private static final long serialVersionUID = 6288692279783454083L;

	private static Log log = LogFactory.getLog(Aidon5530.class);

	private byte[] TIMEZONE = new byte[2];
	private byte[] DST = new byte[2];
	private byte[] YEAR = new byte[2];
	private byte[] MONTH = new byte[1];
	private byte[] DAY = new byte[1];
	private byte[] HOUR = new byte[1];
	private byte[] MINUTE = new byte[1];
	private byte[] SECOND = new byte[1];
	private byte[] CURPULSE = new byte[4];
	private byte[] LPPERIOD = new byte[1];
	private byte[] UNIT = new byte[1];
	private byte[] METERCONSTANT = new byte[2];
	private byte[] DATECNT = new byte[2];
	//private byte[] LPPOINTER = new byte[1];
	private byte[] LPYEAR = new byte[2];
	private byte[] LPMONTH = new byte[1];
	private byte[] LPDAY = new byte[1];
	private byte[] BASEPULSE = new byte[4];
	private byte[] LP = new byte[2];
	private byte[] ERRORCODE = new byte[2];

	private String timestamp = null;
	private byte[] rawData = null;
	private Double meteringValue = null;
	private int flag = 0;
	private int period = 0;
	private int errorCode = 0;
	private int unit = 0;
	private double meterConstant = 0.0;
	private double curPulse = 0.0;

	private Aidon aidonMeta = null;
	private EventLogData[] eventlogdata = null;

	private ModemLPData[] lpData = null;

	/**
	 * constructor
	 */
	public Aidon5530_2() {
	}

	/**
	 * getRawData
	 */
	public byte[] getRawData() {
		return rawData;
	}

	/**
	 * get data length
	 * 
	 * @return length
	 */
	public int getLength() {
		if (rawData == null)
			return 0;

		return rawData.length;
	}

	public Aidon getAidonMeta() {
		return aidonMeta;
	}

	public void setAidonMeta(Aidon aidonMeta) {
		this.aidonMeta = aidonMeta;
	}

	public String getTimestamp() {
		return this.timestamp;
	}

	/**
	 * parse meter mesurement data
	 * 
	 * @param data
	 */
	public void parse(byte[] data) throws Exception {
	int pos = 0;
		
		System.arraycopy(data, pos, TIMEZONE, 0, TIMEZONE.length);
		pos += TIMEZONE.length;
		int timeZone = DataUtil.getIntTo2Byte(TIMEZONE);
		log.debug("TIMEZONE[" + timeZone + "]");

		System.arraycopy(data, pos, DST, 0, DST.length);
		pos += DST.length;
		int dst = DataUtil.getIntTo2Byte(DST);
		log.debug("DST[" + dst + "]");

		System.arraycopy(data, pos, YEAR, 0, YEAR.length);
		pos += YEAR.length;
		DataUtil.convertEndian(YEAR);
		int year = DataUtil.getIntTo2Byte(YEAR);
		log.debug("YEAR[" + year + "]");

		System.arraycopy(data, pos, MONTH, 0, MONTH.length);
		pos += MONTH.length;
		int month = DataUtil.getIntToBytes(MONTH);
		log.debug("MONTH[" + month + "]");

		System.arraycopy(data, pos, DAY, 0, DAY.length);
		pos += DAY.length;
		int day = DataUtil.getIntToBytes(DAY);
		log.debug("DAY[" + day + "]");

		System.arraycopy(data, pos, HOUR, 0, HOUR.length);
		pos += HOUR.length;
		int hour = DataUtil.getIntToBytes(HOUR);
		log.debug("HOUR[" + hour + "]");

		System.arraycopy(data, pos, MINUTE, 0, MINUTE.length);
		pos += MINUTE.length;
		int minute = DataUtil.getIntToBytes(MINUTE);
		log.debug("MINUTE[" + minute + "]");

		System.arraycopy(data, pos, SECOND, 0, SECOND.length);
		pos += SECOND.length;
		int second = DataUtil.getIntToBytes(SECOND);
		log.debug("SECOND[" + second + "]");

		timestamp = Integer.toString(year)
				+ (month < 10 ? "0" + month : "" + month)
				+ (day < 10 ? "0" + day : "" + day)
				+ (hour < 10 ? "0" + hour : "" + hour)
				+ (minute < 10 ? "0" + minute : "" + minute)
				+ (second < 10 ? "0" + second : "" + second);

		System.arraycopy(data, pos, CURPULSE, 0, CURPULSE.length);
		pos += CURPULSE.length;
		DataUtil.convertEndian(CURPULSE);

		// get real pulse
		/*
		 * lucky initpulse 0 maxpulse 0 .... lp =
		 * IUtil.getRealPulseValue(this.meterId, lp); log.debug("REALPULSE[" +
		 * lp + "]");
		 */

		// from here, lp
		System.arraycopy(data, pos, LPPERIOD, 0, LPPERIOD.length);
		pos += LPPERIOD.length;
		period = DataUtil.getIntToBytes(LPPERIOD);
		log.debug("LPPERIOD[" + period + "]");

		if (period == 0) {
			System.arraycopy(data, pos, ERRORCODE, 0, ERRORCODE.length);
			pos += ERRORCODE.length;
			DataUtil.convertEndian(ERRORCODE);
			errorCode = DataUtil.getIntTo2Byte(ERRORCODE);
		} else {
			System.arraycopy(data, pos, UNIT, 0, UNIT.length);
			pos += UNIT.length;
			unit = DataUtil.getIntToBytes(UNIT);
			log.debug("UNIT[" + unit + "]");
			System.arraycopy(data, pos, METERCONSTANT, 0, METERCONSTANT.length);
			pos += METERCONSTANT.length;
			DataUtil.convertEndian(METERCONSTANT);
			meterConstant = DataUtil.getIntTo2Byte(METERCONSTANT);
			log.debug("METERCONSTANT[" + meterConstant + "]");
			meter.setPulseConstant(meterConstant);
			curPulse = (double)DataUtil.getLongToBytes(CURPULSE) / meter.getPulseConstant();
			meteringValue = curPulse;
			log.debug("CURPULSE[" + meteringValue + "]");
			System.arraycopy(data, pos, DATECNT, 0, DATECNT.length);
			pos += DATECNT.length;
			DataUtil.convertEndian(DATECNT);
			int datecnt = DataUtil.getIntTo2Byte(DATECNT);
			log.debug("DATECNT[" + datecnt + "]");

			//System.arraycopy(data, pos, LPPOINTER, 0, LPPOINTER.length);
			//pos += LPPOINTER.length;
			//int lpPointer = DataUtil.getIntToBytes(LPPOINTER);
			//log.debug("LPPOINTER[" + lpPointer + "]");
			int lpLength = LPYEAR.length + LPMONTH.length + LPDAY.length
					+ BASEPULSE.length + (48 * period);

			byte[] bx = new byte[datecnt * lpLength];
			System.arraycopy(data, pos, bx, 0, bx.length);
			pos += bx.length;

			byte[] am = new byte[data.length - pos];
			System.arraycopy(data, pos, am, 0, am.length);
			aidonMeta = new Aidon();
			aidonMeta.setDateTime(timestamp);
			aidonMeta.parse(am);
			eventlogdata = aidonMeta.getEventLogData();

			lpData = new ModemLPData[datecnt];
			pos = 0;

			for (int i = 0; i < datecnt; i++) {
				System.arraycopy(bx, pos, LPYEAR, 0, LPYEAR.length);
				pos += LPYEAR.length;
				System.arraycopy(bx, pos, LPMONTH, 0, LPMONTH.length);
				pos += LPMONTH.length;
				System.arraycopy(bx, pos, LPDAY, 0, LPDAY.length);
				pos += LPDAY.length;
				System.arraycopy(bx, pos, BASEPULSE, 0, BASEPULSE.length);
				pos += BASEPULSE.length;
				month = DataUtil.getIntToBytes(LPMONTH);
				day = DataUtil.getIntToBytes(LPDAY);

				lpData[i] = new ModemLPData();
				lpData[i].setLpDate(Integer.toString(DataUtil
						.getIntTo2Byte(LPYEAR))
						+ (month < 10 ? "0" + month : "" + month)
						+ (day < 10 ? "0" + day : "" + day));
								
				lpData[i].setBasePulse(new double[]{DataUtil.getLongToBytes(BASEPULSE)/meter.getPulseConstant()});

				int hourCount=24;
				if (i == (datecnt - 1)) {
					hourCount = Integer.parseInt(DateTimeUtil.getCurrentDateTimeByFormat("HH"));
				}
				
				// ModemLPData의 lp 배열구조가 변경됨. 주의
				lpData[i].setLp(new double[1][hourCount * period]);				
				
				for (int j = 0; j < lpData[i].getLp()[0].length; j++) {
					System.arraycopy(bx, pos, LP, 0, LP.length);
					pos += LP.length;
					// DataUtil.convertEndian(LP);
					lpData[i].getLp()[0][j] = (double)DataUtil.getIntTo2Byte(LP)
							/ meter.getPulseConstant();
				}
			}
		}
	}

	/**
	 * get flag
	 * 
	 * @return flag measurement flag
	 */
	public int getFlag() {
		return this.flag;
	}

	/**
	 * set flag
	 * 
	 * @param flag
	 *            measurement flag
	 */
	public void setFlag(int flag) {
		this.flag = flag;
	}

	public int getPeriod() {
		return period;
	}

	public MeteringFail getMeteringFail() {

		MeteringFail meteringFail = null;
		if (this.errorCode > 0) {
			meteringFail = new MeteringFail();
			meteringFail.setModemErrCode(this.errorCode);
			meteringFail.setModemErrCodeName(NURI_T002
					.getMODEM_ERROR_NAME(this.errorCode));
			return meteringFail;
		} else {
			return null;
		}
	}

	/**
	 * get String
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("Aidon5530 DATA[");
		sb.append("(meteringValue=").append(meteringValue).append("),");
		sb.append("(lp=(");
		for (int i = 0; i < 24; i++) {
			// sb.append("Hour["+i+"]=["+preDayLPValue[i]+"], ");
		}
		sb.append(")\n");
		sb.append("]\n");

		return sb.toString();
	}

	/**
	 * get Data
	 */
	@Override
	public LinkedHashMap getData() {
		LinkedHashMap res = new LinkedHashMap(16, 0.75f, false);

        DecimalFormat df3 = new DecimalFormat("#.######");
        SimpleDateFormat datef14=null;
        SimpleDateFormat timef = null;
        SimpleDateFormat datef = null;
         
        if(meter!=null && meter.getSupplier()!=null){
            Supplier supplier = meter.getSupplier();
            if(supplier !=null){
                String lang = supplier.getLang().getCode_2letter();
                String country = supplier.getCountry().getCode_2letter();
                
                df3 = TimeLocaleUtil.getDecimalFormat(supplier);
                datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                timef = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(6, lang, country));
                datef = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(8, lang, country));
            }
        }else{
            //locail 정보가 없을때는 기본 포멧을 사용한다.
            df3 = new DecimalFormat();
            datef14 = (SimpleDateFormat)DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            datef = (SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT);
            timef = (SimpleDateFormat)DateFormat.getTimeInstance(DateFormat.MEDIUM);
        }
        
		// res.put("name","ZEUPLS");
		res.put("Metering Time", timestamp);
		res.put("Period", "" + period);
		res.put("Metering Value", "" + df3.format(meteringValue));
		res.put("Unit", unit);
		res.put("Meter Constant", meterConstant);
		res.put("Current Pulse", curPulse);
		int interval = 60 / period;
		try {
    		for (int i = 0, hour = 0, minute = 0; i < lpData.length; i++) {
    			hour = 0;
    			minute = 0;
    			res.put("BasePulse Date:" + i, datef.format(DateTimeUtil.getDateFromYYYYMMDD(lpData[i].getLpDate())));
                for (int ch = 0; ch < lpData[i].getBasePulse().length; ch++) {
                    res.put("BasePulse:" + i + " ch"+ch, df3.format(lpData[i].getBasePulse()[ch]));
                }
                
                for (int ch = 0; ch < lpData[i].getLp().length; ch++) {
                    res.put("channel " + ch, "========");
                    for (int j = 0; j < lpData[i].getLp()[ch].length; j++, minute += interval) {
                        if (minute == 60) {
                            hour++;
                            minute = 0;
                        }
                        res.put(datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lpData[i].getLpDate()
                                + String.format("%02d%02d00", hour, minute))),
                                lpData[i].getLp()[ch][j]);
                    }
                }
    		}
		}
		catch(ParseException e) {
		    
		}
		res.putAll(aidonMeta.getData());
		return res;
	}

	public Double getMeteringValue() {
		return meteringValue;
	}

	public ModemLPData[] getLpData() {
		return lpData;
	}

	public EventLogData[] getEventLog() {
		return eventlogdata;
	}
}
