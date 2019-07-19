package com.aimir.service.mvm;

import java.util.List;
import java.util.Map;

/**
 * 검침 데이터 수동 업로드 가젯을 위한 Manager
 * 전기
 * @author SEJIN
 *
 */
public interface HandheldUnitDataUploadManager_MOE {
	
    /**
     * 기본 이력 조회
     * @param condition : meter id, login id, upload date
     * @return
     */
    public List<Map<String, Object>> getUploadHistory( Map<String,Object> condition );
    
    /**
     * 기본 이력을 조회하는데, 공급사 환경에 해당하는 시간 포맷으로 변환하여 반환.
     * @param condition
     */
    public List<Map<String, Object>> getUploadHistoryWithLocTime( Map<String,Object> condition, String supplierId );
    
    /**
     * 실패 이력 조회
     * @param condition : upload id, upload date
     * @return
     */
    public List<Map<String,Object>> getFailedUploadHistory( Map<String,Object> condition );
    
    /**
     * 엑셀 파일의 최상단 타이틀 읽어오기 (제목으로 데이터 타입을 확인할수 있고, 그 아래줄 미터번호를 추출)
     * @param excel : 파일 이름
     * @param ext : 확장자
     * @return [0]최상단이름, [1]미터번호
     */
    public String[] getTitleName(String excel, String ext);
    
    /**
     * 엑셀 확장자 XLS 파일 decoder
     * 검침 데이터를 파일 타입에 따라 리스트 구조체로 추출
     * @param excel : XLS 파일 경로
     * @param dataType : 검침 데이터 종류 (1.LOAD 2.DAILY 3.MONTH) 
     * @return
     */
    public List<Map<String,Object>> readExcel_XLS(String excel, String dataType, String supplierId); 
    
    /**
     * 엑셀 확장자 XLSX 파일 decoder
     * 검침 데이터를 파일 타입에 따라 리스트 구조체로 추출
     * @param excel : XLSX 파일 경로
     * @param dataType : 검침 데이터 종류 (1.LOAD 2.DAILY 3.MONTH) 
     * @return
     */
    public List<Map<String,Object>> readExcel_XLSX(String excel, String dataType, String supplierId);

    /**
     * 주어진 열(row)에서 컬럼 인덱스를 확인하고 그 이름 배열을 반환
     * LP의 경우 따로 처리하고, Daily와 Month는 항목이 유사하여 하나로 통합
     * @param testRow : 엑셀 파일의 3번째 행(0.데이터타입 1.모델명 2.컬럼이름 ...)
     * @return : 컬럼의 이름이 기록된 문자열 배열
     */
    public String[] readLoadProfile_XLSX_cellNameTest(Object _testRow);
    public String[] readLoadProfile_XLS_cellNameTest(Object _testRow);
    public String[] readDailyMonth_XLSX_cellNameTest(Object _testRow);
    public String[] readDailyMonth_XLS_cellNameTest(Object _testRow);
    
    /**
     * 주어진 열(row)에서 컬럼 인덱스를 확인하고 그 이름 배열을 반환하는 다른 함수 
     * LP채널 변경에 따른 함수수정, LP만 사용하고 Daily, Month는 사용하지 않음.
     * @param testRow : 엑셀 파일의 3번째 행(0.데이터타입 1.모델명 2.컬럼이름 ...)
     * @return : 컬럼의 이름이 기록된 문자열 배열
     */
    public String[] readLoadProfile_XLSX_cellNameTest2(Object _testRow);
    
    /**
     * 주어진 열(row)에서 컬럼 인덱스를 확인하고 그 이름의 배열을 반환
     * LP채널 변경에 따라, Energy와 Power가 분리되었음. 
     * @param testRow : 엑셀 파일의 3번째 행(0.데이터타입 1.모델명 2.컬럼이름 ...)
     * @return : 컬럼의 이름이 기록된 문자열 배열
     */
    public String[] readPowerProfile_XLSX_cellNameTest(Object _testRow);
    
    /**
     * 파일에서 추출한 내용을 알맞은 형태로 변형 및 저장
     * @param _lineList : 추출된 리스트 구조체
     */
    public Map<String,Object> saveLPFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId);
    public Map<String,Object> saveDPFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId);
    public Map<String,Object> saveMPFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId);
    
    /**
     * 파일에서 추출한 내용을 알맞은 형태로 변형 및 저장 (기존 함수에서 LP채널 변경, PQ없어짐)
     * @param _lineList : 추출된 리스트 구조체
     * @return 결과, 성공라인, 실패라인, 전체 라인
     */
    public Map<String,Object> saveLPFromList2(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId);
    
    /**
     * 기존 함수에서 LP와 PQ가 분리되었음
     * @param _lineList : 추출된 리스트 구조체
     * @return 결과, 성공라인, 실패라인, 전체 라인
     */
    public Map<String,Object> savePQFromList(List<Map<String, Object>> _lineList, String _mdsId, String _uHistId, String _modelId);
    
    /**
     * 기본적인 정보만 입력된 업로드 이력생성
     * @param loginId : 로그인아이디
     * @param mdsId : 미터 시리얼번호
     * @return : PK(TimeStamp) 반환
     */
    public String addUploadHistory_basicInfo(String loginId, String mdsId);
    
    /**
     * 기본 생성된 업로드 이력을 찾아서 세부 정보 입력
     * @param conditionMap : 업로드 일자, 미터 등록 여부, 데이터 타입 등을 포함한 모든 정보
     * @return
     */
    public String updateUploadHistory_detailInfo(Map<String,Object> conditionMap);
    
    /**
     * 재업로드시에 기존 업로드 이력을 찾아서 기본 정보 업데이트
     * @param uploadId : 주어진 업로드 아이디
     * @param loginId
     * @param mdsId
     * @return : 기존 업로드 이력의 업로드 아이디 반환
     */
    public String updateUploadHistory_basicInfo(String uploadId, String loginId, String mdsId);
    
    /**
     * LP업데이트에 실패한 항목을 이력으로 생성
     * @param conditionMap : 업로드이력아이디, 행번호, 실패 사유 등..
     * @return
     */
    public String addUploadFailHistory(Map<String,Object> conditionMap);
    
    /**
     * 현재 Locale과 Country Code를 통해 일자표시패턴 탐색 후 반환
     * @param supplierId
     * @return ex)yyyy-mm-dd, mm-dd-yyyy (+ hh:mm:ss 패턴 이어붙임)
     */
    public String getDatePatternFromLocale(String supplierId);
    
    /**
     * 엑셀의 2번째 라인에 포함된 미터 시리얼을 추출하여 반환 
     * @param _testRow (행)
     * @return 미터 시리얼
     */
    public String checkMeterNo_xlsx(Object _testRow);
    public String checkMeterNo_xls(Object _testRow);
    /**
     * 엑셀의 미터 타입을 읽어서 단상, 3상 구분
     * @param testCell : 미터 타입이 포한된 셀
     * @return 1:단상 3:3상
     */
    
    public String checkPhase(Object _testCell);
    
    /**
     * 컬럼 인덱스에 포함된 단위를 파악하여 배율 반환, kWh와 Wh 구분
     * @param _testRow
     * @return kWh이면 1.0 반환, Wh이면 0.001 반환하여 k에 맞도록 조정
     */
    public double check_XLSX_Unit(Object _testRow);
    public double check_XLS_Unit(Object _testRow);
}

