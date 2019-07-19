package com.aimir.fep.meter.parser.elsterA1140Table;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.parser.ElsterA1140;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * 
 * @author choiEJ
 *
 */
public class A1140_BILLING_DATA {    
	private static Log log = LogFactory.getLog(A1140_BILLING_DATA.class);
    
    public static final int OFS_CUMULATIVE_TOTAL                    = 0;
    public static final int OFS_CUMULATIVE_TOU                      = 128;
    public static final int OFS_CUMULATIVE_MD                       = 256;
    public static final int OFS_MAXIMUM_DEMAND                      = 308;
    public static final int OFS_BILLING_RESET_EVENT                 = 452;
    
    public static final int LEN_CUMULATIVE_TOTAL                    = 128;
    public static final int LEN_CUMULATIVE_TOTAL_DATA               = 8;
    
    public static final int LEN_CUMULATIVE_TOU                      = 64;
    public static final int LEN_CUMULATIVE_TOU_DATA                 = 8;
    
    public static final int LEN_CUMULATIVE_MD                       = 52;
    public static final int LEN_TOU_SOURCE                			= 16;
    public static final int LEN_CUMULATIVE_MD_SOURCE                = 1;
    public static final int LEN_CUMULATIVE_MD_DATA                  = 8;
    
    public static final int LEN_MAXIMUM_DEMAND                      = 144;
    public static final int LEN_MAXIMUM_DEMAND_TIME                 = 4;
    public static final int LEN_MAXIMUM_DEMAND_SOURCE               = 1;
    public static final int LEN_MAXIMUM_DEMAND_DATA                 = 7;
    
    public static final int LEN_BILLING_EVENT                 		= 5;
    
    public static final int LEN_COUNT     = 2;
    public static final int LEN_TIMESTAMP = 4;
    public static final int LEN_SOURCE    = 1;
    
	private byte[] rawData = null;
	
	private double ke = 0.000001; // kW, kvar;
	
	BillingData billingData = null;
	List<EventLogData> billingResetEventList = new ArrayList<EventLogData>();
	
	DecimalFormat df = new DecimalFormat("#0.000000");
	/**
	 * Constructor
	 */
	public A1140_BILLING_DATA(byte[] rawData) {
        this.rawData = rawData;
        
        try {
            parseBillingData();
        }
        catch (Exception e) {
            log.error(e, e);
        }
	}
	
	public static void main(String[] args) throws Exception {
		A1140_TEST_DATA testData = new A1140_TEST_DATA();
		A1140_BILLING_DATA elster = new A1140_BILLING_DATA(testData.getTestData_billing());
		elster.parseBillingData();
		System.out.println(elster.toString());
	}
	
	public BillingData getBillingData() {
		return billingData;
	}
	
	// 빌링 리셋 이벤트는 어디에 저장할것인지 정의 요망
	public List<EventLogData> getBillingResetEvent() {
		return null;
	}
	
	private void parseBillingData() throws NumberFormatException, Exception {
		parseCumulativeTotal();	 //문서 37Page참조
		parseCumulativeTOU(); 	 //문서 38Page참조(Cumulative time of use)
		parseCumulativeMD();	 //문서 39Page참조
		parseMaximumDemand();	 //문서 39Page참조
		parseBillingEvent();//41Page참조
	}
	
