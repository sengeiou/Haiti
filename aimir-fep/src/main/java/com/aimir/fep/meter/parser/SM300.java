package com.aimir.fep.meter.parser;

import java.util.LinkedHashMap;

import com.aimir.fep.meter.data.EventLogData;
import com.aimir.fep.meter.data.Instrument;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.PowerQualityMonitor;

import com.aimir.fep.meter.data.TOU_BLOCK;
import com.aimir.fep.meter.parser.SM300Table.MT110;
import com.aimir.fep.meter.parser.SM300Table.NURI_CB;
import com.aimir.fep.meter.parser.SM300Table.NURI_EV;
import com.aimir.fep.meter.parser.SM300Table.NURI_IS;
import com.aimir.fep.meter.parser.SM300Table.NURI_LP;
import com.aimir.fep.meter.parser.SM300Table.NURI_MTR;
import com.aimir.fep.meter.parser.SM300Table.NURI_PB;
import com.aimir.fep.meter.parser.SM300Table.NURI_PQ;
import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * parsing SM300 Meter Data
 *
 * @author kaze
 */
public class SM300 extends MeterDataParser implements java.io.Serializable 
{

	private static final long serialVersionUID = -165806535260897603L;

    private static Log log = LogFactory.getLog(SM300.class);

    private byte[] rawData = null;
    private int lpcount;
    private Double meteringValue = null;
    private String meterId = null;
    private int flag = 0;

    //private byte[] mdm = null;
    private byte[] mtr = null;
    private byte[] pb = null;
    private byte[] cb = null;
    private byte[] LPD = null;
    private byte[] is = null;
    private byte[] ev = null;
    private byte[] pq = null;
    private byte[] er = null;

    //private NURI_MDM nuri_mdm = null;//modem information class
    private NURI_MTR nuri_mtr = null;//meter informatjion class
    private NURI_PB nuri_pb = null;//previous billing class
    private NURI_CB nuri_cb = null;//current billing class
    private NURI_LP nuri_lp = null;//load profile class
    private NURI_IS nuri_is = null;//instrumentation class
    private NURI_EV nuri_ev = null;//event log class
    private NURI_PQ nuri_pq = null;//power quality class
    private Integer nuri_er = null;//Error Code

    /**
     * constructor
     */
    public SM300()
    {
    }

