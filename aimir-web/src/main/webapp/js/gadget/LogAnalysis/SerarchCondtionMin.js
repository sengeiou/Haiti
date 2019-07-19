/**
  Log Analysis Min 검색조건
 */


function getSearchDate(_dateType){   
	if(DateType.HOURLY == _dateType){

		$('#searchStartDate').val($('#hourlyStartDate').val());
		$('#searchEndDate').val($('#hourlyEndDate').val());

		$('#searchStartHour').val('0000');
		$('#searchEndHour').val('2359');
		
		convertSearchDate();
	}  

}

/**
 *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
    데이터 유형변경이 비동기식으로 되어있어 처리가 늦어질경우
	에러발생 => 동기식으로 변경해줌.
 */
function convertSearchDate(){
	$.ajax({
		 type: "POST"
	   , cache: false
	   , async: false        // 동기식
	   , timeout: 5000
	   , dataType: "json"
	   , url: ctxPath + "/common/convertSearchDate.do"
	   , data: {
		  searchStartDate:$('#searchStartDate').val()
		, searchEndDate:$('#searchEndDate').val()
		, supplierId:supplierId			   
	   }
	   , success: function(json){
		   $('#searchStartDate').val(json.searchStartDate);
		   $('#searchEndDate').val(json.searchEndDate);			   
	   }
	   , error: function(){
		   Ext.Msg.alert("ERROR", "ERROR - Connection error.");
	   }
	});
}

	
/**
 * 날짜타입별 조회조건 검증
 */
function validateSearchCondition(_dateType){

	if(DateType.HOURLY == _dateType){
		if(Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())){
			//시작일이 종료일보다 큽니다
			parent.Ext.Msg.alert(err_title, err_msg);
			return false;
		}else{
			if(Number($('#searchStartDate').val()) == Number($('#searchEndDate').val())){
				if(Number($('#searchStartHour').val()) > Number($('#searchEndHour').val())){
					parent.Ext.Msg.alert(err_title, err_msg);
					return false;
				}
			}			
		}		

		return true;
	}else{
		//날짜타입오류
		return false;
	}
}	
	
function commonDateTabInit(){
    $('#searchDateType').val(DateType.HOURLY);//

	//일자별,기간별 날짜입력창 변경시
	$(function() { $('#hourlyStartDate').bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
	$(function() { $('#hourlyEndDate').bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
 
	locDateFormat = "yymmdd";

	//탭별 일자DatePicker 생성
	$("#hourlyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: ctxPath + '/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
	$("#hourlyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: ctxPath + '/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );



	$.ajax({
		 type: "POST"
	   , cache: false
	   , async: false        // 동기식
	   , timeout: 5000
	   , dataType: "json"
	   , url: ctxPath + "/common/getYear.do"
	   , data: {
		  supplierId: supplierId
	   }
	   , success: function(json){
			 var startYear = json.year;//currDate.getYear()-10;
			 var endYear = json.currYear;//currDate.getYear();
			 var currDate = json.currDate;

			 $("#hourlyStartDate").val(currDate);
			 $("#hourlyEndDate").val(currDate);
			 
			 getSearchDate(DateType.HOURLY);
	   }
	   , error: function(){
		   Ext.Msg.alert("ERROR", "ERROR - Get Year Connection error.");
	   }
	});
	

/*
	$.getJSON(ctxPath + "/common/getYear.do"
		,{supplierId:supplierId}
		,function(json) {
			 var startYear = json.year;//currDate.getYear()-10;
			 var endYear = json.currYear;//currDate.getYear();
			 var currDate = json.currDate;

			 $("#hourlyStartDate").val(currDate);
			 $("#hourlyEndDate").val(currDate);
			 
			 getSearchDate(DateType.HOURLY);
			 
	//		 sendRequest();			   // 최초 한번 검색
	});  */
}
	
	
// datepicker로 선택한 날짜의 포맷 변경
function modifyDate(setDate, inst){
	var dateId = '#' + inst.id;

	$.getJSON(ctxPath + "/common/convertLocalDate.do"
			,{dbDate:setDate, supplierId:supplierId}
			,function(json) {
				$(dateId).val(json.localDate);
				$(dateId).trigger('change');
			});
}	