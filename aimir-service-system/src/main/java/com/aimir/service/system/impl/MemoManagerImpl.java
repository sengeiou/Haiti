package com.aimir.service.system.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.flex.remoting.RemotingDestination;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.system.MemoDao;
import com.aimir.model.system.Memo;
import com.aimir.service.system.MemoManager;


@WebService(endpointInterface = "com.aimir.service.system.MemoManager")
@Service(value = "memoManager")
@Transactional
@RemotingDestination
public class MemoManagerImpl implements MemoManager {

	Log logger = LogFactory.getLog(MemoManagerImpl.class);
	
	@Autowired
	MemoDao dao;	
	
	public List<Memo> getMemos(long userId){
		return dao.getMemos(userId);
	}
	
	public int getCount(long userId){
		return dao.getCount(userId);
	}
	
	public void add(Memo memo){
		dao.add(memo);
	}
	
	public void delete(Integer Id){
		dao.deleteById(Id);
	}
	
	public void update(Memo memo){
		dao.update(memo);
	}
	
	public void deleteAll(long userId){
		dao.deleteAll(userId);
	}
	
	public List<Memo> searchMemos(String word) throws UnsupportedEncodingException{
		return dao.searchMemos(URLDecoder.decode(word, "UTF-8"));
	}
	
	public List<Memo> getMemos(long userId, Integer startIndex, Integer maxIndex){
		return dao.getMemos(userId, startIndex, maxIndex);
	}
}
