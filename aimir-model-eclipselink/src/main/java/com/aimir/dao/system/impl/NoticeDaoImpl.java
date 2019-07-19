package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.NoticeDao;
import com.aimir.model.system.Notice;
import com.aimir.util.Condition;

@Repository(value = "noticeDao")
public class NoticeDaoImpl extends AbstractJpaDao<Notice, Integer>
		implements NoticeDao {

	private static Log logger = LogFactory.getLog(NoticeDaoImpl.class);

	public NoticeDaoImpl() {
		super(Notice.class);
	}

    @Override
    public List<Notice> getNotices(int page, int count) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long count() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long countSearch(String searchWord, String searchDetail,
            String searchCategory, String startDate, String endDate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Notice> searchNotice(String searchWord, String searchDetail,
            String searchCategory, String startDate, String endDate, int page,
            int count) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Notice> sortList(String name, int page, int count, int sortCheck) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Notice> getPersistentClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Object> getSumFieldByCondition(Set<Condition> conditions,
            String field, String... groupBy) {
        // TODO Auto-generated method stub
        return null;
    }
}
