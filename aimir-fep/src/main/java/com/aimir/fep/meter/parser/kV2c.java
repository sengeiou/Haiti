package com.aimir.fep.meter.parser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterTimeSyncData;
import com.aimir.fep.meter.data.MeteringFail;
import com.aimir.fep.meter.data.PowerQualityMonitor;
import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.SM110Table.UNIT_OF_MTR;
import com.aimir.fep.meter.parser.kV2cTable.AT055;
import com.aimir.fep.meter.parser.kV2cTable.BT055;
import com.aimir.fep.meter.parser.kV2cTable.MT067;
import com.aimir.fep.meter.parser.kV2cTable.MT070;
import com.aimir.fep.meter.parser.kV2cTable.MT072;
import com.aimir.fep.meter.parser.kV2cTable.MT075;
import com.aimir.fep.meter.parser.kV2cTable.MT078;
import com.aimir.fep.meter.parser.kV2cTable.MT110;
import com.aimir.fep.meter.parser.kV2cTable.ST001;
import com.aimir.fep.meter.parser.kV2cTable.ST003;
import com.aimir.fep.meter.parser.kV2cTable.ST005;
import com.aimir.fep.meter.parser.kV2cTable.ST012;
import com.aimir.fep.meter.parser.kV2cTable.ST021;
import com.aimir.fep.meter.parser.kV2cTable.ST022;
import com.aimir.fep.meter.parser.kV2cTable.ST023;
import com.aimir.fep.meter.parser.kV2cTable.ST025;
import com.aimir.fep.meter.parser.kV2cTable.ST055;
import com.aimir.fep.meter.parser.kV2cTable.ST061;
import com.aimir.fep.meter.parser.kV2cTable.ST062;
import com.aimir.fep.meter.parser.kV2cTable.ST063;
import com.aimir.fep.meter.parser.kV2cTable.ST064;
import com.aimir.fep.meter.parser.kV2cTable.ST071;
import com.aimir.fep.meter.parser.kV2cTable.ST076;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Util;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeLocaleUtil;
import com.aimir.util.TimeUtil;

/**
 * parsing kV2c Meter Data
 * implemented in Autrailia 
 *
 * @author Yeon Kyoung Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2006-12-14 12:00:15 +0900 $,
 */
public class kV2c extends MeterDataParser implements java.io.Serializable
{
	private static final long serialVersionUID = 7355564758870932770L;

	private static Log log = LogFactory.getLog(kV2c.class);
    
    private byte[] rawData = null;
    private int lpcount;
    private Double lp = null;
    private Double lpValue = null;
    private String meterId = null;
    private int flag = 0;

    private byte[] s001 = null;
    private byte[] s003 = null;
    private byte[] s005 = null;
    private byte[] s012 = null;
    private byte[] s021 = null;
    private byte[] s022 = null;
    private byte[] s023 = null;
    private byte[] s025 = null;
    private byte[] s055 = null;
    private byte[] s061 = null;
    private byte[] s062 = null;
    private byte[] s063 = null;
    private byte[] s064 = null;
    private byte[] s071 = null;
    private byte[] s076 = null;
    private byte[] m067 = null;
    private byte[] m070 = null;
    private byte[] m072 = null;
    private byte[] m075 = null;
    private byte[] m078 = null;
    private byte[] m110 = null;
    private byte[] b055 = null;
    private byte[] a055 = null;
    private byte[] n055 = null;
    private byte[] t001 = null;
    private byte[] t002 = null;
    
    private ST001 st001 = null;
    private ST003 st003 = null;
    private ST005 st005 = null;
    private ST012 st012 = null;
    private ST021 st021 = null;
    private ST022 st022 = null;
    private ST023 st023 = null;
    private ST025 st025 = null;
    private ST055 st055 = null;
    private ST061 st061 = null;
    private ST062 st062 = null;
    private ST063 st063 = null;
    private ST064 st064 = null;
    private ST071 st071 = null;
    private ST076 st076 = null;

    private MT067 mt067 = null;
    private MT070 mt070 = null;
    private MT072 mt072 = null;
    private MT075 mt075 = null;
    private MT078 mt078 = null;
    private MT110 mt110 = null;
        
    private NURI_T001 nuri_t001 = null;
    private NURI_T002 nuri_t002 = null;
    
    private BT055 bt055 = null;
    private AT055 at055 = null;
    private NT055 nt055 = null;
    
