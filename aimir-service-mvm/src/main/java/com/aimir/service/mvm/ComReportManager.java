package com.aimir.service.mvm;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 검침율을 확인하기 위한 통계 가젯
 * 전기 데이터
 * @author SEJIN HAN
 */
public interface ComReportManager {

    /**
     * 정전 이벤트를 고려한 전기 미터의 검침율 조사
     * @param condition : 조사일자, 미터번호
     * @return
     */
    public Map<String,Object> getValidMeteringRate(Map<String,Object> condition);

    /**
     * 미터리스트 조회
     * @param condition
     * @return 미터 번호, 모델명, LP 주기 포함
     */
    public List<Object> getMeterNumberList(Map<String,Object> condition);
}
