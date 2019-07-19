package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractJpaDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.model.system.Language;
import com.aimir.util.Condition;

@Repository(value = "languageDao")
public class LanguageDaoImpl extends AbstractJpaDao<Language, Integer> implements LanguageDao {

    Log logger = LogFactory.getLog(LanguageDaoImpl.class);
        
    public LanguageDaoImpl() {
        super(Language.class);
    }

    @Override
    public Class<Language> getPersistentClass() {
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
