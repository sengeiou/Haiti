package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterTimeSyncData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.SM110Table.MT019;
import com.aimir.fep.meter.parser.SM110Table.MT115;
import com.aimir.fep.meter.parser.SM110Table.NT509;
import com.aimir.fep.meter.parser.SM110Table.ST001;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Modem;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;

/**
 * parsing I210 Pulse Meter Data implemented in Haiti
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com) <br>
 *         modified by Ji Woong Park (wll27471297@nuritelecom.com)
 * 
 * @version 1.0
 */
public class I210Plus extends MeterDataParser implements java.io.Serializable {
	private static final long serialVersionUID = 7503986198693601423L;

	private static Log log = LogFactory.getLog(I210Plus.class);

	private final String STD_START_CHAR = "I";			
	
	private byte[] rawData = null;

	private ArrayList<LPData> lpDataList = new ArrayList<LPData>();
	private Double meteringValue = null;
	private String meterId = null;
	private String meterDeviceModelName = "";
	private int dst;
	private int flag = 0;

	private ST001 st001 = null;
	private MT019 mt019 = null;
	private MT115 mt115 = null;
	private ArrayList<NT509> nt509List = new ArrayList<NT509>();
	
	private String START_CHR = null;
	private Double TOTAL_DEL_KWH = null;
	private Double TOTAL_DEL_PLUS_RCVD_KWH = null;
	private Double TOTAL_DEL_MINUS_RCVD_KWH = null;
	private Double TOTAL_REC_KWH = null;
	
	private boolean ACTUAL_SWITCH_STATE;

	public I210Plus() { }

	public Double getMeteringValue() {
		TOU_BLOCK[] curr = getCurrBilling();
		ArrayList list = getSelfReads();

		if (curr != null && curr.length > 0 && curr[0] != null && curr[0].getSummations() != null
				&& curr[0].getSummations().size() > 0 && curr[0].getSummation(0) != null) {
			this.meteringValue = (Double) curr[0].getSummation(0);
		} else {
			if (list != null && list.size() > 0) { // 제일 마지막 self read 날짜의 total값을 찾아서 넣어줌 오름차순으로 데이터가 옴
				TOU_BLOCK[] tou_day = (TOU_BLOCK[]) list.get(list.size() - 1);
				if (tou_day != null && tou_day.length > 0 && tou_day[0].getSummations() != null
						&& tou_day[0].getSummations().size() > 0 && tou_day[0].getSummation(0) != null) {
					this.meteringValue = (Double) tou_day[0].getSummation(0);// 해당날짜의 total사용량
					log.debug("tou event time: " + tou_day[0].getEventTime(0) + " tou sum:" + meteringValue);
				}
			}
		}
		return this.meteringValue;
	}

	public String getMeterId() {
		return meterId;
	}

	public byte[] getRawData() {
		return this.rawData;
	}

