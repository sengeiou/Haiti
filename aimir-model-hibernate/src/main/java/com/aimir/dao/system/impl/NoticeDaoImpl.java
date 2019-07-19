package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.NoticeDao;
import com.aimir.model.system.Notice;

@Repository(value = "noticeDao")
public class NoticeDaoImpl extends AbstractHibernateGenericDao<Notice, Integer>
		implements NoticeDao {

	private static Log logger = LogFactory.getLog(NoticeDaoImpl.class);

	@Autowired
	protected NoticeDaoImpl(SessionFactory sessionFactory) {
		super(Notice.class);
		super.setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public List<Notice> getNotices(int page, int count) {
		int pageSize = count;
		Criteria criteria = getSession().createCriteria(Notice.class);
		criteria.addOrder(Order.desc("id")); // 나중에 입력된 최근 글부터 정렬
		criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();
	}

	public Long count() {
		Criteria criteria = getSession().createCriteria(Notice.class);
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).longValue();
	}
	
	public Long countSearch(String searchWord, String searchDetail, String searchCategory, String startDate, String endDate) {
		String word = searchWord;
		String detail = searchDetail;
		String category = searchCategory;
		String start = startDate;
		String end = endDate;
		Criteria criteria = getSession().createCriteria(Notice.class);

		// 카테고리 별로 검색을 한다. 전체 라는 스트링이 넘어오면 그냥 여기를 무시하고 검색하게 된다.
		if (category.equals("Notice")) {
			criteria.add(Restrictions.like("category", "Notice", MatchMode.ANYWHERE));
		} else if (category.equals("Alert")) {
			criteria.add(Restrictions.like("category", "Alert", MatchMode.ANYWHERE));
		} else if (category.equals("Information")) {
			criteria.add(Restrictions.like("category", "Information", MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.like(detail, word, MatchMode.ANYWHERE));

		if (start != null) {
			try {
				start = startDate + "000000";
				end = endDate + "235959";

				criteria.add(Restrictions.between("writeDate", start, end));
			} catch (Exception e) {
				logger.error(e);
			}
		}
		criteria.setProjection(Projections.rowCount());
		return ((Number) criteria.uniqueResult()).longValue();
	}

	@SuppressWarnings("unchecked")
	public List<Notice> searchNotice(String searchWord, String searchDetail, String searchCategory, String startDate, String endDate, int page, int count) {
		String word = searchWord;
		String detail = searchDetail;
		String category = searchCategory;
		String start = startDate;
		String end = endDate;
		int pageSize = count;		
		Criteria criteria = getSession().createCriteria(Notice.class);

		// 카테고리 별로 검색을 한다. 전체 라는 스트링이 넘어오면 그냥 여기를 무시하고 검색하게 된다.
		if (category.equals("Notice")) {
			criteria.add(Restrictions.like("category", "Notice", MatchMode.ANYWHERE));
		} else if (category.equals("Alert")) {
			criteria.add(Restrictions.like("category", "Alert", MatchMode.ANYWHERE));
		} else if (category.equals("Information")) {
			criteria.add(Restrictions.like("category", "Information", MatchMode.ANYWHERE));
		}
		criteria.add(Restrictions.like(detail, word, MatchMode.ANYWHERE));

		if (start != null) {
			try {
				start = startDate + "000000";
				end = endDate + "235959";

				criteria.add(Restrictions.between("writeDate", start, end));
			} catch (Exception e) {
				logger.error(e,e);
			}
		}		
		criteria.addOrder(Order.desc("id")); // 나중에 입력된 최근 글부터 정렬
		criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	public List<Notice> sortList(String name, int page, int count, int sortCheck) {
		int pageSize = count;
		Criteria criteria = getSession().createCriteria(Notice.class);
		if (sortCheck == 1) {
			criteria.addOrder(Order.desc(name));
		} else {
			criteria.addOrder(Order.asc(name));
		}
		criteria.setFirstResult((page - 1) * pageSize); // page는 1부터 2,3... 하지만
		// 이는 입력 되기를 0번 글부터 시작되고
		// pageSize단위로 곱해준다.
		criteria.setMaxResults(pageSize); // 한번에 불러올 리스트 크기를 정의
		return criteria.list();
	}
}
