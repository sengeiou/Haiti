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
 * @author jhkim
 *
 */
public class A1140_CB_BILLING_DATA {    
	private static Log log = LogFactory.getLog(A1140_CB_BILLING_DATA.class);
    
    public static final int OFS_CUMULATIVE_TOTAL                    = 0;
    public static final int OFS_CUMULATIVE_TOU                      = 128;
    public static final int OFS_CUMULATIVE_MD                       = 192;
    public static final int OFS_MAXIMUM_DEMAND                      = 228;
    
    public static final int LEN_CUMULATIVE_TOTAL                    = 128;
    public static final int LEN_CUMULATIVE_TOTAL_DATA               = 8;
    public static final int LEN_CUMULATIVE_TOU                      = 64;
    public static final int LEN_CUMULATIVE_TOU_DATA                 = 8;
    
    public static final int LEN_CUMULATIVE_MD                       = 36;
    public static final int LEN_CUMULATIVE_MD_SOURCE                = 1;
    public static final int LEN_CUMULATIVE_MD_DATA                  = 8;
    
    public static final int LEN_MAXIMUM_DEMAND                      = 144;
    public static final int LEN_MAXIMUM_DEMAND_DATA                 = 7;
    
    public static final int LEN_COUNT     = 2;
    public static final int LEN_TIMESTAMP = 4;
    public static final int LEN_SOURCE    = 1;
    
	private byte[] rawData = null;
	
	private double ke = 0.000001; // kW, kvar;
	
	BillingData cbBillingData = null;
	List<EventLogData> billingResetEventList = new ArrayList<EventLogData>();
	
	DecimalFormat df = new DecimalFormat("#0.000000");
	/**
	 * Constructor
	 */
	public A1140_CB_BILLING_DATA(byte[] rawData) {
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
		A1140_CB_BILLING_DATA elster = new A1140_CB_BILLING_DATA(testData.getTestData_billing());
		//elster.parseBillingData();
		System.out.println(elster.toString());
	}
	
	public BillingData getBillingData() {
		return cbBillingData;
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
	}
	
