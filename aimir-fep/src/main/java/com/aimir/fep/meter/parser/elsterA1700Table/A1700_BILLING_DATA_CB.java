package com.aimir.fep.meter.parser.elsterA1700Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.parser.ElsterA1700;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;

/**
 * 
 * @author jhKim
 * A1700 CURRENT BILLING DATA PARSER
 */
public class A1700_BILLING_DATA_CB {    
	private Log log = LogFactory.getLog(A1700_BILLING_DATA_CB.class);
    
    public static final int OFS_CUMULATIVE_TOTAL                    = 0;
    public static final int OFS_CUMULATIVE_TOU                      = 80;
    public static final int OFS_CUMULATIVE_MD                       = 336;
    public static final int OFS_MAXIMUM_DEMAND                      = 408;
    
    public static final int LEN_CUMULATIVE_TOTAL                    = 80;
    public static final int LEN_CUMULATIVE_TOTAL_DATA               = 8;
    public static final int LEN_CUMULATIVE_TOU                      = 256;
    public static final int LEN_CUMULATIVE_TOU_DATA                 = 8;
    public static final int LEN_CUMULATIVE_MD                       = 72;
    public static final int LEN_CUMULATIVE_MD_DATA                  = 8;
    public static final int LEN_MAXIMUM_DEMAND                      = 288;
    public static final int LEN_MAXIMUM_DEMAND_DATA                 = 7;
    
    public static final int LEN_COUNT     = 2;
    public static final int LEN_TIMESTAMP = 4;
    public static final int LEN_SOURCE    = 1;
    
	private byte[] rawData = null;
	
	private double ke = 0.000001;
    
	BillingData cBillingData = new BillingData();
	
