package com.aimir.fep.meter.parser.actarisVCTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.vc.VCParameterLogData;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

public class VM_PARAMLOG
{
    private static Log log = LogFactory.getLog(VM_PARAMLOG.class);

    private byte[] data;
    
    private int LEN_DATE = 7;
    private int LEN_PARAM_CODE = 1;
    //private int LEN_RESOLVED1 = 1;
    private int LEN_OLD_VALUE = 8;
    private int LEN_UNCONVERTED_INDEX = 8;
    private int LEN_CONVERTED_INDEX = 8;

    //private int LEN_NEW_VALUE = 8;
    //private int LEN_RESOLVED2 = 2;
    private int LEN_TOTAL = 32;
    
    private int pw;
    
    private VCParameterLogData[] paramLogData = null;

    public VM_PARAMLOG(byte[] data, int pw) {
        this.data = data;
        this.pw = pw;
        try{
            parseData();
            log.debug(toString());
        }catch(Exception e){
            paramLogData = null;
            log.error(e,e);
        }
    }
    
    public void parseData() throws Exception {
        int dataCount = data.length / LEN_TOTAL;
        int pos = 0;
        if(data[pos] == 0x00 && 
                data[pos+1] == 0x00 && 
                data[pos+2] == 0x00 && 
                data[pos+3]== 0x00){
                 return;
             }
        paramLogData = new VCParameterLogData[dataCount];
        for(int i=0;i<dataCount;i++) {
            paramLogData[i] = new VCParameterLogData();
            paramLogData[i].setDate(DataFormat.getDateTime(DataUtil.select(data, pos, LEN_DATE)));
                        
            pos += LEN_DATE;
            paramLogData[i].setParamCode(DataUtil.getIntToByte(data[pos]));
            pos += LEN_PARAM_CODE;
            //pos += LEN_RESOLVED1;
            paramLogData[i].setOldValue(String.valueOf(DataUtil.select(data, pos, LEN_OLD_VALUE)));
            pos += LEN_OLD_VALUE;
            paramLogData[i].setUnconvertedIndex(DataUtil.getLongToBytes(DataUtil.select(data,pos, LEN_UNCONVERTED_INDEX))*Math.pow(10, pw));
            pos += LEN_UNCONVERTED_INDEX;
            paramLogData[i].setConvertedIndex(DataUtil.getLongToBytes(DataUtil.select(data,pos, LEN_CONVERTED_INDEX))*Math.pow(10, pw));
            pos += LEN_CONVERTED_INDEX;
            //pos += LEN_NEW_VALUE;
            //pos += LEN_RESOLVED2;
        }
    }

    public VCParameterLogData[] getParamLogData()
    {
        return paramLogData;
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if(paramLogData != null && paramLogData.length > 0){
            for(int i = 0; i < paramLogData.length; i++)
            {
                sb.append(paramLogData[i].toString());
            }
        }
        return sb.toString();
    }
}
