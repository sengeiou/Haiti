package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Memo;

public interface MemoDao extends GenericDao<Memo, Integer> {
	
	/**
     * method name : getMemos
     * method Desc : 사용자 아이디에 해당하는 메모 목록  리턴
     * 
	 * @param userId Memo.userId
	 * @return List of @see com.aimir.model.system.Memo
	 */
	public List<Memo> getMemos(long userId);
	
	/**
     * method name : getCount
     * method Desc : 사용자 아이디에 해당하는 메모 목록 카운트 리턴
     * 
	 * @param userId Memo.userId
	 * @return
	 */
	public int getCount(long userId);
	
	/**
     * method name : searchMemos
     * method Desc : cont와 like 조건에 부합하는 메모 목록 리턴
     * 
	 * @param word
	 * @return List of @see com.aimir.model.system.Memo
	 */
	public List<Memo> searchMemos(String word);
	
	/**
     * method name : deleteAll
     * method Desc : 사용자 아이디에 해당하는 메모 전체 삭제
     * 
	 * @param userId Memo.userId
	 */
	public void deleteAll(long userId);
	
	/**
     * method name : getMemos
     * method Desc : 페이지 단위로 메모 목록을 리턴
     * 
	 * @param userId Memo.userId
	 * @param startIndex 시작 범위
	 * @param maxIndex 최대 범위
	 * @return List of @see com.aimir.model.system.Memo
	 */
	public List<Memo> getMemos(long userId, Integer startIndex, Integer maxIndex);
}