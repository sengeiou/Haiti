package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.DashboardGadgetDao;
import com.aimir.model.system.DashboardGadget;
import com.aimir.model.system.DashboardGadgetVO;

@Repository(value = "dashboardgadgetDao")
public class DashboardGadgetDaoImpl extends AbstractHibernateGenericDao<DashboardGadget, Integer> implements DashboardGadgetDao {

    Log logger = LogFactory.getLog(DashboardGadgetDaoImpl.class);
    
	@Autowired
	protected DashboardGadgetDaoImpl(SessionFactory sessionFactory) {
		super(DashboardGadget.class);
		super.setSessionFactory(sessionFactory);
	}

	public List<?> getGrid(Integer dashboardId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT dg.gridX, dg.gridY ");
		hqlBuf.append(" FROM DashboardGadget dg JOIN dg.dashboard d ");
		hqlBuf.append(" WHERE d.id = :dashboardId"); 
		hqlBuf.append(" ORDER BY dg.gridY, dg.gridX");

		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("dashboardId", dashboardId);

		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<DashboardGadgetVO> getGadgetsByDashboard(Integer dashboardId) {
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append(" SELECT dg.id as id, g.name as name, g.descr as descr, g.iconSrc as iconSrc, dg.gadgetId as gadgetId");
		hqlBuf.append(" FROM DashboardGadget dg JOIN dg.dashboard d ");
		hqlBuf.append("                         JOIN dg.gadget g ");
		hqlBuf.append(" WHERE d.id = :dashboardId"); 
		
		Query query = getSession().createQuery(hqlBuf.toString());

		query.setParameter("dashboardId", dashboardId);
		
		List result = query.list();
		
		List<DashboardGadgetVO> dashboardGadgetVOs = new ArrayList<DashboardGadgetVO>();
		DashboardGadgetVO dashboardGadgetVO = null;
		
		for (int i = 0; i < result.size(); i++) {
			dashboardGadgetVO = new DashboardGadgetVO();
			
			Object[] resultData = (Object[]) result.get(i);
			dashboardGadgetVO.setId(Integer.parseInt(resultData[0].toString()));
			dashboardGadgetVO.setName(resultData[1].toString());
			dashboardGadgetVO.setDescr(resultData[2].toString());
			dashboardGadgetVO.setIconSrc(resultData[3].toString());
			dashboardGadgetVO.setGadget_id(Integer.parseInt(resultData[4].toString()));

			dashboardGadgetVOs.add(dashboardGadgetVO);
		}
		return dashboardGadgetVOs;
	}
	
	@SuppressWarnings("unchecked")
	public List<DashboardGadget> getDashboardGadgetByDashboardIdGadgetId(int dashboardId, int gadgetId){
		Criteria criteria = getSession().createCriteria(DashboardGadget.class);
		criteria.add(Restrictions.eq("dashboard.id", dashboardId));
		criteria.add(Restrictions.eq("gadget.id", gadgetId));
		//criteria.addOrder(Order.asc("orderNo"));
		return criteria.list();
	}
}