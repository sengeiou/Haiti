package com.aimir.dao.system.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.LanguageDao;
import com.aimir.model.system.Language;

@Repository(value = "languageDao")
public class LanguageDaoImpl extends AbstractHibernateGenericDao<Language, Integer> implements LanguageDao {

    Log logger = LogFactory.getLog(LanguageDaoImpl.class);
        
    @Autowired
    protected LanguageDaoImpl(SessionFactory sessionFactory) {
        super(Language.class);
        super.setSessionFactory(sessionFactory);
    }
}
