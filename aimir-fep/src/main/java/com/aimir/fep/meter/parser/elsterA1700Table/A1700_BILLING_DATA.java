package com.aimir.fep.meter.parser.elsterA1700Table;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.parser.ElsterA1700;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * 
 * @author choiEJ
 *
 */
public class A1700_BILLING_DATA {    
	private Log log = LogFactory.getLog(A1700_BILLING_DATA.class);
    
    public static final int OFS_CUMULATIVE_TOTAL                    = 0;
    public static final int OFS_CUMULATIVE_TOU                      = 80;
    public static final int OFS_MULTI_UTILITY_CUMULATIVE_TOTAL      = 336;
    public static final int OFS_CUMULATIVE_MD                       = 368;
    public static final int OFS_MAXIMUM_DEMAND                      = 440;
    public static final int OFS_COINCIDENT_DEMAND                   = 728;
    public static final int OFS_BILLING_RESET_EVENT                  = 848;
    
    public static final int LEN_CUMULATIVE_TOTAL                    = 80;
    public static final int LEN_CUMULATIVE_TOTAL_DATA               = 8;
    public static final int LEN_CUMULATIVE_TOU                      = 256;
    public static final int LEN_CUMULATIVE_TOU_DATA                 = 8;
    public static final int LEN_MULTI_UTILITY_CUMULATIVE_TOTAL      = 32;
    public static final int LEN_MULTI_UTILITY_CUMULATIVE_TOTAL_DATA = 8;
    public static final int LEN_CUMULATIVE_MD                       = 72;
    public static final int LEN_CUMULATIVE_MD_DATA                  = 8;
    public static final int LEN_MAXIMUM_DEMAND                      = 288;
    public static final int LEN_MAXIMUM_DEMAND_DATA                 = 7;
    public static final int LEN_COINCIDENT_DEMAND                   = 120;
    public static final int LEN_COINCIDENT_DEMAND_DATA              = 7;
    public static final int LEN_BILLING_RESET_EVENT                 = 15;
    
    public static final int LEN_COUNT     = 2;
    public static final int LEN_TIMESTAMP = 4;
    public static final int LEN_SOURCE    = 1;
    
	private byte[] rawData = null;
	
	private double ke = 0.000001;
    
	BillingData billingData = new BillingData();
	List<EventLogData> billingResetEventList = new ArrayList<EventLogData>();
	
	/**
	 * Constructor
	 */
	public A1700_BILLING_DATA(byte[] rawData) {
        this.rawData = rawData;
	}
	
	public static void main(String[] args) throws Exception {
		A1700_TEST_DATA testData = new A1700_TEST_DATA();
		A1700_BILLING_DATA elster = new A1700_BILLING_DATA(testData.getTestData_billing());
		elster.parseBillingData();
		System.out.println(elster.toString());
	}
	
	public BillingData getBillingData() throws NumberFormatException, Exception {
		parseBillingData();
		
		if (billingData != null) {
			return billingData;
		} else {
			return null;
		}
	}
	
	// 빌링 리셋 이벤트는 어디에 저장할것인지 정의 요망
	public List<EventLogData> getBillingResetEvent() {
		return null;
	}
	
	private void parseBillingData() throws NumberFormatException, Exception {
		parseCumulativeTotal();
		parseCumulativeTOU();
		parseMultiUtilityCumulativeTotal();
		parseCumulativeMD();
		parseMaximumDemand();
		parseCoincidentDemand();
		parseBillingResetEvent();
	}
	
