package com.aimir.service.system.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.NoticeDao;
import com.aimir.model.system.Notice;
import com.aimir.service.system.NoticeManager;

@WebService(endpointInterface = "com.aimir.service.system.NoticeManager")
@Service(value="noticeManager")
@Transactional
public class NoticeManagerImpl implements NoticeManager {

    @Autowired
    NoticeDao dao;

    public List<Notice> getNotice() {
        return dao.getAll();
    }
    
    public List<Notice> getNotices(int page, int count) {
        return dao.getNotices(page, count);
    }
    
    public List<Notice> sortList(String name, int page, int count, int sortCheck) {
        return dao.sortList(name, page, count, sortCheck);
    }
    
    public Notice getNotice(Integer id) {
    	Notice notice = dao.get(id);
        return notice;
    }
    
    public Long getCount() { //일반 jsp에서 사용하는 전체 갯수 세기
    	long count = dao.count();
    	return count;
    }
    
	public Map<String,String> getCount1() { //플렉스에서 사용하는 전체 갯수 세기
		Map<String,String> result = new HashMap<String,String>();        
        result.put("total", dao.count()+"");
		return result;
	}
	
	public Map<String,String> getSearchCount(String searchWord, String searchDetail,
    	String searchCategory, String startDate, String endDate) { //플렉스에서 사용하는 전체 갯수 세기
		Map<String,String> result = new HashMap<String,String>();       
        result.put("total", dao.countSearch(searchWord, searchDetail, searchCategory, startDate, endDate)+"");
		return result;
	}

    public void add(Notice notice) {
        dao.add(notice);
    }

    public void update(Notice notice) {
        dao.update(notice);
    }
    
    public void delete(Integer noticeId) {
        dao.deleteById(noticeId);
    }
    
    public List<Notice> searchNotice(String searchWord, String searchDetail,
    		String searchCategory, String startDate, String endDate, int page, int count) {

    	return dao.searchNotice(searchWord, searchDetail, searchCategory, startDate, endDate, page, count);
    }
    
    public void hitsPlus(Integer noticeId) {
    	Notice notice = dao.get(noticeId);
    	int hits = Integer.parseInt(notice.getHits());		//조회수 증가
    	hits++;
    	notice.setHits(Integer.toString(hits));
    	dao.update(notice);
    }
}