	public int getLength() {
		return this.rawData.length;
	}

	
	public void parse(byte[] data) throws Exception {
		rawData = data;
		int totlen = data.length;
		String throwMsg = null;
		log.info("meter : " + meter.getMdsId() +", data : "+Hex.decode(data));
		log.debug("TOTLEN[" + totlen + "]");

		Modem modem = meter.getModem();
		MCU mcu = modem.getMcu();
		if(modem != null) {
			throwMsg = checkTableLength("Total", data);
			if(throwMsg != null) {
				StringBuffer buffer = new StringBuffer();
				if(mcu != null)
					buffer.append("MCU : ").append(mcu.getSysID()).append(", Meter : ").append(meter.getMdsId()).append(", ").append(throwMsg);
				else
					buffer.append("MCU is null, Modem : ").append(modem.getDeviceSerial()).append(", Meter : ").append(meter.getMdsId()).append(", ").append(throwMsg);
				
				EventUtil.sendEvent("Equipment Notification",
	                    TargetClass.valueOf(meter.getMeterType().getName()),
	                    meter.getMdsId(),
	                    new String[][] {{"message", buffer.toString()}});
				
				throw new Exception(buffer.toString());
			}
		}

		int offset = 0;
		while (offset + 6 < totlen) {
			log.debug("OFFSET[" + offset + "]");

			String tbName = new String(data, offset, 4);
			offset += 4;
			int len = 0;
			len |= (data[offset++] & 0xff) << 8;
			len |= (data[offset++] & 0xff);
			byte[] b = new byte[len];

			if (data.length - offset < len)
				break;

			System.arraycopy(data, offset, b, 0, len);
			offset += len;
			
			try {
				throwMsg = checkTableLength("tbName", b);
				if(throwMsg != null) {
					StringBuffer buffer = new StringBuffer();
					if(mcu != null)
						buffer.append("MCU : ").append(mcu.getSysID()).append(", Meter : ").append(meter.getMdsId()).append(", ").append(throwMsg);
					else
						buffer.append("MCU is null, Modem : ").append(modem.getDeviceSerial()).append(", Meter : ").append(meter.getMdsId()).append(", ").append(throwMsg);
					
					EventUtil.sendEvent("Equipment Notification",
		                    TargetClass.valueOf(meter.getMeterType().getName()),
		                    meter.getMdsId(),
		                    new String[][] {{"message", buffer.toString()}});
					
					throw new Exception(buffer.toString());
					
				}
				
				if (tbName.equals("S001")) {
					log.debug("[S001] len=[" + len + "] data=>\n" + Util.getHexString(b));
					// Parse
					st001 = new ST001(b);

//					meter.setModel(DeviceModel.);
					StringBuilder sb = new StringBuilder();
					sb.append("ST001[ \n")
					        .append("  MANUFACTURER=" + st001.getMANUFACTURER() + ", \n")
							.append("  ED_MODEL=" + st001.getED_MODEL() + ", \n")
							.append("  HW_VERSION_NUMBER=" + st001.getHW_VERSION_NUMBER() + ", \n")
							.append("  HW_REVISION_NUMBER=" + st001.getHW_REVISION_NUMBER() + ", \n")
							.append("  FW_VERSION_NUMBER=" + st001.getFW_VERSION_NUMBER() + ", \n")
							.append("  FW_REVISION_NUMBE=" + st001.getFW_REVISION_NUMBER() + ", \n")
							.append("  MSerial=" + st001.getMSerial() + "\n] \n");
					log.debug(sb.toString());
					
					// Set Veriables
					meterId = st001.getMSerial();
					meterDeviceModelName = st001.getED_MODEL();
				} else if (tbName.equals("M019")) {
					log.debug("[M019] len=[" + len + "] data=>\n" + Util.getHexString(b));
					mt019 = new MT019(b);
					log.debug(mt019.printAll());
					
					// Set Veriables
					START_CHR = mt019.getSTART_CHR();
					TOTAL_DEL_KWH = mt019.getTOTAL_DEL_KWH();
					TOTAL_DEL_PLUS_RCVD_KWH = mt019.getTOTAL_DEL_PLUS_RCVD_KWH();
					TOTAL_DEL_MINUS_RCVD_KWH = mt019.getTOTAL_DEL_MINUS_RCVD_KWH();
					TOTAL_REC_KWH = mt019.getTOTAL_REC_KWH();
				} else if (tbName.equals("M115")) {
					log.debug("[M115] len=[" + len + "] data=>\n" + Util.getHexString(b));
					mt115 = new MT115(b);
					ACTUAL_SWITCH_STATE = mt115.getACTUAL_SWITCH_STATE();
					log.debug(mt115.printAll());
				} else if (tbName.equals("N509")) {
					log.debug("[N509] len=[" + len + "] data=>\n" + Util.getHexString(b));
					NT509 nt509 = new NT509(b);
					nt509List.add(nt509);
					log.debug(nt509.printAll());

					// Set Veriables
					meteringTime = nt509.getFrameInfoDateFormat("yyyyMMddHHmmss");
					dst = nt509.getDst();
					lpDataList.addAll(nt509.getLpData());
					if (meter != null)
						meter.setLpInterval(nt509.getLpPeriodMin());
					else
						log.debug("meter is null! Can not set LpInterval.");
				} else {
					log.debug("unknown table=[" + tbName + "] data=>\n" + Util.getHexString(b));
				}
			}catch(ArrayIndexOutOfBoundsException be) {
				log.error(be, be);				
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		log.debug("I210+ Data Parse Finished :: DATA[" + toString() + "]");
	}

	public String checkTableLength(String tbName, byte[] b) {
		String reStr = null;
		
		if (tbName.equals("Total")) {
			if(b.length != 148) {
				reStr = "Invalid Metering Data length | length : " + b.length +"/148, data : " +Hex.decode(b);  
			}
		} else if (tbName.equals("S001")) {
			if(b.length != 32) {
				reStr = "Invalid Table Data length | '" + tbName +"' length : " + b.length +"/32, data : " +Hex.decode(b);  
			}
		} else if (tbName.equals("M019")) {
			if(b.length != 40) {
				reStr = "Invalid Table Data length | '" + tbName +"' length : " + b.length +"/40, data : " +Hex.decode(b);
			}
		} else if (tbName.equals("M115")) {
			if(b.length != 24) {
				reStr = "Invalid Table Data length | '" + tbName +"' length : " + b.length +"/24, data : " +Hex.decode(b);
			}
		} else if (tbName.equals("M114")) {
			if(b.length != 28) {
				reStr = "Invalid Table Data length | '" + tbName +"' length : " + b.length +"/28, data : " +Hex.decode(b);
			}
		}
		
		return reStr;
	}
	
	public Double getTOTAL_DEL_KWH() {
		return TOTAL_DEL_KWH;
	}

	public Double getTOTAL_DEL_PLUS_RCVD_KWH() {
		return TOTAL_DEL_PLUS_RCVD_KWH;
	}

	public Double getTOTAL_DEL_MINUS_RCVD_KWH() {
		return TOTAL_DEL_MINUS_RCVD_KWH;
	}

	public Double getTOTAL_REC_KWH() {
		return TOTAL_REC_KWH;
	}
		
	public boolean getACTUAL_SWITCH_STATE() {
		return ACTUAL_SWITCH_STATE;
	}

	public int getDst() {
		return dst;
	}

	public int getFlag() {
		return this.flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("I210+ Meter DATA[");
		sb.append("(meterId=").append(meterId).append("),");
		// sb.append("(meterSerial=").append(meterSerial).append(")");
		sb.append("]\n");

		return sb.toString();
	}

	public String getLPChannelMap() {
//        try{
//            UNIT_OF_MTR unit_of_mtr = new UNIT_OF_MTR();
//            if(s062!= null && s012 != null){
//                int[] sel_select = st062.getLP_SEL_SET1();
//                String[] uom_code = st012.getUOM_CODE(sel_select);
//                
//                for(int i = 0; i < uom_code.length; i++){
//                    log.info("uom_code="+uom_code[i]);
//                }
//
//                return unit_of_mtr.getChannelMap(uom_code);
//            }
//        }catch(Exception e){
//            log.warn(e);
//        }
		return "";
	}

	public int getLPChannelCount() {
//        try{
//            if(st061 != null){
//                return st061.getNBR_CHNS_SET1()*2+1;
//            }
//            else{
//                return 5; //ch1,ch2,v1,v2,pf
//            }
//        } catch(Exception e){ }
		return 5; // ch1,ch2,v1,v2,pf
	}

	public int getLPChannelCount(String model) {
//        try{
//            if(st061 != null){
//                return st061.getNBR_CHNS_SET1()*2+1;
//            }
//            else{
//            	if(model.equals("12"))
//            		return 3;//ch1,ch2,v1,v2,pf
//            	else
//            		return 5;
//            }
//        } catch(Exception e){
//        	log.error(e,e);
//        }

		if (model.equals("12"))
			return 3;// ch1,ch2,v1,v2,pf
		else
			return 5;
	}

	public int getResolution() {
//        try{
//            if(st061 != null){
//                return st061.getINT_TIME_SET1();
//            }
//            else{
//                return Integer.parseInt(FMPProperty.getProperty("def.lp.resolution"));
//            }
//        } catch(Exception e){
//        	log.error(e,e);
//        }
		return 60;
	}

	public String getMeterLog() {
//        if(st003 != null){
//            return st003.getMeterLog();
//        }else{
		return "";
//        }
	}

	public EventLogData[] getMeterStatusLog() {
//        if(st003 != null){
//            return st003.getEventLog();
//        }else{
		return null;
//        }
	}

	public MeterStatus getMeterStatusCode() {
//        if (st003 != null) {
//            return st003.getStatus();
//        }
//        else {
		return MeterStatus.Normal;
//        }
	}

	public LPData[] getLPData() {
		//return lpDataList.toArray(new LPData[0]);

		Double[] ch = new Double[1];
		ch[0] = (this.getTOTAL_DEL_KWH() / 10000);

		String time = this.getMeteringTime().substring(0, 10) + "0000";
		
		LPData lpData = new LPData();
		lpData.setBasePulse(0);
		lpData.setBaseValue(0);
		lpData.setDatetime(time);		
		lpData.setLPChannelCnt(1);
		lpData.setCh(ch);
		
		return new LPData[] {lpData};
	}

	public TOU_BLOCK[] getPrevBilling() {
//        try{
//            if(st025 != null)
//                return st025.getTOU_BLOCK();
//            else if(nt025 != null)
//                return nt025.getTOU_BLOCK();
//            else
//                return null;
//        } catch(Exception e){
//            log.warn("SM110 get Prev Billing Error:"+e.getMessage());
//        }
		return null;
	}

	public TOU_BLOCK[] getCurrBilling() {
//        try{
//            if(st023 != null)
//                return st023.getTOU_BLOCK();
//            else if(nt023 != null)
//                return nt023.getTOU_BLOCK();
//            else
//                return null;
//        } catch(Exception e){
//            log.warn("SM110 get Curr Billing Error:"+e.getMessage());
//        }
		return null;
	}

	public ArrayList getSelfReads() {

//        TOU_BLOCK[] tou = null;
//        ArrayList list = new ArrayList();
//        try {
//            if(st026 != null) {
//                ST025[] st025s = st026.getSelfReads();
//
//                for(int i = 0; st025s != null && i < st025s.length; i++){
//                    tou = st025s[i].getTOU_BLOCK();
//                    list.add(tou);
//                    //for(int k = 0; k < tou.length; k++){
//                    //    list.add(tou[k]);
//                    //}
//                }
//                return list;
//                //return (TOU_BLOCK[])list.toArray(new TOU_BLOCK[list.size()]);
//            } else  if(nt026 != null) {
//                NT025[] nt025s = nt026.getSelfReads();
//
//                for(int i = 0; nt025s != null && i < nt025s.length; i++){
//                    tou = nt025s[i].getTOU_BLOCK();
//                    list.add(tou);
//                    //for(int k = 0; k < tou.length; k++){
//                    //    list.add(tou[k]);
//                    //}
//                }
//                return list;
//                //return (TOU_BLOCK[])list.toArray(new TOU_BLOCK[list.size()]);
//            } else {
//                return null;
//            }
//        } catch(Exception e) {
//            log.warn("SM110 get Self Read Error:",e);
//        }
		return null;
	}

	public LinkedHashMap getRelayStatus() {
        try{
            if(mt115 != null){
				LinkedHashMap res = new LinkedHashMap(2);
            	res.put("ACTUAL_SWITCH_STATE", mt115.getACTUAL_SWITCH_STATE());
            	return res;
            }

		/*
		 * if(mt115 == null){ if(mt117 != null){ return mt117.getData(); } }else{ return
		 * mt115.getData(); }
		 */
        } catch(Exception e){
            log.warn("I210Plus get RelayStatus Error:"+e.getMessage());
        }
		return null;
	}

	/*
	 * public EventLogData[] getRelayEventLog(){ try{ if(st132 != null ) return
	 * st132.getEventLog(); else return null; } catch(Exception e){
	 * log.warn("SM110 get RelayEvent Error:"+e.getMessage()); } return null; }
	 */

	public EventLogData[] getEventLog() {
//        if(st076 != null){
//            return st076.getEvent();
//        }else{
		return null;
//        }
	}

	public Instrument[] getInstrument() {
//        if(mt113 != null){
//            return mt113.getInstrument();
//        }else{
		return null;
//        }
	}

	/**
	 * get Data
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LinkedHashMap getData() {
		LinkedHashMap res = new LinkedHashMap(16, 0.75f, false);
		TOU_BLOCK[] tou_block = null;
		LPData[] lplist = null;
		EventLogData[] evlog = null;
		String meter_mode = null;

		DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
		// 날짜 포멧팅
		SimpleDateFormat normalDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

		SimpleDateFormat datef14 = null;

		if (meter != null && meter.getSupplier() != null) {
			Supplier supplier = meter.getSupplier();
			if (supplier != null) {
				String lang = supplier.getLang().getCode_2letter();
				String country = supplier.getCountry().getCode_2letter();

				TimeLocaleUtil.getDecimalFormat(supplier);
				datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
			}
		} else {
			new DecimalFormat();
			datef14 = new SimpleDateFormat();
		}

		try {
			tou_block = getCurrBilling();
			lplist = getLPData();
			evlog = getEventLog();
			meter_mode = getMeterMode();

			res.put("<b>[Meter Configuration Data]</b>", "");
			if (st001 != null) {
				res.put("Manufacturer", st001.getMANUFACTURER());
				res.put("Model", st001.getED_MODEL());
				res.put("Manufacturer Serial Number", st001.getMSerial());
				res.put("HW Version Number", df3.format(st001.getHW_VERSION_NUMBER()) + "");
				res.put("HW Revision Number", df3.format(st001.getHW_REVISION_NUMBER()) + "");
				res.put("FW Version Number", df3.format(st001.getFW_VERSION_NUMBER()) + "");
				res.put("FW Revision Number", df3.format(st001.getFW_REVISION_NUMBER()) + "");
			}
//            if(st005 != null)
//                res.put("Device Serial Number",st005.getMSerial());
//            if(st003 != null)
//                res.put("Meter Log",st003.getMeterLog());
//            if(st055 != null){            	
//            	Date meterDate = normalDateFormat.parse(st055.getDateTime());
//                res.put("Meter Time",datef14.format(meterDate));
//                res.put("DST Apply Flag",st055.getDstApplyOnName());
//                res.put("DST Flag",st055.getDstSeasonOnName());
//            }
//            if(st052 != null){            	
//            	Date meterDate = normalDateFormat.parse(st052.getDateTime());
//                res.put("Meter Time",datef14.format(meterDate));
//                res.put("DST Apply Flag",st052.getDstApplyOnName());
//                res.put("DST Flag",st052.getDstSeasonOnName());
//            }
//            if(mt067 != null){
//                res.put("Current Transform Ratio",df3.format(mt067.getCUR_TRANS_RATIO())+"");
//                res.put("Voltage Transform Ratio",df3.format(mt067.getPOT_TRANS_RATIO())+"");
//            }
//            if(nt067 != null){
//                res.put("Current Transform Ratio",df3.format(nt067.getCUR_TRANS_RATIO())+"");
//                res.put("Voltage Transform Ratio",df3.format(nt067.getPOT_TRANS_RATIO())+"");
//            }
//            if(mt070 != null){
//                res.put("Display Multiplier", df3.format(mt070.getDISP_MULTIPLIER())+"");
//                res.put("Display Scalar", df3.format(mt070.getDISP_SCALAR())+"");
//            }
//            if(mt075 != null){
//                res.put("[Scale Factor]", "");
//                res.put("line-to-neutral voltages", df3.format(mt075.getI_SQR_HR_SF())+"");
//                res.put("line-to-line voltages", df3.format(mt075.getV_RMS_SF())+"");
//                res.put("Current", df3.format(mt075.getV_SQR_HR_LN_SF())+"");
//                res.put("Power Scale", df3.format(mt075.getVA_SF())+"");
//                res.put("Energy Scale", df3.format(mt075.getVAH_SF())+"");
//            }
//            if(mt113 != null){
//                res.put("[Momentary Phase]", "");
//                res.put("Interval phase A/C Voltage", df3.format(mt113.getRMS_VOLTAGE_PHA())+"/"+df3.format(mt113.getRMS_VOLTAGE_PHC()));
//                res.put("Interval Power Factor(%)", df3.format(mt113.getMOMENTARY_INTERVAL_PF())+"");
//            }
//            if(mt078 != null){
//                res.put("[Power Outage Information]", "");
//                res.put("Last Power Outage Date",mt078.getDT_LAST_POWER_OUTAGE());
//                res.put("Cummulative Power Outage(Seconds)", df3.format(mt078.getCUM_POWER_OUTAGE_SECS())+"");
//                res.put("Number Of Power Outages", df3.format(mt078.getNBR_POWER_OUTAGES())+"");
//            }
//            if(nt078 != null){
//                res.put("[Power Outage Information]", "");
//                res.put("Last Power Outage Date",nt078.getDT_LAST_POWER_OUTAGE());
//                res.put("Cummulative Power Outage(Seconds)", df3.format(nt078.getCUM_POWER_OUTAGE_SECS())+"");
//                res.put("Number Of Power Outages", df3.format(nt078.getNBR_POWER_OUTAGES())+"");
//            }
//            if(st112 != null){
//                res.put("[Relay Status]", "");
//                res.put("Relay status" , st112.getRelayStatusString());
//                res.put("Relay activate status" , st112.getRelayActivateStatusString());
//            }

			/*
			 * if(mt115 != null){ res.put("[Relay Status]", ""); res.put("Relay status" ,
			 * mt115.getRelayStatusString()); res.put("Relay activate status" ,
			 * mt115.getRelayActivateStatusString()); }
			 * 
			 * if(mt117 != null){ res.put("[Relay Status]", ""); res.put("Relay status" ,
			 * mt117.getRelayStatusString()); res.put("Relay activate status" ,
			 * mt117.getRelayActivateStatusString()); }
			 */

			if (tou_block != null && tou_block.length > 0 && tou_block[0] != null
					&& tou_block[0].getSummations() != null && tou_block[0].getSummations().size() > 0) {

				try {
					res.put("[Current Billing Data]", "");
					res.put("Total Active Energy(kWh)", df3.format(tou_block[0].getSummation(0)));
					meteringValue = new Double(df3.format(tou_block[0].getSummation(0)));
					res.put("Total Reactive Energy(kWh)", df3.format(tou_block[0].getSummation(1)));
					res.put("Total Active Power Max.Demand(kW)", df3.format(tou_block[0].getCurrDemand(0)));
					res.put("Total Active Power Max.Demand Time", tou_block[0].getEventTime(0));
					res.put("Total Reactive Power Max.Demand(kW)", df3.format(tou_block[0].getCurrDemand(1)));
					res.put("Total Reactive Power Max.Demand Time", tou_block[0].getEventTime(1));
					res.put("Total Active Power Cum.Demand(kW)", df3.format(tou_block[0].getCumDemand(0)));
					res.put("Total Reactive Power Cum.Demand(kW)", df3.format(tou_block[0].getCumDemand(1)));

					if (tou_block[0].getCoincident() != null && tou_block[0].getCoincident().size() > 0) {
						res.put("Total Active Power Cont.Demand(kW)", df3.format(tou_block[0].getCoincident(0)));
						res.put("Total Reactive Power Cont.Demand(kW)", df3.format(tou_block[0].getCoincident(1)));
					}

					if (tou_block.length >= 2) {
						res.put("Rate A Active Energy(kWh)", df3.format(tou_block[1].getSummation(0)));
						res.put("Rate A Reactive Energy(kWh)", df3.format(tou_block[1].getSummation(1)));
						res.put("Rate A Active Power Max.Demand(kW)", df3.format(tou_block[1].getCurrDemand(0)));
						res.put("Rate A Active Power Max.Demand Time", tou_block[1].getEventTime(0));
						res.put("Rate A Reactive Power Max.Demand(kW)", df3.format(tou_block[1].getCurrDemand(1)));
						res.put("Rate A Reactive Power Max.Demand Time", tou_block[1].getEventTime(1));
						res.put("Rate A Active Power Cum.Demand(kW)", df3.format(tou_block[1].getCumDemand(0)));
						res.put("Rate A Reactive Power Cum.Demand(kW)", df3.format(tou_block[1].getCumDemand(1)));

						if (tou_block[1].getCoincident() != null && tou_block[1].getCoincident().size() > 0) {
							res.put("Rate A Active Power Cont.Demand(kW)", df3.format(tou_block[1].getCoincident(0)));
							res.put("Rate A Reactive Power Cont.Demand(kW)", df3.format(tou_block[1].getCoincident(1)));
						}
					}

					if (tou_block.length >= 3) {
						res.put("Rate B Active Energy(kWh)", df3.format(tou_block[2].getSummation(0)));
						res.put("Rate B Reactive Energy(kWh)", df3.format(tou_block[2].getSummation(1)));
						res.put("Rate B Active Power Max.Demand(kW)", df3.format(tou_block[2].getCurrDemand(0)));
						res.put("Rate B Active Power Max.Demand Time", tou_block[2].getEventTime(0));
						res.put("Rate B Reactive Power Max.Demand(kW)", df3.format(tou_block[2].getCurrDemand(1)));
						res.put("Rate B Reactive Power Max.Demand Time", tou_block[2].getEventTime(1));
						res.put("Rate B Active Power Cum.Demand(kW)", df3.format(tou_block[2].getCumDemand(0)));
						res.put("Rate B Reactive Power Cum.Demand(kW)", df3.format(tou_block[2].getCumDemand(1)));

						if (tou_block[2].getCoincident() != null && tou_block[2].getCoincident().size() > 0) {
							res.put("Rate B Active Power Cont.Demand(kW)", df3.format(tou_block[2].getCoincident(0)));
							res.put("Rate B Reactive Power Cont.Demand(kW)", df3.format(tou_block[2].getCoincident(1)));
						}
					}

					if (tou_block.length >= 4) {
						res.put("Rate C Active Energy(kWh)", df3.format(tou_block[3].getSummation(0)));
						res.put("Rate C Reactive Energy(kWh)", df3.format(tou_block[3].getSummation(1)));
						res.put("Rate C Active Power Max.Demand(kW)", df3.format(tou_block[3].getCurrDemand(0)));
						res.put("Rate C Active Power Max.Demand Time", tou_block[3].getEventTime(0));
						res.put("Rate C Reactive Power Max.Demand(kW)", df3.format(tou_block[3].getCurrDemand(1)));
						res.put("Rate C Reactive Power Max.Demand Time", tou_block[3].getEventTime(1));
						res.put("Rate C Active Power Cum.Demand(kW)", df3.format(tou_block[3].getCumDemand(0)));
						res.put("Rate C Reactive Power Cum.Demand(kW)", df3.format(tou_block[3].getCumDemand(1)));
						if (tou_block[3].getCoincident() != null && tou_block[3].getCoincident().size() > 0) {
							res.put("Rate C Active Power Cont.Demand(kW)", df3.format(tou_block[3].getCoincident(0)));
							res.put("Rate C Reactive Power Cont.Demand(kW)", df3.format(tou_block[3].getCoincident(1)));
						}
					}
				} catch (Exception e) {
					log.warn(e, e);
				}

			}

			if (meter_mode != null) {
				res.put("Meter Mode", meter_mode);
			}

//            if(s012 != null && s061!= null && st062!= null){
//                res.put("LP Channel Information", getLPChannelMap());
//            }

			if (lplist != null && lplist.length > 0) {
				res.put("[Load Profile Data(kWh)]", "");
				int nbr_chn = 1;// ch
//                if(st061 != null){
//                    nbr_chn = st061.getNBR_CHNS_SET1();
//                }
				// ArrayList chartData0 = new ArrayList();//time chart
				// ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
				// for(int k = 0; k < nbr_chn ; k++){
				// chartDatas[k] = new ArrayList();
				// }
				ArrayList lpDataTime = new ArrayList();
				for (int i = 0; i < lplist.length; i++) {
					String datetime = lplist[i].getDatetime();

					if (meter != null && meter.getSupplier() != null) {
						Supplier supplier = meter.getSupplier();
						if (supplier != null) {
							String lang = supplier.getLang().getCode_2letter();
							String country = supplier.getCountry().getCode_2letter();

							TimeLocaleUtil.getDecimalFormat(supplier);
							datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
						}
					} else {
						new DecimalFormat();
						datef14 = new SimpleDateFormat();
					}
					datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime + "00"));

					lplist[i].getDatetime();
					String val = "";
					Double[] ch = lplist[i].getCh();
					for (int k = 0; k < ch.length; k++) {
						val += "<span style='margin-right: 40px;'>ch" + (k + 1) + "=" + df3.format(ch[k]) + "</span>";
					}

					Date meterDate = normalDateFormat.parse(datetime + "00");
					res.put(datef14.format(meterDate), val);

					// chartData0.add(tempDateTime.substring(6,8)
					// +tempDateTime.substring(8,10)
					// +tempDateTime.substring(10,12));
					// for(int k = 0; k < ch.length ; k++){
					// chartDatas[k].add(ch[k].doubleValue());
					// }
					lpDataTime.add(lplist[i].getDatetime());
				}

				// res.put("chartData0", chartData0);
				// for(int k = 0; k < chartDatas.length ; k++){
				// res.put("chartData"+(k+1), chartDatas[k]);
				// }
				// res.put("lpDataTime", lpDataTime);
				// res.put("chartDatas", chartDatas);
				res.put("[ChannelCount]", nbr_chn);
			}

