package com.aimir.fep.meter.parser.elsterA1700Table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.LineType;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.meter.parser.ElsterA1700;
import com.aimir.fep.meter.parser.elsterA1700Table.EVENT_ATTRIBUTE.EVENTATTRIBUTE;
import com.aimir.fep.util.DataFormat;

/**
 * 
 * @author choiEJ
 *
 */
@SuppressWarnings("unchecked")
public class A1700_EVENT_LOG {    
	private Log log = LogFactory.getLog(A1700_EVENT_LOG.class);
    
    public static final int OFS_PHASE_FAILURE   = 0;
    public static final int OFS_REVERSE_RUNNING = 63;
    public static final int OFS_POWER_DOWN      = 109;
    public static final int OFS_POWER_FAIL      = 131;
    
    public static final int LEN_PHASE_FAILURE   = 63;
    public static final int LEN_REVERSE_RUNNING = 46;
    public static final int LEN_POWER_DOWN      = 22;
    public static final int LEN_POWER_FAIL      = 46;
    
    public static final int LEN_EVENT_COUNT      = 2;
    public static final int LEN_EVENT_TIME_COUNT = 4;
    public static final int LEN_EVENT_TIMESTAMP  = 4;
    
	private byte[] rawData = null;
    
	List<EventLogData>      meterEventList = new ArrayList();
	List<PowerAlarmLogData> powerAlarmList = new ArrayList();
	
	/**
	 * Constructor
	 */
	public A1700_EVENT_LOG(byte[] rawData) {
        this.rawData = rawData;
	}
	
	public static void main(String[] args) throws Exception {
		A1700_TEST_DATA testData = new A1700_TEST_DATA();
		A1700_EVENT_LOG elster = new A1700_EVENT_LOG(testData.getTestData_event());
		elster.parseMeterEventLog();
		elster.parsePowerAlarmLog();
		System.out.println(elster.toString());
/*		PowerAlarmLog powerAlarmLog = new PowerAlarmLog();
		powerAlarmLog.setLineType("A");
		System.out.println(powerAlarmLog.getLineType().getName());*/
	}
	
	public List<EventLogData> getMeterEventLog() throws Exception {
		parseMeterEventLog();
		
		if (meterEventList != null) {
			return meterEventList;
		} else {
			return null;
		}
	}
	
	public List<PowerAlarmLogData> getPowerAlarmLog() throws Exception {
		parsePowerAlarmLog();
		
		if (powerAlarmList != null) {
			return powerAlarmList;
		} else {
			return null;
		}
	}
	
	private void parseMeterEventLog() throws Exception {
		getPhaseFailure();
		getReverseRunning();
		getPowerDown();
		getPowerFail();
	}
	
	private void parsePowerAlarmLog() throws Exception {
		getPhaseFailure();
		getPowerDown();
		getPowerFail();
	}
	