	private void parseCumulativeTotal() throws Exception {
	    billingData = new BillingData();
		//log.debug("START-----parseCumulativeTotal()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOTAL, LEN_CUMULATIVE_TOTAL);
		
		int offset = 0;
		Double value = null;
		Double actEngTot = 0.0;
		
		value = convertBCD2Double("TOTAL_IMPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setActiveEnergyImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		actEngTot += value;
		
		value = convertBCD2Double("TOTAL_EXPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setActiveEnergyExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		actEngTot += value;
		
		//ActiveEnergyRateTotal 추가.
		billingData.setActiveEnergyRateTotal(actEngTot);
		
		value = convertBCD2Double("TOTAL Q1 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLagImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL Q2 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLeadImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL Q3 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLagExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL Q4 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setReactiveEnergyLeadExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		// total vah 저장안함
		value = convertBCD2Double("TOTAL kvah1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		billingData.setkVah(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA; 
		
		value = convertBCD2Double("TOTAL kvah2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		// Reserved1
		value = convertBCD2Double("Reserved1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		// Reserved2
		value = convertBCD2Double("Reserved2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		// Reserved3
		value = convertBCD2Double("Reserved3", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		// Reserved4
		value = convertBCD2Double("Reserved4", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		// Reserved5
		value = convertBCD2Double("Reserved5", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		// Reserved6
		value = convertBCD2Double("Reserved6", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		// Customer Defined 1
		value = convertBCD2Double("Customer Defined 1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		// Customer Defined 2
		value = convertBCD2Double("Customer Defined 2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
	}

	private void parseCumulativeTOU() throws Exception {
		log.debug("START-----parseCumulativeTOU()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOU, LEN_CUMULATIVE_TOU);
		
		int offset = 0;
		Double value = 0.0;
		
		value = convertBCD2Double("TOU1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOU2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		billingData.setActiveEnergyImportRate1(value);
		
		value = convertBCD2Double("TOU3", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		billingData.setActiveEnergyImportRate2(value);
		
		value = convertBCD2Double("TOU4", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		billingData.setActiveEnergyImportRate3(value);
		
		value = convertBCD2Double("TOU5", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOU6", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOU7", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOU8", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		log.debug("END-----parseCumulativeTOU()");
	}

	private void parseCumulativeMD() throws Exception {
		log.debug("START-----parseCumulativeMD()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_MD, LEN_CUMULATIVE_MD);
		
		int offset = 0;
		Double value = null;
		int source = 0;
		
		log.debug("TOU_SOURCE=["+DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_TOU_SOURCE))+"]");
		offset += LEN_TOU_SOURCE;
		
		value = convertBCD2Double("CUMULATIVE MD[1]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, value);
		
		value = convertBCD2Double("CUMULATIVE MD[2]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, value);
		
		value = convertBCD2Double("CUMULATIVE MD[3]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, value);
		
		value = convertBCD2Double("CUMULATIVE MD[4]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, value);
		
	}
	
	private void parseMaximumDemand() throws Exception {
		log.debug("START-----parseMaximumDemand()");
		
		byte[] data = DataFormat.select(rawData, OFS_MAXIMUM_DEMAND, LEN_MAXIMUM_DEMAND); 
		
		int offset = 0;
		Double value = null;
		Double value1 = null;
		Double value2 = null;
		Double value3 = null;
		String timestamp = "";
		String timestamp1 = "";
		String timestamp2 = "";
		String timestamp3 = "";
		int source = 0;
		
//		MD Set1
		// #1
		timestamp1 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#1_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");
		
		value1 = convertBCD2Double("MAXIMUM_DEMAND_#1_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#2
		timestamp2 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#2_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");
		
		value2 = convertBCD2Double("MAXIMUM_DEMAND_#2_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#3
		timestamp3 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#3_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
		
		value3 = convertBCD2Double("MAXIMUM_DEMAND_#3_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
				
		setMaxDmd(source, timestamp1, timestamp2, timestamp3, value1, value2, value3);
//		MD Set2
		// #1
		timestamp1 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#1_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");
		
		value1 = convertBCD2Double("MAXIMUM_DEMAND_#1_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#2
		timestamp2 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#2_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");
		
		value2 = convertBCD2Double("MAXIMUM_DEMAND_#2_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#3
		timestamp3 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#3_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
		
		value3 = convertBCD2Double("MAXIMUM_DEMAND_#3_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		setMaxDmd(source, timestamp1, timestamp2, timestamp3, value1, value2, value3);
		
//		MD Set3
		// #1
		timestamp1 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#1_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");
		
		value1 = convertBCD2Double("MAXIMUM_DEMAND_#1_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#2
		timestamp2 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#2_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");
		
		value2 = convertBCD2Double("MAXIMUM_DEMAND_#2_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#3
		timestamp3 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#3_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
		
		value3 = convertBCD2Double("MAXIMUM_DEMAND_#3_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		setMaxDmd(source, timestamp1, timestamp2, timestamp3, value1, value2, value3);
		
//		MD Set4
		// #1
		timestamp1 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#1_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");
		
		value1 = convertBCD2Double("MAXIMUM_DEMAND_#1_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#2
		timestamp2 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#2_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");
		
		value2 = convertBCD2Double("MAXIMUM_DEMAND_#2_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		//#3
		timestamp3 = ElsterA1140.convertTimestamp("MAXIMUM_DEMAND_#3_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
		
		value3 = convertBCD2Double("MAXIMUM_DEMAND_#3_VALUE", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
		offset += LEN_MAXIMUM_DEMAND_DATA;
		
		setMaxDmd(source, timestamp1, timestamp2, timestamp3, value1, value2, value3);

        log.debug("END-----parseMaximumDemand()");
	}
	
	
	// 저장안함
	private void parseBillingEvent() throws Exception {
		byte[] data = DataFormat.select(rawData, OFS_BILLING_RESET_EVENT, LEN_BILLING_EVENT);
		
		int offset = 0;
		String timestamp = "";
	
		int billingTriggerFlag = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		log.debug("billingTriggerFlag "+billingTriggerFlag);
		
		timestamp = ElsterA1140.convertTimestamp("BILLING_TIMESTAMP", DataFormat.select(data, offset, LEN_TIMESTAMP));
		offset += LEN_TIMESTAMP;
		
		billingData.setBillingTimestamp(timestamp);
	}

	private double convertBCD2Double(String title, byte[] data) {
        byte[] b = data;
        DataFormat.convertEndian(b);
        double d = Double.parseDouble(Hex.decode(b))*ke;
        log.debug(title+"=[" + d + "] RAW=" + Hex.decode(data)+"]");
        return d;
    }
	
	private void setCumMaxDmd(int source, Double value){
		if(source == 0){
			billingData.setCummActivePwrDmdMaxImportRate1(value);
			billingData.setCummActivePwrDmdMaxImportRate2(null);
			billingData.setCummActivePwrDmdMaxImportRate3(null);
			billingData.setCummActivePwrDmdMaxImportRateTotal(value);
		} else if(source == 1 ){
			billingData.setCummActivePwrDmdMaxExportRate1(value);
			billingData.setCummActivePwrDmdMaxExportRate2(null);
			billingData.setCummActivePwrDmdMaxExportRate3(null);
			billingData.setCummActivePwrDmdMaxExportRateTotal(value);
		} else if(source == 2 ){
			billingData.setCummReactivePwrDmdMaxLagImportRate1(value);
			billingData.setCummReactivePwrDmdMaxLagImportRate2(null);
			billingData.setCummReactivePwrDmdMaxLagImportRate3(null);
			billingData.setCummReactivePwrDmdMaxLagImportRateTotal(value);
		} else if(source == 3 ){
			billingData.setCummReactivePwrDmdMaxLeadImportRate1(value);
			billingData.setCummReactivePwrDmdMaxLeadImportRate2(null);
			billingData.setCummReactivePwrDmdMaxLeadImportRate3(null);
			billingData.setCummReactivePwrDmdMaxLeadImportRateTotal(value);
		} else if(source == 4 ){
			billingData.setCummReactivePwrDmdMaxLagExportRate1(value);
			billingData.setCummReactivePwrDmdMaxLagExportRate2(null);
			billingData.setCummReactivePwrDmdMaxLagExportRate3(null);
			billingData.setCummReactivePwrDmdMaxLagExportRateTotal(value);
		} else if(source == 5 ){
			billingData.setCummReactivePwrDmdMaxLeadExportRate1(value);
			billingData.setCummReactivePwrDmdMaxLeadExportRate2(null);
			billingData.setCummReactivePwrDmdMaxLeadExportRate3(null);
			billingData.setCummReactivePwrDmdMaxLeadExportRateTotal(value);
		} else if(source == 6){
			billingData.setCummkVah1Rate1(value);
			billingData.setCummkVah1Rate2(null);
			billingData.setCummkVah1Rate3(null);
			billingData.setCummkVah1RateTotal(value);
		}
	}
	
	
	private void setMaxDmd(int source, String time1, String time2, String time3, Double value1, Double value2, Double value3 ){
		String totTime = "";
		Double value = 0.0;
		
		if(value1 > value2){
			value = value1;
			totTime = time1;
			if(value1 < value3){
				value = value3;
				totTime = time3;
			}
			
		}else{
			value = value2;
			totTime = time2;
			if(value2 < value3){
				value = value3;
				totTime = time3;
			}
		}
		
		if(source == 0){
			billingData.setActivePwrDmdMaxImportRate1(value1);
			billingData.setActivePwrDmdMaxImportRate2(value2);
			billingData.setActivePwrDmdMaxImportRate3(value3);
			billingData.setActivePwrDmdMaxTimeImportRate1(time1);
			billingData.setActivePwrDmdMaxTimeImportRate2(time2);
			billingData.setActivePwrDmdMaxTimeImportRate3(time3);			
			
			billingData.setActivePwrDmdMaxImportRateTotal(value);
			billingData.setActivePwrDmdMaxTimeImportRateTotal(totTime);
		} else if(source == 1 ){
			billingData.setActivePwrDmdMaxExportRate1(value1);
			billingData.setActivePwrDmdMaxExportRate2(value2);
			billingData.setActivePwrDmdMaxExportRate3(value3);
			billingData.setActivePwrDmdMaxTimeExportRate1(time1);
			billingData.setActivePwrDmdMaxTimeExportRate2(time2);
			billingData.setActivePwrDmdMaxTimeExportRate3(time3);

			billingData.setActivePwrDmdMaxExportRateTotal(value);
			billingData.setActivePwrDmdMaxTimeExportRateTotal(totTime);
		} else if(source == 2 ){
			billingData.setReactivePwrDmdMaxLagImportRate1(value1);
			billingData.setReactivePwrDmdMaxLagImportRate2(value2);
			billingData.setReactivePwrDmdMaxLagImportRate3(value3);
			billingData.setReactivePwrDmdMaxTimeLagImportRate1(time1);
			billingData.setReactivePwrDmdMaxTimeLagImportRate2(time2);
			billingData.setReactivePwrDmdMaxTimeLagImportRate3(time3);

			billingData.setReactivePwrDmdMaxLagImportRateTotal(value);
			billingData.setReactivePwrDmdMaxTimeLagImportRateTotal(totTime);	
		} else if(source == 3 ){
			billingData.setReactivePwrDmdMaxLeadImportRate1(value1);
			billingData.setReactivePwrDmdMaxLeadImportRate2(value2);
			billingData.setReactivePwrDmdMaxLeadImportRate3(value3);
			billingData.setReactivePwrDmdMaxTimeLeadImportRate1(time1);
			billingData.setReactivePwrDmdMaxTimeLeadImportRate2(time2);
			billingData.setReactivePwrDmdMaxTimeLeadImportRate3(time3);

			billingData.setReactivePwrDmdMaxLeadImportRateTotal(value);
			billingData.setReactivePwrDmdMaxTimeLeadImportRateTotal(totTime);
		} else if(source == 4 ){
			billingData.setReactivePwrDmdMaxLagExportRate1(value1);
			billingData.setReactivePwrDmdMaxLagExportRate2(value2);
			billingData.setReactivePwrDmdMaxLagExportRate3(value3);
			billingData.setReactivePwrDmdMaxTimeLagExportRate1(time1);
			billingData.setReactivePwrDmdMaxTimeLagExportRate2(time2);
			billingData.setReactivePwrDmdMaxTimeLagExportRate3(time3);

			billingData.setReactivePwrDmdMaxLagExportRateTotal(value);
			billingData.setReactivePwrDmdMaxTimeLagExportRateTotal(totTime);
		} else if(source == 5){
			billingData.setReactivePwrDmdMaxLeadExportRate1(value1);
			billingData.setReactivePwrDmdMaxLeadExportRate2(value2);
			billingData.setReactivePwrDmdMaxLeadExportRate3(value3);
			billingData.setReactivePwrDmdMaxTimeLeadExportRate1(time1);
			billingData.setReactivePwrDmdMaxTimeLeadExportRate2(time2);
			billingData.setReactivePwrDmdMaxTimeLeadExportRate3(time3);

			billingData.setReactivePwrDmdMaxLeadExportRateTotal(value);
			billingData.setReactivePwrDmdMaxTimeLeadExportRateTotal(totTime);
		}else if(source == 6){
			billingData.setMaxDmdkVah1Rate1(value1);
			billingData.setMaxDmdkVah1Rate2(value2);
			billingData.setMaxDmdkVah1Rate3(value3);
			billingData.setMaxDmdkVah1RateTotal(value);
			
			billingData.setMaxDmdkVah1TimeRate1(time1);
			billingData.setMaxDmdkVah1TimeRate2(time2);
			billingData.setMaxDmdkVah1TimeRate3(time3);
			billingData.setMaxDmdkVah1TimeRateTotal(totTime);
		}
	}
	
	public String toString() {
	    try {
    		StringBuffer sb = new StringBuffer();
    		
    		sb.append("A1140_BILLING_DATA[\n")
    		  .append("  (billingTimestamp="                    ).append(billingData.getBillingTimestamp()).append(")\n")
    		  .append("  (activeEnergyImportRateTotal="         ).append(billingData.getActiveEnergyImportRateTotal()).append(")\n")
    		  .append("  (activeEnergyExportRateTotal="         ).append(billingData.getActiveEnergyExportRateTotal()).append(")\n")
    		  .append("  (activeEnergyRateTotal="		        ).append(billingData.getActiveEnergyRateTotal()).append(")\n")
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
	    catch (Exception e) {
	        return e.getMessage();
	    }
	}
}
