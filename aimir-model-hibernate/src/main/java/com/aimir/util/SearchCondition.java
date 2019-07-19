package com.aimir.util;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.aimir.util.Condition.Restriction;

/**
 * 검색 조건을 찾아 Criteria로 변환해주는 클래스
 * @author YeonKyoung Park(goodjob)
 *
 */
public class SearchCondition {

	/**
	 * Hibernate Criteria Query 사용시 쿼리 기준을 Condition 객체의 내용에 맞게 변경.
	 *  
	 * @param criteria Criteria 객체
	 * @param condition Criteria 상태
	 * 
	 * @return Criteria 객체
	 */
	public static Criteria changeCriteria(Criteria criteria, Condition condition) {
		Restriction restriction = condition.getRestriction();
		if(restriction == null) {
			return criteria;
		}
		switch (restriction) {
			case ORDERBY: {
				criteria.addOrder(Order.asc(condition.getField()));						
				break;
			}
			case ORDERBYDESC: {
				criteria.addOrder(Order.desc(condition.getField()));		
				break;
			}
			case ALIAS: {
				criteria.createAlias(condition.getField(),(String)condition.getValue()[0]);					
				break;
			}
			case FIRST: {
				criteria.setFirstResult((Integer) condition.getValue()[0]);				
				break;
			}
			case MAX: {
				criteria.setMaxResults((Integer) condition.getValue()[0]);					
				break;
			}
			case INNER_JOIN: {
				criteria.createAlias(
					condition.getField(), (String)condition.getValue()[0], Criteria.INNER_JOIN
				);					
				break;
			}
			case LEFT_JOIN: {
				criteria.createAlias(
					condition.getField(), (String)condition.getValue()[0], Criteria.LEFT_JOIN
				);					
				break;
			}
			default: break;
			
		}
		return criteria;
	}
	
	public static Criterion getCriterion(Condition condition){
        Criterion criterion = null;
        
        if(condition.getRestriction() == Restriction.BETWEEN)
        	criterion = Restrictions.between(condition.getField(), condition.getValue()[0], condition.getValue()[1]);
        if(condition.getRestriction() == Restriction.EMPTTY)
        	criterion = Restrictions.isEmpty(condition.getField());
        if(condition.getRestriction() == Restriction.EQ)
        	criterion = Restrictions.eq(condition.getField(), condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.NEQ)
        	criterion = Restrictions.ne(condition.getField(), condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.GE)
        	criterion = Restrictions.ge(condition.getField(), condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.GT)
        	criterion = Restrictions.gt(condition.getField(), condition.getValue()[0]);     
        
        if(condition.getRestriction() == Restriction.LE)
        	criterion = Restrictions.le(condition.getField(), condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.LT)
        	criterion = Restrictions.lt(condition.getField(), condition.getValue()[0]);        
        
        if(condition.getRestriction() == Restriction.IN)
        	criterion = Restrictions.in(condition.getField(), condition.getValue());
        if(condition.getRestriction() == Restriction.LIKE)
        	criterion = Restrictions.ilike(condition.getField(), condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.NE)
        	criterion = Restrictions.ne(condition.getField(), condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.NOTEMPTY)
        	criterion = Restrictions.isNotEmpty(condition.getField());
        if(condition.getRestriction() == Restriction.NOTNULL)
        	criterion = Restrictions.isNotNull(condition.getField());
        if(condition.getRestriction() == Restriction.NULL)
        	criterion = Restrictions.isNull(condition.getField());
 
        return criterion;
	}
}
