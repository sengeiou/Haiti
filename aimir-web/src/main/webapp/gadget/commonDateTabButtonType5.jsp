<%-- 
    commonDateTabButtonType.jsp 를 기본으로 생성.
    - 하나의 jsp 에서 두 개의 날짜조건 사용 가능하도록 모든 변수명과 function 명 뒤에 별도의 접미사(Type5)를 추가함.
      style 이 div 의 id 와 맞물려있어서 id 변경 시 style 이 적용되지 않아 일단 Report 관리 화면에서 사용하는 Period 만 적용함.
--%>
<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var seasonDataType5;

    var _supplierIdType5='';
    $(document).ready(function(){
        commonDateTabButtonIntervalType5 = setInterval("commonDateTabButtonDelayType5()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierIdType5 = json.supplierId;
                }
        );
    });

    function commonDateTabButtonDelayType5(){
        if(_supplierIdType5 != ''){
            clearInterval(commonDateTabButtonIntervalType5);
            commonDateTabButtonInitType5();
          }
    }

    function commonDateTabButtonInitType5(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#_hourlyType5')        .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.HOURLY        );getSearchDateType5(DateType.HOURLY); } ); });
        $(function() { $('#_dailyType5')         .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.DAILY         );getSearchDateType5(DateType.DAILY); } ); });
        $(function() { $('#_periodType5')        .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.PERIOD        );getSearchDateType5(DateType.PERIOD); } ); });
        $(function() { $('#_weeklyType5')        .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.WEEKLY        );getSearchDateType5(DateType.WEEKLY); } ); });
        $(function() { $('#_monthlyType5')       .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.MONTHLY       );getSearchDateType5(DateType.MONTHLY); } ); });
        $(function() { $('#_monthlyPeriodType5') .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.MONTHLYPERIOD );getSearchDateType5(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#_weekdailyType5')     .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.WEEKDAILY     );getSearchDateType5(DateType.WEEKDAILY); } ); });
        $(function() { $('#_seasonalType5')      .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.SEASONAL      );getSearchDateType5(DateType.SEASONAL); } ); });
        $(function() { $('#_yearlyType5')        .bind('click',function(event) { $('#searchDateTypeType5').val(DateType.YEARLY        );getSearchDateType5(DateType.YEARLY); } ); });

        //조회버튼클릭 이벤트 생성
        $(function() { $('#btnSearchType5').bind('click',function(event) { sendRequestType5($('#searchDateTypeType5').val()); } ); });

        $(function() { $('#hourlySearchType5')       .bind('click',function(event) { sendRequestType5(DateType.HOURLY      ); } ); });
        $(function() { $('#dailySearchType5')        .bind('click',function(event) { sendRequestType5(DateType.DAILY      ); } ); });
        $(function() { $('#periodSearchType5')       .bind('click',function(event) { sendRequestType5(DateType.PERIOD     ); } ); });
        $(function() { $('#weeklySearchType5')       .bind('click',function(event) { sendRequestType5(DateType.WEEKLY     ); } ); });
        $(function() { $('#monthlySearchType5')      .bind('click',function(event) { sendRequestType5(DateType.MONTHLY    ); } ); });
        $(function() { $('#monthlyPeriodSearchType5').bind('click',function(event) { sendRequestType5(DateType.MONTHLYPERIOD    ); } ); });
        $(function() { $('#weekDailySearchType5')    .bind('click',function(event) { sendRequestType5(DateType.WEEKDAILY  ); } ); });
        $(function() { $('#seasonalSearchType5')     .bind('click',function(event) { sendRequestType5(DateType.SEASONAL   ); } ); });
        $(function() { $('#yearlySearchType5')       .bind('click',function(event) { sendRequestType5(DateType.YEARLY   ); } ); });


        //기간별조회 버튼클릭 이벤트 생성
        $(function() { $('#periodTypeType5')     .bind('change',function(event)
                { var idx = $('#periodTypeType5').val();
                if(idx==0){
                	setPeriodType5(0);
                }else if(idx==1){
                	setPeriodType5(-1);
                }else if(idx==2){
                	setPeriodType5(-2);
                }else if(idx==3){
                	setPeriodType5(-6);
                }else if(idx==4){
                	setPeriodOneMonthType5(-30);
                }

                } ); });

        //$(function() { $('#todayBtn')       .bind('click',function(event) { setPeriodType5(0); } ); });
        //$(function() { $('#yesterdayBtn')   .bind('click',function(event) { setPeriodType5(-1); } ); });
        //$(function() { $('#threeDayBtn')    .bind('click',function(event) { setPeriodType5(-2); } ); });
        //$(function() { $('#weekBtn')        .bind('click',function(event) { setPeriodType5(-6); } ); });
        //$(function() { $('#monthBtn')       .bind('click',function(event) { setPeriodType5(-29); } ); });

        //일자별,기간별 날짜입력창 변경시
        $(function() { $('#dailyStartDateType5')      .bind('change', function(event) { getSearchDateType5(DateType.DAILY);  } ); });
        $(function() { $('#periodStartDateType5')     .bind('change', function(event) { getSearchDateType5(DateType.PERIOD); } ); });
        $(function() { $('#periodEndDateType5')       .bind('change', function(event) { getSearchDateType5(DateType.PERIOD); } ); });
        $(function() { $('#hourlyStartDateType5')     .bind('change', function(event) { getSearchDateType5(DateType.HOURLY); } ); });
        $(function() { $('#hourlyEndDateType5')       .bind('change', function(event) { getSearchDateType5(DateType.HOURLY); } ); });
        $(function() { $('#hourlyStartHourComboType5').bind('change', function(event) { getSearchDateType5(DateType.HOURLY); } ); });
        $(function() { $('#hourlyEndHourComboType5')  .bind('change', function(event) { getSearchDateType5(DateType.HOURLY); } ); });

        //일자별,월별 좌우 화살표클릭 이벤트 생성
        $(function() { $('#dailyLeftType5')      .bind('click',  function(event) { dailyArrowType5($('#dailyStartDateType5').val(),-1); } ); });
        $(function() { $('#dailyRightType5')     .bind('click',  function(event) { dailyArrowType5($('#dailyStartDateType5').val(),1); } ); });
        $(function() { $('#monthlyLeftType5')    .bind('click',  function(event) { monthlyArrowType5(-1); } ); });
        $(function() { $('#monthlyRightType5')   .bind('click',  function(event) { monthlyArrowType5(1 ); } ); });

        //주별 연도,월 콤보 체인지이벤트 생성
        $(function() { $('#weeklyYearComboType5')    .bind('change', function(event) { getWeeklyMonthComboType5(); } ); });
        $(function() { $('#weeklyMonthComboType5')   .bind('change', function(event) { getWeeklyWeekComboType5(); } ); });
        $(function() { $('#weeklyWeekComboType5')    .bind('change', function(event) { getSearchDateType5(DateType.WEEKLY); } ); });

        //월별 연도콤보 체인지이벤트 생성
        $(function() { $('#monthlyYearComboType5')   .bind('change', function(event) { getMonthlyMonthComboType5(); } ); });
        $(function() { $('#monthlyMonthComboType5')  .bind('change', function(event) { getSearchDateType5(DateType.MONTHLY); } ); });

        //월별(기간) 년도,월콤보 체인지 이벤트
        $(function() { $('#monthlyPeriodStartYearComboType5') .bind('change', function(event) { getMonthlyPeriodStartMonthComboType5(); } ); });
        $(function() { $('#monthlyPeriodStartMonthComboType5').bind('change', function(event) { getSearchDateType5(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#monthlyPeriodEndYearComboType5')   .bind('change', function(event) { getMonthlyPeriodEndMonthComboType5(); } ); });
        $(function() { $('#monthlyPeriodEndMonthComboType5')  .bind('change', function(event) { getSearchDateType5(DateType.MONTHLYPERIOD); } ); });

        //요일별 연도,월,주 콤보 체인지이벤트 생성
        $(function() { $('#weekDailyYearComboType5')     .bind('change', function(event) { getWeekDailyMonthComboType5(); } ); });
        $(function() { $('#weekDailyMonthComboType5')    .bind('change', function(event) { getWeekDailyWeekComboType5(); } ); });
        $(function() { $('#weekDailyWeekComboType5')     .bind('change', function(event) { getWeekDailyWeekDayComboType5(); } ); });
        $(function() { $('#weekDailyWeekDayComboType5')  .bind('change', function(event) { getSearchDateType5(DateType.WEEKDAILY); } ); });

        //계절별 연도,계절 콤보 체인지 이벤트 생성
        $(function() { $('#seasonalYearComboType5')  .bind('change', function(event) { getSeasonalSeasonComboType5(); } ); });
        $(function() { $('#seasonalSeasonComboType5').bind('change', function(event) { getSearchDateType5(DateType.SEASONAL); } ); });

        //연별 연도콤보 체인지 이벤트
        $(function() { $('#yearlyYearComboType5')  .bind('change', function(event) { getSearchDateType5(DateType.YEARLY); } ); });


        var locDateFormatType5 = "yymmdd";

        //탭별 일자DatePicker 생성
        $("#hourlyStartDateType5")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormatType5, onSelect: function(dateText, inst) { modifyDateType5(dateText, inst);}} );
        $("#hourlyEndDateType5")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormatType5, onSelect: function(dateText, inst) { modifyDateType5(dateText, inst);}} );
        $("#dailyStartDateType5")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormatType5, onSelect: function(dateText, inst) { modifyDateType5(dateText, inst);}} );
        $("#periodStartDateType5")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormatType5, onSelect: function(dateText, inst) { modifyDateType5(dateText, inst);}} );
        $("#periodEndDateType5")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormatType5, onSelect: function(dateText, inst) { modifyDateType5(dateText, inst);}} );

        $.getJSON("${ctx}/common/getYear.do"
                ,{supplierId:_supplierIdType5}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear   = json.currYear;//currDate.getYear();
                     var currDate  = json.currDate;

                     if(tabsType5.hourly!=0){
	                     $("#hourlyStartDateType5").val(currDate);
	                     $("#hourlyEndDateType5").val(currDate);

	                     var hours = new Array();
	                     for(var i = 0;i<=23;i++){
	                    	 hours[i] = i<10?'0'+i:i+'';
	                     }
	                     $('#hourlyStartHourComboType5').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
	                     $('#hourlyEndHourComboType5').numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
	                     $('#hourlyStartHourComboType5').selectbox();
                         $('#hourlyEndHourComboType5').selectbox();
	                     getSearchDateType5(DateType.HOURLY);
                     }
                     //탭별 현재일자 설정
                     if(tabsType5.daily!=0)dailyArrowType5('',0,false);
                     if(tabsType5.period!=0)setPeriodType5(0,false);

                     if(tabsType5.weekly!=0){
	                     $('#weeklyYearComboType5').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#weeklyYearComboType5').selectbox();
	                     getWeeklyMonthComboType5(false);
                     }

                     if(tabsType5.monthly!=0){
	                     $('#monthlyYearComboType5').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#monthlyYearComboType5').selectbox();
	                     getMonthlyMonthComboType5("",false);
                     }

                     if(tabsType5.monthlyPeriod!=0){
	                     $('#monthlyPeriodStartYearComboType5')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#monthlyPeriodEndYearComboType5')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});

	                     $('#monthlyPeriodStartYearComboType5').selectbox();
	                     $('#monthlyPeriodEndYearComboType5').selectbox();
	                     getMonthlyPeriodStartMonthComboType5(false);
	                     getMonthlyPeriodEndMonthComboType5(false);
                     }

                     if(tabsType5.weekDaily!=0){
	                     $('#weekDailyYearComboType5').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#weekDailyYearComboType5').selectbox();
	                     getWeekDailyMonthComboType5(false);
                     }

                     if(tabsType5.seasonal!=0){
	                     $('#seasonalYearComboType5').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#seasonalYearComboType5').selectbox();
	                     getSeasonalSeasonComboType5(false);
                     }

                     if(tabsType5.yearly!=0){
                    	   $('#yearlyYearComboType5').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    	   $('#yearlyYearComboType5').selectbox();
                     }


                });


        var peroidLabelsType5 = new Array();
        peroidLabelsType5[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
        peroidLabelsType5[1] = '<fmt:message key="aimir.yesterday"/>';//어제''
        peroidLabelsType5[2] = '<fmt:message key="aimir.threedays"/>';//3일''
        peroidLabelsType5[3] = '<fmt:message key="aimir.weekday"/>';//일주일''
        peroidLabelsType5[4] = '<fmt:message key="aimir.onemonth"/>';//한달''

        $("#periodTypeType5").numericOptions({from:0,to:4,selectedIndex:0,labels:peroidLabelsType5});
        $("#periodTypeType5").selectbox();

        //탭생성
        $("#datetabType5").hide();
        $("#datetabType5").tabs();
        $("#datetabType5").show();

        //탭숨김처리
        if(tabsType5.yearly==0)hideTabType5(DateType.YEARLY);
        if(tabsType5.seasonal==0)hideTabType5(DateType.SEASONAL);
        if(tabsType5.weekDaily==0)hideTabType5(DateType.WEEKDAILY);
        if(tabsType5.monthlyPeriod==0)hideTabType5(DateType.MONTHLYPERIOD);
        if(tabsType5.monthly==0)hideTabType5(DateType.MONTHLY);
        if(tabsType5.weekly==0)hideTabType5(DateType.WEEKLY);
        if(tabsType5.period==0)hideTabType5(DateType.PERIOD);
        if(tabsType5.daily==0)hideTabType5(DateType.DAILY);
        if(tabsType5.hourly==0)hideTabType5(DateType.HOURLY);


        if(tabNamesType5.hourly!=null&&tabNamesType5.hourly!=''&&tabNamesType5.hourly!='undefined')$('#_hourlyType5').html(tabNamesType5.hourly);
        if(tabNamesType5.daily!=null&&tabNamesType5.daily!=''&&tabNamesType5.daily!='undefined')$('#_dailyType5').html(tabNamesType5.daily);
        if(tabNamesType5.period!=null&&tabNamesType5.period!=''&&tabNamesType5.period!='undefined')$('#_periodType5').html(tabNamesType5.period);
        if(tabNamesType5.weekly!=null&&tabNamesType5.weekly!=''&&tabNamesType5.weekly!='undefined')$('#_weeklyType5').html(tabNamesType5.weekly);
        if(tabNamesType5.monthly!=null&&tabNamesType5.monthly!=''&&tabNamesType5.monthly!='undefined')$('#_monthlyType5').html(tabNamesType5.monthly);
        if(tabNamesType5.monthlyPeriod!=null&&tabNamesType5.monthlyPeriod!=''&&tabNamesType5.monthlyPeriod!='undefined')$('#_monthlyPeriodType5').html(tabNamesType5.monthlyPeriod);
        if(tabNamesType5.weekDaily!=null&&tabNamesType5.weekDaily!=''&&tabNamesType5.weekDaily!='undefined')$('#_weekDailyType5').html(tabNamesType5.weekDaily);
        if(tabNamesType5.seasonal!=null&&tabNamesType5.seasonal!=''&&tabNamesType5.seasonal!='undefined')$('#_seasonalType5').html(tabNamesType5.seasonal);
        if(tabNamesType5.yearly!=null&&tabNamesType5.yearly!=''&&tabNamesType5.yearly!='undefined')$('#_yearlyType5').html(tabNamesType5.yearly);

        // 검색버튼 표시
        if(tabsType5.search_yearly==1)        showSearchBtnType5('yearlySearchType5');
        if(tabsType5.search_seasonal==1)      showSearchBtnType5('seasonalSearchType5');
        if(tabsType5.search_weekDaily==1)     showSearchBtnType5('weekDailySearchType5');
        if(tabsType5.search_monthlyPeriod==1) showSearchBtnType5('monthlyPeriodSearchType5');
        if(tabsType5.search_monthly==1)       showSearchBtnType5('monthlySearchType5');
        if(tabsType5.search_weekly==1)        showSearchBtnType5('weeklySearchType5');
        if(tabsType5.search_period==1)        showSearchBtnType5('periodSearchType5');
        if(tabsType5.search_daily==1)         showSearchBtnType5('dailySearchType5');
        if(tabsType5.search_hourly==1)        showSearchBtnType5('hourlySearchType5');


    }


    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDateType5(setDate, inst){
        var dateId = '#' + inst.id;

    	$.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:_supplierIdType5}
                ,function(json) {
                	$(dateId).val(json.localDate);
                	$(dateId).trigger('change');
                });
    }


    /**
     * 조회버튼 클릭시 조회조건 검증후 거래호출
     */
    function sendRequestType5(_dateType){
        // 조회조건 검증
        if(!validateSearchConditionType5(_dateType))return false;
        sendType5();
    }


    /**
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     */
    function convertSearchDateType5(){
    	$.getJSON("${ctx}/common/convertSearchDate.do"
                ,{searchStartDate:$('#searchStartDateType5').val(), searchEndDate:$('#searchEndDateType5').val(), supplierId:_supplierIdType5}
                ,function(json) {
                    $('#searchStartDateType5').val(json.searchStartDate);
                    $('#searchEndDateType5').val(json.searchEndDate);
                });
    }

    /**
     * 일별 화살표처리
     */
    function dailyArrowType5(bfDate,val,flag){

    	$.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:bfDate, addVal:val, supplierId:_supplierIdType5}
                ,function(json) {
                    $('#dailyStartDateType5').val(json.searchDate);
                    getSearchDateType5(DateType.DAILY,flag);
                });
    }

    /**
     * 월별 화살표처리
     */
    function monthlyArrowType5(val){
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#monthlyYearComboType5').val(),month:$('#monthlyMonthComboType5').val(),addVal:val}
                ,function(json) {
                    $('#monthlyYearComboType5').val(json.year);
                    $('#monthlyYearComboType5').selectbox();
                    getMonthlyMonthComboType5(json.month);
                });
    }

    /**
     * 기간별 버튼처리
     */
    function setPeriodType5(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierIdType5}
                ,function(json) {
                    $('#periodStartDateType5').val(json.searchDate);
                    $('#periodEndDateType5').val(json.currDate);
                    getSearchDateType5(DateType.PERIOD,flag);
                });
    }

    /**
     * 기간별 버튼처리 - 한달
     */
    function setPeriodOneMonthType5(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierIdType5, monthCalc:true}
                ,function(json) {
                    $('#periodStartDateType5').val(json.searchDate);
                    $('#periodEndDateType5').val(json.currDate);
                    getSearchDateType5(DateType.PERIOD,flag);
                });
    }

    /**
     * 주별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeeklyMonthComboType5(flag){

        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weeklyYearComboType5').val()}
                ,function(json) {
                    var prevMonth = $('#weeklyMonthComboType5').val();
                    $('#weeklyMonthComboType5').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weeklyMonthComboType5').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weeklyMonthComboType5').selectbox();
                    getWeeklyWeekComboType5(flag);
                });
    }

    /**
     * 주별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeeklyWeekComboType5(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weeklyYearComboType5').val(),month:$('#weeklyMonthComboType5').val()}
                ,function(json) {

                    var prevWeek = $('#weeklyWeekComboType5').val();
                    $('#weeklyWeekComboType5').emptySelect();
                    if(prevWeek==""||prevWeek==null||prevWeek > json.weekCount)prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weeklyWeekComboType5').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weeklyWeekComboType5').selectbox();
                    getSearchDateType5(DateType.WEEKLY,flag);
                });
    }



    /**
     * 월별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyMonthComboType5(monthVal,flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyYearComboType5').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyMonthComboType5').val();
                    $('#monthlyMonthComboType5').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyMonthComboType5').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if(monthVal!=null&&monthVal!=""){
                        $('#monthlyMonthComboType5').val(monthVal);
                    }
                    $('#monthlyMonthComboType5').selectbox();
                    getSearchDateType5(DateType.MONTHLY,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodStartMonthComboType5(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodStartYearComboType5').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodStartMonthComboType5').val();
                    $('#monthlyPeriodStartMonthComboType5').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodStartMonthComboType5').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodStartMonthComboType5').selectbox();
                    getSearchDateType5(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodEndMonthComboType5(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodEndYearComboType5').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodEndMonthComboType5').val();
                    $('#monthlyPeriodEndMonthComboType5').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodEndMonthComboType5').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodEndMonthComboType5').selectbox();
                    getSearchDateType5(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 요일별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeekDailyMonthComboType5(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weekDailyYearComboType5').val()}
                ,function(json) {
                    var prevMonth = $('#weekDailyMonthComboType5').val();
                    $('#weekDailyMonthComboType5').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weekDailyMonthComboType5').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weekDailyMonthComboType5').selectbox();
                    getWeekDailyWeekComboType5(flag);
                });
    }

    /**
     * 요일별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeekDailyWeekComboType5(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weekDailyYearComboType5').val(),month:$('#weekDailyMonthComboType5').val()}
                ,function(json) {

                    var prevWeek = $('#weekDailyWeekComboType5').val();
                    $('#weekDailyWeekComboType5').emptySelect();
                    if(prevWeek==""||prevWeek==null||Number(prevWeek) > Number(json.weekCount))prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weekDailyWeekComboType5').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weekDailyWeekComboType5').selectbox();
                    getWeekDailyWeekDayComboType5(flag);
                });
    }

    /**
     * 요일별탭에서 주차콤보 변경시 요일콤보 생성
     * 시작은 일요일
     */
    function getWeekDailyWeekDayComboType5(flag){
        //년도,월,주차 입력받아 현재주이면 오늘날짜까지의 요일만 표시
        $.getJSON("${ctx}/common/getWeekDay.do"
                ,{year:$('#weekDailyYearComboType5').val(),month:$('#weekDailyMonthComboType5').val(),week:$('#weekDailyWeekComboType5').val()}
                ,function(json) {

                    var prevWeek = $('#weekDailyWeekDayComboType5').val();
                    $('#weekDailyWeekDayComboType5').emptySelect();

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

                    $('#weekDailyWeekDayComboType5').numericOptions({from:json.startWeek,to:json.endWeek,labels:dayofweek});
                    $('#weekDailyWeekDayComboType5').val(prevWeek);
                    $('#weekDailyWeekDayComboType5').selectbox();
                    getSearchDateType5(DateType.WEEKDAILY,flag);
                });
    }

    /**
     * 계절별탭에서 년도 변경시 계절콤보 조회
     */
    function getSeasonalSeasonComboType5(flag){
        $.getJSON("${ctx}/common/getSeason.do"
                ,{year:$('#seasonalYearComboType5').val()}
                ,function(json) {

                	seasonDataType5 = json;

                    var prevSeason = $('#seasonalSeasonComboType5').val();
                    $('#seasonalSeasonComboType5').emptySelect();

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

                    $('#seasonalSeasonComboType5').numericOptions({from:1,to:4,labels:season,exclude:excludeIdxs});
                    $('#seasonalSeasonComboType5').val(prevSeason);
                    $('#seasonalSeasonComboType5').selectbox();
                    getSearchDateType5(DateType.SEASONAL,flag);
                });
    }


    /**
     * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
     * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
     * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
     */
    function getSearchDateType5(_dateType,flag){
        var startDate='';
        var endDate='';

        // 화면첫로딩시일경우에는
        // 첫번째탭 클릭 이벤트를 호출한다.
        // ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
        // 첫번째 탭의 날짜를 설정하도록 한다.
        if(flag == false){
            $('div#datetabType5 > ul:first > li:first > a:first').trigger('click');
            return;
        }

        if(DateType.HOURLY == _dateType){
        	if(tabsType5.hourly==0)return;
        	$('#searchStartDateType5').val($('#hourlyStartDateType5').val());
            $('#searchEndDateType5').val($('#hourlyEndDateType5').val());

            $('#searchStartHourType5').val(Number($('#hourlyStartHourComboType5').val())<10?'0'+$('#hourlyStartHourComboType5').val():$('#hourlyStartHourComboType5').val());
            $('#searchEndHourType5')  .val(Number($('#hourlyEndHourComboType5').val())<10?'0'+$('#hourlyEndHourComboType5').val():$('#hourlyEndHourComboType5').val());
            convertSearchDateType5();

        }else if(DateType.DAILY == _dateType){
        	if(tabsType5.daily==0)return;
        	$('#searchStartDateType5').val($('#dailyStartDateType5').val());
            $('#searchEndDateType5').val($('#dailyStartDateType5').val());

            convertSearchDateType5();
        }else if(DateType.PERIOD == _dateType){
        	if(tabsType5.period==0)return;
            $('#searchStartDateType5').val($('#periodStartDateType5').val());
            $('#searchEndDateType5').val($('#periodEndDateType5').val());

            convertSearchDateType5();
        }else if(DateType.WEEKLY == _dateType){
        	if(tabsType5.weekly==0)return;
            $.getJSON("${ctx}/common/getWeekPeriod.do"
            		,{year:$('#weeklyYearComboType5').val(),month:$('#weeklyMonthComboType5').val(),week:$('#weeklyWeekComboType5').val(), supplierId:_supplierIdType5}
                    ,function(json) {
                    	$('#searchStartDateType5').val(json.startDate);
                        $('#searchEndDateType5').val(json.endDate);
                        convertSearchDateType5();
                    });
        }else if(DateType.MONTHLY == _dateType){
        	if(tabsType5.monthly==0)return;
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyYearComboType5').val(),month:$('#monthlyMonthComboType5').val(), supplierId:_supplierIdType5}
                    ,function(json) {
                        $('#searchStartDateType5').val(json.startDate);
                        $('#searchEndDateType5').val(json.endDate);
                        convertSearchDateType5();
                    });
        }else if(DateType.MONTHLYPERIOD == _dateType){
            if(tabsType5.monthlyPeriod==0)return;

            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyPeriodStartYearComboType5').val(),month:$('#monthlyPeriodStartMonthComboType5').val(), supplierId:_supplierIdType5}
                    ,function(json) {
                        $('#searchStartDateType5').val(json.startDate);
                    });
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyPeriodEndYearComboType5').val(),month:$('#monthlyPeriodEndMonthComboType5').val(), supplierId:_supplierIdType5}
                    ,function(json) {
                        $('#searchEndDateType5').val(json.endDate);
                        convertSearchDateType5();
                    });


        }else if(DateType.WEEKDAILY == _dateType){
        	if(tabsType5.weekDaily==0)return;
            $.getJSON("${ctx}/common/getWeekDayPeriod.do"
                    ,{year:$('#weekDailyYearComboType5').val(),month:$('#weekDailyMonthComboType5').val(),week:$('#weekDailyWeekComboType5').val(),weekDay:$('#weekDailyWeekDayComboType5').val(), supplierId:_supplierIdType5}
                    ,function(json) {
                        $('#searchStartDateType5').val(json.startDate);
                        $('#searchEndDateType5').val(json.endDate);
                        convertSearchDateType5();
                    });
        }else if(DateType.SEASONAL == _dateType){
        	if(tabsType5.seasonal==0)return;
        	var seasonIdx = $('#seasonalSeasonComboType5').val();

        	var season;
        	if(seasonIdx == 1){
        		season = seasonDataType5.Spring;
        	}else if(seasonIdx == 2){
        		season = seasonDataType5.Summer;
            }else if(seasonIdx == 3){
            	season = seasonDataType5.Autumn;
            }else if(seasonIdx == 4){
            	season = seasonDataType5.Winter;
            }else{
                season = {startDate:'',endDate:''};
            }

        	$('#searchStartDateType5').val(season.startDate);
            $('#searchEndDateType5').val(season.endDate);


        }else if(DateType.YEARLY == _dateType){
            if(tabsType5.yearly==0)return;

            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#yearlyYearComboType5').val()
                    , supplierId:_supplierIdType5}
                    ,function(json) {
                        $('#searchStartDateType5').val(json.startDate);
                        $('#searchEndDateType5').val(json.endDate);
                        convertSearchDateType5();
                    });
        }


    }


    /**
     * 날짜타입탭을 숨김처리한다.
     */
    function hideTabType5(_idx){
        $("#datetabType5").tabs("remove",_idx);
        $("#datetabType5").tabs("select",0);
    }

    /**
     * 조회버튼을 표시한다.
     */
    function showSearchBtnType5(btnName){

        var liName = '#' + btnName + 'Li';
        $(liName).css('display','block');

    }

    /**
     * 날짜타입별 조회조건 검증
     */
    function validateSearchConditionType5(_dateType){

    	if(DateType.HOURLY == _dateType){
    		if(Number($('#searchStartDateType5').val()) == Number($('#searchEndDateType5').val())){
    			if(Number($('#searchStartHourType5').val()) > Number($('#searchEndHourType5').val())){
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
            if(Number($('#searchStartDateType5').val()) > Number($('#searchEndDateType5').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            if(Number($('#searchEndDateType5').val()) - Number($('#searchStartDateType5').val()) > 30){
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
            if(Number($('#searchStartDateType5').val()) > Number($('#searchEndDateType5').val())){
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
            if($('#seasonalSeasonComboType5').val()==null||$('#seasonalSeasonComboType5').val()==''){
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
<div id="datetabType5" class="buttontype" style="display:none">
    <ul class="buttontype">
        <li><a href="#hourlyType5" id="_hourlyType5"><fmt:message key="aimir.hourly"/></a></li>
        <li><a href="#dailyType5" id="_dailyType5"><fmt:message key="aimir.daily"/></a></li>
        <li><a href="#periodType5" id="_periodType5"><fmt:message key="aimir.period"/></a></li>
        <li><a href="#weeklyType5" id="_weeklyType5"><fmt:message key="aimir.weekly"/></a></li>
        <li><a href="#monthlyType5" id="_monthlyType5"><fmt:message key="aimir.monthly"/></a></li>
        <li><a href="#monthlyPeriodType5" id="_monthlyPeriodType5"><fmt:message key="aimir.monthly"/>(<fmt:message key="aimir.period"/>)</a></li>
        <li><a href="#weekdailyType5" id="_weekdailyType5"><fmt:message key="aimir.weekdaily"/></a></li>
        <li><a href="#seasonalType5" id="_seasonalType5"><fmt:message key="aimir.seasonal"/></a></li>
        <li><a href="#yearlyType5" id="_yearlyType5"><fmt:message key="aimir.yearly"/></a></li>
    </ul>



    <div id="hourlyType5">
	    <!-- <label><fmt:message key="aimir.hourly"/></label> -->
	    <ul>
	        <li><input id="hourlyStartDateType5" class="day" type="text" readonly="readonly"></li>
	        <li class="date-space"></li>
	        <li><select id="hourlyStartHourComboType5" class="sm"></select></li>
	        <li><input value="~" class="between" type="text"></li>
	        <li><input id="hourlyEndDateType5" class="day" type="text" readonly="readonly"></li>
	        <li class="date-space"></li>
	        <li><select id="hourlyEndHourComboType5" class="sm"></select></li>
	    </ul>
	    <div id="btn">
	        <ul><li style="display:none" id="hourlySearchType5Li"><a href="#;" id="hourlySearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	    </div>
    </div>

    <div id="dailyType5">
        <!-- <label><fmt:message key="aimir.date" /></label> -->
        <ul>
            <li><button id="dailyLeftType5" type="button" class="back"></button></li>
            <li><input id="dailyStartDateType5" type="text" class="day" readonly="readonly"></li>
            <li><button id="dailyRightType5" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="dailySearchType5Li"><a href="#;" id="dailySearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="periodType5">
        <!-- <label><fmt:message key="aimir.period"/></label> -->
        <ul>
            <li><select id="periodTypeType5" class="date-w90"></select></li>
            <li><input id="periodStartDateType5" class="day" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="periodEndDateType5" class="day" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="periodSearchType5Li"><a href="#;" id="periodSearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <!--div id="periodType5">
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
                    <li><input id="periodStartDateType5" class="day" type="text" readonly="readonly"></li>
                    <li><input value="~" class="between" type="text"></li>
                    <li><input id="periodEndDateType5" class="day" type="text" readonly="readonly"></li>
                </ul>
                <div id="btn">
                    <ul><li><a href="#;" id="periodSearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
                </div>
           </li>
        </ul>
    </div-->

    <div id="weeklyType5">
        <!-- <label><fmt:message key="aimir.weekly"/></label> -->
        <ul>
            <li><select id="weeklyYearComboType5"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
             <li><select id="weeklyMonthComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
             <li><select id="weeklyWeekComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="weeklySearchType5Li"><a href="#;" id="weeklySearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthlyType5">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><button id="monthlyLeftType5" type="button" class="back"></button></li>
            <li><select id="monthlyYearComboType5"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
           <li><select id="monthlyMonthComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="monthlyRightType5" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="monthlySearchType5Li"><a href="#;" id="monthlySearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthlyPeriodType5">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><select id="monthlyPeriodStartYearComboType5"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li><select id="monthlyPeriodStartMonthComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><input value="~" class="between" type="text"></li>
            <li><select id="monthlyPeriodEndYearComboType5"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
             <li><select id="monthlyPeriodEndMonthComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="monthlyPeriodSearchType5Li"><a href="#;" id="monthlyPeriodSearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="weekdailyType5">
        <!-- <label><fmt:message key="aimir.weekdaily"/></label> -->
        <ul>
            <li><select id="weekDailyYearComboType5"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li><select id="weekDailyMonthComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><select id="weekDailyWeekComboType5" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
            <li><select id="weekDailyWeekDayComboType5" class="date-w80"></select></li>
        </ul>
        <div id="btn" style="display:none">
            <ul><li id="weekDailySearchType5Li"><a href="#;" id="weekDailySearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="seasonalType5">
        <!-- <label><fmt:message key="aimir.seasonal" /></label> -->
        <ul>
            <li><select id="seasonalYearComboType5"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li><select id="seasonalSeasonComboType5" class="date-w80"></select></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="seasonalSearchType5Li"><a href="#;" id="seasonalSearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
    <div id="yearlyType5">
        <!-- <label><fmt:message key="aimir.yearly" /></label> -->
        <ul>
            <li><select id="yearlyYearComboType5"></select></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="yearlySearchType5Li"><a href="#;" id="yearlySearchType5" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
	    <input id="searchStartDateType5" type="hidden"/>
	    <input id="searchEndDateType5" type="hidden" />
	    <input id="searchStartHourType5" type="hidden"/>
	    <input id="searchEndHourType5" type="hidden" />
	    <input id="searchDateTypeType5" type="hidden" value="0"/>
</div>
<!-- Tab (E) -->
