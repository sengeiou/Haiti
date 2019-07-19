package com.aimir.fep.meter.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.data.PowerAlarmLogData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * parsing Kamstrup 351B meter data
 *
 * @author C.H 
 * @version $Rev: 1 $, $Date: 2011-04-05 11:43:15 +0900 $,
 */
public class Kamstrup351B extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = 1974450760100885924L;

	private static Log log = LogFactory.getLog(Kamstrup351B.class);

    private byte[] 	rawData       = null;
    private Double 	meteringValue = null;
    private int 	flag          = 0;
    private String 	meterId       = null;
    private int 	period		  = 0;
    private int 	errorCode     = 0;
	int 			position      = 0; 
	int 			interval      = 0;
	String 			meterDate     = "";
	String 			lastReadDate     = "";
	double basePulse1 = 0.0;
	double basePulse2 = 0.0;
	double basePulse3 = 0.0;
	double basePulse4 = 0.0;

    private Kamstrup 			kamstrupMeta	 = null;
//    private EventLogData[] 		eventlogdataList = null;
    private PowerAlarmLogData[] powerAlarmLogDataList = null;
//  public  BillingData 		kmpbillData      = new BillingData();
    public	BillingData 		presentBillData = null;
    public	BillingData 		lastMonthBillData = null;
    public  LPData[] 			lplist           = null;
    public  EventLogData[] 		eventLogDataList = null;

    public Kamstrup351B() {
    }

    /**
     * constructor
     */
    public Kamstrup351B(String meterId)
    {
        this.meterId = meterId;
    }

    /**
     * getRawData
     */
    public byte[] getRawData()
    {
        return rawData;
    }

    /**
     * get data length
     * @return length
     */
    public int getLength()
    {
        if(rawData == null)
            return 0;

        return rawData.length;
    }

    public Kamstrup getKamstrupMeta()
    {
        return kamstrupMeta;
    }

    public void setKamstrupMeta(Kamstrup kamstrupMeta)
    {
        this.kamstrupMeta = kamstrupMeta;
    }

    /**
     * parse meter mesurement data
     * @param data
     */
    public void parse(byte[] datas) throws Exception
    {
    	   
    	log.debug("after Stuffing Res : "+ Hex.decode(datas));
//    	modemInfo(Hex.encode(Hex.decode(getStuffing(datas))));
//    	LPDataparse(data);
//    	meterInfoparse(Hex.encode(Hex.decode(getStuffing(datas))));
//    	LPDataparse(Hex.encode(Hex.decode(getStuffing(datas))));
//    	EventLogparse(Hex.encode(Hex.decode(getStuffing(datas))));
//    	modemInfo(Hex.encode(Hex.decode(getStuffing(datas))));
    	if4Info(Hex.encode(Hex.decode(getStuffing2(datas))));
    }
    
    private byte[]  if4SID 		= new byte[8];
    private byte[]  if4MID	 	= new byte[20];
    private byte[]  if4STYPE 	= new byte[1];
    private byte[]  if4SVC 		= new byte[1];
    private byte[]  if4VENDOR 	= new byte[1];
    private byte[]  if4DATACNT 	= new byte[2];
    private byte[]  if4LENGTH 	= new byte[2];
    private byte[]  if4TimeStamp= new byte[7];
    
    public void if4Info(byte[] data)throws Exception{
    	log.debug("==================if4Info Start!======================");
    	
/*      	System.arraycopy(data, position, if4SID, 0, if4SID.length);
    	position += if4SID.length;
    	log.debug("if4SID ="+Hex.decode(if4SID) );
    	
    	System.arraycopy(data, position, if4MID, 0, if4MID.length);
    	position += if4MID.length;
    	
    	System.arraycopy(data, position, if4STYPE, 0, if4STYPE.length);
    	position += if4STYPE.length;
    	
    	System.arraycopy(data, position, if4SVC, 0, if4SVC.length);
    	position += if4SVC.length;
    	
    	System.arraycopy(data, position, if4VENDOR, 0, if4VENDOR.length);
    	position += if4VENDOR.length;
    	
    	System.arraycopy(data, position, if4DATACNT, 0, if4DATACNT.length);
    	position += if4DATACNT.length;
    	
    	System.arraycopy(data, position, if4LENGTH, 0, if4LENGTH.length);
    	position += if4LENGTH.length;
    	
    	System.arraycopy(data, position, if4TimeStamp, 0, if4TimeStamp.length);
    	position += if4TimeStamp.length;
    	log.debug("if4TimeStamp   ="+Hex.decode(if4TimeStamp) );*/
    	log.debug("==============if4Info End!=========================");
    	modemInfo(data); 	
    }
    
    private byte[]  modemheader = new byte[2];
    private byte[]  modemlength = new byte[2];
    private byte[]  modemfwver  = new byte[1];
    private byte[]  modembuild  = new byte[1];
    private byte[]  modemhwver  = new byte[1];
    private byte[]  modemlogCnt  = new byte[1];
    private byte[]  modemlogData  = new byte[10];
    /*private byte[]  modemmoduleserial = new byte[8];
*/
    private byte[]  modemsimnumber    = new byte[20];
    private byte[]  modemmoduletime   = new byte[7];
    private byte[]  modemmodulcsq     = new byte[2];
    private byte[]  modemstatus       = new byte[1];
    private byte[]  timeYYYY       	  = new byte[2];
    private byte[]  timeMM       	  = new byte[1];
    private byte[]  timeDD            = new byte[1];
    private byte[]  timeHH            = new byte[1];
    private byte[]  timeMI            = new byte[1];
    private byte[]  timeSI            = new byte[1];
    public 	String  moduletime        = "";
    public 	String 	moduleSerial 	  = "";
    public	String 	simNumber		  = "";

    //TODO
	public void modemInfo(byte[] data)throws Exception{
//    	System.out.println("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
		log.debug("===================modemInfo Start!=======================");
    	
    	System.arraycopy(data, position, modemheader, 0, modemheader.length);//modemheader
    	position += modemheader.length;
//    	log.debug("modemheader "+Hex.decode(modemheader) );
//    	System.out.println("modemheader "+Hex.decode(modemheader) );
    	
    	System.arraycopy(data, position, modemlength, 0, modemlength.length);//modemlength
    	position += modemlength.length;
//    	log.debug("modemlength "+Hex.decode(modemlength) );
    	
    	System.arraycopy(data, position, modemfwver, 0, modemfwver.length);//modemfwver
    	position += modemfwver.length;
//    	log.debug("modemfwver "+Hex.decode(modemfwver) );
    	
    	System.arraycopy(data, position, modembuild, 0, modembuild.length);//modembuild
    	position += modembuild.length;
//    	log.debug("modembuild "+Hex.decode(modembuild) );
    	
    	System.arraycopy(data, position, modemhwver, 0, modemhwver.length);//modemhwver
    	position += modemhwver.length;
//    	log.debug("modemhwver "+Hex.decode(modemhwver) );
    	
/*    	System.arraycopy(data, position, modemmoduleserial, 0, modemmoduleserial.length);//modemmoduleserial
    	position += modemmoduleserial.length;
    	moduleSerial = String.valueOf(DataUtil.getIntToBytes(modemmoduleserial));
	    while (moduleSerial.length() % 16 != 0) {
	    	moduleSerial = "0" + moduleSerial;
		}*/
    	
    	System.arraycopy(data, position, modemsimnumber, 0, modemsimnumber.length);//modemsimnumber
    	position += modemsimnumber.length;
    	simNumber = String.valueOf(DataUtil.getIntToBytes(modemsimnumber));
	    while (simNumber.length() % 12 != 0) {
	    	simNumber = "0" + simNumber;
		}
//	    log.debug("modemsimnumber "+Hex.decode(modemsimnumber) );
	    
/*    	System.arraycopy(data, position, modemmoduletime, 0, modemmoduletime.length);//modemmoduletime
    	position += modemmoduletime.length;
    	moduletime = String.valueOf(DataUtil.getIntToBytes(modemmoduletime));
    	*/
	    
	    System.arraycopy(data, position, timeYYYY, 0, timeYYYY.length);//modemmoduletime
    	position += timeYYYY.length;
	    System.arraycopy(data, position, timeMM, 0, timeMM.length);//modemmoduletime
    	position += timeMM.length;    	
	    System.arraycopy(data, position, timeDD, 0, timeDD.length);//modemmoduletime
    	position += timeDD.length;
	    System.arraycopy(data, position, timeHH, 0, timeHH.length);//modemmoduletime
    	position += timeHH.length;
	    System.arraycopy(data, position, timeMI, 0, timeMI.length);//modemmoduletime
    	position += timeMI.length;
	    System.arraycopy(data, position, timeSI, 0, timeSI.length);//modemmoduletime
    	position += timeSI.length;
    	
    	moduletime = ""+DataUtil.getIntToBytes(timeYYYY)+DataUtil.getIntToBytes(timeMM)+DataUtil.getIntToBytes(timeDD)+DataUtil.getIntToBytes(timeHH)+DataUtil.getIntToBytes(timeMI)+DataUtil.getIntToBytes(timeSI) ;
//    	System.out.println("moduletime "+moduletime );
//    	log.debug("modemmoduletime "+moduletime );
	    
    	System.arraycopy(data, position, modemmodulcsq, 0, modemmodulcsq.length);//modemmodulcsq
    	position += modemmodulcsq.length;
//    	log.debug("modemmodulcsq "+Hex.decode(modemmodulcsq) );
    	
    	System.arraycopy(data, position, modemstatus, 0, modemstatus.length);//modemstatus
    	position += modemstatus.length;
//    	log.debug("modemstatus "+Hex.decode(modemstatus) );
    	
    	/**
    	 * 
    	 * 	Modem Log Data 오픈시.. 주석 열어줌.
    	System.arraycopy(data, position, modemlogCnt, 0, modemlogCnt.length);//modemlogCnt
    	position += modemlogCnt.length;
    	int mdmLogCnt = DataUtil.getIntToBytes(modemlogCnt);
    	
    	for(int i=0; i<mdmLogCnt ; i++){
        	System.arraycopy(data, position, modemlogData, 0, modemlogData.length);//modemlogData
        	position += modemlogData.length;
    	}**/
    	
//    	modemInfoTostring();
    	log.debug("==============modemInfo End!===============");
    	meterInfoparse(data); 	
    }

    private byte[] mheader = new byte[2];
    private byte[] mlength = new byte[2];
    private byte[] mcid    = new byte[1];
    private byte[] meterInfo_Rid      = new byte[2];
    private byte[] meterInfo_Rid_Unit = new byte[1];
    private byte[] meterInfo_Rid_Nob  = new byte[1];
    private byte[] meterInfo_Rid_Siex = new byte[1];
    private byte[] meterInfo_EOF 	  = new byte[1];
    private byte[] meterInfo_LastLogId 	  = new byte[2];
    private byte[] meterInfo_NewLogId 	  = new byte[2];
    private byte[] meterInfo_CRC 	  = new byte[2];
    private byte[] meterInfo_Info 	  = new byte[1];
    private int    meterInfo_Rid_Data_Size = 0;

	private double meterInfoConsumedpower         =0;
    private double meterInfoProducedpower         =0;
    private double meterInfoPositivereactivepower =0;
    private double meterInfoNegativereactivepower =0;
    private double meterInfoVoltageL1             =0;
    private double meterInfoVoltageL2             =0;
    private double meterInfoVoltageL3             =0;
    private double meterInfoCurrentL1             =0;
    private double meterInfoCurrentL2             =0;
    private double meterInfoCurrentL3             =0;
    private double meterInfoActualpowerL1         =0;
    private double meterInfoActualpowerL2         =0;
    private double meterInfoActualpowerL3         =0;
    private String meterTime  = null;
    private String meterModel = null;

    /** 
     * MeterInfo 
     * **/
   	//TODO 
    public void meterInfoparse(byte[] data) throws Exception
    {
    	log.debug("==============MeterInfo Rid Start!================");

    	System.arraycopy(data, position, mheader, 0, mheader.length);//mheader
    	position += mheader.length;
//    	log.debug("mheader "+ Hex.decode(mheader));
//    	System.out.println("mheader "+ Hex.decode(mheader));
    	
    	System.arraycopy(data, position, mlength, 0, mlength.length);//mlength
    	position += mlength.length;
//    	log.debug("mlength "+ Hex.decode(mlength));
//    	System.out.println(Hex.decode(mlength));
    	
    	System.arraycopy(data, position, mcid, 0, mcid.length);//Cid
    	position += mcid.length; 
//    	log.debug("mcid "+ Hex.decode(mcid));
//    	System.out.println(Hex.decode(mcid));
    	
    	for(int i=0 ; i<16 ; i++){
    		//RID
    		System.arraycopy(data, position, meterInfo_Rid, 0, meterInfo_Rid.length);//RID
        	position += meterInfo_Rid.length;
//        	log.debug("meterInfo_Rid "+Hex.decode(meterInfo_Rid));
//        	System.out.println(DataUtil.getIntToBytes(meterInfo_Rid));
//        	System.out.println(Hex.decode(meterInfo_Rid));
        	//Format
        	System.arraycopy(data, position, meterInfo_Rid_Unit, 0, meterInfo_Rid_Unit.length);//RID_Unit        	
        	position += meterInfo_Rid_Unit.length;
//        	System.out.println(Hex.decode(meterInfo_Rid_Unit));
//        	String rid_UnitStr = unitDescript(DataUtil.getIntToBytes(meterInfo_Rid_Unit));
        	
        	System.arraycopy(data, position, meterInfo_Rid_Nob, 0, meterInfo_Rid_Nob.length);//RID_Nob   
        	position += meterInfo_Rid_Nob.length;
        	meterInfo_Rid_Data_Size = DataUtil.getIntToBytes(meterInfo_Rid_Nob); //RID_Nob
//        	System.out.println(Hex.decode(meterInfo_Rid_Nob));
        	
        	System.arraycopy(data, position, meterInfo_Rid_Siex, 0, meterInfo_Rid_Siex.length);//RID_Siex   
        	position += meterInfo_Rid_Siex.length;   
        	double siEx = getSignSiex(meterInfo_Rid_Siex);
//        	System.out.println(Hex.decode(meterInfo_Rid_Siex));
        	
        	if(DataUtil.getIntToBytes(meterInfo_Rid) == 1047){
       			byte[] tmprtc = new byte[8];
       			System.arraycopy(data, position, tmprtc, 0, tmprtc.length);
       			int iBcnt = rtc1Bcount(Hex.decode(tmprtc));
       			byte[] rtc = new byte[8];
       			System.arraycopy(data, position, rtc, 0, rtc.length);//RTC [Info][WK][SS][MM][HH][DD][MM][YY]
       			meterTime = printRTC3(rtc);
//       			System.out.println("meterTime "+Hex.decode(rtc));
//       			System.out.println("meterTime "+meterTime);
       			meterInfo_Rid_Data_Size = 8;
//       			System.out.println(Hex.decode(tmprtc));
       		}else{
            	//Value
            	byte[] meterInfo_Data_value = new byte[meterInfo_Rid_Data_Size];
            	System.arraycopy(data, position, meterInfo_Data_value, 0, meterInfo_Data_value.length);//RID_Siex
            	double regVal = 0;
            	String regValstr = "";
            	if( DataUtil.getIntToBytes(meterInfo_Rid) == 1058 || DataUtil.getIntToBytes(meterInfo_Rid) == 1001 ){
            		regValstr = String.valueOf(DataUtil.getIntToBytes(meterInfo_Data_value));
            	}else{
            		regVal=DataUtil.getIntToBytes(meterInfo_Data_value)*siEx;            	
            	} 
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1058) meterModel =regValstr;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1001) meterId = String.valueOf(regValstr);
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1023) meterInfoConsumedpower = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1024) meterInfoProducedpower = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1025) meterInfoPositivereactivepower = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1026) meterInfoNegativereactivepower = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1054) meterInfoVoltageL1 = regVal; 
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1055) meterInfoVoltageL2 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1056) meterInfoVoltageL3 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1076) meterInfoCurrentL1 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1077) meterInfoCurrentL2 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1078) meterInfoCurrentL3 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1080) meterInfoActualpowerL1 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1081) meterInfoActualpowerL2 = regVal;
       			if(DataUtil.getIntToBytes(meterInfo_Rid) == 1082) meterInfoActualpowerL3 = regVal;
       			
//       			System.out.println(Hex.decode(meterInfo_Data_value));
       		}
       		position += meterInfo_Rid_Data_Size;
       		
       		if(i==7 || i==15){
/*       	    	System.arraycopy(data, position, meterInfo_LastLogId, 0, meterInfo_LastLogId.length);//LastLogId   
       	    	position += meterInfo_LastLogId.length; 
       	    	
       	    	System.arraycopy(data, position, meterInfo_NewLogId, 0, meterInfo_NewLogId.length);//NewLogId
       	    	position += meterInfo_NewLogId.length; */
/*       	    	
       	    	System.arraycopy(data, position, meterInfo_Info, 0, meterInfo_Info.length);//Info   
       	    	position += meterInfo_Info.length; 
       	    	*/
       			
       	    	System.arraycopy(data, position, meterInfo_CRC, 0, meterInfo_CRC.length);//CRC   
       	    	position += meterInfo_CRC.length; 
//       	    	System.out.println(Hex.decode(meterInfo_CRC));
       		}
    	}

			position += 4; //PT Transformer, Transformer CT
    	
    	System.arraycopy(data, position, meterInfo_EOF, 0, meterInfo_EOF.length);//RID_Siex   
    	position += meterInfo_EOF.length; 
    	log.debug("meterInfo EOF: "+Hex.decode(meterInfo_EOF));
//    	System.out.println("meterInfo EOF "+Hex.decode(meterInfo_EOF));
    	
//    	meterInfoTostring();
    	log.debug("=====================MeterInfo Rid End!===================");  	
    	BillingDataparse(data);
//    	LPDataparse(data);
    }

    private byte[] lheader				= new byte[2];
    private byte[] llength 				= new byte[2];
    private byte[] billingData_SOF 		= new byte[1];
    private byte[] billingData_ADDR 	= new byte[1];
    private byte[] billingData_CID 		= new byte[1];
    private byte[] billingData_LogType 	= new byte[1];
    private byte[] billingData_ofREG 	= new byte[1];
    private byte[] billingData_LogId 	= new byte[2];
    private byte[] billingData_Rid 	 	= new byte[2];
    private byte[] billingData_Rid_Unit = new byte[1];
    private byte[] billingData_Rid_Nob	= new byte[1];
    private byte[] billingData_Rid_Siex = new byte[1];
    private int    billingData_Rid_Data_Size 	= 0;
    private byte[] billingData_LatestReadLogID 	= new byte[2];
    private byte[] billingData_NewLogID = new byte[2];
    private byte[] billingData_Info 	= new byte[1];
    private byte[] billingData_CRC 		= new byte[2];
    private byte[] billingData_EOF 		= new byte[1];
    
