package com.aimir.dao.system.impl;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.ContractCapacityDao;
import com.aimir.model.system.ContractCapacity;
import com.aimir.util.TimeUtil;

@Repository(value="contractcapacityDao")
public class ContractCapacityDaoImpl extends AbstractHibernateGenericDao<ContractCapacity, Integer> implements ContractCapacityDao{


	
	@Autowired
	protected ContractCapacityDaoImpl(SessionFactory sessionFactory) {
		super(ContractCapacity.class);
		super.setSessionFactory(sessionFactory);
	}
	
	@SuppressWarnings("unchecked")
	public List<ContractCapacity> getContractCapacityList(int page,
			int count) {
		int pageSize = count;
		Criteria criteria = getSession().createCriteria(ContractCapacity.class);
		criteria.addOrder(Order.desc("id")); // 나중에 입력된 최근 글부터 정렬
		criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	public List<ContractCapacity> getContractCapacityList() {
		
		Criteria criteria = getSession().createCriteria(ContractCapacity.class);
		criteria.addOrder(Order.desc("id")); // 나중에 입력된 최근 글부터 정렬
		
		return criteria.list();
	}
	
	
	// 계약전력 ComboBox setting
	@SuppressWarnings("unchecked")
	public List<Object> contractEnergyCombo () {
		
		StringBuffer sbQry = new StringBuffer();
		sbQry.append("\n SELECT CONTRACTCAPACITY_ID as ID,loc.name as NAME                                                                    ");
		sbQry.append("\n FROM CONTRACTCAPACITY cc INNER JOIN SUPPLYTYPELOCATION sl ON cc.ID = sl.CONTRACTCAPACITY_ID ");
		sbQry.append("\n                          INNER JOIN LOCATION loc ON sl.LOCATION_ID = loc.ID                             					");
		
		SQLQuery query = getSession().createSQLQuery(sbQry.toString());
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
	
	// Gauge Chart setting
	@SuppressWarnings("unchecked")
	public List<Object> contractEnergyPeakDemandGauge (Map<String, Object> condition) {
		
		@SuppressWarnings("unused")
        String today = TimeUtil.getCurrentTimeMilli(); //yyyymmddhhmmss
		Integer contractCapacityId = (Integer)condition.get("contractCapacityId");  
		
		StringBuffer sbQry = new StringBuffer()
		.append(" SELECT ID,								\n")
		.append(" 	  CAPACITY,             				\n")
		.append(" 	  THRESHOLD1,           				\n")
		.append(" 	  THRESHOLD2,           				\n")
		.append(" 	  THRESHOLD3            				\n")
		.append(" FROM CONTRACTCAPACITY cc  	\n")
		.append(" WHERE 1 = 1                				\n")
		.append(" AND cc.ID = :contractCapacityId  \n");
		
		SQLQuery query = getSession().createSQLQuery(sbQry.toString());
		
		query.setInteger("contractCapacityId", contractCapacityId );
		
		return query.list();
	}
	
	// Multiple chart setting
	@SuppressWarnings("unchecked")
	public List<Object> contractEnergyPeakDemand (Map<String, Object> condition) {
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHH");
		Calendar c = Calendar.getInstance();
		int durationHour = Integer.parseInt(String.valueOf(condition.get("durationHour")));
		c.set(Calendar.HOUR_OF_DAY, c.get(Calendar.HOUR_OF_DAY) - durationHour);
		String startDate = dateFormat.format(c.getTime());

        String today = TimeUtil.getCurrentTimeMilli(); 
		String yyyymmddhh = today.substring(0, 10);
		
		String contractCapacityId = (String)condition.get("contractCapacityId");
		
		StringBuffer sbQry = new StringBuffer()
//		.append(" SELECT SUM(lp.VALUE_00),SUM(lp.VALUE_01),SUM(lp.VALUE_02),SUM(lp.VALUE_03),SUM(lp.VALUE_04),SUM(lp.VALUE_05),SUM(lp.VALUE_06),SUM(lp.VALUE_07),SUM(lp.VALUE_08),SUM(lp.VALUE_09),	\n")
//		.append("        SUM(lp.VALUE_10),SUM(lp.VALUE_11),SUM(lp.VALUE_12),SUM(lp.VALUE_13),SUM(lp.VALUE_14),SUM(lp.VALUE_15),SUM(lp.VALUE_16),SUM(lp.VALUE_17),SUM(lp.VALUE_18),SUM(lp.VALUE_19),	 	\n")
//		.append("        SUM(lp.VALUE_20),SUM(lp.VALUE_21),SUM(lp.VALUE_22),SUM(lp.VALUE_23),SUM(lp.VALUE_24),SUM(lp.VALUE_25),SUM(lp.VALUE_26),SUM(lp.VALUE_27),SUM(lp.VALUE_28),SUM(lp.VALUE_29),	 	\n")
//		.append("        SUM(lp.VALUE_30),SUM(lp.VALUE_31),SUM(lp.VALUE_32),SUM(lp.VALUE_33),SUM(lp.VALUE_34),SUM(lp.VALUE_35),SUM(lp.VALUE_36),SUM(lp.VALUE_37),SUM(lp.VALUE_38),SUM(lp.VALUE_39),	 	\n")
//		.append("        SUM(lp.VALUE_40),SUM(lp.VALUE_41),SUM(lp.VALUE_42),SUM(lp.VALUE_43),SUM(lp.VALUE_44),SUM(lp.VALUE_45),SUM(lp.VALUE_46),SUM(lp.VALUE_47),SUM(lp.VALUE_48),SUM(lp.VALUE_49),	 	\n")
//		.append("        SUM(lp.VALUE_50),SUM(lp.VALUE_51),SUM(lp.VALUE_52),SUM(lp.VALUE_53),SUM(lp.VALUE_54),SUM(lp.VALUE_55),SUM(lp.VALUE_56),SUM(lp.VALUE_57),SUM(lp.VALUE_58),SUM(lp.VALUE_59),	 	\n")
//		.append("        max(m.LP_INTERVAL),SUM(lp.VALUE)                                                                                                                                                       																					\n")
//		.append(" FROM CONTRACTCAPACITY cc INNER JOIN SUPPLYTYPELOCATION sl ON cc.ID = sl.CONTRACTCAPACITY_ID                                                                                      											\n")
//		.append("                          INNER JOIN LOCATION loc ON sl.LOCATION_ID = loc.ID                                                                                                      																		\n")
//		.append("                          INNER JOIN METER m ON loc.ID = m.LOCATION_ID                                                                                                            																		\n")
//		.append("                          LEFT OUTER JOIN LP_EM lp ON m.ID = lp.METER_ID AND lp.CHANNEL = 1                                                                                                                          																			\n")
//		.append("                                      AND lp.DST = 0                                                                                                                              																						\n")
//		.append("                                      AND lp.MDEV_TYPE = 'Meter'                                                                                                                  																													\n")
//		.append("                                      AND lp.YYYYMMDDHH = :startDate                                                                                                       																													\n")
//		.append(" WHERE 1=1                                                                                                                                             																												\n")
//		.append(" AND cc.ID = :contractCapacityId                                                                                                                                                                   		 														\n");


		.append(" SELECT SUM(lp.VALUE_00),SUM(lp.VALUE_01),SUM(lp.VALUE_02),SUM(lp.VALUE_03),SUM(lp.VALUE_04),SUM(lp.VALUE_05),SUM(lp.VALUE_06),SUM(lp.VALUE_07),SUM(lp.VALUE_08),SUM(lp.VALUE_09),	\n")
		.append("        SUM(lp.VALUE_10),SUM(lp.VALUE_11),SUM(lp.VALUE_12),SUM(lp.VALUE_13),SUM(lp.VALUE_14),SUM(lp.VALUE_15),SUM(lp.VALUE_16),SUM(lp.VALUE_17),SUM(lp.VALUE_18),SUM(lp.VALUE_19),	 	\n")
		.append("        SUM(lp.VALUE_20),SUM(lp.VALUE_21),SUM(lp.VALUE_22),SUM(lp.VALUE_23),SUM(lp.VALUE_24),SUM(lp.VALUE_25),SUM(lp.VALUE_26),SUM(lp.VALUE_27),SUM(lp.VALUE_28),SUM(lp.VALUE_29),	 	\n")
		.append("        SUM(lp.VALUE_30),SUM(lp.VALUE_31),SUM(lp.VALUE_32),SUM(lp.VALUE_33),SUM(lp.VALUE_34),SUM(lp.VALUE_35),SUM(lp.VALUE_36),SUM(lp.VALUE_37),SUM(lp.VALUE_38),SUM(lp.VALUE_39),	 	\n")
		.append("        SUM(lp.VALUE_40),SUM(lp.VALUE_41),SUM(lp.VALUE_42),SUM(lp.VALUE_43),SUM(lp.VALUE_44),SUM(lp.VALUE_45),SUM(lp.VALUE_46),SUM(lp.VALUE_47),SUM(lp.VALUE_48),SUM(lp.VALUE_49),	 	\n")
		.append("        SUM(lp.VALUE_50),SUM(lp.VALUE_51),SUM(lp.VALUE_52),SUM(lp.VALUE_53),SUM(lp.VALUE_54),SUM(lp.VALUE_55),SUM(lp.VALUE_56),SUM(lp.VALUE_57),SUM(lp.VALUE_58),SUM(lp.VALUE_59),	 	\n")
		.append("        SUM(lp.VALUE), lp.YYYYMMDDHH                                                                                                                                                   \n")
		.append(" FROM supplyTypeLocation s, meter m, lp_em lp                                                                                    		\n");
		
		/*
		if(condition.get("locType").equals("parent")) {
			sbQry.append("                     INNER JOIN LOCATION loc ON sl.LOCATION_ID = loc.ID                                                                                                      	\n")
			.append("                          INNER JOIN LP_EM lp ON loc.ID = lp.LOCATION_ID AND lp.CHANNEL = 1                                                                                        \n");
		} else {		
			sbQry.append("                     INNER JOIN LOCATION loc ON sl.LOCATION_ID = loc.PARENT_ID                                                                                                \n")
			.append("                          INNER JOIN LP_EM lp ON loc.ID = lp.LOCATION_ID AND lp.CHANNEL = 1                                                                                        \n");
		}
		sbQry.append("                                 AND lp.DST = 0                                                                                                                                     \n")		
		*/
		sbQry.append("WHERE s.location_id = m.location_id and m.meter='EnergyMeter' and m.mds_id = lp.mdev_id and lp.mdev_type='Meter' \n")
		.append(" AND lp.YYYYMMDDHH between :startDate and :endDate    \n")
		.append(" AND s.contractCapacity_id = :contractCapacityId      \n")
		.append(" AND lp.CHANNEL = 1  \n")
		.append(" GROUP BY lp.YYYYMMDDHH ");
		
		SQLQuery query = getSession().createSQLQuery(sbQry.toString());
		//startDate="2011041410";
		//yyyymmddhh ="2011041420";
		
//		System.out.println("contractCapacityId:"+contractCapacityId);
//		System.out.println("startDate:"+startDate);
//		System.out.println("endDate:"+yyyymmddhh);
		query.setInteger("contractCapacityId", Integer.parseInt(contractCapacityId));
		query.setString("startDate", startDate);
		query.setString("endDate", yyyymmddhh);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
    public List<Object> getThreshold(Map<String, Object> condition) {
		
		String threshold1 = (String)condition.get("threshold1");
		String threshold2 = (String)condition.get("threshold2");
		String threshold3 = (String)condition.get("threshold3");
		String contractCapacityId = (String)condition.get("contractCapacityId");
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("\n UPDATE CONTRACTCAPACITY		 ");
		sb.append("\n 	SET THRESHOLD1 = :threshold1,  ");
		sb.append("\n		  THRESHOLD2 = :threshold2,  ");
		sb.append("\n		  THRESHOLD3 = :threshold3   ");
		sb.append("\n	WHERE ID = :contractCapacityId	 ");
		
		SQLQuery query = getSession().createSQLQuery(sb.toString());
		
		query.setInteger("threshold1", Integer.parseInt( threshold1));
		query.setInteger("threshold2", Integer.parseInt( threshold2));
		query.setInteger("threshold3", Integer.parseInt( threshold3));
		query.setInteger("contractCapacityId", Integer.parseInt(contractCapacityId));
		
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}	
	
	
	// 위치별 계약전력 존재 여부 체크 
	@SuppressWarnings("unchecked")
	public List<Object> contractEnergyExistCheck(Integer serviceTypeId,Integer locationId) {
		
		StringBuffer sbQry = new StringBuffer();
		sbQry.append("\n SELECT CONTRACTCAPACITY_ID as id                                                                    ");
		sbQry.append("\n FROM CONTRACTCAPACITY cc INNER JOIN SUPPLYTYPELOCATION sl ON cc.ID = sl.CONTRACTCAPACITY_ID ");
		sbQry.append("\n                          INNER JOIN LOCATION loc ON sl.LOCATION_ID = loc.ID                             					");
		sbQry.append("\n                          INNER JOIN TARIFFTYPE tf ON cc.CONTRACTTYPECODE_ID = tf.SERVICETYPE_ID           					");
		sbQry.append("\n WHERE loc.ID =:locationId        					");
		sbQry.append("\n AND tf.SERVICETYPE_ID =:serviceTypeId        					");
		SQLQuery query = getSession().createSQLQuery(sbQry.toString());
		query.setInteger("locationId", locationId);
		query.setInteger("serviceTypeId", serviceTypeId);
		return query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).list();
	}
}
