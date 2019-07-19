<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var seasonData;

    $(document).ready(function(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#_hourly')        .bind('click',function(event) { $('#searchDateType').val(DateType.HOURLY        );getSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#_daily')         .bind('click',function(event) { $('#searchDateType').val(DateType.DAILY         );getSearchDate(DateType.DAILY); } ); });
        $(function() { $('#_period')        .bind('click',function(event) { $('#searchDateType').val(DateType.PERIOD        );getSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#_weekly')        .bind('click',function(event) { $('#searchDateType').val(DateType.WEEKLY        );getSearchDate(DateType.WEEKLY); } ); });
        $(function() { $('#_monthly')       .bind('click',function(event) { $('#searchDateType').val(DateType.MONTHLY       );getSearchDate(DateType.MONTHLY); } ); });
        $(function() { $('#_monthlyPeriod') .bind('click',function(event) { $('#searchDateType').val(DateType.MONTHLYPERIOD );getSearchDate(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#_weekdaily')     .bind('click',function(event) { $('#searchDateType').val(DateType.WEEKDAILY     );getSearchDate(DateType.WEEKDAILY); } ); });
        $(function() { $('#_seasonal')      .bind('click',function(event) { $('#searchDateType').val(DateType.SEASONAL      );getSearchDate(DateType.SEASONAL); } ); });
        $(function() { $('#_yearly')        .bind('click',function(event) { $('#searchDateType').val(DateType.YEARLY        );getSearchDate(DateType.YEARLY); } ); });

        //조회버튼클릭 이벤트 생성
        $(function() { $('#btnSearch').bind('click',function(event) { sendRequest($('#searchDateType').val()); } ); });

        $(function() { $('#hourlySearch')       .bind('click',function(event) { sendRequest(DateType.HOURLY      ); } ); });
        $(function() { $('#dailySearch')        .bind('click',function(event) { sendRequest(DateType.DAILY      ); } ); });
        $(function() { $('#periodSearch')       .bind('click',function(event) { sendRequest(DateType.PERIOD     ); } ); });
        $(function() { $('#weeklySearch')       .bind('click',function(event) { sendRequest(DateType.WEEKLY     ); } ); });
        $(function() { $('#monthlySearch')      .bind('click',function(event) { sendRequest(DateType.MONTHLY    ); } ); });
        $(function() { $('#monthlyPeriodSearch').bind('click',function(event) { sendRequest(DateType.MONTHLYPERIOD    ); } ); });
        $(function() { $('#weekDailySearch')    .bind('click',function(event) { sendRequest(DateType.WEEKDAILY  ); } ); });
        $(function() { $('#seasonalSearch')     .bind('click',function(event) { sendRequest(DateType.SEASONAL   ); } ); });
        $(function() { $('#yearlySearch')       .bind('click',function(event) { sendRequest(DateType.YEARLY   ); } ); });

        //기간별조회 버튼클릭 이벤트 생성
        $(function() { $('#periodType')     .bind('change',function(event)
                { var idx = $('#periodType').val();
                if(idx==0){
                	setPeriod(0);
                }else if(idx==1){
                	setPeriod(-1);
                }else if(idx==2){
                	setPeriod(-2);
                }else if(idx==3){
                	setPeriod(-6);
                }else if(idx==4){
                	setPeriodOneMonth(-30);
                }

                } ); });

        //$(function() { $('#todayBtn')       .bind('click',function(event) { setPeriod(0); } ); });
        //$(function() { $('#yesterdayBtn')   .bind('click',function(event) { setPeriod(-1); } ); });
        //$(function() { $('#threeDayBtn')    .bind('click',function(event) { setPeriod(-2); } ); });
        //$(function() { $('#weekBtn')        .bind('click',function(event) { setPeriod(-6); } ); });
        //$(function() { $('#monthBtn')       .bind('click',function(event) { setPeriod(-29); } ); });

        //일자별,기간별 날짜입력창 변경시
        $(function() { $('#dailyStartDate')     .bind('change', function(event) { getSearchDate(DateType.DAILY); } ); });
        $(function() { $('#periodStartDate')    .bind('change', function(event) { getSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#periodEndDate')      .bind('change', function(event) { getSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#hourlyStartDate')    .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#hourlyEndDate')      .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#hourlyStartHourCombo').bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#hourlyEndHourCombo')  .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });

        //일자별,월별 좌우 화살표클릭 이벤트 생성
        $(function() { $('#dailyLeft')      .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),-1); } ); });
        $(function() { $('#dailyRight')     .bind('click',  function(event) { dailyArrow($('#dailyStartDate').val(),1); } ); });
        $(function() { $('#monthlyLeft')    .bind('click',  function(event) { monthlyArrow(-1); } ); });
        $(function() { $('#monthlyRight')   .bind('click',  function(event) { monthlyArrow(1 ); } ); });

        //주별 연도,월 콤보 체인지이벤트 생성
        $(function() { $('#weeklyYearCombo')    .bind('change', function(event) { getWeeklyMonthCombo(); } ); });
        $(function() { $('#weeklyMonthCombo')   .bind('change', function(event) { getWeeklyWeekCombo(); } ); });
        $(function() { $('#weeklyWeekCombo')    .bind('change', function(event) { getSearchDate(DateType.WEEKLY); } ); });

        //월별 연도콤보 체인지이벤트 생성
        $(function() { $('#monthlyYearCombo')   .bind('change', function(event) { getMonthlyMonthCombo(); } ); });
        $(function() { $('#monthlyMonthCombo')  .bind('change', function(event) { getSearchDate(DateType.MONTHLY); } ); });

        //월별(기간) 년도,월콤보 체인지 이벤트
        $(function() { $('#monthlyPeriodStartYearCombo') .bind('change', function(event) { getMonthlyPeriodStartMonthCombo(); } ); });
        $(function() { $('#monthlyPeriodStartMonthCombo').bind('change', function(event) { getSearchDate(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#monthlyPeriodEndYearCombo')   .bind('change', function(event) { getMonthlyPeriodEndMonthCombo(); } ); });
        $(function() { $('#monthlyPeriodEndMonthCombo')  .bind('change', function(event) { getSearchDate(DateType.MONTHLYPERIOD); } ); });

        //요일별 연도,월,주 콤보 체인지이벤트 생성
        $(function() { $('#weekDailyYearCombo')     .bind('change', function(event) { getWeekDailyMonthCombo(); } ); });
        $(function() { $('#weekDailyMonthCombo')    .bind('change', function(event) { getWeekDailyWeekCombo(); } ); });
        $(function() { $('#weekDailyWeekCombo')     .bind('change', function(event) { getWeekDailyWeekDayCombo(); } ); });
        $(function() { $('#weekDailyWeekDayCombo')  .bind('change', function(event) { getSearchDate(DateType.WEEKDAILY); } ); });

        //계절별 연도,계절 콤보 체인지 이벤트 생성
        $(function() { $('#seasonalYearCombo')  .bind('change', function(event) { getSeasonalSeasonCombo(); } ); });
        $(function() { $('#seasonalSeasonCombo').bind('change', function(event) { getSearchDate(DateType.SEASONAL); } ); });

        //연별 연도콤보 체인지 이벤트
        $(function() { $('#yearlyYearCombo')  .bind('change', function(event) { getSearchDate(DateType.YEARLY); } ); });

        //탭별 일자DatePicker 생성
        $("#hourlyStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#hourlyEndDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#dailyStartDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#periodStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});
        $("#periodEndDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true});

        $.getJSON("${ctx}/common/getYear.do"
                ,{}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear = json.currYear;//currDate.getYear();
                     var currDate = json.currDate;

                     if(tabs.hourly!=0){
	                     $("#hourlyStartDate").val(currDate);
	                     $("#hourlyEndDate").val(currDate);

	                     var hours = new Array();
	                     for(var i = 0;i<=23;i++){
	                    	 hours[i] = i<10?'0'+i:i+'';
	                     }
	                     $('#hourlyStartHourCombo').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
	                     $('#hourlyEndHourCombo').numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
	                     getSearchDate(DateType.HOURLY);
                     }
                     //탭별 현재일자 설정
                     if(tabs.daily!=0)dailyArrow('',0,false);
                     if(tabs.period!=0)setPeriod(0,false);

                     if(tabs.weekly!=0){
	                     $('#weeklyYearCombo')   .numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#weeklyYearCombo').selectbox();
	                     getWeeklyMonthCombo(false);
                     }

                     if(tabs.monthly!=0){
	                     $('#monthlyYearCombo')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#monthlyYearCombo').selectbox();
	                     getMonthlyMonthCombo("",false);
                     }

                     if(tabs.monthlyPeriod!=0){
	                     $('#monthlyPeriodStartYearCombo')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#monthlyPeriodEndYearCombo')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});

	                     $('#monthlyPeriodStartYearCombo').selectbox();
	                     $('#monthlyPeriodEndYearCombo').selectbox();
	                     getMonthlyPeriodStartMonthCombo(false);
	                     getMonthlyPeriodEndMonthCombo(false);
                     }

                     if(tabs.weekDaily!=0){
	                     $('#weekDailyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#weekDailyYearCombo').selectbox();
	                     getWeekDailyMonthCombo(false);
                     }

                     if(tabs.seasonal!=0){
	                     $('#seasonalYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#seasonalYearCombo').selectbox();
	                     getSeasonalSeasonCombo(false);
                     }

                     if(tabs.yearly!=0){
                    	   $('#yearlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    	   $('#yearlyYearCombo').selectbox();
                     }
                });


        var peroidLabels = new Array();
        peroidLabels[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
        peroidLabels[1] = '<fmt:message key="aimir.yesterday"/>';//어제'
        peroidLabels[2] = '<fmt:message key="aimir.threedays"/>';//3일'
        peroidLabels[3] = '<fmt:message key="aimir.weekday"/>';//일주일'
        peroidLabels[4] = '<fmt:message key="aimir.onemonth"/>';//한달'

        $("#periodType").numericOptions({from:0,to:4,selectedIndex:0,labels:peroidLabels});
        $("#periodType").selectbox();
        
        //탭생성
        //탭생성
        $("#datetab").hide();
        $("#datetab").tabs();
        $("#datetab").show();

        //탭숨김처리
        if(tabs.yearly==0)hideTab(DateType.YEARLY);
        if(tabs.seasonal==0)hideTab(DateType.SEASONAL);
        if(tabs.weekDaily==0)hideTab(DateType.WEEKDAILY);
        if(tabs.monthlyPeriod==0)hideTab(DateType.MONTHLYPERIOD);
        if(tabs.monthly==0)hideTab(DateType.MONTHLY);
        if(tabs.weekly==0)hideTab(DateType.WEEKLY);
        if(tabs.period==0)hideTab(DateType.PERIOD);
        if(tabs.daily==0)hideTab(DateType.DAILY);
        if(tabs.hourly==0)hideTab(DateType.HOURLY);

        if(tabNames.hourly!=null&&tabNames.hourly!=''&&tabNames.hourly!='undefined')$('#_hourly').html(tabNames.hourly);
        if(tabNames.daily!=null&&tabNames.daily!=''&&tabNames.daily!='undefined')$('#_daily').html(tabNames.daily);
        if(tabNames.period!=null&&tabNames.period!=''&&tabNames.period!='undefined')$('#_period').html(tabNames.period);
        if(tabNames.weekly!=null&&tabNames.weekly!=''&&tabNames.weekly!='undefined')$('#_weekly').html(tabNames.weekly);
        if(tabNames.monthly!=null&&tabNames.monthly!=''&&tabNames.monthly!='undefined')$('#_monthly').html(tabNames.monthly);
        if(tabNames.monthlyPeriod!=null&&tabNames.monthlyPeriod!=''&&tabNames.monthlyPeriod!='undefined')$('#_monthlyPeriod').html(tabNames.monthlyPeriod);
        if(tabNames.weekDaily!=null&&tabNames.weekDaily!=''&&tabNames.weekDaily!='undefined')$('#_weekDaily').html(tabNames.weekDaily);
        if(tabNames.seasonal!=null&&tabNames.seasonal!=''&&tabNames.seasonal!='undefined')$('#_seasonal').html(tabNames.seasonal);
        if(tabNames.yearly!=null&&tabNames.yearly!=''&&tabNames.yearly!='undefined')$('#_yearly').html(tabNames.yearly);

    });

    /**
     * 조회버튼 클릭시 조회조건 검증후 거래호출
     */
    function sendRequest(_dateType){
        // 조회조건 검증
        if(!validateSearchCondition(_dateType))return false;
        send();
    }

    /**
     * 일별 화살표처리
     */
    function dailyArrow(bfDate,val,flag){

        bfDate = bfDate.replace('/','').replace('/','');

        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:bfDate,addVal:val}
                ,function(json) {
                    $('#dailyStartDate').val(json.searchDate);
                    getSearchDate(DateType.DAILY,flag);
                });
    }

    /**
     * 월별 화살표처리
     */
    function monthlyArrow(val){
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#monthlyYearCombo').val(),month:$('#monthlyMonthCombo').val(),addVal:val}
                ,function(json) {
                    $('#monthlyYearCombo').val(json.year);
                    $('#monthlyYearCombo').selectbox();
                    getMonthlyMonthCombo(json.month);
                });
    }

    /**
     * 기간별 버튼처리
     */
    function setPeriod(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val}
                ,function(json) {
                    $('#periodStartDate').val(json.searchDate);
                    $('#periodEndDate').val(json.currDate);
                    getSearchDate(DateType.PERIOD,flag);
                });
    }

    /**
     * 기간별 버튼처리 - 한달
     */
    function setPeriodOneMonth(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'', addVal:val, monthCalc:true}
                ,function(json) {
                    $('#periodStartDate').val(json.searchDate);
                    $('#periodEndDate').val(json.currDate);
                    getSearchDate(DateType.PERIOD,flag);
                });
    }

    /**
     * 주별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeeklyMonthCombo(flag){

        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weeklyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#weeklyMonthCombo').val();
                    $('#weeklyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weeklyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weeklyMonthCombo').selectbox();
                    getWeeklyWeekCombo(flag);
                });
    }

    /**
     * 주별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeeklyWeekCombo(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weeklyYearCombo').val(),month:$('#weeklyMonthCombo').val()}
                ,function(json) {

                    var prevWeek = $('#weeklyWeekCombo').val();
                    $('#weeklyWeekCombo').emptySelect();
                    if(prevWeek==""||prevWeek==null||prevWeek > json.weekCount)prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weeklyWeekCombo').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weeklyWeekCombo').selectbox();
                    getSearchDate(DateType.WEEKLY,flag);
                });
    }



    /**
     * 월별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyMonthCombo(monthVal,flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyMonthCombo').val();
                    $('#monthlyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if(monthVal!=null&&monthVal!=""){
                        $('#monthlyMonthCombo').val(monthVal);
                    }
                    $('#monthlyMonthCombo').selectbox();
                    getSearchDate(DateType.MONTHLY,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodStartMonthCombo(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodStartYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodStartMonthCombo').val();
                    $('#monthlyPeriodStartMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodStartMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodStartMonthCombo').selectbox();
                    getSearchDate(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodEndMonthCombo(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodEndYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodEndMonthCombo').val();
                    $('#monthlyPeriodEndMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodEndMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodEndMonthCombo').selectbox();
                    getSearchDate(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 요일별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeekDailyMonthCombo(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weekDailyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#weekDailyMonthCombo').val();
                    $('#weekDailyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weekDailyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weekDailyMonthCombo').selectbox();
                    getWeekDailyWeekCombo(flag);
                });
    }

    /**
     * 요일별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeekDailyWeekCombo(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weekDailyYearCombo').val(),month:$('#weekDailyMonthCombo').val()}
                ,function(json) {

                    var prevWeek = $('#weekDailyWeekCombo').val();
                    $('#weekDailyWeekCombo').emptySelect();
                    if(prevWeek==""||prevWeek==null||Number(prevWeek) > Number(json.weekCount))prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weekDailyWeekCombo').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weekDailyWeekCombo').selectbox();
                    getWeekDailyWeekDayCombo(flag);
                });
    }

    /**
     * 요일별탭에서 주차콤보 변경시 요일콤보 생성
     * 시작은 일요일
     */
    function getWeekDailyWeekDayCombo(flag){
        //년도,월,주차 입력받아 현재주이면 오늘날짜까지의 요일만 표시
        $.getJSON("${ctx}/common/getWeekDay.do"
                ,{year:$('#weekDailyYearCombo').val(),month:$('#weekDailyMonthCombo').val(),week:$('#weekDailyWeekCombo').val()}
                ,function(json) {

                    var prevWeek = $('#weekDailyWeekDayCombo').val();
                    $('#weekDailyWeekDayCombo').emptySelect();

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

                    $('#weekDailyWeekDayCombo').numericOptions({from:json.startWeek,to:json.endWeek,labels:dayofweek});
                    $('#weekDailyWeekDayCombo').val(prevWeek);
                    $('#weekDailyWeekDayCombo').selectbox();
                    getSearchDate(DateType.WEEKDAILY,flag);
                });
    }

    /**
     * 계절별탭에서 년도 변경시 계절콤보 조회
     */
    function getSeasonalSeasonCombo(flag){
        $.getJSON("${ctx}/common/getSeason.do"
                ,{year:$('#seasonalYearCombo').val()}
                ,function(json) {

                	seasonData = json;

                    var prevSeason = $('#seasonalSeasonCombo').val();
                    $('#seasonalSeasonCombo').emptySelect();

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

                    $('#seasonalSeasonCombo').numericOptions({from:1,to:4,labels:season,exclude:excludeIdxs});
                    $('#seasonalSeasonCombo').val(prevSeason);
                    $('#seasonalSeasonCombo').selectbox();
                    getSearchDate(DateType.SEASONAL,flag);
                });
    }


    /**
     * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
     * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
     * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
     */
    function getSearchDate(_dateType,flag){
        var startDate='';
        var endDate='';
$("#myUl").hide();
        // 화면첫로딩시일경우에는
        // 첫번째탭 클릭 이벤트를 호출한다.
        // ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
        // 첫번째 탭의 날짜를 설정하도록 한다.
        if(flag == false){
            $('div#datetab > ul:first > li:first > a:first').trigger('click');
            return;
        }

        if(DateType.HOURLY == _dateType){
        	if(tabs.hourly==0)return;
        	$('#searchStartDate').val($('#hourlyStartDate').val().replace('/','').replace('/',''));
            $('#searchEndDate').val($('#hourlyEndDate').val().replace('/','').replace('/',''));

            $('#searchStartHour').val(Number($('#hourlyStartHourCombo').val())<10?'0'+$('#hourlyStartHourCombo').val():$('#hourlyStartHourCombo').val());
            $('#searchEndHour')  .val(Number($('#hourlyEndHourCombo').val())<10?'0'+$('#hourlyEndHourCombo').val():$('#hourlyEndHourCombo').val());


        }else if(DateType.DAILY == _dateType){
        	if(tabs.daily==0)return;
        	$('#searchStartDate').val($('#dailyStartDate').val().replace('/','').replace('/',''));
            $('#searchEndDate').val($('#dailyStartDate').val().replace('/','').replace('/',''));
        }else if(DateType.PERIOD == _dateType){
        	if(tabs.period==0)return;
            $('#searchStartDate').val($('#periodStartDate').val().replace('/','').replace('/',''));
            $('#searchEndDate').val($('#periodEndDate').val().replace('/','').replace('/',''));
        }else if(DateType.WEEKLY == _dateType){
        	if(tabs.weekly==0)return;
            $.getJSON("${ctx}/common/getWeekPeriod.do"
            		,{year:$('#weeklyYearCombo').val(),month:$('#weeklyMonthCombo').val(),week:$('#weeklyWeekCombo').val()}
                    ,function(json) {
                    	$('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                    });
        }else if(DateType.MONTHLY == _dateType){
        	if(tabs.monthly==0)return;
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyYearCombo').val(),month:$('#monthlyMonthCombo').val()}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                    });
        }else if(DateType.MONTHLYPERIOD == _dateType){
            if(tabs.monthlyPeriod==0)return;

            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyPeriodStartYearCombo').val(),month:$('#monthlyPeriodStartMonthCombo').val()}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                    });
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyPeriodEndYearCombo').val(),month:$('#monthlyPeriodEndMonthCombo').val()}
                    ,function(json) {
                        $('#searchEndDate').val(json.endDate);
                    });


        }else if(DateType.WEEKDAILY == _dateType){
        	if(tabs.weekDaily==0)return;
            $.getJSON("${ctx}/common/getWeekDayPeriod.do"
                    ,{year:$('#weekDailyYearCombo').val(),month:$('#weekDailyMonthCombo').val(),week:$('#weekDailyWeekCombo').val(),weekDay:$('#weekDailyWeekDayCombo').val()}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                    });
        }else if(DateType.SEASONAL == _dateType){
        	if(tabs.seasonal==0)return;
        	var seasonIdx = $('#seasonalSeasonCombo').val();

        	var season;
        	if(seasonIdx == 1){
        		season = seasonData.Spring;
        	}else if(seasonIdx == 2){
        		season = seasonData.Summer;
            }else if(seasonIdx == 3){
            	season = seasonData.Autumn;
            }else if(seasonIdx == 4){
            	season = seasonData.Winter;
            }else{
                season = {startDate:'',endDate:''};
            }

        	$('#searchStartDate').val(season.startDate);
            $('#searchEndDate').val(season.endDate);
        }else if(DateType.YEARLY == _dateType){
            if(tabs.yearly==0)return;

            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#yearlyYearCombo').val()}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                    });
        }
    }

    /**
     * 날짜타입탭을 숨김처리한다.
     */
    function hideTab(_idx){
        $("#datetab").tabs("remove",_idx);
        $("#datetab").tabs("select",0);
    }

    /**
     * 날짜타입별 조회조건 검증
     */
    function validateSearchCondition(_dateType){

    	if(DateType.HOURLY == _dateType){
    		if(Number($('#searchStartDate').val()) == Number($('#searchEndDate').val())){
    			if(Number($('#searchStartHour').val()) > Number($('#searchEndHour').val())){
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
            if(Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            if(Number($('#searchEndDate').val()) - Number($('#searchStartDate').val()) > 30){
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
            if(Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())){
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
            if($('#seasonalSeasonCombo').val()==null||$('#seasonalSeasonCombo').val()==''){
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
<div id="datetab" style="background:#fff">

        <ul class="noTapSearch">
            <li class="tit_default">주기</li>
            <li>
                <select id="periodType" style="width:40px">
                  <option value="1"><fmt:message key="aimir.daily"/></option>
                  <option value="3"><fmt:message key="aimir.weekly"/></option>
                  <option value="4"><fmt:message key="aimir.monthly"/></option>
                  <option value="9"><fmt:message key="aimir.seasonal"/></option>
                </select>
            </li> 
           	<li><button id="dailyLeft" type="button" class="backicon srrow" ></button></li>
		   	<li><input id="dailyStartDate" type="text" readonly="readonly" style="width:70px"></li>
		   	<li><button id="dailyRight" type="button" class="nexticon srrow" ></button></li>
			<li><em class="bems_button mt2"><a href="" id="dailySearch"><fmt:message key="aimir.button.search" /></a></em></li>           
        </ul>
        
</div>
<!-- Tab (E) -->
