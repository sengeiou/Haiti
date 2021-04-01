<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<%@ include file="/taglibs.jsp"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
 <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <meta http-equiv="PRAGMA" content="NO-CACHE">
 <meta http-equiv="Expires" content="-1">
 <title></title>
 <style type="text/css">
     html{overflow:auto !important}
     /* Ext-Js Grid 하단 페이징툴바 버튼 간격이 벌어지는 것을 방지 */
 	.x-panel-bbar table {border-collapse: collapse; width:auto;}
     /* chrome 에서 ext-js 의 grid cell layout 이 어긋나는 부분 보완 */
     @media screen and (-webkit-min-device-pixel-ratio:0) {
         .x-grid3-row td.x-grid3-cell {
             padding-left: 0px;
             padding-right: 0px;
         }
     }
    /* ext-js grid header 정렬 */
    .x-grid3-hd-inner{
        text-align: center !important;
        font-weight: bold !important;
    }  
    form {
      margin-top: 10px;
      margin-bottom: 10px;    
    }
    form div {
      margin-bottom: 10px;
    }      
    form img.ui-datepicker-trigger {
      vertical-align: middle;
    }
    .searchSet{
      display: inline; 
      float: none;
    }
    .alertSet{
      float: right;
      width: 440px;
      margin: 20px 0px 0px 0px;
      padding: 0px;
      overflow:hidden;
    }
    form input.alt {
      width: 70px;
    } 
    form span{
      margin-right: 20px;
    }

    div.x-panel-bbar,
    div.x-panel-bbar div.x-toolbar {
      width: auto !important;
    }    
    /* ext-js grid 행 높이 고정 
     cancel이 버튼인 row와 텍스트인 경우 row의 높이가 다르므로 임의로 수정 
    */    

    .hidden {
      display: none;
    }
    .no-width {
      width: 0px;
      visibility: hidden;
    }
    .vertical-top {
      vertical-align: top;
    }
    span.bold-font {
      font-weight: bold;
    }
    button.download {
      cursor: pointer;
    }

    /* selectbox wrapper 관련 margin 제거*/
    div.selectbox-wrapper {
      margin: 0px;
    }
    input.selectbox {
      display: block;
    }
    .inline-block {
      display: inline-block;
    }
    .description_modi a{
		  overflow:hidden;
		  display:block; 
		  width:18px;
		  height:18px;
		  background:url(${ctx}/themes/images/orange/setting/btn-modi.gif) no-repeat;
		}
		.description_modi:hover a{
		  background-position: -18px 0px;
		  overflow:hidden;
		  display:block; 
		  width:18px;
		  height:18px;
		}
    </style>
    <%@ include file="/gadget/system/preLoading.jsp"%>
    <link href="${ctx}/css/style_hems.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/style.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/css/jquery.tablescroll.css" rel="stylesheet" type="text/css">
    <link href="${ctx}/js/extjs/resources/css/ext-all.css" rel="stylesheet" type="text/css" title="blue" >
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/public-customer.js"></script>
    <script type="text/javascript" charset="utf-8" src="${ctx}/js/jquery.tablescroll.js"></script>
    <script type="text/javascript" src="${ctx}/js/tree/jquery.tree.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/upload/ajaxupload.js"></script>
    <script type="text/javascript" src="${ctx}/js/util/commonUtil.js"></script>
    
    <%-- <script type="text/javascript" src="${ctx}/js/tree/location.tree.js"></script> --%>
    <script type="text/javascript" charset="utf-8">/*<![CDATA[*/
	    //async
		var asyncRowNo = "";
		var asyncDeviceId = "";
		var asyncTrid = "";
		var asyncState= "";
		var asyncCommand = "";
		
        var editAuth = "${editAuth}";
        var operatorId = "${operatorId}";
        var supplierId = "${supplierId}";
        var allStr = "<fmt:message key="aimir.all"/>";
        var selectedMeterId = "";
        var selectedMdsId = "";
        var selectedMcuId = "";
        var loginId = "${loginId}";
        var isAllLoading = false;
        var PAGE_SIZE = 10;
		// var stsOTA;
		var descrEditPopupWindow;
		var roleName = "${roleName}";
		var supplierName = "${supplierName}";
		
        //$(document).ready(function() {
        Ext.onReady(function() {
        	if (supplierName == 'ECG' && roleName == 'University of Ghana') {
	        	$('#command').hide();
	            $('#prepay_operator_right').hide();
	        } else {
	        	$('#command').show();
	            $('#prepay_operator_right').show();
	        }
        	
        	if (editAuth == "true") {
                $("#ecBtnList").show();
                $("#notifyBtnList").show();
            } else {
                $("#ecBtnList").hide();
                $("#notifyBtnList").hide();
            }
        	
        	if (loginId == 'admin'){
        		$("#STSChargeHistoryDiv").show();
        	}
            
        	$('#stsControl_SUNI').hide();
        	$('#stsControl_WASION').hide();
            $('#relayControlButton').hide();
            $('#commandResultDiv').hide();

            Ext.QuickTips.init();
            hide();
            //locationTreeGoGo('treeDivA', 'searchWord', 'locationId');
            setSelectBox();
        	$.ajaxSetup({
    	        async: false
    	    });
            searchDivInit();
            initChargeHistoryData();
/*             
            initSTSChargeHistoryData();
            initSTSHistoryData()
            initAsyncHistoryGrid();
             */
            isAllLoading = true;

            // 선불고객 grid 조회
            getPrepayContractDivData();
        	$.ajaxSetup({
    	        async: true
    	    });
        	
        	
        	$("#infoForm span#contractInfoSearch").click(eventHandler.contractInfoSearch);
            $('input[name=startDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
            $('input[name=endDate]').datepicker('option',{onSelect:eventHandler.modifiedDateFormat});
            
        	$("#menu").tabs();
        	
        	//async      	
            var locDateFormat = "yymmdd";
    		//async
    		var date = new Date();
            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();

            if(("" + month).length == 1) month = "0" + month;
            if(("" + day).length == 1) day = "0" + day;

            var setDate      = year + "" + month + "" + day;
            var dateFullName = "";
            // 날짜를 국가별 날짜 포맷으로 변경
            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{dbDate:setDate, supplierId:supplierId}
                    ,function(json) {
                        dateFullName = json.localDate;{}
                        $("#asyncCommandHistoryStartDate").val(dateFullName);
                        $("#asyncCommandHistoryEndDate").val(dateFullName);
                    });

            $("#asyncCommandHistoryStartDateHidden").val(setDate);
            $("#asyncCommandHistoryEndDateHidden").val(setDate);
            
            //async
            $("#asyncCommandHistoryStartDate").datepicker({ maxDate : '+0m', showOn : 'button', buttonImage : '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly : true, dateFormat : locDateFormat, onSelect : function(dateText, inst) { modifyDateLocal(dateText, inst); } });
            $("#asyncCommandHistoryEndDate").datepicker({ maxDate : '+0m', showOn : 'button', buttonImage : '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly : true, dateFormat : locDateFormat, onSelect : function(dateText, inst) { modifyDateLocal(dateText, inst); } });
            
            // 검색조건의 날짜를 Local유형에서 일반 유형으로 변경
            function modifyDateLocal(setDate, inst) {
                var dateId = '#' + inst.id;
                var dateHiddenId = '#' + inst.id + 'Hidden';

                $(dateHiddenId).val($(dateId).val());

                $.getJSON("${ctx}/common/convertLocalDate.do", {
                    dbDate : setDate,
                    supplierId : supplierId
                }, function(json) {
                    $(dateId).val(json.localDate);
                });
            }

    		//월별 연도콤보 체인지이벤트 생성
    		$(function() {
    			$('#monthlyYearCombo').bind('change', function(event) {
    				getMonthlyMonthCombo("");
    			});
    		});
    		$(function() {
    			$('#monthlyMonthCombo').bind('change', function(event) {
    				getSearchDate(DateType.MONTHLY);
    			});
    		});
    		$(function() {
    			$('#monthlyLeft').bind('click', function(event) {
    				monthlyArrow(-1);
    			});
    		});
    		$(function() {
    			$('#monthlyRight').bind('click', function(event) {
    				monthlyArrow(1);
    			});
    		});

			/* stsOTA = new AjaxUpload('stsOTA', {
	            action: '${ctx}/gadget/device/command/cmdSuniFirmwareWrite.do',
	            data : {
					'target' : selectedMeterId,
					'ext' : 'ext'
	            },
	            responseType : 'json',
	            onSubmit : function(file , ext){
	            	stsOTA._settings.data.target=selectedMeterId;
	                stsOTA._settings.data.ext=ext;
	                Ext.Msg.wait('Waiting for response.', 'Wait !');
	
	                return true;
	            },
	            onComplete : function(file, returnData){
	                Ext.Msg.hide();
		    	    var rtnStr = returnData.rtnStr;
		    	    $('#commandResult').val(rtnStr);                 
		    	    Ext.Msg.alert('Message', "Please check the result on Event Alarm Log Gadget");
	            }
        	}); */
        });
        
        var calendarProp = {
        	      showOn: 'button',
        	      buttonImage: '${ctx}/themes/images/default/setting/calendar.gif',
        	      buttonImageOnly: true,
        	      dateFormat: 'yymmdd',
        	      altFormat: ''
        	    };
        
          
          function getMonthlyYearCombo() {
      		$.getJSON("${ctx}/common/getYear.do", {
    			supplierId : supplierId
    		}, function(json) {
    			var startYear = json.year;//currDate.getYear()-10;
    			var endYear = json.currYear;//currDate.getYear();
    			var currDate = json.currDate;

   				$('#monthlyYearCombo').numericOptions({
   					from : startYear,
   					to : endYear,
   					selectedIndex : 9
   				});
   				$('#monthlyYearCombo').selectbox();
   				getMonthlyMonthCombo('', false);
    		});
          }
          
      	/**
      	 * 월별탭에서 연도콤보 변경시 월콤보 생성
      	 */
      	function getMonthlyMonthCombo(monthVal, flag) {
      		$.getJSON("${ctx}/common/getMonth.do", {
      			year : $('#monthlyYearCombo').val()
      		}, function(json) {
      			var prevMonth = $('#monthlyMonthCombo').val();
      			$('#monthlyMonthCombo').emptySelect();
      			if (prevMonth == "" || prevMonth == null
      					|| Number(prevMonth) > Number(json.monthCount))
      				prevMonth = json.monthCount;
      			var idx = Number(prevMonth) - 1;
      			$('#monthlyMonthCombo').numericOptions({
      				from : 1,
      				to : json.monthCount,
      				selectedIndex : idx
      			});

      			if (monthVal != null && monthVal != "") {
      				$('#monthlyMonthCombo').val(monthVal);
      			}
      			$('#monthlyMonthCombo').selectbox();
      			getSearchDate(DateType.MONTHLY, flag);
      		});
      	}
      	
    	/**
    	 * 월별 화살표처리
    	 */
    	function monthlyArrow(val) {

    		var a_year;
    		$.getJSON("${ctx}/common/getYearMonth.do", {
    			year : $('#monthlyYearCombo').val(),
    			month : $('#monthlyMonthCombo').val(),
    			addVal : val
    		}, function(json) {
    			a_year = json.year;
    			$('#monthlyYearCombo').val(a_year);
    			$('#monthlyYearCombo').selectbox();
    			getMonthlyMonthCombo(json.month);
    		});

    	}
      	
    	/**
    	 * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
    	 * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
    	 * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
    	 */
    	function getSearchDate(_dateType, flag) {
    		var startDate = '';
    		var endDate = '';

			$('#monthlyYearCombo').selectbox();
			$('#monthlyMonthCombo').selectbox();
    	}

        function setSelectBox() {
            $.post("${ctx}/gadget/prepaymentMgmt/getSelectBoxData.do",
                   //{},
                   function(json) {

                       var statusResult = json.status;
                       var startArr = Array();
                       for (var i = 0; i < statusResult.length; i++) {
                           var obj = new Object();
                           obj.name=statusResult[i].descr;
                           obj.id=statusResult[i].id;
                           startArr[i]=obj
                       };
                       $("#statusCode").loadSelect(startArr);
                       $("#statusCode option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#statusCode").val("");
                       
                       $("#statusCode").selectbox();
                       
/*                        var paymentTypeResult = json.paymentType;
                       var paymentArr = Array();
                       for (var i = 0; i < paymentTypeResult.length; i++) {
                           var obj = new Object();
                           obj.code=paymentTypeResult[i].code;
                           obj.name=paymentTypeResult[i].descr;
                           obj.id=paymentTypeResult[i].code;
                           paymentArr[i]=obj
                       };
                       $("#paymentType").loadSelect(paymentArr); */
                       
                       $("#searchType").selectbox();
                       $("#paymentType option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#paymentType").val("");
                       
                       $("#paymentType").selectbox();
                       
                       $("#amountStatus").selectbox();
                       
                       $("#selectLastChargeDate").selectbox();

                       var serviceResult = json.serviceType;
                       var serviceArr = Array();
                       for (var i = 0; i < serviceResult.length; i++) {
                           var obj = new Object();
                           obj.name=serviceResult[i].descr;
                           obj.id=serviceResult[i].id;
                           serviceArr[i]=obj;
                       };
                       $("#serviceTypeCode").loadSelect(serviceArr);
                       $("#serviceTypeCode option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#serviceTypeCode").val("");
                       $("#serviceTypeCode").selectbox();

/*                        
                       var stsCommandList = json.stsCommandList;
                       var stsCmdArr = Array();
                       for (var i = 0; i < stsCommandList.length; i++) {
                           var obj = new Object();
                           obj.name=stsCommandList[i];
                           obj.id=stsCommandList[i];
                           stsCmdArr[i]=obj;
                       };
                       $("#stsCommand").loadSelect(stsCmdArr);
                       $("#stsCommand option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#stsCommand").val("");
                       $("#stsCommand").selectbox();

                       $('#stsResult').loadSelect(json.stsResult);
                       $("#stsResult option:eq(0)").replaceWith("<option value=''>" + allStr + "</option>");
                       $("#stsResult").val("");
                       $("#stsResult").selectbox(); */
                       
                   });
        }




        // 날짜조회조건 생성
        function searchDivInit() {

        	 //일자별,기간별 날짜입력창 변경시
            $(function() { $('#STSHourlyStartDate')       .bind('change', function(event) { setSTSSearchDate(); } ); });
            $(function() { $('#STSHourlyEndDate')         .bind('change', function(event) { setSTSSearchDate(); } ); });
            
            //일자별,기간별 날짜입력창 변경시
            $(function() { $('#hourlyStartDate')       .bind('change', function(event) { setSearchDate(); } ); });
            $(function() { $('#hourlyEndDate')         .bind('change', function(event) { setSearchDate(); } ); });
            
           //일자별,기간별 날짜입력창 변경시
            $(function() { $('#hourlyStartDateSTS')       .bind('change', function(event) { setSearchDateSTS(); } ); });
            $(function() { $('#hourlyEndDateSTS')         .bind('change', function(event) { setSearchDateSTS(); } ); });

            $(function() { $('#lastChargeStartDate')   .bind('change', function(event) { setSearchLastDate(); } ); });
            $(function() { $('#lastChargeEndDate')     .bind('change', function(event) { setSearchLastDate(); } ); });
            
            locDateFormat = "yymmdd";
            
            //탭별 일자DatePicker 생성
            $("#STSHourlyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#STSHourlyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

            //탭별 일자DatePicker 생성
            $("#hourlyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#hourlyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

            $("#hourlyStartDateSTS").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#hourlyEndDateSTS")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
     
            $("#lastChargeStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            $("#lastChargeEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
            
            $.getJSON("${ctx}/common/getYear.do"
                ,{ supplierId : supplierId }
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear = json.currYear;//currDate.getYear();
                     var currDate = json.currDate;

                     $("#STSHourlyStartDate").val(currDate);
                     $("#STSHourlyEndDate").val(currDate);
                     
                     $("#hourlyStartDate").val(currDate);
                     $("#hourlyEndDate").val(currDate);
                     
                     $("#hourlyStartDateSTS").val(currDate);
                     $("#hourlyEndDateSTS").val(currDate);
   
                     $("#lastChargeStartDate").val(currDate);
                     $("#lastChargeEndDate").val(currDate);
                     
                     var hours = new Array();
                     for(var i = 0;i<=23;i++){
                         hours[i] = i<10?'0'+i:i+'';
                     }
            
                     var minute = new Array();
                     for(var j = 0; j<=59; j++){
                         minute[j] = j<10 ? '0'+ j:j+'';
                     }

                     setSearchDate();
                     setSearchLastDate();
                     //setSearchDateSTS();
                     //setSTSSearchDate();
            });   
        };

        // datepicker로 선택한 날짜의 포맷 변경
        function modifyDate(setDate, inst){
            var dateId = '#' + inst.id;

            $.getJSON("${ctx}/common/convertLocalDate.do"
                    ,{ dbDate : setDate, supplierId : supplierId }
                    ,function(json) {
                        $(dateId).val(json.localDate);
                        $(dateId).trigger('change');
                    });
        }

        function setSearchDate(){
            var startDate = Number($('#hourlyStartHourCombo').val())<10?'0'+$('#hourlyStartHourCombo').val():$('#hourlyStartHourCombo').val();
                startDate += Number($('#hourlyStartMinuteCombo').val())<10?'0'+$('#hourlyStartMinuteCombo').val():$('#hourlyStartMinuteCombo').val();
            
            var endDate = Number($('#hourlyEndHourCombo').val())<10?'0'+$('#hourlyEndHourCombo').val():$('#hourlyEndHourCombo').val();
                endDate += Number($('#hourlyEndMinuteCombo').val())<10?'0'+$('#hourlyEndMinuteCombo').val():$('#hourlyEndMinuteCombo').val();
                
            $('#searchStartDate').val($('#hourlyStartDate').val());
            $('#searchEndDate').val($('#hourlyEndDate').val());

            $('#searchStartHour').val(startDate);
            $('#searchEndHour')  .val(endDate);
            
            convertSearchDate();
        }
        
        function setSearchDateSTS() {
        	var startDate = Number($('#hourlyStartHourComboSTS').val())<10?'0'+$('#hourlyStartHourComboSTS').val():$('#hourlyStartHourComboSTS').val();
            startDate += Number($('#hourlyStartMinuteComboSTS').val())<10?'0'+$('#hourlyStartMinuteComboSTS').val():$('#hourlyStartMinuteComboSTS').val();
        
	        var endDate = Number($('#hourlyEndHourComboSTS').val())<10?'0'+$('#hourlyEndHourComboSTS').val():$('#hourlyEndHourComboSTS').val();
	            endDate += Number($('#hourlyEndMinuteComboSTS').val())<10?'0'+$('#hourlyEndMinuteComboSTS').val():$('#hourlyEndMinuteComboSTS').val();
	            
	        $('#searchStartDateSTS').val($('#hourlyStartDateSTS').val());
	        $('#searchEndDateSTS').val($('#hourlyEndDateSTS').val());
	
	        $('#searchStartHourSTS').val(startDate);
	        $('#searchEndHourSTS')  .val(endDate);
	        
	        //convertSearchDateSTS();
        }
        
        function setSTSSearchDate() {
	            
	        $('#searchSTSStartDate').val($('#STSHourlyStartDate').val());
	        $('#searchSTSEndDate').val($('#STSHourlyEndDate').val());
	
	        convertSTSSearchLastDate();
        }
        
         function setSearchLastDate(){
            $('#searchLastChargeStartDate').val($('#lastChargeStartDate').val());
            $('#searchLastChargeEndDate').val($('#lastChargeEndDate').val());
            
            convertSearchLastDate();
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
                   , url: "${ctx}/common/convertSearchDate.do"
                   , data: {
                          searchStartDate : $('#searchStartDate').val()
                        , searchEndDate : $('#searchEndDate').val()
                        , supplierId : supplierId               
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

        function convertSearchLastDate(){
            $.ajax({
                     type: "POST"
                   , cache: false
                   , async: false        // 동기식
                   , timeout: 10000
                   , dataType: "json"
                   , url: "${ctx}/common/convertSearchDate.do"
                   , data: {
                          searchStartDate : $('#searchLastChargeStartDate').val()
                        , searchEndDate : $('#searchLastChargeEndDate').val()
                        , supplierId : supplierId               
                   }
                   , success: function(json){
                       $('#searchLastChargeStartDate').val(json.searchStartDate);
                       $('#searchLastChargeEndDate').val(json.searchEndDate);
                   }
                   , error: function(){
                       Ext.Msg.alert("ERROR", "ERROR - Connection error.");
                   }
                });
        }
        
/*         function convertSTSSearchLastDate(){
            $.ajax({
                     type: "POST"
                   , cache: false
                   , async: false        // 동기식
                   , timeout: 10000
                   , dataType: "json"
                   , url: "${ctx}/common/convertSearchDate.do"
                   , data: {
                          searchStartDate : $('#searchSTSStartDate').val()
                        , searchEndDate : $('#searchSTSEndDate').val()
                        , supplierId : supplierId               
                   }
                   , success: function(json){
                       $('#searchSTSStartDate').val(json.searchStartDate+'000000');
                       $('#searchSTSEndDate').val(json.searchEndDate+'235959');
                   }
                   , error: function(){
                       Ext.Msg.alert("ERROR", "ERROR - Connection error.");
                   }
                });
        }
        
        function convertSearchDateSTS() {
        	$.ajax({
        		type: "POST",
        		cache: false,
        		async: false,
        		timeout: 10000,
        		dataType: "json",
        		url: '${ctx}/common/convertSearchDate.do',
        		data: {
        			searchStartDate : $('#searchStartDateSTS').val(),
        			searchEndDate : $('#searchEndDateSTS').val(),
        			supplierId : supplierId
        		},
        		success: function(json) {
        			$('#searchStartDateSTS').val(json.searchStartDate);
        			$('#searchEndDateSTS').val(json.searchEndDate);
        		},
        		error: function() {
        			Ext.Msg.alert("ERROR","ERROR - Connection error.");
        		}
        	})
        } */

        /**
         * 날짜타입별 조회조건 검증
         */
        function validateSearchCondition(startDate, endDate){
            if(Number(startDate) > Number(endDate)){
                //시작일이 종료일보다 큽니다
                parent.Ext.Msg.alert('<fmt:message key="aimir.error"/>', '<fmt:message key="aimir.season.error"/>');
                return false;
            }
            //시작일 = 종료일
            /*else{
                if(Number($('#searchStartDateSTS').val()) == Number($('#searchEndDateSTS').val())){
                    if(Number($('#searchStartHourSTS').val()) > Number($('#searchEndHourSTS').val())){
                        parent.Ext.Msg.alert('<fmt:message key="aimir.error"/>', '<fmt:message key="aimir.season.error"/>!!!!!!!!!!!!');
                        return false;
                    }
                }           
            } */
            if(!checkDayLimit(startDate , endDate)) {
            	parent.Ext.Msg.alert('<fmt:message key="aimir.error"/>', 'Can search the maximum number of days is 90 days.');
            	return false;
            }
            return true;
        }
        
        function checkDayLimit(fromDate, toDate){
        	var daysAgo = 90;
        	var convertedFromDate = makeStringToDate(fromDate);
        	var convertedToDate = makeStringToDate(toDate);
        	return convertedToDate-(3600000*24*daysAgo) < convertedFromDate ? true : false;
        }
        
        function makeStringToDate(yyyyMMdd) {
            var year = yyyyMMdd.substring(0,4);
            var month = yyyyMMdd.substring(4,6);
            var date = yyyyMMdd.substring(6,8);
            return new Date(Number(year), Number(month)-1, Number(date));
        } 


        function getSearchStartDate(){
            return $('#searchStartDate').val() + "0000"; //startDate            
        }

        function getSearchEndDate(){
            return $('#searchEndDate').val() + "2359";     //endDate            
        }
        
        function getSTSSearchStartDate(){
            return $('#searchStartDateSTS').val() + "000000"; //startDate            
        }

        function getSTSSearchEndDate(){
            return $('#searchEndDateSTS').val() + "235959";     //endDate            
        }

        // window resize event
        $(window).resize(function() {
            getPrepayContractDivData();
            //getChargeHistoryData();
            initChargeHistoryData();
            //initSTSHistoryData();
            //initSTSChargeHistoryData();
            //initAsyncHistoryGrid();
        });

        /* 선불고객 리스트 START */
        var prepayContractGridOn = false;
        var prepayContractGrid;
        var prepayContractColModel;

        var getPrepayContractDivData = function() {
            var width = $("#prepayContract").width()-20;
            var mxwidth = 1800;

            var prepayContractStore = new Ext.data.JsonStore({
                autoLoad: {params:{start: 0, limit: 20}},
                url: "${ctx}/gadget/prepaymentMgmt/getPrepaymentContractList.do",
                baseParams: {
                    contractNumber : $("#contractNumber").val(),
                    customerName : $("#customerName").val(),
                    statusCode 	: $("#statusCode").val(),
                    paymentType : $("#paymentType").val(),
                    amountStatus : $("#amountStatus").val(),
                    mdsId		: $("#mdsId").val(),
                    gs1			: $("#gs1").val(),
                    locationId 	: $("#locationId").val(),
                    serviceTypeCode : $("#serviceTypeCode").val(),
                    searchLastChargeDate : $('#selectLastChargeDate').val(),
                    lastChargeStartDate: $("#searchLastChargeStartDate").val(),
                    lastChargeEndDate: $("#searchLastChargeEndDate").val(),
                },
                totalProperty: 'totalCount',
                root:'result',
                fields: ["contractNumber", "customerName", "mdsId", "serviceTypeCode", "serviceTypeName", "creditTypeCode", "creditTypeName", "tariffTypeName","mobileNo",
                         "prepaymentPowerDelay", "lastTokenDate", "currentCredit", "statusName", "emergencyCreditStartTime", "emergencyCreditMaxDuration", "emergencyCreditMaxDate",
                         "meterId", "mcuId", "modelName","lastReadDate","ihdId","address", "SPN", "gs1"],
                listeners: {
                    beforeload: function(store, options){
                        options.params || (options.params = {});
                        Ext.apply(options.params, {
                            page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                        });
                    },
                    load: function(store, record, options){
                        if (record.length > 0) {
                            // 데이터 load 후 첫번째 row 자동 선택
                            prepayContractGrid.getSelectionModel().selectFirstRow();
                        } else {
                            // 이전 데이터 모두 지움.
                            selectedContractNumber = "";
                            selectedServiceType = "";
                            getNotificationInfo();
                            //getAuthDeviceData();
                            //getChargeHistoryData();
                            if(chargeHistoryStore != null) {
                                 chargeHistoryStore.removeAll();
                            }
                        }
                    }
                }
            });

            prepayContractColModel = new Ext.grid.ColumnModel({
                columns: [
                     {header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'contractNumber', renderer: addTooltip, width: width/10-10}
                    //,{header: "<fmt:message key="aimir.accountNo"/>", dataIndex: 'SPN'}
                    ,{header: "<fmt:message key="aimir.customername"/>", dataIndex: 'customerName', renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.celluarphone"/>", dataIndex: 'mobileNo', renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.lastchargedate"/>", dataIndex: 'lastTokenDate'}
                    ,{header: "<fmt:message key="aimir.hems.prepayment.currentbalance"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'currentCredit',  align:'right'}
                    ,{header: "<fmt:message key="aimir.meterid"/>", dataIndex: 'mdsId',  renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.shipment.gs1"/>", dataIndex: 'gs1',  renderer: addTooltip}
                    //,{header: "<fmt:message key="aimir.sts.number"/>", dataIndex: 'ihdId',  renderer: addTooltip, width: width/13-10}
                    ,{header: "<fmt:message key="aimir.supply.type"/>", dataIndex: 'serviceTypeName' }
                    ,{header: "<fmt:message key="aimir.contract.tariff.type"/>", dataIndex: 'tariffTypeName', renderer: addTooltip}
                    ,{header: "<fmt:message key="aimir.meterstatus"/>", dataIndex: 'statusName'}
                    //,{header: "<fmt:message key="aimir.lastreaddate"/>", dataIndex: 'lastReadDate'}
                    //,{header: "<fmt:message key="aimir.address"/>", dataIndex: 'address', renderer: addTooltip}
                    //,{header: "<fmt:message key="aimir.hems.prepayment.validperiod"/>", dataIndex: 'emergencyCreditMaxDate', width: width/13-30}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: width/10
                }
            });

            if(prepayContractGridOn == false) {
                prepayContractGrid = new Ext.grid.GridPanel({
                      //title: '최근 한달 Demand Response History',
                      store: prepayContractStore,
                      colModel : prepayContractColModel,
                      //sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                      sm: new Ext.grid.RowSelectionModel({
                          singleSelect:true,
                          listeners: {
                              rowselect: function(sm, row, rec) {
                                  //Ext.getCmp("company-form").getForm().loadRecord(rec);
                                  selectedContractNumber = rec.get("contractNumber");
                                  selectedSPN = rec.get("SPN")!=null ? rec.get("SPN"):""
                                  selectedServiceType = rec.get("serviceTypeCode");
                                  selectedMeterId = rec.get("meterId");
                                  selectedMdsId = rec.get("mdsId");
                                  selectedMcuId = rec.get("mcuId");
                                  var selectedModelName = rec.get("modelName");
                                  
								  if(selectedModelName == "OmniPower STS P1" || selectedModelName == "OmniPower STS P3" || selectedModelName == "NRAM-1405DR60" || selectedModelName == "NRAM-3410DR100") {  
                                	  $("div a[id=sts]").show();
                                	  $("#stsLog").show();
                                  } else {
                                	  $("div a[id=sts]").hide();
                                	  $("#stsLog").hide();
                                  }
                                  
                                  //enableSts(selectedModelName);
                                  enableRelay(selectedModelName);
                                  getAllHours();
                                  getNotificationInfo();
                                  //getAuthDeviceData();
                                  getChargeHistoryData();
/*                                   getSTSChargeHistoryData();
                                  getSTSHistoryData()
                                  getAsyncHistoryGrid(); 
                                  */
                                  //if(isAllLoading) getChargeHistoryData();
                              }
                          }
                      }),
                      autoScroll:false,
                      height: 520,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'prepayContract',
                      viewConfig: {
                         // forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: 20,
                          store: prepayContractStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                prepayContractGridOn = true;
                // 행 클릭 이벤트 정의
                //prepayContractGrid.on('rowclick', function(grid, rowIndex, e) {
                //    //index = rowIndex;
                //    // 챠트에 값 설정
                //    //setGridChartData();
                //    // 챠트 생성
                //    //renderGridChart();
                //
                //    var record = grid.getStore().getAt(rowIndex);  // 레코드의 Row를 가져온다.
                //    //var data = record.get("contractNumber");
                //});
            } else {
                var bottomToolbar = prepayContractGrid.getBottomToolbar();
                prepayContractGrid.reconfigure(prepayContractStore, prepayContractColModel);
                bottomToolbar.bindStore(prepayContractStore);
            }
        };

        var notificationPeriod      ;
        var notificationInterval    ;
        var notificationTime        ;
        var notificationWeeklyMon   ;
        var notificationWeeklyTue   ;
        var notificationWeeklyWed   ;
        var notificationWeeklyThu   ;
        var notificationWeeklyFri   ;
        var notificationWeeklySat   ;
        var notificationWeeklySun   ;
        var prepaymentThreshold     ;
        var prepaymentThresholdView ;
        var prepaymentPowerDelay    ;
        var prepaymentPowerDelayView;
        var emergencyCreditAutoChange;
        var emergencyCreditMaxDuration;
        
        
        // 통보설정 조회
        function getNotificationInfo(){

            if (selectedContractNumber == "") {
                $("#emergencyCreditBtn").hide();
                $("#notifySettingBtn").hide();
                $("#notifyPeriod option:eq(0)").attr("selected", "true");

                $("#interval").val("");

                $("#limitPower").val("");
                $("#limitPowerView").val("");
                $("#duration").val("");

                $("input[name=autoChange]").filter('input[value=false]').attr("checked", "checked");
                $("#notifyHour option:eq(0)").attr("selected", "true");

                $("#dayofweek").hide();
                $("#intervalDailyDiv").show();
                $("#intervalWeeklyDiv").hide();

                $("#intervalDaily option:eq(0)").attr("selected", "true");
                $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");

                $("#notifyPeriod").selectbox();
                $("#intervalDaily").selectbox();
                $("#intervalWeekly").selectbox();
                $("#notifyHour").selectbox();

                $("#threshold").val("");
                $("#thresholdView").val("");
                //$("#duration").text(emergencyCreditMaxDuration);
            } else {
                $("#emergencyCreditBtn").show();
                $("#notifySettingBtn").show();
            }

            $.getJSON("${ctx}/gadget/prepaymentMgmt/getBalanceNotifySetting.do"
                    ,{contractNumber : selectedContractNumber}
                    ,function(json) {
                        notificationPeriod         = json.result.notificationPeriod;
                        notificationInterval       = json.result.notificationInterval;
                        notificationTime           = json.result.notificationTime;
                        //var notificationTimeView       = json.result.notificationTimeView;
                        notificationWeeklyMon      = json.result.notificationWeeklyMon;
                        notificationWeeklyTue      = json.result.notificationWeeklyTue;
                        notificationWeeklyWed      = json.result.notificationWeeklyWed;
                        notificationWeeklyThu      = json.result.notificationWeeklyThu;
                        notificationWeeklyFri      = json.result.notificationWeeklyFri;
                        notificationWeeklySat      = json.result.notificationWeeklySat;
                        notificationWeeklySun      = json.result.notificationWeeklySun;
                        //var lastNotificationDate       = json.result.lastNotificationDate;
                        prepaymentThreshold        = json.result.prepaymentThreshold;
                        prepaymentThresholdView    = json.result.prepaymentThresholdView;
                        prepaymentPowerDelay       = json.result.prepaymentPowerDelay;
                        prepaymentPowerDelayView   = json.result.prepaymentPowerDelayView;

                        emergencyCreditAutoChange  = json.result.emergencyCreditAutoChange;
                        //var emergencyCreditStartTime   = json.result.emergencyCreditStartTime;
                        emergencyCreditMaxDuration = json.result.emergencyCreditMaxDuration;
                        //var creditType                 = json.result.creditType;
                        //var devices                    = json.result.devices;

                        /* if (notificationPeriod == null || notificationPeriod == "") {
                            $("#notifyPeriod option:eq(0)").attr("selected", "true");
                        } else {
                            $("#notifyPeriod").val(notificationPeriod);
                        }

                        $("#interval").val(notificationInterval);

                        $("#limitPower").val(prepaymentPowerDelay);
                        $("#limitPowerView").val(prepaymentPowerDelayView);
                        $("#duration").val(emergencyCreditMaxDuration);

                        if (emergencyCreditAutoChange == true) {
                            $("input[name=autoChange]").filter('input[value=true]').attr("checked", "checked");
                        } else {
                            $("input[name=autoChange]").filter('input[value=false]').attr("checked", "checked");
                        }

                        if (notificationTime == null || notificationTime == "") {
                            $("#notifyHour option:eq(0)").attr("selected", "true");
                        } else {
                            $("#notifyHour").val(notificationTime);
                        }

                        if($("#notifyPeriod").val() == "2") {
                            $("#dayofweek").show();
                            $("#intervalDailyDiv").hide();
                            $("#intervalWeeklyDiv").show();

                            if (notificationInterval == null || notificationInterval == "") {
                                $("#intervalWeekly option:eq(0)").attr("selected", "true");
                            } else {
                                $("#intervalWeekly").val(notificationInterval);
                            }

                            $("#mon").attr("checked", notificationWeeklyMon);
                            $("#tue").attr("checked", notificationWeeklyTue);
                            $("#wed").attr("checked", notificationWeeklyWed);
                            $("#thu").attr("checked", notificationWeeklyThu);
                            $("#fri").attr("checked", notificationWeeklyFri);
                            $("#sat").attr("checked", notificationWeeklySat);
                            $("#sun").attr("checked", notificationWeeklySun);

                            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.weeks"/>");
                        } else {
                            $("#dayofweek").hide();
                            $("#intervalDailyDiv").show();
                            $("#intervalWeeklyDiv").hide();

                            if (notificationInterval == null || notificationInterval == "") {
                                $("#intervalDaily option:eq(0)").attr("selected", "true");
                            } else {
                                $("#intervalDaily").val(notificationInterval);
                            }

                            $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");
                        }

                        $("#notifyPeriod").selectbox();
                        $("#intervalDaily").selectbox();
                        $("#intervalWeekly").selectbox();
                        $("#notifyHour").selectbox();

                        $("#threshold").val(prepaymentThreshold);
                        $("#thresholdView").val(prepaymentThresholdView);
                        //$("#duration").text(emergencyCreditMaxDuration);
 */
                    });
        }

        <%--
        /* 인증장비 아이디 리스트 START */
        var deviceGridOn = false;
        var deviceGrid;
        var deviceColModel;
        // 인증방식 확정 후 구현
        var getAuthDeviceData = function() {
            var width = $("#authDevice").width();

            var deviceStore = new Ext.data.JsonStore({
                autoLoad: true,
                url: "${ctx}/gadget/prepaymentMgmt/getAuthDeviceList.do",
                baseParams: {
                    contractNumber : selectedContractNumber
                },
                root:'result',
                fields: ["id", "authKey", "friendlyName", "writeDate"]
            });

            deviceColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key="aimir.hems.prepayment.devicename"/>", dataIndex: 'friendlyName',
                        editor: new Ext.form.TextField({
                            id: 'friendlyName',
                            allowBlank: false
                        })
                    }
                   ,{header: "<fmt:message key="aimir.hems.prepayment.devicekey"/>", dataIndex: 'authKey',
                       editor: new Ext.form.TextField({
                           id: 'authKey',
                           allowBlank: false
                       })
                   }
                ],
                defaults: {
                    sortable: false
                   ,menuDisabled: true
                   ,width: 120
               }
            });


            if(deviceGridOn == false) {

                deviceGrid = new Ext.grid.EditorGridPanel({
                    //title: '<fmt:message key="aimir.hems.prepayment.authdevice"/>',
                    store: deviceStore,
                    colModel : deviceColModel,
                    sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
                    autoScroll:false,
                    width: width,
                    height: 100,
                    //stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    clicksToEdit: 1,
                    renderTo: 'authDevice',
                    viewConfig: {
                        forceFit:true,
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    tbar:[{
                        text: "추가",
                        scope: this,
                        handler: function(){
                           var recordObj = deviceGrid.getStore().recordType;
                           var r = new recordObj({
                               friendlyName : "",
                               authKey : "",
                               newRecord : 'yes', // Update할때 트리거로 이용하기 위해서
                               id : -1
                           });
                           deviceGrid.stopEditing();
                           deviceStore.insert(0, r);
                           deviceGrid.startEditing(0, 0); // 추가할 rowIndex, colIndex 위치
                        }
                    }]
                });
                deviceGridOn = true;
            } else {
              //var bottomToolbar = chargeHistoryGrid.getBottomToolbar();
              deviceGrid.reconfigure(deviceStore, deviceColModel);
              //bottomToolbar.bindStore(chargeHistoryStore);
            }
        };
        --%>

        // 저장확인창
        function saveEmergenCreditInfoConfirm() {
            Ext.MessageBox.confirm("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.wouldSave"/>"
                                 , function(btn) {
                                       if (btn == 'yes') {
                                           saveEmergenCreditInfo();
                                       }
                                   });
        }

        // Emergency Credit 정보를 저장
        function saveEmergenCreditInfo() {

            if (selectedContractNumber == "") {
                return;
            }

            var autoChange = $(":input:radio[name=autoChange]:checked").val();
            var duration = $("#duration").val();

            if (autoChange == null) {
                autoChange = "";
            }

            if (duration == "") {
                duration = "0";
            }

            if ($("#limitPowerView").val() == "") {
                $("#limitPower").val(0);
            } else {
                $("#limitPower").val(removeCharForReal($("#limitPowerView").val()));
            }

            $.post("${ctx}/gadget/prepaymentMgmt/updateEmergencyCreditInfo.do",
                  {contractNumber : selectedContractNumber,
                   autoChange     : autoChange,
                   duration       : duration,
                   limitPower     : $("#limitPower").val()
                   },
                  save_callback
           );
        }
        //저장 후 콜백
        function save_callback(json, textStatus) {
            if (json.status == "success") {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>", getNotificationInfo);
            } else {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
            }
        }
        
        /* 충전이력 리스트 START */
        var selectedContractNumber;
        var selectedSPN;
        var selectedServiceType;
        var chargeHistoryStore;
        var chargeHistoryGridOn = false;
        var chargeHistoryGrid;
        var chargeHistoryColModel;
        
        var initChargeHistoryData = function() {
            var width = $("#prepayContract").width()-20;
            var mxwidth = 1210;
            var setColorFrontTag = "<b style=\"color:";
            var setColorMiddleTag = ";\">"; 
            var setColorBackTag = "</b>";
            var purple = '#5E32BB'; //normal 
            var skyBlue = '#12ABBA'; //information
            var red = '#F31523'; // security error
            
            chargeHistoryStore = new Ext.data.JsonStore({
                url: "${ctx}/gadget/prepaymentMgmt/getChargeAndBalanceHistory.do",
                totalProperty: 'totalCount',
                root:'result',
                fields: ["TYPE", "SPN", "CONTRACTID", "DATETIME", "BEFOREBALANCE", "BALANCE", "CHARGEDAMOUNT", "CHARGEDTOKEN", "CANCELDATE", "CANCELTOKEN", "PAYTYPE", "USAGETOTAL", 
                	"USAGECOST", "MONTHLYUSAGE", "MONTHLYCOST", "VAT", "TOTALLEVY", "TOTALSUBSIDY", "SERVICECHARGE", "DESCR"],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }                    
                }
            });

            chargeHistoryColModel = new Ext.grid.ColumnModel({
                columns: [
                    {header: "<fmt:message key="aimir.header.type"/>", dataIndex: 'TYPE', align:'center', width:120,
                        renderer: function(val, me, record, rowNumber, columnIndex, store) {            
                            switch (val){
                                case "Billing(month)": return setColorFrontTag + skyBlue + setColorMiddleTag + val + setColorBackTag ; break; 
                                case "Billing(day)" : return setColorFrontTag + purple + setColorMiddleTag + val + setColorBackTag ; break; 
                                case "Recharge" : return setColorFrontTag + red + setColorMiddleTag + val + setColorBackTag ; break; 
                            }
                        }}
                    ,{header: "<fmt:message key="aimir.contractNumber"/>", dataIndex: 'CONTRACTID', align:'left'}
                    ,{header: "<fmt:message key="aimir.accountNo"/>", dataIndex: 'SPN', align:'left' ,width:100 }
                    ,{header: "<fmt:message key="aimir.time.date"/>", dataIndex: 'DATETIME', align:'left' ,width:140 }
                    ,{header: "<fmt:message key="aimir.prepayment.beforebalance"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'BEFOREBALANCE', align:'right' }
                    ,{header: "<fmt:message key="aimir.balance"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'BALANCE', align:'right' }
                    ,{header: "<fmt:message key="aimir.bill"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'USAGECOST', align:'right' }
                    ,{header: "<fmt:message key="aimir.usage"/>(<fmt:message key='aimir.unit.kwh'/>)", dataIndex: 'USAGETOTAL', align:'right' }
                    ,{header: "<fmt:message key="aimir.chargeAmount"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'CHARGEDAMOUNT', align:'right' }
/*                     ,{header: "<fmt:message key="aimir.prepayment.token"/>", dataIndex: 'CHARGEDTOKEN', align:'right' ,width:160,
                    	listeners : {
                    		dblclick : function(select,config,index,event) {
                    			var token = config.store.data.items[index].data.CHARGEDTOKEN;
                    			if(token!=null && token!="")
                    				cmdSTSHandler.setSTSToken(config.store.data.items[index].data.CHARGEDTOKEN);
                    		}
                    	}		
                    } */
                    ,{header: "<fmt:message key="aimir.cancel"/> <fmt:message key="aimir.time.date"/>", dataIndex: 'CANCELDATE', align:'left' }
                    //,{header: "<fmt:message key="aimir.cancel"/> <fmt:message key="aimir.sts.token"/>", dataIndex: 'CANCELTOKEN', align:'right' }
                    ,{header: "<fmt:message key="aimir.paymenttype"/>", dataIndex: 'PAYTYPE', align:'center' }
                    ,{header: "<fmt:message key="aimir.monthly"/> <fmt:message key="aimir.usage"/>(<fmt:message key='aimir.unit.kwh'/>)", dataIndex: 'MONTHLYUSAGE', align:'right' }
                    ,{header: "<fmt:message key="aimir.monthly"/> <fmt:message key="aimir.bill"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'MONTHLYCOST', align:'right' }
                    ,{header: "<fmt:message key="aimir.prepayment.vat"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'VAT', align:'right' }
                    ,{header: "<fmt:message key="aimir.prepayment.levy"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'TOTALLEVY', align:'right' }
                    ,{header: "<fmt:message key="aimir.prepayment.subsidy"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'TOTALSUBSIDY', align:'right' }
                    ,{header: "<fmt:message key="aimir.serviceCharge"/>(<fmt:message key='aimir.price.unit'/>)", dataIndex: 'SERVICECHARGE', align:'right' }
                    ,{header: "<fmt:message key="aimir.description"/>", dataIndex: 'DESCR', align:'left' }
                    /* ,{header: "<fmt:message key="aimir.hems.prepayment.consumption"/>(kWh)", dataIndex: 'usedConsumption', width: (width > mxwidth) ? width*(160/mxwidth) : 160, align:'right'}
                     ,{
                    	header: "<fmt:message key="aimir.description"/>", 
                    	width: (width > mxwidth) ? width*(120/mxwidth) : 120,
                    	renderer: function(value, metaData, rec, index) {
 	                    	var data = rec.data;
 	                        var btnHtml = "<span style='padding:5px;''>"+data.DESCR +"</span>"+"<span class='description_modi'><a href='#;' onclick='editDescrBtn();' ></a></span>" ;
 	                        var tplBtn = new Ext.Template(btnHtml);
 	                        return tplBtn.apply();
 	                    }
                    }*/
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 150
                }
            });

            if(chargeHistoryGridOn == false) {
                chargeHistoryGrid = new Ext.grid.GridPanel({
                      store: chargeHistoryStore,
                      colModel : chargeHistoryColModel,
                      sm: new Ext.grid.RowSelectionModel({
                    	  singleSelect:true,
                    	  listeners: {
      	                    rowselect: function(selectionModel, row, rec) {
      	                    	var data = rec.data;
      	                    	lastTokenId = data.lastTokenId;
      	                    	cancelReason = data.cancelReason;
      	                    	selectedContractNumber;
      	                    	}
                    	  }
                      }),
                      autoScroll:false,
                      height: 305,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'chargeHistory',
                      viewConfig: {
                          //forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: 10,
                          store: chargeHistoryStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                chargeHistoryGridOn = true;
            } else {
                var bottomToolbar = chargeHistoryGrid.getBottomToolbar();
                chargeHistoryGrid.reconfigure(chargeHistoryStore, chargeHistoryColModel);
                bottomToolbar.bindStore(chargeHistoryStore);
            }
        };

     // description 편집 버튼
        function editDescrBtn() {
        	var url = "${ctx}/gadget/prepaymentMgmt/prepaymentDescrEditPopup.jsp";
        	var opt = "width=400px, height=200px, left=650px, top=100px,  resizable=no, status=no";
        	var obj = new Object();
			obj.contractNumber = selectedContractNumber;
	    	obj.cancelReason = cancelReason;
	    	obj.lastTokenId = lastTokenId;
	    	
            if ( descrEditPopupWindow ) {
            	descrEditPopupWindow.close();
            }
			
            descrEditPopupWindow = window.open(url, "descrEdit", opt);
            descrEditPopupWindow.opener.obj = obj;
            
            getReturnValue(result);
        };
        
        function getReturnValue(result){
        	if(result==true)
        		getChargeHistoryData();
		}
        
        function getChargeHistoryData(){         
            if(validateSearchCondition($('#searchStartDate').val(), $('#searchEndDate').val())){
                chargeHistoryStore.baseParams = {};
                chargeHistoryStore.setBaseParam('contractNumber', selectedContractNumber);
                chargeHistoryStore.setBaseParam('SPN', selectedSPN);
                chargeHistoryStore.setBaseParam('searchType', $("#searchType").val());
                chargeHistoryStore.setBaseParam('searchStartDate', $('#searchStartDate').val());
                chargeHistoryStore.setBaseParam('searchEndDate', $('#searchEndDate').val());
                chargeHistoryStore.setBaseParam('mdsId', selectedMdsId);
                chargeHistoryStore.load({params:{start: 0, limit: 10}});                
            }


 //           }
        }
        
        
        //STS 관련 로직 주석처리
        
        /* 
        // 충전이력 조회
        var stsHistoryStore;
        var stsHistoryGridOn = false;
        var stsHistoryGrid;
        var stsHistoryColModel;
        var initSTSHistoryData = function() {
            var width = $("#prepayContract").width()-20;
            var mxwidth = 1210;

            stsHistoryStore = new Ext.data.JsonStore({
                url: "${ctx}/gadget/prepaymentMgmt/getSTSHistory.do",
                totalProperty: 'totalCount',
                root:'result',
                fields: ["CONTRACTNUMBER","CMD","SEQ","ASYNCTRID","CREATEDATE","PAYMODE", "RESULT", "FAILREASON", "RESULTDATE", "TOKENDATE", "TOKEN", "CHARGEDCREDIT", "GETDATE", "EMERGENCYCREDITDAY",
                         "TARIFFMODE", "TARIFFKIND", "TARIFFCOUNT", "CONDLIMIT1", "CONDLIMIT2", "CONSUMPTION", "FIXEDRATE", "VARRATE", "CONDRATE1", "CONDRATE2", "TARIFFDATE", "ECMODE","REMAININGCREDITDATE","REMAININGCREDIT",
                         "NETCHARGEYYYYMM","NETCHARGEMONTHCONSUMPTION","NETCHARGEMONTHCOST","NETCHARGEYYYYMMDD","NETCHARGEDAYCONSUMPTION","NETCHARGEDAYCOST","FCMODE",
                         "FRIENDLYDATE","FRIENDLYDAYTYPE","FRIENDLYFROMHHMM","FRIENDLYENDHHMM",'STSNUMBER','KCT1','KCT2','CHANNEL','PANID',
                         "ACTIVEENERGYCHARGE","GOVLEVY","STREETLIGHTLEVY","VAT","LIFELINESUBSIDY","FRIENDLYCREDITAMOUNT","EMERGENCYCREDITAMOUNT","SWITCHTIME"], 
                        
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }                    
                }
            });

            stsHistoryColModel = new Ext.grid.ColumnModel({
                columns: [
					{header: "<fmt:message key='aimir.sts.createDate'/>", dataIndex: 'CREATEDATE'}
					,{header: "<fmt:message key='aimir.sts.command'/>", dataIndex: 'CMD' }
					//,{header: "<fmt:message key='aimir.sts.sequence'/>", dataIndex: 'SEQ', align:'right'}
					,{header: "<fmt:message key='aimir.sts.transactionId'/>", dataIndex: 'ASYNCTRID', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.paymode'/>", dataIndex: 'PAYMODE'}
	                //,{header: "<fmt:message key='aimir.sts.resultDate'/>", dataIndex: 'RESULTDATE'}
	                //,{header: "<fmt:message key='aimir.sts.result'/>", dataIndex: 'RESULT', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.failReason'/>", dataIndex: 'FAILREASON', tooltip: "Fail Reason"}
	                ,{header: "<fmt:message key='aimir.sts.tokendate'/>", dataIndex: 'TOKENDATE'}
	                ,{header: "<fmt:message key='aimir.sts.token'/>", dataIndex: 'TOKEN', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.chargedcredit'/>", dataIndex: 'CHARGEDCREDIT', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.getDate'/>", dataIndex: 'GETDATE'}
	                //,{header: "<fmt:message key='aimir.sts.emergencyMode'/>", dataIndex: 'ECMODE'}
	                //,{header: "<fmt:message key='aimir.sts.emergencyDay'/>", dataIndex: 'EMERGENCYCREDITDAY'}
	                ,{header: "<fmt:message key='aimir.sts.tariffDate'/>", dataIndex: 'TARIFFDATE', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.tariffMode'/>", dataIndex: 'TARIFFMODE', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.tariffKind'/>", dataIndex: 'TARIFFKIND', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.tariffCnt'/>", dataIndex: 'TARIFFCOUNT', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.condlimit1'/>", dataIndex: 'CONDLIMIT1', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.condlimit2'/>", dataIndex: 'CONDLIMIT2', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.consumption'/>", dataIndex: 'CONSUMPTION', align:'right'}
	                ,{header: "<fmt:message key='aimir.activeEnergyCharge'/>", dataIndex: 'ACTIVEENERGYCHARGE', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.fixedRate'/>", dataIndex: 'FIXEDRATE', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.varRate'/>", dataIndex: 'VARRATE', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.condRate1'/>", dataIndex: 'CONDRATE1', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.condRate2'/>", dataIndex: 'CONDRATE2', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.remainingDate'/>", dataIndex: 'REMAININGCREDITDATE'
	                ,{header: "<fmt:message key='aimir.prepayment.govLevy'/>", dataIndex: 'GOVLEVY', align:'right'}
	                ,{header: "<fmt:message key='aimir.prepayment.publicLevy'/>", dataIndex: 'STREETLIGHTLEVY', align:'right'}
	                ,{header: "<fmt:message key='aimir.prepayment.vat'/>", dataIndex: 'VAT', align:'right'}
	                ,{header: "<fmt:message key='aimir.prepayment.lifeLineSubsidy'/>", dataIndex: 'LIFELINESUBSIDY', align:'right'}
	                ,{header: "Swtich Time", dataIndex: 'SWITCHTIME', align:'right'}
	                ,{header: "Friendly Credit", dataIndex: 'FRIENDLYCREDITAMOUNT', align:'right'}
	                ,{header: "<fmt:message key='aimir.hems.prepayment.emergencycredit'/>", dataIndex: 'EMERGENCYCREDITAMOUNT', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.remainingCredit'/>", dataIndex: 'REMAININGCREDIT', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.monthlyDate'/>", dataIndex: 'NETCHARGEYYYYMM'}
	                //,{header: "<fmt:message key='aimir.sts.monthlyCons'/>", dataIndex: 'NETCHARGEMONTHCONSUMPTION', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.monthlyCost'/>", dataIndex: 'NETCHARGEMONTHCOST', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.dailyDate'/>", dataIndex: 'NETCHARGEYYYYMMDD'}
	                //,{header: "<fmt:message key='aimir.sts.dailyCons'/>", dataIndex: 'NETCHARGEDAYCONSUMPTION', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.dailyCost'/>", dataIndex: 'NETCHARGEDAYCOST', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.fcmode'/>", dataIndex: 'FCMODE'}
	                ,{header: "<fmt:message key='aimir.sts.friendlyDate'/>", dataIndex: 'FRIENDLYDATE'}
	                ,{header: "<fmt:message key='aimir.sts.firendlyType'/>", dataIndex: 'FRIENDLYDAYTYPE', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.firendly.from'/>", dataIndex: 'FRIENDLYFROMHHMM', align:'right'}
	                ,{header: "<fmt:message key='aimir.sts.firendly.end'/>", dataIndex: 'FRIENDLYENDHHMM', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.number'/>", dataIndex: 'STSNUMBER', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.kct1'/>", dataIndex: 'KCT1', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.kct1'/>", dataIndex: 'KCT2', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.channel'/>", dataIndex: 'CHANNEL', align:'right'}
	                //,{header: "<fmt:message key='aimir.sts.panId'/>", dataIndex: 'PANID', align:'right'}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: 120
                }
            });

            if(stsHistoryGridOn == false) {
                stsHistoryGrid = new Ext.grid.GridPanel({
                      store: stsHistoryStore,
                      colModel : stsHistoryColModel,
                      autoScroll:false,
                      height: 275,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'stsHistory',
                      viewConfig: {
                          //forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: 10,
                          store: stsHistoryStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                stsHistoryGridOn = true;
            } else {
                var bottomToolbar = stsHistoryGrid.getBottomToolbar();
                stsHistoryGrid.reconfigure(stsHistoryStore, stsHistoryColModel);
                bottomToolbar.bindStore(stsHistoryStore);
            }
        };
        
        function getSTSHistoryData() {
        	if(validateSearchCondition($('#searchStartDateSTS').val(), $('#searchEndDateSTS').val())){
        		stsHistoryStore.baseParams = {};
                stsHistoryStore.setBaseParam('supplierId', supplierId);
                stsHistoryStore.setBaseParam('meterNumber', selectedMdsId);
                stsHistoryStore.setBaseParam('cmd', $('#stsCommand').val());
                stsHistoryStore.setBaseParam('startDate', getSTSSearchStartDate());
                stsHistoryStore.setBaseParam('endDate', getSTSSearchEndDate());
                stsHistoryStore.setBaseParam('result', $('#stsResult').val());
                stsHistoryStore.load({params:{start: 0, limit: 10}});                
            }
        }
        
        // STS(미터 정산)용 충전이력 리스트 START
        var stsChargeHistoryStore;
        var stsChargeHistoryGridOn = false;
        var stsChargeHistoryGrid;
        var stsChargeHistoryColModel;
        var initSTSChargeHistoryData = function() {
            var width = $("#prepayContract").width()-20;

            stsChargeHistoryStore = new Ext.data.JsonStore({
                url: "${ctx}/gadget/prepaymentMgmt/getSTSChargeHistory.do",
                totalProperty: 'totalCount',
                baseParams: {
                	contractNumber: selectedContractNumber,
                	mdsId: $("#mdsId").val(),
                    startDate: $("#searchSTSStartDate").val(),
                    endDate: $("#searchSTSEndDate").val(),
                },
                root:'result',
                fields: ["CONTRACTNUMBER","YYYYMMDDHHMM","BALANCE","USAGE","WRITEDATE","RESERVATION01","RESERVATION02","RESERVATION03","RESERVATION04","RESERVATION05"], 
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }                    
                }
            });

            stsChargeHistoryColModel = new Ext.grid.ColumnModel({
                columns: [
					{header: "<fmt:message key='aimir.contractNumber'/>", dataIndex: 'CONTRACTNUMBER' }
					,{header: "<fmt:message key='aimir.poc.dateTime'/>", dataIndex: 'YYYYMMDDHHMM' }
					,{header: "<fmt:message key='aimir.balance'/>", dataIndex: 'BALANCE', align:'right', width: width/10-10}
	                ,{header: "<fmt:message key='aimir.usage'/>", dataIndex: 'USAGE'}
	                ,{header: "<fmt:message key='aimir.writetime'/>", dataIndex: 'WRITEDATE'}
	                ,{header: "<fmt:message key='aimir.owecredit'/>", dataIndex: 'RESERVATION01', align:'right'}
	                ,{header: "<fmt:message key='aimir.owecredit'/> (<fmt:message key='aimir.threshold'/>)", dataIndex: 'RESERVATION02', align:'right'}
	                ,{header: "3", dataIndex: 'RESERVATION03', align:'right'}
	                ,{header: "4", dataIndex: 'RESERVATION04', align:'right'}
	                ,{header: "5", dataIndex: 'RESERVATION05', align:'right'}
                ],
                defaults: {
                     sortable: true
                    ,menuDisabled: true
                    ,width: width/10
                }
            });

            if(stsChargeHistoryGridOn == false) {
                stsChargeHistoryGrid = new Ext.grid.GridPanel({
                      store: stsChargeHistoryStore,
                      colModel : stsChargeHistoryColModel,
                      autoScroll:false,
                      height: 310,
                      stripeRows : true,
                      columnLines: true,
                      loadMask:{
                          msg: 'loading...'
                      },
                      renderTo: 'stsChargeHistory',
                      viewConfig: {
                          //forceFit:true,
                          enableRowBody:true,
                          showPreview:true,
                          emptyText: 'No data to display'
                      },
                      // paging bar on the bottom
                      bbar: new Ext.PagingToolbar({
                          pageSize: 10,
                          store: stsChargeHistoryStore,
                          displayInfo: true,
                          displayMsg: ' {0} - {1} / {2}'
                      })
                  });
                stsChargeHistoryGridOn = true;
            } else {
                var bottomToolbar = stsChargeHistoryGrid.getBottomToolbar();
                stsChargeHistoryGrid.reconfigure(stsChargeHistoryStore, stsChargeHistoryColModel);
                bottomToolbar.bindStore(stsChargeHistoryStore);
            }
        }
        
        function getSTSChargeHistoryData() {
        	if(validateSearchCondition($('#searchSTSStartDate').val(), $('#searchSTSEndDate').val())){
        		stsChargeHistoryStore.baseParams = {};
        		stsChargeHistoryStore.setBaseParam('contractNumber', selectedContractNumber);
                stsChargeHistoryStore.setBaseParam('meterId', selectedMdsId);
                stsChargeHistoryStore.setBaseParam('startDate', $("#searchSTSStartDate").val());
                stsChargeHistoryStore.setBaseParam('endDate', $("#searchSTSEndDate").val());
                stsChargeHistoryStore.load({params:{start: 0, limit: 10}});                
            }
        }
         */
        
        /**
         * 숫자만 입력. 정수
         * focusin:comma 제거, focusout:comma 추가
         */
        // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
        function removeCommaForInt(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.value;
            val = removeCharForInt(val);
            val = removeFstZeroForInt(val);
            src.value = val;
            //src.focus();
        }

        // 숫자 이외 문자 제거
        function removeCharForInt(val) {
            var num = val.replace(/[\D]/g, '');
            return num;
        }

        // 앞에 0 제거
        function removeFstZeroForInt(val) {
            var pattern = /(^0*)(\d+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        /**
         * 숫자만 입력. 실수
         * focusin:comma 제거, focusout:comma 추가
         */
        // inputbox에 focus가 들어오면 숫자 이외 문자 모두 제거
        function removeCommaForReal(ev, src) {
            var evCode = (window.netscape) ? ev.which : event.keyCode;
            if (evCode >= 37 && evCode <= 40) return;

            var val = src.value;
            val = removeCharForReal(val);
            val = removeFstZeroForReal(val);
            src.value = val;
            src.focus();
        }

        // 숫자 이외 문자 제거:소수점 허용
        function removeCharForReal(val) {
            var num = val.replace(/[^\d\.]/g, '');
            var idx = 0;
            var len = 0;

            if (num.indexOf('.', num.indexOf('.')+1) != -1) {
                idx = num.indexOf('.');
                len = num.length;
                num = num.substring(0, idx+1) + num.substring(idx+1, len).replace(/\./g, '');
            }

            return num;
        }

        // 앞에 0 제거:소수점포함
        function removeFstZeroForReal(val) {
            var pattern = /(^0*)([\d\.]+$)/g;

            if (pattern.test(val)) {
                val = val.replace(pattern, '$2');
            }
            return val;
        }

        // inputbox에서 focus가 나가면 comma추가
        function addComma(src) {
            var pattern = /(^-?[0-9]+)([0-9]{3})/;
            var strVal = src.value;

            while(pattern.test(strVal)) {
                strVal = strVal.replace(pattern, '$1,$2');
            }

            src.value = strVal;
        }
        
        //선택한 계약의 미터모델이 Relay 기능이 가능한 모델인지 검색 후 가능한 모델일 경우 화면에 Relay On/Off 버튼을 보여준다.
        function enableRelay(selectedModelName) {
        	$('#relayControlButton').hide();
        	$('#commandResultDiv').hide();
            $.getJSON("${ctx}/gadget/prepaymentMgmt/getRelayEnableModel.do",
                    {devicemodelName : selectedModelName},
                    function(result) {
                        var namesOfContain = result.namesOfContain;
                        if (namesOfContain.length > 0) {
                            for (var i = 0; i < namesOfContain.length; i++) {
                            	switch (namesOfContain[i]) {
                                    case 'relayControl':
                                        $('#relayControlButton').show();
                                        $('#commandResultDiv').show();
                                        break;
                                }
                            }
                        }
                    }
                );
        }

        function enableSts(selectedModelName) {
        	$("#stsLog").hide();
        	$('#stsControl_SUNI').hide();
        	$('#stsControl_WASION').hide();
        	$('#commandResultDiv').hide();
        	
            $.getJSON("${ctx}/gadget/prepaymentMgmt/getRelayEnableModel.do",
                    {devicemodelName : selectedModelName},
                    function(result) {
                        var namesOfContain = result.namesOfContain;
                        if (namesOfContain.length > 0) {
                            for (var i = 0; i < namesOfContain.length; i++) {
                            	switch (namesOfContain[i]) {
                                    case 'stsControl_SUNI':
                                    	$("#stsLog").show();
                                    	$('#stsControl_SUNI').show();
                                    	$('#commandResultDiv').show();
                                    	break;
                                    case 'stsControl_WASION':
                                    	$("#stsLog").show();
                                    	$('#stsControl_WASION').show();
                                    	$('#commandResultDiv').show();
                                    	break;    
                                }
                            }
                        }
                    }
                );
        }
        
        // 잔액통보주기 시간 selectbox 데이터를 조회한다.
        var getAllHours = function() {

            var params = {"contractNumber" : selectedContractNumber};

            $.getJSON("${ctx}/gadget/prepaymentMgmt/getAllHours.do",
                    params,
                    function(result) {
                        var hours = result.hours;

                        // 시간 콤보박스 생성
                        $("#notifyHour").pureSelect(hours);
                        $("#notifyHour").selectbox();
                    }
                );
        };

        // 선택된 주기에 따라 요일항목 enable/disable
        function controlDayofWeek() {
            var periodValue = $("#notifyPeriod").val();
            if (periodValue == "2") {
                $("#dayofweek").show();
                $("#intervalDailyDiv").hide();
                $("#intervalWeeklyDiv").show();
                $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.weeks"/>");
                $("#intervalDaily option:eq(0)").attr("selected", "true");
                $("#intervalDaily").selectbox();
            } else {
                $("#mon").attr("checked", false);
                $("#tue").attr("checked", false);
                $("#wed").attr("checked", false);
                $("#thu").attr("checked", false);
                $("#fri").attr("checked", false);
                $("#sat").attr("checked", false);
                $("#sun").attr("checked", false);
                $("#dayofweek").hide();
                $("#intervalWeeklyDiv").hide();
                $("#intervalDailyDiv").show();
                $("#intervalUnit").text("<fmt:message key="aimir.hems.prepayment.days"/>");
                $("#intervalWeekly option:eq(0)").attr("selected", "true");
                $("#intervalWeekly").selectbox();
            }
        }


        //저장 후 콜백
        function save_callback(json, textStatus) {
            if (json.status == "success") {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>", getNotificationInfo);
            } else {
                Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
            }
        }

        // grid column tooltip
        function addTooltip(value, metadata) {
            if (value != null && value != "" && metadata != null) {
                metadata.attr = 'ext:qtip="' + value + '"';
            }
            return value;
        }
        
        function changeChargeDateStatus() {

        	if($('#selectLastChargeDate').val() == 'enable') {
        		$('#searchlastChargeDate_2').show();
        	} else {
        		$('#searchlastChargeDate_2').hide();
        	}

        }

        function cmdRelayOn() {
            //비동기 설정
            $.ajaxSetup({
                async : true
            });
            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');

            $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOn.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                Ext.Msg.hide();
                
                var rtnStr = returnData.rtnStr;
                var cmdResult = returnData.status;
                
                if (returnData.isGPRSModem) {
                	cmdResult = returnData.status + " - Please refer to [Async Commm. Log] section for detail information."; 
                }
                
	            $('#commandResult').val(cmdResult);
                

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }
        
        function cmdRelayOff() {
            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');
            $.getJSON('${ctx}/gadget/device/command/cmdRemotePowerOff.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                
                Ext.Msg.hide();
                
                var rtnStr = returnData.rtnStr;
                var cmdResult = returnData.status;
                
                if (returnData.isGPRSModem) {
                	cmdResult = returnData.status + " - Please refer to [Async Commm. Log] section for detail information."; 
                }
                
                $('#commandResult').val(cmdResult);
                

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }

        function cmdRelayStatus() {
            //비동기 설정
            $.ajaxSetup({
                async : true
            });

            $('#commandResult').val("");
            Ext.Msg.wait('Waiting for response.', 'Wait !');

            $.getJSON('${ctx}/gadget/device/command/cmdRemoteGetStatus.do', {
                'target' : selectedMeterId,
                'mcuId' : selectedMcuId,
                'loginId' : loginId
            }, function(returnData) {
                //원래 동기방식으로 설정
                $.ajaxSetup({
                    async : false
                });
                
                Ext.Msg.hide();
                
                var rtnStr = returnData.rtnStr;
                var cmdResult = returnData.status;
                
                if (returnData.isGPRSModem) {
                	cmdResult = returnData.status + " - Please refer to [Async Commm. Log] section for detail information."; 
                }
                
                $('#commandResult').val(cmdResult);
                

                if (rtnStr == 'Success') {
                    Ext.Msg.alert('', 'Success!', null, null);
                } else {
                    Ext.Msg.alert('', 'Done', null, null);
                }

            });
        }
        
        /**
         * 현재 일자 조회
         */
        function initDateCondition() {
        	getMonthlyYearCombo();
        }
        
        var tariffSTSGrid;
        var tariffSTSStore;
        function drawTariffGrid(tariffParam, tariffInfo) {
        	
        	tariffSTSStore = new Ext.data.JsonStore({
                fields : [ {name:'cons'},{name:'fixedRate'},{name:'varRate'},{name:'condRate1'},{name:'condRate2'}]
            });
			tariffSTSStore.loadData(tariffInfo);
	       var colModel = new Ext.grid.ColumnModel({
	            defaults : {
	                width : 80,
	                height : 100,
	                sortable : true
	            },
	            columns : [{
	                width : 100,
	                header : "<b>Supply Min Size(kWh)</b>",
	                dataIndex : "cons",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'cons'
                    })
	            }, {
	                header : "<b>Service Charge</b>",
	                width : 90,
	                dataIndex : "fixedRate",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'fixedRate'
                    })
	            }, {
	                header : "<b>Var Rate</b>",
	                width : 80,
	                dataIndex : "varRate",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'varRate'
                    })
	            }, {
	                header : "<b>Gove. Subsidy</b>",
	                width : 100,
	                dataIndex : "condRate1",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'condRate1'
                    })
	            }, {
	                header : "<b>Utility Relief</b>",
	                width : 100,
	                dataIndex : "condRate2",
	                renderer: addTooltip,
	                editor: new Ext.form.TextField({
                        id : 'condRate2'
                    })
	            }]
	        });
	       
	       tariffSTSGrid = new Ext.grid.EditorGridPanel({
	   			 store: tariffSTSStore,
	   			 autoScroll : true,
	   			 loadMask: true,
	   			 colModel: colModel,
	   			 listeners: {
	   				afteredit: function(e) {
	   					
	   				}
	   			 },
	   			 viewConfig: {forceFit: true},
	   			 autoScroll : true,
	             scroll : true,
	             stripeRows : true,
	             columnLines : true,
	             loadMask : {
	                msg : 'loading...'
	             },
	  		      width: 600,
	  		      height: 300,
	  		      tbar:[{
	  		    	  iconCls: 'icon-obis-add',
	  		    	  text: "<b><fmt:message key='aimir.add'/></b>",
	  		    	  handler: function() {
	  		    		addRecordData();
	  		    	  }
	  		      },{
	  		    	  iconCls: 'icon-obis-delete',
	  		    	  text: "<b><fmt:message key='aimir.button.delete'/></b>",
	  		    	  handler: function() {
	  		    		delRecordData();
	  		    	  }
	  		      }]
	   		  });
	
	        $('#setTariffDiv').empty();

	        var imgWin = new Ext.Window({
	            title : 'Set Tariff',
	            id : 'setTariffWinId',
	            applyTo : 'setTariffDiv',
	            autoScroll : true,
	            autoHeight : true,
	            pageX : 400,
	            pageY : 130,
	            width : 600,
	            height : 300,
	            items : tariffSTSGrid,
	            buttons : [{text : '<fmt:message key="aimir.save2"/>',
	            	handler : function() {	
	            		$.ajaxSetup({
	       	                async : true
	       	            });
	            		
	            		var records = tariffSTSStore.data.items;
	            		var flag = false;
	            		
	            		//마지막라인에 대해서 유효성 체크
	            		if(records.length-1 < 0) {
	            			Ext.Msg.alert("<fmt:message key='aimir.error'/>","<fmt:message key='aimir.data.empty'/>");
	            			flag = false;
	            			return false;
	            		}
	                    
	            		flag = validateMandatory(tariffSTSGrid,"<fmt:message key='aimir.mandatoryValue'/>");
        	        	if(flag) {
        	        		var saveArrList = new Array();
		            		for(var i = 0; i<records.length; i++) {
		            			var saveArr = new Array();
		            			saveArr.push({
		            				'cons' : records[i].data.cons,
				                    'fixedRate' : records[i].data.fixedRate,
				                    'varRate' : records[i].data.varRate,
				                    'condRate1' : records[i].data.condRate1,
				                    'condRate2' : records[i].data.condRate2
			            		})
			            		
			            		saveArrList.push(saveArr);
		            		}
		            		
		            		Ext.Msg.show({
	                       		title: '<b><fmt:message key='aimir.setting'/><b/>',
	                       		msg: '<fmt:message key='aimir.wouldSave'/>',
	                       		buttons : Ext.MessageBox.OKCANCEL,
	                       		fn : function(btn) {
				                	if(btn == 'ok') {
				                		$.ajaxSetup({
					       	                async : true
					       	            });
				                		$('#commandResult').val("");
				                		Ext.Msg.wait('Waiting for response.', 'Wait !');
					            		
					            		$.post('${ctx}/gadget/device/command/cmdSetTariff.do',{
											'supplierId':supplierId,		            			
											'target':selectedMeterId,
											'tariffType':tariffParam.tariffType,
											'yyyymmdd':tariffParam.yyyymmdd,
											'condLimit1':tariffParam.condLimit1,
											'condLimit2':tariffParam.condLimit2,
											'param':JSON.stringify(saveArrList)
					                    }, function(returnData) {
					                    	Ext.Msg.hide();
					                    	if(returnData.result != null && returnData.result.indexOf("FAIL") > -1) {
					                    		Ext.Msg.show({
						                       		title: '',
						                       		msg: returnData.result,
						                       		buttons : Ext.MessageBox.OK
					                       		});   
				                            } else {
				                               imgWin.hide(this);    				                            	
				                        	   Ext.Msg.show({
						                       		title: '',
						                       		msg: "Please check the Result on Prepayment customer gadget.",
						                       		buttons : Ext.MessageBox.OK
					                       		});  
				                            }
					                    });
				                	} else {
				                		return;
				                	}
				              }
                       		}); 
        	        	}
                	}},
                	{text : '<fmt:message key="aimir.board.close"/>',
                	handler : function() {
                		
                		imgWin.hide(this);        		
                	}}],
	            closeAction : 'hide',
	            onHide : function() {
	            }
	        });
	        Ext.getCmp('setTariffWinId').show();
        }
        
       function addRecordData() {
        	$.ajaxSetup({
                async : false
            });
			var flag = true;
			
			var store = tariffSTSGrid.getStore();
			
        	if(store.data.length > 0) {
	        	var preRecord = store.data.last().data;
                
	        	flag = validate(tariffSTSGrid,tariffSTSGrid.lastEdit.row,preRecord.cons,"<fmt:message key='aimir.mandatoryValue'/>",0);
	        	
	        	if(flag && (isNaN(preRecord.cons) || (preRecord.cons + "").indexOf(".") >= 0)) {
	        		flag=false;
	        		Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(integer type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSGrid.lastEdit.row, 0); return false;});
	        	}
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSGrid.lastEdit.row,preRecord.fixedRate,"<fmt:message key='aimir.mandatoryValue'/>",1);
                }
                
                if(flag && isNaN(preRecord.fixedRate)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSGrid.lastEdit.row, 1); return false;});	
                }
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSGrid.lastEdit.row,preRecord.varRate,"<fmt:message key='aimir.mandatoryValue'/>",2);
                }
                
                if(flag && isNaN(preRecord.varRate)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSGrid.lastEdit.row, 2); return false;});	
                }
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSGrid.lastEdit.row,preRecord.condRate1,"<fmt:message key='aimir.mandatoryValue'/>",3);
                }
                
                if(flag && isNaN(preRecord.condRate1)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSGrid.lastEdit.row, 3); return false;});	
                }
                
                if(flag) {
                	flag = validate(tariffSTSGrid,tariffSTSGrid.lastEdit.row,preRecord.condRate2,"<fmt:message key='aimir.mandatoryValue'/>",4);
                }
                
                if(flag && isNaN(preRecord.condRate2)) {
                	flag=false;
                	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { tariffSTSGrid.startEditing(tariffSTSGrid.lastEdit.row, 4); return false;});	
                }

        	}

        	if(flag) {
                var Plant = store.recordType;
	            var p = new Plant({
	                consumption : "",
	                price : "",
	            });
	            var length = store.getCount();
	            tariffSTSGrid.stopEditing();
	            tariffSTSStore.insert(length, p);
	            tariffSTSGrid.startEditing(length, 0);
	            tariffSTSGrid.getSelectionModel().selectLastRow();
        	}
        }
       
       function delRecordData() {
    	   tariffSTSGrid.stopEditing();
           var s = tariffSTSGrid.getSelectionModel().selection.record
           tariffSTSStore.remove(s);  
       }
       
       function validate(grid,rec,data,msg,row) {
    	   var bol;    	   
    	   if(data == null || (data+"" == "")) {
    		   bol = false;     
	        	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", msg,
	    				function() {
	    			grid.startEditing(rec, row);
	        		return bol;
	    		});
	       	} else {
				bol = true;	       		
	       		return bol;
	       	}
    	   return bol;
       }
       
       function validateMandatory(grid,msg) {
    	   $.ajaxSetup({
    	        async : false
    	    });

    	   var items = grid.store.data.items;
    	   for(var i = 0; i<items.length; i++) {
    		   var obj = items[i].data;
    		   var flag = validate(grid,i,obj.cons,msg,0);
    		   if(flag && (isNaN(obj.cons) || (obj.cons + "").indexOf(".") >= 0)) {
	        		flag=false;
	        		Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(integer type)",
		    				function() { grid.startEditing(i, 0); return false;});
	        	}
               
               if(flag) {
               	flag = validate(grid,i,obj.fixedRate,"<fmt:message key='aimir.mandatoryValue'/>",1);
               }
               if(flag && isNaN(obj.fixedRate)) {
               	flag=false;
               	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
		    				function() { grid.startEditing(i, 1); return false;});	
               }
               
               if(flag) {
                  	flag = validate(grid,i,obj.varRate,"<fmt:message key='aimir.mandatoryValue'/>",2);
               }
               if(flag && isNaN(obj.varRate)) {
                  	flag=false;
                  	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
   		    				function() { grid.startEditing(i, 2); return false;});	
                }
                  
               if(flag) {
                  	flag = validate(grid,i,obj.condRate1,"<fmt:message key='aimir.mandatoryValue'/>",3);
               }
               if(flag && isNaN(obj.condRate1)) {
                  	flag=false;
                  	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
   		    				function() { grid.startEditing(i, 3); return false;});	
               }
                     
               if(flag) {
                  	flag = validate(grid,i,obj.condRate2,"<fmt:message key='aimir.mandatoryValue'/>",4);
               }
               if(flag && isNaN(obj.condRate2)) {
                  	flag=false;
                  	Ext.Msg.alert("<fmt:message key='aimir.warning'/>", "Pelase input Number.(double type)",
   		    				function() { grid.startEditing(i, 4); return false;});	
               }
               if(!flag) {
            	   return flag;
               }
    	   }
    	   return flag;
       }
        
		function showNetCharge() {
        	$("#tCountDiv").hide();
        	$("#tCountDiv2").hide();
        	$("#fcModeDiv").hide();
        	$("#fwKeyDiv").hide();
        	$("#selectDate").show();
            initDateCondition();
        }
        
        function showTCount() { 
        	$("#selectDate").hide();
        	$("#fcModeDiv").hide();
        	$("#fwKeyDiv").hide();
        	$("#tCountDiv").show();
        	$("#tCountDiv2").hide();
        }
        
        function showTCount2() { 
        	$("#selectDate").hide();
        	$("#fcModeDiv").hide();
        	$("#fwKeyDiv").hide();
        	$("#tCountDiv").hide();
        	$("#tCountDiv2").show();
        }
        
        function showFcMode() {
        	$("#tCountDiv").hide();
        	$("#tCountDiv2").hide();
        	$("#selectDate").hide();
        	$("#fwKeyDiv").hide();
        	$("#fcModeDiv").show();
        }
        
        function showPaymentMode() {
        	$("#tCountDiv").hide();
        	$("#tCountDiv2").hide();
        	$("#selectDate").hide();
        	$("#creditDiv").hide();
        	$("#fcModeDiv").hide();
        	$("#fwKeyDiv").hide();
        }
        
        function showSTSSetup() {
        	$("#tCountDiv").hide();
        	$("#tCountDiv2").hide();
        	$("#selectDate").hide();
        	$("#creditDiv").hide();
        	$("#fcModeDiv").hide();
        	$("#fwKeyDiv").hide();
        	$("#setupDiv").show();
        }
        
        function showFwUpdateKey() {
        	$("#tCountDiv").hide();
        	$("#tCountDiv2").hide();
        	$("#selectDate").hide();
        	$("#creditDiv").hide();
        	$("#fcModeDiv").hide();
        	$("#fwKeyDiv").show();
        }
        
        function clearAndHideNetChargeButton() {
            $('#monthlyYearCombo').val('');
            $('#monthlyMonthCombo').val('');
            $('#selectDate').hide();
        }
        
        function clearAndHideGetSTSTokenButton() {
            $('#tCount').val('');
            $('#tCount2').val('');
            $('#tCountDiv').hide();
            $('#tCountDiv2').hide();
        }
        
        function clearAndHideFcModeButton() {
            $('#fcMode').val('');
            $('#fcModeDiv').hide();
        }
        
        function clearAndHideFwKeyButton() {
        	$('#keyHex').val('');
            $('#keyNo').val('');
            $('#fwKeyDiv').hide();
        }
        
     	// Send STS Token to Target
        function sendSTSTokenCmd(_target, _tNumber) {
        	$('#commandResult').val("");
        	Ext.Msg.confirm(
        			'Confirm', 
        			'Send Token by ['+_tNumber+'] ',
        			function(btn){  
        				if(btn == 'yes') {
        					Ext.Msg.wait('Waiting for response.', 'Wait !');
        					$.getJSON('${ctx}/gadget/device/command/cmdSetSTSToken.do', {
	    	        			'target' : _target,
	    	                    'token'  : _tNumber,
	    	                    'isClear': true
	    	                }, function(returnData) {
	    	                	Ext.Msg.hide();
	    	                	$('#commandResult').val(returnData.result);
	                            Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	    	                });
        				}else{
        					// close
        					return;
        				}
        			}
        			); //~confirm
        }
        
        var cmdSTSHandler = {
        	sendSTSMessage: function() {
        		$('#commandResult').val("");
        		var charLenText = 'character : ';
                var msg =  new Ext.Window({
	      		    title: '<b>Sent STSMessage<b/>',
	      		    modal: true, closable:true, resizable: true,
	      		    width:300, height:250,
	      		    border:true, plain:false,                      
	      		    items:[{
	      		        xtype: 'panel',
	      		        frame: false, border: false,
	      		        items:{
	      		          id: 'msg_form',
	      		          xtype: 'form',
	      		          bodyStyle:'padding:10px',
	      		          layout:'fit',
	      		          width:300,
	      		          height:250,
	      		          frame: false, border: false,
	      		          items: [
	      		            {xtype: 'label', html:'<div style="text-align:left;">' + "Message" +'</div>',  anchor: '100%'}, 
	      		            {xtype: 'textarea', 
	      		            	id: 'msg_id', name: 'msg_name', 
	      		            anchor: '100', height:'80', width:'250', 
	      		            enableKeyEvents:true, 
	      		            listeners:{
	      		            	keyup:function(t,s){
	      		            		var textLen=Ext.getCmp('msg_id').getValue().length;
	      		            		Ext.getCmp('msg_labelId').setText(charLenText + textLen)
	      		            	}
	      		            }},
	      		            {xtype: 'label', id:'msg_labelId', text:charLenText,  anchor: '100%'},]
	      		        }
	      		    }],
	      		    buttons: [{
	      		      text: 'Ok',
	      		      handler: function() { 
	      		        var text = Ext.getCmp('msg_id').getValue();
	      		        if(text.length > 160) {
	      		        	Ext.Msg.alert("<fmt:message key='aimir.warning'/>","<fmt:message key='aimir.limited.char255'/>".replace('255','160'));
	      		        	return;
	      		        } else {
	      		        	Ext.Msg.show({
	      	               		title: '<b>Sent STSMessage<b/>',
	      	               		msg: '<fmt:message key='aimir.wouldSend'/>',
	      	               		buttons : Ext.MessageBox.OKCANCEL,
	      	               		fn : function(btn) {
	      		                	if(btn == 'ok') {
	      		                		msg.close();	      		                		
	      		                		Ext.Msg.wait('Waiting for response.', 'Wait !');

	      		                        $.getJSON('${ctx}/gadget/device/command/cmdSetMessage.do', {
	      		                        	'target' : selectedMeterId,
	      		                        	'supplierId' : supplierId,
	      		                            'message' : text
	      		                        }, function(returnData) {
	      		                            Ext.Msg.hide();
      	                                	Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	      		                        });
	      		                	} else {
	      		                		return;
	      		                	}
	      		              }
	      	           		});
	      		        }
	      		      }
	      		    }, {
	      		      text: '<fmt:message key="aimir.cancel"/>',
	      		      handler: function() {
	      		    	msg.close();
	      		      }
	      		    }]
        		});
                msg.show();
        	},
        	getEmergencyCredit: function() {
        		$('#commandResult').val("");        		
        		Ext.Msg.show({
               		title: '<b>Get EmergencyCredit<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                		
	                		$.getJSON('${ctx}/gadget/device/command/cmdGetEmergencyCredit.do', {
	                			'target' : selectedMeterId,
	                        }, function(returnData) {
	                        	Ext.Msg.hide();
	                            Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                        });
	                	} else {
	                		return;
	                	}
	              }
           		});     
        	},
        	getRemainingCredit: function() {
        		$('#commandResult').val("");        		
        		Ext.Msg.show({
               		title: '<b>Get RemainingCredit<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                		$.getJSON('${ctx}/gadget/device/command/cmdGetRemainingCredit.do', {
	                			'target' : selectedMeterId,
	                        }, function(returnData) {
	                        	Ext.Msg.hide();
	                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                        });
	                	} else {
	                		return;
	                	}
	              }
           		}); 
        	},
        	getSTSToken: function() {
        		$('#commandResult').val("");
				if ($('#tCount').val() < 1 || $('#tCount').val() > 5) {
					Ext.Msg.alert('', 'FAIL: Range 1 ~ 5', null, null);
				} else {
					Ext.Msg.show({
	               		title: '<b>Get STSToken<b/>',
	               		msg: '<fmt:message key='aimir.wouldGet'/>',
	               		buttons : Ext.MessageBox.OKCANCEL,
	               		fn : function(btn) {
		                	if(btn == 'ok') {
		                		Ext.Msg.wait('Waiting for response.', 'Wait !');
		    	        		$.getJSON('${ctx}/gadget/device/command/cmdGetSTSToken.do', {
		    	        			'target' : selectedMeterId,
		    	                    'tCount' : $('#tCount').val()
		    	                }, function(returnData) {
		    	                	Ext.Msg.hide();
		                            Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
		    	                });
		                	} else {
		                		return;
		                	}
		              }
	           		}); 					
				}
        	},
        	getSTSToken2: function() {
        		$('#commandResult').val("");
				if ($('#tCount2').val() < 1 || $('#tCount2').val() > 5) {
					Ext.Msg.alert('', 'FAIL: Range 1 ~ 5', null, null);
				} else {
					Ext.Msg.show({
	               		title: '<b>Get STSToken<b/>',
	               		msg: '<fmt:message key='aimir.wouldGet'/>',
	               		buttons : Ext.MessageBox.OKCANCEL,
	               		fn : function(btn) {
		                	if(btn == 'ok') {
		                		Ext.Msg.wait('Waiting for response.', 'Wait !');
		    	        		$.getJSON('${ctx}/gadget/device/command/cmdGetSTSToken.do', {
		    	        			'target' : selectedMeterId,
		    	                    'tCount' : $('#tCount2').val()
		    	                }, function(returnData) {
		    	                	Ext.Msg.hide();
		                            Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
		    	                });
		                	} else {
		                		return;
		                	}
		              }
	           		}); 					
				}
        	},
        	setSTSToken: function(token) { 
        		// 입력칸 출력
        		Ext.Msg.prompt(
						'<b>Send STS Token</b>',
						'Please enter token number.<br/>(Number Only).',
						function(btn,text) {
							if(btn=='ok'){
								var tNumber = text.trim().replace(/[\D]/g, '');
								if(text != null && tNumber.length == 20){
									// Send Token
									sendSTSTokenCmd(selectedMeterId,tNumber);
								}else{
									Ext.Msg.alert('<fmt:message key='aimir.message'/>', 'Token number is not valid.');
								}
							}
						},
						null,
						false, 
						token
				); //~prompt
        		
        	}, //~setSTSToken
        	setInitialCreidt: function() {
        		$('#commandResult').val("");        		
				Ext.Msg.show({
               		title: '<b>Set Initial Credit<b/>',
               		msg: '<fmt:message key='aimir.wouldSet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	    	        		$.getJSON('${ctx}/gadget/device/command/cmdSetInitialCredit.do', {
	    	        			'target' : selectedMeterId
	    	                }, function(returnData) {
	    	                    Ext.Msg.hide();
	    	                    Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	    	                });
	                	} else {
	                		return;
	                	}
	              }
           		}); 					

        	},
        	setTariff: function() {
        		var tariffType = '';
                var searchWin = new Ext.Window({
                  title: '<b><fmt:message key='aimir.setting'/></b>',
                  modal: true, closable:true, resizable: true,
                  width:300, height:250,
                  border:true, plain:false,                      
                  items:[{
                      xtype: 'panel',
                      frame: false, border: false,
                      items:{
                        id: 'reTypeAmount_form',
                        xtype: 'form',
                        bodyStyle:'padding:10px',
                        labelWidth: 100,
                        frame: false, border: false,
                        items: [{
                          xtype: 'label', html:'<div style="text-align:left;">' + 'Please input tariff Information.' +'</div>',  anchor: '100%'
                        },{
                            xtype: 'combo',
                            id:'tariffType_id', name: 'tariffType_name', value:'Select...',          
                            fieldLabel: 'Tariff', triggerAction: 'all', editable: false, mode: 'local',
                            store: new Ext.data.JsonStore({
                              autoLoad   : true,
                              baseParams: {serviceType : selectedServiceType, supplierId : supplierId},
                              url: '${ctx}/gadget/system/supplier/getTariffType.do',
                              storeId: 'tariffTypeListStore',
                              root: 'result',
                              idProperty: 'name',
                              fields: ['name',{name: 'id', type: 'int'}],
                              listeners: {
                                load: function(store, records, options){                     	
                                  Ext.getCmp('tariffType_id').setValue(records[0].data.name);
                                  tariffType = records[0].data.id;
                                }
                              }
                            }),
                            valueField: 'id', displayField: 'name',
                            anchor: '100%',
                            listeners: {
                              render: function() {
                                this.store.load();
                              },
                              select : function(combo, record, index){
                                Ext.getCmp('tariffType_id').setValue(record.data.name);
                                tariffType = record.data.id;
                              }
                            }
                        }, {
                          xtype: 'datefield', fieldLabel: 'Tariff Date', id: 'tariffDate_id', name: 'tariffDate_name', anchor: '100%'
                        },{
                      	  xtype: 'textfield', fieldLabel: 'Gov. Subsidy Limit', id: 'condLimit1_id', name: 'condLimit1_name', anchor: '100%'  
                        },{
                      	  xtype: 'textfield', fieldLabel: 'Utility Relief Limit', id: 'condLimit2_id', name: 'condLimit2_name', anchor: '100%'  
                        }]
                      }
                  }],
                  
                  buttons: [{
                    text: 'Ok',
                    handler: function() {
      				  var flag = true;
      				  if(flag && Ext.getCmp('tariffType_id').getValue() == null) {
                  		  Ext.Msg.alert("","Please select Tariff");
                  		  flag = false;
                  		  return flag;
                  	  }
      				  
      				  if(flag && (Ext.getCmp('tariffDate_id').value == null || Ext.getCmp('tariffDate_id').value == '')) {
                  		  Ext.Msg.alert("","Please input Tariff Date.");
                  		  flag = false;
                  		  return flag;
                  	  }
                  	              	  
                  	  if(flag && (Ext.getCmp('tariffDate_id').value == null || Ext.getCmp('tariffDate_id').value == '')) {
                  		  Ext.Msg.alert("","Please input Tariff Date.");
                  		  flag = false;
                  		  return flag;
                  	  }
                  	  
                  	  if(flag && ($('#condLimit1_id').val() == null || $('#condLimit1_id').val() == '') || isNaN($('#condLimit1_id').val())) {
                  		  Ext.Msg.alert("","Please input Gove. Subsidy limit.");
                  		  flag = false;
                  		  return flag;
                  	  }
                  	  
                  	  if(flag && ($('#condLimit2_id').val() == null || $('#condLimit2_id').val() == '' || isNaN($('#condLimit2_id').val()))) {
                  		  Ext.Msg.alert("","Please input Utility Relief limit");
                  		  flag = false;
                  		  return flag;
                  	  }
                  	 //dd/mm/yyyy 포맷으로 저장되어 있음
                		 var yyyymmdd = Ext.getCmp('tariffDate_id').getValue().format('Ymd')
                  	  var tariffParam = {
                     	    'tariffType' : tariffType,
                  	  	'yyyymmdd' : yyyymmdd,
                  	  	'condLimit1' : $('#condLimit1_id').val(),
                  	  	'condLimit2' : $('#condLimit2_id').val()
                  	  }
                  	  
                  	  if(flag) {
                  		  searchWin.close();
                  		  var tariffStr;
          	              $.getJSON('${ctx}/gadget/system/supplier/getSTSTariff.do'
          	                      , {'tariffIndexId' : tariffType,
          	            	  		'yyyymmdd' : yyyymmdd
          	              }, function(json) {
          	            	  drawTariffGrid(tariffParam, json.tariffInfo); 
                            });
                  	  }
                    }
                  }, {
                    text: '<fmt:message key="aimir.cancel"/>',
                    handler: function() {
                  	  searchWin.close();
                    }
                  }]
                });

                searchWin.show(this);
        	},
        	getTariff: function() {
        		var tariffType = '';
                var searchWin = new Ext.Window({
                  title: '<b><fmt:message key='aimir.setting'/></b>',
                  modal: true, closable:true, resizable: true,
                  width:300, height:130,
                  border:true, plain:false,                      
                  items:[{
                      xtype: 'panel',
                      frame: false, border: false,
                      items:{
                        id: 'getTariffMode_form',
                        xtype: 'form',
                        bodyStyle:'padding:10px',
                        labelWidth: 100,
                        frame: false, border: false,
                        items: [{
                          xtype: 'label', html:'<div style="text-align:left;">' + 'Please select tariff mode.' +'</div>',  anchor: '100%'
                        },{
                          xtype: 'combo',
                          id:'tariffMode_id', name: 'tariffMode_name', value:'Select...',          
                          fieldLabel: 'Tariff mode', triggerAction: 'all', editable: false, mode: 'local',
                          store: new Ext.data.JsonStore({
                        	  root:'datas',
                        	  fields: ['code','name'],
                        	  autoLoad: true,
                        	  data: {datas: [
     						  	 {"code":0,"name":'Current tariff'},
     						  	{"code":1,"name":'Future tariff'}
                        	  ]}
                          }),
                          valueField: 'code', displayField: 'name',
                          anchor: '100%',
                          listeners: {
                            select : function(combo, record, index){
                              Ext.getCmp('tariffMode_id').setValue(record.data.name);
                              tariffType = record.data.code;
                            }
                          }
                        }]
                      }
                  }],
                  
                  buttons: [{
                    text: 'Ok',
                    handler: function() {
      				  var flag = true;

                  	  if((flag && (Ext.getCmp('tariffMode_id').value == null || Ext.getCmp('tariffMode_id').value == ''))
                  			  || (Ext.getCmp('tariffMode_id').value == 'Select...')) {
                  		  Ext.Msg.alert("","Please select Tariff mode");
                  		  flag = false;
                  		  return flag;
                  	  }
                  	  
                  	  if(flag) {
                  		  searchWin.close();
          	              
                  		  Ext.Msg.show({
                         		title: '<b><fmt:message key='aimir.setting'/><b/>',
                         		msg: '<fmt:message key='aimir.wouldSave'/>?',
                         		buttons : Ext.MessageBox.OKCANCEL,
                         		fn : function(btn) {
    			                	if(btn == 'ok') {
    			                		$.ajaxSetup({
    				       	                async : true
    				       	            });
    			                		$('#commandResult').val("");
    			                		Ext.Msg.wait('Waiting for response.', 'Wait !');
    				               		
    				       				$.getJSON('${ctx}/gadget/device/command/cmdGetTariff.do', {
    				       					'supplierId':supplierId,		
    				       					'target' : selectedMeterId,
    				       					'tariffMode' : tariffType
    				                       }, function(returnData) {
    				                    	   Ext.Msg.hide();
    				                           if(returnData.result != null && returnData.result.indexOf("FAIL") > -1) {
    				                        	   Ext.Msg.show({
    						                       		title: '',
    						                       		msg: returnData.result,
    						                       		buttons : Ext.MessageBox.OK
    					                       		});   
    				                           } else {
    				                        	   Ext.Msg.show({
    						                       		title: '',
    						                       		msg: "Please check the Result on Prepayment customer gadget.",
    						                       		buttons : Ext.MessageBox.OK
    					                       		});  
    				                           }
    				                           
    				                       });
    			                	}
                         		}
                  		  })
    	              		
                  		  
                  	  }
                    }
                  }, {
                    text: '<fmt:message key="aimir.cancel"/>',
                    handler: function() {
                  	  searchWin.close();
                    }
                  }]
                });

                searchWin.show(this);
        	},
        	getPreviousMonthNetCharge: function() {
        		$('#commandResult').val("");
        		Ext.Msg.show({
               		title: '<b>Get PreviousMonth NetCharge<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.<br/><font color="red"><b>*</b></font> It will take about 2 minutes.', 'Wait !');
	                		$.getJSON('${ctx}/gadget/device/command/cmdGetPreviousMonthNetCharge.do', {
	                			'target' : selectedMeterId,
	                        }, function(returnData) {
	                        	Ext.Msg.hide();
	                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                        });
	                	} else {
	                		return;
	                	}
	              }
           		});         		
        	}, getSpecifiedNetCharge_SUNI: function() {
        		$('#commandResult').val("");        		
        		Ext.Msg.show({
               		title: '<b>Get Specifiend NetCharge<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.<br/><font color="red"><b>*</b></font> It will take about 2 minutes.', 'Wait !');
	        				$.getJSON('${ctx}/gadget/device/command/cmdGetSpecificMonthNetCharge.do', {
	        					'target' : selectedMeterId,
	                            'yyyy' : $('#monthlyYearCombo').val(),
	                            'mm': $('#monthlyMonthCombo').val()
	                        }, function(returnData) {
	                            Ext.Msg.hide();
	                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                        });
	                	} else {
	                		return;
	                	}
	              }
           		});            		
        	}, getSpecifiedNetCharge_WASION: function() {
        		$('#commandResult').val("");

        		var optionVal = '';
        		var searchWin = new Ext.Window({
        		  title: '<b>Get Specifiend NetCharge<b/>',
        		  modal: true, closable:true, resizable: true,
        		  width:300, height:130,
        		  border:true, plain:false,                      
        		  items:[{
        			  xtype: 'panel',
        			  frame: false, border: false,
        			  items:{
        				id: 'getNetchargeform',
        				xtype: 'form',
        				bodyStyle:'padding:10px',
        				labelWidth: 100,
        				frame: false, border: false,
        				items: [{
        				  xtype: 'label', html:'<div style="text-align:left;">' + 'Please select a period.' +'</div>',  anchor: '100%'
        				},{
        				  xtype: 'combo',
        				  id:'netCharge_id', name: 'netCharge_name', value:'Select...',          
        				  fieldLabel: 'options', triggerAction: 'all', editable: false, mode: 'local',
        				  store: new Ext.data.JsonStore({
        					  root:'datas',
        					  fields: ['code','name'],
        					  autoLoad: true,
        					  data: {datas: [
        						 {"code":1,"name":'For one month'},
        						 {"code":2,"name":'For two months'},
        						 {"code":3,"name":'For three months'},
        						 {"code":4,"name":'For four months'},
        						 {"code":5,"name":'For five months'},
        						 {"code":6,"name":'For six months'},
        						 {"code":7,"name":'For seven months'},
        						 {"code":8,"name":'For eight months'},
        						 {"code":9,"name":'For nine months'},
        						 {"code":10,"name":'For ten months'},
        						 {"code":11,"name":'For eleven months'},
        						 {"code":12,"name":'For twelve months'}
        					  ]}
        				  }),
        				  valueField: 'code', displayField: 'name',
        				  anchor: '100%',
        				  listeners: {
        					select : function(combo, record, index){
        					  Ext.getCmp('netCharge_id').setValue(record.data.name);
        					  optionVal = record.data.code;
        					}
        				  }
        				}]
        			  }
        		  }],
        		  
        		buttons: [{
        			text: 'Ok',
        			handler: function() {
        			  var flag = true;

        			  if((flag && (Ext.getCmp('netCharge_id').value == null || Ext.getCmp('netCharge_id').value == ''))
        					  || (Ext.getCmp('netCharge_id').value == 'Select...')) {
        				  Ext.Msg.alert("","Please select options");
        				  flag = false;
        				  return flag;
        			  }
        			  
        			  if(flag) {
        				  searchWin.close();
        				  Ext.Msg.show({
        						title: '<b>Get Specifiend NetCharge<b/>',
        						width:300, height:300,
        						msg: '<fmt:message key='aimir.wouldGet'/>',
        						buttons : Ext.MessageBox.OKCANCEL,
        						fn : function(btn) {
        							if(btn == 'ok') {
        								$.ajaxSetup({
        									async : true
        								});
        								
										Ext.Msg.wait('Waiting for response.', 'Wait !');
										$.getJSON('${ctx}/gadget/device/command/cmdGetSpecificMonthNetCharge.do', {        												
											'target' : selectedMeterId,
											'period' : optionVal
										   }, function(returnData) {
												Ext.Msg.hide();
												Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
										   });
        								
        							}
        						}
        				  })
        					
        				  
        			  }
        			}
        		  }, {
        			text: '<fmt:message key="aimir.cancel"/>',
        			handler: function() {
        			  searchWin.close();
        			}
        		  }]
        		});

        		searchWin.show(this);
        	},
        	setFriendlyCreditSchedule: function() {
        		$('#commandResult').val("");        		
        		Ext.Msg.show({
               		title: '<b>Set FriendlyCredit Schedule<b/>',
               		msg: '<fmt:message key='aimir.wouldSet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.<br/><font color="red"><b>*</b></font> It will take about 2 minutes.', 'Wait !');
	                		
	        				$.getJSON('${ctx}/gadget/device/command/cmdSetFriendlyCreditSchedule.do', {
	        					'target' : selectedMeterId
	                        }, function(returnData) {
	                            Ext.Msg.hide();
	                            Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);	        					
	                        });
	                	} else {
	                		return;
	                	}
	              }
           		});          		
        	},
        	getFriendlyCreditSchedule: function() {
        		$('#commandResult').val("");
        		
        		Ext.Msg.show({
              		title: '<b>Get FriendlyCredit Schedule<b/>',
              		msg: '<fmt:message key='aimir.wouldGet'/>',
              		buttons : Ext.MessageBox.OKCANCEL, 
              		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.<br/><font color="red"><b>*</b></font> It will take about 2 minutes.', 'Wait !');
	                		
	        				$.getJSON('${ctx}/gadget/device/command/cmdGetFriendlyCreditSchedule.do', {
	        					'target' : selectedMeterId
	        					
	        					//'target' : selectedMeterId,
	                            //'fcMode': $('#fcMode').val()
	                        }, function(returnData) {
	                        	Ext.Msg.hide();
	                        	//$('#commandResult').val(returnData.result);
	                        	Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                        });
	                	} else {
	                		return;
	                	}
					}
				});     
	        		
        	},
        	setPaymentMode: function() {
 				var paymentMode = '';
        		$('#commandResult').val("");
                var searchWin =  new Ext.Window({
                	title: '<b>Set PaymentMode<b/>',
	      		    modal: true, closable:true, resizable: true,
	      		    width:300, height:160,
	      		    border:true, plain:false,                      
	      		  	items:[{
	                  xtype: 'panel',
	                  frame: false, border: false,
	                  items:{
	                    id: 'paymentMode_form',
	                    xtype: 'form',
	                    bodyStyle:'padding:10px',
	                    labelWidth: 100,
	                    frame: false, border: false,
	                    items: [{
	                      xtype: 'label', html:'<div style="text-align:left;">' + 'Please select Payment mode.' +'</div>',  anchor: '100%'
	                    },{
	                      xtype: 'combo',
	                      id:'paymentMode_id', name: 'paymentMode_name', value:'Select...',          
	                      fieldLabel: 'Payment Mode', triggerAction: 'all', editable: false, mode: 'local',
	                      store: new Ext.data.JsonStore({
	                    	  root:'datas',
	                    	  fields: ['code','name'],
	                    	  autoLoad: true,
	                    	  data: {datas: [
	 						  	 /* {"code":0,"name":'Disable STS module control'},
	 						  	{"code":1,"name":'Manual OFF(Postpay)'},
	 						  	{"code":2,"name":'Manuel ON'}, */
	 						  	{"code":3,"name":'Prepaid'}
	                    	  ]}
	                      }),
	                      valueField: 'code', displayField: 'name',
	                      anchor: '100%',
	                      listeners: {
	                        select : function(combo, record, index){
	                          Ext.getCmp('paymentMode_id').setValue(record.data.name);
	                          paymentMode = record.data.code;
	                        }
	                      }
	                    }]
	                  }
	              	}],
	              	buttons: [{
	                    text: 'Ok',
	                    handler: function() {
	      				  var flag = true;

	                  	  if((flag && (Ext.getCmp('paymentMode_id').value == null || Ext.getCmp('paymentMode_id').value == ''))
	                  			  || (Ext.getCmp('paymentMode_id').value == 'Select...')) {
	                  		  Ext.Msg.alert("","Please select Payment Mode");
	                  		  flag = false;
	                  		  return flag;
	                  	  }
	                  	  
	                  	  if(flag) {
	                  		  searchWin.close();
	          	              
	                  		  Ext.Msg.show({
	                  			    title: '<b>Set PaymentMode<b/>',
	                         		msg: '<fmt:message key='aimir.wouldSet'/>',
	                         		buttons : Ext.MessageBox.OKCANCEL,
	                         		fn : function(btn) {
	    			                	if(btn == 'ok') {
	    			                		$.ajaxSetup({
	    				       	                async : true
	    				       	            });		                	
	    			                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	    				               		
	    			                		$.getJSON('${ctx}/gadget/device/command/cmdSetPaymentMode.do', {
	    			           					'target' : selectedMeterId,
	    			                               'mode': paymentMode
	    			                           }, function(returnData) {
	    			                               Ext.Msg.hide();
	    			                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	    			                           });
	    			                	} else {
	    			                		return;
	    			                	}
	                         		}
	                  		  })
	    	              		
	                  		  
	                  	  }
	                    }
	                  }, {
	                    text: '<fmt:message key="aimir.cancel"/>',
	                    handler: function() {
	                  	  searchWin.close();
	                    }
	                  }]
        		});
                searchWin.show();
       		},
       		getPaymentMode: function() {
       			$('#commandResult').val("");       			
       			Ext.Msg.show({
               		title: '<b>Get PaymentMode<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdGetPaymentMode.do', {
	           					'target' : selectedMeterId
	                           }, function(returnData) {
	                        	    Ext.Msg.hide();
									Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		});        			
       			
       		}, setSTSSetup: function() {
       			var stsNumber = '';
                var kct1 = '';
                var kct2 = '';
       			$('#commandResult').val("");
                var searchWin =  new Ext.Window({
                	title: '<b>Set STSSetup<b/>',
	      		    modal: true, closable:true, resizable: true,
	      		    width:300, height:210,
	      		    border:true, plain:false,                      
	      		  	items:[{
	                  xtype: 'panel',
	                  frame: false, border: false,
	                  items:{
	                    id: 'stsSetup_form',
	                    xtype: 'form',
	                    bodyStyle:'padding:10px',
	                    labelWidth: 100,
	                    frame: false, border: false,
	                    items: [{
	                        xtype: 'label', html:'<div style="text-align:left;">' + 'Please input STS Setup Information.' +'</div>',  anchor: '100%'
	                      }, {
	                        xtype: 'textfield', fieldLabel: 'STS Number', id: 'stsNumber', name: 'stsNumber', anchor: '100%'
	                      },{
	                    	  xtype: 'textfield', fieldLabel: 'KCT1', id: 'kct1', name: 'kct1', anchor: '100%'  
	                      },{
	                    	  xtype: 'textfield', fieldLabel: 'KCT2', id: 'kct2', name: 'kct2', anchor: '100%'  
	                      }]
	                  }
	              	}],
	              	buttons: [{
	                    text: 'Ok',
	                    handler: function() {
	      				  var flag = true;
	      				  
		              	  if(flag && ($('#stsNumber').val() == null || $('#stsNumber').val() == '')) {
		              		  Ext.Msg.alert("","Please input STS Number.");
		              		  flag = false;
		              		  return flag;
		              	  }
		              	  
		              	  if(flag && ($('#kct1').val() == null || $('#kct1').val() == '')) {
		              		  Ext.Msg.alert("","Please input kct1");
		              		  flag = false;
		              		  return flag;
		              	  }
		              	  
		              	  if(flag && ($('#kct2').val() == null || $('#kct2').val() == '')) {
		              		  Ext.Msg.alert("","Please input KCT2");
		              		  flag = false;
		              		  return flag;
		              	  }
		              	  
		              	  stsNumber =  $('#stsNumber').val();
		              	  kct1 = $('#kct1').val();
		              	  kct2 = $('#kct2').val();
	                  	  
	                  	  if(flag) {
	                  		  searchWin.close();
	          	              
	                  		  Ext.Msg.show({
	                  				title: '<b>Set STSSetup<b/>',
	                         		msg: '<fmt:message key='aimir.wouldSet'/>',
	                         		buttons : Ext.MessageBox.OKCANCEL,
	                         		fn : function(btn) {
	                         			if(btn == 'ok') {
	            	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	            	           				$.getJSON('${ctx}/gadget/device/command/cmdSetSTSSetup.do', {
	            	           					'target' : selectedMeterId,
	            	                               'stsNumber': stsNumber,
	            	                               'kct1': kct1,
	            	                               'kct2': kct2
	            	                           }, function(returnData) {
	            	                               Ext.Msg.hide();
	            	                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	            	                           });
	            	                	} else {
	            	                		return;
	            	                	}
	                         		}
	                  		  })
	    	              		
	                  		  
	                  	  }
	                    }
	                  }, {
	                    text: '<fmt:message key="aimir.cancel"/>',
	                    handler: function() {
	                  	  searchWin.close();
	                    }
	                  }]
        		});
                searchWin.show();       			
       		},
       		getSTSSetup: function() {
        		$('#commandResult').val("");        		
        		Ext.Msg.show({
               		title: '<b>Get STSSetup<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdGetSTSSetup.do', {
	           					'target' : selectedMeterId
	                           }, function(returnData) {
	                               Ext.Msg.hide();
	                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		});         		
       		},
       		readFirmware: function() {
       			$('#commandResult').val("");       			
       			Ext.Msg.show({
               		title: '<b>Read Firmware<b/>',
               		msg: '<fmt:message key='aimir.wouldRead'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdSuniFirmwareRead.do', {
	           					'target' : selectedMeterId
	                           }, function(returnData) {
	                               Ext.Msg.hide();
	                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		});    
       		},
       		getFwUpdateInfo: function() {
       			$('#commandResult').val("");       			
       			Ext.Msg.show({
               		title: '<b>Get Firmware Update Info<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdGetSuniFirmwareUpdateInfo.do', {
	           					'target' : selectedMeterId
	                           }, function(returnData) {
	                               Ext.Msg.hide();
	                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		}); 
       		},
       		readFwUpdateKey: function() {
       			$('#commandResult').val("");       			
       			Ext.Msg.show({
               		title: '<b>Read Firmware Update Key<b/>',
               		msg: '<fmt:message key='aimir.wouldRead'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdSuniFirmwareUpdateKeyRead.do', {
	           					'target' : selectedMeterId
	                           }, function(returnData) {
	                               Ext.Msg.hide();
	                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		}); 
       		},
       		getCIUCommStateHistory: function() {
       			$('#commandResult').val("");       			
       			Ext.Msg.show({
               		title: '<b>Get CIUComm State History<b/>',
               		msg: '<fmt:message key='aimir.wouldGet'/>',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdGetCIUCommStateHistory.do', {
	           					'target' : selectedMeterId
	                           }, function(returnData) {
	                               Ext.Msg.hide();
	                               Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		});
       		},
       		writeFwKey: function() {
       			$('#commandResult').val("");       			
       			Ext.Msg.show({
               		title: '<b>Write Fw Update Key<b/>',
               		msg: 'Do you want to write?',
               		buttons : Ext.MessageBox.OKCANCEL,
               		fn : function(btn) {
	                	if(btn == 'ok') {
	                		Ext.Msg.wait('Waiting for response.', 'Wait !');
	                   		
	           				$.getJSON('${ctx}/gadget/device/command/cmdSuniFirmwareUpdateKeyWrite.do', {
	           					'target' : selectedMeterId,
	           					'keyNo': $('#keyNo').val(),
	           					'keyHex': $('#keyHex').val()
	                           }, function(returnData) {
	                        	   Ext.Msg.hide();
	                        	   Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                           });
	                	} else {
	                		return;
	                	}
	              }
           		});
       		},
       		/* OPF-255 STS Command Retry */
       		cmdTcpRetry: function(){
       			Ext.Msg.wait('Waiting for response.', 'Wait !');
           		
   				$.getJSON('${ctx}/gadget/device/command/cmdTCPRetry.do', {
   					'target': selectedMeterId,
                   }, function(returnData) {
                	   Ext.Msg.hide();
                       Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
                   });
       		
       		}
        }
        
        var changeCredit = function() {
            $('#duration').val('');
        }
        
        /* // Async History Start    
        var asyncHistoryGridStore;
        var asyncHistoryGridModel;
        var asyncHistoryGridPanel;
        var asyncHistoryGridInstanceOn = false;
        function initAsyncHistoryGrid(){
        	//checkMeterId();
            //setting grid panel width
            var gridWidth = $("#prepayContract").width()-20;
            var pageSize = 10;

            asyncHistoryGridStore = new Ext.data.JsonStore({
                url: "${ctx}/gadget/device/getAsyncLogListForMeterPrepm.do",
                //파라매터 설정.
                 baseParams: {
                	meterId: selectedMdsId,
                	startDate : $("#asyncCommandHistoryStartDateHidden").val(),
                    endDate : $("#asyncCommandHistoryEndDateHidden").val(),
                    loginId : loginId
                }, 
                totalProperty: 'totalCount',
                root:'rtnStr',
                fields: [
    					   "rowNo"
                         , "command"
                         , "requestTime"
                         , "state"
                         , "deviceSerial"
                         , "trid"
                ],
                listeners: {
                    beforeload: function(store, options){
                    options.params || (options.params = {});
                    Ext.apply(options.params, {
                                  page: Math.ceil((options.params.start + options.params.limit) / options.params.limit)
                             });
                    }
                }
            });//Store End

            asyncHistoryGridModel = new Ext.grid.ColumnModel({
                columns: [
    				 {header: "No.", dataIndex: 'rowNo', width: 100, align: 'center'}
    				,{header: "Transaction ID", dataIndex: 'trid', width:gridWidth/6, align: 'center'}
    				,{header: "Request Time", dataIndex: 'requestTime', width:gridWidth/6, align: 'center'}
                    ,{header: "Command", dataIndex: 'command', width:gridWidth/6, align: 'center'}
                    ,{header: "Meter ID", dataIndex: 'deviceSerial', width:gridWidth/6, align: 'center'}
                    ,{header: "State", dataIndex: 'state', width:gridWidth/6, align: 'center',
                    	 renderer : function(value, me, record, rowNumber, columnIndex, store) {
             		 		if(record.data.state == "0") //Security Error
                 				return "Success";
             		 		else if(record.data.state == "1")
             		 			return "Waiting";
             		 		else if(record.data.state == "2")
             		 			return "Running";
             		 		else if(record.data.state == "4")
             		 			return "Terminate";
             		 		else if(record.data.state == "8")
             		 			return "Delete";
             		 		else if(record.data.state == "255")
             		 			return "Unknown";
             		 		else
             		 			return record.data.state
                 			
                				}	
                    }
                    ,{header: "Result", align: 'center', width:gridWidth/6-105,
                        renderer: function(value, metaData, record, index) {
                        	var data = record.data;
                        	asyncRowNo = data.rowNo;
                        	asyncDeviceId = data.deviceSerial;
                            asyncTrid = data.trid;
                            asyncState = data.state;
                            asyncCommand = data.command;
                            var btnHtml = "<a href='#;' onclick='asyncResultCheck();' class='btn_blue'><span>Result</span></a>";
                            var tplBtn = new Ext.Template(btnHtml);
                            return tplBtn.apply();
                        }
                    }

                ],
                defaults: {
                    sortable: true
                   ,menuDisabled: true
                   ,width: 120
                }
            });

            if (asyncHistoryGridInstanceOn == false) {
                //Grid panel instance create
                asyncHistoryGridPanel = new Ext.grid.GridPanel({
                    store: asyncHistoryGridStore,
                    colModel : asyncHistoryGridModel,
                   //selectModel define.
                    singleSelect:true,
                    sm : new Ext.grid.RowSelectionModel({
            			singleSelect:true,
            			listeners: {
                            rowselect: function(sm, row, rec) {
                            	var data = rec.data;
                            	asyncRowNo = data.rowNo;
                            	asyncDeviceId = data.deviceSerial;
                                asyncTrid = data.trid;
                                asyncState = data.state;
                                asyncCommand = data.command;
                            }
                        }
            		}), 
                    autoScroll:false,
                    height: 340,
                    width : gridWidth,
                    //scroll:false,
                    //style: 'align:center;',
                    //패널 높이 설정
                    stripeRows : true,
                    columnLines: true,
                    loadMask:{
                        msg: 'loading...'
                    },
                    //랜더링 디비전
                    renderTo: 'asyncHistoryGrid',
                    viewConfig: {
                        enableRowBody:true,
                        showPreview:true,
                        emptyText: 'No data to display'
                    },
                    bbar: new Ext.PagingToolbar({
                        pageSize: pageSize,
                        store: asyncHistoryGridStore,
                        displayInfo: true,
                        displayMsg: ' {0} - {1} / {2}'
                    })
                });
                asyncHistoryGridInstanceOn = true;
            } else {
                var bottomToolbar = asyncHistoryGridPanel.getBottomToolbar();
                asyncHistoryGridPanel.reconfigure(asyncHistoryGridStore, asyncHistoryGridModel);
                bottomToolbar.bindStore(asyncHistoryGridStore);
            }

        }// End of getMeterListByNotModemGrid
        
        function getAsyncHistoryGrid() {
        	if(validateSearchCondition($('#asyncCommandHistoryStartDateHidden').val(), $('#asyncCommandHistoryEndDateHidden').val())){
        		asyncHistoryGridStore.baseParams = {};
                asyncHistoryGridStore.setBaseParam('meterId', selectedMdsId);
                asyncHistoryGridStore.setBaseParam('startDate', $("#asyncCommandHistoryStartDateHidden").val());
                asyncHistoryGridStore.setBaseParam('endDate', $("#asyncCommandHistoryEndDateHidden").val());
        		asyncHistoryGridStore.setBaseParam('loginId', loginId);
                asyncHistoryGridStore.load({params:{start: 0, limit: 10}});
            }
        }
        
        
        var win;
        function asyncResultCheck() {
        	var opts = "width=450px, height=340px, left=650px, top=200px, resizable=no, status=no, location=no";
        	var obj = new Object();

        	if (asyncState != 0) {
        		Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
        	} else {
        		$.getJSON('${ctx}/gadget/device/getAsyncResultForMeter.do'
        				, { 'deviceSerial' : asyncDeviceId,
        					'trid' : asyncTrid,
        					'command' : asyncCommand
        				}, function (returnData){
        					if (returnData.result == "") {
        						Ext.Msg.alert('<fmt:message key='aimir.message'/>','No Result');
        					} else {
        						obj.result = returnData.result;
        						
        						if (win) {
        							win.close();
        						}
        						
        						win = window.open("${ctx}/gadget/device/asyncResultPopup.do", "asyncResult", opts);
        						win.opener.obj = obj;
        					}
        				});
        	}
        } 
        // async end
        */
        
      //report window(Excel)

        function openExcelReport(exType) {
            var opts = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no, center:yes";
            var obj = new Object();
            var header = new Array();
            var param = new Array();

            if(exType == 'main_list'){
                obj.excelType = 'main_list'

                //title
                header[0] = '<fmt:message key="aimir.contractNumber"/>';
                header[1] = '<fmt:message key="aimir.accountNo"/>'; 
                header[2] = '<fmt:message key="aimir.customername"/>'; 
                header[3] = '<fmt:message key="aimir.celluarphone"/>'; 
                header[4] = '<fmt:message key="aimir.hems.prepayment.lastchargedate"/>';
                header[5] = '<fmt:message key="aimir.hems.prepayment.currentbalance"/>(<fmt:message key="aimir.price.unit"/>)'; 
                header[6] = '<fmt:message key="aimir.meterid"/>'; 
                header[7] = '<fmt:message key="aimir.sts.number"/>'; 
                header[8] = '<fmt:message key="aimir.supply.type"/>'; 
                header[9] = '<fmt:message key="aimir.contract.tariff.type"/>'; 
                header[10] = '<fmt:message key="aimir.meterstatus"/>';
                header[11] = '<fmt:message key="aimir.lastreaddate"/>'; 
                header[12] = '<fmt:message key="aimir.hems.prepayment.validperiod"/>';
                header[13] = '<fmt:message key="aimir.address"/>'; 
                header[14] = '<fmt:message key="aimir.hems.prepayment.prepaymentCustomerList"/>';
                header[15] = '<fmt:message key="aimir.shipment.gs1"/>'; 
                

                //parameter
                param[0] = $("#contractNumber").val();
                param[1] = $("#customerName").val();
                param[2] = $("#statusCode").val();
                param[3] = $("#mdsId").val();
                param[4] = $("#locationId").val();
                param[5] = $("#serviceTypeCode").val();
                param[6] = supplierId;
                param[7] = $("#amountStatus").val();
                param[8] = $('#selectLastChargeDate').val();
                param[9] = $("#searchLastChargeStartDate").val();
                param[10] = $("#searchLastChargeEndDate").val();
                param[11] = $("#paymentType").val();
                param[12] = $("#gs1").val();
            }else if(exType == 'balanceHistory'){
                obj.excelType = 'balanceHistory'
                
               	if(!validateSearchCondition($('#searchStartDate').val(), $('#searchEndDate').val())){
               		return;
               	}

                //title
				header[0] = '<fmt:message key="aimir.header.type"/>';
                header[1] = '<fmt:message key="aimir.contractNumber"/>';
                header[2] = '<fmt:message key="aimir.accountNo"/>';
                header[3] = '<fmt:message key="aimir.time.date"/>';
                header[4] = '<fmt:message key="aimir.prepayment.beforebalance"/>(<fmt:message key="aimir.price.unit"/>)';
                header[5] = '<fmt:message key="aimir.balance"/>(<fmt:message key="aimir.price.unit"/>)';
                header[6] = '<fmt:message key="aimir.bill"/>(<fmt:message key="aimir.price.unit"/>)';
                header[7] = '<fmt:message key="aimir.usage"/>(<fmt:message key="aimir.unit.kwh"/>)';
                header[8] = '<fmt:message key="aimir.chargeAmount"/>(<fmt:message key="aimir.price.unit"/>)';
                header[9] = '<fmt:message key="aimir.prepayment.token"/>';
                header[10] = '<fmt:message key="aimir.cancel"/> <fmt:message key="aimir.time.date"/>';
                header[11] = '<fmt:message key="aimir.cancel"/> <fmt:message key="aimir.sts.token"/>';
                header[12] = '<fmt:message key="aimir.paymenttype"/>';
                header[13] = '<fmt:message key="aimir.monthly"/> <fmt:message key="aimir.usage"/>(<fmt:message key="aimir.unit.kwh"/>)';
                header[14] = '<fmt:message key="aimir.monthly"/> <fmt:message key="aimir.bill"/>(<fmt:message key="aimir.price.unit"/>)';
                header[15] = '<fmt:message key="aimir.prepayment.vat"/>(<fmt:message key="aimir.price.unit"/>)';
                header[16] = '<fmt:message key="aimir.prepayment.levy"/>(<fmt:message key="aimir.price.unit"/>)';
                header[17] = '<fmt:message key="aimir.prepayment.subsidy"/>(<fmt:message key="aimir.price.unit"/>)';
                header[18] = '<fmt:message key="aimir.serviceCharge"/>(<fmt:message key="aimir.price.unit"/>)';
                header[19] = '<fmt:message key="aimir.description"/>';
                header[20] = '<fmt:message key="aimir.prepayment.balancehistory"/>';
                //parameter
                param[0] = selectedContractNumber;
                param[1] = $("#searchType").val();
                param[2] = $('#searchStartDate').val();
                param[3] = $('#searchEndDate').val();
                param[4] = supplierId;
                param[5] = selectedMdsId;
                param[6] = selectedSPN;
                
            }else if(exType == 'STSHistory'){
            		obj.excelType = 'STSHistory'

           			if(!validateSearchCondition(getSTSSearchStartDate(), getSTSSearchEndDate() )){
           				return;
           			}
                    //title
                    header[0] = 'Meter Number';
                    header[1] = 'Create Date';
            		header[2] = 'Command';
            		header[3] = 'Sequence';
            		header[4] = 'Transaction Id';
                    header[5] = 'Pay Mode';
                    header[6] = 'Result Date';
                    header[7] = 'Result';
                    header[8] = 'Fail Reason';
                    header[9] = 'Token Date';
                    header[10] = 'Token';
                    header[11] = 'Charged Credit';
                    header[12] = 'Get Date';
                    header[13] = 'EmergencyCredit Mode';
                    header[14] = 'EmergencyCredit Day';
                    header[15] = 'Tariff Date';
                    header[16] = 'Tariff Mode';
                    header[17] = 'Tariff Kind';
                    header[18] = 'Tariff Count';
                    header[19] = 'CondLimit1';
                    header[20] = 'CondLimit2';
                    header[21] = 'Consumption';
                    header[22] = 'fixedRate';
                    header[23] = 'varRate';
                    header[24] = 'CondRate1';
                    header[25] = 'CondRate2';
                    header[26] = 'RemainingCredit Date';
                    header[27] = 'RemainingCredit';
                    header[28] = 'Monthly Date(yyyymm)';
                    header[29] = 'Monthly Consumption';
                    header[30] = 'Monthly Cost';
                    header[31] = 'Daily Date(yyyymmdd)';
                    header[32] = 'Daily Consumption';
                    header[33] = 'Daily Cost';
                    header[34] = 'Fc Mode';
                    header[35] = 'Friendly Date';
                    header[36] = 'FriendlyDay Type';
                    header[37] = 'Friendly From(hhmm)';
                    header[38] = 'Friendly End(hhmm)';
                    header[39] = 'STS Number';
                    header[40] = 'KCT1';
                    header[41] = 'KCT2';
                    header[42] = 'Channel';
                    header[43] = 'Pan ID';
                    header[44] = 'STSHistory';

                    //parameter
                    param[0] = selectedMdsId;
                    param[1] = $('#stsCommand').val();
                    param[2] = getSTSSearchStartDate();
                    param[3] = getSTSSearchEndDate();
                    param[4] = $('#stsResult').val();
                    param[5] = supplierId;
                    
            }else if(exType == 'stsBalanceHistory'){
            		obj.excelType = 'stsBalanceHistory'

           			if(!validateSearchCondition($('#searchSTSStartDate').val(), $('#searchSTSEndDate').val())){
           				return ;
           			}
            		
                    //title
					header[0] = '<fmt:message key="aimir.contractNumber"/>';
					header[1] = '<fmt:message key="aimir.poc.dateTime"/>';
					header[2] = '<fmt:message key="aimir.balance"/>';
	                header[3] = '<fmt:message key="aimir.usage"/>';
	                header[4] = '<fmt:message key="aimir.writetime"/>';
	                header[5] = '<fmt:message key="aimir.owecredit"/>';
	                header[6] = '<fmt:message key="aimir.owecredit"/> (<fmt:message key="aimir.threshold"/>)';
	                header[7] = '3';
	                header[8] = '4';
	                header[9] = '5';
	                header[10] =  '<fmt:message key="aimir.prepayment.balancehistory"/> (<fmt:message key="aimir.sts.module"/>)';

                    //parameter
                    param[0] = selectedMdsId;
                    param[1] = $("#searchSTSStartDate").val();
                    param[2] = $("#searchSTSEndDate").val();
                    param[3] = supplierId;
            }else {
                Ext.MessageBox.alert('<fmt:message key="aimir.message"/>', '<fmt:message key="aimir.firmware.msg20"/>');
                return;
            }

            obj.fmtMessage = header;
            obj.condition = param ;

            var winObj = window.open('${ctx}/gadget/prepaymentMgmt/prepaymentBalanceExcelDownloadPopup.do', "Open Excel Report", opts);
            winObj.opener.obj = obj;
        }
      
      var eventHandler = {
   	      contractInfoSearch: function(callback) {
   	        },
   	     infoDetailSearch: function() {
   	    	drawBalanceHistoryData($("#infoDetailForm input[name=contractNumberInfo]").val());
   	     },
   	     infoDetailTotalExcel: function() {
   	    	excelType = 8;

            var opt = "width=600px, height=400px, left=100px, top=100px  resizable=no, status=no, center:yes";
            contactListObj = new Object();
            var header = new Array();
            var param = new Array();

            header[0]='Balance History';
            header[1]='<fmt:message key="aimir.contractNumber"/>';

            //parameter
            param[1] = $("#infoDetailForm input[name=contractNumberInfo]").val();
            param[2] = $("#infoDetailForm input[name=startDate]").val() || '00000000';
            param[3] = $("#infoDetailForm input[name=endDate]").val() || '99999999'; 
            param[4] = supplierId;

            contactListObj.fmtMessage = header;
            contactListObj.condition = param ;

            window.open('${ctx}/gadget/prepaymentMgmt/vendorChargeHistoryExcelDownloadPopup.do', "Balance History Excel", opt);
   	     },
   	  	modifiedDateFormat: function(date) {        
          var $this = $(this);

          $.getJSON("${ctx}/common/convertLocalDate.do", 
            {supplierId: supplierId, dbDate: date},
            function(data) {            
              $this.siblings("." + $this.attr('name')).val(data.localDate);
            });
        },
    	      
      }
      
  	function setEmergencyCredit() {
        $('#commandResult').val(""); 
        
        if(Ext.getCmp('emergencyCreditFormPanel')){
   			Ext.getCmp('emergencyCreditFormPanel').close();
   		}
        if(Ext.getCmp('emergencyCreditFormPanel')){
   			Ext.getCmp('emergencyCreditFormPanel').close();
   		} 
        
   		var emergencyCreditFormPanel =  new Ext.form.FormPanel({
  			id: 'emergencyCreditFormPanel',
  			formId: 'emergencyForm',
            bodyStyle:'padding:10px 10px 10px 10px',
            labelWidth: 140,
            listeners: {
            	afterlayout: function(c){
	             	if(emergencyCreditAutoChange == true){
	             		Ext.getCmp('limit').enable();
	             		Ext.getCmp('disable').setValue(false);
	            		Ext.getCmp('enable').setValue(true);
	            	}else{
	            		Ext.getCmp('limit').disable();
	            		Ext.getCmp('disable').setValue(true);
	            		Ext.getCmp('enable').setValue(false);
	            	}
            	}
            },
            items: [{
                    xtype: 'label',
                    html: 'Wasion Meter(<b>Limited Amount</b>), Suni Kamstrupt Meter(<b>Days</b>)<br>',
              	},{
                    xtype : 'radiogroup',
                    id : 'type',
                    height : 20,
                  	listeners:{
	                    change: function(thisRadioGroup, checkedItem){
	                        if(checkedItem.value==0){
	                            Ext.getCmp('limit').disable();
	                        }else{
	                        	Ext.getCmp('limit').enable();
	                        }
	                    }
	                },
	                items : [
	                      {boxLabel: '<fmt:message key="aimir.disable2"/>', id:'disable', name: 'radio-action2', value:'0'},
	                      {boxLabel: '<fmt:message key="aimir.enable2"/>', id:'enable', name: 'radio-action2', value:'1'}
	                  ]
	            },{
	                xtype: 'numberfield',
	                width : 50,
	                fieldLabel: 'Limited Amount/Days ',
	                id : 'limit',
	                value: emergencyCreditMaxDuration
	            }],
	  		buttons: [{
	  			id : 'cllButions',
	          	text: ' OK ',
	          	formBind : true,
	          	handler: function() {
	            	Ext.Msg.wait('Waiting for response.', 'Wait !');
	                $.getJSON('${ctx}/gadget/device/command/cmdSetEmergencyCredit.do', {
	                    'target' : selectedMeterId,
	                    'ec_mode' : Ext.getCmp('type').getValue().value,
	                    'days' : Ext.getCmp('limit').getValue()
	                }, function(returnData) {
	                    Ext.Msg.hide();
	                    Ext.Msg.alert('<fmt:message key='aimir.message'/>', returnData.result);
	                });	
	           	}
	         },{
	          		text: 'Cancel',
	              	listeners: {
	              		click: function(btn,e) {
	              	    	Ext.getCmp('setEmergencyCreditWin').close();
	              	    }
	           		}
	          }]
	      });
       
          var currentWindow = new Ext.Window({
          	  id     : 'setEmergencyCreditWin',
              title  : ' Set Emergency Credit ',
              pageX : 600,
              pageY : 200,
              height : 200,
              width  : 540,
              layout : 'fit',
              bodyStyle   : 'padding: 5px 5px 5px 5px;',
              items  : [emergencyCreditFormPanel],
          });
          currentWindow.show();
      }
  /*     
  	function saveNotifySettingConfirm() {
        $('#commandResult').val(""); 
        
   		var saveNotiFormPanel =  new Ext.form.FormPanel({
  			id : 'saveNotiFormPanel',
  			formId: 'saveNotiForm',
            bodyStyle:'padding:10px 10px 10px 10px',
            labelWidth  : 140,              
            items       : [{                  
                  xtype: 'label',
                  html: '<b><fmt:message key="aimir.hems.prepayment.intervalmsg"/></b>'
              },{
                  id:'periodType',
                  fieldLabel: '<fmt:message key="aimir.period2"/> ',
                  xtype : 'combo',
                  triggerAction : 'all',
                  mode : 'local',
                  valueField : 'id',
                  displayField : 'name',
                  editable : false,
                  value: notificationPeriod,
				  store: new Ext.data.JsonStore({
					  root:'datas',
					  fields: ['id','name'],
					  data: {datas: [
						 {"id":1,"name":'<fmt:message key="aimir.daily"/>',value:0},
						 {"id":2,"name":'<fmt:message key="aimir.weekly"/>',value:1}
					  ]}
				  }),
				  listeners:{
				   	  select : function(combo, record, index){
		      			   if(record.data.name=='Weekly'){
		      			 	  Ext.getCmp('dailyInterval').hide();
		      			 	  Ext.getCmp('weeklyInterval').show();
		      				  Ext.getCmp('checkDay').show();
		      			   }else if(record.data.name=='Daily'){
		      				  Ext.getCmp('dailyInterval').show();
		      			 	  Ext.getCmp('weeklyInterval').hide();
		      			 	  Ext.getCmp('checkDay').hide();
		      			   }
				      },
			          afterrender: function(c){
			        	  if(notificationPeriod == 2){
		      				 	Ext.getCmp('dailyInterval').hide();
		      				 	Ext.getCmp('weeklyInterval').show();
		      					Ext.getCmp('checkDay').show();
		      					Ext.getCmp('weeklyInterval').setValue(notificationInterval);
		      					Ext.getCmp('dailyInterval').setValue(1);
			            	}else{
		      					Ext.getCmp('dailyInterval').show();
		      				 	Ext.getCmp('weeklyInterval').hide();
		      				 	Ext.getCmp('checkDay').hide();
		      				 	Ext.getCmp('dailyInterval').setValue(notificationInterval);
		      				 	Ext.getCmp('weeklyInterval').setValue(1);
			            	}
		              }
			      }
              },{
                  id : 'dailyInterval',
            	  fieldLabel: '<fmt:message key="aimir.hems.prepayment.interval"/> (<fmt:message key="aimir.hems.prepayment.days"/>)',
                  xtype : 'combo',
                  triggerAction : 'all',
                  mode : 'local',
                  hidden : true,
                  valueField : 'id',
                  displayField : 'name',
                  editable : false,
				  store: new Ext.data.JsonStore({
					  root:'datas',
					  fields: ['id','name'],
					  autoLoad: true,
					  data: {datas: [
						 {"id":1,"name":'1'},
						 {"id":2,"name":'2'},
						 {"id":3,"name":'3'},
						 {"id":4,"name":'4'},
						 {"id":5,"name":'5'},
						 {"id":6,"name":'6'}
					  ]}
				  })
              },{
                  id : 'weeklyInterval',
                  fieldLabel: '<fmt:message key="aimir.hems.prepayment.interval"/> (<fmt:message key="aimir.hems.prepayment.weeks"/>)',
                  xtype : 'combo',
                  triggerAction : 'all',
                  mode : 'local',
                  valueField : 'id',
                  displayField : 'name',
                  editable : false,
                  hidden : true,
                  autoShow: true,
                  autoSelect: true,
				  store: new Ext.data.JsonStore({
					  root:'datas',
					  fields: ['id','name'],
					  autoLoad: true,
					  data: {datas: [
						 {"id":1,"name":'1'},
						 {"id":2,"name":'2'},
						 {"id":3,"name":'3'},
						 {"id":4,"name":'4'}
					  ]}
				  })
              },{
                  xtype : 'checkboxgroup',
                  id : 'checkDay',
                  hidden : true,
                  width : 360,
                  height :30,
                  items : [
                      {boxLabel: '<fmt:message key="aimir.day.mon"/>', name: 'radio-action2', id:'mon', checked:notificationWeeklyMon},
                      {boxLabel: '<fmt:message key="aimir.day.tue"/>', name: 'radio-action2', id:'tue', checked:notificationWeeklyTue},
                      {boxLabel: '<fmt:message key="aimir.day.wed"/>', name: 'radio-action2', id:'wed', checked:notificationWeeklyWed},
                      {boxLabel: '<fmt:message key="aimir.day.thu"/>', name: 'radio-action2', id:'thu', checked:notificationWeeklyThu},
                      {boxLabel: '<fmt:message key="aimir.day.fri"/>', name: 'radio-action2', id:'fri', checked:notificationWeeklyFri},
                      {boxLabel: '<fmt:message key="aimir.day.sat"/>', name: 'radio-action2', id:'sat', checked:notificationWeeklySat},
                      {boxLabel: '<fmt:message key="aimir.day.sun"/>', name: 'radio-action2', id:'sun', checked:notificationWeeklySun}
                  ]
              },{                  
                  xtype: 'label',
                  html: '<b><fmt:message key="aimir.hems.prepayment.balancemsg"/></b>'
              },{
                  id: 'threshold',
                  xtype: 'numberfield',
                  maxValue : 1000,
                  width : 100,
                  value: prepaymentThreshold,
                  fieldLabel: '<fmt:message key="aimir.hems.prepayment.notifythreshold"/>(<fmt:message key='aimir.price.unit'/>) ',
              }],
  			buttons: [{
  				id : 'cllButions',
          	    text: ' OK ',
          	    formBind : true,
          	    handler: function() {
            		Ext.Msg.wait('Waiting for response.', 'Wait !');
                    $.post("${ctx}/gadget/prepaymentMgmt/updateBalanceNotifySetting.do",{
                    	contractNumber : selectedContractNumber,
                        operatorId     : operatorId,
                        serviceType    : selectedServiceType,
                        period         : Ext.getCmp('periodType').getValue(),
                        interval       : Ext.getCmp('periodType').getValue()==1 ? Ext.getCmp('dailyInterval').getValue():Ext.getCmp('weeklyInterval').getValue(),
                        //hour           : $("#notifyHour").val(),
                        threshold      : Ext.getCmp('threshold').getValue(),
                        mon            : Ext.getCmp('mon').getValue(),
                        tue            : Ext.getCmp('tue').getValue(),
                        wed            : Ext.getCmp('wed').getValue(),
                        thu            : Ext.getCmp('thu').getValue(),
                        fri            : Ext.getCmp('fri').getValue(),
                        sat            : Ext.getCmp('sat').getValue(),
                        sun            : Ext.getCmp('sun').getValue()
                    }, function(returnData) {
                        Ext.Msg.hide();
                        if (returnData.status == "success") {
                            Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save"/>", getNotificationInfo);
                        } else {
                            Ext.MessageBox.alert("<fmt:message key="aimir.message"/>", "<fmt:message key="aimir.save.error"/>");
                        }
                    });	
              	}
          	},{
          		text: 'Cancel',
              	listeners: {
              		click: function(btn,e) {
              			Ext.getCmp('saveNotifySettingWin').close();
              	    }
             	}
          	}]
      	});
       
          var currentWindow = new Ext.Window({
          	  id     : 'saveNotifySettingWin',
              title  : ' Set Balance Alert ',
              pageX : 600,
              pageY : 200,
              height : 300,
              width  : 540,
              layout : 'fit',
              bodyStyle   : 'padding: 5px 5px 5px 5px;',
              items  : [saveNotiFormPanel],
          });
          currentWindow.show();
      } */

        /*]]>*/
    </script>
</head>
<body>
  <div id="menu">
    <ul>
      <li>
        <a href="#defultTab">
          <fmt:message key="aimir.balance"/>
        </a>
      </li>
<%--       <li>
        <a href="#infoTab">
          <fmt:message key='aimir.sts.command'/>
        </a>
      </li> --%>
    </ul>
    
    <div id="commonDiv" class="ui-tabs-panel ui-widget-content ui-corner-bottom" style="margin-bottom:700px;">
		<!--
		<input type="hidden" name="searchStartMonth" id="searchStartMonth"/>
		<input type="hidden" name="searchEndMonth" id="searchEndMonth"/>
		-->
		<input id="searchStartDate" type="hidden"/>
		<input id="searchEndDate" type="hidden" />
		<input id="searchStartHour" type="hidden"/>
		<input id="searchEndHour" type="hidden" />
		<input id="searchLastChargeStartDate" type="hidden" />
		<input id="searchLastChargeEndDate" type="hidden" />
		
		<input id="searchSTSStartDate" type="hidden"/>
		<input id="searchSTSEndDate" type="hidden" />
		<input id="searchStartDateSTS" type="hidden"/>
		<input id="searchEndDateSTS" type="hidden" />
		<input id="searchStartHourSTS" type="hidden"/>
		<input id="searchEndHourSTS" type="hidden" />
		
		<input type="hidden" name="limitPower" id="limitPower"/>
		<input type="hidden" name="locationId" id="locationId"/>
		<input type="hidden" name="interval" id="interval"/>
		<input type="hidden" name="threshold" id="threshold"/>
		
	    <!--contract no.-->
        <div style="margin-top: 10px; margin-bottom: 10px;">
            <table class="search_basic" style="width: auto;">
                <tr>
                    <td><fmt:message key='aimir.contractNumber'/></td>
                    <td><input id="contractNumber" name="contractNumber" style="width:130px"/></td>
                    <td><fmt:message key="aimir.meterid"/><!-- 미터 아이디 --></td>
                    <td><input name="mdsId" id='mdsId' type="text" style="width:100px"/></td>
                    <td><fmt:message key="aimir.shipment.gs1"/></td>
                    <td><input name="gs1" id='gs1' type="text" style="width:100px"/></td>
                    <td><fmt:message key="aimir.supplystatus"/><!-- 상태 --></td>
                    <td><select id="statusCode" name="statusCode" style="width:110px"><option value=""></option></select></td>
                    <td><fmt:message key="aimir.paymenttype"/><!-- Payment Type --></td>
                    <td><select id="paymentType" name="paymentType" style="width:110px"><option value=""></option></select></td>
                </tr>
                <tr style="height: 5px"></tr>
                </tr>
                	<td><fmt:message key="aimir.customername"/><!-- 고객명 --></td>
                    <td><input id="customerName" name="customerName" style="width:130px"/></td>
					<td><fmt:message key="aimir.supply.type"/><!-- 공급타입 --></td>
                    <td><select id="serviceTypeCode" name="serviceTypeCode" style="width:100px"><option value=""></option></select></td>
					<td><fmt:message key='aimir.amount.status'/><!-- 금액 상태 --></td>
                    <td>
                    	<select id="amountStatus" name="amountStatus" style="width:80px">
                    		<option value=""><fmt:message key='aimir.all'/></option>
                    		<option value="negative"><fmt:message key='aimir.negative'/></option>
                    		<option value="positive"><fmt:message key='aimir.positive'/></option>
                    	</select>
                    </td>
                    <td><fmt:message key='aimir.hems.prepayment.lastchargedate'/></td>
                    <td>
                    	<div id="searchlastChargeDate_1">
                    		<select id="selectLastChargeDate" name="selectLastChargeDate" style="width:80px" onchange="javascript:changeChargeDateStatus();">
	                    		<option value="disable"><fmt:message key='aimir.disable2'/></option>
                    			<option value="enable"><fmt:message key='aimir.enable2'/></option>
                    		</select>
                    	</div>
                    </td>
                    <td>
                    	<div id="searchlastChargeDate_2" style="display: none;">
                            <ul style="display: flex;">
                                <li>
                                	<li><input id="lastChargeStartDate" class="day" type="text" readonly="readonly"></li>
                                	<li class="date-space"></li>
                                <li><input value="~" class="between" type="text"></li>
                                <li><input id="lastChargeEndDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                </li>
                                
                            </ul>
                        </div>       
                    </td>
                    <td><a href="#" class="btn_blue" onClick="getPrepayContractDivData();"><span><fmt:message key='aimir.button.search'/></span></a></td>
                    <td><a href="#" class="btn_blue" onClick="openExcelReport('main_list');"><span><fmt:message key='aimir.button.excel'/></span></a></td>
               </tr>
            </table>
        </div>
	    
	    <!--//contract no.-->
	    <div id="treeDivAOuter" class="tree-billing auto" style="display:none;">
	        <div id="treeDivA"></div>
	    </div>
	
	    <!-- content1-->
	    <div id="" class="overflow_hidden">
	
	
	        <!--content1 left -->
	        <div>
	            <div id="prepayContract" class="ext_grid"></div>
	        </div>
	        <!--//content1 left -->
	
	    </div>
	    <!--// content1 -->
	    
	    <div id="command" class="margin_t5">
	    	<div id="setTariffDiv"></div>
	    
		    <div id="relayControlButton">
	        	<a href="#" class="btn_blue" style="margin-left: 0px"  onclick="cmdRelayOn();"><span><fmt:message key="aimir.meter.command.RelayOn"/><!-- Relay On --></span></a>
	        	<a href="#" class="btn_blue" onClick="cmdRelayOff();"><span><fmt:message key="aimir.meter.command.RelayOff"/><!-- Relay Off --></span></a>
	        	<a href="#" class="btn_blue" onClick="cmdRelayStatus();"><span><fmt:message key="aimir.meter.command.RelayStatus"/><!-- Relay Status --></span></a>
	       	</div>
	    	
	    	<div id="stsControl_SUNI">
	        	<a href="#" id="sts" class" class="btn_blue" style="display: none" title="Get Previous Month Net Charge" onClick="cmdSTSHandler.getPreviousMonthNetCharge();"><span><fmt:message key='aimir.getPrevious'/></span></a>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" title="Get Specified Net Charge" onClick="showNetCharge();"><span>Get Specified</span></a>
	       		<div id="selectDate" class="floatleft tootipbox" style="display: none;">
	       			<span style="margin: 0px; padding: 0px;">
	       				<button id="monthlyLeft" type="button" class="back"></button>
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<select id="monthlyYearCombo" style="width: 50px"></select>
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<label class="descr"><fmt:message key="aimir.year1" /></label>
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<li class="date-space"></li>
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<select id="monthlyMonthCombo" class="sm" style="width: 40px"></select>
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<label class="descr"><fmt:message key="aimir.day.mon" /></label>
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<button id="monthlyRight" type="button" class="next"></button>
	       			</span>
	       			<span>
						<div id="btn">
							<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getSpecifiedNetCharge_SUNI();"><span><fmt:message key="aimir.button.confirm" /></span></a>
	       					<a href="#" id="sts" class="btn_blue" style="display: none" onClick="clearAndHideNetChargeButton();"><span><fmt:message key="aimir.cancel" /></span></a>
						</div>	       			
	       			</span>
				</div>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="showTCount();"><span><fmt:message key='aimir.getSTSToken'/><!-- For STS --></span></a>
	       		<div id="tCountDiv" class="floatleft tootipbox" style="display: none;" >
	       			<span style="margin: 0px; padding: 0px; color: #0f7cd0" >
	       				T Count : 
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<input type="text" id="tCount" name="tCount" size="5px"></input>
	       			</span>
	       			<span>
						<div id="btn">
							<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getSTSToken();"><span><fmt:message key="aimir.button.confirm" /></span></a>
		       				<a href="#" id="sts" class="btn_blue" style="display: none" onClick="clearAndHideGetSTSTokenButton();"><span><fmt:message key="aimir.cancel" /></span></a>
						</div>	       			
	       			</span>
				</div>
				<!-- STS토큰 전송 -->
				<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.setSTSToken();"><span><fmt:message key='aimir.setSTSToken'/></span></a>
				 <a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getEmergencyCredit();"><span><fmt:message key='aimir.getEmergency'/></span></a>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="setEmergencyCredit();"><span><fmt:message key='aimir.setEmergency'/></span></a>
	            <%-- <a href="#" id="sts" class="btn_blue" style="display: none" onClick="saveNotifySettingConfirm();"><span><fmt:message key="aimir.hems.prepayment.balancenotify"/></span></a> --%>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.setFriendlyCreditSchedule();"><span><fmt:message key='aimir.setFriendly'/><!-- For STS --></span></a>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="showFcMode();"><span><fmt:message key='aimir.getFriendly'/><!-- For STS --></span></a>
	       		
	       		
	       		<div id="fcModeDiv" class="floatleft tootipbox" style="display: none;">
	       			<span style="margin: 0px; padding: 0px; color: #0f7cd0" >&nbsp;Friendly Credit Mode&nbsp;&nbsp;</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<select id="fcMode" class="sm" style="width: 120px">
	       					<option value='0' selected>Current Schedule</option>                                                                                                                          
							<option value='1'>Pending Schedule</option>   
	       				</select>
	       			</span>
	       			<span>
						<div id="btn">
							<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getFriendlyCreditSchedule();"><span><fmt:message key="aimir.button.confirm" /></span></a>
		       				<a href="#" id="sts" class="btn_blue" style="display: none" onClick="clearAndHideFcModeButton();"><span><fmt:message key="aimir.cancel" /></span></a>
						</div>	       			
	       			</span>
				</div>
				
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.sendSTSMessage();"><span><fmt:message key='aimir.sendMessage'/><!-- For STS --></span></a>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.cmdTcpRetry();"><span><fmt:message key='aimir.tcp.retry'/><!-- For STS --></span></a>
	       		<%-- <div id="fwKeyDiv" class="floatleft tootipbox" style="display: none;">
	       			<span style="margin: 0px; padding: 0px; color: #0f7cd0" >
	       				Key No. : 
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<input type="text" id="keyNo" name="keyNo" size="5px"></input>
	       			</span>
	       			<span style="margin: 0px; padding: 0px; color: #0f7cd0" >
	       				key(Hex) :
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<input type="text" id="keyHex" name="keyHex" size="30px"></input>
	       			</span>
	       			<span>
						<div id="btn">
							<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.writeFwKey();"><span><fmt:message key="aimir.button.confirm" /></span></a>
		       				<a href="#" id="sts" class="btn_blue" style="display: none" onClick="clearAndHideFwKeyButton();"><span><fmt:message key="aimir.cancel" /></span></a>
						</div>	       			
	       			</span>
				</div> --%>
	       	</div>
	    	
	    	<div id="stsControl_WASION">
	    	<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getPreviousMonthNetCharge();"><span><fmt:message key='aimir.getPrevious'/></span></a>
	        <a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getSpecifiedNetCharge_WASION();"><span>Get Specified</span></a>
	    	
	    	<a href="#" id="sts" class="btn_blue" style="display: none" onClick="showTCount2();"><span><fmt:message key='aimir.getSTSToken'/></span></a>
	       		<div id="tCountDiv2" class="floatleft tootipbox" style="display: none;" >
	       			<span style="margin: 0px; padding: 0px; color: #0f7cd0" >
	       				T Count : 
	       			</span>
	       			<span style="margin: 0px; padding: 0px;">
	       				<input type="text" id="tCount2" name="tCount" size="5px"></input>
	       			</span>
	       			<span>
						<div id="btn">
							<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getSTSToken2();"><span><fmt:message key="aimir.button.confirm" /></span></a>
		       				<a href="#" id="sts" class="btn_blue" style="display: none" onClick="clearAndHideGetSTSTokenButton();"><span><fmt:message key="aimir.cancel" /></span></a>
						</div>	       			
	       			</span>
				</div>
				
				<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.setSTSToken();"><span><fmt:message key='aimir.setSTSToken'/></span></a>
	            <a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getEmergencyCredit();"><span><fmt:message key='aimir.getEmergency'/></span></a>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="setEmergencyCredit();"><span><fmt:message key='aimir.setEmergency'/></span></a>
	            <%-- <a href="#" id="sts" class="btn_blue" style="display: none" onClick="saveNotifySettingConfirm();"><span><fmt:message key="aimir.hems.prepayment.balancenotify"/></span></a> --%>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.setFriendlyCreditSchedule();"><span><fmt:message key='aimir.setFriendly'/></span></a>
	       		<a href="#" id="sts" class="btn_blue" style="display: none" onClick="cmdSTSHandler.getFriendlyCreditSchedule();"><span><fmt:message key='aimir.getFriendly'/></span></a>
	    	</div>
	       	
	        <div id="commandResultDiv" class="meterinfo-textarea clear">
		            <ul>
		                <li><textarea id="commandResult" readonly style="height: 52px;"><fmt:message key='aimir.result'/></textarea></li>
		            </ul>
			</div>
			
	    </div><!-- end -->
	
	    <div class="h_dotline_blue"></div>
    </div>

    <div id="defultTab">
	
	    <!-- content2 -->
        <div id="balanceHistoryTitle" class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.prepayment.balancehistory'/></div>
        <div style="width: 50%">
        	<b>(Can  search the maximum number of days is 90 days)</b>
             <table>
                <tr>
                    <td class="padding-r20px">
                        <div id="hourly">
                            <ul>
                                <li><input id="hourlyStartDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                                <li><input value="~" class="between" type="text"></li>
                                <li><input id="hourlyEndDate" class="day" type="text" readonly="readonly"></li>
                                <li class="date-space"></li>
                            </ul>
                        </div>          
                    </td>
                    <td><label class="datetxt"><fmt:message key='aimir.header.type'/></label></td>
                    <td><select id="searchType" class="sm" size="320">
                    	<option value="all" selected><fmt:message key="aimir.all"/></option>
                    	<option value="charge"><fmt:message key="aimir.hems.prepayment.chargehistory"/></option>
                    	<option value="day"><fmt:message key="aimir.prepayment.chargehistory"/> (<fmt:message key="aimir.day"/>)</option>
                    	<option value="month"><fmt:message key="aimir.prepayment.chargehistory"/> (<fmt:message key="aimir.month"/>)</option>
                    </select></td>
                    <td></td>
                    <td class="btnspace"><a href="javascript:getChargeHistoryData();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a></td>

                    <td><a href="#" class="btn_blue" onClick="openExcelReport('balanceHistory');"><span><fmt:message key='aimir.button.excel'/></span></a></td>
                </tr>
            </table>

        </div>
        <div id="chargeHistory" class="balance_grid margin_t5"></div>
	    <!--// content2 getDate사용-->
	    
<%-- 	    <div id="STSChargeHistoryDiv" style="display: none">
		    <div id="STSBalanceHistoryTitle" class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.prepayment.balancehistory'/> (<fmt:message key='aimir.sts.module'/>)</div>
	        <div style="width: 50%">
	        	<b>(Can search the maximum number of days is 90 days)</b>
	              <table>
	                <tr>
	                    <td class="padding-r20px">
	                        <div id="hourly">
	                            <ul>
	                                <li><input id="STSHourlyStartDate" class="day" type="text" readonly="readonly"></li>
	                                <li class="date-space"></li>
	                                <li><input value="~" class="between" type="text"></li>
	                                <li><input id="STSHourlyEndDate" class="day" type="text" readonly="readonly"></li>
	                                <li class="date-space"></li>
	                            </ul>
	                        </div>          
	                    </td>
	                    <td class="btnspace"><a href="javascript:getSTSChargeHistoryData();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a></td>
	                    <td><a href="#" class="btn_blue" onClick="openExcelReport('stsBalanceHistory');"><span><fmt:message key='aimir.button.excel'/></span></a></td>
	                </tr>
	            </table> 
	        </div>
	        <div id="stsChargeHistory" class="balance_grid margin_t5"></div>
	    </div> --%>
	</div>
	
<%--     <div id="infoTab">
	    <div id="stsLog">
			<div id="stsHistoryTitle" class="title_basic"><span class="icon_title_blue"></span><fmt:message key='aimir.stsHistory'/></div>
	        <div style="width: 80%">
	        	<b>(Can search the maximum number of days is 90 days)</b>
	             <table>
	                <tr>
	                    <td>
	                        <div id="hourly">
	                            <ul>
	                                <li><input id="hourlyStartDateSTS" class="day" type="text" readonly="readonly"></li>
	                                <li class="date-space"></li>
	                                <li><input value="~" class="between" type="text"></li>
	                                <li><input id="hourlyEndDateSTS" class="day" type="text" readonly="readonly"></li>
	                                <li class="date-space"></li>
	                            </ul>
	                        </div>          
	                    </td>
	                    <td><label class="datetxt"><fmt:message key='aimir.instrumentation'/></label></td>
	                    <td><select id="stsCommand" class="sm" size="180"></td>
	                    <td><label class="datetxt"><fmt:message key='aimir.result'/></label></td>
	                    <td><select id="stsResult" class="sm" size="160"></td>
	                    <td class="btnspace"><a href="javascript:getSTSHistoryData();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a></td>
	                    
	                    <td><a href="#" class="btn_blue" onClick="openExcelReport('STSHistory');"><span><fmt:message key='aimir.button.excel'/></span></a></td>
	                </tr>
	            </table>
	
	        </div>
	        <div id="stsHistory" class="balance_grid margin_t5"></div>
        </div>
	    <!--// content2 -->
		<div id="asyncHistory" class="title_basic"><span class="icon_title_blue"></span>Async Comm. Log</div>
	        <div style="width: 80%">
	        	<b>(Can search the maximum number of days is 90 days)</b>
	             <table>
	                <tr>
	                    <td>
	                        <div id="hourly">
	                            <ul>
	                                <li><input id="asyncCommandHistoryStartDate" class="day" type="text" readonly="readonly"></li>
	                                <li class="date-space"></li>
	                                <li><input value="~" class="between" type="text"></li>
	                                <li><input id="asyncCommandHistoryEndDate" class="day" type="text" readonly="readonly"></li>
	                                <li class="date-space"></li>
	                            </ul>
	                                <a href="javascript:getAsyncHistoryGrid();" class="btn_blue"><span><fmt:message key='aimir.button.search'/></span></a>
	                        </div>    
	                    </td>
	                </tr>
	            </table>
	            <input type="hidden" id="asyncCommandHistoryStartDateHidden" />
				<input type="hidden" id="asyncCommandHistoryEndDateHidden" />
	        </div>
	        <div id="asyncHistoryGrid" class="balance_grid margin_t5"></div>
	        
    </div>  --%> 
  </div>
</body>
</html>