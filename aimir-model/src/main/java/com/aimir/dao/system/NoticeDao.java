package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Notice;

public interface NoticeDao extends GenericDao<Notice, Integer> {
	
	/**
	 * method name : getNotices
	 * method Desc : 페이지 단위로 Notice 목록을 리턴
	 * 
	 * @param page : page number
	 * @param count :  data count
	 * @return List Of Notice @see com.aimir.model.system.Notice
	 */
	public List<Notice> getNotices(int page, int count);
	
	/**
	 * method name : count
	 * method Desc : Total Data Count
	 * 
	 * @return
	 */
	public Long count();
	
	/**
	 * method name : countSearch
	 * method Desc : 조회조건에 해당하는 데이터 카운트를 리턴한다.
	 * 
	 * @param searchWord : 검색어
	 * @param searchDetail : 상세 내용
	 * @param searchCategory : 카테고리
	 * @param startDate : 검색날짜
	 * @param endDate : 검색 종료날짜
	 * @return
	 */
	public Long countSearch(String searchWord, 
							String searchDetail, 
							String searchCategory, 
							String startDate, 
							String endDate);
	
	/**
	 * method name : searchNotice
	 * method Desc : 조회조건에 해당하는 데이터 목록을 리턴한다.
	 * 
	 * @param searchWord : 검색어
	 * @param searchDetail : 상세 내용
	 * @param searchCategory : 카테고리
	 * @param startDate : 검색날짜
	 * @param endDate : 검색 종료날짜
	 * @param page : 페이지번호
	 * @param count : 데이터 카운트
	 * @return List Of Notice @see com.aimir.model.system.Notice
	 */
	public List<Notice> searchNotice(String searchWord, 
									 String searchDetail, 
									 String searchCategory, 
									 String startDate, 
									 String endDate, 
									 int page, 
									 int count);
	
	/**
	 * method name : sortList
	 * method Desc : 조회조건에 해당하는 데이터 목록을 리턴한다.
	 * 
	 * @param name : Notice.name
	 * @param page : 페이지번호
	 * @param count : 데이터 카운트
	 * @param sortCheck : 1이면 desc 아니면 asc로 검색
	 * @return List Of Notice @see com.aimir.model.system.Notice
	 */
	public List<Notice> sortList(String name, 
								int page, 
								int count, 
								int sortCheck);    
}