			if (evlog != null && evlog.length > 0) {
				res.put("[Event Log]", "");
				for (int i = 0; i < evlog.length; i++) {
					String datetime = evlog[i].getDate() + evlog[i].getTime();
					if (!datetime.startsWith("0000") && !datetime.equals("")) {
						Date meterDate = normalDateFormat.parse(datetime + "00");
						res.put(datef14.format(meterDate), evlog[i].getMsg());
					}
				}
			}

		} catch (Exception e) {
			log.warn("Get Data Error=>", e);
		}

		return res;
	}

	public int getDstApplyOn() throws Exception {
//        if(st055!= null){
//            return st055.getDstApplyOn();
//        }else if(st052!= null){
//        	return st052.getDstApplyOn();
//        }else{
		return 0;
//        }

	}

	public int getDstSeasonOn() throws Exception {
//        if(st052!= null){
//            return st052.getDstSeasonOn();
//        }
//        else if(st055!= null){
//            return st055.getDstSeasonOn();
//        }else{
		return 0;
//        }

	}

	public String getMeterMode() throws Exception {
//        if(mt000 != null){
//            return mt000.getMETER_MODE_NAME();
//        }else{
		return null;
//        }
	}

	public String getTimeDiff() throws Exception {
//        if(st055 != null && nt055 != null) {
//            return (int)((nt055.getTime() - st055.getTime())/1000)+"";
//        }else if(st052 != null && nt055 != null) {
//            return (int)((nt055.getTime() - st052.getTime())/1000)+"";
//        } else {
		return null;
//        }
	}

	public MeterTimeSyncData getMeterTimeSync() {

		new MeterTimeSyncData();

		try {
//            if(st055 != null && bt055 != null && at055 != null){
//                String meterTime = st055.getDateTime();
//                String beforeTime = bt055.getDateTime();
//                String afterTime = at055.getDateTime();
//                int timeDiff = (int)((TimeUtil.getLongTime(afterTime)
//                        - TimeUtil.getLongTime(beforeTime))/1000);
//
//                meterTimeSyncData.setId(st001.getMSerial());
//                meterTimeSyncData.setAtime(afterTime);
//                meterTimeSyncData.setBtime(beforeTime);
//                meterTimeSyncData.setCtime(meterTime);
//                meterTimeSyncData.setEtime(meterTime);
//                meterTimeSyncData.setMethod(1);//auto
//                meterTimeSyncData.setResult(0);//success
//                meterTimeSyncData.setTimediff(timeDiff);
//                meterTimeSyncData.setUserID("AUTO Synchronized");
//                return meterTimeSyncData;
//            }
//            else if(st052 != null && bt055 != null && at055 != null){
//                String meterTime = st052.getDateTime();
//                String beforeTime = bt055.getDateTime();
//                String afterTime = at055.getDateTime();
//                int timeDiff = (int)((TimeUtil.getLongTime(afterTime)
//                        - TimeUtil.getLongTime(beforeTime))/1000);
//
//                meterTimeSyncData.setId(st001.getMSerial());
//                meterTimeSyncData.setAtime(afterTime);
//                meterTimeSyncData.setBtime(beforeTime);
//                meterTimeSyncData.setCtime(meterTime);
//                meterTimeSyncData.setEtime(meterTime);
//                meterTimeSyncData.setMethod(1);//auto
//                meterTimeSyncData.setResult(0);//success
//                meterTimeSyncData.setTimediff(timeDiff);
//                meterTimeSyncData.setUserID("AUTO Synchronized");
//                return meterTimeSyncData;
//            }
//            else
//            {
//                return null;
//            }
		} catch (Exception e) {
			log.warn("get meter time sync log error: " + e.getMessage());
		}
		return null;
	}
