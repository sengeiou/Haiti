package com.aimir.fep.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.FMPProperty;

public class AimirThreadMapper {
	
	protected static Log log = LogFactory.getLog(AimirThreadMapper.class);
	
	private static AimirThreadMapper instance;
		
	//LP 정규화에 사용되는 프로시저에서 접근되는 사용자 테이블을 알기위한 용도
	//1부터 시작해서 n까지.. 중복되서는 안된다.
	
	// 최소 THREAD_COUNT + 5, RECOVERY_THREAD_COUNT + 5 만큼은 프로시저 테이블이 준비되어 있어야 한다.
	
	private final int MAPPER_PREFIX = Integer.parseInt(FMPProperty.getProperty("lp.normalization.server.index", "1"));
	private final int THREAD_COUNT = Integer.parseInt(FMPProperty.getProperty("lp.normalization.server.index.size", "20"));
	
	private final int RECOVERY_MAPPER_PRIFIX = Integer.parseInt(FMPProperty.getProperty("lp.normalization.server.recovery.index", "5000"));
	private final int RECOVERY_THREAD_COUNT = Integer.parseInt(FMPProperty.getProperty("lp.normalization.server.recovery.index.size", "10"));
	
	private Map<Long, String> activeMap = null;
	private ArrayList<String> idleList = null;
	
	private Map<Long, String> recoveryMap = null;
	private ArrayList<String> recoveryIdleList = null;
		
	public AimirThreadMapper() {
		init();
	}
	
	public static AimirThreadMapper getInstance() {
		if(instance == null) {
			synchronized (AimirThreadMapper.class) {
                if(instance == null)
                    instance = new AimirThreadMapper();
            }
		}
		
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	private void init() {
		if(activeMap == null) {
			activeMap = new HashMap<Long, String>();
			recoveryMap = new HashMap<Long, String>();
		}
		
		if(idleList == null) {
			idleList = new ArrayList<String>();
			recoveryIdleList = new ArrayList<String>(); 
			
			for(int i=1; i<=THREAD_COUNT; i++) {
				String idleId = String.format("%03d", (100 + ((MAPPER_PREFIX - 1) * THREAD_COUNT) + i));
				idleList.add(idleId);
			}
			
			for(int i=1; i<=RECOVERY_THREAD_COUNT; i++) {
				String idleId = String.format("%04d", (RECOVERY_MAPPER_PRIFIX + ((MAPPER_PREFIX - 1) * RECOVERY_MAPPER_PRIFIX) + i));
				recoveryIdleList.add(idleId);
			}
		}
	}
	
	public String getMapperId(long threadId) {
		String mapperId = null;
		
		try {
			if(activeMap.containsKey(threadId)) {
				mapperId = activeMap.get(threadId);
			} else {				
				mapperId = editIdleList(threadId, true);
				activeMap.put(threadId, mapperId);
			}
			
		}catch(Exception e) {
			log.error(e,e);
		}
		
		return mapperId;
	}
	
	public String getRecoveryMapperId(long threadId) {
		String mapperId = null;
		
		try {
			if(recoveryMap.containsKey(threadId)) {
				mapperId = recoveryMap.get(threadId);
			} else {				
				mapperId = editRecoveryIdleList(threadId, true);
				recoveryMap.put(threadId, mapperId);
			}
			
		}catch(Exception e) {
			log.error(e,e);
		}
		
		return mapperId;
	}
	
	public synchronized String editIdleList(long threadId, boolean isGet) {
		
		if(!isGet && activeMap.containsKey(threadId)) {			
			idleList.add(activeMap.get(threadId));
			activeMap.remove(threadId);
			return "";
		}else if(isGet && idleList.size() > 0) {
			String mapperId = idleList.get(0);
			idleList.remove(0);
			return mapperId;
		} 
		
		return "ERR";
	}
	
	public synchronized String editRecoveryIdleList(long threadId, boolean isGet) {
		
		if(!isGet && recoveryMap.containsKey(threadId)) {			
			recoveryIdleList.add(recoveryMap.get(threadId));
			recoveryMap.remove(threadId);
			return "";
		}else if(isGet && recoveryIdleList.size() > 0) {
			String mapperId = recoveryIdleList.get(0);
			recoveryIdleList.remove(0);
			return mapperId;
		} 
		
		return "ERR";
	}
	
	@SuppressWarnings("unchecked")
	public void deleteMapperId(long threadId) {
		try {
			editIdleList(threadId, false);
		}catch(Exception e) {
			log.error(e,e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void deleteRecoveryMapperId(long threadId) {
		try {
			editRecoveryIdleList(threadId, false);
		}catch(Exception e) {
			log.error(e,e);
		}
	}
}
