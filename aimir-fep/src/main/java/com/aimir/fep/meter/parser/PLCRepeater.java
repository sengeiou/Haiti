package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSTable;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSNamjunTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.EnergyMeter;

/**
 * parsing PLC Repeater
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2015-07-08 09:59:15 +0900 $,
 */
public class PLCRepeater extends MeterDataParser implements ModemParser, java.io.Serializable 
{
	private static final long serialVersionUID = -7243426705275788933L;
	private static Log log = LogFactory.getLog(PLCRepeater.class);
    
    LinkedHashMap<String, Map<String, Object>> result = 
            new LinkedHashMap<String, Map<String, Object>>();
    /**
     * constructor
     */
    public PLCRepeater()
    {
    }

    /**
     * parse meter mesurement data
     * @param data 
     */
    public void parse(byte[] data) throws Exception
    {
        String obisCode = "";
        int clazz = 0;
        int attr = 0;

        int pos = 0;
        int len = 0;
        // DLMS Header OBIS(6), CLASS(1), ATTR(1), LENGTH(2)
        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
        byte[] OBIS = new byte[6];
        byte[] CLAZZ = new byte[2];
        byte[] ATTR = new byte[1];
        byte[] LEN = new byte[2];
        byte[] TAGDATA = null;
        
        DLMSTable dlms = null;
        while (pos < data.length) {
            dlms = new DLMSTable();
            System.arraycopy(data, pos, OBIS, 0, OBIS.length);
            pos += OBIS.length;
            obisCode = Hex.decode(OBIS);
            log.debug("OBIS[" + obisCode + "]");
            dlms.setObis(obisCode);
            
            System.arraycopy(data, pos, CLAZZ, 0, CLAZZ.length);
            pos += CLAZZ.length;
            clazz = DataUtil.getIntTo2Byte(CLAZZ);
            log.debug("CLASS[" + clazz + "]");
            dlms.setClazz(clazz);
            
            System.arraycopy(data, pos, ATTR, 0, ATTR.length);
            pos += ATTR.length;
            attr = DataUtil.getIntToBytes(ATTR);
            log.debug("ATTR[" + attr + "]");
            dlms.setAttr(attr);

            System.arraycopy(data, pos, LEN, 0, LEN.length);
            pos += LEN.length;
            len = DataUtil.getIntTo2Byte(LEN);
            log.debug("LENGTH[" + len + "]");
            dlms.setLength(len);

            TAGDATA = new byte[len];
            if (pos + TAGDATA.length <= data.length) {
            	System.arraycopy(data, pos, TAGDATA, 0, TAGDATA.length);
            	pos += TAGDATA.length;
            }
            else {
            	System.arraycopy(data, pos, TAGDATA, 0, data.length-pos);
            	pos += data.length-pos;
            }
            
            log.debug("TAGDATA=["+Hex.decode(TAGDATA)+"]");
            
            dlms.parseDlmsTag(TAGDATA);
            result.put(obisCode, dlms.getData());
        }
    }    
    
    public Double getLQISNRValue(){
    	
    	Object obj = null;    
    	if(result.get(OBIS.WEAK_LQI_VALUE.getCode()) != null){
        	obj = result.get(OBIS.WEAK_LQI_VALUE.getCode()).get(OBIS.WEAK_LQI_VALUE.getName());
        	if (obj != null) {   
        		log.debug("LQI SNR[" + obj + "]");   
        		if(obj instanceof Double){
        			return (Double)obj;
        		}
        	}
    	}

    	return null;
    }

    /**
     * get String
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        return sb.toString();
    }

    /**
     * get Data
     */
    @Override
    public LinkedHashMap<String, String> getData()
    {
        LinkedHashMap<String, String> res = new LinkedHashMap<String, String>(16,0.75f,false);
        
        return res;
    }

    public Double getMeteringValue()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMeterId()
    {
        // TODO Auto-generated method stub
        return "";
    }

    public int getFlag()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    public byte[] getRawData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void setFlag(int flag)
    {
        // TODO Auto-generated method stub
        
    }

    public ModemLPData[] getLpData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public int getPeriod()
    {
        // TODO Auto-generated method stub
        return 0;
    }

}
