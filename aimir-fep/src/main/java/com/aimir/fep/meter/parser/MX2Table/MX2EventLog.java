package com.aimir.fep.meter.parser.MX2Table;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.util.Hex;

/**
 * Event Log <br>
 * 
 * <p>
 * Event log는 Date 와 Event Flag 로 구성된 Event Sequential Data 가 순차적으로 저장되며 최대
 * 1024개의 로그를 저장하고 있다. 이중 최근에 저장된 100개의 이벤트 만을 검침한다.
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
@SuppressWarnings("serial")
public class MX2EventLog extends CommonTable {
	private static Log log = LogFactory.getLog(MX2EventLog.class);

	/**
	 * <p>
	 * Event Log
	 * </p>
	 * <p>
	 * Date 정보와 Flag 정보가 100개까지 저장되며 각각 6 size Binary, 1 size ASCII 값을 갖는다.
	 * </p>
	 */
	private Vector<EventLogData> eventLogDatas = new Vector<EventLogData>();
	
	private Vector<PowerAlarmLogData> powerAlarmLogData = new Vector<PowerAlarmLogData>();

	// //////////////////////////////////////////////////

	public static final int LEN_EVENT_LOG = 7; // byte array 길이
	public static final int LEN_DATE = 6;

	/**
	 * <p>
	 * EL Flag 값에 해당하는 메시지 목록.
	 * </p>
	 * <p>
	 * EL Flag 값은 16진수 ASCII 로 되어 있으며 해당 인덱스별로 메시지를 치환한다. 인덱스 범위는 1~9~A~P 까지이며 인덱스
	 * 0은 사용되지 않는다.
	 * </p>
	 */
	public static final String[] ELFlags = new String[] { "Invalid Flag",
			"Over Voltage(Phase A)", "Over Voltage(Phase B)",
			"Over Voltage(Phase C)", "Under Voltage(Phase A)",
			"Under Voltage(Phase B)", "Under Voltage(Phase C)",
			"Over Current(Phase A)", "Over Current(Phase B)",
			"Over Current(Phase C)", "Power ON", "Power OFF",
			"Time Change(Before)", "Time Change(After)",
			"Manual Demand Reset", "Self-reading", "TOU Calendar Change",
			"Low Battery", "Reverse Current", "T-Cover Open", "Data Reset",
			"Potential Loss", "Invalid Password", "Internal H/W Loss",
			"F-cover Open", "External Input Ch.1" };

	/**
	 * Byte Array 를 입력받아 생성되며, 생성시 필드 값으로 파싱된다.<br>
	 * 
	 * @param eventLog
	 *            Event Log Byte Array
	 */
	public MX2EventLog(byte[] eventLog) {
		// log.debug("eventLog : " + Hex.getHexDump(eventLog));
		ByteArrayInputStream bis = new ByteArrayInputStream(eventLog);
		try {
			
			boolean powerOn = false;
			
			String closeDate = "";
			String closeTime = "";
			
			
			while (bis.available() > 0) {
				EventLogData eventLogData = new EventLogData();
				
				//이벤트 로그의 타입을 설정해준다. MX2에서는 Standard Event 로 설정한다.
				eventLogData.setKind(MeterEventKind.STE.name());
				
				
				
				// 날짜를 읽어온다.
				byte[] date = new byte[LEN_DATE];
				bis.read(date);
				
				String yyyyMMdd = getYyyyMMdd(date);
				String HHmmss = getHHmmss(date);
				
				eventLogData.setDate(yyyyMMdd);
				eventLogData.setTime(HHmmss);
				
				// Flag 메시지를 읽는다 ASCII
				byte[] bFlag = new byte[] { (byte) bis.read() };
				
				int iFlag=0;
				
				//0x41는 A ASCII 코드 int 로 변환하면 65 
				if(bFlag[0] >= 0x41 && bFlag[0] <= 0x50){ // A ~ P
					iFlag = 10+((65 - bFlag[0]) * -1);
				}else if(bFlag[0] >= 0x31 && bFlag[0] <= 0x39){ // 1 ~ 9
					iFlag = (48 - bFlag[0]) * -1;
				}else {
					//throw new MRPException(MRPError.ERR_READ_METER_CLASS,"Event Log Data In Flag Data is Error");
					iFlag = 0;
				}
					
				eventLogData.setFlag(iFlag);
				eventLogData.setMsg(this.ELFlags[iFlag]);
				this.eventLogDatas.add(eventLogData);
				
				
				if(iFlag==10){
					// Power ON Log
					powerOn = true;
					closeDate = yyyyMMdd;
					closeTime = HHmmss;
				}
				
				if(iFlag==11){
					// Power Off log
					if(powerOn){
						powerOn=false;
						
						PowerAlarmLogData paData = new PowerAlarmLogData();
						paData.setFlag(iFlag);
						paData.setMsg(this.ELFlags[iFlag]);
						paData.setKind(MeterEventKind.STE.name());
						paData.setDate(yyyyMMdd);
						paData.setTime(HHmmss);
						paData.setCloseDate(closeDate);
						paData.setCloseTime(closeTime);
						
						this.powerAlarmLogData.add(paData);
					}
				}
				

				
			}
			bis.close();
		} catch (IOException e) {
			log.debug(e.getStackTrace());
			return;
		}
	}

	/**
	 * 날짜 정보를 파싱한다.
	 * 
	 * @param date
	 */
	public String getYyyyMMdd(byte[] date) {
		ByteArrayInputStream bis = new ByteArrayInputStream(date);
		int YY = 0;
		int MM = 0;
		int DD = 0;
		try {
			YY = bis.read();
			MM = bis.read();
			DD = bis.read();
			bis.close();
		} catch (IOException e) {
			log.debug(e.getStackTrace());
		}

		String yyMMdd = bcdValidation(new int[]{YY,MM,DD});
		
		
		String yyyyMMdd = convertDateFormat(yyMMdd, "yyMMdd", "yyyyMMdd");

		return yyyyMMdd;
	}
	
	/**
	 * BCD타입을 확인하고 아니면 그냥 int로 파싱한다.
	 * @param is
	 * @return
	 */
	private String bcdValidation(int[] is) {
		
		StringBuilder sb = new StringBuilder();
		for (int i : is) {
			
			String s = String.format("%02x", i);
			try{
				int parse = Integer.parseInt(s);
			}catch (Exception e){
				sb.append(String.format("%02d", i));
				continue;
			}
			sb.append(s);
		}
		return sb.toString();
	}

	public String getHHmmss(byte[] date) {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(date);
		int HH = 0;
		int mm = 0;
		int ss = 0;
		try {
			bis.skip(3);
			HH = bis.read();
			mm = bis.read();
			ss = bis.read();
			bis.close();
		} catch (IOException e) {
			log.debug(e.getStackTrace());
		}

		String HHmmss = bcdValidation(new int[]{HH,mm,ss});

		return HHmmss;
	}

	public Vector<EventLogData> getEventLogData() {
		return eventLogDatas;
	}

	public EventLogData[] getEventLog() {
		return eventLogDatas.toArray(new EventLogData[0]);
	}

	public Vector<PowerAlarmLogData> getPowerAlarmLog() {
		return this.powerAlarmLogData;
	}
}
