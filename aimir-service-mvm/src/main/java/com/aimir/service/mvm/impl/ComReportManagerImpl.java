package com.aimir.service.mvm.impl;

import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterEventDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.mvm.LpEMDao;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterEventLog;
import com.aimir.model.mvm.LpEM;
import com.aimir.service.mvm.ComReportManager;
import com.aimir.util.Condition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(value = "ComReportManager")
@Transactional(readOnly=false)
public class ComReportManagerImpl implements ComReportManager{

    private static Log logger = LogFactory.getLog(ComReportManagerImpl.class);

    @Autowired
    MeterDao meterDao;

    @Autowired
    LpEMDao lpemDao;

    @Autowired
    MeterEventDao meDao;

    @Autowired
    MeterEventLogDao meLogDao;

    @Override
    public Map<String,Object> getValidMeteringRate(Map<String, Object> condition) {
        // 반환
        Map<String,Object> result = new HashMap<String,Object>();
        // 중간 메시지 반환
        Map<String,String> message = new HashMap<String,String>();
        // 시간별 LP 존재 여부 및 이벤트 저장
        Map<String,String> timeLine = new HashMap<String,String>();

        //LP List 조회
        List<LpEM> lpList = new ArrayList<LpEM>();
        Set<Condition> set = new HashSet<Condition>();
        set.add(new Condition("id.channel", new Object[]{condition.get("channel")}, null, Condition.Restriction.EQ));
        set.add(new Condition("yyyymmdd", new Object[]{condition.get("yyyymmdd")}, null, Condition.Restriction.EQ));
        set.add(new Condition("id.mdevId", new Object[]{condition.get("mdevId")}, null, Condition.Restriction.EQ));
        lpList = lpemDao.getLpEMsByListCondition(set);

        //Meter, Meter Model 조회
        Meter target = meterDao.get(condition.get("mdevId").toString());
        String modelName = target.getModel().getName();

        //정전 이벤트 아이디 조회
        List<Object> downIds = meDao.getEventIdsByNames2("POWER DOWN",modelName);
        List<Object> upIds = meDao.getEventIdsByNames2("POWER UP",modelName);
        if(downIds.isEmpty() || upIds.isEmpty()){
            // 정전 이벤트를 확인할 수 없으면 검침율을 조사할 수 없다.
            message.put("RESULT", "FAIL");
            message.put("MESSAGE", "System can't find event definition.");
            result.put("MESSAGE", message);
            return result;
        }
        Object[] downId = (Object[])downIds.get(0);
        Object[] upId = (Object[])upIds.get(0);

        //정전 로그 조회
        List<MeterEventLog> downLogs = meLogDao.getEventLogListByActivator(condition,(String)downId[0]);
        List<MeterEventLog> upLogs = meLogDao.getEventLogListByActivator(condition,(String)upId[0]);

        //검색일 이전 마지막 이벤트 조회 (타임라인의 시작점으로 지정)
        String[] eId = {(String)downId[0], (String)upId[0]};
        List<Object> lastLog = meLogDao.getLastEventLogByEventId(condition, eId);

        //Time Line 생성
        timeLine = initTimeLine(target);
        if(timeLine.isEmpty()){
            // lp interval이 일반적인 값이 아니면 time line을 생성할 수 없다.
            message.put("RESULT", "FAIL");
            message.put("MESSAGE", "LP_Interval should be one of 10,15,30,60.");
            result.put("MESSAGE", message);
            return result;
        }
        // 과거 이벤트 입력 (앞에서 탐색한 전일 마지막 이벤트)
        if(!lastLog.isEmpty()){
            // 과거 Power 관련 이벤트가 존재하는 경우 '(-time)음수시간'으로 입력
            Object[] log = (Object[]) lastLog.get(0);
            String downEventId = (String)downId[0];
            String upEventId = (String)upId[0];
            if(downEventId.equals(log[0])){
                timeLine.put("-".concat(log[1].toString()), "POWER DOWN");
            }else if(upEventId.equals(log[0])){
                timeLine.put("-".concat(log[1].toString()), "POWER UP");
            }
        }else{
            // 없으면 key없이 NORMAL이라고 입력
            timeLine.put("-", "NORMAL");
        }

        // LP 누락 여부 입력
        Integer lpInterval = target.getLpInterval();
        int lpLength = lpList.size();
        for(int i=0; i<lpLength; i++){
            LpEM lp = lpList.get(i);
            String hour = lp.getHour();
            if(lpInterval==10){
                timeLine.put(hour.concat("0000"), lp.getValue_00()==null?"X" : "O");
                timeLine.put(hour.concat("1000"), lp.getValue_10()==null?"X" : "O");
                timeLine.put(hour.concat("2000"), lp.getValue_20()==null?"X" : "O");
                timeLine.put(hour.concat("3000"), lp.getValue_30()==null?"X" : "O");
                timeLine.put(hour.concat("4000"), lp.getValue_40()==null?"X" : "O");
                timeLine.put(hour.concat("5000"), lp.getValue_50()==null?"X" : "O");
            }else if(lpInterval==15){
                timeLine.put(hour.concat("0000"), lp.getValue_00()==null?"X" : "O");
                timeLine.put(hour.concat("1500"), lp.getValue_15()==null?"X" : "O");
                timeLine.put(hour.concat("3000"), lp.getValue_30()==null?"X" : "O");
                timeLine.put(hour.concat("4500"), lp.getValue_45()==null?"X" : "O");
            }else if(lpInterval==30){
                timeLine.put(hour.concat("0000"), lp.getValue_00()==null?"X" : "O");
                timeLine.put(hour.concat("3000"), lp.getValue_30()==null?"X" : "O");
            }else if(lpInterval==60){
                timeLine.put(hour.concat("0000"), lp.getValue_00()==null?"X" : "O");
            }else{

            }
        }
        // 정전 시간 입력
        int logLength = downLogs.size();
        for(int i=0; i<logLength; i++){
            MeterEventLog log = downLogs.get(i);
            String hhmmss = log.getOpenTime().substring(8);
            if(timeLine.containsKey(hhmmss)){
                // LP 시간과 겹칠 경우를 고려
                if(timeLine.get(hhmmss)=="1"){
                    Integer tempTime = Integer.parseInt(hhmmss)+1;
                    timeLine.put(tempTime.toString(), "POWER DOWN");
                }else{
                    Integer tempTime = Integer.parseInt(hhmmss)+1;
                    timeLine.put(hhmmss, "POWER DOWN");
                    timeLine.put(tempTime.toString(), "0");
                }
            }else{
                timeLine.put(hhmmss, "POWER DOWN");
            }
        }
        // 복전 시간 입력
        logLength = upLogs.size();
        for(int i=0; i<logLength; i++){
            MeterEventLog log = upLogs.get(i);
            String hhmmss = log.getOpenTime().substring(8);
            if(timeLine.containsKey(hhmmss)){
                // LP 시간과 겹칠 경우를 고려
                if(timeLine.get(hhmmss)=="0"){
                    Integer tempTime = Integer.parseInt(hhmmss)+1;
                    timeLine.put(tempTime.toString(), "POWER UP");
                }else{
                    Integer tempTime = Integer.parseInt(hhmmss)+1;
                    timeLine.put(hhmmss, "POWER UP");
                    timeLine.put(tempTime.toString(), "0");
                }
            }else{
                timeLine.put(hhmmss, "POWER UP");
            }
        }

        // key 값 정렬 (필수, 시간별로 정렬됨)
        TreeMap<String,String> timeMap = new TreeMap<String,String>(timeLine);

        // 정렬 결과 탐색 (정전 누락 파악), 그리드 출력용 리스트 맵 생성
        ArrayList<HashMap<String, String>> calcMap = calcResult(timeMap,lpInterval);

        // 결과 정리
        message.put("RESULT", "SUCCESS");
        result.put("MESSAGE", message);
        result.put("CALC", calcMap);

        return result;
    }