    /**
     * constructor
     */
    public kV2c()
    {
    }

    /**
     * get LP
     */
    public Double getLp()
    {
        return this.lp;
    }

    /**
     * get LP Value ( lp * pulse divider )
     */
    public Double getLpValue()
    {
        return this.lpValue;
    }   

    /**
     * get meter id
     * @return meter id
     */
    public String getMeterId()
    {
        return this.meterId;
    }

    /**
     * get raw Data
     */
    public byte[] getRawData()
    {
        return this.rawData;
    }

    public int getLength()
    {
        return this.rawData.length;
    }
    
    public int getLPCount(){
    	return this.lpcount;
    }

    /**
     * parseing Energy Meter Data of kV2c Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        int totlen = data.length;

        int offset = 0;
        while(offset < totlen){
            String tbName = new String(data,offset,4);
            offset += 4;
            int len = 0;
            len |= (data[offset++] & 0xff) << 8;
            len |= (data[offset++] & 0xff);
            byte[] b = new byte[len];
            System.arraycopy(data,offset,b,0,len);
            offset += len;

                  if(tbName.equals("S001"))
            {
                s001 = b;
                log.debug("[s001] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S003"))
            {
                s003 = b;
                log.debug("[s003] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S005"))
            {
                s005 = b;
                log.debug("[s005] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S012"))
            {
                s012 = b;
                log.debug("[s012] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S021"))
            {
                s021 = b;
                log.debug("[s021] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S022"))
            {
                s022 = b;
                log.debug("[s022] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S023"))
            {
                s023 = b;
                log.debug("[s023] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S025"))
            {
                s025 = b;
                log.debug("[s025] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S055"))
            {
                s055 = b;
                log.debug("[s055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S061"))
            {
                s061 = b;
                log.debug("[s061] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S062"))
            {
                s062 = b;
                log.debug("[s062] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S063"))
            {
                s063 = b;
                log.debug("[s063] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S064"))
            {
                s064 = b;
                log.debug("[s064] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S071"))
            {
                s071 = b;
                log.debug("[s071] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S076"))
            {
                s076 = b;
                log.debug("[s076] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M067"))
            {
                m067 = b;
                log.debug("[m067] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M070"))
            {
                m070 = b;
                log.debug("[m070] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M072"))
            {
                m072 = b;
                log.debug("[m072] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M075"))
            {
                m075 = b;
                log.debug("[m075] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M078"))
            {
                m078 = b;
                log.debug("[m078] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("M110"))
            {
                 m110 = b;
                 log.debug("[m110] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("B055"))
            {
                 b055 = b;
                 log.debug("[b055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("A055"))
            {
                 a055 = b;
                 log.debug("[a055] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("N055"))
            {
                 n055 = b;
                 log.debug("[n055] len=["+len+"] data=>"+Util.getHexString(b));
            }      
            else if(tbName.equals("T001"))
            {
                 t001 = b;
                 log.debug("[t001] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("T002"))
            {
                 t002 = b;
                 log.debug("[t002] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else 
            {
                log.debug("unknown table=>"+tbName);
            }
        }
        
        if(t001!=null){
            nuri_t001 = new NURI_T001(t001);
        }
        if(t002!=null){
            nuri_t002 = new NURI_T002(t002);
        }
        
        if(s001 != null){
            st001 = new ST001(s001);
            this.meterId = st001.getMSerial();
        }

        if(s005 != null)
            st005 = new ST005(s005);
        if(s012 != null)
            st012 = new ST012(s012);
        if(s021 != null)
            st021 = new ST021(s021);
        if(s022 != null)
            st022 = new ST022(s022);
        if(m070 != null)
            mt070 = new MT070(m070);
        if(m075 != null)
            mt075 = new MT075(m075);
        
        if(s023 != null){
            st023 = new ST023(s023,
                              st021.getNBR_TIERS(),
                              st021.getNBR_SUMMATIONS(),
                              st021.getNBR_DEMANDS(),
                              st021.getNBR_COINCIDENT(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070.getDISP_SCALAR(),
                              mt070.getDISP_MULTIPLIER());
        }

        if(s025 != null){
            st025 = new ST025(s025,
                              st021.getNBR_TIERS(),
                              st021.getNBR_SUMMATIONS(),
                              st021.getNBR_DEMANDS(),
                              st021.getNBR_COINCIDENT(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(),
                              mt070.getDISP_SCALAR(),
                              mt070.getDISP_MULTIPLIER());
        }

        if(s055 != null){
            st055 = new ST055(s055);
            this.meterTime = st055.getDateTime();
            if(s003 != null)
                st003 = new ST003(s003,st055.getDateTime());
        }

        if(s061 != null){
            st061 = new ST061(s061);
            log.debug("NBR_BLKS_SET1      ="+ st061.getNBR_BLKS_SET1());
            log.debug("NBR_BLKS_INTS_SET1 ="+ st061.getNBR_BLKS_INTS_SET1());
            log.debug("NBR_BLKS_CHNS_SET1 ="+ st061.getNBR_CHNS_SET1());
            log.debug("NBR_INT_TIME_SET1  =" + st061.getINT_TIME_SET1());
            if(s062!= null){
                st062 = new ST062(s062,st061.getNBR_CHNS_SET1());
            }
        }
        if(s063 != null)
            st063 = new ST063(s063);
        if(s071 != null)
            st071 = new ST071(s071);
        if(s076 != null){
            st076 = new ST076(s076);
        }

        if(s064 != null){
            st064 = new ST064(s064,
                              st061.getNBR_BLKS_SET1(),
                              st061.getNBR_BLKS_INTS_SET1(),
                              st061.getNBR_CHNS_SET1(),
                              st061.getINT_TIME_SET1(),
                              mt075.getVAH_SF(),
                              mt075.getVA_SF(), 
                              mt070.getDISP_SCALAR(),
                              mt070.getDISP_MULTIPLIER(),
                              st062);
        }

        if(m067 != null)
            mt067 = new MT067(m067);
        if(m072 != null)
            mt072 = new MT072(m072);
        if(m078 != null)
            mt078 = new MT078(m078);
        if(m110!=null){
            mt110 = new MT110(m110);
        }        

        if(s064 != null && st064 != null){
            this.lpcount = st064.getTotpulseCount();
        }
        
        if(b055 != null){
            bt055 = new BT055(b055);
            if(a055 != null)
                at055 = new AT055(a055);
        }
        if(n055 != null)
            nt055 = new NT055(n055);
        
        log.debug("kV2c Data Parse Finished :: DATA["+toString()+"]");
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

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
       
        sb.append("kV2c Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        //sb.append("(meterSerial=").append(meterSerial).append(")");
        sb.append("]\n");

        return sb.toString();
    }
    
    public String getLPChannelMap(){
        
        try{
            UNIT_OF_MTR unit_of_mtr = new UNIT_OF_MTR();
            if(s061 != null && s062!= null && s012 != null){
                int[] sel_select = st062.getLP_SEL_SET1();
                String[] uom_code = st012.getUOM_CODE(sel_select);            
                return unit_of_mtr.getChannelMap(uom_code);           
            }
        }catch(Exception e){
            log.warn(e);
        }
        return "";
    }
    
    public int getLPChannelCount(){
        try{
            if(s061 != null){
                return st061.getNBR_CHNS_SET1()*2+1;
            }
            else{
                return 5;//ch1,ch2,v1,v2,pf
            }
        } catch(Exception e){
        }
        return 5;//ch1,ch2,v1,v2,pf
    }

    public int getResolution(){

        try{
            if(s061 != null){
                return st061.getINT_TIME_SET1();
            }
            else{
                return Integer.parseInt(FMPProperty.getProperty("def.lpResolution"));
            }
        } catch(Exception e){
        }
        return 60;
    }
    
    public EventLogData[] getMeterStatusLog(){
        if(s003 != null){
            return st003.getEventLog();
        }else{
            return null;
        }
    }
    
    public String getMeterLog(){
        if(s003 != null){
            return st003.getMeterLog();
        }else{
            return "";
        }
    }
    
    public int getDstApplyOn() throws Exception
    {
        if(s055 != null && st055!= null){
            return st055.getDstApplyOn();
        }else{
            return 0;
        }

    }
    
    public int getDstSeasonOn() throws Exception
    {
        if(s055 != null && st055!= null){
            return st055.getDstSeasonOn();
        }else{
            return 0;
        }

    }
    
    public String getTimeDiff() throws Exception {
        
        if(s055 != null && n055 != null)
        {
            return (int)((nt055.getTime() - st055.getTime())/1000)+"";
        }
        else
        {
            return null;
        }
    }
    
    public MeterTimeSyncData getMeterTimeSync(){
        
        MeterTimeSyncData meterTimeSyncData = new MeterTimeSyncData();
        
        try{
            if(s005 != null && s055 != null && b055 != null && a055 != null){                    
                String meterTime = st055.getDateTime();
                String beforeTime = bt055.getDateTime();
                String afterTime = at055.getDateTime();
                int timeDiff = (int)((TimeUtil.getLongTime(afterTime)
                        - TimeUtil.getLongTime(beforeTime))/1000);

                meterTimeSyncData.setId(st001.getMSerial());
                meterTimeSyncData.setAtime(afterTime);
                meterTimeSyncData.setBtime(beforeTime);
                meterTimeSyncData.setCtime(meterTime);
                meterTimeSyncData.setEtime(meterTime);
                meterTimeSyncData.setMethod(1);//auto
                meterTimeSyncData.setResult(0);//success
                meterTimeSyncData.setTimediff(timeDiff);
                meterTimeSyncData.setUserID("AUTO Synchronized");
                return meterTimeSyncData;
            }
            else
            {
                return null;
            }
        }catch(Exception e){
            log.warn("get meter time sync log error: "+e.getMessage());
        }
        return null;
    }
    
    public MeterStatus getMeterStatusCode() {
        if (s003 != null) {
            return st003.getStatus();
        }
        else {
            return MeterStatus.Normal;
        }
    }

    @SuppressWarnings("unchecked")
    public LPData[] getLPData(){
        if(s064 != null)
    	    return st064.getLPData();
        else
            return null;
    }
    
    public TOU_BLOCK[] getPrevBilling(){
    	
    	if(s025 != null)
    		return st025.getTOU_BLOCK();
    	else
    		return null;    	
    }
    
    public TOU_BLOCK[] getCurrBilling(){
    	
    	if(s023 != null)
    		return st023.getTOU_BLOCK();
    	else
    		return null;    	
    }
    
    public Instrument[] getInstrument(){
    	
        Instrument[] insts = new Instrument[1];
        try {

            if(m072 != null && m110 != null)
            {
                insts[0] = new Instrument();
                insts[0].setVOL_A(mt110.getV_L_TO_L_FUND_ONLY()[0]);
                insts[0].setVOL_B(mt110.getV_L_TO_L_FUND_ONLY()[1]);
                insts[0].setVOL_C(mt110.getV_L_TO_L_FUND_ONLY()[2]);
                insts[0].setCURR_A(mt110.getCURR_FUND_ONLY()[0]);
                insts[0].setCURR_B(mt110.getCURR_FUND_ONLY()[1]);
                insts[0].setCURR_C(mt110.getCURR_FUND_ONLY()[2]);
                insts[0].setVOL_ANGLE_A(mt072.getVOLTAGE_ANGLE_PHA());
                insts[0].setVOL_ANGLE_B(mt072.getVOLTAGE_ANGLE_PHB());
                insts[0].setVOL_ANGLE_C(mt072.getVOLTAGE_ANGLE_PHC());
                insts[0].setCURR_ANGLE_A(mt072.getCURRENT_ANGLE_PHA());
                insts[0].setCURR_ANGLE_B(mt072.getCURRENT_ANGLE_PHB());
                insts[0].setCURR_ANGLE_C(mt072.getCURRENT_ANGLE_PHC());
                insts[0].setVOL_THD_A(mt110.getVTHD()[0]);
                insts[0].setVOL_THD_B(mt110.getVTHD()[1]);
                insts[0].setVOL_THD_C(mt110.getVTHD()[2]);
                insts[0].setCURR_THD_A(mt110.getITHD()[0]);
                insts[0].setCURR_THD_B(mt110.getITHD()[1]);
                insts[0].setCURR_THD_C(mt110.getITHD()[2]);
                insts[0].setTDD_A(mt110.getTDD()[0]);
                insts[0].setTDD_B(mt110.getTDD()[1]);
                insts[0].setTDD_C(mt110.getTDD()[2]);
                insts[0].setPF_TOTAL(mt110.getPOWER_FACTOR());
                insts[0].setDISTORTION_PF_A(mt110.getDISTORTION_PF()[0]);
                insts[0].setDISTORTION_PF_B(mt110.getDISTORTION_PF()[1]);
                insts[0].setDISTORTION_PF_C(mt110.getDISTORTION_PF()[2]);
                insts[0].setDISTORTION_PF_TOTAL(mt110.getDISTORTION_PF()[3]);
                insts[0].setKW_A(mt110.getKW_DMD_FUND_ONLY()[0]);
                insts[0].setKW_B(mt110.getKW_DMD_FUND_ONLY()[1]);
                insts[0].setKW_C(mt110.getKW_DMD_FUND_ONLY()[2]);
                insts[0].setKVAR_A(mt110.getKVAR_DMD_FUND_ONLY()[0]);
                insts[0].setKVAR_B(mt110.getKVAR_DMD_FUND_ONLY()[1]);
                insts[0].setKVAR_C(mt110.getKVAR_DMD_FUND_ONLY()[2]);
                insts[0].setKVA_A(mt110.getAPPARENT_KVA_DMD()[0]);
                insts[0].setKVA_B(mt110.getAPPARENT_KVA_DMD()[1]);
                insts[0].setKVA_C(mt110.getAPPARENT_KVA_DMD()[2]);
                insts[0].setDISTORTION_KVA_A(mt110.getDISTORTION_KVA_DMD()[0]);
                insts[0].setDISTORTION_KVA_B(mt110.getDISTORTION_KVA_DMD()[1]);
                insts[0].setDISTORTION_KVA_C(mt110.getDISTORTION_KVA_DMD()[2]);
                insts[0].setLINE_FREQUENCY(mt110.getFREQUENCY());

                return insts;
            }
            else
            {
                return null;
            }
        } catch(Exception e){
            log.warn("transform instrument error: "+e.getMessage());
        }
        return null;
    }
    
    public PowerQualityMonitor getPowerQuality(){
    	
    	if(m072 != null){    		
    		return mt072.getData();
    	}else{
    		return null;
    	}
    }
    
    public EventLogData[] getEventLog(){
    	if(s076 != null){
    		return st076.getEvent();
    	}else{
    		return null;
    	}
    }
    

    /**
     * get Data
     */
    @SuppressWarnings("unchecked")
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);
        TOU_BLOCK[] tou_block = null;
        LPData[] lplist = null;
        EventLogData[] evlog = null;
                
        DecimalFormat df3 = TimeLocaleUtil.getDecimalFormat(meter.getSupplier());
        
        try
        {
            tou_block = getCurrBilling();
            lplist = getLPData();
            evlog = getEventLog();
            
			res.put("<b>[Meter Configuration Data]</b>", "");
            if(st001 != null){
                res.put("Manufacturer",st001.getMANUFACTURER());
                res.put("Model",st001.getED_MODEL());
                res.put("Manufacturer Serial Number",st001.getMSerial());
                res.put("HW Version Number",st001.getHW_VERSION_NUMBER()+"");
                res.put("HW Revision Number",st001.getHW_REVISION_NUMBER()+"");
                res.put("FW Version Number",st001.getFW_VERSION_NUMBER()+"");
                res.put("FW Revision Number",st001.getFW_REVISION_NUMBER()+"");
            }
            if(st005 != null)
                res.put("Device Serial Number",st005.getMSerial());
            if(st003 != null)
                res.put("Meter Log",st003.getMeterLog());
            if(st055 != null){
                res.put("Meter Time",DateTimeUtil.getDateFromYYYYMMDDHHMMSS(st055.getDateTime()));
                res.put("DST Apply Flag",st055.getDstApplyOnName());
                res.put("DST Flag",st055.getDstSeasonOnName());
            }
            if(mt067 != null){
                res.put("Current Transform Ratio",df3.format(mt067.getCUR_TRANS_RATIO())+"");
                res.put("Voltage Transform Ratio",df3.format(mt067.getPOT_TRANS_RATIO())+"");
            }
            if(mt070 != null){
                res.put("Display Multiplier", df3.format(mt070.getDISP_MULTIPLIER())+"");
                res.put("Display Scalar", df3.format(mt070.getDISP_SCALAR())+"");
            }
            if(mt075 != null){
                res.put("[Scale Factor]", "");
                res.put("line-to-neutral voltages", df3.format(mt075.getI_SQR_HR_SF())+"");
                res.put("line-to-line voltages", df3.format(mt075.getV_SQR_HR_LL_SF())+"");
                res.put("Current", df3.format(mt075.getI_SQR_HR_SF())+"");
                res.put("Neutral current", df3.format(mt075.getI_N_SQR_SF())+"");
                res.put("Power Scale", df3.format(mt075.getVA_SF())+"");
                res.put("Energy Scale", df3.format(mt075.getVAH_SF())+"");
            }            
            if(mt078 != null){
                res.put("[Power Outage Information]", "");
                res.put("Last Power Outage Date",DateTimeUtil.getDateFromYYYYMMDDHHMMSS(mt078.getDT_LAST_POWER_OUTAGE()));
                res.put("Cummulative Power Outage(Seconds)", mt078.getCUM_POWER_OUTAGE_SECS()+"");
                res.put("Number Of Power Outages", mt078.getNBR_POWER_OUTAGES()+"");
            }          
            if(tou_block != null){
                res.put("[Current Billing Data]", "");
                res.put("Total Active Energy(kWh)"              ,df3.format(tou_block[0].getSummation(0)));
                res.put("Total Reactive Energy(kWh)"            ,df3.format(tou_block[0].getSummation(1)));
                res.put("Total Active Power Max.Demand(kW)"     ,df3.format(tou_block[0].getCurrDemand(0)));
                res.put("Total Active Power Max.Demand Time"    ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[0].getEventTime(0)));
                res.put("Total Reactive Power Max.Demand(kW)"   ,df3.format(tou_block[0].getCurrDemand(1)));
                res.put("Total Reactive Power Max.Demand Time"  ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[0].getEventTime(1)));
                res.put("Total Active Power Cum.Demand(kW)"     ,df3.format(tou_block[0].getCumDemand(0)));
                res.put("Total Reactive Power Cum.Demand(kW)"   ,df3.format(tou_block[0].getCumDemand(1)));
                res.put("Total Active Power Cont.Demand(kW)"    ,df3.format(tou_block[0].getCoincident(0)));
                res.put("Total Reactive Power Cont.Demand(kW)"  ,df3.format(tou_block[0].getCoincident(1)));
                    
                res.put("Rate A Active Energy(kWh)"             ,df3.format(tou_block[1].getSummation(0)));
                res.put("Rate A Reactive Energy(kWh)"           ,df3.format(tou_block[1].getSummation(1)));
                res.put("Rate A Active Power Max.Demand(kW)"    ,df3.format(tou_block[1].getCurrDemand(0)));
                res.put("Rate A Active Power Max.Demand Time"   ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[1].getEventTime(0)));
                res.put("Rate A Reactive Power Max.Demand(kW)"  ,df3.format(tou_block[1].getCurrDemand(1)));
                res.put("Rate A Reactive Power Max.Demand Time" ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[1].getEventTime(1)));
                res.put("Rate A Active Power Cum.Demand(kW)"    ,df3.format(tou_block[1].getCumDemand(0)));
                res.put("Rate A Reactive Power Cum.Demand(kW)"  ,df3.format(tou_block[1].getCumDemand(1)));
                res.put("Rate A Active Power Cont.Demand(kW)"   ,df3.format(tou_block[1].getCoincident(0)));
                res.put("Rate A Reactive Power Cont.Demand(kW)" ,df3.format(tou_block[1].getCoincident(1)));
                    
                res.put("Rate B Active Energy(kWh)"             ,df3.format(tou_block[2].getSummation(0)));
                res.put("Rate B Reactive Energy(kWh)"           ,df3.format(tou_block[2].getSummation(1)));
                res.put("Rate B Active Power Max.Demand(kW)"    ,df3.format(tou_block[2].getCurrDemand(0)));
                res.put("Rate B Active Power Max.Demand Time"   ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[2].getEventTime(0)));
                res.put("Rate B Reactive Power Max.Demand(kW)"  ,df3.format(tou_block[2].getCurrDemand(1)));
                res.put("Rate B Reactive Power Max.Demand Time" ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[2].getEventTime(1)));
                res.put("Rate B Active Power Cum.Demand(kW)"    ,df3.format(tou_block[2].getCumDemand(0)));
                res.put("Rate B Reactive Power Cum.Demand(kW)"  ,df3.format(tou_block[2].getCumDemand(1)));
                res.put("Rate B Active Power Cont.Demand(kW)"   ,df3.format(tou_block[2].getCoincident(0)));
                res.put("Rate B Reactive Power Cont.Demand(kW)" ,df3.format(tou_block[2].getCoincident(1)));
                    
                res.put("Rate C Active Energy(kWh)"             ,df3.format(tou_block[3].getSummation(0)));
                res.put("Rate C Reactive Energy(kWh)"           ,df3.format(tou_block[3].getSummation(1)));
                res.put("Rate C Active Power Max.Demand(kW)"    ,df3.format(tou_block[3].getCurrDemand(0)));
                res.put("Rate C Active Power Max.Demand Time"   ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[3].getEventTime(0)));
                res.put("Rate C Reactive Power Max.Demand(kW)"  ,df3.format(tou_block[3].getCurrDemand(1)));
                res.put("Rate C Reactive Power Max.Demand Time" ,DateTimeUtil.getDateFromYYYYMMDDHHMMSS((String)tou_block[3].getEventTime(1)));
                res.put("Rate C Active Power Cum.Demand(kW)"    ,df3.format(tou_block[3].getCumDemand(0)));
                res.put("Rate C Reactive Power Cum.Demand(kW)"  ,df3.format(tou_block[3].getCumDemand(1)));
                res.put("Rate C Active Power Cont.Demand(kW)"   ,df3.format(tou_block[3].getCoincident(0)));
                res.put("Rate C Reactive Power Cont.Demand(kW)" ,df3.format(tou_block[3].getCoincident(1)));                    
            }
            
            if(lplist != null && lplist.length > 0){
                res.put("[Load Profile Data(kWh)]", "");
                int nbr_chn = 2;//ch1,ch2
                if(st061 != null){
                    nbr_chn = st061.getNBR_CHNS_SET1();
                }
                ArrayList chartData0 = new ArrayList();//time chart
                ArrayList[] chartDatas = new ArrayList[nbr_chn]; //channel chart(ch1,ch2,...)
                for(int k = 0; k < nbr_chn ; k++){
                    chartDatas[k] = new ArrayList();                    
                }
                
                DecimalFormat decimalf=null;
                SimpleDateFormat datef14=null;
                ArrayList lpDataTime = new ArrayList();
                for(int i = 0; i < lplist.length; i++){
                    String datetime = (String)lplist[i].getDatetime();
                   
                    if(meter!=null && meter.getSupplier()!=null){
                        Supplier supplier = meter.getSupplier();
                        if(supplier !=null){
                            String lang = supplier.getLang().getCode_2letter();
                            String country = supplier.getCountry().getCode_2letter();
                            
                            decimalf = TimeLocaleUtil.getDecimalFormat(supplier);
                            datef14 = new SimpleDateFormat(TimeLocaleUtil.getDateFormat(14, lang, country));
                        }
                    	}else{
                        //locail 정보가 없을때는 기본 포멧을 사용한다.
                        decimalf = new DecimalFormat();
                        datef14 = new SimpleDateFormat();
                    }
                        String date;
                    	date = datef14.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(datetime+"00"));
                       
                    String tempDateTime = (String)lplist[i].getDatetime();
                    String val = "";
                    Double[] ch = lplist[i].getCh();
                    for(int k = 0; k < ch.length ; k++){
                        val += "<span style='margin-right: 40px;'>ch"+(k+1)+"="+df3.format(ch[k])+"</span>";
                    }
                    res.put("LP"+" "+date, val);

                    chartData0.add(tempDateTime.substring(6,8)
                                  +tempDateTime.substring(8,10)
                                  +tempDateTime.substring(10,12));
                    for(int k = 0; k < ch.length ; k++){                        
                        chartDatas[k].add(ch[k].doubleValue());
                    }
                    lpDataTime.add((String)lplist[i].getDatetime());
                }
                
                //res.put("chartData0", chartData0);
               //for(int k = 0; k < chartDatas.length ; k++){
                //    res.put("chartData"+(k+1), chartDatas[k]);
                //}
                //res.put("lpDataTime", lpDataTime);
                //res.put("chartDatas", chartDatas);
                res.put("[ChannelCount]", nbr_chn);
            }
            
            if(mt072 != null){
                res.put("[Line-Side Diagnostics/Power Quality Data Table]", "");                
                res.put("Current Angle Phase(A)",df3.format(mt072.getCURRENT_ANGLE_PHA()));
                res.put("Voltage Angle Phase(A)",df3.format(mt072.getVOLTAGE_ANGLE_PHA()));
                res.put("Current Angle Phase(B)",df3.format(mt072.getCURRENT_ANGLE_PHB()));
                res.put("Voltage Angle Phase(B)",df3.format(mt072.getVOLTAGE_ANGLE_PHB()));
                res.put("Current Angle Phase(C)",df3.format(mt072.getCURRENT_ANGLE_PHC()));
                res.put("Voltage Angle Phase(C)",df3.format(mt072.getVOLTAGE_ANGLE_PHC()));
                res.put("Current Magnitude Phase(A)",df3.format(mt072.getCURRENT_MAG_PHA())+"");
                res.put("Voltage Magnitude Phase(A)",df3.format(mt072.getVOLTAGE_MAG_PHA())+"");
                res.put("Current Magnitude Phase(B)",df3.format(mt072.getCURRENT_MAG_PHB())+"");
                res.put("Voltage Magnitude Phase(B)",df3.format(mt072.getVOLTAGE_MAG_PHB())+"");
                res.put("Current Magnitude Phase(C)",df3.format(mt072.getCURRENT_MAG_PHC())+"");
                res.put("Voltage Magnitude Phase(C)",df3.format(mt072.getVOLTAGE_MAG_PHC())+"");
                res.put("Distortion power factor",df3.format(mt072.getDU_PF())+"");                
                res.put("Polarity, Cross Phase, Reverse Energy(Counters)",df3.format(mt072.getDIAG1_COUNTERS())+"");
                res.put("Polarity, Cross Phase, Reverse Energy(Status)",df3.format(mt072.getDIAG1_STATUS_STR()));
                res.put("Condition Cleared(Counters)",df3.format(mt072.getDIAG2_COUNTERS())+"");
                res.put("Condition Cleared(Status)",mt072.getDIAG2_STATUS_STR());
                res.put("Voltage Imbalance(Counters)",df3.format(mt072.getDIAG3_COUNTERS())+"");
                res.put("Voltage Imbalance(Status)",mt072.getDIAG3_STATUS_STR());
                res.put("Phase Angle Alert(Counters)",df3.format(mt072.getDIAG4_COUNTERS())+"");
                res.put("Phase Angle Alert(Status)",mt072.getDIAG4_STATUS_STR());
                res.put("High Distortion Phase A/B/C(Counters)",df3.format(mt072.getDIAG5_PHA_COUNTERS())
                                                           +"/"+df3.format(mt072.getDIAG5_PHA_COUNTERS())
                                                           +"/"+df3.format(mt072.getDIAG5_PHA_COUNTERS()));
                res.put("High Distortion(Status)",df3.format(mt072.getDIAG5_STATUS_STR()));
                res.put("Under Voltage, Phase A(Counters)",df3.format(mt072.getDIAG6_COUNTERS())+"");
                res.put("Under Voltage, Phase A(Status)",mt072.getDIAG6_STATUS_STR());
                res.put("Over Voltage, Phase A(Counters)",df3.format(mt072.getDIAG7_COUNTERS())+"");
                res.put("Over Voltage, Phase A(Status)",mt072.getDIAG7_STATUS_STR());
                res.put("High Neutral Current(Counters)",df3.format(mt072.getDIAG8_COUNTERS())+"");
                res.put("High Neutral Current(Status)",mt072.getDIAG8_STATUS_STR());                        
            }
            
            if(evlog != null && evlog.length > 0){
                res.put("[Event Log]", "");
                int idx = 0;
                for(int i = 0; i < evlog.length; i++){
                    String datetime = evlog[i].getDate()+evlog[i].getTime();
                    if(!datetime.startsWith("0000") && !datetime.equals("")){
                        res.put("EV"+datetime+"00", evlog[i].getMsg());
                    }
                }
            }
            if(s012 != null && s061!= null && st062!= null){
                res.put("LP Channel Information", getLPChannelMap());
            }
        }
        catch (Exception e)
        {
            log.warn("Get Data Error=>",e);
        }

        return res;
    }
    
    public boolean isSavingLP(){

        try{
            if(s063 != null){
                int blkCnt = st063.getNBR_VALID_BLOCKS();
                if(blkCnt == 0){
                    return true;
                }
            }
        }catch(Exception e){
            log.warn("Get valid lp block count Error=>"+e.getMessage()); 
        }
        return false;
    }
    
    public MeteringFail getMeteringFail(){
        if(t002 != null){
            return nuri_t002.getMeteringFail();
        }else{
            return null;
        }
    }

	@Override
	public Double getMeteringValue() {
		// TODO Auto-generated method stub
		return null;
	}
}
