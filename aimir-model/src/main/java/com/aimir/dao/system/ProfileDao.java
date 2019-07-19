package com.aimir.dao.system;

import java.util.List;

import com.aimir.dao.GenericDao;
import com.aimir.model.system.Profile;

public interface ProfileDao extends GenericDao<Profile, Integer> {

	/**
	 * method name : getProfileByUser
	 * method Desc : 사용자 아이디에 해당하는 프로파일 정보 목록을 리턴한다.
	 * 
	 * @param userId Profile.operator.id 
	 * @return List of Profile @see com.aimir.model.system.Profile
	 */
    public List<Profile> getProfileByUser(Integer userId);
    
    /**
	 * method name : deleteByUser
	 * method Desc : 사용자 아이디에 해당하는 프로파일 정보를 삭제한다.
	 * 
     * @param userId Profile.operator.id 
     */
    public void deleteByUser(Integer userId);
    
    /**
 	 * method name : checkProfileByUser
	 * method Desc : 사용자 아이디에 해당하는 프로파일이 있는지 체크한다.
	 * 
     * @param userId Profile.operator.id 
     * @return 목록이 하나라도 있으면 false를 리턴하고 없으면 true를 리턴한다.
     */
    public boolean checkProfileByUser(Integer userId);
    
    /**
	 * method name : getMeterEventProfileByUser
	 * method Desc : 사용자 아이디로 미터이벤트 id을 리턴한다.
	 * 
     * @param userId Profile.operator.id 
     * @return List of Profile @see com.aimir.model.system.Profile
     */
    public List<String> getMeterEventProfileByUser(Integer userId);
    
    /**
	 * method name : deleteMeterEventProfileByUser
	 * method Desc : 사용자 아이디에 해당하는 프로파일 정보를 삭제한다.
	 * 
     * @param userId Profile.operator.id 
     * @return
     */
    public int deleteMeterEventProfileByUser(Integer userId);
}
