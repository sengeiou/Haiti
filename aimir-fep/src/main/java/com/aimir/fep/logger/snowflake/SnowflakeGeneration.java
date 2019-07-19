package com.aimir.fep.logger.snowflake;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class SnowflakeGeneration {

	public static SnowflakeGeneration instance;
	private static Snowflake mSnowflake;
	private static Log log = LogFactory.getLog(SnowflakeGeneration.class);
	
	private static LoadingCache<String, Long> seqMap = CacheBuilder.newBuilder()
			.maximumSize(30000)
			.expireAfterAccess(3, TimeUnit.MINUTES)
			.build(new CacheLoader<String, Long> () {

				@Override
				public Long load(String key) throws Exception {
					if(mSnowflake == null)
						getInstance();
					
					return mSnowflake.nextId();
				}
			});
	
	public static SnowflakeGeneration getInstance() {
		if(instance == null) {
			synchronized (SnowflakeGeneration.class) {
				if(instance == null) {
					instance = new SnowflakeGeneration();
				}
			}
		}
		
		return instance;
	}
	
	public SnowflakeGeneration() {
		mSnowflake = new Snowflake(Long.parseLong("2") ,Long.parseLong("22"));
	}
	
		
	public static long getId() {
		try {
			if(mSnowflake != null) {
				String tname = Thread.currentThread().getName();
				
				if(seqMap.getIfPresent(tname) == null) {
					long seq = seqMap.getUnchecked(tname);
					Thread.currentThread().setName(String.valueOf(seq));
					seqMap.invalidate(tname);
					seqMap.put(String.valueOf(seq), seq);
					return seq;
				} else {
					return seqMap.get(tname);
				}
			}
			
			//if(instance.mSnowflake == null)
			//	getInstance();
		}catch(Exception e) {
			log.error(e,e);
		}
		
		return 0;
	}
	
	public static long getId(String tname) {
		try {
			return seqMap.getUnchecked(tname);
		}catch(Exception e) {
			log.error(e,e);
		}
		
		return 0;
	}
	
	public static void setSeq(long value) {
		try {
			String tname = Thread.currentThread().getName();
			setSeq(tname, value);	
		}catch(Exception e) {
			log.error(e,e);
		}
	}
	
	public static void setSeq(String key, long value) {
		try {
			seqMap.put(key, value);	
		}catch(Exception e) {
			log.error(e,e);
		}
	}
	
	public static void deleteId() {
		try {
			String tname = Thread.currentThread().getName();
			
			if(seqMap.getIfPresent(tname) != null) {
				seqMap.invalidate(tname);
			}
		}catch(Exception e) {
			log.error(e,e);
		}
	}
	
/*		
	public static long getId() {
		long myId = Thread.currentThread().getId();
		
		if(mSnowflake == null)
			getInstance();
		
		if(seqMap.containsKey(myId)) {
			return seqMap.get(myId);
		}
		else {
			//return Long.parseLong("1234567891234");
			long Id = nextId();
			seqMap.put(myId, Id);
			return Id;
		}
	}
	
	public static long nextId() {
		long myId = Thread.currentThread().getId();
		long seq;
		
		if(mSnowflake == null)
			getInstance();
		
		if(seqMap.containsKey(myId))
			seqMap.remove(myId);
		
		seq = mSnowflake.nextId();
		seqMap.put(myId, seq);
		
		return seq;
	}
	
	public static void deleteId() {
		long myId = Thread.currentThread().getId();
		seqMap.remove(myId);
	}
*/	
	
	

}