	private void parseCumulativeTotal() throws Exception {
	    cbBillingData = new BillingData();
		//log.debug("START-----parseCumulativeTotal()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOTAL, LEN_CUMULATIVE_TOTAL);
		
		int offset = 0;
		Double value = null;
		Double actEngTot = 0.0;
		
		value = convertBCD2Double("TOTAL_IMPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setActiveEnergyImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		actEngTot += value;
		
		value = convertBCD2Double("TOTAL_EXPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setActiveEnergyExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		actEngTot += value;
		
		//ActiveEnergyRateTotal 추가.
		cbBillingData.setActiveEnergyRateTotal(actEngTot);
		
		value = convertBCD2Double("TOTAL Q1 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setReactiveEnergyLagImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL Q2 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setReactiveEnergyLeadImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL Q3 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setReactiveEnergyLagExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL Q4 kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setReactiveEnergyLeadExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		// total vah 저장안함
		value = convertBCD2Double("TOTAL kvah1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cbBillingData.setkVah(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA; 
		
		value = convertBCD2Double("TOTAL kvah2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		// Reserved1
		value = convertBCD2Double("Reserved1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		cbBillingData.setImportkWhPhaseA(value);
		// Reserved2
		value = convertBCD2Double("Reserved2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		cbBillingData.setImportkWhPhaseB(value);
		// Reserved3
		value = convertBCD2Double("Reserved3", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		cbBillingData.setImportkWhPhaseC(value);
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
		cbBillingData.setActiveEnergyImportRate1(value);
		
		value = convertBCD2Double("TOU2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		cbBillingData.setActiveEnergyImportRate2(value);
		
		value = convertBCD2Double("TOU3", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		cbBillingData.setActiveEnergyImportRate3(value);
		
		value = convertBCD2Double("TOU4", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		cbBillingData.setActiveEnergyImportRate4(value);				
		
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
		Double TotVal	= null;
		Double rate1Val	= null;
		Double rate2Val = null;
		Double rate3Val = null;
		Double rate4Val = null;
		int source = 0;

		rate1Val = convertBCD2Double("CUMULATIVE MD[1]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		
		setCumMaxDmd(source, rate1Val, "1");
		
		rate2Val = convertBCD2Double("CUMULATIVE MD[2]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, rate2Val, "2");
		
		rate3Val = convertBCD2Double("CUMULATIVE MD[3]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, rate3Val, "3");
		
		rate4Val = convertBCD2Double("CUMULATIVE MD[4]", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
		source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
		offset += LEN_SOURCE;
		setCumMaxDmd(source, rate4Val, "4");
		
	}
	
	private void parseMaximumDemand() throws Exception {
		log.debug("START-----parseMaximumDemand()");
		
		byte[] data = DataFormat.select(rawData, OFS_MAXIMUM_DEMAND, LEN_MAXIMUM_DEMAND); 
		int offset = 0;
		int source = 0;
		
		Double value1 = 0.0;
		Double value2 = 0.0;
		Double value3 = 0.0;

		String timestamp1 = "";
		String timestamp2 = "";
		String timestamp3 = "";
				
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
			cbBillingData.setActivePwrDmdMaxImportRate1(value1);
			cbBillingData.setActivePwrDmdMaxImportRate2(value2);
			cbBillingData.setActivePwrDmdMaxImportRate3(value3);
			cbBillingData.setActivePwrDmdMaxTimeImportRate1(time1);
			cbBillingData.setActivePwrDmdMaxTimeImportRate2(time2);
			cbBillingData.setActivePwrDmdMaxTimeImportRate3(time3);			
			
			cbBillingData.setActivePwrDmdMaxImportRateTotal(value);
			cbBillingData.setActivePwrDmdMaxTimeImportRateTotal(totTime);
		} else if(source == 1 ){
			cbBillingData.setActivePwrDmdMaxExportRate1(value1);
			cbBillingData.setActivePwrDmdMaxExportRate2(value2);
			cbBillingData.setActivePwrDmdMaxExportRate3(value3);
			cbBillingData.setActivePwrDmdMaxTimeExportRate1(time1);
			cbBillingData.setActivePwrDmdMaxTimeExportRate2(time2);
			cbBillingData.setActivePwrDmdMaxTimeExportRate3(time3);

			cbBillingData.setActivePwrDmdMaxExportRateTotal(value);
			cbBillingData.setActivePwrDmdMaxTimeExportRateTotal(totTime);
		} else if(source == 2 ){
			cbBillingData.setReactivePwrDmdMaxLagImportRate1(value1);
			cbBillingData.setReactivePwrDmdMaxLagImportRate2(value2);
			cbBillingData.setReactivePwrDmdMaxLagImportRate3(value3);
			cbBillingData.setReactivePwrDmdMaxTimeLagImportRate1(time1);
			cbBillingData.setReactivePwrDmdMaxTimeLagImportRate2(time2);
			cbBillingData.setReactivePwrDmdMaxTimeLagImportRate3(time3);

			cbBillingData.setReactivePwrDmdMaxLagImportRateTotal(value);
			cbBillingData.setReactivePwrDmdMaxTimeLagImportRateTotal(totTime);	
		} else if(source == 3 ){
			cbBillingData.setReactivePwrDmdMaxLeadImportRate1(value1);
			cbBillingData.setReactivePwrDmdMaxLeadImportRate2(value2);
			cbBillingData.setReactivePwrDmdMaxLeadImportRate3(value3);
			cbBillingData.setReactivePwrDmdMaxTimeLeadImportRate1(time1);
			cbBillingData.setReactivePwrDmdMaxTimeLeadImportRate2(time2);
			cbBillingData.setReactivePwrDmdMaxTimeLeadImportRate3(time3);

			cbBillingData.setReactivePwrDmdMaxLeadImportRateTotal(value);
			cbBillingData.setReactivePwrDmdMaxTimeLeadImportRateTotal(totTime);
		} else if(source == 4 ){
			cbBillingData.setReactivePwrDmdMaxLagExportRate1(value1);
			cbBillingData.setReactivePwrDmdMaxLagExportRate2(value2);
			cbBillingData.setReactivePwrDmdMaxLagExportRate3(value3);
			cbBillingData.setReactivePwrDmdMaxTimeLagExportRate1(time1);
			cbBillingData.setReactivePwrDmdMaxTimeLagExportRate2(time2);
			cbBillingData.setReactivePwrDmdMaxTimeLagExportRate3(time3);

			cbBillingData.setReactivePwrDmdMaxLagExportRateTotal(value);
			cbBillingData.setReactivePwrDmdMaxTimeLagExportRateTotal(totTime);
		} else if(source == 5){
			cbBillingData.setReactivePwrDmdMaxLeadExportRate1(value1);
			cbBillingData.setReactivePwrDmdMaxLeadExportRate2(value2);
			cbBillingData.setReactivePwrDmdMaxLeadExportRate3(value3);
			cbBillingData.setReactivePwrDmdMaxTimeLeadExportRate1(time1);
			cbBillingData.setReactivePwrDmdMaxTimeLeadExportRate2(time2);
			cbBillingData.setReactivePwrDmdMaxTimeLeadExportRate3(time3);

			cbBillingData.setReactivePwrDmdMaxLeadExportRateTotal(value);
			cbBillingData.setReactivePwrDmdMaxTimeLeadExportRateTotal(totTime);
		}else if(source == 6){
			cbBillingData.setMaxDmdkVah1Rate1(value1);
			cbBillingData.setMaxDmdkVah1Rate2(value2);
			cbBillingData.setMaxDmdkVah1Rate3(value3);
			cbBillingData.setMaxDmdkVah1RateTotal(value);
			
			cbBillingData.setMaxDmdkVah1TimeRate1(time1);
			cbBillingData.setMaxDmdkVah1TimeRate2(time2);
			cbBillingData.setMaxDmdkVah1TimeRate3(time3);
			cbBillingData.setMaxDmdkVah1TimeRateTotal(totTime);
		}
	}
	
	private void setCumMaxDmd(int source, Double value, String rate){
		if(rate.equals("1")){
			if(source == 0){
				cbBillingData.setCummActivePwrDmdMaxImportRate1(value);
				cbBillingData.setCummActivePwrDmdMaxImportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxImportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxImportRateTotal()));
			} else if(source == 1 ){
				cbBillingData.setCummActivePwrDmdMaxExportRate1(value);
				cbBillingData.setCummActivePwrDmdMaxExportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxExportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxExportRateTotal()));
			} else if(source == 2 ){
				cbBillingData.setCummReactivePwrDmdMaxLagImportRate1(value);
				cbBillingData.setCummReactivePwrDmdMaxLagImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()));
			} else if(source == 3 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRate1(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()));
			} else if(source == 4 ){
				cbBillingData.setCummReactivePwrDmdMaxLagExportRate1(value);
				cbBillingData.setCummReactivePwrDmdMaxLagExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()));
			} else if(source == 5 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRate1(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()));
			} else if(source == 6){
				cbBillingData.setCummkVah1Rate1(value);
				cbBillingData.setCummkVah1RateTotal(value + (cbBillingData.getCummkVah1RateTotal()==null?0:cbBillingData.getCummkVah1RateTotal()));
			}
		} else if(rate.equals("2")){
			if(source == 0){
				cbBillingData.setCummActivePwrDmdMaxImportRate2(value);
				cbBillingData.setCummActivePwrDmdMaxImportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxImportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxImportRateTotal()));
			} else if(source == 1 ){
				cbBillingData.setCummActivePwrDmdMaxExportRate2(value);
				cbBillingData.setCummActivePwrDmdMaxExportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxExportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxExportRateTotal()));
			} else if(source == 2 ){
				cbBillingData.setCummReactivePwrDmdMaxLagImportRate2(value);
				cbBillingData.setCummReactivePwrDmdMaxLagImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()));
			} else if(source == 3 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRate2(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()));
			} else if(source == 4 ){
				cbBillingData.setCummReactivePwrDmdMaxLagExportRate2(value);
				cbBillingData.setCummReactivePwrDmdMaxLagExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()));
			} else if(source == 5 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRate2(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()));
			} else if(source == 6){
				cbBillingData.setCummkVah1Rate2(value);
				cbBillingData.setCummkVah1RateTotal(value + (cbBillingData.getCummkVah1RateTotal()==null?0:cbBillingData.getCummkVah1RateTotal()));
			}
		} else if(rate.equals("3")){
			if(source == 0){
				cbBillingData.setCummActivePwrDmdMaxImportRate3(value);
				cbBillingData.setCummActivePwrDmdMaxImportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxImportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxImportRateTotal()));
			} else if(source == 1 ){
				cbBillingData.setCummActivePwrDmdMaxExportRate3(value);
				cbBillingData.setCummActivePwrDmdMaxExportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxExportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxExportRateTotal()));
			} else if(source == 2 ){
				cbBillingData.setCummReactivePwrDmdMaxLagImportRate3(value);
				cbBillingData.setCummReactivePwrDmdMaxLagImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()));
			} else if(source == 3 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRate3(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()));
			} else if(source == 4 ){
				cbBillingData.setCummReactivePwrDmdMaxLagExportRate3(value);
				cbBillingData.setCummReactivePwrDmdMaxLagExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()));
			} else if(source == 5 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRate3(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()));
			} else if(source == 6){
				cbBillingData.setCummkVah1Rate3(value);
				cbBillingData.setCummkVah1RateTotal(value + (cbBillingData.getCummkVah1RateTotal()==null?0:cbBillingData.getCummkVah1RateTotal()));
			}
		} else if(rate.equals("4")){
			if(source == 0){
				cbBillingData.setCummActivePwrDmdMaxImportRate4(value);
				cbBillingData.setCummActivePwrDmdMaxImportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxImportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxImportRateTotal()));
			} else if(source == 1 ){
				cbBillingData.setCummActivePwrDmdMaxExportRate4(value);
				cbBillingData.setCummActivePwrDmdMaxExportRateTotal(value + (cbBillingData.getCummActivePwrDmdMaxExportRateTotal()==null?0:cbBillingData.getCummActivePwrDmdMaxExportRateTotal()));
			} else if(source == 2 ){
				cbBillingData.setCummReactivePwrDmdMaxLagImportRate4(value);
				cbBillingData.setCummReactivePwrDmdMaxLagImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagImportRateTotal()));
			} else if(source == 3 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRate4(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadImportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadImportRateTotal()));
			} else if(source == 4 ){
				cbBillingData.setCummReactivePwrDmdMaxLagExportRate4(value);
				cbBillingData.setCummReactivePwrDmdMaxLagExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLagExportRateTotal()));
			} else if(source == 5 ){
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRate4(value);
				cbBillingData.setCummReactivePwrDmdMaxLeadExportRateTotal(value + (cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()==null?0:cbBillingData.getCummReactivePwrDmdMaxLeadExportRateTotal()));
			} else if(source == 6){
				cbBillingData.setCummkVah1Rate4(value);
				cbBillingData.setCummkVah1RateTotal(value + (cbBillingData.getCummkVah1RateTotal()==null?0:cbBillingData.getCummkVah1RateTotal()));
			}
		}
		
	}

	private double convertBCD2Double(String title, byte[] data) {
        byte[] b = data;
        DataFormat.convertEndian(b);
        double d = Double.parseDouble(Hex.decode(b))*ke;
        log.debug(title+"=[" + d + "] RAW=" + Hex.decode(data)+"]");
        return d;
    }
	
	public String toString() {
	    try {
    		StringBuffer sb = new StringBuffer();
    		
    		sb.append("A1140_CB_BILLING_DATA[\n")
    		  .append("  (billingTimestamp="                    ).append(cbBillingData.getBillingTimestamp()).append(")\n")
    		  .append("  (activeEnergyImportRateTotal="         ).append(cbBillingData.getActiveEnergyImportRateTotal()).append(")\n")
    		  .append("  (activeEnergyExportRateTotal="         ).append(cbBillingData.getActiveEnergyExportRateTotal()).append(")\n")
    		  .append("  (activeEnergyRateTotal="		        ).append(cbBillingData.getActiveEnergyRateTotal()).append(")\n")
    		  .append("  (reactiveEnergyLagImportRateTotal="    ).append(cbBillingData.getReactiveEnergyLagImportRateTotal()).append(")\n")
    		  .append("  (reactiveEnergyLeadImportRateTotal="   ).append(cbBillingData.getReactiveEnergyLeadImportRateTotal()).append(")\n")
    		  .append("  (reactiveEnergyLagExportRateTotal="    ).append(cbBillingData.getReactiveEnergyLagExportRateTotal()).append(")\n")
    		  .append("  (reactiveEnergyLeadExportRateTotal="   ).append(cbBillingData.getReactiveEnergyLeadExportRateTotal()).append(")\n")
    		  .append("  (activePwrDmdMaxImportRate1="          ).append(cbBillingData.getActivePwrDmdMaxImportRate1()).append(")\n")
    		  .append("  (activePwrDmdMaxExportRate1="          ).append(cbBillingData.getActivePwrDmdMaxExportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLagImportRate1="     ).append(cbBillingData.getReactivePwrDmdMaxLagImportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLeadImportRate1="    ).append(cbBillingData.getReactivePwrDmdMaxLeadImportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLagExportRate1="     ).append(cbBillingData.getReactivePwrDmdMaxLagExportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLeadExportRate1="    ).append(cbBillingData.getReactivePwrDmdMaxLeadExportRate1()).append(")\n")
    		  .append("  (activePwrDmdMaxTimeImportRate1="      ).append(cbBillingData.getActivePwrDmdMaxTimeImportRate1()).append(")\n")
    		  .append("  (activePwrDmdMaxTimeExportRate1="      ).append(cbBillingData.getActivePwrDmdMaxTimeExportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLagImportRate1=" ).append(cbBillingData.getReactivePwrDmdMaxTimeLagImportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLeadImportRate1=").append(cbBillingData.getReactivePwrDmdMaxTimeLeadImportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLagExportRate1=" ).append(cbBillingData.getReactivePwrDmdMaxTimeLagExportRate1()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLeadExportRate1=").append(cbBillingData.getReactivePwrDmdMaxTimeLeadExportRate1()).append(")\n")
    		  .append("  (activePwrDmdMaxImportRate2="          ).append(cbBillingData.getActivePwrDmdMaxImportRate2()).append(")\n")
    		  .append("  (activePwrDmdMaxExportRate2="          ).append(cbBillingData.getActivePwrDmdMaxExportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLagImportRate2="     ).append(cbBillingData.getReactivePwrDmdMaxLagImportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLeadImportRate2="    ).append(cbBillingData.getReactivePwrDmdMaxLeadImportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLagExportRate2="     ).append(cbBillingData.getReactivePwrDmdMaxLagExportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLeadExportRate2="    ).append(cbBillingData.getReactivePwrDmdMaxLeadExportRate2()).append(")\n")
    		  .append("  (activePwrDmdMaxTimeImportRate2="      ).append(cbBillingData.getActivePwrDmdMaxTimeImportRate2()).append(")\n")
    		  .append("  (activePwrDmdMaxTimeExportRate2="      ).append(cbBillingData.getActivePwrDmdMaxTimeExportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLagImportRate2=" ).append(cbBillingData.getReactivePwrDmdMaxTimeLagImportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLeadImportRate2=").append(cbBillingData.getReactivePwrDmdMaxTimeLeadImportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLagExportRate2=" ).append(cbBillingData.getReactivePwrDmdMaxTimeLagExportRate2()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLeadExportRate2=").append(cbBillingData.getReactivePwrDmdMaxTimeLeadExportRate2()).append(")\n")
    		  .append("  (activePwrDmdMaxImportRate3="          ).append(cbBillingData.getActivePwrDmdMaxImportRate3()).append(")\n")
    		  .append("  (activePwrDmdMaxExportRate3="          ).append(cbBillingData.getActivePwrDmdMaxExportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLagImportRate3="     ).append(cbBillingData.getReactivePwrDmdMaxLagImportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLeadImportRate3="    ).append(cbBillingData.getReactivePwrDmdMaxLeadImportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLagExportRate3="     ).append(cbBillingData.getReactivePwrDmdMaxLagExportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxLeadExportRate3="    ).append(cbBillingData.getReactivePwrDmdMaxLeadExportRate3()).append(")\n")
    		  .append("  (activePwrDmdMaxTimeImportRate3="      ).append(cbBillingData.getActivePwrDmdMaxTimeImportRate3()).append(")\n")
    		  .append("  (activePwrDmdMaxTimeExportRate3="      ).append(cbBillingData.getActivePwrDmdMaxTimeExportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLagImportRate3=" ).append(cbBillingData.getReactivePwrDmdMaxTimeLagImportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLeadImportRate3=").append(cbBillingData.getReactivePwrDmdMaxTimeLeadImportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLagExportRate3=" ).append(cbBillingData.getReactivePwrDmdMaxTimeLagExportRate3()).append(")\n")
    		  .append("  (reactivePwrDmdMaxTimeLeadExportRate3=").append(cbBillingData.getReactivePwrDmdMaxTimeLeadExportRate3()).append(")\n")
    		  .append("  (cummActivePwrDmdMaxImportRate1="      ).append(cbBillingData.getCummActivePwrDmdMaxImportRate1()).append(")\n")
    		  .append("  (cummActivePwrDmdMaxExportRate1="      ).append(cbBillingData.getCummActivePwrDmdMaxExportRate1()).append(")\n")
    		  .append("  (cummReactivePwrDmdMaxLagImportRate1=" ).append(cbBillingData.getCummReactivePwrDmdMaxLagImportRate1()).append(")\n")
    		  .append("  (cummReactivePwrDmdMaxLeadImportRate1=").append(cbBillingData.getCummReactivePwrDmdMaxLeadImportRate1()).append(")\n")
    		  .append("  (cummReactivePwrDmdMaxLagExportRate1=" ).append(cbBillingData.getCummReactivePwrDmdMaxLagExportRate1()).append(")\n")
    		  .append("  (cummReactivePwrDmdMaxLeadExportRate1=").append(cbBillingData.getCummReactivePwrDmdMaxLeadExportRate1()).append(")\n")
    		  .append("]");
    		
    		return sb.toString();
	    }
	    catch (Exception e) {
	        return e.getMessage();
	    }
	}
}
