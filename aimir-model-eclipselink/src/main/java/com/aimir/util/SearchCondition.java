package com.aimir.util;

import java.util.StringTokenizer;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.aimir.util.Condition.Restriction;

/**
 * 검색 조건을 찾아 Criteria로 변환해주는 클래스
 * @author YeonKyoung Park(goodjob)
 *
 */
public class SearchCondition {

	/**
	 * Criteria Query 사용시 쿼리 기준을 Condition 객체의 내용에 맞게 변경.
	 *  
	 * @param criteria Criteria 객체
	 * @param condition Criteria 상태
	 * 
	 * @return Criteria 객체
	 */
	public static <T>CriteriaQuery changeCriteria(CriteriaBuilder cb, CriteriaQuery criteria, Condition condition, Root<T> root) {
	    StringTokenizer st = new StringTokenizer(condition.getField(), ".");
	    Path p1 = root;
	    while (st.hasMoreTokens()) {
	        p1 = p1.get(st.nextToken());
	    }
		
		Restriction restriction = condition.getRestriction();
		if(restriction == null) {
			return criteria;
		}
		switch (restriction) {
			case ORDERBY: {
				//criteria.orderBy(cb.asc(root.get(condition.getField())));						
				criteria.orderBy(cb.asc(p1));
				break;
			}
			case ORDERBYDESC: {
				//criteria.orderBy(cb.desc(root.get(condition.getField())));
				criteria.orderBy(cb.desc(p1));
				break;
			}
			case ALIAS: {
			    //criteria.select(root.get(condition.getField()).alias((String)condition.getValue()[0]));
				criteria.select(p1.alias((String)condition.getValue()[0]));
				break;
			}
			case INNER_JOIN: {
			    //root.get(condition.getField()).alias((String)condition.getValue()[0]);
				p1.alias((String)condition.getValue()[0]);
			    root.join((String)condition.getValue()[0], JoinType.INNER);
			    criteria.multiselect(root.getCompoundSelectionItems());
                break;
            }
            case LEFT_JOIN: {
                //root.get(condition.getField()).alias((String)condition.getValue()[0]);
            	p1.alias((String)condition.getValue()[0]);
                root.join((String)condition.getValue()[0], JoinType.LEFT);
                criteria.multiselect(root.getCompoundSelectionItems());                  
                break;
            }
			default: break;
			
		}
		return criteria;
	}
	
	public static TypedQuery changeCriteria(TypedQuery criteria, Condition condition) {
        Restriction restriction = condition.getRestriction();
        if(restriction == null) {
            return criteria;
        }
        switch (restriction) {
            case FIRST: {
                criteria.setFirstResult((Integer) condition.getValue()[0]);             
                break;
            }
            case MAX: {
                criteria.setMaxResults((Integer) condition.getValue()[0]);                  
                break;
            }
            default: break;
            
        }
        return criteria;
    }
	
	public static <T> Predicate getCriterion(CriteriaBuilder cb, Condition condition, Root<T> root){
	    Predicate predicate = null;
	    StringTokenizer st = new StringTokenizer(condition.getField(), ".");
	    Path p1 = root;
	    while (st.hasMoreTokens()) {
	        p1 = p1.get(st.nextToken());
	    }
	    
        if(condition.getRestriction() == Restriction.BETWEEN) {
            if (condition.getValue()[0] instanceof String) {
                predicate = cb.between(p1, (String)condition.getValue()[0], (String)condition.getValue()[1]);
            }
            else if (condition.getValue()[0] instanceof Integer) {
                predicate = cb.between(p1, (Integer)condition.getValue()[0], (Integer)condition.getValue()[1]);
            }
            else if (condition.getValue()[0] instanceof Double) {
                predicate = cb.between(p1, (Double)condition.getValue()[0], (Double)condition.getValue()[1]);
            }
            else if (condition.getValue()[0] instanceof Float) {
                predicate = cb.between(p1, (Float)condition.getValue()[0], (Float)condition.getValue()[1]);
            }
        }
        if(condition.getRestriction() == Restriction.EMPTTY)
            predicate = cb.isEmpty(p1);
        if(condition.getRestriction() == Restriction.EQ)
            predicate = cb.equal(p1, condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.NEQ)
            predicate = cb.notEqual(p1, condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.GE)
            predicate = cb.ge(p1, (Number)condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.GT)
            predicate = cb.gt(p1, (Number)condition.getValue()[0]);     
        
        if(condition.getRestriction() == Restriction.LE)
            predicate = cb.le(p1, (Number)condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.LT)
            predicate = cb.lt(p1, (Number)condition.getValue()[0]);        
        
        if(condition.getRestriction() == Restriction.IN)
        	//predicate = cb.in(p1.in(condition.getValue()));  // 만일 에러나면 아래 주석처리되어있는것으로 사용할것.
        	predicate = p1.in(condition.getValue());
        if(condition.getRestriction() == Restriction.LIKE)
            predicate = cb.like(p1, (String)condition.getValue()[0]);
        if(condition.getRestriction() == Restriction.NOTEMPTY)
            predicate = cb.isNotEmpty(p1);
        if(condition.getRestriction() == Restriction.NOTNULL)
            predicate = cb.isNotNull(p1); // 만일 에러나면 아래 주석처리되어있는것으로 사용할것.
        	//predicate = p1.isNotNull();
        if(condition.getRestriction() == Restriction.NULL)
            predicate = cb.isNull(p1);  // 만일 에러나면 아래 주석처리되어있는것으로 사용할것.
        	//predicate = p1.isNull();
        
        return predicate;
	}
}