	/**
	 * Constructor
	 */
	public A1700_BILLING_DATA_CB(byte[] rawData) {
        this.rawData = rawData;
        try {
			parseBillingData();
		} catch (NumberFormatException e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		A1700_TEST_DATA testData = new A1700_TEST_DATA();
		A1700_BILLING_DATA_CB elster = new A1700_BILLING_DATA_CB(testData.getTestData_billing_cb());
		elster.parseBillingData();
		System.out.println(elster.toString());
	}
	
	public BillingData getBillingData() throws NumberFormatException, Exception {
		// parseBillingData();
		
		if (cBillingData != null) {
			return cBillingData;
		} else {
			return null;
		}
	}
	
	private void parseBillingData() throws NumberFormatException, Exception {
		parseCumulativeTotal();
		parseCumulativeTOU();		
		parseCumulativeMD();
		parseMaximumDemand();
	}
	
	private void parseCumulativeTotal() throws Exception {
		log.debug("START-----parseCumulativeTotal()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOTAL, LEN_CUMULATIVE_TOTAL);
		
		int offset = 0;
		Double value = null;
		Double actEngTot = 0.0;
		
		value = convertBCD2Double("TOTAL_IMPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cBillingData.setActiveEnergyImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		actEngTot += value;
		
        value = convertBCD2Double("TOTAL_EXPORT_kWh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
        cBillingData.setActiveEnergyExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		actEngTot += value;
		
		//ActiveEnergyRateTotal 추가.
		cBillingData.setActiveEnergyRateTotal(actEngTot);
		
		value = convertBCD2Double("TOTAL_IMPORT_LAGGING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cBillingData.setReactiveEnergyLagImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("TOTAL_IMPORT_LEADING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		cBillingData.setReactiveEnergyLeadImportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
        value = convertBCD2Double("TOTAL_EXPORT_LAGGING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
        cBillingData.setReactiveEnergyLagExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
        value = convertBCD2Double("TOTAL_EXPORT_LEADING_kvarh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
        cBillingData.setReactiveEnergyLeadExportRateTotal(value);
		offset += LEN_CUMULATIVE_TOTAL_DATA;
				
        value = convertBCD2Double("TOTAL_kVAh", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;//KVAh 로 변환 위해 /1000 으로 한다
		cBillingData.setkVah(value);
		
		// customer defined #1~3 저장 안함.
		value = convertBCD2Double("CUSTOMER_DEFINED #1", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("CUSTOMER_DEFINED #2", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;
		
		value = convertBCD2Double("CUSTOMER_DEFINED #3", DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
		offset += LEN_CUMULATIVE_TOTAL_DATA;

		log.debug("END-----parseCumulativeTotal()");
	}

	private void parseCumulativeTOU() throws Exception {
		log.debug("START-----parseCumulativeTOU()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_TOU, LEN_CUMULATIVE_TOU);
		
		int offset = 0;
		Double value = null;
		int cnt = 1;
		
		// CUMULATIVE_TOU 저장안함
		byte[] b = null;
		while (offset < data.length) {
			value = convertBCD2Double("CUMULATIVE_TOU #" + cnt, DataFormat.select(data, offset, LEN_CUMULATIVE_TOTAL_DATA));
			offset += LEN_CUMULATIVE_TOU_DATA;
			if (cnt == 1) { 
			    cBillingData.setActiveEnergyRate1(value);
			    cBillingData.setActiveEnergyImportRate1(value);
            }
            else if (cnt == 2) {
                cBillingData.setActiveEnergyRate2(value);
                cBillingData.setActiveEnergyImportRate2(value);
            }
            else if (cnt == 3) {
                cBillingData.setActiveEnergyRate3(value);
                cBillingData.setActiveEnergyImportRate3(value);
            }
			cnt++;
		}
		log.debug("END-----parseCumulativeTOU()");
	}
	
	private void parseCumulativeMD() throws Exception {
		log.debug("START-----parseCumulativeMD()");
		
		byte[] data = DataFormat.select(rawData, OFS_CUMULATIVE_MD, LEN_CUMULATIVE_MD);
		
		int offset = 0;
		Double value = null;
		int source = 0;
		
		byte[] b = null;
		////////////////////////////////////////////////////
		//
		//	if (source == 7)         
		//	billingData.setCummActivePwrDmdMaxImportRate1(value); ??
		//
		///////////////////////////////////////////////////////
		value = convertBCD2Double("CUMULATIVE_MD_REGISTER #1", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    setCumMaxDmd(source, value);	    
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #2", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #3", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #4", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #5", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #6", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #7", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    value = convertBCD2Double("CUMULATIVE_MD_REGISTER #8", DataFormat.select(data, offset, LEN_CUMULATIVE_MD_DATA));
		offset += LEN_CUMULATIVE_MD_DATA;
	    source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
	    offset += LEN_SOURCE;
	    
	    log.debug("END-----parseCumulativeMD()");
		/*
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
		*/
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
		
		byte[] b = null;
		/////////////////////////////////////////////////////////
		//
		// source == 7 ?
		// cBillingData.setActivePwrDmdMaxTimeImportRate1(timestamp);
		// cBillingData.setActivePwrDmdMaxImportRate1(value);
		//
		////////////////////////////////////////////////////////////
		for(int i = 0 ; i < 8 ; i++){
			log.debug( "record #" + (i+1) );
			timestamp1 = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#1", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");
	        value1 = convertBCD2Double("MAXIMUM_DEMAND_#1_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));  
			offset += LEN_MAXIMUM_DEMAND_DATA;

			
			timestamp2 = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#2", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");
	        value2 = convertBCD2Double("MAXIMUM_DEMAND_#2_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));  
			offset += LEN_MAXIMUM_DEMAND_DATA;
			

			timestamp3 = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#3", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
	        value3 = convertBCD2Double("MAXIMUM_DEMAND_#3_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));  
			offset += LEN_MAXIMUM_DEMAND_DATA;

		    setMaxDmd(i, source, timestamp1, timestamp2, timestamp3, value1, value2, value3);
		}
				
		
		/*
		while (offset < data.length) {
			
			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#1", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#1_SOURCE=[" + source + "]");

			if (source == 7) {
	            value = convertBCD2Double("MAXIMUM_DEMAND_#1_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
	            
				cBillingData.setActivePwrDmdMaxTimeImportRate1(timestamp);
				cBillingData.setActivePwrDmdMaxImportRate1(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;

			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#2", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#2_SOURCE=[" + source + "]");

			if (source == 7) {
	            value = convertBCD2Double("MAXIMUM_DEMAND_#2_REGISTER", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
	            
				cBillingData.setActivePwrDmdMaxTimeImportRate2(timestamp);
				cBillingData.setActivePwrDmdMaxImportRate2(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;
			
			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#3", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
			
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#3_SOURCE=[" + source + "]");
			
			if (source == 7 ) {
			    value = convertBCD2Double("MAXIMUM_DEMAND_#3", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
				cBillingData.setActivePwrDmdMaxTimeImportRate3(timestamp);
				cBillingData.setActivePwrDmdMaxImportRate3(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;

			timestamp = ElsterA1700.convertTimestamp("MAXIMUM_DEMAND_#4", DataFormat.select(data, offset, LEN_TIMESTAMP));
			offset += LEN_TIMESTAMP;
				
			source = DataFormat.getIntToBytes(DataFormat.select(data, offset, LEN_SOURCE));
			offset += LEN_SOURCE;
			log.debug("MAXIMUM_DEMAND_#4_SOURCE=[" + source + "]");
			
			if (source == 7) {
			    value = convertBCD2Double("MAXIMUM_DEMAND_#4", DataFormat.select(data, offset, LEN_MAXIMUM_DEMAND_DATA));
	            
				cBillingData.setActivePwrDmdMaxTimeImportRate4(timestamp);
				cBillingData.setActivePwrDmdMaxImportRate4(value);
			}
			offset += LEN_MAXIMUM_DEMAND_DATA;
		}
		*/
		log.debug("END-----parseMaximumDemand()");
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
			cBillingData.setCummActivePwrDmdMaxImportRate1(value);
			cBillingData.setCummActivePwrDmdMaxImportRate2(null);
			cBillingData.setCummActivePwrDmdMaxImportRate3(null);
			cBillingData.setCummActivePwrDmdMaxImportRateTotal(value);
		} else if(source == 1 ){
			cBillingData.setCummActivePwrDmdMaxExportRate1(value);
			cBillingData.setCummActivePwrDmdMaxExportRate2(null);
			cBillingData.setCummActivePwrDmdMaxExportRate3(null);
			cBillingData.setCummActivePwrDmdMaxExportRateTotal(value);
		} else if(source == 2 ){
			cBillingData.setCummReactivePwrDmdMaxLagImportRate1(value);
			cBillingData.setCummReactivePwrDmdMaxLagImportRate2(null);
			cBillingData.setCummReactivePwrDmdMaxLagImportRate3(null);
			cBillingData.setCummReactivePwrDmdMaxLagImportRateTotal(value);
		} else if(source == 3 ){
			cBillingData.setCummReactivePwrDmdMaxLeadImportRate1(value);
			cBillingData.setCummReactivePwrDmdMaxLeadImportRate2(null);
			cBillingData.setCummReactivePwrDmdMaxLeadImportRate3(null);
			cBillingData.setCummReactivePwrDmdMaxLeadImportRateTotal(value);
		} else if(source == 4 ){
			cBillingData.setCummReactivePwrDmdMaxLagExportRate1(value);
			cBillingData.setCummReactivePwrDmdMaxLagExportRate2(null);
			cBillingData.setCummReactivePwrDmdMaxLagExportRate3(null);
			cBillingData.setCummReactivePwrDmdMaxLagExportRateTotal(value);
		} else if(source == 5 ){
			cBillingData.setCummReactivePwrDmdMaxLeadExportRate1(value);
			cBillingData.setCummReactivePwrDmdMaxLeadExportRate2(null);
			cBillingData.setCummReactivePwrDmdMaxLeadExportRate3(null);
			cBillingData.setCummReactivePwrDmdMaxLeadExportRateTotal(value);
		} else if(source == 6){
			cBillingData.setCummkVah1Rate1(value);
			cBillingData.setCummkVah1Rate2(null);
			cBillingData.setCummkVah1Rate3(null);
			cBillingData.setCummkVah1RateTotal(value);
		} else if(source == 7){
			// cBillingData.setCummActivePwrDmdMaxImportRate1(value);
			// cBillingData.setCummActivePwrDmdMaxImportRate2(null);
			// cBillingData.setCummActivePwrDmdMaxImportRate3(null);
			// cBillingData.setCummActivePwrDmdMaxImportRateTotal(value);
		}
	}
    
    private void setMaxDmd(int record, int source, String time1, String time2, String time3, Double value1, Double value2, Double value3 ){
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
		
		if (cBillingData == null) cBillingData = new BillingData();
		
		if (record == 0) {
		    cBillingData.setActivePwrDmdMaxImportRateTotal(value);
		    cBillingData.setActivePwrDmdMaxTimeImportRateTotal(totTime);
            // 어느 필드를 사용할 지 알 수 없어서 추가함.
            cBillingData.setMaxDmdkVah1Rate1(value);
            cBillingData.setMaxDmdkVah1TimeRate1(totTime);
            cBillingData.setMaxDmdkVah1RateTotal(value);
            cBillingData.setMaxDmdkVah1TimeRateTotal(totTime);
		}
		else if (record == 1) {
		    cBillingData.setActivePwrDmdMaxImportRate1(value);
            cBillingData.setActivePwrDmdMaxTimeImportRate1(totTime);
            // 어느 필드를 사용할 지 알 수 없어서 추가함.
            cBillingData.setMaxDmdkVah1Rate1(value);
            cBillingData.setMaxDmdkVah1TimeRate1(totTime);
		}
		else if (record == 2) {
		    cBillingData.setActivePwrDmdMaxImportRate2(value);
            cBillingData.setActivePwrDmdMaxTimeImportRate2(totTime);
            // 어느 필드를 사용할 지 알 수 없어서 추가함.
            cBillingData.setMaxDmdkVah1Rate2(value);
            cBillingData.setMaxDmdkVah1TimeRate2(totTime);
		}
		else if (record == 3) {
		    cBillingData.setActivePwrDmdMaxImportRate3(value);
            cBillingData.setActivePwrDmdMaxTimeImportRate3(totTime);
            // 어느 필드를 사용할 지 알 수 없어서 추가함.
            cBillingData.setMaxDmdkVah1Rate3(value);
            cBillingData.setMaxDmdkVah1TimeRate3(totTime);
		}
		
		/*
		if(source == 0){
			cBillingData.setActivePwrDmdMaxImportRate1(value1);
			cBillingData.setActivePwrDmdMaxImportRate2(value2);
			cBillingData.setActivePwrDmdMaxImportRate3(value3);
			cBillingData.setActivePwrDmdMaxTimeImportRate1(time1);
			cBillingData.setActivePwrDmdMaxTimeImportRate2(time2);
			cBillingData.setActivePwrDmdMaxTimeImportRate3(time3);			
			
			cBillingData.setActivePwrDmdMaxImportRateTotal(value);
			cBillingData.setActivePwrDmdMaxTimeImportRateTotal(totTime);
		} else if(source == 1 ){
			cBillingData.setActivePwrDmdMaxExportRate1(value1);
			cBillingData.setActivePwrDmdMaxExportRate2(value2);
			cBillingData.setActivePwrDmdMaxExportRate3(value3);
			cBillingData.setActivePwrDmdMaxTimeExportRate1(time1);
			cBillingData.setActivePwrDmdMaxTimeExportRate2(time2);
			cBillingData.setActivePwrDmdMaxTimeExportRate3(time3);

			cBillingData.setActivePwrDmdMaxExportRateTotal(value);
			cBillingData.setActivePwrDmdMaxTimeExportRateTotal(totTime);
		} else if(source == 2 ){
			cBillingData.setReactivePwrDmdMaxLagImportRate1(value1);
			cBillingData.setReactivePwrDmdMaxLagImportRate2(value2);
			cBillingData.setReactivePwrDmdMaxLagImportRate3(value3);
			cBillingData.setReactivePwrDmdMaxTimeLagImportRate1(time1);
			cBillingData.setReactivePwrDmdMaxTimeLagImportRate2(time2);
			cBillingData.setReactivePwrDmdMaxTimeLagImportRate3(time3);

			cBillingData.setReactivePwrDmdMaxLagImportRateTotal(value);
			cBillingData.setReactivePwrDmdMaxTimeLagImportRateTotal(totTime);	
		} else if(source == 3 ){
			cBillingData.setReactivePwrDmdMaxLeadImportRate1(value1);
			cBillingData.setReactivePwrDmdMaxLeadImportRate2(value2);
			cBillingData.setReactivePwrDmdMaxLeadImportRate3(value3);
			cBillingData.setReactivePwrDmdMaxTimeLeadImportRate1(time1);
			cBillingData.setReactivePwrDmdMaxTimeLeadImportRate2(time2);
			cBillingData.setReactivePwrDmdMaxTimeLeadImportRate3(time3);

			cBillingData.setReactivePwrDmdMaxLeadImportRateTotal(value);
			cBillingData.setReactivePwrDmdMaxTimeLeadImportRate4(totTime);
		} else if(source == 4 ){
			cBillingData.setReactivePwrDmdMaxLagExportRate1(value1);
			cBillingData.setReactivePwrDmdMaxLagExportRate2(value2);
			cBillingData.setReactivePwrDmdMaxLagExportRate3(value3);
			cBillingData.setReactivePwrDmdMaxTimeLagExportRate1(time1);
			cBillingData.setReactivePwrDmdMaxTimeLagExportRate2(time2);
			cBillingData.setReactivePwrDmdMaxTimeLagExportRate3(time3);

			cBillingData.setReactivePwrDmdMaxLagExportRateTotal(value);
			cBillingData.setReactivePwrDmdMaxTimeLagExportRateTotal(totTime);
		} else if(source == 5){
			cBillingData.setReactivePwrDmdMaxLeadExportRate1(value1);
			cBillingData.setReactivePwrDmdMaxLeadExportRate2(value2);
			cBillingData.setReactivePwrDmdMaxLeadExportRate3(value3);
			cBillingData.setReactivePwrDmdMaxTimeLeadExportRate1(time1);
			cBillingData.setReactivePwrDmdMaxTimeLeadExportRate2(time2);
			cBillingData.setReactivePwrDmdMaxTimeLeadExportRate3(time3);

			cBillingData.setReactivePwrDmdMaxLeadExportRateTotal(value);
			cBillingData.setReactivePwrDmdMaxTimeLeadExportRateTotal(totTime);
		}else if(source == 6){
			cBillingData.setMaxDmdkVah1Rate1(value1);
			cBillingData.setMaxDmdkVah1Rate2(value2);
			cBillingData.setMaxDmdkVah1Rate3(value3);			

			cBillingData.setMaxDmdkVah1RateTotal(value);
		} else if( source == 7 ){
			 cBillingData.setActivePwrDmdMaxTimeImportRate1(time1);
			 cBillingData.setActivePwrDmdMaxTimeImportRate2(time2);
			 cBillingData.setActivePwrDmdMaxTimeImportRate3(time3);
			 cBillingData.setActivePwrDmdMaxTimeImportRateTotal(totTime);
			 
			 cBillingData.setActivePwrDmdMaxImportRate1(value1);
			 cBillingData.setActivePwrDmdMaxImportRate1(value2);
			 cBillingData.setActivePwrDmdMaxImportRate1(value3);
			 cBillingData.setActivePwrDmdMaxImportRateTotal(value);			
		}    
        */
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		
		sb.append("A1700_BILLING_DATA_CB[\n")
		  .append("  (activeEnergyImportRateTotal="         ).append(cBillingData.getActiveEnergyImportRateTotal()).append(")\n")
		  .append("  (activeEnergyExportRateTotal="         ).append(cBillingData.getActiveEnergyExportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLagImportRateTotal="    ).append(cBillingData.getReactiveEnergyLagImportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLeadImportRateTotal="   ).append(cBillingData.getReactiveEnergyLeadImportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLagExportRateTotal="    ).append(cBillingData.getReactiveEnergyLagExportRateTotal()).append(")\n")
		  .append("  (reactiveEnergyLeadExportRateTotal="   ).append(cBillingData.getReactiveEnergyLeadExportRateTotal()).append(")\n")
		  .append("  (activePwrDmdMaxImportRate1="          ).append(cBillingData.getActivePwrDmdMaxImportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxExportRate1="          ).append(cBillingData.getActivePwrDmdMaxExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagImportRate1="     ).append(cBillingData.getReactivePwrDmdMaxLagImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadImportRate1="    ).append(cBillingData.getReactivePwrDmdMaxLeadImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagExportRate1="     ).append(cBillingData.getReactivePwrDmdMaxLagExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadExportRate1="    ).append(cBillingData.getReactivePwrDmdMaxLeadExportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxTimeImportRate1="      ).append(cBillingData.getActivePwrDmdMaxTimeImportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxTimeExportRate1="      ).append(cBillingData.getActivePwrDmdMaxTimeExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagImportRate1=" ).append(cBillingData.getReactivePwrDmdMaxTimeLagImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadImportRate1=").append(cBillingData.getReactivePwrDmdMaxTimeLeadImportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagExportRate1=" ).append(cBillingData.getReactivePwrDmdMaxTimeLagExportRate1()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadExportRate1=").append(cBillingData.getReactivePwrDmdMaxTimeLeadExportRate1()).append(")\n")
		  .append("  (activePwrDmdMaxImportRate2="          ).append(cBillingData.getActivePwrDmdMaxImportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxExportRate2="          ).append(cBillingData.getActivePwrDmdMaxExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagImportRate2="     ).append(cBillingData.getReactivePwrDmdMaxLagImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadImportRate2="    ).append(cBillingData.getReactivePwrDmdMaxLeadImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagExportRate2="     ).append(cBillingData.getReactivePwrDmdMaxLagExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadExportRate2="    ).append(cBillingData.getReactivePwrDmdMaxLeadExportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxTimeImportRate2="      ).append(cBillingData.getActivePwrDmdMaxTimeImportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxTimeExportRate2="      ).append(cBillingData.getActivePwrDmdMaxTimeExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagImportRate2=" ).append(cBillingData.getReactivePwrDmdMaxTimeLagImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadImportRate2=").append(cBillingData.getReactivePwrDmdMaxTimeLeadImportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagExportRate2=" ).append(cBillingData.getReactivePwrDmdMaxTimeLagExportRate2()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadExportRate2=").append(cBillingData.getReactivePwrDmdMaxTimeLeadExportRate2()).append(")\n")
		  .append("  (activePwrDmdMaxImportRate3="          ).append(cBillingData.getActivePwrDmdMaxImportRate3()).append(")\n")
		  .append("  (activePwrDmdMaxExportRate3="          ).append(cBillingData.getActivePwrDmdMaxExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagImportRate3="     ).append(cBillingData.getReactivePwrDmdMaxLagImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadImportRate3="    ).append(cBillingData.getReactivePwrDmdMaxLeadImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLagExportRate3="     ).append(cBillingData.getReactivePwrDmdMaxLagExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxLeadExportRate3="    ).append(cBillingData.getReactivePwrDmdMaxLeadExportRate3()).append(")\n")
		  .append("  (activePwrDmdMaxTimeImportRate3="      ).append(cBillingData.getActivePwrDmdMaxTimeImportRate3()).append(")\n")
		  .append("  (activePwrDmdMaxTimeExportRate3="      ).append(cBillingData.getActivePwrDmdMaxTimeExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagImportRate3=" ).append(cBillingData.getReactivePwrDmdMaxTimeLagImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadImportRate3=").append(cBillingData.getReactivePwrDmdMaxTimeLeadImportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLagExportRate3=" ).append(cBillingData.getReactivePwrDmdMaxTimeLagExportRate3()).append(")\n")
		  .append("  (reactivePwrDmdMaxTimeLeadExportRate3=").append(cBillingData.getReactivePwrDmdMaxTimeLeadExportRate3()).append(")\n")
		  .append("  (cummActivePwrDmdMaxImportRate1="      ).append(cBillingData.getCummActivePwrDmdMaxImportRate1()).append(")\n")
		  .append("  (cummActivePwrDmdMaxExportRate1="      ).append(cBillingData.getCummActivePwrDmdMaxExportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLagImportRate1=" ).append(cBillingData.getCummReactivePwrDmdMaxLagImportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLeadImportRate1=").append(cBillingData.getCummReactivePwrDmdMaxLeadImportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLagExportRate1=" ).append(cBillingData.getCummReactivePwrDmdMaxLagExportRate1()).append(")\n")
		  .append("  (cummReactivePwrDmdMaxLeadExportRate1=").append(cBillingData.getCummReactivePwrDmdMaxLeadExportRate1()).append(")\n")
		  .append("]");
		
		return sb.toString();
	}
}
