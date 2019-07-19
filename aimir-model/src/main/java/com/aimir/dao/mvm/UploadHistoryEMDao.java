package com.aimir.dao.mvm;


import java.util.List;
import java.util.Map;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.UploadHistoryEM;

/**
 * Upload History EM 테이블 관리
 * @author SEJIN HAN
 *
 */

public interface UploadHistoryEMDao extends GenericDao<UploadHistoryEM, String> {
		
	/**
	 * 주어진 아이디에 해당하는 이력 조회
	 * @param _id : 이력 아이디(생성한 시점의 Time Stamp)
	 * @return
	 */
	public UploadHistoryEM getUploadHistory(String _id);
	
	/**
	 * 주어진 조건에 해당하는 이력 전체 조회
	 * @param condition : 미터아이디, 로그인아이디, 업로드 기간
	 * @return
	 */
	public List<UploadHistoryEM> getUploadHistory_List(Map<String,Object> condition);
	
}
