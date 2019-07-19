package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.HMData;
import com.aimir.fep.meter.parser.multical401CompatTable.BaseRecord;
import com.aimir.fep.meter.parser.multical401CompatTable.LPRecordData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * parsing Multical401COMPAT meter data
 *
 * @author kaze
 */
public class Multical401COMPAT extends MBus
{
	private static final long serialVersionUID = 9191952095144340718L;

	private static Log log = LogFactory.getLog(Multical401COMPAT.class);

    protected LPRecordData lpRecordData = null;//LP Record Data

    public Multical401COMPAT() {
    	BANK = new byte[1];
    	LPYEAR = new byte[2];
    	LPMONTH = new byte[1];
    	LPDAY = new byte[1];
    	BASERECORD = new byte[136];
    	LPRECORDDATA = new byte[1536];

    	BASEPULSE = new byte[4];
    	LP = new byte[2];
    	ERRORCODE = new byte[2];

    	lpChannelCount = 9;
    }

    /**
     * constructor
     */
    public Multical401COMPAT(String meterId)
    {
    	this();
        this.meterId = meterId;
    }

    /**
     * parse meter mesurement data
     * @param data
     */
    /* (non-Javadoc)
     * @see nuri.aimir.service.common.parser.em.EnergyMeterDataParser#parse(byte[], int)
     */
    public void parse(byte[] data) throws Exception
    {
    	log.debug("Multical401COMPAT LENGTH["+data.length+"] RAW["+Hex.decode(data)+"]");
    	rawData=data;
        int pos = 0;

        log.debug("LPPERIOD[" + period + "]");

        //Metering Fail
        if (period == 0) {
            System.arraycopy(data, pos, ERRORCODE, 0, ERRORCODE.length);
            pos += ERRORCODE.length;
            DataUtil.convertEndian(ERRORCODE);
            errorCode = DataUtil.getIntTo2Byte(ERRORCODE);
        }
        //Metering Success
        else {
        	DecimalFormat df = new DecimalFormat("######.######");
            int dateCnt = 1;
            log.debug("DATECNT[" + dateCnt + "]");
            byte[] mbusTotalRom = new byte[data.length - pos];//MBus Total Metering Log Data
            System.arraycopy(data, pos, mbusTotalRom, 0, mbusTotalRom.length);
            
            int totalpos = 0;
            
            ArrayList<HMData> currentList = null;
            ArrayList<HMData> lpList = null;
            ArrayList<HMData> dayList= null;
            ArrayList<HMData> monthList= null;
            
            for (int i = 0; totalpos < mbusTotalRom.length ; i++) {
            	pos = 0;
                /*
            	System.arraycopy(data, pos, BANK, 0, BANK.length);//1byte
                pos += BANK.length;
                int bank = DataUtil.getIntToBytes(BANK);
                */

                System.arraycopy(data, totalpos, METERTYPE, 0, METERTYPE.length);//1byte
                pos += METERTYPE.length;
                meterType = DataUtil.getIntToBytes(METERTYPE);
                log.debug("meterType[" + meterType + "]");

            	//old int lpLength = BANK.length + LPYEAR.length + LPMONTH.length + LPDAY.length + BASERECORD.length + LPRECORDDATA.length;
            	int lpLength = METERTYPE.length + LPYEAR.length + LPMONTH.length + LPDAY.length + BASERECORD.length + LPRECORDDATA.length;

            	byte[] mbusDayRom = new byte[lpLength];//MBus One Day Metering Log Data : 1677 byte
            	System.arraycopy(mbusTotalRom, totalpos , mbusDayRom, 0, mbusDayRom.length);

            	//-------------------
                //GMT : 4byte
            	//-------------------
            	System.arraycopy(mbusDayRom, pos, LPYEAR, 0, LPYEAR.length);//2byte
                pos += LPYEAR.length;
                int year = DataUtil.getIntToBytes(LPYEAR);
                System.arraycopy(mbusDayRom, pos, LPMONTH, 0, LPMONTH.length);//1byte
                pos += LPMONTH.length;
                int month = DataUtil.getIntToBytes(LPMONTH);
                System.arraycopy(mbusDayRom, pos, LPDAY, 0, LPDAY.length);//1byte
                pos += LPDAY.length;
                int day = DataUtil.getIntToBytes(LPDAY);
                gmt=year  + (month < 10? "0"+month:""+month)+ (day < 10? "0"+day:""+day);//GMT from Sensor Rom
                timestamp=gmt+"000000";
                log.debug("GMT["+gmt+"]");

                //-------------------
                //Base Record(136 byte)
                //-------------------
                System.arraycopy(mbusDayRom, pos, BASERECORD, 0, BASERECORD.length);//baseRecord : 136 byte
                baseRecord = new BaseRecord(BASERECORD);
            	setPortNumber(baseRecord.getAddress());
                pos += BASERECORD.length;
                if(baseRecord.getStart1()!=0x68){
                	isCommError=true;
                }else{
                	isCommError=false;
                }


                if(baseRecord!=null && !isCommError()){
	                //-------------------
	                //LP Record Data(64 x 24 = 1536 byte)
	                //-------------------
                	log.debug("pos["+pos+"] mbusDayRom.length["+mbusDayRom.length+"] LPRECORDDATA.length["+LPRECORDDATA.length+"]");
	                System.arraycopy(mbusDayRom, pos, LPRECORDDATA, 0, LPRECORDDATA.length);//lpRecordData : 1536 byte
	            	pos += LPRECORDDATA.length;

	                int tempHour=0;
	                int tempMin=0;

	                String hhmm="";

	                pos = 0;
	                
	                if(currentList==null)
	                	currentList= new ArrayList<HMData>();
	                if(lpList==null)
	                	lpList= new ArrayList<HMData>();
	                if(dayList==null)
	                	dayList= new ArrayList<HMData>();
	                if(monthList==null)
	                	monthList= new ArrayList<HMData>();
	                	
	                for(int j=0;j<24*period;j++,tempMin += resolution){

	                	byte[] mbusOnePeriodRom = new byte[64];//MBus One Period Metering Log Data
	                	System.arraycopy(LPRECORDDATA, pos, mbusOnePeriodRom, 0, mbusOnePeriodRom.length);
	                	pos += mbusOnePeriodRom.length;
	                	lpRecordData=new LPRecordData(j, mbusOnePeriodRom,baseRecord.getControlInformation());

	                	//make hhmm
	                	if (tempMin == 60) {
	                        tempHour++;
	                        tempMin = 0;
	                    }
	                    hhmm = (tempHour < 10? "0"+tempHour:""+tempHour) + (tempMin < 10? "0"+tempMin:""+tempMin);

	                	//---------------------------
	                    //Setting current Data
	                    //---------------------------
	                    if(lpRecordData.getDataBlock() != null && !lpRecordData.getDataBlock()[0].isEmpty()){
		                    HMData currentHmData = new HMData();
		                    currentHmData.setKind("CURRENT");
		                    currentHmData.setDate(gmt);//yyyymmdd
		                    currentHmData.setTime(hhmm);//hhmm
		                    currentHmData.setChannelCnt(9);
		                    currentHmData.setCh(1,new Double(df.format(lpRecordData.getDataBlock()[8].getDataValue()*lpRecordData.getDataBlock()[8].getDataMultiplier())));//Record Read Energy
		                    currentHmData.setCh(2,new Double(df.format(lpRecordData.getDataBlock()[0].getDataValue()*lpRecordData.getDataBlock()[0].getDataMultiplier())));//Record Energy
		                    currentHmData.setCh(3,new Double(df.format(lpRecordData.getDataBlock()[9].getDataValue()*lpRecordData.getDataBlock()[9].getDataMultiplier())));//Record Read Water
		                    currentHmData.setCh(4,new Double(df.format(lpRecordData.getDataBlock()[1].getDataValue()*lpRecordData.getDataBlock()[1].getDataMultiplier())));//Record Water
		                    currentHmData.setCh(5,new Double(df.format(lpRecordData.getDataBlock()[3].getDataValue()*lpRecordData.getDataBlock()[3].getDataMultiplier())));//Record Forward Temperature
		                    currentHmData.setCh(6,new Double(df.format(lpRecordData.getDataBlock()[4].getDataValue()*lpRecordData.getDataBlock()[4].getDataMultiplier())));//Record Return Temperature
		                    currentHmData.setCh(7,new Double(df.format(lpRecordData.getDataBlock()[6].getDataValue()*lpRecordData.getDataBlock()[6].getDataMultiplier())));//Record Power
		                    currentHmData.setCh(8,new Double(df.format(lpRecordData.getDataBlock()[7].getDataValue()*lpRecordData.getDataBlock()[7].getDataMultiplier())));//Record Flow
		                    currentHmData.setCh(9,new Double(df.format(lpRecordData.getDataBlock()[5].getDataValue()*lpRecordData.getDataBlock()[5].getDataMultiplier())));//Record F-R Temperature
		                    currentHmData.setFlag(0);
		                    currentList.add(currentHmData);
		                    timestamp=gmt+hhmm+"00";
		                    isCommError=false;
		                    this.lpValue = currentList.get(currentList.size()-1).getCh()[1];
	                    }else{
	                    	log.debug("TIME["+gmt+hhmm+"] CURRENT IS EMPTY");
	                    }
	                    //---------------------------
	                    //Setting lp Data
	                    //---------------------------
	                    if(!lpRecordData.getDataBlock()[0].isEmpty()){
		                    HMData lpHmData = new HMData();
		                    lpHmData.setKind("LP");
		                    lpHmData.setDate(gmt);//yyyymmdd
		                    lpHmData.setTime(hhmm);//hhmm
		                    lpHmData.setChannelCnt(9);

		                    lpHmData.setCh(1,new Double(df.format(lpRecordData.getDataBlock()[8].getDataValue()*lpRecordData.getDataBlock()[8].getDataMultiplier())));//Record Read Energy
		                    lpHmData.setCh(2,new Double(df.format(lpRecordData.getDataBlock()[0].getDataValue()*lpRecordData.getDataBlock()[0].getDataMultiplier())));//Record Energy
		                    lpHmData.setCh(3,new Double(df.format(lpRecordData.getDataBlock()[9].getDataValue()*lpRecordData.getDataBlock()[9].getDataMultiplier())));//Record Read Water
		                    lpHmData.setCh(4,new Double(df.format(lpRecordData.getDataBlock()[1].getDataValue()*lpRecordData.getDataBlock()[1].getDataMultiplier())));//Record Water
		                    lpHmData.setCh(5,new Double(df.format(lpRecordData.getDataBlock()[3].getDataValue()*lpRecordData.getDataBlock()[3].getDataMultiplier())));//Record Forward Temperature
		                    lpHmData.setCh(6,new Double(df.format(lpRecordData.getDataBlock()[4].getDataValue()*lpRecordData.getDataBlock()[4].getDataMultiplier())));//Record Return Temperature
		                    lpHmData.setCh(7,new Double(df.format(lpRecordData.getDataBlock()[6].getDataValue()*lpRecordData.getDataBlock()[6].getDataMultiplier())));//Record Power
		                    lpHmData.setCh(8,new Double(df.format(lpRecordData.getDataBlock()[7].getDataValue()*lpRecordData.getDataBlock()[7].getDataMultiplier())));//Record Flow
		                    lpHmData.setCh(9,new Double(df.format(lpRecordData.getDataBlock()[5].getDataValue()*lpRecordData.getDataBlock()[5].getDataMultiplier())));//Record F-R Temperature
		                    lpHmData.setFlag(0);

		                    lpList.add(lpHmData);
		                    timestamp=gmt+hhmm+"00";
		                    isCommError=false;
	                    }else{
	                    	log.debug("TIME["+gmt+hhmm+"] LP IS EMPTY");
	                    }
	                }

	                //---------------------------
	                //Setting Day Data
	                //---------------------------
	                if(!baseRecord.getDataBlocks().getDataBlock()[0].isEmpty()){
	                	HMData dayHmData = new HMData();
		                dayHmData.setKind("DAY");
		                dayHmData.setDate(gmt);//yyyymmdd
		            	dayHmData.setTime("0000");//hhmm
		                dayHmData.setChannelCnt(9);

		                dayHmData.setCh(1,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[8].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[8].getDataMultiplier())));//Record Read Energy
		                dayHmData.setCh(2,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[0].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[0].getDataMultiplier())));//Record Energy
		                dayHmData.setCh(3,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[9].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[9].getDataMultiplier())));//Record Read Water
		                dayHmData.setCh(4,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[1].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[1].getDataMultiplier())));//Record Water
		                dayHmData.setCh(5,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[3].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[3].getDataMultiplier())));//Record Forward Temperature
		                dayHmData.setCh(6,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[4].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[4].getDataMultiplier())));//Record Return Temperature
		                dayHmData.setCh(7,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[6].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[6].getDataMultiplier())));//Record Power
		                dayHmData.setCh(8,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[7].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[7].getDataMultiplier())));//Record Flow
		                dayHmData.setCh(9,new Double(df.format(baseRecord.getDataBlocks().getDataBlock()[5].getDataValue()*baseRecord.getDataBlocks().getDataBlock()[5].getDataMultiplier())));//Record F-R Temperature
		                dayHmData.setFlag(0);
		                dayList.add(dayHmData);
	                }else{
	                	log.debug("TIME["+gmt+"0000"+"] DAY LP IS EMPTY");
	                }


	                //---------------------------
	                //Setting Month Data
	                //---------------------------
	                if(dayList.get(dayList.size()-1)!=null && dayList.get(dayList.size()-1).getDate().substring(6, 8).equals("01")){
			            HMData monthHmData = new HMData();
			            monthHmData.setKind("MONTH");
			            monthHmData.setDate(dayList.get(dayList.size()-1).getDate());//yyyymmdd
			            monthHmData.setTime(dayList.get(dayList.size()-1).getTime());//hhmm
			            monthHmData.setChannelCnt(9);
			            monthHmData.setCh(1,dayList.get(dayList.size()-1).getCh()[0]);//Record Read Energy
			            monthHmData.setCh(2,dayList.get(dayList.size()-1).getCh()[1]);//Record Energy
			            monthHmData.setCh(3,dayList.get(dayList.size()-1).getCh()[2]);//Record Read Water
			            monthHmData.setCh(4,dayList.get(dayList.size()-1).getCh()[3]);//Record Water
			            monthHmData.setCh(5,dayList.get(dayList.size()-1).getCh()[4]);//Record Forward Temperature
			            monthHmData.setCh(6,dayList.get(dayList.size()-1).getCh()[5]);//Record Return Temperature
			            monthHmData.setCh(7,dayList.get(dayList.size()-1).getCh()[6]);//Record Power
			            monthHmData.setCh(8,dayList.get(dayList.size()-1).getCh()[7]);//Record Flow
			            monthHmData.setCh(9,dayList.get(dayList.size()-1).getCh()[8]);//Record F-R Temperature
			            monthHmData.setFlag(dayList.get(dayList.size()-1).getFlag());
			            monthList.add(monthHmData);
	            	}

	            }else{
	            	log.error("MBus Master/Slave Communication Error!");
	            }
                
                totalpos = (i+1)*lpLength;
            }
            
            if(currentList!=null)
            	currentData=currentList.toArray(new HMData[currentList.size()]);
            if(lpList!=null)
            	lpData=lpList.toArray(new HMData[lpList.size()]);
            if(dayList!=null)
            	dayData=dayList.toArray(new HMData[dayList.size()]);
            if(monthList!=null)
            	monthData=monthList.toArray(new HMData[monthList.size()]);
            
            startChar=baseRecord.getStart1();
            statusCode=baseRecord.getFixedDataHeader()!=null ? baseRecord.getFixedDataHeader().getStatusCode():1;
            meterId=baseRecord.getFixedDataHeader()!=null ? baseRecord.getFixedDataHeader().getIdentificationNumber():null;
            meterLog=baseRecord.getFixedDataHeader()!=null ? baseRecord.getFixedDataHeader().getStatusStr():null;
            portNumber=baseRecord.getAddress();

            log.debug("timeStamp: "+timestamp);
            log.debug("isCommError: "+isCommError);
            log.debug("LP length: "+(lpData!=null ? lpData.length:0));
            log.debug("CURRENT length: "+(currentData!=null ? currentData.length:0));
            log.debug("DAY length: "+(dayData!=null ? dayData.length:0));
            log.debug("MONTH length: "+(monthData!=null ? monthData.length:0));
        }
    }


    public static void main(String args[]){
    	Multical401COMPAT mbus = new Multical401COMPAT();
    	try {
    		mbus.parse(Hex.encode("0007DA071F68828268080472046810022D2C010CCC0000000C06223008000C14079426000C22421600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160F046810020000000000002800000003971700000000006467100000000000000000000000000033480300000024010103090060160C06223008000C14079426000C22431600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22441600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22451600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22461600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22471600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22481600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22491600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22501600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22511600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22521600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22531600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22541600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22551600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22561600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22571600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22581600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22591600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22601600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22611600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22621600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22631600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22641600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22651600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C01160C06223008000C14079426000C22661600000C59005001000C5D005001000C61000000000C2D000000000C3B000000004C06426107004C1457762300426C0116"));
    		//mbus.parse(Hex.encode("0007DA0A0768828268083372517212082D2C01047C0000000C06692600000C14651900000C22712600000C59006501000C5D402600000C61603801000C2D000000000C3B000000004C06000000004C1400000000426C00000F51721208000000000000260001000000000000000000000000000000000000000000000000001941040000241001010708009E16FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF"), 0);

    		System.out.println("---------------------------------------------------");
    		HMData[] currentList = mbus.getCurrentData();
    		for(int i=0;currentList!= null && i<currentList.length;i++) {
    			System.out.println(currentList[i].getDate() + currentList[i].getTime() + " " + currentList[i].getCh()[1]);
    		}
    		System.out.println("---------------------------------------------------");
    		HMData[] dayList = mbus.getDayData();
    		for(int i=0;dayList!= null && i<dayList.length;i++) {
    			System.out.println(dayList[i].getDate() + dayList[i].getTime() + " " + dayList[i].getCh()[1]);
    		}

		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * @return
     */
    public String getLPChannelMap(){
    	String res ="";
    	if(baseRecord == null) {
    		res+="ch1=Record Read Energy,";
    		res+="ch2=Record Energy,";
    		res+="ch3=Record Read Water,";
            res+="ch4=Record Water,";
            res+="ch5=Record Forward Temperature,";
            res+="ch6=Record Return Temperature,";
            res+="ch7=Record Power,";
            res+="ch8=Record Flow,";
            res+="ch9=Record F-R Temperature";
    	} else {
    		res+="ch1=Record Read Energy["+baseRecord.getDataBlocks().getDataBlock()[8].getDataUnit()+"],";
    		res+="ch2=Record Energy["+baseRecord.getDataBlocks().getDataBlock()[0].getDataUnit()+"],";
    		res+="ch3=Record Read Water["+baseRecord.getDataBlocks().getDataBlock()[9].getDataUnit()+"],";
            res+="ch4=Record Water["+baseRecord.getDataBlocks().getDataBlock()[1].getDataUnit()+"],";
            res+="ch5=Record Forward Temperature["+baseRecord.getDataBlocks().getDataBlock()[3].getDataUnit()+"],";
            res+="ch6=Record Return Temperature["+baseRecord.getDataBlocks().getDataBlock()[4].getDataUnit()+"],";
            res+="ch7=Record Power["+baseRecord.getDataBlocks().getDataBlock()[6].getDataUnit()+"],";
            res+="ch8=Record Flow["+baseRecord.getDataBlocks().getDataBlock()[7].getDataUnit()+"],";
            res+="ch9=Record F-R Temperature["+baseRecord.getDataBlocks().getDataBlock()[5].getDataUnit()+"]";
    	}
        return res;
    }

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "\n";

	    StringBuffer retValue = new StringBuffer();

	    retValue.append(this.getClass().getSimpleName()+" ( ")
	        .append(super.toString()).append(TAB)
	        .append("LPYEAR = ").append(this.LPYEAR).append(TAB)
	        .append("LPMONTH = ").append(this.LPMONTH).append(TAB)
	        .append("LPDAY = ").append(this.LPDAY).append(TAB)
	        .append("BASEPULSE = ").append(this.BASEPULSE).append(TAB)
	        .append("LP = ").append(this.LP).append(TAB)
	        .append("ERRORCODE = ").append(this.ERRORCODE).append(TAB)
	        .append("timestamp = ").append(this.timestamp).append(TAB)
	        .append("rawData = ").append(this.rawData).append(TAB)
	        .append("lp = ").append(this.lp).append(TAB)
	        .append("lpValue = ").append(this.lpValue).append(TAB)
	        .append("flag = ").append(this.flag).append(TAB)
	        .append("meterId = ").append(this.meterId).append(TAB)
	        .append("period = ").append(this.period).append(TAB)
	        .append("errorCode = ").append(this.errorCode).append(TAB)
	        .append("currentData = ").append(this.currentData).append(TAB)
	        .append("dayData = ").append(this.dayData).append(TAB)
	        .append("monthData = ").append(this.monthData).append(TAB)
	        .append("baseRecord = ").append(this.baseRecord).append(TAB)
	        .append(" )");

	    return retValue.toString();
	}
}