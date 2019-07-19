package com.aimir.fep.meter.parser.amuLsrwRs232Table;

import java.text.DecimalFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.Util;

/**
 * LSRW_PB Billing Data Field
 * 
 * @author  : taeho Park 
 * @version : $REV $1, 2010. 3. 11. 오후 5:49:10$
 */
public class LSRWRS232_PB{

    private static Log log = LogFactory.getLog(LSRWRS232_PB.class);
    /**
     * ***************  WORD SINPFEMO ***************
     * 단어의 길이가 길어 임의적으로 약어를 적용하였음.
     * 
     * TRANSFORMER          - 변성기       - TRAN
     * SCHEDULE             - 정기        - SCHE
     * READING              - 검침        - READ
     * MANUAL_OPERATION     - 수동        - MAN_OP
     * DAY   TIME           - 날짜&시간 - DT
     * LAST MONTH           - 전월        - LT_MN
     *                      - 전전월       - LLT_MN
     * CUMULATIVE           - 누적        - CUMT
     * WATTAGE              - 전력량       - WATT
     * ACTIVE               - 유효        - ACT
     * REACTIVE             - 무효        - REACT
     * LOAD                 - 부하        - LD
     * MIDDLE               - 중간        - MID

     * ***********************************************
     */
    private static final int OFS_PROGRAM_NAME                   = 0;  // Meter Program Name
    private static final int OFS_METER_TIME                     = 8;  // Meter Time(Hex Data(YY+M+D+H+M+S+S))
    private static final int OFS_METER_STATUS                   = 16; // 전력량계 상태정보
    private static final int OFS_METER_CT_PT                    = 20; // 전력량계 변성기 배수
    private static final int OFS_METER_ENERGY_FORMAT            = 24; // 전력량계 ENERGY_FORMAT
    private static final int OFS_METER_DEMAND_FORMAT            = 25; // 전력량계 DEMAND_FORMAT
    private static final int OFS_METER_BILLING_DAY              = 26; // 전력량계 정기 검침일
    private static final int OFS_METER_CNT_MANU_RECOVERY        = 32; // 전력량계 수동복귀 횟수
    private static final int OFS_SELF_READ_DATE_TIME_1          = 33; // 전력량계 SELF_READING_DATE&TIME_1
    
    private static final int OFS_LT_MN_CMLT_WATT_ACT            = 93; // 전월 누적 전력량-유효          
    private static final int OFS_LT_MN_CMLT_MAX_WATT_ACT        = 97; // 전월 누적 최대부하 전력량 유효
    private static final int OFS_LT_MN_MAX_POWER_ACT            = 101; // 전월 최대부하 최대전력-유효
    private static final int OFS_LT_MN_MAX_POWER_ACT_DATE_TIME  = 103; // 전월 최대부하 최대전력 날짜 시간 정보 - 유효
    private static final int OFS_LT_MN_CUMT_WATT_REACT          = 151;// 전월 누적 전력량-무효              
    private static final int OFS_LT_MN_CMLT_MAX_WATT_REACT      = 155; // 전월 누적 최대부하 전력량 - 무효
    private static final int OFS_LT_MN_MAX_POWER_REACT          = 159; // 전월 누적 최대부하 전력 - 무효
    private static final int OFS_LT_MN_MAX_POWER_REACT_DATE_TIME= 161; // 전월 최대부하 최대전력 날짜 시간정보 - 무효
    
    private static final int LEN_PROGRAM_NAME                   = 8; 
    private static final int LEN_METER_TIME                     = 8; 
    private static final int LEN_METER_STATUS                   = 4;
    private static final int LEN_METER_CT_PT                    = 4; 
    private static final int LEN_METER_ENERGY_FORMAT            = 1; 
    private static final int LEN_METER_DEMAND_FORMAT            = 1; 
    private static final int LEN_METER_BILLING_DAY              = 6; 
    private static final int LEN_METER_CNT_MANU_RECOVERY        = 1;
    private static final int LEN_SELF_READ_DATE_TIME_1          = 12;


    private static final int LEN_ENERGY                         = 4; // 전월 누적 전력량-유효
    private static final int LEN_MAX_POWER                      = 2; // 전월 최대부하 전력량-유효
    private static final int LEN_MAX_POWER_DATETIME             = 8; // 전월 최대부하 최대전력 날짜&시간 정보-유효
    private static final int LEN_CUMM_ENERGY                    = 4; // 전전월 누적 최대부하 전력량-유효
    
    private final int CNT_BLOCK                                 = 4; //total, a,b,c
    private double ke                                           = 0.05;
    DecimalFormat dformat                                       = new DecimalFormat("###############0.000000");
    
