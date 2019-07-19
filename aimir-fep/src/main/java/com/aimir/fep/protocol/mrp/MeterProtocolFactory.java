package com.aimir.fep.protocol.mrp;

import com.aimir.constants.CommonConstants.MeterModel;
import com.aimir.fep.protocol.mrp.protocol.A1830RLN_Handler;
import com.aimir.fep.protocol.mrp.protocol.A2RL_Handler;
import com.aimir.fep.protocol.mrp.protocol.DLMSKepco_Handler;
import com.aimir.fep.protocol.mrp.protocol.EDMI_Mk10_Handler;
import com.aimir.fep.protocol.mrp.protocol.EDMI_Mk6N_Handler;
import com.aimir.fep.protocol.mrp.protocol.KAMSTRUP382_Handler;
import com.aimir.fep.protocol.mrp.protocol.KAMSTRUP601_Handler;
import com.aimir.fep.protocol.mrp.protocol.KDH_Handler;
import com.aimir.fep.protocol.mrp.protocol.LGRW3410_Handler;
import com.aimir.fep.protocol.mrp.protocol.LK1210DRB_Handler;
import com.aimir.fep.protocol.mrp.protocol.LK3410CP_005_Handler;
import com.aimir.fep.protocol.mrp.protocol.MX2_Handler;
import com.aimir.fep.protocol.mrp.protocol.kV2c_Handler;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GSMMMIUClient factory
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class MeterProtocolFactory
{
    private static Log log = LogFactory.getLog(
            MeterProtocolFactory.class);

    /**
     * get MeterProtocolHandler
     *
     * @param target <code>mobileId</code> Meter Protocol Target
     * @return client <code>MeterProtocolHandler</code>
     * @throws Exception
     */
    public synchronized static MeterProtocolHandler getHandler(String mobileId, int meterModel, 
    											String groupNumber, String memberNumber, String mcuSwVersion) 
        throws Exception
    {
        MeterProtocolHandler handler = null;

        if(mobileId == null || mobileId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("Target mcuId is null"); 
        }
 
        if(meterModel == MeterModel.GE_KV2C.getCode())
        {
            handler = new kV2c_Handler();
        }
        else if(meterModel == MeterModel.LSIS_LK3410CP_005.getCode())
        {
            handler = new LK3410CP_005_Handler();
        }
        else if(meterModel == MeterModel.EDMI_Mk6N.getCode())
        {
            handler = new EDMI_Mk6N_Handler();
        }
        else if(meterModel == MeterModel.LSIS_LGRW3410.getCode())
        {
            handler = new LGRW3410_Handler();
        }
        else if(meterModel == MeterModel.LSIS_LK1210DRB_120.getCode() ||
                meterModel == MeterModel.LSIS_LK3410DRB_120.getCode())
        {
            handler = new LK1210DRB_Handler(meterModel);
        }
        else if(meterModel == MeterModel.WIZIT_KDH.getCode())
        {
            handler = new KDH_Handler();
        }
        else if(meterModel == MeterModel.KAMSTRUP_382.getCode()
        		|| meterModel == MeterModel.GE_SM300.getCode() 
        		|| meterModel == MeterModel.ELSTER_A1700.getCode()
        		|| meterModel == MeterModel.ELSTER_A1140.getCode())
        {
            handler = new KAMSTRUP382_Handler();
            handler.setModemNumber(mobileId);
        }
        else if(meterModel == MeterModel.ELSTER_A2R.getCode())
        {
            handler = new A2RL_Handler();
            handler.setModemNumber(mobileId);
        }
        else if(meterModel == MeterModel.KAMSTRUP_601.getCode())
        {
            handler = new KAMSTRUP601_Handler();
            handler.setModemNumber(mobileId);
            handler.setGroupNumber(groupNumber);
            handler.setMemberNumber(memberNumber);
            handler.setMcuSwVersion(mcuSwVersion);
        }
        else if(meterModel == MeterModel.ELSTER_A1RL.getCode()){
        	//TODO DEFINE HANDLER
        }
        else if(meterModel == MeterModel.ELSTER_A3RLNQ.getCode()){
        	//TODO DEFINE HANDLER
        }
        else if(meterModel == MeterModel.ELSTER_A1830RLNQ.getCode()){
        	handler = new A1830RLN_Handler();
        }
        else if(meterModel == MeterModel.EDMI_Mk10A.getCode() || meterModel == MeterModel.EDMI_Mk10E.getCode()){
        	handler = new EDMI_Mk10_Handler();
        }
        else if(meterModel == MeterModel.MITSUBISHI_MX2.getCode()){
        	handler = new MX2_Handler();
        }
        else if(meterModel == MeterModel.DLMSKEPCO.getCode()){
        	handler = new DLMSKepco_Handler();
        }
        
        else
        {
            throw new Exception("Target parser is null");
        }

        return handler;
    }

}
