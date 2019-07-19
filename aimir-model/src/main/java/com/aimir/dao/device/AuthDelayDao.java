// INSERT SP-121
package com.aimir.dao.device;


import com.aimir.dao.GenericDao;
import com.aimir.model.device.AuthDelay;

public interface AuthDelayDao extends GenericDao<AuthDelay, Long> {
	
	public AuthDelay getAuthDelay(String  ipaddress);

	public AuthDelay getAuthDelay(String  ipaddress, Integer limitcnt);
}
