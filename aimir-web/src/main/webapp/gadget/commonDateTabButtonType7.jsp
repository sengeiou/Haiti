<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var seasonData;
    
    function setToday(today)
    {
    
  		  //alert("lsdkflsdkflk==>");
    
    		$('#btn_periodStartDate').val("8/24/12");
        	$('#btn_periodEndDate').val("8/24/12");
    
    }
    
    

    var _supplierId='';
    $(document).ready(function(){
        commonDateTabButton2Interval = setInterval("commonDateTabButton2Delay()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierId = json.supplierId;
                }
        );

    });

    function commonDateTabButton2Delay(){
        if(_supplierId != ''){
            clearInterval(commonDateTabButton2Interval);
            commonDateTabButton2Init();
          }
    }

    function commonDateTabButton2Init(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#btn_hourly')        .bind('click',function(event) { $('#btn_searchDateType').val(DateType.HOURLY        );getBtnSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#btn_daily')         .bind('click',function(event) { $('#btn_searchDateType').val(DateType.DAILY         );getBtnSearchDate(DateType.DAILY); } ); });
        $(function() { $('#btn_period')        .bind('click',function(event) { $('#btn_searchDateType').val(DateType.PERIOD        );getBtnSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#btn_weekly')        .bind('click',function(event) { $('#btn_searchDateType').val(DateType.WEEKLY        );getBtnSearchDate(DateType.WEEKLY); } ); });
        $(function() { $('#btn_monthly')       .bind('click',function(event) { $('#btn_searchDateType').val(DateType.MONTHLY       );getBtnSearchDate(DateType.MONTHLY); } ); });
        $(function() { $('#btn_monthlyPeriod') .bind('click',function(event) { $('#btn_searchDateType').val(DateType.MONTHLYPERIOD );getBtnSearchDate(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#btn_weekdaily')     .bind('click',function(event) { $('#btn_searchDateType').val(DateType.WEEKDAILY     );getBtnSearchDate(DateType.WEEKDAILY); } ); });
        $(function() { $('#btn_seasonal')      .bind('click',function(event) { $('#btn_searchDateType').val(DateType.SEASONAL      );getBtnSearchDate(DateType.SEASONAL); } ); });
        $(function() { $('#btn_yearly')        .bind('click',function(event) { $('#btn_searchDateType').val(DateType.YEARLY        );getBtnSearchDate(DateType.YEARLY); } ); });

        //조회버튼클릭 이벤트 생성
        //$(function() { $('#btn_btnSearch').bind('click',function(event) { btnSendRequest($('#btn_searchDateType').val()); } ); });

        $(function() { $('#btn_hourlySearch')       .bind('click',function(event) { btnRequest(DateType.HOURLY      ); } ); });
        $(function() { $('#btn_dailySearch')        .bind('click',function(event) { btnSendRequest(DateType.DAILY      ); } ); });
        $(function() { $('#btn_periodSearch')       .bind('click',function(event) { btnSendRequest(DateType.PERIOD     ); } ); });
        $(function() { $('#btn_weeklySearch')       .bind('click',function(event) { btnSendRequest(DateType.WEEKLY     ); } ); });
        $(function() { $('#btn_monthlySearch')      .bind('click',function(event) { btnSendRequest(DateType.MONTHLY    ); } ); });
        $(function() { $('#btn_monthlyPeriodSearch').bind('click',function(event) { btnSendRequest(DateType.MONTHLYPERIOD    ); } ); });
        $(function() { $('#btn_weekDailySearch')    .bind('click',function(event) { btnSendRequest(DateType.WEEKDAILY  ); } ); });
        $(function() { $('#btn_seasonalSearch')     .bind('click',function(event) { btnSendRequest(DateType.SEASONAL   ); } ); });
        $(function() { $('#btn_yearlySearch')       .bind('click',function(event) { btnSendRequest(DateType.YEARLY   ); } ); });


        //기간별조회 버튼클릭 이벤트 생성
        $(function() { $('#btn_periodType')     .bind('change',function(event)
                { var idx = $('#btn_periodType').val();
                if(idx==0){
                	setBtnPeriod(0);
                }else if(idx==1){
                	setBtnPeriod(-1);
                }else if(idx==2){
                	setBtnPeriod(-2);
                }else if(idx==3){
                	setBtnPeriod(-6);
                }else if(idx==4){
                	setBtnPeriodOneMonth(-30);
                }

                } ); });

        //$(function() { $('#btn_todayBtn')       .bind('click',function(event) { setBtnPeriod(0); } ); });
        //$(function() { $('#btn_yesterdayBtn')   .bind('click',function(event) { setBtnPeriod(-1); } ); });
        //$(function() { $('#btn_threeDayBtn')    .bind('click',function(event) { setBtnPeriod(-2); } ); });
        //$(function() { $('#btn_weekBtn')        .bind('click',function(event) { setBtnPeriod(-6); } ); });
        //$(function() { $('#btn_monthBtn')       .bind('click',function(event) { setBtnPeriod(-29); } ); });

        //일자별,기간별 날짜입력창 변경시
        $(function() { $('#btn_dailyStartDate')      .bind('change', function(event) { getBtnSearchDate(DateType.DAILY); } ); });
        $(function() { $('#btn_periodStartDate')     .bind('change', function(event) { getBtnSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#btn_periodEndDate')       .bind('change', function(event) { getBtnSearchDate(DateType.PERIOD); } ); });
        $(function() { $('#btn_hourlyStartDate')     .bind('change', function(event) { getBtnSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#btn_hourlyEndDate')       .bind('change', function(event) { getBtnSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#btn_hourlyStartHourCombo').bind('change', function(event) { getBtnSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#btn_hourlyEndHourCombo')  .bind('change', function(event) { getBtnSearchDate(DateType.HOURLY); } ); });

        //일자별,월별 좌우 화살표클릭 이벤트 생성
        $(function() { $('#btn_dailyLeft')      .bind('click',  function(event) { btnDailyArrow($('#btn_dailyStartDate').val(),-1); } ); });
        $(function() { $('#btn_dailyRight')     .bind('click',  function(event) { btnDailyArrow($('#btn_dailyStartDate').val(),1); } ); });
        $(function() { $('#btn_monthlyLeft')    .bind('click',  function(event) { btnMonthlyArrow(-1); } ); });
        $(function() { $('#btn_monthlyRight')   .bind('click',  function(event) { btnMonthlyArrow(1 ); } ); });

        //주별 연도,월 콤보 체인지이벤트 생성
        $(function() { $('#btn_weeklyYearCombo')    .bind('change', function(event) { getBtnWeeklyMonthCombo(); } ); });
        $(function() { $('#btn_weeklyMonthCombo')   .bind('change', function(event) { getBtnWeeklyWeekCombo(); } ); });
        $(function() { $('#btn_weeklyWeekCombo')    .bind('change', function(event) { getBtnSearchDate(DateType.WEEKLY); } ); });

        //월별 연도콤보 체인지이벤트 생성
        $(function() { $('#btn_monthlyYearCombo')   .bind('change', function(event) { getBtnMonthlyMonthCombo(); } ); });
        $(function() { $('#btn_monthlyMonthCombo')  .bind('change', function(event) { getBtnSearchDate(DateType.MONTHLY); } ); });

        //월별(기간) 년도,월콤보 체인지 이벤트
        $(function() { $('#btn_monthlyPeriodStartYearCombo') .bind('change', function(event) { getBtnMonthlyPeriodStartMonthCombo(); } ); });
        $(function() { $('#btn_monthlyPeriodStartMonthCombo').bind('change', function(event) { getBtnSearchDate(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#btn_monthlyPeriodEndYearCombo')   .bind('change', function(event) { getBtnMonthlyPeriodEndMonthCombo(); } ); });
        $(function() { $('#btn_monthlyPeriodEndMonthCombo')  .bind('change', function(event) { getBtnSearchDate(DateType.MONTHLYPERIOD); } ); });

        //요일별 연도,월,주 콤보 체인지이벤트 생성
        $(function() { $('#btn_weekDailyYearCombo')     .bind('change', function(event) { getBtnWeekDailyMonthCombo(); } ); });
        $(function() { $('#btn_weekDailyMonthCombo')    .bind('change', function(event) { getBtnWeekDailyWeekCombo(); } ); });
        $(function() { $('#btn_weekDailyWeekCombo')     .bind('change', function(event) { getBtnWeekDailyWeekDayCombo(); } ); });
        $(function() { $('#btn_weekDailyWeekDayCombo')  .bind('change', function(event) { getBtnSearchDate(DateType.WEEKDAILY); } ); });

        //계절별 연도,계절 콤보 체인지 이벤트 생성
        $(function() { $('#btn_seasonalYearCombo')  .bind('change', function(event) { getBtnSeasonalSeasonCombo(); } ); });
        $(function() { $('#btn_seasonalSeasonCombo').bind('change', function(event) { getBtnSearchDate(DateType.SEASONAL); } ); });

        //연별 연도콤보 체인지 이벤트
        $(function() { $('#btn_yearlyYearCombo')  .bind('change', function(event) { getBtnSearchDate(DateType.YEARLY); } ); });

        var locDateFormat = "yymmdd";

        //탭별 일자DatePicker 생성
        $("#btn_hourlyStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyBtnDate(dateText, inst);}} );
        $("#btn_hourlyEndDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyBtnDate(dateText, inst);}} );
        $("#btn_dailyStartDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyBtnDate(dateText, inst);}} );
        $("#btn_periodStartDate")   .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyBtnDate(dateText, inst);}} );
        $("#btn_periodEndDate")     .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyBtnDate(dateText, inst);}} );

        $.getJSON("${ctx}/common/getYear.do"
        		,{supplierId:_supplierId}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear   = json.currYear;//currDate.getYear();
                     var currDate  = json.currDate;

                     if(tabs8.btn_hourly!=0){
	                     $("#btn_hourlyStartDate").val(currDate);
	                     $("#btn_hourlyEndDate").val(currDate);

	                     var hours = new Array();
	                     for(var i = 0;i<=23;i++){
	                    	 hours[i] = i<10?'0'+i:i+'';
	                     }
	                     $('#btn_hourlyStartHourCombo').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
	                     $('#btn_hourlyEndHourCombo')  .numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
	                     $('#hourlyStartHourCombo').selectbox();
                         $('#hourlyEndHourCombo').selectbox();
	                     getBtnSearchDate(DateType.HOURLY);
                     }
                     //탭별 현재일자 설정
                     if(tabs8.btn_daily!=0)btnDailyArrow('',0,false);
                     if(tabs8.btn_period!=0)setBtnPeriod(0,false);

                     if(tabs8.btn_weekly!=0){
	                     $('#btn_weeklyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#btn_weeklyYearCombo').selectbox();
	                     getBtnWeeklyMonthCombo(false);
                     }

                     if(tabs8.btn_monthly!=0){
	                     $('#btn_monthlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#btn_monthlyYearCombo').selectbox();
	                     getBtnMonthlyMonthCombo("",false);
                     }

                     if(tabs8.btn_monthlyPeriod!=0){
	                     $('#btn_monthlyPeriodStartYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#btn_monthlyPeriodEndYearCombo')  .numericOptions({from:startYear,to:endYear,selectedIndex:9});

	                     $('#btn_monthlyPeriodStartYearCombo').selectbox();
	                     $('#btn_monthlyPeriodEndYearCombo')  .selectbox();
	                     getBtnMonthlyPeriodStartMonthCombo(false);
	                     getBtnMonthlyPeriodEndMonthCombo(false);
                     }

                     if(tabs8.btn_weekDaily!=0){
	                     $('#btn_weekDailyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#btn_weekDailyYearCombo').selectbox();
	                     getBtnWeekDailyMonthCombo(false);
                     }

                     if(tabs8.btn_seasonal!=0){
	                     $('#btn_seasonalYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
	                     $('#btn_seasonalYearCombo').selectbox();
	                     getBtnSeasonalSeasonCombo(false);
                     }

                     if(tabs8.btn_yearly!=0){
                    	   $('#btn_yearlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                    	   $('#btn_yearlyYearCombo').selectbox();
                     }
                });


        var peroidLabels = new Array();
        peroidLabels[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
        peroidLabels[1] = '<fmt:message key="aimir.yesterday"/>';//어제''
        peroidLabels[2] = '<fmt:message key="aimir.threedays"/>';//3일''
        peroidLabels[3] = '<fmt:message key="aimir.weekday"/>';//일주일''
        peroidLabels[4] = '<fmt:message key="aimir.onemonth"/>';//한달''

        $("#btn_periodType").numericOptions({from:0,to:4,selectedIndex:0,labels:peroidLabels});
        $("#btn_periodType").selectbox();

        //탭생성
        $("#btn_datetab").hide();
        $("#btn_datetab").tabs();
        $("#btn_datetab").show();

        //탭숨김처리
        if(tabs8.btn_yearly==0)btnHideTab(DateType.YEARLY);
        if(tabs8.btn_seasonal==0)btnHideTab(DateType.SEASONAL);
        if(tabs8.btn_weekDaily==0)btnHideTab(DateType.WEEKDAILY);
        if(tabs8.btn_monthlyPeriod==0)btnHideTab(DateType.MONTHLYPERIOD);
        if(tabs8.btn_monthly==0)btnHideTab(DateType.MONTHLY);
        if(tabs8.btn_weekly==0)btnHideTab(DateType.WEEKLY);
        if(tabs8.btn_period==0)btnHideTab(DateType.PERIOD);
        if(tabs8.btn_daily==0)btnHideTab(DateType.DAILY);
        if(tabs8.btn_hourly==0)btnHideTab(DateType.HOURLY);

        if(tabNames8.hourly!=null&&tabNames8.hourly!=''&&tabNames8.hourly!='undefined') $('#btn_hourly').html(tabNames8.hourly);
        if(tabNames8.daily!=null&&tabNames8.daily!=''&&tabNames8.daily!='undefined')$('#btn_daily').html(tabNames8.daily);
        if(tabNames8.period!=null&&tabNames8.period!=''&&tabNames8.period!='undefined')$('#btn_period').html(tabNames8.period);
        if(tabNames8.weekly!=null&&tabNames8.weekly!=''&&tabNames8.weekly!='undefined')$('#btn_weekly').html(tabNames8.weekly);
        if(tabNames8.monthly!=null&&tabNames8.monthly!=''&&tabNames8.monthly!='undefined')$('#btn_monthly').html(tabNames8.monthly);
        if(tabNames8.monthlyPeriod!=null&&tabNames8.monthlyPeriod!=''&&tabNames8.monthlyPeriod!='undefined')$('#btn_monthlyPeriod').html(tabNames8.monthlyPeriod);
        if(tabNames8.weekDaily!=null&&tabNames8.weekDaily!=''&&tabNames8.weekDaily!='undefined')$('#btn_weekDaily').html(tabNames8.weekDaily);
        if(tabNames8.seasonal!=null&&tabNames8.seasonal!=''&&tabNames8.seasonal!='undefined')$('#btn_seasonal').html(tabNames8.seasonal);
        if(tabNames8.yearly!=null&&tabNames8.yearly!=''&&tabNames8.yearly!='undefined')$('#btn_yearly').html(tabNames8.yearly);

     // 검색버튼 표시
        if(tabs8.btn_search_yearly==1)        showSearchBtn2('btn_yearlySearch');
        if(tabs8.btn_search_seasonal==1)      showSearchBtn2('btn_seasonalSearch');
        if(tabs8.btn_search_weekDaily==1)     showSearchBtn2('btn_weekDailySearch');
        if(tabs8.btn_search_monthlyPeriod==1) showSearchBtn2('btn_monthlyPeriodSearch');
        if(tabs8.btn_search_monthly==1)       showSearchBtn2('btn_monthlySearch');
        if(tabs8.btn_search_weekly==1)        showSearchBtn2('btn_weeklySearch');
        if(tabs8.btn_search_period==1)        showSearchBtn2('btn_periodSearch');
        if(tabs8.btn_search_daily==1)         showSearchBtn2('btn_dailySearch');
        if(tabs8.btn_search_hourly==1)        showSearchBtn2('btn_hourlySearch');


    }

    // datepicker로 선택한 날짜의 포맷 변경
    function modifyBtnDate(setDate, inst){
        var dateId = '#' + inst.id;

        $.getJSON("${ctx}/common/convertLocalDate.do"
                ,{dbDate:setDate, supplierId:_supplierId}
                ,function(json) {
                    $(dateId).val(json.localDate);
                    $(dateId).trigger('change');
                });
    }

    /**
     * 조회버튼 클릭시 조회조건 검증후 거래호출
     */
    function btnSendRequest(_dateType){
    	console.log(_dateType);
        // 조회조건 검증
        if(!btnValidateSearchCondition(_dateType))return false;
        send2();
    }

    /**
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     */
    function convertBtnSearchDate(){
        $.getJSON("${ctx}/common/convertSearchDate.do"
                ,{searchStartDate:$('#btn_searchStartDate').val(), searchEndDate:$('#btn_searchEndDate').val(), supplierId:_supplierId}
                ,function(json) {
                    $('#btn_searchStartDate').val(json.searchStartDate);
                    $('#btn_searchEndDate').val(json.searchEndDate);
                });
    }



    /**
     * 일별 화살표처리
     */
    function btnDailyArrow(bfDate,val,flag){

        //bfDate = bfDate.replace('/','').replace('/','');

        $.getJSON("${ctx}/common/getDate.do"
        		,{searchDate:bfDate, addVal:val, supplierId:_supplierId}
                ,function(json) {
                    $('#btn_dailyStartDate').val(json.searchDate);
                    getBtnSearchDate(DateType.DAILY,flag);
                });
    }

    /**
     * 월별 화살표처리
     */
    function btnMonthlyArrow(val){
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#btn_monthlyYearCombo').val(),month:$('#btn_monthlyMonthCombo').val(),addVal:val}
                ,function(json) {
                    $('#btn_monthlyYearCombo').val(json.year);
                    $('#btn_monthlyYearCombo').selectbox();
                    getBtnMonthlyMonthCombo(json.month);
                });
    }

    /**
     * 기간별 버튼처리
     */
    function setBtnPeriod(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
        		,{searchDate:'',addVal:val, supplierId:_supplierId}
                ,function(json) {
                    $('#btn_periodStartDate').val(json.searchDate);
                    $('#btn_periodEndDate').val(json.currDate);
                    getBtnSearchDate(DateType.PERIOD,flag);
                });
    }

    /**
     * 기간별 버튼처리 - 한달
     */
    function setBtnPeriodOneMonth(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierId, monthCalc:true}
                ,function(json) {
                    $('#btn_periodStartDate').val(json.searchDate);
                    $('#btn_periodEndDate').val(json.currDate);
                    getBtnSearchDate(DateType.PERIOD,flag);
                });
    }

    /**
     * 주별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getBtnWeeklyMonthCombo(flag){

        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#btn_weeklyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#btn_weeklyMonthCombo').val();
                    $('#btn_weeklyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#btn_weeklyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#btn_weeklyMonthCombo').selectbox();
                    getBtnWeeklyWeekCombo(flag);
                });
    }

    /**
     * 주별탭에서 월콤보 변경시 주콤보 생성
     */
    function getBtnWeeklyWeekCombo(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#btn_weeklyYearCombo').val(),month:$('#btn_weeklyMonthCombo').val()}
                ,function(json) {

                    var prevWeek = $('#btn_weeklyWeekCombo').val();
                    $('#btn_weeklyWeekCombo').emptySelect();
                    if(prevWeek==""||prevWeek==null||prevWeek > json.weekCount)prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#btn_weeklyWeekCombo').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#btn_weeklyWeekCombo').selectbox();
                    getBtnSearchDate(DateType.WEEKLY,flag);
                });
    }



    /**
     * 월별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getBtnMonthlyMonthCombo(monthVal,flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#btn_monthlyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#btn_monthlyMonthCombo').val();
                    $('#btn_monthlyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#btn_monthlyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if(monthVal!=null&&monthVal!=""){
                        $('#btn_monthlyMonthCombo').val(monthVal);
                    }
                    $('#btn_monthlyMonthCombo').selectbox();
                    getBtnSearchDate(DateType.MONTHLY,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getBtnMonthlyPeriodStartMonthCombo(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#btn_monthlyPeriodStartYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#btn_monthlyPeriodStartMonthCombo').val();
                    $('#btn_monthlyPeriodStartMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#btn_monthlyPeriodStartMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#btn_monthlyPeriodStartMonthCombo').selectbox();
                    getBtnSearchDate(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getBtnMonthlyPeriodEndMonthCombo(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#btn_monthlyPeriodEndYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#btn_monthlyPeriodEndMonthCombo').val();
                    $('#btn_monthlyPeriodEndMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#btn_monthlyPeriodEndMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#btn_monthlyPeriodEndMonthCombo').selectbox();
                    getBtnSearchDate(DateType.MONTHLYPERIOD,flag);
                });
    }

    /**
     * 요일별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getBtnWeekDailyMonthCombo(flag){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#btn_weekDailyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#btn_weekDailyMonthCombo').val();
                    $('#btn_weekDailyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#btn_weekDailyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#btn_weekDailyMonthCombo').selectbox();
                    getBtnWeekDailyWeekCombo(flag);
                });
    }

    /**
     * 요일별탭에서 월콤보 변경시 주콤보 생성
     */
    function getBtnWeekDailyWeekCombo(flag){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#btn_weekDailyYearCombo').val(),month:$('#btn_weekDailyMonthCombo').val()}
                ,function(json) {

                    var prevWeek = $('#btn_weekDailyWeekCombo').val();
                    $('#btn_weekDailyWeekCombo').emptySelect();
                    if(prevWeek==""||prevWeek==null||Number(prevWeek) > Number(json.weekCount))prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#btn_weekDailyWeekCombo').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#btn_weekDailyWeekCombo').selectbox();
                    getBtnWeekDailyWeekDayCombo(flag);
                });
    }

    /**
     * 요일별탭에서 주차콤보 변경시 요일콤보 생성
     * 시작은 일요일
     */
    function getBtnWeekDailyWeekDayCombo(flag){
        //년도,월,주차 입력받아 현재주이면 오늘날짜까지의 요일만 표시
        $.getJSON("${ctx}/common/getWeekDay.do"
                ,{year:$('#btn_weekDailyYearCombo').val(),month:$('#btn_weekDailyMonthCombo').val(),week:$('#btn_weekDailyWeekCombo').val()}
                ,function(json) {

                    var prevWeek = $('#btn_weekDailyWeekDayCombo').val();
                    $('#btn_weekDailyWeekDayCombo').emptySelect();

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

                    $('#btn_weekDailyWeekDayCombo').numericOptions({from:json.startWeek,to:json.endWeek,labels:dayofweek});
                    $('#btn_weekDailyWeekDayCombo').val(prevWeek);
                    $('#btn_weekDailyWeekDayCombo').selectbox();
                    getBtnSearchDate(DateType.WEEKDAILY,flag);
                });
    }

    /**
     * 계절별탭에서 년도 변경시 계절콤보 조회
     */
    function getBtnSeasonalSeasonCombo(flag){
        $.getJSON("${ctx}/common/getSeason.do"
                ,{year:$('#btn_seasonalYearCombo').val()}
                ,function(json) {

                	seasonData = json;

                    var prevSeason = $('#btn_seasonalSeasonCombo').val();
                    $('#btn_seasonalSeasonCombo').emptySelect();

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

                    $('#btn_seasonalSeasonCombo').numericOptions({from:1,to:4,labels:season,exclude:excludeIdxs});
                    $('#btn_seasonalSeasonCombo').val(prevSeason);
                    $('#btn_seasonalSeasonCombo').selectbox();
                    getBtnSearchDate(DateType.SEASONAL,flag);
                });
    }


    /**
     * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
     * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
     * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
     */
    function getBtnSearchDate(_dateType,flag){
        var startDate='';
        var endDate='';

        // 화면첫로딩시일경우에는
        // 첫번째탭 클릭 이벤트를 호출한다.
        // ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
        // 첫번째 탭의 날짜를 설정하도록 한다.
        if(flag == false){
            $('div#btn_datetab > ul:first > li:first > a:first').trigger('click');
            return;
        }

        if(DateType.HOURLY == _dateType){
        	if(tabs8.btn_hourly==0)return;
        	$('#btn_searchStartDate').val($('#btn_hourlyStartDate').val());
            $('#btn_searchEndDate').val($('#btn_hourlyEndDate').val());

            $('#btn_searchStartHour').val(Number($('#btn_hourlyStartHourCombo').val())<10?'0'+$('#btn_hourlyStartHourCombo').val():$('#btn_hourlyStartHourCombo').val());
            $('#btn_searchEndHour')  .val(Number($('#btn_hourlyEndHourCombo').val())<10?'0'+$('#btn_hourlyEndHourCombo').val():$('#btn_hourlyEndHourCombo').val());
            convertBtnSearchDate();

        }else if(DateType.DAILY == _dateType){
        	if(tabs8.btn_daily==0)return;
        	$('#btn_searchStartDate').val($('#btn_dailyStartDate').val());
            $('#btn_searchEndDate').val($('#btn_dailyStartDate').val());
            convertBtnSearchDate();
        }else if(DateType.PERIOD == _dateType){
        	if(tabs8.btn_period==0)return;
            $('#btn_searchStartDate').val($('#btn_periodStartDate').val());
            $('#btn_searchEndDate').val($('#btn_periodEndDate').val());
            convertBtnSearchDate();
        }else if(DateType.WEEKLY == _dateType){
        	if(tabs8.btn_weekly==0)return;
            $.getJSON("${ctx}/common/getWeekPeriod.do"
            		,{year:$('#btn_weeklyYearCombo').val(),month:$('#btn_weeklyMonthCombo').val(),week:$('#btn_weeklyWeekCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                    	$('#btn_searchStartDate').val(json.startDate);
                        $('#btn_searchEndDate').val(json.endDate);
                        convertBtnSearchDate();
                    });
        }else if(DateType.MONTHLY == _dateType){
        	if(tabs8.btn_monthly==0)return;
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#btn_monthlyYearCombo').val(),month:$('#btn_monthlyMonthCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                        $('#btn_searchStartDate').val(json.startDate);
                        $('#btn_searchEndDate').val(json.endDate);
                        convertBtnSearchDate();
                    });
        }else if(DateType.MONTHLYPERIOD == _dateType){
            if(tabs8.btn_monthlyPeriod==0)return;

            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#btn_monthlyPeriodStartYearCombo').val(),month:$('#btn_monthlyPeriodStartMonthCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                        $('#btn_searchStartDate').val(json.startDate);
                    });
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#btn_monthlyPeriodEndYearCombo').val(),month:$('#btn_monthlyPeriodEndMonthCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                        $('#btn_searchEndDate').val(json.endDate);
                        convertBtnSearchDate();
                    });


        }else if(DateType.WEEKDAILY == _dateType){
        	if(tabs8.btn_weekDaily==0)return;
            $.getJSON("${ctx}/common/getWeekDayPeriod.do"
                    ,{year:$('#btn_weekDailyYearCombo').val(),month:$('#btn_weekDailyMonthCombo').val(),week:$('#btn_weekDailyWeekCombo').val(),weekDay:$('#btn_weekDailyWeekDayCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                        $('#btn_searchStartDate').val(json.startDate);
                        $('#btn_searchEndDate').val(json.endDate);
                        convertBtnSearchDate();
                    });
        }else if(DateType.SEASONAL == _dateType){
        	if(tabs8.btn_seasonal==0)return;
        	var seasonIdx = $('#btn_seasonalSeasonCombo').val();

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

        	$('#btn_searchStartDate').val(season.startDate);
            $('#btn_searchEndDate').val(season.endDate);
        }else if(DateType.YEARLY == _dateType){
            if(tabs8.btn_yearly==0)return;

            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#btn_yearlyYearCombo').val()}
                    ,function(json) {
                        $('#btn_searchStartDate').val(json.startDate);
                        $('#btn_searchEndDate').val(json.endDate);
                        convertBtnSearchDate();
                    });
        }
    }

    /**
     * 날짜타입탭을 숨김처리한다.
     */
    function btnHideTab(_idx){
        $("#btn_datetab").tabs("remove",_idx);
        $("#btn_datetab").tabs("select",0);
    }

    /**
     * 조회버튼을 표시한다.
     */
    function showSearchBtn2(btnName){
        var liName = '#' + btnName + 'Li';
        $(liName).css('display','block');

    }

    /**
     * 날짜타입별 조회조건 검증
     */
    function btnValidateSearchCondition(_dateType){

    	if(DateType.HOURLY == _dateType){
    		if(Number($('#btn_searchStartDate').val()) == Number($('#btn_searchEndDate').val())){
    			if(Number($('#btn_searchStartHour').val()) > Number($('#btn_searchEndHour').val())){
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
            if(Number($('#btn_searchStartDate').val()) > Number($('#btn_searchEndDate').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            if(Number($('#btn_searchEndDate').val()) - Number($('#btn_searchStartDate').val()) > 30){
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
            if(Number($('#btn_searchStartDate').val()) > Number($('#btn_searchEndDate').val())){
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
            if($('#btn_seasonalSeasonCombo').val()==null||$('#btn_seasonalSeasonCombo').val()==''){
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
<div id="btn_datetab" class="buttontype" style="display:none">
    <ul class="buttontype">
        <li><a href="#div_hourly" id="btn_hourly"><fmt:message key="aimir.hourly"/></a></li>
        <li><a href="#div_daily" id="btn_daily"><fmt:message key="aimir.daily"/></a></li>
        <li><a href="#div_period" id="btn_period"><fmt:message key="aimir.period"/></a></li>
        <li><a href="#div_weekly" id="btn_weekly"><fmt:message key="aimir.weekly"/></a></li>
        <li><a href="#div_monthly" id="btn_monthly"><fmt:message key="aimir.monthly"/></a></li>
        <li><a href="#div_monthlyPeriod" id="btn_monthlyPeriod"><fmt:message key="aimir.monthly"/>(<fmt:message key="aimir.period"/>)</a></li>
        <li><a href="#div_weekdaily" id="btn_weekdaily"><fmt:message key="aimir.weekdaily"/></a></li>
        <li><a href="#div_seasonal" id="btn_seasonal"><fmt:message key="aimir.seasonal"/></a></li>
        <li><a href="#div_yearly" id="btn_yearly"><fmt:message key="aimir.yearly"/></a></li>
    </ul>



    <div id="div_hourly">
	    <!-- <label><fmt:message key="aimir.hourly"/></label> -->
	    <ul>
	        <li><input id="btn_hourlyStartDate" class="day" type="text" readonly="readonly"></li>
	        <li class="date-space"></li>
	        <li><select id="btn_hourlyStartHourCombo" class="sm"></select></li>
	        <li><input value="~" class="between" type="text"></li>
	        <li><input id="btn_hourlyEndDate" class="day" type="text" readonly="readonly"></li>
	        <li class="date-space"></li>
	        <li><select id="btn_hourlyEndHourCombo" class="sm"></select></li>
	    </ul>
	    <div id="btn">
	        <ul><li style="display:none" id="btn_hourlySearchLi"><a href="#;" id="btn_hourlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	    </div>
    </div>

    <div id="div_daily">
        <!-- <label><fmt:message key="aimir.date" /></label> -->
        <ul>
            <li><button id="btn_dailyLeft" type="button" class="back"></button></li>
            <li><input id="btn_dailyStartDate" type="text" class="day" readonly="readonly"></li>
            <li><button id="btn_dailyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_dailySearchLi"><a href="#;" id="btn_dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="div_period">
        <!-- <label><fmt:message key="aimir.period"/></label> -->
        <ul>
            <li><select id="btn_periodType" class="date-w90"></select></li>
            <li class="date-space"></li>
            
            <!-- 날짜 input box -->
            <li>
            	<input id="btn_periodStartDate" class="day" type="text" readonly="readonly">
           	</li>
            <li><input value="~" class="between" type="text"></li>
            
            <li>
            	<input id="btn_periodEndDate" class="day" type="text" readonly="readonly">
           	</li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_periodSearchLi"><a href="#;" id="btn_periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
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

    <div id="div_weekly">
        <!-- <label><fmt:message key="aimir.weekly"/></label> -->
        <ul>
            <li><select id="btn_weeklyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_weeklyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_weeklyWeekCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_weeklySearchLi"><a href="#;" id="btn_weeklySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="div_monthly">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><button id="btn_monthlyLeft" type="button" class="back"></button></li>
            <li><select id="btn_monthlyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_monthlyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="btn_monthlyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_monthlySearchLi"><a href="#;" id="btn_monthlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="div_monthlyPeriod">
        <!-- <label><fmt:message key="aimir.monthly" /></label> -->
        <ul>
            <li><select id="btn_monthlyPeriodStartYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_monthlyPeriodStartMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><input value="~" class="between" type="text"></li>
            <li><select id="btn_monthlyPeriodEndYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_monthlyPeriodEndMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_monthlyPeriodSearchLi"><a href="#;" id="btn_monthlyPeriodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="div_weekdaily">
        <!-- <label><fmt:message key="aimir.weekdaily"/></label> -->
        <ul>
            <li><select id="btn_weekDailyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_weekDailyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_weekDailyWeekCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_weekDailyWeekDayCombo" class="date-w80"></select></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_weekDailySearchLi"><a href="#;" id="btn_weekDailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="div_seasonal">
        <!-- <label><fmt:message key="aimir.seasonal" /></label> -->
        <ul>
            <li><select id="btn_seasonalYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="btn_seasonalSeasonCombo" class="date-w80"></select></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_seasonalSearchLi"><a href="#;" id="btn_seasonalSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
    <div id="div_yearly">
        <!-- <label><fmt:message key="aimir.yearly" /></label> -->
        <ul>
            <li><select id="btn_yearlyYearCombo"></select></li>
        </ul>
        <div id="btn">
            <ul><li style="display:none" id="btn_yearlySearchLi"><a href="#;" id="btn_yearlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
    <!--날짜-->
	    <input id="btn_searchStartDate" type="hidden"/>
	    <input id="btn_searchEndDate"   type="hidden" />
	    <input id="btn_searchStartHour" type="hidden" />
	    <input id="btn_searchEndHour"   type="hidden" />
	    <input id="btn_searchDateType"  type="hidden" value="0"/>
</div>
<!-- Tab (E) -->
