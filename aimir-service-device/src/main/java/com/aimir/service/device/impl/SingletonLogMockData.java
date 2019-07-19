package com.aimir.service.device.impl;

import java.util.ArrayList;
import java.util.List;

public class SingletonLogMockData {


	private static SingletonLogMockData instance;	
	private List<MockData> mockDatas;
	
	private SingletonLogMockData() {
		
		mockDatas = new ArrayList<MockData>();
		
		for(int i = 0 ; i < 500 ; i++) {
			mockDatas.add(new MockData(i, Integer.toString(i), Integer.toString(i), Integer.toString(i), 
					Integer.toString(i), Integer.toString(i), Integer.toString(i), Integer.toString(i)));
		}		
	}
	
	public static SingletonLogMockData getInstance() {
		
		if(instance == null)
			instance = new SingletonLogMockData();
		
		return instance;
	}
	
	public List<MockData> getMockDatas(int startPage, int endPage) {

		List<MockData> rtnMockData = new ArrayList<MockData>();
		
		for(int i = startPage; i < endPage ; i++) {
			rtnMockData.add(mockDatas.get(i));
		}
		
		return rtnMockData;
	}
	
	public int getTotalRecordSize() {
		return mockDatas.size();
	}
	
	
}
