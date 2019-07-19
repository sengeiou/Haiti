package com.aimir.dao.system.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Repository;

import com.aimir.dao.AbstractHibernateGenericDao;
import com.aimir.dao.system.MemoDao;
import com.aimir.model.system.Memo;


@Repository(value = "memoDao")
public class MemoDaoImpl extends AbstractHibernateGenericDao<Memo, Integer> implements MemoDao {
	Log logger = LogFactory.getLog(MemoDaoImpl.class);
	
	@Autowired
    protected MemoDaoImpl(SessionFactory sessionFactory) {
        super(Memo.class);
        super.setSessionFactory(sessionFactory);
    }
	
	//전체 메모리스트 조회
	@SuppressWarnings("unchecked")
	public List<Memo> getMemos(long userId) {
		Query query = getSession().createQuery("FROM Memo WHERE userId = " + userId + " order by Id desc");
		
		return query.list();
		
		// return (List<Memo>)getHibernateTemplate().find("FROM Memo WHERE userId = ? order by Id desc", userId);
	}
	
	//전체 메모 카운트
	public int getCount(long userId) {
		Query query = getSession().createQuery("SELECT COUNT(*) FROM Memo WHERE userID = " + userId);
		
		return DataAccessUtils.intResult(query.list());
		
		/*return DataAccessUtils.intResult(getHibernateTemplate().find(
				"SELECT COUNT(*) FROM Memo " +
				"WHERE userID = ?", userId));*/
	}

	
	//페이징이 적용된 리스트 조회
	@SuppressWarnings("unchecked")
	public List<Memo> getMemos(long userId, Integer startIndex, Integer maxIndex) {

		StringBuffer hqlBuf = new StringBuffer();

		hqlBuf.append("FROM Memo memo where memo.userId = :userId1 order by Id desc");
		Query hqlQuery = getSession().createQuery(hqlBuf.toString());
		hqlQuery.setParameter("userId1", userId);
		hqlQuery.setFirstResult(startIndex);
		hqlQuery.setMaxResults(maxIndex);
		
		List daomemos = hqlQuery.list();

		return daomemos;
	}

	//전체삭제
	public void deleteAll(long userId){
		StringBuffer hqlBuf = new StringBuffer();
		hqlBuf.append("DELETE Memo memo ");
		hqlBuf.append("WHERE userId = ? ");	
		
		Query query = getSession().createQuery(hqlBuf.toString());
		query.setParameter(1, userId);
		query.executeUpdate();
		// this.getHibernateTemplate().bulkUpdate(hqlBuf.toString(), userId );	
	}
	
	//검색
	@SuppressWarnings("unchecked")
	public List<Memo> searchMemos(String word) {
		Query query = getSession().createQuery("FROM Memo WHERE cont like :word order by Id desc");
		query.setString("word", "%" + word + "%");
		
		return query.list();
		
		// return (List<Memo>)getHibernateTemplate().find("FROM Memo WHERE cont like ? order by Id desc", "%"+word+"%");	
	}
}
