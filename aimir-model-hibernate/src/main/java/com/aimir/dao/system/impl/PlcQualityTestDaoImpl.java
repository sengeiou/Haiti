package com.aimir.dao.system.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.PlcQualityTestDao;
import com.aimir.model.system.PlcQualityTest;
import com.aimir.util.StringUtil;

@Repository("PlcQualityTestDao")
public class PlcQualityTestDaoImpl extends AbstractHibernateGenericDao<PlcQualityTest, Integer> implements PlcQualityTestDao {

	@Autowired
	protected PlcQualityTestDaoImpl(SessionFactory sessionFactory) {
		super(PlcQualityTest.class);
		super.setSessionFactory(sessionFactory);
	}
	
	public List<Object> getPlcQualityResult(Map<String, Object> condition) {
		List<Object> returnData = new ArrayList<Object>();

		String startDate = StringUtil.nullToBlank(condition.get("startDate"));
		String endDate = StringUtil.nullToBlank(condition.get("endDate"));
		String zigName = StringUtil.nullToBlank(condition.get("zigName"));
		String searchType = (String)condition.get("searchType");
		String testResult = (String)condition.get("testResult");
		String limit = (String)condition.get("limit");
		String curPage = (String)condition.get("curPage");
		
        Criteria criteria = getSession().createCriteria(PlcQualityTest.class,"p");
        
        criteria.createAlias("plcQualityTestDetails", "pd",CriteriaSpecification.LEFT_JOIN);
        
        ProjectionList projectionList = Projections.projectionList();
        projectionList.add(Projections.property("p.id").as("zigId"));
        projectionList.add(Projections.property("p.zigName").as("zigName"));
        projectionList.add(Projections.property("p.completeDate").as("completeDate"));
        projectionList.add(Projections.property("p.successCount").as("successCnt"));
        projectionList.add(Projections.property("p.totalCount").as("totalCnt"));
        //projectionList.add(Projections.sum("p.totalCount-p.successCount").as("failCnt"));
        projectionList.add(Projections.groupProperty("p.id"));
        projectionList.add(Projections.groupProperty("p.zigName"));
        projectionList.add(Projections.groupProperty("p.completeDate"));
        projectionList.add(Projections.groupProperty("p.successCount"));
        projectionList.add(Projections.groupProperty("p.totalCount"));
        criteria.setProjection(projectionList);
        if("complete".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	criteria.add(Restrictions.between("pd.completeDate", startDate+"000000", endDate+"235959"));
        }

        if("start".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	criteria.add(Restrictions.between("pd.testStartDate", startDate+"000000", endDate+"235959"));
        }

        if("success".equals(testResult)) {
        	criteria.add(Restrictions.eq("pd.testResult", true));
        } else if ("fail".equals(testResult)) {
        	criteria.add(Restrictions.eq("pd.testResult", false));
        } else if ("unKnown".equals(testResult)) {
        	criteria.add(Restrictions.isNull("pd.testResult"));
        }
        
        if(!zigName.isEmpty()) {
        	criteria.add(Restrictions.eq("zigName", zigName));
        }
        if("latest".equals(searchType)) {
        	DetachedCriteria dc = DetachedCriteria.forClass(PlcQualityTest.class, "p");
            dc.createAlias("plcQualityTestDetails", "pd",CriteriaSpecification.LEFT_JOIN);
            
            if(!startDate.isEmpty() && !endDate.isEmpty()) {
            	dc.add(Restrictions.between("pd.completeDate", startDate+"000000", endDate+"235959"));
            }
            
            if("success".equals(testResult)) {
            	dc.add(Restrictions.eq("pd.testResult", true));
            } else if ("fail".equals(testResult)) {
            	dc.add(Restrictions.eq("pd.testResult", false));
            } else if ("unKnown".equals(testResult)) {
            	dc.add(Restrictions.isNull("pd.testResult"));
            }
            
            if(!zigName.isEmpty()) {
            	dc.add(Restrictions.eq("zigName", zigName));
            }
            dc.setProjection(Projections.projectionList().add(Projections.max("pd.testStartDate")));
            criteria.add(Subqueries.propertyEq("pd.testStartDate", dc));
        }
   
        Boolean isExcel = Boolean.parseBoolean(String.valueOf(condition.get("isExcel")));
        if (!isExcel) {
            // Paging
            int rowPerPage = 10;
            if (!limit.isEmpty()) {
                rowPerPage = Integer.parseInt(limit);
            }

            int firstIdx  = Integer.parseInt(curPage) * rowPerPage;
            
            criteria.setFirstResult(firstIdx);
            criteria.setMaxResults(rowPerPage);
        }

        Criteria countRoot = getSession().createCriteria(PlcQualityTest.class,"countRoot");
        DetachedCriteria criteriaCount = DetachedCriteria.forClass(PlcQualityTest.class,"p");
        
        criteriaCount.createAlias("plcQualityTestDetails", "pd",CriteriaSpecification.LEFT_JOIN);
        
        ProjectionList projectionCountList = Projections.projectionList();
        projectionCountList.add(Projections.groupProperty("p.id"));
        
        if("complete".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	criteriaCount.add(Restrictions.between("pd.completeDate", startDate+"000000", endDate+"235959"));
        }
        
        if("start".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
        	criteriaCount.add(Restrictions.between("pd.testStartDate", startDate+"000000", endDate+"235959"));
        }
        
        if("success".equals(testResult)) {
        	criteriaCount.add(Restrictions.eq("pd.testResult", true));
        } else if ("fail".equals(testResult)) {
        	criteriaCount.add(Restrictions.eq("pd.testResult", false));
        } else if ("unKnown".equals(testResult)) {
        	criteriaCount.add(Restrictions.isNull("pd.testResult"));
        }
        
        if(!zigName.isEmpty()) {
        	criteriaCount.add(Restrictions.eq("zigName", zigName));
        }
        
        if("latest".equals(searchType)) {
        	DetachedCriteria dc = DetachedCriteria.forClass(PlcQualityTest.class, "p");
            dc.createAlias("plcQualityTestDetails", "pd",CriteriaSpecification.LEFT_JOIN);
            
            if("complete".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
            	dc.add(Restrictions.between("pd.completeDate", startDate+"000000", endDate+"235959"));
            }
            if("start".equals(searchType) && !startDate.isEmpty() && !endDate.isEmpty()) {
            	dc.add(Restrictions.between("pd.completeDate", startDate+"000000", endDate+"235959"));
            }
            
            if("success".equals(testResult)) {
            	dc.add(Restrictions.eq("pd.testResult", true));
            } else if ("fail".equals(testResult)) {
            	dc.add(Restrictions.eq("pd.testResult", false));
            } else if ("unKnown".equals(testResult)) {
            	dc.add(Restrictions.isNull("pd.testResult"));
            }
            
            if(!zigName.isEmpty()) {
            	dc.add(Restrictions.eq("zigName", zigName));
            }
            dc.setProjection(Projections.projectionList().add(Projections.max("pd.testStartDate")));
            criteriaCount.add(Subqueries.propertyEq("pd.testStartDate", dc));
        }

        criteriaCount.setProjection(projectionCountList);
        countRoot.add(Subqueries.propertyIn("countRoot.id", criteriaCount));
        countRoot.setProjection(Projections.count("countRoot.id"));

        returnData.add(criteria.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP).list());
        returnData.add(((Number)countRoot.uniqueResult()).intValue());

		return returnData;
	}

	@Override
    @Transactional(readOnly=true, propagation=Propagation.REQUIRED)
	public PlcQualityTest getInfoByZig(String zigId) {
		PlcQualityTest m = findByCondition("zigName", zigId);
        return m;
	}
    
	
	public Integer getCount(Integer zigId) {
        StringBuffer countQuery = new StringBuffer();
        countQuery.append("\n SELECT COUNT(*) ");
        countQuery.append("\n FROM PlcQualityTestDetail pd ");
        countQuery.append("\n WHERE pd.zigId = :zigId");
        
        Query countQueryObj = getSession().createQuery(countQuery.toString());
        
        countQueryObj.setInteger("zigId", zigId);

	Number count = (Number)countQueryObj.uniqueResult();
        
        return count.intValue();
	}
}