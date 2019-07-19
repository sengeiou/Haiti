package com.aimir.dao.system.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ModemConfigDao;
import com.aimir.model.system.ModemConfig;
import com.aimir.util.Condition;
import com.aimir.util.Condition.Restriction;

@Repository(value="modemconfigDao")
public class ModemConfigDaoImpl extends AbstractHibernateGenericDao<ModemConfig, Integer> implements ModemConfigDao{

	@Autowired
	protected ModemConfigDaoImpl(SessionFactory sessionFactory) {
		super(ModemConfig.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<ModemConfig> getDeviceConfigs(Integer configId) {
		Query query = getSession().createQuery("from ModemConfig c where c.deviceconfig.id = :configId");
		query.setInteger("configId", configId);
		
		return query.list();
		// return (List<ModemConfig>)getHibernateTemplate().find("from ModemConfig c where c.deviceconfig.id = ?", configId);
	}
		
	public ModemConfig getDeviceConfig(String swVersion,String swRevision, Integer connectedDeviceModel) {
		
		Set<Condition> set = new HashSet<Condition>();
		
		if(swVersion != null && !"".equals(swVersion)){
			Condition cdt1 = new Condition("swVersion", new Object[] { swVersion }, null, Restriction.EQ);
			set.add(cdt1);
		}
		
		if(swRevision != null && !"".equals(swRevision)){
			Condition cdt2 = new Condition("swRevision", new Object[] { swVersion }, null, Restriction.EQ);
			set.add(cdt2);
		}
		if(connectedDeviceModel != null){
			Condition cdt3 = new Condition("connectedDeviceModel", new Object[] { swVersion }, null, Restriction.EQ);
			set.add(cdt3);
		}
		
    	return getDeviceConfig(set);
	}
	
	public ModemConfig getDeviceConfig(Set<Condition> conditions) {
		
    	List<ModemConfig> list = findByConditions(conditions);
    	
    	if(list !=null && list.size()>0)
    		return (ModemConfig)list.get(0);
    	else return null; 
	}
	
	public Map<?,?> getParsers(){
		
		Set<Condition> set = new HashSet<Condition>();
		Condition cdt1 = new Condition("parserName", new Object[] {  }, null, Restriction.NOTNULL);
		set.add(cdt1);
		
		HashMap<String, HashMap<String,String>> map = new HashMap<String, HashMap<String,String>>();
		List<ModemConfig> list = findByConditions(set);
		ModemConfig modemConfig = null;
		if(list !=null && list.size()>0){
			Iterator<ModemConfig> it = list.iterator();
			while(it.hasNext()){
				modemConfig = it.next();
				HashMap<String, String> meterModelMap = new HashMap<String,String>();
				meterModelMap.put(modemConfig.getDeviceModel().toString(),  modemConfig.getParserName());
			}
		}
		return map;
	}

}
