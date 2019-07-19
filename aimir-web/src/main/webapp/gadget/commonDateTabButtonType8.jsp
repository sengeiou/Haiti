<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var seasonData8="";
    
    var firstTabType8="";

    var _supplierId8='';
    $(document).ready(function(){
        commonDateTabButtonInterval8 = setInterval("commonDateTabButtonDelay8()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierId8 = json.supplierId;
                }
        );
    });

    function commonDateTabButtonDelay8(){
        if(_supplierId8 != ''){
            clearInterval(commonDateTabButtonInterval8);
            commonDateTabButtonInit8();
          }
    }
    
    	
  	//가장 상단 탭클릭시 상단 탭값 설정..
	$(".topTabType").click(function(){
    	
    	    	
    	firstTabType8 = $(this).attr("id");
    	
    	//alert(firstTabType8);
    	
    });
    
    var periodStartDate8="";
	var periodEndDate8= "";
	

   
      

    function commonDateTabButtonInit8(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#_hourly8')        .bind('click',function(event) { $('#searchDateType8').val(DateType.HOURLY        );getSearchDate8(DateType.HOURLY); } ); });
        $(function() { $('#_daily8')         .bind('click',function(event) { $('#searchDateType8').val(DateType.DAILY         );getSearchDate8(DateType.DAILY); } ); });
        $(function() { $('#_period8')        .bind('click',function(event) { $('#searchDateType8').val(DateType.PERIOD        );getSearchDate8(DateType.PERIOD); } ); });
        $(function() { $('#_weekly8')        .bind('click',function(event) { $('#searchDateType8').val(DateType.WEEKLY        );getSearchDate8(DateType.WEEKLY); } ); });
        $(function() { $('#_monthly8')       .bind('click',function(event) { $('#searchDateType8').val(DateType.MONTHLY       );getSearchDate8(DateType.MONTHLY); } ); });
        $(function() { $('#_monthlyPeriod8') .bind('click',function(event) { $('#searchDateType8').val(DateType.MONTHLYPERIOD );getSearchDate8(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#_weekdaily8')     .bind('click',function(event) { $('#searchDateType8').val(DateType.WEEKDAILY     );getSearchDate8(DateType.WEEKDAILY); } ); });
        $(function() { $('#_seasonal8')      .bind('click',function(event) { $('#searchDateType8').val(DateType.SEASONAL      );getSearchDate8(DateType.SEASONAL); } ); });
        $(function() { $('#_yearly8')        .bind('click',function(event) { $('#searchDateType8').val(DateType.YEARLY        );getSearchDate8(DateType.YEARLY); } ); });

        //조회버튼클릭 이벤트 생성
        $(function() { 
        	
        	$('#btnSearch8').bind('click',function(event){
        		
        		//alert("상단 처시 버튼");
        		
        		//alert(firstTabType8);
        		
        	/*	if (firstTabType8=="_power" )
        		{
        			
        			//파워 탭일 경우 상단의 날짜를 하단div에 상단 날짜를 setting
        			//alert("파워탭");
        			periodStartDate8= $("#periodStartDate").val();
        			periodEndDate8= $("#periodEndDate").val();
        			
        	alert(periodStartDate);
        			
        			alert(periodEndDate);
        			
        		}*/
        		
        		
        		sendRequest8($('#searchDateType8').val()); 
      		}); 
      		
      		
      	
        	
       	});

        $(function() { $('#hourlySearch8')       .bind('click',function(event) { sendRequest8(DateType.HOURLY      ); } ); });
        $(function() { $('#dailySearch8')        .bind('click',function(event) { sendRequest8(DateType.DAILY      ); } ); });
        $(function() { $('#periodSearch8')       .bind('click',function(event) { sendRequest8(DateType.PERIOD     ); } ); });
        $(function() { $('#weeklySearch8')       .bind('click',function(event) { sendRequest8(DateType.WEEKLY     ); } ); });
        $(function() { $('#monthlySearch8')      .bind('click',function(event) { sendRequest8(DateType.MONTHLY    ); } ); });
        $(function() { $('#monthlyPeriodSearch8').bind('click',function(event) { sendRequest8(DateType.MONTHLYPERIOD    ); } ); });
        $(function() { $('#weekDailySearch8')    .bind('click',function(event) { sendRequest8(DateType.WEEKDAILY  ); } ); });
        $(function() { $('#seasonalSearch8')     .bind('click',function(event) { sendRequest8(DateType.SEASONAL   ); } ); });
        $(function() { $('#yearlySearch8')       .bind('click',function(event) { sendRequest8(DateType.YEARLY   ); } ); });


        //기간별조회 버튼클릭 이벤트 생성
        $(function() { $('#periodType8')     .bind('change',function(event)
                { var idx = $('#periodType8').val();
                if(idx==0){
                	setPeriod8(0);
                }else if(idx==1){
                	setPeriod8(-1);
                }else if(idx==2){
                	setPeriod8(-2);
                }else if(idx==3){
                	setPeriod8(-6);
                }else if(idx==4){
                	setPeriodOneMonth8(-30);
                }

                } ); });

        //$(function() { $('#todayBtn')       .bind('click',function(event) { setPeriod8(0); } ); });
        //$(function() { $('#yesterdayBtn')   .bind('click',function(event) { setPeriod8(-1); } ); });
        //$(function() { $('#threeDayBtn')    .bind('click',function(event) { setPeriod8(-2); } ); });
        //$(function() { $('#weekBtn')        .bind('click',function(event) { setPeriod8(-6); } ); });
        //$(function() { $('#monthBtn')       .bind('click',function(event) { setPeriod8(-29); } ); });

        //일자별,기간별 날짜입력창 변경시
        $(function() { $('#dailyStartDate8')      .bind('change', function(event) { getSearchDate8(DateType.DAILY);  } ); });
        $(function() { $('#periodStartDate8')     .bind('change', function(event) { getSearchDate8(DateType.PERIOD); } ); });
        $(function() { $('#periodEndDate8')       .bind('change', function(event) { getSearchDate8(DateType.PERIOD); } ); });
        $(function() { $('#hourlyStartDate8')     .bind('change', function(event) { getSearchDate8(DateType.HOURLY); } ); });
        $(function() { $('#hourlyEndDate8')       .bind('change', function(event) { getSearchDate8(DateType.HOURLY); } ); });
        $(function() { $('#hourlyStartHourCombo8').bind('change', function(event) { getSearchDate8(DateType.HOURLY); } ); });
        $(function() { $('#hourlyEndHourCombo8')  .bind('change', function(event) { getSearchDate8(DateType.HOURLY); } ); });

        //일자별,월별 좌우 화살표클릭 이벤트 생성
        $(function() { $('#dailyLeft8')      .bind('click',  function(event) { dailyArrow8($('#dailyStartDate8').val(),-1); } ); });
        $(function() { $('#dailyRight8')     .bind('click',  function(event) { dailyArrow8($('#dailyStartDate8').val(),1); } ); });
        $(function() { $('#monthlyLeft8')    .bind('click',  function(event) { monthlyArrow8(-1); } ); });
        $(function() { $('#monthlyRight8')   .bind('click',  function(event) { monthlyArrow8(1 ); } ); });

        //주별 연도,월 콤보 체인지이벤트 생성
        $(function() { $('#weeklyYearCombo8')    .bind('change', function(event) { getWeeklyMonthCombo8(); } ); });
        $(function() { $('#weeklyMonthCombo8')   .bind('change', function(event) { getWeeklyWeekCombo8(); } ); });
        $(function() { $('#weeklyWeekCombo8')    .bind('change', function(event) { getSearchDate8(DateType.WEEKLY); } ); });

        //월별 연도콤보 체인지이벤트 생성
        $(function() { $('#monthlyYearCombo8')   .bind('change', function(event) { getMonthlyMonthCombo8(); } ); });
        $(function() { $('#monthlyMonthCombo8')  .bind('change', function(event) { getSearchDate8(DateType.MONTHLY); } ); });

        //월별(기간) 년도,월콤보 체인지 이벤트
        $(function() { $('#monthlyPeriodStartYearCombo8') .bind('change', function(event) { getMonthlyPeriodStartMonthCombo8(); } ); });
        $(function() { $('#monthlyPeriodStartMonthCombo8').bind('change', function(event) { getSearchDate8(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#monthlyPeriodEndYearCombo8')   .bind('change', function(event) { getMonthlyPeriodEndMonthCombo8(); } ); });
        $(function() { $('#monthlyPeriodEndMonthCombo8')  .bind('change', function(event) { getSearchDate8(DateType.MONTHLYPERIOD); } ); });

        //요일별 연도,월,주 콤보 체인지이벤트 생성
        $(function() { $('#weekDailyYearCombo8')     .bind('change', function(event) { getWeekDailyMonthCombo8(); } ); });
        $(function() { $('#weekDailyMonthCombo8')    .bind('change', function(event) { getWeekDailyWeekCombo8(); } ); });
        $(function() { $('#weekDailyWeekCombo8')     .bind('change', function(event) { getWeekDailyWeekDayCombo8(); } ); });
        $(function() { $('#weekDailyWeekDayCombo8')  .bind('change', function(event) { getSearchDate8(DateType.WEEKDAILY); } ); });

        //계절별 연도,계절 콤보 체인지 이벤트 생성
        $(function() { $('#seasonalYearCombo8')  .bind('change', function(event) { getSeasonalSeasonCombo8(); } ); });
        $(function() { $('#seasonalSeasonCombo8').bind('change', function(event) { getSearchDate8(DateType.SEASONAL); } ); });

        //연별 연도콤보 체인지 이벤트
        $(function() { $('#yearlyYearCombo8')  .bind('change', function(event) { getSearchDate8(DateType.YEARLY); } ); });


        var locDateFormat8 = "yymmdd";

        //탭별 일자DatePicker 생성
        $("#hourlyStartDate8")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat8, onSelect: function(dateText, inst) { modifyDate8(dateText, inst);}} );
        $("#hourlyEndDate8")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat8, onSelect: function(dateText, inst) { modifyDate8(dateText, inst);}} );
        $("#dailyStartDate8")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat8, onSelect: function(dateText, inst) { modifyDate8(dateText, inst);}} );
        $("#periodStartDate8")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat8, onSelect: function(dateText, inst) { modifyDate8(dateText, inst);}} );
        $("#periodEndDate8")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat8, onSelect: function(dateText, inst) { modifyDate8(dateText, inst);}} );

        
        
        
        
        $.getJSON("${ctx}/common/getYear.do"
                ,{supplierId:_supplierId8}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear   = json.currYear;//currDate.getYear();
                     var currDate  = json.currDate;

                     if(tabs8.hourly!=0){
	                     $("#hourlyStartDate8").val(currDate8);
	                     $("#hourlyEndDate8").val(currDate8);

	                     var hours = new Array();
	                     for(var i = 0;i<=23;i++){
	                    	 hours[i] = i<10?'0'+i:i+'';
	                     }
	                     $('#hourlyStartHourCombo8').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
	                     $('#hourlyEndHourCombo8').numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
	                     $('#hourlyStartHourCombo8').selectbox();
                         $('#hourlyEndHourCombo8').selectbox();
	                     getSearchDate8(DateType.HOURLY);
                     }
                     //탭별 현재일자 설정
                     if(tabs8.daily!=0)dailyArrow8('',0,false);
                     if(tabs.period!=0)setPeriod8(0,false);

                     if(tabs8.weekly!=0){
	                     $('#weeklyYearCombo8').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#weeklyYearCombo8').selectbox();
	                     getWeeklyMonthCombo8(false);
                     }

                     if(tabs8.monthly!=0){
	                     $('#monthlyYearCombo8').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#monthlyYearCombo8').selectbox();
	                     getMonthlyMonthCombo8("",false);
                     }

                     if(tabs8.monthlyPeriod!=0){
	                     $('#monthlyPeriodStartYearCombo8')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#monthlyPeriodEndYearCombo8')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});

	                     $('#monthlyPeriodStartYearCombo8').selectbox();
	                     $('#monthlyPeriodEndYearCombo8').selectbox();
	                     getMonthlyPeriodStartMonthCombo8(false);
	                     getMonthlyPeriodEndMonthCombo8(false);
                     }

                     if(tabs8.weekDaily!=0){
	                     $('#weekDailyYearCombo8').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#weekDailyYearCombo8').selectbox();
	                     getWeekDailyMonthCombo8(false);
                     }

                     if(tabs8.seasonal!=0){
	                     $('#seasonalYearCombo8').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#seasonalYearCombo8').selectbox();
	                     getSeasonalSeasonCombo8(false);
                     }

                     if(tabs8.yearly!=0){
                    	   $('#yearlyYearCombo8').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    	   $('#yearlyYearCombo8').selectbox();
                     }


                });


        var peroidLabels8 = new Array();
        peroidLabels8[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
        peroidLabels8[1] = '<fmt:message key="aimir.yesterday"/>';//어제''
        peroidLabels8[2] = '<fmt:message key="aimir.threedays"/>';//3일''
        peroidLabels8[3] = '<fmt:message key="aimir.weekday"/>';//일주일''
        peroidLabels8[4] = '<fmt:message key="aimir.onemonth"/>';//한달''

        $("#periodType8").numericOptions({from:0,to:4,selectedIndex:0,labels:peroidLabels8});
        $("#periodType8").selectbox();

        //탭생성
        $("#datetab8").hide();
        $("#datetab8").tabs();
        $("#datetab8").show();

        //탭숨김처리
        if(tabs8.yearly==0)hideTab8(DateType.YEARLY);
        if(tabs8.seasonal==0)hideTab8(DateType.SEASONAL);
        if(tabs8.weekDaily==0)hideTab8(DateType.WEEKDAILY);
        if(tabs8.monthlyPeriod==0)hideTab8(DateType.MONTHLYPERIOD);
        if(tabs8.monthly==0)hideTab8(DateType.MONTHLY);
        if(tabs8.weekly==0)hideTab8(DateType.WEEKLY);
        if(tabs8.period==0)hideTab8(DateType.PERIOD);
        if(tabs8.daily==0)hideTab8(DateType.DAILY);
        if(tabs8.hourly==0)hideTab8(DateType.HOURLY);


        if(tabNames8.hourly!=null&&tabNames8.hourly!=''&&tabNames8.hourly!='undefined')$('#_hourly8').html(tabNames8.hourly);
        if(tabNames8.daily!=null&&tabNames8.daily!=''&&tabNames8.daily!='undefined')$('#_daily8').html(tabNames8.daily);
        if(tabNames8.period!=null&&tabNames8.period!=''&&tabNames8.period!='undefined')$('#_period8').html(tabNames8.period);
        if(tabNames8.weekly!=null&&tabNames8.weekly!=''&&tabNames8.weekly!='undefined')$('#_weekly8').html(tabNames8.weekly);
        if(tabNames8.monthly!=null&&tabNames8.monthly!=''&&tabNames8.monthly!='undefined')$('#_monthly8').html(tabNames8.monthly);
        if(tabNames8.monthlyPeriod!=null&&tabNames8.monthlyPeriod!=''&&tabNames8.monthlyPeriod!='undefined')$('#_monthlyPeriod8').html(tabNames8.monthlyPeriod);
        if(tabNames8.weekDaily!=null&&tabNames8.weekDaily!=''&&tabNames8.weekDaily!='undefined')$('#_weekDaily8').html(tabNames8.weekDaily);
        if(tabNames8.seasonal!=null&&tabNames8.seasonal!=''&&tabNames8.seasonal!='undefined')$('#_seasonal8').html(tabNames8.seasonal);
        if(tabNames8.yearly!=null&&tabNames8.yearly!=''&&tabNames8.yearly!='undefined')$('#_yearly8').html(tabNames8.yearly);

        // 검색버튼 표시
        if(tabs8.search_yearly==1)        showSearchBtn8('yearlySearch8');
        if(tabs8.search_seasonal==1)      showSearchBtn8('seasonalSearch8');
        if(tabs8.search_weekDaily==1)     showSearchBtn8('weekDailySearch8');
        if(tabs8.search_monthlyPeriod==1) showSearchBtn8('monthlyPeriodSearch8');
        if(tabs8.search_monthly==1)       showSearchBtn8('monthlySearch8');
        if(tabs8.search_weekly==1)        showSearchBtn8('weeklySearch8');
        if(tabs8.search_period==1)        showSearchBtn8('periodSearch8');
        if(tabs8.search_daily==1)         showSearchBtn8('dailySearch8');
        if(tabs8.search_hourly==1)        showSearchBtn8('hourlySearch8');


    }


    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate8(setDate, inst){
        var dateId = '#' + inst.id;

    	$.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:_supplierId8}
                ,function(json) {
                	$(dateId).val(json.localDate);
                	$(dateId).trigger('change');
                });
    }


    /**
     * 조회버튼 클릭시 조회조건 검증후 거래호출
     */
    function sendRequest8(_dateType){
        // 조회조건 검증
        if(!validateSearchCondition8(_dateType))return false;
        send();
    }


    /**
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     */
    function convertSearchDate8(){
    	$.getJSON("${ctx}/common/convertSearchDate.do"
                ,{searchStartDate:$('#searchStartDate8').val(), searchEndDate:$('#searchEndDate8').val(), supplierId:_supplierId8}
                ,function(json) {
                    $('#searchStartDate8').val(json.searchStartDate);
                    $('#searchEndDate8').val(json.searchEndDate);
                });
    }

    /**
     * 일별 화살표처리
     */
    function dailyArrow8(bfDate,val,flag){

    	$.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:bfDate, addVal:val, supplierId:_supplierId8}
                ,function(json) {
                    $('#dailyStartDate8').val(json.searchDate);
                    getSearchDate8(DateType.DAILY,flag);
                });
    }

    /**
     * 월별 화살표처리ssss
     */
    function monthlyArrow8(val){
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#monthlyYearCombo8').val(),month:$('#monthlyMonthCombo8').val(),addVal:val}
                ,function(json) {
                    $('#monthlyYearCombo8').val(json.year);
                    $('#monthlyYearCombo8').selectbox();
                    getMonthlyMonthCombo8(json.month);
                });
    }

    /**
     * 기간별 버튼처리
     */
    function setPeriod8(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierId8}
                ,function(json) {
                    $('#periodStartDate8').val(json.searchDate);
                    $('#periodEndDate8').val(json.currDate);
                    getSearchDate8(DateType.PERIOD,flag);
                });
    }

    /**
     * 기간별 버튼처리 - 한달
     */
    function setPeriodOneMonth8(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierId8, monthCalc:true}
                ,function(json) {
                    $('#periodStartDate8').val(json.searchDate);
                    $('#periodEndDate8').val(json.currDate);
                    getSearchDate8(DateType.PERIOD,flag);
                });
    }

    /**
     * 주별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeeklyMonthCombo8(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weeklyYearCombo8').val()}
                ,function(json) {
                    var prevMonth = $('#weeklyMonthCombo8').val();
                    $('#weeklyMonthCombo8').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weeklyMonthCombo8').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weeklyMonthCombo8').selectbox();
                    getWeeklyWeekCombo8(flag);
                });
    }

    /**
     * 주별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeeklyWeekCombo8(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weeklyYearCombo8').val(),month:$('#weeklyMonthCombo8').val()}
                ,function(json) {

                    var prevWeek = $('#weeklyWeekCombo8').val();
                    $('#weeklyWeekCombo8').emptySelect();
                    if(prevWeek==""||prevWeek==null||prevWeek > json.weekCount)prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weeklyWeekCombo8').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weeklyWeekCombo8').selectbox();
                    getSearchDate8(DateType.WEEKLY,flag);
                });
    }



    /**
     * 월별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyMonthCombo8(monthVal,flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyYearCombo8').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyMonthCombo8').val();
                    $('#monthlyMonthCombo8').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyMonthCombo8').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if(monthVal!=null&&monthVal!=""){
                        $('#monthlyMonthCombo8').val(monthVal);
                    }
                    $('#monthlyMonthCombo8').selectbox();
                    getSearchDate8(DateType.MONTHLY,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodStartMonthCombo8(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodStartYearCombo8').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodStartMonthCombo8').val();
                    $('#monthlyPeriodStartMonthCombo8').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodStartMonthCombo8').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodStartMonthCombo8').selectbox();
                    getSearchDate8(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodEndMonthCombo8(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodEndYearCombo8').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodEndMonthCombo8').val();
                    $('#monthlyPeriodEndMonthCombo8').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodEndMonthCombo8').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodEndMonthCombo8').selectbox();
                    getSearchDate8(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 요일별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeekDailyMonthCombo8(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weekDailyYearCombo8').val()}
                ,function(json) {
                    var prevMonth = $('#weekDailyMonthCombo8').val();
                    $('#weekDailyMonthCombo8').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weekDailyMonthCombo8').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weekDailyMonthCombo8').selectbox();
                    getWeekDailyWeekCombo8(flag);
                });
    }

    /**
     * 요일별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeekDailyWeekCombo8(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weekDailyYearCombo8').val(),month:$('#weekDailyMonthCombo8').val()}
                ,function(json) {

                    var prevWeek = $('#weekDailyWeekCombo8').val();
                    $('#weekDailyWeekCombo8').emptySelect();
                    if(prevWeek==""||prevWeek==null||Number(prevWeek) > Number(json.weekCount))prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weekDailyWeekCombo8').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weekDailyWeekCombo8').selectbox();
                     getWeekDailyWeekDayCombo8(flag);
                });
    }

    /**
     * 요일별탭에서 주차콤보 변경시 요일콤보 생성
     * 시작은 일요일
     */
    function  getWeekDailyWeekDayCombo8(flag){
        //년도,월,주차 입력받아 현재주이면 오늘날짜까지의 요일만 표시
        $.getJSON("${ctx}/common/getWeekDay.do"
                ,{year:$('#weekDailyYearCombo8').val(),month:$('#weekDailyMonthCombo8').val(),week:$('#weekDailyWeekCombo8').val()}
                ,function(json) {

                    var prevWeek = $('#weekDailyWeekDayCombo8').val();
                    $('#weekDailyWeekDayCombo8').emptySelect();

                    var dayofweeks = new Array();

                    dayofweeks[0] = '<fmt:message key="aimir.quartz.week.sun"/>';
                    dayofweeks[1] = '<fmt:message key="aimir.quartz.week.mon"/>';
                    dayofweeks[2] = '<fmt:message key="aimir.quartz.week.tue"/>';
                    dayofweeks[3] = '<fmt:message key="aimir.quartz.week.wed"/>';
                    dayofweeks[4] = '<fmt:message key="aimir.quartz.week.thu"/>';
                    dayofweeks[5] = '<fmt:message key="aimir.quartz.week.fri"/>';
                    dayofweeks[6] = '<fmt:message key="aimir.quartz.week.sat"/>';

                    if(prevWeek==""||prevWeek==null||(Number(prevWeek) < Number(json.startWeek)&&Number(prevWeek) > Number(json.endWeek)))prevWeek = json.endWeek;

                    var dayofweek = new Array();
                    var idx=0;
                    for(var i = Number(json.startWeek);i<=Number(json.endWeek);i++){
                    	dayofweek[idx] = dayofweeks[i-1];
                    	idx++;
                    }

                    $('#weekDailyWeekDayCombo8').numericOptions({from:json.startWeek,to:json.endWeek,labels:dayofweek});
                    $('#weekDailyWeekDayCombo8').val(prevWeek);
                    $('#weekDailyWeekDayCombo8').selectbox();
                    getSearchDate8(DateType.WEEKDAILY,flag);
                });
    }

    /**
     * 계절별탭에서 년도 변경시 계절콤보 조회
     */
    function getSeasonalSeasonCombo8(flag){
        $.getJSON("${ctx}/common/getSeason.do"
                ,{year:$('#seasonalYearCombo8').val()}
                ,function(json) {

                	seasonData8 = json;

                    var prevSeason = $('#seasonalSeasonCombo8').val();
                    $('#seasonalSeasonCombo8').emptySelect();

                    var season = new Array();

                    var excludeIdx = 0;
                    var excludeIdxs = new Array();

                    var includeIdx = 0;
                    var includeIdxs = new Array();

                    var seasonIdxs = 0;
                    if(json.Spring.name == Season.SPRING){
                    	season[seasonIdxs++] = '<fmt:message key="aimir.spring"/>';
                    	includeIdxs[includeIdx++] = 1;
                    }else{
                    	excludeIdxs[excludeIdx++] = 1;
                    }

                    if(json.Summer.name == Season.SUMMER){
                        season[seasonIdxs++] = '<fmt:message key="aimir.summer"/>';
                        includeIdxs[includeIdx++] = 2;
                    }else{
                        excludeIdxs[excludeIdx++] = 2;
                    }

                    if(json.Autumn.name == Season.AUTUMN){
                        season[seasonIdxs++] = '<fmt:message key="aimir.autumn"/>';
                        includeIdxs[includeIdx++] = 3;
                    }else{
                        excludeIdxs[excludeIdx++] = 3;
                    }

                    if(json.Winter.name == Season.WINTER){
                        season[seasonIdxs++] = '<fmt:message key="aimir.winter"/>';
                        includeIdxs[includeIdx++] = 4;
                    }else{
                        excludeIdxs[excludeIdx++] = 4;
                    }


                    if(prevSeason==""||prevSeason==null){
                        prevSeason = includeIdxs[includeIdx-1];
                    }else{
                        for(var i=0;i<excludeIdx;i++){
                        	if(excludeIdxs[i] == prevSeason){
                        		prevSeason = includeIdxs[includeIdx-1];
                        	}
                        }
                    }

                    $('#seasonalSeasonCombo8').numericOptions({from:1,to:4,labels:season,exclude:excludeIdxs});
                    $('#seasonalSeasonCombo8').val(prevSeason);
                    $('#seasonalSeasonCombo8').selectbox();
                    getSearchDate8(DateType.SEASONAL,flag);
                });
    }


    /**
     * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
     * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
     * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
     */
    function getSearchDate8(_dateType,flag){
        var startDate='';
        var endDate='';

        // 화면첫로딩시일경우에는
        // 첫번째탭 클릭 이벤트를 호출한다.
        // ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
        // 첫번째 탭의 날짜를 설정하도록 한다.
        if(flag == false){
            $('div#datetab > ul:first > li:first > a:first').trigger('click');
            return;
        }

        if(DateType.HOURLY == _dateType){
        	if(tabs8.hourly==0)return;
        	$('#searchStartDate8').val($('#hourlyStartDate8').val());
            $('#searchEndDate8').val($('#hourlyEndDate8').val());

            $('#searchStartHour8').val(Number($('#hourlyStartHourCombo8').val())<10?'0'+$('#hourlyStartHourCombo8').val():$('#hourlyStartHourCombo8').val());
            $('#searchEndHour8')  .val(Number($('#hourlyEndHourCombo8').val())<10?'0'+$('#hourlyEndHourCombo8').val():$('#hourlyEndHourCombo8').val());
            convertSearchDate8();

        }else if(DateType.DAILY == _dateType){
        	if(tabs8.daily==0)return;
        	$('#searchStartDate8').val($('#dailyStartDate8').val());
            $('#searchEndDate8').val($('#dailyStartDate8').val());

            convertSearchDate8();
        }else if(DateType.PERIOD == _dateType){
        	if(tabs8.period==0)return;
            $('#searchStartDate8').val($('#periodStartDate8').val());
            $('#searchEndDate8').val($('#periodEndDate8').val());

            convertSearchDate8();
        }else if(DateType.WEEKLY == _dateType){
        	if(tabs8.weekly==0)return;
            $.getJSON("${ctx}/common/getWeekPeriod.do"
            		,{year:$('#weeklyYearCombo8').val(),month:$('#weeklyMonthCombo8').val(),week:$('#weeklyWeekCombo8').val(), supplierId:_supplierId8}
                    ,function(json) {
                    	$('#searchStartDate8').val(json.startDate);
                        $('#searchEndDate8').val(json.endDate);
                        convertSearchDate8();
                    });
        }else if(DateType.MONTHLY == _dateType){
        	if(tabs8.monthly==0)return;
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyYearCombo8').val(),month:$('#monthlyMonthCombo8').val(), supplierId:_supplierId8}
                    ,function(json) {
                        $('#searchStartDate8').val(json.startDate);
                        $('#searchEndDate8').val(json.endDate);
                        convertSearchDate8();
                    });
        }else if(DateType.MONTHLYPERIOD == _dateType){
            if(tabs8.monthlyPeriod==0)return;

            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyPeriodStartYearCombo8').val(),month:$('#monthlyPeriodStartMonthCombo8').val(), supplierId:_supplierId8}
                    ,function(json) {
                        $('#searchStartDate8').val(json.startDate);
                    });
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyPeriodEndYearCombo8').val(),month:$('#monthlyPeriodEndMonthCombo8').val(), supplierId:_supplierId8}
                    ,function(json) {
                        $('#searchEndDate8').val(json.endDate);
                        convertSearchDate8();
                    });


        }else if(DateType.WEEKDAILY == _dateType){
        	if(tabs8.weekDaily==0)return;
            $.getJSON("${ctx}/common/getWeekDayPeriod.do"
                    ,{year:$('#weekDailyYearCombo8').val(),month:$('#weekDailyMonthCombo8').val(),week:$('#weekDailyWeekCombo8').val(),weekDay:$('#weekDailyWeekDayCombo8').val(), supplierId:_supplierId8}
                    ,function(json) {
                        $('#searchStartDate8').val(json.startDate);
                        $('#searchEndDate8').val(json.endDate);
                        convertSearchDate8();
                    });
        }else if(DateType.SEASONAL == _dateType){
        	if(tabs8.seasonal==0)return;
        	var seasonIdx = $('#seasonalSeasonCombo8').val();

        	var season;
        	if(seasonIdx == 1){
        		season = seasonData8.Spring;
        	}else if(seasonIdx == 2){
        		season = seasonData8.Summer;
            }else if(seasonIdx == 3){
            	season = seasonData8.Autumn;
            }else if(seasonIdx == 4){
            	season = seasonData8.Winter;
            }else{
                season = {startDate:'',endDate:''};
            }

        	$('#searchStartDate8').val(season.startDate);
            $('#searchEndDate8').val(season.endDate);


        }else if(DateType.YEARLY == _dateType){
            if(tabs8.yearly==0)return;

            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#yearlyYearCombo8').val()
                    , supplierId:_supplierId8}
                    ,function(json) {
                        $('#searchStartDate8').val(json.startDate);
                        $('#searchEndDate8').val(json.endDate);
                        convertSearchDate8();
                    });
        }


    }


    /**
     * 날짜타입탭을 숨김처리한다.
     */
    function hideTab8(_idx){
        $("#datetab8").tabs("remove",_idx);
        $("#datetab8").tabs("select",0);
    }

    /**
     * 조회버튼을 표시한다.
     */
    function showSearchBtn8(btnName){

        var liName = '#' + btnName + 'Li';
        $(liName).css('display','block');

    }

    /**
     * 날짜타입별 조회조건 검증
     */
    function validateSearchCondition8(_dateType){

    	if(DateType.HOURLY == _dateType){
    		if(Number($('#searchStartDate8').val()) == Number($('#searchEndDate8').val())){
    			if(Number($('#searchStartHour8').val()) > Number($('#searchEndHour8').val())){
    				alert('<fmt:message key="aimir.season.error"/>');
                    return false;
    			}
            }
            return true;
        }else if(DateType.DAILY == _dateType){
            //시작일 필수 체크
            return true;
        }else if(DateType.PERIOD == _dateType){
            //시작일,종료일 필수 체크
            //시작일,종료일 선후 체크
            //시작일,종료일 기간체크(31일 이내)
            if(Number($('#searchStartDate8').val()) > Number($('#searchEndDate8').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            if(Number($('#searchEndDate8').val()) - Number($('#searchStartDate8').val()) > 30){
            	//최대 31일까지만 조회 가능합니다.
                //alert('<fmt:message key="aimir.season.error"/>');
                //return false;
            }
            return true;
        }else if(DateType.WEEKLY == _dateType){
            //조회월,조회주차 필수체크
            return true;
        }else if(DateType.MONTHLY == _dateType){
            //조회월 필수체크
            return true;
        }else if(DateType.MONTHLYPERIOD == _dateType){
            if(Number($('#searchStartDate8').val()) > Number($('#searchEndDate8').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }
        	return true;
        }else if(DateType.WEEKDAILY == _dateType){
            //조회월,주차,요일 필수체크
            return true;
        }else if(DateType.SEASONAL == _dateType){
            //조회연도,계절 필수체크
            if($('#seasonalSeasonCombo8').val()==null||$('#seasonalSeasonCombo8').val()==''){
            	//계절데이터가 없습니다.
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }
            return true;
        }else if(DateType.YEARLY == _dateType){

            return true;
        }else{
            //날짜타입오류
            return false;
        }
    }

    //======================================================================================

/*]]>*/
    </script>

<!-- Tab (S) -->
<div id="datetab8" class="buttontype" style="display:none; padding-top: 0">
    <ul class="buttontype">
        <li><a href="#hourly8" id="_hourly8"><fmt:message key="aimir.hourly"/></a></li>
        <li><a href="#daily8" id="_daily8"><fmt:message key="aimir.daily"/></a></li>
        <li><a href="#period8" id="_period8"><fmt:message key="aimir.period"/></a></li>
        <li><a href="#weekly8" id="_weekly8"><fmt:message key="aimir.weekly"/></a></li>
        <li><a href="#monthly8" id="_monthly8"><fmt:message key="aimir.monthly"/></a></li>
        <li><a href="#monthlyPeriod8" id="_monthlyPeriod8"><fmt:message key="aimir.monthly"/>(<fmt:message key="aimir.period"/>)</a></li>
        <li><a href="#weekdaily8" id="_weekdaily8"><fmt:message key="aimir.weekdaily"/></a></li>
        <li><a href="#seasonal8" id="_seasonal8"><fmt:message key="aimir.seasonal"/></a></li>
        <li><a href="#yearly8" id="_yearly8"><fmt:message key="aimir.yearly"/></a></li>
    </ul>



    <div id="hourly8">
	    <!-- <label><fmt:message key="aimir.hourly"/></label> -->
	    <ul>
	        <li><input id="hourlyStartDate8" class="day" type="text" readonly="readonly"></li>
	        
	        <li><select id="hourlyStartHourCombo8" class="sm"></select></li>
	        <li><input value="~" class="between" type="text"></li>
	        <li><input id="hourlyEndDate8" class="day" type="text" readonly="readonly"></li>
	        
	        <li><select id="hourlyEndHourCombo8" class="sm"></select></li>
	    </ul>
	    <div id="btn8">
	        <ul><li style="display:none" id="hourlySearchLi8"><a href="#;" id="hourlySearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	    </div>
    </div>

    <div id="daily8">
        <!-- <label><fmt:message key="aimir.date" /></label> -->
        <ul>
            <li><button id="dailyLeft8" type="button" class="back"></button></li>
            <li><input id="dailyStartDate8" type="text" class="day" readonly="readonly"></li>
            <li><button id="dailyRight8" type="button" class="next"></button></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="dailySearchLi8"><a href="#;" id="dailySearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="period8">
        <!-- <label><fmt:message key="aimir.period"/></label> -->
        <ul>
            <li><select id="periodType8" class="date-w90"></select></li>
            
            <li><input id="periodStartDate8" class="day" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="periodEndDate8" class="day" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="periodSearchLi8"><a href="#;" id="periodSearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <!--div id="period">
        <ul class="align">
            <li class="btnleft">
                <button id="todayBtn" type="button" class="sm">오늘</button>
                <button id="yesterdayBtn" type="button" class="sm">어제</button>
                <button id="threeDayBtn" type="button" class="sm">3일</button>
                <button id="weekBtn" type="button" class="sm">1주</button>
                <button id="monthBtn"type="button" class="sm">1개월</button>
            </li>
            <li>
                <label><fmt:message key="aimir.period"/></label>
                <ul>
                    <li><input id="periodStartDate" class="day" type="text" readonly="readonly"></li>
                    <li><input value="~" class="between" type="text"></li>
                    <li><input id="periodEndDate" class="day" type="text" readonly="readonly"></li>
                </ul>
                <div id="btn">
                    <ul><li><a href="#;" id="periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                </div>
           </li>
        </ul>
    </div-->

    <div id="weekly8">
        <!-- <label><fmt:message key="aimir.weekly"/></label> -->
        <ul>
            <li><select id="weeklyYearCombo8"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="weeklyMonthCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            
            <li><select id="weeklyWeekCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="weeklySearchLi8"><a href="#;" id="weeklySearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthly8">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><button id="monthlyLeft8" type="button" class="back"></button></li>
            <li><select id="monthlyYearCombo8"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="monthlyMonthCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="monthlyRight8" type="button" class="next"></button></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="monthlySearchLi8"><a href="#;" id="monthlySearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthlyPeriod8">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><select id="monthlyPeriodStartYearCombo8"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="monthlyPeriodStartMonthCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><input value="~" class="between" type="text"></li>
            <li><select id="monthlyPeriodEndYearCombo8"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="monthlyPeriodEndMonthCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="monthlyPeriodSearchLi8"><a href="#;" id="monthlyPeriodSearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="weekdaily8">
        <!-- <label><fmt:message key="aimir.weekdaily"/></label> -->
        <ul>
            <li><select id="weekDailyYearCombo8"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="weekDailyMonthCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            
            <li><select id="weekDailyWeekCombo8" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
            
            <li><select id="weekDailyWeekDayCombo8" class="date-w80"></select></li>
        </ul>
        <div id="btn8" style="display:none">
            <ul><li id="weekDailySearchLi8"><a href="#;" id="weekDailySearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="seasonal8">
        <!-- <label><fmt:message key="aimir.seasonal" /></label> -->
        <ul>
            <li><select id="seasonalYearCombo8"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            
            <li><select id="seasonalSeasonCombo8" class="date-w80"></select></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="seasonalSearchLi8"><a href="#;" id="seasonalSearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
    <div id="yearly8">
        <!-- <label><fmt:message key="aimir.yearly" /></label> -->
        <ul>
            <li><select id="yearlyYearCombo8"></select></li>
        </ul>
        <div id="btn8">
            <ul><li style="display:none" id="yearlySearchLi8"><a href="#;" id="yearlySearch8" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
	    <input id="searchStartDate8" type="hidden"/>
	    <input id="searchEndDate8" type="hidden" />
	    <input id="searchStartHour8" type="hidden"/>
	    <input id="searchEndHour8" type="hidden" />
	    <input id="searchDateType8" type="hidden" value="0"/>
</div>
<!-- Tab (E) -->
