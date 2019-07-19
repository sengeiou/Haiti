package com.aimir.fep.meter.parser.vcTable;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.data.vc.VMCommonData;
import com.aimir.fep.meter.parser.VCParser;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.DataUtil;

public class VM_MONTHSTAT
{
    private static Log log = LogFactory.getLog(VM_MONTHSTAT.class);

    private byte[] data;

    private int _dataCntLen = 1;
    private int _yyyymmLen = 7;
    private int _cummCuvLen = 4;
    private int _cummCvLen = 4;
    private int _maxCuvLen = 4;
    private int _maxCuvTimeLen = 4;
    private int _maxCvLen = 4;
    private int _maxCvTimeLen = 4;

    private int pw = 0;

    private int dataCount = 0;

    private VMCommonData[] vmData;
    private List<VMCommonData> vmlist = null;
    
    public VM_MONTHSTAT(byte[] data, int pw) throws Exception{
        this.data = data;
        this.pw = pw;
        try {
            parseData();
            getVMData();
            log.debug(toString());
        } catch(Exception e){
            log.error("parsing error["+e.getMessage()+"]",e);
            throw e;
        }
    }
    
    public void parseData() throws Exception {
        int pos = 0;
        
        dataCount = DataUtil.getIntToBytes(DataUtil.select(data, pos, _dataCntLen));
        pos += _dataCntLen;
        if(dataCount!=0){
            vmlist = new ArrayList<VMCommonData>();
        }

        for(int i=0; i<dataCount;i++) {
            if(data[pos] == 0x00 && 
                    data[pos+1] == 0x00 && 
                    data[pos+2] == 0x00 && 
                    data[pos+3]== 0x00){
                     return;
                 }
            VMCommonData vmData = new VMCommonData();
            vmData.setVM_KIND(VCParser.VM_KIND_MONTHSTAT);

            vmData.setYyyymm(DataFormat.getDateTime(DataUtil.select(data, pos, _yyyymmLen)).substring(0,6));
            pos += _yyyymmLen;
            vmData.setCumm_cuv((double)DataFormat.hex2float32(DataUtil.select(data, pos, _cummCuvLen)));
            pos += _cummCuvLen;
            vmData.setCumm_cv((double) DataFormat.hex2float32(DataUtil.select(data, pos, _cummCvLen)));
            pos += _cummCvLen;
            vmData.setMax_cuv(DataFormat.hex2dec(DataUtil.select(data, pos, _maxCuvLen)) * Math.pow(10, pw));
            pos += _maxCuvLen;
            vmData.setMax_cuv_time(String.valueOf(DataUtil.getLongToBytes(DataUtil.select(data, pos, _maxCuvTimeLen)) * Math.pow(10, pw)));
            pos += _maxCuvTimeLen;
            vmData.setMax_cv(DataFormat.hex2dec(DataUtil.select(data, pos, _maxCvLen)) * Math.pow(10, pw));
            pos += _maxCvLen;
            vmData.setMax_cv_time(String.valueOf(DataUtil.getLongToBytes(DataUtil.select(data, pos, _maxCvTimeLen)) * Math.pow(10, pw)));
            pos += _maxCvTimeLen;
            vmlist.add(vmData);
        }
    }
    
    public VMCommonData[] getVMData() {
        if(vmlist != null && vmlist.size() > 0){
            vmData = null;
            Object[] obj = vmlist.toArray();
            
            vmData = new VMCommonData[obj.length];
            for(int i = 0; i < obj.length; i++){
                vmData[i] = (VMCommonData)obj[i];
            }
            return vmData;
        }
        else
        {
            return null;
        }
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        if(vmData != null && vmData.length > 0){
            for(int i = 0; i < vmData.length; i++)
            {
                sb.append(vmData[i].toString());
            }
        }
        return sb.toString();
    }
}
