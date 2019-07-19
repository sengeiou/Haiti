package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * Load Recent Data <br>
 * 
 * <p>
 * LP Data는 4800 records를 저장하고 있으며 최대 33개 채널 데이터를 기록한다. 1개의 record 의 Size는
 * 97바이트이며 LP Interval은 15분, 1Day 의 경우 96개의 Record를 갖게된다.
 * </p>
 * 
 * <p>
 * Power Quality(Instrument data)가 함깨 전송받기때문에 같이 처리해준다.
 * </p>
 * 
 * <p>
 * 본 Class 의 기본 기능은 포맷에 맞는 Byte Array <=> Object로 변환하는 기능.
 * </p>
 * 
 * @author kskim
 * @see <참고문서><br>
 *      <ul>
 *      <li>MX2_AMR_Communication_Specification-2011-06-16_Signed.pdf</li>
 *      <li>NAMR_P213GP(2011)_Protocol.doc</li>
 *      </ul>
 */

public class MX2LPData extends CommonTable implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3020892178915661476L;

	private static Log log = LogFactory.getLog(MX2LPData.class);
	
	private double energyUnit = 0.0;
	private double energyDecimal = 0.0;
	private double demandUnit = 0.0;
	private double demandDecimal = 0.0;

	public MX2LPData() {}
	
	/**
	 * lpdata struct
	 */
	private Map<String, LPData> lpData = new HashMap<String, LPData>();

	private int period;

	/**
	 * Power Quality(Instrument data)
	 */
	private Map<String, Instrument> instrument = new HashMap<String, Instrument>();

	private String flagMsg = null;

	private int[] flagStatus = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	private int[] tamper_Indicator = new int[]{0,0,0,0,0,0,0,0};
	
	private final String[] flagMessages = new String[] { "Time change",
			"Manual Demand reset", "Self-reading", "Measuring Data clear",
			"Power fail passing Demand interval",
			"Power fail passing Self-reading", "Power fail passing RTC reset",
			"MCU/ASIC power fail and/or reset", "Voltage Cable Loose Status",
			"Wiring wrong status", "Unbalance voltage status", "Abnormal voltage status",
			"Battery fail status", "", "",
			"Normal Incompletion" };

	private final String[] indicatorMessage = new String[] { "Reverse current",
			"Terminal block opened", "Front cover opened", "Meter box opened",
			"Low Battery", "", "", "" };

	public enum LR_FLAG_STATUS {
	    TIME_CHANGE,
	    MANUAL_DEMAND_RESET,
	    SELF_READING,
	    MEASURING_DATA_CLEAR,
	    POWER_FAIL_PASSING_DEMAND_INTERVAL,
	    POWER_FAIL_PASSING_SELF_READING,
	    POWER_FAIL_PASSING_RTC_RESET,
	    MCU_ASIC_POWER_FAIL_AND_OR_RESET,
	    VOLTAGE_CABLE_LOOSE,
	    WIRING_WRONG,
	    UNBALANCE_VOLTAGE,
	    ABNORMAL_VOLTAGE,
	    RESERVED_12,
	    RESERVED_13,
	    RESERVED_14,
	    NORMAL_INCOMPLETION;
	}
	
	public enum LOAD_TAMPER_INDICATOR {
	    REVERSE_CURRENT,
	    TERMINAL_BLOCK_OPENED,
	    FRONT_COVER_OPENED,
	    METER_BOX_OPENED,
	    LOW_BATTERY,
	    UNUSED_5,
	    UNUSED_6,
	    UNUSED_7;
	}
	
	public double getEnergyUnit() {
        return energyUnit;
    }

    public void setEnergyUnit(double energyUnit) {
        this.energyUnit = energyUnit;
    }

    public double getEnergyDecimal() {
        return energyDecimal;
    }

    public void setEnergyDecimal(double energyDecimal) {
        this.energyDecimal = energyDecimal;
    }

    public double getDemandUnit() {
        return demandUnit;
    }

    public void setDemandUnit(double demandUnit) {
        this.demandUnit = demandUnit;
    }

    public double getDemandDecimal() {
        return demandDecimal;
    }

    public void setDemandDecimal(double demandDecimal) {
        this.demandDecimal = demandDecimal;
    }


    /**
	 * lpdata byte array 를 받아 LPData 로 파싱한다.
	 * 
	 * @param lpdata
	 *            미터에서 전송받은 데이터
	 */
	public MX2LPData(byte[] lpdata, double lmax) {
		// log.debug("lpdata : " + Hex.getHexDump(lpdata));
		ByteArrayInputStream bis = new ByteArrayInputStream(lpdata);
		double currentLsb = 0.01;
		if (lmax >= 100) currentLsb = 0.1;
		
		try {

		    String strYYYYMMDDHHMM = "";
			while (bis.available() > 0) {
				LPData lpData = null;
				Instrument instrument = null;
				/* LP Data 부분 */
				// date 부분 5 size 읽어와 파싱
				byte[] yyyyMMddHHmm = new byte[5];
				bis.read(yyyyMMddHHmm);
				strYYYYMMDDHHMM = getDateToString(yyyyMMddHHmm);
				lpData = this.lpData.get(strYYYYMMDDHHMM);
				instrument = this.instrument.get(strYYYYMMDDHHMM);
				if (lpData == null) {
				    lpData = new LPData();
				    lpData.setDatetime(strYYYYMMDDHHMM);
				    instrument = new Instrument();
				    instrument.datetime = strYYYYMMDDHHMM;
				}
				log.debug(Hex.getHexDump(yyyyMMddHHmm));
				log.debug(instrument.datetime);

				// Energy 값의 단위를 읽어와 kilo 단위로 변환되는 승수를 구한다.
				byte _energyUnit = (byte) bis.read();
				log.debug("EnergyUnit[" + getMultiplier(_energyUnit) + "]");
				double energyMultiplier = getMultiplier(_energyUnit);
				if (energyUnit == 0.0) energyUnit = energyMultiplier;

				// Energy 소수점 을 구하기 위한 승수를 구한다.
				byte _energyDecimal = (byte) bis.read();
				
				log.debug("EnergyDecimal[" + getDecimal(_energyDecimal) + "]");
				double dEnergyDecimal = getDecimal(_energyDecimal);
				if (energyDecimal == 0.0) energyDecimal = dEnergyDecimal;

				// Demand 값의 단위를 읽어와 kilo 단위로 변환되는 승수를 구한다.
				byte _demandUnit = (byte) bis.read();
				log.debug("DemandUnit[" + getMultiplier(_demandUnit) + "]");
				double demandMultiplier = getMultiplier(_demandUnit);
				if (demandUnit == 0.0) demandUnit = demandMultiplier;

				// Demand 소수점 을 구하기 위한 승수를 구한다.
				byte _demandDecimal = (byte) bis.read();
				log.debug("DemandDecimal[" + getDecimal(_demandDecimal) + "]");
				double dDemandDecimal = getDecimal(_demandDecimal);
				if (demandDecimal == 0.0) demandDecimal = dDemandDecimal;

				// current decimal place - 사용 안함.
				bis.skip(1);

				// Interval BCD 타입이다.
				int bcdInterval = bis.read();
				this.period = Integer.parseInt(String.format("%x", bcdInterval));

				/**
				 * flag status bit<br>
				 * 비트 위치에 따라 1 = Active or 0 = Deactive 상태를 나타낸다.<br>
				 * bit0 = Time change <br>
				 * 1 = Manual Demand reset<br>
				 * 2 = Self-reading<br>
				 * 3 = Measuring Data clear<br>
				 * 4 = Power fail passing Demand interval<br>
				 * 5 = Power fail passing Self-reading<br>
				 * 6 = Power fail passing RTC reset<br>
				 * 7 = MCU/ASIC power fail and/or reset<br>
				 * 8 = Voltage Cable Loose Status<br>
				 * 9 = Wiring wrong status<br>
				 * 10 = Unbalance voltage status<br>
				 * 11 = Abnormal voltage status<br>
				 * 12 = Battery fail status<br>
				 * 13~14 is unused (All unused bit: 0)<br>
				 * 15 = Normal Incompletion<br>
				 */
				byte[] flag = new byte[2];
				bis.read(flag);
				int nFlag = byteArrayToInt(flag);
				lpData.setFlag(nFlag);

				int nIndicator = bis.read();
				log.debug("lp flag bit - " + Hex.getHexDump(flag));
				log.debug("lp flag int - " + nFlag);
				log.debug("lp indicator - " + nIndicator);
				// load tamper indicator

				// flag 메시지는 제일 처음 한번만 설정한다.
				// index에 해당하는 메시지 설정.
				if (this.lpData.size() == 0)
				    setFlagMsg(nFlag, nIndicator);

				// Energy (채널 데이터) size 21(each 3bytes) - 데이터 사이즈는 3이고 종류는
				// 7가지이다.
				// Wh(imp), Wh (exp), Varh Q1, Q2, Q3, Q4 and VAh (selectable).
				byte[] storage = new byte[21];
				bis.read(storage);
				Double[] energyChannels = getStorageValue(storage,
						dEnergyDecimal, energyMultiplier);

				// Demand (볼트 데이터) size 21(each 3bytes) - 데이터 사이즈는 3이고 종류는
				// 7가지이다.
				// W (imp), W (exp), Var Q1, Q2, Q3, Q4 and VA (selectable).
				bis.read(storage);
				Double[] demandVolts = getStorageValue(storage, dDemandDecimal,
						demandMultiplier);
				
				// Demand 값을 채널에 보여주기 위해 채널값에 추가한다.
				int channelCnt = energyChannels.length + demandVolts.length;
				Double[] channels = new Double[channelCnt];
				
				//두개의 배열을 합친다.
				System.arraycopy(energyChannels, 0, channels, 0, energyChannels.length);
				System.arraycopy(demandVolts, 0, channels, energyChannels.length, demandVolts.length);

				// v[] ch[] 설정
				if (lpData.getCh() == null) {
				    lpData.setCh(channels);
				}
				else {
				    for (int i = 0; i < channels.length; i++) {
				        lpData.getCh()[i] += channels[i];
				    }
				}
				if (lpData.getV() == null) {
				    lpData.setV(demandVolts);
				}
				else {
				    for (int i = 0; i < demandVolts.length; i++) {
				        lpData.getV()[i] += demandVolts[i];
				    }
				}
				lpData.setLPChannelCnt(channelCnt);
				/* Power Quality (Instrument) */
				// Voltage RMS
				byte[] volt = new byte[9];
				bis.read(volt);
				instrument.setVOL_A(byteArrayToInt(volt, 0, 3) * 0.01);
				instrument.setVOL_B(byteArrayToInt(volt, 3, 3) * 0.01);
				instrument.setVOL_C(byteArrayToInt(volt, 6, 3) * 0.01);

				// Current RMS
				byte[] currentRMS = new byte[12];
				bis.read(currentRMS);
				instrument.setCURR_A(byteArrayToInt(currentRMS, 0, 3) * currentLsb);
				instrument.setCURR_B(byteArrayToInt(currentRMS, 3, 3) * currentLsb);
				instrument.setCURR_C(byteArrayToInt(currentRMS, 6, 3) * currentLsb);

				// Power Factor
				byte[] powerFactor = new byte[8];
				bis.read(powerFactor);

				Double nPf = genPowerFactor(powerFactor, 0, 2);
				Double nPf_total = genPowerFactor(powerFactor, 0, 2);
				Double nPf_a = genPowerFactor(powerFactor, 2, 2);
				Double nPf_b = genPowerFactor(powerFactor, 4, 2);
				Double nPf_c = genPowerFactor(powerFactor, 6, 2);
				
				lpData.setPF(nPf);
				instrument.setPF_TOTAL(instrument.getPF_TOTAL() + nPf_total);
				instrument.setPF_A(nPf_a);
				instrument.setPF_B(nPf_b);
				instrument.setPF_C(nPf_c);

				// THD (Voltage)
				byte[] volTHD = new byte[6];
				bis.read(volTHD);
				instrument.setVOL_THD_A(byteArrayToInt(volTHD, 0, 2) * 0.01);
				instrument.setVOL_THD_B(byteArrayToInt(volTHD, 2, 2) * 0.01);
				instrument.setVOL_THD_C(byteArrayToInt(volTHD, 4, 2) * 0.01);

				// THD (Current)
				byte[] currTHD = new byte[6];
				bis.read(currTHD);
				instrument.setCURR_THD_A(byteArrayToInt(currTHD, 0, 2) * 0.01);
				instrument.setCURR_THD_B(byteArrayToInt(currTHD, 2, 2) * 0.01);
				instrument.setCURR_THD_C(byteArrayToInt(currTHD, 4, 2) * 0.01);

				this.lpData.put(strYYYYMMDDHHMM, lpData);
				this.instrument.put(strYYYYMMDDHHMM, instrument);
				// end
			}
			bis.close();
		} catch (Exception e) {
			log.debug(e, e);
		}

	}


	/**
	 * 값을 PowerFactor 값으로 변환한다.<br>
	 * signed byte
	 * <xmp>
	 * 0000h ~ 7FFFh and
	 * FFFFh ~ 8000h
	 * 
	 * 0000 = 000.00 %
	 * 7FFF = 327.67 %
	 * FFFF = -001.00 %(잘못된것일수있음)
	 * 8000 = -327.68 %
	 * </xmp>
	 * @param n
	 * @return
	 */
	private Double genPowerFactor(byte[] byteArray,int pos, int len) {
		
		byte[] bytes = new byte[len];
		System.arraycopy(byteArray, pos, bytes, 0, len);
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
    	
		return ((double) byteBuffer.getShort())*0.01;
	}


	public LPData[] getLPData() {
		return this.lpData.values().toArray(new LPData[0]);
	}

	public Instrument[] getInstrument() {
		return instrument.values().toArray(new Instrument[0]);
	}

	/**
	 * flag 메시지 + indicator 메시지
	 * 
	 * @return
	 */
	public String getFlagMsg() {
		return this.flagMsg;
	}
	
	/**
	 * flag 상태값을 반환한다.
	 * 
	 * @return 0~15배열의 0 또는 1
	 */
	public int[] getFlagStatus() {
	    return this.flagStatus;
	}
	
	/**
	 * method name : getTamperIndicator
	 * method Desc : TamperIndicator 상태값 반환
	 * @return
	 */
	public int[] getTamperIndicator(){
		return this.tamper_Indicator;
	}

	/**
	 * flag 값과 indicator 값으로 메시지를
	 * 
	 * @param flag
	 * @param indicator
	 */
	private void setFlagMsg(int flag, int indicator) {
	    log.debug("FLAG[" + flag + "] INDICATOR[" +  indicator + "]");
		StringBuffer tempString = new StringBuffer();

		// i 는 bit 의 index라고 보면 된다.
		for (int i = 0; i < 16; i++) {
			// 2의 제곱 을 구하여 해당 bit 가 1일때의 수를 구한다.
			int sqrt = (int) Math.pow(2, i);

			// flag 값과 sqrt 값을 AND 연산을 하였을때 해당 index 의 Status 가 Active 된것이다.
			if ((flag & sqrt) != 0) {
			    // flagStatus의 bit 배열을 1로 설정한다.
			    flagStatus[i] = 1;
			    
				if (tempString.length() != 0)
					tempString.append(" / " + LR_FLAG_STATUS.values()[i].name());
				else
					tempString.append(LR_FLAG_STATUS.values()[i].name());
			}

			// indicator 값과 sqrt 값을 AND 연산을 하였을때 해당 index 의 indicator 가 Active
			// 된것이다.
			if ((indicator & sqrt) != 0 && i < 5) {
				tamper_Indicator[i] = 1;
				if (tempString.length() != 0)
					tempString.append(" / " + LOAD_TAMPER_INDICATOR.values()[i].name());
				else
					tempString.append(LOAD_TAMPER_INDICATOR.values()[i].name());
			}
		}

		this.flagMsg = tempString.toString();
	}

	/**
	 * Channel 및 Volt 값을 읽어온다.<br>
	 * 
	 * @param storage
	 *            Channel 및 Volt값
	 * @param decimal
	 *            소수점 위치
	 * @param multiplier
	 *            킬로(kilo) 단위로 변환할 승수값
	 * @return
	 * @throws IOException
	 */
	private Double[] getStorageValue(byte[] storage, double decimal,
			double multiplier) throws IOException {
	    DecimalFormat df = new DecimalFormat("0.00");
		ByteArrayInputStream bis = new ByteArrayInputStream(storage);
		Double[] dStorage = new Double[7];
		byte[] eStorage = new byte[3];
		for (int i = 0; i < dStorage.length; i++) {
			bis.read(eStorage);
			double nStorage = Double.parseDouble(DataUtil.getBCDtoBytes(eStorage));//byteArrayToInt(eStorage);

			// 먼저 값에 소수점을 설정하고 승수를 곱하여 kilo 단위로 변환한다.
			dStorage[i] = nStorage * decimal * multiplier;
			log.debug("LP[" + df.format(dStorage[i]) + "]");
		}
		return dStorage;
	}

	/**
	 * 값의 단위에 따라 kilo로 변환되는 승수를 구한다.<br>
	 * 단위가 kilo 단위보다 낮을경우 1000 을 나뉜것과 같은 효과를 주기위해 0.001 을 리턴한다.<br>
	 * kilo 이상일경우 1000 을 리턴하고 같을경우 1을 리턴한다.<br>
	 * 
	 * @param unit
	 *            값의 단위<br>
	 *            00h = Wh, varh, VAh<br>
	 *            01h = kWh, kvarh, kVAh<br>
	 *            02h = MWh, Mvarh, MVAh<br>
	 * @return
	 */
	private double getMultiplier(byte unit) {
		double multiplier = 1;
		switch (unit) {
		case 0:
			multiplier = 0.001;
			break;
		case 0x01:
			multiplier = 1;
			break;
		case 0x02:
			multiplier = 1000;
			break;
		}
		return multiplier;
	}

	/**
	 * 수수점 변환을 위한 승수를 구한다.
	 * 
	 * @param bDecimal
	 *            적용할 소수점 정보<br>
	 *            01h = 1 decimal place<br>
	 *            02h = 2 decimal places<br>
	 *            03h = 3 decimal places<br>
	 * @return
	 */
	private double getDecimal(byte bDecimal) {
		double nDecimal = 1;
		switch (bDecimal) {
		case 0:
			nDecimal = 1;
			break;
		case 0x01:
			nDecimal = 0.1;
			break;
		case 0x02:
			nDecimal = 0.01;
			break;
		case 0x03:
			nDecimal = 0.001;
			break;
		}
		return nDecimal;
	}

	/**
	 * 날짜 정보를 파싱한다.
	 * 
	 * @param date
	 */
	public String getDateToString(byte[] date) {
		ByteArrayInputStream bis = new ByteArrayInputStream(date);
		int YY = 0;
		int MM = 0;
		int DD = 0;
		int hh = 0;
		int mm = 0;
		try {
			YY = bis.read();
			MM = bis.read();
			DD = bis.read();
			hh = bis.read();
			mm = bis.read();
			bis.close();
		} catch (IOException e) {
			log.debug(e.getStackTrace());
		}

		String yyyyMMddHHmm = String.format("%02x%02x%02x%02x%02x", YY, MM, DD,
				hh, mm);
		return convertDateFormat(yyyyMMddHHmm, "yyMMddHHmm", "yyyyMMddHHmm");
	}

	public int getPeriod() {
		return period;
	}

}
