<%@ page language="java" errorPage="/error.jsp" pageEncoding="UTF-8" contentType="text/html;charset=utf-8" %>
<script type="text/javascript" charset="utf-8">/*<![CDATA[*/

    var seasonData;

    var _supplierId='';
    
    $(document).ready(function(){ 

        commonDateTabButtonInterval = setInterval("commonDateTabButtonDelay()",100);

        //공급사ID
        //로그인한 사용자정보를 조회한다.
        $.getJSON('${ctx}/common/getUserInfo.do',
                function(json) {
                    _supplierId = json.supplierId;
                }
        );
        
        // 탭클릭이벤트 생성
        // 탭유형설정,조회일자설정
        
        $(function() { $('#searchDateType').bind('change',function(event) {

            var selectedDateType = $("#searchDateType > option:selected").val();

            if( "1" == selectedDateType ){
                
                getSearchDate(DateType.DAILY);
            }else if( "3" == selectedDateType ){
                
                getSearchDate(DateType.WEEKLY);
                
            }else if( "4" == selectedDateType ){

                getSearchDate(DateType.MONTHLY);
                
            }else if( "8" == selectedDateType ){

                getSearchDate(DateType.YEARLY);
            }

        })});

        
        //조회버튼클릭 이벤트 생성
        $(function() { $('#monthlySearch')      .bind('click',function(event) { 

            var selectedDateType = $("#searchDateType > option:selected").val();
            
            if( "1" == selectedDateType ){

            	sendRequest(DateType.DAILY);
            }
            
        } ); });
        $(function() { $('#yearlySearch')       .bind('click',function(event) { 
            
            var selectedDateType = $("#searchDateType > option:selected").val();
            
            if( "3" == selectedDateType ){
                
                sendRequest(DateType.WEEKLY);
                
            }else if( "4" == selectedDateType ){

                sendRequest(DateType.MONTHLY);
                
            }else if( "8" == selectedDateType ){

                sendRequest(DateType.YEARLY);
            }
        } ); });


        //월별 연도콤보 체인지이벤트 생성
        $(function() { $('#monthlyYearCombo')   .bind('change', function(event) { getMonthlyMonthCombo(""); } ); });
        $(function() { $('#monthlyMonthCombo')  .bind('change', function(event) { 

            var selectedDateType = $("#searchDateType > option:selected").val();
            
            if( "1" == selectedDateType ){

                getSearchDate(DateType.DAILY);
            } 
        } ); });


        //연별 연도콤보 체인지 이벤트
        $(function() { $('#yearlyYearCombo')  .bind('change', function(event) { getSearchDate(DateType.YEARLY); } ); });


        //if(tabs.daily!=0)dailyArrow('',0,false);
        
        $.getJSON("${ctx}/common/getYear.do"
                ,{supplierId:supplierId}
                ,function(json) {
                     var startYear = json.year;//currDate.getYear()-10;
                     var endYear = json.currYear;//currDate.getYear();
                     var currDate = json.currDate;
                     
                     //탭별 현재일자 설정
                     
                     $('#yearlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                     $('#yearlyYearCombo').selectbox();
                     
                     $('#monthlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:9});
                     $('#monthlyYearCombo').selectbox();
                     
                     getMonthlyMonthCombo("",false); // 월 selectBox 내용을 채운다.

                });



        $(function() { $('#monthlyLeft')    .bind('click',  function(event) { monthlyArrow(-1); } ); });
        $(function() { $('#monthlyRight')   .bind('click',  function(event) { monthlyArrow(1 ); } ); });

        $(function() { $('#yearlyLeft')    .bind('click',  function(event) { yearlyArrow(-1); } ); });
        $(function() { $('#yearlyRight')   .bind('click',  function(event) { yearlyArrow(1 ); } ); });
        
        // 일을 선택
        //$("#searchDateType > option:first").attr("selected", "true");
        //getSearchDate(DateType.DAILY);
        
        //$('#searchDateType > option:first').trigger('change');

        $('#searchDateType').selectbox();
    });



    /**
     * 월별 화살표처리
     */
    function monthlyArrow(val){
        $.getJSON("${ctx}/common/getYearMonth.do"
                ,{year:$('#monthlyYearCombo').val(),month:$('#monthlyMonthCombo').val(),addVal:val}
                ,function(json) {
                    $('#monthlyYearCombo').val(json.year);
                    getMonthlyMonthCombo(json.month);
                });
    }

    /**
     * 년별 화살표처리
     */
    function yearlyArrow(val){
        $.getJSON("${ctx}/common/getYearAddVal.do"
                ,{year:$('#yearlyYearCombo').val(),addVal:val}
                ,function(json) {

                    var targetYear = json.targetYear;
                    var startYear = json.year;//currDate.getYear()-10;
                    var endYear = json.currYear;//currDate.getYear();
                    var currDate = json.currDate;

                    var index = 0;
                    for( var i=0; i< Number( endYear ) - Number( startYear ); i++ ){

                    	if( (Number( startYear ) + i) == Number( targetYear ) ){

                    		index = i;
                    	}
                    }
                    
                    $('#yearlyYearCombo').numericOptions({from:startYear,to:endYear,selectedIndex:index});

                    if(targetYear!=null&&targetYear!=""){
                        $('#yearlyYearCombo').val(targetYear);
                    }

                    $('#yearlyYearCombo').selectbox();

                    getSearchDate(DateType.YEARLY);
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

                    getSearchDate(DateType.DAILY,flag);
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

        if(DateType.DAILY == _dateType){
            $.getJSON("${ctx}/common/getMonthPeriod.do"
                    ,{year:$('#monthlyYearCombo').val(),month:$('#monthlyMonthCombo').val(),supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);

                        convertSearchDate();
                    });


            $('#monthly').show();
            $('#yearly').hide();
        }else if(
                DateType.WEEKLY == _dateType 
                || DateType.MONTHLY == _dateType 
                || DateType.YEARLY == _dateType ){
            if(tabs.yearly==0)return;

            $.getJSON("${ctx}/common/getYearPeriod.do"
                    ,{year:$('#yearlyYearCombo').val(),supplierId:supplierId}
                    ,function(json) {
                        $('#searchStartDate').val(json.startDate);
                        $('#searchEndDate').val(json.endDate);

                        convertSearchDate();
                    });


            $('#monthly').hide();
            $('#yearly').show();
        }

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
     * 조회버튼 클릭시 조회조건 검증후 거래호출
     */
    function sendRequest(_dateType){
        // 조회조건 검증
        if(!validateSearchCondition(_dateType))return false;
        send();
    }
    
    /**
     * 날짜타입별 조회조건 검증
     */
    function validateSearchCondition(_dateType){

    	if(DateType.DAILY == _dateType){
            //시작일 필수 체크
            return true;
        }else if(DateType.WEEKLY == _dateType){
            //조회월,조회주차 필수체크
            return true;
        }else if(DateType.MONTHLY == _dateType){
            //조회월 필수체크
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
<div style="padding-left: 10px; padding-top: 5px;">
    <div class="float_left">
	    <select id="searchDateType" style="width:50px">
	        <option id="_daily" value="1" selected="selected"><fmt:message key="aimir.daily"/></option>
	        <option id="_weekly" value="3"><fmt:message key="aimir.weekly"/></option>
	        <option id="_monthly" value="4"><fmt:message key="aimir.monthly"/></option>
	        <option id="_yearly" value="8"><fmt:message key="aimir.yearly"/></option>
	    </select>
	</div>
    <div class="float_left">
	    <span id="monthly" style="display:none;padding-top: 0px;">
	        <ul>
	            <li><button id="monthlyLeft" type="button" class="back"></button></li>
	            <li><select id="monthlyYearCombo"></select></li>
	            <li><label class="descr"><fmt:message key="aimir.year1" /></label></li>
	            <li class="space5"></li>
	            <li><select id="monthlyMonthCombo" class="sm"></select></li>
	            <li><label class="descr"><fmt:message key="aimir.day.mon" /></label></li>
	            <li><button id="monthlyRight" type="button" class="next"></button></li>
	        </ul>
	        <div id="btn">
	            <ul><li><a href="#" id="monthlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	        </div>
	    </span>
	
	    <span id="yearly" style="display:none; padding-top: 0px;">
	        <ul>
                      <li><button id="yearlyLeft" type="button" class="back"></button></li>
	            <li><select id="yearlyYearCombo"></select></li>
                      <li><button id="yearlyRight" type="button" class="next"></button></li>
	        </ul>
	        <div id="btn">
	            <ul><li><a href="#" id="yearlySearch" class="on"><fmt:message key="aimir.button.search" /></a></li></ul>
	        </div>
	    </span>
     </div>
	    <input id="searchStartDate" type="hidden"/>
	    <input id="searchEndDate" type="hidden" />
	    <input id="searchStartHour" type="hidden"/>
	    <input id="searchEndHour" type="hidden" />

    
</div>
<!-- Tab (E) -->
