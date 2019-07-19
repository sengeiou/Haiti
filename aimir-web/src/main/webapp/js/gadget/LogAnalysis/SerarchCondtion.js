/**
  Log Analysis 검색조건
 */

//검색 조건.
function getSearchConditionArray(){
	var arrayObj = Array();
	arrayObj[0] = '';  //$('#operatorType').val();//operatorType
	arrayObj[1] = ''; //$('#userId').val();      // userId
	arrayObj[2] = ''; //$('#targetType').val();  //targetType
	arrayObj[3] = $('#deviceId').val();  //targetName  => deviceId  장비아이디
	arrayObj[4] = ''; //$('#status').val();      //status
	arrayObj[5] = ''; //description
	arrayObj[6] = $('#searchDateType').val();
	arrayObj[7] = $('#searchStartDate').val() + $('#searchStartHour').val() + "00"; //startDate
	arrayObj[8] = $('#searchEndDate').val() + $('#searchEndHour').val() + "59";     //endDate
	arrayObj[9] = _supplierId; //supplierId
	arrayObj[10] = $('#operationCombo').val(); //operation
	arrayObj[11] = ''; //clickControl
	arrayObj[12] = ''; //operatorType2
	arrayObj[13] = ''; //status2
	arrayObj[14] = ''; //date
	arrayObj[15] = $('#operationCombo option:selected').attr('codeValue');  // operation code
	//             $('#operationCombo').find('option:selected').attr('codeValue')
	
	
	// Comm Log 용
	/*
	<option value="C">(C)Command</option>	
	<option value="D">(D)DataFile</option>	
	<option value="E">(E)Event</option>	
	<option value="M">(M)Metering</option>	
	<option value="P">(P)Partial</option>	
	<option value="R">(R)Metering</option>	
	<option value="S">(S)Metering</option>*/	
	arrayObj[17] = 'C';   // (C)Command.
	
	
	//EventAlert Log용
	arrayObj[18] = '33'   // EnergyLevelChanged = 33
	//console.log(arrayObj);
	
	return arrayObj;
};


function getSearchDate(_dateType){   
	var startDate = Number($('#hourlyStartHourCombo').val())<10?'0'+$('#hourlyStartHourCombo').val():$('#hourlyStartHourCombo').val();
        startDate += Number($('#hourlyStartMinuteCombo').val())<10?'0'+$('#hourlyStartMinuteCombo').val():$('#hourlyStartMinuteCombo').val();
	
	var endDate = Number($('#hourlyEndHourCombo').val())<10?'0'+$('#hourlyEndHourCombo').val():$('#hourlyEndHourCombo').val();
		endDate += Number($('#hourlyEndMinuteCombo').val())<10?'0'+$('#hourlyEndMinuteCombo').val():$('#hourlyEndMinuteCombo').val();
		
	if(DateType.HOURLY == _dateType){

		$('#searchStartDate').val($('#hourlyStartDate').val());
		$('#searchEndDate').val($('#hourlyEndDate').val());

		$('#searchStartHour').val(startDate);
		$('#searchEndHour')  .val(endDate);
		
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
		   , timeout: 10000
		   , dataType: "json"
		   , url: ctxPath + "/common/convertSearchDate.do"
		   , data: {
			  searchStartDate:$('#searchStartDate').val()
			, searchEndDate:$('#searchEndDate').val()
			, supplierId:_supplierId			   
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
 * 조회버튼 클릭시 조회조건 검증후 거래호출
 */
function sendRequest(){ 
	getSearchDate(DateType.HOURLY);
	
	// 조회조건 검증
	if(!validateSearchCondition(DateType.HOURLY))return false;
	send();
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
	$(function() { $('#hourlyStartDate')       .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
	$(function() { $('#hourlyEndDate')         .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
	$(function() { $('#hourlyStartHourCombo')  .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
	$(function() { $('#hourlyStartMinuteCombo').bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
	$(function() { $('#hourlyEndHourCombo')    .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
	$(function() { $('#hourlyEndMinuteCombo')  .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });

 
	locDateFormat = "yymmdd";

	//탭별 일자DatePicker 생성
	$("#hourlyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: ctxPath + '/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
	$("#hourlyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: ctxPath + '/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

	$.getJSON(ctxPath + "/common/getYear.do"
		,{supplierId:_supplierId}
		,function(json) {
			 var startYear = json.year;//currDate.getYear()-10;
			 var endYear = json.currYear;//currDate.getYear();
			 var currDate = json.currDate;

			 $("#hourlyStartDate").val(currDate);
			 $("#hourlyEndDate").val(currDate);

			 var hours = new Array();
			 for(var i = 0;i<=23;i++){
				 hours[i] = i<10?'0'+i:i+'';
			 }
	
			 var minute = new Array();
			 for(var j = 0; j<=59; j++){
				 minute[j] = j<10 ? '0'+ j:j+'';
			 }
			 
			 $('#hourlyStartHourCombo').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
			 $('#hourlyStartMinuteCombo').numericOptions({from:0,to:59,selectedIndex:0,labels:minute});
			 
			 $('#hourlyEndHourCombo').numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
			 $('#hourlyEndMinuteCombo').numericOptions({from:0,to:59,selectedIndex:59,labels:minute});
			 
			 $('#hourlyStartHourCombo').selectbox();
		     $('#hourlyStartMinuteCombo').selectbox();
			 
			 $('#hourlyEndHourCombo').selectbox();
			 $('#hourlyEndMinuteCombo').selectbox();
			 
			 $("#operationCombo").selectbox();
			 $("#timeGapSelect").selectbox();
			 
			 getSearchDate(DateType.HOURLY);
			 
			 sendRequest();			   // 최초 한번 검색
	});
}
	
	
// datepicker로 선택한 날짜의 포맷 변경
function modifyDate(setDate, inst){
	var dateId = '#' + inst.id;

	$.getJSON(ctxPath + "/common/convertLocalDate.do"
			,{dbDate:setDate, supplierId:_supplierId}
			,function(json) {
				$(dateId).val(json.localDate);
				$(dateId).trigger('change');
			});
}	