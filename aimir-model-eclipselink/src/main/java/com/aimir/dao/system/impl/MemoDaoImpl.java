package com.aimir.dao.system.impl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.MemoDao;
import com.aimir.model.system.Memo;
import com.aimir.util.Condition;


@Repository(value = "memoDao")
public class MemoDaoImpl extends AbstractJpaDao<Memo, Integer> implements MemoDao {
	Log logger = LogFactory.getLog(MemoDaoImpl.class);
	
    public MemoDaoImpl() {
        super(Memo.class);
    }

    @Override
    public Class<Memo> getPersistentClass() {
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
    public List<Memo> getMemos(long userId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount(long userId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<Memo> searchMemos(String word) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteAll(long userId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Memo> getMemos(long userId, Integer startIndex,
            Integer maxIndex) {
        // TODO Auto-generated method stub
        return null;
    }

}