    /**
     * get Metering Value
     */
    public Double getMeteringValue()
    {
        return this.meteringValue;
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
     * parseing Energy Meter Data of SM300 Meter
     * @param data stream of result command
     */
    public void parse(byte[] data) throws Exception
    {
        //int LEN_MDM_INFO = 30;
        int offset = 0;

        log.debug("[TOTAL] len=["+data.length+"] data=>"+Util.getHexString(data));
        /*
        if (data.length < 119) {

            if(data.length == 30)
            {
                byte[] b = new byte[LEN_MDM_INFO];
                System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
                mdm = b;
                log.debug("[NURI_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
                nuri_mdm = new NURI_MDM(mdm);
                log.debug(nuri_mdm);
            }else{
                log.error("[NURI_kV2c] Data total length[" + data.length + "] is invalid");
            }
            return;
        }

        byte[] b = new byte[LEN_MDM_INFO];
        System.arraycopy(data,offset,b,0,LEN_MDM_INFO);
        mdm = b;
        offset += LEN_MDM_INFO;
        log.debug("[NURI_MDM] len=["+LEN_MDM_INFO+"] data=>"+Util.getHexString(mdm));
        */
        String tbName = new String(data,offset,1);
        offset += 1;
        log.debug("[NURI_MDM] KIND_NAME = "+tbName);

        int len = 0;//msb ->lsb
        len = DataFormat.hex2unsigned16(
                  DataFormat.LSB2MSB(
                      DataFormat.select(data,offset,2)))-3;
        log.debug("[NURI_MDM] KIND_LENGTH = "+len);
        offset += 2;

        byte[] b = new byte[len];//전체 데이터에서 kind_name과 kind_lengt를 뺀 바이트 배열
        System.arraycopy(data,offset,b,0,len);
        offset += len;

        if(tbName.equals("I"))
        {
            mtr = b;
            log.debug("[NURI_MTR] len=["+len+"] data=>"+Util.getHexString(mtr));
        }


        int totlen = data.length;

        while(offset < totlen){
            tbName = new String(data,offset,1);
            offset += 1;
            len = 0;//msb ->lsb
            len = DataFormat.hex2unsigned16(
                      DataFormat.LSB2MSB(
                          DataFormat.select(data,offset,2)))-3;
            offset += 2;

            b = new byte[len];
            System.arraycopy(data,offset,b,0,len);
            offset += len;

            if(tbName.equals("P"))
            {
                pb = b;
                log.debug("[NURI_PB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("C"))
            {
                cb = b;
                log.debug("[NURI_CB] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("L"))
            {
                LPD = b;
                log.debug("[NURI_LP] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("S"))
            {
                is = b;
                log.debug("[NURI_IS] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("V"))
            {
                ev = b;
                log.debug("[NURI_EV] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("Q"))
            {
                pq = b;
                log.debug("[NURI_PQ] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else if(tbName.equals("E"))
            {
                er = b;
                log.debug("[NURI_ER] len=["+len+"] data=>"+Util.getHexString(b));
            }
            else
            {
                log.debug("unknown table=>"+tbName);
            }
        }

        /*
        nuri_mdm = new NURI_MDM(mdm);
        log.debug(nuri_mdm);
        */
        nuri_mtr = new NURI_MTR(mtr);
        this.meterTime = nuri_mtr.getDateTime();
        
        log.debug(nuri_mtr);


        if(pb != null){
            nuri_pb = new NURI_PB(pb,
                                  nuri_mtr.getST021().getNBR_TIERS(),
                                  nuri_mtr.getST021().getNBR_SUMMATIONS(),
                                  nuri_mtr.getST021().getNBR_DEMANDS(),
                                  nuri_mtr.getST021().getNBR_COINCIDENT(),
                                  nuri_mtr.getVAH_SF(),
                                  nuri_mtr.getVA_SF(),
                                  nuri_mtr.getDISP_SCALAR(),
                                  nuri_mtr.getDISP_MULTIPLIER());
        }
        if(cb!= null){
            nuri_cb = new NURI_CB(cb,
                                  nuri_mtr.getST021().getNBR_TIERS(),
                                  nuri_mtr.getST021().getNBR_SUMMATIONS(),
                                  nuri_mtr.getST021().getNBR_DEMANDS(),
                                  nuri_mtr.getST021().getNBR_COINCIDENT(),
                                  nuri_mtr.getVAH_SF(),
                                  nuri_mtr.getVA_SF(),
                                  nuri_mtr.getDISP_SCALAR(),
                                  nuri_mtr.getDISP_MULTIPLIER());
        }

        if(LPD != null){
            nuri_lp = new NURI_LP(LPD,nuri_mtr.getVAH_SF());
        }

        if(ev != null){
            nuri_ev = new NURI_EV(ev);
        }

        if(is != null){
            nuri_is = new NURI_IS(is);
        }

        if(pq != null){
            nuri_pq = new NURI_PQ(pq);
        }

        if(mtr != null){
            this.meterId = nuri_mtr.getMETER_ID();
        }

        log.debug("SM300 Data Parse Finished :: DATA["+toString()+"]");
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

        sb.append("SM300 Meter DATA[");
        sb.append("(meterId=").append(meterId).append("),");
        sb.append("]\n");

        return sb.toString();
    }

    public String getLPChannelMap(){
        /*
        try{
            if(LPD != null && s062!= null && s012 != null){
                int[] sel_select = st062.getLP_SEL_SET1();
                String[] uom_code = st012.getUOM_CODE(sel_select);
                return UNIT_OF_MTR.getChannelMap(uom_code);
            }
        }catch(Exception e){
            log.warn(e);
        }*/
        return "";
    }

    public int getLPChannelCount(){
        try{
            if(LPD != null){
                return nuri_lp.getNBR_CHNS_SET1()*2+1;
            }
            else{
                return 5;//ch1,v1,ch2,v2,pf
            }
        } catch(Exception e){
        }
        return 5;//ch1,v1,ch2,v2,pf
    }

    public int getResolution(){

        try{
            if(LPD!= null){
                return nuri_lp.getINT_TIME_SET1();
            }
            else{
                return Integer.parseInt(FMPProperty.getProperty("lp.resolution.default"));
            }
        } catch(Exception e){
        }
        return 60;
    }

    public String getMeterLog(){
        return "";
    }

    /*
    public int getMeterStatusCode() {
        return this.nuri_mdm.getERROR_STATUS();
    }
    */

    @SuppressWarnings("unchecked")
    public LPData[] getLPData(){

        try{
            if(LPD != null) {
				return nuri_lp.parse();
			}
			else {
				return null;
			}
        }catch(Exception e){
            log.error("lp parse error",e);
        }
        return null;
    }

    public TOU_BLOCK[] getPrevBilling(){

    	if(pb != null) {
			return nuri_pb.getTOU_BLOCK();
		}
		else {
			return null;
		}
    }

    public TOU_BLOCK[] getCurrBilling(){

    	if(cb != null) {
			return nuri_cb.getTOU_BLOCK();
		}
		else {
			return null;
		}
    }

    public Instrument[] getInstrument(){

        Instrument[] insts = new Instrument[1];
        try {

            if(is != null)
            {
                log.debug(nuri_is);
                MT110 mt110 = nuri_is.getMT110();

                insts[0] = new Instrument();
                insts[0].setVOL_A(mt110.getV_L_TO_L_FUND_ONLY()[0]);
                insts[0].setVOL_B(mt110.getV_L_TO_L_FUND_ONLY()[1]);
                insts[0].setVOL_C(mt110.getV_L_TO_L_FUND_ONLY()[2]);
                insts[0].setCURR_A(mt110.getCURR_FUND_ONLY()[0]);
                insts[0].setCURR_B(mt110.getCURR_FUND_ONLY()[1]);
                insts[0].setCURR_C(mt110.getCURR_FUND_ONLY()[2]);
                insts[0].setVOL_ANGLE_A(nuri_is.getVOLTAGE_ANGLE_PHA());
                insts[0].setVOL_ANGLE_B(nuri_is.getVOLTAGE_ANGLE_PHB());
                insts[0].setVOL_ANGLE_C(nuri_is.getVOLTAGE_ANGLE_PHC());
                insts[0].setCURR_ANGLE_A(nuri_is.getCURRENT_ANGLE_PHA());
                insts[0].setCURR_ANGLE_B(nuri_is.getCURRENT_ANGLE_PHB());
                insts[0].setCURR_ANGLE_C(nuri_is.getCURRENT_ANGLE_PHC());
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

        try{
            if(pq != null){
                return nuri_pq.getData();
            }else{
                return null;
            }
        }catch(Exception e){
            log.error("power quality parse error",e);
        }
        return null;
    }

    public EventLogData[] getEventLog(){
    	if(ev != null){
    		return nuri_ev.getEvent();
    	}else{
    		return null;
    	}
    }

    /*
    public HashMap<String, String> getMdmData(){

        HashMap<String, String> map = null;

        try{
            if(mdm != null){
                map = new HashMap<String, String>();
                map.put("mcuType","5");

                if(this.nuri_mdm.getFW_VER().startsWith("NG")){
                    map.put("protocolType","2");
                }else{
                    map.put("protocolType","1");
                }

                map.put("sysPhoneNumber", this.nuri_mdm.getPHONE_NUM());
                map.put("id", this.nuri_mdm.getPHONE_NUM());
                map.put("swVersion", this.nuri_mdm.getFW_VER());
                map.put("networkStatus", "1");
                map.put("csq", this.nuri_mdm.getCSQ_LEVEL()+"");
            }
        }catch(Exception e){
            log.warn(e,e);
        }
        return map;
    }
     */

    /**
     * get Data
     */
    @Override
    public LinkedHashMap getData()
    {
        LinkedHashMap res = new LinkedHashMap(16,0.75f,false);

        String val;

        return res;
    }
}
