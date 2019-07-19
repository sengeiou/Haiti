package com.aimir.dao.system;


import com.aimir.dao.GenericDao;
import com.aimir.model.system.SmsInfo;

/**
 * SmsInfoDao.java Description
 *
 */
public interface SmsInfoDao extends GenericDao<SmsInfo, Integer>{
	
	public SmsInfo getSmsInfo(Integer contractId);
    /**
     * @MethodName updateSmsNumber
     * @param contractId, msg
     * @param msg
     * @Modified
     * @Description update시 smsNumber만 수정(only column update)
     */
    public void updateSmsNumber(int contractId, String msg);
    
    /**
     * @MethodName updateSMSPriod
     * @param contractId, msg, lastNotificationDate
     * @param msg
     * @param lastNotificationDate
     * @Modified
     * @Description update시 smsNumber만 수정(only column update)
     */
    public void updateSMSPriod(int contractId, String msg, String lastNotificationDate);
}