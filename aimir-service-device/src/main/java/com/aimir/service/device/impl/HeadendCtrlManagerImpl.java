package com.aimir.service.device.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.dao.device.HeadendCtrlDao;
import com.aimir.model.device.HeadendCtrl;
import com.aimir.service.device.HeadendCtrlManager;
import com.aimir.util.StringUtil;

@WebService(endpointInterface = "com.aimir.service.device.HeadendCtrlManager")
@Service(value = "headendCtrlManager")
public class HeadendCtrlManagerImpl implements HeadendCtrlManager{
	
	@Autowired
	HeadendCtrlDao headendCtrlDao;

	@Transactional
	public void insertHeadendCtrl(Map<String, Object> map) {
		
		try {
			HeadendCtrl headendCtrl = new HeadendCtrl();
			headendCtrl.id.setCtrlId(map.get("ctrlId").toString());
			headendCtrl.id.setWriteDate(map.get("writeDate").toString());
			headendCtrl.setParam1(map.get("timeout").toString());
			headendCtrl.setParam2(map.get("retry").toString());
			headendCtrl.setStatus(0); //초기화 : 0
			
			headendCtrlDao.insert(headendCtrl);
			
		} catch (Exception e) {
			e.getStackTrace();
		}
		
	}
	
	public Map<String, Object> getHeadendCtrlCommandResultData(Map<String, Object> conditionMap) {
		
		List<HeadendCtrl> list = headendCtrlDao.getHeadendCtrlLastData(conditionMap);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();

        if (list == null || list.size() <= 0) {
            return resultMap;
        }

        HeadendCtrl headendCtrl = list.get(0);
        int status = headendCtrl.getStatus();
        String msg = null;

        switch(status) {
            case 2 :        // Return Value
                // make result message
                msg = makeHeadendCtrlCommandResultMsg(headendCtrl);
                resultMap.put("status", "complete");
                resultMap.put("msg", msg);
                break;
            case -1 :       // Error
                // make error message
                msg = makeHeadendCtrlCommandResultMsg(headendCtrl);
                resultMap.put("status", "error");
                resultMap.put("msg", msg);
                break;
            default :       // Progress
                // progress
                resultMap.put("status", "progress");
                break;
        }

        return resultMap;
		
	}
	
	/**
     * method name : makeHeadendCtrlCommandResultMsg<b/>
     * method Desc : HeadendCtrl Command 의 결과 메시지를 생성한다.
     *
     * @param meterCtrl
     * @return 결과메시지
     */
    private String makeHeadendCtrlCommandResultMsg(HeadendCtrl headendCtrl) {

        StringBuilder msg = new StringBuilder();
        String ctrlId = headendCtrl.id.getCtrlId();
        String result1 = StringUtil.nullToBlank(headendCtrl.getResult1());

        //msg.append(makeMeterCommandMsgPrefix(ctrlId));

        if (ctrlId.equals("ST")) {
        	if("0".equals(result1)) {
        		msg.append("Settings Result : Success");
        	} else {
        		msg.append("Error");
        	}
        } 

        return msg.toString();
    }
}
