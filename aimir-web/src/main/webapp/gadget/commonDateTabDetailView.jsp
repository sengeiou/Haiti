<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var startSeasonData;
    var endSeasonData;
    var tabClickExec = true;
    var defaultSelect = 0;      // tab 최초 생성 시 기본으로 선택되는 tab index

    var _supplierId='';
    $(document).ready(function(){
        commonDateTabInterval = setInterval("commonDateTabDelay()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierId = json.supplierId;
                }
        );
    });

    function commonDateTabDelay(){
        if(_supplierId != ''){
            clearInterval(commonDateTabInterval);
            commonDateTabInit();
        }
    }

    function commonDateTabInit(){

        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        $(function() { $('#_rately')     .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateTabOther.RATE    );getSearchDate(DateTabOther.RATE);}tabClickExec=true;} ); });
        $(function() { $('#_lpintervals').bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateTabOther.INTERVAL);getSearchDate(DateTabOther.INTERVAL);}tabClickExec=true;} ); });
        $(function() { $('#_hourly')     .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateType.HOURLY      );getSearchDate(DateType.HOURLY);}tabClickExec=true;} ); });
        $(function() { $('#_daily')      .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateType.DAILY       );getSearchDate(DateType.DAILY);}tabClickExec=true;} ); });
        $(function() { $('#_weekly')     .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateType.WEEKLY      );getSearchDate(DateType.WEEKLY);}tabClickExec=true;} ); });
        $(function() { $('#_monthly')    .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateType.MONTHLY     );getSearchDate(DateType.MONTHLY);}tabClickExec=true;} ); });
        $(function() { $('#_weekdaily')  .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateType.WEEKDAILY   );getSearchDate(DateType.WEEKDAILY);}tabClickExec=true;} ); });
        $(function() { $('#_seasonal')   .bind('click',function(event) { if(tabClickExec){$('#searchDateType').val(DateType.SEASONAL    );getSearchDate(DateType.SEASONAL);}tabClickExec=true;} ); });

        //조회버튼클릭 이벤트 생성
        $(function() { $('#ratelySearch')   .bind('click',function(event) { sendRequest(DateTabOther.RATE ); } ); });
        $(function() { $('#intervalSearch') .bind('click',function(event) { sendRequest(DateTabOther.INTERVAL); } ); });
        $(function() { $('#hourlySearch')   .bind('click',function(event) { sendRequest(DateType.HOURLY     ); } ); });
        $(function() { $('#dailySearch')    .bind('click',function(event) { sendRequest(DateType.DAILY      ); } ); });
        $(function() { $('#weeklySearch')   .bind('click',function(event) { sendRequest(DateType.WEEKLY     ); } ); });
        $(function() { $('#monthlySearch')  .bind('click',function(event) { sendRequest(DateType.MONTHLY    ); } ); });
        $(function() { $('#weekDailySearch').bind('click',function(event) { sendRequest(DateType.WEEKDAILY  ); } ); });
        $(function() { $('#seasonalSearch') .bind('click',function(event) { sendRequest(DateType.SEASONAL   ); } ); });

        //기간별조회 버튼클릭 이벤트 생성
        /*$(function() { $('#periodType').bind('change', function(event) {
            var idx = $('#periodType').val();
            if (idx == 0) {
                setPeriod(0);
            } else if (idx == 1) {
                setPeriod(-1);
            } else if (idx == 2) {
                setPeriod(-2);
            } else if (idx == 3) {
                setPeriod(-6);
            } else if (idx == 4) {
                setPeriod(-30);
            }
        } ); });*/

        //일자별,기간별 날짜입력창 변경시
        $(function() { $('#ratelyStartDate')       .bind('change', function(event) { getSearchDate(DateTabOther.RATE); } ); });
        $(function() { $('#ratelyEndDate')         .bind('change', function(event) { getSearchDate(DateTabOther.RATE); } ); });
        $(function() { $('#intervalStartDate')     .bind('change', function(event) { getSearchDate(DateTabOther.INTERVAL); } ); });
        $(function() { $('#intervalEndDate')       .bind('change', function(event) { getSearchDate(DateTabOther.INTERVAL); } ); });
        $(function() { $('#intervalStartHourCombo').bind('change', function(event) { getSearchDate(DateTabOther.INTERVAL); } ); });
        $(function() { $('#intervalEndHourCombo')  .bind('change', function(event) { getSearchDate(DateTabOther.INTERVAL); } ); });
        $(function() { $('#hourlyStartDate')       .bind('change', function(event) { getSearchDate(DateType.HOURLY); } ); });
        $(function() { $('#dailyStartDate')        .bind('change', function(event) { getSearchDate(DateType.DAILY); } ); });
        $(function() { $('#dailyEndDate')          .bind('change', function(event) { getSearchDate(DateType.DAILY); } ); });
        //$(function() { $('#periodStartDate').bind('change', function(event) { getSearchDate(DateType.PERIOD); } ); });
        //$(function() { $('#periodEndDate')  .bind('change', function(event) { getSearchDate(DateType.PERIOD); } ); });

        //Rately 연도콤보 체인지이벤트 생성
        //$(function() { $('#ratelyYearCombo').bind('change', function(event) { getRatelyMonthCombo(""); } ); });
        //$(function() { $('#ratelyMonthCombo').bind('change', function(event) { getSearchDate(DateTabOther.RATE); } ); });

        //일자별,월별,Rately 좌우 화살표클릭 이벤트 생성
        $(function() { $('#hourlyLeft')    .bind('click',  function(event) { hourlyArrow($('#hourlyStartDate').val(),-1); } ); });
        $(function() { $('#hourlyRight')   .bind('click',  function(event) { hourlyArrow($('#hourlyStartDate').val(),1); } ); });
        //$(function() { $('#dailyLeft')     .bind('click',  function(event) { dailyArrow(-1); } ); });
        //$(function() { $('#dailyRight')    .bind('click',  function(event) { dailyArrow(1 ); } ); });
        $(function() { $('#weekDailyLeft') .bind('click',  function(event) { weekDailyArrow(-1); } ); });
        $(function() { $('#weekDailyRight').bind('click',  function(event) { weekDailyArrow(1 ); } ); });
        //$(function() { $('#ratelyLeft')    .bind('click',  function(event) { ratelyArrow(-1); } ); });
        //$(function() { $('#ratelyRight')   .bind('click',  function(event) { ratelyArrow(1 ); } ); });

        //주별 연도,월 콤보 체인지이벤트 생성
        $(function() { $('#weeklyYearCombo')    .bind('change', function(event) { getWeeklyMonthCombo(); } ); });
        $(function() { $('#weeklyMonthCombo')   .bind('change', function(event) { getWeeklyWeekCombo(); } ); });
        $(function() { $('#weeklyWeekCombo')    .bind('change', function(event) { getSearchDate(DateType.WEEKLY); } ); });
        $(function() { $('#weeklyYearEndCombo') .bind('change', function(event) { getWeeklyMonthEndCombo(); } ); });
        $(function() { $('#weeklyMonthEndCombo').bind('change', function(event) { getWeeklyWeekEndCombo(); } ); });
        $(function() { $('#weeklyWeekEndCombo') .bind('change', function(event) { getSearchDate(DateType.WEEKLY); } ); });

        //일별 연도콤보 체인지이벤트 생성
        //$(function() { $('#dailyYearCombo') .bind('change', function(event) { getDailyMonthCombo(""); } ); });
        //$(function() { $('#dailyMonthCombo').bind('change', function(event) { getSearchDate(DateType.DAILY); } ); });

        //월별 연도콤보 체인지 이벤트
        $(function() { $('#monthlyYearCombo').bind('change', function(event) { getSearchDate(DateType.MONTHLY); } ); });

        //요일별 연도콤보 체인지이벤트 생성
        $(function() { $('#weekDailyYearCombo') .bind('change', function(event) { getWeekDailyMonthCombo(""); } ); });
        $(function() { $('#weekDailyMonthCombo').bind('change', function(event) { getSearchDate(DateType.WEEKDAILY); } ); });

        //월별(기간) 년도,월콤보 체인지 이벤트
        $(function() { $('#monthlyPeriodStartYearCombo') .bind('change', function(event) { getMonthlyPeriodStartMonthCombo(); } ); });
        $(function() { $('#monthlyPeriodStartMonthCombo').bind('change', function(event) { getSearchDate(DateType.MONTHLYPERIOD); } ); });
        $(function() { $('#monthlyPeriodEndYearCombo')   .bind('change', function(event) { getMonthlyPeriodEndMonthCombo(); } ); });
        $(function() { $('#monthlyPeriodEndMonthCombo')  .bind('change', function(event) { getSearchDate(DateType.MONTHLYPERIOD); } ); });

        //계절별 연도,계절 콤보 체인지 이벤트 생성
        $(function() { $('#seasonalStartYearCombo')  .bind('change', function(event) { getSeasonalStartSeasonCombo(); } ); });
        $(function() { $('#seasonalStartSeasonCombo').bind('change', function(event) { getSearchDate(DateType.SEASONAL); } ); });
        $(function() { $('#seasonalEndYearCombo')  .bind('change', function(event) { getSeasonalEndSeasonCombo(); } ); });
        $(function() { $('#seasonalEndSeasonCombo').bind('change', function(event) { getSearchDate(DateType.SEASONAL); } ); });

        locDateFormat = "yymmdd";

        //탭별 일자DatePicker 생성
        $("#ratelyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#ratelyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#intervalStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#intervalEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#hourlyStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#dailyStartDate").datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        $("#dailyEndDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        //$("#periodStartDate")  .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );
        //$("#periodEndDate")    .datepicker({maxDate:'+0m',showOn: 'button', buttonImage: '${ctx}/themes/images/default/setting/calendar.gif', buttonImageOnly: true, dateFormat:locDateFormat, onSelect: function(dateText, inst) { modifyDate(dateText, inst);}} );

        if(tabs.hourly!=0)hourlyArrow('',0,false);
        //if(tabs.period!=0)setPeriod(0,false);

        $.getJSON("${ctx}/common/getYear.do"
                ,{supplierId:_supplierId}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear = json.currYear;//currDate.getYear();
                     var currDate = json.currDate;

                     if(tabs.rately!=0){
                         //$('#ratelyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         //$('#ratelyYearCombo').selectbox();
                         //getRatelyMonthCombo("", false, true);
                         $("#ratelyStartDate").val(currDate);
                         $("#ratelyEndDate").val(currDate);
                         getSearchDate(DateTabOther.RATE,false);
                     }

                     if(tabs.interval!=0){
                         $("#intervalStartDate").val(currDate);
                         $("#intervalEndDate").val(currDate);

                         var hours = new Array();
                         for(var i = 0;i<=23;i++){
                             hours[i] = i<10?'0'+i:i+'';
                         }
                         $('#intervalStartHourCombo').numericOptions({from:0,to:23,selectedIndex:0,labels:hours});
                         $('#intervalEndHourCombo').numericOptions({from:0,to:23,selectedIndex:23,labels:hours});
                         $('#intervalStartHourCombo').selectbox();
                         $('#intervalEndHourCombo').selectbox();
                         getSearchDate(DateTabOther.INTERVAL,false);
                     }

                     if(tabs.daily!=0){
                         //$('#dailyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         //$('#dailyYearCombo').selectbox();
                         //getDailyMonthCombo("", false, true);
                         $("#dailyStartDate").val(currDate);
                         $("#dailyEndDate").val(currDate);
                         getSearchDate(DateType.DAILY,false);
                     }

                     if(tabs.monthly!=0){
                         $('#monthlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         $('#monthlyYearCombo').selectbox();
                     }

                     //탭별 현재일자 설정
                     if(tabs.seasonal!=0){
                         $('#seasonalStartYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         $('#seasonalStartYearCombo').selectbox();
                         getSeasonalStartSeasonCombo(false, true);
                         $('#seasonalEndYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         $('#seasonalEndYearCombo').selectbox();
                         getSeasonalEndSeasonCombo(false, true);
                     }

                     if(tabs.weekDaily!=0){
                         $('#weekDailyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         $('#weekDailyYearCombo').selectbox();
                         getWeekDailyMonthCombo('', false, true);
                     }

                     if(tabs.weekly!=0){
                         $('#weeklyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         $('#weeklyYearEndCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                         $('#weeklyYearCombo').selectbox();
                         $('#weeklyYearEndCombo').selectbox();
                         getWeeklyMonthCombo(false, true);
                         getWeeklyMonthEndCombo(false, true);
                     }

                     //var peroidLabels = new Array();
                     //peroidLabels[0] = '<fmt:message key="aimir.today"/>'; //'오늘'
                     //peroidLabels[1] = '<fmt:message key="aimir.yesterday"/>';//어제''
                     //peroidLabels[2] = '<fmt:message key="aimir.threedays"/>';//3일''
                     //peroidLabels[3] = '<fmt:message key="aimir.weekday"/>';//일주일''
                     //peroidLabels[4] = '<fmt:message key="aimir.onemonth"/>';//한달''

                     //$("#periodType").numericOptions({from:0,to:4,selectedIndex:0,labels:peroidLabels});
                     //$("#periodType").selectbox();

                     //탭생성
                     $("#datetab").hide();
                     $("#datetab").tabs();
                     $("#datetab").show();

                     $("#datetab").tabs("select", defaultSelect);

                     if(tabNames.rately!=null&&tabNames.rately!=''&&tabNames.rately!='undefined'){
                         $('#_rately').html(tabNames.rately);
                         $('#ratelyLabel').html(tabNames.rately);
                     }

                     if(tabNames.interval!=null&&tabNames.interval!=''&&tabNames.interval!='undefined'){
                         $('#_lpintervals').html(tabNames.interval);
                         $('#intervalLabel').html(tabNames.interval);
                     }

                     if(tabNames.hourly!=null&&tabNames.hourly!=''&&tabNames.hourly!='undefined'){
                         $('#_hourly').html(tabNames.hourly);
                         $('#hourlyLabel').html(tabNames.hourly);
                     }
                     if(tabNames.daily!=null&&tabNames.daily!=''&&tabNames.daily!='undefined'){
                         $('#_daily').html(tabNames.daily);
                         $('#dailyLabel').html(tabNames.daily);
                     }
                     if(tabNames.period!=null&&tabNames.period!=''&&tabNames.period!='undefined'){
                         $('#_period').html(tabNames.period);
                         $('#periodLabel').html(tabNames.period);
                     }
                     if(tabNames.weekly!=null&&tabNames.weekly!=''&&tabNames.weekly!='undefined'){
                         $('#_weekly').html(tabNames.weekly);
                         $('#weeklyLabel').html(tabNames.weekly);
                     }
                     if(tabNames.monthly!=null&&tabNames.monthly!=''&&tabNames.monthly!='undefined'){
                         $('#_monthly').html(tabNames.monthly);
                         $('#monthlyLabel').html(tabNames.monthly);
                     }
                     if(tabNames.monthlyPeriod!=null&&tabNames.monthlyPeriod!=''&&tabNames.monthlyPeriod!='undefined'){
                         $('#_monthlyPeriod').html(tabNames.monthlyPeriod);
                         $('#monthlyPeriodLabel').html(tabNames.monthlyPeriod);
                     }
                     if(tabNames.weekDaily!=null&&tabNames.weekDaily!=''&&tabNames.weekDaily!='undefined'){
                         $('#_weekdaily').html(tabNames.weekDaily);
                         $('#weekdailyLabel').html(tabNames.weekDaily);
                     }
                     if(tabNames.seasonal!=null&&tabNames.seasonal!=''&&tabNames.seasonal!='undefined'){
                         $('#_seasonal').html(tabNames.seasonal);
                         $('#seasonalLabel').html(tabNames.seasonal);
                     }
                     if(tabNames.yearly!=null&&tabNames.yearly!=''&&tabNames.yearly!='undefined'){
                         $('#_yearly').html(tabNames.yearly);
                         $('#yearlyLabel').html(tabNames.yearly);
                     }

                     var DefaultDate = tabs.InputDate;
                     if (DefaultDate != null && DefaultDate != 'undefined') {
                         //alert("DefaultDate:" + DefaultDate);
                         getSearchDate('', false);
                     }
                });
    }

    // datepicker로 선택한 날짜의 포맷 변경
    function modifyDate(setDate, inst){
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
    function sendRequest(_dateType){
        // 조회조건 검증
        if(!validateSearchCondition(_dateType)) return false;
        send();
    }

    /**
     *  locDate유형을 DB유형(YYYYMMDD)로 변경 처리
     */
    function convertSearchDate(){
        $.getJSON("${ctx}/common/convertSearchDate.do"
                ,{searchStartDate:$('#searchStartDate').val(), searchEndDate:$('#searchEndDate').val(), supplierId:_supplierId}
                ,function(json) {
                    $('#searchStartDate').val(json.searchStartDate);
                    $('#searchEndDate').val(json.searchEndDate);
                });
    }

    /**
     * Rately 화살표처리
     */
    /*function ratelyArrow(val){

        var a_year;
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#ratelyYearCombo').val(),month:$('#ratelyMonthCombo').val(),addVal:val}
                ,function(json) {
                    a_year = json.year;
                    $('#ratelyYearCombo').val(a_year);
                    $('#ratelyYearCombo').selectbox();
                    getRatelyMonthCombo(json.month);
                });
    }*/

    /**
     * Hourly 화살표처리
     */
    function hourlyArrow(bfDate,val,flag){

        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:bfDate,addVal:val, supplierId:_supplierId}
                ,function(json) {
                    $('#hourlyStartDate').val(json.searchDate);
                    getSearchDate(DateType.HOURLY,flag);
                });
    }

    /**
     * Daily 화살표처리
     */
    /*function dailyArrow(val){

        var a_year;
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#dailyYearCombo').val(),month:$('#dailyMonthCombo').val(),addVal:val}
                ,function(json) {
                    a_year = json.year;
                    $('#dailyYearCombo').val(a_year);
                    $('#dailyYearCombo').selectbox();
                    getDailyMonthCombo(json.month);
                });
    }*/

    /**
     * Week Daily 화살표처리
     */
    function weekDailyArrow(val){

        var a_year;
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#weekDailyYearCombo').val(),month:$('#weekDailyMonthCombo').val(),addVal:val}
                ,function(json) {
                    a_year = json.year;
                    $('#weekDailyYearCombo').val(a_year);
                    $('#weekDailyYearCombo').selectbox();
                    getWeekDailyMonthCombo(json.month);
                });
    }

    /**
     * 기간별 버튼처리
     */
    /*function setPeriod(val,flag){
        $.getJSON("${ctx}/common/getDate.do"
                ,{searchDate:'',addVal:val, supplierId:_supplierId}
                ,function(json) {
                    $('#periodStartDate').val(json.searchDate);
                    $('#periodEndDate').val(json.currDate);

                    getSearchDate(DateType.PERIOD,flag);
                });
    }*/

    /**
     * 주별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeeklyMonthCombo(flag, isInit){

        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weeklyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#weeklyMonthCombo').val();
                    $('#weeklyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weeklyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weeklyMonthCombo').selectbox();
                    getWeeklyWeekCombo(flag, isInit);
                });
    }

    /**
     * 주별탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeeklyMonthEndCombo(flag, isInit){

        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weeklyYearEndCombo').val()}
                ,function(json) {
                    var prevMonth = $('#weeklyMonthEndCombo').val();
                    $('#weeklyMonthEndCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weeklyMonthEndCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});
                    $('#weeklyMonthEndCombo').selectbox();
                    getWeeklyWeekEndCombo(flag, isInit);
                });
    }

    /**
     * 주별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeeklyWeekCombo(flag, isInit){
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weeklyYearCombo').val(),month:$('#weeklyMonthCombo').val()}
                ,function(json) {

                    var prevWeek = $('#weeklyWeekCombo').val();
                    $('#weeklyWeekCombo').emptySelect();
                    if(prevWeek==""||prevWeek==null||prevWeek > json.weekCount)prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weeklyWeekCombo').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weeklyWeekCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.WEEKLY,flag);
                    }
                });
    }

    /**
     * 주별탭에서 월콤보 변경시 주콤보 생성
     */
    function getWeeklyWeekEndCombo(flag, isInit) {
        $.getJSON("${ctx}/common/getWeek.do"
                ,{year:$('#weeklyYearEndCombo').val(),month:$('#weeklyMonthEndCombo').val()}
                ,function(json) {

                    var prevWeek = $('#weeklyWeekEndCombo').val();
                    $('#weeklyWeekEndCombo').emptySelect();
                    if(prevWeek==""||prevWeek==null||prevWeek > json.weekCount)prevWeek = json.weekCount;
                    var idx = Number(prevWeek)-1;
                    $('#weeklyWeekEndCombo').numericOptions({from:1,to:json.weekCount,selectedIndex:idx});
                    $('#weeklyWeekEndCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.WEEKLY,flag);
                    }
                });
    }

    /**
     * Rately 탭에서 연도콤보 변경시 월콤보 생성
     */
    /*function getRatelyMonthCombo(monthVal, flag, isInit){
        //alert("getRatelyMonthCombo:" + monthVal + "/" + flag + "/" + isInit);
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#ratelyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#ratelyMonthCombo').val();
                    $('#ratelyMonthCombo').emptySelect();
                    if (prevMonth == "" || prevMonth == null || Number(prevMonth) > Number(json.monthCount)) prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#ratelyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if (monthVal != null && monthVal != "") {
                        $('#ratelyMonthCombo').val(monthVal);
                    }
                    $('#ratelyMonthCombo').selectbox();

                    //if (isInit != true) {
                        //alert("isInit:" + isInit);
                        getSearchDate(DateTabOther.RATE, flag);
                    //}
                });
    }*/

    /**
     * Daily 탭에서 연도콤보 변경시 월콤보 생성
     */
    /*function getDailyMonthCombo(monthVal, flag, isInit){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#dailyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#dailyMonthCombo').val();
                    $('#dailyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#dailyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if(monthVal!=null&&monthVal!=""){
                        $('#dailyMonthCombo').val(monthVal);
                    }
                    $('#dailyMonthCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.DAILY,flag);
                    }
                });
    }*/

    /**
     * Week Daily 탭에서 연도콤보 변경시 월콤보 생성
     */
    function getWeekDailyMonthCombo(monthVal, flag, isInit){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#weekDailyYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#weekDailyMonthCombo').val();
                    $('#weekDailyMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#weekDailyMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    if(monthVal!=null&&monthVal!=""){
                        $('#weekDailyMonthCombo').val(monthVal);
                    }
                    $('#weekDailyMonthCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.WEEKDAILY,flag);
                    }
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodStartMonthCombo(flag, isInit){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodStartYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodStartMonthCombo').val();
                    $('#monthlyPeriodStartMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodStartMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodStartMonthCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.MONTHLYPERIOD,flag);
                    }
                });
    }

    /**
     * 월별(기간)탭에서 연도콤보 변경시 월콤보 생성
     */
    function getMonthlyPeriodEndMonthCombo(flag, isInit){
        $.getJSON("${ctx}/common/getMonth.do"
                ,{year:$('#monthlyPeriodEndYearCombo').val()}
                ,function(json) {
                    var prevMonth = $('#monthlyPeriodEndMonthCombo').val();
                    $('#monthlyPeriodEndMonthCombo').emptySelect();
                    if(prevMonth==""||prevMonth==null||Number(prevMonth) > Number(json.monthCount))prevMonth = json.monthCount;
                    var idx = Number(prevMonth)-1;
                    $('#monthlyPeriodEndMonthCombo').numericOptions({from:1,to:json.monthCount,selectedIndex:idx});

                    $('#monthlyPeriodEndMonthCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.MONTHLYPERIOD,flag);
                    }
                });
    }

    /**
     * 계절별탭에서 시작년도 변경시 계절콤보 조회
     */
    function getSeasonalStartSeasonCombo(flag, isInit){
        $.getJSON("${ctx}/common/getSeason.do"
                ,{year:$('#seasonalStartYearCombo').val()}
                ,function(json) {

                    startSeasonData = json;

                    var prevSeason = $('#seasonalStartSeasonCombo').val();
                    $('#seasonalStartSeasonCombo').emptySelect();

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

                    $('#seasonalStartSeasonCombo').numericOptions({from:1,to:4,labels:season,exclude:excludeIdxs});
                    $('#seasonalStartSeasonCombo').val(prevSeason);
                    $('#seasonalStartSeasonCombo').selectbox();

                    if (isInit != true) {
                        getSearchDate(DateType.SEASONAL,flag);
                    }
                });
    }

    /**
     * 계절별탭에서 종료년도 변경시 계절콤보 조회
     */
    function getSeasonalEndSeasonCombo(flag, isInit){
        $.getJSON("${ctx}/common/getSeason.do"
                ,{year:$('#seasonalEndYearCombo').val()}
                ,function(json) {
                    endSeasonData = json;

                    var prevSeason = $('#seasonalEndSeasonCombo').val();
                    $('#seasonalEndSeasonCombo').emptySelect();

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

                    $('#seasonalEndSeasonCombo').numericOptions({from:1,to:4,labels:season,exclude:excludeIdxs});
                    $('#seasonalEndSeasonCombo').val(prevSeason);
                    $('#seasonalEndSeasonCombo').selectbox();
                    
                    if (isInit != true) {
                        getSearchDate(DateType.SEASONAL,flag);
                    }
                });
    }

    /**
     * 연도,월,주차,요일을 입력받아 시작일,종료일을 구한다.
     * 요일이 입력되지 않았을경우 주차의 시작일,종료일을 구한다.
     * 주차,요일이 입력되지 않았을경우 월의 시작일,종료일을 구한다.
     */
    function getSearchDate(_dateType, flag) {
		//console.log("getSearchDate", tabs);
        var startDate='';
        var endDate='';
        //alert("dateType:" + _dateType + "\nflag:" + flag);
        
		$.ajaxSetup({
	        async: false
	    });
	    
        // 화면첫로딩시일경우에는
        // 첫번째탭 클릭 이벤트를 호출한다.
        // ajax 가 async 이므로 탭별 날짜가 순서대로 조회되지 않아서
        // 첫번째 탭의 날짜를 설정하도록 한다.
        if (flag == false) {
            //alert("flag=false");
            var DefaultDate = tabs.InputDate;
            if (DefaultDate == null || DefaultDate == 'undefined') {
                $('div#datetab > ul:first > li:first > a:first').trigger('click');
            } else {
                tabClickExec = false;

                $('#searchStartDate').val(DefaultDate.searchStartDate);
                $('#searchEndDate').val(DefaultDate.searchEndDate);
                $('#searchDateType').val(DefaultDate._dateType);

                if (DateTabOther.RATE == DefaultDate._dateType) {
                    if (tabs.rately == 0) return;
                    $('#_rately').trigger('click');
                    //$('#ratelyYearCombo').val(DefaultDate.ratelyYearCombo);
                    //$('#ratelyYearCombo').trigger('change');
                    //$('#ratelyMonthCombo').val(DefaultDate.ratelyMonthCombo);

                    //$('#ratelyYearCombo').selectbox();
                    //$('#ratelyMonthCombo').selectbox();
                    $('#ratelyStartDate').val(DefaultDate.ratelyStartDate);
                    $('#ratelyEndDate').val(DefaultDate.ratelyEndDate);
                } else if(DateTabOther.INTERVAL == DefaultDate._dateType){
                    if(tabs.interval==0)return;

                    $('#_lpintervals').trigger('click');

                    $('#intervalStartDate').val(DefaultDate.intervalStartDate);
                    $('#intervalEndDate').val(DefaultDate.intervalEndDate);
                    $('#intervalStartHourCombo').option(DefaultDate.intervalStartHourCombo);
                    $('#intervalEndHourCombo').option(DefaultDate.intervalEndHourCombo);

                    $('#searchStartHour').val(DefaultDate.searchStartHour);
                    $('#searchEndHour')  .val(DefaultDate.searchEndHour);
                } else if (DateType.HOURLY == DefaultDate._dateType) {
                    if (tabs.hourly == 0) return;
                    $('#_hourly').trigger('click');
                    $('#hourlyStartDate').val(DefaultDate.hourlyStartDate);

                } else if (DateType.DAILY == DefaultDate._dateType) {
                    if (tabs.daily == 0) return;
                    $('#_daily').trigger('click');
                    //$('#dailyYearCombo').val(DefaultDate.dailyYearCombo);
                    //$('#dailyYearCombo').trigger('change');
                    //$('#dailyMonthCombo').val(DefaultDate.dailyMonthCombo);
                    //$('#dailyYearCombo').selectbox();
                    //$('#dailyMonthCombo').selectbox();
                    $('#dailyStartDate').val(DefaultDate.dailyStartDate);
                    $('#dailyEndDate').val(DefaultDate.dailyEndDate);
                } else if (DateType.WEEKLY == DefaultDate._dateType) {
                    if (tabs.weekly == 0) return;

                    $('#_weekly').trigger('click');

                    $('#weeklyYearCombo').val(DefaultDate.weeklyYearCombo);
                    $('#weeklyYearCombo').trigger('change');
                    $('#weeklyYearEndCombo').val(DefaultDate.weeklyYearEndCombo);
                    $('#weeklyYearEndCombo').trigger('change');
                    
                    $('#weeklyMonthCombo').val(DefaultDate.weeklyMonthCombo);
                    $('#weeklyMonthCombo').trigger('change');
                    $('#weeklyMonthEndCombo').val(DefaultDate.weeklyMonthEndCombo);
                    $('#weeklyMonthEndCombo').trigger('change');

                    $('#weeklyWeekCombo').val(DefaultDate.weeklyWeekCombo);
                    $('#weeklyWeekEndCombo').val(DefaultDate.weeklyWeekEndCombo);

                    $('#weeklyYearCombo').selectbox();
                    $('#weeklyMonthCombo').selectbox();
                    $('#weeklyWeekCombo').selectbox();

                    $('#weeklyYearEndCombo').selectbox();
                    $('#weeklyMonthEndCombo').selectbox();
                    $('#weeklyWeekEndCombo').selectbox();
                } else if (DateType.MONTHLY == DefaultDate._dateType) {
                    if (tabs.monthly == 0) return;
                    $('#_monthly').trigger('click');

                    $('#monthlyYearCombo').val(DefaultDate.monthlyYearCombo);
                    $('#monthlyYearCombo').selectbox();
                } else if (DateType.WEEKDAILY == DefaultDate._dateType) {
                    if (tabs.weekDaily == 0) return;
                    $('#_weekdaily').trigger('click');

                    $('#weekDailyYearCombo').val(DefaultDate.weekDailyYearCombo);
                    $('#weekDailyYearCombo').trigger('change');
                    $('#weekDailyMonthCombo').val(DefaultDate.weekDailyMonthCombo);

                    $('#weekDailyYearCombo').selectbox();
                    $('#weekDailyMonthCombo').selectbox();

                } else if (DateType.SEASONAL == DefaultDate._dateType) {
                    if (tabs.seasonal == 0) return;

                    $('#_seasonal').trigger('click');
                    $('#seasonalStartYearCombo').val(DefaultDate.seasonalStartYearCombo);
                    $('#seasonalStartYearCombo').trigger('change');
                    $('#seasonalStartSeasonCombo').val(DefaultDate.seasonalStartSeasonCombo);

                    $('#seasonalEndYearCombo').val(DefaultDate.seasonalEndYearCombo);
                    $('#seasonalEndYearCombo').trigger('change');
                    $('#seasonalEndSeasonCombo').val(DefaultDate.seasonalEndSeasonCombo);

                    $('#seasonalStartYearCombo').selectbox();
                    $('#seasonalStartSeasonCombo').selectbox();
                    $('#seasonalEndYearCombo').selectbox();
                    $('#seasonalEndSeasonCombo').selectbox();

                } else if(DateType.YEARLY == DefaultDate._dateType) {
                    if (tabs.yearly == 0) return;

                    $('#_yearly').trigger('click');

                    $('#yearlyYearCombo').val(DefaultDate.yearlyYearCombo);
                    $('#yearlyYearCombo').selectbox();
                }
            }
            return;
        }

        if (DateTabOther.RATE == _dateType) {
            if (tabs.rately == 0) return;
            //alert("month:" + $('#ratelyMonthCombo').val());
            //if ($('#ratelyYearCombo').val() == null || $('#ratelyMonthCombo').val() == null) {
            //    return;
            //}
            //$.getJSON("${ctx}/common/getMonthPeriod.do"
            //        ,{year:$('#ratelyYearCombo').val(), month:$('#ratelyMonthCombo').val(), supplierId:_supplierId}
            //        ,function(json) {
            //            $('#searchStartDate').val(json.startDate);
            //            $('#searchEndDate').val(json.endDate);
            //            convertSearchDate();
            //        });
            $('#searchStartDate').val($('#ratelyStartDate').val());
            $('#searchEndDate').val($('#ratelyEndDate').val());
            convertSearchDate();
        } else if(DateTabOther.INTERVAL == _dateType){
            if(tabs.interval==0)return;
            $('#searchStartDate').val($('#intervalStartDate').val());
            $('#searchEndDate').val($('#intervalEndDate').val());

            $('#searchStartHour').val(Number($('#intervalStartHourCombo').val())<10?'0'+$('#intervalStartHourCombo').val():$('#intervalStartHourCombo').val());
            $('#searchEndHour')  .val(Number($('#intervalEndHourCombo').val())<10?'0'+$('#intervalEndHourCombo').val():$('#intervalEndHourCombo').val());
            convertSearchDate();
        } else if (DateType.HOURLY == _dateType) {
            if (tabs.hourly == 0) return;
            $('#searchStartDate').val($('#hourlyStartDate').val());
            $('#searchEndDate').val($('#hourlyStartDate').val());
            convertSearchDate();

        } else if (DateType.DAILY == _dateType) {
            if (tabs.daily == 0) return;
            //$.getJSON("${ctx}/common/getMonthPeriod.do"
            //        ,{year:$('#dailyYearCombo').val(),month:$('#dailyMonthCombo').val(), supplierId:_supplierId}
            //        ,function(json) {
            //            $('#searchStartDate').val(json.startDate);
            //            $('#searchEndDate').val(json.endDate);
            //            convertSearchDate();
            //        });
            $('#searchStartDate').val($('#dailyStartDate').val());
            $('#searchEndDate').val($('#dailyEndDate').val());
            convertSearchDate();
        } else if(DateType.WEEKLY == _dateType) {
            if (tabs.weekly == 0) return;

            $.getJSON("${ctx}/common/getWeekPeriodFromTo.do"
                    ,{startYear:$('#weeklyYearCombo').val(),
                      startMonth:$('#weeklyMonthCombo').val(),
                      startWeek:$('#weeklyWeekCombo').val(),
                      endYear:$('#weeklyYearEndCombo').val(),
                      endMonth:$('#weeklyMonthEndCombo').val(),
                      endWeek:$('#weeklyWeekEndCombo').val(),
                      supplierId:_supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                        convertSearchDate();
                    });
        } else if (DateType.MONTHLY == _dateType) {
            if (tabs.monthly==0) return;
            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#monthlyYearCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                        convertSearchDate();
                    });

        } else if(DateType.WEEKDAILY == _dateType) {
            if (tabs.weekDaily==0) return;
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#weekDailyYearCombo').val(),month:$('#weekDailyMonthCombo').val(), supplierId:_supplierId, supplierId:_supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                        convertSearchDate();
                    });

        } else if (DateType.SEASONAL == _dateType) {
            if (tabs.seasonal==0)return;
            var startSeasonIdx = $('#seasonalStartSeasonCombo').val();

            var startSeason;
            if (startSeasonIdx == 1) {
                startSeason = startSeasonData.Spring;
            } else if (startSeasonIdx == 2) {
                startSeason = startSeasonData.Summer;
            } else if (startSeasonIdx == 3) {
                startSeason = startSeasonData.Autumn;
            } else if (startSeasonIdx == 4) {
                startSeason = startSeasonData.Winter;
            } else {
                startSeason = {startDate:'',endDate:''};
            }

            $('#searchStartDate').val(startSeason.startDate);
            //$('#searchEndDate').val(stSeason.endDate);

            var endSeasonIdx = $('#seasonalEndSeasonCombo').val();

            var endSeason;
            if (endSeasonIdx == 1) {
                endSeason = endSeasonData.Spring;
            } else if (endSeasonIdx == 2) {
                endSeason = endSeasonData.Summer;
            } else if (endSeasonIdx == 3) {
                endSeason = endSeasonData.Autumn;
            } else if (endSeasonIdx == 4) {
                endSeason = endSeasonData.Winter;
            } else{
                endSeason = {startDate:'',endDate:''};
            }

            //$('#searchStartDate').val(season.startDate);
            $('#searchEndDate').val(endSeason.endDate);
        } else if (DateType.YEARLY == _dateType) {
            if (tabs.yearly==0) return;

            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#yearlyYearCombo').val(), supplierId:_supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);
                        convertSearchDate();
                    });
        }
		$.ajaxSetup({
	        async: true
	    });
    }

    /**
     * 날짜타입탭을 숨김처리한다.
     */
    function hideTab(_idx) {
        $("#datetab").tabs("remove",_idx);
        //$("#datetab").tabs("select",0);
    }

    /**
     * 날짜타입별 조회조건 검증
     */
    function validateSearchCondition(_dateType) {
        if (DateTabOther.RATE == _dateType) {
            //시작일,종료일 필수 체크
            //시작일,종료일 선후 체크
            //시작일,종료일 기간체크(31일 이내)
            if(Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            var limitDays = 31;   // 검색최대 일자 범위
            var params = {
                    startDate:$('#searchStartDate').val(),
                    endDate:$('#searchEndDate').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/common/getDaysCount.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);

            if (result.daysCount != null && limitDays >= result.daysCount) {
                return true;
            } else {
                alert('<fmt:message key="aimir.day.count.error"/>');
                return false;
            }
        } else if(DateTabOther.INTERVAL == _dateType){
            if(Number($('#searchStartDate').val()) == Number($('#searchEndDate').val())){
                if(Number($('#searchStartHour').val()) > Number($('#searchEndHour').val())){
                    alert('<fmt:message key="aimir.season.error"/>');
                    return false;
                }
            }

            var limitHours = 24;   // 검색최대 시간 범위
            var params = {
                    startDate:$('#searchStartDate').val(),
                    startHour:$('#searchStartHour').val(),
                    endDate:$('#searchEndDate').val(),
                    endHour:$('#searchEndHour').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/common/getHoursCount.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);

            if (result.hoursCount != null && limitHours >= result.hoursCount) {
                return true;
            } else {
                alert('<fmt:message key="aimir.hour.count.error"/>');
                return false;
            }
        } else if (DateType.HOURLY == _dateType) {
            return true;
        } else if(DateType.DAILY == _dateType) {
            //시작일,종료일 필수 체크
            //시작일,종료일 선후 체크
            //시작일,종료일 기간체크(31일 이내)
            if(Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())){
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            var limitDays = 31;   // 검색최대 일자 범위
            var params = {
                    startDate:$('#searchStartDate').val(),
                    endDate:$('#searchEndDate').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/common/getDaysCount.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);

            if (result.daysCount != null && limitDays >= result.daysCount) {
                return true;
            } else {
                alert('<fmt:message key="aimir.day.count.error"/>');
                return false;
            }
        } else if(DateType.PERIOD == _dateType) {
            //시작일,종료일 필수 체크
            //시작일,종료일 선후 체크
            //시작일,종료일 기간체크(31일 이내)
            if (Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())) {
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            if (Number($('#searchEndDate').val()) - Number($('#searchStartDate').val()) > 30) {
                //최대 31일까지만 조회 가능합니다.
                //alert('<fmt:message key="aimir.season.error"/>');
                //return false;
            }
            return true;
        } else if(DateType.WEEKLY == _dateType) {
            var limitWeeks = 24;   // 검색최대주수
            var params = {
                    startYear:$('#weeklyYearCombo').val(),
                    startMonth:$('#weeklyMonthCombo').val(),
                    startWeek:$('#weeklyWeekCombo').val(),
                    endYear:$('#weeklyYearEndCombo').val(),
                    endMonth:$('#weeklyMonthEndCombo').val(),
                    endWeek:$('#weeklyWeekEndCombo').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/common/getWeeksCount.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);

            if (result.weekCount != null && limitWeeks >= result.weekCount) {
                return true;
            } else {
                alert('<fmt:message key="aimir.week.error"/>');
                return false;
            }
        } else if(DateType.MONTHLY == _dateType) {
            //조회월 필수체크
            return true;
        } else if(DateType.MONTHLYPERIOD == _dateType) {
            if (Number($('#searchStartDate').val()) > Number($('#searchEndDate').val())) {
                //시작일이 종료일보다 큽니다
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }
            return true;
        } else if(DateType.WEEKDAILY == _dateType) {
            //조회월,주차,요일 필수체크
            return true;
        } else if(DateType.SEASONAL == _dateType) {
            //조회시작연도,계절 필수체크
            if ($('#seasonalStartSeasonCombo').val() == null || $('#seasonalStartSeasonCombo').val() == '') {
                //계절데이터가 없습니다.
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }
            //조회종료연도,계절 필수체크
            if ($('#seasonalEndSeasonCombo').val() == null || $('#seasonalEndSeasonCombo').val() == '') {
                //계절데이터가 없습니다.
                alert('<fmt:message key="aimir.season.error"/>');
                return false;
            }

            var limitSeasons = 24;   // 검색최대 계절수
            var params = {
                    startYear:$('#seasonalStartYearCombo').val(),
                    startSeason:$('#seasonalStartSeasonCombo').val(),
                    endYear:$('#seasonalEndYearCombo').val(),
                    endSeason:$('#seasonalEndSeasonCombo').val()
            };

            var jsonText = $.ajax({
                type: "POST",
                url: "${ctx}/common/getSeasonsCount.do",
                data: params,
                async: false
            }).responseText;

            // json string -> json object
            eval("result=" + jsonText);

            if (result.seasonsCount != null && limitSeasons >= result.seasonsCount) {
                return true;
            } else {
                alert('<fmt:message key="aimir.season.count.error"/>');
                return false;
            }
        } else if(DateType.YEARLY == _dateType) {

            return true;
        } else {
            //날짜타입오류
            return false;
        }
    }

    //======================================================================================

/*]]>*/
    </script>

<!-- Tab (S) -->
<div id="datetab" style="display:none">
    <ul>
        <li><a href="#lpintervals" id="_lpintervals"><fmt:message key="aimir.meteringdata.interval"/></a></li>
        <li><a href="#hourly" id="_hourly"><fmt:message key="aimir.hourly"/></a></li>
        <li><a href="#daily" id="_daily"><fmt:message key="aimir.daily"/></a></li>
        <!-- <li><a href="#period" id="_period"><fmt:message key="aimir.period"/></a></li> -->
        <li><a href="#weekly" id="_weekly"><fmt:message key="aimir.weekly"/></a></li>
        <li><a href="#monthly" id="_monthly"><fmt:message key="aimir.monthly"/></a></li>
        <!-- <li><a href="#monthlyPeriod" id="_monthlyPeriod"><fmt:message key="aimir.monthly"/>(<fmt:message key="aimir.period"/>)</a></li> -->
        <li><a href="#weekdaily" id="_weekdaily"><fmt:message key="aimir.weekdaily"/></a></li>
        <li><a href="#seasonal" id="_seasonal"><fmt:message key="aimir.seasonal"/></a></li>
        <li><a href="#rately" id="_rately"><fmt:message key="aimir.customer.usage.rate"/></a></li>
        <!-- <li><a href="#yearly" id="_yearly"><fmt:message key="aimir.yearly"/></a></li> -->
    </ul>
 	<div id="lpintervals">
        <!-- <label id="hourlyLabel"><fmt:message key="aimir.hourly"/></label> -->
        <ul>
            <li><input id="intervalStartDate" class="day" type="text" readonly="readonly"></li>
            <li class="date-space"></li>
            <li><select id="intervalStartHourCombo" class="sm"></select></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="intervalEndDate" class="day" type="text" readonly="readonly"></li>
            <li class="date-space"></li>
            <li><select id="intervalEndHourCombo" class="sm"></select></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="intervalSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>
    <div id="rately">
        <!-- <ul>
            <li><button id="ratelyLeft" type="button" class="back"></button></li>
            <li><select id="ratelyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="ratelyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="ratelyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="ratelySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div> -->
        <ul>
            <li><input id="ratelyStartDate" class="day" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="ratelyEndDate" class="day" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="ratelySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

   

    <div id="hourly">
        <ul>
            <li><button id="hourlyLeft" type="button" class="back"></button></li>
            <li><input id="hourlyStartDate" type="text" class="day" readonly="readonly"></li>
            <li><button id="hourlyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="hourlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="daily">
        <!-- <ul>
            <li><button id="dailyLeft" type="button" class="back"></button></li>
            <li><select id="dailyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="dailyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="dailyRight" type="button" class="next"></button></li>
        </ul> -->
        <ul>
            <li><input id="dailyStartDate" class="day" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="dailyEndDate" class="day" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="dailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <!-- <div id="period">
        <ul>
            <li><select id="periodType" class="date-w90"></select></li>
            <li class="date-space"></li>
            <li><input id="periodStartDate" class="day" type="text" readonly="readonly"></li>
            <li><input value="~" class="between" type="text"></li>
            <li><input id="periodEndDate" class="day" type="text" readonly="readonly"></li>
        </ul>
        <div id="btn">
                    <ul><li><a href="#" id="periodSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div> -->

    <div id="weekly">
        <ul>
            <li><select id="weeklyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="weeklyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li class="date-space"></li>
            <li><select id="weeklyWeekCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
            <li><input value="~" class="between" type="text"></li>
            <li><select id="weeklyYearEndCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="weeklyMonthEndCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li class="date-space"></li>
            <li><select id="weeklyWeekEndCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.week" /></label></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="weeklySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="monthly">
        <ul>
            <li><select id="monthlyYearCombo"></select></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="monthlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="weekdaily">
        <ul>
            <li><button id="weekDailyLeft" type="button" class="back"></button></li>
            <li><select id="weekDailyYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="weekDailyMonthCombo" class="sm"></select></li>
            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
            <li><button id="weekDailyRight" type="button" class="next"></button></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="weekDailySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <div id="seasonal">
        <ul>
            <li><select id="seasonalStartYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="seasonalStartSeasonCombo" class="date-w80"></select></li>
            <li><input value="~" class="between" type="text"></li>
            <li><select id="seasonalEndYearCombo"></select></li>
            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
            <li class="date-space"></li>
            <li><select id="seasonalEndSeasonCombo" class="date-w80"></select></li>
        </ul>
        <div id="btn">
            <ul><li><a href="#" id="seasonalSearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
        </div>
    </div>

    <input id="searchStartDate" type="hidden"/>
    <input id="searchEndDate" type="hidden" />
    <input id="searchStartHour" type="hidden"/>
    <input id="searchEndHour" type="hidden" />
    <!-- <input id="searchDateType" type="hidden" value="20"/> -->
    <input id="searchDateType" type="hidden" value="21"/>
    
    <!-- <input id="searchStartDate" type="text"/>
    <input id="searchEndDate" type="text" />
    <input id="searchStartHour" type="hidden"/>
    <input id="searchEndHour" type="hidden" />
    <input id="searchDateType" type="text" value="20"/> -->
    
</div>
<!-- Tab (E) -->