	private void getPhaseFailure() throws Exception {
		log.debug("START-----getPhaseFailure()");
		
		byte[] data = DataFormat.select(rawData, OFS_PHASE_FAILURE, LEN_PHASE_FAILURE);
		
		int offset = 0;
		String dateTime = "";
		
		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();
		
		int countA = convertInt2("PHASE_A_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		offset += LEN_EVENT_COUNT;
		
		int countB = convertInt2("PHASE_B_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		offset += LEN_EVENT_COUNT;
		
		int countC = convertInt2("PHASE_C_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		offset += LEN_EVENT_COUNT;
		
		int timeCntA = convertInt4("PHASE_A_EVENT_TIME_COUNT", DataFormat.select(data, offset, LEN_EVENT_TIME_COUNT));
		offset += LEN_EVENT_TIME_COUNT;
		
		int timeCntB = convertInt4("PHASE_B_EVENT_TIME_COUNT", DataFormat.select(data, offset, LEN_EVENT_TIME_COUNT));
		offset += LEN_EVENT_TIME_COUNT;
		
		int timeCntC = convertInt4("PHASE_C_EVENT_TIME_COUNT", DataFormat.select(data, offset, LEN_EVENT_TIME_COUNT));
		offset += LEN_EVENT_TIME_COUNT;
		
		String startTime = ElsterA1700.convertTimestamp("START_PHASE_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;	   // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		String endTime = ElsterA1700.convertTimestamp("END_PHASE_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;     // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		int phaseType = DataFormat.getIntToBytes(DataFormat.select(data, offset, 1));
		
		if (phaseType == 1) {
			powerAlarmLog.setLineType(LineType.A);
		} else if (phaseType == 2) {
			powerAlarmLog.setLineType(LineType.B);
		} else if (phaseType == 3) {
			powerAlarmLog.setLineType(LineType.C);
		}
		
		eventLog.setFlag(EVENTATTRIBUTE.PHASE_FAILURE.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.PHASE_FAILURE.getName());
		eventLog.setAppend(EVENTATTRIBUTE.PHASE_FAILURE.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.PHASE_FAILURE.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.PHASE_FAILURE.getName());
		
		
		dateTime = startTime;
		eventLog.setDate(dateTime.substring(0, 8));               // yyyymmdd
		eventLog.setTime(dateTime.substring(8));                  // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));             // hhmmss
		
		dateTime = endTime;
		powerAlarmLog.setCloseDate(dateTime.substring(0, 8));     // yyyymmdd
		powerAlarmLog.setCloseTime(dateTime.substring(8));        // hhmmss

		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private void getReverseRunning() throws Exception {
		log.debug("START-----getReverseRunning()");
		
		byte[] data = DataFormat.select(rawData, OFS_REVERSE_RUNNING, LEN_REVERSE_RUNNING);
		
		int offset = 0;
		String dateTime = "";
		
		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();

		int count = convertInt2("REVERSE_RUNNING_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		offset += LEN_EVENT_COUNT;
		
		int timeCnt = convertInt4("REVERSE_RUNNING_EVENT_TIME_COUNT", DataFormat.select(data, offset, LEN_EVENT_TIME_COUNT));
		offset += LEN_EVENT_TIME_COUNT;
		
		String startTime = ElsterA1700.convertTimestamp("START_REVERSE_RUNNING_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;	   // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		String endTime = ElsterA1700.convertTimestamp("END_REVERSE_RUNNING_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;     // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		eventLog.setFlag(EVENTATTRIBUTE.REVERSE_RUNNING.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.REVERSE_RUNNING.getName());
		eventLog.setAppend(EVENTATTRIBUTE.REVERSE_RUNNING.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.REVERSE_RUNNING.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.REVERSE_RUNNING.getName());
		
		dateTime = startTime;
		eventLog.setDate(dateTime.substring(0, 8));               // yyyymmdd
		eventLog.setTime(dateTime.substring(8));                  // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));             // hhmmss
		
		String _endTime = endTime;
		dateTime = _endTime; //DateTimeUtil.getDateString(getTimestampForElster(endTime));
		powerAlarmLog.setCloseDate(dateTime.substring(0, 8));     // yyyymmdd
		powerAlarmLog.setCloseTime(dateTime.substring(8));        // hhmmss
		
		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private void getPowerDown() throws Exception {
		log.debug("START-----getPowerDown()");

		byte[] data = DataFormat.select(rawData, OFS_POWER_DOWN, LEN_POWER_DOWN);
		
		int offset = 0;
		String dateTime = "";
		
		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();

		int count = convertInt2("POWER_DOWN_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		offset += LEN_EVENT_COUNT;
		
		String startTime = ElsterA1700.convertTimestamp("START_POWER_DOWN_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;	   // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		eventLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
		eventLog.setAppend(EVENTATTRIBUTE.POWER_DOWN.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.POWER_DOWN.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.POWER_DOWN.getName());
		
		dateTime = startTime;
		eventLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		eventLog.setTime(dateTime.substring(8));             // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));     // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));        // hhmmss
		
		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private void getPowerFail() throws Exception {
		log.debug("START-----getPowerFail()");

		byte[] data = DataFormat.select(rawData, OFS_POWER_FAIL, LEN_POWER_FAIL);
		
		int offset = 0;
		String dateTime = "";

		EventLogData      eventLog      = new EventLogData();
		PowerAlarmLogData powerAlarmLog = new PowerAlarmLogData();

		int count = convertInt2("POWER_FAIL_EVENT_COUNT", DataFormat.select(data, offset, LEN_EVENT_COUNT));
		offset += LEN_EVENT_COUNT;
		
		int timeCnt = convertInt4("POWER_FAIL_EVENT_TIME_COUNT", DataFormat.select(data, offset, LEN_EVENT_TIME_COUNT));
		offset += LEN_EVENT_TIME_COUNT;
		
		String startTime = ElsterA1700.convertTimestamp("START_POWER_FAIL_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;	   // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		String endTime = ElsterA1700.convertTimestamp("END_POWER_FAIL_EVENT_TIME", DataFormat.select(data, offset, LEN_EVENT_TIMESTAMP));
		offset += LEN_EVENT_TIMESTAMP * 5;     // 뒤로 이전 4개의 시간 데이터가 오지만 필요없음
		
		eventLog.setFlag(EVENTATTRIBUTE.POWER_FAIL.getCode());
		eventLog.setMsg(EVENTATTRIBUTE.POWER_FAIL.getName());
		eventLog.setAppend(EVENTATTRIBUTE.POWER_FAIL.getName());
		
		powerAlarmLog.setFlag(EVENTATTRIBUTE.POWER_FAIL.getCode());
		powerAlarmLog.setMsg(EVENTATTRIBUTE.POWER_FAIL.getName());
		
		dateTime = startTime;
		eventLog.setDate(dateTime.substring(0, 8));               // yyyymmdd
		eventLog.setTime(dateTime.substring(8));                  // hhmmss
		powerAlarmLog.setDate(dateTime.substring(0, 8));          // yyyymmdd
		powerAlarmLog.setTime(dateTime.substring(8));             // hhmmss
		
		// 종료시간이 발생시간보다 나중일때
		if (endTime.compareTo(startTime) > 0) {
			dateTime = endTime;
			powerAlarmLog.setCloseDate(dateTime.substring(0, 8));     // yyyymmdd
			powerAlarmLog.setCloseTime(dateTime.substring(8));        // hhmmss
		}

		meterEventList.add(eventLog);
		powerAlarmList.add(powerAlarmLog);
	}
	
	private int convertInt2(String title, byte[] data) {
        byte[] b = data;
        DataFormat.convertEndian(b);
        int i = DataFormat.getIntTo2Byte(b);
        log.debug(title + "=[" + i + "]");
        return i;
    }
	
	private int convertInt4(String title, byte[] data) {
        byte[] b = data;
        DataFormat.convertEndian(b);
        int i = DataFormat.getIntTo4Byte(b);
        log.debug(title + "=[" + i + "]");
        return i;
    }
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator iter = null;
		
		sb.append("A1700_EVENT_LOG[\n")
		  .append("  (METER_EVENT={\n");
		iter = meterEventList.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next());
		}
		sb.append("  }),\n");
		
		sb.append("  (POWER_EVENT={\n");
		iter = powerAlarmList.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next());
		}
		sb.append("  })\n");
		sb.append("]\n");
		
		return sb.toString();
	}
	
}