    // 주어진 Meter의 Lp_Interval에 알맞은 기본 collection 생성
    private Map<String,String> initTimeLine(Meter meter){
        Map<String,String> baseLine = new HashMap<String,String>();
        Integer lpInterval = meter.getLpInterval();
        //String.format("%03d",input);
        for(int i=0; i<24; i++){
            String hh = String.format("%02d",i);
            if(lpInterval==10){
                baseLine.put(hh.concat("0000"), "X");
                baseLine.put(hh.concat("1000"), "X");
                baseLine.put(hh.concat("2000"), "X");
                baseLine.put(hh.concat("3000"), "X");
                baseLine.put(hh.concat("4000"), "X");
                baseLine.put(hh.concat("5000"), "X");
            }else if(lpInterval==15){
                baseLine.put(hh.concat("0000"), "X");
                baseLine.put(hh.concat("1500"), "X");
                baseLine.put(hh.concat("3000"), "X");
                baseLine.put(hh.concat("4500"), "X");
            }else if(lpInterval==30){
                baseLine.put(hh.concat("0000"), "X");
                baseLine.put(hh.concat("3000"), "X");
            }else if(lpInterval==60){
                baseLine.put(hh.concat("0000"), "X");
            }else{
                break;
            }
        }
        return baseLine;
    }

    private ArrayList<HashMap<String, String>> calcResult(TreeMap<String,String> resultMap, Integer lpInterval){
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        int LpTotal = (60/lpInterval)*24;   //LP Interval에 의한 LP총계
        int valueCnt = 0;   // LP 개수
        int downLp = 0;     // 정전 누락 LP
        int missingLp = 0;  // 일반 누락 LP

        Iterator<String> keys = resultMap.keySet().iterator();
        Boolean isDownState = false;
        // 첫번째 항목
        String start = keys.next();
        String value = resultMap.get(start);
        if(value.equals("POWER DOWN")){
            isDownState = true;
        }
        // 첫번째 항목 리스트에 삽입
        HashMap<String,String> item = new HashMap<String,String>();
        item.put("hhmmss", start);
        item.put("name", value);
        list.add(item);

        // 나머지 항목 순회
        while(keys.hasNext()){
            String key = keys.next();
            value = resultMap.get(key);
            switch (value){
                case "POWER UP":
                    isDownState = false;
                    break;
                case "POWER DOWN":
                    isDownState = true;
                    break;
                case "O":
                    valueCnt++;
                    break;
                case "X":
                    if(isDownState){
                        downLp++;
                    }else{
                        missingLp++;
                    }
                    break;
                default:
                    break;
            }
            // 리스트 맵 형태로 저장 (그리드 출력을 편하게)
            item = new HashMap<String,String>();
            item.put("hhmmss", key);
            item.put("name", value);
            list.add(item);
        }

        HashMap<String,String> calcMap = new HashMap<String,String>();
        calcMap.put("LP_TOTAL", Integer.toString(LpTotal));
        calcMap.put("VALUE_CNT", Integer.toString(valueCnt));
        calcMap.put("DOWN_LP", Integer.toString(downLp));
        calcMap.put("MISSING_LP", Integer.toString(missingLp));
        list.add(calcMap);

        //리스트 맵 반환
        return list;
    }

    // meterMaxGadget의 미터 데이터 사용
    @Override
    public List<Object> getMeterNumberList(Map<String,Object> condition){
        List<Object> result = new ArrayList<Object>();

        // 공급사, PAGE 조건만 입력
        result = meterDao.getMeterSearchGrid(condition);


        return result;
    }
}
