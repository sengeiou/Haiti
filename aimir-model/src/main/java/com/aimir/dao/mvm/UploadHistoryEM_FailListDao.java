package com.aimir.dao.mvm;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.mvm.UploadHistoryEM_FailList;

public interface UploadHistoryEM_FailListDao extends GenericDao<UploadHistoryEM_FailList, Integer> {
	
	/**
	 * 주어진 업로드이력 아이디에 해당하는 리스트 반환
	 * @param _uid
	 * @return
	 */
	public List<UploadHistoryEM_FailList> getUploadFailHistory(String _uid);
}