/*    private double bdHoureCounter              = 0;
    private double bdActiveenergyA14           = 0;
    private double bdActiveenergyA23           = 0;
    private double bdReactiveenergyR12         = 0;
    private double bdReactiveenergyR34         = 0;
    private double bdActiveenergyA14Tariff1    = 0;
    private double bdActiveenergyA14Tariff2    = 0;
    private double bdActiveenergyA14Tariff3    = 0;
    private double bdActiveenergyA14Tariff4    = 0;
    private double bdReactiveenergyR12Tariff1  = 0;
    private double bdReactiveenergyR12Tariff2  = 0;
    private double bdReactiveenergyR12Tariff3  = 0;
    private double bdReactiveenergyR12Tariff4  = 0;
    private double bdPeakpowerP14              = 0;
    private double bdAccumulatedpeakpowerP14   = 0;
    private double bdQ12Peak                   = 0;
    private double bdAccumulatedpeakpowerQ12   = 0;
    private double bdDebitingstopcounter       = 0;
    private double bdPulseinput                = 0;
    private double bdTransfomerratio           = 0;
    private double bdPeakpowerP14Tariff1       = 0;
    private double bdPeakpowerP14Tariff2       = 0;
    private double bdPowerlimitcounter         = 0;
    private double bdPeakpowerQ12Tariff1       = 0;
    private double bdPeakpowerQ12Tariff2       = 0;*/
    
    /* BillingData */
	//TODO BillingData
    public void BillingDataparse(byte[] data) throws Exception
    {
    	log.debug("=================Billing Data Start!=================");
    	
    	System.arraycopy(data, position, lheader, 0, lheader.length);//lheader
    	position += lheader.length;
//    	log.debug("billheader "+ Hex.decode(lheader));
//    	System.out.println("billheader "+ Hex.decode(lheader));

    	System.arraycopy(data, position, llength, 0, llength.length);//llength
    	position += llength.length;
//    	System.out.println("llength "+ Hex.decode(llength));
    	
    	System.arraycopy(data, position, billingData_SOF, 0, billingData_SOF.length);//SOF
    	position += billingData_SOF.length;
//    	System.out.println("billingData_SOF "+ Hex.decode(billingData_SOF));
    	
    	System.arraycopy(data, position, billingData_ADDR, 0, billingData_ADDR.length);//ADDR
    	position += billingData_ADDR.length;
//    	System.out.println("billingData_ADDR "+ Hex.decode(billingData_ADDR));
    	
    	System.arraycopy(data, position, billingData_CID, 0, billingData_CID.length);//CID
    	position += billingData_CID.length;
//    	System.out.println("billingData_CID "+ Hex.decode(billingData_CID));
    	
    	System.arraycopy(data, position, billingData_LogType, 0, billingData_LogType.length);//LogType
    	position += billingData_LogType.length;
//    	System.out.println("billingData_LogType "+ Hex.decode(billingData_LogType));
    	
    	System.arraycopy(data, position, billingData_ofREG, 0, billingData_ofREG.length);//ofREG
    	int ofReg = DataUtil.getIntToBytes(billingData_ofREG);
    	
        boolean 	firstCheck 		= false;
        boolean 	presentCheck	= true;
        BillingData bBillData	= null;
        BillingData aBillData = null;
        if(ofReg == 0){
        	position += 1;
        	System.arraycopy(data, position, billingData_Info, 0, billingData_Info.length);//Info       	
        	position += billingData_Info.length;
//        	System.out.println(Hex.decode(billingData_Info));
        	
        	System.arraycopy(data, position, billingData_CRC, 0, billingData_CRC.length);//CRC       	
        	position += billingData_CRC.length;
//        	System.out.println(Hex.decode(billingData_CRC));
        }else{
            aBillData     	= new BillingData();
            bBillData  		= new BillingData();
    		for(int i=0 ; i< 6 ; i++){
            	int logId = 0;
            	
            	if(firstCheck || i==0){
                	System.arraycopy(data, position, billingData_ofREG, 0, billingData_ofREG.length);//ofREG
                	position += billingData_ofREG.length;
//                	System.out.println("billingData_ofREG "+ Hex.decode(billingData_ofREG));
                	
                	if(DataUtil.getIntToBytes(billingData_ofREG)==0){	            	
                		firstCheck	= true;
                		continue;
                	}
            	}
            	String[][] format = null;
            	for(int b=0; b<2 ;b++){
                	
            		System.arraycopy(data, position, billingData_LogId, 0, billingData_LogId.length);//LogId
                	position += billingData_LogId.length;
                	logId	  =	DataUtil.getIntToBytes(billingData_LogId);
//                	System.out.println("billingData_LogId "+ Hex.decode(billingData_LogId));
                	
                	for(int j=0 ; j<DataUtil.getIntToBytes(billingData_ofREG);j++){
                		//value
                		if(b==0 || DataUtil.getIntToBytes(billingData_ofREG)==0){
                			if(b==0 && j==0){
                			format = new String[DataUtil.getIntToBytes(billingData_ofREG)][3];
                			}
                			//RID
                    		System.arraycopy(data, position, billingData_Rid, 0, billingData_Rid.length);//RID
                        	position += billingData_Rid.length;
                        	format[j][0] = DataUtil.getIntToBytes(billingData_Rid)+"";
                        	
                        	//Format
                        	System.arraycopy(data, position, billingData_Rid_Unit, 0, billingData_Rid_Unit.length);//RID_Unit        	
                        	position += billingData_Rid_Unit.length;
                        	
                        	System.arraycopy(data, position, billingData_Rid_Nob, 0, billingData_Rid_Nob.length);//RID_Nob   
                        	position += billingData_Rid_Nob.length;
                        	billingData_Rid_Data_Size = DataUtil.getIntToBytes(billingData_Rid_Nob);//Integer.parseInt(Hex.decode(billingData_Rid_Nob)); //RID_Nob
                        	format[j][1] = billingData_Rid_Data_Size+"";
                        	
                        	System.arraycopy(data, position, billingData_Rid_Siex, 0, billingData_Rid_Siex.length);//RID_Siex   
                        	position += billingData_Rid_Siex.length;
                        	format[j][2] = getSignSiex(billingData_Rid_Siex)+"";
                        	
//                        	System.out.println("billingData_Rid "+ Hex.decode(billingData_Rid)+Hex.decode(billingData_Rid_Unit)+Hex.decode(billingData_Rid_Nob)+Hex.decode(billingData_Rid_Siex));
                		}
//                		System.out.println("Rid="+format[j][0]);
                		if(Integer.parseInt(format[j][1]) > 0){
//                			System.out.println("Integer.parseInt(format[j][0])  "+Integer.parseInt(format[j][0]) );
                    		//Value 
                        	if(Integer.parseInt(       format[j][0]) == 1047 || Integer.parseInt(format[j][0])  == 1049 || Integer.parseInt(format[j][0])  == 1127 || Integer.parseInt(format[j][0])  == 1050 || Integer.parseInt(format[j][0])  == 1051 || Integer.parseInt(format[j][0])  == 1131 || Integer.parseInt(format[j][0])  == 1135){//RTC
                       			byte[] rtc = new byte[Integer.parseInt(format[j][1])];
                       			System.arraycopy(data, position, rtc, 0, rtc.length);//RTC [Info][WK][SS][MM][HH][DD][MM][YY]
                    			if(b==0){
                    				presentCheck = true;
                    			}else if(b==1){
                    				presentCheck = false;
                    			}
                        		if(Integer.parseInt(format[j][0]) == 1049) {
                        			if(b==0){
                        				if(!printRTC4(rtc).equals("2000000000")){
                            				aBillData.setActivePowerDemandMaxTimeRateTotal(printRTC4(rtc));  
                        				};
                        			}else if(b==1){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				bBillData.setActivePowerDemandMaxTimeRateTotal(printRTC4(rtc));
                        				}
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0]) == 1050) {
                        			if(b==0){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				aBillData.setActivePowerDemandMaxTimeRate1(printRTC4(rtc));
                        				}
                        			}else if(b==1){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				bBillData.setActivePowerDemandMaxTimeRate1(printRTC4(rtc));
                        				}
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0]) == 1051) {
                        			if(b==0){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				aBillData.setActivePowerDemandMaxTimeRate2(printRTC4(rtc));
                        				}
                        			}else if(b==1){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				bBillData.setActivePowerDemandMaxTimeRate2(printRTC4(rtc));
                        				}
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0]) == 1131) {
                        			if(b==0){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				aBillData.setReactivePowerDemandMaxTimeRate1(printRTC4(rtc));
                        				}
                        			}else if(b==1){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				bBillData.setReactivePowerDemandMaxTimeRate1(printRTC4(rtc));
                        				}
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0]) == 1135) {
                        			if(b==0){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				aBillData.setReactivePowerDemandMaxTimeRate2(printRTC4(rtc));
                        				}
                        			}else if(b==1){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				bBillData.setReactivePowerDemandMaxTimeRate2(printRTC4(rtc));
                        				}
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0]) == 1127) {
                        			if(b==0){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				aBillData.setReactivePowerDemandMaxTimeRateTotal(printRTC4(rtc));
                        				}
                        			}else if(b==1){
                        				if(!printRTC4(rtc).equals("2000000000")){
                        				bBillData.setReactivePowerDemandMaxTimeRateTotal(printRTC4(rtc));
                        				}
                        			}
                        		}
                        	}else if(Integer.parseInt(format[j][0])  == 1003 || Integer.parseInt(format[j][0])  == 1028 || Integer.parseInt(format[j][0])  == 1129 || Integer.parseInt(format[j][0])  == 1035 || Integer.parseInt(format[j][0])  == 1038  || Integer.parseInt(format[j][0])  == 1133 || Integer.parseInt(format[j][0])  == 1137){//Date
                        		byte[] bdate = new byte[Integer.parseInt(format[j][1])];//YYMMDD
                        		System.arraycopy(data, position, bdate, 0, bdate.length);
                        		printDate(bdate);
                        	}else if(Integer.parseInt(format[j][0])  == 1002 || Integer.parseInt(format[j][0])  == 1027 || Integer.parseInt(format[j][0])  == 1128 || Integer.parseInt(format[j][0])  == 1034 || Integer.parseInt(format[j][0])  == 1037 || Integer.parseInt(format[j][0])  == 1132 || Integer.parseInt(format[j][0])  == 1136){//Time
                        		byte[] btime = new byte[Integer.parseInt(format[j][1])];//YYMMDD
                        		System.arraycopy(data, position, btime, 0, btime.length);
                        		printTime(btime);
                        	}else{
                        		byte[] billingData_Data_value = new byte[Integer.parseInt(format[j][1])];
                            	System.arraycopy(data, position, billingData_Data_value, 0, billingData_Data_value.length);//RID_Siex
                        		double regVal=DataUtil.getIntToBytes(billingData_Data_value)*Double.parseDouble(format[j][2]);
                        		if(DataUtil.getIntToBytes(billingData_Rid) == 1004) {
//                        			bdHoureCounter		     = regVal;
                        		}
//                        		System.out.println("regVal "+regVal);
                        		if(Integer.parseInt(format[j][0]) == 1) {
                        			if(b==0){
                        				aBillData.setActiveEnergyRateTotal(regVal);
//                        				System.out.println(aBillData.getActiveEnergyRateTotal());
                        			}else if(b==1){
                        				bBillData.setActiveEnergyRateTotal(regVal);
//                        				System.out.println(bBillData.getActiveEnergyRateTotal());
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 2)    {
                        			if(b==0){
                        			}else if(b==1){
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 3)    {
                        			if(b==0){
                        				aBillData.setReactiveEnergyRateTotal(regVal);
                        			}else if(b==1){
                        				bBillData.setReactiveEnergyRateTotal(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 4)    {
                        			if(b==0){
                        			}else if(b==1){
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 19)   {
                        			if(b==0){
                        				aBillData.setActiveEnergyRate1(regVal);
                        			}else if(b==1){
                        				bBillData.setActiveEnergyRate1(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 23)   {
                        			if(b==0){
                        				aBillData.setActiveEnergyRate2(regVal);
                        			}else if(b==1){
                        				bBillData.setActiveEnergyRate2(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 27)   {
                        			if(b==0){
                        				aBillData.setActiveEnergyRate3(regVal);
                        			}else if(b==1){
                        				bBillData.setActiveEnergyRate3(regVal);
                        			}

                        		}
                        		if(Integer.parseInt(format[j][0])  == 31)   { 
                        			if(b==0){
                        				aBillData.setActiveEnergyRate4(regVal);
                        			}else if(b==1){
                        				bBillData.setActiveEnergyRate4(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 21)   {
                        			if(b==0){
                        				aBillData.setReactiveEnergyRate1(regVal);
                        			}else if(b==1){
                        				bBillData.setReactiveEnergyRate1(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 25)   {
                        			if(b==0){
                        				aBillData.setReactiveEnergyRate2(regVal);
                        			}else if(b==1){
                        				bBillData.setReactiveEnergyRate2(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 29)   {
                        			if(b==0){
                        				aBillData.setReactiveEnergyRate3(regVal);
                        			}else if(b==1){
                        				bBillData.setReactiveEnergyRate3(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 33)   {
                        			if(b==0){
                        				aBillData.setReactiveEnergyRate4(regVal);
                        			}else if(b==1){
                        				bBillData.setReactiveEnergyRate4(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 39)   { 
                        			if(b==0){
                        				aBillData.setActivePowerMaxDemandRateTotal(regVal);
                        			}else if(b==1){
                        				bBillData.setActivePowerMaxDemandRateTotal(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 43)   {
                        			if(b==0){
                        				aBillData.setCumulativeActivePowerDemandRateTotal(regVal);
                        			}else if(b==1){
                        				bBillData.setCumulativeActivePowerDemandRateTotal(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 41)   {
                        			if(b==0){
                        				aBillData.setReactivePowerMaxDemandRateTotal(regVal);
                        			}else if(b==1){
                        				bBillData.setReactivePowerMaxDemandRateTotal(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 45)   { 
                        			if(b==0){
                        				aBillData.setCumulativeReactivePowerDemandRateTotal(regVal);
                        			}else if(b==1){
                        				bBillData.setCumulativeReactivePowerDemandRateTotal(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 47)   { 
                        		}
                        		if(Integer.parseInt(format[j][0])  == 58)   { 
                        		}
                        		if(Integer.parseInt(format[j][0])  == 48)   { 
                        		}
                        		if(Integer.parseInt(format[j][0])  == 1033) { 
                        			if(b==0){
                        				aBillData.setActivePowerMaxDemandRate1(regVal);
                        			}else if(b==1){
                        				bBillData.setActivePowerMaxDemandRate1(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 1036) { 
                        			if(b==0){
                        				aBillData.setActivePowerMaxDemandRate2(regVal);
                        			}else if(b==1){
                        				bBillData.setActivePowerMaxDemandRate2(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 1040) { 
                        		}
                        		if(Integer.parseInt(format[j][0])  == 1130) { 
                        			if(b==0){
                        				aBillData.setReactivePowerMaxDemandRate1(regVal);
                        			}else if(b==1){
                        				bBillData.setReactivePowerMaxDemandRate1(regVal);
                        			}
                        		}
                        		if(Integer.parseInt(format[j][0])  == 1134) {
                        			if(b==0){
                        				aBillData.setReactivePowerMaxDemandRate2(regVal);
                        			}else if(b==1){
                        				bBillData.setReactivePowerMaxDemandRate2(regVal);
                        			}
                        		}
//                        		System.out.println(Hex.decode(billingData_Data_value));
                    	   }
                       		position += Integer.parseInt(format[j][1]); 
                    	}
                	}
            		
                	byte[] checkLastLogId = new byte[2];
                	System.arraycopy(data, position, checkLastLogId, 0, checkLastLogId.length);
//                	System.out.println("LogId "+logId+" checkId"+DataUtil.getIntToBytes(checkLastLogId));
                	if(logId == DataUtil.getIntToBytes(checkLastLogId)){
                    	System.arraycopy(data, position, billingData_LatestReadLogID, 0, billingData_LatestReadLogID.length);//Latest Log        	
                    	position += billingData_LatestReadLogID.length;
//                    	System.out.println("billingData_LatestReadLogID "+Hex.decode(billingData_LatestReadLogID));
                    	
                    	System.arraycopy(data, position, billingData_NewLogID, 0, billingData_NewLogID.length);//New Log        	
                    	position += billingData_NewLogID.length;
//                    	System.out.println("billingData_NewLogID "+Hex.decode(billingData_NewLogID));
                    	
                    	System.arraycopy(data, position, billingData_Info, 0, billingData_Info.length);//Info       	
                    	position += billingData_Info.length;
//                    	System.out.println("billingData_Info "+Hex.decode(billingData_Info));
//                    	String infoDescription = infoDescript(billingData_Info);
                    	
                    	System.arraycopy(data, position, billingData_CRC, 0, billingData_CRC.length);//CRC       	
                    	position += billingData_CRC.length;
//                    	System.out.println("billingData_CRC "+Hex.decode(billingData_CRC));
                		firstCheck	= true;
                		
                		b = 2;
                		
                		byte[] eofCheck = new byte[1];
                    	System.arraycopy(data, position, eofCheck, 0, eofCheck.length);
                    	if(Hex.decode(eofCheck).equals("0D")){
                    		i=6;
                    	}
                	}
            	}
            }
    		
    		if(presentCheck){
    			presentBillData		= aBillData;
    		}else{
    			lastMonthBillData 	= aBillData; 
    			presentBillData		= bBillData;
    		}
        }
        
    	
    	System.arraycopy(data, position, billingData_EOF, 0, billingData_EOF.length);//EOF       	
    	position += billingData_EOF.length;
    	log.debug("billingData_EOF : "+Hex.decode(billingData_EOF));
//    	System.out.println("billingData_EOF : "+Hex.decode(billingData_EOF));
    	
//    	billingDataTostring();
    	log.debug("===============Billing Data End!================");
    	
    	LPDataparse(data);
    }
    
    private byte[] pheader 		= new byte[2];
    private byte[] plength 		= new byte[2];
    private byte[] lpCount 		= new byte[2];
    private byte[] lpInterval 	= new byte[1];
    private int    lpcnt		= 0;
    private byte[] lpDataSOF 	= new byte[1];
    private byte[] lpDataADDR 	= new byte[1];
    private byte[] lpDataCID 	= new byte[1];
    private byte[] lpDataLogType= new byte[1];//stuffing
    private byte[] lpofREG 		= new byte[1];
    private byte[] lpDataLogID 	= new byte[2];//stuffing
    private byte[] lpDataRegID 	= new byte[2];
    private byte[] lpData_Unit 	= new byte[1];
    private byte[] lpData_Nob 	= new byte[1];
    private byte[] lpData_Siex 	= new byte[1];
//  private int    lpData_size 	= 0;
    private byte[] lpData_LatestReadLogID 	= new byte[2];
    private byte[] lpData_NewLoagID 		= new byte[2];
    private byte[] lpData_Info 				= new byte[1];
    private byte[] lpData_CRC 				= new byte[2];
    private byte[] lpData_EOF 				= new byte[1];

/*  private double lpActiveenergyA14   = 0 ;
	private double lpActiveenergyA23   = 0 ;
    private double lpReactiveenergyR12 = 0 ;
    private double lpReactiveenergyR34 = 0 ;*/
    
    /* LPData 
     * 정전 또는 에러 발생을 하였을 경우 어떻게 데이터를 받는지 알아봐야 함.
     * 시간이 어떻게 넘어 오는지 확인해봐야 함
     * */
	//TODO LPData    
    public void LPDataparse(byte[] data) throws Exception
    {
    	log.debug("================LPData Start!==============");
    	System.arraycopy(data, position, pheader, 0, pheader.length);//header       	
    	position += pheader.length;
//    	log.debug("Lpheader "+ Hex.decode(pheader));
//    	System.out.println("Lpheader "+ Hex.decode(pheader));
    	
    	System.arraycopy(data, position, plength, 0, plength.length);//length,      	
    	position += plength.length;
    	
    	System.arraycopy(data, position, lpCount, 0, lpCount.length);//lpCount       	
    	position += lpCount.length;
//    	System.out.println(Hex.decode(lpCount));
    	lpcnt = DataUtil.getIntToBytes(lpCount);//Integer.parseInt(Hex.decode(lpCount));
    	    	
    	System.arraycopy(data, position, lpInterval, 0, lpInterval.length);//lpInterval       
    	interval = DataUtil.getIntToBytes(lpInterval);//분 30min --> 0x1E
    	position += lpInterval.length;//15분
    	interval = DataUtil.getIntToBytes(lpInterval);
    	
		String[][]	format	= null;
		int			chInt	= 0;
//					lplist	=  new LPData[lpcnt];
		Double		lastCh 	= 0.0;
        boolean 	firstCheck 	= false;
        boolean 	addArrayCk 	= true;
    	ArrayList 	lPDataList 		= new ArrayList();
    	ArrayList 	comparelPDataList 		= new ArrayList();
//		System.out.println("lpCnt "+lpcnt);
//		log.debug("lpCnt "+lpcnt);
		for(int i=0 ; i<lpcnt; i++){
//			System.out.println("i="+i);
        	LPData lpData = new LPData();
        	LPData comparelpData = new LPData();
        	Double[] lpch = new Double[4];
        	Double[] comparelpch = new Double[4];
        	
        	if(firstCheck || i==0){
        		System.arraycopy(data, position, lpDataSOF, 0, lpDataSOF.length);//SOF       	
            	position += lpDataSOF.length;
//            	System.out.println("lpDataSOF="+Hex.decode(lpDataSOF));
            	
            	System.arraycopy(data, position, lpDataADDR, 0, lpDataADDR.length);//ADDR       	
            	position += lpDataADDR.length;
//            	System.out.println("lpDataADDR="+Hex.decode(lpDataADDR));

            	System.arraycopy(data, position, lpDataCID, 0, lpDataCID.length);//CID       	
            	position += lpDataCID.length;
//            	System.out.println("lpDataCID="+Hex.decode(lpDataCID));

            	System.arraycopy(data, position, lpDataLogType, 0, lpDataLogType.length);//Logtype       	
            	position += lpDataLogType.length;
//            	System.out.println("lpDataLogType="+Hex.decode(lpDataLogType));
            	
            	System.arraycopy(data, position, lpofREG, 0, lpofREG.length);//lpofREG   	
            	position += lpofREG.length;    
//            	System.out.println("lpofREG="+Hex.decode(lpofREG));
            	
            	if(DataUtil.getIntToBytes(lpofREG)==0){
	            	System.arraycopy(data, position, lpData_Info, 0, lpData_Info.length);//Info   	
	            	position += lpData_Info.length;
	            	
	            	System.arraycopy(data, position, lpData_CRC, 0, lpData_CRC.length);//CRC   	
	            	position += lpData_CRC.length;
            		firstCheck	= true;
            		addArrayCk	= false;
            		continue;
            	}
    			format = new String[5][4];
        	}
        	
        	int logId = 0;
        	//value
        	for(int j=0 ; j<5; j++){//REG가 N개 온다
        		if(j==0){
                	System.arraycopy(data, position, lpDataLogID, 0, lpDataLogID.length);//LOGID   	
                	position += lpDataLogID.length;
                	logId	= DataUtil.getIntToBytes(lpDataLogID);
//                	System.out.println(Hex.decode(lpDataLogID));
                	addArrayCk = true;
	    		}
        		if(firstCheck || i==0 ){
               		
        			System.arraycopy(data, position, lpDataRegID, 0, lpDataRegID.length);//RegId   	
                	position += lpDataRegID.length;
                	format[j][0] = DataUtil.getIntToBytes(lpDataRegID)+"";
//                	System.out.println("format[j][0] "+format[j][0]);

                	System.arraycopy(data, position, lpData_Unit, 0, lpData_Unit.length);//Unit   	
                	position += lpData_Unit.length;
                	format[j][1] = DataUtil.getIntToBytes(lpData_Unit)+"";
                	
                	System.arraycopy(data, position, lpData_Nob, 0, lpData_Nob.length);//lpData_Nob   	
                	position += lpData_Nob.length;
                	format[j][2] = DataUtil.getIntToBytes(lpData_Nob)+"";
                	
                	System.arraycopy(data, position, lpData_Siex, 0, lpData_Siex.length);//Siex   	
                	position += lpData_Siex.length;
                	format[j][3] = getSignSiex(lpData_Siex)+""; 
                	
                	if(j==4){
                		firstCheck	=	false;
                	}
               	}
        		
        		double siEx = Double.parseDouble(format[j][3]); 
				byte[] lpData_Data_value = new byte[retunIntbyString(format[j][2])];
				System.arraycopy(data, position, lpData_Data_value, 0, lpData_Data_value.length);//RID_Siex
//				System.out.println("Hex.decode(Hex.encode(format[j][0])) "+Hex.decode(Hex.encode(format[j][0])));
				if(format[j][0].equals("1047")){//Reg 1 RTC [Info][WK][SS][MM][HH][DD][MM][YY]
					byte[] rtc = new byte[8];
            		System.arraycopy(data, position, rtc, 0, rtc.length);
            		lpData.setDatetime(printRTC(rtc));
//            		System.out.println(printRTC(rtc));
             	}else{
//                 	System.arraycopy(data, position, lpData_Data_value, 0, lpData_Data_value.length);//RID_Siex
             		double regVal=DataUtil.getIntToBytes(lpData_Data_value)*siEx;  
//             		System.out.println("i="+i+"j="+j);
//             		System.out.println("regVal="+regVal);
             		if(i==0){
             			//처음 오는 갓은 누적값만 기록한다.
             			if(chInt == 0){
             				basePulse1 = regVal;             				
             			}else if(chInt == 1){
             				basePulse2 = regVal;
             			}else if(chInt == 2){
             				basePulse3 = regVal;
             			}else if(chInt == 3){
             				basePulse4 = regVal;
             			}
             			comparelpch[chInt] = regVal;
             			chInt++;
             			if(j==4){
             				chInt = 0;
             			}
             		}else{
//             			System.out.println("lPDataList.size() "+lPDataList.size());
//             			LPData lpd = (LPData)lPDataList.get(lPDataList.size()-1);
             			LPData comparelpd = (LPData)comparelPDataList.get(comparelPDataList.size()-1);
//             			System.out.println("lpd.getCh()[j] "+lpd.getCh()[chInt]);
//             			System.out.println("comparelpd.getCh()[j] "+comparelpd.getCh()[chInt]);
             			lpch[chInt] = regVal - comparelpd.getCh()[chInt];
             			comparelpch[chInt] = regVal;
             			chInt++;
             			if(j==4){
             				chInt = 0;
             			}
             		}
             		
             		if(j==4){
             			addArrayCk = true;
             		}
             		/*else if(i>0 && j==1){
             			lpch[chInt]	= regVal - lastCh;
             			chInt++;
             		}else{
             			lpch[chInt]	= regVal - lpch[chInt-1];
             			chInt++;
             			if(j==4){
             				chInt = 0;
             			}
             		}*/
             	}
//				System.out.println(Hex.decode(Hex.encode(format[j][0])));
//				System.out.println("lpData_Data_value "+Hex.decode(lpData_Data_value));
            	position += lpData_Data_value.length;
            	byte[] checkLastLogId = new byte[2];
            	System.arraycopy(data, position, checkLastLogId, 0, checkLastLogId.length);
//            	System.out.println(logId +"=="+ DataUtil.getIntToBytes(checkLastLogId));
            	if(logId == DataUtil.getIntToBytes(checkLastLogId)){
            		System.arraycopy(data, position, lpData_LatestReadLogID, 0, lpData_LatestReadLogID.length);//LatestReadLog   	
                	position += lpData_LatestReadLogID.length;
//                	System.out.println("lpData_LatestReadLogID=="+ Hex.decode(lpData_LatestReadLogID));
                	System.arraycopy(data, position, lpData_NewLoagID, 0, lpData_NewLoagID.length);//NewLog   	
                	position += lpData_NewLoagID.length;
//                	System.out.println("lpData_NewLoagID=="+ Hex.decode(lpData_NewLoagID));
                	System.arraycopy(data, position, lpData_Info, 0, lpData_Info.length);//Info   	
                	position += lpData_Info.length;
//                	System.out.println("lpData_Info=="+ Hex.decode(lpData_Info));
	            	System.arraycopy(data, position, lpData_CRC, 0, lpData_CRC.length);//CRC   	
	            	position += lpData_CRC.length;
//	            	System.out.println("lpData_CRC=="+ Hex.decode(lpData_CRC));
            		firstCheck	= true;
            	}
        	}
        	if(addArrayCk){
        		comparelpData.setCh(comparelpch);
        		comparelPDataList.add(comparelpData);
        		if(i!=0){
        			lpData.setCh(lpch);      
        			lPDataList.add(lpData);
        		}
        	}
        }
		if(lpcnt == 0){
			System.arraycopy(data, position, lpDataSOF, 0, lpDataSOF.length);//SOF       	
        	position += lpDataSOF.length;
        	
        	System.arraycopy(data, position, lpDataADDR, 0, lpDataADDR.length);//ADDR       	
        	position += lpDataADDR.length;

        	System.arraycopy(data, position, lpDataCID, 0, lpDataCID.length);//CID       	
        	position += lpDataCID.length;

        	System.arraycopy(data, position, lpDataLogType, 0, lpDataLogType.length);//Logtype       	
        	position += lpDataLogType.length;
        	
        	System.arraycopy(data, position, lpofREG, 0, lpofREG.length);//lpofREG   	
        	position += lpofREG.length;
        	
        	System.arraycopy(data, position, lpData_Info, 0, lpData_Info.length);//Info   	
        	position += lpData_Info.length;
        	
        	System.arraycopy(data, position, lpData_CRC, 0, lpData_CRC.length);//CRC   	
        	position += lpData_CRC.length;
		}
		
    	System.arraycopy(data, position, lpData_EOF, 0, lpData_EOF.length);//EOF   	
    	position += lpData_EOF.length;
    	log.debug("lpData_EOF="+Hex.decode(lpData_EOF));
//    	System.out.println("lpData_EOF="+Hex.decode(lpData_EOF));
    	
    	
/*    	LPData l = (LPData) lPDataList.get(lPDataList.size()-1);
    	String lastTime = l.getDatetime();
    	
    	//interval 경우의 수 5,15,30
    	if(lastTime.substring(10,12).equals("00")){
    		lPDataList.remove(lPDataList.size()-1);
    	}else if(lastTime.substring(10,12).equals("15")){
    		lPDataList.remove(lPDataList.size()-1);
    		lPDataList.remove(lPDataList.size()-1);
    	}else if(lastTime.substring(10,12).equals("30")){
    		lPDataList.remove(lPDataList.size()-1);
    		lPDataList.remove(lPDataList.size()-1);
    		lPDataList.remove(lPDataList.size()-1);
    	}  
 		*/    	
    	
    	//Meter lastReadData 세팅
    	LPData lt = (LPData) lPDataList.get(lPDataList.size()-1);
    	lastReadDate =  lt.getDatetime();
    	
    	lplist = (LPData[])lPDataList.toArray(new LPData[lPDataList.size()]);
    	
//    	lpDataTostring();
    	log.debug("==================LPData End!==================");
    	EventLogparse(data);
    }

    private int	   logCnt 			= 0;
    private byte[] eventHeader		= new byte[2];
    private byte[] eventLength		= new byte[2];
    private byte[] eventLogCount	= new byte[2];
    private byte[] eventSOF  		= new byte[1];
    private byte[] eventADDR 		= new byte[1];
    private byte[] eventCID  		= new byte[1];
    private byte[] eventLOGTYPE 	= new byte[1];
    private byte[] eventREGCOUNT 	= new byte[1];
    private byte[] eventLogID 		= new byte[2];
    private byte[] eventREGID 		= new byte[2];
    private byte[] event_Unit 		= new byte[1];
    private byte[] event_Nob  		= new byte[1];
    private byte[] event_Siex 		= new byte[1];
    
    private byte[] event_LatestReadLogId	= new byte[2];
    private byte[] event_NewLogId 			= new byte[2];
    private byte[] event_Info 				= new byte[1];
    private byte[] event_CRC 				= new byte[2];
    private byte[] event_EOF  				= new byte[1];
    
    /* EventLogData */
	//TODO Event
    public void EventLogparse(byte[] data) throws Exception
    {
    	log.debug("================EventLogData Start!=================");
    	
    	System.arraycopy(data, position, eventHeader, 0, eventHeader.length);//eventHeader       	
    	position += eventHeader.length;
//    	log.debug("eventHeder "+ Hex.decode(eventHeader));
//    	System.out.println("eventHeder "+ Hex.decode(eventHeader));
    	
    	System.arraycopy(data, position, eventLength, 0, eventLength.length);//eventLength       	
    	position += eventLength.length;
    	
    	System.arraycopy(data, position, eventLogCount, 0, eventLogCount.length);//LogCount       	
    	logCnt = DataUtil.getIntTo2Byte(eventLogCount);
    	position += eventLogCount.length;
//    	System.out.println(logCnt);
    	
    	/*
    	 * ★★★★★★ 테스트  후 주석★★★★★
    	 * */
//    	logCnt = 26;
    	ArrayList arEventList 		= new ArrayList();
    	ArrayList arpowerAlarmList 	= new ArrayList();// 담을 ArrayList 
    	String 		logType 	= "";
        String[][] 	etFormat 	= null;
        boolean 	firstCheck 	= false;
        boolean 	addArrayCk 	= true;
        
//        System.out.println("logCnt "+logCnt);
//        log.debug("logCnt "+logCnt);
        
        for(int i=0 ; i<logCnt; i++){
            EventLogData eventLogData =  new EventLogData();
        	PowerAlarmLogData powerAlarmLogDatas =  new PowerAlarmLogData();
        	
        	if(firstCheck || i==0){
        		System.arraycopy(data, position, eventSOF, 0, eventSOF.length);//SOF       	
            	position += eventSOF.length;
            	
            	System.arraycopy(data, position, eventADDR, 0, eventADDR.length);//ADDR       	
            	position += eventSOF.length;
            	
            	System.arraycopy(data, position, eventCID, 0, eventCID.length);//CID       	
            	position += eventCID.length;
            	
            	System.arraycopy(data, position, eventLOGTYPE, 0, eventLOGTYPE.length);//LogType       	
            	position += eventLOGTYPE.length;
            	logType = logTypevalue(eventLOGTYPE);
//            	System.out.println("logType "+logType);
            	
            	System.arraycopy(data, position, eventREGCOUNT, 0, eventREGCOUNT.length);//regcnt       	
            	position += eventREGCOUNT.length;           
            	if(DataUtil.getIntToBytes(eventREGCOUNT)==0){
	            	System.arraycopy(data, position, event_Info, 0, event_Info.length);//Info   	
	            	position += event_Info.length;
//	            	log.debug("event_Info "+ Hex.decode(event_Info));
//	            	System.out.println("event_Info "+ Hex.decode(event_Info));
	            	
	            	System.arraycopy(data, position, event_CRC, 0, event_CRC.length);//CRC   	
	            	position += event_CRC.length;
//	            	log.debug("event_CRC "+ Hex.decode(event_CRC));
//	            	System.out.println("event_CRC "+ Hex.decode(event_CRC));
	            	
            		firstCheck	= true;
            		addArrayCk	= false;
            		continue;
            	}
            	etFormat = new String[regCountByLogType(logType)][4];
        	}
        	
        	int logId = 0;
        	//value
//        	System.out.println("regCountByLogType(logType) "+regCountByLogType(logType));
        	for(int j=0 ; j<regCountByLogType(logType); j++){//REG가 N개 온다
        		if(j==0){
                   	System.arraycopy(data, position, eventLogID, 0, eventLogID.length);//LOGID   
                   	logId = DataUtil.getIntToBytes(eventLogID);
                   	position += eventLogID.length;
//                   	log.debug("eventLogID "+Hex.decode(eventLogID));
//                   	System.out.println("eventLogID "+Hex.decode(eventLogID));
	    		}
        		if(firstCheck || i==0 ){
               		System.arraycopy(data, position, eventREGID, 0, eventREGID.length);//RegId   	
                	position += eventREGID.length;
                	etFormat[j][0] = DataUtil.getIntToBytes(eventREGID)+"";

                	System.arraycopy(data, position, event_Unit, 0, event_Unit.length);//Unit   	
                	position += event_Unit.length;
                	
                	
                	System.arraycopy(data, position, event_Nob, 0, event_Nob.length);//lpData_Nob   	
                	position += event_Nob.length;   
                	etFormat[j][2] =  DataUtil.getIntToBytes(event_Nob)+"";
                	
            		System.arraycopy(data, position, event_Siex, 0, event_Siex.length);//Siex   	
                	position += event_Siex.length;
                	etFormat[j][3] =  getSignSiex(event_Siex)+"";
                	if(j==regCountByLogType(logType)-1){
                		firstCheck	=	false;
                	}
//                   	log.debug("reg "+Hex.decode(eventREGID)+Hex.decode(event_Unit)+Hex.decode(event_Nob)+Hex.decode(event_Siex)+"");
//                   	System.out.println("reg "+Hex.decode(eventREGID)+Hex.decode(event_Unit)+Hex.decode(event_Nob)+Hex.decode(event_Siex)+"");
               	}
        		
        		double evSiEx = Double.parseDouble(etFormat[j][3]); 
        		byte[] format_value = new byte[retunIntbyString(etFormat[j][2])];
            	System.arraycopy(data, position, format_value, 0, format_value.length);//RID_Siex
//               	log.debug("etFormat[j][0] "+etFormat[j][0]);
//               	System.out.println("format_value "+Hex.decode(format_value));
//               	System.out.println("etFormat[j][0] "+etFormat[j][0]);

        		if(etFormat[j][0].equals("1047")){//RTC
        			//yyyymmddhhmmss 
            		eventLogData.setDate	  (printRTC3(format_value).substring(0,8));
            		powerAlarmLogDatas.setDate(printRTC3(format_value).substring(0,8));
        			eventLogData.setTime	  (printRTC3(format_value).substring(8,printRTC3(format_value).length()));
        			powerAlarmLogDatas.setTime(printRTC3(format_value).substring(8,printRTC3(format_value).length()));
        			
        		}else if(etFormat[j][0].equals("1002")){//Time 
        			//hhmmss
        			eventLogData.setTime	  (printTime(format_value));
        			powerAlarmLogDatas.setTime(printTime(format_value));
        			powerAlarmLogDatas.setCloseTime(printTime(format_value));
            	}else if(etFormat[j][0].equals("1003")){//Date
            		//yyyymmdd
            		eventLogData.setDate	  (printDate(format_value));
            		powerAlarmLogDatas.setDate(printDate(format_value));
            		powerAlarmLogDatas.setCloseDate(printDate(format_value));
            	}else if(etFormat[j][0].equals("50")){//status
            		int value = DataUtil.getIntToBytes(format_value);
            		eventLogData.setFlag	  (eventStatusEventGetCode(value));
            		eventLogData.setMsg		  (eventStatusEventGetName(value));
            		powerAlarmLogDatas.setFlag(eventStatusEventGetCode(value));
            		powerAlarmLogDatas.setMsg (eventStatusEventGetName(value));
            	}else if(etFormat[j][0].equals("1045")){//RTC status 
            		int value = DataUtil.getIntToByte(format_value[format_value.length - 1]);
            		String binaryString = Integer.toBinaryString(value);
            	    while (binaryString.length() % 8 != 0) {
            	     binaryString = "0" + binaryString;
            	    }
            	    if(binaryString.substring(0, 1).equals("1")){
            	    	eventLogData.setFlag  		(EVENTATTRIBUTE.RTCSTATUS_0.getCode());
            	    	eventLogData.setMsg   		(EVENTATTRIBUTE.RTCSTATUS_0.getName());
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.RTCSTATUS_0.getName());
            	    	powerAlarmLogDatas.setFlag  (EVENTATTRIBUTE.RTCSTATUS_0.getCode());
            	    	powerAlarmLogDatas.setMsg   (EVENTATTRIBUTE.RTCSTATUS_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.RTCSTATUS_0.getName());		            		
            	    }else if(binaryString.substring(2, 3).equals("1")){
            	    	eventLogData.setFlag	  	(EVENTATTRIBUTE.RTCSTATUS_2.getCode());
            	    	eventLogData.setMsg      	(EVENTATTRIBUTE.RTCSTATUS_2.getName());
            	    	powerAlarmLogDatas.setFlag	(EVENTATTRIBUTE.RTCSTATUS_2.getCode());
            	    	powerAlarmLogDatas.setMsg 	(EVENTATTRIBUTE.RTCSTATUS_2.getName());
            	    	
            	    }
            	}else if(etFormat[j][0].equals("1085")){//Voltage event
            		/*정전 : 57   01010 111  4:0 5:1 6:0 7:1
            		    복전 : 2F   00101 111  4:1 5:0 6:1 7:0 */
            		int value = DataUtil.getIntToByte(format_value[format_value.length - 1]);
            		String binaryString = Integer.toBinaryString(value);
            	    while (binaryString.length() % 8 != 0) {
            	     binaryString = "0" + binaryString;
            	    }
            	    
            	    if(binaryString.substring(3, 4).equals("0")&&binaryString.substring(4, 5).equals("1")
            	       &&binaryString.substring(5, 6).equals("0")&&binaryString.substring(6, 7).equals("1")){//Power down
            	    	eventLogData.setFlag      (EVENTATTRIBUTE.POWERDOWN.getCode());
            	    	eventLogData.setMsg       (EVENTATTRIBUTE.POWERDOWN.getName());
            	    	powerAlarmLogDatas.setFlag(EVENTATTRIBUTE.POWERDOWN.getCode());
            	    	powerAlarmLogDatas.setMsg (EVENTATTRIBUTE.POWERDOWN.getName());
            	    }else if(binaryString.substring(3, 4).equals("1")&&binaryString.substring(4, 5).equals("0")
 	            	       &&binaryString.substring(5, 6).equals("1")&&binaryString.substring(6, 7).equals("0")){//Power up
            	    	eventLogData.setFlag      (EVENTATTRIBUTE.POWERUP.getCode());
            	    	eventLogData.setMsg       (EVENTATTRIBUTE.POWERUP.getName());
            	    	powerAlarmLogDatas.setFlag(EVENTATTRIBUTE.POWERUP.getCode());
            	    	powerAlarmLogDatas.setMsg (EVENTATTRIBUTE.POWERUP.getName());
            	    }else if(binaryString.substring(0, 1).equals("1")){
            	    	powerAlarmLogDatas.setLineType(CommonConstants.LineType.A);    	
            	    }else if(binaryString.substring(1, 2).equals("1")){
            	    	powerAlarmLogDatas.setLineType(CommonConstants.LineType.B);	            	    	
            	    }else if(binaryString.substring(2, 3).equals("1")){
            	    	powerAlarmLogDatas.setLineType(CommonConstants.LineType.C);
            	    }
            	}else if(etFormat[j][0].equals("1087")){//Disconnect status
            		int value = DataUtil.getIntToBytes(format_value);
            		eventLogData.setFlag(eventDisconnectStatusGetCode(value));
            		eventLogData.setMsg (eventDisconnectStatusGetName(value));
            	}else if(etFormat[j][0].equals("1088")){//Disconnect feedback
            		int value = DataUtil.getIntToByte(format_value[format_value.length - 1]);
            		String binaryString = Integer.toBinaryString(value);
            	    while (binaryString.length() % 8 != 0) {
            	     binaryString = "0" + binaryString;
            	    }
            	    if(binaryString.substring(6, 7).equals("0")){
            	    	eventLogData.setAppend      (EVENTATTRIBUTE.DISCONNECTFEEDBACK_0_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_0_0.getName());
            	    }else if(binaryString.substring(6, 7).equals("1")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_0_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_0_1.getName());	            	    	
            	    }else if(binaryString.substring(5, 6).equals("0")){
            	    	eventLogData.setAppend      (EVENTATTRIBUTE.DISCONNECTFEEDBACK_1_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_1_0.getName());
            	    }else if(binaryString.substring(5, 6).equals("1")){
            	    	eventLogData.setAppend      (EVENTATTRIBUTE.DISCONNECTFEEDBACK_1_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_1_1.getName());	            	    	
            	    }else if(binaryString.substring(4, 5).equals("0")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_2_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_2_0.getName());
            	    }else if(binaryString.substring(4, 5).equals("1")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_2_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_2_1.getName());	            	    	
            	    }else if(binaryString.substring(3, 4).equals("0")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_3_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_3_0.getName());
            	    }else if(binaryString.substring(3, 4).equals("1")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_3_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_3_1.getName());	            	    	
            	    }else if(binaryString.substring(2, 3).equals("0")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_4_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_4_0.getName());
            	    }else if(binaryString.substring(2, 3).equals("1")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_4_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_4_1.getName());	            	    	
            	    }else if(binaryString.substring(1, 2).equals("0")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_5_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_5_0.getName());
            	    }else if(binaryString.substring(1, 2).equals("1")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_5_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_5_1.getName());	            	    	
            	    }else if(binaryString.substring(0, 1).equals("0")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_6_0.getName());
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_6_0.getName());
            	    }else if(binaryString.substring(0, 1).equals("1")){
            	    	eventLogData.setAppend		(EVENTATTRIBUTE.DISCONNECTFEEDBACK_6_1.getName());	            	    	
            	    	powerAlarmLogDatas.setAppend(EVENTATTRIBUTE.DISCONNECTFEEDBACK_6_1.getName());	            	    	
            	    }
            	}else{
            		double regVal=DataUtil.getIntToBytes(format_value)*evSiEx;
            		eventLogData.setMsg		 (String.valueOf(regVal));
            		powerAlarmLogDatas.setMsg(String.valueOf(regVal));
            	}               
            	position += format_value.length;
            	byte[] checkLastLogId = new byte[2];
            	System.arraycopy(data, position, checkLastLogId, 0, checkLastLogId.length);
//            	System.out.println(logId +"="+ DataUtil.getIntToBytes(checkLastLogId));
//            	System.out.println(j +"=="+ (regCountByLogType(logType)-1));
            	if(j == (regCountByLogType(logType)-1) && logId == DataUtil.getIntToBytes(checkLastLogId)){
//           		if(j == regCountByLogType(logType)-1){
            		System.arraycopy(data, position, event_LatestReadLogId, 0, event_LatestReadLogId.length);//LatestReadLog   	
	            	position += event_LatestReadLogId.length;
//	            	log.debug("event_LatestReadLogId "+ Hex.decode(event_LatestReadLogId));
//	            	System.out.println("event_LatestReadLogId "+ Hex.decode(event_LatestReadLogId));
	            	
	            	System.arraycopy(data, position, event_NewLogId, 0, event_NewLogId.length);//NewLog   	
	            	position += event_NewLogId.length;
//	            	log.debug("event_NewLogId "+ Hex.decode(event_NewLogId));
//	            	System.out.println("event_NewLogId "+ Hex.decode(event_NewLogId));
	            	
	            	System.arraycopy(data, position, event_Info, 0, event_Info.length);//Info   	
	            	position += event_Info.length;
//	            	log.debug("event_Info "+ Hex.decode(event_Info));
//	            	System.out.println("event_Info "+ Hex.decode(event_Info));
	            	
	            	System.arraycopy(data, position, event_CRC, 0, event_CRC.length);//CRC   	
	            	position += event_CRC.length;
//	            	log.debug("event_CRC "+ Hex.decode(event_CRC));
//	            	System.out.println("event_CRC "+ Hex.decode(event_CRC));
            		firstCheck	= true;
            		
            		if(i == (logCnt-1)){
            			System.arraycopy(data, position, eventSOF, 0, eventSOF.length);//SOF       	
                    	position += eventSOF.length;
                    	
                    	System.arraycopy(data, position, eventADDR, 0, eventADDR.length);//ADDR       	
                    	position += eventSOF.length;
                    	
                    	System.arraycopy(data, position, eventCID, 0, eventCID.length);//CID       	
                    	position += eventCID.length;
                    	
                    	System.arraycopy(data, position, eventLOGTYPE, 0, eventLOGTYPE.length);//LogType       	
                    	position += eventLOGTYPE.length;
                    	logType = logTypevalue(eventLOGTYPE);
                    	
                    	System.arraycopy(data, position, eventREGCOUNT, 0, eventREGCOUNT.length);//regcnt       	
                    	position += eventREGCOUNT.length;           

                    	System.arraycopy(data, position, event_Info, 0, event_Info.length);//Info   	
    	            	position += event_Info.length;
//    	            	log.debug("event_Info "+ Hex.decode(event_Info));
//    	            	System.out.println("event_Info "+ Hex.decode(event_Info));
    	            	
    	            	System.arraycopy(data, position, event_CRC, 0, event_CRC.length);//CRC   	
    	            	position += event_CRC.length;
//    	            	log.debug("event_CRC "+ Hex.decode(event_CRC));
//    	            	System.out.println("event_CRC "+ Hex.decode(event_CRC));
            		}
            	}
        	}
        	if(addArrayCk){
        		arEventList.add(eventLogData);
        		arpowerAlarmList.add(powerAlarmLogDatas);
        	}
        }
        
    	System.arraycopy(data, position, event_EOF, 0, event_EOF.length);//EOF 	
    	position += event_EOF.length;
    	log.debug("event_EOF="+Hex.decode(event_EOF));
//    	System.out.println("event_EOF="+Hex.decode(event_EOF));
    	eventLogDataList = (EventLogData[])arEventList.toArray(new EventLogData[arEventList.size()]);
    	powerAlarmLogDataList = (PowerAlarmLogData[])arpowerAlarmList.toArray(new PowerAlarmLogData[arpowerAlarmList.size()]);
//    	eventLogDataTostring();
//    	alarmLogDataTostring();
    	log.debug("==============EventLogData End!=================");
    }
    
    /**
     * get flag
     * @return flag measurement flag
     */
    public int getFlag()
    {
        return this.flag;
    }

    /**
     * set flag
     * @param flag measurement flag
     */
    public void setFlag(int flag)
    {
        this.flag = flag;
    }

    public int getPeriod() {
        return period;
    }

    public MeteringFail getMeteringFail() {

        MeteringFail meteringFail = null;
        if(this.errorCode > 0){
             meteringFail = new MeteringFail();
             meteringFail.setModemErrCode(this.errorCode);
             meteringFail.setModemErrCodeName(NURI_T002.getMODEM_ERROR_NAME(this.errorCode));
             return meteringFail;
        }else{
            return null;
        }
    }

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("Kamstrup351B DATA[");
        sb.append("]\n");

        return sb.toString();
    }
    

    public String getMeterTime() {
		return meterTime;
	}

	public Double getMeteringValue()
    {
        return meteringValue;
    }

    public String getMeterId()
    {
        return meterId;
    }

    public LPData[] getLpData() {
        return lplist;
    }
    
    public EventLogData[] getEventLog(){
        return eventLogDataList;
    }
    
    public PowerAlarmLogData[] getPowerAlarmLogDataList() {
		return powerAlarmLogDataList;
	}

	public String infoDescript(byte[] data){
    	int n = DataUtil.getIntToBytes(data);
    	String str = "";
    	switch(n){
    	case	0: str="Logger is empty"; break;
    	case	2: str="Log ID or time is not in logger range(not existing)"; break;
    	case	3: str="Length of the application layer is exceeded"; break;
    	case	4: str="The end or the beginning of the logger is reached"; break;
    	}
//    	System.out.println("n="+str);
    	return str;
    }
    
    public String unitDescript(int n){
    	String str = "";
    	switch(n){
	    	case  0: str="None"; break;
	    	case  1: str="Wh"; break;
	    	case  2: str="kWh"; break;
	    	case  3: str="MWh"; break;
	    	case  4: str="GWh"; break;
	    	case  13: str="varh"; break;
	    	case  14: str="kvarh"; break;
	    	case  15: str="Mvarh"; break;
	    	case  16: str="Gvarh"; break;
	    	case  17: str="VAh"; break;
	    	case  18: str="kVAh"; break;
	    	case  19: str="MVAh"; break;
	    	case  20: str="GVAh"; break;
	    	case  21: str="W"; break;
	    	case  22: str="kW"; break;
	    	case  23: str="MW"; break;
	    	case  24: str="GW"; break;
	    	case  25: str="var"; break;
	    	case  26: str="kvar"; break;
	    	case  27: str="Mvar"; break;
	    	case  28: str="Gvar"; break;
	    	case  29: str="VA"; break;
	    	case  30: str="kVA"; break;
	    	case  31: str="MVA"; break;
	    	case  32: str="GVA"; break;
	    	case  33: str="V"; break;
	    	case  34: str="A"; break;
	    	case  35: str="kV"; break;
	    	case  36: str="kA"; break;
	    	case  37: str="C"; break;
	    	case  38: str="K"; break;
	    	case  39: str="l"; break;
	    	case  40: str="m3"; break;
	    	case  46: str="h"; break;
	    	case  47: str="clock"; break;
	    	case  48: str="dato1"; break;
	    	case  51: str="number"; break; 
	    	case  53: str="RTC"; break; 
	    	case  54: str="ASCII coded data"; break; 
	    	case  55: str="m3 x 10"; break; 
	    	case  56: str="ton x 10"; break; 
	    	case  57: str="GJ x 10"; break; 
    	}
    	return str;
    }
    
    public String logTypevalue(byte[] data){
    	int n = DataUtil.getIntToBytes(data);
    	String str = "";
    	switch(n){
    	case	2: str="Status"; break;
    	case	3: str="RTC"; break;
    	case	4: str="Voltage Quality"; break;
    	case	5: str="Disconnect"; break;
    	case	10: str="Load Profile"; break;
    	}
//    	System.out.println("n="+str);
    	return str;
    }
    
    public int regCountByLogType(String str){
    	int rtInt = 0;
    	if(str.equals("Status")){
    		rtInt = 4;
    	}else if(str.equals("RTC")){
    		rtInt = 4;
    	}else if(str.equals("Voltage Quality")){
    		rtInt = 4;
    	}else if(str.equals("Disconnect")){
    		rtInt = 4;
    	}else if(str.equals("Load Profile")){
    		rtInt = 2;
    	}
    	return rtInt;
    }
    
    public String printMMDDHH(byte[] data){
    	String rtStr = String.valueOf(DataUtil.getIntToBytes(getStuffing(data)));
    	while (rtStr.length() % 2 != 0) {
    		rtStr = "0" + rtStr;
    	}
        
    	return rtStr;
    }
    
    public String printYYYY(byte[] data){
    	String rtStr = String.valueOf(DataUtil.getIntToBytes(getStuffing(data)));
    	while (rtStr.length() % 2 != 0) {
    		rtStr = "0" + rtStr;
    	}
    	rtStr = "20"+rtStr;
    	return rtStr;
    }
    
    
    public String printRTC(byte[] rtc){
		int rtcPosition = 0;
		byte[] rtcINFO = new byte[1];
		byte[] rtcWK = new byte[1];
		byte[] rtcSS = new byte[1];
		byte[] rtcMI = new byte[1];
		byte[] rtcHH = new byte[1];
		byte[] rtcDD = new byte[1];
		byte[] rtcMM = new byte[1];
		byte[] rtcYY = new byte[1];
		
		System.arraycopy(rtc, rtcPosition, rtcINFO, 0, rtcINFO.length);
		rtcPosition += rtcINFO.length;
		System.arraycopy(rtc, rtcPosition, rtcWK, 0, rtcWK.length);
		rtcPosition += rtcWK.length;
		System.arraycopy(rtc, rtcPosition, rtcSS, 0, rtcSS.length);
		rtcPosition += rtcSS.length;
		System.arraycopy(rtc, rtcPosition, rtcMI, 0, rtcMI.length);
		rtcPosition += rtcMI.length;
		System.arraycopy(rtc, rtcPosition, rtcHH, 0, rtcHH.length);
		rtcPosition += rtcHH.length;
		System.arraycopy(rtc, rtcPosition, rtcDD, 0, rtcDD.length);
		rtcPosition += rtcDD.length;
		System.arraycopy(rtc, rtcPosition, rtcMM, 0, rtcMM.length);
		rtcPosition += rtcMM.length;
		System.arraycopy(rtc, rtcPosition, rtcYY, 0, rtcYY.length);

		int value = DataUtil.getIntToByte(rtcINFO[rtcINFO.length - 1]);
		String binaryString = Integer.toBinaryString(value);
	    while (binaryString.length() % 8 != 0) {
	     binaryString = "0" + binaryString;
	    }
//	    System.out.println("binaryString======"+binaryString);
	    String dayLightSaving = binaryString.substring(6,8);
	    String rtcValid = binaryString.substring(4,6);
	    String timeBackupSource = binaryString.substring(2,4);
	    String t = binaryString.substring(0,2);
	    String yy = String.valueOf(DataUtil.getIntToBytes(rtcYY));
	    String mm = String.valueOf(DataUtil.getIntToBytes(rtcMM));
	    String dd = String.valueOf(DataUtil.getIntToBytes(rtcDD));
	    String hh = String.valueOf(DataUtil.getIntToBytes(rtcHH));
	    String mi = String.valueOf(DataUtil.getIntToBytes(rtcMI));
	    String ss = String.valueOf(DataUtil.getIntToBytes(rtcSS));
	    while (yy.length() % 2 != 0) {
	    	yy = "0" + yy;
		}
	    while (mm.length() % 2 != 0) {
	    	mm = "0" + mm;
		}
	    while (dd.length() % 2 != 0) {
	    	dd = "0" + dd;
		}
	    while (hh.length() % 2 != 0) {
	    	hh = "0" + hh;
		}
	    while (mi.length() % 2 != 0) {
	    	mi = "0" + mi;
		}
	    while (ss.length() % 2 != 0) {
	    	ss = "0" + ss;
		}
	    yy = "20"+yy;
	    return yy+mm+dd+hh+mi;
    }
    
    public String printRTC2(byte[] rtc){
    	String rtcStr = Hex.decode(rtc);
    	
    	int rtcPosition = 0;
		byte[] rtcINFO = null;
		byte[] rtcWK = null;
		byte[] rtcSS = null;
		byte[] rtcMI = null;
		byte[] rtcHH = null;
		byte[] rtcDD = null;
		byte[] rtcMM = null;
		byte[] rtcYY = null;
    	if(rtcStr.contains("1B")){
    		ArrayList ar = new ArrayList();
    		int j=0;
    		for(int i=0 ; i<8 ; i++){
    			if(rtcStr.substring(j,j+2).equals("1B")){
    				ar.add(rtcStr.substring(j,j+4));
    				if(j<rtcStr.length()){
        				j=j+2;    					
    				}
    			}else{
    				ar.add(rtcStr.substring(j,j+2));
    			}
    			j=j+2;
    		}
    		rtcINFO = new byte[String.valueOf(ar.get(0)).length()/2];
    		rtcWK = new byte[String.valueOf(ar.get(1)).length()/2];
    		rtcSS = new byte[String.valueOf(ar.get(2)).length()/2];
    		rtcMI = new byte[String.valueOf(ar.get(3)).length()/2];
    		rtcHH = new byte[String.valueOf(ar.get(4)).length()/2];
    		rtcDD = new byte[String.valueOf(ar.get(5)).length()/2];
    		rtcMM = new byte[String.valueOf(ar.get(6)).length()/2];
    		rtcYY = new byte[String.valueOf(ar.get(7)).length()/2];
    	}else{
    		rtcINFO = new byte[1];
    		rtcWK = new byte[1];
    		rtcSS = new byte[1];
    		rtcMI = new byte[1];
    		rtcHH = new byte[1];
    		rtcDD = new byte[1];
    		rtcMM = new byte[1];
    		rtcYY = new byte[1];
    		
    	}
		
    	System.arraycopy(rtc, rtcPosition, rtcINFO, 0, rtcINFO.length);
		rtcPosition += rtcINFO.length;
		System.arraycopy(rtc, rtcPosition, rtcWK, 0, rtcWK.length);
		rtcPosition += rtcWK.length;
		System.arraycopy(rtc, rtcPosition, rtcSS, 0, rtcSS.length);
		rtcPosition += rtcSS.length;
		System.arraycopy(rtc, rtcPosition, rtcMI, 0, rtcMI.length);
		rtcPosition += rtcMI.length;
		System.arraycopy(rtc, rtcPosition, rtcHH, 0, rtcHH.length);
		rtcPosition += rtcHH.length;
		System.arraycopy(rtc, rtcPosition, rtcDD, 0, rtcDD.length);
		rtcPosition += rtcDD.length;
		System.arraycopy(rtc, rtcPosition, rtcMM, 0, rtcMM.length);
		rtcPosition += rtcMM.length;
		System.arraycopy(rtc, rtcPosition, rtcYY, 0, rtcYY.length);

		int value = DataUtil.getIntToByte(rtcINFO[rtcINFO.length - 1]);
		String binaryString = Integer.toBinaryString(value);
	    while (binaryString.length() % 8 != 0) {
	     binaryString = "0" + binaryString;
	    }
	    String dayLightSaving = binaryString.substring(6,8);
	    String rtcValid = binaryString.substring(4,6);
	    String timeBackupSource = binaryString.substring(2,4);
	    String t = binaryString.substring(0,2);
	    String yy = String.valueOf(DataUtil.getIntToBytes(rtcYY));
	    String mm = String.valueOf(DataUtil.getIntToBytes(rtcMM));
	    String dd = "";
	    String mi = "";
	    if(rtcDD.length == 2){
		    dd = String.valueOf(DataUtil.getIntToBytes(getStuffing(rtcDD)));	    	
	    }else{
		    dd = String.valueOf(DataUtil.getIntToBytes(rtcDD));
	    }
	    String hh = String.valueOf(DataUtil.getIntToBytes(rtcHH));
	    if(rtcMI.length == 2){
		    mi = String.valueOf(DataUtil.getIntToBytes(getStuffing(rtcMI)));	    	
	    }else{
		    mi = String.valueOf(DataUtil.getIntToBytes(rtcMI));
	    }
	    String ss = String.valueOf(DataUtil.getIntToBytes(rtcSS));

	    while (yy.length() % 2 != 0) {
	    	yy = "0" + yy;
		}
	    while (mm.length() % 2 != 0) {
	    	mm = "0" + mm;
		}
	    while (dd.length() % 2 != 0) {
	    	dd = "0" + dd;
		}
	    while (hh.length() % 2 != 0) {
	    	hh = "0" + hh;
		}
	    while (mi.length() % 2 != 0) {
	    	mi = "0" + mi;
		}
	    while (ss.length() % 2 != 0) {
	    	ss = "0" + ss;
		}
	    
//	    System.out.println( "20"+yy+mm+dd+hh+mi+ss );
	    return "20"+yy+mm+dd+hh+mi;
    }
    
    public String printRTC3(byte[] rtc){
		int rtcPosition = 0;
		byte[] rtcINFO = new byte[1];
		byte[] rtcWK = new byte[1];
		byte[] rtcSS = new byte[1];
		byte[] rtcMI = new byte[1];
		byte[] rtcHH = new byte[1];
		byte[] rtcDD = new byte[1];
		byte[] rtcMM = new byte[1];
		byte[] rtcYY = new byte[1];
		
		System.arraycopy(rtc, rtcPosition, rtcINFO, 0, rtcINFO.length);
		rtcPosition += rtcINFO.length;
		System.arraycopy(rtc, rtcPosition, rtcWK, 0, rtcWK.length);
		rtcPosition += rtcWK.length;
		System.arraycopy(rtc, rtcPosition, rtcSS, 0, rtcSS.length);
		rtcPosition += rtcSS.length;
		System.arraycopy(rtc, rtcPosition, rtcMI, 0, rtcMI.length);
		rtcPosition += rtcMI.length;
		System.arraycopy(rtc, rtcPosition, rtcHH, 0, rtcHH.length);
		rtcPosition += rtcHH.length;
		System.arraycopy(rtc, rtcPosition, rtcDD, 0, rtcDD.length);
		rtcPosition += rtcDD.length;
		System.arraycopy(rtc, rtcPosition, rtcMM, 0, rtcMM.length);
		rtcPosition += rtcMM.length;
		System.arraycopy(rtc, rtcPosition, rtcYY, 0, rtcYY.length);

		int value = DataUtil.getIntToByte(rtcINFO[rtcINFO.length - 1]);
		String binaryString = Integer.toBinaryString(value);
	    while (binaryString.length() % 8 != 0) {
	     binaryString = "0" + binaryString;
	    }
//	    System.out.println("binaryString======"+binaryString);
	    String dayLightSaving = binaryString.substring(6,8);
	    String rtcValid = binaryString.substring(4,6);
	    String timeBackupSource = binaryString.substring(2,4);
	    String t = binaryString.substring(0,2);
	    String yy = String.valueOf(DataUtil.getIntToBytes(rtcYY));
	    String mm = String.valueOf(DataUtil.getIntToBytes(rtcMM));
	    String dd = String.valueOf(DataUtil.getIntToBytes(rtcDD));
	    String hh = String.valueOf(DataUtil.getIntToBytes(rtcHH));
	    String mi = String.valueOf(DataUtil.getIntToBytes(rtcMI));
	    String ss = String.valueOf(DataUtil.getIntToBytes(rtcSS));
	    while (yy.length() % 2 != 0) {
	    	yy = "0" + yy;
		}
	    while (mm.length() % 2 != 0) {
	    	mm = "0" + mm;
		}
	    while (dd.length() % 2 != 0) {
	    	dd = "0" + dd;
		}
	    while (hh.length() % 2 != 0) {
	    	hh = "0" + hh;
		}
	    while (mi.length() % 2 != 0) {
	    	mi = "0" + mi;
		}
	    while (ss.length() % 2 != 0) {
	    	ss = "0" + ss;
		}
	    yy = "20"+yy;
	    return yy+mm+dd+hh+mi+ss;
    }
    
    public String printRTC4(byte[] rtc){
		int rtcPosition = 0;
		byte[] rtcINFO = new byte[1];
		byte[] rtcWK = new byte[1];
		byte[] rtcSS = new byte[1];
		byte[] rtcMI = new byte[1];
		byte[] rtcHH = new byte[1];
		byte[] rtcDD = new byte[1];
		byte[] rtcMM = new byte[1];
		byte[] rtcYY = new byte[1];
		
		System.arraycopy(rtc, rtcPosition, rtcINFO, 0, rtcINFO.length);
		rtcPosition += rtcINFO.length;
		System.arraycopy(rtc, rtcPosition, rtcWK, 0, rtcWK.length);
		rtcPosition += rtcWK.length;
		System.arraycopy(rtc, rtcPosition, rtcSS, 0, rtcSS.length);
		rtcPosition += rtcSS.length;
		System.arraycopy(rtc, rtcPosition, rtcMI, 0, rtcMI.length);
		rtcPosition += rtcMI.length;
		System.arraycopy(rtc, rtcPosition, rtcHH, 0, rtcHH.length);
		rtcPosition += rtcHH.length;
		System.arraycopy(rtc, rtcPosition, rtcDD, 0, rtcDD.length);
		rtcPosition += rtcDD.length;
		System.arraycopy(rtc, rtcPosition, rtcMM, 0, rtcMM.length);
		rtcPosition += rtcMM.length;
		System.arraycopy(rtc, rtcPosition, rtcYY, 0, rtcYY.length);

		int value = DataUtil.getIntToByte(rtcINFO[rtcINFO.length - 1]);
		String binaryString = Integer.toBinaryString(value);
	    while (binaryString.length() % 8 != 0) {
	     binaryString = "0" + binaryString;
	    }
//	    System.out.println("binaryString======"+binaryString);
	    String dayLightSaving = binaryString.substring(6,8);
	    String rtcValid = binaryString.substring(4,6);
	    String timeBackupSource = binaryString.substring(2,4);
	    String t = binaryString.substring(0,2);
	    String yy = String.valueOf(DataUtil.getIntToBytes(rtcYY));
	    String mm = String.valueOf(DataUtil.getIntToBytes(rtcMM));
	    String dd = String.valueOf(DataUtil.getIntToBytes(rtcDD));
	    String hh = String.valueOf(DataUtil.getIntToBytes(rtcHH));
	    String mi = String.valueOf(DataUtil.getIntToBytes(rtcMI));
	    String ss = String.valueOf(DataUtil.getIntToBytes(rtcSS));
	    while (yy.length() % 2 != 0) {
	    	yy = "0" + yy;
		}
	    while (mm.length() % 2 != 0) {
	    	mm = "0" + mm;
		}
	    while (dd.length() % 2 != 0) {
	    	dd = "0" + dd;
		}
	    while (hh.length() % 2 != 0) {
	    	hh = "0" + hh;
		}
	    while (mi.length() % 2 != 0) {
	    	mi = "0" + mi;
		}
	    while (ss.length() % 2 != 0) {
	    	ss = "0" + ss;
		}
	    yy = "20"+yy;
	    return yy+mm+dd+hh;
    }
    
    
    public Double getSignSiex(byte[] data ){
        byte byteSiEx=data[0];
/*        int value = DataUtil.getIntToByte(byteSiEx);
		String binaryString = Integer.toBinaryString(value);
		System.out.println("binaryString="+binaryString);*/
        int signInt=(byteSiEx & 128)/128;
        int signExp=(byteSiEx & 64)/64;
        int exp=((byteSiEx&32) + (byteSiEx&16) + (byteSiEx&8) + (byteSiEx&4) + (byteSiEx&2) + (byteSiEx&1));
        double siEx=Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent 
        return siEx;
    }
    
    public String getBCD(byte[] data, int size){
    	int value = DataUtil.getIntToByte(data[data.length - 1]);
		String binaryString = Integer.toBinaryString(value);
	    while (binaryString.length() % size != 0) {
	     binaryString = "0" + binaryString;
	    }
	    return binaryString;
    }
    
    public String printDate(byte[] data){//yyyymmdd
			String binaryString = String.valueOf(DataUtil.getIntToBytes(data));
   		    while (binaryString.length() % 6 != 0) {
   		     binaryString = "0" + binaryString;
   		    }
   		 return "20"+binaryString;
    }

    public String printTime(byte[] btime){//hhmmss
			String binaryString = String.valueOf(DataUtil.getIntToBytes(btime));
   		    while (binaryString.length() % 6 != 0) {
   		     binaryString = "0" + binaryString;
   		    }
   		    return binaryString;
    }
    
    public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	
	public String getMeterDate() {
		return meterDate;
	}

	public void setMeterDate(String meterDate) {
		this.meterDate = meterDate;
	}
	
    public String getModuletime() {
		return moduletime;
	}

	public Instrument[] getInstrument(){
        Instrument[] instruments = new Instrument[1];
        Instrument inst = new Instrument();
        inst.setVOL_A(meterInfoVoltageL1);
        inst.setVOL_B(meterInfoVoltageL2);
        inst.setVOL_C(meterInfoVoltageL3);
        inst.setCURR_A(meterInfoCurrentL1);
        inst.setCURR_B(meterInfoCurrentL2);
        inst.setCURR_C(meterInfoCurrentL3);
        inst.setKW_A(meterInfoActualpowerL1);
        inst.setKW_B(meterInfoActualpowerL2);
        inst.setKW_C(meterInfoActualpowerL3);
        instruments[0] = inst;
        return instruments;
    }

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        return res;
    }

    public BillingData getBillingData(){
    	return presentBillData;
    }
    
    public BillingData getLasMonthBillingData(){
    	return lastMonthBillData;
    }
    
    public String getlastReadDate(){
    	return lastReadDate;
    }
    
    public static byte[] getStuffing(byte[] res){
        String decodeStr=Hex.decode(res);
        if(decodeStr.contains("1B")){//수정필요 2바이트씩 순차적으로 읽어야 함.
        	
            decodeStr=decodeStr.replaceAll("1B7F", "80");
            decodeStr=decodeStr.replaceAll("1BBF", "40");
            decodeStr=decodeStr.replaceAll("1BF2", "0D");
            decodeStr=decodeStr.replaceAll("1BF9", "06");
            decodeStr=decodeStr.replaceAll("1BE4", "1B");
        }
        return Hex.encode(decodeStr);        
    }
    
    public static byte[] getStuffing2(byte[] res){
        String decodeStr=Hex.decode(res).trim();
        int allLen = decodeStr.length();
        
        String a ="";
        String b ="";
        String c ="";
        
//        System.out.println(allLen);

        for(int i=0 ; i<allLen-4 ; i++){
//        	System.out.println("i="+i);

        	if(decodeStr.substring(i,i+2).equals("1B")){
        		
        		if(getStuffingCheck(decodeStr.substring(i,i+4)).equals("true")){
//        			System.out.println(decodeStr.substring(i,i+4));
//        			System.out.println("in "+i);
        			a =	decodeStr.substring(0,i);
//        			System.out.println("a "+a);

        			b = getStuffingStr(decodeStr.substring(i,i+4));
//            		System.out.println("b "+b);
            		
            		c = decodeStr.substring(i+4,decodeStr.length());
//            		System.out.println("c "+c);

            		decodeStr = a.trim()+b.trim()+c.trim();
            		allLen = decodeStr.length();
            		i--;
            		continue;
        		}
        	}

//        	System.out.println(i+"="+allLen+"="+ decodeStr.length());
        	i++;
        }
//        System.out.println(decodeStr.trim());
        return Hex.encode(decodeStr.trim());        
    }
    
    public static String getStuffingStr(String str){
    	String returnStr = "";
    
    	if(str.equals("1B7F")) returnStr = "80";
    	if(str.equals("1BBF")) returnStr = "40";
    	if(str.equals("1BF2")) returnStr = "0D";
    	if(str.equals("1BF9")) returnStr = "06";
    	if(str.equals("1BE4")) returnStr = "1B";
		
    	return returnStr ;
    }
    
    public static String getStuffingCheck(String str){
    	String returnStr = "";
    
    	if(str.equals("1B7F")) returnStr = "true";
    	if(str.equals("1BBF")) returnStr = "true";
    	if(str.equals("1BF2")) returnStr = "true";
    	if(str.equals("1BF9")) returnStr = "true";
    	if(str.equals("1BE4")) returnStr = "true";
		
    	return returnStr ;
    }

    
    /*    public String getMeterStatus(byte[] res){
    	int value = DataUtil.getIntToByte(res[res.length - 1]);
    	String str="";
    	//String binaryString = Integer.toBinaryString(value);
    	
    	switch(value){
    	case  1: str="Meter reset"; break;
    	case  2: str="Error at EEPROM access Error at restore or backup"; break;
    	case  4: str="Detection of magnet"; break;
    	case  8: str="RAM test error"; break;
    	case  16: str="ROM checksum error"; break;
    	case  32: str="Alarm input"; break;
    	case  64: str="Tamper detected"; break;
    	case  128: str="-"; break;
    	}
    	
    	return str;
    }*/
    
    public void modemInfoTostring(){
    	 StringBuffer sb = new StringBuffer();
    	 sb.append("moduleSerial="+moduleSerial +"\n");
    	 sb.append("simNumber   ="   +simNumber    +"\n");
    	 log.debug(sb.toString());
    }
	
	public void meterInfoTostring(){
		 StringBuffer sb = new StringBuffer();
	        
	        try {
	        	sb.append("MeterInfo Data\n");
	        	sb.append("meterModel                    =" + meterModel                           +"\n");
	        	sb.append("meterId                       =" + meterId                              +"\n");
	        	sb.append("meterInfoConsumedpower        =" + meterInfoConsumedpower               +"\n");
	        	sb.append("meterInfoProducedpower        =" + meterInfoProducedpower               +"\n");
	        	sb.append("meterInfoPositivereactivepower=" + meterInfoPositivereactivepower       +"\n"); 
	        	sb.append("meterInfoNegativereactivepower=" + meterInfoNegativereactivepower       +"\n"); 
	        	sb.append("meterInfoVoltageL1            =" + meterInfoVoltageL1                   +"\n");
	        	sb.append("meterInfoVoltageL2            =" + meterInfoVoltageL2                   +"\n");
	        	sb.append("meterInfoVoltageL3            =" + meterInfoVoltageL3                   +"\n");
	        	sb.append("meterInfoCurrentL1            =" + meterInfoCurrentL1                   +"\n");
	        	sb.append("meterInfoCurrentL2            =" + meterInfoCurrentL2                   +"\n");
	        	sb.append("meterInfoCurrentL3            =" + meterInfoCurrentL3                   +"\n");
	        	sb.append("meterInfoActualpowerL1        =" + meterInfoActualpowerL1               +"\n");
	        	sb.append("meterInfoActualpowerL2        =" + meterInfoActualpowerL2               +"\n");
	        	sb.append("meterInfoActualpowerL3        =" + meterInfoActualpowerL3               +"\n");
	        	sb.append("meterTime                     =" + meterTime                            +"\n");
	        }catch (Exception e){
	        }
	        log.debug(sb.toString());
//	        System.out.println(sb.toString());
	}
	
	public void billingDataTostring(){
		
		 StringBuffer sb = new StringBuffer();
		 
	        try {
	        	sb.append("BillingData\n");
	        	sb.append("present	\n");
	        	sb.append("getActivePowerDemandMaxTimeRateTotal()			"+presentBillData.getActivePowerDemandMaxTimeRateTotal()+"	\n");
	        	sb.append("getActivePowerDemandMaxTimeRate1()			"+presentBillData.getActivePowerDemandMaxTimeRate1()+"	\n");
	        	sb.append("getActivePowerDemandMaxTimeRate2()	"+presentBillData.getActivePowerDemandMaxTimeRate2()+"	\n");
	        	sb.append("getReactivePowerDemandMaxTimeRate1()	"+presentBillData.getReactivePowerDemandMaxTimeRate1()+"	\n");
	        	sb.append("getReactivePowerDemandMaxTimeRate2()				"+presentBillData.getReactivePowerDemandMaxTimeRate2()+"	\n");
	        	sb.append("getReactiveEnergyRateTotal()				"+presentBillData.getReactiveEnergyRateTotal()+"	\n");
	        	sb.append("getActiveEnergyRate1()				"+presentBillData.getActiveEnergyRate1()+"	\n");
	        	sb.append("getActiveEnergyExportRate4()				"+presentBillData.getActiveEnergyExportRate4()+"	\n");
	        	sb.append("getActiveEnergyRate2()		"+presentBillData.getActiveEnergyRate2()+"	\n");
	        	sb.append("getReactiveEnergyRate1()		"+presentBillData.getReactiveEnergyRate1()+"	\n");
	        	sb.append("getReactiveEnergyRate3()		"+presentBillData.getReactiveEnergyRate3()+"	\n");
	        	sb.append("LastMonth	\n");
	        	sb.append("getActivePowerDemandMaxTimeRateTotal()			"+lastMonthBillData.getActivePowerDemandMaxTimeRateTotal()+"	\n");
	        	sb.append("getActivePowerDemandMaxTimeRate1()			"+lastMonthBillData.getActivePowerDemandMaxTimeRate1()+"	\n");
	        	sb.append("getActivePowerDemandMaxTimeRate2()	"+lastMonthBillData.getActivePowerDemandMaxTimeRate2()+"	\n");
	        	sb.append("getReactivePowerDemandMaxTimeRate1()	"+lastMonthBillData.getReactivePowerDemandMaxTimeRate1()+"	\n");
	        	sb.append("getReactivePowerDemandMaxTimeRate2()				"+lastMonthBillData.getReactivePowerDemandMaxTimeRate2()+"	\n");
	        	sb.append("getReactiveEnergyRateTotal()				"+lastMonthBillData.getReactiveEnergyRateTotal()+"	\n");
	        	sb.append("getActiveEnergyRate1()				"+lastMonthBillData.getActiveEnergyRate1()+"	\n");
	        	sb.append("getActiveEnergyExportRate4()				"+lastMonthBillData.getActiveEnergyExportRate4()+"	\n");
	        	sb.append("getActiveEnergyRate2()		"+lastMonthBillData.getActiveEnergyRate2()+"	\n");
	        	sb.append("getReactiveEnergyRate1()		"+lastMonthBillData.getReactiveEnergyRate1()+"	\n");
	        	sb.append("getReactiveEnergyRate3()		"+lastMonthBillData.getReactiveEnergyRate3()+"	\n");
	        	

	        }catch (Exception e){
	        	e.printStackTrace();
	        }
	        log.debug(sb.toString());
//	        System.out.println(sb.toString());
	}
	
	
	public void lpDataTostring(){
		LPData[] 	 lplistw = getLpData();
		StringBuffer sb 	 = new StringBuffer();
	    
/*		System.out.println("getBasePulse1() "+getBasePulse1());
		System.out.println("getBasePulse2() "+getBasePulse2());
		System.out.println("getBasePulse3() "+getBasePulse3());
		System.out.println("getBasePulse4() "+getBasePulse4());*/

		
    	for (int i = 0; i < lplistw.length; i++) {
    		Double[] ch = lplistw[i].getCh();
    		sb.append("dataTime="+lplistw[i].getDatetime()+"\n");
    		for(int j=0 ; j<ch.length;j++){
    			sb.append("ch"+j+" "+ch[j]+"\n");
    		}
    		sb.append("lp 		"+lplistw[i].getLp()+"\n");
    		sb.append("lpvalue 	"+lplistw[i].getLpValue()+"\n");    		
    		sb.append("flag		"+lplistw[i].getFlag()+"\n");
    		sb.append("pf		"+lplistw[i].getPF()+"\n");
		}
    	log.debug(sb.toString());
//    	System.out.println(sb.toString());
	}
	
	public void eventLogDataTostring(){
		System.out.println("eventLogDataList:"+eventLogDataList.length);
		for(int i=0 ; i<eventLogDataList.length ; i++){
			EventLogData et = (EventLogData)eventLogDataList[i];
//			log.info("Date="+et.getDate()+"Time"+et.getTime()+"Flag="+et.getFlag()+"Msg"+et.getMsg()+"Append="+et.getAppend()==null?"":et.getAppend());
			log.debug("Date="+et.getDate()+" Time"+et.getTime()+" Flag="+et.getFlag()+" Msg"+et.getMsg());
//			System.out.println("Date="+et.getDate()+" Time"+et.getTime()+" Flag="+et.getFlag()+" Msg"+et.getMsg());
//			System.out.println("Date="+et.getDate()+"Time"+et.getTime()+"Flag="+et.getFlag()+"Msg"+et.getMsg()+"Append="+et.getAppend()==null?"":et.getAppend());
		}
	}
	
	public void alarmLogDataTostring(){
//		System.out.println(powerAlarmLogDataList.length);
		for(int i=0 ; i<powerAlarmLogDataList.length ; i++){
			PowerAlarmLogData et = (PowerAlarmLogData)powerAlarmLogDataList[i];
			log.info("Date="+et.getDate()+"Time"+et.getTime()+"Flag="+et.getFlag()+"Msg"+et.getMsg()+"Append="+et.getAppend()==null?"":et.getAppend()+"LineType="+et.getLineType());
//			System.out.println("Date="+et.getCloseDate()+"Time"+et.getTime()+"Flag="+et.getFlag()+"Msg"+et.getMsg()+"Append="+et.getAppend()==null?"":et.getAppend()+"LineType="+et.getLineType());

		}
	}
	
	public String getLPrtpmake(byte[] data, int pos){
		int stuffInt = 1;
		byte[] rtcTMP = new byte[1];//TMP
		byte[] rtcINFO = new byte[1];//INfo
		System.arraycopy(data, position, rtcINFO, 0, rtcINFO.length);
		position += rtcINFO.length;
		byte[] rtcWK = new byte[1];//WK
		System.arraycopy(data, position, rtcWK, 0, rtcWK.length);
		position += rtcWK.length;           			
		System.arraycopy(data, position, rtcTMP, 0, rtcTMP.length);
		String decodeStr=Hex.decode(rtcTMP);
        if(decodeStr.contains("1B")){
        	stuffInt =2;
        }
		byte[] rtcSS = new byte[stuffInt];//SS
		stuffInt = 1;
		System.arraycopy(data, position, rtcSS, 0, rtcSS.length);
		position += rtcSS.length;
		System.arraycopy(data, position, rtcTMP, 0, rtcTMP.length);
		decodeStr=Hex.decode(rtcTMP);
        if(decodeStr.contains("1B")){
        	stuffInt =2;
        }
		byte[] rtcMI = new byte[stuffInt];//MI
		stuffInt = 1;
		System.arraycopy(data, position, rtcMI, 0, rtcMI.length);
		position += rtcMI.length;
		System.arraycopy(data, position, rtcTMP, 0, rtcTMP.length);
		decodeStr=Hex.decode(rtcTMP);
        if(decodeStr.contains("1B")){
        	stuffInt =2;
        }
		byte[] rtcHH = new byte[stuffInt];//HH
		stuffInt = 1;
		System.arraycopy(data, position, rtcHH, 0, rtcHH.length);
		position += rtcHH.length;
		System.arraycopy(data, position, rtcTMP, 0, rtcTMP.length);
		decodeStr=Hex.decode(rtcTMP);
        if(decodeStr.contains("1B")){
        	stuffInt =2;
        }           			
		byte[] rtcDD = new byte[stuffInt];//DD
		stuffInt = 1;
		System.arraycopy(data, position, rtcDD, 0, rtcDD.length);
		position += rtcDD.length;
		System.arraycopy(data, position, rtcTMP, 0, rtcTMP.length);
		decodeStr=Hex.decode(rtcTMP);
        if(decodeStr.contains("1B")){
        	stuffInt =2;
        }              			
		byte[] rtcMM = new byte[1];//MM
		System.arraycopy(data, position, rtcMM, 0, rtcMM.length);
		position += rtcDD.length;
		byte[] rtcYY = new byte[1];//YY
		System.arraycopy(data, position, rtcYY, 0, rtcYY.length);
		position += rtcDD.length;
			
		String tm = "20"+printYYYY(rtcYY)+printMMDDHH(rtcMM)+printMMDDHH(rtcDD)+printMMDDHH(rtcHH)+printMMDDHH(rtcMI);
		return tm;
	}

	public int eventStatusEventGetCode(int val){
		int rtInt = 0;
		if(val == 1){
			rtInt = EVENTATTRIBUTE.METERSTATUS_01.getCode();
    	}else if(val == 2){
    		rtInt = EVENTATTRIBUTE.METERSTATUS_02.getCode();
    	}else if(val == 4){
    		rtInt = EVENTATTRIBUTE.METERSTATUS_04.getCode();
    	}else if(val == 8){
    		rtInt = EVENTATTRIBUTE.METERSTATUS_08.getCode();
    	}else if(val == 16){
    		rtInt = EVENTATTRIBUTE.METERSTATUS_16.getCode();
    	}else if(val == 32){
    		rtInt = EVENTATTRIBUTE.METERSTATUS_32.getCode();
    	}else if(val == 64){
    		rtInt = EVENTATTRIBUTE.METERSTATUS_68.getCode();
    	}else if(val == 128){
    	}		
		return rtInt ; 
	}
	
	public String eventStatusEventGetName(int val){
		
		String rtStr = "";
		if(val == 1){
			rtStr = EVENTATTRIBUTE.METERSTATUS_01.getName();
    	}else if(val == 2){
    		rtStr = EVENTATTRIBUTE.METERSTATUS_02.getName();
    	}else if(val == 4){
    		rtStr = EVENTATTRIBUTE.METERSTATUS_04.getName();
    	}else if(val == 8){
    		rtStr = EVENTATTRIBUTE.METERSTATUS_08.getName();
    	}else if(val == 16){
    		rtStr = EVENTATTRIBUTE.METERSTATUS_16.getName();
    	}else if(val == 32){
    		rtStr = EVENTATTRIBUTE.METERSTATUS_32.getName();
    	}else if(val == 64){
    		rtStr = EVENTATTRIBUTE.METERSTATUS_68.getName();
    	}else if(val == 128){
    	}	
		return rtStr;
	}
	public int eventDisconnectStatusGetCode(int val){
		int rtInt = 0;
		
		if(val == 1){
			rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_01.getCode();
    	}else if(val == 4){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_04.getCode();
    	}else if(val == 5){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_05.getCode();
    	}else if(val == 6){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_06.getCode();
    	}else if(val == 7){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_07.getCode();
    	}else if(val == 8){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_08.getCode();
    	}else if(val == 9){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_09.getCode();
    	}else if(val == 11){
    		rtInt = EVENTATTRIBUTE.DISCONNECTSTATUS_11.getCode();
    	}	
		return rtInt ; 
	}
	public String eventDisconnectStatusGetName(int val){
		String rtStr = "";
		if(val == 1){
			rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_01.getName();
    	}else if(val == 4){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_04.getName();
    	}else if(val == 5){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_05.getName();
    	}else if(val == 6){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_06.getName();
    	}else if(val == 7){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_07.getName();
    	}else if(val == 8){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_08.getName();
    	}else if(val == 9){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_09.getName();
    	}else if(val == 11){
    		rtStr = EVENTATTRIBUTE.DISCONNECTSTATUS_11.getName();
    	}	
		return rtStr;
	}
	public int eventLogProfileGetCode(int val){
		int rtInt = 0;
		
		if(val == 0){
			rtInt = EVENTATTRIBUTE.LOADPROFILE_0.getCode();
    	}else if(val == 1){
    		rtInt = EVENTATTRIBUTE.LOADPROFILE_1.getCode();
    	}else if(val == 2){
    		rtInt = EVENTATTRIBUTE.LOADPROFILE_2.getCode();
    	}else if(val == 3){
    		rtInt = EVENTATTRIBUTE.LOADPROFILE_3.getCode();
    	}

		return rtInt ; 
	}
	public String eventLogProfileGetName(int val){
		String rtStr = "";
		if(val == 0){
			rtStr = EVENTATTRIBUTE.LOADPROFILE_0.getName();
    	}else if(val == 1){
    		rtStr = EVENTATTRIBUTE.LOADPROFILE_1.getName();
    	}else if(val == 2){
    		rtStr = EVENTATTRIBUTE.LOADPROFILE_2.getName();
    	}else if(val == 3){
    		rtStr = EVENTATTRIBUTE.LOADPROFILE_3.getName();
    	}
		return rtStr;
	}
	public int rtc1Bcount(String str){
		ArrayList ar = new ArrayList();
		int j=0;
		for(int i=0 ; i<str.length()/2 ; i++){
//			System.out.println(str.substring(j,j+2)+"\n");
			if(str.substring(j,j+2).equals("1B")){
				ar.add(str.substring(j,j+2));
			}
			j=j+2;
		}
		return ar.size();
	}
	
	public int retunIntbyString(String v){
		if(v.indexOf(".")>-1){
			v = v.substring(0,v.indexOf("."));
		}

		return Integer.parseInt(v); 
	}
    
	public enum EVENTATTRIBUTE {

		METERSTATUS_01  (1,  "Meter reset"),
		METERSTATUS_02  (2,  "Error at EEPROM access Error at restore or backup"),
		METERSTATUS_04  (3,  "Detection of magnet"),
		METERSTATUS_08  (4,  "RAM test error"),
		METERSTATUS_16  (5,  "ROM checksum error"),
		METERSTATUS_32  (6,  "Alarm input"),
		METERSTATUS_68  (7,  "Tamper detected"),
		RTCSTATUS_0     (9,  "KMP command ‘SetClock’/PutRegister 1047"),
		RTCSTATUS_2     (10,  "No change"),
		VOLTAGEEVENT_0   (11,  "System L1"),
		VOLTAGEEVENT_1   (12,  "System L2"),
		VOLTAGEEVENT_2   (13,  "System L3"),
		VOLTAGEEVENT_3   (14,  "Voltage exceeds cutoff voltage"),
		VOLTAGEEVENT_4   (15,  "Voltage below cutoff voltage"),
		VOLTAGEEVENT_5   (16,  "Voltage within limits of under- and over voltage"),
		VOLTAGEEVENT_6   (17,  "Under voltage"),
		VOLTAGEEVENT_7   (18,  "Over voltage"),
		DISCONNECTSTATUS_01 (19,  "Relays disconnected by command"),
		DISCONNECTSTATUS_04 (20,  "Relays connected"),
		DISCONNECTSTATUS_05 (21,  "Pre cutoff warning"),
		DISCONNECTSTATUS_06 (22,  "Cutoff"),
		DISCONNECTSTATUS_07 (23,  "Cutoff prepayment"),
		DISCONNECTSTATUS_08 (24,  "Relays released for reconnection"),
		DISCONNECTSTATUS_09 (25,  "Cutoff prepayment, LowMax expired"),
		DISCONNECTSTATUS_11 (26,  "Relays disconnected by push button"),
		DISCONNECTFEEDBACK_0_0 (27,  "No voltage of Voltage L1"),
		DISCONNECTFEEDBACK_0_1 (28,  "voltage of Voltage L1"),
		DISCONNECTFEEDBACK_1_0 (29,  "No voltage of Voltage L2"),
		DISCONNECTFEEDBACK_1_1 (30,  "voltage of Voltage L2"),
		DISCONNECTFEEDBACK_2_0 (31,  "No voltage of Voltage L3"),
		DISCONNECTFEEDBACK_2_1 (32,  "voltage of Voltage L3"),
		DISCONNECTFEEDBACK_3_0 (33,  "Validation(Bit 0-2 not valid)"),
		DISCONNECTFEEDBACK_3_1 (34,  "Validation(Bit 0-2 is valid)"),
		DISCONNECTFEEDBACK_4_0 (35,  "Internal relay control status(Error)"),
		DISCONNECTFEEDBACK_4_1 (36,  "Internal relay control status(No Error)"),
		DISCONNECTFEEDBACK_5_0 (37,  "Disconnect(follows Low)"),
		DISCONNECTFEEDBACK_5_1 (38,  "Disconnect(follows High)"),
		DISCONNECTFEEDBACK_6_0 (39,  "Transition pending(No transition pending)"),
		DISCONNECTFEEDBACK_6_1 (40,  "Transition pending(Transition pending)"),
		LOADPROFILE_0 (41,  "Default"),
		LOADPROFILE_1 (42,  "Loadprofile_Logger_Size"),
		LOADPROFILE_2 (43,  "Loadprofile_Log_Reg_Changed"),
		LOADPROFILE_3 (44,  "Loadprofile_Logger_Interval"),
		POWERDOWN (45,  "Power Down"),
		POWERUP (46,  "Power Up")
		;

        private int code;
        private String name;

        EVENTATTRIBUTE(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
        
    }   
	
	
	public double getBasePulse1() {
		return basePulse1;
	}

	public void setBasePulse1(double basePulse1) {
		this.basePulse1 = basePulse1;
	}

	public double getBasePulse2() {
		return basePulse2;
	}

	public void setBasePulse2(double basePulse2) {
		this.basePulse2 = basePulse2;
	}

	public double getBasePulse3() {
		return basePulse3;
	}

	public void setBasePulse3(double basePulse3) {
		this.basePulse3 = basePulse3;
	}

	public double getBasePulse4() {
		return basePulse4;
	}

	public void setBasePulse4(double basePulse4) {
		this.basePulse4 = basePulse4;
	}

	public static void main(String[] args) {
		 try {
			 Kamstrup351B k351B = new Kamstrup351B();
//			MeterInfo 
//			k351B.parse(Hex.encode("4D5400A2100422360A003035303333344243353303E933040000E620FA041735080082020E081BF203050B03FF1604430000000004001604430000000004011A04430000000004021A044300000000041E21020000D647CE041F21020000D6042021020000D6043422044300000000043522044300000000043622044300000000043816044300000000043916044300000000043A160443000000008A940D"));
			//LP
//			k351B.parse(Hex.encode("4C44121B008D0F403FA01BF905110B0417350800020100000002050B00010204420000023E00020204420000000000030E04420000000200040E044200000110110C0201000F0002050B0000023E000000000000000200000110111BF20201001E0002050B0000023E000000000000000200000110110E0201002D0002050B0000023E000000000000000200000110110F020100000102050B0000023E00000000000000020000011011100201000F0102050B0000023E00000000000000020000011011110201001E0102050B0000023E00000000000000020000011011120201002D0102050B0000023E0000000000000002000001101112119F08105B403FA21BF90511130417350800020100000202050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011140201000F0202050B0000023E00000000000000020000011011150201001E0202050B0000023E00000000000000020000011011160201002D0202050B0000023E0000000000000002000001101117020100000302050B0000023E00000000000000020000011011180201000F0302050B0000023E00000000000000020000011011190201001E0302050B0000023E000000000000000200000110111A0201002D0302050B0000023E000000000000000200000110111A119F088297403FA21BF905111BE40417350800020100000402050B00010204420000023E00020204420000000000030E04420000000200040E044200000110111C0201000F0402050B0000023E000000000000000200000110111D0201001E0402050B0000023E000000000000000200000110111E0201002D0402050B0000023E000000000000000200000110111F020100000502050B0000023E00000000000000020000011011200201000F0502050B0000023E00000000000000020000011011210201001E0502050B0000023E00000000000000020000011011220201002D0502050B0000023E0000000000000002000001101122119F084DD4403FA21BF90511230417350800020100001BF902050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011240201000F1BF902050B0000023E00000000000000020000011011250201001E1BF902050B0000023E00000000000000020000011011260201002D1BF902050B0000023E0000000000000002000001101127020100000702050B0000023E00000000000000020000011011280201000F0702050B0000023E00000000000000020000011011290201001E0702050B0000023E000000000000000200000110112A0201002D0702050B0000023E000000000000000200000110112A119F0886E4403FA21BF905112B0417350800020100000802050B00010204420000023E00020204420000000000030E04420000000200040E044200000110112C0201000F0802050B0000023E000000000000000200000110112D0201001E0802050B0000023E000000000000000200000110112E0201002D0802050B0000023E000000000000000200000110112F020100000902050B0000023E00000000000000020000011011300201000F0902050B0000023E00000000000000020000011011310201001E0902050B0000023E00000000000000020000011011320201002D0902050B0000023E0000000000000002000001101132119F0898C8403FA21BF90511330417350800020100000A02050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011340201000F0A02050B0000023E00000000000000020000011011350201001E0A02050B0000023E00000000000000020000011011360201002D0A02050B0000023E0000000000000002000001101137020100000B02050B0000023E00000000000000020000011011380201000F0B02050B0000023E00000000000000020000011011390201001E0B02050B0000023E000000000000000200000110113A0201002D0B02050B0000023E000000000000000200000110113A119F0817B7403FA21BF905113B0417350800020100000C02050B00010204420000023E00020204420000000000030E04420000000200040E044200000110113C0201000F0C02050B0000023E000000000000000200000110113D0201001E0C02050B0000023E000000000000000200000110113E0201002D0C02050B0000023E000000000000000200000110113F020100001BF202050B0000023E000000000000000200000110111BBF0201000F1BF202050B0000023E00000000000000020000011011410201001E1BF202050B0000023E00000000000000020000011011420201002D1BF202050B0000023E0000000000000002000001101142119F08CDAC403FA21BF90511430417350800020100000E02050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011440201000F0E02050B0000023E00000000000000020000011011450201001E0E02050B0000023E00000000000000020000011011460201002D0E02050B0000023E0000000000000002000001101147020100000F02050B0000023E00000000000000020000011011480201000F0F02050B0000023E00000000000000020000011011490201001E0F02050B0000023E000000000000000200000110114A0201002D0F02050B0000023E000000000000000200000110114A119F088E02403FA21BF905114B0417350800020100001002050B00010204420000023E00020204420000000000030E04420000000200040E044200000110114C0201000F1002050B0000023E000000000000000200000110114D0201001E1002050B0000023E000000000000000200000110114E0201002D1002050B0000023E000000000000000200000110114F020100001102050B0000023E00000000000000020000011011500201000F1102050B0000023E00000000000000020000011011510201001E1102050B0000023E00000000000000020000011011520201002D1102050B0000023E0000000000000002000001101152119F083789403FA21BF90511530417350800020100001202050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011540201000F1202050B0000023E00000000000000020000011011550201001E1202050B0000023E00000000000000020000011011560201002D1202050B0000023E0000000000000002000001101157020100001302050B0000023E00000000000000020000011011580201000F1302050B0000023E00000000000000020000011011590201001E1302050B0000023E000000000000000200000110115A0201002D1302050B0000023E000000000000000200000110115A119F08B8F6403FA21BF905115B0417350800020100001402050B00010204420000023E00020204420000000000030E04420000000200040E044200000110115C0201000F1402050B0000023E000000000000000200000110115D0201001E1402050B0000023E000000000000000200000110115E0201002D1402050B0000023E000000000000000200000110115F020100001502050B0000023E00000000000000020000011011600201000F1502050B0000023E00000000000000020000011011610201001E1502050B0000023E00000000000000020000011011620201002D1502050B0000023E0000000000000002000001101162119F0877B5403FA21BF90511630417350800020100001602050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011640201000F1602050B0000023E00000000000000020000011011650201001E1602050B0000023E00000000000000020000011011660201002D1602050B0000023E0000000000000002000001101167020100001702050B0000023E00000000000000020000011011680201000F1702050B0000023E00000000000000020000011011690201001E1702050B0000023E000000000000000200000110116A0201002D1702050B0000023E000000000000000200000110116A119F08BC85403FA21BF905116B0417350800020200000003050B00010204420000023E00020204420000000000030E04420000000200040E044200000110116C0202000F0003050B0000023E000000000000000200000110116D0202001E0003050B0000023E000000000000000200000110116E0202002D0003050B0000023E000000000000000200000110116F020200000103050B0000023E00000000000000020000011011700202000F0103050B0000023E00000000000000020000011011710202001E0103050B0000023E00000000000000020000011011720202002D0103050B0000023E0000000000000002000001101172119F08C6A2403FA21BF90511730417350800020200000203050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011740202000F0203050B0000023E00000000000000020000011011750202001E0203050B0000023E00000000000000020000011011760202002D0203050B0000023E0000000000000002000001101177020200000303050B0000023E00000000000000020000011011780202000F0303050B0000023E00000000000000020000011011790202001E0303050B0000023E000000000000000200000110117A0202002D0303050B0000023E000000000000000200000110117A119F0849DD00403FA21BF905117B0417350800020200000403050B00010204420000023E00020204420000000000030E04420000000200040E044200000110117C0202000F0403050B0000023E000000000000000200000110117D0202001E0403050B0000023E000000000000000200000110117E0202002D0403050B0000023E000000000000000200000110117F020200000503050B0000023E000000000000000200000110111B7F0202000F0503050B0000023E00000000000000020000011011810202001E0503050B0000023E00000000000000020000011011820202002D0503050B0000023E0000000000000002000001101182119F08B976403FA21BF90511830417350800020200001BF903050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011840202000F1BF903050B0000023E00000000000000020000011011850202001E1BF903050B0000023E00000000000000020000011011860202002D1BF903050B0000023E0000000000000002000001101187020200000703050B0000023E00000000000000020000011011880202000F0703050B0000023E00000000000000020000011011890202001E0703050B0000023E000000000000000200000110118A0202002D0703050B0000023E000000000000000200000110118A119F08FBC5403FA21BF905118B0417350800020200000803050B00010204420000023E00020204420000000000030E04420000000200040E044200000110118C0202000F0803050B0000023E000000000000000200000110118D0202001E0803050B0000023E000000000000000200000110118E0202002D0803050B0000023E000000000000000200000110118F020200000903050B0000023E00000000000000020000011011900202000F0903050B0000023E00000000000000020000011011910202001E0903050B0000023E00000000000000020000011011920202002D0903050B0000023E0000000000000002000001101192119F08E5E9403FA21BF90511930417350800020200000A03050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011940202000F0A03050B0000023E00000000000000020000011011950202001E0A03050B0000023E00000000000000020000011011960202002D0A03050B0000023E0000000000000002000001101197020200000B03050B0000023E00000000000000020000011011980202000F0B03050B0000023E00000000000000020000011011990202001E0B03050B0000023E000000000000000200000110119A0202002D0B03050B0000023E000000000000000200000110119A119F086A96403FA21BF905119B0417350800020200000C03050B00010204420000023E00020204420000000000030E04420000000200040E044200000110119C0202000F0C03050B0000023E000000000000000200000110119D0202001E0C03050B0000023E000000000000000200000110119E0202002D0C03050B0000023E000000000000000200000110119F020200001BF203050B0000023E000000000000000200000110119F119F006A710D"));
//	        k351B.parse(Hex.encode("4C44121B00950F403FA01BF905110B0417350800020100000002050B00010204420000023E00020204420000000000030E04420000000200040E044200000110110C0201000F0002050B0000023E000000000000000200000110111BF20201001E0002050B0000023E000000000000000200000110110E0201002D0002050B0000023E000000000000000200000110110F020100000102050B0000023E00000000000000020000011011100201000F0102050B0000023E00000000000000020000011011110201001E0102050B0000023E00000000000000020000011011120201002D0102050B0000023E0000000000000002000001101112119F08105B403FA21BF90511130417350800020100000202050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011140201000F0202050B0000023E00000000000000020000011011150201001E0202050B0000023E00000000000000020000011011160201002D0202050B0000023E0000000000000002000001101117020100000302050B0000023E00000000000000020000011011180201000F0302050B0000023E00000000000000020000011011190201001E0302050B0000023E000000000000000200000110111A0201002D0302050B0000023E000000000000000200000110111A119F088297403FA21BF905111BE40417350800020100000402050B00010204420000023E00020204420000000000030E04420000000200040E044200000110111C0201000F0402050B0000023E000000000000000200000110111D0201001E0402050B0000023E000000000000000200000110111E0201002D0402050B0000023E000000000000000200000110111F020100000502050B0000023E00000000000000020000011011200201000F0502050B0000023E00000000000000020000011011210201001E0502050B0000023E00000000000000020000011011220201002D0502050B0000023E0000000000000002000001101122119F084DD4403FA21BF90511230417350800020100001BF902050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011240201000F1BF902050B0000023E00000000000000020000011011250201001E1BF902050B0000023E00000000000000020000011011260201002D1BF902050B0000023E0000000000000002000001101127020100000702050B0000023E00000000000000020000011011280201000F0702050B0000023E00000000000000020000011011290201001E0702050B0000023E000000000000000200000110112A0201002D0702050B0000023E000000000000000200000110112A119F0886E4403FA21BF905112B0417350800020100000802050B00010204420000023E00020204420000000000030E04420000000200040E044200000110112C0201000F0802050B0000023E000000000000000200000110112D0201001E0802050B0000023E000000000000000200000110112E0201002D0802050B0000023E000000000000000200000110112F020100000902050B0000023E00000000000000020000011011300201000F0902050B0000023E00000000000000020000011011310201001E0902050B0000023E00000000000000020000011011320201002D0902050B0000023E0000000000000002000001101132119F0898C8403FA21BF90511330417350800020100000A02050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011340201000F0A02050B0000023E00000000000000020000011011350201001E0A02050B0000023E00000000000000020000011011360201002D0A02050B0000023E0000000000000002000001101137020100000B02050B0000023E00000000000000020000011011380201000F0B02050B0000023E00000000000000020000011011390201001E0B02050B0000023E000000000000000200000110113A0201002D0B02050B0000023E000000000000000200000110113A119F0817B7403FA21BF905113B0417350800020100000C02050B00010204420000023E00020204420000000000030E04420000000200040E044200000110113C0201000F0C02050B0000023E000000000000000200000110113D0201001E0C02050B0000023E000000000000000200000110113E0201002D0C02050B0000023E000000000000000200000110113F020100001BF202050B0000023E000000000000000200000110111BBF0201000F1BF202050B0000023E00000000000000020000011011410201001E1BF202050B0000023E00000000000000020000011011420201002D1BF202050B0000023E0000000000000002000001101142119F08CDAC403FA21BF90511430417350800020100000E02050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011440201000F0E02050B0000023E00000000000000020000011011450201001E0E02050B0000023E00000000000000020000011011460201002D0E02050B0000023E0000000000000002000001101147020100000F02050B0000023E00000000000000020000011011480201000F0F02050B0000023E00000000000000020000011011490201001E0F02050B0000023E000000000000000200000110114A0201002D0F02050B0000023E000000000000000200000110114A119F088E02403FA21BF905114B0417350800020100001002050B00010204420000023E00020204420000000000030E04420000000200040E044200000110114C0201000F1002050B0000023E000000000000000200000110114D0201001E1002050B0000023E000000000000000200000110114E0201002D1002050B0000023E000000000000000200000110114F020100001102050B0000023E00000000000000020000011011500201000F1102050B0000023E00000000000000020000011011510201001E1102050B0000023E00000000000000020000011011520201002D1102050B0000023E0000000000000002000001101152119F083789403FA21BF90511530417350800020100001202050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011540201000F1202050B0000023E00000000000000020000011011550201001E1202050B0000023E00000000000000020000011011560201002D1202050B0000023E0000000000000002000001101157020100001302050B0000023E00000000000000020000011011580201000F1302050B0000023E00000000000000020000011011590201001E1302050B0000023E000000000000000200000110115A0201002D1302050B0000023E000000000000000200000110115A119F08B8F6403FA21BF905115B0417350800020100001402050B00010204420000023E00020204420000000000030E04420000000200040E044200000110115C0201000F1402050B0000023E000000000000000200000110115D0201001E1402050B0000023E000000000000000200000110115E0201002D1402050B0000023E000000000000000200000110115F020100001502050B0000023E00000000000000020000011011600201000F1502050B0000023E00000000000000020000011011610201001E1502050B0000023E00000000000000020000011011620201002D1502050B0000023E0000000000000002000001101162119F0877B5403FA21BF90511630417350800020100001602050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011640201000F1602050B0000023E00000000000000020000011011650201001E1602050B0000023E00000000000000020000011011660201002D1602050B0000023E0000000000000002000001101167020100001702050B0000023E00000000000000020000011011680201000F1702050B0000023E00000000000000020000011011690201001E1702050B0000023E000000000000000200000110116A0201002D1702050B0000023E000000000000000200000110116A119F08BC85403FA21BF905116B0417350800020200000003050B00010204420000023E00020204420000000000030E04420000000200040E044200000110116C0202000F0003050B0000023E000000000000000200000110116D0202001E0003050B0000023E000000000000000200000110116E0202002D0003050B0000023E000000000000000200000110116F020200000103050B0000023E00000000000000020000011011700202000F0103050B0000023E00000000000000020000011011710202001E0103050B0000023E00000000000000020000011011720202002D0103050B0000023E0000000000000002000001101172119F08C6A2403FA21BF90511730417350800020200000203050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011740202000F0203050B0000023E00000000000000020000011011750202001E0203050B0000023E00000000000000020000011011760202002D0203050B0000023E0000000000000002000001101177020200000303050B0000023E00000000000000020000011011780202000F0303050B0000023E00000000000000020000011011790202001E0303050B0000023E000000000000000200000110117A0202002D0303050B0000023E000000000000000200000110117A119F0849DD403FA21BF905117B0417350800020200000403050B00010204420000023E00020204420000000000030E04420000000200040E044200000110117C0202000F0403050B0000023E000000000000000200000110117D0202001E0403050B0000023E000000000000000200000110117E0202002D0403050B0000023E000000000000000200000110117F020200000503050B0000023E000000000000000200000110111B7F0202000F0503050B0000023E00000000000000020000011011810202001E0503050B0000023E00000000000000020000011011820202002D0503050B0000023E0000000000000002000001101182119F08B976403FA21BF90511830417350800020200001BF903050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011840202000F1BF903050B0000023E00000000000000020000011011850202001E1BF903050B0000023E00000000000000020000011011860202002D1BF903050B0000023E0000000000000002000001101187020200000703050B0000023E00000000000000020000011011880202000F0703050B0000023E00000000000000020000011011890202001E0703050B0000023E000000000000000200000110118A0202002D0703050B0000023E000000000000000200000110118A119F08FBC5403FA21BF905118B0417350800020200000803050B00010204420000023E00020204420000000000030E04420000000200040E044200000110118C0202000F0803050B0000023E000000000000000200000110118D0202001E0803050B0000023E000000000000000200000110118E0202002D0803050B0000023E000000000000000200000110118F020200000903050B0000023E00000000000000020000011011900202000F0903050B0000023E00000000000000020000011011910202001E0903050B0000023E00000000000000020000011011920202002D0903050B0000023E0000000000000002000001101192119F08E5E9403FA21BF90511930417350800020200000A03050B00010204420000023E00020204420000000000030E04420000000200040E04420000011011940202000F0A03050B0000023E00000000000000020000011011950202001E0A03050B0000023E00000000000000020000011011960202002D0A03050B0000023E0000000000000002000001101197020200000B03050B0000023E00000000000000020000011011980202000F0B03050B0000023E00000000000000020000011011990202001E0B03050B0000023E000000000000000200000110119A0202002D0B03050B0000023E000000000000000200000110119A119F086A96403FA21BF905119B0417350800020200000C03050B00010204420000023E00020204420000000000030E04420000000200040E044200000110119C0202000F0C03050B0000023E000000000000000200000110119D0202001E0C03050B0000023E000000000000000200000110119E0202002D0C03050B0000023E000000000000000200000110119F020200001BF203050B0000023E000000000000000200000110119F119F006A710D"));			 
//			Event
//			k351B.parse(Hex.encode("454C027B001C403FA2020403A30417350800820339290A1BE4040B03EA2F0400000196DD03EB3004000001AF5B00323301000003A482031BF21A0B1BE4040B0001B7E50001AF5B0103A582031BF21A0B1BE4040B0001B7E50001AF5B0003A682031C1C0B1BE4040B0001B8BC0001AF5B0103A782031C1C0B1BE4040B0001B8BC0001AF5B0003A882031C150C1BE4040B0001DD100001AF5B0103A982031C150C1BE4040B0001DD100001AF5B0003AA82030A360F1BE4040B00025F120001AF5B0103AB82030A360F1BE4040B00025F120001AF5B0003AC82031312111BE4040B00029F2B0001AF5B0103AD82031312111BE4040B00029F2B0001AF5B0003AD03AD101FA2403FA203040001041735080082023A330A07090A03EA2F040000019AC603EB30040000018A2B0415000100010002840225050B07090A0001AFC900018A2B040003840314200B150A0A0001BA4400018A9D040004841BF918201BF2050C0A0002086800018B55040005820333330A14040B00019ABF0001AF540100050005107645403FA2040403B7041735080082031C1C0B1BE4040B03EC2E0400000003D3043C0002000000043D0001002F03B8820325130C1BE4040B000003D400005703B982031C150C1BE4040B000003D400002F03BA820338160C1BE4040B000003D400004103BB820339160C1BE4040B000003D400001103BC820308180C1BE4040B000003D400000903BD820308180C1BE4040B000003D400006103BE82031BF91BE40F1BE4040B000003D700005703BF82030A360F1BE4040B000003D700002F03C082030110111BE4040B000003D900005703C182031312111BE4040B000003D900002F03C103C1100BA1403FA2050004C57D0D"));
//			Billing
//			k351B.parse(Hex.encode("42440269403FA20708000104173508001B7F05000000010A0A03EB30040000018A8903EA2F04000000000003EC2E04000000004100010204420000000000020204420000000000030E04420000000000040E0442000000000002820700000001050B0001AFA500000000000004280000023E000000000000000200000110000200021093E1080001001302044200000000001702044200000000001BE402044200000000001F0204420000000000150E04420000000000190E044200000000001D0E04420000000000210E04420000000000020000023E000000000000000000000000000000020000000000000000000000000002000210244C08000100271604430000000004032F04000000000004043004000000000004193508000000000000000000002B1604430000000000291A04430000000004682F04000000000004693004000000000000020000027A0001443800018A9E1B7F04001E08160A0A0000027A000000070001D4C00001AF540002000210EB7B080001041A3508000000000000000000040C16044300000000041BF22F040000000000040E30040000000000041BE43508000000000000000000041000010000046A1A044300000000046C2F04000000000000021B7F04001E08160A0A000000000000000000000000000000000000000000000000070001D4C000020002108EB21BF90001046D30040000000000046B3508000000000000000000046E1A04430000000004702F040000000000047130040000000000046F350800000000000000000000020001AF54820300000C14040B000000000000000000000000000000000000000000020002106ED30D"));
//			present Month값만 넘어올때
//			k351B.parse(Hex.encode("42440204403FA2070800020417350800820700000001050B03EB3004000001AFA503EA2F04000000000003EC2E04000000042800010204420000023E00020204420000000000030E04420000000200040E0442000001100002000210EA5208000200130204420000023E001702044200000000001BE402044200000000001F0204420000000000150E04420000000200190E044200000000001D0E04420000000000210E04420000000000020002106FA608000200271604430000027A04032F040000014438040430040000018A9E04193508001B7F04001E08160A0A002B1604430000027A00291A04430000000704682F04000001D4C004693004000001AF5400020002105C570800020467350800820300000C14040B002D1A044300000007002F33010002003A000400000000020030330200000104091604430000027A040A2F040000014438040B30040000018A9E000200021082C0080002041A3508001B7F04001E08160A0A040C16044300000000041BF22F040000000000040E30040000000000041BE43508000000000000000000041000010000046A1A044300000007046C2F04000001D4C00002000210811BBF1BF90002046D3004000001AF54046B350800820300000C14040B046E1A04430000000004702F040000000000047130040000000000046F350800000000000000000000020002101BF2280D"));
//			k351B.parse(Hex.encode("000000000000000000000000000000000000000000000000000000000B010101000411DB0705090F23344D44001D010101000000000000000000000000000007DB05090923340801004D5400A1100422360A003035303333344243353303E933040000E620FA0417350800820131230F09050B03FF1604430000000004001604430000000004011A04430000000004021A044300000000041E21020000D73BD8041F21020000D7042021020000D7043422044300000000043522044300000000043622044300000000043816044300000000043916044300000000043A16044300000000D47D0D42440269403FA20708000104173508001B7F05000000010A0A03EB30040000018A8903EA2F04000000000003EC2E04000000004100010204420000000000020204420000000000030E04420000000000040E0442000000000002820700000001050B0001AFA500000000000004280000023E000000000000000200000110000200021093E1080001001302044200000000001702044200000000001BE402044200000000001F0204420000000000150E04420000000000190E044200000000001D0E04420000000000210E04420000000000020000023E000000000000000000000000000000020000000000000000000000000002000210244C08000100271604430000000004032F04000000000004043004000000000004193508000000000000000000002B1604430000000000291A04430000000004682F04000000000004693004000000000000020000027A0001443800018A9E1B7F04001E08160A0A0000027A000000070001D4C00001AF540002000210EB7B080001041A3508000000000000000000040C16044300000000041BF22F040000000000040E30040000000000041BE43508000000000000000000041000010000046A1A044300000000046C2F04000000000000021B7F04001E08160A0A000000000000000000000000000000000000000000000000070001D4C000020002108EB21BF90001046D30040000000000046B3508000000000000000000046E1A04430000000004702F040000000000047130040000000000046F350800000000000000000000020001AF54820300000C14040B000000000000000000000000000000000000000000020002106ED30D4C440BA000600F403FA01BF905127C0417350800020700000F08050B00010204420000023E00020204420000000000030E04420000000200040E044200000110127D0207000F0F08050B0000023E000000000000000200000110127E0207001E0F08050B0000023E000000000000000200000110127F0207002D0F08050B0000023E000000000000000200000110121B7F020700001008050B0000023E00000000000000020000011012810207000F1008050B0000023E00000000000000020000011012820207001E1008050B0000023E00000000000000020000011012830207002D1008050B0000023E000000000000000200000110128312DB08E636403FA21BF90512840417350800020700001108050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012850207000F1108050B0000023E00000000000000020000011012860207001E1108050B0000023E00000000000000020000011012870207002D1108050B0000023E0000000000000002000001101288020700001208050B0000023E00000000000000020000011012890207000F1208050B0000023E000000000000000200000110128A0207001E1208050B0000023E000000000000000200000110128B0207002D1208050B0000023E000000000000000200000110128B12DB08F9E5403FA21BF905128C0417350800020700001308050B00010204420000023E00020204420000000000030E04420000000200040E044200000110128D0207000F1308050B0000023E000000000000000200000110128E0207001E1308050B0000023E000000000000000200000110128F0207002D1308050B0000023E0000000000000002000001101290020700001408050B0000023E00000000000000020000011012910207000F1408050B0000023E00000000000000020000011012920207001E1408050B0000023E00000000000000020000011012930207002D1408050B0000023E000000000000000200000110129312DB08719E403FA21BF90512940417350800020700001508050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012950207000F1508050B0000023E00000000000000020000011012960207001E1508050B0000023E00000000000000020000011012970207002D1508050B0000023E0000000000000002000001101298020700001608050B0000023E00000000000000020000011012990207000F1608050B0000023E000000000000000200000110129A0207001E1608050B0000023E000000000000000200000110129B0207002D1608050B0000023E000000000000000200000110129B12DB08B375403FA21BF905129C0417350800020700001708050B00010204420000023E00020204420000000000030E04420000000200040E044200000110129D0207000F1708050B0000023E000000000000000200000110129E0207001E1708050B0000023E000000000000000200000110129F0207002D1708050B0000023E00000000000000020000011012A0020100000009050B0000023E00000000000000020000011012A10201000F0009050B0000023E00000000000000020000011012A20201001E0009050B0000023E00000000000000020000011012A30201002D0009050B0000023E00000000000000020000011012A312DB08F6A4403FA21BF90512A40417350800020100000109050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012A50201000F0109050B0000023E00000000000000020000011012A60201001E0109050B0000023E00000000000000020000011012A70201002D0109050B0000023E00000000000000020000011012A8020100000209050B0000023E00000000000000020000011012A90201000F0209050B0000023E00000000000000020000011012AA0201001E0209050B0000023E00000000000000020000011012AB0201002D0209050B0000023E00000000000000020000011012AB12DB083D3B403FA21BF90512AC0417350800020100000309050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012AD0201000F0309050B0000023E00000000000000020000011012AE0201001E0309050B0000023E00000000000000020000011012AF0201002D0309050B0000023E00000000000000020000011012B0020100000409050B0000023E00000000000000020000011012B10201000F0409050B0000023E00000000000000020000011012B20201001E0409050B0000023E00000000000000020000011012B30201002D0409050B0000023E00000000000000020000011012B312DB08B51BBF403FA21BF90512B40417350800020100000509050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012B50201000F0509050B0000023E00000000000000020000011012B60201001E0509050B0000023E00000000000000020000011012B70201002D0509050B0000023E00000000000000020000011012B8020100001BF909050B0000023E00000000000000020000011012B90201000F1BF909050B0000023E00000000000000020000011012BA0201001E1BF909050B0000023E00000000000000020000011012BB0201002D1BF909050B0000023E00000000000000020000011012BB12DB0877AB403FA21BF90512BC0417350800020100000709050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012BD0201000F0709050B0000023E00000000000000020000011012BE0201001E0709050B0000023E00000000000000020000011012BF0201002D0709050B0000023E00000000000000020000011012C0020100000809050B0000023E00000000000000020000011012C10201000F0809050B0000023E00000000000000020000011012C20201001E0809050B0000023E00000000000000020000011012C30201002D0809050B0000023E00000000000000020000011012C312DB08F8C0403FA21BF90512C40417350800020100000909050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012C50201000F0909050B0000023E00000000000000020000011012C60201001E0909050B0000023E00000000000000020000011012C70201002D0909050B0000023E00000000000000020000011012C8020100000A09050B0000023E00000000000000020000011012C98201000F0A09050B0000023E00000000000000020000011012CA8201002D0A09050B0000023E00000000000000020000011012CB020100000B09050B0000023E00000000000000020000011012CB12DB086CE6403FA21BF90512CC04173508000201000F0B09050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012CD0201001E0B09050B0000023E00000000000000020000011012CE0201002D0B09050B0000023E00000000000000020000011012CF020100000C09050B0000023E00000000000000020000011012D00201000F0C09050B0000023E00000000000000020000011012D10201001E0C09050B0000023E00000000000000020000011012D20201002D0C09050B0000023E00000000000000020000011012D3020100001BF209050B0000023E00000000000000020000011012D312DB0860A5403FA21BF90512D404173508000201000F1BF209050B00010204420000023E00020204420000000000030E04420000000200040E04420000011012D58201001E1BF209050B0000023E00000000000000020000011012D60201002D1BF209050B0000023E00000000000000020000011012D7020100000E09050B0000023E00000000000000020000011012D88201002D0E09050B0000023E00000000000000020000011012D9820100000F09050B0000023E00000000000000020000011012DA0201000F0F09050B0000023E00000000000000020000011012DB0201001E0F09050B0000023E00000000000000020000011012DB12DB00F6390D454C0247000A403FA2020403D0041735080082011BF20B0A09050B03EA2F040000018AF903EB3004000001AFAD00323301000103D182011BF20B0A09050B00018AF90001AFAD0003D28201291F0A09050B000192E50001AFAD0103D38201291F0A09050B000192E50001AFAD0003D4820129151BF209050B0002042D0001AFAD0103D5820129151BF209050B0002042D0001AFAD0003D682012C250E09050B0002311B7F0001AFAD0103D782012C250E09050B0002311B7F0001AFAD0003D882012C320E09050B000236940001AFAD0103D982012C320E09050B000236940001AFAD0003D903D9101D41403FA203040001041735080082023A330A07090A03EA2F040000019AC603EB30040000018A2B0415000100010002840225050B07090A0001AFC900018A2B040003840314200B150A0A0001BA4400018A9D040004841BF918201BF2050C0A0002086800018B55040005820333330A14040B00019ABF0001AF540100050005107645403FA2040403E2041735080082010B1BF90A09050B03EC2E0400000004B0043C0002000000043D0001005703E382011BF20B0A09050B000004B000002F03E48201171C0A09050B000004B000005703E58201291F0A09050B000004B000002F03E6820133131BF209050B000004B300005703E7820129151BF209050B000004B300002F03E88201340B0E09050B000004B400005703E982012C250E09050B000004B400002F03EA82010E310E09050B000004B400005703EB82012C320E09050B000004B400002F03EB03EB1083CA403FA2050004C57D0D"));
//			k351B.parse(Hex.encode("035998300120573531353038313732320000000000000000000000000B01010100F505DB0705110822334D4400230101010D0A3435303030313032313134303037350D0A0007DB05111122320C00004D5400A2100422360A003035303333344243353303E933040000E620FA041735080082050423081BF2050B03FF1604430000000004001604430000000004011A04430000000004021A044300000000041E21020000D661A0041F21020000D6042021020000D6043422044300000000043522044300000000043622044300000000043816044300000000043916044300000000043A160443000000008A940D424402DB403FA20708000104173508001B7F05000000010A0A03EB30040000018A8903EA2F04000000000003EC2E04000000004100010204420000000000020204420000000000030E04420000000000040E0442000000000002820700000001050B0001AFA500000000000004280000023E000000000000000200000110000200021093E1080001001302044200000000001702044200000000001BE402044200000000001F0204420000000000150E04420000000000190E044200000000001D0E04420000000000210E04420000000000020000023E000000000000000000000000000000020000000000000000000000000002000210244C08000100271604430000000004032F04000000000004043004000000000004193508000000000000000000002B1604430000000000291A04430000000004682F04000000000004693004000000000000020000027A0001443800018A9E1B7F04001E08160A0A0000027A000000070001D4C00001AF540002000210EB7B08000104673508000000000000000000002D1A044300000000002F33010001003A0004000000000000303302000001040916044300000000040A2F040000000000040B300400000000000002820300000C14040B00000007020000000200010000027A0001443800018A9E00020002104826080001041A3508000000000000000000040C16044300000000041BF22F040000000000040E30040000000000041BE43508000000000000000000041000010000046A1A044300000000046C2F04000000000000021B7F04001E08160A0A000000000000000000000000000000000000000000000000070001D4C000020002108EB21BF90001046D30040000000000046B3508000000000000000000046E1A04430000000004702F040000000000047130040000000000046F350800000000000000000000020001AF54820300000C14040B000000000000000000000000000000000000000000020002106ED30D4C44000C00000F403FA01BF9000471450D454C02480019403FA202040402041735080082040E050B0C050B03EA2F04000001AFB203EB3004000001AFB0003233010001040382040E050B0C050B0001AFB20001AFB000040482042F0C0B0C050B0001B28F0001AFB001040582042F0C0B0C050B0001B28F0001AFB000041BF9820416140B0C050B0001B5960001AFB0010407820416140B0C050B0001B5960001AFB000040882040F170B0C050B0001B6BB0001AFB001040982040F170B0C050B0001B6BB0001AFB000040A82041BF21C1BF20C050B00021BF9CD0001AFB001040B82041BF21C1BF20C050B00021BF9CD0001AFB000040B040B10D95E403FA203040001041735080082023A330A07090A03EA2F040000019AC603EB30040000018A2B0415000100010002840225050B07090A0001AFC900018A2B040003840314200B150A0A0001BA4400018A9D040004841BF918201BF2050C0A0002086800018B55040005820333330A14040B00019ABF0001AF540100050005107645403FA2040404140417350800820423040B0C050B03EC2E0400000004CB043C0002000000043D00010057041582040E050B0C050B000004CB00002F04168204000C0B0C050B000004CB000057041782042F0C0B0C050B000004CB00002F041882041BE4130B0C050B000004CB0000570419820416140B0C050B000004CB00002F041A820420140B0C050B000004CB000057041BE482040F170B0C050B000004CB00002F041C820436200B0C050B000004CB000057041D82041BF21C1BF20C050B000004CB00002F041D041D10DB77403FA2050004C57D0D"));
			 //4C440C760063
			 StringBuffer t = new StringBuffer();
			 t.append("4D440023010201343530303031303231313430303735000000000007DB051A1A2E310501004D5400A1100422360A003035303333344243353303E933040000E620FA04173508008204192D091A050B03FF1604430000000004001604430000000004011A04430000000004021A044300000000041E21020000D63791041F21020000D6042021020000D6043422044300000000043522044300000000043622044300000000043816044300000000043916044300000000043A160443000000008A94000100010D424402DB403FA20708000104173508001B7F05000000010A0A03EB30040000018A8903EA2F04000000000003EC2E04000000004100010204420000000000020204420000000000030E04420000000000040E0442000000000002820700000001050B0001AFA500000000000004280000023E000000000000000200000110000200021093E1080001001302044200000000001702044200000000001BE402044200000000001F0204420000000000150E04420000000000190E044200000000001D0E04420000000000210E04420000000000020000023E000000000000000000000000000000020000000000000000000000000002000210244C08000100271604430000000004032F04000000000004043004000000000004193508000000000000000000002B1604430000000000291A04430000000004682F04000000000004693004000000000000020000027A0001443800018A9E1B7F04001E08160A0A0000027A000000070001D4C00001AF540002000210EB7B08000104673508000000000000000000002D1A044300000000002F33010001003A0004000000000000303302000001040916044300000000040A2F040000000000040B300400000000000002820300000C14040B00000007020000000200010000027A0001443800018A9E00020002104826080001041A3508000000000000000000040C16044300000000041BF22F040000000000040E30040000000000041BE43508000000000000000000041000010000046A1A044300000000046C2F04000000000000021B7F04001E08160A0A000000000000000000000000000000000000000000000000070001D4C000020002108EB21BF90001046D30040000000000046B3508000000000000000000046E1A04430000000004702F040000000000047130040000000000046F350800000000000000000000020001AF54820300000C14040B000000000000000000000000000000000000000000020002106ED30D4C440F8300800F403FA01BF90517E00417350800020300000219050B00010204420000052E00020204420000000000030E04420000000300040E04420000029017E10203000F0219050B0000052E00000000000000030000029017E20203001E0219050B0000052E00000000000000030000029017E30203002D0219050B0000052E00000000000000030000029017E4020300000319050B0000052E00000000000000030000029017E50203000F0319050B0000052E00000000000000030000029117E60203001E0319050B0000052E00000000000000030000029117E70203002D0319050B0000052E00000000000000030000029117E7185F088CD1403FA21BF90517E80417350800020300000419050B00010204420000052E00020204420000000000030E04420000000300040E04420000029117E90203000F0419050B0000052F00000000000000030000029117EA0203001E0419050B0000052F00000000000000030000029117EB0203002D0419050B0000052F00000000000000030000029117EC020300000519050B0000052F00000000000000030000029117ED0203000F0519050B0000052F00000000000000030000029117EE0203001E0519050B0000052F00000000000000030000029217EF0203002D0519050B0000052F00000000000000030000029217EF185F08B289403FA21BF90517F00417350800020300001BF919050B00010204420000052F00020204420000000000030E04420000000300040E04420000029217F10203000F1BF919050B0000052F00000000000000030000029217F20203001E1BF919050B0000052F00000000000000030000029217F30203002D1BF919050B0000052F00000000000000030000029217F4020300000719050B0000052F00000000000000030000029217F50203000F0719050B0000052F00000000000000030000029217F60203001E0719050B0000052F00000000000000030000029217F70203002D0719050B0000052F00000000000000030000029317F7185F085F64403FA21BF90517F80417350800020300000819050B00010204420000052F00020204420000000000030E04420000000300040E04420000029317F90203000F0819050B0000052F00000000000000030000029317FA0203001E0819050B0000052F00000000000000030000029317FB0203002D0819050B0000052F00000000000000030000029317FC020300000919050B0000052F00000000000000030000029317FD0203000F0919050B0000053000000000000000030000029317FE0203001E0919050B0000053000000000000000030000029317FF0203002D0919050B0000053000000000000000030000029317FF185F08E6F7403FA21BF90518000417350800020300000A19050B00010204420000053000020204420000000000030E04420000000300040E04420000029418010203000F0A19050B0000053000000000000000030000029418020203001E0A19050B0000053000000000000000030000029418030203002D0A19050B000005300000000000000003000002941804020300000B19050B0000053000000000000000030000029418050203000F0B19050B00000530000000000000000300000294181BF90203001E0B19050B0000053000000000000000030000029418070203002D0B19050B000005300000000000000003000002941807185F08938D403FA21BF90518080417350800020300000C19050B00010204420000053000020204420000000000030E04420000000300040E04420000029418090203000F0C19050B00000530000000000000000300000295180A0203001E0C19050B00000530000000000000000300000295180B0203002D0C19050B00000530000000000000000300000295180C020300001BF219050B00000530000000000000000300000295181BF20203000F1BF219050B00000530000000000000000300000295180E0203001E1BF219050B00000530000000000000000300000295180F0203002D1BF219050B00000530000000000000000300000295180F185F0830F9403FA21BF90518100417350800020300000E19050B00010204420000053100020204420000000000030E04420000000300040E04420000029518110203000F0E19050B0000053100000000000000030000029518120203001E0E19050B0000053100000000000000030000029618130203002D0E19050B000005310000000000000003000002961814020300000F19050B0000053100000000000000030000029618150203000F0F19050B0000053100000000000000030000029618160203001E0F19050B0000053100000000000000030000029618170203002D0F19050B000005310000000000000003000002961817185F0841F1403FA21BF90518180417350800020300001019050B00010204420000053100020204420000000000030E04420000000300040E04420000029618190203000F1019050B00000531000000000000000300000296181A0203001E1019050B00000531000000000000000300000296181BE42203002D1019050B00000531000000000000000300000297181C020300001119050B00000531000000000000000300000297181D0203000F1119050B00000531000000000000000300000297181E0203001E1119050B00000531000000000000000300000297181F0203002D1119050B00000531000000000000000300000297181F185F080C82403FA21BF90518200417350800020300001219050B00010204420000053100020204420000000000030E04420000000300040E04420000029718210203000F1219050B0000053100000000000000030000029718220203001E1219050B0000053100000000000000030000029718230203002D1219050B000005310000000000000003000002971824020300001319050B0000053200000000000000030000029818250203000F1319050B0000053200000000000000030000029818260203001E1319050B0000053200000000000000030000029818270203002D1319050B000005320000000000000003000002981827185F084249403FA21BF90518280417350800020300001419050B00010204420000053200020204420000000000030E04420000000300040E04420000029818290203000F1419050B00000532000000000000000300000298182A0203001E1419050B00000532000000000000000300000298182B0203002D1419050B00000532000000000000000300000298182C020300001519050B00000535000000000000000300000299182D0203000F1519050B0000053B000000000000000300000299182E0203001E1519050B0000051BBF000000000000000300000299182F0203002D1519050B00000541000000000000000300000299182F185F083725403FA21BF90518300417350800020300001619050B00010204420000054100020204420000000000030E04420000000300040E04420000029918310203000F1619050B0000054100000000000000030000029918320203001E1619050B0000054100000000000000030000029918330203002D1619050B000005410000000000000003000002991834020300001719050B0000054100000000000000030000029918350203000F1719050B0000054100000000000000030000029918360203001E1719050B0000054100000000000000030000029918370203002D1719050B000005410000000000000003000002991837185F085D10403FA21BF9051838041735080002040000001A050B00010204420000054100020204420000000000030E04420000000300040E04420000029918390204000F001A050B00000541000000000000000300000299183A0204001E001A050B00000541000000000000000300000299183B0204002D001A050B00000541000000000000000300000299183C02040000011A050B00000541000000000000000300000299183D0204000F011A050B00000541000000000000000300000299183E0204001E011A050B00000541000000000000000300000299183F0204002D011A050B00000541000000000000000300000299183F185F08AF37403FA21BF905181BBF041735080002040000021A050B00010204420000054100020204420000000000030E04420000000300040E04420000029918410204000F021A050B0000054100000000000000030000029918420204001E021A050B0000054100000000000000030000029918430204002D021A050B00000541000000000000000300000299184402040000031A050B0000054100000000000000030000029918450204000F031A050B0000054100000000000000030000029918460204001E031A050B0000054100000000000000030000029918470204002D031A050B000005410000000000000003000002991847185F08F63B403FA21BF9051848041735080002040000041A050B00010204420000054100020204420000000000030E04420000000300040E04420000029918490204000F041A050B00000541000000000000000300000299184A0204001E041A050B00000541000000000000000300000299184B0204002D041A050B00000541000000000000000300000299184C02040000051A050B00000541000000000000000300000299184D0204000F051A050B00000541000000000000000300000299184E0204001E051A050B00000541000000000000000300000299184F0204002D051A050B00000541000000000000000300000299184F185F083682403FA21BF90518500417350800020400001BF91A050B00010204420000054100020204420000000000030E04420000000300040E04420000029918510204000F1BF91A050B0000054100000000000000030000029918520204001E1BF91A050B0000054100000000000000030000029918530204002D1BF91A050B00000541000000000000000300000299185402040000071A050B0000054100000000000000030000029918550204000F071A050B0000054100000000000000030000029918560204001E071A050B0000054100000000000000030000029918570204002D071A050B000005410000000000000003000002991857185F08BCAB403FA21BF9051858041735080002040000081A050B00010204420000054100020204420000000000030E04420000000300040E04420000029918590204000F081A050B00000541000000000000000300000299185A0204001E081A050B00000541000000000000000300000299185B0204002D081A050B00000541000000000000000300000299185C02040000091A050B0000054200000000000000030000029A185D0204000F091A050B0000054400000000000000030000029A185E0204001E091A050B0000054400000000000000030000029A185F0204002D091A050B0000054400000000000000030000029A185F185F00E48E0D454C025E001A403FA2020404220417350800820113280A17050B03EA2F04000001965303EB3004000001AFBB0032330100010423820113280A17050B000196530001AFBB0004248201321BF90B17050B0001B03A0001AFBB0104258201321BF90B17050B0001B03A0001AFBB0004268201080B1BF217050B000200240001AFBB0104278201080B1BF217050B000200240001AFBB000428820209041118050B000299A90001AFBC010429820209041118050B000299A90001AFBC00042A82030B261019050B00027FE30001AFBD01042B82030B261019050B00027FE30001AFBD00042B042B10D65B403FA203040001041735080082023A330A07090A03EA2F040000019AC603EB30040000018A2B0415000100010002840225050B07090A0001AFC900018A2B040003840314200B150A0A0001BA4400018A9D040004841BF918201BF2050C0A0002086800018B55040005820333330A14040B00019ABF0001AF5401001BF9820100371BF210050B0002114C0001AFB401001BF9001BF9107137403FA20404041BBF0417350800820134270A17050B03EC2E0400000005CF043C0002000000043D000100570441820113280A17050B000005CF00002F044282013B2A0A17050B000005CF00005704438201321BF90B17050B000005CF00002F0444820118091BF217050B000005D100005704458201080B1BF217050B000005D100002F0446820202041118050B000005ED0000570447820209041118050B000005ED00002F0448820302261019050B00001BF904000057044982030B261019050B00001BF90400002F0449044910BBA8403FA2050004C57D0D");
//			 t.append("0194154E020300000512050B0000036D000000000000000200000196154F0203000F0512050B0000036D00000000000000020000019815500203001E0512050B0000036D00000000000000020000019A15510203002D0512050B0000036E00000000000000020000019C1551158D089A53403FA21BF90515520417350800020300001BF912050B00010204420000036E00020204420000000000030E04420000000200040E04420000019E15530203000F1BF912050B0000036E0000000000000002000001A015540203001E1BF912050B0000036F0000000000000002000001A215550203002D1BF912050B0000036F0000000000000002000001A51556020300000712050B0000036F0000000000000002000001A715570203000F0712050B000003700000000000000002000001A915580203001E0712050B000003700000000000000002000001AB15590203002D0712050B000003700000000000000002000001AD1559158D0834E2403FA21BF905155A0417350800020300000812050B00010204420000037000020204420000000000030E04420000000200040E0442000001AF155B0203000F0812050B000003710000000000000002000001B1155C0203001E0812050B000003710000000000000002000001B3155D0203002D0812050B000003710000000000000002000001B5155E020300000912050B000003720000000000000002000001B7155F0203000F0912050B000003750000000000000002000001B915600203001E0912050B000003790000000000000002000001B915610203002D0912050B0000037D0000000000000003000001BA1561158D081BF970403FA21BF90515620417350800020300000A12050B00010204420000038200020204420000000000030E04420000000300040E0442000001BB15630203000F0A12050B000003860000000000000003000001BC15640203001E0A12050B0000038A0000000000000003000001BD15650203002D0A12050B0000038E0000000000000003000001BE1566020300000B12050B000003920000000000000003000001BF15670203000F0B12050B000003940000000000000003000001BF15680203001E0B12050B000003960000000000000003000001C015690203002D0B12050B000003980000000000000003000001C01569158D08E752403FA21BF905156A0417350800020300000C12050B00010204420000039A00020204420000000000030E04420000000300040E0442000001C0156B0203000F0C12050B0000039B0000000000000003000001C1156C0203001E0C12050B0000039B0000000000000003000001C1156D0203002D0C12050B0000039C0000000000000003000001C2156E020300001BF212050B0000039E0000000000000003000001C2156F0203000F1BF212050B000003A00000000000000003000001C215700203001E1BF212050B000003A20000000000000003000001C315710203002D1BF212050B000003A40000000000000003000001C31571158D089C61403FA21BF90515720417350800020300000E12050B0001020442000003A500020204420000000000030E04420000000300040E0442000001C315730203000F0E12050B000003A70000000000000003000001C315740203001E0E12050B000003A90000000000000003000001C415750203002D0E12050B000003AB0000000000000003000001C41576020300000F12050B000003AD0000000000000003000001C415770203000F0F12050B000003AF0000000000000003000001C515780203001E0F12050B000003B10000000000000003000001C515790203002D0F12050B000003B30000000000000003000001C51579158D08C1C5403FA21BF905157A0417350800020300001012050B0001020442000003B500020204420000000000030E04420000000300040E0442000001C6157B0203000F1012050B000003B70000000000000003000001C6157C0203001E1012050B000003B90000000000000003000001C6157D0203002D1012050B000003BA0000000000000003000001C7157E020300001112050B000003BC0000000000000003000001C7157F0203000F1112050B000003BE0000000000000003000001C7151B7F0203001E1112050B000003C00000000000000003000001C715810203002D1112050B000003C20000000000000003000001C81581158D08D885403FA21BF90515820417350800020300001212050B0001020442000003C400020204420000000000030E04420000000300040E0442000001C815830203000F1212050B000003C60000000000000003000001C815840203001E1212050B000003C80000000000000003000001C915850203002D1212050B000003C80000000000000003000001C91586020300001312050B000003C90000000000000003000001CA15870203000F1312050B000003CB0000000000000003000001CA15880203001E1312050B000003CD0000000000000003000001CA15890203002D1312050B000003CF0000000000000003000001CA1589158D088F7D403FA21BF905158A0417350800020300001412050B0001020442000003D100020204420000000000030E04420000000300040E0442000001CB158B0203000F1412050B000003D30000000000000003000001CB158C0203001E1412050B000003D50000000000000003000001CB158D0203002D1412050B000003D70000000000000003000001CB158D158D004C850D454C0255001A403FA2020404180417350800820104041110050B03EA2F0400000299A403EB3004000001AFB40032330100010419820104041110050B");
//			 t.append("000299A40001AFB400041A82012C0C1110050B00029CEC0001AFB401041BE482012C0C1110050B00029CEC0001AFB400041C820114101110050B00029E640001AFB401041D820114101110050B00029E640001AFB400041E820120171110050B0002A12C0001AFB401041F820120171110050B0002A12C0001AFB400042082011A1F1110050B0002A4460001AFB401042182011A1F1110050B0002A4460001AFB40004210421104E6F403FA203040001041735080082023A330A07090A03EA2F040000019AC603EB30040000018A2B0415000100010002840225050B07090A0001AFC900018A2B040003840314200B150A0A0001BA4400018A9D040004841BF918201BF2050C0A0002086800018B55040005820333330A14040B00019ABF0001AF5401001BF9820100371BF210050B0002114C0001AFB401001BF9001BF9107137403FA20404043604173508008201281A1110050B03EC2E04000000052E043C0002000000043D0001004104378201291A1110050B0000052E00001104388201291A1110050B0000052E00004204398201291A1110050B0000052E000012043A8201101D1110050B0000052E00000A043B8201101D1110050B0000052E000062043C8201111D1110050B0000052E000009043D8201111D1110050B0000052E000061043E8201071F1110050B0000052E000057043F82011A1F1110050B0000052E00002F043F043F101701403FA2050004C57D0D");
			k351B.parse(Hex.encode(t.toString()));
//			 k351B.getStuffing2(Hex.encode(t.toString()));
			                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     			 																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																															
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	}
	
}


