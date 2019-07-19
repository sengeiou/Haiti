/**
 * Copyright Nuri Telecom Corp.
 * 파일명: FirmwareIssueDaoImpl
 * 작성일자/작성자 : 2016.09.13 elevas park
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.device.FirmwareIssueDao;
import com.aimir.model.device.FirmwareIssue;
import com.aimir.model.device.FirmwareIssuePk;
import com.aimir.util.Condition;

@Repository(value = "firmwareIssueDao")
public class FirmwareIssueDaoImpl extends AbstractJpaDao<FirmwareIssue, FirmwareIssuePk> implements FirmwareIssueDao {
	private static Log logger = LogFactory.getLog(FirmwareIssueDaoImpl.class);

	protected FirmwareIssueDaoImpl() {
		super(FirmwareIssue.class);
	}

    @Override
    public Class<FirmwareIssue> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public List<Object> getFirmwareIssueList(Map<String, Object> condition) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	
    @Transactional(readOnly=true, propagation=Propagation.REQUIRES_NEW)
    public List<FirmwareIssue> getFirmwareIssue(Set<Condition> set) {

        return findByConditions(set);
    }

}
