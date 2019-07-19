/**
 * ReportParameterDataDaoImpl.java Copyright NuriTelecom Limited 2011
 */
package com.aimir.dao.system.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.constants.CommonConstants.ReportParameterType;
import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ReportParameterDataDao;
import com.aimir.model.system.ReportParameterData;

/**
 * ReportParameterDataDaoImpl.java Description 
 * <p>
 * <pre>
 * Date          Version     Author   Description
 * 2011. 10. 6.   v1.0       문동규   최초생성
 * </pre>
 */
@Repository(value = "reportParameterDataDao")
public class ReportParameterDataDaoImpl extends AbstractHibernateGenericDao<ReportParameterData, Integer> implements ReportParameterDataDao {

	@Autowired
	protected ReportParameterDataDaoImpl(SessionFactory sessionFactory) {
		super(ReportParameterData.class);
		super.setSessionFactory(sessionFactory);
	}

    /**
     * method name : getReportParameterDataBySchedule<b/>
     * method Desc : ReportSchedule Id 로 ReportParameterData 를 조회한다.
     *
     * @param scheduleId
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getReportParameterDataBySchedule(Integer scheduleId) {
        List<Map<String, Object>> result;
        StringBuilder sb = new StringBuilder();

//        sb.append("\nSELECT m.parameterType AS parameterType, ");
//        sb.append("\n       CASE WHEN m.parameterType.name = '").append(ReportParameterType.Location.toString()).append("' ");
//        sb.append("\n            THEN (SELECT l.name FROM Location l WHERE l.id = p.value) ");
//        sb.append("\n            WHEN m.parameterType.name = '").append(ReportParameterType.MeterType.toString()).append("' ");
//        sb.append("\n            THEN (SELECT d.name FROM Code d WHERE d.id = p.value) ");
//        sb.append("\n       ELSE p.value END AS parameterData ");
//        sb.append("\nFROM ReportParameterData p ");
//        sb.append("\n     LEFT JOIN p.reportParameter m ");
//        sb.append("\nWHERE 1=1 ");
//        sb.append("\nAND   p.reportSchedule.id = :scheduleId ");

        
        sb.append("\nSELECT m.parameterType AS parameterType, ");
        sb.append("\n       p.value AS parameterData ");
        sb.append("\nFROM ReportParameterData p ");
        sb.append("\n     LEFT JOIN p.reportParameter m ");
        sb.append("\nWHERE 1=1 ");
        sb.append("\nAND   p.reportSchedule.id = :scheduleId ");

        
        
        
        Query query = getSession().createQuery(sb.toString());
        query.setInteger("scheduleId", scheduleId);
//        query.setInteger("operatorId", operatorId);
//        query.setString("startDate", startDate + "000000");
//        query.setString("endDate", endDate + "235959");

//        if (!StringUtil.nullToBlank(reportId).isEmpty()) {
//            query.setInteger("reportId", reportId);
//        }
        result = query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();

        return result;
    }

}
