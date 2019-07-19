/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareTriggerDaoImpl
 * 작성일자/작성자 : 2011.01.13 박연경
 * @see 
 * 
 *
 * 펌웨어 배포 이력 DAO
 * 
 * ============================================================================
 * 수정 내역
 * NO  수정일자   수정자   수정내역
 * 
 * ============================================================================
 */
package com.aimir.dao.device.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.device.FirmwareTriggerDao;
import com.aimir.model.device.FirmwareTrigger;


@Repository(value = "firmwaretriggerDao")
public class FirmwareTriggerDaoImpl extends AbstractHibernateGenericDao<FirmwareTrigger, Long> implements FirmwareTriggerDao {
	private static Log logger = LogFactory.getLog(FirmwareTriggerDaoImpl.class);

	@Autowired
	protected FirmwareTriggerDaoImpl(SessionFactory sessionFactory) {
		super(FirmwareTrigger.class);
		super.setSessionFactory(sessionFactory);
	}
	
	/**
	 * trigger 테이블에 인서트(cmd 호출 후 작업)  
	 **/
    public void createTrigger(FirmwareTrigger firmwaretrigger)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"createTrigger()");
       	getSession().save(firmwaretrigger);
    }
    
	/**
	 * trigger 테이블에 업데이트(cmd 호출 후 작업)  
	 **/
    public void updateTrigger(FirmwareTrigger firmwaretrigger)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"updateTrigger()");
       	getSession().update(firmwaretrigger);
    }
    
	/**
	 * trigger 테이블조회
	 **/
    public FirmwareTrigger getFirmwareTrigger(String tr_id)throws Exception{
       	logger.debug(this.getClass().getName()+":"+"getFirmwareTrigger()");
       	
		Criteria criteria = getSession().createCriteria(FirmwareTrigger.class);
		criteria.add(Restrictions.eq("id", Long.parseLong(tr_id)));	
	    
		FirmwareTrigger firmwaretrigger =  (FirmwareTrigger) criteria.list().get(0);
        
        return firmwaretrigger; 

    }

}
