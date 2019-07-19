package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.TagDao;
import com.aimir.model.system.Gadget;
import com.aimir.model.system.Tag;
import com.aimir.util.Condition;

@Repository(value = "tagDao")
public class TagDaoImpl extends AbstractJpaDao<Tag, Integer> implements TagDao {
		
    Log logger = LogFactory.getLog(TagDaoImpl.class);
    
    public TagDaoImpl() {
        super(Tag.class);
    }

    @Override
    public List<Gadget> searchGadgetByTag(String tag, int roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Tag> getTags(int roleId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<Tag> getPersistentClass() {
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