/*
	public boolean isSavingLP() {

		try {
//            if(st063 != null){
//                int blkCnt = st063.getNBR_VALID_BLOCKS();
//                if(blkCnt == 0){
//                    return true;
//                }
//            }
		} catch (Exception e) {
			log.warn("Get valid lp block count Error=>" + e.getMessage());
		}
		return false;
	}
*/
	
	public boolean isSavingLP() {
		if(STD_START_CHAR.equals(START_CHR)) {
			return true;
		} else {
			log.info("ERROR START CHR. please check hex.");
			return false;
		}
		
	}
	
	
	public MeteringFail getMeteringFail() {
//        if(nuri_t002 != null){
//            return nuri_t002.getMeteringFail();
//        }else{
		return null;
//        }
	}
	
	public String getParsingResult(byte[] data) throws Exception {
		StringBuffer result = new StringBuffer();
		rawData = data;
		int totlen = data.length;
		result.append("TOTLEN[" + totlen + "]"+System.getProperty("line.separator"));

		int offset = 0;
		while (offset + 6 < totlen) {
			result.append("OFFSET[" + offset + "]"+System.getProperty("line.separator"));

			String tbName = new String(data, offset, 4);
			offset += 4;
			int len = 0;
			len |= (data[offset++] & 0xff) << 8;
			len |= (data[offset++] & 0xff);
			byte[] b = new byte[len];

			if (data.length - offset < len)
				break;

			System.arraycopy(data, offset, b, 0, len);
			offset += len;

			try {
				if (tbName.equals("S001")) {
					result.append("[S001] len=[" + len + "] data=>"+System.getProperty("line.separator") + Util.getHexString(b));
					// Parse
					st001 = new ST001(b);

//					meter.setModel(DeviceModel.);
					StringBuilder sb = new StringBuilder();
					sb.append("ST001[ ")
					        .append("  MANUFACTURER=" + st001.getMANUFACTURER() + ", "+System.getProperty("line.separator"))
							.append("  ED_MODEL=" + st001.getED_MODEL() + ", "+System.getProperty("line.separator"))
							.append("  HW_VERSION_NUMBER=" + st001.getHW_VERSION_NUMBER() + ", "+System.getProperty("line.separator"))
							.append("  HW_REVISION_NUMBER=" + st001.getHW_REVISION_NUMBER() + ", "+System.getProperty("line.separator"))
							.append("  FW_VERSION_NUMBER=" + st001.getFW_VERSION_NUMBER() + ", "+System.getProperty("line.separator"))
							.append("  FW_REVISION_NUMBE=" + st001.getFW_REVISION_NUMBER() + ", "+System.getProperty("line.separator"))
							.append("  MSerial=" + st001.getMSerial() + "] "+System.getProperty("line.separator"));
					result.append(sb.toString()+System.getProperty("line.separator"));
					
					// Set Veriables
					meterId = st001.getMSerial();
					meterDeviceModelName = st001.getED_MODEL();
					
					
				} else if (tbName.equals("M019")) {
					result.append("[M019] len=[" + len + "] data=>"+System.getProperty("line.separator") + Util.getHexString(b)+System.getProperty("line.separator"));
					mt019 = new MT019(b);
					result.append(mt019.printAll()+System.getProperty("line.separator"));
					
					// Set Veriables
					TOTAL_DEL_KWH = mt019.getTOTAL_DEL_KWH();
					TOTAL_DEL_PLUS_RCVD_KWH = mt019.getTOTAL_DEL_PLUS_RCVD_KWH();
					TOTAL_DEL_MINUS_RCVD_KWH = mt019.getTOTAL_DEL_MINUS_RCVD_KWH();
					TOTAL_REC_KWH = mt019.getTOTAL_REC_KWH();
				} else if (tbName.equals("M115")) {
					result.append("[M115] len=[" + len + "] data=>"+System.getProperty("line.separator") + Util.getHexString(b)+System.getProperty("line.separator"));
					mt115 = new MT115(b);
					result.append(mt115.printAll()+System.getProperty("line.separator"));
				} else if (tbName.equals("N509")) {
					result.append("[N509] len=[" + len + "] data=>"+System.getProperty("line.separator") + Util.getHexString(b)+System.getProperty("line.separator"));
					NT509 nt509 = new NT509(b);
					nt509List.add(nt509);
					result.append(nt509.printAll()+System.getProperty("line.separator"));

					// Set Veriables
					meteringTime = nt509.getFrameInfoDateFormat("yyyyMMddHHmmss");
					dst = nt509.getDst();
					lpDataList.addAll(nt509.getLpData());
					if (meter != null)
						meter.setLpInterval(nt509.getLpPeriodMin());
					else
						result.append("meter is null! Can not set LpInterval."+System.getProperty("line.separator"));
				} else {
					result.append("unknown table=[" + tbName + "] data=>"+System.getProperty("line.separator") + Util.getHexString(b)+System.getProperty("line.separator"));
				}
			} catch (Exception e) {
				log.error(e, e);
			}
		}
		result.append("I210+ Data Parse Finished :: DATA[" + toString() + "]"+System.getProperty("line.separator"));
		
		
		return result.toString();
	}
	
}
