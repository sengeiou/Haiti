package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;


import com.aimir.model.mvm.DisplayChannel;

public interface ChannelConfigManager {
	@WebMethod
	@WebResult(name="DisplayChannelListbyId")
	public DisplayChannel getDisplayChannel(
			@WebParam(name ="chanelId")Integer channelId);
	
	public List<Object> getDisplayChannelList(Integer channelid, Integer supplierId);

	public Boolean addChannelConfig(Map<String, Object> condition);
	
	public Boolean updateChannelConfig(Map<String, Object> condition);
	
	public Boolean saveOrUpdateChannelConfig(Integer meterConfigId,List<Object> channelList);
	
	public Boolean deleteChannelConfig(Integer meterConfigId,List<Object> channelList);

	public List<Object> getExceptDisplayChannelList(Integer id, Integer supplierId);
}