    private byte[] rowData;
    private TOU_BLOCK[] tou_block;
    
    /**
     * Constructor
     * @param rowData
     */
    public LSRWRS232_PB(byte[] rowData){
        this.rowData = rowData;
        this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
        try {
            parseBillingData();
        } catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
        }
    }
    
    /**
     * Constructor
     * @param rowData
     */
    public LSRWRS232_PB(byte[] rowData , double ke){
        
        this.rowData = rowData;
        this.ke      = ke;
        this.tou_block = new TOU_BLOCK[this.CNT_BLOCK];
        try {
            parseBillingData();
        } catch (Exception e) {
            log.warn("BillingData Parse Error :",e);
        }
    }
    
    /**
     * get KE 계기정수
     * @return
     */
    public Double getKe(){
        return this.ke;
    }
    
    /**
     * set KE 
     * @param ke
     */
    public void setKe(double ke){
        this.ke =ke;
    }
    
    /**
     * get Row Data
     * @return
     */
    public byte[] getRowData() {
        return rowData;
    }

    /**
     * set Row Data
     * @param rowData
     */
    public void setRowData(byte[] rowData) {
        this.rowData = rowData;
    }

    /**
     * get TOU_BLOCK
     * @return
     */
    public TOU_BLOCK[] getTou_block() {
        return tou_block;
    }

    /**
     * set TOU_BLOCK
     * @param touBlock
     */
    public void setTou_block(TOU_BLOCK[] touBlock) {
        tou_block = touBlock;
    }
    
    /**
     * get Program Name
     * @return  
     */
    public String getProgramName(){
        
        String ret = new String();
        try{
            ret = new String(DataFormat.select(rowData,OFS_PROGRAM_NAME,LEN_PROGRAM_NAME)).trim();
        }catch(Exception e){
            log.warn("invalid model->"+e.getMessage());
        }
        return ret;
    }
    
    /**
     * get Meter Status
     * @return
     * @throws Exception
     */
    public MeterStatus getMeterStatus() throws Exception {
        return new MeterStatus(
            DataFormat.select(rowData,OFS_METER_STATUS, LEN_METER_STATUS));
    }

    /**
     * get CT_PT 
     * 변성기 배수
     * @return
     * @throws Exception
     */
    public int getMeterCtpt() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                rowData,OFS_METER_CT_PT,LEN_METER_CT_PT)));
    }
    
    /**
     * get Energy Format
     * @return
     * @throws Exception
     */
    public int getMeterEnergyFormat() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                rowData,OFS_METER_ENERGY_FORMAT,LEN_METER_ENERGY_FORMAT)));
    }
    
    /**
     * get Demand Format
     * @return
     * @throws Exception
     */
    public int getMeterDemandFormat() throws Exception {
        return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                rowData,OFS_METER_DEMAND_FORMAT,LEN_METER_DEMAND_FORMAT)));
    }
    
    /**
     * get Meter Time
     * @return
     * @throws Exception
     */
    public String getMeterTime() throws Exception {
        return getYyyymmddhhmmss(
                DataFormat.select(rowData, OFS_METER_TIME, LEN_METER_TIME));
    }
    
    /**
     * get Billing Day
     * 정기 검침일
     * @return
     * @throws Exception
     */
    public String getMeterBillingDay() throws Exception {
        return getYyyymmdd(
                DataFormat.select(rowData, OFS_METER_BILLING_DAY, LEN_METER_BILLING_DAY));
    }
    
    /**
     * get Count Of Manual Recovery
     * 
     * 수동복귀 횟수 
     * @return
     * @throws Exception
     */        
    public int getCountOfManualRecovery() throws Exception {

       return DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(
                rowData,OFS_METER_CNT_MANU_RECOVERY,LEN_METER_CNT_MANU_RECOVERY)));  
    }
    /**
     * get Self Reading Date & Time
     * @return
     * @throws Exception
     */
    public String getLastSelfReadingDate() throws Exception {
        
        String datetime = "";
        SelfReadingDateTime time = new SelfReadingDateTime(DataFormat.select(
                        rowData, OFS_SELF_READ_DATE_TIME_1, LEN_SELF_READ_DATE_TIME_1 *5));
        datetime = time.getSelfReadingDateTime();

        return datetime;
    }
    
    private final int NBR_TIERS = 2;
     
    private void parseBillingData() throws Exception {

        log.debug("=============== BillingData Parse Start =================");
        log.debug("Previous billing Data : " + Hex.decode(rowData));
        
        int ofs_kw      =   OFS_LT_MN_CMLT_WATT_ACT;    // 전월 누적 전력량 - 유효
        int ofs_kvar    =   OFS_LT_MN_CUMT_WATT_REACT;  // 전월 누적 전력량 - 무효
        
        for(int i = 0; i < this.CNT_BLOCK; i++){            
            
            tou_block[i] = new TOU_BLOCK(NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS, 
                                         NBR_TIERS);
        }
        
        for(int i = 0; i < this.CNT_BLOCK; i++){

            Double a = new Double(dformat.format((double)(DataFormat.hex2signeddec(
                    DataFormat.select(rowData,ofs_kw,LEN_ENERGY)))*0.001*ke));
           
            Double a1 = new Double(dformat.format((double)(DataFormat.hex2signeddec(
                    DataFormat.select(rowData,ofs_kvar,LEN_ENERGY)))*0.001*ke));
            
            ofs_kw      += LEN_ENERGY;
            ofs_kvar    += LEN_ENERGY;
            
            if(i==0){ // total
                log.debug("############  Total Time Zone ############");

                log.debug(" Reset Time (LastSelfReadingDate) :" + getLastSelfReadingDate());
                log.debug(" Reset Count(RecoveryCount) : " + getCountOfManualRecovery());
                tou_block[0].setResetTime(getLastSelfReadingDate());
                tou_block[0].setResetCount(getCountOfManualRecovery());
                
                log.debug("#### TOTAL ENERGY(kWh)  [" + a  +"]");  
                log.debug("#### TOTAL ENERGY(kVARh)[" + a1 +"]");
                
                tou_block[0].setSummations(0, a);
                tou_block[0].setSummations(1, a1);
                tou_block[0].setCumDemand(0, new Double(0.0));
                tou_block[0].setCumDemand(1, new Double(0.0));
                tou_block[0].setCurrDemand(0, new Double(0.0));
                tou_block[0].setCurrDemand(1, new Double(0.0));
                tou_block[0].setCoincident(0, new Double(0.0));
                tou_block[0].setCoincident(1, new Double(0.0));
                tou_block[0].setEventTime(0, new String(""));
                tou_block[0].setEventTime(1, new String(""));
                
            }else{
                int k=i;
                // A,B,C 시간대
                if(k == 1){
                    log.debug("############  A Time Area ############");
                }else if(k == 2 ){
                    log.debug("############  B Time Area ############");
                }else if(k == 3) {
                    log.debug("############  C Time ############");
                }
                
                log.debug("#### CUMM_POWER (KW)  [" + a  +"]");  
                log.debug("#### CUMM_POWER (KVAR)[" + a1 +"]");
                
                // 전월 누적 에너지
                tou_block[k].setSummations(0, a);
                tou_block[k].setSummations(1, a1);
     
                // 전월 최대 수요전력
                Double c = new Double(((double)DataFormat.hex2signeddec(
                                DataFormat.select(rowData,ofs_kw,LEN_MAX_POWER)))*0.001*ke);
                
                Double c1 = new Double(((double)DataFormat.hex2signeddec(
                        DataFormat.select(rowData,ofs_kvar,LEN_MAX_POWER)))*0.001*ke);
                
                log.debug("#### MAX_POWER(KW)  [" + c  +"]" );  
                log.debug("#### MAX_POWER(KVAR)[" + c1 +"]" );
                
                tou_block[k].setCurrDemand(0,c);
                tou_block[k].setCurrDemand(1,c1);
                
                ofs_kw      += LEN_MAX_POWER;                               
                ofs_kvar    += LEN_MAX_POWER;
                         
                log.debug("#### EVENT TIME(KW) ["+ new String(getYymmddhhmmss(DataFormat.select(rowData, ofs_kw,    LEN_MAX_POWER_DATETIME)))+"]"); 
                log.debug("#### EVENT TIME(KVAR) ["+ new String(getYymmddhhmmss(DataFormat.select(rowData, ofs_kvar,    LEN_MAX_POWER_DATETIME)))+"]" );
                
                String e    = new String(getYymmddhhmmss(DataFormat.select(rowData, ofs_kw, LEN_MAX_POWER_DATETIME)));
                String e1   = new String(getYymmddhhmmss(DataFormat.select(rowData, ofs_kvar, LEN_MAX_POWER_DATETIME)));
                // 전월 최대부하 최대전력 날짜&시간 정보
                tou_block[k].setEventTime(0, e);
                tou_block[k].setEventTime(1, e1);
                
                ofs_kw += LEN_MAX_POWER_DATETIME;
                ofs_kvar += LEN_MAX_POWER_DATETIME;
                
                /**
                 *  MAX 최대 유효 부하 산출  -  A,B,C 중  최대값 
                 *  처음 tou_block[0]의 값은 i==0 일때 저장한 0
                 *  이 후 Loop가 돌면서 최대값을 tou_block[0]에 저장
                 */
                double maxCurrDemand_kw = (Double)tou_block[0].getCurrDemand(0);
                double maxCurrDemand_kvar = (Double)tou_block[0].getCurrDemand(1);
     
                if(maxCurrDemand_kw < c){   
                    log.debug("maxCurrDemand_kw < c ==> ["+i+"]");
                    log.debug("maxCurrDemand_kw   [" + maxCurrDemand_kw+"]");
                    log.debug("currDenamd kw ["+c+"]["+e+"]");
                    tou_block[0].setCurrDemand(0,c);
                    tou_block[0].setEventTime(0, e);
                }
                if(maxCurrDemand_kvar < c1){
                     
                    log.debug("maxCurrDemand_kvar < c1  ==> ["+i+"]");
                    log.debug("maxCurrDemand_kvar [" + maxCurrDemand_kvar+"]");
                    log.debug("currDenamd kvar ["+c1+"]["+e1+"]");
                    tou_block[0].setCurrDemand(1,c1);
                    tou_block[0].setEventTime(1,e1);
                }
                
                c = new Double(((double)DataFormat.hex2signeddec(
                        DataFormat.select(rowData,ofs_kw,LEN_CUMM_ENERGY)))*0.001*ke);
        
                c1 = new Double(((double)DataFormat.hex2signeddec(
                        DataFormat.select(rowData,ofs_kvar,LEN_CUMM_ENERGY)))*0.001*ke);
        
                log.debug("CummulativeDemand_kw [" + c + "]");
                log.debug("CummulativeDemand_kvar [" + c1 + "]");
                
                // 전월 누적 수요에너지 
                tou_block[k].setCumDemand(0, c);
                tou_block[k].setCumDemand(1, c1);
                
                // 전전월 누적 최대부하 최대전력
                ofs_kw += LEN_CUMM_ENERGY;
                ofs_kvar += LEN_CUMM_ENERGY;
                
                // Coincident
                tou_block[k].setCoincident(0, new Double(0.0));
                tou_block[k].setCoincident(1, new Double(0.0));     
            }
        } // End of for loop
        log.debug("=================BillingData Parse End=================");
    }
    
    /**
     * get YYMMDDHHMMSS
     * @param b
     * @return
     * @throws Exception
     */ 
    private String getYymmddhhmmss(byte[] b) throws Exception {

        int len = b.length;
        if(len != 8 && len != 6)
            throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
        
        int LEN_YYYY =2;
        if(len==6){
            LEN_YYYY =1;
        }
        int idx = 0;

        int year = DataFormat.hex2dec(DataFormat.select(b, idx, LEN_YYYY));
        if(len==6){
            year+=2000;
        }
        idx +=LEN_YYYY;
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);
        int ss = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();
                
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
        
        log.debug(" eventime :"+ ret.toString());
        return ret.toString();
    }
    
    /**
     * getYyyymmddhhmmss
     *  
     * @param b
     * @return
     * @throws Exception
     */
    private String getYyyymmddhhmmss(byte[] b) throws Exception {

        int len = b.length;
        if(len != 8 )
        throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);

        int LEN_YYYY =2;
        int idx = 0;
        
        int year = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(b, idx, LEN_YYYY)));

        idx +=LEN_YYYY;
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);
        int hh = DataFormat.hex2unsigned8(b[idx++]);
        int MM = DataFormat.hex2unsigned8(b[idx++]);
        int ss = DataFormat.hex2unsigned8(b[idx++]);
        
        StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
        
        log.debug(" meterTime :"+ ret.toString());
        return ret.toString();
    }
    
    /**
     * get YYYYMMDD
     * @param b
     * @return
     * @throws Exception
     */ 
    private String getYyyymmdd(byte[] b) throws Exception {

        int len = b.length;
        if(len != 6)
            throw new Exception("YYYYMMDD LEN ERROR : "+len);
   
        int LEN_YYYY = 1;
        
        int idx = 0;
        int year = DataFormat.hex2dec(DataFormat.select(b, idx, LEN_YYYY));
        
        if(len==6){
            year+=2000;
        }
        idx += LEN_YYYY;
        int mm = DataFormat.hex2unsigned8(b[idx++]);
        int dd = DataFormat.hex2unsigned8(b[idx++]);

        StringBuffer ret = new StringBuffer();
                
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
        
        log.debug("billingDay :"+ ret.toString());
        return ret.toString();
    }
	
}