	private void parseCumulativeTotal() throws Exception {
		log.debug("START-----parseCumulativeTotal()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOTAL, LEN_CUMULATIVE_TOTAL);
		
		int offset = 0;
		Double value = null;
		
		value = convertBCD2Double("TOTAL_IMPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setActiveEnergyImportRateTotal(value);
		billingData.setActiveEnergyRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
        value = convertBCD2Double("TOTAL_EXPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setActiveEnergyExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL_IMPORT_LAGGING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLagImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL_IMPORT_LEADING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLeadImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
        value = convertBCD2Double("TOTAL_EXPORT_LAGGING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLagExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
        value = convertBCD2Double("TOTAL_EXPORT_LEADING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLeadExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		// total vah 저장안함
        value = convertBCD2Double("TOTAL_kVAh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;//KVAh 로 변환 위해 /1000 dmfh gksek 
		billingData.setkVah(value);
		billingData.setMaxDmdkVah1RateTotal(value);
		billingData.setActivePwrDmdMaxImportRateTotal(value);
		
		// customer defined #1~3 저장안함
		while (offset < data.length) {
			value = convertBCD2Double("CUSTOMER_DEFINED", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
			offset += LEN_CUMULATIVE_TOTAL_DATA;
		}

		log.debug("END-----parseCumulativeTotal()");
	}

	private void parseCumulativeTOU() throws Exception {
		log.debug("START-----parseCumulativeTOU()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOU, LEN_CUMULATIVE_TOU);
		
		int offset = 0;
		Double value = null;
		
		// CUMULATIVE_TOU 저장안함
		int cnt = 1;
		while (offset < data.length) {
			value = convertBCD2Double("CUMULATIVE_TOU", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
			offset += LEN_CUMULATIVE_TOU_DATA;
			
			// 2012.11.06 Rate1:on-peak kWh, Rate2:Standard kWh, Rate3:Off-peak kWh
			if (cnt == 1) { 
			    billingData.setActiveEnergyRate1(value);
			    billingData.setActiveEnergyImportRate1(value);
			}
			else if (cnt == 2) {
			    billingData.setActiveEnergyRate2(value);
			    billingData.setActiveEnergyImportRate2(value);
			}
			else if (cnt == 3) {
			    billingData.setActiveEnergyRate3(value);
			    billingData.setActiveEnergyImportRate3(value);
			}
			cnt++;
		}
		log.debug("END-----parseCumulativeTOU()");
	}

	private void parseMultiUtilityCumulativeTotal() throws Exception {
		log.debug("START-----parseMultiUtilityCumulativeTotal()");
		
		byte[] data = DataFormat.select(rawData, OFS_MULTI_UTILITY_CUMULATIVE_TOTAL, LEN_MULTI_UTILITY_CUMULATIVE_TOTAL);
		
		int offset = 0;
		Double value = null;
		
		// MULTI_UTILITY_CUMULATIVE_TOTAL 저장안함
		while (offset < data.length) {
			value = convertBCD2Double("MULTI_UTILITY", DataFormat.select(data, offset, LEN_MULTI_UTILITY_CUMULATIVE_TOTAL_DATA));
			offset += LEN_MULTI_UTILITY_CUMULATIVE_TOTAL_DATA;
		}
		
		log.debug("END-----parseMultiUtilityCumulativeTotal()");
	}
	
	private void parseCumulativeMD() throws Exception {
		log.debug("START-----parseCumulativeMD()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_MD, LEN_CUMULATIVE_MD);
		
		int offset = 0;
		Double value = null;
		int source = 0;
		
		byte[] b = null;
		while (offset < data.length) {
			// source 7을 선택한 이유?
			if (source == 7) {
	            value = convertBCD2Double("CUMULATIVE_MD_REGISTER", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
				billingData.setCummActivePwrDmdMaxImportRate1(value);
				log.debug("END-----parseCumulativeMD()");
				break;
			}
			offset += LEN_CUMULATIVE_MD_DATA;
            source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
            offset += LEN_SOURCE;
		}
		
	}
	
	private void parseMaximumDemand() throws Exception {
		log.debug("START-----parseMaximumDemand()");
		
		byte[] data = DataFormat.select(rawData, OFS_MAXIMUM_DEMAND, LEN_MAXIMUM_DEMAND); 
		int offset = 0;
		Double value = null;
		String timestamp = "";
		int source = 0;
		
		byte[] b = null;
		while (offset < data.length) {
			
			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#1", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");

			if (source == 7) {
	            value = convertBCD2Double("MAXIMUM_DEMAND_#1_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
	            
				billingData.setActivePwrDmdMaxTimeImportRate1(timestamp);
				billingData.setActivePwrDmdMaxImportRate1(value);
				// 어느 필드를 사용할 지 알 수 없어서 추가함.
				billingData.setMaxDmdkVah1TimeRate1(timestamp);
				billingData.setMaxDmdkVah1Rate1(value);
				billingData.setMaxDmdkVah1TimeRateTotal(timestamp);
				billingData.setMaxDmdkVah1RateTotal(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;

			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#2", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");

			if (source == 7) {
	            value = convertBCD2Double("MAXIMUM_DEMAND_#2_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
	            
				billingData.setActivePwrDmdMaxTimeImportRate2(timestamp);
				billingData.setActivePwrDmdMaxImportRate2(value);
				billingData.setMaxDmdkVah1TimeRate2(timestamp);
                billingData.setMaxDmdkVah1Rate2(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;
			
			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#3", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
			
			if (source == 7 ) {
			    value = convertBCD2Double("MAXIMUM_DEMAND_#3", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
				billingData.setActivePwrDmdMaxTimeImportRate3(timestamp);
				billingData.setActivePwrDmdMaxImportRate3(value);
				billingData.setMaxDmdkVah1TimeRate3(timestamp);
                billingData.setMaxDmdkVah1Rate3(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;

			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#4", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
				
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#4_SOURCE=[" + source + "]");
			
			if (source == 7) {
			    value = convertBCD2Double("MAXIMUM_DEMAND_#4", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
	            
				billingData.setActivePwrDmdMaxTimeImportRate4(timestamp);
				billingData.setActivePwrDmdMaxImportRate4(value);
				billingData.setMaxDmdkVah1TimeRate4(timestamp);
                billingData.setMaxDmdkVah1Rate4(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;
		}
		
		log.debug("END-----parseMaximumDemand()");
	}
	
	private void parseCoincidentDemand() throws Exception {
		log.debug("START-----parseCoincidentDemand()");
		
		byte[] data = DataFormat.select(rawData, OFS_COINCIDENT_DEMAND, LEN_COINCIDENT_DEMAND);
		
		int offset = 0;
		Double value = null;
		int source = 0;
		
		byte[] b = null;
		while (offset < data.length) {
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("COINCIDENT_DEMAND_#1_SOURCE=[" + source + "]");
			
			value = convertBCD2Double("COINCIDENT_DEMNAND_#1", DataFormat.select(data, offset, LEN_COINCIDENT_DEMAND_DATA));
			offset += LEN_COINCIDENT_DEMAND_DATA;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("COINCIDENT_DEMAND_#2_SOURCE=[" + source + "]");
			
			value = convertBCD2Double("COINCIDENT_DEMNAND_#2", DataFormat.select(data, offset, LEN_COINCIDENT_DEMAND_DATA));
			offset += LEN_COINCIDENT_DEMAND_DATA;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("COINCIDENT_DEMAND_#3_SOURCE=[" + source + "]");

			value = convertBCD2Double("COINCIDENT_DEMNAND_#3", DataFormat.select(data, offset, LEN_COINCIDENT_DEMAND_DATA));
			offset += LEN_COINCIDENT_DEMAND_DATA;
		}
		
		log.debug("END-----parseCoincidentDemand()");
	}
	
	// 저장안함
	private void parseBillingResetEvent() throws Exception {
		log.debug("START-----parseBillingResetEvent()");
		
		byte[] data = DataFormat.select(rawData, OFS_BILLING_RESET_EVENT, LEN_BILLING_RESET_EVENT);
		
		int offset = 0;
		String timestamp = null;
		int source = 0;

		byte[] b = DataFormat.select(data, offset, LEN_COUNT);
		DataFormat.convertEndian(b);
		int billingCnt = DataFormat.getIntToBytes(b);
		offset += LEN_COUNT;
		log.debug("BILLING_RESET_EVENT_COUNT=[" + billingCnt + "]");
		
		timestamp = ElsterA1700.convertTimestamp("START_BILLING_REST_EVENT_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		log.debug("START_BILLING_RESET_EVENTTIME[" + timestamp + "]");
		
		timestamp = ElsterA1700.convertTimestamp("END_BILLING_REST_EVENT_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		log.debug("END_BILLING_RESET_EVENTTIME[" + timestamp + "]");
		// 월별 빌링 데이타의 날짜 설정을 이벤트 종료 시간으로 한다. 매달 1일
		billingData.setBillingTimestamp(timestamp);
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("BILLING_RESET_EVENT_SOURCE=[" + source + "]");
		
		timestamp = ElsterA1700.convertTimestamp("BILLING_PERIOD_RESET_TRIGGER_EVENT_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		log.debug("END-----parseBillingResetEvent()");
	}

    private double convertBCD2Double(String title, byte[] data) {
        byte[] b = data;
        DataFormat.convertEndian(b);
        double d = Double.parseDouble(Hex.decode(b))*ke;
        log.debug(title+"=[" + d + "] RAW=" + Hex.decode(data)+"]");
        return d;
    }
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("A1700_BILLING_DATA[\n")
		  .append("  (activeEnergyImportRateTotal="         ).append(billingData.getActiveEnergyImportRateTotal()).append(")\n")
		  .append("  (activeEnergyExportRateTotal="         ).append(billingData.getActiveEnergyExportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLagImportRateTotal="    ).append(billingData.getReactiveEnergyLagImportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLeadImportRateTotal="   ).append(billingData.getReactiveEnergyLeadImportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLagExportRateTotal="    ).append(billingData.getReactiveEnergyLagExportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLeadExportRateTotal="   ).append(billingData.getReactiveEnergyLeadExportRateTotal()).append(")\n")
		  .append("  (activePwrDmdMaxImportRate1="          ).append(billingData.getActivePwrDmdMaxImportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxExportRate1="          ).append(billingData.getActivePwrDmdMaxExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagImportRate1="     ).append(billingData.getReactivePwrDmdMaxLagImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadImportRate1="    ).append(billingData.getReactivePwrDmdMaxLeadImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagExportRate1="     ).append(billingData.getReactivePwrDmdMaxLagExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadExportRate1="    ).append(billingData.getReactivePwrDmdMaxLeadExportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxTimeImportRate1="      ).append(billingData.getActivePwrDmdMaxTimeImportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxTimeExportRate1="      ).append(billingData.getActivePwrDmdMaxTimeExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagImportRate1=" ).append(billingData.getReactivePwrDmdMaxTimeLagImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadImportRate1=").append(billingData.getReactivePwrDmdMaxTimeLeadImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagExportRate1=" ).append(billingData.getReactivePwrDmdMaxTimeLagExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadExportRate1=").append(billingData.getReactivePwrDmdMaxTimeLeadExportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxImportRate2="          ).append(billingData.getActivePwrDmdMaxImportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxExportRate2="          ).append(billingData.getActivePwrDmdMaxExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagImportRate2="     ).append(billingData.getReactivePwrDmdMaxLagImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadImportRate2="    ).append(billingData.getReactivePwrDmdMaxLeadImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagExportRate2="     ).append(billingData.getReactivePwrDmdMaxLagExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadExportRate2="    ).append(billingData.getReactivePwrDmdMaxLeadExportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxTimeImportRate2="      ).append(billingData.getActivePwrDmdMaxTimeImportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxTimeExportRate2="      ).append(billingData.getActivePwrDmdMaxTimeExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagImportRate2=" ).append(billingData.getReactivePwrDmdMaxTimeLagImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadImportRate2=").append(billingData.getReactivePwrDmdMaxTimeLeadImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagExportRate2=" ).append(billingData.getReactivePwrDmdMaxTimeLagExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadExportRate2=").append(billingData.getReactivePwrDmdMaxTimeLeadExportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxImportRate3="          ).append(billingData.getActivePwrDmdMaxImportRate3()).append(")\n")
		  .append("  (activePwrDmdMaxExportRate3="          ).append(billingData.getActivePwrDmdMaxExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagImportRate3="     ).append(billingData.getReactivePwrDmdMaxLagImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadImportRate3="    ).append(billingData.getReactivePwrDmdMaxLeadImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagExportRate3="     ).append(billingData.getReactivePwrDmdMaxLagExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadExportRate3="    ).append(billingData.getReactivePwrDmdMaxLeadExportRate3()).append(")\n")
		  .append("  (activePwrDmdMaxTimeImportRate3="      ).append(billingData.getActivePwrDmdMaxTimeImportRate3()).append(")\n")
		  .append("  (activePwrDmdMaxTimeExportRate3="      ).append(billingData.getActivePwrDmdMaxTimeExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagImportRate3=" ).append(billingData.getReactivePwrDmdMaxTimeLagImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadImportRate3=").append(billingData.getReactivePwrDmdMaxTimeLeadImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagExportRate3=" ).append(billingData.getReactivePwrDmdMaxTimeLagExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadExportRate3=").append(billingData.getReactivePwrDmdMaxTimeLeadExportRate3()).append(")\n")
		  .append("  (cummActivePwrDmdMaxImportRate1="      ).append(billingData.getCummActivePwrDmdMaxImportRate1()).append(")\n")
		  .append("  (cummActivePwrDmdMaxExportRate1="      ).append(billingData.getCummActivePwrDmdMaxExportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLagImportRate1=" ).append(billingData.getCummReactivePwrDmdMaxLagImportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLeadImportRate1=").append(billingData.getCummReactivePwrDmdMaxLeadImportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLagExportRate1=" ).append(billingData.getCummReactivePwrDmdMaxLagExportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLeadExportRate1=").append(billingData.getCummReactivePwrDmdMaxLeadExportRate1()).append(")\n")
		  .append("]");
		
		return sb.toString();
	}
}
